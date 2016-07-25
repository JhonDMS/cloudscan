package com.bch.sefe.rating.srv.impl;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.srv.ConsultaServicios;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.srv.osb.DetalleCompraVentaOsb;
import com.bch.sefe.comun.srv.osb.RespuestaDeclaracionIVAOsb;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.ServicioRatingIndividual;
import com.bch.sefe.rating.impl.ServicioRatingIndividualImpl;
import com.bch.sefe.rating.srv.GestorComponentesRating;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.ValidadorRating;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.Segmento;
import com.bch.sefe.rating.vo.ValidacionRatingFinanciero;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.CatalogoVaciados;
import com.bch.sefe.vaciados.impl.CatalogoVaciadosImpl;
import com.bch.sefe.vaciados.vo.Vaciado;

public class ValidadorRatingFinancieroImpl implements ValidadorRating {
	final static int MESES_PERIODO_ANUAL = 12;

	private static final Logger log = Logger.getLogger(ValidadorRatingFinancieroImpl.class);
	
	public ValidacionRatingFinanciero validarIngreso(Long idCliente, Long idRtgInd, Long IdVac, Integer idBanca) {
		ValidacionRatingFinanciero validacion = this.validarReporte(idCliente, idRtgInd, IdVac, idBanca);
		CatalogoVaciados catalogo = new CatalogoVaciadosImpl();
		Vaciado vac = catalogo.buscarDatosGeneral(IdVac);
		List mensajes = new ArrayList();

		// ya expiro el periodo de vigencia del vaciado
		if (!vaciadoEsVigente(vac, idBanca)) {
			mensajes.add(MessageManager.getMessage(ConstantesSEFE.MSG6_INTEGRIDAD_RATING_FINANCIERO));
			validacion.setVaciadoEsVigente(false);
		}
		
		mensajes.addAll(validacion.getMensajes());
		
		if (ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
			// No existen los ivas necesarios
			if (!hayIvasVigentes(idCliente)) {
				validacion.setHayIvasVigentes(false);
				mensajes.add(MessageManager.getMessage(ConstantesSEFE.MSG8_INTEGRIDAD_RATING_FINANCIERO));
			}
		}

		validacion.setMensajes(mensajes);

		return validacion;
	}

	public ValidacionRatingFinanciero validarModificacion(Long idCliente, Long idRtgInd, Long idVac, Integer idBanca) {
		ValidacionRatingFinanciero validacion = new ValidacionRatingFinanciero();
		List mensajes = new ArrayList();
		ServicioRatingIndividual servicioRtgIndiv = new ServicioRatingIndividualImpl();
		RatingIndividual ratingIndiv = servicioRtgIndiv.buscarRatingPorId(idCliente, idRtgInd);

		// validar si los vaciados asociados al rating han sufrido algun cambio posterior al vigenteo
		CatalogoVaciados catalogo = new CatalogoVaciadosImpl();

		GestorComponentesRating gestor = new GestorComponentesRatingImpl();
		if (ratingIndiv.getIdRatingFinanciero() != null) {
			RatingFinanciero ratingFinan = gestor.consultarRatingFinancieroPorId(ratingIndiv.getIdRatingFinanciero());

			// se recupera el primer vaciado
			Vaciado vac0 = catalogo.buscarDatosGeneral(ratingFinan.getIdVaciado0());
			if (!vac0.getVersion().equals(ConstantesSEFE.VERSION_VACIADO_VALIDA)) {
				mensajes.add(MessageManager.getMessage(ConstantesSEFE.MSG2_INTEGRIDAD_RATING_FINANCIERO));
				validacion.setVaciadosModificados(true);
			}

			// Si no es banca pyme debe existir el tercer vaciado
			// Ajuste solicitado por funcional ya que hay un caso en que pyme
			// puede calcular con al menos un unico vaciado
			//if (!ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
			Vaciado vac = catalogo.buscarDatosGeneral(idVac);
			if (!existenPeriodosRequeridos(vac, idBanca)) {
				mensajes.add(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_FINAN_ALERTA_NO_EXISTEN_VACIADOS_ANTERIORES));
				validacion.setExistenPeriodosVaciado(false);
			}

			boolean esVaciadoAnual = (vac0.getMesesPer().intValue() == 12);

			if (!matrizEsVigente(vac0, idBanca, esVaciadoAnual)) {
				mensajes.add(MessageManager.getMessage(ConstantesSEFE.MSG3_INTEGRIDAD_RATING_FINANCIERO));
				validacion.setMatrizVigente(false);
			}

			if (!vaciadoEsVigente(vac0, idBanca)) {
				mensajes.add(MessageManager.getMessage(ConstantesSEFE.MSG6_INTEGRIDAD_RATING_FINANCIERO));
				validacion.setVaciadoEsVigente(false);
			}
		}

		validacion.setMensajes(mensajes);

		return validacion;
	}

	public boolean vaciadoEsVigente(Vaciado vac, Integer idBanca) {
		boolean esVigente = false;
		// Vigencia del vaciado para rating financiero
		String claveVigencia = ConstantesSEFE.KEY_RATING_ANTIGUEDAD_MAX_VACIADO_VIGENTE;
		// se recuperan los meses de vigencia del vaciado para la banca
		Integer diasVigencia = ConfigDBManager.getValueAsInteger(claveVigencia, idBanca);
		//Date fechaActual = new Date();
		Calendar fechaActual = new GregorianCalendar();
		fechaActual.setTime(new Date());		
		fechaActual.set(Calendar.HOUR, 0);
		fechaActual.set(Calendar.MINUTE, 0);
		fechaActual.set(Calendar.SECOND, 0);
		fechaActual.set(Calendar.MILLISECOND, 0);

		// se avanza la fecha del vaciado en los meses de la vigencia
		Calendar fechaFinVigencia = new GregorianCalendar();
		fechaFinVigencia.setTime(vac.getPeriodo());
		fechaFinVigencia.set(Calendar.HOUR, 0);
		fechaFinVigencia.set(Calendar.MINUTE, 0);
		fechaFinVigencia.set(Calendar.SECOND, 0);
		fechaFinVigencia.set(Calendar.MILLISECOND, 0);
		fechaFinVigencia.add(Calendar.DAY_OF_MONTH, diasVigencia.intValue());

		// ya expiro el periodo de vigencia del vaciado
		esVigente = !fechaActual.after(fechaFinVigencia);

		return esVigente;
	}
	
	// Sprint 9 validación de componentes: se valida que no haya pasado un mes desde la fecha de confirmación del rating financiero
	public boolean validarIvas(Date fechaConfirmacion) {
		//Date fechaActual = new Date();
		Calendar fechaActual = new GregorianCalendar();
		fechaActual.setTime(new Date());		

		// se avanza la fecha del vaciado en los meses de la vigencia
		Calendar fechaRtg = new GregorianCalendar();
		fechaRtg.setTime(fechaConfirmacion);

		return fechaRtg.get(Calendar.MONTH) == fechaActual.get(Calendar.MONTH) && fechaRtg.get(Calendar.YEAR) == fechaActual.get(Calendar.YEAR);
	}

	private boolean validarExistenciaSegmentoVentaPorVaciado(Vaciado vac) {
		boolean retorno = false;
		GestorRating gestorRating = new GestorRatingImpl();
		Integer idBanca = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		Integer tpoSegmento = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_FINANCIERO_TPO_SEGMENTO, idBanca);
		if (gestorRating.obtenerSegmentoVentasPorVaciado(vac,tpoSegmento) != null) {
			return true;
		}
		return retorno;
	}

	private boolean matrizEsVigente(Vaciado vac, Integer idBanca, boolean esVaciadoAnual) {
		boolean matrizEsVigente = true;
		// se valida la existencia de matriz financiera vigente
		GestorRating gestorRating = new GestorRatingImpl();
		Segmento segmento = new Segmento();
		Integer tpoSegmento = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_FINANCIERO_TPO_SEGMENTO, idBanca);
		segmento = gestorRating.obtenerSegmentoVentasPorVaciado(vac,tpoSegmento);

		GestorRatingFinanciero gestorFinan = new GestorRatingFinancieroImpl();

		// es un vaciado anual??
		Boolean esVacAnual = new Boolean(vac.getMesesPer().intValue() == MESES_PERIODO_ANUAL);
		Integer idSegmento= null;
		if (segmento!= null) {
			idSegmento = segmento.getIdSegmento();
		}
		MatrizFinanciera matriz = gestorFinan.obtenerMatrizFinanciera(idBanca, idSegmento, esVacAnual);

		// se toma la fecha actual
		Calendar fechaEfectiva = Calendar.getInstance();
		Date fechaActual = new Date();
		// la matriz no existe, o tiene fecha de termino de vigencia o el inicio es posterior a fecha actual
		if (matriz == null || matriz.getFechaFin() != null || fechaEfectiva.after(fechaActual)) {
			matrizEsVigente = false;
		}

		return matrizEsVigente;
	}
	private boolean existeUnicoPeriodoParcialInmobiliaria(Vaciado vacCabecera, Integer idBanca){
		boolean retorno = false;
		Vaciado vacAnterior = null;
		Vaciado vacAnteAnterior = null;
		GestorRatingFinanciero gestRatFinan = new GestorRatingFinancieroImpl();
		if (vacCabecera != null) {
			vacAnterior = gestRatFinan.buscarVaciadoAnteriorRating(vacCabecera, idBanca);
			if (vacAnterior != null) {	
				vacAnteAnterior = gestRatFinan.buscarVaciadoAnteriorRating(vacAnterior, idBanca);
			}
			if (vacAnterior == null && vacAnteAnterior == null && vacCabecera.getMesesPer().intValue() != 12){
				retorno = true;
			}
		}
		return retorno;
	}
	private boolean existenPeriodosRequeridos(Vaciado vacCabecera, Integer idBanca) {
		boolean retorno = false;
		Vaciado vacAnterior = null;
		Vaciado vacAnteAnterior = null;
		GestorRatingFinanciero gestRatFinan = new GestorRatingFinancieroImpl();
		// hay casos especiales que es posible hacer el rating financiero con un solo vaciado
		Integer iniBusqueda = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_NUMERO_MINIMO_VACIADO_SOPORTADO, idBanca);
		Integer maxVacSoportado = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_NUMERO_MAXIMO_VACIADO_SOPORTADO, idBanca);
		//Boolean parcialSoportado = ConfigDBManager.getValueAsBoolean(ConstantesSEFE.RTG_FINANCIERO_CIERRE_PARCIAL_SOPORTADO, idBanca);
		Boolean parcialSoportado = new Boolean(ConfigDBManager.getValueAsString(ConstantesSEFE.RTG_FINANCIERO_CIERRE_PARCIAL_SOPORTADO, idBanca));

		
		if (ConstantesSEFE.INTEGER_UNO.equals(iniBusqueda)&& vacCabecera != null ) {
			if (vacCabecera.getMesesPer().intValue() == 12 || parcialSoportado.booleanValue()) {
				return true; // no es posible hacer un rating financiero con un solo vaciado
			}
			if (ConstantesSEFE.INTEGER_UNO.intValue()==maxVacSoportado.intValue() && !parcialSoportado.booleanValue() ) {
				return false;// combinacion P no es soportada
			}	
		}
		
		// Se validan que para No PyME que existan los tres periodos,
		if (vacCabecera != null) {
			vacAnterior = gestRatFinan.buscarVaciadoAnteriorRating(vacCabecera, idBanca);
			if (vacAnterior != null) {	
				if ((ConstantesSEFE.INTEGER_UNO.equals(iniBusqueda) ||  ConstantesSEFE.INTEGER_DOS.equals(iniBusqueda))
						&& ConstantesSEFE.INTEGER_DOS.intValue()==maxVacSoportado.intValue() ) {
					return true;// combinacion AP O AA es soportada
				}
				vacAnteAnterior = gestRatFinan.buscarVaciadoAnteriorRating(vacAnterior, idBanca);
				if (vacAnteAnterior != null) {
					retorno = true;
				}
			}
		}

		return retorno;
	}

	public ValidacionRatingFinanciero validarReporte(Long idCliente, Long idRtgInd, Long IdVac, Integer idBanca) {
		ValidacionRatingFinanciero validacion = new ValidacionRatingFinanciero();
		List mensajes = new ArrayList();

		CatalogoVaciados catalogo = new CatalogoVaciadosImpl();
		Vaciado vac = catalogo.buscarDatosGeneral(IdVac);

		if (vac.getIdEstado().equals(ConstantesSEFE.ID_ESTADO_EN_CURSO)) {
			mensajes.add(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_FINAN_ALERTA_NO_PUEDE_UTILIZAR_VACIADO));
		}

		// si es banca PyME se valida vigencia IVA's
		if (ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
			if (!validarExistenciaSegmentoVentaPorVaciado(vac)) {
				mensajes.add(MessageManager.getError(ConstantesSEFE.KEY_RATING_MSG_NO_EXISTEN_SEGMENTOS_VENTAS));
				validacion.setMatrizVigente(false);
				validacion.setExisteSegmentoVenta(false);
			}
		}

		if (!existenPeriodosRequeridos(vac, idBanca)) {
			mensajes.add(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_FINAN_ALERTA_NO_EXISTEN_VACIADOS_ANTERIORES));
			validacion.setExistenPeriodosVaciado(false);
		}
		if (ConstantesSEFE.BANCA_INMOBILIARIAS.equals(idBanca)){
			if (existeUnicoPeriodoParcialInmobiliaria(vac,idBanca)){
				if (!vac.getIdEstado().equals(ConstantesSEFE.ID_ESTADO_EN_CURSO)){
					mensajes.add(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_FINAN_ALERTA_NO_PUEDE_UTILIZAR_VACIADO));
				}
				validacion.setExistenPeriodosVaciado(false);
			}
		}
		validacion.setMensajes(mensajes);

		return validacion;
	}

	private boolean hayIvasVigentes(Long idCliente) {
		Integer idPlantilla = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		ConsultaServicios consultaSrv = (ConsultaServicios) ConfigManager.getInstanceOf(ConstantesSEFE.CONSULTA_SERVICIOS_OSB_CLASS);
		GestorServicioClientes gestCli = new GestorServicioClientesImpl();
		
		Cliente cli = gestCli.obtenerParteInvolucradaPorId(idCliente);

		Integer iniBusqueda = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RATING_FINANCIERO_INICIO_BUSQUEDA_IVAS, idPlantilla);
		Integer finBusqueda = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RATING_FINANCIERO_TERMINO_BUSQUEDA_IVAS, idPlantilla);
		//el numero de detalles a usar debe incluir los extremos por lo que se le suma 1
		final int nroDetallesAUsar = (finBusqueda.intValue() - iniBusqueda.intValue()) + 1;
		
		// Se buscan los IVAs a partir de (Fecha Actual - Meses_Configurados_Por_Usuario). Al valor configurado por el usuario se le resta un mes, 
		// ya que el servicio OSB comienza la busqueda desde un mes antes al que se consulta.
		Calendar calIniBusqueda = GregorianCalendar.getInstance();
		calIniBusqueda.add(Calendar.MONTH, -iniBusqueda.intValue() + 1);

		// Se consultan los 24 meses hacia atras a partir de la fecha
		RespuestaDeclaracionIVAOsb declaracionIVA;
		
		try {
			declaracionIVA = consultaSrv.consultarDeclaracionIVA(cli.getRut(), String.valueOf((calIniBusqueda.get(Calendar.YEAR))), String.valueOf(calIniBusqueda
					.get(Calendar.MONTH) + 1));
		} catch (Exception e) {
			log.info(MessageFormat.format("No fue posible obtener información de los IVA para el cliente [{0}], año [{1}] y mes [{2}]", new Object[] { cli.getRut(),
					String.valueOf((calIniBusqueda.get(Calendar.YEAR))), String.valueOf(calIniBusqueda.get(Calendar.MONTH) + 1) }));

			declaracionIVA = null;
		}

		if (declaracionIVA != null && declaracionIVA.retornoEsOk()) {
			if (declaracionIVA.getVentasCompras() != null 
					&& !declaracionIVA.getVentasCompras().isEmpty()
					&& declaracionIVA.getVentasCompras().size() >= nroDetallesAUsar) {
				// El primer periodo esperado, corresponde a Fecha Actual - Meses_Configurados_Por_Usuario
				Calendar calPeriodoEsperado = GregorianCalendar.getInstance();
				calPeriodoEsperado.add(Calendar.MONTH, -iniBusqueda.intValue());

				// La regla de negocio dice que deben existir IVAs entre (Fecha Actual - 2Meses) y (Fecha Actual - 18Meses)
				for (int i = 0; i < declaracionIVA.getVentasCompras().size() && i < nroDetallesAUsar; i++) {
					DetalleCompraVentaOsb compraVenta = (DetalleCompraVentaOsb) declaracionIVA.getVentasCompras().get(i);
					Date periodoCompraVenta;

					try {
						// Se transforma a fecha, el Long con anio y mes retornado por el servicio OSB. (yyyyMM)
						periodoCompraVenta = FormatUtil.parseDateOSBDeclararIva(compraVenta.getPeriodo().toString());
					} catch (ParseException e) {
						throw new BusinessOperationException("El Formato del Período retornado por el Servicio OSB CS000330 no es correcto");
					} catch (NullPointerException e) {
						throw new BusinessOperationException("El Formato del Período retornado por el Servicio OSB CS000330 no es correcto");
					}

					Calendar calPeriodoCompraVenta = GregorianCalendar.getInstance();
					calPeriodoCompraVenta.setTime(periodoCompraVenta);

					if (calPeriodoCompraVenta.get(Calendar.YEAR) != calPeriodoEsperado.get(Calendar.YEAR)
							|| calPeriodoCompraVenta.get(Calendar.MONTH) != calPeriodoEsperado.get(Calendar.MONTH)) {
						// Si el periodo retornado por el servicio no es el mismo que el esperado, quiere decir que no existen todos los IVAs
						// requeridos
						return false;
					}

					// Se pasa al mes anterior... asi hasta completar los 16 meses requeridos
					calPeriodoEsperado.add(Calendar.MONTH, -1);
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean versionVaciadoValida(Vaciado vac) {
		return (vac !=null?ConstantesSEFE.VERSION_VACIADO_VALIDA.equals(vac.getVersion()):true );
	}
	
	/**
	 * metodo que verifica si el rating soporta como maximo un solo vaciado.
	 * @param idPlantilla
	 * @return
	 */
	public Boolean soportaUnicoVaciadoParaRtgFinanciero(Integer idPlantilla) {
		Integer maxVacSoportado = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_NUMERO_MAXIMO_VACIADO_SOPORTADO, idPlantilla);
		if (ConstantesSEFE.INTEGER_UNO.equals(maxVacSoportado)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
