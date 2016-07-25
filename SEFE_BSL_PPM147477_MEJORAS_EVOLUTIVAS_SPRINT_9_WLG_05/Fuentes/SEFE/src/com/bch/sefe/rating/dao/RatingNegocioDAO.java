package com.bch.sefe.rating.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.bch.sefe.rating.vo.AlternativaNegocio;
import com.bch.sefe.rating.vo.MatrizNegocio;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.rating.vo.RespuestaNegocio;

public interface RatingNegocioDAO {
	
	public Collection buscarUltimosRating(String rut, Integer idBanca);
	
	public Collection buscarRatingPorCliente(String rutCliente);
	
	public RatingNegocio buscarRatingVigente(String rut, Integer idBanca,Integer diasEvaluacion);
	
	public MatrizNegocio buscarMatrizNegocio(Integer idBanca, Integer idSegmento);
	
	public RatingNegocio guardarRating(RatingNegocio rating);
	
	public Collection buscarTemaNegocio(Long idMatriz, Integer idEstado, Date fechaRating);
	
	public Collection buscarPreguntaNegocio(Long idMatriz, Long idTema, Integer idEstado, Date fechaRating);
	
	public Collection buscarAlternativaNegocio(Long idMatriz, Long idTema, Long idPregunta, Integer idEstado, Date fechaRating);
	
	public Collection buscarAlternativaNegocioVigHis(Long idMatriz, Long idTema, Long idPregunta);
	
	public RatingNegocio buscarRatingNegocioPorId(Long idRating);
	
	public MatrizNegocio buscarMatrizNegocioPorId(Long idMatriz);
	
	/**
	 * Busca el rating vigente, si disponinble, para el cliente y banca pasados como argumento.
	 * Si no existe un rating vigente, retorna null.
	 * @deprecated
	 * @param idCliente - id parte involucrada cliente empresa / persona con giro
	 * @param idBanca - identificador de la banca
	 * @return
	 */
	public RatingNegocio buscarVigentePorClienteBanca(Long idCliente, Integer idBanca);
	
	/**
	 * Busca las respuestas ingresadas por un usuario para un rating de negocio.
	 * 
	 * @param idRatingNegocio
	 *            - Long con el identificador unico del rating de negocio.
	 * @return - Listado de respuestas.
	 */
	public List buscarRespuestasRatingNegocio(Long idRatingNegocio);
	
	/**
	 * Graba una instancia de respuesta para el rating de negocio con la informacion ingresada.
	 * @param respuesta - Instancia de respuesta de negocio.
	 */
	public void grabarRespuesta(RespuestaNegocio respuesta);
	
	/**
	 * Elimina todas las respuestas asociadas a un rating de negocio.
	 * 
	 * @param idCliente - Identificador del cliente (parte involucrada)
	 * @param idRatingNegocio - Identificador del rating de negocio.
	 * @return - Numero de registros afectados.
	 */
	public Integer borrarRespuestas(Long idCliente, Long idRatingNegocio);
	
	/**
	 * Actualiza el detalle del rating de negocio.
	 * 
	 * @param rating - Instancia de RatingNegocio.
	 * @return - retorna rating de negocio actualizado.
	 */
	public RatingNegocio actualizarRating(RatingNegocio rating);
	
	/**
	 * Busca la alternativa por llave primaria.
	 * 
	 * @param idAlternativa - identificador de la alternativa.
	 * @param idMatriz - identificador de la matriz.
	 * @param idTema - identificador del tema.
	 * @param idPregunta - identificador de pregunta.
	 * @return - instancia de Alternativa de Negocio.
	 */
	public AlternativaNegocio buscarAlternativa(Long idAlternativa, Long idMatriz, Long idTema, Long idPregunta);
	
	public RatingNegocio actualizarEstadoRating(RatingNegocio rating);
}
