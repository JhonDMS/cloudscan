package com.bch.sefe.rating.srv.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.srv.AlgoritmoRatingGrupal;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.vo.CalificadoraRGOHojaBanco;
import com.bch.sefe.rating.vo.IndicadorFinanciero;
import com.bch.sefe.rating.vo.IndicadorHojaBanco;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;

public class RatingUtil {
	private static Logger log = Logger.getLogger(RatingUtil.class);
	private static Integer FLAG_INDICADOR_OPCIONAL 	= new Integer(1);
	private static char CIERRE_ANUAL 	= 'A';
	private static char CIERRE_PARCIAL 	= 'P';
	

	/**
	 * Retorna la cadena que indica la combinacion de periodos utilizados
	 * dependiendo del numero y tipo de vaciados pasados como argumento
	 * 
	 * @param vaciados - lista de instancias de Vaciado en el orden de evaluacion para
	 * el rating financiero
	 * 
	 * @return - cadena con el texto que indica la combinacion.
	 * Ej: AAA - tres periodos anuales
	 *     AAP - dos periodos anuales y un parcial
	 *       p - un periodo parcial
	 */
	public static String getCombinacionPeriodos(List vaciados) {
		StringBuffer clave = new StringBuffer();
		
		if (vaciados == null || vaciados.isEmpty()) {
			return null;
		}
		
		// se eliminan los valores nulos desde la lista de vaciados
		for (int i = 0; i < vaciados.size(); ++i) {
			if (vaciados.get(i) == null) {
				vaciados.remove(i);
			}
		}
		
		Object[] arrayVac = vaciados.toArray();
		Arrays.sort(arrayVac, new RatingUtil.ComparadorVaciadoPorPeriodo());
		
		for (int i = 0; i < arrayVac.length; ++i) {
			Vaciado v = (Vaciado) arrayVac[i];
			
			if (v == null) {
				continue;
			}
			
			// es cierre anual??
			if (v.getMesesPer().intValue() == 12) {
				clave.append(CIERRE_ANUAL);
			} else {
				clave.append(CIERRE_PARCIAL);
			}
		}
		
		return clave.toString();
	}
	
	
	
	/**
	 * Ajusta las ponderaciones de los indicadores financieros dependiendo
	 * del valor del flagAjustado y el valor del indicador
	 * 
	 * @param indicadores - lista de instancias de IndicadorFinanciero de una matriz financiera
	 * @param cuentas - mapa de instancias de Cuenta, para un vaciado dado
	 * 
	 * @return - lista de indicadores ajustados
	 */
	public static List ajustarIndicadoresFinancieros(List indicadores, Map cuentas) {
		// se agrupan los indicadores por tema
		Map temas = agruparIndicadoresPorTema(new ArrayList(indicadores));
		
		// se buscan los temas cuyos indicadores hay que ajustar
		Map temasPorAjustar = buscarTemasPorAjustar(temas, cuentas);
		
		// se hace el ajuste de las ponderaciones de los indicadores
		List indicadoresAjustados = ajustarIndicadores(indicadores, temasPorAjustar);
		
		return indicadoresAjustados;
	}
	

	public static void main(String args[]) {
		Calendar cal = GregorianCalendar.getInstance();
		
		Vaciado v1 = new Vaciado();
		cal.set(Calendar.YEAR, 2009);
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 31);
		v1.setPeriodo(cal.getTime());
		v1.setMesesPer(new Integer(12));

		Vaciado v2 = new Vaciado();
		cal.set(Calendar.YEAR, 2010);
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 31);
		v2.setPeriodo(cal.getTime());
		v2.setMesesPer(new Integer(5));
		
		Vaciado v3 = new Vaciado();
		cal.set(Calendar.YEAR, 2008);
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 31);
		v3.setPeriodo(cal.getTime());
		v3.setMesesPer(new Integer(12));
		
		List vaciados = new ArrayList();
		vaciados.add(v1);
		vaciados.add(v2);
		vaciados.add(v3);
	}
	
	
	/* ====================================================================== */
	/*       M E T O D O S  Y  C O M P O N E N T E S   P R I V A D O S        */
	/* ====================================================================== */
	
	/*
	 * Este comparador se utiliza para ordenar los vaciados en orden decreciente
	 * antes de determinar las ponderaciones para cada uno de los vaciados
	 */
	private static class ComparadorVaciadoPorPeriodo implements Comparator {
		public int compare(Object o1, Object o2) {
			if (o2 == null) {
				return 0;
			}
			
			return ((Vaciado) o1).getPeriodo().compareTo(((Vaciado) o2).getPeriodo());
		}
	}
	
	
	/*
	 * Ajusta la ponderacion de los indicadores
	 */
	private static List ajustarIndicadores(List indicadores, Map temasPorAjustar) {
		Iterator it = temasPorAjustar.keySet().iterator();
		List indicadoresAjustados = new ArrayList();

		while (it.hasNext()) {
			List indPorAjustar = (List) temasPorAjustar.get(it.next());;
			if (indPorAjustar != null && !indPorAjustar.isEmpty()) {
				// se suma el total de las ponderaciones
				double suma = 0;
				for (int i = 0; i < indPorAjustar.size(); ++i) {
					IndicadorFinanciero ind = (IndicadorFinanciero) indPorAjustar.get(i);
					suma += ind.getPonderacion().doubleValue();
				}
				
				// se redistribuyen los porcentajes
				//BigDecimal k = new BigDecimal(100.0 - suma).divide(new BigDecimal(suma), 2, BigDecimal.ROUND_HALF_UP);
				double k =(100-suma)/suma;
				for (int i = 0; i < indPorAjustar.size(); ++i) {
					IndicadorFinanciero ind = (IndicadorFinanciero) indPorAjustar.get(i);
					//double newPonderacion = k.multiply(new BigDecimal(ind.getPonderacion().doubleValue())).doubleValue();
					double newPonderacion = k*ind.getPonderacion().doubleValue();
					newPonderacion = newPonderacion+ind.getPonderacion().doubleValue();
					BigDecimal roundPonderacion = (new BigDecimal(newPonderacion)).setScale(2, BigDecimal.ROUND_HALF_UP);
					ind.setPonderacion(new Double(roundPonderacion.doubleValue()));
				}
				
				// se acumulan los indicadores ajustados
				indicadoresAjustados.addAll(indPorAjustar);
			}
		}
		
		// y luego se suman los restantes no ajustados
		for (int i = 0; i < indicadores.size(); ++i) {
			IndicadorFinanciero ind = (IndicadorFinanciero) indicadores.get(i);
			
			// solamente los indicados de temas no ajustados
			if (!temasPorAjustar.containsKey(ind.getIdTema())) {
				indicadoresAjustados.add(ind);
			}
		}
		
		return indicadoresAjustados;
	}


	/*
	 * Busca los temas cuyos indicadores deben ajustar sus ponderaciones.
	 * Retorna un mapa indexado por tema, con la lista de los indicadores
	 * que se deben redistribuir
	 */
	private static Map buscarTemasPorAjustar(Map temas, Map cuentas) {
		Iterator it = temas.keySet().iterator();
		Map temasPorAjustar = new HashMap();
		while (it.hasNext()) {
			List indicadores = (List) temas.get(it.next());
			
			// se recorre la lista de indicadores para identificar
			// cuales se deben ajustar
			for (int i = 0; i < indicadores.size(); ++i) {
				IndicadorFinanciero ind = (IndicadorFinanciero) indicadores.get(i);
				
				String codCta = ind.getCodigoCuenta();
				// si el indicador es requerido y el valor del indicador es null
				// hay que ajustar las ponderaciones para los indicadores de ese tema
				Object object = (Object) cuentas.get(codCta);
				Double monto = null;
				if (object instanceof Cuenta) {
					monto = ((Cuenta)object).getMonto();
				}else if (object instanceof CalificadoraRGOHojaBanco){
					monto = ((CalificadoraRGOHojaBanco)object).getValor();
				}else if (object instanceof IndicadorHojaBanco){
					monto = ((IndicadorHojaBanco)object).getValor();
				}
				if (ind.getFlagOpcional().equals(FLAG_INDICADOR_OPCIONAL) && ((object == null || monto== null)) ) { 
					// el indicador se quita de la lista
					indicadores.remove(i);
					i--;
					// y se pone en el buffer de temas por ajustar
					temasPorAjustar.put(ind.getIdTema(), indicadores);
				}
					
			}
		}
		
		return temasPorAjustar;
	}
	
	
	/*
	 * Se agrupan los indicadores por tema
	 */
	private static Map agruparIndicadoresPorTema(List indicadores) {
		// los indicadores se agrupan por tema
		Map temas = new HashMap();
		Iterator it = indicadores.iterator();
		
		while (it.hasNext()) {
			IndicadorFinanciero ind = (IndicadorFinanciero) it.next();
			
			// se buscan en el buffer la lista de indicadores asociados al tema
			List listaInd = (List) temas.get(ind.getIdTema());
			
			// el tema es el primero
			// entonces se crea, y se pone en el buffer
			if (listaInd == null) {
				listaInd = new ArrayList();
				temas.put(ind.getIdTema(), listaInd);
			}
			
			// el indicador se pone en la lista del tema
			listaInd.add(ind);
		}
		
		return temas;
	}
	
	/**
	 * sprint 1 req 7.4.1  ;modificaci騨 del redondeo rating individual
	 * m閠odo para aproximar notas finales para su visualizaci髇
	 * @param nota a aproximar a un decimal
	 * @return restultado
	 */
	public static Double aproximarNota(Double nota) {
		Double notaFinal = nota;
		if (notaFinal != null) {
			BigDecimal notaBigDecimal = new BigDecimal(notaFinal.doubleValue());
			notaBigDecimal = notaBigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
			BigDecimal integer = new BigDecimal(notaBigDecimal.intValue());
			BigDecimal decimal = notaBigDecimal.subtract(integer);
			if (decimal.doubleValue() == 0.75d){
				notaBigDecimal = notaBigDecimal.setScale(1,BigDecimal.ROUND_UP);
			} else {
				notaBigDecimal = notaBigDecimal.setScale(1,BigDecimal.ROUND_HALF_UP);
			}
			notaFinal = new Double(notaBigDecimal.doubleValue());
		}
		return notaFinal;
	}
	
	/**
	 * Realiza una aproximaci贸n segun la regla de negocio descrita mas abajo. El ajuste es utilizado hasta la fecha de esta implementaci贸n, por los
	 * ratings Individuales <b>PyME</b><br>
	 * <ul>
	 * <li>Si parte decimal <= 0,25 -> se redondea al entero inferior</li>
	 * <li>Si parte decimal >= 0,75 -> se redondea al entero superior</li>
	 * <li>De lo contrario se aproxima al 'medio punto' (0,5)</li>
	 * </ul>
	 * 
	 * @param nota
	 * @return
	 */
	public static BigDecimal redondearRatingEnMedioPunto(BigDecimal nota) {
		BigDecimal integer = new BigDecimal(nota.intValue());
		BigDecimal decimal = nota.subtract(integer);
		decimal = decimal.setScale(3, BigDecimal.ROUND_DOWN);		//sprint 3 req 7.4.1  ;modificaci騨 del redondeo rating individual
		decimal = decimal.setScale(2, BigDecimal.ROUND_HALF_UP); 	//sprint 1 req 7.4.1  ;modificaci騨 del redondeo rating individual
		double d = decimal.doubleValue();
		//d = (d < 0.3) ? 0 : (d >= 0.7) ? 1 : 0.5; sprint 1 req 7.4.1  ;modificaci騨 del redondeo rating individual
		d = (d < 0.25) ? 0 : (d >= 0.75) ? 1 : 0.5;

		BigDecimal notaRatingAproxFinal = integer.add(new BigDecimal(d));

		return notaRatingAproxFinal;
	}
	
	/**
	 * Realiza una aproximaci贸n segun la regla de negocio descrita mas abajo. El ajuste es utilizado hasta la fecha de esta implementaci贸n, por los
	 * ratings grupales <b>PyME</b><br>
	 * <ul>
	 * <li>Si parte decimal <= 0,25 -> se redondea al entero inferior</li>
	 * <li>Si parte decimal >= 0,75 -> se redondea al entero superior</li>
	 * <li>De lo contrario se aproxima al 'medio punto' (0,5)</li>
	 * </ul>
	 * 
	 * @param nota
	 * @return
	 */
	public static BigDecimal redondearRatingGrupalEnMedioPunto(BigDecimal nota) {
		BigDecimal integer = new BigDecimal(nota.intValue());
		BigDecimal decimal = nota.subtract(integer);
		decimal = decimal.setScale(3, BigDecimal.ROUND_DOWN);		//sprint 3 req 7.4.1  ;modificaci騨 del redondeo rating individual
		decimal = decimal.setScale(2, BigDecimal.ROUND_HALF_UP); 	//sprint 1 req 7.4.1  ;modificaci騨 del redondeo rating individual
		double d = decimal.doubleValue();
		d = (d < 0.25) ? 0 : (d >= 0.75) ? 1 : 0.5;

		BigDecimal notaRatingAproxFinal = integer.add(new BigDecimal(d));

		return notaRatingAproxFinal;
	}
	
	/**
	 * Obtiene un vaciado, con el cual se obtendran las ventas del cliente. En primera instancia se busca se intenta obtener el vaciado utilizado por
	 * el rating financiero si es que existe, en caso contrario, se busca el vaciado que 'deber铆a' usar de manera automatica para el calculo del
	 * rating financiero.
	 * 
	 * @param ri
	 *            - instancia de {@link RatingIndividual} existente
	 * @return instancia de vaciado encontrado
	 */
	public static Vaciado buscarVaciado(RatingIndividual rtgInd) {
		Vaciado vac = null;

		// Primero si intenta obtener el vaciado utilizado desde el rating financiero si es que existe
		vac = getVaciadoRatingFinanciero(rtgInd);

		// Si no se obtuvo el vaciado desde el rating financiero, se obtiene el vaciado que se deber铆a utilizar en el rating financiero
		if (vac == null) {
			vac = getVaciadoDefault(rtgInd.getIdCliente(), rtgInd.getIdBanca());
		}

		return vac;
	}

	/*
	 * Retorna el vaciado utilizado en el rating financiero si es que existe
	 */
	private static Vaciado getVaciadoRatingFinanciero(RatingIndividual ri) {
		Vaciado vac = null;

		if (ri.getIdRatingFinanciero() != null) {
			GestorRatingFinanciero gestRtgFin = new GestorRatingFinancieroImpl();
			GestorVaciados gv = new GestorVaciadosImpl();

			RatingFinanciero rf = gestRtgFin.obtenerRating(ri.getIdRatingFinanciero());

			if (rf.getIdVaciado0() != null) {
				vac = gv.buscarVaciado(rf.getIdVaciado0());
			}
		}
		return vac;
	}

	/*
	 * Retorna el vaciado que deber铆a utilizar el rating financiero cuando se genere.
	 */
	private static Vaciado getVaciadoDefault(Long idCliente, Integer idBanca) {
		GestorRating gr = new GestorRatingImpl();
		GestorServicioClientes gsc = new GestorServicioClientesImpl();
		Vaciado vac = null;

		Cliente cli = gsc.obtenerParteInvolucradaPorId(idCliente);
		vac = gr.buscarVaciadoParaRating(cli.getRut(), idBanca);

		return vac;
	}
	
	/**
	 * Sprint 3 7.4.2 ajuste de nota minima
	 * metodok que devuelve la nota minima segun id de banca, el tipo de rating
	 * @param idBanca identificador de la banca
	 * @param idModelo identificador del tipo de rating
	 * @param notaOriginal nota por defecto
	 * @return nota minima segun criterios
	 */
	public static Double getNotaMinima(Integer idBanca, Integer idModelo, Double notaOriginal) {
		Double nota = notaOriginal;
		String parametro = "";
		Integer idPlantilla;
		try {
			idPlantilla = (Integer) SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		} catch (Exception e) {
			idPlantilla = null;
		}
		if (ConstantesSEFE.TIPO_RATING_INDIVIDUAL.equals(idModelo)) {
			parametro = ConstantesSEFE.PARAM_NOTA_MIMINA_INDIVIDUAL;
		} else if (ConstantesSEFE.TIPO_RATING_FINANCIERO.equals(idModelo)) {
			parametro = ConstantesSEFE.PARAM_NOTA_MIMINA_FINANCIERO;
		} else if (ConstantesSEFE.TIPO_RATING_PROYECTADO.equals(idModelo)) {
			parametro = ConstantesSEFE.PARAM_NOTA_MIMINA_PROYECTADO;
		} else if (ConstantesSEFE.TIPO_RATING_NEGOCIO.equals(idModelo)) {
			parametro = ConstantesSEFE.PARAM_NOTA_MIMINA_NEGOCIO;
		} else if (ConstantesSEFE.TIPO_RATING_COMPORTAMIENTO.equals(idModelo)) {
			parametro = ConstantesSEFE.PARAM_NOTA_MIMINA_COMPORTAMIENTO;
		} else if (ConstantesSEFE.TIPO_RATING_GRUPAL.equals(idModelo)) {
			if (ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
				parametro = ConstantesSEFE.PARAM_NOTA_MIMINA_GRUPAL_PYME;
			} else {
				parametro = ConstantesSEFE.PARAM_NOTA_MIMINA_GRUPAL_MULTISEGMENTO;
			}
		}
		try {
			nota = ConfigDBManager.getValueAsDouble(parametro, idPlantilla);
			nota = nota == null ? notaOriginal : nota;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return nota;
	}
	
	/**
	 * Sprint 9, req: parametrizaci髇 del rating proyectado
	 * @param matriz matriz de rating proyectado utilizado
	 * @param notaFinanciero nota del rating proyectado
	 * @return nota final de evaluaci髇
	 */
	public static Double evalTope(MatrizFinanciera matriz, Double notaFinanciero) {
		//Se evalua la nota tope del rating proyectado
		BigDecimal notaTope = new BigDecimal(matriz.getNotaTope().doubleValue());
		BigDecimal nota = new BigDecimal(notaFinanciero.doubleValue());
		nota = nota.min(notaTope);
		return new Double(nota.doubleValue());
	}
}
