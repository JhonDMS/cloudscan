package com.bch.sefe.rating.vo;

import java.util.Date;

/**
 * Clase que representa toda la informacion que compone la cabecera de una matriz financiera.
 * 
 * @author jlmanriq
 * 
 */
public class MatrizFinanciera {
	private Date fechaEfectiva;
	private Double ponderacionNoAnual0;
	private Double ponderacionNoAnual1;
	private Double ponderacionNoAnual2;
	private Long idUsuario;
	private Date fechaFin;
	private Double notaTope;
	private Double porcentajeAjuste;
	private Integer idBanca;
	private Integer idSegmento;
	private Long idMatriz;
	private Double ponderacionAnual0;
	private Double ponderacionAnual1;
	private Double ponderacionAnual2;
	private Long idMatrizProy;
	private boolean existePonderacion;
	private boolean esProyectado;
	private Integer estadoId;
	
	/**
	 * @return the estadoId
	 */
	public Integer getEstadoId() {
		return estadoId;
	}

	/**
	 * @param estadoId the estadoId to set
	 */
	public void setEstadoId(Integer estadoId) {
		this.estadoId = estadoId;
	}

	/**
	 * @return the esProyectado
	 */
	public boolean esProyectado() {
		return esProyectado;
	}

	/**
	 * @param esProyectado the esProyectado to set
	 */
	public void setEsProyectado(boolean esProyectado) {
		this.esProyectado = esProyectado;
	}

	/**
	 * Obtiene la nota correspondiente para el codigo de cuenta(indicador) y su valor.
	 *
	 * @param valor
	 * @param codCuenta
	 * @return
	 */
	public Double obtenerNota(Double valor, String codCuenta) {
		Double nota = new Double(0.0);

		return nota;
	}

	/**
	 * Busca la ponderacion correspondiente para el codigo de cuenta.
	 * 
	 * @param codCuenta
	 *            - String con el codigo de cuenta.
	 * @return - Instancia de Double con el valor de la ponderacion.
	 */
	public Double obtenerPonderacionIndicador(String codCuenta) {
		Double ponderacion = new Double(0.0);

		return ponderacion;
	}
	
	public Double obtenerPonderacionActual() {
		Double ponderacion = new Double(0.0);
		
		return ponderacion;
	}

	/**
	 * @return el fechaEfectiva
	 */
	public Date getFechaEfectiva() {
		return fechaEfectiva;
	}

	/**
	 * @param fechaEfectiva el fechaEfectiva a establecer
	 */
	public void setFechaEfectiva(Date fechaEfectiva) {
		this.fechaEfectiva = fechaEfectiva;
	}

	/**
	 * @return el ponderacion0
	 */
	public Double getPonderacionNoAnual0() {
		return ponderacionNoAnual0;
	}

	/**
	 * @param ponderacion0 el ponderacion0 a establecer
	 */
	public void setPonderacionNoAnual0(Double ponderacionNoAnual0) {
		this.ponderacionNoAnual0 = ponderacionNoAnual0;
	}

	/**
	 * @return el ponderacion1
	 */
	public Double getPonderacionNoAnual1() {
		return ponderacionNoAnual1;
	}

	/**
	 * @param ponderacion1 el ponderacion1 a establecer
	 */
	public void setPonderacionNoAnual1(Double ponderacionNoAnual1) {
		this.ponderacionNoAnual1 = ponderacionNoAnual1;
	}

	/**
	 * @return el ponderacion2
	 */
	public Double getPonderacionNoAnual2() {
		return ponderacionNoAnual2;
	}

	/**
	 * @param ponderacion2 el ponderacion2 a establecer
	 */
	public void setPonderacionNoAnual2(Double ponderacionNoAnual2) {
		this.ponderacionNoAnual2 = ponderacionNoAnual2;
	}

	public Double getPonderacionAnual0() {
		return ponderacionAnual0;
	}

	public void setPonderacionAnual0(Double ponderacionAnual0) {
		this.ponderacionAnual0 = ponderacionAnual0;
	}

	public Double getPonderacionAnual1() {
		return ponderacionAnual1;
	}

	public void setPonderacionAnual1(Double ponderacionAnual1) {
		this.ponderacionAnual1 = ponderacionAnual1;
	}

	public Double getPonderacionAnual2() {
		return ponderacionAnual2;
	}

	public void setPonderacionAnual2(Double ponderacionAnual2) {
		this.ponderacionAnual2 = ponderacionAnual2;
	}

	/**
	 * @return el idUsuario
	 */
	public Long getIdUsuario() {
		return idUsuario;
	}

	/**
	 * @param idUsuario el idUsuario a establecer
	 */
	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	/**
	 * @return el fechaFin
	 */
	public Date getFechaFin() {
		return fechaFin;
	}

	/**
	 * @param fechaFin el fechaFin a establecer
	 */
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	/**
	 * @return el notaTope
	 */
	public Double getNotaTope() {
		return notaTope;
	}

	/**
	 * @param notaTope el notaTope a establecer
	 */
	public void setNotaTope(Double notaTope) {
		this.notaTope = notaTope;
	}

	/**
	 * @return el porcentajeAjuste
	 */
	public Double getPorcentajeAjuste() {
		return porcentajeAjuste;
	}

	/**
	 * @param porcentajeAjuste el porcentajeAjuste a establecer
	 */
	public void setPorcentajeAjuste(Double porcentajeAjuste) {
		this.porcentajeAjuste = porcentajeAjuste;
	}

	/**
	 * @return el idBanca
	 */
	public Integer getIdBanca() {
		return idBanca;
	}

	/**
	 * @param idBanca el idBanca a establecer
	 */
	public void setIdBanca(Integer idBanca) {
		this.idBanca = idBanca;
	}

	/**
	 * @return el idSegmento
	 */
	public Integer getIdSegmento() {
		return idSegmento;
	}

	/**
	 * @param idSegmento el idSegmento a establecer
	 */
	public void setIdSegmento(Integer idSegmento) {
		this.idSegmento = idSegmento;
	}

	public Long getIdMatriz() {
		return idMatriz;
	}

	public void setIdMatriz(Long idMatriz) {
		this.idMatriz = idMatriz;
	}

	/**
	 * @return el idMatrizProy
	 */
	public Long getIdMatrizProy() {
		return idMatrizProy;
	}

	/**
	 * @param idMatrizProy el idMatrizProy a establecer
	 */
	public void setIdMatrizProy(Long idMatrizProy) {
		this.idMatrizProy = idMatrizProy;
	}

	public boolean isExistePonderacion() {
		return existePonderacion;
	}

	public void setExistePonderacion(boolean existePonderacion) {
		this.existePonderacion = existePonderacion;
	}
}
