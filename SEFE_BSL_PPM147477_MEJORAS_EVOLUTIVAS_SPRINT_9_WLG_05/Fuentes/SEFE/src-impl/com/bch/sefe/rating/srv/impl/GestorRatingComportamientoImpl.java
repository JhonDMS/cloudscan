/**
 * 
 */
package com.bch.sefe.rating.srv.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.ErrorMessagesSEFE;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.dao.DeudaClienteDAO;
import com.bch.sefe.comun.dao.impl.DeudaClienteDAOImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.ConsultaServicios;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.ServicioConsultaDeuda;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.srv.impl.ServicioConsultaDeudaODS;
import com.bch.sefe.comun.srv.osb.EvaluacionOsb;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.DeudaCliente;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.dao.RatingComportamientoDAO;
import com.bch.sefe.rating.dao.impl.RatingComportamientoDAOImpl;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingComportamiento;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.vo.Caritas;
import com.bch.sefe.rating.vo.MatrizComportamiento;
import com.bch.sefe.rating.vo.MatrizPuntajeCaritas;
import com.bch.sefe.rating.vo.RatingComportamiento;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.Segmento;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.vaciados.vo.Vaciado;

/**
 * @author Raul Astudillo
 * 
 */
public class GestorRatingComportamientoImpl implements GestorRatingComportamiento {
private final static Logger log = Logger.getLogger(GestorRatingComportamientoImpl.class);
	/*
	 * (sin Javadoc)
	 * 
	 * @see com.bch.sefe.rating.ServicioRatingComportamiento#obtenerRating(java.lang.Long, java.lang.Long)
	 */
	public RatingComportamiento generarRating(String rut, Long idRtgIndividual, String loginUsuario) {
		GestorRatingIndividual gestorRatingIndividual = new GestorRatingIndividualImpl();
		RatingComportamiento ratingComportamiento = new RatingComportamiento();

		ServicioClientes srvCtes = new ServicioClientesImpl();
		Cliente cte = srvCtes.obtenerClientePorRut(rut);

		Long idCliente = Long.valueOf(cte.getClienteId());

		RatingIndividual ratingIndividual = gestorRatingIndividual.buscarRatingIndividual(idCliente, idRtgIndividual);
		
		Caritas caritas = null;
		
		Calendar hoy = Calendar.getInstance();
		int diasVigencia = ConfigDBManager.getValueAsInteger(ConstantesSEFE.KEY_VIGENCIA_EVALUACION_CARITAS_RTG_COMPORTAMIENTO).intValue();
		Calendar limiteVigencia = Calendar.getInstance();
		limiteVigencia.add(Calendar.DAY_OF_MONTH, -diasVigencia);
		caritas = buscarHistoricoCliente(idCliente, hoy.getTime(), limiteVigencia.getTime());
		
		if (caritas == null){
			try {
				caritas = obtenerEvaluacionCaritas(rut, ratingIndividual.getIdBanca());
				if (caritas != null) {
					caritas.setDeServicio(true);
				}
			} catch (Exception e) {
				log.warn(MessageManager.getError(ConstantesSEFE.KEY_MSG_ERROR_INVOCACION_OPERACION, new String[] {"Consulta Comportamiento Crediticio"}), e);
			}
		} else {
			caritas.setDeServicio(false);
			caritas.setPuntajeFinal(caritas.getPuntajePonderado());
		}

		// se guarda la descripcion
		String descripcion = "";
		
		//en caso que caritas sea distinto de null se debe setear fecha puntaje siebel para validar vigencia
		if (caritas != null) {
			ratingComportamiento.setFechaPuntajeSiebel(caritas.getFechaEvaluacion());
		}
		
		// Si no existe informacion de CARITAS se agrega mensaje de error
		if (caritas == null || caritas.getPuntajeFinal() == null) {
			gestorRatingIndividual.borrarRtgComportamientoParaRtgIndividualCurso(idCliente, idRtgIndividual);
			ratingComportamiento.agregarAlerta(MessageManager.getError(ConstantesSEFE.KEY_RTG_COMPORT_ERROR_NO_EXISTE_EVALUACION_CARITAS));
			return ratingComportamiento;
		} else if (Boolean.FALSE.equals(ratingComportamiento.esVigente())) {
			gestorRatingIndividual.borrarRtgComportamientoParaRtgIndividualCurso(idCliente, idRtgIndividual);
			ratingComportamiento.agregarAlerta(MessageManager.getError(ConstantesSEFE.KEY_RTG_COMPORT_ERROR_VIGENCIA_EVALUACION_CARITAS));
			ratingComportamiento.setFechaPuntajeSiebel(null);
			return ratingComportamiento;
		} else {
			GestorUsuarios gestorUsr = new GestorUsuariosImpl();
			Usuario usr = gestorUsr.obtenerPrimerUsuario(loginUsuario);

			ratingComportamiento.setIdCliente(idCliente);
			ratingComportamiento.setIdUsuario(usr.getUsuarioId());
			ratingComportamiento.setDeudaBanco(obtenerTotalDeudaDirecta(rut, ConstantesSEFE.FLAG_DEUDA_TIPO_BANCO));
			ratingComportamiento.setDeudaSBIF(obtenerTotalDeudaDirecta(rut, ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF));
			
			// Se calcula el rating de comportamiento
			ratingComportamiento = calcularRatingComportamiento(ratingIndividual, ratingComportamiento, caritas, ratingIndividual.getIdBanca());
			//se guarda la descripcion siebel en una variable local 
			//debido a que es reemplazada al guardar y actualizar el rating de comportamiento
			descripcion = ratingComportamiento.getDescripcionSiebel();
			
			if (ratingIndividual.getIdRatingComportamiento() == null) {
				ratingComportamiento = guardarRating(ratingComportamiento);
			} else {
				ratingComportamiento.setIdRating(ratingIndividual.getIdRatingComportamiento());
				ratingComportamiento = actualizarRating(ratingComportamiento);
			}
			
			gestorRatingIndividual.actualizarRatingComportamiento(idRtgIndividual, ratingComportamiento);

			gestorRatingIndividual.calcularRating(idCliente, idRtgIndividual);
		}		
		
		// se restaura la descripcion
		ratingComportamiento.setDescripcionSiebel(descripcion);
		
		return ratingComportamiento;
	}
	
	/*
	 * Se obtiene la 'descripcion situacion' que se saca de la historia de 6 meses. En caso que no exista informacion se retorna null.
	 */
	private String obtenerDescripcionHistorico(Long idCliente, Date fechaPuntajeSiebel) {
		if (fechaPuntajeSiebel == null)
			return null;

		Caritas caritas = buscarHistorio6m(idCliente, fechaPuntajeSiebel);

		return (caritas != null ? caritas.getDescripcion() : null);
	}

	/*
	 * Obtiene la deuda total directa para tipo de deuda (SBIF, Banco).
	 * 
	 * En caso de no encontrar deuda se retorna null.
	 */
	private Double obtenerTotalDeudaDirecta(String rutCliente, Integer idTipoDeuda) {
		ServicioConsultaDeuda servicioDeudaODS = new ServicioConsultaDeudaODS();

		DeudaCliente deuda = servicioDeudaODS.buscarDeudaUltimoPeriodoCache(rutCliente, idTipoDeuda);

		return (deuda != null ? deuda.getTotalDeudaDirecta() : null);
	}

	/*
	 * (sin Javadoc)
	 * 
	 * @see com.bch.sefe.rating.srv.GestorRatingComportamiento#actualizarHistorico()
	 */
	public void actualizarHistorico(Caritas caritas) {
		RatingComportamientoDAO comportamientoDAO = new RatingComportamientoDAOImpl();

		comportamientoDAO.actualizarHistorico(caritas);
	}

	/*
	 * (sin Javadoc)
	 * 
	 * @see com.bch.sefe.rating.srv.GestorRatingComportamiento#buscarHistorico6m()
	 */
	public Collection buscarHistorico6m(String rutCliente) {
		RatingComportamientoDAO comportamientoDAO = new RatingComportamientoDAOImpl();
		DeudaClienteDAO deudaDAO = new DeudaClienteDAOImpl();

		ArrayList historico6M = (ArrayList) comportamientoDAO.buscarHistorico6m(rutCliente);

		for (int i = 0; i < historico6M.size(); i++) {
			RatingComportamiento ratingComportamiento = (RatingComportamiento) historico6M.get(i);

			DeudaCliente deudaBanco = deudaDAO.buscarDeuda(rutCliente, ConfigManager.getValueAsInteger("tipo.deuda.banco"), ratingComportamiento.getFechaPuntajeSiebel());
			DeudaCliente deudaSBIF = deudaDAO.buscarDeuda(rutCliente, ConfigManager.getValueAsInteger("tipo.deuda.sbif"), ratingComportamiento.getFechaPuntajeSiebel());

			((RatingComportamiento) historico6M.get(i)).setDeudaBanco(deudaBanco.getTotalDeudaDirecta());
			((RatingComportamiento) historico6M.get(i)).setDeudaSBIF(deudaSBIF.getTotalDeudaDirecta());
		}

		return historico6M;
	}

	private RatingComportamiento calcularRatingComportamiento(RatingIndividual ri, RatingComportamiento rating, Caritas caritas, Integer idBanca) {
		MatrizPuntajeCaritas mtzPtjCarita = null;
		MatrizComportamiento mtzComportamiento = null;
		
		Segmento segmento = getSegmentoPorRI(ri);
		
		mtzPtjCarita = getMatrizPuntajeCaritas(segmento, caritas, ri.getIdBanca());		
		mtzComportamiento = buscarMatrizComportamiento(idBanca,segmento != null ? segmento.getIdSegmento() : null);

		double notaRating = 0;
		double ptjAjuste = 0;
		try {
			notaRating = mtzPtjCarita.getNotaRatingComportamiento().doubleValue();
			ptjAjuste = mtzComportamiento.getPorcentajeAjuste().doubleValue();
		} catch (Exception ex) {
			String msg = new String();
			if (mtzPtjCarita == null) {
				msg = ErrorMessagesSEFE.ERR_RANGO_PTJ_CARITAS_NO_VIGENTE;
			}
			if (mtzComportamiento == null) {
				msg = ErrorMessagesSEFE.ERR_MATRIZ_COMPORTAMIENTO_NO_VIGENTE;
			}
			throw new BusinessOperationException(msg);
		}

		Double nota = new Double((notaRating * ptjAjuste) / 100);

		rating.setNotaComp0m(new Double(mtzPtjCarita.getNotaRatingComportamiento().toString()));
		rating.setIdPuntajeCaritas(mtzPtjCarita.getPuntajeCaritasID());
		rating.setFechaEvaluacion(new Date());
		rating.setPorcentajeAjusteComportamiento(mtzComportamiento.getPorcentajeAjuste());
		rating.setRatingComportamiento(nota);
		rating.setPorcentajeAjusteComportamiento(new Double(ptjAjuste));
		rating.setPuntajeSiebel(caritas.getPuntajeFinal());
		rating.setFechaPuntajeSiebel(caritas.getFechaEvaluacion());
		if (caritas.isDeServicio()) {
			rating.setDescripcionSiebel(obtenerDescripcionHistorico(rating.getIdCliente(), rating.getFechaEvaluacion()));
		} else {
			rating.setDescripcionSiebel(caritas.getDescripcion());
		}

		// marias 2012.07.18
		// se ajusta el rating segun historico de comportamiento
		// solo para las PyME
		if (ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
			if (log.isDebugEnabled()) {
				log.debug("Aplicando ajuste rating comportamiento PyME...");
			}

			rating = ajustarRatingPyme(rating);

			if (log.isInfoEnabled()) {
				log.info("Rating comportamiento PyME ajustado por comportamiento historico :: " + rating.getRatingComportamiento());
			}

		}
		//Sprint 3 7.4.2 ajuste de nota minima
		Double notaMinima = RatingUtil.getNotaMinima(idBanca, ConstantesSEFE.TIPO_RATING_COMPORTAMIENTO, rating.getRatingComportamiento());
		if (rating.getRatingComportamiento() != null && rating.getRatingComportamiento().compareTo(notaMinima) < 0) {
			rating.setRatingComportamiento(notaMinima);
		}
		return rating;
	}
	
	/*
	 * Busca la matriz de puntaje caritas que corresponde. Si la banca utiliza segmentos de ventas para la matriz de puntaje caritas, se obtiene el
	 * segmento en base a las ventas y con ese segmento se busca la matriz. Si la banca no utiliza segmentos, no se utiliza el segmento para la
	 * busqueda de la matriz.
	 */
	private MatrizPuntajeCaritas getMatrizPuntajeCaritas(Segmento segVtas, Caritas caritas, Integer idBanca) {
		MatrizPuntajeCaritas mtzPtjCarita = null;
		if (segVtas != null) {
			mtzPtjCarita = buscarMatrizPuntajeCaritas(idBanca, caritas.getPuntajeFinal(), segVtas.getIdSegmento());
		} else {
			if (log.isDebugEnabled()) {
				log.debug("La banca [" + idBanca + "] NO utiliza Tipo de Segmentaci贸n para obtener la Matriz de Puntaje Caritas");
			}
			mtzPtjCarita = buscarMatrizPuntajeCaritas(idBanca, caritas.getPuntajeFinal());
		}
		return mtzPtjCarita;
	}
	
	/**
	 * m閠odo que retorna el segmento seg鷑 el modelo de evaluaci髇 del rating individual
	 * @param ri Rating Individual
	 * @return segmento asociado al modelo de evaluaci髇 del rating individual pasado como argumento
	 */
	private Segmento getSegmentoPorRI(RatingIndividual ri) {
		Segmento segVtas = null;
		Integer idTpoSegmento = getIdTipoSegmento(ri.getIdBanca());
		boolean usaSegmentacion = (idTpoSegmento != null);
		if (usaSegmentacion) {
			GestorRating gr = new GestorRatingImpl();
			Vaciado vac;
			
			if (log.isDebugEnabled()) {
				log.debug("La banca [" + ri.getIdBanca() + "] utiliza Tipo de Segmentaci贸n para obtener la Matriz de Puntaje Caritas");
				log.debug("El Tipo de Segmentacion a utilizar es [" + idTpoSegmento + "]");
			}
			
			// Se busca el vaciado utilizado 贸 a utilizar por el rating financiero
			vac = RatingUtil.buscarVaciado(ri);
			
			if (vac == null  && !ConstantesSEFE.BANCA_AGRICOLAS.equals(ri.getIdBanca())) {
				log.error("No se encontro Vaciado, por lo que no es posible obtener la Matriz de Puntaje Caritas por segmento de ventas");
				throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.KEY_ERR_COMPORTAMIENTO_NO_EXISTE_VACIADO_PARA_CALCULO_SEGMENTO));
			}
			if (ConstantesSEFE.BANCA_AGRICOLAS.equals(ri.getIdBanca())) {
				segVtas = gr.obtenerSegmentoVentasRatingComportamiento(ri, idTpoSegmento, ri.getIdBanca());				
			}else {
				segVtas = gr.obtenerSegmentoVentasRatingComportamiento(vac, idTpoSegmento, ri.getIdBanca());
			}
				
			if (segVtas == null) {
				log.error("No existe un segmento de ventas configurado para el rating de comportamiento");
				throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.KEY_ERR_COMPORTAMIENTO_NO_EXISTE_SEGMENTO));
			}
		}
		return segVtas;
	}
	
	/*
	 * Retorna el tipo de segmento configurado para la banca. Si se retorna null quiere decir que no utiliza segmentaci贸n.
	 */
	private Integer getIdTipoSegmento(Integer idBanca) {
		return ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_COMPORTAMIENTO_USA_TPO_SEGMENTO, idBanca);
	}
	
	/**
	 * Calcula un ajuste al rating de comportamiento PyME basado en el comportamiento historico
	 * del cliente, dado un per铆odo de tiempo.
	 * 
	 * @param rating
	 * @return
	 */
	private RatingComportamiento ajustarRatingPyme(RatingComportamiento rating) {
		int mesesDesde = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RATING_COMP_HISTORICO_LIM_INFERIOR).intValue();
		int mesesHasta = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RATING_COMP_HISTORICO_LIM_SUPERIOR).intValue();
		
		Calendar fechaEval = Calendar.getInstance();
		fechaEval.setTime(rating.getFechaEvaluacion());
		
		// rango de fecha desde - hasta
		fechaEval.add(Calendar.MONTH, -mesesDesde);
		Date fechaDesde = fechaEval.getTime();
		
		fechaEval.setTime(rating.getFechaEvaluacion());
		fechaEval.add(Calendar.MONTH, -mesesHasta);
		Date fechaHasta = fechaEval.getTime();
		
		// se consulta el historico de comportamiento
		Caritas historico = this.buscarHistoricoCliente(rating.getIdCliente(), fechaDesde, fechaHasta);
		
		// existe comportamiento historico entre 4 y 6 meses - default
		// no hay correcion a la nota calculada
		if (historico != null) {
			final double PORCENTAJE_AJUSTE_NOTA_CON_HISTORIA = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RATING_COMP_AJUSTE_NOTA_CON_HISTORIA).doubleValue();
			double notaAjustada = rating.getRatingComportamiento().doubleValue() * PORCENTAJE_AJUSTE_NOTA_CON_HISTORIA / 100.0;
			rating.setRatingComportamiento(new Double(notaAjustada));
			return rating;
		}
		
		// no existe comportamiento historico,se recuperan los valores parametricos
		final double PORCENTAJE_AJUSTE_NOTA_ACTUAL 	= ConfigDBManager.getValueAsInteger(ConstantesSEFE.RATING_COMP_AJUSTE_NOTA_ACTUAL).doubleValue();
		final double PORCENTAJE_AJUSTE_NOTA_PARAM 	= ConfigDBManager.getValueAsInteger(ConstantesSEFE.RATING_COMP_AJUSTE_NOTA_PARAM).doubleValue();

		// valor de nota default, cuando no existe nota historica del cliente
		double notaHistorica = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RATING_COMP_NOTA_PUNTAJE_NO_DISPONIBLE).doubleValue();
		
		double notaAjustada = (PORCENTAJE_AJUSTE_NOTA_ACTUAL * rating.getRatingComportamiento().doubleValue() + PORCENTAJE_AJUSTE_NOTA_PARAM * notaHistorica) / 100.0;
		
		rating.setRatingComportamiento(new Double(notaAjustada));
		return rating;
	}
	
	
	public MatrizPuntajeCaritas buscarMatrizPuntajeCaritas(Integer idBanca, Double puntaje) {
		RatingComportamientoDAO comportamientoDAO = new RatingComportamientoDAOImpl();

		return comportamientoDAO.obtenerMatrizPuntajeCaritas(idBanca, puntaje);
	}
	
	public MatrizPuntajeCaritas buscarMatrizPuntajeCaritas(Integer idBanca, Double puntaje, Integer idSegmento) {
		return new RatingComportamientoDAOImpl().obtenerMatrizPuntajeCaritas(idBanca, puntaje, idSegmento);
	}

	public MatrizComportamiento buscarMatrizComportamiento(Integer idBanca, Integer idSegmento) {
		RatingComportamientoDAO comportamientoDAO = new RatingComportamientoDAOImpl();

		return comportamientoDAO.obtenerMatrizComportamiento(idBanca, idSegmento);
	}

	public RatingComportamiento obtenerRating(Long idCliente, Long idRating) {
		RatingComportamientoDAO rtgComportamientoDAO = new RatingComportamientoDAOImpl();

		return rtgComportamientoDAO.buscarRatingPorID(idRating, idCliente);
	}

	public RatingComportamiento guardarRating(RatingComportamiento rating) {
		RatingComportamientoDAO ratingComportamientoDAO = new RatingComportamientoDAOImpl();
		return ratingComportamientoDAO.guardarRating(rating);
	}

	public RatingComportamiento actualizarRating(RatingComportamiento rating) {
		RatingComportamientoDAO ratingComportamientoDAO = new RatingComportamientoDAOImpl();
		ratingComportamientoDAO.actualizarRating(rating);
		return ratingComportamientoDAO.buscarRatingPorID(rating.getIdRating(), rating.getIdCliente());
	}

	public Caritas obtenerEvaluacionCaritas(String rutCliente, Integer idBanca) {
		ConsultaServicios consultaServicios = (ConsultaServicios) ConfigManager.getInstanceOf(ConstantesSEFE.CONSULTA_SERVICIOS_OSB_CLASS);
		Caritas caritas = null;
		
		// 28.05.2012 Enrique indica: Para el Rating de Comportamiento del 
		// Rating Individual SIEMPRE se invoca al "Servicio Crediticio Empresa".
		// El servicio de "Pre-Evaluador Persona" solo es utilizado en el Rating de Grupo, 
		// cuando se vincula a una persona.
		List evaluacionOsbList = consultaServicios.consultarComportamientoCrediticio(rutCliente);
		EvaluacionOsb evaluacionOsb = (EvaluacionOsb) evaluacionOsbList.get(0);

		caritas = crearObjetoCaritas(evaluacionOsb, rutCliente);

		return caritas;
	}

	/*
	 * Crea un objeto CARITAS en base a la informacion de Evalucion Osb.
	 */
	private Caritas crearObjetoCaritas(EvaluacionOsb evalOsb, String rut) {
		Caritas caritas = new Caritas();
		GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();

		Cliente cliente = gestorClientes.obtenerClientePorRut(rut);

		caritas.setIdParteInvolucrada(new Long(cliente.getClienteId()));
		caritas.setFechaEvaluacion(evalOsb.getFechaHora().getTime());
		caritas.setPuntajeFinal(new Double(evalOsb.getPuntajeEvaluacion()));
		caritas.setPuntajePonderado(new Double(0));
		caritas.setDescripcion(evalOsb.getDescripcionPlan() != null ? evalOsb.getDescripcionPlan() : "");

		return caritas;
	}

	public Caritas buscarHistorio6m(Long idCliente, Date fechaPuntajeSiebel) {
		RatingComportamientoDAO rtgComportamientoDAO = new RatingComportamientoDAOImpl();
		
		return rtgComportamientoDAO.buscarHistorico6m(idCliente, fechaPuntajeSiebel);
	}

	public String calcularColorPuntaje(Double nota) {
		if (nota != null && nota.compareTo(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_VERDE_MIN)) >= 0
				&& nota.compareTo(new Double(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_VERDE_MAX).doubleValue() + 1)) < 0) {

			return ConstantesSEFE.KEY_RTG_COMP_PJE_VERDE;

		} else if (nota != null && nota.compareTo(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_AMARILLO_MIN)) >= 0
				&& nota.compareTo(new Double(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_AMARILLO_MAX).doubleValue() + 1)) < 0) {

			return ConstantesSEFE.KEY_RTG_COMP_PJE_AMARILLO;

		} else if (nota != null && nota.compareTo(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_ROJO_MIN)) >= 0
				&& nota.compareTo(new Double(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_ROJO_MAX).doubleValue() + 1)) < 0) {

			return ConstantesSEFE.KEY_RTG_COMP_PJE_ROJO;

		}
		
		return null;
	}

	public String calcularColorPuntaje(String evaluacion) {
		if (ConfigDBManager.getValueAsString(ConstantesSEFE.RESULTADO_VERDE_PRE_EVALUACION_PERSONA).equalsIgnoreCase(evaluacion)) {
			return ConstantesSEFE.KEY_RTG_COMP_PJE_VERDE;
		}
		
		if (ConfigDBManager.getValueAsString(ConstantesSEFE.RESULTADO_AMARILLO_PRE_EVALUACION_PERSONA).equalsIgnoreCase(evaluacion)) {
			return ConstantesSEFE.KEY_RTG_COMP_PJE_AMARILLO;
		}
		
		if (ConfigDBManager.getValueAsString(ConstantesSEFE.RESULTADO_ROJO_PRE_EVALUACION_PERSONA).equalsIgnoreCase(evaluacion)) {
			return ConstantesSEFE.KEY_RTG_COMP_PJE_ROJO;
		}
		
		return null;
	}
	
	
	/* (sin Javadoc)
	 * @see com.bch.sefe.rating.srv.GestorRatingComportamiento#buscarHistoricoCliente(java.lang.Long, java.util.Date, java.util.Date)
	 */
	public Caritas buscarHistoricoCliente(Long idCliente, Date desde, Date hasta) {
		RatingComportamientoDAO dao = new RatingComportamientoDAOImpl();
		
		Caritas caritas = dao.buscarHistoricoCliente(idCliente, desde, hasta);

		return caritas;
	}
}
