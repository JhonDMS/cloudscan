package com.bch.sefe.comun.srv;

import com.bch.sefe.rating.vo.CalculoRatingGrupal;

public interface AlgoritmoRatingGrupal {
	public static final String PARAM_RELACIONADOS = "rel";
	public static final String PARAM_NOTAS_EQUIVALENTES_PJE_SIEBEL = "notasEquivPjeSiebel";
	
	//BCHC-JRF
	public static final String COLOR_RELACIONADO_EMP_MADRE = "colorEmpresaMadre";
	
	public static final int DECIMALES_PARA_CALCULO = 10;
	
	/**
	 * Metodo encargado de realizar el calculo del rating grupal. Cada implementacion de la interfaz agregara la logica pertinente.
	 * 
	 * @param parametros
	 * @return
	 */
	public CalculoRatingGrupal calcular(Object parametros);
}
