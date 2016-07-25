package com.bch.sefe.rating.srv.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.CatalogoGeneral;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.CatalogoGeneralImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.ServicioRatingIndividual;
import com.bch.sefe.rating.dao.RatingDAO;
import com.bch.sefe.rating.dao.RatingNegocioDAO;
import com.bch.sefe.rating.dao.impl.RatingDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingNegocioDAOImpl;
import com.bch.sefe.rating.impl.ServicioRatingIndividualImpl;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.GestorRatingNegocio;
import com.bch.sefe.rating.vo.AlternativaNegocio;
import com.bch.sefe.rating.vo.MatrizNegocio;
import com.bch.sefe.rating.vo.PreguntaNegocio;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.rating.vo.RespuestaNegocio;
import com.bch.sefe.rating.vo.Segmento;
import com.bch.sefe.rating.vo.TemaNegocio;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.vaciados.CatalogoVaciados;
import com.bch.sefe.vaciados.impl.CatalogoVaciadosImpl;
import com.bch.sefe.vaciados.srv.GestorPlanCuentas;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorPlanCuentasImpl;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;

public class GestorRatingNegocioImpl implements GestorRatingNegocio {
	
	private static final Double CIEN = new Double("100.0");

	public MatrizNegocio buscarMatrizNegocio(Integer idBanca, Integer idSegmento, Integer idEstado, Date fechaRating) {
		RatingNegocioDAO daoNeg = new RatingNegocioDAOImpl();
		MatrizNegocio matrizNeg = new MatrizNegocio();
		matrizNeg = daoNeg.buscarMatrizNegocio(idBanca, idSegmento);
		
		if (matrizNeg != null)
			matrizNeg = cargarMatrizNegocio(matrizNeg, idEstado, fechaRating);
		
		return matrizNeg;
	}
	
	
	/*
	 * Carga la instancia de matriz de negocio con los temas, preguntas y respuestas.
	 */
	protected MatrizNegocio cargarMatrizNegocio(MatrizNegocio matriz, Integer idEstado, Date fechaRating) {
		RatingNegocioDAO daoNeg = new RatingNegocioDAOImpl();
		Collection temasNegocio = daoNeg.buscarTemaNegocio(matriz.getIdMatriz(), idEstado, fechaRating);

		Iterator itTemasNegocio = temasNegocio.iterator();
		while(itTemasNegocio.hasNext()) {
			TemaNegocio temaNegocio = (TemaNegocio) itTemasNegocio.next();

			matriz.agregarTema(temaNegocio);

			Collection preguntasNegocio = daoNeg.buscarPreguntaNegocio(matriz.getIdMatriz(), temaNegocio.getIdTema(), idEstado, fechaRating);
			Iterator itPreguntasNegocio = preguntasNegocio.iterator();
			while(itPreguntasNegocio.hasNext()) {
				PreguntaNegocio preguntaNegocio = (PreguntaNegocio) itPreguntasNegocio.next();

				temaNegocio.agregarPregunta(preguntaNegocio);

				ArrayList alternativasNegocio  = (ArrayList) daoNeg.buscarAlternativaNegocio(matriz.getIdMatriz(), temaNegocio.getIdTema(), preguntaNegocio.getIdPregunta(), idEstado, fechaRating);
				Iterator itAlternavitasNegocio = alternativasNegocio.iterator();
				while(itAlternavitasNegocio.hasNext()) {
					AlternativaNegocio alternativaNegocio = (AlternativaNegocio) itAlternavitasNegocio.next();

					preguntaNegocio.agregarAlternativa(alternativaNegocio);
				}
			}
		}

		return matriz;
	}

	public RatingNegocio buscarRatingVigente(String rut, Integer idBanca) {
		RatingNegocioDAO daoNeg = new RatingNegocioDAOImpl();
		RatingNegocio rtgNeg = new RatingNegocio();
		Integer diasEvaluacion=ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_NEGOCIO_DIAS_MAX_EVALUACION,idBanca);
		rtgNeg = daoNeg.buscarRatingVigente(rut, idBanca,diasEvaluacion);
		return rtgNeg;
	}
	
	/**
	 * Actualiza la nota del rating de negocio. Si no existe el rating, lo crea.
	 * 
	 * Si el rating de negocio existe, se debe verificar que no este asociado a un rating
	 * final historico o en curso. En ese caso, se crea una nueva instancia del rating de 
	 * negocio; en caso contrario, se actualiza.
	 */
	public RatingNegocio guardarRating(RatingNegocio rating) {
		RatingNegocioDAO daoNeg = new RatingNegocioDAOImpl();
		RatingNegocio rtgNeg = new RatingNegocio();
		rtgNeg = daoNeg.guardarRating(rating);
		return rtgNeg;
	}
	
	public boolean esVigente(RatingNegocio rating, Integer idBanca) {
		if (rating == null || rating.getFechaEvaluacion() == null)
			return false;
		//se ha cambiado de usar calendar a date puesto que la comparacion equals provocaba problemas.
		long timeFechaActual = buscarFechaHoy().getTime();
		
		Calendar fchPuntajeSiebel = Calendar.getInstance();
		fchPuntajeSiebel.setTime(rating.getFechaEvaluacion());
		fchPuntajeSiebel.set(Calendar.HOUR, 0);
		fchPuntajeSiebel.set(Calendar.MINUTE, 0);
		fchPuntajeSiebel.set(Calendar.SECOND, 0);
		fchPuntajeSiebel.set(Calendar.MILLISECOND, 0);
		
		long diferencia = timeFechaActual - fchPuntajeSiebel.getTime().getTime();		
		diferencia = (diferencia / (1000 * 60 * 60 * 24));

		//long diasVigencia = ConfigManager.getValueAsInteger(ConstantesSEFE.KEY_RATING_NEGOCIO_VIGENTE + ConstantesSEFE.PUNTO + idBanca).longValue();
		Object diasVigencia = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_NEGOCIO_DIAS_MAX_EVALUACION, idBanca);
		long newDiasVigencia =0;
		if (diasVigencia== null || ConstantesSEFE.ESPACIO_VACIO.equals(diasVigencia)) {
			return false;
		}
		newDiasVigencia = new Long(diasVigencia.toString()).longValue();
		// Si la fecha de evaluacion es posterior o igual a la fecha de antiguedad maxima, es vigente
		return (diferencia <= newDiasVigencia);
	}

	public RatingNegocio buscarRatingNegocioPorId(Long idRating) {
		RatingNegocio rtgNeg = new RatingNegocio();
		RatingNegocioDAO daoNeg = new RatingNegocioDAOImpl();
		rtgNeg = daoNeg.buscarRatingNegocioPorId(idRating);
		return rtgNeg;
	}

	public MatrizNegocio buscarMatrizNegocioPorId(Long idMatriz, Integer idEstado, Date fechaRating) {
		MatrizNegocio matrizNeg = new MatrizNegocio();
		RatingNegocioDAO daoNeg = new RatingNegocioDAOImpl();
		
		matrizNeg = daoNeg.buscarMatrizNegocioPorId(idMatriz);
		
		matrizNeg = cargarMatrizNegocio(matrizNeg,idEstado, fechaRating);
		
		return matrizNeg;
	}

	public List buscarRespuestasPorRating(Long idRating) {
		RatingNegocioDAO ratNegocioDAO = new RatingNegocioDAOImpl();
		
		return ratNegocioDAO.buscarRespuestasRatingNegocio(idRating);
	}

	public Collection buscarTemasPorMatriz(Long idMatriz, Integer idEstado, Date fechaRating) {
		RatingNegocioDAO rtgNegocioDAO = new RatingNegocioDAOImpl();
		
		return rtgNegocioDAO.buscarTemaNegocio(idMatriz, idEstado, fechaRating);
	}

	public void guardarRespuesta(RespuestaNegocio resp) {
		RatingNegocioDAO rtgNegocioDAO = new RatingNegocioDAOImpl();
		
		rtgNegocioDAO.grabarRespuesta(resp);		
	}
	
	public Integer borrarRespuestas(Long idCliente, Long idRatingNegocio) {
		RatingNegocioDAO rtgNegocioDAO = new RatingNegocioDAOImpl();
		
		return rtgNegocioDAO.borrarRespuestas(idCliente, idRatingNegocio);
	}

	public RatingNegocio calcularRating(Long idRatingNegocio, Integer idEstado, Date fechaRating) {
		GestorRatingNegocio gestorNegocio = new GestorRatingNegocioImpl();
		
		RatingNegocio rating = gestorNegocio.buscarRatingNegocioPorId(idRatingNegocio);
		MatrizNegocio matriz = gestorNegocio.buscarMatrizNegocioPorId(rating.getIdMatriz(), idEstado, fechaRating);
		
		// temas vienen ordenados por campo orden
		List temas = matriz.getTemasNegocio();
		
		// preguntas vienen ordenados por campo orden
		List preguntas	 = agruparPreguntasMatriz(temas);
		List respuestas	 = gestorNegocio.buscarRespuestasPorRating(idRatingNegocio);

		// se valida que el número de preguntas sea igual al numero de respuestas
		if (preguntas.size() != respuestas.size()) {			
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.KEY_RTG_NEG_ERROR_CUESTIONARIO_INCOMPLETO));
		}

		// las respuestas se ordenan por matriz tema pregunta
		Map mapaRespuestas = new HashMap();
		for (int i = 0; i < respuestas.size(); ++i) {
			RespuestaNegocio resp = (RespuestaNegocio) respuestas.get(i);
			mapaRespuestas.put(getKey(resp), resp);
		}
		
		// se comienza a calcular por tema
		double notaRating = 0;
		
		Iterator itTemas = temas.iterator();
		while(itTemas.hasNext()) {
			TemaNegocio tema = (TemaNegocio) itTemas.next();
			double notaTema = 0;
			
			for (int j = 0; j < preguntas.size(); ++j) {
				PreguntaNegocio pregunta = (PreguntaNegocio) preguntas.get(j);
				
				if (pregunta.getIdTema().equals(tema.getIdTema())) {
					String key = getKey(pregunta);
					RespuestaNegocio resp = (RespuestaNegocio) mapaRespuestas.get(key);
					notaTema = notaTema + resp.getNota().doubleValue() * convertirAPorcentaje(pregunta.getPonderacion());
				}
			}
			
			notaRating = notaRating + notaTema * convertirAPorcentaje(tema.getPonderacion());
		}
		
		// se aplica el ajuste de la nota
		double notaRatingAjustada = notaRating * convertirAPorcentaje(matriz.getPorcentajeAjuste());
		
		// se actualiza el rating
		rating.setNotaNegocioSinTope(new Double(notaRating));
		
		// si es pyme, la nota ajustada tiene tope
		//if (matriz.getIdBanca().equals(ConstantesSEFE.BANCA_PYME) && matriz.getNotaTope() != null) {
		if (matriz.getNotaTope() != null) {
			double tope = matriz.getNotaTope().doubleValue();
			
			// se conserva el menor valor
			if (notaRatingAjustada < tope) {
				rating.setNotaNegocio(new Double(notaRatingAjustada));
			} else {
				rating.setNotaNegocio(new Double(tope));
			}
		} else {
			rating.setNotaNegocio(new Double(notaRatingAjustada));
		}
		//Sprint 3 7.4.2 ajuste de nota minima
		Double notaMinima = RatingUtil.getNotaMinima(matriz.getIdBanca(), ConstantesSEFE.TIPO_RATING_NEGOCIO, rating.getNotaNegocio());
		if (rating.getNotaNegocio() != null && rating.getNotaNegocio().compareTo(notaMinima) < 0) {
			rating.setNotaNegocio(notaMinima);
		}
		return rating;
	}
	
	private double convertirAPorcentaje(Double valor) {
		if(valor == null)
			return 0.0;
		
		return (valor.doubleValue() / CIEN.doubleValue());
	}
	
	/**
	 * Recorre todos los temas y saca sus preguntas agrupandolas en una sola lista.
	 * @param temas
	 * @return
	 */
	private List agruparPreguntasMatriz(List temas) {
		ArrayList lstPreguntas = new ArrayList();
		Iterator itTemas = temas.iterator();
		
		while (itTemas.hasNext()) {
			TemaNegocio tema = (TemaNegocio) itTemas.next();
			
			lstPreguntas.addAll(tema.getPreguntas());
		}
		
		return lstPreguntas;
	}
	
	public boolean esRatingVigenteHistorico(Long idRatingNegocio) {
		// TODO Apéndice de método generado automáticamente
		return false;
	}
	
	
	/**
	 * Genera una clave unica para la pregunta de negocio.
	 * Esta clave es la misma que se genera para las respuestas.
	 * 
	 * @param p
	 * @return
	 */
	private String getKey(PreguntaNegocio p) {
		return MessageFormat.format("{0}:{1}:{2}", new String[] {p.getIdMatriz().toString(), p.getIdTema().toString(), p.getIdPregunta().toString()});
	}
	
	
	/**
	 * Genera una clave unica para la respuesta de negocio.
	 * Esta clave es la misma que se genera para las preguntas.
	 * 
	 * @param r
	 * @return
	 */
	private String getKey(RespuestaNegocio r) {
		return MessageFormat.format("{0}:{1}:{2}", new String[] {r.getIdMatriz().toString(), r.getIdTema().toString(), r.getIdPregunta().toString()});
	}

	public RatingNegocio actualizarRating(RatingNegocio rating) {
		RatingNegocioDAO rtgDao = new RatingNegocioDAOImpl();
		return rtgDao.actualizarRating(rating);
	}

	public AlternativaNegocio buscarAlternativa(Long idAlternativa, Long idMatriz, Long idTema, Long idPregunta) {
		RatingNegocioDAO ratingNegocioDAO = new RatingNegocioDAOImpl();
		
		return ratingNegocioDAO.buscarAlternativa(idAlternativa, idMatriz, idTema, idPregunta);
	}


	public Integer buscarTipoEvaluacionRating(String rutCliente, Integer idBanca, Long idRatingInd) {
		CatalogoGeneral catalogo = new CatalogoGeneralImpl();
		Integer idClasif = null;
		// PyME: Siempre debe mostrar el individual, aunque no se haya confirmado el Rating Financiero
		if (idBanca.equals(ConstantesSEFE.BANCA_PYME)) {
			idClasif = ConstantesSEFE.CLASIF_ID_TPO_VACIADO_INDIVIDUAL;
		} else {
			// Corp y GGEE – Soc. de Inversiones: Si no se ha confirmado el Rating Financiero, 
			// este campo debe mostrar el Tipo de Vaciado por defecto con el cual se calcula 
			// de forma automática en Rating Financiero.
			GestorRatingFinanciero gestorFinan = new GestorRatingFinancieroImpl();
			GestorRating		gestorRating   = new GestorRatingImpl();
			CatalogoVaciados catalogoVac = new CatalogoVaciadosImpl();
			ServicioRatingIndividual servInd = new ServicioRatingIndividualImpl();
			ServicioClientes servCtes = new ServicioClientesImpl();
			Cliente cte = servCtes.obtenerClientePorRut(rutCliente);
			
			RatingIndividual ratingIndividual = servInd.buscarRatingPorId(new Long(cte.getClienteId()), idRatingInd);
			if (ratingIndividual.getIdRatingFinanciero() != null) {
				RatingFinanciero finan = gestorFinan.obtenerRating(ratingIndividual.getIdRatingFinanciero());
				Vaciado vac = catalogoVac.buscarDatosGeneral(finan.getIdVaciado0());
				idClasif = vac.getIdTpoVaciado();
			} else {
				try {
					List vaciados = gestorRating.buscarListaVaciadosParaRating(rutCliente, idBanca);
					Vaciado vac = (Vaciado) vaciados.get(0);
					idClasif = vac.getIdTpoVaciado();
				} catch (Exception ex) {
					idClasif = ConstantesSEFE.CLASIF_ID_TPO_VACIADO_INDIVIDUAL;
				}
			}
		}
		Clasificacion cl = catalogo.buscarClasificacionPorId(idClasif);
		
		//return cl.getNombre();
		return idClasif;
	}
	
	public  void validarVigenciaVaciado(RatingIndividual ratingInd, String rutCliente, Integer idEstado) {
		Segmento segmentoVta                   = null;
		Integer idSegmento                     = null;
		RatingNegocio ratingNegocio            = null;
		// se determina si es banca PyME
		final boolean esBancaPyME = ConstantesSEFE.BANCA_PYME.equals(ratingInd.getIdBanca());
		
		// Se identifica el segmento de venta. Opcional. Solo si es PyME.
		// El segmento se saca siempre(para PyME) basado en el ultimo vaciado vigente que exista.
		if (esBancaPyME) {			
			segmentoVta = obtenerSegmentoVentas(rutCliente, ratingInd);
			idSegmento  = segmentoVta.getIdSegmento();
		}else {// no aplica validacion
			return;
		}
	
		if (ratingInd.getIdRatingNegocio() != null) {
			ratingNegocio = buscarRatingNegocioPorId(ratingInd.getIdRatingNegocio());
		}else {
			//no es necesario validar
			return;
		}
		
		Integer idSegmentoActual;
		MatrizNegocio matrizNegocio = null;
		
		matrizNegocio = buscarMatrizNegocioPorId(ratingNegocio.getIdMatriz(), idEstado, ratingNegocio.getFechaEvaluacion());
		idSegmentoActual = matrizNegocio.getIdSegmento();
	
		
		// Si es banca PyME se compara el segmento sacado con el ultimo vaciado vigente existente y se compara con el segmento asociado
		// al rating de negocio existente. Si segmentos son distintos se lanza excepcion de negocio.
		// En caso que el segmento no haya cambiado se valida que la matriz siga vigente.
		if ( !idSegmentoActual.equals(idSegmento)) {
			throw new  BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_NEG_ALERTA_CAMBIO_SEGMENTO));
		} 
		
	}
	
	
	private Segmento obtenerSegmentoVentas(String rutCliente, RatingIndividual ri) {
		GestorRating gestorRating = new GestorRatingImpl();
		Vaciado vaciado   	 = null;
		Segmento segmentoVta = null;
		
		// PYME, el cuestionario y calculos asociados a la matriz de configuracion
		// vigente mas actual con un maximo de 18 meses de antiguedad respecto a la fecha actual.
		// Esta seleccion debe privilegiar tipo Ajustado sobre Original y fuente Balancesobre DAI y
		// considerar los planes de cuenta CHGAAP, IFRSCN e IFRSCF.
		vaciado = gestorRating.buscarVaciadoParaRatingPYME(rutCliente, ConstantesSEFE.BANCA_PYME, ri);
		//vaciado = gestorRating.buscarVaciadoParaRatingPYME(rutCliente, ConstantesSEFE.BANCA_PYME);

		if (vaciado == null) {
			// No existe vaciado anual vigente.
			// Sistema debe enviar mensaje de error (Mensaje 1) al usuario,
			// finalizando acciones sobre la pestana Rating de Negocio
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.KEY_RTG_NEG_ERROR_VACIADO_NO_EXISTE));
		}

		// nivel de ventas se obtiene del vaciado
		Double montoVentas = obtenerMontoVentas(vaciado);

		Integer idBanca = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		segmentoVta = gestorRating.buscarSegmento(ConstantesSEFE.SEGMENTO_VENTAS, montoVentas, idBanca);
		
		if (segmentoVta == null) {			
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.KEY_MENSAJE_NO_EXISTE_SEGMENTO_VENTAS));
		}

		return segmentoVta;
	}
	
	private Double obtenerMontoVentas(Vaciado vaciado) {
		String claveVentas 	= ConstantesSEFE.CODIGO_CUENTA_VENTAS ;
		String codCuentaVentas = ConfigDBManager.getValueAsString(claveVentas);

		// Se recupera la cuenta desde el gestor de plan de cuentas
		GestorPlanCuentas gestorCuentas = new GestorPlanCuentasImpl();
		Cuenta cuenta = gestorCuentas.consultarValorCuentaVaciado(vaciado.getIdVaciado(), codCuentaVentas);
		Double ventas = cuenta.getMonto();
		Double ajuste = cuenta.getAjuste();

		// se obtiene el valor de las ventas del vaciado
		if (ajuste != null) {
			ventas = new Double(ventas.doubleValue() + ajuste.doubleValue());
		}

		// Se convierte la moneda y unidad del vaciado a M UF
		if (ventas != null) {
			ConversorMoneda convMoneda = new ConversorMonedaImpl();
			
			ventas = convMoneda.convertirMoneda(ventas, vaciado.getIdMoneda(), vaciado.getUnidMedida(), 
					ConstantesSEFE.ID_CLASIF_MONEDA_UF, ConstantesSEFE.ID_CLASIF_MILES, 
					convMoneda.buscarDiaHabilSiguiente(vaciado.getPeriodo()));
		}

		return ventas;
	}
	
	public void validarEvaluacionRatingNegocio(Long idCliente, Long idRatingNegocio, Long idRatingIndividual) {
		GestorRatingIndividual gestorInd = new GestorRatingIndividualImpl();
		RatingIndividual ratingInd = gestorInd.buscarRatingIndividual(idCliente, idRatingIndividual);
		
		if (ratingInd.getIdRatingFinanciero() != null) {
			GestorRatingFinanciero gestorFin = new GestorRatingFinancieroImpl();
			RatingFinanciero ratingFin = gestorFin.obtenerRating(ratingInd.getIdRatingFinanciero());
			
			GestorVaciados gestorVac = new GestorVaciadosImpl();
			Integer tipoVacFinan = gestorVac.buscarVaciado(ratingFin.getIdVaciado0()).getIdTpoVaciado();
			
			if (tipoVacFinan.intValue() != ratingInd.getIdTipoVaciadoRatingNegocio().intValue()) {
				throw new  BusinessOperationException(ConstantesSEFE.MSG_ALERTA_VALIDACION_TPO_VACIADO_RTG_NEGOCIO);
			}
		}
	}
	public Date buscarFechaHoy(){
		RatingDAO ratingDao = new RatingDAOImpl();
		return ratingDao.buscarFechaHoy();
	}
}
