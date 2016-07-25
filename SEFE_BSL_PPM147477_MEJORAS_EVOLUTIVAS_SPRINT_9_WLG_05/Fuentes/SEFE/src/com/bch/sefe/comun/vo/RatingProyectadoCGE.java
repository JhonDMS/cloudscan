package com.bch.sefe.comun.vo;

import java.util.ArrayList;
import java.util.List;

import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.vaciados.vo.Vaciado;

public class RatingProyectadoCGE extends RatingProyectado {
	private RatingFinanciero ratingFinanciero;
	private PeriodoRating periodo0;
	private PeriodoRating periodo1;
	private PeriodoRating periodo2;
	private PeriodoRating periodoProy;
	private List warnings;
	private List flags;
	private String modoConsulta;
	private boolean matrizVigente;
	
	/**
	 * @return the matrizVigente
	 */
	public boolean isMatrizVigente() {
		return matrizVigente;
	}

	/**
	 * @param matrizVigente the matrizVigente to set
	 */
	public void setMatrizVigente(boolean matrizVigente) {
		this.matrizVigente = matrizVigente;
	}

	public RatingProyectadoCGE() {
		this.ratingFinanciero = new RatingFinanciero();
		this.warnings = new ArrayList();
	}
	
	public Vaciado getVaciadoCierreAnual() {
		if (this.periodo2 != null && this.periodo1 != null) {
			return this.getPeriodo1().getVaciado();
		} else if (this.periodo2 == null && this.periodo0 != null) {
			return this.getPeriodo0().getVaciado();
		} else {
			return null;
		}
	}
	
	public void agregarMensaje(String msg) {
		this.warnings.add(msg);
	}
	
	public RatingProyectadoCGE(RatingFinanciero rating) {
		this.ratingFinanciero = rating;
		this.warnings = new ArrayList();
	}
	
	public Long getIdRating() {
		return this.ratingFinanciero.getIdRating();
	}

	public void setIdRating(Long id) {
		this.ratingFinanciero.setIdRating(id);
	}

	/**
	 * @return el periodo0
	 */
	public PeriodoRating getPeriodo0() {
		return periodo0;
	}

	/**
	 * @param periodo0 el periodo0 a establecer
	 */
	public void setPeriodo0(PeriodoRating periodo0) {
		this.periodo0 = periodo0;
	}

	/**
	 * @return el periodo1
	 */
	public PeriodoRating getPeriodo1() {
		return periodo1;
	}

	/**
	 * @param periodo1 el periodo1 a establecer
	 */
	public void setPeriodo1(PeriodoRating periodo1) {
		this.periodo1 = periodo1;
	}

	/**
	 * @return el periodo2
	 */
	public PeriodoRating getPeriodo2() {
		return periodo2;
	}

	/**
	 * @param periodo2 el periodo2 a establecer
	 */
	public void setPeriodo2(PeriodoRating periodo2) {
		this.periodo2 = periodo2;
	}

	/**
	 * @return el periodoProy
	 */
	public PeriodoRating getPeriodoProy() {
		return periodoProy;
	}

	/**
	 * @param periodoProy el periodoProy a establecer
	 */
	public void setPeriodoProy(PeriodoRating periodoProy) {
		this.periodoProy = periodoProy;
	}

	/**
	 * @return el ratingFinanciero
	 */
	public RatingFinanciero getRatingFinanciero() {
		return ratingFinanciero;
	}

	/**
	 * @param ratingFinanciero el ratingFinanciero a establecer
	 */
	public void setRatingFinanciero(RatingFinanciero ratingFinanciero) {
		this.ratingFinanciero = ratingFinanciero;
	}

	/**
	 * @return el warnings
	 */
	public List getWarnings() {
		return warnings;
	}

	public void setFlags(List flags) {
		this.flags = flags;
	}

	public List getFlags() {
		return flags;
	}

	/**
	 * @return el modoConsulta
	 */
	public String getModoConsulta() {
		return modoConsulta;
	}

	/**
	 * @param modoConsulta el modoConsulta a establecer
	 */
	public void setModoConsulta(String modoConsulta) {
		this.modoConsulta = modoConsulta;
	}

}
