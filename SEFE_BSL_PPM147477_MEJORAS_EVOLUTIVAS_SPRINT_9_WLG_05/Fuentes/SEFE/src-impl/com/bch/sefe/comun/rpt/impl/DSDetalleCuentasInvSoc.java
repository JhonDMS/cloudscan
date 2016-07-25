package com.bch.sefe.comun.rpt.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.rpt.ConstantesReportes;
import com.bch.sefe.comun.rpt.SEFEDataSource;
import com.bch.sefe.comun.vo.InversionSociedad;

public class DSDetalleCuentasInvSoc implements SEFEDataSource,
		JRRewindableDataSource {

	private List nombreEmpresas;
	private Map valoresEmpresas;
	private List idsVaciados;

	static final String TIPO_INFO_VPP = "VPP";
	static final String TIPO_INFO_RPROP = "RPROP";
	static final String TIPO_INFO_PARTICIP = "PARTICIP";

	static final int PERIODO_1 = 1;
	static final int PERIODO_2 = 2;
	static final int PERIODO_3 = 3;

	int index = -1;

	public String getNombreBloque() {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public String getNombreReporte() {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public DSDetalleCuentasInvSoc(Map parametros) {
		setNombreEmpresas((List) parametros
				.get(ConstantesSEFE.KEYMAP_LISTA_NOMBRES_EMPRESAS_INV_SOC));
		ordenarNombresEmpresas();
		setValoresEmpresas((Map) parametros
				.get(ConstantesSEFE.KEYMAP_LISTA_VALORES_EMPRESAS_INV_SOC));
		obtenerIdsVaciados(parametros);
	}

	private void ordenarNombresEmpresas() {
		List nombresOrdenados = new ArrayList();

		for (int i = 0; i < getNombreEmpresas().size(); i++) {
			String nombre = (String) getNombreEmpresas().get(i);
			if (!ConstantesReportes.NOMBRE_CUENTA_OTROS
					.equalsIgnoreCase(nombre)) {
				nombresOrdenados.add(nombre);
			}
		}

		nombresOrdenados.add(ConstantesReportes.NOMBRE_CUENTA_OTROS);
		setNombreEmpresas(nombresOrdenados);

	}

	private void obtenerIdsVaciados(Map parametros) {
		List idsVaciados = new ArrayList();

		for (int i = 0; i < parametros.size(); i++) {
			Long idVaciado = (Long) parametros
					.get(ConstantesReportes.KEYMAP_INV_SOC_ID_VACIADO + (i + 1));
			if (idVaciado != null) {
				idsVaciados.add(idVaciado.toString());
			}
		}
		setIdsVaciados(idsVaciados);
	}

	public Object getFieldValue(JRField jrField) throws JRException {
		Object value = null;
		String nombreCampo = jrField.getName();
		String nombreEmpresa = new String();
		BigDecimal partic1 = null;
		BigDecimal partic2 = null;
		BigDecimal partic3 = null;
		BigDecimal rprop1 = null;
		BigDecimal rprop2 = null;
		BigDecimal rprop3 = null;
		BigDecimal vpp1 = null;
		BigDecimal vpp2 = null;
		BigDecimal vpp3 = null;
		BigDecimal x = new BigDecimal(0);
		BigDecimal y = new BigDecimal("0E-15");
		/*
		 * Requerimiento 7.2.13 - OTROS
		 */
		if (getNombreEmpresas() != null && !getNombreEmpresas().isEmpty()) {
			nombreEmpresa = (String) getNombreEmpresas().get(index);
		}
		if (nombreEmpresa.equals("OTROS")) {
			partic1 = obtenerInfo(nombreEmpresa, PERIODO_1, TIPO_INFO_PARTICIP);
			if (y.equals(partic1) || x.equals(partic1)) {
				partic1 = null;
			}
			partic2 = obtenerInfo(nombreEmpresa, PERIODO_2, TIPO_INFO_PARTICIP);
			if (y.equals(partic2) || x.equals(partic2)) {
				partic2 = null;
			}
			partic3 = obtenerInfo(nombreEmpresa, PERIODO_3, TIPO_INFO_PARTICIP);
			if (y.equals(partic3) || x.equals(partic3)) {
				partic3 = null;
			}
			rprop1 = obtenerInfo(nombreEmpresa, PERIODO_1, TIPO_INFO_RPROP);
			if (y.equals(rprop1) || x.equals(rprop1)) {
				rprop1 = null;
			}
			rprop2 = obtenerInfo(nombreEmpresa, PERIODO_2, TIPO_INFO_RPROP);
			if (y.equals(rprop2) || x.equals(rprop2) ) {
				rprop2 = null;
			}
			rprop3 = obtenerInfo(nombreEmpresa, PERIODO_3, TIPO_INFO_RPROP);
			if (y.equals(rprop3) || x.equals(rprop3)) {
				rprop3 = null;
			}
			vpp1 = obtenerInfo(nombreEmpresa, PERIODO_1, TIPO_INFO_VPP);
			if (y.equals(vpp1) || x.equals(vpp1)) {
				vpp1 = null;
			}
			vpp2 = obtenerInfo(nombreEmpresa, PERIODO_2, TIPO_INFO_VPP);
			if (y.equals(vpp2) || x.equals(vpp2)) {
				vpp2 = null;
			}
			vpp3 = obtenerInfo(nombreEmpresa, PERIODO_3, TIPO_INFO_VPP);
			if (y.equals(vpp3) || x.equals(vpp3)) {
				vpp3 = null;
			}
		}
		if (nombreEmpresa.equals("OTROS")) {
			if ((partic1 == null && partic2 == null && partic3 == null)
					&& (rprop1 == null && rprop2 == null && rprop3 == null)
					&& (vpp1 == null && vpp2 == null && vpp3 == null))  {

					if (ConstantesReportes.FN_DS_INVSOC_NOMBRE_EMPRESA
							.equalsIgnoreCase(nombreCampo)) {
						value = null;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_PARTIC_PER1
							.equalsIgnoreCase(nombreCampo)) {
						value = partic1;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_PARTIC_PER2
							.equalsIgnoreCase(nombreCampo)) {
						value = partic2;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_PARTIC_PER3
							.equalsIgnoreCase(nombreCampo)) {
						value = partic3;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_RPROP_PER1
							.equalsIgnoreCase(nombreCampo)) {
						value = rprop1;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_RPROP_PER2
							.equalsIgnoreCase(nombreCampo)) {
						value = rprop2;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_RPROP_PER3
							.equalsIgnoreCase(nombreCampo)) {
						value = rprop3;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_VPP_PER1
							.equalsIgnoreCase(nombreCampo)) {
						value = vpp1;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_VPP_PER2
							.equalsIgnoreCase(nombreCampo)) {
						value = vpp2;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_VPP_PER3
							.equalsIgnoreCase(nombreCampo)) {
						value = vpp3;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_ES_ULTIMA_CTA
							.equalsIgnoreCase(nombreCampo)) {
						if (index == getNombreEmpresas().size() - 1) {
							value = new Boolean(true);
						} else {
							value = new Boolean(false);
						}
					}
				} 
		else {
					if (ConstantesReportes.FN_DS_INVSOC_NOMBRE_EMPRESA
							.equalsIgnoreCase(nombreCampo)) {
						value = nombreEmpresa;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_PARTIC_PER1
							.equalsIgnoreCase(nombreCampo)) {
						value = partic1;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_PARTIC_PER2
							.equalsIgnoreCase(nombreCampo)) {
						value = partic2;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_PARTIC_PER3
							.equalsIgnoreCase(nombreCampo)) {
						value = partic3;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_RPROP_PER1
							.equalsIgnoreCase(nombreCampo)) {
						value = rprop1;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_RPROP_PER2
							.equalsIgnoreCase(nombreCampo)) {
						value = rprop2;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_RPROP_PER3
							.equalsIgnoreCase(nombreCampo)) {
						value = rprop3;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_VPP_PER1
							.equalsIgnoreCase(nombreCampo)) {
						value = vpp1;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_VPP_PER2
							.equalsIgnoreCase(nombreCampo)) {
						value = vpp2;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_VPP_PER3
							.equalsIgnoreCase(nombreCampo)) {
						value = vpp3;
					}

					if (ConstantesReportes.FN_DS_INVSOC_VAL_ES_ULTIMA_CTA
							.equalsIgnoreCase(nombreCampo)) {
						if (index == getNombreEmpresas().size() - 1) {
							value = new Boolean(true);
						} else {
							value = new Boolean(false);
						}
					}
				}
				
			
		}

		else  {

			if (ConstantesReportes.FN_DS_INVSOC_NOMBRE_EMPRESA
					.equalsIgnoreCase(nombreCampo)) {
				value = nombreEmpresa;
			}

			if (ConstantesReportes.FN_DS_INVSOC_VAL_PARTIC_PER1
					.equalsIgnoreCase(nombreCampo)) {
				value = obtenerInfo(nombreEmpresa, PERIODO_1,
						TIPO_INFO_PARTICIP);
			}

			if (ConstantesReportes.FN_DS_INVSOC_VAL_PARTIC_PER2
					.equalsIgnoreCase(nombreCampo)) {
				value = obtenerInfo(nombreEmpresa, PERIODO_2,
						TIPO_INFO_PARTICIP);
			}

			if (ConstantesReportes.FN_DS_INVSOC_VAL_PARTIC_PER3
					.equalsIgnoreCase(nombreCampo)) {
				value = obtenerInfo(nombreEmpresa, PERIODO_3,
						TIPO_INFO_PARTICIP);
			}

			if (ConstantesReportes.FN_DS_INVSOC_VAL_RPROP_PER1
					.equalsIgnoreCase(nombreCampo)) {
				value = obtenerInfo(nombreEmpresa, PERIODO_1, TIPO_INFO_RPROP);
			}

			if (ConstantesReportes.FN_DS_INVSOC_VAL_RPROP_PER2
					.equalsIgnoreCase(nombreCampo)) {
				value = obtenerInfo(nombreEmpresa, PERIODO_2, TIPO_INFO_RPROP);
			}

			if (ConstantesReportes.FN_DS_INVSOC_VAL_RPROP_PER3
					.equalsIgnoreCase(nombreCampo)) {
				value = obtenerInfo(nombreEmpresa, PERIODO_3, TIPO_INFO_RPROP);
			}

			if (ConstantesReportes.FN_DS_INVSOC_VAL_VPP_PER1
					.equalsIgnoreCase(nombreCampo)) {
				value = obtenerInfo(nombreEmpresa, PERIODO_1, TIPO_INFO_VPP);
			}

			if (ConstantesReportes.FN_DS_INVSOC_VAL_VPP_PER2
					.equalsIgnoreCase(nombreCampo)) {
				value = obtenerInfo(nombreEmpresa, PERIODO_2, TIPO_INFO_VPP);
			}

			if (ConstantesReportes.FN_DS_INVSOC_VAL_VPP_PER3
					.equalsIgnoreCase(nombreCampo)) {
				value = obtenerInfo(nombreEmpresa, PERIODO_3, TIPO_INFO_VPP);
			}

			if (ConstantesReportes.FN_DS_INVSOC_VAL_ES_ULTIMA_CTA
					.equalsIgnoreCase(nombreCampo)) {
				if (index == getNombreEmpresas().size() - 1) {
					value = new Boolean(true);
				} else {
					value = new Boolean(false);
				}
			}
		}
		return value;
	}

	public BigDecimal obtenerInfo(String nombreEmpresa, int periodo, String tipo) {
		Object valor = null;

		if (periodo <= getIdsVaciados().size()) {
			String idVac = (String) getIdsVaciados().get(periodo - 1);
			Object obj = getValoresEmpresas().get(idVac.concat(nombreEmpresa));
			if (obj != null) {
				InversionSociedad invSoc = (InversionSociedad) obj;

				if (TIPO_INFO_VPP.equalsIgnoreCase(tipo)) {
					valor = invSoc.getVppN();
					valor = convertirObjetoDoubleABigDecimal(valor);
				}

				if (TIPO_INFO_RPROP.equalsIgnoreCase(tipo)) {
					valor = invSoc.getResPropN();
					valor = convertirObjetoDoubleABigDecimal(valor);
				}

				if (TIPO_INFO_PARTICIP.equalsIgnoreCase(tipo)) {
					valor = invSoc.getPorcentPartN();
					valor = convertirObjetoDoubleABigDecimalPorcentaje(valor);
				}
			}
		}

		return (BigDecimal) valor;
	}

	public BigDecimal convertirObjetoDoubleABigDecimal(Object valor) {
		Double doubleValue = (valor != null ? (Double) valor : null);
		BigDecimal bdValue = (doubleValue != null ? new BigDecimal(doubleValue
				.doubleValue()) : null);
		return bdValue;
	}

	public BigDecimal convertirObjetoDoubleABigDecimalPorcentaje(Object valor) {
		Double doubleValue = (valor != null ? (Double) valor : null);
		BigDecimal bdValue = (doubleValue != null ? new BigDecimal(doubleValue
				.doubleValue()) : null);
		if (bdValue != null) {
			bdValue = bdValue.divide(new BigDecimal(100));
		}
		return bdValue;
	}

	public boolean next() throws JRException {
		index++;
		return (index < getNombreEmpresas().size());
	}

	/**
	 * @return el nombreEmpresas
	 */
	public List getNombreEmpresas() {
		return nombreEmpresas;
	}

	/**
	 * @param nombreEmpresas
	 *            el nombreEmpresas a establecer
	 */
	public void setNombreEmpresas(List nombreEmpresas) {
		this.nombreEmpresas = nombreEmpresas;
	}

	/**
	 * @return el valoresEmpresas
	 */
	public Map getValoresEmpresas() {
		return valoresEmpresas;
	}

	/**
	 * @param valoresEmpresas
	 *            el valoresEmpresas a establecer
	 */
	public void setValoresEmpresas(Map valoresEmpresas) {
		this.valoresEmpresas = valoresEmpresas;
	}

	/**
	 * @return el idsVaciados
	 */
	public List getIdsVaciados() {
		return idsVaciados;
	}

	/**
	 * @param idsVaciados
	 *            el idsVaciados a establecer
	 */
	public void setIdsVaciados(List idsVaciados) {
		this.idsVaciados = idsVaciados;
	}

	public void moveFirst() throws JRException {
		index = -1;
	}

}