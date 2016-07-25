package com.bch.sefe.comun.rpt.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.bch.sefe.comun.ServicioReportes;
import com.bch.sefe.comun.impl.ServicioReportesImpl;
import com.bch.sefe.comun.rpt.ConstantesReportes;
import com.bch.sefe.comun.rpt.SEFEDataSource;
import com.bch.sefe.comun.rpt.SEFEReportePOI;
import com.bch.sefe.comun.srv.GestorClasificaciones;
import com.bch.sefe.comun.srv.GestorReportes;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.impl.GestorClasificacionesImpl;
import com.bch.sefe.comun.srv.impl.GestorReportesImpl;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.constructora.srv.GestorConstructoraInmobiliaria;
import com.bch.sefe.constructora.srv.impl.GestorConstructoraInmobilariaImpl;
import com.bch.sefe.rating.ConstantesRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.impl.GestorRatingFinancieroImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.vo.BalanceInmobiliario;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.TemaFinanciero;
import com.bch.sefe.util.FormatUtil;

public class RptRatingProyectadoInmobiliaria implements SEFEReportePOI {

	private Cliente cliente = null;
	private RatingIndividual ratingIndividual = null;
	private RatingFinanciero ratingFinancieroProy = null;
	private String logOpe = null;
	private List indicadores = new ArrayList();;

	private final static int INDEX_COLUMNA_ENCABEZADO_1 = 2;
	private final static int INDEX_COLUMNA_ENCABEZADO_2 = 5;
	private final static int INDEX_COLUMNA_ENCABEZADO_3 = 8;
	private final static int INDEX_COLUMNA_ENCABEZADO_4 = 11;
	private final static int INDEX_FILA_ENCABEZADO = 2;
	private final static int INDEX_COLUMNA_INDICADORES = 1;
	private final static int INDEX_FILA_INDICADORES = 14;
	private final static int INDEX_COLUMNA_TEMAS = 7;
	private final static int INDEX_FILA_TEMAS = 12;

	private ServicioReportes servicioRpt = new ServicioReportesImpl();

	public void setParametros(HSSFSheet planilla, Map parametros) {
		obtenerParametrosEntrada(parametros);
		obtenerInformacionReporte(planilla);
	}

	private void obtenerParametrosEntrada(Map parametros) {
		obtenerCliente((String) parametros.get(ConstantesReportes.RUT_CLIENTE));
		obtenerRatingIndividual(Long.valueOf((String) parametros.get(ConstantesReportes.ID_RATING_IND)));
		setLogOpe((String) parametros.get(ConstantesReportes.COD_USUARIO));
	}

	private void obtenerCliente(String rut) {
		GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();
		setCliente(gestorClientes.obtenerClientePorRut(rut));
	}

	private void obtenerRatingIndividual(Long idRtgInd) {
		GestorRatingIndividual gestorInd = new GestorRatingIndividualImpl();
		setRatingIndividual(gestorInd.buscarRatingIndividual(Long.valueOf(getCliente().getClienteId()), idRtgInd));
	}

	private void obtenerRatingFinancieroProyectado() {
		GestorRatingFinanciero gestorFinan = new GestorRatingFinancieroImpl();
		setRatingFinancieroProy(gestorFinan.obtenerRating(getRatingIndividual().getIdRatingProyectado()));
	}

	private void obtenerInformacionReporte(HSSFSheet planilla) {
		obtenerRatingFinancieroProyectado();
		obtenerIndicadoresBI();
		imprimirEncabezadoReporte(planilla);
		imprimirIndicadoresBI(planilla);
		imprimeTemasFinancieros(planilla);
	}

	private void obtenerIndicadoresBI() {
		GestorReportes gstRpt = new GestorReportesImpl();

		if (getRatingFinancieroProy() != null && getRatingFinancieroProy().getIdVaciado0() != null) {
			setIndicadores(gstRpt.getValorNotaPesoPercVaciado(getRatingFinancieroProy().getIdRating(), getRatingFinancieroProy().getIdVaciado0()));
		}
	}
	
	private BalanceInmobiliario getBalanceInmobiliario() {
		GestorConstructoraInmobiliaria gestorInmob = new GestorConstructoraInmobilariaImpl();
		BalanceInmobiliario balanceI = gestorInmob.obtenerBi(Long.valueOf(getCliente().getClienteId()),  getRatingIndividual().getFechaAvance());
		return balanceI;
	}

	private void imprimirEncabezadoReporte(HSSFSheet planilla) {
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO, INDEX_COLUMNA_ENCABEZADO_1, getCliente().getNombreCliente());
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 1, INDEX_COLUMNA_ENCABEZADO_1, getCliente().getRut());
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 2, INDEX_COLUMNA_ENCABEZADO_1, obtenerNombreClasificacion(getCliente().getSubSectorId()));
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 3, INDEX_COLUMNA_ENCABEZADO_1, getRatingFinancieroProy().getResponsable());
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 4, INDEX_COLUMNA_ENCABEZADO_1, FormatUtil.formatDate(new Date()));
		// Dato "Reporte"
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 5, INDEX_COLUMNA_ENCABEZADO_1, "-");

		// Flag idioma
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO, INDEX_COLUMNA_ENCABEZADO_2, "-");
		// Informacion
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 1, INDEX_COLUMNA_ENCABEZADO_2, "-");
		// Unidad
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 2, INDEX_COLUMNA_ENCABEZADO_2, "-");
		// Moneda
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 3, INDEX_COLUMNA_ENCABEZADO_2, "-");
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 4, INDEX_COLUMNA_ENCABEZADO_2, FormatUtil.castBigDecimal(getRatingIndividual().getIdBanca()));
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 5, INDEX_COLUMNA_ENCABEZADO_2, obtenerNombreClasificacion(getRatingIndividual().getIdBanca()));

		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO, INDEX_COLUMNA_ENCABEZADO_3, "-");
		GestorRatingFinanciero gstFinan = new GestorRatingFinancieroImpl();		
		MatrizFinanciera matriz = gstFinan.obtenerMatrizFinanciera(getRatingFinancieroProy().getIdMatriz());
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 1, INDEX_COLUMNA_ENCABEZADO_3, FormatUtil.castBigDecimal(matriz.getPorcentajeAjuste()));
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 2, INDEX_COLUMNA_ENCABEZADO_3, FormatUtil.castBigDecimal(getRatingFinancieroProy().getNotaFinanciera()));
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 3, INDEX_COLUMNA_ENCABEZADO_3, 
				(!getRatingIndividual().getEstado().equalsIgnoreCase(ConstantesRating.CLASIF_RATING_EN_CURSO) 
				&& getRatingIndividual().getFechaCambioEstado() != null ) ? FormatUtil.formatDate(getRatingIndividual().getFechaCambioEstado()) : null);
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 4, INDEX_COLUMNA_ENCABEZADO_3, obtenerNombreClasificacion(getRatingIndividual().getIdEstado()));
		// Segmento
		BalanceInmobiliario balancence = getBalanceInmobiliario();
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO , INDEX_COLUMNA_ENCABEZADO_4, FormatUtil.formatDate(balancence.getFechaAvance()));
		
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 1, INDEX_COLUMNA_ENCABEZADO_4, FormatUtil.formatDate(balancence.getFechaAvance()));
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 2, INDEX_COLUMNA_ENCABEZADO_4, balancence.getDeuSbif());
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 3, INDEX_COLUMNA_ENCABEZADO_4, FormatUtil.formatDate(balancence.getFechaAvance()));
		servicioRpt.insertarValorEnCelda(planilla, 11, 4, FormatUtil.formatDate(balancence.getFechaAvance()));
		servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_ENCABEZADO + 4, INDEX_COLUMNA_ENCABEZADO_4, balancence.getUf());
		
	}
	

	private void imprimirIndicadoresBI(HSSFSheet planilla) {
		for(int i=0; i < getIndicadores().size(); i++){
			IndicadorCuentaRpt indicador = (IndicadorCuentaRpt) getIndicadores().get(i);
			servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_INDICADORES + i, INDEX_COLUMNA_INDICADORES, indicador.getCodCuenta());
			servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_INDICADORES + i, INDEX_COLUMNA_INDICADORES + 1, FormatUtil.castBigDecimal(indicador.getPesoRelativo()));
			servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_INDICADORES + i, INDEX_COLUMNA_INDICADORES + 2, FormatUtil.castBigDecimal(indicador.getValorCuenta()));
			servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_INDICADORES + i, INDEX_COLUMNA_INDICADORES + 3, FormatUtil.castBigDecimal(indicador.getNotaCuenta()));
		}
	}
	
	private void imprimeTemasFinancieros(HSSFSheet planilla) {
		GestorRatingFinanciero gstFinan = new GestorRatingFinancieroImpl();
		List temas = gstFinan.obtenerTemasFinancieros(getRatingFinancieroProy().getIdMatriz(), getRatingFinancieroProy().getFechaRating(), getRatingIndividual().getIdEstado());
		for(int i = 0; i < temas.size() ; i++){
			TemaFinanciero tema = (TemaFinanciero) temas.get(i);
			servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_TEMAS + i, INDEX_COLUMNA_TEMAS, FormatUtil.castBigDecimal(tema.getIdTema()));
			servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_TEMAS + i, INDEX_COLUMNA_TEMAS + 1, tema.getTema());
			servicioRpt.insertarValorEnCelda(planilla, INDEX_FILA_TEMAS + i, INDEX_COLUMNA_TEMAS + 2, FormatUtil.castBigDecimal(tema.getPonderacion()));
		}
	}

	/**
	 * Obtiene el nombre de una calsificacion
	 * 
	 * @param idCategoria
	 * @return
	 */
	private String obtenerNombreClasificacion(Integer idCategoria) {
		GestorClasificaciones gestorClasif = new GestorClasificacionesImpl();

		Clasificacion clas = gestorClasif.buscarClasificacionPorId(idCategoria);

		return clas.getNombre();
	}

	public Map crearDataSources(Map parametros) {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public Map crearImagenes() {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public SEFEDataSource obtenerDataSource() {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setRatingIndividual(RatingIndividual ratingIndividual) {
		this.ratingIndividual = ratingIndividual;
		if (ratingIndividual == null) {
			this.ratingIndividual = new RatingIndividual();
		}
	}

	public RatingIndividual getRatingIndividual() {
		return ratingIndividual;
	}

	public void setRatingFinancieroProy(RatingFinanciero ratingFinancieroProy) {
		this.ratingFinancieroProy = ratingFinancieroProy;
		if (ratingFinancieroProy == null) {
			this.ratingFinancieroProy = new RatingFinanciero();
		}
	}

	public RatingFinanciero getRatingFinancieroProy() {
		return ratingFinancieroProy;
	}

	public void setLogOpe(String logOpe) {
		this.logOpe = logOpe;
	}

	public String getLogOpe() {
		return logOpe;
	}

	/**
	 * @param indicadores el indicadores a establecer
	 */
	public void setIndicadores(List indicadores) {
		this.indicadores = indicadores;
	}

	/**
	 * @return el indicadores
	 */
	public List getIndicadores() {
		return indicadores;
	}

}
