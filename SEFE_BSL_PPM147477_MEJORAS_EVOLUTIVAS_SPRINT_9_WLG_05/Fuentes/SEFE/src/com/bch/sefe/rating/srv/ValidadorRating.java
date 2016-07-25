package com.bch.sefe.rating.srv;

import java.util.Date;

import com.bch.sefe.rating.vo.ValidacionRatingFinanciero;
import com.bch.sefe.vaciados.vo.Vaciado;

public interface ValidadorRating {
	public ValidacionRatingFinanciero validarIngreso(Long idCliente, Long idRtgInd, Long IdVac, Integer idBanca);

	public ValidacionRatingFinanciero validarModificacion(Long idCliente, Long idRtgInd, Long IdVac, Integer idBanca);
	
	public ValidacionRatingFinanciero validarReporte(Long idCliente, Long idRtgInd, Long IdVac, Integer idBanca);
	
	public boolean vaciadoEsVigente(Vaciado vac, Integer idBanca);
	
	public boolean versionVaciadoValida(Vaciado vac);
	
	public Boolean soportaUnicoVaciadoParaRtgFinanciero(Integer idPlantilla) ;
	
	public boolean validarIvas(Date fechaConfirmacion);
}
