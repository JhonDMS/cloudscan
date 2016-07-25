package com.bch.sefe.rating;

import java.util.Map;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.util.XMLData;
import com.bch.sefe.comun.util.XMLDataList;
import com.bch.sefe.comun.util.XMLDataObject;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.rating.dao.RatingIndividualDAO;
import com.bch.sefe.rating.dao.impl.RatingIndividualDAOImpl;
import com.bch.sefe.rating.impl.ServicioRatingComportamientoImpl;
import com.bch.sefe.rating.vo.RatingComportamiento;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.util.FormatUtil;

public class AdaptadorRatingComportamiento {
	private ServicioRatingComportamiento servicioNegocio = new ServicioRatingComportamientoImpl();
	
	/**
	 * Obtiene los parametros de entrada requeridos para poder obtener el rating de comportamiento.
	 * 
	 * @param request
	 *            - XMLDataObject con la siguiente estructura:<br>
	 *            <p>
	 *            <b>XMLDataObject</b><br>
	 *            -->idRtgComp<br>
	 *            -->idRtg<br>
	 *            -->rutCte<br>
	 *            -->idBca<br>
	 *            </p>
	 * @return - XMLDataObject con la siguiente estructura:<br>
	 *         <p>
	 *         <b>XMLDataObject</b> -->cliente<br>
	 *         -->id_rat_ind<br>
	 *         -->id_banca<br>
	 *         -->idRtgComp<br>
	 *         --><b>XMLDataObject</b> ---->encabFecha<br>
	 *         ---->encabDeudaBanco<br>
	 *         ---->encabDeudaSBIF<br>
	 *         ---->encabPuntaje<br>
	 *         ---->encabCirculo<br>
	 *         ---->encabNotaRTG<br>
	 *         ---->encabDescripcion<br>
	 *         </p>
	 */
	public XMLData consultarRating(XMLData request) {
		XMLDataObject xmlRequest = (XMLDataObject) request;
		
		Long idRatingComportamiento = xmlRequest.getLong(ConstantesRating.ID_RATING_COMPORT);
		Long idRatingIndividual = xmlRequest.getLong(ConstantesRating.ID_RATING);
		String rutCliente = xmlRequest.getString(ConstantesRating.RUT_CLIENTE);
		Long idBanca = xmlRequest.getLong(ConstantesRating.ID_BANCA);
		String loginUsuario = xmlRequest.getString(ConstantesRating.LOG_OPERADOR);
		
		Map map = servicioNegocio.consultarRating(rutCliente, idRatingIndividual, idRatingComportamiento, loginUsuario);
		
		RatingComportamiento ratingComportamiento = (RatingComportamiento)map.get(ConstantesRating.KEY_RTG_COMP);
		
		ServicioClientes srvClientes = new ServicioClientesImpl();
		
		Cliente cliente = srvClientes.obtenerClientePorRut(rutCliente);
		RatingIndividualDAO ratingIndDao = new RatingIndividualDAOImpl();
		RatingIndividual ratingIndividual = ratingIndDao.buscarRatingIndividual(Long.valueOf(cliente.getClienteId()), idRatingIndividual);
		
		if(!ratingComportamiento.getWarnings().isEmpty() && ratingIndividual.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO))
		{
			ratingIndividual.setRatingFinal(null);
			ratingIndividual = ratingIndDao.actualizarRatingIndividual(ratingIndividual);
		}
		
		return crearRespuestaRatingComportamiento(rutCliente, idRatingIndividual, idBanca, map);
	}
	
	/*
	 * Genera el XMLData con la estructura necesaria para pintar en la vista los datos del rating de comportamiento.
	 */
	private XMLData crearRespuestaRatingComportamiento(String rutCliente, Long idRtgInd, Long idBanca, Map map) {
		RatingComportamiento ratingComportamiento = (RatingComportamiento)map.get(ConstantesRating.KEY_RTG_COMP);
		XMLDataObject response = new XMLDataObject();
		response.put(ConstantesSEFE.CONSUL_RTG_KEY_CLIENTE, rutCliente);
		response.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_RTG_IND, idRtgInd);
		response.put(ConstantesRating.ID_RATING_COMPORT, ratingComportamiento.getIdRating());
		response.put(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_BANCA, idBanca);
		response.put(ConstantesRating.KEY_RTG_INDIV_ESTADO, map.get(ConstantesRating.KEY_RTG_INDIV_ESTADO));
		response.put(ConstantesRating.KEY_RTG_COMP_ES_VIGENTE, ratingComportamiento.esVigente());
		response.put(ConstantesRating.KEY_RTG_COMP_MENSAJE, MessageManager.getError(ConstantesSEFE.KEY_RTG_COMPORT_ERROR_VIGENCIA_EVALUACION_CARITAS));

		XMLDataObject respEnc = new XMLDataObject();
		respEnc.put(ConstantesSEFE.KEY_RTG_COMP_ENCAB_FECHA, (ratingComportamiento.getFechaPuntajeSiebel() != null) ? FormatUtil.formatDate(ratingComportamiento.getFechaPuntajeSiebel()) : "");
		respEnc.put(ConstantesSEFE.KEY_RTG_COMP_ENCAB_DEUDA_BANCO, ratingComportamiento.getDeudaBanco());
		respEnc.put(ConstantesSEFE.KEY_RTG_COMP_ENCAB_DEUDA_SBIF, ratingComportamiento.getDeudaSBIF());
		respEnc.put(ConstantesSEFE.KEY_RTG_COMP_ENCAB_PUNTAJE, ratingComportamiento.getPuntajeSiebel());
		respEnc.put(ConstantesSEFE.KEY_RTG_COMP_ENCAB_CIRCULO, obtenerColorPuntaje(ratingComportamiento.getPuntajeSiebel()));
		respEnc.put(ConstantesSEFE.KEY_RTG_COMP_ENCAB_NOTA_RTG, ratingComportamiento.getRatingComportamiento());
		respEnc.put(ConstantesSEFE.KEY_RTG_COMP_ENCAB_DESCRIPCION, ratingComportamiento.getDescripcionSiebel());
		
		XMLDataList mensajesList = new XMLDataList();
		for(int i = 0; i < ratingComportamiento.getWarnings().size(); i++){
			XMLDataObject obj = new XMLDataObject();
			obj.put(ConstantesSEFE.RTG_COMP_KEY_MENSAJE,(String)ratingComportamiento.getWarnings().get(i));
			mensajesList.add(obj);
		}
		response.put(ConstantesSEFE.RTG_COMP_KEY_LISTA_MENSAJES, mensajesList);
		
		response.put(ConstantesSEFE.KEY_RTG_COMP_ENCABEZADO, respEnc);

		return response;
	}

	/**
	 * 
	 * @deprecated - Utilizar Servicio de rating de comportamiento
	 */
	private String obtenerColorPuntaje(Double notaSiebel){
		
		if( notaSiebel != null 
				&& notaSiebel.compareTo(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_VERDE_MIN)) >= 0
				&& notaSiebel.compareTo(new Double(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_VERDE_MAX).doubleValue()+1)) < 0 ) {
			
			return ConstantesRating.KEY_RTG_COMP_PJE_VERDE;
			
		} else if( notaSiebel != null
				&& notaSiebel.compareTo(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_AMARILLO_MIN)) >= 0
				&& notaSiebel.compareTo(new Double(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_AMARILLO_MAX).doubleValue()+1)) < 0 ) {
			
			return ConstantesRating.KEY_RTG_COMP_PJE_AMARILLO;
		
		} else if( notaSiebel != null
				&& notaSiebel.compareTo(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_ROJO_MIN)) >= 0 
				&& notaSiebel.compareTo(new Double(ConfigDBManager.getValueAsDouble(ConstantesSEFE.RTG_COMPORTAMIENTO_PJE_ROJO_MAX).doubleValue()+1)) < 0  ){
			
			return ConstantesRating.KEY_RTG_COMP_PJE_ROJO;
			
		}
		
		return null;
		
	}

	/**
	 * Obtiene los parametros de entrada requeridos para poder obtener el rating
	 * de comportamiento.
	 * 
	 * @param request
	 *            - XMLDataObject con la siguiente estructura:<br>
	 *            <p>
	 *            <b>XMLDataObject</b><br>
	 *            -->cliente<br>
	 *            -->id_rat_ind<br>
	 *            -->id_banca<br>
	 *            </p>
	 * @return - XMLDataObject con la siguiente estructura:<br>
	 *         <p>
	 *         <b>XMLDataObject</b> -->cliente<br>
	 *         -->id_rat_ind<br>
	 *         -->id_banca<br>
	 *         -->idRtgComp<br>
	 *         --><b>XMLDataObject</b> ---->encabFecha<br>
	 *         ---->encabDeudaBanco<br>
	 *         ---->encabDeudaSBIF<br>
	 *         ---->encabPuntaje<br>
	 *         ---->encabCirculo<br>
	 *         ---->encabNotaRTG<br>
	 *         ---->encabDescripcion<br>
	 *         </p>
	 * @deprecated Este metodo no es invocado desde el front ya que la creacion
	 *             o solo visualizacion se resuelve en
	 *             {@link ServicioRatingComportamiento#consultarRating(String, Long, Long, String)}
	 *             que es invocado desde
	 *             {@link AdaptadorRatingComportamiento#consultarRating(XMLData)}
	 *             .
	 */
	public XMLData obtenerRatingComportamiento(XMLData request) {
		XMLDataObject xmlDataObject = (XMLDataObject) request;
		String rutCliente = (String) xmlDataObject.getObject(ConstantesSEFE.CONSUL_RTG_KEY_CLIENTE);
		Long idRtgInd = xmlDataObject.getLong(ConstantesSEFE.CONSUL_RTG_KEY_ID_RTG_IND);
		Long idBanca = xmlDataObject.getLong(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_ID_BANCA);
		String loginUsuario = xmlDataObject.getString(ConstantesRating.LOG_OPERADOR);
		Long idRtgComportamiento = xmlDataObject.getLong(ConstantesRating.ID_RATING_COMPORT);
		
		Map map = servicioNegocio.consultarRating(rutCliente, idRtgInd, idRtgComportamiento, loginUsuario);

		return crearRespuestaRatingComportamiento(rutCliente, idRtgInd, idBanca, map);
	}

}
