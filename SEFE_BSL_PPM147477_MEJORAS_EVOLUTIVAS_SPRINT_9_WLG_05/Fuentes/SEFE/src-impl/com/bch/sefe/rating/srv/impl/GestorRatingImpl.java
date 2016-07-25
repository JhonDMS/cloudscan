package com.bch.sefe.rating.srv.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.agricola.srv.GestorAgricola;
import com.bch.sefe.agricola.srv.impl.GestorAgricolaImpl;
import com.bch.sefe.agricola.vo.Agricola;
import com.bch.sefe.agricola.vo.FlujoResumen;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioCalculo;
import com.bch.sefe.comun.impl.ServicioCalculoImpl;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.ICalculoEvolucionVentasAnuales;
import com.bch.sefe.comun.srv.impl.CalculoVentasPeriodoAnterior;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.ProbDefault;
import com.bch.sefe.constructora.srv.GestorConstructoraInmobiliaria;
import com.bch.sefe.constructora.srv.impl.GestorConstructoraInmobilariaImpl;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.exception.ServiceException;
import com.bch.sefe.rating.dao.RatingDAO;
import com.bch.sefe.rating.dao.impl.RatingDAOImpl;
import com.bch.sefe.rating.impl.ComparadorVaciadosRatingFinancieroCGE;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.vo.ComponenteRating;
import com.bch.sefe.rating.vo.PonderacionNivelVenta;
import com.bch.sefe.rating.vo.PremioPatrimonio;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.Segmento;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.srv.GestorHojaIndependiente;
import com.bch.sefe.vaciados.srv.GestorPlanCuentas;
import com.bch.sefe.vaciados.srv.impl.GestorHojaIndependienteImpl;
import com.bch.sefe.vaciados.srv.impl.GestorPlanCuentasImpl;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.HojaIndependiente;
import com.bch.sefe.vaciados.vo.Vaciado;

public class GestorRatingImpl implements GestorRating {
	
	private static final Logger log = Logger.getLogger(GestorRatingImpl.class);

	public Segmento obtenerSegmento(double valor, Integer tipoSegmento) {
		Segmento segmento = new Segmento();
		RatingDAO rtgDAO = new RatingDAOImpl();
		
		
		// se obtiene el id plantilla desde el contexto del request
		Integer idPlantilla = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
				
		segmento = rtgDAO.buscarSegmento(tipoSegmento, new Double(valor), idPlantilla);
		
		return segmento;
	}
	
	public Segmento obtenerSegmentoPorId(Integer sgmId) {
		Segmento segmento = new Segmento();
		RatingDAO rtgDAO = new RatingDAOImpl();
		segmento = rtgDAO.obtenerSegmentoPorId(sgmId);
		
		return segmento;
	}

	public Integer obtenerAntiguedadMaximaVaciado(Integer idBanca) {
		return ConfigDBManager.getValueAsInteger(ConstantesSEFE.KEY_RATING_ANTIGUEDAD_MAX_VACIADO_VIGENTE, idBanca);
	}
	
	public Vaciado buscarVaciadoParaRating(String rutCliente, Integer idBanca) {
		RatingDAO rtgDAO 	= new RatingDAOImpl();
		Vaciado vaciado 	= null;
		List lstVaciados 	= null;
		
		Integer vigenciaMaximaBanca = obtenerAntiguedadMaximaVaciado(idBanca);
		
		if (ConfigDBManager.getValuesAsListString(ConstantesSEFE.LISTA_PLANTILLA_PERFILACION).contains(idBanca.toString())) {
			lstVaciados = rtgDAO.buscarVaciadosParaRating(rutCliente, vigenciaMaximaBanca);
		} else {
			// TODO en proximos release quitar esto y agregar las otras bancas
			log.error("Se intenta buscar un Vaciado para Rating para una banca no soportada. Banca <" + idBanca + ">");
			throw new BusinessOperationException(MessageManager.getError("error.banca.no.soportada"));
		}
		
		// Se filtra la lista de vaciados, de acuerdo con las restricciones de cada banca
		// filtros disponibles son:
		// - Tipo de vaciado
		// - Plan de cuentas
		// - Tipo balance
		//lstVaciados = aplicarFiltroPorPlantilla(lstVaciados, idBanca);
		lstVaciados = aplicarFiltroPorPlantilla(lstVaciados, idBanca);
		
		if (lstVaciados != null && !lstVaciados.isEmpty()) {
			// se ordenan los vaciados de acuerdo al criterio de seleccion
			Collections.sort(lstVaciados, new ComparadorVaciadosRatingFinancieroCGE());
	
			// En caso de que se encuentren mas de un vaciado que cumpla con las condiciones se retorna el primero.
			vaciado = (Vaciado) lstVaciados.get(0);
		}
		
		return vaciado;
	}
	
	public Vaciado buscarVaciadoParaRatingPYME(String rutCliente, Integer idBanca, RatingIndividual ri){
		RatingDAO rtgDAO 	= new RatingDAOImpl();
		Vaciado vaciado 	= null;
		List lstVaciados 	= null;
				
		Integer vigenciaMaximaBanca = obtenerAntiguedadMaximaVaciado(idBanca);		
		if (ConfigDBManager.getValuesAsListString(ConstantesSEFE.LISTA_PLANTILLA_PERFILACION).contains(idBanca.toString())) {
			lstVaciados = rtgDAO.buscarVaciadosParaRating(rutCliente, vigenciaMaximaBanca);
		} else {
			// TODO en proximos release quitar esto y agregar las otras bancas
			log.error("Se intenta buscar un Vaciado para Rating para una banca no soportada. Banca <" + idBanca + ">");
			throw new BusinessOperationException(MessageManager.getError("error.banca.no.soportada"));
		}		
		// Se filtra la lista de vaciados, de acuerdo con las restricciones de cada banca
		// filtros disponibles son: Tipo de vaciado, Plan de cuentas, Tipo balance
		
		lstVaciados = aplicarFiltroPorPlantillaPYME(lstVaciados, idBanca);
		
		if (lstVaciados != null && !lstVaciados.isEmpty()) {
			
				// se ordenan los vaciados de acuerdo al criterio de seleccion
				Collections.sort(lstVaciados, new ComparadorVaciadosRatingFinancieroCGE());			
				// En caso de que se encuentren mas de un vaciado que cumpla con  las condiciones y que el 
				//rating financiero no este confirmado se retorna el primero 	
				GestorRatingFinanciero gestorRatingFinanciero = new GestorRatingFinancieroImpl();
				RatingFinanciero rtgFinanciero = gestorRatingFinanciero.obtenerRating(ri.getIdRatingFinanciero());
	
						
				if(ri.getIdRatingFinanciero() != null && rtgFinanciero.getIdVaciado0() != null){		
					for(int indeceVac = 0; indeceVac < lstVaciados.size(); indeceVac++){					
						vaciado = (Vaciado) lstVaciados.get(indeceVac);					
						//if(vaciado.getIdVaciado().equals(rtgFinanciero.getIdVaciado0())){	
						if(vaciado.getIdVaciado().equals(rtgFinanciero.getIdVaciado0())){	
							return vaciado;
						}						
					}
				}else if(lstVaciados.get(0) != null){
					vaciado = (Vaciado) lstVaciados.get(0);	
				}
							
		}		
		return vaciado;
	}
	
	public List buscarListaVaciadosParaRating(String rutCliente, Integer idBanca) {
		RatingDAO rtgDAO 	= new RatingDAOImpl();
		List lstVaciados 	= null;
		
		Integer vigenciaMaximaBanca = obtenerAntiguedadMaximaVaciado(idBanca);
		
		if (ConfigDBManager.getValuesAsListString(ConstantesSEFE.LISTA_PLANTILLA_PERFILACION).contains(idBanca.toString())) {
			lstVaciados = rtgDAO.buscarVaciadosParaRating(rutCliente, vigenciaMaximaBanca);
		} else {
			// TODO en proximos release quitar esto y agregar las otras bancas
			log.error("Se intenta buscar un Vaciado para Rating para una banca no soportada. Banca <" + idBanca + ">");
			throw new BusinessOperationException(MessageManager.getError("error.banca.no.soportada"));
		}
		
		// Se filtra la lista de vaciados, de acuerdo con las restricciones de cada banca
		// filtros disponibles son:
		// - Tipo de vaciado
		// - Plan de cuentas
		// - Tipo balance
		
		lstVaciados = aplicarFiltroPorPlantilla(lstVaciados, idBanca);
		
		if (lstVaciados != null && !lstVaciados.isEmpty()) {
			// se ordenan los vaciados de acuerdo al criterio de seleccion
			Collections.sort(lstVaciados, new ComparadorVaciadosRatingFinancieroCGE());
	
			// En caso de que se encuentren mas de un vaciado que cumpla con las condiciones se retorna el primero.
		}
		
		return lstVaciados;
	}

	/*
	 * Aplica un filtro para descartar los vaciados de acuerdo a 
	 * las condiciones de negocio de cada banca / plantilla y retorna
	 * la lista de vaciados que cumplen con los criterios de negocio
	 */
	private List aplicarFiltroPorPlantilla(List lstVaciados, Integer idBanca) {
		List tposVac = ConfigDBManager.getValuesAsListString(ConstantesSEFE.RTG_FINANCIERO_TIPO_VACIADO_SOPORTADO, idBanca);
		List tposBal = ConfigDBManager.getValuesAsListString(ConstantesSEFE.RTG_FINANCIERO_TIPO_BALANCE_SOPORTADO, idBanca);
		List planCta = ConfigDBManager.getValuesAsListString(ConstantesSEFE.RTG_FINANCIERO_PLAN_CUENTA_SOPORTADO, idBanca);
		boolean parcialSoportado = Boolean.getBoolean(ConfigDBManager.getValueAsString(ConstantesSEFE.RTG_FINANCIERO_CIERRE_PARCIAL_SOPORTADO, idBanca));
		boolean soloVigenteSoportado = Boolean.getBoolean(ConfigDBManager.getValueAsString(ConstantesSEFE.RTG_FINANCIERO_SOLO_VIGENTE_SOPORTADO, idBanca));

		List filtrados = new ArrayList();
		for (int i = 0; i < lstVaciados.size(); ++i) {
			Vaciado v = (Vaciado) lstVaciados.get(i);
			
			// soporta solo vaciados vigentes
//			if (soloVigenteSoportado && !v.getIdEstado().equals(ConstantesSEFE.ID_ESTADO_VIGENTE)) {
//				continue;
//			}
//			
//			// soporta cierres parciales
//			if (!parcialSoportado && (v.getMesesPer().intValue() != 12)) {
//				continue;
//			}
//			
			// tipos de vaciado, balance y plan de cuenta
			if (tposVac.contains(v.getIdTpoVaciado().toString()) &&
					tposBal.contains(v.getIdTipoBalance().toString()) &&
					planCta.contains(v.getIdNombrePlanCtas().toString())) {
				filtrados.add(v);
			}else {
				log.info("vaciado descartado="+ v.getIdTpoVaciado()+","+v.getIdTipoBalance()+","+  v.getIdNombrePlanCtas());
			}
		}
		return filtrados;
	}
	
	private List aplicarFiltroPorPlantillaPYME(List lstVaciados, Integer idBanca) {
		List tposVac = ConfigDBManager.getValuesAsListString(ConstantesSEFE.RTG_FINANCIERO_TIPO_VACIADO_SOPORTADO, idBanca);
		List tposBal = ConfigDBManager.getValuesAsListString(ConstantesSEFE.RTG_FINANCIERO_TIPO_BALANCE_SOPORTADO, idBanca);
		List planCta = ConfigDBManager.getValuesAsListString(ConstantesSEFE.RTG_FINANCIERO_PLAN_CUENTA_SOPORTADO, idBanca);
		//boolean parcialSoportado = Boolean.getBoolean(ConfigDBManager.getValueAsString(ConstantesSEFE.RTG_FINANCIERO_CIERRE_PARCIAL_SOPORTADO, idBanca));
		boolean parcialSoportado = new Boolean(ConfigDBManager.getValueAsString(ConstantesSEFE.RTG_FINANCIERO_CIERRE_PARCIAL_SOPORTADO, idBanca)).booleanValue();
		boolean soloVigenteSoportado = new Boolean (ConfigDBManager.getValueAsString(ConstantesSEFE.RTG_FINANCIERO_SOLO_VIGENTE_SOPORTADO, idBanca)).booleanValue();

		List filtrados = new ArrayList();
		for (int i = 0; i < lstVaciados.size(); ++i) {
			Vaciado v = (Vaciado) lstVaciados.get(i);
			
			// soporta solo vaciados vigentes
			 if (soloVigenteSoportado == true && !v.getIdEstado().equals(ConstantesSEFE.ID_ESTADO_VIGENTE)) {
				continue;			
			}
			
			// soporta cierres parciales
			if (!parcialSoportado && (v.getMesesPer().intValue() != 12)) {
				continue;
			}
			
			// tipos de vaciado, balance y plan de cuenta
			if (tposVac.contains(v.getIdTpoVaciado().toString()) &&
					tposBal.contains(v.getIdTipoBalance().toString()) &&
					planCta.contains(v.getIdNombrePlanCtas().toString())) {
				filtrados.add(v);
			}else {
				log.info("vaciado descartado="+ v.getIdTpoVaciado()+","+v.getIdTipoBalance()+","+  v.getIdNombrePlanCtas());
			}
		}
		return filtrados;
	}
	
	public Segmento obtenerSegmentoVentasPorVaciado(Vaciado vac, Integer tpoSegmento ) {
		Double ventas = obtenerMontoVentas(vac);
		if (tpoSegmento== null || ConstantesSEFE.ESPACIO_VACIO.equals(tpoSegmento)) {
			return null;
		}
		Integer idBanca = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		// SEGMENTO_CONDICION_BORDE_PYME_LEVERAGE en realida es segmento ventas rating financiero.
		Segmento segmento = new GestorRatingImpl().buscarSegmento(tpoSegmento, ventas, idBanca);
		
		return segmento;
	}
	
	public Segmento obtenerSegmentoVentasRatingIndividual(Vaciado vac, Integer idBanca) {
		Double ventas = obtenerMontoVentasRatingIndividual(vac);
		Segmento segmento = buscarSegmento(ConstantesSEFE.SEGMENTO_VENTAS_RATING_INDIVIDUAL, ventas, idBanca);
		
		return segmento;
	}
	
	public Segmento obtenerSegmentoRatingIndividual(Vaciado vac, Integer idBanca, Integer tpoSegmeneto, RatingIndividual ratingIndv) {
		Double ventas = getNivelVentasPatrimonios(vac,ratingIndv);
		if (ventas==null) {
			return null;
		}
		Segmento segmento;
		if (!ratingIndv.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO)){
			segmento = buscarSegmentoPorFecha(tpoSegmeneto, ventas, idBanca, ratingIndv.getIdRating());
		}else{
			segmento = buscarSegmento(tpoSegmeneto, ventas, idBanca);
		}
		return segmento;
	}
	
	public Segmento obtenerSegmentoVentasRatingComportamiento(Vaciado v, Integer idTpoSegmento, Integer idBanca) {
		Double ventas = obtenerMontoVentasRatingIndividual(v);
		Segmento segmento = buscarSegmento(idTpoSegmento, ventas, idBanca);
		return segmento;
	}
	
	public Segmento obtenerSegmentoVentasRatingComportamiento(RatingIndividual rating, Integer idTpoSegmento, Integer idBanca) {
		
		Double ventas = obtenerMontoVentasRatingIndividualAgricola(rating);
		Segmento segmento = buscarSegmento(idTpoSegmento, ventas, idBanca);
		return segmento;
	}
	
	public Double obtenerMontoVentasRatingIndividual(Long idCliente) {
		GestorServicioClientes gestorCliente = new GestorServicioClientesImpl();
		Cliente cliente = gestorCliente.obtenerParteInvolucradaPorId(idCliente);
		ICalculoEvolucionVentasAnuales calculo = new CalculoVentasPeriodoAnterior();
		Double valorVentasIva = calculo.calcular(cliente.getRut());
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		Date fechaMesAnterior = FormatUtil.ultimoDiaCalendario(calendar.getTime());
		// Se convierte la moneda y unidad del vaciado a M UF
		if (valorVentasIva != null) {
			ConversorMoneda convMoneda = new ConversorMonedaImpl();
			
			/*
			 * marias 20121106 - se utiliza la fecha de vaciado para
			 * determinar el valor de ventas en MUF
			 */
				valorVentasIva = convMoneda.convertirMonedaSegunReglas(valorVentasIva, ConstantesSEFE.ID_CLASIF_MONEDA_CLP, ConstantesSEFE.ID_CLASIF_MILLONES, 
					ConstantesSEFE.ID_CLASIF_MONEDA_UF, ConstantesSEFE.ID_CLASIF_MILES, 
					fechaMesAnterior);
		}

		return valorVentasIva;
	}
	
	private Double obtenerMontoVentasRatingIndividualAgricola(RatingIndividual ratingIndividual) {
		GestorAgricola gestorAgricola = new GestorAgricolaImpl();
		ConversorMoneda convMoneda = new ConversorMonedaImpl();

		//Cambio para las ventas agricolas, ahora se obtiene el monto de ingresos del flujo agricola
		Double ventas =  ConstantesSEFE.DOUBLE_CERO;
		
		Agricola agricola = gestorAgricola.obtenerVaciadoAgricola(ratingIndividual.getIdCliente(), ratingIndividual.getIdRating());
		
		if(agricola == null)
		{
			ratingIndividual.setMontoVenta(ventas);
			return ventas ;
		}
		
		Long idAgricola = agricola.getIdAgricola();
		
		FlujoResumen flujo = (FlujoResumen)gestorAgricola.buscarFlujoResumen(idAgricola).get(0);
		ventas = flujo.getIngresos();
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		Date fechaMesAnterior = FormatUtil.ultimoDiaCalendario(calendar.getTime());
		// Se convierte la moneda y unidad del vaciado a M UF
		if (ventas != null) {		
			/*
			 * marias 20121106 - se utiliza la fecha de vaciado para
			 * determinar el valor de ventas en MUF
			 */
			ventas = convMoneda.convertirMonedaSegunReglas(ventas, ConstantesSEFE.ID_CLASIF_MONEDA_CLP, ConstantesSEFE.ID_CLASIF_MILES, 
					ConstantesSEFE.ID_CLASIF_MONEDA_UF, ConstantesSEFE.ID_CLASIF_MILES, 
					fechaMesAnterior);
		}
		
		ratingIndividual.setMontoVenta(ventas);
		return ventas ;
	}
	
	
	private Double getNivelVentasPatrimonios(Vaciado vaciado, RatingIndividual ratingIndi) {
		GestorHojaIndependiente hojaInd	= new GestorHojaIndependienteImpl();
		Integer idBanca = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		if (ConstantesSEFE.BANCA_INMOBILIARIAS.equals(idBanca)) {
			GestorConstructoraInmobiliaria gestor = new GestorConstructoraInmobilariaImpl();
			// se recupera la BI para obtener la cantidad de proyectos
			return gestor.obtenerCantidadProyectosBI(ratingIndi.getIdCliente(), ratingIndi.getIdRating());			
		} else if (ConstantesSEFE.BANCA_AGRICOLAS.equals(idBanca)){
			Double ventas =  ratingIndi.getMontoVenta();
			return ventas;
		} else if (hojaInd.bancaUsaHojaIndependiente(idBanca).booleanValue()) {
			HojaIndependiente hoja = hojaInd.obtenerHojaIndependiente(ratingIndi.getIdCliente(), ratingIndi.getIdRating());
			return hoja != null ? hoja.getBancoLocal() : null;
		}
		if (vaciado==null) {
			return null;
		}
		Double ventasVac = ConstantesSEFE.DOUBLE_CERO;
		GestorPlanCuentas gestorPlanCtas = new GestorPlanCuentasImpl();
		Cuenta ctaVentas = gestorPlanCtas.consultarValorCuentaVaciado(vaciado.getIdVaciado(), ConfigDBManager.getValueAsString(ConstantesSEFE.RTG_INDIVIDUAL_COD_CTA_SEGMENTO, idBanca));
		if (ctaVentas != null && ctaVentas.getMontoMasAjuste() != null) {
			ConversorMoneda convMoneda = new ConversorMonedaImpl();
			ventasVac = convMoneda.convertirMonedaSegunReglas(ctaVentas.getMontoMasAjuste(), vaciado.getIdMoneda(), vaciado.getUnidMedida(),
					ConstantesSEFE.ID_CLASIF_MONEDA_UF, ConstantesSEFE.ID_CLASIF_MILES, vaciado.getPeriodo());
		}
		return ventasVac;
	}
	
	
	public Double obtenerMontoVentasRatingIndividual(Vaciado vaciado) {
		Integer idBanca = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		//TODO: solucion temporal hasta que se termine el desarrollo de la hoja independiente
		if (ConstantesSEFE.BANCA_BANCOS.equals(idBanca)) {
			 return ConstantesSEFE.DOUBLE_CERO;
		}
		String claveVentas 	= ConstantesSEFE.CODIGO_CUENTA_VENTAS;
		String codCuentaVentas = ConfigDBManager.getValueAsString(claveVentas);

		// Se recupera la cuenta desde el gestor de plan de cuentas
		GestorPlanCuentas gestorCuentas = new GestorPlanCuentasImpl();
		Cuenta cuenta = gestorCuentas.consultarValorCuentaVaciado(vaciado.getIdVaciado(), codCuentaVentas);
		Double ventas = cuenta.getMonto();
		Double ajuste = cuenta.getAjuste();

		// se obtiene el valor de las ventas del vaciado
		if (ajuste != null) {
			ventas = new Double(ventas.doubleValue() + ajuste.doubleValue());
		}

		// Se convierte la moneda y unidad del vaciado a M UF
		if (ventas != null) {
			ConversorMoneda convMoneda = new ConversorMonedaImpl();
			
			/*
			 * marias 20121106 - se utiliza la fecha de vaciado para
			 * determinar el valor de ventas en MUF
			 */
			
			ventas = convMoneda.convertirMonedaSegunReglas(ventas, vaciado.getIdMoneda(), vaciado.getUnidMedida(), 
					ConstantesSEFE.ID_CLASIF_MONEDA_UF, ConstantesSEFE.ID_CLASIF_MILES, 
					vaciado.getPeriodo());
		}

		return ventas;
	}
	
	public Double obtenerMontoVentas(Vaciado vaciado) {
		Double ventas = null;
		//Double ventas2 = null;
		String mensaje = ""; //ConstantesSEFE.MSG9_INTEGRIDAD_RATING_FINANCIERO;
		GestorHojaIndependiente gestorHoja = new GestorHojaIndependienteImpl();
		Integer idBanca = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		Object idRating = SEFEContext.getValueAsObject(ConstantesSEFE.SEFE_CTX_ID_RATING_IND);
		//se verifica si la banca usa hoja independiente. Si el resultado es verdadero entonces se recupera el flag si es banco Nacional o Internacional
		if (gestorHoja.bancaUsaHojaIndependiente(idBanca).booleanValue()) {
			HojaIndependiente hoja = gestorHoja.obtenerHojaIndependiente(vaciado.getIdParteInv(), Long.valueOf(idRating.toString()) );
			 return hoja.getBancoLocal();
		}else if (ConstantesSEFE.BANCA_AGRICOLAS.equals(idBanca)) {
			// recuperar el valor por el cual se debe segmentar el rating proyectado agricola
			GestorAgricola gestorAgricola = new GestorAgricolaImpl();
			return gestorAgricola.obtenerSegmentacionVaciadoAgricola(vaciado.getIdParteInv(), (Long) idRating);
			// TODO: IMPLEMENTAR LOGICA PARA CALCULAR EL VALOR CORRECTO
			//return ConstantesSEFE.DOUBLE_CERO;
		}
		try {
			
			String claveVentas = ConfigDBManager.getValueAsString(ConstantesSEFE.RTG_FINANCIERO_COD_CTA_SEGMENTO, idBanca);
			
			if (claveVentas != null && !ConstantesSEFE.ESPACIO_VACIO.equals(claveVentas)) {
				// Se recupera la cuenta desde el gestor de plan de cuentas
				GestorPlanCuentas gestorCuentas = new GestorPlanCuentasImpl();
				Cuenta cuenta = new Cuenta();
				cuenta = gestorCuentas.consultarValorCuentaVaciado(vaciado.getIdVaciado(), claveVentas);
				if(cuenta == null){
					ServicioCalculo servicioCalculo = new ServicioCalculoImpl();
					servicioCalculo.calcularCuentasVaciado(vaciado.getIdVaciado());
					cuenta = gestorCuentas.consultarValorCuentaVaciado(vaciado.getIdVaciado(), claveVentas);
					//throw new BusinessOperationException("La Operación no se puede realizar, Favor recalcular el vaciado asociado al Rating");
				}
				ventas = cuenta.getMonto();
				Double ajuste = cuenta.getAjuste();
				// se obtiene el valor de las ventas del vaciado
				if (ajuste != null) {
					ventas = new Double(ventas.doubleValue() + ajuste.doubleValue());
				}		
				// Se convierte la moneda y unidad del vaciado a M UF
				if (ventas != null) {
					ConversorMoneda convMoneda = new ConversorMonedaImpl();
					
					/*
					 * marias 20121106 - se utiliza la fecha de vaciado para
					 * determinar el valor de ventas en MUF
					 */
					
					ventas = convMoneda.convertirMonedaSegunReglas(ventas, vaciado.getIdMoneda(), vaciado.getUnidMedida(), 
							ConstantesSEFE.ID_CLASIF_MONEDA_UF, ConstantesSEFE.ID_CLASIF_MILES, 
							vaciado.getPeriodo());
				}
			}
		}catch (Exception ex) {
		mensaje = ex.getMessage();
		log.info("Error en la aplicacion : " + mensaje);
		throw new ServiceException(mensaje, ex);
		//throw new BusinessOperationException(mensaje);
		}
		return ventas;
	}

	public PonderacionNivelVenta buscarPonderacionNivelVentas(Segmento segmento, Integer idTipoRating) {
		RatingDAO ratingDAO = new RatingDAOImpl();
		Collection ponderaciones = ratingDAO.buscarPonderacionNivelVentas(segmento.getIdBanca(), idTipoRating, segmento.getIdSegmento());
		if (ponderaciones != null && !ponderaciones.isEmpty())
			return (PonderacionNivelVenta)ponderaciones.iterator().next();
		return null;
	}

	public Segmento buscarSegmento(Integer tipoSegmento, Double valor, Integer idBanca) {
		RatingDAO ratingDAO = new RatingDAOImpl();
		
		return ratingDAO.buscarSegmento(tipoSegmento, valor, idBanca);
	}
	public Segmento buscarSegmentoPorFecha(Integer tipoSegmento, Double valor, Integer idBanca, Long idRating) {
		RatingDAO ratingDAO = new RatingDAOImpl();
		
		return ratingDAO.buscarSegmentoPorFecha(tipoSegmento, valor, idBanca, idRating);
	}
	public PremioPatrimonio buscarPremioPatrimonio(Segmento segmento) {
		RatingDAO ratingDAO = new RatingDAOImpl();
		
		List premios = ratingDAO.buscarPremioPatrimonio(segmento.getIdSegmento());
		
		return (premios.isEmpty() ? null : (PremioPatrimonio) premios.get(0));
	}

	public List buscarEquivalenciasRating(Integer idBanca) {
		RatingDAO rtgDAO = new RatingDAOImpl();
		
		return rtgDAO.buscarEquivalenciasRating(idBanca);
	}
	
	public ProbDefault buscarProbabilidadDefault(Integer idBanca, Double notaRtgFinal) {
		List allProbDefault = buscarProbabilidadesDefault();

		if (allProbDefault != null) {
			for (int i = 0; i < allProbDefault.size(); i++) {
				ProbDefault probDefautl = (ProbDefault) allProbDefault.get(i);

				if (probDefautl.getRtgFinal().doubleValue() == notaRtgFinal.doubleValue()) {
					return probDefautl;
				}
			}
		}

		return null;
	}

	public List obtenerSegmentosVentaPorBanca(Integer idBanca) {
		List segmentos = null;
		RatingDAO rtgDAO = new RatingDAOImpl();
		segmentos = rtgDAO.obtenerSegmentosPorBanca(idBanca, ConstantesSEFE.CLASIF_ID_TPO_SEGMENTO_VENTAS);
		
		return segmentos;
	}
	
	
	public List obtenerSegmentosPorTipoYBanca(Integer idBanca, Integer tpoSegemento) {
		List segmentos = null;
		RatingDAO rtgDAO = new RatingDAOImpl();
		segmentos = rtgDAO.obtenerSegmentosPorBanca(idBanca, tpoSegemento);
		
		return segmentos;
	}
	public List obtenerSegmentosPorTipoBancaYFecha(Integer idBanca, Integer tpoSegemento, Long idRating) {
		List segmentos = null;
		RatingDAO rtgDAO = new RatingDAOImpl();
		segmentos = rtgDAO.obtenerSegmentosPorBancaYFecha(idBanca, tpoSegemento, idRating);
		
		return segmentos;
	}
	public List buscarComponentesRating(Integer idBanca, Integer idComponente) {
		RatingDAO rtgDAO = new RatingDAOImpl();
		return rtgDAO.buscarComponentesRating(idBanca, idComponente);
	}
	
	public List buscarComponentesRatingSoloIdComponente(Integer idBanca, Integer idComponente) {
		RatingDAO rtgDAO = new RatingDAOImpl();
		List lst = rtgDAO.buscarComponentesRating(idBanca, idComponente);
		List newList = new ArrayList();
		for (int i=0; i<lst.size(); i++) {
			ComponenteRating componente = (ComponenteRating)lst.get(i);
			newList.add(componente.getIdComponente());
		}
		return newList;
	}
	
	public List buscarProbabilidadesDefault() {
		return new RatingDAOImpl().buscarProbabilidadesDefault();
	}

	public Integer obtenerAntiguedadMaximaBI(Integer idBanca) {
		// TODO Auto-generated method stub
		return null;
	}

	//sprint 2 req 7.1.4 alinear y borrar rating en curso
	public boolean borrarRatingEnCurso(Long idRating) {
		RatingDAO rtgDAO = new RatingDAOImpl();
		return rtgDAO.borrarRatingCurso(idRating);
	}


		
}
