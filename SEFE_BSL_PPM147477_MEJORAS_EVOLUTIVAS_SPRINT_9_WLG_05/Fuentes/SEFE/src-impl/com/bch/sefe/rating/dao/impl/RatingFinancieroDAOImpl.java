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
import com.bch.sefe.rating.dao.RatingFinancieroDAO;
import com.bch.sefe.rating.vo.AjusteCalidad;
import com.bch.sefe.rating.vo.EvaluacionFinanciera;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.RangoNotaFinanciera;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.vaciados.vo.Vaciado;

public class RatingFinancieroDAOImpl implements RatingFinancieroDAO {

	private static final Logger log = Logger.getLogger(RatingFinancieroDAOImpl.class);

	public Collection buscarRatingPorCliente(String rutCliente) {
		ArrayList rtgFin = new ArrayList();
		ParametrizadorIbatis param = new ParametrizadorIbatis(rutCliente);
		rtgFin = (ArrayList) SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_BUSCAR_RATING_FINANCIERO_POR_CLIENTE, param);

		return rtgFin;
	}

	public RatingFinanciero buscarVigentePorClienteBanca(Long idCliente, Integer idBanca) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		map.put(ConstantesSEFE.PARAM_ID_BANCA, idBanca);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingFinanciero rating = (RatingFinanciero) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_FINANC_VIGENTE_POR_CLIENTE_BANCA, param);

		return rating;
	}

	public RatingFinanciero buscarVigenteProyectadoPorClienteBanca(Long idCliente, Integer idBanca) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_CLIENTE, idCliente);
		map.put(ConstantesSEFE.PARAM_ID_BANCA, idBanca);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingFinanciero rating = (RatingFinanciero) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_FINANC_PROY_VIGENTE_POR_CLIENTE_BANCA, param);

		return rating;
	}

	public RatingFinanciero buscarRatingPorId(Long idRating) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_RATING, idRating);
		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		RatingFinanciero rtgFinanciero = (RatingFinanciero) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_CONSULTA_FINANCIERO, param);

		return rtgFinanciero;

	}
	
	public RatingFinanciero buscarRatingPorIdVaciadoActual(Long idVaciado) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(idVaciado);
		
		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Buscando Rating financiero que utiliza el vaciado [{0}] como vaciado cabecera", new Object[] { idVaciado }));
		}
		
		RatingFinanciero ratingFinanciero = (RatingFinanciero) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_RATING_FINANCIERO_POR_VACIADO_ACTUAL, paramIbatis);
		
		if (log.isDebugEnabled()) {
			if (ratingFinanciero == null) {
				log.debug("No existe un rating financiero que este utilizando el vaciado como cabecera");
			} else {
				log.debug("El rating financiero [{" + ratingFinanciero.getIdRating() + "}] utiliza al vaciado");
			}
		}
		
		return ratingFinanciero;
	}
	
	public AjusteCalidad buscarAjustePorCalidad(Integer idClasificacion) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(idClasificacion);
		
		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Buscando un ajuste por calidad con id clasificacion [{0}]", new Object[] { idClasificacion }));
		}
		
		AjusteCalidad ajusteCalidad = (AjusteCalidad) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_AJUSTE_X_CALIDAD, paramIbatis);
		
		if (log.isDebugEnabled()) {
			if (ajusteCalidad == null) {
				log.debug("No existe el ajuste por calidad con el id solicitado.");
			} else {
				log.debug(MessageFormat.format("El factor obtenido para el ajuste por calidad [{0}] es de [{1}]", new Object[] {ajusteCalidad.getIdClasificacion(), ajusteCalidad.getFactor()}));
			}
		}
		
		return ajusteCalidad;
	}

	public List buscarVaciadosParaRating(String rutCliente, Integer idBanca) {
		Map params = new HashMap();
		params.put(ConstantesSEFE.PARAM_RUT_CLIENTE, rutCliente);
		params.put(ConstantesSEFE.PARAM_ID_BANCA, idBanca);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Buscando vaciados para el calculo de rating. Rut de cliente [{0}] y Id de Banca [{1}]", new Object[] { rutCliente, idBanca }));
		}

		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(params);

		List vaciados = SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_RTG_BUSCAR_VACIADOS_PARA_RATING, paramIbatis);

		return vaciados;
	}

	public RatingFinanciero insertarRating(RatingFinanciero rating) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(rating);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Insertando un nuevo rating financiero para el vaciado [{0}].", new Object[] { rating.getIdVaciado0() }));
		}

		RatingFinanciero ratingFinanciero = (RatingFinanciero) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_INSERT_RTG_FINANCIERO, paramIbatis);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Se insertado un nuevo rating financiero con id [{0}]", new Object[] { ratingFinanciero.getIdRating() }));
		}

		return ratingFinanciero;
	}

	public void insertarEvaluacionFinanciera(EvaluacionFinanciera eval) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(eval);
		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Insertando una nueva evaluacion para el indicador [{0}].", new Object[] { eval.getCodigoCuenta() }));
		}
		SQLMapper.getInstance().insert(ConstantesSEFE.SP_RTG_INSERT_EVAL_FINANCIERO, paramIbatis);
		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Se insertado una nueva evaluacion para el rating financiero con id [{0}]", new Object[] { eval.getIdRating() }));
		}
	}
	
	public List buscarIndicadoresFinancieros(Long idMatriz, Long idTema, Date fechaRating, Integer estado) {
		Map parametros = new HashMap();
		parametros.put(ConstantesSEFE.PARAM_ID_MATRIZ, idMatriz);
		parametros.put(ConstantesSEFE.PARAM_ID_TEMA, idTema);
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(fechaRating);
        Long fechaNumerica = Long.valueOf(date);
		parametros.put("fechaRating", fechaNumerica);
		parametros.put("estado", estado);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Buscando indiciadores financieros asociados a la matriz [{0}] y tema [{1}]", new Object[] { idMatriz, idTema }));
		}

		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(parametros);

		List lstIndicadores = SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_RTG_BUSCAR_INDIC_FINANCIEROS_POR_TEMA, paramIbatis);

		if (log.isDebugEnabled()) {
			if (lstIndicadores == null || lstIndicadores.isEmpty()) {
				log.debug(MessageFormat.format("No existen indicadores financieros para la matriz [{0}] y tema [{1}]", new Object[] { idMatriz, idTema }));
			} else {
				log.debug(MessageFormat.format("Se encontraron [{0}] indicadores financieros para la matriz [{1}] y tema [{2}]", new Object[] { new Integer(lstIndicadores.size()),
						idMatriz, idTema }));
			}
		}

		return (lstIndicadores != null ? lstIndicadores : new ArrayList());
	}

	public MatrizFinanciera buscarMatrizFinanciera(Integer idBanca, Integer idSegmento) {
		Map parametros = new HashMap();
		parametros.put(ConstantesSEFE.PARAM_ID_BANCA, idBanca);
		parametros.put(ConstantesSEFE.PARAM_ID_SEGMENTO, idSegmento);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Buscando matriz financiera por id de banca [{0}] y id de segmento [{1}]", new Integer[] { idBanca, idSegmento }));
		}

		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(parametros);

		MatrizFinanciera matriz = (MatrizFinanciera) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_MATRIZ_FINANCIERA_POR_BANCA_Y_SEGMENTO, paramIbatis);

		if (log.isDebugEnabled()) {
			if (matriz == null)
				log.debug("No se ha encontrado matriz financiera.");
			else
				log.debug(MessageFormat.format("Matriz financiera encontrada. Id Matriz [{0}] y id de banca [{1}]", new Object[] { matriz.getIdMatriz(), matriz.getIdBanca() }));
		}

		return matriz;
	}
	
	public MatrizFinanciera buscarMatrizFinancieraPorId(Long idMatriz) {
		Map parametros = new HashMap();
		parametros.put(ConstantesSEFE.PARAM_ID_MATRIZ, idMatriz);

		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(parametros);
		MatrizFinanciera matriz = (MatrizFinanciera) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_MATRIZ_FINANCIERA_POR_ID, paramIbatis);

		return matriz;
	}

	public List buscarTemasMatrizFinanciera(Long idMatriz, Date fechaRating, Integer estadoRating) {
		Map param = new HashMap();
		param.put(ConstantesSEFE.PARAM_ID_MATRIZ, idMatriz);
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(fechaRating);
        Long fechaNumerica = Long.valueOf(date);
		param.put("fechaRating", fechaNumerica);
		param.put("estado", estadoRating);

		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(param);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Buscando temas financieros de la matriz [{0}]", new Long[] { idMatriz }));
		}

		List lstTemas = SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_RTG_BUSCAR_TEMAS_FINANCIEROS_POR_MATRIZ, paramIbatis);

		if (log.isDebugEnabled()) {
			if (lstTemas == null || lstTemas.isEmpty()) {
				log.debug(MessageFormat.format("No existen temas financieros para la matriz financiera [{0}]", new Long[] { idMatriz }));
			} else {
				log.debug(MessageFormat.format("Se encontraron [{0}] temas financieros para la matriz financiera [{1}]", new Object[] { new Integer(lstTemas.size()), idMatriz }));
			}
		}

		return (lstTemas != null ? lstTemas : new ArrayList());
	}

	public RangoNotaFinanciera buscarRangoNota(Long idMatriz, Long idTema, Long codigoCta, Double valor, Integer notaInferior, Integer notaSuperior, Date fechaRating, Integer estado) {
		Map parametros = new HashMap();
		parametros.put(ConstantesSEFE.PARAM_ID_MATRIZ, idMatriz);
		parametros.put(ConstantesSEFE.PARAM_ID_TEMA, idTema);
		parametros.put(ConstantesSEFE.PARAM_CODIGO_CUENTA, codigoCta);
		parametros.put(ConstantesSEFE.PARAM_VALOR, valor);
		parametros.put(ConstantesSEFE.PARAM_INDICE_MIN, notaInferior);
		parametros.put(ConstantesSEFE.PARAM_INDICE_MAX, notaSuperior);
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(fechaRating);
        Long fechaNumerica = Long.valueOf(date);
        parametros.put("fechaRating", fechaNumerica);
        parametros.put("estado", estado);

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Buscando rango de nota para indicador [{0}], de la matriz [{1}] tema [{2}] y valor [{3}]", new Object[] { codigoCta, idMatriz, idTema,
					valor }));
		}

		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(parametros);

		RangoNotaFinanciera rangoNotaFinanciera = (RangoNotaFinanciera) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_RANGO_NOTA_POR_INDICADOR, paramIbatis);

		if (log.isDebugEnabled()) {
			if (rangoNotaFinanciera == null) {
				log.debug(MessageFormat.format("No existe un rango de nota financiera para el indicador [{0}] con valor [{1}], de la matriz [{2}] y tema [{3}]", new Object[] {
						codigoCta, valor, idMatriz, idTema }));
			} else {
				log.debug(MessageFormat.format("El rango de nota encontrado es [{0}], con valor inferior: {1} y valor superior: {2}", new Object[] {
						rangoNotaFinanciera.getIdRango(), rangoNotaFinanciera.getValorInferior(), rangoNotaFinanciera.getValorSuperior() }));
			}
		}

		return rangoNotaFinanciera;
	}
	
	public RangoNotaFinanciera buscarRangoNota(Long idRangoNota, Long idMatriz, Long idTema, Long codCuentaId, Date fechaRating, Integer estado) {
		RangoNotaFinanciera rango = null;
		Map paramEntrada = new HashMap();
		
		paramEntrada.put(ConstantesSEFE.PARAM_ID_RGO_NOTA, idRangoNota);
		paramEntrada.put(ConstantesSEFE.PARAM_ID_MATRIZ, idMatriz);
		paramEntrada.put(ConstantesSEFE.PARAM_ID_TEMA, idTema);
		paramEntrada.put(ConstantesSEFE.PARAM_CODIGO_CUENTA, codCuentaId);
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(fechaRating);
        Long fechaNumerica = Long.valueOf(date);
        paramEntrada.put("fechaRating", fechaNumerica);
        paramEntrada.put("estado", estado);
		
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(paramEntrada);
		
		rango = (RangoNotaFinanciera) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_BUSCAR_RANGO_NOTA_POR_ID, paramIbatis);
		
		return rango;
	}

	public AjusteCalidad buscarAjusteCalidad(Long idAjteCal) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_AJTE_CALIDAD, idAjteCal);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);

		AjusteCalidad rating = (AjusteCalidad) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_FIN_BUSCAR_AJUSTE_CALIDAD, param);

		return rating;
	}

	/*
	 * (sin Javadoc)
	 * @see com.bch.sefe.rating.dao.RatingFinancieroDAO#guardarProyectado(com.bch.sefe.rating.vo.RatingFinanciero)
	 */
	public RatingFinanciero guardarProyectado(RatingFinanciero ratingProy) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(ratingProy);

		RatingFinanciero ratingFinanciero = (RatingFinanciero) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_INSERT_RTG_PROYECTADO, paramIbatis);

		return ratingFinanciero;
	}
	
	/*
	 * (sin Javadoc)
	 * @see com.bch.sefe.rating.dao.RatingFinancieroDAO#actualizarProyectado(com.bch.sefe.rating.vo.RatingFinanciero)
	 */
	public RatingFinanciero actualizarProyectado(RatingFinanciero ratingProy) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(ratingProy);

		RatingFinanciero ratingFinanciero = (RatingFinanciero) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_ACTUALIZAR_RTG_PROYECTADO, paramIbatis);

		return ratingFinanciero;
	}
	
	public RatingFinanciero actualizarEstadoFinanciero(RatingFinanciero rating) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(rating);

		RatingFinanciero ratingFinanciero = (RatingFinanciero) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_ACTUALIZAR_ESTADO_RATING_FINANCIERO, paramIbatis);

		return ratingFinanciero;
	}

	public RatingFinanciero actualizarFinanciero(RatingFinanciero ratingFinanciero) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(ratingFinanciero);

		if (log.isDebugEnabled()) {
			log.debug("Actualizando rating financiero con id [" + ratingFinanciero.getIdRating() + "]");
		}

		RatingFinanciero rtg = (RatingFinanciero) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_ACTUALIZAR_RATING_FINANCIERO, paramIbatis);

		if (log.isDebugEnabled()) {
			log.debug("Se ha actualizado la informacion del rating financiero");
		}

		return rtg;
	}
	
	public Vaciado cambiarProyectadoAVigente(Long idVaciado) {
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_VACIADO, idVaciado);
		
		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		Vaciado vac = (Vaciado) SQLMapper.getInstance().queryForObject(ConstantesSEFE.SP_RTG_ACT_EST_VACIADO_PROYECTADO, param);
		
		return vac;
	}

	public List buscarEvaluacionesFinancieras(Long idRatingFinanciero) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(idRatingFinanciero);
		
		if (log.isDebugEnabled()) {
			log.debug("Buscando las evaluaciones del rating financiero [" + idRatingFinanciero + "]");
		}
		
		List evaluaciones = SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_RTG_BUSCAR_EVALUACIONES_FINANCIERAS_POR_RTG_FINANCIERO, paramIbatis);
		
		if (log.isDebugEnabled()) {
			if (evaluaciones != null && !evaluaciones.isEmpty()) {
				log.debug(MessageFormat.format("Se han encontrado [{0}] registros para el rating financiero [{1}]", new Object[] { new Integer(evaluaciones.size()), idRatingFinanciero }));
			} else {
				log.debug("No se encontaron registros de evaluaciones financieras");
			}
		}
		
		return (evaluaciones == null ? new ArrayList() : evaluaciones);
	}

		
	
	
	public List buscarVaciadosAnterioresGenerico(Long idVaciado) {
			ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(idVaciado);
		
		if (log.isDebugEnabled()) {
			log.debug("Buscando vaciados para el vaciado [" + idVaciado + "]");
		}
		
		List vaciadosAnteriores = SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_RTG_BUSCAR_VACIADOS_ANTERIORES_GENERICO, paramIbatis);
		
		if (log.isDebugEnabled()) {
			if (vaciadosAnteriores != null) {
				log.debug(MessageFormat.format("Se encontraron {0} vaciados anteriores(rating financiero) para el vaciado {1}", new Object[] { new Integer(vaciadosAnteriores.size()), idVaciado }));
			} else {
				log.debug(MessageFormat.format("No se encontraron vaciados anteriores(rating financiero) para el vaciado {1}", new Object[] { idVaciado }));
			}
		}
		
		return (vaciadosAnteriores != null ? vaciadosAnteriores : new ArrayList());
	}

	public List buscarPonderacionesMatriz(Long idMatriz) {
		ParametrizadorIbatis paramIbatis = new ParametrizadorIbatis(idMatriz);
		
		List ponderaciones = new ArrayList();
		
		ponderaciones = SQLMapper.getInstance().queryForList(ConstantesSEFE.SP_RTG_BUSCAR_PONDERACION_PERIODOS_POR_MATRIZ, paramIbatis);
		
		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Se han encontrado {0} ponderaciones para la matriz financiera #{1}", new String[] {String.valueOf(ponderaciones.size()), idMatriz.toString()}));
		}
		
		return ponderaciones;
	}
}
