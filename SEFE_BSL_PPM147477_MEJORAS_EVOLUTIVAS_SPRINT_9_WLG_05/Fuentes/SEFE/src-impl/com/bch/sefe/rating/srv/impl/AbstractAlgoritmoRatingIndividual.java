package com.bch.sefe.rating.srv.impl;

import java.math.BigDecimal;
import java.util.Date;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.AlgoritmoRatingIndividual;
import com.bch.sefe.rating.CatalogoRating;
import com.bch.sefe.rating.impl.CatalogoRatingImpl;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.Segmento;
import com.bch.sefe.servicios.impl.MessageManager;

public abstract class AbstractAlgoritmoRatingIndividual implements AlgoritmoRatingIndividual {

	private static final int BANCA_CONSTRUCTORA = 4204;;
	
	private static final int BANCA_INMOBILIARIA = 4203;

	public RatingIndividual calcularRating(Long idParteInvol, Long idRatingInd) {
		GestorRating gestorRating = new GestorRatingImpl();
		GestorRatingIndividual gestorRatingInd = new GestorRatingIndividualImpl();

		RatingIndividual ratingIndividual = gestorRatingInd.buscarRatingIndividual(idParteInvol, idRatingInd);
		
		CatalogoRating catalogoRating = new CatalogoRatingImpl();
		catalogoRating.verificarSiComponentesRatingConfirmado(ratingIndividual);
		Integer tpoSegmento = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_INDIVIDUAL_TPO_SEGMENTO, ratingIndividual.getIdBanca());
		// recupera el valor de ventas/compras o cualquier otro concepto para poder segmentar.
		Double ventasCompras = (ratingIndividual.getIdRatingFinanciero() != null  || ratingIndividual.getIdRatingProyectado() != null ) ? getNivelVentasPatrimonios(ratingIndividual) : null;
		// se invoca para recuperar las ventas y se asigna el valor al rating individual
		
		Segmento segmento = gestorRating.buscarSegmento(tpoSegmento, ventasCompras, ratingIndividual.getIdBanca());

		if (segmento == null) {
			segmento = new Segmento();
			segmento.setIdBanca(ratingIndividual.getIdBanca());
			//throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.KEY_RATING_IND_MSG_NO_EXISTEN_SEGMENTOS_VENTAS));
		}

		if (!esPosibleCalcularRatingIndividual(ratingIndividual)) {
			if (((ratingIndividual.getIdBanca().intValue() == BANCA_INMOBILIARIA && null != ratingIndividual.getIdRatingProyectado() && ratingIndividual.getRtgProyectadoConfirmado().booleanValue()) ||
				(ratingIndividual.getIdBanca().intValue() == BANCA_CONSTRUCTORA && null != ratingIndividual.getIdRatingFinanciero() && ratingIndividual.getRtgFinancieroConfirmado().booleanValue())) &&
				ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO.equals(ratingIndividual.getIdEstado())) {
				
				getPonderacionRating(ratingIndividual, segmento);

				ratingIndividual.setRatingPreliminar1(null);
				ratingIndividual.setRatingPreliminar2(null); //BCH-CGN-20150109
				ratingIndividual.setPremioTamano(null);
				ratingIndividual.setRatingFinalSugerido(null);
				ratingIndividual.setRatingFinal(null);
				return ratingIndividual;
			} else {
				// si no se cumplen las condiciones se retorna el rating individual tal cual se encuentra en ese minuto.
				//se borran los porcentajes y notas de los rating que componen la nota modelo
				ratingIndividual.setPrcRatingFinanciero(null);
				ratingIndividual.setPrcRatingProyectado(null);
				ratingIndividual.setPrcRatingNegocio(null);
				ratingIndividual.setPrcRatingComportamiento(null);
				ratingIndividual.setRatingPreliminar1(null);
				ratingIndividual.setRatingPreliminar2(null); //BCH-CGN-20150109
				ratingIndividual.setPremioTamano(null);
				ratingIndividual.setRatingFinalSugerido(null);
				ratingIndividual.setRatingFinal(null);
				return ratingIndividual;
			}
		}
		
		BigDecimal notaRatingIndividual = getRatingPreliminar(ratingIndividual, segmento);
		
		notaRatingIndividual = aplicarLimites(ratingIndividual, notaRatingIndividual, ventasCompras);

		notaRatingIndividual = aplicarAjustePorTamano(ratingIndividual, notaRatingIndividual);
		notaRatingIndividual = aplicarConversionPorMatriz(ratingIndividual, notaRatingIndividual);
		notaRatingIndividual = aplicarLimitesAdicionales(ratingIndividual, notaRatingIndividual);
		if (ratingIndividual.getIdBanca().equals(ConstantesSEFE.BANCA_AGRICOLAS)){
			notaRatingIndividual = aplicarLimitesAgricola(ratingIndividual, notaRatingIndividual, ventasCompras);
			ratingIndividual.setRatingAproxFinal(new Double(notaRatingIndividual.doubleValue()));
			ratingIndividual.setRatingFinalSugerido(ratingIndividual.getRatingAproxFinal());
			ratingIndividual.setRatingFinal(ratingIndividual.getRatingAproxFinal());
		}
		//Sprint 3 7.4.2 ajuste de nota minima
		Double nota = new Double(notaRatingIndividual.doubleValue());
		Double notaMinima = RatingUtil.getNotaMinima(ratingIndividual.getIdBanca(), ConstantesSEFE.TIPO_RATING_INDIVIDUAL, nota);
		if (nota != null && nota.compareTo(notaMinima) < 0) {
			notaRatingIndividual = new BigDecimal(notaMinima.doubleValue());
			ratingIndividual.setRatingAproxFinal(new Double(notaRatingIndividual.doubleValue()));
			ratingIndividual.setRatingFinalSugerido(ratingIndividual.getRatingAproxFinal());
			ratingIndividual.setRatingFinal(ratingIndividual.getRatingAproxFinal());
		}
		ratingIndividual.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		ratingIndividual.setFechaModificacion(new Date());		

		return ratingIndividual;
	}
	private BigDecimal aplicarLimitesAgricola(RatingIndividual ratingIndividual, BigDecimal notaRating, Double nivelVentas){
		if (nivelVentas!=null && notaRating!= null){
			if (nivelVentas.doubleValue()>=0 && nivelVentas.doubleValue()<20){
				if (notaRating.doubleValue()>6){
					notaRating = new BigDecimal(6);
				}
			}else if (nivelVentas.doubleValue()>=20 && nivelVentas.doubleValue()<=40){
				if (notaRating.doubleValue()>7){
					notaRating = new BigDecimal(7);
				}
			}else if (nivelVentas.doubleValue()>40){
				if (notaRating.doubleValue()>10){
					notaRating = new BigDecimal(10);
				}
			}
		}
		return notaRating;
	}
	protected abstract boolean esPosibleCalcularRatingIndividual(RatingIndividual ratingIndividual);
	
	/**
	 * Este metodo retorna el nivel de ventas o Patrimonio convertido en MUF.
	 * 
	 * @param ratingIndividual
	 * @return
	 */
	protected abstract Double getNivelVentasPatrimonios(RatingIndividual ratingIndividual);
	
	
	/**
	 * Este metodo retorna el nivel de ventas o Patrimonio convertido en MUF.
	 * 
	 * @param ratingIndividual
	 * @return
	 */
	protected abstract Double getNivelVentas(RatingIndividual ratingIndividual);
	
	/**
	 * Este metodo retorna el nivel de patrionio .
	 * 
	 * @param ratingIndividual
	 * @return
	 */
	protected abstract Double getNivelPatrimonio(RatingIndividual ratingIndividual);

	/**
	 * Calcula el rating preliminar y guarda la nota del rating preliminar calculado en la instancia del rating individual.
	 * 
	 * @param ratingIndividual
	 * @param segmento
	 * @return
	 */
	protected abstract BigDecimal getRatingPreliminar(RatingIndividual ratingIndividual, Segmento segmento);

	
	/**
	 * Obtiene las ponderaciones de los rating para banca inmobiliario y contruccion
	 * 
	 * @param ratingIndividual
	 * @param segmento
	 * @return
	 */
	protected abstract void getPonderacionRating(RatingIndividual ratingIndividual, Segmento segmento);
	
	
	
	/**
	 * Aplica los limites en caso que apliquen.
	 * 
	 * @param ratingIndividual
	 * @param notaRating
	 * @return
	 */
	protected abstract BigDecimal aplicarLimites(RatingIndividual ratingIndividual, BigDecimal notaRating, Double nivelVentas);

	/**
	 * Aplica los premios por tamano.
	 * 
	 * @param ratingIndividual
	 * @param notaRating
	 * @return
	 */
	protected abstract BigDecimal aplicarAjustePorTamano(RatingIndividual ratingIndividual, BigDecimal notaRating);

	/**
	 * Aplica la conversion por matriz.
	 * 
	 * @param ratingIndividual
	 * @param notaRating
	 * @return
	 */
	protected abstract BigDecimal aplicarConversionPorMatriz(RatingIndividual ratingIndividual, BigDecimal notaRating);

	/**
	 * 
	 * @param ratingIndividual
	 * @param notaRating
	 * @return
	 */
	protected abstract BigDecimal aplicarLimitesAdicionales(RatingIndividual ratingIndividual, BigDecimal notaRating);
}
