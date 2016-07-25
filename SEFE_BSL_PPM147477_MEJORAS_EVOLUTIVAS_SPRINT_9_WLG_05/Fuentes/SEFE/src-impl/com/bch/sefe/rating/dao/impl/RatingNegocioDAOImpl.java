package com.bch.sefe.rating.dao.impl;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.dao.ParametrizadorIbatis;
import com.bch.sefe.comun.impl.SQLMapper;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.rating.dao.RatingNegocioDAO;
import com.bch.sefe.rating.vo.AlternativaNegocio;
import com.bch.sefe.rating.vo.MatrizNegocio;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.rating.vo.RespuestaNegocio;

public class RatingNegocioDAOImpl implements RatingNegocioDAO {
	
	final static Integer RETORNO_OK = new Integer(0);
	final static Logger log = Logger.getLogger(RatingNegocioDAOImpl.class);

	private static final String PARAM_ID_CLIENTE     = "idCliente";
	private static final String PARAM_ID_RATING      = "idRating";
	private static final String PARAM_ID_MATRIZ      = "idMatriz";
	private static final String PARAM_ID_TEMA        = "idTema";
	private static final String PARAM_ID_PREGUNTA    = "idPregunta";
	private static final Object PARAM_ID_ALTERNATIVA = "idAlternativa";
	
	private static final Integer ZERO = Integer.valueOf("0");	

	public Collection buscarRatingPorCliente(String rutCliente) {
		ArrayList rtgNeg = new ArrayList();
		ParametrizadorIbatis param = new ParametrizadorIbatis(rutCliente);
		rtgNeg = (ArrayList)SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_RATING_NEGOCIO_POR_CLIENTE, param);
		
		return rtgNeg;
	}

	public MatrizNegocio buscarMatrizNegocio(Integer idBanca, Integer idSegmento) {
		ArrayList lstMtrNeg 	= new ArrayList();
		MatrizNegocio matrizNeg = null;
		HashMap objetosEntrada 	= new HashMap();
		
		objetosEntrada.put("idSegmento", idSegmento);
		objetosEntrada.put("idBanca", idBanca);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);
		
		lstMtrNeg = (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_MATRIZ_NEGOCIO, parametros);
		
		if(!lstMtrNeg.isEmpty())
		{
			matrizNeg =(MatrizNegocio) lstMtrNeg.get(0);
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametros.getObjSalida().get("mensaje"));
			}
		}
		else
		{	
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametros.getObjSalida().get("mensaje"));
			}
		}
		
		return matrizNeg;
	}

	public RatingNegocio buscarRatingVigente(String rut, Integer idBanca, Integer diasEvaluacion) {
		RatingNegocio rtgNegocio = null;
		ArrayList lstNegocio     = new ArrayList();
		HashMap objetosEntrada   = new HashMap();
		
		objetosEntrada.put("rut", rut);
		objetosEntrada.put("idBanca", idBanca);
		objetosEntrada.put("diasEval", diasEvaluacion);
		
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);
		
		lstNegocio = (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_RATING_NEGOCIO_VIGENTE, parametros);
		if(!lstNegocio.isEmpty())
		{
			rtgNegocio = (RatingNegocio) lstNegocio.get(0);
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametros.getObjSalida().get("mensaje"));
			}
		}
		else
		{	
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametros.getObjSalida().get("mensaje"));
			}
		}
		return rtgNegocio;
	}
	
	public Collection buscarUltimosRating(String rut, Integer idBanca) {
		ArrayList lstNegocio     = new ArrayList();
		HashMap objetosEntrada   = new HashMap();
		Integer diasEvaluacion=ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_NEGOCIO_DIAS_MAX_EVALUACION,idBanca);
		
		objetosEntrada.put("rut", rut);
		objetosEntrada.put("idBanca", idBanca);
		objetosEntrada.put("diasEval", diasEvaluacion);
		
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);
		
		lstNegocio = (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_RATING_NEGOCIO_VIGENTE, parametros);
		return lstNegocio;
	}

	public RatingNegocio guardarRating(RatingNegocio rating) {
		RatingNegocio rtgNegocio = new RatingNegocio();
		
		HashMap objetosEntrada = new HashMap();
		objetosEntrada.put("idCliente", rating.getIdCliente());
		objetosEntrada.put("estado", rating.getEstado());
		objetosEntrada.put("idMatriz", rating.getIdMatriz());
		objetosEntrada.put("idUsuario", rating.getIdUsuario());
		objetosEntrada.put("notaNegocio", rating.getNotaNegocio());
		objetosEntrada.put("notaNegocioSinTope", rating.getNotaNegocioSinTope());
		if(rating.getFechaEvaluacion() != null)
		{
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	        String date = df.format(rating.getFechaEvaluacion());
	        Long fechaNumerica = Long.valueOf(date);
			objetosEntrada.put("fechaEvaluacion", fechaNumerica);
		}
		else
		{
			objetosEntrada.put("fechaEvaluacion", new Long(0));
		}
		ParametrizadorIbatis parametro = new ParametrizadorIbatis(objetosEntrada);
		
		rtgNegocio = (RatingNegocio) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_INSERTAR_RATING_NEGOCIO, parametro);
		
		if(rtgNegocio != null)
		{
			
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametro.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametro.getObjSalida().get("mensaje"));
			}
		}
		else
		{	
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametro.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametro.getObjSalida().get("mensaje"));
			}
		}
		return rtgNegocio;
		
	}
	
	public Collection buscarTemaNegocio(Long idMatriz, Integer idEstado, Date fechaRating) {
		
		Collection lstTema = new ArrayList();
		HashMap objetosEntrada = new HashMap();
		objetosEntrada.put("idMatriz", idMatriz);
		objetosEntrada.put("idEstado", idEstado);
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(fechaRating);
        Long fechaNumerica = Long.valueOf(date);
		objetosEntrada.put("fechaRating", fechaNumerica);
		ParametrizadorIbatis parametro = new ParametrizadorIbatis(objetosEntrada);
		lstTema =   (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_TEMA_NEGOCIO, parametro);
		
		if(!lstTema.isEmpty())
		{
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametro.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametro.getObjSalida().get("mensaje"));
			}
		}
		else
		{	
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametro.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametro.getObjSalida().get("mensaje"));
			}
		}
		
		return lstTema;
	}
	
	public Collection buscarPreguntaNegocio(Long idMatriz, Long idTema, Integer idEstado, Date fechaRating ) {
		
		Collection lstPregunta = new ArrayList();
		HashMap objetosEntrada = new HashMap();
		objetosEntrada.put("idMatriz", idMatriz);
		objetosEntrada.put("idTema", idTema);
		objetosEntrada.put("idEstado", idEstado);
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(fechaRating);
        Long fechaNumerica = Long.valueOf(date);
		objetosEntrada.put("fechaRating", fechaNumerica);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);
		
		lstPregunta =   (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_PREGUNTA_NEGOCIO, parametros);
		
		if(!lstPregunta.isEmpty())
		{
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametros.getObjSalida().get("mensaje"));
			}
		}
		else
		{	
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametros.getObjSalida().get("mensaje"));
			}
		}
		
		return lstPregunta;
	}
	
	public Collection buscarAlternativaNegocio(Long idMatriz, Long idTema, Long idPregunta, Integer idEstado, Date fechaRating) {
		
		Collection lstAlternativa = new ArrayList();
		HashMap objetosEntrada = new HashMap();
		objetosEntrada.put("idMatriz", idMatriz);
		objetosEntrada.put("idTema", idTema);
		objetosEntrada.put("idPregunta", idPregunta);
		objetosEntrada.put("idEstado", idEstado);
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(fechaRating);
        Long fechaNumerica = Long.valueOf(date);
		objetosEntrada.put("fechaRating", fechaNumerica);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);
		
		lstAlternativa =   (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_ALTERNATIVA_NEGOCIO, parametros);
		
		if(!lstAlternativa.isEmpty())
		{
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametros.getObjSalida().get("mensaje"));
			}
		}
		else
		{	
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametros.getObjSalida().get("mensaje"));
			}
		}
		
		return lstAlternativa;
		
	}
	
	public Collection buscarAlternativaNegocioVigHis(Long idMatriz, Long idTema, Long idPregunta) {
		
		Collection lstAlternativa = new ArrayList();
		HashMap objetosEntrada = new HashMap();
		objetosEntrada.put("idMatriz", idMatriz);
		objetosEntrada.put("idTema", idTema);
		objetosEntrada.put("idPregunta", idPregunta);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);
		
		lstAlternativa = (ArrayList)SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_ALTERNATIVA_X_PREG, parametros);
		
		if(!lstAlternativa.isEmpty())
		{
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametros.getObjSalida().get("mensaje"));
			}
		}
		else
		{	
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametros.getObjSalida().get("mensaje"));
			}
		}
		
		return lstAlternativa;
		
	}

	public RatingNegocio buscarRatingNegocioPorId(Long idRating) {
		RatingNegocio rtgNeg = null;
		ParametrizadorIbatis parametro = new ParametrizadorIbatis(idRating);

		rtgNeg = (RatingNegocio) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_BUSCAR_RATING_NEGOCIO_POR_ID, parametro);

		if (log.isDebugEnabled()) {
			log.debug("Codigo: #" + parametro.getObjSalida().get("codigo"));
			log.debug("Mensaje: " + parametro.getObjSalida().get("mensaje"));
		}

		return rtgNeg;
	}

	public MatrizNegocio buscarMatrizNegocioPorId(Long idMatriz) {
		MatrizNegocio matrizNeg = null;
		ParametrizadorIbatis parametro = new ParametrizadorIbatis(idMatriz);
		
		matrizNeg = (MatrizNegocio) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_BUSCAR_MATRIZ_NEGOCIO_POR_ID, parametro);
		
		if(matrizNeg != null)
		{
			
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametro.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametro.getObjSalida().get("mensaje"));
			}
		}
		else
		{	
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #"+parametro.getObjSalida().get("codigo"));
				log.debug("Mensaje: "+parametro.getObjSalida().get("mensaje"));
			}
		}
		
		return matrizNeg;
	}

	public RatingNegocio buscarVigentePorClienteBanca(Long idCliente, Integer idBanca) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		map.put(ConstantesSEFE.PARAM_ID_BANCA, idBanca);
		
		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingNegocio rating = (RatingNegocio) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_NEGOCIO_VIGENTE_POR_CLIENTE_BANCA, param);
		
		return rating;
	}
	
	public List buscarRespuestasRatingNegocio(Long idRatingNegocio) {
		List listaRatings = null;
		
		if (log.isInfoEnabled()) {
			log.info(MessageFormat.format("Buscando las respuestas de rating de negocio para Rut del Cliente [{0}] ", new Object[] { idRatingNegocio }));
		}
	
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(idRatingNegocio);
		
		listaRatings = (List) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_RATING_OBTENER_RESPUESTAS_RATING_NEGOCIO, paramIbatis);
		
		if (log.isInfoEnabled()) {
			if (listaRatings != null)
				log.info("Se encontraron " + listaRatings.size() + " respuestas");
		}
		
		return listaRatings;
	}
	
	public void grabarRespuesta(RespuestaNegocio respuesta) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(respuesta);
		
		SQLMapper.getInstance().insert(ConstantesSEFE.SP_RATING_GRABAR_RESPUESTA_RATING_NEGOCIO, paramIbatis);
	}
	
	public Integer borrarRespuestas(Long idCliente, Long idRatingNegocio) {
		HashMap parametros = new HashMap();
		parametros.put(PARAM_ID_CLIENTE, idCliente);
		parametros.put(PARAM_ID_RATING, idRatingNegocio);

		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(parametros);

		SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RATING_BORRAR_RESPUESTAS_RATING_NEGOCIO, paramIbatis);
		
		Integer regEliminados = (Integer) paramIbatis.getObjSalida().get("registrosEliminados");
		
		return (regEliminados == null ? ZERO : regEliminados);
	}

	public RatingNegocio actualizarRating(RatingNegocio rating) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(rating);
		
		// TODO agregar log a nivel de debug
		if (log.isDebugEnabled()) {
			log.debug("");
		}
		
		RatingNegocio rtg = (RatingNegocio) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_ACTUALIZAR_RATING_NEGOCIO, paramIbatis);
		
		// TODO agregar log a nivel de debug
		if (log.isDebugEnabled()) {
			
		}
		
		return rtg;
	}
	
	public RatingNegocio actualizarEstadoRating(RatingNegocio rating) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(rating);
		
		// TODO agregar log a nivel de debug
		if (log.isDebugEnabled()) {
			log.debug("");
		}
		
		RatingNegocio rtg = (RatingNegocio) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_ACTUALIZAR_ESTADO_RATING_NEGOCIO, paramIbatis);
		
		// TODO agregar log a nivel de debug
		if (log.isDebugEnabled()) {
			
		}
		
		return rtg;
	}

	public AlternativaNegocio buscarAlternativa(Long idAlternativa, Long idMatriz, Long idTema, Long idPregunta) {
		Map parametros = new HashMap();
		parametros.put(PARAM_ID_ALTERNATIVA, idAlternativa);
		parametros.put(PARAM_ID_MATRIZ, idMatriz);
		parametros.put(PARAM_ID_TEMA, idTema);
		parametros.put(PARAM_ID_PREGUNTA, idPregunta);
		
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(parametros);
		
		AlternativaNegocio alternativa = (AlternativaNegocio) SQLMapper.getInstance().queryForObject(/*ConstantesSEFE.SP_BUSCAR_ALTERNATIVA_NEGOCIO_POR_PK*/"Rating.sp_buscarAlternativaNegocioPk", paramIbatis);
		
		return alternativa;
	}
}
