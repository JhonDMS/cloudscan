package com.bch.sefe.constructora.srv.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.ErrorMessagesSEFE;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioCalculo;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.ServicioCalculoImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.GestorOtrasFormulas;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.srv.impl.GestorOtrasFormulasImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.constructora.dao.ConstructoraInmobilariaDAO;
import com.bch.sefe.constructora.dao.impl.ConstructoraInmobilariaDAOImpl;
import com.bch.sefe.constructora.srv.GestorConstructoraInmobiliaria;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.dao.impl.RatingProyectadoDAOImpl;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.vo.BalanceInmobiliario;
import com.bch.sefe.rating.vo.CabeceraHojaBi;
import com.bch.sefe.rating.vo.CabeceraHojaSoe;
import com.bch.sefe.rating.vo.HojaBalanceInmobiliario;
import com.bch.sefe.rating.vo.HojaSoe;
import com.bch.sefe.rating.vo.IndicadorFinanciero;
import com.bch.sefe.rating.vo.IndicadorSoe;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.RangoNotaFinanciera;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.Soe;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;


public class GestorConstructoraInmobilariaImpl implements GestorConstructoraInmobiliaria {
	
	private static final Logger log = Logger.getLogger(GestorConstructoraInmobilariaImpl.class);
	ServicioCalculo servicioCalculo = new ServicioCalculoImpl();
	
	
	public IndicadorSoe obtenerIndicadorSoe(Long idCliente, Long idRatingInd, String codCta) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		
		return constructora.obtenerIndicadorSoe(idCliente, idRatingInd, codCta);
	}

	public Soe obtenerSoe(Long idCliente, Long idRatingInd) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		return constructora.obtenerSoe(idCliente, idRatingInd);
	}
	public BalanceInmobiliario obtenerBi(Long idCliente, Long idRatingInd, String fechaAvance) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		return constructora.obtenerBi(idCliente, idRatingInd, fechaAvance);
	}
	public List obtenerListaHojaSoe(Long idCliente, Long idRatingInd) {
		Soe soe = obtenerSoe(idCliente, idRatingInd); 
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		return constructora.obtenerListaHojaSoe(idCliente, soe.getFechaAvance());
	}
	public List obtenerListaHojaSoe(Soe soe, Long idCliente, Long idRatingInd) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		return constructora.obtenerListaHojaSoe(idCliente, soe.getFechaAvance());
	}
	public List obtenerListaHojaBi(BalanceInmobiliario bi, Long idCliente) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		return constructora.obtenerListaHojaBi(idCliente, bi.getFechaAvance());
	}
	public CabeceraHojaSoe obtenerCabeceraHojaSoe(Long idCliente, Date fechaAvance) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		return constructora.obtenerCabeceraHojaSoe(idCliente, fechaAvance);
	}
	public CabeceraHojaBi obtenerCabeceraHojaBi(Long idCliente, Date fechaAvance) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		return constructora.obtenerCabeceraHojaBi(idCliente, fechaAvance);
	}
	public void actualizarHojaSoe(HojaSoe hojaSoe, String codigCta) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		constructora.actualizarHojaSoe(hojaSoe, codigCta);
	}
	public void actualizarHojaBi(HojaBalanceInmobiliario hojaBi, String codigCta) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		constructora.actualizarHojaBi(hojaBi, codigCta);
	}
	
	public void calcularHojaSOE(Soe soe, Long idCliente, Long idRatingInd, Long idVaciadoPivote) {
		GestorVaciados gestorVaciado = new GestorVaciadosImpl();
		// se recupera el vaciado para homologar la monedad/unidad con el SOE
		Vaciado vaciado = gestorVaciado.buscarVaciado(idVaciadoPivote);
		// recupera el soe asociado al cliente y al rating individual
		//se recupera los totalizadores de la hoja soe para poner resultado en el contexto de calculo
		CabeceraHojaSoe cabeceraHojaSoe = obtenerCabeceraHojaSoe(idCliente, soe.getFechaAvance());
		// recupera la lista de hojas soe. Si no existe valores el resultado de la lista es un array vacio y NO nulo
		List listaHojaSoe = obtenerListaHojaSoe(soe,idCliente, idRatingInd);
		
		double totalSoe = 0;
		
		for (int i=0; i<listaHojaSoe.size(); i++) {
			HojaSoe hojaSoe = (HojaSoe)listaHojaSoe.get(i);
			totalSoe += hojaSoe.getSoePorMandante().doubleValue();
		}
		
		//se pone en contexto la cabecera de hoja soe que contiene los totales de los proyectos 
		Map ctx = obtenerContextoCabeceraHojaSoe(cabeceraHojaSoe,vaciado, soe);
		Iterator itCtx = ctx.keySet().iterator();
		while (itCtx.hasNext()) {
			String key = itCtx.next().toString();
			servicioCalculo.ponerEnContexto(key, ctx.get(key));
		}
		// recupera la cabecera de la hoja soe para ponerlo en el contexto de calculo
		
		
		// recorrer los proyectos para calcular los datos faltantes. La lista nunca sera nula por tal motivo no se valida el null pointer exception.
		for (int i=0; i<listaHojaSoe.size(); i++) {
			HojaSoe hojaSoe = (HojaSoe)listaHojaSoe.get(i);
			servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_SOE_MANDATE, hojaSoe.getSoePorMandante());
			servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_MTO_SOE_1, hojaSoe.getSoeA1Ano());
			servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_MTO_SOE, hojaSoe.getSaldoPorEjecutar());
			servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_PRC_AVANCE_OBRA, hojaSoe.getPrcAvanceObra());
			servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_PLAZO, hojaSoe.getPlazo());
			servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_PRC_AVANCE_LINEAL, hojaSoe.getPrcLineal());
			servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_MTO_CONTRATO, hojaSoe.getMontoContrato());
			servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_CPX_OBRA, new Integer(hojaSoe.getComplejidadObra()));
			servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_KH_CLIENTE, new Integer(hojaSoe.getKnowHow()));	
			calcularOtrasCuentas(idVaciadoPivote, hojaSoe, totalSoe);
		}
	}
	
	public void calcularHojaBi(BalanceInmobiliario bi, Long idCliente, Long idRatingInd, String logUsu, String rutCliente) {
		// recupera la lista de hojas bi. Si no existe valores el resultado de la lista es un array vacio y NO nulo
		List listaHojaBi = obtenerListaHojaBi(bi, idCliente);
		Integer idBanca = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		List listOtrasFormulas = obtenerListaOtrasFormulas(idBanca);
		// recorrer los proyectos para calcular los datos faltantes. La lista nunca sera nula por tal motivo no se valida el null pointer exception.
		for (int i=0; i<listaHojaBi.size(); i++) {
			HojaBalanceInmobiliario hojaBi = (HojaBalanceInmobiliario)listaHojaBi.get(i);
			cargarCtxHojaBi(hojaBi);
			servicioCalculo.setDeudaSistema(bi.getDeudaSistema());
//			servicioCalculo.ponerEnContexto("DeudaSistema", bi.getDeudaSistema());
			calcularOtrasCuentas(hojaBi,listOtrasFormulas);
		}		
	}
	
		


	public void cargarCtxHojaBi(HojaBalanceInmobiliario hojaBi){
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_FECHA_AVANCE, new Long(hojaBi.getFechaAvance().getTime()));
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_FECHA_TERMINO, new Long(hojaBi.getFechaDeTermino().getTime()));
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_DURACION, hojaBi.getDuracion());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_AVANCE_OBRA, hojaBi.getAvanceDeObra());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_TOTAL_PROMESAS_VTA, hojaBi.getPctTotalVentas());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_VAL_PROMEDIO, hojaBi.getValorPromedio());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_CARTAS_RESGUARDO, hojaBi.getFlujoCartasDeResguardo());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_PROMESAS_POR_RECIBIR, hojaBi.getFlujoPorRecibirPromesas());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_SALDO_PROMESAS_POR_VENDER,hojaBi.getSaldoPorVenderPromesarMonto());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_DEUDA_MAXIMA, hojaBi.getDeudaMaxima());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_VTA_TOTAL_PROYECTADA, hojaBi.getVentaTotalProyectada());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_CONSTRUCTORA, Integer.valueOf(hojaBi.getConstructora()));
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_EXPERIENCIA_CONSTRUCTORA, Integer.valueOf(hojaBi.getExperienciaConstructora()));
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_TPO_PRODUCTO, Integer.valueOf(hojaBi.getTipoProducto()));
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_PRIMERA_O_SEGUNDA_VIV, Double.valueOf(hojaBi.getPrimeraOSegundaVivienda()));
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_CONSOLIDADO_MERCADO, Integer.valueOf(hojaBi.getConslnMdo()));
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_EXPERIENCIA_MERCADO, Integer.valueOf(hojaBi.getExperienciaMdo()));
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_OFERTA_COMPETENCIA, Integer.valueOf(hojaBi.getOfertaCompetencia()));
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_LINEA_APROBADA, hojaBi.getLineaAprobada());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_DEUDA_VIGENTE, hojaBi.getDeudaVigente());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_POR_GIRAR, hojaBi.getPorGirar());
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_VTAS_PROM_PCT, hojaBi.getPctTotalVentas());
	}
	
	public RangoNotaFinanciera obtenerRangoNota(GestorRatingFinanciero gestRatFinan, IndicadorFinanciero indicador, Double valorCuenta, Date fechaRating, Integer estado){
		RangoNotaFinanciera rangoNota= null;
		try {
			rangoNota = gestRatFinan.obtenerRangoNota(indicador, valorCuenta, null, null, fechaRating, estado);
		}catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug("El valor del indicador debe ser igual al valor de la nota");
			}
		}
		if (rangoNota== null) {
			rangoNota = new RangoNotaFinanciera();
			rangoNota.setCodigoCuenta(indicador.getCodigoCuenta());
		}
		return rangoNota;
	}
	
	public Cuenta calcularIndicadorBi(Cuenta cuenta) {
		return servicioCalculo.calcularCuenta(cuenta);
	}
	
	protected Long buscarIdClientePorRut(String rutCliente) {
		ServicioClientes srvCtes = new ServicioClientesImpl();
		return srvCtes.obtenerIdClientePorRut(rutCliente);
	}
	
	

	private void calcularOtrasCuentas(Long idVaciadoPivote, HojaSoe hojaSoe, double totalSoe) {
		Integer idBanca = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		List listOtrasFormulas = obtenerListaOtrasFormulas(idBanca);
		servicioCalculo.setOtrasCuentas(listOtrasFormulas);
		for (int j=0; j<listOtrasFormulas.size();j++) {
			Cuenta cta = (Cuenta)listOtrasFormulas.get(j);
			if(cta.getCodigoCuenta().equals("IDXC1") && totalSoe == 0)
			{
				hojaSoe.setValorGenerico(new Double(0)); 
			}
			else if(cta.getCodigoCuenta().equals("IDXC5") && totalSoe == 0)
			{
				hojaSoe.setValorGenerico(new Double(0)); 
			}
			//if((cta.getCodigoCuenta().equals("IDXC1") || cta.getCodigoCuenta().equals("NTAC1")) && hojaSoe.getSaldoPorEjecutar().doubleValue() == 0 && ConstantesSEFE.BANCA_CONSTRUCTORAS.equals(idBanca))
//			if(cta.getCodigoCuenta().equals("NTAC1") && hojaSoe.getSaldoPorEjecutar().doubleValue() == 0 && ConstantesSEFE.BANCA_CONSTRUCTORAS.equals(idBanca))
//			{
//				hojaSoe.setValorGenerico(new Double(2)); 
//	        }
//			
//			// else if((cta.getCodigoCuenta().equals("IDXC5") || cta.getCodigoCuenta().equals("NTAC5")) && hojaSoe.getSaldoPorEjecutar().doubleValue() == 0 && ConstantesSEFE.BANCA_CONSTRUCTORAS.equals(idBanca))
//			else if(cta.getCodigoCuenta().equals("NTAC5") && hojaSoe.getSaldoPorEjecutar().doubleValue() == 0 && ConstantesSEFE.BANCA_CONSTRUCTORAS.equals(idBanca))
//			{
//				hojaSoe.setValorGenerico(new Double(10)); 
//
//	        }
//			
//			//else if((cta.getCodigoCuenta().equals("IDXC2") || cta.getCodigoCuenta().equals("NTAC2") || cta.getCodigoCuenta().equals("IDXC4")) && hojaSoe.getSaldoPorEjecutar().doubleValue() == 0 && ConstantesSEFE.BANCA_CONSTRUCTORAS.equals(idBanca))
//			else if(cta.getCodigoCuenta().equals("NTAC2") && hojaSoe.getSaldoPorEjecutar().doubleValue() == 0 && ConstantesSEFE.BANCA_CONSTRUCTORAS.equals(idBanca))
//			{
//				hojaSoe.setValorGenerico(new Double(0)); 
//	        }
			else
			{
				cta =servicioCalculo.calcularCuentaIndependiente(idVaciadoPivote, cta);
				hojaSoe.setValorGenerico(cta.getMonto());
			}
			actualizarHojaSoe(hojaSoe, cta.getCodigoCuenta());
		}
	}
	private void calcularOtrasCuentas(HojaBalanceInmobiliario hojaBi, List listOtrasFormulas) {
		servicioCalculo.setOtrasCuentas(listOtrasFormulas);
		for (int j=0; j<listOtrasFormulas.size();j++) {
			Cuenta cta = (Cuenta)listOtrasFormulas.get(j);
			cta =servicioCalculo.calcularCuenta(cta);
			hojaBi.setValorGenerico(cta.getMonto());
			actualizarHojaBi(hojaBi, cta.getCodigoCuenta());
			servicioCalculo.ponerEnContexto(cta.getCodigoCuenta(), cta.getMonto());
		}
	}
		
	private List obtenerListaOtrasFormulas(Integer idBanca) {
		GestorOtrasFormulas gestorFormulas = new GestorOtrasFormulasImpl();	
		// se recupera el id de la banca
		return  gestorFormulas.obtenerListaFormulas(idBanca, ConstantesSEFE.OTRAS_FORMULAS_FLAG_COND_BORDE_OFF);
		 
	}
	private MatrizFinanciera obtenerMatrizPorIdBanca(Integer idBanca){
		ConstructoraInmobilariaDAO constructoraInmobilariaDAO = new ConstructoraInmobilariaDAOImpl();
		List matrices=constructoraInmobilariaDAO.obtenerMatrizPorIdBanca(idBanca);
		if (matrices.size()>0){
			return (MatrizFinanciera)matrices.get(0);
		}else{
			throw new BusinessOperationException(ErrorMessagesSEFE.ERR_SIN_MATRIZ_PARA_BANCA);
		}
	}
	private  Map obtenerContextoCabeceraHojaSoe(CabeceraHojaSoe cabeceraHojaSoe, Vaciado vaciado, Soe soe) {
		
		Map ctx = new HashMap();
		ctx.put(ConstantesSEFE.CTX_TOTAL_MONTO_CONTRATO, cabeceraHojaSoe.getTotalContrato());
		ctx.put(ConstantesSEFE.CTX_TOTAL_MONTO_AVANCE_OBRA, cabeceraHojaSoe.getTotalAvanceObraMonto());
		ctx.put(ConstantesSEFE.CTX_TOTAL_PORCENT_AVANCE_OBRA, cabeceraHojaSoe.getTotalAvanceObraPorcentaje());
		ctx.put(ConstantesSEFE.CTX_TOTAL_PORCENT_SOE,cabeceraHojaSoe.getTotalSoePorcentaje());
		ctx.put(ConstantesSEFE.CTX_TOTAL_MONTO_SOE, cabeceraHojaSoe.getTotalSoeMonto());
		ctx.put(ConstantesSEFE.CTX_T_SOE, cabeceraHojaSoe.getTotalSoeMonto());
		ctx.put(ConstantesSEFE.CTX_T_SOE_1, cabeceraHojaSoe.getTotalSoeUnAnoMonto());
		ctx.put(ConstantesSEFE.CTX_TOTAL_PORCENT_POR_COBRAR, cabeceraHojaSoe.getTotalSaladoPorCobrarPorcentaje());
		ctx.put(ConstantesSEFE.CTX_TOTAL_MONTO_POR_COBRAR, cabeceraHojaSoe.getTotalSaldoPorCobrarMonto());
		ctx.put(ConstantesSEFE.CTX_TOTAL_INDICADOR_POR_COBRAR, cabeceraHojaSoe.getTotalSaldoPorCobrarInd());
		ctx.put(ConstantesSEFE.CTX_TOTAL_MONTO_SOE_1_ANO, cabeceraHojaSoe.getTotalSoeUnAnoMonto());
		ctx.put(ConstantesSEFE.CTX_TOTAL_PORCENTSOE_1_ANO, cabeceraHojaSoe.getTotalSoeUnAnoPorcent());
		ctx.put(ConstantesSEFE.CTX_SP_IDXC1_NTAC1, cabeceraHojaSoe.getSumaProdDivMandante());
		ctx.put(ConstantesSEFE.CTX_SP_NTAC2_IRCC4, cabeceraHojaSoe.getSumaProdDivContrato());
		ctx.put(ConstantesSEFE.CTX_SP_IDXC1_NTAC6, cabeceraHojaSoe.getSumaProdDesContrato());
		ctx.put(ConstantesSEFE.CTX_SP_NTAC5_IRCC3, cabeceraHojaSoe.getSumaProdMadContrato());
		ctx.put(ConstantesSEFE.CTX_SP_IDXC4_IRCC3, cabeceraHojaSoe.getSumaProdComplContrato());
		ctx.put(ConstantesSEFE.CTX_SP_IRCC10_IRCC6, cabeceraHojaSoe.getSumaProdPlazoPromedio());
		ctx.put(ConstantesSEFE.CTX_FACTOR, homologarMonedadUnidadVaciadoASoe( vaciado, soe,Double.valueOf("1")));
		return ctx;
	}
	private Double homologarMonedadUnidadVaciadoASoe(Vaciado vaciado, Soe soe, Double valor)	{
		ConversorMoneda conversor = new ConversorMonedaImpl();
		Double montoHomologado = conversor.convertirMonedaSegunReglas(valor, vaciado.getIdMoneda(), vaciado.getUnidMedida(), soe.getIdMoneda(), soe.getIdUnidadMedida(), vaciado.getPeriodo());
		return montoHomologado;
	}
	
	private Map obtenerContextoCabeceraHojaBi(CabeceraHojaBi cabeceraHojaBi) {
		
		Map ctx = new HashMap();
		ctx.put(ConstantesSEFE.CTX_TOTAL_FLUJOS_POR_RECIBIR, cabeceraHojaBi.getTotalFlujosPorRecibir());
		ctx.put(ConstantesSEFE.CTX_SP_IDXI12_IDXI4, cabeceraHojaBi.getSumaProdNotaSgmFlujosPorRecibir());
		ctx.put(ConstantesSEFE.CTX_TOTAL_VTA_TOT_PROYECTADA, cabeceraHojaBi.getTotalVentaTotalProyectada());
		ctx.put(ConstantesSEFE.CTX_TOTAL_LINEA_APROBADA,cabeceraHojaBi.getTotalLineaAprobada());
		ctx.put(ConstantesSEFE.CTX_TOTAL_DEUDA_MAXIMA, cabeceraHojaBi.getTotalDeudaMaxima());
		ctx.put(ConstantesSEFE.CTX_TOTAL_DEUDA_VIGENTE, cabeceraHojaBi.getTotalDeudaVigente());
		ctx.put(ConstantesSEFE.CTX_TOTAL_DMAX_PONDERADO, cabeceraHojaBi.getTotalDmaxPonderado());
		ctx.put(ConstantesSEFE.CTX_TOTAL_FLUJOS_PONDERADOS, cabeceraHojaBi.getTotalFlujosPonderados());
		ctx.put(ConstantesSEFE.CTX_TOTAL_SALDO_PROMESAS_X_VENDER, cabeceraHojaBi.getTotalSaldoPorVenderPromesas());
		ctx.put(ConstantesSEFE.CTX_TOTAL_POR_GIRAR, cabeceraHojaBi.getTotalPorGirar());
		ctx.put(ConstantesSEFE.CTX_SP_IDXI5_IDXI4, cabeceraHojaBi.getSumaProdExitoFlujosPorRecibir());
		ctx.put(ConstantesSEFE.CTX_TOTAL_CARTAS_RESGUARDO, cabeceraHojaBi.getTotalCartasDeResguardo());
		ctx.put(ConstantesSEFE.CTX_TOTAL_PROMESAS_RECIBIR, cabeceraHojaBi.getTotalPorRecibirPromesas());
		ctx.put(ConstantesSEFE.CTX_TOTAL_RGO_CONSTRUCCION, cabeceraHojaBi.getTotalRgoConstructora());
		return ctx;
	}
	public void asociarSoeRatingIndividual(Long idCliente, Date fechaAvance, Long idRatingInd) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		constructora.asociarSoeRatingIndividual(idCliente, fechaAvance, idRatingInd);
	}
	
	public void asociarBiRatingIndividual(Long idCliente, Date fechaAvance, Long idRatingInd) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		constructora.asociarBiRatingIndividual(idCliente, fechaAvance, idRatingInd);
	}
	
	public Soe obtenerSoe(Long idCliente, Date fechaAvance) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		return constructora.obtenerSoe(idCliente, fechaAvance);
	}
	public BalanceInmobiliario obtenerBi(Long idCliente, Date fechaAvance) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		return constructora.obtenerBi(idCliente, fechaAvance);
	}
	public void actualizarSoe(Soe soe) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		constructora.actualizarSoe(soe);
		
	}
	public void actualizarBi(BalanceInmobiliario bi) {
		ConstructoraInmobilariaDAO constructora = new ConstructoraInmobilariaDAOImpl();
		constructora.actualizarBi(bi);
		
	}

	public Double obtenerCantidadProyectosBI(Long idCliente, Long idRating) {
		BalanceInmobiliario balance = this.obtenerBi(idCliente, idRating, null);
		if (balance==null) {
			// no hay proyectos para bi
			return  null;
		}
		List listaHojaBi  =this.obtenerListaHojaBi(balance, idCliente);
		// no es necesario validar si la lista es nula porque el dao ya tiene tal validacion
		return new Double(listaHojaBi.size());
	}

	public List obtenerIndicadoresBI(Long idCliente, Date fechaAvance) {
		ConstructoraInmobilariaDAO inmobiliaria = new ConstructoraInmobilariaDAOImpl();
		return inmobiliaria.obtenerIndicadoresBalanceInmob(idCliente, fechaAvance);
	}

	public Map obtenerContextoCabeceraHojaSoe(Long idCliente, Long idVaciado, Long idRatingInd) {
		// se recupera el soe asociado al rating individual
		Soe soe	= obtenerSoe(idCliente, idRatingInd);
		if (soe== null) 
			return new HashMap();
		GestorVaciados gestorVaciado = new GestorVaciadosImpl();
		// se recupera el vaciado para homologar la monedad/unidad con el SOE
		Vaciado vaciado = gestorVaciado.buscarVaciado(idVaciado);
		//se recupera los totalizadores de la hoja soe para poner resultado en el contexto de calculo
		CabeceraHojaSoe cabeceraHojaSoe = obtenerCabeceraHojaSoe(idCliente, soe.getFechaAvance());
		return obtenerContextoCabeceraHojaSoe(cabeceraHojaSoe, vaciado, soe);
	}
	public void actualizarSoeCalculo(Long idCliente, Date fechaAvance, Long idUsu){
		ConstructoraInmobilariaDAO soeDAO = new ConstructoraInmobilariaDAOImpl();
		soeDAO.actualizarSoeCalculo(idCliente, fechaAvance, idUsu);
	}
	public void eliminarIndicadoresBalanceInmobiliario(Long idCliente, Date fechaAvance){
		ConstructoraInmobilariaDAO soeDAO = new ConstructoraInmobilariaDAOImpl();
		soeDAO.eliminarIndicadoresBalanceInmobiliario(idCliente, fechaAvance);
	}

	public Map obtenerContextoCabeceraBI(Long idParteInv, Long idVaciado, Long idRatingIndividual) {
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndiv = gestorIndiv.buscarRatingIndividual(idParteInv, idRatingIndividual);
		if (ratingIndiv.getFechaAvance() == null) {
			return new HashMap();
		}
		BalanceInmobiliario bi = obtenerBi(idParteInv, ratingIndiv.getFechaAvance());
		if (bi== null) {
			return new HashMap();
		}
		CabeceraHojaBi cabeceraHojaBi = obtenerCabeceraHojaBi(idParteInv, bi.getFechaAvance());
		if (cabeceraHojaBi == null) {
			return new HashMap();
		}
		//se pone en contexto la cabecera de hoja bi que contiene los totales de los proyectos 
		Map ctx = obtenerContextoCabeceraHojaBi(cabeceraHojaBi);
		Double deuda=Double.valueOf("0");
		if (bi.getUf() != null && bi.getDeuSbif() != null && bi.getDeuSbif().intValue() > 0){
			ConversorMoneda convertor = new ConversorMonedaImpl();
			deuda = convertor.convertirMonedaSegunReglas(bi.getDeuSbif(), ConstantesSEFE.ID_CLASIF_MONEDA_CLP,ConstantesSEFE.ID_CLASIF_MILES , ConstantesSEFE.ID_CLASIF_MONEDA_UF, ConstantesSEFE.ID_CLASIF_MILES , bi.getFechaAvance());
		}
		ctx.put(ConstantesSEFE.DEU_SBIF,deuda);
		return ctx;
	}
	
	public List buscarSoeXRut(String rutCliente){
		ConstructoraInmobilariaDAO dao = new ConstructoraInmobilariaDAOImpl();
		List balances = dao.buscarSoeXRut(rutCliente);
		return balances;
	}

	public Integer obtenerAntiguedadMaximaBI(Integer idBanca) {
		return ConfigDBManager.getValueAsInteger(ConstantesSEFE.KEY_RATING_ANTIGUEDAD_MAX_BI_VIGENTE, idBanca);
	}

	public Integer obtenerAntiguedadMaximaSoe(Integer idBanca) {
		return ConfigDBManager.getValueAsInteger(ConstantesSEFE.KEY_RATING_ANTIGUEDAD_MAX_SOE_VIGENTE, idBanca);
	}

	public Boolean eliminarBalancesInmobiliarios(String rut, String fechaAvance) {
		// TODO Auto-generated method stub
		RatingProyectadoDAOImpl ratingProyectadoDAO = new RatingProyectadoDAOImpl();
		return new Boolean(ratingProyectadoDAO.borrarBalances(rut, fechaAvance, new Integer(0)));
	}

	public Boolean eliminarCuadroObras(String rut, String fechaAvance) {
		// TODO Auto-generated method stub
		RatingProyectadoDAOImpl ratingProyectadoDAO = new RatingProyectadoDAOImpl();
		return new Boolean(ratingProyectadoDAO.borrarBalances(rut, fechaAvance, new Integer(1)));
	}
}
