/**
 * 
 */
package com.bch.sefe.rating;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.util.XMLData;
import com.bch.sefe.comun.util.XMLDataList;
import com.bch.sefe.comun.util.XMLDataObject;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.rating.dao.RatingFinancieroDAO;
import com.bch.sefe.rating.dao.RatingIndividualDAO;
import com.bch.sefe.rating.dao.RatingNegocioDAO;
import com.bch.sefe.rating.dao.impl.RatingFinancieroDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingIndividualDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingNegocioDAOImpl;
import com.bch.sefe.rating.impl.ServicioRatingNegocioImpl;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.vo.AlternativaNegocio;
import com.bch.sefe.rating.vo.MatrizNegocio;
import com.bch.sefe.rating.vo.PreguntaNegocio;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.rating.vo.RespuestaNegocio;
import com.bch.sefe.rating.vo.TemaNegocio;
import com.bch.sefe.util.FormatUtil;

/**
 * @author Raul Astudillo
 *
 */
public class AdaptadorServicioRatingNegocio {
	
	private ServicioRatingNegocio servicioNegocio = new ServicioRatingNegocioImpl();
	
	/**
	 * 
	 */
	public AdaptadorServicioRatingNegocio() {
		
	}
	
	/**
	 * guarda y calcula la calificación de las respuestas seleccionadas por un usuario en el cuestionario de rating de negocio.
	 * @param xmlData
	 * <p>
	 * <b>XMLDataObject</b><br>
	 * ---->idRating
	 * @return
	 */
	public XMLData calcularRating(XMLData xmlData) {		
		Long idRatingNegocio 	= ((XMLDataObject) xmlData).getLong(ConstantesSEFE.ID_RATING);
		Long idRatingIndividual = ((XMLDataObject) xmlData).getLong(ConstantesSEFE.ID_RATING_INDIVIDUAL);
		String usuario 			= (String) ((XMLDataObject) xmlData).getObject(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_LOG_OPE);
		String rutCliente 		= (String) (((XMLDataObject) xmlData).getObject(ConstantesSEFE.RUT_CLIENTE));
		Long idMatriz 			= ((XMLDataObject) xmlData).getLong(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_MATRIZ);
		String rolUsuario       = (String) ((XMLDataObject) xmlData).getObject(ConstantesSEFE.CONSUL_RTG_KEY_ROL);
		XMLDataList respuestas 	= (XMLDataList)((XMLDataObject)xmlData).getObject(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_LISTA_ALTERNATIVAS);
		
		ServicioClientes servCliente = new ServicioClientesImpl();
		Cliente cliente = servCliente.obtenerClientePorRut(rutCliente);
		Long idCliente = Long.valueOf(cliente.getClienteId());
		
		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_CLIENTE, idCliente);
		
		List respNegList = new ArrayList();
		
		for(int i=0; i< respuestas.size(); i++){
			
			XMLDataObject data = (XMLDataObject)respuestas.get(i);
			RespuestaNegocio respNeg = new RespuestaNegocio();
			
			respNeg.setIdMatriz(idMatriz);
			respNeg.setIdCliente(idCliente);
			respNeg.setIdTema(((XMLDataObject)data).getLong(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_TEMA));
			respNeg.setIdPregunta(((XMLDataObject)data).getLong(ConstantesSEFE.ID_PREGUNTA));
			respNeg.setIdRespuesta(((XMLDataObject)data).getLong(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_ALTERNATIVA));
			respNeg.setIdRatingNegocio(idRatingNegocio);
			respNegList.add(respNeg);
			
		}
		
		Map response = servicioNegocio.calcular(idRatingNegocio, idRatingIndividual, usuario, respNegList);
		RatingNegocio cabeceraRating = (RatingNegocio)response.get(ServicioRatingNegocio.PARAM_RATING_NEGOCIO);
		cabeceraRating.setLoginUsuario(usuario);
		return generarRespuestaGuardarRating(cabeceraRating, (String)response.get(ConstantesRating.TIPO_EVALUACION_RATING_NEGOCIO));
	}
	
	/**
	 * Ingresa o actualiza las respuestas seleccionadas por un usuario en el cuestionario de rating de negocio.
	 * 
	 * @param req - XMLDataObject con la estructura definida.
	 * <p>
	 * 	<b>XMLDataObject</b><br>
	 * 	-->idRating<br>
	 *  -->rutCliente<br>
	 *  -->id_matriz<br>
	 *  --><b>lstAlt(XMLDataList)</b><br>
	 *  ----><b>XMLDataObject</b><br>
	 *  ------>idTem<br>
	 *  ------>idPreg<br>
	 *  ------>id_alt<br>  
	 * </p>
	 */
	public XMLData guardarRating(XMLData req) {
		XMLDataObject xmlDataObj = (XMLDataObject) req;
		ArrayList lstRespuestas = new ArrayList();
		ServicioClientes servCliente = new ServicioClientesImpl();
		
		// Se recupera la data de entrada
		Long idRatingNegocio = xmlDataObj.getLong(ConstantesSEFE.ID_RATING);
		Long idRatingIndividual = xmlDataObj.getLong(ConstantesSEFE.ID_RATING_INDIVIDUAL);
		String rutCliente = (String) xmlDataObj.getObject(ConstantesSEFE.RUT_CLIENTE);
		Long idMatriz = xmlDataObj.getLong(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_MATRIZ);
		String loginUsuario = (String) xmlDataObj.getObject(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_LOG_OPE);
		String rolUsuario = (String) xmlDataObj.getObject(ConstantesSEFE.CONSUL_RTG_KEY_ROL);
		XMLDataList xmlLstRespuestas = (XMLDataList) xmlDataObj.getObject(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_LISTA_ALTERNATIVAS);
		
		Cliente cliente = servCliente.obtenerClientePorRut(rutCliente);
		Long idCliente = Long.valueOf(cliente.getClienteId());
		
		// Se obtienen las respuestas seleccionadas por el usuario
		for (int i = 0; i < xmlLstRespuestas.size(); i++) {	
			XMLDataObject xmlRespuesta = (XMLDataObject) xmlLstRespuestas.get(i);

			RespuestaNegocio respNegocio = new RespuestaNegocio();
			respNegocio.setIdTema(xmlRespuesta.getLong(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_TEMA));
			respNegocio.setIdPregunta(xmlRespuesta.getLong(ConstantesSEFE.ID_PREGUNTA));
			respNegocio.setIdRespuesta(xmlRespuesta.getLong(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_ALTERNATIVA));
			respNegocio.setIdCliente(idCliente);
			respNegocio.setIdMatriz(idMatriz);
			respNegocio.setIdRatingNegocio(idRatingNegocio);
			
			lstRespuestas.add(respNegocio);
		}
		
		RatingNegocio cabeceraRating = servicioNegocio.guardarRating(loginUsuario, idRatingNegocio, idRatingIndividual, lstRespuestas);
		
		return generarRespuestaGuardarRating(cabeceraRating);
	}
	
	private XMLDataObject generarRespuestaGuardarRating(RatingNegocio rating, String tipoEval) {
		XMLDataObject xmlRetorno = new XMLDataObject();
		
		xmlRetorno.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_RESPON_RATING_NEG, rating.getLoginUsuario());
		
		// Flag que indica si se puede visualizar la nota. En caso de no poder visualizar la nota, la nota no es agregada
		// en el xml de salida.
		xmlRetorno.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_NOTA, rating.getNotaNegocio());
		
		xmlRetorno.put(ConstantesSEFE.PARAM_ID_RATING, rating.getIdRating());
		
		if (rating.getFechaEvaluacion() != null) {
			xmlRetorno.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_FECHA_RATING_NEG, FormatUtil.formatDate(rating.getFechaEvaluacion()));
		}
		
		if (tipoEval != null) {
			xmlRetorno.put(ConstantesRating.TIPO_EVALUACION_RATING_NEGOCIO, tipoEval);
		}
		
		return xmlRetorno;
	}
	
	
	private XMLDataObject generarRespuestaGuardarRating(RatingNegocio rating) {
		return generarRespuestaGuardarRating(rating, null);
	}
	
	public XMLDataObject generarRating(XMLData req) {
		XMLDataObject request = (XMLDataObject) req;
		String rutCliente     = (String) request.getObject(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_CLIENTE);
		Integer idBanca       = request.getInteger(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_BANCA);
		String logOpe		  = (String) request.getObject(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_LOG_OPE);
		Long idRatingInd	  = (Long) request.getLong(ConstantesSEFE.ID_RATING_INDIVIDUAL);
		String rol            = (String) request.getObject(ConstantesSEFE.CONSUL_RTG_KEY_ROL);
				
		// Se obtienen los objetos de negocio necesarios para generar la salida
		Map retornoRtgNegocio       = servicioNegocio.generarRating(rutCliente, idRatingInd, idBanca, logOpe);
		String tipoEvaluacion 		= (String) retornoRtgNegocio.get(ConstantesRating.TIPO_EVALUACION_RATING_NEGOCIO);
		
		String msgVigenciaMatriz	= (String) retornoRtgNegocio.get(ServicioRatingNegocio.PARAM_MSG_VIGENCIA_MATRIZ);
		RatingNegocio ratingNegocio = (RatingNegocio) retornoRtgNegocio.get(ServicioRatingNegocio.PARAM_RATING_NEGOCIO);
		
		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();
		
		RatingIndividual ratingIndividual = gestorRating.buscarRatingIndividualPorNegocio(ratingNegocio.getIdCliente(), ratingNegocio.getIdRating());
		
		Clasificacion tpoVaciado    = (Clasificacion) retornoRtgNegocio.get(ServicioRatingNegocio.PARAM_TIPO_VACIADO);
		MatrizNegocio matrizNegocio = servicioNegocio.obtenerMatrizNegocio(ratingNegocio.getIdMatriz(), ratingIndividual.getIdEstado(), ratingNegocio.getFechaEvaluacion());
		List respuestasSel			= servicioNegocio.buscarRespuestas(ratingNegocio.getIdRating());

		List idsRespuestasSelec = obtenerIdsRespuestasSeleccionadas(respuestasSel);
		
		XMLDataObject xmlMatrizNeg = new XMLDataObject();
		XMLDataList xmlTemas       = new XMLDataList();
		
		if (matrizNegocio != null)
			xmlMatrizNeg.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_MATRIZ, matrizNegocio.getIdMatriz());
		
		if (ratingNegocio.getFechaEvaluacion() != null)
			xmlMatrizNeg.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_FECHA_RATING_NEG, FormatUtil.formatDate(ratingNegocio.getFechaEvaluacion()));
		
		xmlMatrizNeg.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_RESPON_RATING_NEG, ratingNegocio.getLoginUsuario());
		xmlMatrizNeg.put(ConstantesRating.TIPO_EVALUACION_RATING_NEGOCIO, tipoEvaluacion);
		
		// Se envia la nota en la respuesta solo si es que puede ser visualizada por el rol.
		xmlMatrizNeg.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_NOTA, ratingNegocio.getNotaNegocio());
		
		xmlMatrizNeg.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_MSG_ALERTA, msgVigenciaMatriz);
		
		if(msgVigenciaMatriz != null && !msgVigenciaMatriz.isEmpty() && ratingIndividual.getIdRatingNegocio() != null && ratingIndividual.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO)
				&& 
				(!msgVigenciaMatriz.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_TPO_VACIADO_RTG_NEGOCIO) || (msgVigenciaMatriz.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_TPO_VACIADO_RTG_NEGOCIO)
				&& msgVigenciaMatriz.length() > 109)))
		{
			RatingIndividualDAO ratingIndDao = new RatingIndividualDAOImpl();
			
			// dejar en curso rating proyectado
			RatingNegocioDAO negocioDao = new RatingNegocioDAOImpl();
			RatingNegocio rtgNeg = negocioDao.buscarRatingNegocioPorId(ratingIndividual.getIdRatingNegocio());
			rtgNeg.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
			negocioDao.actualizarEstadoRating(rtgNeg);
//			ratingIndividual.setIdRatingNegocio(null);
//			ratingIndividual.setRatingNegocio(null);
//			ratingIndividual = ratingIndDao.desasociarRatingNegocio(ratingIndividual.getIdCliente(), ratingIndividual.getIdRating());
			
			ratingIndividual.setRatingFinal(null);
			ratingIndividual = ratingIndDao.actualizarRatingIndividual(ratingIndividual);
		}
		
		xmlMatrizNeg.put(ConstantesSEFE.ID_RATING, ratingNegocio.getIdRating());
		xmlMatrizNeg.put(ConstantesRating.FORMULARIO_ESTADO, retornoRtgNegocio.get(ConstantesRating.FORMULARIO_ESTADO));
		
		if (tpoVaciado != null)
			xmlMatrizNeg.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_TIPO_EVALUACION, tpoVaciado.getNombre());
		
		if (matrizNegocio != null) {
			List temasNegocio		= matrizNegocio.getTemasNegocio();
			Iterator itTemasNegocio = temasNegocio.iterator();	

			// Se recorren los temas de la matriz de negocio
			while (itTemasNegocio.hasNext()) {
				XMLDataObject temaObject  = new XMLDataObject();
				TemaNegocio temaVO        = (TemaNegocio) itTemasNegocio.next();
				
				temaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_TEMA, temaVO.getIdTema());
				temaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_NOMBRE_TEMA, temaVO.getTema());
				temaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ORDEN_TEMA, temaVO.getOrden());
				temaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_LISTA_PREGUNTAS, obtenerPreguntasXML(temaVO.getPreguntas(), idsRespuestasSelec));
				
				xmlTemas.add(temaObject);
			}
			
			xmlMatrizNeg.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_LISTA_TEMAS, xmlTemas);	
		}
		
		return xmlMatrizNeg;
	}
	
	/*
	 * Crea la instancia de XMLDataList con las instancias de XMLDataObject para cada pregunta pasada como parametro.
	 *
	 */
	private XMLDataList obtenerPreguntasXML(List preguntas, List idsRespuestasSelec) {
		XMLDataList xmlPreguntas = null;
		
		if (preguntas != null) {
			xmlPreguntas = new XMLDataList();
			
			Iterator preguntasNegocioIt = preguntas.iterator();			
			
			// Se obtienen las preguntas del tema que se esta recorriendo
			while(preguntasNegocioIt.hasNext()) {
				XMLDataObject preguntaObject = new XMLDataObject();
				PreguntaNegocio preguntaVO 	 = (PreguntaNegocio) preguntasNegocioIt.next();
				
				preguntaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID, preguntaVO.getIdPregunta());
				preguntaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_NOMBRE_PREGUNTA, preguntaVO.getPregunta());
				preguntaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ORDEN_PREGUNTA, preguntaVO.getOrden());	
				preguntaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_LISTA_ALTERNATIVAS, obtenerAlternativasXML(preguntaVO.getAlternativas(), idsRespuestasSelec));
				
				xmlPreguntas.add(preguntaObject);					
			}
		}
		
		return xmlPreguntas;
	}

	/*
	 * Crea la instancia de XMLDataList con las instancias de XMLDataObject para las alternativas pasadas como parametro.
	 * Ademas este metodo utiliza la lista idsRespuestasSelec para identificar si alguna respuesta se encuentra seleccionada.
	 */
	private XMLDataList obtenerAlternativasXML(List lstAlternativas, List idsRespuestasSelec) {
		XMLDataList xmlAlternativas = null;
		
		if (lstAlternativas != null) {
			xmlAlternativas = new XMLDataList();
			
			Iterator itAlternativas = lstAlternativas.iterator();
			
			// Se obtienen las alternativas de la pregunta que se esta recorriendo
			while (itAlternativas.hasNext()) {
				XMLDataObject alternativaObject  = new XMLDataObject();
				AlternativaNegocio alternativaVO = (AlternativaNegocio) itAlternativas.next();
				
				alternativaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_ALTERNATIVA, alternativaVO.getIdAlternativa());
				alternativaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_NOMBRE_ALTERNATIVA, alternativaVO.getAlternativa());
				alternativaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ORDEN_ALTERNATIVA, alternativaVO.getOrden());
				alternativaObject.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_OPC_SELECCIONADA, Boolean.valueOf(idsRespuestasSelec.contains(alternativaVO.getIdAlternativa())));
				
				xmlAlternativas.add(alternativaObject);
			}
		}		
		
		return xmlAlternativas;
	}
	
	private List obtenerIdsRespuestasSeleccionadas(List respuestas) {
		List idsRespuestas = new ArrayList();
		
		if (respuestas != null) {
			Iterator itRespuestas = respuestas.iterator();
			
			while (itRespuestas.hasNext()) {
				RespuestaNegocio resp = (RespuestaNegocio) itRespuestas.next();
				
				idsRespuestas.add(resp.getIdRespuesta());
			}
		}
		
		return idsRespuestas;
	}
}