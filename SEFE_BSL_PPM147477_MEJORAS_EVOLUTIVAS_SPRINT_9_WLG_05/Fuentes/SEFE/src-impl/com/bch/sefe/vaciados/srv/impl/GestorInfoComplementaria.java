package com.bch.sefe.vaciados.srv.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.GestorClasificaciones;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.srv.impl.GestorClasificacionesImpl;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.comun.vo.ValorDetalleCtaPlanCtas;
import com.bch.sefe.vaciados.ConstantesVaciados;
import com.bch.sefe.vaciados.dao.VaciadosDAO;
import com.bch.sefe.vaciados.dao.VaciadosDAOImpl;
import com.bch.sefe.vaciados.srv.GestorPlanCuentas;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.IGestorInfoComplementaria;
import com.bch.sefe.vaciados.vo.ConceptoInfoComplementaria;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;
import com.bch.sefe.vaciados.vo.ValorInfoComplementaria;
import com.bch.sefe.vaciados.vo.ValorInfoEspecifica;

public class GestorInfoComplementaria implements IGestorInfoComplementaria {
	private static Logger log = Logger.getLogger(GestorInfoComplementaria.class);

	public List buscarInfoSegmento(Vaciado vac1, Vaciado vac2, Vaciado vac3) {
		GestorPlanCuentas gpc = new GestorPlanCuentasImpl();
		List conceptos = new ArrayList();

		if (log.isDebugEnabled()) {
			Long idVac1 = vac1.getIdVaciado();
			Long idVac2 = (vac2 != null ? vac2.getIdVaciado() : null);
			Long idVac3 = (vac3 != null ? vac3.getIdVaciado() : null);
			log.debug(MessageFormat.format("Buscando informacion de segmento para los vaciados: {0}, {1}, {2}", new Long[] { idVac1, idVac2, idVac3 }));
		}
		List idsCtas = new ArrayList();
		if (!vac1.getIdNombrePlanCtas().equals(Integer.valueOf(ConstantesSEFE.NOMBRE_PLAN_CUENTAS_CHGAAP_ID))){
			idsCtas = gpc.buscarIdsCuentasSegmento(vac1.getIdNombrePlanCtas(), vac1.getIdTipoPlan());
		}else if (vac2!=null && !vac2.getIdNombrePlanCtas().equals(Integer.valueOf(ConstantesSEFE.NOMBRE_PLAN_CUENTAS_CHGAAP_ID))){
			idsCtas = gpc.buscarIdsCuentasSegmento(vac2.getIdNombrePlanCtas(), vac2.getIdTipoPlan());
		}else if (vac3!=null && !vac3.getIdNombrePlanCtas().equals(Integer.valueOf(ConstantesSEFE.NOMBRE_PLAN_CUENTAS_CHGAAP_ID))){
			idsCtas = gpc.buscarIdsCuentasSegmento(vac3.getIdNombrePlanCtas(), vac3.getIdTipoPlan());
		}
		if (log.isDebugEnabled()) {
			log.debug("Listado de cuentas de segmento encontradas: " + idsCtas);
		}

		if (idsCtas != null && !idsCtas.isEmpty()) {
			for (int i = 0; i < idsCtas.size(); i++) {
				Long idCta = (Long) idsCtas.get(i);

				Cuenta cta = getValoresCuentaInfoSegmento(idCta, vac1, vac2, vac3);
				if (cta!=null){
					List aperturas = getAperturasInfoSegmento(idCta, vac1, vac2, vac3);
	
					ConceptoInfoComplementaria conceptoCta = new ConceptoInfoComplementaria(cta.getNumCta().toString(), cta.getDescripCta());
					if (vac1!=null && !vac1.getIdNombrePlanCtas().equals(Integer.valueOf(ConstantesSEFE.NOMBRE_PLAN_CUENTAS_CHGAAP_ID))){
						conceptoCta.setValorPeriodo1(cta.getMonto());
					}
					if (vac2!=null && !vac2.getIdNombrePlanCtas().equals(Integer.valueOf(ConstantesSEFE.NOMBRE_PLAN_CUENTAS_CHGAAP_ID))){
						conceptoCta.setValorPeriodo2(cta.getMontoUltBce());
					}
					if (vac3!=null && !vac3.getIdNombrePlanCtas().equals(Integer.valueOf(ConstantesSEFE.NOMBRE_PLAN_CUENTAS_CHGAAP_ID))){
						conceptoCta.setValorPeriodo3(cta.getMontoN_2());
					}
					conceptoCta.setUnidad(cta.getUnidad());
					conceptoCta.setIdUnidad(cta.getIdUnidad());
	
					homologarValoresCuenta(conceptoCta, vac1, vac2, vac3);
	
					if (aperturas != null) {
						agregarTotales(conceptoCta, aperturas);
						conceptoCta.setSubConceptos(aperturas);
					}
					conceptos.add(conceptoCta);
				}
			}
		}

		return conceptos;
	}

	public void guardarInfoSegmento(Vaciado vac1, List info) {
		GestorVaciados gv = new GestorVaciadosImpl();
		Integer idNombrePlan = vac1.getIdNombrePlanCtas();
		Integer idTipoPlan = vac1.getIdTipoPlan();
		Long idVac = vac1.getIdVaciado();
		final String AJUSTE_CONSOLIDACION = "AJUSTES DE CONSOLIDACI\u00D3N";
		for (int i = 0; i < info.size(); i++) {
			ConceptoInfoComplementaria concepto = (ConceptoInfoComplementaria) info.get(i);
			Long idCuenta = Long.valueOf(concepto.getId());
			List aperturas = concepto.getSubConceptos();

			gv.borrarAperturasCuentaSegmento(idVac, idTipoPlan, idNombrePlan, idCuenta);

			for (int j = 0; j < aperturas.size(); j++) {
				ConceptoInfoComplementaria apertura = (ConceptoInfoComplementaria) aperturas.get(j);
				if (AJUSTE_CONSOLIDACION.equals(apertura.getNombre()) && apertura.getValorPeriodo1() != null && apertura.getValorPeriodo1().doubleValue()==0){
					continue;
				}
				if (apertura.getValorPeriodo1()!=null){
					gv.guardarAperturasCuentaSegmento(idVac, apertura.getValorPeriodo1(), idTipoPlan, idNombrePlan, idCuenta, apertura.getNombre());
				}
			}

		}
	}

	public List buscarInfoAdicional(Vaciado vac1, Vaciado vac2, Vaciado vac3) {
		GestorVaciados gv = new GestorVaciadosImpl();
		List conceptos = new ArrayList();
		Long idVac1 = vac1.getIdVaciado();
		Long idVac2 = (vac2 != null ? vac2.getIdVaciado() : null);
		Long idVac3 = (vac3 != null ? vac3.getIdVaciado() : null);

		Collection tiposInfoAdic = getTiposInfoAdicionales();
		Iterator itTiposInfoAdic = tiposInfoAdic.iterator();
		while (itTiposInfoAdic.hasNext()) {
			Clasificacion tipoInfoAdic = (Clasificacion) itTiposInfoAdic.next();

			ConceptoInfoComplementaria concepto = new ConceptoInfoComplementaria(tipoInfoAdic.getIdClasif().toString(), tipoInfoAdic.getDescripcion());
			concepto.setIdTipoInfo(tipoInfoAdic.getIdClasif());

			List aperturas = gv.buscarAperturasInfoAdicional(idVac1, idVac2, idVac3, concepto.getIdTipoInfo());
			Integer idUnidadMedida = obtenerUnidadPrimerPeriodo(aperturas);
			if (idUnidadMedida != null){
				//guardarCambioUnidad(aperturas, idUnidadMedida);
				aperturas = gv.buscarAperturasInfoAdicional(idVac1, idVac2, idVac3, concepto.getIdTipoInfo());
			}
			if (aperturas != null && aperturas.size() > 0) {
				agregarTotales(concepto, aperturas);

				// Se obtiene del primer registro la unidad. Si bien la unidad se guarda por apertura es una configuracion a nivel del CONCEPTO.
				ValorInfoComplementaria via = (ValorInfoComplementaria) aperturas.get(0);

				concepto.setIdUnidad(via.getIdUnidad());
				concepto.setSubConceptos(aperturas);
			}

			conceptos.add(concepto);
		}

		return conceptos;
	}
	public List buscarInfoAdicionalReporte(Vaciado vac1, Vaciado vac2, Vaciado vac3) {
		GestorVaciados gv = new GestorVaciadosImpl();
		List conceptos = new ArrayList();
		Long idVac1 = vac1.getIdVaciado();
		Long idVac2 = (vac2 != null ? vac2.getIdVaciado() : null);
		Long idVac3 = (vac3 != null ? vac3.getIdVaciado() : null);

		Collection tiposInfoAdic = getTiposInfoAdicionales();
		Iterator itTiposInfoAdic = tiposInfoAdic.iterator();
		while (itTiposInfoAdic.hasNext()) {
			Clasificacion tipoInfoAdic = (Clasificacion) itTiposInfoAdic.next();

			ConceptoInfoComplementaria concepto = new ConceptoInfoComplementaria(tipoInfoAdic.getIdClasif().toString(), tipoInfoAdic.getDescripcion());
			concepto.setIdTipoInfo(tipoInfoAdic.getIdClasif());

			List aperturas = gv.buscarAperturasInfoAdicional(idVac1, idVac2, idVac3, concepto.getIdTipoInfo());
			if (aperturas != null && aperturas.size() > 0) {
				agregarTotales(concepto, aperturas);

				// Se obtiene del primer registro la unidad. Si bien la unidad se guarda por apertura es una configuracion a nivel del CONCEPTO.
				ValorInfoComplementaria via = (ValorInfoComplementaria) aperturas.get(0);

				concepto.setIdUnidad(via.getIdUnidad());
				concepto.setSubConceptos(aperturas);
			}

			conceptos.add(concepto);
		}

		return conceptos;
	}
	private void guardarCambioUnidad(List aperturas, Integer idUnidadMedida){
		for (int i = 0; i<aperturas.size(); i++){
			ValorInfoComplementaria via = (ValorInfoComplementaria) aperturas.get(i);
			GestorVaciados gv = new GestorVaciadosImpl();
			Long idVaciado = null;
			Double valorVaciado = null;
			if (via.getIdVaciado1()!=null){
				idVaciado = via.getIdVaciado1();
				valorVaciado = via.getValorVaciado1();
			}else if (via.getIdVaciado2()!=null){
				idVaciado = via.getIdVaciado2();
				valorVaciado = via.getValorVaciado2();
			}else if (via.getIdVaciado3()!=null){
				idVaciado = via.getIdVaciado3();
				valorVaciado = via.getValorVaciado3();
			} 
			gv.guardarAperturasInfoAdicional(idVaciado, valorVaciado, via.getApertura(), via.getIdTipoInfoAdicional(), idUnidadMedida);
		}
	}
	private void guardarCambioUnidadInfoEspecifica(ConceptoInfoComplementaria concepto, Integer idUnidadMedida){
		for (int i = 0; i<concepto.getSubConceptos().size(); i++){
			ValorInfoComplementaria via = (ValorInfoComplementaria) concepto.getSubConceptos().get(i);
			GestorVaciados gv = new GestorVaciadosImpl();
			Long idVaciado = null;
			Double valorVaciado = null;
			if (via.getIdVaciado1()!=null){
				idVaciado = via.getIdVaciado1();
				valorVaciado = via.getValorVaciado1();
			}else if (via.getIdVaciado2()!=null){
				idVaciado = via.getIdVaciado2();
				valorVaciado = via.getValorVaciado2();
			}else if (via.getIdVaciado3()!=null){
				idVaciado = via.getIdVaciado3();
				valorVaciado = via.getValorVaciado3();
			} 
			gv.guardarAperturasInfoEspecifica(idVaciado, valorVaciado, concepto.getNombre(),via.getApertura(), idUnidadMedida, concepto.getNombre());
		}
	}
	private Integer obtenerUnidadPrimerPeriodo(List aperturas){
		for (int i = 0; i<aperturas.size(); i++){
			ValorInfoComplementaria via = (ValorInfoComplementaria) aperturas.get(i);
			if (via.getIdVaciado3() != null){
				return via.getIdUnidad();
			}			
		}
		for (int i = 0; i<aperturas.size(); i++){
			ValorInfoComplementaria via = (ValorInfoComplementaria) aperturas.get(i);
			if (via.getIdVaciado2() != null){
				return via.getIdUnidad();
			}			
		}
		for (int i = 0; i<aperturas.size(); i++){
			ValorInfoComplementaria via = (ValorInfoComplementaria) aperturas.get(i);
			if (via.getIdVaciado1() != null){
				return via.getIdUnidad();
			}			
		}
		return null;
	}
	public void guardarInfoAdicional(Vaciado vac1, List info) {
		GestorVaciados gv = new GestorVaciadosImpl();
		Long idVac = vac1.getIdVaciado();

		gv.borrarAperturasInfoAdicional(idVac);

		for (int i = 0; i < info.size(); i++) {
			// El primer registro no corresponde a una apertura a guardar
			ConceptoInfoComplementaria concepto = (ConceptoInfoComplementaria) info.get(i);
			Integer idTipoInfo = Integer.valueOf(concepto.getId());
			// Cuando la informacion adicional es ingreso por linea de producto de negocio, la unidad es la utilizada en el vaciado
			
			List aperturas = concepto.getSubConceptos();
			if (aperturas != null){
				for (int j = 0; j < aperturas.size(); j++) {
					if(aperturas.get(j).getClass().getName().equals("com.bch.sefe.vaciados.vo.ValorInfoComplementaria")){
						ValorInfoComplementaria apert = (ValorInfoComplementaria)aperturas.get(j);
						if (apert.getValorVaciado1()!=null){
							gv.guardarAperturasInfoAdicional(idVac, apert.getValorVaciado1(), apert.getApertura(), idTipoInfo, apert.getIdUnidad());
						}
						
					}else{
						ConceptoInfoComplementaria apertura = (ConceptoInfoComplementaria) aperturas.get(j);
						Integer idUnidad = (ConstantesSEFE.ID_INFO_ADICIONAL_INGRESO_POR_LINEA_PRODUCTO_NEG.equals(idTipoInfo) ? vac1.getUnidMedida() : apertura.getIdUnidad());
						if (apertura.getValorPeriodo1()!=null){
							gv.guardarAperturasInfoAdicional(idVac, apertura.getValorPeriodo1(), apertura.getNombre(), idTipoInfo, idUnidad);
						}
					}
					
				}
			}
			

		}
	}

	public ConceptoInfoComplementaria buscarInfoAdicionalEstructura(Vaciado vac1, Vaciado vac2, Vaciado vac3) {
		GestorVaciados gv = new GestorVaciadosImpl();
		Long idVac1 = vac1.getIdVaciado();
		Long idVac2 = (vac2 != null ? vac2.getIdVaciado() : null);
		Long idVac3 = (vac3 != null ? vac3.getIdVaciado() : null);

		List aperturas = gv.buscarAperturasInfoAdicionalEstructura(idVac1, idVac2, idVac3);

		return getInfoAdicionalMercadoOEstructura(ConstantesSEFE.ID_CLASIF_INFO_ADICIONAL_ESTRUCTURA_COSTOS, aperturas);
	}

	public ConceptoInfoComplementaria buscarInfoAdicionalMercado(Vaciado vac1, Vaciado vac2, Vaciado vac3) {
		GestorVaciados gv = new GestorVaciadosImpl();
		Long idVac1 = vac1.getIdVaciado();
		Long idVac2 = (vac2 != null ? vac2.getIdVaciado() : null);
		Long idVac3 = (vac3 != null ? vac3.getIdVaciado() : null);

		List aperturas = gv.buscarAperturasInfoAdicionalMercado(idVac1, idVac2, idVac3);

		return getInfoAdicionalMercadoOEstructura(ConstantesSEFE.ID_CLASIF_INFO_ADICIONAL_MERCADO_DESTINO, aperturas);
	}

	public void guardarInfoAdicionalEstructura(Vaciado vac1, ConceptoInfoComplementaria info) {
		GestorVaciados gv = new GestorVaciadosImpl();
		Long idVac = vac1.getIdVaciado();
		List aperturas;

		gv.borrarAperturasInfoAdicionalEstructura(idVac);

		aperturas = info.getSubConceptos();

		if (aperturas == null)
			return;

		for (int i = 0; i < aperturas.size(); i++) {
			ConceptoInfoComplementaria apertura = (ConceptoInfoComplementaria) aperturas.get(i);
			gv.guardarAperturasInfoAdicionalEstructura(idVac, apertura.getValorPeriodo1(), Integer.valueOf(apertura.getId()));
		}
	}

	public void guardarInfoAdicionalMercado(Vaciado vac1, ConceptoInfoComplementaria info) {
		GestorVaciados gv = new GestorVaciadosImpl();
		Long idVac = vac1.getIdVaciado();
		List aperturas;

		gv.borrarAperturasInfoAdicionalMercado(idVac);

		aperturas = info.getSubConceptos();

		if (aperturas == null)
			return;

		for (int i = 0; i < aperturas.size(); i++) {
			ConceptoInfoComplementaria apertura = (ConceptoInfoComplementaria) aperturas.get(i);
			gv.guardarAperturasInfoAdicionalMercado(idVac, apertura.getValorPeriodo1(), Integer.valueOf(apertura.getId()));
		}
	}

	public List buscarInfoEspecifica(Vaciado vac1, Vaciado vac2, Vaciado vac3) {
		GestorVaciados gv = new GestorVaciadosImpl();
		Long idVac = vac1.getIdVaciado();
		Long idVac2 = (vac2 != null ? vac2.getIdVaciado() : null);
		Long idVac3 = (vac3 != null ? vac3.getIdVaciado() : null);
		List lstConceptos;

		lstConceptos = gv.buscarAperturasInfoEspecifica(idVac, idVac2, idVac3);
		if (lstConceptos != null) {
			lstConceptos = new ArrayList(agruparInfoEspecificaPorConcepto(lstConceptos));
			for (int i = 0; i < lstConceptos.size(); i++) {
				ConceptoInfoComplementaria concepto = (ConceptoInfoComplementaria) lstConceptos.get(i);
				Integer idUnidadMedida = obtenerUnidadPrimerPeriodo(concepto.getSubConceptos());
				if (idUnidadMedida != null){
					guardarCambioUnidadInfoEspecifica(concepto, idUnidadMedida);
				}
			}
			lstConceptos = gv.buscarAperturasInfoEspecifica(idVac, idVac2, idVac3);
			lstConceptos = new ArrayList(agruparInfoEspecificaPorConcepto(lstConceptos));
			// Al tener los conceptos con sus respectivas aperturas se totaliza
			for (int i = 0; i < lstConceptos.size(); i++) {
				ConceptoInfoComplementaria concepto = (ConceptoInfoComplementaria) lstConceptos.get(i);

				if (concepto.getSubConceptos() != null && !concepto.getSubConceptos().isEmpty()) {
					agregarTotales(concepto, concepto.getSubConceptos());
				}
			}
		}

		return lstConceptos;
	}
	public List buscarInfoEspecificaReporte(Vaciado vac1, Vaciado vac2, Vaciado vac3) {
		GestorVaciados gv = new GestorVaciadosImpl();
		Long idVac = vac1.getIdVaciado();
		Long idVac2 = (vac2 != null ? vac2.getIdVaciado() : null);
		Long idVac3 = (vac3 != null ? vac3.getIdVaciado() : null);
		List lstConceptos;

		lstConceptos = gv.buscarAperturasInfoEspecifica(idVac, idVac2, idVac3);
		if (lstConceptos != null) {
			lstConceptos = gv.buscarAperturasInfoEspecifica(idVac, idVac2, idVac3);
			lstConceptos = new ArrayList(agruparInfoEspecificaPorConcepto(lstConceptos));
			// Al tener los conceptos con sus respectivas aperturas se totaliza
			for (int i = 0; i < lstConceptos.size(); i++) {
				ConceptoInfoComplementaria concepto = (ConceptoInfoComplementaria) lstConceptos.get(i);

				if (concepto.getSubConceptos() != null && !concepto.getSubConceptos().isEmpty()) {
					agregarTotales(concepto, concepto.getSubConceptos());
				}
			}
		}

		return lstConceptos;
	}
	public void guardarInfoEspecifica(Vaciado vac1, List info) {
		GestorVaciados gv = new GestorVaciadosImpl();
		Long idVac = vac1.getIdVaciado();

		gv.borrarAperturasInfoEspecifica(idVac);
		
		for (int i = 0; i < info.size(); i++) {
			ConceptoInfoComplementaria concepto = (ConceptoInfoComplementaria) info.get(i);
			List aperturas = concepto.getSubConceptos();

			if (aperturas != null && !aperturas.isEmpty()) {
				for (int j = 0; j < aperturas.size(); j++) {
					ConceptoInfoComplementaria apertura = (ConceptoInfoComplementaria) aperturas.get(j);
					if (apertura.getValorPeriodo1()!=null){
						gv.guardarAperturasInfoEspecifica(idVac, apertura.getValorPeriodo1(), concepto.getNombre(), apertura.getNombre(), concepto.getIdUnidad(), concepto.getId());
					}
				}
			}
		}
	}

	public Map getInformacionVaciado(Vaciado vac) {
		HashMap info = new HashMap();
		info.put(ConstantesVaciados.PLAN_CUENTA, getNombreClasificacion(vac.getIdNombrePlanCtas()));
		info.put(ConstantesVaciados.FLAG_AJUSTE, getTxtAjustado(vac.getAjustadoFlg()));
		info.put(ConstantesVaciados.RESPONSABLE, getUsuarioModif(vac.getIdUsuModifInfoComp()));
		info.put(ConstantesVaciados.UNIDAD, getNombreClasificacion(vac.getUnidMedida()));
		info.put(ConstantesVaciados.MONEDA, getNombreClasificacion(vac.getIdMoneda()));
		info.put(ConstantesVaciados.MESES, vac.getMesesPer());

		return info;
	}

	public Map totalizar(List aperturas) {
		Map totales = new HashMap();
		BigDecimal totalPeriodo1 = ConstantesSEFE.BIG_DECIMAL_CERO;
		BigDecimal totalPeriodo2 = ConstantesSEFE.BIG_DECIMAL_CERO;
		BigDecimal totalPeriodo3 = ConstantesSEFE.BIG_DECIMAL_CERO;

		for (int i = 0; i < aperturas.size(); i++) {
			Object object = null;
			object = (Object) aperturas.get(i);
			if (object instanceof ValorInfoComplementaria){
				ValorInfoComplementaria apertura = (ValorInfoComplementaria) aperturas.get(i);
				// Se realizan las sumas para los 3 periodos
				totalPeriodo1 = sumar(totalPeriodo1, apertura.getValorVaciado1());
				totalPeriodo2 = sumar(totalPeriodo2, apertura.getValorVaciado2());
				totalPeriodo3 = sumar(totalPeriodo3, apertura.getValorVaciado3());
			}else {
				ValorDetalleCtaPlanCtas subConcepto = (ValorDetalleCtaPlanCtas) object;
				totalPeriodo1 = sumar(totalPeriodo1, subConcepto.getValor());
				totalPeriodo2 = sumar(totalPeriodo2, subConcepto.getValorN_1());
				totalPeriodo3 = sumar(totalPeriodo3, subConcepto.getValorN_2());
			}
		}

		totales.put(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.PRIMER_PERIODO, new Double(totalPeriodo1.doubleValue()));
		totales.put(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.SEGUNDO_PERIODO, new Double(totalPeriodo2.doubleValue()));
		totales.put(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.TERCER_PERIODO, new Double(totalPeriodo3.doubleValue()));

		return totales;
	}

	public void homologarValoresCuenta(ConceptoInfoComplementaria concepto, Vaciado vac1, Vaciado vac2, Vaciado vac3) {
		Integer idMonedaVac1 = vac1.getIdMoneda();
		Integer idUnidadVac1 = vac1.getUnidMedida();
		ConversorMoneda cm = new ConversorMonedaImpl();
		Double montoHomologado;

		if (vac2 != null && concepto.getValorPeriodo2() != null && (!vac2.getIdMoneda().equals(idMonedaVac1) || !vac2.getUnidMedida().equals(idUnidadVac1))) {
			montoHomologado = cm.convertirMonedaSegunReglas(concepto.getValorPeriodo2(), vac2.getIdMoneda(), vac2.getUnidMedida(), idMonedaVac1, idUnidadVac1, vac1.getPeriodo());
			concepto.setValorPeriodo2(montoHomologado);
		}

		if (vac3 != null && concepto.getValorPeriodo3() != null && (!vac3.getIdMoneda().equals(idMonedaVac1) || !vac3.getUnidMedida().equals(idUnidadVac1))) {
			montoHomologado = cm.convertirMonedaSegunReglas(concepto.getValorPeriodo3(), vac3.getIdMoneda(), vac3.getUnidMedida(), idMonedaVac1, idUnidadVac1, vac1.getPeriodo());
			concepto.setValorPeriodo3(montoHomologado);
		}
	}

	/*
	 * Totaliza las aperturas por cada vaciado y agrega los resultados en el concepto.
	 */
	private void agregarTotales(ConceptoInfoComplementaria concepto, List aperturas) {
		Map totales = totalizar(aperturas);

		concepto.setTotalPeriodo1((Double) totales.get(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.PRIMER_PERIODO));
		concepto.setTotalPeriodo2((Double) totales.get(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.SEGUNDO_PERIODO));
		concepto.setTotalPeriodo3((Double) totales.get(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.TERCER_PERIODO));
	}

	/*
	 * Agrupa cada apertura por su respectivo concepto.
	 */
	private Collection agruparInfoEspecificaPorConcepto(List lstAperturas) {
		Map mConceptos = new HashMap();

		for (int i = 0; i < lstAperturas.size(); i++) {
			ValorInfoEspecifica valorInfoEspec = (ValorInfoEspecifica) lstAperturas.get(i);
			ConceptoInfoComplementaria concepto = (ConceptoInfoComplementaria) mConceptos.get(valorInfoEspec.getNombreConcepto());

			// Si no existe, se crea un concepto nuevo y se agrupa en base a su Id
			if (concepto == null) {
				// El nombre del concepto y el id del conepto en info. especifica es lo mismo
				concepto = new ConceptoInfoComplementaria(valorInfoEspec.getNombreConcepto(), valorInfoEspec.getNombreConcepto());
				concepto.setSubConceptos(new ArrayList());
				concepto.setIdUnidad(valorInfoEspec.getIdUnidad());

				mConceptos.put(valorInfoEspec.getNombreConcepto(), concepto);
			}

			concepto.getSubConceptos().add(valorInfoEspec);
		}

		return mConceptos.values();
	}

	private BigDecimal sumar(BigDecimal total, Double valorASumar) {
		return (valorASumar != null ? total.add(new BigDecimal(valorASumar.toString())) : total);
	}

	/*
	 * Retorna una instancia de ConceptoInfoComplementaria que contiene la información de Mercado de destino de venta o Estructura de Costos
	 * dependiendo de los parametros pasados.
	 */
	private ConceptoInfoComplementaria getInfoAdicionalMercadoOEstructura(final Integer idTipoInfo, List aperturas) {
		ConceptoInfoComplementaria concepto = null;
		GestorClasificaciones gc = new GestorClasificacionesImpl();
		Clasificacion tipoInformacion;

		tipoInformacion = gc.buscarClasificacionPorId(idTipoInfo);
		concepto = new ConceptoInfoComplementaria(tipoInformacion.getIdClasif().toString(), tipoInformacion.getDescripcion());

		if (aperturas != null && !aperturas.isEmpty()) {
			agregarTotales(concepto, aperturas);

			concepto.setSubConceptos(aperturas);
		}

		return concepto;
	}

	/*
	 * Retorna la lista con los tipos de informacion adicional existente
	 */
	private Collection getTiposInfoAdicionales() {
		return new GestorClasificacionesImpl().buscarClasificacionesPorCategoria(ConstantesSEFE.ID_CLASIF_TIPO_INFO_ADICIONAL);
	}

	private String getUsuarioModif(Long idUsuarioModif) {
		GestorUsuarios gu;
		Usuario usuario;

		if (idUsuarioModif == null)
			return null;

		gu = new GestorUsuariosImpl();
		usuario = gu.obtenerUsuario(idUsuarioModif);

		return (usuario != null ? usuario.getCodigoUsuario() : null);
	}

	/*
	 * Retorna los valores de la cuenta aperturable (Cuenta de Segmento) para cada vaciado.
	 */
	private Cuenta getValoresCuentaInfoSegmento(Long idCta, Vaciado vac1, Vaciado vac2, Vaciado vac3) {
		VaciadosDAO vacDao = new VaciadosDAOImpl();
		Long idVac = vac1.getIdVaciado();
		Integer idTipoPlan = vac1.getIdTipoPlan();
		Integer idNombrePlan = vac1.getIdNombrePlanCtas();
		Long idVac2 = (vac2 != null ? vac2.getIdVaciado() : null);
		Long idVac3 = (vac3 != null ? vac3.getIdVaciado() : null);

		return vacDao.buscarValoresCuentaSegmento(idVac, idVac2, idVac3, idTipoPlan, idNombrePlan, idCta);
	}

	/*
	 * Recupera todas las aperturas de una cuenta de segmento para los vaciados pasados como parametro.
	 */
	private List getAperturasInfoSegmento(Long idCta, Vaciado vac1, Vaciado vac2, Vaciado vac3) {
		GestorVaciados gv = new GestorVaciadosImpl();
		Long idVac = vac1.getIdVaciado();
		Integer idTipoPlan = vac1.getIdTipoPlan();
		Integer idNombrePlan = vac1.getIdNombrePlanCtas();
		Long idVac2 = (vac2 != null ? vac2.getIdVaciado() : null);
		Long idVac3 = (vac3 != null ? vac3.getIdVaciado() : null);

		return gv.buscarAperturasCuentasSegmento(idVac, idVac2, idVac3, idTipoPlan, idNombrePlan, idCta);
	}

	private String getNombreClasificacion(Integer id) {
		GestorClasificaciones gc = new GestorClasificacionesImpl();
		Clasificacion cla = gc.buscarClasificacionPorId(id);
		return (cla != null ? cla.getNombre() : null);
	}

	private String getTxtAjustado(Integer flag) {
		return (ConstantesSEFE.FLAG_VACIADO_AJUSTADO.equals(flag) ? ConstantesSEFE.OPCION_SI : ConstantesSEFE.OPCION_NO);
	}
}
