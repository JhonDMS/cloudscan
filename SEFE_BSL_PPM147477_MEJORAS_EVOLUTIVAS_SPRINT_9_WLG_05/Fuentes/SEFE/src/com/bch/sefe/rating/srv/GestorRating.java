package com.bch.sefe.rating.srv;

import java.util.List;

import com.bch.sefe.comun.vo.ProbDefault;
import com.bch.sefe.rating.vo.EquivalenciaRating;
import com.bch.sefe.rating.vo.PonderacionNivelVenta;
import com.bch.sefe.rating.vo.PremioPatrimonio;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.Segmento;
import com.bch.sefe.vaciados.vo.Vaciado;

public interface GestorRating {

	public Segmento obtenerSegmento(double valor, Integer tipoSegmento);

	public Segmento obtenerSegmentoPorId(Integer sgmId);
	
	/**
	 * Retorna el segmento de ventas basado en la informaciÃ³n del vaciado
	 * 
	 * @param vac - instancia de Vaciado
	 * @return - segmento de ventas; null en caso de no existir
	 */
	public Segmento obtenerSegmentoVentasPorVaciado(Vaciado vac, Integer tpoSegmento);

	/**
	 * Busca el vaciado que cumple con las condiciones para el calculo de rating.<br>
	 * La banca es utilizada para discriminar que consulta se realizara ya que las condiciones del vacaciado varian segun la banca.
	 * 
	 * @param rutCliente
	 *            - rut cliente empresa al que se le genera el rating
	 * @param rutCliente
	 *            - identificador de la banca del cliente.
	 * 
	 * @return - instancia del vaciado buscado. Null si no existe
	 */
	public Vaciado buscarVaciadoParaRating(String rutCliente, Integer idBanca);
	
	public Vaciado buscarVaciadoParaRatingPYME(String rutCliente, Integer idBanca, RatingIndividual ri);
	
	/**
	 * Retorna la antiguedad maxima en meses, permitida para los vaciados para el calculo de rating. La vigencia depende de la banca.
	 * 
	 * La busqueda se realiza en el archivo de configuracion <b>sefe.properties</b> haciendo uso de las propiedades
	 * <b>rating.antiguedad.vaciado.vigente.xxx</b>
	 * 
	 * @param idBanca
	 *            - identificador de la banca.
	 * @return - antiguedad maxima permitida.
	 */
	public Integer obtenerAntiguedadMaximaVaciado(Integer idBanca);
	
	/**
	 * Obtiene la ponderacion segun el segmento y tipo de rating. Si la ponderacion no es encontrada entonces el metodo retornal NULL
	 * 
	 * @param segmento
	 *            - instancia de segmento.
	 * @param idTipoRating
	 *            - identificador del tipo de rating. (Identificador de clasificacion).
	 * @return - instancia de {@link PonderacionNivelVenta}.
	 */
	public PonderacionNivelVenta buscarPonderacionNivelVentas(Segmento segmento, Integer idTipoRating);
	
	/**
	 * Busca un segmento por tipo de segmento, valor y banca.
	 * 
	 * @param tipoSegmento
	 *            - identificador del tipo de segmento. (Identificador de clasificacion).
	 * @param valor
	 *            - valor que se busca entre los rangos.
	 * @param idBanca
	 *            - identificador de la banca.
	 * @return - instancia de Segmento encontrado.
	 */
	public Segmento buscarSegmento(Integer tipoSegmento, Double valor, Integer idBanca);
	
	/**
	 * Busca el premio por patrimonio vigente mas nuevo que existe para el segmento.
	 * 
	 * @param segmento
	 *            - instancia del {@link Segmento}.
	 * @return - instancia de {@link PremioPatrimonio}.
	 */
	public PremioPatrimonio buscarPremioPatrimonio(Segmento segmento);
	
	/**
	 * Retorna el listado de instancias de {@link EquivalenciaRating} existentes.
	 * 
	 * @return - listado de {@link EquivalenciaRating}. Lista vacia en caso de no encontrar informacion.
	 */
	public List buscarEquivalenciasRating(Integer idBanca) ;
	
	public Double obtenerMontoVentas(Vaciado vaciado);
	
	/**
	 * Busca la instancia de {@link ProbDefault} para un rating final.
	 * 
	 * @param nota
	 *            - nota con la que se busca la probabilidad default.
	 * @return - instancia de {@link ProbDefault} que coincide con la busqueda.
	 */
	public ProbDefault buscarProbabilidadDefault(Integer idBanca, Double notaRtgFinal);

	public List obtenerSegmentosVentaPorBanca(Integer idBanca);
	
	public Segmento obtenerSegmentoVentasRatingIndividual(Vaciado vac, Integer idBanca);
	
	public Segmento obtenerSegmentoVentasRatingComportamiento(Vaciado v, Integer idTpoSegmento, Integer idBanca);
	
	
	public List buscarListaVaciadosParaRating(String rutCliente, Integer idBanca);
	
	public Double obtenerMontoVentasRatingIndividual(Vaciado vaciado);
	
	/**
	 * buscar el componente de un tipo de rating
	 * @param idBanca
	 * 					- si id banca es nulo entonces recupera todas componentes que cumplan con idComponente
	 * @param idComponente
	 * @return
	 */
	public List buscarComponentesRating(Integer idBanca, Integer idComponente);
	
	/**
	 * Retorna solamente los id de los componentes utilizados
	 * @param idBanca
	 * @param idComponente
	 * @return
	 */
	public List buscarComponentesRatingSoloIdComponente(Integer idBanca, Integer idComponente);
	
	/**
	 * Busca todas las probabilidades default que existan.
	 * 
	 * @return
	 */
	public List buscarProbabilidadesDefault();

	/**
	 * metodo que recupera la segmentacion de ventas utilizando el servicio de ivas
	 * @param idTpoSegmento
	 * @param idBanca
	 * @return
	 */
	public Segmento obtenerSegmentoVentasRatingComportamiento(RatingIndividual rating, Integer idTpoSegmento, Integer idBanca);
	
	/**
	 * metodo que recupera la lista de segmento por tipo y banca
	 * @param idBanca
	 * @param tpoSegemento
	 * @return
	 */
	public List obtenerSegmentosPorTipoYBanca(Integer idBanca, Integer tpoSegemento) ;
	
	public List obtenerSegmentosPorTipoBancaYFecha(Integer idBanca, Integer tpoSegemento, Long idRating);
	
	/**
	 * metodo que recupera el segmento por tipoSegmento, banca y una cuenta de vaciado
	 * @param vac
	 * @param idBanca
	 * @param tpoSegmeneto
	 * @return
	 */
	public Segmento obtenerSegmentoRatingIndividual(Vaciado vac, Integer idBanca, Integer tpoSegmeneto, RatingIndividual ratingIndiv);
	
	/**
	 * metodo que recupera las ventas desde el servicio de iva. Valor retornado es en UF
	 * @param idCliente
	 * @return
	 */
	public Double obtenerMontoVentasRatingIndividual(Long idCliente);

	/**
	 * sprint 2 req 7.1.4 alinear y borrar rating en curso
	 * método para borrar un rating en curso
	 * @param idRating
	 * @return
	 */
	public boolean borrarRatingEnCurso(Long idRating);

}
