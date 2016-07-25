package com.bch.sefe.rating.srv.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.srv.AlgoritmoRatingGrupal;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.rating.vo.CalculoRatingGrupal;
import com.bch.sefe.rating.vo.CalculoRatingGrupalPyME;
import com.bch.sefe.rating.vo.IntegranteRatingGrupo;

public class AlgoritmoRatingGrupalPyMEImpl implements AlgoritmoRatingGrupal {

	private Logger log = Logger.getLogger(AlgoritmoRatingGrupal.class);
	
	private static final int INDICE_REDISTRIBUCION_PERSONAS = 0;
	private static final int INDICE_REDISTRIBUCION_EMPRESAS = 1;

	public CalculoRatingGrupal calcular(Object parametros) {
		CalculoRatingGrupalPyME resultadoCalculo = new CalculoRatingGrupalPyME();
		ArrayList personas = new ArrayList();
		ArrayList empresas = new ArrayList();
		ArrayList socios = new ArrayList();

		Map mapParametros = (Map) parametros;
		List relacionados = (List) mapParametros.get(PARAM_RELACIONADOS);
		Map notasEquivalentesPjeSiebel = (Map) mapParametros.get(PARAM_NOTAS_EQUIVALENTES_PJE_SIEBEL);

		String esComportamientoRojo = (String) mapParametros.get(COLOR_RELACIONADO_EMP_MADRE);
		if (log.isDebugEnabled()) {
			log.debug("Iniciando proceso de calculo de rating grupal");
		}

		// Se separan los relacionados que operan como empresa de los que operan como personas
		separarRelacionados(relacionados, empresas, personas, socios);

		// Se obtiene la cantidad de socios que operan como empresa y la cantidad de empresas sin rating individual
		Integer nroSociosOperanComoEmpresa = getCantidadSociosOperanComoEmpresa(socios);
		Integer nroEmpresasSinRatingInd = getCantidadEmpresasSinRatingInd(empresas);
		Double participacionSociosRojo = getParticipacionSociosRojo(socios);

		resultadoCalculo.setSociosOperanComoEmpresa(nroSociosOperanComoEmpresa);
		resultadoCalculo.setEmpresasSinRatingInd(nroEmpresasSinRatingInd);
		resultadoCalculo.setParticipacionSociosEnRojo(participacionSociosRojo);

		if (log.isDebugEnabled()) {
			log.debug("Nro de Socios que operan como empresa es: " + nroSociosOperanComoEmpresa);
			log.debug("Nro de Empresas sin Rating Individual: " + nroEmpresasSinRatingInd);
		}

		// Se obtiene la minima nota de comportamiento de todas las empresas que no tienen rating individuales
		Double minNotaComportEmpSinRatInd = getMinimoNotasCompEmpresasSinRatingInd(empresas, notasEquivalentesPjeSiebel);

		resultadoCalculo.setMinNotaComportamientoEmpresasSinRtgInd(minNotaComportEmpSinRatInd);

		if (log.isDebugEnabled()) {
			log.debug("La minima nota de comportamiento de las 'Empresas sin Rating Individual' es: " + minNotaComportEmpSinRatInd);
		}

		// Se obtiene el monto de ventas mas alto de los relacionados que participan como empresa. Esto es utilizado
		// en caso que la empresa madre tenga ventas CERO.
		Double maxVentasOperanEmpresa = getMaxVentasEmpresas(empresas);
		
		// Se calculan las notas de rating 'parciales' por personas y empresas
		Double notaPersonasNat = calcularNotaPersonas(personas);
		Double notaEmpresas = calcularNotaEmpresas(empresas, maxVentasOperanEmpresa);

		resultadoCalculo.setNotaPersonasNat(notaPersonasNat);
		resultadoCalculo.setNotaEmpresa(notaEmpresas);

		if (log.isDebugEnabled()) {
			log.debug("La Nota de Personas Natulares es: " + notaPersonasNat);
			log.debug("La Nota de Empresas es: " + notaEmpresas);
		}

		// Se calcula el ajuste que se debera aplicar sobre la nota de rating de grupo empresas
		Double ajusteXEmpresasSinRtg = calcularAjustePorEmpresaSinRating(resultadoCalculo);
		Double notaEmpresasConAjusteXEmprSinRtg = new Double(notaEmpresas.doubleValue() + ajusteXEmpresasSinRtg.doubleValue());

		resultadoCalculo.setAjusteEmpresasSinRating(ajusteXEmpresasSinRtg);
		resultadoCalculo.setNotaEmpresaConAjuste(notaEmpresasConAjusteXEmprSinRtg);

		if (log.isDebugEnabled()) {
			log.debug("El ajuste por 'Empresas Sin Rating Individual' a aplicar sobre la Nota de Empresas es: " + ajusteXEmpresasSinRtg);
			log.debug("Nota Empresas, luego del ajuste por 'Empresas Sin Rating Individual' es: " + notaEmpresasConAjusteXEmprSinRtg);
		}

		// Se realiza la redistribucion de participacion socios con giro
		List redistribuciones = redistribuirParticipacionSocioConGiroPersonas(socios);

		resultadoCalculo.setRedistribucionEmpresas((Double) redistribuciones.get(INDICE_REDISTRIBUCION_EMPRESAS));
		resultadoCalculo.setRedistribucionPersonas((Double) redistribuciones.get(INDICE_REDISTRIBUCION_PERSONAS));

		if (log.isDebugEnabled()) {
			log.debug("La redistribucion para Personas es: " + resultadoCalculo.getRedistribucionPersonas());
			log.debug("La redistribucion para Empresas es: " + resultadoCalculo.getRedistribucionEmpresas());
		}

		Double pyeMayor6ConAjuste = calcularPyEMayor6ConAjuste(resultadoCalculo);

		resultadoCalculo.setPyeMayor6ConAjuste(pyeMayor6ConAjuste);

		if (log.isDebugEnabled()) {
			log.debug("El resultado del calculo 'P y E > 6 c/ajuste' es: " + pyeMayor6ConAjuste);
		}

		Double ratingGrupal = calcularNotaGrupoSinAjustes(resultadoCalculo);

		resultadoCalculo.setRatingGrupalSinAjustes(ratingGrupal);

		// Se obtiene la nota aplicando el primer ajuste. Esto es utilizando la nota de empresas con ajuste por empresas sin rating ind.
		ratingGrupal = aplicarPrimerAjuste(resultadoCalculo);

		resultadoCalculo.setRatingGrupalPrimerAjuste(ratingGrupal);

		ratingGrupal = aplicarSegundoAjuste(resultadoCalculo);

		resultadoCalculo.setRatingGrupalSegundoAjuste(ratingGrupal);

		ratingGrupal = aplicarTercerAjuste(resultadoCalculo);

		resultadoCalculo.setRatingGrupalTercerAjuste(ratingGrupal);

		ratingGrupal = aplicarCuartoAjuste(resultadoCalculo,esComportamientoRojo);
		
		if (log.isInfoEnabled()) {
			log.info("Rating Grupal PyME previo a redondeo: " + ratingGrupal);
		}
		
		// 20130103.jlm: Se aplica ajuste que redondea en medio punto, segÃºn cambios solicitados.
		BigDecimal ratingGrupalRedondeado = RatingUtil.redondearRatingGrupalEnMedioPunto(new BigDecimal(ratingGrupal.doubleValue()));

		ratingGrupal = new Double(ratingGrupalRedondeado.doubleValue());
		
		if (log.isInfoEnabled()) {
			log.info("Rating Grupal PyME despues del redondeo: " + ratingGrupal);
		}
		
		resultadoCalculo.setRatingGrupalCuartoAjuste(ratingGrupal);
		resultadoCalculo.setRatingGrupalFinal(resultadoCalculo.getRatingGrupalCuartoAjuste());
		//Sprint 3 7.4.2 ajuste de nota minima
		Double notaMinima = RatingUtil.getNotaMinima(new Integer(4202), ConstantesSEFE.TIPO_RATING_GRUPAL, resultadoCalculo.getRatingGrupalFinal());
		if (resultadoCalculo.getRatingGrupalFinal() != null && resultadoCalculo.getRatingGrupalFinal().compareTo(notaMinima) < 0) {
			resultadoCalculo.setRatingGrupalFinal(notaMinima);
		}
		resultadoCalculo.setFechaCalculo(new Date());

		if (log.isDebugEnabled()) {
			log.debug("El Rating Grupal sin aplicar ningun ajuste es: " + resultadoCalculo.getRatingGrupalSinAjustes());
			log.debug("El Rating Grupal con Primer Ajuste es: " + resultadoCalculo.getRatingGrupalPrimerAjuste());
			log.debug("El Rating Grupal con Segundo Ajuste es: " + resultadoCalculo.getRatingGrupalSegundoAjuste());
			log.debug("El Rating Grupal con Tercer Ajuste es: " + resultadoCalculo.getRatingGrupalTercerAjuste());
			log.debug("El Rating Grupal con Cuarto Ajuste es: " + resultadoCalculo.getRatingGrupalCuartoAjuste());
		}

		return resultadoCalculo;
	}

	private Double getMaxVentasEmpresas(List operanEmpresas) {
		Double maxVentas = Double.valueOf("1");
		
		for (int i = 0; i < operanEmpresas.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) operanEmpresas.get(i);
			
			if (!ConstantesSEFE.ID_RELACION_EMPRESA_MADRE.equals(relacionado.getIdRelacion()) && relacionado.getVentas() != null && relacionado.getVentas().doubleValue() > maxVentas.doubleValue()) {
				maxVentas = relacionado.getVentas();
			}
		}
		
		return maxVentas;
	}
	
	/**
	 * método que devuelve el minimo de las ventas de los relacionados de las lista de los que operan como empresa
	 * @param operanEmpresas lista de relacionados que operan como empresa
	 * @return Double valor del minimo de ventas
	 */
	private Double getMinVentasEmpresas(List operanEmpresas) {
		Double minVentas = null;
		for (int i = 0; i < operanEmpresas.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) operanEmpresas.get(i);
			if (!ConstantesSEFE.ID_RELACION_EMPRESA_MADRE.equals(relacionado.getIdRelacion()) && relacionado.getVentas() != null && relacionado.getVentas().doubleValue() > 0.0d) {
				if (minVentas == null) {
					minVentas = relacionado.getVentas();
				}
				if (relacionado.getVentas().doubleValue() < minVentas.doubleValue()) {
					minVentas = relacionado.getVentas();
				}
			}
		}
		if (minVentas == null) {
			minVentas = ConstantesSEFE.DOUBLE_CERO;
		}
		return minVentas;
	}
	
	/**
	 * método que devuelve la sumatoria de las ventas de los relacionados de las lista de los que operan como empresa
	 * @param operanEmpresas lista de relacionados que operan como empresa
	 * @return Double valor de sumatoria de ventas
	 */
	private Double getSumaVentasEmpresa(List operanEmpresas) {
		double sumVentas = 0.0d;
		for (int i = 0; i < operanEmpresas.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) operanEmpresas.get(i);
			if (!ConstantesSEFE.ID_RELACION_EMPRESA_MADRE.equals(relacionado.getIdRelacion()) && relacionado.getVentas() != null) {
				sumVentas += relacionado.getVentas().doubleValue();
			}
		}
		return new Double(sumVentas);
	}
	
	private Double getVentaEmpresaMadre(List operanEmpresas, Double maxVentasOperanEmpresa) {
		Double ventaEmpresaMadre = maxVentasOperanEmpresa;
		for (int i = 0; i < operanEmpresas.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) operanEmpresas.get(i);
			if (ConstantesSEFE.ID_RELACION_EMPRESA_MADRE.equals(relacionado.getIdRelacion())) {
				if (relacionado.getVentas() != null && relacionado.getVentas().doubleValue() > 0.00) {
					ventaEmpresaMadre = relacionado.getVentas();
				}
				break;
			}
		}
		return ventaEmpresaMadre;
	}
	
	/*
	 * Carga las listas operanEmpresa, operanPersona con los relacionados que operan como empresa y persona respectivamente y en otra agrega las
	 * empresas (sin importar que operen como empresa o persona) que son socios.
	 */
	private void separarRelacionados(List todosRelacionados, List operanEmpresa, List operanPersona, List socios) {
		for (int i = 0; i < todosRelacionados.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) todosRelacionados.get(i);

			if (relacionado.operaComoEmpresa()) {
				operanEmpresa.add(relacionado);
			} else {
				operanPersona.add(relacionado);
			}

			if (ConstantesSEFE.ID_RELACION_SOCIO.equals(relacionado.getIdRelacion())) {
				socios.add(relacionado);
			}
		}
	}

	/*
	 * Calcula la nota de rating grupo para las personas naturales sin giro. Requisitos: - Todos deben tener nota de comportamiento. - La suma de los
	 * porcentajes de participacion debe estar entre un 90% y 100%. - Deben tener porcentaje de participacion.
	 */
	private Double calcularNotaPersonas(List personas) {
		BigDecimal totalPrcParticipacion = ConstantesSEFE.BIG_DECIMAL_CERO;

		// Se suman los porcentajes de participacion de cada persona
		for (int i = 0; i < personas.size(); i++) {
			IntegranteRatingGrupo persona = (IntegranteRatingGrupo) personas.get(i);
			BigDecimal prcParticipacionPersona = new BigDecimal(persona.getPrcParticipacion().doubleValue());

			// Se obtiene el total de porcentaje de participacion
			totalPrcParticipacion = totalPrcParticipacion.add(prcParticipacionPersona);
		}

		BigDecimal prcPartSobreTotalPart = null;
		BigDecimal notaPersona = null;
		BigDecimal notaRatingPersonas = null;
		for (int i = 0; i < personas.size(); i++) {
			IntegranteRatingGrupo persona = (IntegranteRatingGrupo) personas.get(i);

			BigDecimal rtgComportamiento = ConstantesSEFE.BIG_DECIMAL_CERO;

			if (persona.getRatingComportamiento() != null) {
				rtgComportamiento = new BigDecimal(persona.getRatingComportamiento().doubleValue());
			}

			BigDecimal prcParticipacionPersona = new BigDecimal(persona.getPrcParticipacion().doubleValue());

			try {
				// Se utiliza 1 decimal en este calculo siguiendo ejemplo de excel
				prcPartSobreTotalPart = prcParticipacionPersona.divide(totalPrcParticipacion, BigDecimal.ROUND_HALF_UP, 1);
			} catch(Exception ex) {
				/*
				 * marias - 20121019
				 * en caso de error, la ponderacion es cero y no aporta a la nota de personas
				 */ 
				prcPartSobreTotalPart = ConstantesSEFE.BIG_DECIMAL_CERO; 
			}
			
			
			notaPersona = prcPartSobreTotalPart.multiply(rtgComportamiento);
			notaPersona.setScale(2, BigDecimal.ROUND_HALF_UP);

			if (notaRatingPersonas == null) {
				notaRatingPersonas = ConstantesSEFE.BIG_DECIMAL_CERO;
			}

			notaRatingPersonas = notaRatingPersonas.add(notaPersona);
		}

		return (notaRatingPersonas != null ? new Double(notaRatingPersonas.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()) : ConstantesSEFE.DOUBLE_CERO);
	}

	/*
	 * Calcula la nota de rating grupo para las empresas. Requisitos: - Empresa Madre debe tener un porcentaje de participacion del 100%, Nivel de
	 * Ventas y Rating Individual. - Deben tener nota de comportamiento. - Deben tener porcentaje de participacion. - Deben tener rating individual y
	 * Ventas.
	 */
	private Double calcularNotaEmpresas(List empresas, final Double maxVentasOperanEmpresa) {
		BigDecimal sumaPrcPartPorVentas = ConstantesSEFE.BIG_DECIMAL_CERO;
		HashMap prcPartPorVentasEmpresa = new HashMap();
		Double ventaEmpresaMadre = this.getVentaEmpresaMadre(empresas, maxVentasOperanEmpresa);

		// Se suman las ventas de las empresas
		for (int i = 0; i < empresas.size(); i++) {
			IntegranteRatingGrupo empresa = (IntegranteRatingGrupo) empresas.get(i);
			BigDecimal prcPartPorVentas = ConstantesSEFE.BIG_DECIMAL_CERO;

			// jlmanriquez.20130315 - Incidencia P24. Se pide que cuando empresa madre tenga ventas 0 se utilice el monto maximo de ventas
			// de los relacionados que operan como empresa y en caso de no existir relacionados que operen como empresa se deja las ventas en 1.
			// Segun lo conversado con erioseco esto se validara cuando las ventas = 0 o ventas = null
			if (ConstantesSEFE.ID_RELACION_EMPRESA_MADRE.equals(empresa.getIdRelacion())) {
				if (empresa.getVentas() == null || empresa.getVentas().doubleValue() == 0.00) {
					empresa.setVentas(maxVentasOperanEmpresa);
				}
			}
			// criterio de ventas para cada empresa sprint 2 req: 7.4.9 formula
			BigDecimal ventasEmpresa = null;
			if (empresa.getRatingIndividual() != null) {
				if (empresa.getVentas() == null || empresa.getVentas().doubleValue() == 0) {
					Double sumVentas = getSumaVentasEmpresa(empresas);
					if (sumVentas.doubleValue() == 0.0d) {
						ventasEmpresa = new BigDecimal(ventaEmpresaMadre.doubleValue());
					} else {
						if (empresa.getRatingIndividual().doubleValue() >= 5) {
							ventasEmpresa = new BigDecimal(this.getMinVentasEmpresas(empresas).doubleValue());
						} else {
							ventasEmpresa = new BigDecimal(maxVentasOperanEmpresa.doubleValue());
						}
					}
				} else {
					ventasEmpresa = new BigDecimal(empresa.getVentas().doubleValue());
				}
			} else {
				continue;
			}
			//Calcula de participacion por ventas
			BigDecimal prcParticipacion = new BigDecimal(empresa.getPrcParticipacion().doubleValue()).divide(ConstantesSEFE.BIG_DECIMAL_CIEN, 2, BigDecimal.ROUND_HALF_UP);
			// Prc. Part * Ventas
			prcPartPorVentas = ventasEmpresa.multiply(prcParticipacion).setScale(0, BigDecimal.ROUND_HALF_UP);

			// Se guarda el (Prc.Part. * Ventas) obtenido por cada empresa
			prcPartPorVentasEmpresa.put(empresa.getRutRelacionado(), prcPartPorVentas);

			// Suma(Prc. Part * Ventas)
			sumaPrcPartPorVentas = sumaPrcPartPorVentas.add(prcPartPorVentas);
		}

		// Se pondera cada venta por la suma de las ventas antes calculada y se multiplica por la nota de
		// rating individual
		BigDecimal totalNotaEmpresas = null;
		for (int i = 0; i < empresas.size(); i++) {
			IntegranteRatingGrupo empresa = (IntegranteRatingGrupo) empresas.get(i);

			// ((Prc. Part * Ventas) / Sumatoria(Prc. Part * Ventas)) * Rating Ind.
			if (empresa.getRatingIndividual() != null) {
				BigDecimal ratingIndividual = new BigDecimal(empresa.getRatingIndividual().doubleValue());
				BigDecimal prcPartPorVentas = (BigDecimal) prcPartPorVentasEmpresa.get(empresa.getRutRelacionado());
				// (Prc. Part * Ventas) / Sumatoria(Prc. Part * Ventas)
				BigDecimal ponderacion = prcPartPorVentas.divide(sumaPrcPartPorVentas, 3, BigDecimal.ROUND_HALF_UP);

				BigDecimal notaFinalEmpresa = ponderacion.multiply(ratingIndividual);
				notaFinalEmpresa = notaFinalEmpresa.setScale(2, BigDecimal.ROUND_HALF_UP);

				if (totalNotaEmpresas == null) {
					totalNotaEmpresas = ConstantesSEFE.BIG_DECIMAL_CERO;
				}

				// Sumatoria(notaFinaEmpresa)
				totalNotaEmpresas = totalNotaEmpresas.add(notaFinalEmpresa);
			}
		}

		return (totalNotaEmpresas != null ? new Double(totalNotaEmpresas.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()) : ConstantesSEFE.DOUBLE_CERO);
	}

	/*
	 * Suma las participaciones de todos los socios que se encuentran con el comportamiento actual en Rojo.
	 */
	private Double getParticipacionSociosRojo(List socios) {
		double sumaPrcPart = 0.0;

		for (int i = 0; i < socios.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) socios.get(i);

			// Si es un socio con comportamiento actual en rojo
			if (ConstantesSEFE.KEY_RTG_COMP_PJE_ROJO.equalsIgnoreCase(relacionado.getFlagComportamientoActual())) {
				sumaPrcPart += relacionado.getPrcParticipacion().doubleValue();
			}
		}

		return new Double(sumaPrcPart);
	}

	/*
	 * Cuenta la cantidad de SOCIOS que operan como empresa.
	 */
	private Integer getCantidadSociosOperanComoEmpresa(List relacionados) {
		int sociosOperanEmpresa = 0;

		for (int i = 0; i < relacionados.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) relacionados.get(i);

			if (ConstantesSEFE.ID_RELACION_SOCIO.equals(relacionado.getIdRelacion()) && relacionado.operaComoEmpresa()) {
				sociosOperanEmpresa++;
			}
		}

		return new Integer(sociosOperanEmpresa);
	}

	/*
	 * Obtiene la nota de comportamiento dentro de las empresas que no tengan rating individuales
	 */
	private Double getMinimoNotasCompEmpresasSinRatingInd(List empresas, Map notasEquivalentesPjeSiebel) {
		Double minimaNotaComportamiento = null;

		for (int i = 0; i < empresas.size(); i++) {
			IntegranteRatingGrupo empresa = (IntegranteRatingGrupo) empresas.get(i);

			// Se busca la minima nota de comportamiento de las empresas SIN rating individual y tiene nota de comportamiento equivalente al pje siebel
			if (empresa.getRatingIndividual() == null && notasEquivalentesPjeSiebel.containsKey(empresa.getRutRelacionado())) {
				Double notaComportamiento = (Double) notasEquivalentesPjeSiebel.get(empresa.getRutRelacionado());
				
				// Si la empresa actual tiene una nota de comportamiento menor a la guardada en minimaNotaComportamiento se reemplaza
				if (notaComportamiento != null && (minimaNotaComportamiento == null || minimaNotaComportamiento.compareTo(notaComportamiento) > 0)) {
					minimaNotaComportamiento = new Double(notaComportamiento.doubleValue());
				}
			}
		}
		return minimaNotaComportamiento;
	}

	/*
	 * Resuelve el la parte del algoritmo P y E > 6 c/ajuste
	 */
	private Double calcularPyEMayor6ConAjuste(CalculoRatingGrupalPyME calculo) {
		BigDecimal ajuste = ConstantesSEFE.BIG_DECIMAL_CERO;

		if (calculo.haySociosQueOperanComoEmpresa() && calculo.hayEmpresasSinRatingIndividual()) {
			BigDecimal bdNotaEmpresaConAjuste = new BigDecimal(calculo.getNotaEmpresaConAjuste().doubleValue());
			BigDecimal ponderacionEmpresa = getPonderacionEmpresas();
			BigDecimal bdPorcentajeRedistribucionEmpresa = new BigDecimal(calculo.getRedistribucionEmpresas().doubleValue()).divide(ConstantesSEFE.BIG_DECIMAL_CIEN, DECIMALES_PARA_CALCULO, BigDecimal.ROUND_HALF_UP);

			// Ponderacion_Empresa(70%) + Porcentaje_Redistribucion_Empresa
			BigDecimal pondEmpresaMasPorcRedistribuido = ponderacionEmpresa.add(bdPorcentajeRedistribucionEmpresa);
			ajuste = bdNotaEmpresaConAjuste.multiply(pondEmpresaMasPorcRedistribuido);
		} else if (calculo.hayEmpresasSinRatingIndividual()) {
			ajuste = new BigDecimal(calculo.getNotaEmpresaConAjuste().doubleValue());
		} else if (calculo.haySociosQueOperanComoEmpresa()) {
			BigDecimal bdNotaSinAjuste = new BigDecimal(calculo.getNotaEmpresa().doubleValue());
			BigDecimal ponderacionEmpresa = getPonderacionEmpresas();
			BigDecimal bdPorcentajeRedistribucionEmpresa = new BigDecimal(calculo.getRedistribucionEmpresas().doubleValue());

			// Ponderacion_Empresa(70%) + Porcentaje_Redistribucion_Empresa
			BigDecimal pondEmpresaMasPorcRedistribuido = ponderacionEmpresa.add(bdPorcentajeRedistribucionEmpresa)
					.divide(ConstantesSEFE.BIG_DECIMAL_CIEN, BigDecimal.ROUND_HALF_UP);
			ajuste = bdNotaSinAjuste.multiply(pondEmpresaMasPorcRedistribuido);
		}

		return new Double(ajuste.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	/*
	 * Redistribuye la participacion de las personas que son SOCIOS.
	 * Las redistribuciones son retornadas en la lista:
	 * 0 - Personas
	 * 1 - Empresas
	 */
	private List redistribuirParticipacionSocioConGiroPersonas(List socios) {
		BigDecimal sumatoriaParticipacion = ConstantesSEFE.BIG_DECIMAL_CERO;
		ArrayList redistribuciones = new ArrayList();

		// Se realiza la sumatoria de las participaciones
		for (int i = 0; i < socios.size(); i++) {
			IntegranteRatingGrupo socio = (IntegranteRatingGrupo) socios.get(i);

			BigDecimal participacion = new BigDecimal(socio.getPrcParticipacion().doubleValue());

			sumatoriaParticipacion = sumatoriaParticipacion.add(participacion);
		}

		BigDecimal redistribucionPersonas = ConstantesSEFE.BIG_DECIMAL_CERO;
		BigDecimal redistribucionEmpresas = ConstantesSEFE.BIG_DECIMAL_CERO;
		for (int i = 0; i < socios.size(); i++) {
			IntegranteRatingGrupo socio = (IntegranteRatingGrupo) socios.get(i);

			BigDecimal participacion = new BigDecimal(socio.getPrcParticipacion().doubleValue());
			// Participacion_Socio / Sumatoria(Participaciones_Socios)
			BigDecimal ponderacionSocio = participacion.divide(sumatoriaParticipacion, DECIMALES_PARA_CALCULO, BigDecimal.ROUND_HALF_UP);

			BigDecimal ponderacionPersonas = getPonderacionPersonas();
			// Ponderacion_Persona(30%) * ponderacionSocio
			BigDecimal redistribucion = ponderacionSocio.multiply(ponderacionPersonas);

			// Se realiza sumatoria de redistribucion segun si opera como empresa o persona
			if (socio.operaComoEmpresa()) {
				redistribucionEmpresas = redistribucionEmpresas.add(redistribucion);
			} else {
				redistribucionPersonas = redistribucionPersonas.add(redistribucion);
			}
		}

		redistribucionEmpresas = redistribucionEmpresas.multiply(ConstantesSEFE.BIG_DECIMAL_CIEN);
		redistribucionEmpresas = redistribucionEmpresas.setScale(1, BigDecimal.ROUND_HALF_UP);
		
		redistribucionPersonas = redistribucionPersonas.multiply(ConstantesSEFE.BIG_DECIMAL_CIEN);
		redistribucionPersonas = redistribucionPersonas.setScale(1, BigDecimal.ROUND_HALF_UP);
		
		redistribuciones.add(INDICE_REDISTRIBUCION_PERSONAS, new Double(redistribucionPersonas.doubleValue()));
		redistribuciones.add(INDICE_REDISTRIBUCION_EMPRESAS, new Double(redistribucionEmpresas.doubleValue()));
		
		return redistribuciones;
	}

	/*
	 * Cuenta la cantidad de EMPRESAS que no tienen rating individual.
	 */
	private Integer getCantidadEmpresasSinRatingInd(List empresas) {
		int nroEmpresasSinRatingInd = 0;

		for (int i = 0; i < empresas.size(); i++) {
			IntegranteRatingGrupo empresa = (IntegranteRatingGrupo) empresas.get(i);

			if (empresa.getRatingIndividual() == null) {
				nroEmpresasSinRatingInd++;
			}
		}

		return new Integer(nroEmpresasSinRatingInd);
	}

	private Double calcularAjustePorEmpresaSinRating(CalculoRatingGrupalPyME calculo) {
		Double ajuste = ConstantesSEFE.DOUBLE_CERO;
		Double limInferior = ConfigDBManager.getValueAsDouble(ConstantesSEFE.LIMITE_INFERIOR_NOTA_MINIMA_COMPORTAMIENTO_RATING_GRUPAL_PYME);
		Double limSuperior = ConfigDBManager.getValueAsDouble(ConstantesSEFE.LIMITE_SUPERIOR_NOTA_MINIMA_COMPORTAMIENTO_RATING_GRUPAL_PYME);

		Double minCastigo = ConfigDBManager.getValueAsDouble(ConstantesSEFE.CASTIGO_MINIMO_NOTA_EMPRESAS_POR_EMPRESAS_SIN_RATING);
		Double maxCastigo = ConfigDBManager.getValueAsDouble(ConstantesSEFE.CASTIGO_MAXIMO_NOTA_EMPRESAS_POR_EMPRESAS_SIN_RATING);
		
		if (calculo.getMinNotaComportamientoEmpresasSinRtgInd() != null
				&& calculo.getMinNotaComportamientoEmpresasSinRtgInd().doubleValue() > limSuperior.doubleValue()) {
			return minCastigo;
		} else if (calculo.getMinNotaComportamientoEmpresasSinRtgInd() != null
				&& limInferior.doubleValue() <= calculo.getMinNotaComportamientoEmpresasSinRtgInd().doubleValue()
				&& calculo.getMinNotaComportamientoEmpresasSinRtgInd().doubleValue() <= limSuperior.doubleValue()) {
			return maxCastigo;
		}

		return ajuste;
	}

	/*
	 * Calcula la nota de grupo antes de aplicar cualquier ajuste.
	 */
	private Double calcularNotaGrupoSinAjustes(CalculoRatingGrupalPyME calculo) {
		BigDecimal pondPersonas = getPonderacionPersonas();
		BigDecimal pondEmpresas = getPonderacionEmpresas();

		if (calculo.getNotaPersonasNat() == null || calculo.getNotaPersonasNat().equals(ConstantesSEFE.DOUBLE_CERO)) {
			return calculo.getNotaEmpresa();
		}

		BigDecimal bdNotaGrpPersonas = new BigDecimal(calculo.getNotaPersonasNat().doubleValue());
		BigDecimal bdNotaGrpEmpresas = new BigDecimal(calculo.getNotaEmpresa().doubleValue());

		bdNotaGrpPersonas = bdNotaGrpPersonas.multiply(pondPersonas);
		bdNotaGrpEmpresas = bdNotaGrpEmpresas.multiply(pondEmpresas);

		BigDecimal notaGrupoSinAjuste = bdNotaGrpEmpresas.add(bdNotaGrpPersonas);
		notaGrupoSinAjuste = notaGrupoSinAjuste.setScale(2, BigDecimal.ROUND_HALF_UP);

		return new Double(notaGrupoSinAjuste.doubleValue());
	}

	/*
	 * Aplica el primer ajuste. El primer ajuste consiste en verificar si existen empresas sin rating individual y en caso de ser asi se realiza el
	 * mismo calculo que en calcularNotaGrupoSinAjustes pero utilizando la nota empresas con el ajuste por 'empresas sin rating individual'.
	 */
	private Double aplicarPrimerAjuste(CalculoRatingGrupalPyME calculo) {

		// En caso que no hayan empresas sin rating individual, se mantiene la nota de rating grupal sin ajustes
		if (!calculo.hayEmpresasSinRatingIndividual()) {
			return calculo.getRatingGrupalSinAjustes();
		}
		
		BigDecimal bdNotaGrpPersonas = new BigDecimal(calculo.getNotaPersonasNat().doubleValue());
		// Aca se utiliza la nota empresa + ajuste por 'empresas sin rating individual'
		BigDecimal bdNotaGrpEmpresas = new BigDecimal(calculo.getNotaEmpresaConAjuste().doubleValue());

		if (bdNotaGrpPersonas.doubleValue() > 0.0d) {
			bdNotaGrpPersonas = bdNotaGrpPersonas.multiply(getPonderacionPersonas());
			bdNotaGrpEmpresas = bdNotaGrpEmpresas.multiply(getPonderacionEmpresas());
		} else {
			BigDecimal ponderacion = new BigDecimal(ConstantesSEFE.BIG_DECIMAL_CIEN.doubleValue()).divide(ConstantesSEFE.BIG_DECIMAL_CIEN, DECIMALES_PARA_CALCULO, BigDecimal.ROUND_HALF_UP);
			bdNotaGrpEmpresas = bdNotaGrpEmpresas.multiply(ponderacion);
		}
		BigDecimal notaGrupoSinAjuste = bdNotaGrpEmpresas.add(bdNotaGrpPersonas);
		notaGrupoSinAjuste = notaGrupoSinAjuste.setScale(2, BigDecimal.ROUND_HALF_UP);

		// Se retorna resultado truncado segun excel
		return new Double(notaGrupoSinAjuste.doubleValue());
	}

	private Double aplicarSegundoAjuste(CalculoRatingGrupalPyME calculo) {
		BigDecimal notaEmpresa;

		if (calculo.hayEmpresasSinRatingIndividual() && calculo.haySociosQueOperanComoEmpresa()) {
			// Se utiliza la nota empresa con ajuste por 'Empresas Sin Rating Individual'
			notaEmpresa = new BigDecimal(calculo.getNotaEmpresaConAjuste().doubleValue());
		} else if (calculo.haySociosQueOperanComoEmpresa()) {
			// Se utiliza la nota empresas sin ajuste
			notaEmpresa = new BigDecimal(calculo.getNotaEmpresa().doubleValue());
		} else {
			// La nota del segundo ajuste sera la misma que la del primer ajuste
			return calculo.getRatingGrupalPrimerAjuste();
		}

		// ((NOTA PERSONAS x (PONDERACION PERSONAS - PORCENTAJE DISTRIBUIDO PERS.)) + (NOTA EMPRESA[CASTIGADA o SIN CASTIGO] x (PONDERACION EMPRESA +
		// PORCENTAJE
		// DISTRIBUIDO PERS.)))
		BigDecimal notaPersonas = new BigDecimal(calculo.getNotaPersonasNat().doubleValue());
		BigDecimal pondPersonas = getPonderacionPersonas();
		BigDecimal prcRedistEmpresa = new BigDecimal(calculo.getRedistribucionEmpresas().doubleValue()).divide(ConstantesSEFE.BIG_DECIMAL_CIEN, DECIMALES_PARA_CALCULO, BigDecimal.ROUND_HALF_UP);
		BigDecimal pondEmpresas = getPonderacionEmpresas();

		BigDecimal restaPondPers = pondPersonas.subtract(prcRedistEmpresa);
		BigDecimal operandoA = notaPersonas.multiply(restaPondPers);

		BigDecimal sumaPondEmpr = pondEmpresas.add(prcRedistEmpresa);
		BigDecimal operandoB = notaEmpresa.multiply(sumaPondEmpr);

		// Se trunca resultado a dos decimales segun excel
		return new Double(operandoA.add(operandoB).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}

	private Double aplicarTercerAjuste(CalculoRatingGrupalPyME calculo) {
		double limite = ConfigDBManager.getValueAsDouble(ConstantesSEFE.LIMITE_NOTA_PERSONAS_NATURALES_TERCER_AJUSTE).doubleValue();
		
		// Si hay empresas sin rating individual y la nota minima de las notas de comportamiento de las empresas sin rating es distinto de cero
		if (calculo.hayEmpresasSinRatingIndividual()
				|| (calculo.getMinNotaComportamientoEmpresasSinRtgInd() != null && calculo.getMinNotaComportamientoEmpresasSinRtgInd().doubleValue() != 0)) {
			if (calculo.getNotaPersonasNat().doubleValue() >= limite && calculo.getPyeMayor6ConAjuste().doubleValue() >= limite) {
				return calculo.getPyeMayor6ConAjuste();
			} else {
				return calculo.getRatingGrupalSegundoAjuste();
			}
		} else if (calculo.getNotaPersonasNat().doubleValue() >= limite && calculo.getNotaEmpresa().doubleValue() >= limite) {
			return calculo.getNotaEmpresa();
		} else {
			return calculo.getRatingGrupalSegundoAjuste();
		}
	}

	private Double aplicarCuartoAjuste(CalculoRatingGrupalPyME calculo, String esComportamientoRojo) {
		double limiteParticipacion = ConfigDBManager.getValueAsDouble(ConstantesSEFE.LIMITE_PORCENTAJE_PARTICIPACION_SOCIOS_EN_ROJO_RATING_GRUPAL_PYME).doubleValue();

		// Si la participacion de los socios que tienen comportamiento actual en rojo es mayor al limite
		esComportamientoRojo = (esComportamientoRojo==null)?"":esComportamientoRojo;
		if (calculo.getParticipacionSociosEnRojo().doubleValue() > limiteParticipacion || (esComportamientoRojo.equals(String.valueOf(true)))) {
			ArrayList notasOrdenadas = new ArrayList();
			notasOrdenadas.add(calculo.getRatingGrupalSinAjustes());
			notasOrdenadas.add(calculo.getRatingGrupalPrimerAjuste());
			notasOrdenadas.add(calculo.getRatingGrupalSegundoAjuste());
			notasOrdenadas.add(calculo.getRatingGrupalTercerAjuste());

			Collections.sort(notasOrdenadas);

			Double minima = (Double) notasOrdenadas.get(0);

			Double limNotaRating = ConfigDBManager.getValueAsDouble(ConstantesSEFE.LIMITE_NOTA_RATING_CUARTO_AJUSTE);
			if (minima.doubleValue() >= limNotaRating.doubleValue()) {
				return limNotaRating;
			} else {
				return minima;
			}
		}

		// En caso que no se cumpla ninguna de las condiciones anteriores se retorna el tercer ajuste
		return calculo.getRatingGrupalTercerAjuste();
	}

	private BigDecimal getPonderacionEmpresas() {
		Integer idPlantilla = (Integer) SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		Double ponderacion = ConfigDBManager.getValueAsDouble(ConstantesSEFE.PONDERACION_EMPRESAS_RATING_GRUPAL, idPlantilla);
		return new BigDecimal(ponderacion.doubleValue()).divide(ConstantesSEFE.BIG_DECIMAL_CIEN, DECIMALES_PARA_CALCULO, BigDecimal.ROUND_HALF_UP);
	}

	private BigDecimal getPonderacionPersonas() {
		Integer idPlantilla = (Integer) SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		Double ponderacion = ConfigDBManager.getValueAsDouble(ConstantesSEFE.PONDERACION_PERSONAS_RATING_GRUPAL, idPlantilla);
		return new BigDecimal(ponderacion.doubleValue()).divide(ConstantesSEFE.BIG_DECIMAL_CIEN, DECIMALES_PARA_CALCULO, BigDecimal.ROUND_HALF_UP);
	}
}
