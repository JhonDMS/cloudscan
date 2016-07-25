package com.bch.sefe.rating.srv.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.agricola.srv.GestorAgricola;
import com.bch.sefe.agricola.srv.impl.GestorAgricolaImpl;
import com.bch.sefe.agricola.vo.Agricola;
import com.bch.sefe.comun.vo.CriterioBusqueda;
import com.bch.sefe.constructora.srv.GestorConstructoraInmobiliaria;
import com.bch.sefe.constructora.srv.impl.GestorConstructoraInmobilariaImpl;
import com.bch.sefe.rating.dao.RatingProyectadoDAO;
import com.bch.sefe.rating.dao.impl.RatingProyectadoDAOImpl;
import com.bch.sefe.rating.impl.SEFERatingProyectadoException;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingProyectado;
import com.bch.sefe.rating.vo.BalanceInmobiliario;
import com.bch.sefe.rating.vo.HojaBalanceInmobiliario;
import com.bch.sefe.rating.vo.HojaSoe;
import com.bch.sefe.rating.vo.IndicadorBi;
import com.bch.sefe.rating.vo.IndicadorSoe;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.Soe;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.CatalogoVaciados;
import com.bch.sefe.vaciados.impl.CatalogoVaciadosImpl;
import com.bch.sefe.vaciados.srv.GestorPlanCuentas;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorPlanCuentasImpl;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;

public class GestorRatingProyectadoImpl implements GestorRatingProyectado {
	final static Logger log = Logger.getLogger(GestorRatingProyectadoImpl.class);
	
	/*
	 * (sin Javadoc)
	 * @see com.bch.sefe.rating.srv.GestorRatingProyectado#buscarCuentasPorPeriodo(java.lang.Long)
	 */
	public List buscarCuentasPorPeriodo(Long idVaciado) {
		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
		Double def = null;
		Collection cuentas = gestor.buscarCuentasParaRatingPorIdVacaciado(idVaciado, def);
		List cuentasRating = new ArrayList();
		
		final Integer TIPO_CUENTA_RATING = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_RATING);
		// se seleccionan las cuentas de rating
		Iterator iterator = cuentas.iterator();
		while (iterator.hasNext()) {
			Cuenta cta = (Cuenta) iterator.next();
			if (cta.getTipoCuenta().equals(TIPO_CUENTA_RATING)) {
				cuentasRating.add(cta); 
			}
		}
		
		return cuentasRating;
	}

	
	/*
	 * (sin Javadoc)
	 * @see com.bch.sefe.rating.srv.GestorRatingProyectado#crearVaciadoProyectado(com.bch.sefe.vaciados.vo.Vaciado, java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Long)
	 */
	public Vaciado crearVaciadoProyectado(Vaciado vac0, Long idCte, Long idUsr, Long idRating, Long idFinan) {
		GestorVaciados gestorVaciados = new GestorVaciadosImpl();
		
		// de determina la fecha contable del nuevo vaciado
		Calendar fechaProyectado = Calendar.getInstance();
		fechaProyectado.setTime(vac0.getPeriodo());
		
		// se corre un anno hasta el cierre anual siguiente
		fechaProyectado.add(Calendar.YEAR, 1);
		
		if (log.isDebugEnabled()) {
			log.debug("Creando vaciado proyectado");
		}
						
		// se crea una copia del vaciado y se setean la informacion de cliente y usuario
		Vaciado vacProyectado = (Vaciado) vac0.clone();
		Date fechaActual = new Date();
		vacProyectado.setIdParteInv(idCte);
		vacProyectado.setIdUsuario(idUsr);
		vacProyectado.setIdUsuModif(idUsr);
		//vacProyectado.setVersion(new Integer(idFinan.intValue()));
		vacProyectado.setVersion(gestorVaciados.generarNumeroVersion(vac0.getIdVaciado()));
		vacProyectado.setIdVaciado(null);
		vacProyectado.setIdEstado(ConstantesSEFE.ID_ESTADO_EN_CURSO);		
		vacProyectado.setCodOperacion(vac0.getIdVaciado().toString());
		vacProyectado.setFechaCreacion(fechaActual);
		vacProyectado.setFechaModif(fechaActual);
		
		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Fecha contable vaciado proyectado >> {0}", new String[] {fechaProyectado.getTime().toString()}));
		}
		
		// se fija la nueva fecha y el flag de proyeccion
		vacProyectado.setTipoProyeccion(ConstantesSEFE.FLAG_PROYECCION_ON);
		vacProyectado.setPeriodo(fechaProyectado.getTime());
		
		vacProyectado = gestorVaciados.crearVaciados(vacProyectado);
		
		if (log.isDebugEnabled()) {
			log.debug("\nSe ha creado el vaciado proyectado --> " + vacProyectado.getIdVaciado() + "\n");
		}
		
		return vacProyectado;
	}

	
	/*
	 * Busca la base de vaciado para el calculo del rating proyectado
	 * (sin Javadoc)
	 * @see com.bch.sefe.rating.srv.GestorRatingProyectado#buscarVaciadosRatingProyectado(com.bch.sefe.rating.vo.RatingFinanciero)
	 */
	public List buscarVaciadosRatingProyectado(RatingFinanciero rtgFinanciero, Integer idBanca, RatingIndividual ratingInd) {
		List vaciados = new ArrayList();
		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Iniciando busqueda de vaciados para rating proyectado", new String[] {}));
		}
		
		GestorVaciados gestorVac 	= new GestorVaciadosImpl();
		Vaciado ultimoVaciado		= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado0());
		Vaciado ultimoVaciadoAnual 		= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado1());
		Vaciado penultimoVaciadoAnual 	= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado2());
		
		if (log.isDebugEnabled()) {
			log.debug("Ultimo vaciado rating financiero..." + ultimoVaciado);
		}
		
		Integer planUltimoVaciado = ultimoVaciado.getIdTipoPlan();
		// cuando el ultimo vaciado cierre anual / parcial es IFRSCx
		if (ConstantesSEFE.CLASIF_ID_TPO_PLAN_IFRSCF.equals(planUltimoVaciado)  || 
				ConstantesSEFE.CLASIF_ID_TPO_PLAN_IFRSCN.equals(planUltimoVaciado)) {
			if (log.isDebugEnabled()) {
				log.debug("Ultimo vaciado es IFRSCx...");
			}
			
			// si no es cierre anual, y es parcial se busca el vaciado del último cierre anual, 
			// que debe corresponde al anterior entre la data del rating financiero
			if (!esCierreAnual(ultimoVaciado)) {
				Date fechaVaciado = ultimoVaciado.getPeriodo();
				Date fecha = FormatUtil.obtenerUltimoCierreAnual(fechaVaciado);
				if (!ultimoVaciado.getIdTipoPlan().equals(ultimoVaciadoAnual.getIdTipoPlan())) {
					ultimoVaciadoAnual = gestorVac.obtenerVaciadoPorPeriodo(ultimoVaciado.getIdVaciado(), fecha);
				}
				//ultimoVaciadoAnual 		= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado1());
				//penultimoVaciadoAnual 	= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado2());
				penultimoVaciadoAnual 	= gestorVac.buscarVaciadoAnterior(ultimoVaciadoAnual.getIdVaciado());
				if (log.isDebugEnabled()) {
					log.debug("Ultimo vaciado es parcial...se cambiara por ultimo cierre anual");
					log.debug("Ultimo cierre anual..." + ultimoVaciadoAnual);
					log.debug("Penultimo cierre anual..." + penultimoVaciadoAnual);
				}
			} else {
				ultimoVaciadoAnual 		= ultimoVaciado;
				penultimoVaciadoAnual 	= gestorVac.buscarVaciadoAnterior(ultimoVaciado.getIdVaciado());
				//penultimoVaciadoAnual 	= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado1());
				if (log.isDebugEnabled()) {
					log.debug("Ultimo vaciado es anual...");
					log.debug("Ultimo cierre anual..." + ultimoVaciadoAnual);
					log.debug("Penultimo cierre anual..." + penultimoVaciadoAnual);
					
				}
			}
		}
		
		Date fechaValidacionProy = null;
		if (ConstantesSEFE.BANCA_AGRICOLAS.equals(idBanca))
		{
			GestorAgricola gestorAgricola = new GestorAgricolaImpl();
			Agricola agricola = gestorAgricola.buscarVaciadoAgricola(ratingInd.getIdVaciadoAgricola());
			fechaValidacionProy = agricola.getFecha();
		}
		
		// se verifica la vigencia del vaciado
		if (!esVaciadoVigente(ultimoVaciado, idBanca, fechaValidacionProy)) {
			throw new SEFERatingProyectadoException(MessageManager.getMessage(ConstantesSEFE.MSG2_MODELO_RATING_PROYECTADO));
		}
			
	
		if (!esCierreAnual(ultimoVaciado)) {
			vaciados.add(ultimoVaciado);
			vaciados.add(ultimoVaciadoAnual);
			if (log.isDebugEnabled()) {
				log.debug("Vaciado seleccionado >> " + ultimoVaciado);
				log.debug("Vaciado seleccionado >> " + ultimoVaciadoAnual);
			}
			//vaciados.add(gestorVac.clonarVaciadoProyeccion(penultimoVaciadoAnual.getIdVaciado()));
			if (penultimoVaciadoAnual != null) {
				vaciados.add(penultimoVaciadoAnual);
			}
			if (log.isDebugEnabled()) {
				log.debug("Vaciado seleccionado >> " + penultimoVaciadoAnual);
			}
		}else {
			ultimoVaciadoAnual 		= ultimoVaciado;
			penultimoVaciadoAnual 	= gestorVac.buscarVaciadoAnterior(ultimoVaciado.getIdVaciado());
			vaciados.add(ultimoVaciadoAnual);
			vaciados.add(penultimoVaciadoAnual);

			if (log.isDebugEnabled()) {
				log.debug("Vaciado seleccionado >> " + ultimoVaciado);
				log.debug("Vaciado seleccionado >> " + ultimoVaciadoAnual);
			}
		}
		
		if (log.isInfoEnabled()) {
			log.info("Historia para proyeccion " + vaciados);
		}		
		return vaciados;
	}
	
	
	/*
	 * Busca la base de vaciado para el calculo del rating proyectado
	 * (sin Javadoc)
	 * @see com.bch.sefe.rating.srv.GestorRatingProyectado#buscarVaciadosRatingProyectado(com.bch.sefe.rating.vo.RatingFinanciero)
	 */
	public List buscarVaciadosRatingProyectado(RatingFinanciero rtgFinanciero) {
		List vaciados = new ArrayList();
		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Iniciando busqueda de vaciados para rating proyectado", new String[] {}));
		}
		
		GestorVaciados gestorVac 	= new GestorVaciadosImpl();
		Vaciado ultimoVaciado		= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado0());
		Vaciado ultimoVaciadoAnual 		= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado1());
		Vaciado penultimoVaciadoAnual 	= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado2());
		
		if (log.isDebugEnabled()) {
			log.debug("Ultimo vaciado rating financiero..." + ultimoVaciado);
		}
		
		Integer planUltimoVaciado = ultimoVaciado.getIdTipoPlan();
		// cuando el ultimo vaciado cierre anual / parcial es IFRSCx
		if (ConstantesSEFE.CLASIF_ID_TPO_PLAN_IFRSCF.equals(planUltimoVaciado)  || 
				ConstantesSEFE.CLASIF_ID_TPO_PLAN_IFRSCN.equals(planUltimoVaciado)) {
			if (log.isDebugEnabled()) {
				log.debug("Ultimo vaciado es IFRSCx...");
			}
			
			// si no es cierre anual, y es parcial se busca el vaciado del último cierre anual, 
			// que debe corresponde al anterior entre la data del rating financiero
			if (!esCierreAnual(ultimoVaciado)) {
				Date fechaVaciado = ultimoVaciado.getPeriodo();
				Date fecha = FormatUtil.obtenerUltimoCierreAnual(fechaVaciado);
				ultimoVaciadoAnual = gestorVac.obtenerVaciadoPorPeriodo(ultimoVaciado.getIdVaciado(), fecha);
				//ultimoVaciadoAnual 		= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado1());
				//penultimoVaciadoAnual 	= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado2());
				penultimoVaciadoAnual 	= gestorVac.buscarVaciadoAnterior(ultimoVaciadoAnual.getIdVaciado());
				if (log.isDebugEnabled()) {
					log.debug("Ultimo vaciado es parcial...se cambiara por ultimo cierre anual");
					log.debug("Ultimo cierre anual..." + ultimoVaciadoAnual);
					log.debug("Penultimo cierre anual..." + penultimoVaciadoAnual);
				}
			} else {
				ultimoVaciadoAnual 		= ultimoVaciado;
				penultimoVaciadoAnual 	= gestorVac.buscarVaciadoAnterior(ultimoVaciado.getIdVaciado());
				//penultimoVaciadoAnual 	= gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado1());
				if (log.isDebugEnabled()) {
					log.debug("Ultimo vaciado es anual...");
					log.debug("Ultimo cierre anual..." + ultimoVaciadoAnual);
					log.debug("Penultimo cierre anual..." + penultimoVaciadoAnual);
				}
			}
		}
	//			Calendar fechaUltimoVaciadoAnual = Calendar.getInstance();
//			fechaUltimoVaciadoAnual.setTime(ultimoVaciadoAnual.getPeriodo());
//			int annoAnterior = fechaUltimoVaciadoAnual.get(Calendar.YEAR) - 1;
			
//			// plan de cuentas de ultimo cierre anual debe ser IFRSCx
//			if (!planUltimoVaciado.equals( ultimoVaciadoAnual.getIdTipoPlan())) {
//				ultimoVaciadoAnual = buscarVaciadoIFRS(ultimoVaciadoAnual, annoAnterior, Calendar.DECEMBER);
//				if (ultimoVaciadoAnual == null) {
//					if (log.isInfoEnabled()) {
//						log.info("No se puede encontrar ultimo vaciado anual IFRSCx...");
//					}
//					throw new SEFERatingProyectadoException(MessageManager.getMessage(ConstantesSEFE.MSG4_MODELO_RATING_PROYECTADO));
//				}
//			}
			
//			// el plan de cuentas X-2 tambien debe ser IFRSCx
//			if (!planUltimoVaciado.equals(ultimoVaciadoAnual.getIdTipoPlan())) {
//				penultimoVaciadoAnual = buscarVaciadoIFRS(ultimoVaciadoAnual, annoAnterior - 1, Calendar.DECEMBER);
//				if (ultimoVaciadoAnual == null) {
//					if (log.isInfoEnabled()) {
//						log.info("No se puede encontrar penultimo vaciado anual IFRSCx...");
//					}
//					throw new SEFERatingProyectadoException(MessageManager.getMessage(ConstantesSEFE.MSG4_MODELO_RATING_PROYECTADO));
//				}
//			}
//		}
		
		if (!esCierreAnual(ultimoVaciado)) {
			vaciados.add(ultimoVaciado);
			vaciados.add(ultimoVaciadoAnual);
			if (log.isDebugEnabled()) {
				log.debug("Vaciado seleccionado >> " + ultimoVaciado);
				log.debug("Vaciado seleccionado >> " + ultimoVaciadoAnual);
			}
			//vaciados.add(gestorVac.clonarVaciadoProyeccion(penultimoVaciadoAnual.getIdVaciado()));
			if (penultimoVaciadoAnual != null) {
				vaciados.add(penultimoVaciadoAnual);
			}
			if (log.isDebugEnabled()) {
				log.debug("Vaciado seleccionado >> " + penultimoVaciadoAnual);
			}
		}else {
			vaciados.add(ultimoVaciadoAnual);
			if (penultimoVaciadoAnual != null) {
				vaciados.add(penultimoVaciadoAnual);
			}
			if (log.isDebugEnabled()) {
				log.debug("Vaciado seleccionado >> " + ultimoVaciadoAnual);
				log.debug("Vaciado seleccionado >> " + penultimoVaciadoAnual);
			}
		}
		
		if (log.isInfoEnabled()) {
			log.info("Historia para proyeccion " + vaciados);
		}		
		return vaciados;
	}
	
	
	/*
	 * Busca el vaciado IFRSCx correspondiente al vaciado pasado como argumento,
	 * el anno y mes indicado
	 */
	private Vaciado buscarVaciadoIFRS(Vaciado ultimoVaciadoAnual, int annoCierre, int mesCierre) {
		CriterioBusqueda criterio = new CriterioBusqueda();
		criterio.setParteInvol(ultimoVaciadoAnual.getIdParteInv());
		criterio.setAnioEEFF(String.valueOf(annoCierre));
		criterio.setMesEEFF(String.valueOf(mesCierre));
		criterio.setIdPlanCtas(ultimoVaciadoAnual.getIdTipoPlan());
		criterio.setIdEstado(new Long(ultimoVaciadoAnual.getIdEstado().intValue()));
		
		CatalogoVaciados catalogo = new CatalogoVaciadosImpl();
		Collection resultados = catalogo.buscarVaciados(criterio);
		
		Vaciado vac = null;
		if (resultados != null && !resultados.isEmpty()) {
			vac = (Vaciado) resultados.toArray()[0];
		}
		
		return vac;
	}


	/*
	 * Retorna true si la fecha del estado financiero del vaciado es cierre anual; 
	 * falso en caso contrario
	 */
	public boolean esCierreAnual(Vaciado vac) {
//		Calendar fechaVac = Calendar.getInstance();
//		fechaVac.setTime(vac.getPeriodo());
//		
//		return (fechaVac.get(Calendar.MONTH) == Calendar.DECEMBER);
		return (vac.getMesesPer().intValue()== 12);
	}
	
	
	/*
	 * Determina si el vaciado pasado como argumento tiene la vigencia requerida
	 * para el calculo del rating proyectado
	 */
	public boolean esVaciadoVigente(Vaciado vaciado, Integer idBanca, Date fechaAvance) {
		// se determina el periodo de vigencia del ultimo vaciado
		GestorRating gestorRating = new GestorRatingImpl();
		Date periodo = vaciado.getPeriodo();
		Integer diasVigencia = gestorRating.obtenerAntiguedadMaximaVaciado(idBanca);
		if (ConstantesSEFE.BANCA_CONSTRUCTORAS.equals(idBanca)) {
			GestorConstructoraInmobiliaria constructora = new GestorConstructoraInmobilariaImpl();
			periodo = fechaAvance;
			diasVigencia = constructora.obtenerAntiguedadMaximaSoe(idBanca);
		}
		if (ConstantesSEFE.BANCA_INMOBILIARIAS.equals(idBanca)) {
			GestorConstructoraInmobiliaria constructora = new GestorConstructoraInmobilariaImpl();
			periodo = fechaAvance;
			diasVigencia = constructora.obtenerAntiguedadMaximaBI(idBanca);
		}
		if(ConstantesSEFE.BANCA_AGRICOLAS.equals(idBanca))
		{
			periodo = fechaAvance;
		}
		
		if (periodo == null) {
			periodo = new Date();
		}
		Calendar vigenteHasta = Calendar.getInstance();
		vigenteHasta.setTime(periodo);
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
		
		return (vigenteHasta.after(fechaActual));
	}


	/*
	 * Busca la lista de flags de opcionalidad asociados a la cuentas de ingreso para 
	 * las proyecciones
	 */
	public List buscarFlagsIngresoCuentasProyeccion(Integer idPlanCta, Integer idNombrePlan) {
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		
		List flags = dao.buscarFlagCuentasProyeccion(idPlanCta, idNombrePlan, ConstantesSEFE.CLASIF_ID_TPO_PROYECCION_CORTA);
		
		return flags;
	}
	
	
	/*
	 * Busca la lista de flags de opcionalidad asociados a la cuentas de ingreso para 
	 * las proyecciones
	 */
	public List buscarFlagsIngresoCuentasProyeccionLarga(Integer idPlanCta, Integer idNombrePlan) {
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		
		List flags = dao.buscarFlagCuentasProyeccion(idPlanCta, idNombrePlan, ConstantesSEFE.CLASIF_ID_TPO_PROYECCION_LARGA);
		
		return flags;
	}
	public BalanceInmobiliario insertarBalanceInmobiliario(BalanceInmobiliario balanceInmobiliario){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		dao.insertarBalanceInmobiliario(balanceInmobiliario);
		return balanceInmobiliario;
	}
	public void insertarHojaBalanceInmobiliario(HojaBalanceInmobiliario hojaBalanceInmobiliario){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		dao.insertarHojaBalanceInmobiliario(hojaBalanceInmobiliario);
	}
	public Soe insertarSoe(Soe soe){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		dao.insertarSoe(soe);
		return soe;
	}
	public void insertarHojaSoe(HojaSoe hojaSoe){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		dao.insertarHojaSoe(hojaSoe);
	}
	
	
	public List buscarBiXRut(String rutCliente){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		List balances = dao.buscarBiXRut(rutCliente);
		return balances;
	}
	
	public void insertarIndicadorSoe(IndicadorSoe indicadorSoe){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		dao.insertarIndicadorSoe(indicadorSoe);
	}
	public void insertarIndicadorBi(IndicadorBi indicadorBi){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		dao.insertarIndicadorBi(indicadorBi);
	}
	public void actualizarUsuarioBalance(Long idUsuario, Long idParteInvol, Date fechaAvance, String tabla){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		dao.actualizarUsuarioBalance(idUsuario, idParteInvol, fechaAvance, tabla);
	}
	
	public void actualizarDeudaBalanceInmobiliario(String rutConsulta, String deuda, Date fechaAvance){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		dao.actualizarDeudaBalanceInmobiliario(rutConsulta, deuda, fechaAvance);
	}
	
	public void borrarHojasEIndicadores(Long idParteInvol, Date fechaAvance, String tabla){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		dao.borrarHojasEIndicadores(idParteInvol, fechaAvance, tabla);
	}
	public Double obtenerDeudaXFechaAvance(String rutCliente , Integer tipoConsulta, Date fechaAvance){
		RatingProyectadoDAO dao = new RatingProyectadoDAOImpl();
		return dao.obtenerDeudaXFechaAvance(rutCliente, tipoConsulta, fechaAvance);
	}
}
