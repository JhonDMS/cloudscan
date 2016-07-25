package com.bch.sefe.rating.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.agricola.srv.GestorAgricola;
import com.bch.sefe.agricola.srv.impl.GestorAgricolaImpl;
import com.bch.sefe.agricola.vo.Agricola;
import com.bch.sefe.agricola.vo.ArriendoSuelo;
import com.bch.sefe.agricola.vo.Audit;
import com.bch.sefe.agricola.vo.CabeceraProductoAgricola;
import com.bch.sefe.agricola.vo.CalendarioDeuda;
import com.bch.sefe.agricola.vo.DeudaCortoPlazo;
import com.bch.sefe.agricola.vo.DeudaLargoPlazo;
import com.bch.sefe.agricola.vo.FlujoAgricola;
import com.bch.sefe.agricola.vo.FlujoGanadero;
import com.bch.sefe.agricola.vo.FlujoVentaGanaderia;
import com.bch.sefe.agricola.vo.IngresoEgreso;
import com.bch.sefe.agricola.vo.OtroActivoFijo;
import com.bch.sefe.agricola.vo.OtroConcepto;
import com.bch.sefe.agricola.vo.ParamGanaderia;
import com.bch.sefe.agricola.vo.ParamPlantacion;
import com.bch.sefe.agricola.vo.ParamPlantacionAno;
import com.bch.sefe.agricola.vo.ParamProductoGanaderia;
import com.bch.sefe.agricola.vo.ProductoAgricola;
import com.bch.sefe.agricola.vo.ProductoGanadero;
import com.bch.sefe.agricola.vo.Propiedad;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioCalculo;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.ServicioCalculoImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.GestorClasificaciones;
import com.bch.sefe.comun.srv.GestorOtrasFormulas;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.GestoresNegocioFactory;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.srv.impl.GestorOtrasFormulasImpl;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Archivo;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.PeriodoRating;
import com.bch.sefe.comun.vo.RatingProyectadoESP;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.ConstantesRating;
import com.bch.sefe.rating.ServicioRatingAgricola;
import com.bch.sefe.rating.ServicioRatingIndividual;
import com.bch.sefe.rating.srv.AlgoritmoRatingFinanciero;
import com.bch.sefe.rating.srv.GestorComponentesRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.ValidadorRatingProyectado;
import com.bch.sefe.rating.srv.impl.GestorComponentesRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingFinancieroImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.RatingUtil;
import com.bch.sefe.rating.srv.impl.ValidadorRatingProyectadoImpl;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.srv.impl.POIExcelReader;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;

public class ServicioRatingAgricolaImpl implements ServicioRatingAgricola{
	
	GestorAgricola gestorAgricola = new GestorAgricolaImpl();
	final static Logger log = Logger.getLogger(ServicioRatingAgricolaImpl.class);
	ServicioCalculo servicioCalculo  = new ServicioCalculoImpl();
	final static Logger logMotor = Logger.getLogger("track.motor");



	public RatingProyectadoESP generaProyeccionAgricola(String rutCliente, Long idRatingInd ,String logOperador, Long idVaciadoAgricola) {
		GestorAgricola gestorAgricola = new GestorAgricolaImpl();
		ValidadorRatingProyectado validador = new ValidadorRatingProyectadoImpl();
		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRatingInd);
		// se asocial el id del rating indiviadual a la SOE seleccionada
		Long idCte = buscarIdClientePorRut(rutCliente);
		Long idUsr = buscarIdUsuarioPorNombre(logOperador);
		Agricola agricola = gestorAgricola.buscarVaciadoAgricola(idVaciadoAgricola) ;		
		// si el soe está asociado a otro rating entonces se lanza una excepcion de negocio
		validador.esPosibleUtilizarVaciadoAgricola(agricola.getIdRatingInd(), idRatingInd, agricola.getIdEstado());
		// asociar el vaciado agricola al rating individual
		gestorAgricola.asociarAgricolaRatingIndividual(idCte, idVaciadoAgricola, idRatingInd);
		agricola.setIdRatingInd(idRatingInd);
		// borrar todos los flujos calculados antiormente si existe.
		gestorAgricola.borrarFlujos(agricola.getIdAgricola());
		// se calculan los flujos agricolas, ganaderos, calendario de pago y resumen
		calcularFlujos(agricola, idCte, idRatingInd);
		RatingProyectadoESP proyectado=  crearProyectado(rutCliente,agricola,idRatingInd ,idUsr, logOperador);
		proyectado =  generarProyeccion(rutCliente, idRatingInd ,logOperador, proyectado.getIdRating());
		proyectado =  consultarProyeccion(rutCliente, idRatingInd,proyectado.getIdRating(), logOperador, null);
		proyectado.setTemporada(agricola.getTemporada());
		proyectado.setFechaBalance(agricola.getFecha());
		proyectado.setDescripcion(agricola.getDescripcion());
		proyectado.setIdVaciadoAgricola(idVaciadoAgricola);
		return proyectado;
	}
	
	public RatingProyectadoESP consultarProyeccion(String rutCliente, Long idRating, Long idProy, String logUsu, String modo) {
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();	
		GestorRatingIndividual gestorIndiv= new GestorRatingIndividualImpl();
		Long idCte = buscarIdClientePorRut(rutCliente);
		RatingIndividual ratingInd = gestorIndiv.buscarRatingIndividual(idCte, idRating);
	
		// se busca el financiero vigente para verificar cambios...
		GestorComponentesRating gestorComp = new GestorComponentesRatingImpl();
	//	RatingFinanciero finanVigente = gestorComp.buscarRatingFinacieroVigente(idCte, ratingInd.getIdBanca());
			// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);

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

	private RatingProyectadoESP generarProyeccion(String rutCliente, Long idRatingInd,  String logUsu, Long idRatingProy) {
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();

		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		// se verifica el rating financiero
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();

		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRatingInd);
		// aqui se calcula el rating proyectado....
		proyeccion = (RatingProyectadoESP) calcularProyeccion(rutCliente, idRatingInd, idRatingProy, logUsu, null);

		
		proyeccion.getRatingFinanciero().setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		
		Long idCte = buscarIdClientePorRut(rutCliente);
		RatingIndividual ratingInd = gestorIndiv.buscarRatingIndividual(idCte, idRatingInd);
		
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
	
	
	private RatingProyectadoESP calcularProyeccion(String rutCliente, Long idRating, Long idProy, String logUsu, String valorDefault) {
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
	
	
	private RatingProyectadoESP crearProyectado(String rutCliente, Agricola agricola , Long idRatingInd, Long idUsr,String loginUsuario) {
		GestorVaciados gestorVaciado 					= new GestorVaciadosImpl();
		GestorRatingFinanciero gestorRatingFinanciero 	= new GestorRatingFinancieroImpl();
		Vaciado vaciado = gestorVaciado.buscarVaciadoAgricola(agricola.getIdAgricola());
		if (vaciado== null) {
			vaciado=gestorAgricola.crearVaciadoAgricola(agricola, agricola.getIdParteInv(),  idUsr);
		}
			
		if (log.isDebugEnabled()) {
			log.debug("Creando vaciado para rating proyectado...");
		}
		RatingProyectadoESP proyeccion = null;
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndiv = gestorIndiv.consultaRatingSugerido(agricola.getIdParteInv(), idRatingInd);

		if (ratingIndiv.getIdRatingProyectado() != null) {
			proyeccion = consultarProyeccion(rutCliente, idRatingInd, ratingIndiv.getIdRatingProyectado(), idUsr, null);
			if (proyeccion.getRatingFinanciero()!=null && proyeccion.getRatingFinanciero().getIdVaciado0()!=null &&
					proyeccion.getRatingFinanciero().getIdVaciado0().equals(vaciado.getIdVaciado()) && proyeccion.isMatrizVigente()){
				return proyeccion;
			}
		}
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero ratingBase = new RatingFinanciero();

		// se remueve el id de rating para que se genere uno nuevo para el
		// proyectado
		ratingBase.setIdUsuario(idUsr);
		ratingBase.setIdVaciado0(vaciado.getIdVaciado());
		ratingBase.setFechaBalance(vaciado.getPeriodo());
		ratingBase.setNumeroMeses(vaciado.getMesesPer());

		
		ratingBase.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		ratingBase.setFechaRating(new Date());
		RatingFinanciero rtgFinanciero = gestorRatingFinanciero.calcularRating(rutCliente, ratingIndiv.getIdBanca(), vaciado.getIdVaciado(), loginUsuario);
		
		if (rtgFinanciero.getIdMatriz() == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_PROY_ERROR_MATRIZ_VIGENTE_NO_EXISTE));
		}
		ratingBase.setIdMatriz(rtgFinanciero.getIdMatriz());
		RatingFinanciero ratingProyectado = null;
		proyeccion = new RatingProyectadoESP();
		ratingProyectado = gestorFinanciero.grabarRatingProyectado(ratingBase);
		proyeccion.setIdRating(ratingProyectado.getIdProyectado());

		// se guardan los valores intermedios de los calculos y puntajes
		// si no hay rating financiero para el vaciado cabecera entonces se
		// generar un nuevo calculo y nuevo rating financiero
	//	gestorFinanciero.grabarEvaluacionesProyectado(rtgFinanciero);

		if (log.isInfoEnabled()) {
			log.info("> > > > Actualizando cuentas con los valores ingresados ");
		}			
		//gestorIndiv.actualizarRatingProyectado(agricola.getIdParteInv(), idRatingInd, rtgFinanciero);
		if (log.isInfoEnabled()) {
			log.info("Creando rating proyectado #" + ratingProyectado.getIdRating());
		}

		return proyeccion;
	}
	
	
	public RatingProyectadoESP confirmarProyeccion(String rutCliente,String logUsu, Long idRating, Long idProy, Long idAgricolaSelecciondo) {
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();
		GestorAgricola gestorAgricola = new GestorAgricolaImpl();
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		ValidadorRatingProyectado validador = new ValidadorRatingProyectadoImpl();
		// se actualiza el rating individual
		Long idCte = buscarIdClientePorRut(rutCliente);
		RatingIndividual ratingInd = gestorIndiv.buscarRatingIndividual(idCte, idRating);
		// ValidadorVaciados validador = new ValidadorVaciadosImpl();
		// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();		
		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerPrimerUsuario(logUsu);
		Agricola agricola = gestorAgricola.buscarVaciadoAgricola(idAgricolaSelecciondo) ;
		// si el soe está asociado a otro rating entonces se lanza una excepcion de negocio
		validador.esPosibleUtilizarVaciadoAgricola(agricola.getIdRatingInd(), idRating, agricola.getIdEstado());
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);
		validador.sonIgualesVaciadosAgricolas(idAgricolaSelecciondo, agricola.getIdAgricola());
		validador.existeVaciadoAgricola(agricola);
		validador.esPosibleUtilizarVaciadoAgricola(agricola.getIdRatingInd(), idRating, agricola.getIdEstado());
		agricola.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);
		gestorAgricola.actualizarVaciadoAgricola(agricola);
		
		rtgFinanciero.setResponsable(usr.getCodigoUsuario());
		rtgFinanciero.setIdUsuario(usr.getUsuarioId());
		rtgFinanciero.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);
		rtgFinanciero.setFechaRating(new Date());
		rtgFinanciero = gestorFinanciero.actualizarRatingProyectado(ratingInd.getIdBanca(), rtgFinanciero);

		if (log.isInfoEnabled()) {
			log.info(MessageFormat
					.format("Rating proyectado #{0} confirmado!", new String[] { rtgFinanciero.getIdProyectado().toString() }));
		}
		// ============================================================================
		
		rtgFinanciero.setResponsable(usr.getCodigoUsuario());
		rtgFinanciero.setIdUsuario(usr.getUsuarioId());
		rtgFinanciero.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);

		rtgFinanciero = gestorFinanciero.actualizarRatingProyectado(ratingInd.getIdBanca(), rtgFinanciero);
		// se calculan los valores del rating individual
		gestorIndiv.calcularRating(idCte, idRating);
		// ============================================================================

		proyeccion =  consultarProyeccion(rutCliente, idRating, idProy, usr.getUsuarioId(),
				ConstantesRating.MODO_CONSULTA_DEFAULT);
		proyeccion.setTemporada(agricola.getTemporada());
		proyeccion.setFechaBalance(agricola.getFecha());
		proyeccion.setDescripcion(agricola.getDescripcion());
		proyeccion.setIdVaciadoAgricola(idAgricolaSelecciondo);
		return proyeccion;
	}

	private RatingProyectadoESP consultarProyeccion(String rutCliente, Long idRatingInd, Long idProy, Long idUsr, String modo) {
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();

		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();
		Long idCte = buscarIdClientePorRut(rutCliente);
		RatingIndividual ratingInd = gestorRating.buscarRatingIndividual(idCte, idRatingInd);

		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);
		proyeccion.setRatingFinanciero(rtgFinanciero);
		MatrizFinanciera matriz = gestorFinanciero.obtenerMatrizFinanciera(rtgFinanciero.getIdMatriz());
		proyeccion.setMatrizVigente(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE.equals(matriz.getEstadoId()));

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

	public void calcularFlujos(Agricola agricola, Long idCliente, Long idRatingInd) {		
		// obtiene todos los productos asociado al vaciado agricola
		List listaProductos = gestorAgricola.obtenerListaProductosAgricolas(agricola.getIdAgricola());
		
		// se obtiene el factor correccion para las ventas de lanas
		// el vaciado agricola siempre esta en CLP y unidad. La conversion siempre sera de pesos a dolares
		Calendar fechaTemporada = Calendar.getInstance();
		fechaTemporada.setTime(agricola.getFecha());
		fechaTemporada.add(Calendar.MONTH, -1);
		Date fechaVaciado = FormatUtil.ultimoDiaCalendario(fechaTemporada.getTime());
		ConversorMoneda conversor = new ConversorMonedaImpl();
		Double factorCorreccion = conversor.convertirMonedaSegunReglas(ConstantesSEFE.DOUBLE_UNO, ConstantesSEFE.ID_CLASIF_MONEDA_USD, ConstantesSEFE.ID_CLASIF_UNIDAD, ConstantesSEFE.ID_CLASIF_MONEDA_CLP, ConstantesSEFE.ID_CLASIF_UNIDAD, fechaVaciado);
		Integer  anteriorProducto = Integer.valueOf("0");
		for (int i=0; i<listaProductos.size(); i++) {
			ProductoAgricola productoAgricola = (ProductoAgricola)listaProductos.get(i);
	//		if (!anteriorProducto.equals( productoAgricola.getIdProductoAgricola())) {
				// obtener la cabecera del producto. La cabecera contiene: ano proyectados, suma producto de ano por rendimiento, etc
				CabeceraProductoAgricola cabeceraProductoAgricola = gestorAgricola.obtenerCabeceraProductosAgricolas(agricola.getIdAgricola(), productoAgricola.getIdParamProductoAgricola());
				// se recupera los anos proyectados de un producto con el objetivo de preparar el contexto de calculo.
				List anosProyectados = gestorAgricola.obtenerProyeccionProductoAgricola(agricola.getIdAgricola(), productoAgricola.getIdParamProductoAgricola());
				// se crea el contexto de la cabecera para ser utilizado en el calculo por ano proyectado. La lista nunca será vacia por lo cual no se valida el null pointer exception para
				// recuperar los anos proyectados
				Map ctx = obtenerContextoCabeceraProductoAgricola(cabeceraProductoAgricola, factorCorreccion, new Double(anosProyectados.size()));
				
				// se carga en contexto de calculo todos los anos para un producto
				Double rendimientoPeriodo6 = ConstantesSEFE.DOUBLE_CERO;
				Double costoPeriodo6 = ConstantesSEFE.DOUBLE_CERO;
				for (int j=0; j<anosProyectados.size(); j++) {
					int edad=j+1;
					ProductoAgricola productoPorAno = (ProductoAgricola)anosProyectados.get(j);
					if (productoAgricola.getEdad().intValue() == 1 ) {
						ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+edad, productoPorAno.getRendimiento());
						ctx.put(ConstantesSEFE.CTX_COSTO+edad,productoPorAno.getCosto());
					}
					if (productoAgricola.getEdad().intValue() == 2 ) {
						ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+(edad-1), productoPorAno.getRendimiento());	
						ctx.put(ConstantesSEFE.CTX_COSTO+(edad-1),productoPorAno.getCosto());
					}
					if (productoAgricola.getEdad().intValue() == 3 ) {
						ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+(edad-2), productoPorAno.getRendimiento());
						ctx.put(ConstantesSEFE.CTX_COSTO+(edad-2),productoPorAno.getCosto());
					}
					if (productoAgricola.getEdad().intValue() == 4 ) {
						ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+(edad-3), productoPorAno.getRendimiento());
						ctx.put(ConstantesSEFE.CTX_COSTO+(edad-3),productoPorAno.getCosto());
					}
					if (productoAgricola.getEdad().intValue() == 5 ) {
						ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+(edad-4), productoPorAno.getRendimiento());
						ctx.put(ConstantesSEFE.CTX_COSTO+(edad-4),productoPorAno.getCosto());
					}
					if (productoAgricola.getEdad().intValue() == 6 ) {
						ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+(edad-5), productoPorAno.getRendimiento());
						ctx.put(ConstantesSEFE.CTX_COSTO+(edad-5),productoPorAno.getCosto());
					}
					rendimientoPeriodo6 = productoPorAno.getRendimiento();
					costoPeriodo6 = productoPorAno.getCosto();
					ctx.put(ConstantesSEFE.CTX_HA_PRODUCTO+edad, productoPorAno.getSuperficie());
					
					ctx.put(ConstantesSEFE.CTX_PRC_EXPORTACION, productoPorAno.getPorcentajeExportacion());
					ctx.put(ConstantesSEFE.CTX_PRECIO_NACIONAL, productoPorAno.getPrecioNacional());
					ctx.put(ConstantesSEFE.CTX_PRECIO_PRODUCTO, productoPorAno.getPrecioExportacion());	
					anteriorProducto =productoAgricola.getIdParamProductoAgricola();
				}
				if (productoAgricola.getEdad().intValue() == 2 ) {
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+6, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+6,costoPeriodo6);
				}
				if (productoAgricola.getEdad().intValue() == 3 ) {
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+5, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+6, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+5,costoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+6,costoPeriodo6);
				}
				if (productoAgricola.getEdad().intValue() == 4) {
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+4, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+5, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+6, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+4,costoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+5,costoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+6,costoPeriodo6);
				}
				if (productoAgricola.getEdad().intValue() == 5 ) {
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+3, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+4, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+5, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+6, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+3,costoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+4,costoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+5,costoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+6,costoPeriodo6);
				}
				if (productoAgricola.getEdad().intValue() == 6 ) {
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+2, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+3, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+4, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+5, rendimientoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_AGRICOLA_REND+6, rendimientoPeriodo6);
					
					ctx.put(ConstantesSEFE.CTX_COSTO+2,costoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+3,costoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+4,costoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+5,costoPeriodo6);
					ctx.put(ConstantesSEFE.CTX_COSTO+6,costoPeriodo6);
				}
				
				
				Iterator itCtx = ctx.keySet().iterator();
				while (itCtx.hasNext()) {
					String key = itCtx.next().toString();
					servicioCalculo.ponerEnContexto(key, ctx.get(key));
				}
				List listaOtrasFormulas = calcularOtrasCuentas(  ConstantesSEFE.CLASIF_ORIGEN_INFO_PRODUCTO_AGRICOLA);
				gestorAgricola.guardarFlujoAgricola(listaOtrasFormulas,productoAgricola.getIdProductoAgricola(),productoAgricola.getPorcentajeExportacion());
//			}
//			anteriorProducto =productoAgricola.getIdProductoAgricola();
			
		}	
		// se pobla el flujo ganadero asociado la id agricola consultado
		gestorAgricola.guardarFlujoGanadero(agricola.getIdAgricola());
		//TODO: SE DEBE INVOCAR EL SERVICIO PARA OBTENER EL TIPO DE CAMBIO
		gestorAgricola.guardarFlujoVentaGanadero(agricola.getIdAgricola(), factorCorreccion);
		guardarCalendarioPago(agricola.getIdAgricola());
		calcularFlujoResumen(agricola.getIdAgricola(), agricola.getTemporada());
	}
	
	
	private void guardarCalendarioPago(Long idAgricola) {
		try {
			GestorAgricola gestorAgricola = new GestorAgricolaImpl();
			Agricola agricola = gestorAgricola.buscarVaciadoAgricola(idAgricola);
			//Primer Ano: TA_SEFE_AGR_CALENDARIO_DEU_LP.SALDO_INICIAL = TA_SEFE_AGR_DEU_LARGO_PLAZO.MONTO_ACTUAL where TPO_DEU_ID = 6906 (1 registro)
			List deudaLargoPlazo = gestorAgricola.buscarDeudaLargoPlazo(idAgricola,ConstantesSEFE.ID_DEUDA_LARGO_PLAZO );
			//TA_SEFE_AGR_CALENDARIO_DEU_LP.INTERESES = TA_SEFE_AGR_CALENDARIO_DEU_LP.SALDO_INICIAL * NOMBRE_CORTO WHERE CLASIF_ID = 900437;
			//TASA DE INTERES (GASTOS FINANCIEROS) - HOJA FLUJOS Y HOJA DEUDAS
			
			Double gastoCalendarioDeudaLP = ConfigDBManager.getValueAsDouble(ConstantesSEFE.KEY_TASA_INTERES_GASTOS_FINANCIEROS);
			gastoCalendarioDeudaLP = new Double(gastoCalendarioDeudaLP.doubleValue()/100);
			for (int i=0; i<deudaLargoPlazo.size(); i++) {
				DeudaLargoPlazo deuda = (DeudaLargoPlazo)deudaLargoPlazo.get(i);
				int temporadoInicial = agricola.getTemporada().intValue();
				Double saldoFinal = null;
				//todo:valor 15 debe ser pasado a parametros
				for (int CONTADOR=1; CONTADOR<= 15; CONTADOR++) {
					logMotor.info("CONTADOR="+CONTADOR);
					CalendarioDeuda calendario = new CalendarioDeuda();
					if (saldoFinal == null) {
						calendario.setSaldoInicial(deuda.getMontoActual());
					}else {
						calendario.setSaldoInicial(saldoFinal);
					}
					logMotor.info("saldo inicial="+calendario.getSaldoInicial());
					logMotor.info("deuda largoPlazo="+deuda.getIdDeudaLargoPlazo());
					calendario.setIdDeudalargoPlazo(deuda.getIdDeudaLargoPlazo());
					calendario.setDetalleFlag(new Integer(1));
					calendario.setAno(new Integer(temporadoInicial));
					calendario.setIntereses(new Double(calendario.getSaldoInicial().doubleValue()*gastoCalendarioDeudaLP.doubleValue()));
					logMotor.info("intereses=calendario.getSaldoInicial().doubleValue()*gastos.doubleValue()="+calendario.getIntereses());
					logMotor.info("gastos="+gastoCalendarioDeudaLP.doubleValue());

					Double anosReales = new Double (agricola.getTemporada().intValue()-deuda.getAnoOtorgamiento().intValue());
					logMotor.info("ANOS_REALES}	= agricola.getTemporada().intValue()-deuda.getAnoOtorgamiento().intValue()");
					logMotor.info("getTemporada}"+agricola.getTemporada());
					logMotor.info("getAnoOtorgamiento}"+deuda.getAnoOtorgamiento());
					logMotor.info("ANOS_REALES}	= "+anosReales.doubleValue());
					Double saldoAnosGracia;
					// {SALDO_ANOS_GRACIA} = SI(( {ANOS_GRACIA} - {ANOS_REALES} )<0;0;( {ANOS_GRACIA} - {ANOS_REALES} ))
					if ((deuda.getPeriodosGracia().doubleValue()-anosReales.doubleValue())<0) {
						saldoAnosGracia = ConstantesSEFE.DOUBLE_CERO;
					} else { // {ANOS_GRACIA} - {ANOS_REALES} ))
						saldoAnosGracia = new Double (deuda.getPeriodosGracia().doubleValue()- anosReales.doubleValue());
					}
					logMotor.info("getPeriodosGracia	= "+deuda.getPeriodosGracia());
					logMotor.info("anosReales	= "+anosReales);
					logMotor.info("ANOS_GRACIA}	= "+saldoAnosGracia.doubleValue());
					//{PLAZO_EFECTIVO} = (TA_SEFE_AGR_VAC.TEMPORADA - TA_SEFE_AGR_CALENDARIO_DEU_LP.ANO)
					//Double plazoEfectivo = new Double (agricola.getTemporada().intValue()- calendario.getAno().doubleValue());
					Double plazoEfectivo = new Double (deuda.getPlazo().doubleValue()- anosReales.doubleValue()-saldoAnosGracia.doubleValue());
					logMotor.info("PLAZO_EFECTIVO}	= deuda.getPlazo().doubleValue()- anosReales.doubleValue()-saldoAnosGracia.doubleValue()");
					logMotor.info("getPlazo	= "+deuda.getPlazo());
					logMotor.info("anosReales	= "+anosReales);
					logMotor.info("saldoAnosGracia	= "+saldoAnosGracia);
					logMotor.info("PLAZO_EFECTIVO}	= "+plazoEfectivo.doubleValue());	
					BigDecimal cuotaFinal = new BigDecimal(1);
					if (plazoEfectivo.doubleValue()<=0){
						plazoEfectivo = new Double(1);
					}
					if (plazoEfectivo.doubleValue()>0){
						// {CUOTA} = (TA_SEFE_AGR_DEU_LARGO_PLAZO.MONTO_ACTUAL * (NOMBRE_CORTO WHERE CLASIF_ID = 900437)*
						//(1 + (NOMBRE_CORTO WHERE CLASIF_ID = 900437))^{PLAZO_EFECTIVO})/((1 + (NOMBRE_CORTO WHERE CLASIF_ID = 900437))^{PLAZO_EFECTIVO}-1)					
						BigDecimal cuota1 = new BigDecimal( deuda.getMontoActual().doubleValue() * gastoCalendarioDeudaLP.doubleValue());
						logMotor.info("cuota1	=deuda.getMontoActual().doubleValue() * gastos.doubleValue()= "+cuota1.doubleValue());
						logMotor.info("deuda.getMontoActual()="+deuda.getMontoActual());
						logMotor.info("gastos="+gastoCalendarioDeudaLP);
						//(1 + (NOMBRE_CORTO WHERE CLASIF_ID = 900437))^{PLAZO_EFECTIVO})
						BigDecimal cuota2 = new BigDecimal( 1 + gastoCalendarioDeudaLP.doubleValue());	
						logMotor.info("valor inicial cuota2	=new BigDecimal( 1 + gastos.doubleValue()"+cuota2);
						logMotor.info("plazoEfectivo	="+plazoEfectivo);
						cuota2 = new BigDecimal(Math.pow(cuota2.doubleValue(), plazoEfectivo.doubleValue()));	
						logMotor.info("cuota2	= BigDecimal(Math.pow(cuota2.doubleValue(), plazoEfectivo.doubleValue()))="+cuota2);
						//((1 + (NOMBRE_CORTO WHERE CLASIF_ID = 900437))^{PLAZO_EFECTIVO}-1)
						BigDecimal cuota3 = new BigDecimal( 1 + gastoCalendarioDeudaLP.doubleValue());
						logMotor.info("cuota3	=new BigDecimal( 1 + gastos.doubleValue() ="+cuota3);
						cuota3 = (new BigDecimal(Math.pow(cuota3.doubleValue(), plazoEfectivo.doubleValue()))).subtract(new BigDecimal(1));
						logMotor.info("cuota3	=new BigDecimal(Math.pow(cuota3.doubleValue(), plazoEfectivo.doubleValue()) - 1="+cuota3);
						cuotaFinal = new BigDecimal((cuota1.doubleValue()*cuota2.doubleValue())/cuota3.doubleValue());
						logMotor.info("cuotaFinal = new BigDecimal((cuota1.doubleValue()*cuota2.doubleValue())/cuota3.doubleValue())="+cuotaFinal);
						cuotaFinal =cuotaFinal.setScale(2, BigDecimal.ROUND_HALF_UP);				
						logMotor.info("cuotaFinal =cuotaFinal.setScale(2, BigDecimal.ROUND_HALF_UP);="+cuotaFinal);
					}
					//Amortizaciones
					//TA_SEFE_AGR_CALENDARIO_DEU_LP.AMORTIZACIONES = SI({CONTADOR} > ( {SALDO_ANOS_GRACIA} + {PLAZO_EFECTIVO} ) ; 0 ;
					//SI( {CONTADOR} <= {SALDO_ANOS_GRACIA} ; 0 ; ({CUOTA} - {INTERES}) ))
					// {CONTADOR} = Corresponde ano de crédito.
					logMotor.info("CONTADOR>(saldoAnosGracia.intValue()+plazoEfectivo.intValue()");
					if (CONTADOR>(saldoAnosGracia.intValue()+plazoEfectivo.intValue())) {
						calendario.setAmortizaciones(ConstantesSEFE.DOUBLE_CERO);				
						logMotor.info("AMOTIRZACIONES=="+calendario.getAmortizaciones());
					}else {
						//SI( {CONTADOR} <= {SALDO_ANOS_GRACIA} ; 0 ; ({CUOTA} - {INTERES}) ))
						if (CONTADOR<= saldoAnosGracia.doubleValue() ) {
							calendario.setAmortizaciones(ConstantesSEFE.DOUBLE_CERO);	
							logMotor.info("AMOTIRZACIONES=="+calendario.getAmortizaciones());
						}else { 
							calendario.setAmortizaciones(new Double(cuotaFinal.doubleValue()-calendario.getIntereses().doubleValue()));
							logMotor.info("AMOTIRZACIONES=new Double(cuotaFinal.doubleValue()-calendario.getIntereses().doubleValue()="+calendario.getAmortizaciones());
						}
					}
					
					logMotor.info("calendario.getIntereses()"+calendario.getIntereses().doubleValue());
				
					// Cuota
					//  TA_SEFE_AGR_CALENDARIO_DEU_LP.CUOTA = TA_SEFE_AGR_CALENDARIO_DEU_LP.INTERESES + TA_SEFE_AGR_CALENDARIO_DEU_LP.AMORTIZACIONES
					calendario.setCuota(new Double(calendario.getIntereses().doubleValue()+ calendario.getAmortizaciones().doubleValue()));
					logMotor.info("CUOTA=calendario.getIntereses().doubleValue()+ calendario.getAmortizaciones().doubleValue()="+calendario.getCuota());
					//Saldo Final
					
					//  TA_SEFE_AGR_CALENDARIO_DEU_LP.SALDO_FINAL = TA_SEFE_AGR_CALENDARIO_DEU_LP.SALDO_INICIAL - TA_SEFE_AGR_CALENDARIO_DEU_LP.AMORTIZACIONES
					calendario.setSaldoFinal(new Double(calendario.getSaldoInicial().doubleValue()-calendario.getAmortizaciones().doubleValue()));
					logMotor.info("new Double(calendario.getSaldoInicial().doubleValue()-calendario.getAmortizaciones().doubleValue())="+calendario.getSaldoFinal());
					saldoFinal = calendario.getSaldoFinal();
					gestorAgricola.insertarCalendarioDeudaPL(calendario);
					// fin de la deuda
					temporadoInicial++;
					if (CONTADOR>=(saldoAnosGracia.intValue()+plazoEfectivo.intValue())) {
						break;
					}
				}
			}
		}catch (Exception e) {
			log.error("",e);
			throw new BusinessOperationException("No fue posible calcular el Calendario Pago");
		}
		
		
	}

	public void calcularFlujoResumen(Long idAgricola, Integer temporada) {
		// se recuperan los totalizadores para el flujo agricola agrupado por ano
		List cabeceraFlujoAgricola = gestorAgricola.buscarCabeceraFlujoAgricola(idAgricola);
		// se recuperan los totalizadores para el flujo ganadero
		FlujoGanadero cabeceraFlujoGanadero = gestorAgricola.buscarCabeceraFlujoGanadero(idAgricola);
		// se recupera los totalizadores para el flujo venta ganadero
		FlujoVentaGanaderia cabeceraFlujoVentaGanadero = gestorAgricola.buscarCabeceraFlujoVentaGanadero(idAgricola); 
		Map contextoCalculo = gestorAgricola.cargarContextoCalculo(idAgricola);
		Map ctx = new HashMap();
		ctx.putAll(contextoCalculo);
		// se prepara el contexto con los datos generales para todos los periodos proyectados
		prepararContextoCalculoDatosGenerales(idAgricola, ctx);
		//        Otros Ingresos Operacionales 6878
		List ingresoOperacionales = gestorAgricola.buscarIngresoEgreso(idAgricola, ConstantesSEFE.CLASIF_ID_OTROS_INGR_EXPLOT);
		//      OTROS EGRESOS OPERACIONALES EXPLOTACION
		List egresoOperacionales = gestorAgricola.buscarIngresoEgreso(idAgricola, ConstantesSEFE.CLASIF_ID_OTROS_EGRE_EXPLOT);
		//INGRESOS NO OPERAC. (FUERA EXPLOTACION)
		List ingresoNoOperacionales = gestorAgricola.buscarIngresoEgreso(idAgricola, ConstantesSEFE.CLASIF_ID_INGR_NO_OPERACIONALES);
		//   EGRESOS NO OPERAC. (FUERA EXPLOTACION)
		List egresoNoOperacionales = gestorAgricola.buscarIngresoEgreso(idAgricola, ConstantesSEFE.CLASIF_ID_EGRE_NO_OPERACIONALES);
		//INTERES_CONSOLIDADO =>suma de (TA_SEFE_AGR_CALENDARIO_DEU_LP.INTERESES)
		List lstCalendarioDeuda = gestorAgricola.buscarCalendarioDeuda(idAgricola,ConstantesSEFE.ID_DEUDA_LARGO_PLAZO);		
		int temporadaInicial = temporada.intValue();
		List clasificacionRentaPresunta = ConfigDBManager.getValuesAsListString(ConstantesSEFE.CLASIF_ID_IMPUESTO_RENTA_I);
		List listaRentaPresuntaPorAno = new ArrayList();
		if (clasificacionRentaPresunta != null  ) {
			for (int i =0 ; i<clasificacionRentaPresunta.size(); i++) {
				listaRentaPresuntaPorAno.add(new Double(clasificacionRentaPresunta.get(i).toString()));
			}
		}
		ctx.put(ConstantesSEFE.SALDO_CAJA_ACUM_ANT,ConstantesSEFE.DOUBLE_CERO);	
		int interar=cabeceraFlujoAgricola.size();
		Double divisorRobVentas = null;
		if (interar ==0 ) {
			divisorRobVentas =  ConfigDBManager.getValueAsDouble(ConstantesSEFE.KEY_DIVISOR_ROB_VTAS);
			interar=divisorRobVentas.intValue();
		}
		//INICIO DGJO; 12/11/2014
			
		//divisorRobVentas =  ConfigDBManager.getValueAsDouble(ConstantesSEFE.KEY_DIVISOR_ROB_VTAS);
		//interar=divisorRobVentas.intValue();
		//FIN DGJO;  12/11/2014
		
		for (int i=0 ;i < interar; i++) {
			FlujoAgricola flujoAgricola = null ;
			if (divisorRobVentas == null){ // no hay productos agricolas 
				flujoAgricola = (FlujoAgricola) cabeceraFlujoAgricola.get(i);
			}
			// se pone en contexto el flujo agricola,la cabecera flujo ganadero y el flujo venta ganadero por ano proyectado
			prepararContextoCalculoFlujoResumen(flujoAgricola, cabeceraFlujoGanadero, cabeceraFlujoVentaGanadero,ctx);
			IngresoEgreso ingresoOperacional = null;
			IngresoEgreso egresoOperacional = null;
			IngresoEgreso iningresoNoOperacionalgresoOperacional = null;
			IngresoEgreso egresoNoOperacional = null;
			CalendarioDeuda calendarioDeuda = null;
			Double interes = null;
			Double amortizaciones = null;
			// no es necesario validar si la lista es nula porque tal accion es controlada por los daos.
			if (!ingresoOperacionales.isEmpty()) {
				ingresoOperacional = (IngresoEgreso)ingresoOperacionales.get(i);
				prepararContextoCalculoOtrosIngresosEgresos(ingresoOperacional, ctx);
			}
			if (!egresoOperacionales.isEmpty()) {
				egresoOperacional = (IngresoEgreso)egresoOperacionales.get(i);
				prepararContextoCalculoOtrosIngresosEgresos(egresoOperacional, ctx);
			}
			if (!ingresoNoOperacionales.isEmpty()) {
				iningresoNoOperacionalgresoOperacional = (IngresoEgreso)ingresoNoOperacionales.get(i);
				prepararContextoCalculoOtrosIngresosEgresos(iningresoNoOperacionalgresoOperacional, ctx);
			}if (!egresoNoOperacionales.isEmpty()) {
				egresoNoOperacional = (IngresoEgreso)egresoNoOperacionales.get(i);
				prepararContextoCalculoOtrosIngresosEgresos(egresoNoOperacional, ctx);
			}
			if ( i< listaRentaPresuntaPorAno.size()) {
				ctx.put(ConstantesSEFE.CTX_IMPUESTO_RENTA_I, listaRentaPresuntaPorAno.get(i));
			}
			if (!lstCalendarioDeuda.isEmpty()) {
				if (i<lstCalendarioDeuda.size()) {
					calendarioDeuda = (CalendarioDeuda)lstCalendarioDeuda.get(i);
					interes = calendarioDeuda.getIntereses();
					amortizaciones =  calendarioDeuda.getAmortizaciones();
					
				}	
			}
			ctx.put(ConstantesSEFE.CTX_INTERES_CONSOLIDADO, interes != null ? interes: ConstantesSEFE.DOUBLE_CERO );
			ctx.put(ConstantesSEFE.CTX_AMORT_CONSOLIDADO_DEU, amortizaciones != null ? amortizaciones: ConstantesSEFE.DOUBLE_CERO  );
			Iterator itCtx = ctx.keySet().iterator();
			while (itCtx.hasNext()) {
				String key = itCtx.next().toString();
				servicioCalculo.ponerEnContexto(key, ctx.get(key));
			}
			List listaOtrasFormulas = calcularOtrasCuentas(  ConstantesSEFE.CLASIF_ORIGEN_INFO_FLUJO_RESUMEN);
			gestorAgricola.guardarFlujoResumen(idAgricola,listaOtrasFormulas, new Integer(temporadaInicial), ctx);
			temporadaInicial++;
		
		}
	//	throw new BusinessOperationException("No fue posible calcular el flujo de resumen");
		
	}
	
	/**
	 * pone en contexto todos los tipos de ingresos o egresos por ano proyectado
	 * @param ingresoOperacional
	 * @param ctx
	 */
	private void prepararContextoCalculoOtrosIngresosEgresos(IngresoEgreso ingresoOperacional, Map ctx) {
		if (ConstantesSEFE.CLASIF_ID_OTROS_INGR_EXPLOT.equals(ingresoOperacional.getTipo())) {
			ctx.put(ConstantesSEFE.CTX_OTROS_ING_OPER, ingresoOperacional.getValor());
		} else if (ConstantesSEFE.CLASIF_ID_OTROS_EGRE_EXPLOT.equals(ingresoOperacional.getTipo())) {
			ctx.put(ConstantesSEFE.CTX_OTROS_EGRE_OPER, ingresoOperacional.getValor());
		}else if (ConstantesSEFE.CLASIF_ID_INGR_NO_OPERACIONALES.equals(ingresoOperacional.getTipo())) {
			ctx.put(ConstantesSEFE.CTX_INGRESOS_NO_OPERAC, ingresoOperacional.getValor());
		}else if (ConstantesSEFE.CLASIF_ID_EGRE_NO_OPERACIONALES.equals(ingresoOperacional.getTipo())) {
			ctx.put(ConstantesSEFE.CTX_EGRESOS_NO_OPERAC, ingresoOperacional.getValor());
		}
	}

	/**
	 * metodo que pone en contexto todos los valores que debe ser usado en todos los periodos proyectados
	 * @param idAgricola
	 * @param ctx
	 */
	private void prepararContextoCalculoDatosGenerales(Long idAgricola, Map ctx) {
		GestorAgricola gestorAgricola = new GestorAgricolaImpl();
		Agricola agricola = gestorAgricola.buscarVaciadoAgricola(idAgricola);
		//RET_ANUALES_CLI => TA_SEFE_AGR_VAC.RETIROS_ANUALES 
		if (agricola.getRetirosAnuales()== null) {
			ctx.put(ConstantesSEFE.CTX_RET_ANUALES_CLI, ConstantesSEFE.DOUBLE_CERO);		
		}else {
			ctx.put(ConstantesSEFE.CTX_RET_ANUALES_CLI, agricola.getRetirosAnuales());
		}
		
		//GASTOS ADMINISTRACION =>  NOMBRE_CORTO WHERE CLASIF_ID = 900436;
		Double gastoAdmin = ConfigDBManager.getValueAsDouble(ConstantesSEFE.KEY_GASTOS_ADMINISTRACION);
		ctx.put(ConstantesSEFE.CTX_GASTOS_ADMINISTRACION,gastoAdmin);
		//CULTIVOS_SUP => TA_SEFE_AGR_ARR_SUELOS_AGRIC.SUPERFICIE where TPO_ARR_ID = 6871
		ArriendoSuelo arriendoCultivo = gestorAgricola.buscarArriendoSueloPorTipo(idAgricola, ConstantesSEFE.ID_CULTIVOS);
		if (arriendoCultivo != null) {
			ctx.put(ConstantesSEFE.CTX_CULTIVOS_SUP, arriendoCultivo.getSuperficie());
			//CULTIVOS_VAL => TA_SEFE_AGR_ARR_SUELOS_AGRIC.VAL_UND_SUPERFICIE where TPO_ARR_ID = 6871
			ctx.put(ConstantesSEFE.CTX_CULTIVOS_VAL, arriendoCultivo.getValorUnidadSuperficie());
		}
		//FRUTALES_SUP => TA_SEFE_AGR_ARR_SUELOS_AGRIC.SUPERFICIE where TPO_ARR_ID = 6872
		ArriendoSuelo arriendoFrutales = gestorAgricola.buscarArriendoSueloPorTipo(idAgricola, ConstantesSEFE.ID_FRUTALES);
		if (arriendoFrutales != null) {
			ctx.put(ConstantesSEFE.CTX_FRUTALES_SUP, arriendoFrutales.getSuperficie());
			//FRUTALES_VAL => TA_SEFE_AGR_ARR_SUELOS_AGRIC.VAL_UND_SUPERFICIE where TPO_ARR_ID = 6872
			ctx.put(ConstantesSEFE.CTX_FRUTALES_VAL, arriendoFrutales.getValorUnidadSuperficie());
		}	
		//OTR_ARR_AGR => TA_SEFE_AGR_ARR_SUELOS_AGRIC.VAL_TOT where TPO_ARR_ID = 6873
		ArriendoSuelo otroArriendoAgr = gestorAgricola.buscarArriendoSueloPorTipo(idAgricola, ConstantesSEFE.ID_OTR_ARR_AGR);
		if (arriendoFrutales != null) {
			ctx.put(ConstantesSEFE.CTX_OTR_ARR_AGR, otroArriendoAgr.getValorTotal());
		}	
		//TASA_INT => NOMBRE_CORTO WHERE CLASIF_ID = 900437;
		Double tasaInt =ConfigDBManager.getValueAsDouble(ConstantesSEFE.KEY_TASA_INTERES_GASTOS_FINANCIEROS);
		ctx.put(ConstantesSEFE.CTX_TASA_INT, tasaInt);		
		DeudaCortoPlazo deudaCortoPlazo = gestorAgricola.buscarConsolidadoDeudaCortoPlazo(idAgricola);
		//TOTAL_MONTO_APROB_CP => suma de (TA_SEFE_AGR_DEU_CORTO_PLAZO.MONTO_APROBADO)
		ctx.put(ConstantesSEFE.CTX_TOTAL_MONTO_APROB_CP, deudaCortoPlazo.getMontoAprobado());
		//TOTAL_MONTO_SOLIC_CP => suma de (TA_SEFE_AGR_DEU_CORTO_PLAZO.MONTO_SOLICITADO)
		ctx.put(ConstantesSEFE.CTX_TOTAL_MONTO_SOLIC_CP, deudaCortoPlazo.getMontoSolicitado());
		
	}
	
	private void prepararContextoCalculoFlujoResumen(FlujoAgricola resumenPorTemporada ,FlujoGanadero cabeceraFlujoGanadero,FlujoVentaGanaderia cabeceraFlujoVentaGanadero, Map ctx) {		
		ctx.put(ConstantesSEFE.CTX_SUMA_ING_TOTALES_PL, resumenPorTemporada != null? resumenPorTemporada.getIngresoTotal(): ConstantesSEFE.DOUBLE_CERO);
		ctx.put(ConstantesSEFE.CTX_SUMA_COST_TOTALES_PL, resumenPorTemporada != null? resumenPorTemporada.getCostoTotales(): ConstantesSEFE.DOUBLE_CERO);			
		if (cabeceraFlujoGanadero != null ) {
			ctx.put(ConstantesSEFE.CTX_SUMA_INGRESOS_ANIMALES	,cabeceraFlujoGanadero.getValorVenta());
			ctx.put(ConstantesSEFE.CTX_COSTOS_TOTALES_ANIMALES, cabeceraFlujoGanadero.getValorCosto());	
			ctx.put(ConstantesSEFE.CTX_SUMA_INGRESO_TOTAL_GAN, cabeceraFlujoVentaGanadero.getIngresoTotal());	
			ctx.put(ConstantesSEFE.CTX_SUMA_COSTO_TOTAL_GAN, cabeceraFlujoVentaGanadero.getCostoTotal());	
		}
				
	}
	/**
	 * metodo que recupera la lista de ctas  para ser calculadas en el motor de calculo.
	 * @param origenInfo
	 *           
	 * @return
	 */
	private List calcularOtrasCuentas( Integer origenInfo) {
		GestorOtrasFormulas gestorFormulas = new GestorOtrasFormulasImpl();	
		Integer idBanca = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		List listOtrasFormulas = gestorFormulas.obtenerListaFormulas(idBanca, ConstantesSEFE.OTRAS_FORMULAS_FLAG_COND_BORDE_OFF, origenInfo);
		servicioCalculo.setOtrasCuentas(listOtrasFormulas);
		for (int j=0; j<listOtrasFormulas.size();j++) {
			Cuenta cta = (Cuenta)listOtrasFormulas.get(j);
			// el id del vaciado es null porque las cuentas no estan asociadas a un vaciado.
			Cuenta ctaTemp =servicioCalculo.calcularCuentaIndependiente(null, cta);
			cta.setMonto(ctaTemp.getMonto());
		}
		return listOtrasFormulas;
	}
	
	
	private Map obtenerContextoCabeceraProductoAgricola(CabeceraProductoAgricola cabeceraProductoAgricola, Double moneda, Double anosProyectados) {
		Map ctx = new HashMap();
		ctx.put(ConstantesSEFE.CTX_ANOS_PROYECTADOS,anosProyectados);
		ctx.put(ConstantesSEFE.CTX_SUM_PRODUCT_ANO_X_REND,cabeceraProductoAgricola.getSumaProductoAnoRendimiento());
		ctx.put(ConstantesSEFE.CTX_SUM_SUPERFICIE, cabeceraProductoAgricola.getSumaSuperficie());
		ctx.put(ConstantesSEFE.CTX_MONEDA,moneda);	
		return ctx;
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
	
	
	
	private void transformVoDeudaCortoPlazo(ArrayList deudas, Long idAgricola, String tipo){
		final Integer GRUPO_TIPO_DEUDA_CORTO_PLAZO = Integer.valueOf("6911");
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = deudas.iterator(); iterator.hasNext();) {
			DeudaCortoPlazo deudaCortoPlazo = (DeudaCortoPlazo) iterator.next();
			deudaCortoPlazo.setIdAgricola(idAgricola);
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_TIPO_DEUDA_CORTO_PLAZO, tipo);
			deudaCortoPlazo.setIdTipoDeuda(clasifProducto.getIdClasif());
		}
	}
	private ArrayList transformVoProdGanSinItem(ArrayList prodGanads, Long idAgricola){
		final String TRES_3="3.3 Masa Ganadera (BOVINA - OVINA)";
		final String TRES_4="3.4 Ventas Ganadera (BOVINA - OVINA)";
		final Integer GRUPO_ITEM_RUBRO = Integer.valueOf("6825");
		final Integer GRUPO_UNIDAD = Integer.valueOf("6850");
		final Integer GRUPO_TIPO = Integer.valueOf("6785");
		ArrayList finalList = new ArrayList();
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = prodGanads.iterator(); iterator.hasNext();) {
			ProductoGanadero productoGanadero = null;
			productoGanadero = (ProductoGanadero) iterator.next();
			Clasificacion clasifRubroA = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_ITEM_RUBRO, TRES_3);
			if (clasifRubroA == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(TRES_3));
			}
			Clasificacion clasifRubroB = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_ITEM_RUBRO, TRES_4);
			if (clasifRubroB == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(TRES_4));
			}
			if (productoGanadero.getUnidad() == null){
				productoGanadero.setUnidad("UNIDAD");
			}
			Clasificacion clasifUnidad = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_UNIDAD, productoGanadero.getUnidad());
			if (clasifUnidad == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(productoGanadero.getUnidad()));
			}
			Clasificacion clasifTipo = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_TIPO, productoGanadero.getTipoRubro());
			ProductoGanadero productoGanaderoList = new ProductoGanadero();
			if (productoGanadero.getTipoRubro()!=null){
				productoGanaderoList = new ProductoGanadero();
				productoGanaderoList.setIdAgricola(idAgricola);
				productoGanaderoList.setIdItemRubro(clasifRubroA.getIdClasif());
				productoGanaderoList.setIdTipoRubro(clasifTipo.getIdClasif());
				productoGanaderoList.setIdUnidadMedida(clasifUnidad.getIdClasif());
				productoGanaderoList.setValor(productoGanadero.getMasaGanadera()!=null ? productoGanadero.getMasaGanadera(): Double.valueOf("0"));
				finalList.add(productoGanaderoList);
				productoGanaderoList = new ProductoGanadero();
				productoGanaderoList.setIdAgricola(idAgricola);
				productoGanaderoList.setIdItemRubro(clasifRubroB.getIdClasif());
				productoGanaderoList.setIdTipoRubro(clasifTipo.getIdClasif());
				productoGanaderoList.setIdUnidadMedida(clasifUnidad.getIdClasif());
				productoGanaderoList.setValor(productoGanadero.getVentasGanaderia()!=null ? productoGanadero.getVentasGanaderia(): Double.valueOf("0"));
				finalList.add(productoGanaderoList);
			}
		}
		return finalList;
	}
	
	private void transformVoProdGanConItem(ArrayList prodGanads, Long idAgricola, String rubro){
		final Integer GRUPO_ITEM_RUBRO = Integer.valueOf("6825");
		final Integer GRUPO_UNIDAD = Integer.valueOf("6850");
		final Integer GRUPO_ITEM_TIPO = Integer.valueOf("6835");
		final String TRES_5="3.5 LANA";
		final String TRES_6="3.6 LECHE";
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = prodGanads.iterator(); iterator.hasNext();) {
			ProductoGanadero productoGanadero = (ProductoGanadero) iterator.next();
			productoGanadero.setIdAgricola(idAgricola);
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_ITEM_RUBRO, rubro);
			if (clasifProducto == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(rubro));
			}
			productoGanadero.setIdItemRubro(clasifProducto.getIdClasif());
			if (productoGanadero.getUnidad() == null){
				productoGanadero.setUnidad("UNIDAD");
			}
			if (rubro.equals(TRES_5)||rubro.equals(TRES_6)){
				Clasificacion clasifTipo = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_ITEM_TIPO, productoGanadero.getItemRubro());
				if (clasifTipo == null){
					throw new BusinessOperationException("No se encontro clasificacion para : ".concat(productoGanadero.getItemRubro()));
				}
				productoGanadero.setIdTipoRubro(clasifTipo.getIdClasif());
			}
			clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_UNIDAD, productoGanadero.getUnidad());
			if (clasifProducto == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(productoGanadero.getUnidad()));
			}
			productoGanadero.setIdUnidadMedida(clasifProducto.getIdClasif());
		}
	}
	
	private void transformVosDeudasLargoPlazo(ArrayList deudasLargoPlazo, Long idAgricola,String tipo){
		final Integer GRUPO_TIPO_DEUDA_LARGO_PLAZO = Integer.valueOf("6905");
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = deudasLargoPlazo.iterator(); iterator.hasNext();) {
			DeudaLargoPlazo deudaLargoPlazo = (DeudaLargoPlazo) iterator.next();
			deudaLargoPlazo.setIdAgricola(idAgricola);
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_TIPO_DEUDA_LARGO_PLAZO, tipo);
			if (clasifProducto == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(tipo));
			}
			deudaLargoPlazo.setIdTipoDeuda(clasifProducto.getIdClasif());
		}
	}
	private void transformVosOtrActFijo(ArrayList otrosActivosFijos, Long idAgricola){
		final Integer GRUPO_TIPO_OTR_ACT_FIJO = Integer.valueOf("6895");
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = otrosActivosFijos.iterator(); iterator.hasNext();) {
			OtroActivoFijo otroActivoFijo = (OtroActivoFijo) iterator.next();
			otroActivoFijo.setIdAgricola(idAgricola);
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_TIPO_OTR_ACT_FIJO, otroActivoFijo.getTipoActivo());
			if (clasifProducto == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(otroActivoFijo.getTipoActivo()));
			}
			otroActivoFijo.setTipo(clasifProducto.getIdClasif());
		}
	}
	
	private void transformVosOtrConce(ArrayList otrosConceptos, Long idAgricola, String tipo){
		final Integer GRUPO_TIPO_OTR_CONCE = Integer.valueOf("6890");
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = otrosConceptos.iterator(); iterator.hasNext();) {
			OtroConcepto otroConcepto = (OtroConcepto) iterator.next();
			otroConcepto.setIdAgricola(idAgricola);
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_TIPO_OTR_CONCE, tipo);
			if (clasifProducto == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(tipo));
			}
			otroConcepto.setTipo(clasifProducto.getIdClasif());
		}
	}
	
	private void transformVosPropiedades (ArrayList props, Long idAgricola){
		for (Iterator iterator = props.iterator(); iterator.hasNext();) {
			Propiedad propiedad = (Propiedad) iterator.next();
			propiedad.setIdAgricola(idAgricola);
		}
	}
	
	private ArrayList transformVosIngEgr(ArrayList ingEgrs, Long idAgricola){
		final Integer GRUPO_TIPO_ING_EGR = Integer.valueOf("6877");
		ArrayList finalList = new ArrayList();
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = ingEgrs.iterator(); iterator.hasNext();) {
			IngresoEgreso ingEgr = (IngresoEgreso) iterator.next();
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_TIPO_ING_EGR, ingEgr.getTipoIngEgr());
			if (clasifProducto == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(ingEgr.getTipoIngEgr()));
			}
			IngresoEgreso ingrEgrList = new IngresoEgreso();
			if (ingEgr.getAnoUno() != null){
				ingrEgrList = new IngresoEgreso();
				ingrEgrList.setIdAgricola(idAgricola);
				ingrEgrList.setTipo(clasifProducto.getIdClasif());
				ingrEgrList.setAno(Integer.valueOf("1"));
				ingrEgrList.setValor(ingEgr.getAnoUno());
				finalList.add(ingrEgrList);
			}
			if (ingEgr.getAnoDos() != null){
				ingrEgrList = new IngresoEgreso();
				ingrEgrList.setIdAgricola(idAgricola);
				ingrEgrList.setTipo(clasifProducto.getIdClasif());
				ingrEgrList.setAno(Integer.valueOf("2"));
				ingrEgrList.setValor(ingEgr.getAnoDos());
				finalList.add(ingrEgrList);
			}
			if (ingEgr.getAnoTres() != null){
				ingrEgrList = new IngresoEgreso();
				ingrEgrList.setIdAgricola(idAgricola);
				ingrEgrList.setTipo(clasifProducto.getIdClasif());
				ingrEgrList.setAno(Integer.valueOf("3"));
				ingrEgrList.setValor(ingEgr.getAnoTres());
				finalList.add(ingrEgrList);
			}
			if (ingEgr.getAnoCuatro() != null){
				ingrEgrList = new IngresoEgreso();
				ingrEgrList.setIdAgricola(idAgricola);
				ingrEgrList.setTipo(clasifProducto.getIdClasif());
				ingrEgrList.setAno(Integer.valueOf("4"));
				ingrEgrList.setValor(ingEgr.getAnoCuatro());
				finalList.add(ingrEgrList);
			}
			if (ingEgr.getAnoCinco() != null){
				ingrEgrList = new IngresoEgreso();
				ingrEgrList.setIdAgricola(idAgricola);
				ingrEgrList.setTipo(clasifProducto.getIdClasif());
				ingrEgrList.setAno(Integer.valueOf("5"));
				ingrEgrList.setValor(ingEgr.getAnoCinco());
				finalList.add(ingrEgrList);
			}
			if (ingEgr.getAnoSeis() != null){
				ingrEgrList = new IngresoEgreso();
				ingrEgrList.setIdAgricola(idAgricola);
				ingrEgrList.setTipo(clasifProducto.getIdClasif());
				ingrEgrList.setAno(Integer.valueOf("6"));
				ingrEgrList.setValor(ingEgr.getAnoSeis());
				finalList.add(ingrEgrList);
			}
		}
		return finalList;
	}
	private void transformVosArrSuelo (ArrayList arrSuelos, Long idAgricola){
		final Integer GRUPO_TIPO_ARR = Integer.valueOf("6870");
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = arrSuelos.iterator(); iterator.hasNext();) {
			ArriendoSuelo arrSuelo = (ArriendoSuelo) iterator.next();
			arrSuelo.setIdAgricola(idAgricola);
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_TIPO_ARR, arrSuelo.getTipoArriendo());
			if (clasifProducto == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(arrSuelo.getTipoArriendo()));
			}
			arrSuelo.setIdTipoArriendo(clasifProducto.getIdClasif());
		}
	}
	private ArrayList cambiarNulosPorCeroProductoAgricola(ArrayList prodAgrs){
		for (Iterator iterator = prodAgrs.iterator(); iterator.hasNext();) {
			ProductoAgricola productoAgricola = (ProductoAgricola) iterator.next();
			if (productoAgricola.getHasUnoAno() != null||productoAgricola.getHasDosAno() != null||productoAgricola.getHasTresAno() != null
				||productoAgricola.getHasCuatroAno() != null||productoAgricola.getHasCincoAno() != null||productoAgricola.getHasSeisAno() != null){
				if (productoAgricola.getHasUnoAno() == null){
					productoAgricola.setHasUnoAno(Double.valueOf("0"));
				}
				if (productoAgricola.getHasDosAno() == null){
					productoAgricola.setHasDosAno(Double.valueOf("0"));
				}
				if (productoAgricola.getHasTresAno() == null){
					productoAgricola.setHasTresAno(Double.valueOf("0"));
				}
				if (productoAgricola.getHasCuatroAno() == null){
					productoAgricola.setHasCuatroAno(Double.valueOf("0"));
				}
				if (productoAgricola.getHasCincoAno() == null){
					productoAgricola.setHasCincoAno(Double.valueOf("0"));
				}
				if (productoAgricola.getHasSeisAno() == null){
					productoAgricola.setHasSeisAno(Double.valueOf("0"));
				}
			}
		}
		return prodAgrs;
	}
	private ArrayList transformVosProductoAgricola(ArrayList prodAgrs, Long idAgricola){
		final Integer GRUPO_PRODUCTO = Integer.valueOf("6650");
		ArrayList finalList = new ArrayList();
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = prodAgrs.iterator(); iterator.hasNext();) {
			ProductoAgricola productoAgricola = (ProductoAgricola) iterator.next();
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_PRODUCTO, productoAgricola.getNombreProducto());
			if (clasifProducto == null){
				throw new BusinessOperationException("No se encontro clasificacion para : ".concat(productoAgricola.getNombreProducto()));
			}
			ProductoAgricola prodAgroList = new ProductoAgricola();
			if (productoAgricola.getHasUnoAno() != null){
				prodAgroList = new ProductoAgricola();
				prodAgroList.setIdAgricola(idAgricola);
				prodAgroList.setIdParamProductoAgricola(clasifProducto.getIdClasif());
				prodAgroList.setEdad(Integer.valueOf("1"));
				prodAgroList.setSuperficie(productoAgricola.getHasUnoAno());
				finalList.add(prodAgroList);
			}
			if (productoAgricola.getHasDosAno() != null){
				prodAgroList = new ProductoAgricola();
				prodAgroList.setIdAgricola(idAgricola);
				prodAgroList.setIdParamProductoAgricola(clasifProducto.getIdClasif());
				prodAgroList.setEdad(Integer.valueOf("2"));
				prodAgroList.setSuperficie(productoAgricola.getHasDosAno());
				finalList.add(prodAgroList);
			}
			if (productoAgricola.getHasTresAno() != null){
				prodAgroList = new ProductoAgricola();
				prodAgroList.setIdAgricola(idAgricola);
				prodAgroList.setIdParamProductoAgricola(clasifProducto.getIdClasif());
				prodAgroList.setEdad(Integer.valueOf("3"));
				prodAgroList.setSuperficie(productoAgricola.getHasTresAno());
				finalList.add(prodAgroList);
			}
			if (productoAgricola.getHasCuatroAno() != null){
				prodAgroList = new ProductoAgricola();
				prodAgroList.setIdAgricola(idAgricola);
				prodAgroList.setIdParamProductoAgricola(clasifProducto.getIdClasif());
				prodAgroList.setEdad(Integer.valueOf("4"));
				prodAgroList.setSuperficie(productoAgricola.getHasCuatroAno());
				finalList.add(prodAgroList);
			}
			if (productoAgricola.getHasCincoAno() != null){
				prodAgroList = new ProductoAgricola();
				prodAgroList.setIdAgricola(idAgricola);
				prodAgroList.setIdParamProductoAgricola(clasifProducto.getIdClasif());
				prodAgroList.setEdad(Integer.valueOf("5"));
				prodAgroList.setSuperficie(productoAgricola.getHasCincoAno());
				finalList.add(prodAgroList);
			}
			if (productoAgricola.getHasSeisAno() != null){
				prodAgroList = new ProductoAgricola();
				prodAgroList.setIdAgricola(idAgricola);
				prodAgroList.setIdParamProductoAgricola(clasifProducto.getIdClasif());
				prodAgroList.setEdad(Integer.valueOf("6"));
				prodAgroList.setSuperficie(productoAgricola.getHasSeisAno());
				finalList.add(prodAgroList);
			}
		}
		return finalList;
	}
	
	public List buscarAgrXRut(String rutCliente) {
		GestorAgricola gestor = new GestorAgricolaImpl();
		return gestor.obtenerListaVaciadosAgricola(rutCliente);
	}
	
	public List buscarAgricolaXRutYRating(String rutCliente, Long idRating) {
		GestorAgricola gestor = new GestorAgricolaImpl();
		return gestor.buscarAgricolaXRutYRating(rutCliente, idRating);
	}
	public String checkRepetidos(ArrayList parametros){
		HashMap repetidos = new HashMap();
		HashMap elementos = new HashMap();
		for (int i = 0; i < parametros.size();i++){
			Object object = parametros.get(i);
			ParamPlantacion paramPlantacion = null;
			ParamGanaderia paramGanaderia = null;
			ProductoAgricola productoAgricola = null;
			if (object instanceof ParamPlantacion){
				paramPlantacion = (ParamPlantacion) object;
				if (elementos.get(paramPlantacion.getNombre()) == null){
					elementos.put(paramPlantacion.getNombre(), paramPlantacion.getNombre());
				}else{
					if (repetidos.get(paramPlantacion.getNombre()) == null){
						repetidos.put(paramPlantacion.getNombre(),paramPlantacion.getNombre());
					}
				}
			}else if (object instanceof ParamGanaderia){
				paramGanaderia = (ParamGanaderia) object;
				if (elementos.get(paramGanaderia.getNombre()) == null){
					elementos.put(paramGanaderia.getNombre(), paramGanaderia.getNombre());
				}else{
					if (repetidos.get(paramGanaderia.getNombre()) == null){
						repetidos.put(paramGanaderia.getNombre(),paramGanaderia.getNombre());
					}
				}
			}else if (object instanceof ProductoAgricola){
				productoAgricola = (ProductoAgricola) object;
				if (elementos.get(productoAgricola.getNombreProducto()) == null){
					elementos.put(productoAgricola.getNombreProducto(), productoAgricola.getNombreProducto());
				}else{
					if (repetidos.get(productoAgricola.getNombreProducto()) == null){
						repetidos.put(productoAgricola.getNombreProducto(),productoAgricola.getNombreProducto());
					}
				}
			}
		}
		StringBuffer sf = new StringBuffer();
		for (Iterator it = repetidos.entrySet().iterator();it.hasNext();){
			Entry e = (Entry) it.next();
			sf.append(e.getValue()).append(" ");
		}
		return sf.toString();
	}
	
	
	public void cargarParametrosAgricola(Archivo archivo, String logOperador, String rutCliente) {
		final Integer ID_LECHE = Integer.valueOf("6837");
		final Integer ID_LANA = Integer.valueOf("6836");
		POIExcelReader reader = new POIExcelReader();
		GestorAgricola gestorAgricola = new GestorAgricolaImpl();
		gestorAgricola.caducarParametros();
		Archivo file = (Archivo) archivo;
		ArrayList parametrosPlantacion = reader.getFileAsList("ParamPlantacion", file);
		transformVoParametros(parametrosPlantacion,gestorAgricola);
		String repetidosPlantacion = checkRepetidos(parametrosPlantacion);
		if (!repetidosPlantacion.equals("")){
			throw new BusinessOperationException("Existen elementos repetidos en el cuadro Agricola");
		}
		for (Iterator iterator = parametrosPlantacion.iterator(); iterator.hasNext();) {
			ParamPlantacion paramPlantacion = (ParamPlantacion) iterator.next();
			Long idParamPlantacion = gestorAgricola.insertarParametroAgricola(paramPlantacion);
			ArrayList listParamPlantacionAno=getVosParamPlantacionAno(paramPlantacion, idParamPlantacion);
			for (Iterator iterator2 = listParamPlantacionAno.iterator(); iterator2.hasNext();) {
				ParamPlantacionAno paramPlantacionAno = (ParamPlantacionAno) iterator2.next();
				gestorAgricola.insertarParametroAgricolaAno(paramPlantacionAno);				
			}
		}
		ArrayList parametrosGanaderos = reader.getFileAsList("ParamGanaderia", file);
		transformVoParametrosGanaderos(parametrosGanaderos, gestorAgricola);
		String repetidosGanaderia = checkRepetidos(parametrosGanaderos);
		if (!repetidosGanaderia.equals("")){
			throw new BusinessOperationException("Existen elementos repetidos en el cuadro Ganadero");
		}
		for (Iterator iterator = parametrosGanaderos.iterator(); iterator.hasNext();) {
			ParamGanaderia paramGanaderia = (ParamGanaderia) iterator.next();
			gestorAgricola.insertarParametroGanadero(paramGanaderia);
		}
		ArrayList parametrosProdGanaderos = reader.getFileAsList("ParamProdGanaderia", file);
		transformVoParametrosProdGanderos(parametrosProdGanaderos , ID_LANA);
		ArrayList parametrosProdGanaderosB = reader.getFileAsList("ParamProdGanaderiaB", file);
		transformVoParametrosProdGanderos(parametrosProdGanaderosB , ID_LECHE);
		parametrosProdGanaderos.addAll(parametrosProdGanaderosB);
		for (Iterator iterator = parametrosProdGanaderos.iterator(); iterator.hasNext();) {
			ParamProductoGanaderia paramProductoGanaderia = (ParamProductoGanaderia) iterator.next();
			if (paramProductoGanaderia.getNombre()==null){
				throw new BusinessOperationException("Debe Ingresar la informacion de Productos Ganaderos ");
			}
			gestorAgricola.insertarParametroProductoGanadero(paramProductoGanaderia);
		}
	}
	
	public boolean revisarVersionPlantilla(Agricola agricola, Audit auditoria){
		if (auditoria != null){
			if (agricola.getVersion()!=null){
				Integer version = Integer.valueOf(auditoria.getDetAlteracion());
				if (version.equals(agricola.getVersion())){
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
	
	
	public Boolean ingresarRatingProyectadoAgricola(Boolean existe, Archivo archivo, String logOperador, String rutCliente, Long idRatingInd) {
		final String OACCP = "9. OTROS ACTIVOS CIRCULANTES CORTO PLAZO (d) - (M$)" ;
		final String OALP ="11.3 OTROS ACREEDORES LARGO PLAZO";
		final String OACP ="11.4.3 OTROS ACREEDORES CORTO PLAZO";
		final String DLPP="11.1 DEUDAS DE LARGO PLAZO PROPUESTAS";
		final String DLPV="11.2 DEUDAS DE LARGO PLAZO VIGENTES (BANCOS/PROVEEDORES/OTROS)";
		final String TRES_1="3.1 Suelos destinados empastadas y/o cultivos forrajeros";
		final String TRES_5="3.5 LANA";
		final String TRES_6="3.6 LECHE";
		final String DEUDAS_CORTO_PLAZO_PROP="11.4.1 DEUDAS DE CORTO PLAZO PROPUESTAS ";
		final String DEUDAS_CORTO_PLAZO_VIGENTES="11.4.2 DEUDAS CORTO PLAZO  VIGENTES";		
		POIExcelReader reader = new POIExcelReader();
		GestorAgricola gestorAgricola = new GestorAgricolaImpl();
		Agricola agricola = new Agricola();
		Archivo file = (Archivo) archivo;
		Long idUsuario = buscarIdUsuarioPorNombre(logOperador);
		if (idUsuario.equals(null)) {
			throw new BusinessOperationException("El usuario: " + logOperador+ "no existe en la aplicacion");
		}
		if (existe.equals(Boolean.FALSE)){
			ArrayList agricolas = reader.getFileAsList("Agricola", file);
			if (agricolas.size()>0){
				agricola=(Agricola)agricolas.get(0);
//				Audit auditoria = gestorAgricola.obtenerVersionPlantila(Integer.valueOf(ConstantesSEFE.CODIGO_OP_CARGA_PLANTILLA_AGRICOLA));
//				if (!revisarVersionPlantilla(agricola, auditoria)){
//					throw new BusinessOperationException("La plantilla que intenta cargar no corresponde a la ultima version cargada el : ".concat(FormatUtil.formatDate(auditoria.getFechaHora())));	
//				}
				if (agricola.getError() == null){
					throw new BusinessOperationException("No se encontro el campo de validaci\u00F3n en archivo");
				}
				if (agricola.getError().intValue()!=(ConstantesSEFE.INTEGER_CERO).intValue()){
					throw new BusinessOperationException("Existen errores de validaci\u00F3n en el archivo, corregir e intentar nuevamente.");
				}
				if (agricola.getRut() == null ||!agricola.getRut().equals(rutCliente)){
					throw new BusinessOperationException("El rut del archivo no concuerda con el de la sesi\u00F3n");
				}
			}else{
				throw new BusinessOperationException("No se encontraron datos en archivo");
			}
			agricola.setIdUsuario(idUsuario);
			Long idCliente = null;
			try {
				idCliente = buscarIdClientePorRut(rutCliente);
			} catch (NullPointerException e) {
				throw new BusinessOperationException("El rut: " + rutCliente + " no existe en la aplicaci\u00F3n");
			}
			List agricolasBD=buscarAgrXRut(rutCliente);
			if (agricolasBD.size()>0){
				for (Iterator iterator = agricolasBD.iterator(); iterator.hasNext();) {
					Agricola agrBd = (Agricola) iterator.next();
					if (agrBd.getFecha().equals(agricola.getFecha())&&agrBd.getTemporada().equals(agricola.getTemporada())
							&&agrBd.getDescripcion().equals(agricola.getDescripcion()) ){
						if (agrBd.getIdRatingInd() != null){
							ServicioRatingIndividual srvRtgInd = new ServicioRatingIndividualImpl();
							RatingIndividual ratingIndividual = srvRtgInd.buscarRatingPorId(idCliente, agrBd.getIdRatingInd());
							if (ratingIndividual != null && !ratingIndividual.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO)){
								throw new BusinessOperationException ("El archivo ya existe y fue utilizado en un rating vigente");
							}
						}
						existe=Boolean.TRUE;
					}
				}
				if (existe.booleanValue()){
					return existe;
				}
				ArrayList tipoExplBov = reader.getFileAsList("ProdGanE", file);
				GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
				if (tipoExplBov.size()>0){
					final Integer GRUPO_TIPO_EXPL_B = Integer.valueOf("6860");
					ProductoGanadero productoGanadero = (ProductoGanadero)tipoExplBov.get(0);
					Clasificacion clasifTipoExplB = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_TIPO_EXPL_B, productoGanadero.getTipoRubro());
					if (clasifTipoExplB != null && clasifTipoExplB.getIdClasif() != null){
						agricola.setTpoExportacionBov(clasifTipoExplB.getIdClasif());
					}
				}

				agricola.setIdParteInv(idCliente);
				agricola.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
				Long idAgricola = gestorAgricola.insertarAgricola(agricola);
				agricola.setIdAgricola(idAgricola);
			}else{
				ArrayList tipoExplBov = reader.getFileAsList("ProdGanE", file);
				GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
				if (tipoExplBov.size()>0){
					final Integer GRUPO_TIPO_EXPL_B = Integer.valueOf("6860");
					ProductoGanadero productoGanadero = (ProductoGanadero)tipoExplBov.get(0);
					Clasificacion clasifTipoExplB = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_TIPO_EXPL_B, productoGanadero.getTipoRubro());
					if (clasifTipoExplB != null && clasifTipoExplB.getIdClasif() != null){
						agricola.setTpoExportacionBov(clasifTipoExplB.getIdClasif());
					}
				}
				idCliente = null;
				try {
					idCliente = buscarIdClientePorRut(rutCliente);
				} catch (NullPointerException e) {
					throw new BusinessOperationException("El rut: " + rutCliente + " no existe en la aplicaci\u00F3n");
				}
				agricola.setIdParteInv(idCliente);
				agricola.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
				Long idAgricola = gestorAgricola.insertarAgricola(agricola);
				agricola.setIdAgricola(idAgricola);
			}
		}else{
			Long idCliente = null;
			try {
				idCliente = buscarIdClientePorRut(rutCliente);
			} catch (NullPointerException e) {
				throw new BusinessOperationException("El rut: " + rutCliente + " no existe en la aplicaci\u00F3n");
			}
			ArrayList agricolas = reader.getFileAsList("Agricola", file);
			if (agricolas.size()>0){
				agricola=(Agricola)agricolas.get(0);
			}
			List agricolasBD=buscarAgrXRut(rutCliente);
			if (agricolasBD.size()>0){
				for (Iterator iterator = agricolasBD.iterator(); iterator.hasNext();) {
					Agricola agrBd = (Agricola) iterator.next();
					if (agrBd.getFecha().equals(agricola.getFecha())&&agrBd.getTemporada().equals(agricola.getTemporada())
							&&agrBd.getDescripcion().equals(agricola.getDescripcion()) ){
						agrBd.setEjecutivo(agricola.getEjecutivo());
						agrBd.setIdRubro(agricola.getIdRubro());
						agrBd.setNombreGrupo(agricola.getNombreGrupo());
						agrBd.setOficinaRegion(agricola.getOficinaRegion());
						agrBd.setRetirosAnuales(agricola.getRetirosAnuales());
						ArrayList tipoExplBov = reader.getFileAsList("ProdGanE", file);
						GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
						if (tipoExplBov.size()>0){
							final Integer GRUPO_TIPO_EXPL_B = Integer.valueOf("6860");
							ProductoGanadero productoGanadero = (ProductoGanadero)tipoExplBov.get(0);
							Clasificacion clasifTipoExplB = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_TIPO_EXPL_B, productoGanadero.getTipoRubro());
							if (clasifTipoExplB != null && clasifTipoExplB.getIdClasif() != null){
								agrBd.setTpoExportacionBov(clasifTipoExplB.getIdClasif());
							}
						}
						agricola=agrBd;
					}
				}
			}
			gestorAgricola.actualizarUsuarioVaciadoAgricola(idUsuario, agricola);
			gestorAgricola.borrarFlujos(agricola.getIdAgricola());
			gestorAgricola.borrarTablasVaciadoAgricola(agricola.getIdAgricola());
		}
		ArrayList prodAgrs = reader.getFileAsList("ProdAgr", file);
		String repetidosProductosAgricola = checkRepetidos(prodAgrs);
		if (!repetidosProductosAgricola.equals("")){
			throw new BusinessOperationException("Existen elementos repetidos en PLANTACIONES, CULTIVOS Y HORTALIZAS (HA)");
		}
		prodAgrs = cambiarNulosPorCeroProductoAgricola(prodAgrs);
		prodAgrs = transformVosProductoAgricola(prodAgrs , agricola.getIdAgricola());
		for (Iterator iterator = prodAgrs.iterator(); iterator.hasNext();) {
			ProductoAgricola productoAgricola = (ProductoAgricola) iterator.next();
			gestorAgricola.insertarProductoAgricola(productoAgricola);
		}

		ArrayList prodGands = reader.getFileAsList("ProdGan", file);
		transformVoProdGanConItem(prodGands, agricola.getIdAgricola(),TRES_1 );
		ArrayList prodGandsB = reader.getFileAsList("ProdGanB", file);
		prodGandsB=transformVoProdGanSinItem(prodGandsB, agricola.getIdAgricola());
		ArrayList prodGandsC = reader.getFileAsList("ProdGanC", file);
		transformVoProdGanConItem(prodGandsC, agricola.getIdAgricola(),TRES_5 );
		ArrayList prodGandsD = reader.getFileAsList("ProdGanD", file);
		transformVoProdGanConItem(prodGandsD, agricola.getIdAgricola(),TRES_6 );
		prodGands.addAll(prodGandsB);
		prodGands.addAll(prodGandsC);
		prodGands.addAll(prodGandsD);
		for (Iterator iterator = prodGands.iterator(); iterator.hasNext();) {
			ProductoGanadero productoGanadero = (ProductoGanadero) iterator.next();
			gestorAgricola.insertarProductoGanadero(productoGanadero);
		}
		ArrayList arrSuelos = reader.getFileAsList("ArriendoSuelo", file);
		transformVosArrSuelo(arrSuelos, agricola.getIdAgricola());
		for (Iterator iterator = arrSuelos.iterator(); iterator.hasNext();) {
			ArriendoSuelo arriendoSuelo = (ArriendoSuelo) iterator.next();
			gestorAgricola.insertarArriendoSuelo(arriendoSuelo);
		}
		ArrayList ingEgr = reader.getFileAsList("IngEgr", file);
		ArrayList ingEgrB = reader.getFileAsList("IngEgrB", file);
		ingEgr.addAll(ingEgrB);
		ingEgr = cambiarNulosPorCeroIngresoEgreso(ingEgr);
		ingEgr = transformVosIngEgr(ingEgr, agricola.getIdAgricola());
		for (Iterator iterator = ingEgr.iterator(); iterator.hasNext();) {
			IngresoEgreso ingresoEgreso = (IngresoEgreso) iterator.next();
			gestorAgricola.insertarIngresoEgreso(ingresoEgreso);
		}
		ArrayList props = reader.getFileAsList("Props", file);
		transformVosPropiedades(props , agricola.getIdAgricola());
		for (Iterator iterator = props.iterator(); iterator.hasNext();) {
			Propiedad propiedad = (Propiedad) iterator.next();
			gestorAgricola.insertarPropiedad(propiedad);
		}
		ArrayList otrosConceptos = reader.getFileAsList("OtrConce", file);
		transformVosOtrConce(otrosConceptos, agricola.getIdAgricola(), OACCP);
		ArrayList otrosConceptosB = reader.getFileAsList("OtrConceB", file);
		transformVosOtrConce(otrosConceptosB, agricola.getIdAgricola(), OALP);
		ArrayList otrosConceptosC = reader.getFileAsList("OtrConceC", file);
		transformVosOtrConce(otrosConceptosC, agricola.getIdAgricola(), OACP);
		otrosConceptos.addAll(otrosConceptosB);
		otrosConceptos.addAll(otrosConceptosC);
		for (Iterator iterator = otrosConceptos.iterator(); iterator.hasNext();) {
			OtroConcepto otroConcepto = (OtroConcepto) iterator.next();
			gestorAgricola.insertarOtroConcepto(otroConcepto);
		}
		ArrayList otrosActivosFijos = reader.getFileAsList("OtrActFijo", file);
		transformVosOtrActFijo(otrosActivosFijos, agricola.getIdAgricola());
		for (Iterator iterator = otrosActivosFijos.iterator(); iterator.hasNext();) {
			OtroActivoFijo otroActivoFijo = (OtroActivoFijo) iterator.next();
			gestorAgricola.insertarOtroActivoFijo(otroActivoFijo);
		}
		ArrayList deudasLargoPlazo = reader.getFileAsList("DeudaLargoPlazo", file);
		transformVosDeudasLargoPlazo(deudasLargoPlazo, agricola.getIdAgricola(), DLPP);
		ArrayList deudasLargoPlazoB = reader.getFileAsList("DeudaLargoPlazoB", file);
		transformVosDeudasLargoPlazo(deudasLargoPlazoB, agricola.getIdAgricola(), DLPV);
		deudasLargoPlazo.addAll(deudasLargoPlazoB);
		for (Iterator iterator = deudasLargoPlazo.iterator(); iterator.hasNext();) {
			DeudaLargoPlazo deudaLargoPlazo = (DeudaLargoPlazo) iterator.next();
			gestorAgricola.insertarDeudaLargoPlazo(deudaLargoPlazo);
		}
		ArrayList deudasCortoPlazo = reader.getFileAsList("DeudaCortoPlazo", file);
		transformVoDeudaCortoPlazo(deudasCortoPlazo, agricola.getIdAgricola(),DEUDAS_CORTO_PLAZO_PROP );
		ArrayList deudasCortoPlazoB = reader.getFileAsList("DeudaCortoPlazoB", file);
		transformVoDeudaCortoPlazo(deudasCortoPlazoB, agricola.getIdAgricola(),DEUDAS_CORTO_PLAZO_VIGENTES );
		deudasCortoPlazo.addAll(deudasCortoPlazoB);
		for (Iterator iterator = deudasCortoPlazo.iterator(); iterator.hasNext();) {
			DeudaCortoPlazo deudaCortoPlazo = (DeudaCortoPlazo) iterator.next();
			gestorAgricola.insertarDeudaCortoPlazo(deudaCortoPlazo);
		}
		return existe;
	}
	private ArrayList cambiarNulosPorCeroIngresoEgreso(ArrayList ingrEgrs){
		for (Iterator iterator = ingrEgrs.iterator(); iterator.hasNext();) {
			IngresoEgreso ingresoEgreso = (IngresoEgreso) iterator.next();
			if (ingresoEgreso.getAnoUno()==null){
				ingresoEgreso.setAnoUno(Double.valueOf("0"));
			}
			if (ingresoEgreso.getAnoDos()==null){
				ingresoEgreso.setAnoDos(Double.valueOf("0"));
			}
			if (ingresoEgreso.getAnoTres()==null){
				ingresoEgreso.setAnoTres(Double.valueOf("0"));
			}
			if (ingresoEgreso.getAnoCuatro()==null){
				ingresoEgreso.setAnoCuatro(Double.valueOf("0"));
			}
			if (ingresoEgreso.getAnoCinco()==null){
				ingresoEgreso.setAnoCinco(Double.valueOf("0"));
			}
			if (ingresoEgreso.getAnoSeis()==null){
				ingresoEgreso.setAnoSeis(Double.valueOf("0"));
			}
		}
		return ingrEgrs;
	}
	
	public void transformVoParametrosProdGanderos(ArrayList parametrosProdGanaderos, Integer tipo){
		for (Iterator iterator = parametrosProdGanaderos.iterator(); iterator.hasNext();) {
			ParamProductoGanaderia paramProductoGanaderia = (ParamProductoGanaderia) iterator.next();
			paramProductoGanaderia.setIdParam(tipo);
		}
	}
	public void transformVoParametrosGanaderos(ArrayList parametrosGanaderos, GestorAgricola gestorAgricola){
		final Integer GRUPO_PRODUCTOS = Integer.valueOf("6785");
		final Integer GRUPO_CATEGORIAS = Integer.valueOf("6810");
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = parametrosGanaderos.iterator(); iterator.hasNext();) {
			ParamGanaderia paramGanaderia = (ParamGanaderia) iterator.next();
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_PRODUCTOS, paramGanaderia.getNombre());
			if (clasifProducto == null){
				Clasificacion clasificacion = new Clasificacion();
				clasificacion.setIdGrupoClasificacion(GRUPO_PRODUCTOS);
				clasificacion.setNombre(paramGanaderia.getNombre());
				Integer idClasif=gestorAgricola.insertarClasificacionProducto(clasificacion);
				paramGanaderia.setIdParam(idClasif);
			}else{
				paramGanaderia.setIdParam(clasifProducto.getIdClasif());
			}
			Clasificacion clasifCategoria = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_CATEGORIAS, paramGanaderia.getCategoria().toUpperCase());
			if (clasifCategoria != null){
				paramGanaderia.setIdCategoria(clasifCategoria.getIdClasif());
			}
		}
	}
	public ArrayList getVosParamPlantacionAno(ParamPlantacion paramPlantacion, Long idParamPlantacion){
		ArrayList finalList = new ArrayList();
		ParamPlantacionAno paramPlantacionAno = new ParamPlantacionAno();
		if (paramPlantacion.getRend1Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("1"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend1Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto1Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend2Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("2"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend2Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto2Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend3Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("3"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend3Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto3Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend4Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("4"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend4Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto4Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend5Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("5"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend5Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto5Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend6Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("6"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend6Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto6Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend7Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("7"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend7Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto7Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend8Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("8"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend8Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto8Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend9Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("9"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend9Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto9Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend10Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("10"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend10Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto10Ano());
			finalList.add(paramPlantacionAno);
		}
		return finalList;
	}
	
	
	public void transformVoParametros (ArrayList parametros,GestorAgricola gestorAgricola){
		final Integer GRUPO_PRODUCTOS = Integer.valueOf("6650");
		final Integer GRUPO_VULNERABILIDAD= Integer.valueOf("6760");
		final Integer GRUPO_PER_CRIT= Integer.valueOf("6750");
		for (Iterator iterator = parametros.iterator(); iterator.hasNext();) {
			ParamPlantacion paramPlantacion = (ParamPlantacion) iterator.next();
			GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_PRODUCTOS, paramPlantacion.getNombre());
			if (clasifProducto == null){
				Clasificacion clasificacion = new Clasificacion();
				clasificacion.setIdGrupoClasificacion(GRUPO_PRODUCTOS);
				clasificacion.setNombre(paramPlantacion.getNombre());
				Integer idClasif=gestorAgricola.insertarClasificacionProducto(clasificacion);
				paramPlantacion.setIdParam(idClasif);
			}else{
				paramPlantacion.setIdParam(clasifProducto.getIdClasif());
			}
			Clasificacion clasifVulner = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_VULNERABILIDAD, paramPlantacion.getClasifVulClim());
			if (clasifVulner != null){
				paramPlantacion.setIdVulClim(clasifVulner.getIdClasif());
			}
			Clasificacion clasifPerCrit = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_PER_CRIT, paramPlantacion.getPeriodoCriticoNecesidades());
			if (clasifPerCrit != null){
				paramPlantacion.setIdPerCrit(clasifPerCrit.getIdClasif());
			}
			if (paramPlantacion.getVentaContrato().equalsIgnoreCase("NO")){
				paramPlantacion.setIdVentaContrato(Integer.valueOf("0"));
			}else{
				paramPlantacion.setIdVentaContrato(Integer.valueOf("1"));
			}
		}
	}



	
}
