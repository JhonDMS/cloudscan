package com.bch.sefe.rating.dao;

import java.util.Collection;
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

public interface RatingFinancieroDAO {

	/**
	 * Busca el listado de vaciados que pueden ser utilizados para el calculo del rating financiero.<br>
	 * El criterio de busqueda para los vaciados difiere segun la banca.
	 * 
	 * @param rutCliente
	 *            - rut del cliente.
	 * @param idBanca
	 *            - identificador de la banca.
	 * @return - listado de con las instancias de vaciados.
	 */
	public List buscarVaciadosParaRating(String rutCliente, Integer idBanca);

	public Collection buscarRatingPorCliente(String rutCliente);
	
	/**
	 * Busca todas evaluaciones financieras asociadas a un rating financiero.
	 * 
	 * @param idRatingFinanciero
	 *            - identificador del rating financiero.
	 * @return - listado con las instancias de {@link EvaluacionFinanciera}. Retorna una lista vacia en caso de no encontrar resultados.
	 */
	public List buscarEvaluacionesFinancieras(Long idRatingFinanciero);

	/**
	 * Busca el rating vigente, si disponible, para el cliente y banca pasados como argumento. Si no existe un rating vigente, retorna null.
	 * 
	 * @param idCliente
	 *            - id parte involucrada cliente empresa / persona con giro
	 * @param idBanca
	 *            - identificador de la banca
	 * @return
	 */
	public RatingFinanciero buscarVigentePorClienteBanca(Long idCliente, Integer idBanca);

	/**
	 * Busca el rating vigente proyectado, si disponible, para el cliente y banca pasados como argumento. Si no existe un rating vigente, retorna
	 * null.
	 * 
	 * @param idCliente
	 *            - id parte involucrada cliente empresa / persona con giro
	 * @param idBanca
	 *            - identificador de la banca
	 * @return
	 */
	public RatingFinanciero buscarVigenteProyectadoPorClienteBanca(Long idCliente, Integer idBanca);

	/**
	 * Busca el rating financiero.
	 * 
	 * @param idRating
	 *            - identificador del rating financiero
	 * 
	 * @return - instancia de RatingFinanciero. Null si no existe
	 */
	public RatingFinanciero buscarRatingPorId(Long idRating);
	
	/**
	 * Busca al rating financiero existente que utilice como vaciado 0 (vaciado cabecera) idVaciado.
	 * 
	 * @param idVaciado
	 *            - identificador de vaciado cabecera.
	 * @return - instancia de rating financiero.
	 */
	public RatingFinanciero buscarRatingPorIdVaciadoActual(Long idVaciado);
	
	/**
	 * Busca al ajuste por calidad existente que utilice como clasificacion el idClasificacion pasado por parametro.
	 * 
	 * @param idClasificacion
	 *            - identificador para obtener el factor .
	 * @return - instancia de AjusteCalidad.
	 */
	public AjusteCalidad buscarAjustePorCalidad(Integer idClasificacion);

	/**
	 * Inserta una nueva instancia de un rating financiero.
	 * 
	 * @param rating
	 *            - instancia del rating financiero a guardar.
	 * @return - instancia del rating financiero guardado.
	 */
	public RatingFinanciero insertarRating(RatingFinanciero rating);

	/**
	 * Inserta una nueva instancia de una evaluacion financiera.
	 * 
	 * @param eval - instancia de la evaluacion financiera
	 */
	public void insertarEvaluacionFinanciera(EvaluacionFinanciera eval);
	
	/**
	 * Busca una matriz financiera por banca y segmento.<br>
	 * Se toman encuenta solo las matrices que esten vigente(fecha efectiva no sea nula y fecha final no sea null).<br>
	 * Este metodo solo carga la informacion que forma parte de la 'cabecera' de la matriz financiera.
	 * 
	 * @param idBanca
	 *            - identificador de la banca.
	 * @param idSegmento
	 *            - identificador del segmento.
	 * @return - instancia de matriz financiera.
	 */
	public MatrizFinanciera buscarMatrizFinanciera(Integer idBanca, Integer idSegmento);

	public MatrizFinanciera buscarMatrizFinancieraPorId(Long idMatriz);
	
	/**
	 * Busca todos los temas de una matriz financiera.<br>
	 * 
	 * @param idMatriz
	 *            - identificador de la matriz.
	 * @return - listado de {@link TemaFinanciero}. En caso de no encontrar temas se devuelve una instancia vacia.
	 */
	public List buscarTemasMatrizFinanciera(Long idMatriz, Date fechaRating, Integer estadoRating);

	/**
	 * Busca todos los indiciadores financieros asociados a un tema.
	 * 
	 * @param idMatriz
	 *            - identificador de la matriz financiera.
	 * @param idTema
	 *            - identificador del tema financiero.
	 * @return - listado de {@link IndicadorFinanciero}. En caso de no encontrar indicadores se devuelve una instancia vacia.
	 */
	public List buscarIndicadoresFinancieros(Long idMatriz, Long idTema, Date fechaRating, Integer estado);
	
	/**
	 * Busca el rango para el valor del indicador financiero pasado como parametro.<br>
	 * Retorna null en caso de no encontrar registro.
	 * 
	 * @param idMatriz
	 *            - identificador de la matriz.
	 * @param idTema
	 *            - identificador del tema.
	 * @param codigoCta
	 *            - codigo de la cuenta.
	 * @param valor
	 *            - valor de la cuenta.
	 * @param indiceMin
	 *            - corresponde a la nota menor de un rango especifico, en caso de ser null tomará la nota minima.
	 * @param indiceMax
	 *            - corresponde a la nota mayor de un rango especifico, en caso de ser null tomará la nota maxima.
	 * @return - instancia de {@link RangoNotaFinanciera}.
	 */
	public RangoNotaFinanciera buscarRangoNota(Long idMatriz, Long idTema, Long codigoCta, Double valor, Integer notaInferior, Integer notaSuperior, Date fechaRating, Integer estado);

	/**
	 * Crea una instancia del rating proyectado
	 */
	public RatingFinanciero guardarProyectado(RatingFinanciero ratingProy);
	
	/**
	 * Actualiza los valores correspondientes al rating proyectado
	 */
	public RatingFinanciero actualizarProyectado(RatingFinanciero ratingProy);
	
	/**
	 * Actualiza la informacion del rating financiero.
	 * 
	 * @param ratingFinanciero
	 *            - instancia del rating financiero.
	 * @return - instancia del rating financiero.
	 */
	public RatingFinanciero actualizarFinanciero(RatingFinanciero ratingFinanciero);
	
	public Vaciado cambiarProyectadoAVigente(Long idVaciado);
	
	/**
	 * Busca el rango de nota por PK.
	 * 
	 * @param idRangoNota
	 * @param idMatriz
	 * @param idTema
	 * @param codCuenta
	 * @return
	 */
	public RangoNotaFinanciera buscarRangoNota(Long idRangoNota, Long idMatriz, Long idTema, Long codCuentaId, Date fechaRating, Integer estado);
	
	/**
	 * Busca las ponderaciones de los periodos para la matriz financiera
	 * 
	 * @param idMatriz - identificador Long de la matriz financiera
	 * @return - lista de PonderacionMatrizFinanciera
	 */
	public List buscarPonderacionesMatriz(Long idMatriz);
	
	/**
	 * buscar el vaciado anterior independiente de la banca
	 * @param idVaciado - id del vaciado pivote
	 * @param limiteMeses - limite de meses minimo del estado financiero
	 * @return
	 */
	public List buscarVaciadosAnterioresGenerico(Long idVaciado);
	
	public RatingFinanciero actualizarEstadoFinanciero(RatingFinanciero rating);
}
