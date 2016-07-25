package com.bch.sefe.rating.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bch.sefe.rating.vo.RatingComportamiento;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingGrupal;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;

public interface RatingIndividualDAO {
	
	public Collection buscarRatingPorCliente(String rutCliente);
	
	public RatingIndividual buscarRatingIndividual(String rut, Long idRatIndividual);
	
	public Map buscarRatingAnterior(String rut, Long idRatingIndividual);
	
	public Collection buscarFortalezasDebilidades(String rut, Long idRatingIndividual);
	
	/**
	 * 
	 * 	Obtiene todos los rating relacionados asociados al rating segun su version.
	 * @param rut, identificador del cliente.
	 * @param idRatingIndividual, identificador del rating individual
	 * @param version, del rating de grupo pyme.
	 * @return List de objetos RelacionadoRpt.
	 */
	public Collection buscarRelacionadosPyme (String rut, Long idRatingIndividual, Long version);
	
	public Collection buscarRelacionadosNoPyme (String rut, Long idRatingIndividual);
	
	public Collection buscarRelacionadosGGEE(String rut, Long idRatingIndividual);
	
	/**
	 * Actualiza el rating individual con los valores del rating financiero
	 * 
	 * @param idCliente - identificador del cliente.
	 * @param idRatingInd - identificador del rating individual
	 * @param rating - instancia del rating financiero
	 * 
	 * @return - instancia actualizada del rating individual
	 */
	public RatingIndividual actualizarRatingFinanciero(Long idCliente, Long idRatingInd, RatingFinanciero rating);
	
	
	/**
	 * Actualiza el rating individual con los valores del rating proyectado
	 * 
	 * @param idRatingInd - identificador del rating individual
	 * @param rating - instancia del rating proyectado
	 * 
	 * @return - instancia actualizada del rating individual
	 */
	public RatingIndividual actualizarRatingProyectado(Long idCte, Long idRatingInd, RatingFinanciero rating);
	
	
	/**
	 * Actualiza el rating individual con los valores del rating de negocio
	 * 
	 * @param idRatingInd
	 *            - identificador del rating individual
	 * @param rating
	 *            - instancia del rating de negocio
	 */
	public void actualizarRatingNegocio(Long idRatingInd, RatingNegocio rating);
	
	
	/**
	 * Actualiza el rating individual con los valores del rating de comportamiento
	 * 
	 * @param idRatingInd - identificador del rating individual
	 * @param rating - instancia del rating de comportamiento
	 * 
	 * @return - instancia actualizada del rating individual
	 */
	public RatingIndividual actualizarRatingComportamiento(Long idRatingInd, RatingComportamiento rating);
	
	
	/**
	 * Actualiza la tabla de rating individual con los valores de rating garante
	 * 
	 * @param idRatingInd - identificador rating individual
	 * @param idGte - identificador rating garante
	 * @param notaGte - nota rating garante
	 * @param fecGte - fecha rating garante
	 * @return - instancia de RatingIndividual
	 */
	public RatingIndividual actualizarRatingGarante(Long idRatingInd, Long idCte, Long idGte, Double notaGte, Date fecGte);
	
	
	/**
	 * Busca el rating individual vigente para el cliente y banca pasados como argumento.
	 * Retorna null, si no existe rating vigente.
	 * 
	 * @param idCliente - id parte involucrada rut cliente empresa
	 * @return - instancia del rating individual vigente
	 */
	public RatingIndividual buscarRatingVigente(Long idCliente);

	
	/**
	 * Cambia el estado del rating individual.
	 * 
	 * @param rating - el rating a cambiar de estado
	 */
	public int cambiarEstado(RatingIndividual ratingVigente);

	
	/**
	 * Retorna el rating individual identificado por el id pasado como argumento
	 * 
	 * @param idCliente - identificador parte involucrada id del cliente empresa
	 * @param idRatIndividual - identificador del rating individual
	 * 
	 * @return - instancia de ratingIndividual. Null si no existe
	 */
	public RatingIndividual buscarRatingIndividual(Long idCliente, Long idRatIndividual);
	
	/**
	 * Crea una nueva instancia del rating individual
	 * 
	 * @param idCliente - identificador parte involucrada id del cliente empresa
	 * @param idBanca - identificador de la banca
	 * @param iidUsr - identificador parte involucrada log operador
	 * 
	 * @return - instancia de ratingIndividual. Null si no existe
	 */
	public RatingIndividual crearRating(Long idCliente, Integer idBanca, Long idUsr);

	public RatingIndividual buscarRatingEnCurso(Long idCliente, Integer idBanca);
	
	/**
	 * Actualiza el comentario del RatingIndividual.
	 * @param idRating
	 * @param comentario
	 * @param idUsuario
	 * @return
	 */
	public RatingIndividual actualizarComentario(Long idCliente, Long idRating, String comentario, Long idUsuario);

	public RatingIndividual confirmarRatingIndividualModificado(Long idRating, Long idUsr, Double nota, String comm, Integer idMotivo, String comMotivo, Double deudaBanco, Double deudaSbif);
	
	/**
	 * Elimina las referencias del rating de comportamiento asociadas al rating individual en curso.
	 * @param idRating
	 * @param idCliente
	 */
	public void borrarRtgComportamientoParaRtgIvidualCurso(Long idRating, Long idCliente);

	public List obtenerRatingsIndividualesNoInformados(boolean estado, Date desde, Date hasta);
	
	public List obtenerRatingsIndividualesInformados(Date fecha);

	public void establecerInformadoSiebel(RatingIndividual ratingIndividual, Date hasta, String logOper);

	public List obtenerPartesInvolucradasParaGrupal(RatingGrupal rg);

	public int cambiarEstadoHistorico(RatingIndividual rating);

	public RatingIndividual confirmarRatingIndividualModelo(Long idRating, Long idUsr, Double nota, String comm, Integer idMotivo,
			String comMotivo, Double deudaBanco, Double deudaSbif);

	public RatingIndividual actualizarRatingIndividual(RatingIndividual rating);

	/**
	 * Busca el rating individual asociado al rating financiero pasado como argumento.
	 * 
	 * @param idCliente - identificador parte involucrada cliente
	 * @param idBanca - identificador banca
	 * @param idRating - identificador rating financiero asociado
	 * @return
	 */
	public RatingIndividual buscarRatingIndividualPorFinanciero(Long idCliente, Integer idBanca, Long idRating);
	
	/**
	 * Busca el rating individual asociado al rating de negocio pasado como argumento.
	 * 
	 * @param idCliente
	 * @param idRtgNegocio
	 * @return
	 */
	public RatingIndividual buscarRatingIndividualPorNegocio(Long idCliente, Long idRtgNegocio);
	
	/**
	 * Busca el rating individual asociado al rating de comportamiento pasado como argumento.
	 * @param idCliente
	 * @param idRtgComp
	 * @return
	 */
	public RatingIndividual buscarRatingIndividualPorComportamiento(Long idCliente, Long idRtgComp);
	
	/**
	 * Busca el rating individual asociado al rating proyectado pasado como argumento.
	 * 
	 * @param idRtgProy
	 * @return
	 */
	public RatingIndividual buscarRatingIndividualPorProyectado(Long idRtgProy);
	
	public RatingIndividual guardarTipoVaciadoRatingNegocio(Long idRtgInd, Integer idTpoVac);
	
	public RatingIndividual desasociarRatingFinanciero(Long idCliente, Long idRatingInd);
	
	public RatingIndividual desasociarRatingNegocio(Long idCliente, Long idRatingInd);
	
	public RatingIndividual desasociarRatingProyectado(Long idCliente, Long idRatingInd);
	
	public RatingIndividual borrarPonderaciones(Long idCliente, Long idRatingInd);
}
