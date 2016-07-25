package com.bch.sefe.rating.srv;

import java.util.Date;
import java.util.List;

import com.bch.sefe.rating.vo.AjusteCalidad;
import com.bch.sefe.rating.vo.EvaluacionFinanciera;
import com.bch.sefe.rating.vo.IndicadorFinanciero;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.RangoNotaFinanciera;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.TemaFinanciero;
import com.bch.sefe.vaciados.vo.Vaciado;

public interface GestorRatingFinanciero {

	public RatingFinanciero calcularRating(String rutCliente, Integer idBanca, Long idVaciado, String loginUsuario);

	/**
	 * Busca el listado de vaciados que cumplen con el criterio para poder realizar el calculo del rating. Existe una regla dentro del criterio de
	 * busqueda que indica que si todos los vaciados que cumplen con la condicion son DAI estos son desplegados en caso contrario los DAI no se
	 * muestran. Este criterio es implementado en este metodo; es decir cuando en los vaciados existan DAI y no DAI solo se retornaran los no DAI y en
	 * caso de existir solo DAI estos seran los retornados.<br>
	 * El listado retornado se encuentra ordenado segun criterio.
	 * 
	 * @param rutCliente
	 *            - rut del cliente.
	 * @param idBanca
	 *            - identificador de la banca.
	 * @return - listado de vaciados.
	 */
//	public List buscarVaciadosParaRating(String rutCliente, Integer idBanca);

	/**
	 * Inserta un nuevo rating financiero.<br>
	 * Guarda un nuevo rating financiero seteando todos los campos de la instancia y retorna la nueva instancia creada con el identificador de rating
	 * asignado.
	 * 
	 * @param rating
	 *            - instancia de rating financiero.
	 * @return - instncia de rating financiero con identificador generado.
	 */
	public RatingFinanciero grabarRating(RatingFinanciero rating);
	
	
	/**
	 * Inserta un nuevo rating financiero correspondiente a un rating proyectado.<br>
	 * Guarda un nuevo rating financiero seteando todos los campos de la instancia y retorna la nueva instancia creada con el identificador de rating
	 * asignado.
	 * El proyectado almacena el mismo identificador para los campos RTG_FINAN_ID y RATING_FINAN_PROYECTADO_ID
	 * 
	 * @param rating
	 *            - instancia de rating financiero.
	 * @return - instncia de rating financiero con identificador generado.
	 */
	public RatingFinanciero grabarRatingProyectado(RatingFinanciero rating);
	
	
	/**
	 * Actualiza los valores de rating proyectado
	 * El proyectado almacena el mismo identificador para los campos RTG_FINAN_ID y RATING_FINAN_PROYECTADO_ID
	 * 
	 * @param rating
	 *            - instancia de rating financiero.
	 * @return - instncia de rating financiero con identificador generado.
	 */
	public RatingFinanciero actualizarRatingProyectado(Integer idBanca, RatingFinanciero rating);
	
	/**
	 * Metodo que graba o actualiza las evaluaciones asociadas a un rating financiero calculado
	 * @param rating
	 */
	 public void grabarEvaluaciones(RatingFinanciero ratingFinanciero);
	

	/**
	 * Busca el vaciado anterior que cumpla con las reglas de negocio para calculo de rating financiero.
	 * 
	 * @param vaciado
	 *            - Instancia de vaciado para el que se obtendra su vaciado anterior.
	 * @return - Instancia de Vaciado.
	 */
	public Vaciado buscarVaciadoAnteriorRating(Vaciado vaciado, Integer idBanca);

	/**
	 * Busca la matriz financiera que corresponda.
	 * 
	 * @param idBanca
	 *            - Identificador de la banca.
	 * @param idSegmento
	 *            - Identificador del segmento.
	 * @param esAnual
	 *            - Flag que identifica si se necesita la matriz para un periodo parcial o anual.
	 * @return
	 */
	public MatrizFinanciera obtenerMatrizFinanciera(Integer idBanca, Integer idSegmento, Boolean esAnual);
	
	public MatrizFinanciera obtenerMatrizFinanciera(Long idMatriz);
	
	/**
	 * marias 20121030
	 * Se modifica para retornar la matriz financiera con los porcentajes ponderados
	 * basados en la configuracion de la combinacion de periodos
	 * 
	 * @param idBanca - Identificador de la banca
	 * @param idSegmento - Identificador del segmento
	 * @param esAnual - Flag que identifica si se necesita la matriz para un periodo parcial o anual
	 * @param combinacion - cadena que indica la combinacion de periodos buscado. Ej: AAP - AA - P
	 * @param mesesPer - numero de meses del periodo de cierre: entre 1 y 12
	 */
	public MatrizFinanciera obtenerMatrizFinanciera(Integer idBanca, Integer idSegmento, Boolean esAnual, String combinacion, int mesesPer);
	
	/**
	 * marias 20121030
	 * Se modifica para retornar la matriz financiera con los porcentajes ponderados
	 * basados en la configuracion de la combinacion de periodos
	 * 
	 * @param idBanca - Identificador de la banca
	 * @param idSegmento - Identificador del segmento
	 * @param esAnual - Flag que identifica si se necesita la matriz para un periodo parcial o anual
	 * @param combinacion - cadena que indica la combinacion de periodos buscado. Ej: AAP - AA - P
	 * @param mesesPer - numero de meses del periodo de cierre: entre 1 y 12
	 */
	public MatrizFinanciera obtenerMatrizFinanciera(Long idMatriz, String combinacion, int mesesPer);
	
	
	/**
	 * Obtiene los temas financieros de una matriz.
	 * 
	 * @param idMatriz
	 *            - identificador de la matriz.
	 * @return - listado de {@link TemaFinanciero}.
	 */
	public List obtenerTemasFinancieros(Long idMatriz, Date fechaRating, Integer estadoRating);
	
	/**
	 * Obtiene todos los indicadores financieros asociados a un tema de una matriz.
	 * 
	 * @param idMatriz - identificador de la matriz.
	 * @param idTema - identificador del tema.
	 * @return - listado de {@link IndicadorFinanciero}.
	 */
	public List obtenerIndicadoresFinancieros(Long idMatriz, Long idTema, Date fechaRating, Integer estado);

	/**
	 * Busca la instancia de rating financiero para el identificador pasado como parametro.
	 * 
	 * @param idRatingFinanciero
	 *            - identificador del rating financiero.
	 * @return - instancia del rating financiero
	 */
	public RatingFinanciero obtenerRating(Long idRatingFinanciero);

	/**
	 * Valida si el vaciado es vigente o no.
	 * 
	 * @param rtgFinanciero
	 * @param rtgFinanciero
	 * @return
	 */
	public boolean estaVigente(RatingFinanciero rtgFinanciero, Integer idBanca);
	
	/**
	 * Retorna una instancia de {@link RangoNotaFinanciera} para el valor de un indicador.
	 * 
	 * @param indicador
	 *            - instancia de {@link IndicadorFinanciero}.
	 * @param valor
	 *            - valor del indicador financiero.
	 * @return - instancia de {@link RangoNotaFinanciera}.
	 */
	public RangoNotaFinanciera obtenerRangoNota(IndicadorFinanciero indicador, Double valor, Integer notaInferior, Integer notaSuperior, Date fechaRating, Integer estado);
	
	/**
	 * Busca el ajuste a utilizar para la calidad del vaciado pasado como parametro.
	 * 
	 * @param idCalidadVaciado
	 *            - identificador de la clasificacion de calidad.
	 * @return - instancia de {@link AjusteCalidad}.
	 */
	public AjusteCalidad buscarAjustePorCalidad(Integer idCalidadVaciado);
	
	/**
	 * Recibe una instancia de {@link EvaluacionFinanciera} pasada por parametro y la inserta en la base de datos.
	 * 
	 * @param idCliente
	 * @param idTema
	 * @param idMatriz
	 * @param codigoCuenta
	 * @param idVaciado
	 * @param idRating
	 * @param idRango
	 * @param ptjTema
	 * @param ptjEvaluador
	 * @param ptjIndice
	 * 
	 * @return - retorna la instancia persistente de {@link EvaluacionFinanciera}.
	 */
	public EvaluacionFinanciera grabarEvaluacionFinanciera(EvaluacionFinanciera evaluacion);
	
	/**
	 * Busca por el id de vaciado, un rating financiero previamente calculado. 
	 * 
	 * @param idVaciado
	 * @return
	 */
	public RatingFinanciero buscarRatingFinancieroPorIdVaciadoActual(Long idVaciado);
	
	/**
	 * Busca todas las {@link EvaluacionFinanciera} de un rating financiero.
	 * 
	 * @param idRatingFinanciero
	 *            - identificador del rating financiero.
	 * @return - listado de instancias de {@link EvaluacionFinanciera}. Lista vacia si es que no hay resultados.
	 */
	public List buscarEvaluacionesFinancieras(Long idRatingFinanciero);
	
	/**
	 * Se actualiza la informacion del rating financiero.
	 * 
	 * @param rating
	 *            - instancia del rating financiero a actualizar.
	 * @return - instancia del rating financiero.
	 */
	public RatingFinanciero actualizarRating(RatingFinanciero rating);

	
	public Vaciado cambiarProyectadoAVigente(Long idVaciado);
	
	/**
	 * Guarda las evauaciones creadas en el rating financiero proyectado
	 * @param ratingFinanciero
	 * @return
	 */
	public RatingFinanciero grabarEvaluacionesProyectado(RatingFinanciero ratingFinanciero);
	
	public RangoNotaFinanciera buscarRangoNota(Long idRangoNota, Long idMatriz, Long idTema, Long codCuenta, Date fechaRating, Integer estado);
	
	
	/**
	 * Busca las ponderaciones de los periodos para la matriz financiera
	 * 
	 * @param idMatriz - identificador Long de la matriz financiera
	 * @return - lista de PonderacionMatrizFinanciera
	 */
	public List buscarPonderacionesMatriz(Long idMatriz);
	
	
	/**
	 * buscar el ajusta por calida d para el rating proyectado
	 * @param idClasifCalidad
	 * @return
	 */
	public AjusteCalidad buscarAjustePorCalidadParaProyecciones(Integer idClasifCalidad);
	
	public boolean validarIvas(RatingFinanciero rtgFinanciero) ;
}
