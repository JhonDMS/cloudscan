/**
 * 
 */
package com.bch.sefe.rating.vo;

import java.util.Date;

/**
 * @author Raul Astudillo
 *
 */
public class Caritas {
	
	private Long idParteInvolucrada;
	private Date fechaEvaluacion;
	
	private Double puntajeFinal;
	private Double puntajePonderado;
	private String descripcion;
	private boolean deServicio;
	
	/**
	 * @return the deServicio
	 */
	public boolean isDeServicio() {
		return deServicio;
	}
	/**
	 * @param deServicio the deServicio to set
	 */
	public void setDeServicio(boolean deServicio) {
		this.deServicio = deServicio;
	}
	/**
	 * @return el idParteInvolucrada
	 */
	public Long getIdParteInvolucrada() {
		return idParteInvolucrada;
	}
	/**
	 * @param idParteInvolucrada el idParteInvolucrada a establecer
	 */
	public void setIdParteInvolucrada(Long idParteInvolucrada) {
		this.idParteInvolucrada = idParteInvolucrada;
	}
	/**
	 * @return el fechaEvaluacion
	 */
	public Date getFechaEvaluacion() {
		return fechaEvaluacion;
	}
	/**
	 * @param fechaEvaluacion el fechaEvaluacion a establecer
	 */
	public void setFechaEvaluacion(Date fechaEvaluacion) {
		this.fechaEvaluacion = fechaEvaluacion;
	}
	/**
	 * @return el puntajeFinal
	 */
	public Double getPuntajeFinal() {
		return puntajeFinal;
	}
	/**
	 * @param puntajeFinal el puntajeFinal a establecer
	 */
	public void setPuntajeFinal(Double puntajeFinal) {
		this.puntajeFinal = puntajeFinal;
	}
	/**
	 * @return el puntajePonderado
	 */
	public Double getPuntajePonderado() {
		return puntajePonderado;
	}
	/**
	 * @param puntajePonderado el puntajePonderado a establecer
	 */
	public void setPuntajePonderado(Double puntajePonderado) {
		this.puntajePonderado = puntajePonderado;
	}
	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}
	/**
	 * @param descripcion el descripcion a establecer
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
