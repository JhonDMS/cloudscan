package com.bch.sefe.rating.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.ErrorMessagesSEFE;
import com.bch.sefe.agricola.srv.GestorAgricola;
import com.bch.sefe.agricola.srv.impl.GestorAgricolaImpl;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioCalculo;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.ServicioCalculoImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.ConsultaServicios;
import com.bch.sefe.comun.srv.GestorClasificaciones;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.GestoresNegocioFactory;
import com.bch.sefe.comun.srv.ServicioConsultaDeuda;
import com.bch.sefe.comun.srv.impl.ConsultaServiciosImplCache;
import com.bch.sefe.comun.srv.impl.GestorClasificacionesImpl;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.srv.impl.ServicioConsultaDeudaODS;
import com.bch.sefe.comun.vo.Archivo;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.DeudaCliente;
import com.bch.sefe.comun.vo.PeriodoRating;
import com.bch.sefe.comun.vo.RatingProyectado;
import com.bch.sefe.comun.vo.RatingProyectadoESP;
import com.bch.sefe.comun.vo.TipoCambio;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.constructora.srv.GestorConstructoraInmobiliaria;
import com.bch.sefe.constructora.srv.impl.GestorConstructoraInmobilariaImpl;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.ConstantesRating;
import com.bch.sefe.rating.ServicioRatingProyectadoInmobiliaria;
import com.bch.sefe.rating.srv.AlgoritmoRatingFinanciero;
import com.bch.sefe.rating.srv.GestorAlertasRtgIndividual;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.GestorRatingProyectado;
import com.bch.sefe.rating.srv.impl.GestorRatingFinancieroImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingProyectadoImpl;
import com.bch.sefe.rating.srv.impl.RatingUtil;
import com.bch.sefe.rating.vo.BalanceInmobiliario;
import com.bch.sefe.rating.vo.HojaBalanceInmobiliario;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.PlantillaRating;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.ValidacionRatingFinanciero;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.ServicioVaciados;
import com.bch.sefe.vaciados.impl.ServicioVaciadosImpl;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.srv.impl.POIExcelReader;
import com.bch.sefe.vaciados.vo.Vaciado;

public class ServicioRatingProyectadoInmobiliariaImpl implements ServicioRatingProyectadoInmobiliaria{
	
	GestorConstructoraInmobiliaria gestorConstr = new GestorConstructoraInmobilariaImpl();
	ServicioCalculo servicioCalculo = new ServicioCalculoImpl();
	private Date fechaSbif;
	
	final static Logger log = Logger.getLogger(ServicioRatingProyectadoInmobiliariaImpl.class);


	
	private RatingProyectadoESP calcularProyeccion(String rutCliente, Long idRating, Long idProy, String logUsu, String valorDefault, String deudaSistema) {
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();

		// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);

		GestorVaciados gestorVac = new GestorVaciadosImpl();
		
		// se crean los periodos y se pasan al rating proyectado
		PeriodoRating periodoP = new PeriodoRating();
		PeriodoRating periodo0 = new PeriodoRating();

		Vaciado vac0 = gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado0());
		periodo0.setPeriodo(vac0.getPeriodo());
		proyeccion.setPeriodoProy(periodoP);
		proyeccion.setPeriodo0(periodo0);
		proyeccion.setRatingFinanciero(rtgFinanciero);

		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndiv = gestorIndiv.buscarRatingIndividual(buscarIdClientePorRut(rutCliente), idRating);

	
		String keyImplementacionAlgoritmo =  ConstantesSEFE.KEY_ALGORITMO_RTG_PROYECTADO + ConstantesSEFE.PUNTO + ConstantesSEFE.KEY_DEFAULT;

		AlgoritmoRatingFinanciero algoritmo = (AlgoritmoRatingFinanciero) ConfigManager.getInstanceOf(keyImplementacionAlgoritmo);

		// lista de los vaciados para el financiero proyectado
		List vaciados = new ArrayList();
			// los meses del vaciado de cierre
		int mesesVacCierre = vac0.getMesesPer().intValue();
		// se determina el tipo de combinacion
		String combinacion = RatingUtil.getCombinacionPeriodos(vaciados);

		MatrizFinanciera matriz = gestorFinanciero.obtenerMatrizFinanciera(rtgFinanciero.getIdMatriz(), combinacion, mesesVacCierre);
		if (matriz == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_PROY_ERROR_MATRIZ_VIGENTE_NO_EXISTE));
		}

	    // req 7.4.23
		rtgFinanciero.setDeudaSistema(deudaSistema);
		
		rtgFinanciero = gestorFinanciero.actualizarRatingProyectado(matriz.getIdBanca(), rtgFinanciero);
		proyeccion.setRatingFinanciero(rtgFinanciero);
		algoritmo.calcularRating(vac0, null, null, ratingIndiv.getIdBanca(), matriz, rtgFinanciero);
		
		//Se evalua la nota tope del rating proyectado
		rtgFinanciero.setNotaFinanciera(RatingUtil.evalTope(matriz, rtgFinanciero.getNotaFinanciera()));

		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerPrimerUsuario(logUsu);

		proyeccion.getRatingFinanciero().setResponsable(usr.getCodigoUsuario());
		proyeccion.getRatingFinanciero().setIdUsuario(usr.getUsuarioId());

		return proyeccion;
	}

	public RatingProyectado confirmarProyeccion(String rutCliente,String logUsu, Long idRating, Long idProy, String fechaAvance) {
		if (idProy == null){
			throw new BusinessOperationException ("Para confirmar, primero se debe calcular");
		}
		Vaciado vacUltPeriodoProy = null;
		Long idCte = buscarIdClientePorRut(rutCliente);
		GestorRatingIndividual gestorRatingIndividual = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndividual = gestorRatingIndividual.buscarRatingIndividual(idCte, idRating);
		if (idProy != null) {
			GestorRatingFinanciero gestorRatingFinanciero = new GestorRatingFinancieroImpl();
			RatingFinanciero ratingProy = null;
			ratingProy = gestorRatingFinanciero.obtenerRating(idProy);
			if (ratingProy != null && ratingProy.getIdVaciado0() != null) {
				GestorVaciados gestVaciados = new GestorVaciadosImpl();

				vacUltPeriodoProy = gestVaciados.buscarVaciado(ratingProy.getIdVaciado0());
				GestorRatingProyectado gestorRatingProy = new GestorRatingProyectadoImpl();
				if ((vacUltPeriodoProy == null || !gestorRatingProy.esVaciadoVigente(vacUltPeriodoProy, ratingIndividual.getIdBanca(),ratingIndividual.getFechaAvance() ))) {
					throw new BusinessOperationException ("Balance Inmobiliario con fecha avance ".concat(FormatUtil.formatDate(ratingIndividual.getFechaAvance())).concat(" excede el tiempo de  vigencia"));
				}
			}
		}
		
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();
		GestorConstructoraInmobiliaria gestorContru = new GestorConstructoraInmobilariaImpl();
		// se actualiza el rating individual
		Long idUsu = buscarIdUsuarioPorNombre(logUsu);
		// ValidadorVaciados validador = new ValidadorVaciadosImpl();

		// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);
		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerPrimerUsuario(logUsu);
		BalanceInmobiliario bi = gestorContru.obtenerBi(idCte, idRating, fechaAvance);
		bi.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);
		gestorContru.actualizarBi(bi);
		rtgFinanciero.setResponsable(usr.getCodigoUsuario());
		rtgFinanciero.setIdUsuario(usr.getUsuarioId());
		rtgFinanciero.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);
		rtgFinanciero.setFechaRating(new Date());
		rtgFinanciero = gestorFinanciero.actualizarRatingProyectado(ratingIndividual.getIdBanca(), rtgFinanciero);

		if (log.isInfoEnabled()) {
			log.info(MessageFormat
					.format("Rating proyectado #{0} confirmado!", new String[] { rtgFinanciero.getIdProyectado().toString() }));
		}
		// ============================================================================
		
		gestorRatingIndividual.actualizarRatingProyectado(idCte, idRating, rtgFinanciero);

		// se calculan los valores del rating individual
		gestorRatingIndividual.calcularRating(idCte, idRating);
		// ============================================================================

		proyeccion = (RatingProyectadoESP) this.consultarProyeccion(rutCliente, idRating, idProy, idUsu, ConstantesRating.MODO_CONSULTA_DEFAULT);

		return proyeccion;
	}
	
	/*
	 * Busca el identificador de usuario a partir del log de usuario (nombre)
	 */
	protected Long buscarIdClientePorRut(String rutCliente) {
		ServicioClientes srvCtes = new ServicioClientesImpl();
		return srvCtes.obtenerIdClientePorRut(rutCliente);
	}
	
	/*
	 * Busca el identificador del cliente a partir del rut
	 */
	protected Long buscarIdUsuarioPorNombre(String logUsu) {
		GestorUsuarios gstUsr = new GestorUsuariosImpl();
		return gstUsr.obtenerPrimerUsuario(logUsu).getUsuarioId();
	}

	
	private void calcularHojaBi(BalanceInmobiliario bi, Long idCte, Long idRatingInd,String logUsu, String rutCliente) {
		gestorConstr.calcularHojaBi(bi,idCte,idRatingInd, logUsu,rutCliente);
		
	}
	
	public boolean revisarVersionPlantilla(BalanceInmobiliario bi, PlantillaRating plantilla){
		if (plantilla != null){
			if (bi.getVersion()!=null){
				if (plantilla.getVersion().equals(bi.getVersion())){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	public Boolean ingresarRatingProyectadoInmobiliario( Archivo archivo,  String logOperador, String rutCliente) {
		final Integer GRUPO_MONEDA = Integer.valueOf("1500");
		final Integer GRUPO_UNIDAD = Integer.valueOf("1600");
		POIExcelReader reader = new POIExcelReader();
		Archivo file = (Archivo) archivo;
		String baseXLS = file.getBase64String();
		byte[] byteXLS = Base64.decodeBase64(baseXLS.getBytes());
		InputStream streamXLS = new ByteArrayInputStream(byteXLS);
		ArrayList bis = reader.getFileAsList("BI", streamXLS);
		GestorRatingProyectado gestor = new GestorRatingProyectadoImpl();
		Date fechaHoy = new Date();
		try {
			streamXLS.close();
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug("no fue posible cerrar archivo rating proyectado inmobiliario. La aplicaion puede seguir operando");
			}
		}
		if (bis.size() > 0) {
			BalanceInmobiliario balanceInmobiliario = (BalanceInmobiliario) bis.get(0);
			GestorAgricola gestorAgricola = new GestorAgricolaImpl();
			PlantillaRating plantilla = gestorAgricola.buscarPlantillaRating(ConstantesSEFE.BANCA_INMOBILIARIAS);
			if (!revisarVersionPlantilla(balanceInmobiliario, plantilla)){
				throw new BusinessOperationException("La plantilla que intenta cargar no corresponde a la \u00faltima versi\u00F3n cargada el : ".concat(FormatUtil.formatDate(plantilla.getFechaActualizacion())));	
			}
			ArrayList detalle = reader.getFileAsList("detalleBI", file);
			if (detalle.size() == 0 || detalle.isEmpty()){
				throw new BusinessOperationException("Balance Inmobiliario no contiene informaci\u00F3n para calcular Nota de Rating Proyectado");
			}
			if (balanceInmobiliario.getRut() == null ||!balanceInmobiliario.getRut().equals(rutCliente)){
				throw new BusinessOperationException("El rut del archivo no concuerda con el de la sesi\u00F3n");
			}
			
			if(balanceInmobiliario.getDeudaSistema() != null && !balanceInmobiliario.getDeudaSistema().equals("") && !validarDeudaManual(balanceInmobiliario))
			{
				throw new BusinessOperationException("La deuda debe ser un n\u00famero real mayor o igual a cero");
			}
			//02-06-2014 ncerda; Se valida que fechaAvance no sea mayor a Fecha Actual.
			if (fechaHoy.before(balanceInmobiliario.getFechaAvance())){
				throw new BusinessOperationException(ErrorMessagesSEFE.ERR_PROY_FEC_AV_BI);
			}
			if (balanceInmobiliario.getFechaAvance() == null || balanceInmobiliario.getFechaAvance().toString().equals("Sun Dec 31 00:00:00 CLT 1899")
					|| balanceInmobiliario.getFechaAvance().toString().equals("Sun Dec 31 00:00:00 CST 1899")){
				throw new BusinessOperationException("La fecha de avance no puede ser nula");
			}
			List bisBd=buscarBiXRut(rutCliente , null);
			if (bisBd.size()>0){
				Boolean existe=Boolean.FALSE;
				for (Iterator iterator = bisBd.iterator(); iterator.hasNext();) {
					BalanceInmobiliario biBd = (BalanceInmobiliario) iterator.next();
					if (biBd.getFechaAvance().equals(balanceInmobiliario.getFechaAvance())){
						existe=Boolean.TRUE;
					}
				}
				if (existe.booleanValue()){
					// Req 7.4.23
					gestor.actualizarDeudaBalanceInmobiliario(rutCliente, balanceInmobiliario.getDeudaSistema(), balanceInmobiliario.getFechaAvance());
					return existe;
				}
			}
			Long idUsuario = buscarIdUsuarioPorNombre(logOperador);
			if (idUsuario.equals(null)) {
				throw new BusinessOperationException("El usuario: " + logOperador+ "no existe en la aplicacion");
			}
			balanceInmobiliario.setIdUsuario(idUsuario);
			Long idCliente = null;
			balanceInmobiliario.setRut(rutCliente);
			try {
				idCliente = buscarIdClientePorRut(balanceInmobiliario.getRut());
			} catch (NullPointerException e) {
				throw new BusinessOperationException("El rut: " + balanceInmobiliario.getRut() + " no existe en la aplicaci�n");
			}
			//Double totalDeuda=gestor.obtenerDeudaXFechaAvance(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF, balanceInmobiliario.getFechaAvance());
			Double totalDeuda=obtenerDeudaXFechaAvance(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF, balanceInmobiliario.getFechaAvance());
			balanceInmobiliario.setFechaSbif(getFechaSbif()); // Deuda Sbif req. 7.2.8 Sprint 4
			balanceInmobiliario.setDeuSbif(totalDeuda);
			balanceInmobiliario.setIdParteInvol(idCliente);
			GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
			Clasificacion clasifMoneda = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_MONEDA, balanceInmobiliario.getMoneda());
			balanceInmobiliario.setIdMoneda(clasifMoneda.getIdClasif());
			Clasificacion clasifUnidad = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_UNIDAD, balanceInmobiliario.getUnidad());
			balanceInmobiliario.setIdUnidadMedida(clasifUnidad.getIdClasif());
			ConsultaServicios servicios = new ConsultaServiciosImplCache();
			ServicioVaciados servicio = new ServicioVaciadosImpl();
			Date fechaConsultaCambio = servicio.buscarDiaHabilSiguiente(balanceInmobiliario.getFechaAvance());
			if (!clasifMoneda.getIdClasif().equals(ConstantesSEFE.ID_CLASIF_MONEDA_CLP)) {
				TipoCambio tipoCambio = servicios.consultaTipoCambio(fechaConsultaCambio, clasifMoneda.getCodigo());
				balanceInmobiliario.setTipoCambio(tipoCambio.getValorObservado());
			} else {
				balanceInmobiliario.setTipoCambio(new Double("1"));
			}
			String codigoMonedaUf = ConfigManager.getValueAsString(ConstantesSEFE.KEY_CODIGO_MONEDA_CONSULTA_VALOR_MONEDA + ConstantesSEFE.PUNTO + ConstantesSEFE.ID_CLASIF_MONEDA_UF, null);
			TipoCambio tpoCambioDestino = servicios.consultaTipoCambio(balanceInmobiliario.getFechaAvance(), codigoMonedaUf);
			balanceInmobiliario.setUf(tpoCambioDestino.getValorObservado());
			gestor.insertarBalanceInmobiliario(balanceInmobiliario);
			setFechaSbif(null); // Deuda Sbif req. 7.2.8 Sprint 4
			streamXLS = new ByteArrayInputStream(byteXLS);
			insertarHojasBi(streamXLS,balanceInmobiliario, reader, gestor);
			//streamXLS = new ByteArrayInputStream(byteXLS);
			//insertarIndicadoresBi(streamXLS,balanceInmobiliario, reader, gestor);
		}
		return Boolean.FALSE;
	}
	
	private boolean validarDeudaManual(BalanceInmobiliario balance)
	{
		try
		{
			String valor = balance.getDeudaSistema();
			int validarComas = valor.length()-valor.replaceAll("\\.", "").length();
			
			String decimal = "";
			
			if(validarComas == 1)
			{
				// toma el primer decimal
				decimal = valor.substring(valor.indexOf(".")+1, valor.indexOf(".")+2);
			}
			else if (validarComas > 1)
			{
				return false;
			}
			
			if(decimal != null)
			{
				if(Integer.parseInt(decimal) > 5)
				{
					balance.setDeudaSistema(String.valueOf(((new Double(valor).intValue())+1)));
				}
				else
				{
					balance.setDeudaSistema(String.valueOf(((new Double(valor).intValue()))));
				}
			}
			
			return new Double(balance.getDeudaSistema()).doubleValue()>=0;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}
	
	//ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF
	private Double obtenerDeudaXFechaAvance(String rutCliente, Integer flagDeudaTipoSbif, Date fechaAvance) {
		GestorRatingProyectado gestor = new GestorRatingProyectadoImpl();
		ServicioConsultaDeuda servicioDeudaODS = new ServicioConsultaDeudaODS();
		Calendar nuevaFechaAvance = Calendar.getInstance();
		nuevaFechaAvance.setTime(fechaAvance);
		
	   
		int dia = nuevaFechaAvance.get(Calendar.DAY_OF_MONTH);
		Double deudaSBIF = null;
		Date fechaConsulta = null ;
		if (dia <15 ) {
			fechaConsulta = FormatUtil.obtenerMesAnterior(fechaAvance);
			int mes = nuevaFechaAvance.get(Calendar.MONTH);
			int ano = nuevaFechaAvance.get(Calendar.YEAR);
			String nuevoMes= ""+mes;
			if (mes<10) {
				nuevoMes="0"+mes;
			}
			String nuevaFechaConsutlada = ""+ano+nuevoMes;
			String utltimaFechaCargada = servicioDeudaODS.buscarMaxPeriodoDeuda( ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF);
			// fecha balance debe ser menor a la ultima fecha cargada en la base de datos.
			if (nuevaFechaConsutlada.compareTo(utltimaFechaCargada)  <=-1 ) {
				deudaSBIF = gestor.obtenerDeudaXFechaAvance(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF, fechaConsulta);
				// Req. 7.2.8 fecha deuda sbif - Sprint 4 - Insertar en Hoja Balance Inmobiliario
				Date fecha = deudaSBIF == null ? null: fechaConsulta;
				setFechaSbif(fecha);
				return (deudaSBIF == null ? ConstantesSEFE.DOUBLE_CERO: deudaSBIF);
			}			 
		} else {
			fechaConsulta = fechaAvance;
			
		}
		deudaSBIF = gestor.obtenerDeudaXFechaAvance(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF, fechaConsulta);
		// Req. 7.2.8 fecha deuda sbif - Sprint 4 - Insertar en Hoja Balance Inmobiliario
		Date fecha = deudaSBIF != null ? fechaConsulta: null;
		setFechaSbif(fecha);
		
		if (deudaSBIF == null) {
			deudaSBIF = ConstantesSEFE.DOUBLE_CERO;
			DeudaCliente deudaCliente = servicioDeudaODS.buscarDeudaUltimoPeriodoCache(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF);
			if (deudaCliente != null) {
				// Req. 7.2.8 fecha deuda sbif - Sprint 4 - Insertar en Hoja Balance Inmobiliario
				Date fec = deudaCliente.getTotalDeudaDirecta() != null ? deudaCliente.getPeriodo(): null;
				setFechaSbif(fec);
				
				return deudaCliente.getTotalDeudaDirecta();
				
			}
		}
		return deudaSBIF;
	}


	
	public List buscarBiXRut(String rutCliente, Long idRatingIndividual) {
		GestorRatingProyectado gestorRatingProy = new GestorRatingProyectadoImpl();
		GestorClasificaciones gestorClasif = new GestorClasificacionesImpl();
		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();
		GestorServicioClientes gestorClientes  = new GestorServicioClientesImpl();
		Cliente cliente = gestorClientes.obtenerClientePorRut(rutCliente);
		List listaBi = gestorRatingProy.buscarBiXRut(rutCliente);
		if (idRatingIndividual == null) {
			return listaBi;
		}
		List ratingList = (List) gestorRating.buscarRatingsIndividualesPorCliente(null, rutCliente);
		RatingIndividual ratingIndividual = gestorRating.buscarRatingIndividual(new Long(cliente.getClienteId()), idRatingIndividual);	
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(ratingIndividual.getIdRatingProyectado());
		for (int i=0; i < listaBi.size(); i++) {
			BalanceInmobiliario bi = (BalanceInmobiliario)listaBi.get(i);
			// el soe esta asociado al rating individual
			if (bi.getFechaAvance().equals(ratingIndividual.getFechaAvance()) && ratingIndividual.getIdRating().equals(idRatingIndividual)) {
				bi.setSeqRtg(ratingIndividual.getIdRating());
				bi.setIdRatingProyectado(ratingIndividual.getIdRatingProyectado());
				bi.setNota(ratingIndividual.getRatingProyectado());
				bi.setIdEstadoRating(ratingIndividual.getIdEstado());
				if (rtgFinanciero != null) {
					bi.setEstadoRating(gestorClasif.buscarClasificacionPorId(rtgFinanciero.getEstado()).getNombre());
				}
			}
			boolean puedeBorrar = true;
			for (int j = 0; j < ratingList.size(); j++) {
				RatingIndividual rtgIndividual = (RatingIndividual) ratingList.get(j);
				if (bi.getFechaAvance().equals(rtgIndividual.getFechaAvance()) && ConstantesSEFE.BANCA_INMOBILIARIAS.equals(rtgIndividual.getIdBanca())) {
					puedeBorrar = ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO.equals(rtgIndividual.getIdEstado());
					if (!puedeBorrar) {
						break;
					}
				}
			}
			bi.setPuedeBorrar(new Boolean(puedeBorrar));
		}
		return listaBi;
	}
	
	



	public RatingProyectado generaProyeccion(String rutCliente, Long idRatingInd ,String logOperador, Date fechaAvance, Integer idBanca) {
		// se asocial el id del rating indiviadual a la SOE seleccionada
		Long idCte = buscarIdClientePorRut(rutCliente);
		Long idUsr = buscarIdUsuarioPorNombre(logOperador);
		Date fechaHoy = new Date();
		BalanceInmobiliario bi = gestorConstr.obtenerBi(idCte, fechaAvance);
		// si el bi está asociado a otro rating entonces se lanza una excepcion de negocio
		if (bi!= null && bi.getSeqRtg()!=null && bi.getSeqRtg().equals(idRatingInd)) {
			throw new BusinessOperationException(ErrorMessagesSEFE.ERR_PROYECTADO_BI);
		}
		//03-06-2014 ncerda; Se valida que fechaAvance no sea mayor a Fecha Actual.
		if (fechaHoy.before(fechaAvance)){
			throw new BusinessOperationException(ErrorMessagesSEFE.ERR_PROY_FEC_AV_BI);
		}
		gestorConstr.asociarBiRatingIndividual(idCte, fechaAvance, idRatingInd);
		bi.setSeqRtg(idRatingInd);
		gestorConstr.eliminarIndicadoresBalanceInmobiliario(idCte, fechaAvance);
		this.calcularHojaBi(bi, idCte, idRatingInd, logOperador, rutCliente);
		RatingProyectadoESP proyectado=  crearProyectado(rutCliente,bi,idRatingInd ,idUsr, logOperador);
		proyectado =  generarProyeccion(rutCliente, idRatingInd ,logOperador, proyectado.getIdRating(), bi.getDeudaSistema());
		proyectado =  consultarProyeccion(rutCliente, idRatingInd,proyectado.getIdRating(), idUsr, null);
		return proyectado;

	}
	
	private RatingProyectadoESP crearProyectado(String rutCliente, BalanceInmobiliario bi , Long idRatingInd, Long idUsr,String loginUsuario) {
		GestorVaciados gestorVaciado 					= new GestorVaciadosImpl();
		GestorRatingFinanciero gestorRatingFinanciero 	= new GestorRatingFinancieroImpl();
		GestorRating gestorRating = new GestorRatingImpl();
		Vaciado vaciadosRatingProy = null;
		ValidacionRatingFinanciero mensajes =null;
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndiv = gestorIndiv.consultaRatingSugerido(bi.getIdParteInvol(), idRatingInd);
		if (vaciadosRatingProy == null || (mensajes != null && mensajes.getMensajes() != null && !mensajes.getMensajes().isEmpty()) ) {
			 vaciadosRatingProy = gestorVaciado.buscarVaciadoInmobiliario(idRatingInd);
			 // si no existe vaciado para inmobiliaria se crea uno individual del tipo CHGAAP
			 if (vaciadosRatingProy == null ) {
				 vaciadosRatingProy=gestorVaciado.crearVaciadoInmobiliario(bi,  idUsr);
			 }
		 }			
		if (log.isDebugEnabled()) {
			log.debug("Creando vaciado para rating proyectado...");
		}
		RatingProyectadoESP proyeccion = null;
		
//		String mensaje = validarVigenciaProyectado(ratingIndiv, rutCliente);
//		 if(mensaje != null)
//		 {
//			 throw new BusinessOperationException(mensaje);
//		 }

		if (ratingIndiv.getIdRatingProyectado() != null) {
			proyeccion = consultarProyeccion(rutCliente, idRatingInd, ratingIndiv.getIdRatingProyectado(), idUsr, null);
			if (proyeccion.isMatrizVigente()) {
				return proyeccion;
			}
		}
		 
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero ratingBase = new RatingFinanciero();

		// se remueve el id de rating para que se genere uno nuevo para el
		// proyectado
		ratingBase.setIdUsuario(idUsr);
		ratingBase.setIdVaciado0(vaciadosRatingProy.getIdVaciado());
		ratingBase.setFechaBalance(vaciadosRatingProy.getPeriodo());
		ratingBase.setNumeroMeses(vaciadosRatingProy.getMesesPer());

		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND,idRatingInd );
		ratingBase.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		ratingBase.setFechaRating(new Date());
		RatingFinanciero rtgFinanciero = gestorRatingFinanciero.calcularRating(rutCliente, ratingIndiv.getIdBanca(), vaciadosRatingProy.getIdVaciado(), loginUsuario);
		
		if (rtgFinanciero.getIdMatriz() == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_PROY_ERROR_MATRIZ_VIGENTE_NO_EXISTE));
		}
		MatrizFinanciera matrizProyectada = gestorRatingFinanciero.obtenerMatrizFinanciera(rtgFinanciero.getIdMatriz());
		if (matrizProyectada.getIdMatrizProy() == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_PROY_ERROR_MATRIZ_VIGENTE_NO_EXISTE));
		}
		rtgFinanciero.setIdMatriz(matrizProyectada.getIdMatrizProy());
		RatingFinanciero ratingProyectado = null;
		proyeccion = new RatingProyectadoESP();
		ratingProyectado = gestorFinanciero.grabarRatingProyectado(rtgFinanciero);
		proyeccion.setRatingFinanciero(ratingProyectado);
		proyeccion.setIdRating(ratingProyectado.getIdProyectado());
		if (log.isInfoEnabled()) {
			log.info("> > > > Actualizando cuentas con los valores ingresados ");
		}			
		if (log.isInfoEnabled()) {
			log.info("Creando rating proyectado #" + ratingProyectado.getIdRating());
		}

		return proyeccion;
	}
	
	private String validarVigenciaProyectado(RatingIndividual rtgInd, String rutCliente)
	{		
		GestorAlertasRtgIndividual gestorAlertas = new GestorAlertasRtgIndividualImpl();
		List alertas = gestorAlertas.obtenerAlertasRtgIndividualModelo(rtgInd.getIdRating(), rutCliente, rtgInd.getIdBanca());
		boolean validacionFechaAvance = true;
		
		// se determina el periodo de vigencia del ultimo vaciado
		GestorConstructoraInmobiliaria constructora = new GestorConstructoraInmobilariaImpl();
		Integer diasVigencia = constructora.obtenerAntiguedadMaximaBI(rtgInd.getIdBanca());
		Calendar vigenteHasta = Calendar.getInstance();
		vigenteHasta.setTime(rtgInd.getFechaAvance());
		vigenteHasta.add(Calendar.DAY_OF_MONTH, diasVigencia.intValue());
		vigenteHasta.set(Calendar.HOUR, 0);
		vigenteHasta.set(Calendar.MINUTE, 0);
		vigenteHasta.set(Calendar.SECOND, 0);
		vigenteHasta.set(Calendar.MILLISECOND, 0);
		
		Calendar fechaActual = Calendar.getInstance();
		fechaActual.set(Calendar.HOUR, 0);
		fechaActual.set(Calendar.MINUTE, 0);
		fechaActual.set(Calendar.SECOND, 0);
		fechaActual.set(Calendar.MILLISECOND, 0);		
		
		validacionFechaAvance = (vigenteHasta.after(fechaActual));
				
		if(rtgInd.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO))
		{
			for (int i = 0; i < alertas.size(); ++i) {
				String mensaje = (String) alertas.get(i);
				if(!validacionFechaAvance || mensaje.equals(ConstantesSEFE.MSG_ALERTA_VALIDACION_VERSION_VACIADOS))
				{
					return mensaje;
				}
			}
		}
		return null;
	}
	
	
	public RatingProyectadoESP consultarProyeccion(String rutCliente, Long idRating, Long idProy, Long logUsu, String modo) {
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();	
		GestorRatingIndividual gestorIndiv= new GestorRatingIndividualImpl();
		Long idCte = buscarIdClientePorRut(rutCliente);
		RatingIndividual ratingInd = gestorIndiv.buscarRatingIndividual(idCte, idRating);
	
		//	RatingFinanciero finanVigente = gestorComp.buscarRatingFinacieroVigente(idCte, ratingInd.getIdBanca());
			// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);
		MatrizFinanciera matriz = gestorFinanciero.obtenerMatrizFinanciera(rtgFinanciero.getIdMatriz());
		proyeccion.setMatrizVigente(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE.equals(matriz.getEstadoId()));

		proyeccion.setRatingFinanciero(rtgFinanciero);

		String modoConsulta = modo;
		if (modoConsulta == null) {
			if (rtgFinanciero.getEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE)) {
				modoConsulta = ConstantesRating.MODO_CONSULTA_DEFAULT;

				if (ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE.equals(ratingInd.getIdEstado())
						|| ConstantesSEFE.ID_CLASIF_ESTADO_RATING_HISTORICO.equals(ratingInd.getIdEstado())) {
					modoConsulta = ConstantesRating.MODO_CONSULTA_SOLO_LECTURA;
				}
			} else {
				if (rtgFinanciero.getNotaFinanciera() != null) {
					modoConsulta = ConstantesRating.MODO_CONSULTA_CONFIRMAR;
				} else {
					modoConsulta = ConstantesRating.MODO_CONSULTA_PROYECTAR;
				}
			}
		}

		proyeccion.setModoConsulta(modoConsulta);

		return proyeccion;
	}


	public RatingProyectado obtenerRatingProyectadoSoe(Long idRatingProyectado) {
		return null;
	}

	private RatingProyectadoESP generarProyeccion(String rutCliente, Long idRatingInd,  String logUsu, Long idRatingProy, String deudaSistema) {
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();

		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		// se verifica el rating financiero
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		Long idCte = buscarIdClientePorRut(rutCliente);
		RatingIndividual ratingInd = gestorIndiv.buscarRatingIndividual(idCte, idRatingInd);

		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRatingInd);
		// aqui se calcula el rating proyectado....
		proyeccion = (RatingProyectadoESP) calcularProyeccion(rutCliente, idRatingInd, idRatingProy, logUsu, null, deudaSistema);

		
		proyeccion.getRatingFinanciero().setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		RatingFinanciero ratingFinanciero = gestorFinanciero.actualizarRatingProyectado(ratingInd.getIdBanca(), proyeccion.getRatingFinanciero());

		// se guardan los valores intermedios de los calculos y puntajes
		// si no hay rating financiero para el vaciado cabecera entonces se
		// generar un nuevo calculo y nuevo rating financiero
		gestorFinanciero.grabarEvaluacionesProyectado(proyeccion.getRatingFinanciero());

		if (log.isInfoEnabled()) {
			log.info("> > > > Actualizando cuentas con los valores ingresados ");
		}
		
	
		// ============================================================================
		// se actualiza el rating individual con el valor del proyectado
		gestorIndiv.actualizarRatingProyectado(idCte, idRatingInd, proyeccion.getRatingFinanciero());

		// el rating individual borra nota modelo, ponderado y ajustado por
		// tamaño
		ratingInd.setRatingFinalSugerido(null);
		ratingInd.setRatingPreliminar1(null);
		ratingInd.setRatingFinal(null);
		gestorIndiv.actualizarRatingIndividual(ratingInd);
		// ============================================================================

		return proyeccion;
	}
	
	public void actualizarCargaBalanceInmobiliario(Archivo archivo,  String logOperador, String rutCliente) {
		POIExcelReader reader = new POIExcelReader();
		Archivo file = (Archivo) archivo;
		String baseXLS = file.getBase64String();
		byte[] byteXLS = Base64.decodeBase64(baseXLS.getBytes());
		InputStream streamXLS = new ByteArrayInputStream(byteXLS);
		ArrayList bis = reader.getFileAsList("BI", streamXLS);
		try {
			streamXLS.close();
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug("no fue posible cerrar archivo rating proyectado inmobiliario. La aplicaion puede seguir operando");
			}
		}
		if (bis.size() > 0) {
			BalanceInmobiliario balanceInmobiliario = (BalanceInmobiliario) bis.get(0);
			Long idUsuario = buscarIdUsuarioPorNombre(logOperador);
			if (idUsuario.equals(null)) {
				throw new BusinessOperationException("El usuario: " + logOperador + "no existe en la aplicaci�n");
			}
			balanceInmobiliario.setIdUsuario(idUsuario);
			GestorRatingProyectado gestor = new GestorRatingProyectadoImpl();
			Long idCliente = null;
			balanceInmobiliario.setRut(rutCliente);
			try {
				idCliente = buscarIdClientePorRut(balanceInmobiliario.getRut());
			} catch (NullPointerException e) {
				throw new BusinessOperationException("El rut: " + balanceInmobiliario.getRut() + " no existe en la aplicacion");
			}
			gestor.actualizarUsuarioBalance(idUsuario, idCliente, balanceInmobiliario.getFechaAvance(), ConstantesSEFE.KEY_BI);
			gestor.borrarHojasEIndicadores(idCliente,  balanceInmobiliario.getFechaAvance(), ConstantesSEFE.KEY_BI);
			balanceInmobiliario.setIdParteInvol(idCliente);
			streamXLS = new ByteArrayInputStream(byteXLS);
			insertarHojasBi(streamXLS,balanceInmobiliario, reader, gestor);
			//streamXLS = new ByteArrayInputStream(byteXLS);
			//insertarIndicadoresBi(streamXLS,balanceInmobiliario, reader, gestor);
		}
	}
	
	public void insertarHojasBi(InputStream streamXLS, BalanceInmobiliario balanceInmobiliario,POIExcelReader reader, GestorRatingProyectado gestor ){
		ArrayList detalle = reader.getFileAsList("detalleBI", streamXLS);
		if (detalle.size() == 0 || detalle.isEmpty()){
			throw new BusinessOperationException("Balance Inmobiliario no contiene información para calcular Nota de Rating Proyectado");
		}
		if (detalle.size()>50){
			throw new BusinessOperationException("Balance Inmobiliario no puede tener mas de 50 registros");
		}
		String errores = "";
		for (Iterator iterator = detalle.iterator(); iterator.hasNext();) {
			HojaBalanceInmobiliario hoja = (HojaBalanceInmobiliario) iterator.next();
			errores = errores.concat(validarHojaBi(hoja, errores));
		}
		if (!errores.equals("")){
			throw new BusinessOperationException(errores);
		}
		for (Iterator iterator = detalle.iterator(); iterator.hasNext();) {
			HojaBalanceInmobiliario hoja = (HojaBalanceInmobiliario) iterator.next();
			hoja.setIdParteInvol(balanceInmobiliario.getIdParteInvol());
			hoja.setFechaAvance(balanceInmobiliario.getFechaAvance());
			gestor.insertarHojaBalanceInmobiliario(hoja);
		}
		try {
			streamXLS.close();
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug("no fue posible cerrar archivo rating proyectado Inmobiliaria. La aplicacion puede seguir operando");
			}
		}
	}
	public String validarHojaBi(HojaBalanceInmobiliario hojaBi, String errores){
		String errorFila = "";
		if (hojaBi.getVentaTotalProyectada() == null || hojaBi.getVentaTotalProyectada().equals("-")){
			final String error = "Venta Total Proyectada no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getVentaTotalProyectada().doubleValue()<0){
				final String error = "Venta Total Proyectada debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getNumeroUnidades() == null || hojaBi.getNumeroUnidades().equals("-")){
			final String error = "Numero Unidades no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getNumeroUnidades().doubleValue()<0){
				final String error = "Numero Unidades debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getValorPromedio() == null || hojaBi.getValorPromedio().equals("-")){
			final String error = "Valor Promedio no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getValorPromedio().doubleValue()<0){
				final String error = "Valor Promedio debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getAvanceDeObra() == null || hojaBi.getAvanceDeObra().equals("-")){
			final String error = "Avance Obra no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getAvanceDeObra().doubleValue()<0){
				final String error = "Avance Obra debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getFechaDeTermino() == null || hojaBi.getFechaDeTermino().equals("-")){
			final String error = "Fecha de Termino no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getLineaAprobada() == null || hojaBi.getLineaAprobada().equals("-")){
			final String error = "Linea Aprobada no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getLineaAprobada().doubleValue()<0){
				final String error = "Linea Aprobada debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getDeudaVigente() == null || hojaBi.getDeudaVigente().equals("-")){
			final String error = "Deuda Vigente no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getPorGirar() == null || hojaBi.getPorGirar().equals("-")){
			final String error = "Por Girar no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getDeudaMaxima() == null || hojaBi.getDeudaMaxima().equals("-")){
			final String error = "Deuda Maxima no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getPctVentasEscrituradas() == null || hojaBi.getPctVentasEscrituradas().equals("-")){
			final String error = "% Ventas Escrituradas no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getPctVentasEscrituradas().doubleValue()<0 || hojaBi.getPctVentasEscrituradas().doubleValue()>100){
				final String error = "% Ventas Escrituradas  debe ser mayor o igual a cero y menor o igual a 100";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getMtoVentasEscrituradas() == null || hojaBi.getMtoVentasEscrituradas().equals("-")){
			final String error = "Monto Ventas Escrituradas no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getMtoVentasEscrituradas().doubleValue()<0){
				final String error = "Monto Ventas Escrituradas debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getPctPromesasDeVenta() == null || hojaBi.getPctPromesasDeVenta().equals("-")){
			final String error = "% Promesas de Venta no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getPctPromesasDeVenta().doubleValue()<0 || hojaBi.getPctPromesasDeVenta().doubleValue()>100){
				final String error = "% Promesas de Venta debe ser mayor o igual a cero y menor o igual a 100";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getMtoPromesasDeVenta() == null || hojaBi.getMtoPromesasDeVenta().equals("-")){
			final String error = "Monto Promesas de Venta no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getMtoPromesasDeVenta().doubleValue()<0){
				final String error = "Monto Promesas de Venta debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getPctTotalVentas() == null || hojaBi.getPctTotalVentas().equals("-")){
			final String error = "% Total Ventas no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getPctTotalVentas().doubleValue()<0 || hojaBi.getPctTotalVentas().doubleValue()>100){
				final String error = "% Total Ventas debe ser mayor o igual a cero y menor o igual a 100";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getMtoTotalVentas() == null || hojaBi.getMtoTotalVentas().equals("-")){
			final String error = "Monto Total Ventas no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getMtoTotalVentas().doubleValue()<0){
				final String error = "Monto Total Ventas debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getFlujoRecibido() == null || hojaBi.getFlujoRecibido().equals("-")){
			final String error = "Recibido no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getFlujoRecibido().doubleValue()<0){
				final String error = "Recibido debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getFlujoCartasDeResguardo() == null || hojaBi.getFlujoCartasDeResguardo().equals("-")){
			final String error = "Cartas de Resguardo no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getFlujoCartasDeResguardo().doubleValue()<0){
				final String error = "Cartas de Resguardo debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getFlujoPorRecibirPromesas() == null || hojaBi.getFlujoPorRecibirPromesas().equals("-")){
			final String error = "Por Recibir Promesas no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getFlujoPorRecibirPromesas().doubleValue()<0){
				final String error = "Por Recibir Promesas debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getDeuNetaCartasDeResguardo() == null || hojaBi.getDeuNetaCartasDeResguardo().equals("-")){
			final String error = "Deuda Neta Cartas de Resguardo no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getDeuNetaVtaPromesas() == null || hojaBi.getDeuNetaVtaPromesas().equals("-")){
			final String error = "Deuda Neta Ventas o Promesas no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getSaldoPorVenderPromesarUnidad() == null || hojaBi.getSaldoPorVenderPromesarUnidad().equals("-")){
			final String error = "Saldo Por Vender o Promesar Unidad no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getSaldoPorVenderPromesarUnidad().doubleValue()<0){
				final String error = "Saldo Por Vender o Promesar Unidad debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getSaldoPorVenderPromesarMonto() == null || hojaBi.getSaldoPorVenderPromesarMonto().equals("-")){
			final String error = "Saldo Por Vender o Promesar Monto no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaBi.getSaldoPorVenderPromesarMonto().doubleValue()<0){
				final String error = "Saldo Por Vender o Promesar Monto debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaBi.getComuna() == null || hojaBi.getComuna().equals("-")){
			final String error = "Comuna no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getDuracion() == null || hojaBi.getDuracion().equals("-")){
			final String error = "Duracion no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getTipoProducto() == null || hojaBi.getTipoProducto().equals("-")){
			final String error = "Tipo Producto no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getPrimeraOSegundaVivienda() == null || hojaBi.getPrimeraOSegundaVivienda().equals("-")){
			final String error = "Primera o Segunda Vivienda no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getConslnMdo() == null || hojaBi.getConslnMdo().equals("-")){
			final String error = "Consolidacion Mercado no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getExperienciaMdo() == null || hojaBi.getExperienciaMdo().equals("-")){
			final String error = "Experiencia Mercado no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getConstructora() == null || hojaBi.getConstructora().equals("-")){
			final String error = "Constructora no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getExperienciaConstructora() == null || hojaBi.getExperienciaConstructora().equals("-")){
			final String error = "Experiencia Constructora no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaBi.getOfertaCompetencia() == null || hojaBi.getOfertaCompetencia().equals("-")){
			final String error = "Oferta Competencia no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		return errorFila;
	}

	public Boolean eliminarBalance(String rutCLiente, String fechaAvance) {
		// TODO Auto-generated method stub
		GestorConstructoraInmobiliaria gestor = new GestorConstructoraInmobilariaImpl();
		return gestor.eliminarBalancesInmobiliarios(rutCLiente, fechaAvance);
	}
	
	public Date getFechaSbif() {
		return fechaSbif;
	}

	public void setFechaSbif(Date fechaSbif) {
		this.fechaSbif = fechaSbif;
	}

	

}
