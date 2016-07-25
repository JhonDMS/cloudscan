package com.bch.sefe.rating.dao.impl;

import java.text.MessageFormat;
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
import com.bch.sefe.rating.dao.RatingIndividualDAO;
import com.bch.sefe.rating.vo.RatingComportamiento;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingGrupal;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;

public class RatingIndividualDAOImpl implements RatingIndividualDAO {

	final static Logger log = Logger.getLogger(RatingIndividualDAOImpl.class);

	public Collection buscarRatingPorCliente(String rutCliente) {
		ArrayList rtgInd = new ArrayList();
		ParametrizadorIbatis param = new ParametrizadorIbatis(rutCliente);
		rtgInd = (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_RATING_INDIVIDUAL_POR_CLIENTE, param);

		return rtgInd;
	}

	public RatingIndividual buscarRatingIndividual(String rut, Long idRatIndividual) {
		RatingIndividual rtgInd = null;
		HashMap objetosEntrada = new HashMap();

		objetosEntrada.put("rut", rut);
		objetosEntrada.put("idRatatingIndividual", idRatIndividual);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);
		rtgInd = (RatingIndividual) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_OBTENER_INDIVIDUAL_INFOMACION_Y_DETALLE, parametros);

		return rtgInd;
	}

	public Map buscarRatingAnterior(String rut, Long idRatingIndividual) {
		HashMap objetosEntrada = new HashMap();
		Map objetosSalida = new HashMap();

		objetosEntrada.put("rut", rut);
		objetosEntrada.put("idRatingIndividual", idRatingIndividual);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);

		SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_OBTENER_RATING_ANTERIOR, parametros);

		objetosSalida.put("idRatingAnterior", parametros.getObjSalida().get("ID_RATING_ANTERIOR"));
		objetosSalida.put("idTipoRating", parametros.getObjSalida().get("ID_TIPO_RATING"));

		return objetosSalida;
	}

	public Collection buscarFortalezasDebilidades(String rut, Long idRatingIndividual) {
		Collection fortDeb = new ArrayList();
		Map objetosEntrada = new HashMap();

		objetosEntrada.put("rut", rut);
		objetosEntrada.put("idRatingIndividual", idRatingIndividual);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);

		fortDeb = (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_OBTENER_FORTALEZA_DEBILIDADES, parametros);

		if (!fortDeb.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
			}
		}

		return fortDeb;
	}

	public Collection buscarRelacionadosPyme(String rut, Long idRatingIndividual, Long version) {

		HashMap objetosEntrada = new HashMap();
		Collection relPyme = new ArrayList();

		objetosEntrada.put("rut", rut);
		objetosEntrada.put("idRatingIndividual", idRatingIndividual);
		objetosEntrada.put("version", version);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);

		relPyme = (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_RELACIONADO_RPT_PYME, parametros);

		if (!relPyme.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
			}
		}

		return relPyme;
	}

	public Collection buscarRelacionadosNoPyme(String rut, Long idRatingIndividual) {

		HashMap objetosEntrada = new HashMap();
		Collection relPyme = new ArrayList();

		objetosEntrada.put("rut", rut);
		objetosEntrada.put("idRatingIndividual", idRatingIndividual);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);

		relPyme = (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_RELACIONADO_RPT_NO_PYME, parametros);

		if (!relPyme.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
			}
		}

		return relPyme;
	}

	public Collection buscarRelacionadosGGEE(String rut, Long idRatingIndividual) {

		HashMap objetosEntrada = new HashMap();
		Collection relGGEE = new ArrayList();

		objetosEntrada.put("rut", rut);
		objetosEntrada.put("idRatingIndividual", idRatingIndividual);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(objetosEntrada);

		relGGEE = (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_RELACIONADO_GGEE, parametros);

		if (!relGGEE.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
				log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
			}
		}

		return null;
	}

	public void actualizarRatingNegocio(Long idRatingIndividual, RatingNegocio ratingNegocio) {
		HashMap paramEntrada = new HashMap();
		paramEntrada.put("idCliente", ratingNegocio.getIdCliente());
		paramEntrada.put("idRating", ratingNegocio.getIdRating());
		paramEntrada.put("fechaEvaluacion", ratingNegocio.getFechaEvaluacion());
		paramEntrada.put("notaNegocio", ratingNegocio.getNotaNegocio());
		paramEntrada.put("idRatingIndividual", idRatingIndividual);

		ParametrizadorIbatis parametros = new ParametrizadorIbatis(paramEntrada);

		SQLMapper.getInstance().update(ConstantesSEFE.SP_ACTUALIZAR_RATING_CON_RATING_NEGOCIO, parametros);

		if (log.isDebugEnabled()) {
			log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
			log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
		}
	}

	public RatingIndividual actualizarRatingComportamiento(Long idRatingInd, RatingComportamiento rating) {
		HashMap paramEntrada = new HashMap();
		paramEntrada.put("idRating", rating.getIdRating());
		//Requerimiento 7.4.7 - Fecha de comportamiento -  Se obtiene la fecha siebel
		paramEntrada.put("fechaEvaluacion", rating.getFechaPuntajeSiebel());
		paramEntrada.put("ratingComportamiento", rating.getRatingComportamiento());
		paramEntrada.put("porcentajeAjusteComportamiento", rating.getPorcentajeAjusteComportamiento());
		paramEntrada.put("idRatingInd", idRatingInd);

		ParametrizadorIbatis parametros = new ParametrizadorIbatis(paramEntrada);

		RatingIndividual rtgInd = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_ACTUALIZAR_RTG_COMPORTAMIENTO, parametros);

		return rtgInd;
	}
	
	public RatingIndividual actualizarRatingIndividual(RatingIndividual rating) {
		HashMap paramEntrada = new HashMap();
		paramEntrada.put(ConstantesSEFE.PARAM_ID_CLIENTE, rating.getIdCliente());
		paramEntrada.put(ConstantesSEFE.PARAM_ID_RATING, rating.getIdRating());
		paramEntrada.put(ConstantesSEFE.PARAM_FECHA_MODIFICACION, rating.getFechaModificacion());
		paramEntrada.put(ConstantesSEFE.PARAM_RATING_FINAL_SUGERIDO, rating.getRatingFinalSugerido());
		paramEntrada.put(ConstantesSEFE.PARAM_RATING_APROX_FINAL, rating.getRatingAproxFinal());
		paramEntrada.put(ConstantesSEFE.PARAM_RATING_FINAL, rating.getRatingFinal());
		paramEntrada.put(ConstantesSEFE.PARAM_PREMIO_TAMANO, rating.getPremioTamano());
		paramEntrada.put(ConstantesSEFE.PARAM_RATING_PRELIMINAR_1, rating.getRatingPreliminar1());
		paramEntrada.put(ConstantesSEFE.PARAM_RATING_PRELIMINAR_2, rating.getRatingPreliminar2());
		paramEntrada.put(ConstantesSEFE.PARAM_MONTO_VENTA, rating.getMontoVenta());
		paramEntrada.put(ConstantesSEFE.PARAM_MONTO_PATRIMONIO, rating.getMontoPatrimonio());
		paramEntrada.put(ConstantesSEFE.PARAM_POND_FINANCIERO, rating.getPrcRatingFinanciero());
		paramEntrada.put(ConstantesSEFE.PARAM_POND_PROYECTADO, rating.getPrcRatingProyectado());
		paramEntrada.put(ConstantesSEFE.PARAM_POND_COMPORTAMIENTO, rating.getPrcRatingComportamiento());
		paramEntrada.put(ConstantesSEFE.PARAM_POND_NEGOCIO, rating.getPrcRatingNegocio());
			
		
		if (log.isDebugEnabled())
		{
			log.debug("actualizarRatingIndividual="+paramEntrada);
		}
		
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(paramEntrada);

		RatingIndividual rtgInd = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_ACTUALIZAR_RTG_INDIVIDUAL, parametros);

		return rtgInd;
	
	}

	public RatingIndividual actualizarRatingFinanciero(Long idCliente, Long idRatingInd, RatingFinanciero rating) {
		Map paramEntrada = new HashMap();
		paramEntrada.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		paramEntrada.put(ConstantesSEFE.PARAM_ID_RATING, idRatingInd);
		paramEntrada.put(ConstantesSEFE.PARAM_RATING_FINANCIERO, rating.getNotaFinanciera());
		paramEntrada.put(ConstantesSEFE.PARAM_FECHA_RATING_FINANCIERO, rating.getFechaRating());
		paramEntrada.put(ConstantesSEFE.PARAM_ID_RATING_FINANCIERO, rating.getIdRating());
		paramEntrada.put(ConstantesSEFE.PARAM_PERIODO_VACIADO_0, rating.getFechaBalance());
		paramEntrada.put(ConstantesSEFE.PARAM_MONTO_DEUDA_BANCO, rating.getDeudaBanco());
		paramEntrada.put(ConstantesSEFE.PARAM_MONTO_DEUDA_SBIF, rating.getDeudaSBIF());
		paramEntrada.put(ConstantesSEFE.PARAM_MONTO_DEUDA_SIN_HIP_BANCO, rating.getDeudaSinHipotecarioBanco());
		paramEntrada.put(ConstantesSEFE.PARAM_MONTO_DEUDA_SIN_HIP_SBIF, rating.getDeudaSinHipotecarioSBIF());
		paramEntrada.put(ConstantesSEFE.PARAM_MONTO_DEUDA_ACHEL, rating.getDeudaACHEL());
		paramEntrada.put(ConstantesSEFE.PARAM_PERIODO_VACIADO_1, rating.getFechaBalance1());
		paramEntrada.put(ConstantesSEFE.PARAM_PERIODO_VACIADO_2, rating.getFechaBalance2());
		paramEntrada.put(ConstantesSEFE.PARAM_NIVEL_DE_VENTAS, rating.getNivelVentas());
		paramEntrada.put(ConstantesSEFE.PARAM_MONTO_DE_ACTIVOS, rating.getMontoActivos());
		paramEntrada.put(ConstantesSEFE.PARAM_MONTO_VENTA, rating.getMontoVentaMMUF());
		paramEntrada.put(ConstantesSEFE.PARAM_MONTO_PATRIMONIO, rating.getMontoPatrimonioMMUF());
		

		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(paramEntrada);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Actualizando rating individual para el cliente [{0}] e id de rating individual [{1}] con los siguientes parametros: ", new Object[] { idCliente, idRatingInd } ));
			log.debug("Parametros: " + paramEntrada);
		}
		
		RatingIndividual rtgIndividual = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_ACTUALIZAR_RTG_INDIVIDUAL_CON_FINANCIERO, paramIbatis);
		
		if (log.isDebugEnabled()) {
			if (rtgIndividual != null) {
				log.debug(MessageFormat.format("Se ha actualizado el rating individual [{0}] con informacion del rating financiero [{1}]", new Object[] { rtgIndividual.getIdRating(), rtgIndividual.getIdRatingFinanciero() } ));
			} else {
				log.debug("Ha ocurrido un error ya que no se ha obtenido el rating individual actualizado");
			}
		}
		
		return rtgIndividual;
	}
	
	public RatingIndividual desasociarRatingFinanciero(Long idCliente, Long idRatingInd) {
		Map paramEntrada = new HashMap();
		paramEntrada.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		paramEntrada.put(ConstantesSEFE.PARAM_ID_RATING, idRatingInd);
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(paramEntrada);

		RatingIndividual rtgIndividual = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTGIND_DESASOC_FINAN, paramIbatis);

		return rtgIndividual;
	}
	
	public RatingIndividual borrarPonderaciones(Long idCliente, Long idRatingInd) {
		Map paramEntrada = new HashMap();
		paramEntrada.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		paramEntrada.put(ConstantesSEFE.PARAM_ID_RATING, idRatingInd);
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(paramEntrada);

		RatingIndividual rtgIndividual = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_ELIM_PONDERACIONES, paramIbatis);

		return rtgIndividual;
	}
	
	public RatingIndividual desasociarRatingNegocio(Long idCliente, Long idRatingInd) {
		Map paramEntrada = new HashMap();
		paramEntrada.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		paramEntrada.put(ConstantesSEFE.PARAM_ID_RATING, idRatingInd);
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(paramEntrada);

		RatingIndividual rtgIndividual = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTGIND_DESASOC_NEG, paramIbatis);

		return rtgIndividual;
	}
	
	public RatingIndividual desasociarRatingProyectado(Long idCliente, Long idRatingInd) {
		Map paramEntrada = new HashMap();
		paramEntrada.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		paramEntrada.put(ConstantesSEFE.PARAM_ID_RATING, idRatingInd);
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(paramEntrada);

		RatingIndividual rtgIndividual = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTGIND_DESASOC_PROY, paramIbatis);

		return rtgIndividual;
	}

	public RatingIndividual actualizarRatingProyectado(Long idCte, Long idRatingInd, RatingFinanciero rating) {
		Map map = new HashMap();		
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(map);
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCte);
		map.put(ConstantesSEFE.PARAM_ID_RATING, idRatingInd);
		map.put(ConstantesSEFE.PARAM_FECHA_RATING_FINANCIERO, rating.getFechaRating());
		map.put(ConstantesSEFE.PARAM_NOTA_RATING, rating.getNotaFinanciera());
		map.put(ConstantesSEFE.PARAM_ID_USUARIO, rating.getIdUsuario());
		map.put(ConstantesSEFE.PARAM_ID_ESTADO, rating.getEstado());
		map.put(ConstantesSEFE.PARAM_ID_PROYECTADO, rating.getIdProyectado());
		
		log.info("Actualizando rating individual con " + map);
		
		RatingIndividual rtgIndividual = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_ACTUALIZAR_RTG_INDIVIDUAL_CON_PROYECTADO, paramIbatis);
		
		if (log.isDebugEnabled()) {
			if (rtgIndividual != null) {
				log.debug(MessageFormat.format("Se ha actualizado el rating individual [{0}] con informacion del rating proyectado [{1}]", new Object[] { rtgIndividual.getIdRating(), rtgIndividual.getIdRatingProyectado() } ));
			} else {
				log.debug("Ha ocurrido un error ya que no se ha obtenido el rating individual actualizado");
			}
		}
		
		return rtgIndividual;
	}
	

	public RatingIndividual buscarRatingVigente(Long idCliente) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		map.put(ConstantesSEFE.PARAM_ID_BANCA, null);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingIndividual rating = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_INDIVIDUAL_VIGENTE, param);

		return rating;
	}

	public int cambiarEstado(RatingIndividual rating) {
		if (log.isDebugEnabled()) {
			log.debug("Materializando cambio estado rating individual...");
		}

		ParametrizadorIbatis parametros = new ParametrizadorIbatis(rating);

		int filasActualizadas = SQLMapper.getInstance().update(ConstantesSEFE.SP_RTG_IND_CAMBIAR_ESTADO, parametros);

		if (filasActualizadas > 0) {
			if (log.isDebugEnabled()) {
				log.debug("Cambio de estado realizado!");
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("No se pudo cambiar estado a rating individual #" + rating.getIdRating());
			}
		}

		return filasActualizadas;
	}

	public RatingIndividual buscarRatingIndividual(Long idCliente, Long idRatIndividual) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		map.put(ConstantesSEFE.PARAM_ID_RATING_INDIVIDUAL, idRatIndividual);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingIndividual rating = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_CONSULTA_INDIVIDUAL, param);

		return rating;
	}

	public RatingIndividual crearRating(Long idCliente, Integer idBanca, Long idUsr) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		map.put(ConstantesSEFE.PARAM_ID_BANCA, idBanca);
		map.put(ConstantesSEFE.PARAM_ID_USUARIO, idUsr);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingIndividual rating = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_CREAR_RATING_INDIV, param);

		return rating;
	}

	public RatingIndividual buscarRatingEnCurso(Long idCliente, Integer idBanca) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		map.put(ConstantesSEFE.PARAM_ID_BANCA, idBanca);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingIndividual rating = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_INDIVIDUAL_EN_CURSO, param);

		return rating;
	}

	public RatingIndividual actualizarComentario(Long idCliente, Long idRating, String comentario, Long idUsuario) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		map.put(ConstantesSEFE.PARAM_ID_RATING, idRating);
		map.put(ConstantesSEFE.PARAM_COMENTARIO, comentario);
		map.put(ConstantesSEFE.PARAM_ID_USUARIO, idUsuario);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingIndividual rating = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_ACTUALIZAR_COMENTARIO_RTG_INDIVIDUAL, param);

		return rating;
	}

	public RatingIndividual confirmarRatingIndividualModificado(Long idRating, Long idUsr, Double nota, String comm, Integer idMotivo, String comMotivo, Double deudaBanco, Double deudaSbif) {

		// Long notaRating = nota.

		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_RATING, idRating);
		map.put(ConstantesSEFE.PARAM_ID_USUARIO, idUsr);
		map.put(ConstantesSEFE.PARAM_NOTA_RATING, nota);
		map.put(ConstantesSEFE.PARAM_COMENTARIO, comm);
		map.put(ConstantesSEFE.PARAM_ID_MOTIVO, idMotivo);
		map.put(ConstantesSEFE.PARAM_COMENTARIO_MOTIVO, comMotivo);
		map.put(ConstantesSEFE.PARAM_DEUDA_BANCO, deudaBanco);
		map.put(ConstantesSEFE.PARAM_DEUDA_SBIF, deudaSbif);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingIndividual rating = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_CONFIRMAR_RTG_INDIVIDUAL_MODIFICADO, param);

		return rating;
	}

	public void borrarRtgComportamientoParaRtgIvidualCurso(Long idRating, Long idCliente) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_RATING, idRating);
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);

		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(map);

		SQLMapper.getInstance().update(ConstantesSEFE.SP_RTG_BORRAR_COMP_INDIV, paramIbatis);
	}

	public RatingIndividual actualizarRatingGarante(Long idRatingInd, Long idCte, Long idGte, Double notaGte, Date fecGte) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_RATING, idRatingInd);
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCte);
		map.put(ConstantesSEFE.PARAM_ID_RATING_GTE, idGte);
		map.put(ConstantesSEFE.PARAM_NOTA_RATING, notaGte);
		map.put(ConstantesSEFE.PARAM_FECHA, fecGte);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingIndividual rating = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_ACTUALIZAR_GARANTE_INDIVIDUAL, param);

		return rating;
	}

	public List obtenerRatingsIndividualesNoInformados(boolean estado, Date desde, Date hasta) {
		// SP_LISTAR_RRII_VENC_NOINFO
		HashMap map = new HashMap();
		map.put("fdesde", desde);
		map.put("fhasta", hasta);
		if (estado) {
			map.put("estado", new Integer(4302));
		} else {
			map.put("estado", new Integer(4303));
		}
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(map);
		List result = SQLMapper.getInstance().queryForList("Rating.sp_listarRatingsIndividualesNoInformadosVigentes", parametros);
		if (log.isDebugEnabled()) {
			log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
			log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
		}
		return result;
	}

	public List obtenerRatingsIndividualesInformados(Date fecha) {
		// SP_LISTAR_RRII_VENC_INFO
		HashMap map = new HashMap();
		map.put("fecha", fecha);
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(map);
		List result = SQLMapper.getInstance().queryForList("Rating.sp_listarRatingsIndividualesInformados", parametros);
		if (log.isDebugEnabled()) {
			log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
			log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
		}
		return result;
	}

	public void establecerInformadoSiebel(RatingIndividual ratingIndividual, Date hasta, String logOper) {
		HashMap parametros = new HashMap();
		parametros.put("piid", ratingIndividual.getIdCliente());
		parametros.put("seq", ratingIndividual.getIdRating());
		parametros.put("fecha", hasta);
		ParametrizadorIbatis param = new ParametrizadorIbatis(parametros);
		SQLMapper.getInstance().update("Rating.sp_establecerRatingIndividualInformadoSiebel", param);
		return;
	}

	public List obtenerPartesInvolucradasParaGrupal(RatingGrupal rg) {
		HashMap map = new HashMap();
		map.put("piid", rg.getIdParteInvolucrada());
		map.put("seq", rg.getIdRatingGrupal());
		ParametrizadorIbatis parametros = new ParametrizadorIbatis(map);
		List result = SQLMapper.getInstance().queryForList("Rating.sp_obtenerRutsParaGrupal", parametros);
		if (log.isDebugEnabled()) {
			log.debug("Codigo: #" + parametros.getObjSalida().get("codigo"));
			log.debug("Mensaje: " + parametros.getObjSalida().get("mensaje"));
		}
		return result;
	}

	public int cambiarEstadoHistorico(RatingIndividual rating) {
		if (log.isDebugEnabled()) {
			log.debug("Materializando cambio estado rating individual...");
		}
		Map map = new HashMap();

		map.put("ID_RATING", rating.getIdRating());
		map.put("ID_CLIENTE",rating.getIdCliente());
		map.put("ID_ESTADO", new Integer(4303));

		ParametrizadorIbatis parametros = new ParametrizadorIbatis(map);

		int filasActualizadas = SQLMapper.getInstance().update(ConstantesSEFE.SP_RTG_IND_CAMBIAR_ESTADO, parametros);

		if (filasActualizadas > 0) {
			if (log.isDebugEnabled()) {
				log.debug("Cambio de estado realizado!");
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("No se pudo cambiar estado a rating individual #" + rating.getIdRating());
			}
		}

		return filasActualizadas;
	}

	public RatingIndividual confirmarRatingIndividualModelo(Long idRating,	Long idUsr, Double nota, String comm, Integer idMotivo,
			String comMotivo, Double deudaBanco, Double deudaSbif) {
		
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_RATING, idRating);
		map.put(ConstantesSEFE.PARAM_ID_USUARIO, idUsr);
		map.put(ConstantesSEFE.PARAM_NOTA_RATING, nota);
		map.put(ConstantesSEFE.PARAM_COMENTARIO, comm);
		map.put(ConstantesSEFE.PARAM_ID_MOTIVO, idMotivo);
		map.put(ConstantesSEFE.PARAM_COMENTARIO_MOTIVO, comMotivo);
		map.put(ConstantesSEFE.PARAM_DEUDA_BANCO, deudaBanco);
		map.put(ConstantesSEFE.PARAM_DEUDA_SBIF, deudaSbif);

		
		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingIndividual rating = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_CONFIRMAR_RTG_INDIVIDUAL_MODIFICADO, param);
		if (log.isDebugEnabled()) {
			log.debug("Codigo: #" + param.getObjSalida().get("codigo"));
			log.debug("Mensaje: " + param.getObjSalida().get("mensaje"));
		}
		
		return rating;
	}

	public RatingIndividual buscarRatingIndividualPorFinanciero(Long idCliente, Integer idBanca, Long idRating) {
		HashMap paramEntrada = new HashMap();
		paramEntrada.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		paramEntrada.put(ConstantesSEFE.PARAM_ID_BANCA, idBanca);
		paramEntrada.put(ConstantesSEFE.PARAM_ID_RATING, idRating);

		ParametrizadorIbatis parametros = new ParametrizadorIbatis(paramEntrada);

		RatingIndividual rating = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_BUSCAR_INDIVIDUAL_POR_FINANCIERO, parametros);
		
		return rating;
	}

	public RatingIndividual buscarRatingIndividualPorNegocio(Long idCliente, Long idRtgNegocio) {
		Map paramsEntrada = new HashMap();
		paramsEntrada.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		paramsEntrada.put(ConstantesSEFE.PARAM_ID_RATING_NEGOCIO, idRtgNegocio);
		
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(paramsEntrada);
		
		RatingIndividual rtgInd = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_RTG_INDIVIDUAL_X_RTG_NEGOCIO, paramIbatis);
		
		return rtgInd;
	}

	public RatingIndividual buscarRatingIndividualPorComportamiento(Long idCliente, Long idRtgComp) {
		Map paramsEntrada = new HashMap();
		paramsEntrada.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		paramsEntrada.put(ConstantesSEFE.PARAM_ID_RATING_COMPORTAMIENTO, idRtgComp);
		
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(paramsEntrada);
		
		RatingIndividual rtgInd = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_RTG_INDIVIDUAL_X_RTG_COMPORTAMIENTO, paramIbatis);
		
		return rtgInd;
	}

	public RatingIndividual buscarRatingIndividualPorProyectado(Long idRtgProy) {		
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(idRtgProy);
		
		RatingIndividual rtgInd = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_RTG_INDIVIDUAL_X_RTG_PROYECTADO, paramIbatis);
		
		return rtgInd;
	}
	
	public RatingIndividual guardarTipoVaciadoRatingNegocio(Long idRtgInd, Integer idTpoVac) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_RATING, idRtgInd);
		map.put(ConstantesSEFE.PARAM_ID_TPO_VAC, idTpoVac);
		
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(map);
		
		RatingIndividual rtgInd = (RatingIndividual) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_UPD_TPO_VAC_RATING_NEGOCIO, paramIbatis);
		
		return rtgInd;
	}
}
