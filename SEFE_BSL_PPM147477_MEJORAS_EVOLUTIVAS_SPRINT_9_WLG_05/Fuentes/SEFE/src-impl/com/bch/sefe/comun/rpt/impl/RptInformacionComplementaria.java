package com.bch.sefe.comun.rpt.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.jfree.util.Log;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.ServicioReportes;
import com.bch.sefe.comun.impl.ComparadorVaciadoFechaBalanceMenorAMayor;
import com.bch.sefe.comun.impl.ServicioReportesImpl;
import com.bch.sefe.comun.rpt.ConstantesReportes;
import com.bch.sefe.comun.rpt.SEFEDataSource;
import com.bch.sefe.comun.rpt.SEFEReportePOI;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.GestorClasificaciones;
import com.bch.sefe.comun.srv.GestorReportes;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.srv.impl.GestorClasificacionesImpl;
import com.bch.sefe.comun.srv.impl.GestorReportesImpl;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.comun.vo.ValorDetalleCtaPlanCtas;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.ServicioVaciados;
import com.bch.sefe.vaciados.impl.ServicioVaciadosImpl;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.IGestorInfoComplementaria;
import com.bch.sefe.vaciados.srv.impl.GestorInfoComplementaria;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Comentario;
import com.bch.sefe.vaciados.vo.ConceptoInfoComplementaria;
import com.bch.sefe.vaciados.vo.Vaciado;
import com.bch.sefe.vaciados.vo.ValorInfoComplementaria;

import edu.emory.mathcs.backport.java.util.Arrays;

public class RptInformacionComplementaria implements SEFEReportePOI {
	private Vaciado vacOrig;
	private Vaciado vac2;
	private Vaciado vac3;
	private List listaSegmentos;
	private String rutCliente;
	private Integer idMonedaDestino;
	private Integer idUnidadDestino;
	private List lstIdsVac;
	private List lstVacOrdenados;
	private int fila;
	private Map mapDiaHabil = new HashMap();
	private ConceptoInfoComplementaria conceptoInfoAdicMercadoVenta;
	private ConceptoInfoComplementaria conceptoEstructuraCosto;
	private List lstInfoAdicional;
	private List lstInfoEspecifica;
	private ServicioReportes srvR = new ServicioReportesImpl();
	private String flagBorrador = "false";
	GestorReportes gstRpt = new GestorReportesImpl();
	ServicioVaciados srvVaciado = new ServicioVaciadosImpl();

	public void setParametros(HSSFSheet planilla, Map parametros) {
		obtenerParametrosEntrada(parametros);
		imprimirVaciados(planilla);
		imprimeDatosCliente(planilla);
		imprimeSegmentos(planilla);
		srvR.insertarValorEnCelda(planilla, 28, 3, flagBorrador);
		imprimeInfoAdicional(planilla);
		imprimeInfoEspecifica(planilla);
	}

	private void imprimeInfoEspecifica(HSSFSheet planilla) {
		srvR.insertarValorEnCelda(planilla, fila, 1, String.valueOf(ConstantesSEFE.INTEGER_CERO));
		int filaG=fila;
		int cantInfoEspe = getLstInfoEspecifica().size();
		srvR.insertarValorEnCelda(planilla, fila, 3, String.valueOf(ConstantesSEFE.CHR_GUION));
		srvR.insertarValorEnCelda(planilla, fila, 4, "Informaci\u00F3n Especifica");
		fila++;
		for (int i = 0; i < getLstInfoEspecifica().size(); i++) {
			ConceptoInfoComplementaria conceptoInfoComplementaria = new ConceptoInfoComplementaria();
			conceptoInfoComplementaria = (ConceptoInfoComplementaria) getLstInfoEspecifica().get(i);
			boolean existeApertura = existenValoresEnAperturas(conceptoInfoComplementaria);
			if (existeApertura) {
				imprimeConcepto(planilla, conceptoInfoComplementaria);
			} else {
				cantInfoEspe--;
			}
		}
		srvR.insertarValorEnCelda(planilla, filaG, 2, String.valueOf(cantInfoEspe));
	}

	private void imprimeInfoAdicional(HSSFSheet planilla) {
		srvR.insertarValorEnCelda(planilla, fila, 1, String.valueOf(ConstantesSEFE.INTEGER_CERO));
		int cantInfoAdic = getLstInfoAdicional().size();
		boolean existeAperturaMercVenta = existenValoresEnAperturas(getConceptoInfoAdicMercadoVenta());
		boolean existeAperturaEstCosto = existenValoresEnAperturas(getConceptoEstructuraCosto());
		if (existeAperturaMercVenta) {
			cantInfoAdic++;
		}
		if (existeAperturaEstCosto) {
			cantInfoAdic++;
		}
		int filaG = fila;
		srvR.insertarValorEnCelda(planilla, fila, 3, String.valueOf(ConstantesSEFE.CHR_GUION));
		srvR.insertarValorEnCelda(planilla, fila, 4, "Informaci\u00F3n Adicional");
		fila++;

		for (int i = 0; i < getLstInfoAdicional().size(); i++) {
			ConceptoInfoComplementaria conceptoInfoComplementaria = new ConceptoInfoComplementaria();
			conceptoInfoComplementaria = (ConceptoInfoComplementaria) getLstInfoAdicional().get(i);
			boolean existeApertura = existenValoresEnAperturas(conceptoInfoComplementaria);
			if (existeApertura) {
				conceptoInfoComplementaria.setIdUnidad(null); // removiendo unidad de medida
				imprimeConcepto(planilla, conceptoInfoComplementaria);
			} else {
				cantInfoAdic--;
			}
		}
		srvR.insertarValorEnCelda(planilla, filaG, 2, String.valueOf(cantInfoAdic));
		// Se imprime Info Adicional Mercado Venta
		if (existeAperturaMercVenta) {
			imprimeConcepto(planilla, getConceptoInfoAdicMercadoVenta());
		}
		if (existeAperturaEstCosto) {
			imprimeConcepto(planilla, getConceptoEstructuraCosto());
		}
	}

	private boolean existenValoresEnAperturas(ConceptoInfoComplementaria concepto) {
		boolean existenAperturas = false;
		List lstAperturas = concepto.getSubConceptos();
		if (lstAperturas != null) {
			for (Iterator iterator = lstAperturas.iterator(); iterator.hasNext();) {
				ValorInfoComplementaria objetoInfoCom = (ValorInfoComplementaria) iterator.next();
				if (objetoInfoCom.getValorVaciado1() != null || objetoInfoCom.getValorVaciado2() != null
						|| objetoInfoCom.getValorVaciado3() != null) {
					return true;
				}
			}
		}
		return existenAperturas;
	}

	private void imprimeConcepto(HSSFSheet planilla, ConceptoInfoComplementaria concepto) {
		final String AJUSTE_CONSOLIDACION = "AJUSTES DE CONSOLIDACI\u00D3N";
		srvR.insertarValorEnCelda(planilla, fila, 1, String.valueOf(ConstantesSEFE.INTEGER_UNO));
		srvR.insertarValorEnCelda(planilla, fila, 2, String.valueOf(ConstantesSEFE.CHR_GUION));
		if (concepto.getSubConceptos() != null) {
			srvR.insertarValorEnCelda(planilla, fila, 3, String.valueOf(concepto.getSubConceptos().size()));
		} else {
			srvR.insertarValorEnCelda(planilla, fila, 3, String.valueOf("0"));
		}
		srvR.insertarValorEnCelda(planilla, fila, 4, concepto.getNombre());
		if (concepto.getIdUnidad() != null) {
			srvR.insertarValorEnCelda(planilla, fila, 5, obtenerNombreClasificacion(concepto.getIdUnidad()));
		}
		srvR.insertarValorEnCelda(planilla, fila, 6, concepto.getId());
		srvR.insertarValorEnCelda(planilla, fila, 9, concepto.getId());
		srvR.insertarValorEnCelda(planilla, fila, 12, concepto.getId());
		if (getLstVacOrdenados().size() == 3) {
			srvR.insertarValorEnCelda(planilla, fila, 7, concepto.getValorPeriodo1());
			srvR.insertarValorEnCelda(planilla, fila, 10, concepto.getValorPeriodo2());
			srvR.insertarValorEnCelda(planilla, fila, 13, concepto.getValorPeriodo3());
		} else if (getLstVacOrdenados().size() == 2) {
			srvR.insertarValorEnCelda(planilla, fila, 10, concepto.getValorPeriodo1());
			srvR.insertarValorEnCelda(planilla, fila, 13, concepto.getValorPeriodo2());
		} else {
			srvR.insertarValorEnCelda(planilla, fila, 13, concepto.getValorPeriodo1());
		}

		if (concepto.getSubConceptos() != null) {
			List subConceptos = concepto.getSubConceptos();
			ValorDetalleCtaPlanCtas valorConceptoAjusteConsol = null;
			for (int j = 0; j < subConceptos.size(); j++) {
				Object object = null;
				object = (Object) subConceptos.get(j);
				if (object instanceof ValorDetalleCtaPlanCtas) {
					ValorDetalleCtaPlanCtas subConcepto = (ValorDetalleCtaPlanCtas) object;					
					if (AJUSTE_CONSOLIDACION.equals(subConcepto.getApertura())) {
						valorConceptoAjusteConsol = subConcepto;
						//Comparacion para conocer si el ajuste de consolidacion es distinto de cero
						//-----------------------------------------------------------
						
						if (getLstVacOrdenados().size() == 3) {
							
							Double valorAjuste= valorConceptoAjusteConsol.getValor();
							Double valorAjuste1= valorConceptoAjusteConsol.getValorN_1();
							Double valorAjuste2= valorConceptoAjusteConsol.getValorN_2();
							
							if (valorAjuste != null){
								if ( valorAjuste.compareTo(new Double (0.0)) != 0.0){
									Double totalPeriodo = concepto.getTotalPeriodo1();
									Double valorPeriodo = concepto.getValorPeriodo1();
									if(!totalPeriodo.equals(valorPeriodo) ){
										//Balance descuadrado
										flagBorrador = "true";
									}
								}
							}
							if (valorAjuste1 != null){
								if ( valorAjuste1.compareTo(new Double (0.0)) != 0.0){
									Double totalPeriodo = concepto.getTotalPeriodo2();
									Double valorPeriodo = concepto.getValorPeriodo2();
									if(!totalPeriodo.equals(valorPeriodo) ){
										//Balance descuadrado
										flagBorrador = "true";
									}
								}
							}
							
							if (valorAjuste2 != null){
								if ( valorAjuste2.compareTo(new Double (0.0)) != 0.0){
									Double totalPeriodo = concepto.getTotalPeriodo3();
									Double valorPeriodo = concepto.getValorPeriodo3();
									if(!totalPeriodo.equals(valorPeriodo) ){
										//Balance descuadrado
										flagBorrador = "true";
									}
								}
							}
							
							
							
						} else if (getLstVacOrdenados().size() == 2) {
							Double valorAjuste= valorConceptoAjusteConsol.getValor();
							Double valorAjuste1= valorConceptoAjusteConsol.getValorN_1();
							
							if (valorAjuste != null){
								if ( valorAjuste.compareTo(new Double (0.0)) != 0.0){
									Double totalPeriodo = concepto.getTotalPeriodo1();
									Double valorPeriodo = concepto.getValorPeriodo1();
									if(!totalPeriodo.equals(valorPeriodo) ){
										//Balance descuadrado
										flagBorrador = "true";
									}
								}
							}
							if (valorAjuste1 != null){
								if ( valorAjuste1.compareTo(new Double (0.0)) != 0.0){
									Double totalPeriodo = concepto.getTotalPeriodo2();
									Double valorPeriodo = concepto.getValorPeriodo2();
									if(!totalPeriodo.equals(valorPeriodo) ){
										//Balance descuadrado
										flagBorrador = "true";
									}
							}
								
								
								
							}
						} else {
							Double valorAjuste= valorConceptoAjusteConsol.getValor();
							if (( valorAjuste.compareTo(new Double (0.0)) != 0.0) && (valorAjuste != null)){
								
								Double totalPeriodo = concepto.getTotalPeriodo1();
								Double valorPeriodo = concepto.getValorPeriodo1();
								if(!totalPeriodo.equals(valorPeriodo) ){
									//Balance descuadrado
									flagBorrador = "true";
								}
							}
						}
			
						//------------------------------------------------------/
						continue;
					}
					fila++;
					//////////////////
					Double totalPeriodo = new Double(0);
					Double valorPeriodo = new Double(0);
					Double totalPeriodo1 = new Double(0);
					Double valorPeriodo1 = new Double(0);
					Double totalPeriodo2 = new Double(0);
					Double valorPeriodo2 = new Double(0);
					if (getLstVacOrdenados().size() == 3) {
						totalPeriodo = concepto.getTotalPeriodo1();
						valorPeriodo = concepto.getValorPeriodo1();
						totalPeriodo1 = concepto.getTotalPeriodo2();
						valorPeriodo1 = concepto.getValorPeriodo2();
						totalPeriodo2 = concepto.getTotalPeriodo3();
						valorPeriodo2 = concepto.getValorPeriodo3();
					} else if (getLstVacOrdenados().size() == 2) {
						totalPeriodo = concepto.getTotalPeriodo1();
						valorPeriodo = concepto.getValorPeriodo1();
						totalPeriodo1 = concepto.getTotalPeriodo2();
						valorPeriodo1 = concepto.getValorPeriodo2();
					} else {
						totalPeriodo = concepto.getTotalPeriodo1();
						valorPeriodo = concepto.getValorPeriodo1();
					}
					
					if(!totalPeriodo.equals(valorPeriodo) ||  !totalPeriodo1.equals(valorPeriodo1)
							|| !totalPeriodo2.equals(valorPeriodo2)){
						//Balance descuadrado
						flagBorrador = "true";
					}
					///////////////////
					srvR.insertarValorEnCelda(planilla, fila, 1, String.valueOf(2));
					srvR.insertarValorEnCelda(planilla, fila, 2, String.valueOf(ConstantesSEFE.CHR_GUION));
					srvR.insertarValorEnCelda(planilla, fila, 3, String.valueOf(ConstantesSEFE.CHR_GUION));
					srvR.insertarValorEnCelda(planilla, fila, 4, subConcepto.getApertura());					
					srvR.insertarValorEnCelda(planilla, fila, 6, subConcepto.getCtaId().toString());
					srvR.insertarValorEnCelda(planilla, fila, 9, subConcepto.getCtaId().toString());
					srvR.insertarValorEnCelda(planilla, fila, 12, subConcepto.getCtaId().toString());
					if (getLstVacOrdenados().size() == 3) {
						srvR.insertarValorEnCelda(planilla, fila, 7, subConcepto.getValor());
						srvR.insertarValorEnCelda(planilla, fila, 10, subConcepto.getValorN_1());
						srvR.insertarValorEnCelda(planilla, fila, 13, subConcepto.getValorN_2());
					} else if (getLstVacOrdenados().size() == 2) {
						srvR.insertarValorEnCelda(planilla, fila, 10, subConcepto.getValor());
						srvR.insertarValorEnCelda(planilla, fila, 13, subConcepto.getValorN_1());
					} else {
						srvR.insertarValorEnCelda(planilla, fila, 13, subConcepto.getValor());
					}
				} else if (object instanceof ValorInfoComplementaria) {
					fila++;
					//////////////////
					
					if (getLstVacOrdenados().size() == 3) {
						Double totalPeriodo1 = concepto.getTotalPeriodo1();
						Double valorPeriodo1 = concepto.getValorPeriodo1();
						Double totalPeriodo2 = concepto.getTotalPeriodo2();
						Double valorPeriodo2 = concepto.getValorPeriodo2();
						Double totalPeriodo3 = concepto.getTotalPeriodo2();
						Double valorPeriodo3 = concepto.getValorPeriodo2();
						if(!totalPeriodo1.equals(valorPeriodo1) || !totalPeriodo2.equals(valorPeriodo2) ||
								!totalPeriodo3.equals(valorPeriodo3) ){
							//Balance descuadrado
							flagBorrador = "true";
						}
					} else if (getLstVacOrdenados().size() == 2) {
						Double totalPeriodo1 = concepto.getTotalPeriodo1();
						Double valorPeriodo1 = concepto.getValorPeriodo1();
						Double totalPeriodo2 = concepto.getTotalPeriodo2();
						Double valorPeriodo2 = concepto.getValorPeriodo2();
						if(!totalPeriodo1.equals(valorPeriodo1) || !totalPeriodo2.equals(valorPeriodo2)){
							//Balance descuadrado
							flagBorrador = "true";
						}
					} else {
						Double totalPeriodo1 = concepto.getTotalPeriodo1();
						Double valorPeriodo1 = concepto.getValorPeriodo1();
						if(!totalPeriodo1.equals(valorPeriodo1)){
							//Balance descuadrado
							flagBorrador = "true";
						}
					}
					ValorInfoComplementaria subConcepto = (ValorInfoComplementaria) object;
					srvR.insertarValorEnCelda(planilla, fila, 1, String.valueOf(2));
					srvR.insertarValorEnCelda(planilla, fila, 2, String.valueOf(ConstantesSEFE.CHR_GUION));
					srvR.insertarValorEnCelda(planilla, fila, 3, String.valueOf(ConstantesSEFE.CHR_GUION));
					srvR.insertarValorEnCelda(planilla, fila, 4, subConcepto.getApertura());
					//Req Asociado 7.4.8.2 Unidad de medida individualizada para cada apertura en Info. Complementaria
					if (subConcepto.getIdUnidad() != null) {
						srvR.insertarValorEnCelda(planilla, fila, 5, obtenerNombreClasificacion(subConcepto.getIdUnidad()));
					}
					
					if (getLstVacOrdenados().size() == 3) {
						srvR.insertarValorEnCelda(planilla, fila, 7, subConcepto.getValorVaciado1());
						srvR.insertarValorEnCelda(planilla, fila, 10, subConcepto.getValorVaciado2());
						srvR.insertarValorEnCelda(planilla, fila, 13, subConcepto.getValorVaciado3());
					} else if (getLstVacOrdenados().size() == 2) {
						srvR.insertarValorEnCelda(planilla, fila, 10, subConcepto.getValorVaciado1());
						srvR.insertarValorEnCelda(planilla, fila, 13, subConcepto.getValorVaciado2());
					} else {
						srvR.insertarValorEnCelda(planilla, fila, 13, subConcepto.getValorVaciado1());
					}
				}
			}
			if (valorConceptoAjusteConsol != null) {
				fila++;
				srvR.insertarValorEnCelda(planilla, fila, 1, String.valueOf(2));
				srvR.insertarValorEnCelda(planilla, fila, 2, String.valueOf(ConstantesSEFE.CHR_GUION));
				srvR.insertarValorEnCelda(planilla, fila, 3, String.valueOf(ConstantesSEFE.CHR_GUION));
				srvR.insertarValorEnCelda(planilla, fila, 4, valorConceptoAjusteConsol.getApertura());
				if (getLstVacOrdenados().size() == 3) {
					srvR.insertarValorEnCelda(planilla, fila, 7, valorConceptoAjusteConsol.getValor());
					srvR.insertarValorEnCelda(planilla, fila, 10, valorConceptoAjusteConsol.getValorN_1());
					srvR.insertarValorEnCelda(planilla, fila, 13, valorConceptoAjusteConsol.getValorN_2());
				} else if (getLstVacOrdenados().size() == 2) {
					srvR.insertarValorEnCelda(planilla, fila, 10, valorConceptoAjusteConsol.getValor());
					srvR.insertarValorEnCelda(planilla, fila, 13, valorConceptoAjusteConsol.getValorN_1());
				} else {
					srvR.insertarValorEnCelda(planilla, fila, 13, valorConceptoAjusteConsol.getValor());
				}
			}
		}
		fila++;
	}

	private void imprimeSegmentos(HSSFSheet planilla) {
		List listaSegmentos = getListaSegmentos();
		fila = 41;
		srvR.insertarValorEnCelda(planilla, fila, 1, String.valueOf(ConstantesSEFE.INTEGER_CERO));
		srvR.insertarValorEnCelda(planilla, fila, 2, String.valueOf(listaSegmentos.size()));
		srvR.insertarValorEnCelda(planilla, fila, 3, String.valueOf(ConstantesSEFE.CHR_GUION));
		srvR.insertarValorEnCelda(planilla, fila, 4, "Informaci\u00F3n por Segmento");
		fila++;
		for (int i = 0; i < listaSegmentos.size(); i++) {
			ConceptoInfoComplementaria conceptoInfoComplementaria = new ConceptoInfoComplementaria();
			conceptoInfoComplementaria = (ConceptoInfoComplementaria) listaSegmentos.get(i);
			imprimeConcepto(planilla, conceptoInfoComplementaria);
		}
	}

	private void imprimeDatosCliente(HSSFSheet planilla) {
		GestorServicioClientes srvCli = new GestorServicioClientesImpl();
		GestorReportes gstRpt = new GestorReportesImpl();
		Cliente cliente = srvCli.obtenerClientePorRut(getRutCliente());
		srvR.insertarValorEnCelda(planilla, 2, 5, gstRpt.getNombreEmpresaDespliegue(cliente, getVacOrig()));
		srvR.insertarValorEnCelda(planilla, 3, 5, cliente.getRut());
		srvR.insertarValorEnCelda(planilla, 4, 5, FormatUtil.formatDate(new Date()));
		srvR.insertarValorEnCelda(planilla, 5, 5, obtenerNombreClasificacion(cliente.getSubSectorId()));
		srvR.insertarValorEnCelda(planilla, 6, 5, obtenerNombreClasificacion(getIdMonedaDestino()));
		srvR.insertarValorEnCelda(planilla, 6, 6, obtenerNombreClasificacion(getIdUnidadDestino()));

	}

	private void imprimirVaciados(HSSFSheet planilla) {
		GestorUsuarios gstUsu = new GestorUsuariosImpl();
		int indexColumnaValor = 6;
		if (getLstVacOrdenados().size() == 2) {
			indexColumnaValor = 9;
		} else if (getLstVacOrdenados().size() == 1) {
			indexColumnaValor = 12;
		}
		for (int i = 0; i < getLstVacOrdenados().size(); i++) {
			Vaciado vac = (Vaciado) getLstVacOrdenados().get(i);
			Usuario usu = gstUsu.obtenerUsuario(vac.getIdUsuModifInfoComp());
			srvR.insertarValorEnCelda(planilla, 8, indexColumnaValor, obtenerNombreClasificacion(vac.getIdTipoPlan()));
			srvR.insertarValorEnCelda(planilla, 9, indexColumnaValor, obtenerNombreClasificacion(vac.getIdTpoVaciado()));
			srvR.insertarValorEnCelda(planilla, 10, indexColumnaValor, FormatUtil.formatDateRptFF(vac.getPeriodo()));
			srvR.insertarValorEnCelda(planilla, 11, indexColumnaValor, castingBigDecimal(vac.getMesesPer()));
			srvR.insertarValorEnCelda(planilla, 12, indexColumnaValor, obtenerNombreClasificacion(vac.getIdMoneda()));
			srvR.insertarValorEnCelda(planilla, 13, indexColumnaValor, obtenerNombreClasificacion(vac.getUnidMedida()));
			srvR.insertarValorEnCelda(planilla, 14, indexColumnaValor, obtenerNombreClasificacion(vac.getIdAudit()));
			srvR.insertarValorEnCelda(planilla, 15, indexColumnaValor, obtenerNombreClasificacion(vac.getIdTipoBalance()));
			srvR.insertarValorEnCelda(planilla, 16, indexColumnaValor, obtenerFactorConversion(vac));
			srvR.insertarValorEnCelda(planilla, 17, indexColumnaValor, usu != null ? usu.getCodigoUsuario() : "");
			srvR.insertarValorEnCelda(planilla, 18, indexColumnaValor, usu != null ? usu.getArea() : "");
			srvR.insertarValorEnCelda(planilla, 19, indexColumnaValor, obtenerNombreClasificacion(vac.getIdClasifCalidad()));
			srvR.insertarValorEnCelda(planilla, 20, indexColumnaValor, esVaciadoAjustado(vac));
			srvR.insertarValorEnCelda(planilla, 21, indexColumnaValor, obtenerNombreClasificacion(vac.getIdEstado()));
			srvR.insertarValorEnCelda(planilla, 22, indexColumnaValor, FormatUtil.formatDateRptFF(vac.getPeriodo()));
			srvR.insertarValorEnCelda(planilla, 23, indexColumnaValor, obtenerNombreClasificacion(vac.getIdFuenteConsol()));
			srvR.insertarValorEnCelda(planilla, 24, indexColumnaValor, obtenerNotaVaciado(vac.getIdVaciado()));
			indexColumnaValor += 3;
		}
	}

	private Double obtenerFactorConversion(Vaciado vac) {

		if (getIdMonedaDestino() != null && getIdUnidadDestino() != null) {
			ConversorMoneda conversor = new ConversorMonedaImpl();
			return conversor.convertirMonedaSegunReglas(Double.valueOf("1"), vac.getIdMoneda(), vac.getUnidMedida(), getIdMonedaDestino(),
					getIdUnidadDestino(), vac.getPeriodo());
		}

		return null;
	}

	private String obtenerNombreClasificacion(Integer idCategoria) {
		Clasificacion clas = obtenerClasificacion(idCategoria);

		return clas.getNombre();
	}

	private String obtenerNotaVaciado(Long idVaciado) {
		GestorVaciados gestVac = new GestorVaciadosImpl();
		Comentario nota = new Comentario();
		nota.setIdVaciado(idVaciado);

		// Se obtiene la nota del vaciado
		nota = gestVac.getNota(nota);
		String notaStr = null;
		if (nota != null) {
			notaStr = nota.getNota();
		}
		return notaStr;
	}

	private String esVaciadoAjustado(Vaciado vaciado) {
		String valor = null;

		if (ConstantesReportes.VACIADO_NO_AJUSTADO.equals(vaciado.getAjustadoFlg())) {
			valor = "Original";
		}

		if (ConstantesReportes.VACIADO_AJUSTADO.equals(vaciado.getAjustadoFlg())) {
			valor = "Ajustada";
		}

		return valor;
	}

	/**
	 * Transforma los tipos Double, Integer en BigDecimal
	 * 
	 * @param valor
	 * @return
	 */
	private BigDecimal castingBigDecimal(Object valor) {
		BigDecimal big = null;

		if (valor != null) {

			if (valor instanceof Double) {
				big = new BigDecimal(((Double) valor).doubleValue());
			}
			if (valor instanceof Integer) {
				big = new BigDecimal(((Integer) valor).intValue());
			}
		}
		return big;
	}

	private Clasificacion obtenerClasificacion(Integer idCategoria) {
		GestorClasificaciones gestorClasif = new GestorClasificacionesImpl();

		Clasificacion clas = gestorClasif.buscarClasificacionPorId(idCategoria);

		return clas;
	}

	private void obtenerParametrosEntrada(Map parametros) {
		setLstIdsVac(((List) parametros.get(ConstantesReportes.LST_ID_VACIADO)));
		setRutCliente((String) parametros.get(ConstantesReportes.RUT_CLIENTE));
		setIdMonedaDestino((Integer) parametros.get(ConstantesReportes.MONEDA));
		setIdUnidadDestino((Integer) parametros.get(ConstantesReportes.UNIDAD));
		setLstVacOrdenados(ordenaListaVaciados());
		obtenerListaSegmentos();
		obtenerInfoAdicionalMercado();
		obtenerEstructuraCostos();
		obtenerInfoAdicional();
		obtenerInfoEspecifica();
	}

	private List ordenaListaVaciados() {
		List lista = new ArrayList();

		for (int i = 0; i < getLstIdsVac().size(); ++i) {
			lista.add(obtenerVaciado((Long) getLstIdsVac().get(i)));
		}

		Object[] vaciados = lista.toArray();
		Arrays.sort(vaciados, new ComparadorVaciadoFechaBalanceMenorAMayor());

		List listaOrdenada = Arrays.asList(vaciados);
		obtenerVaciadosSingulares(listaOrdenada);
		return listaOrdenada;
	}

	private void obtenerVaciadosSingulares(List vaciados) {
		try {
			if (vaciados.size() == 3) {
				setVac3((Vaciado) vaciados.get(0));
				setVac2((Vaciado) vaciados.get(1));
				setVacOrig((Vaciado) vaciados.get(2));
			} else if (vaciados.size() == 2) {
				setVac3((Vaciado) vaciados.get(0));
				setVac2((Vaciado) vaciados.get(1));
			} else {
				setVac3((Vaciado) vaciados.get(0));
			}
		} catch (Exception e) {
			if (Log.isDebugEnabled()) {
				Log.debug("Se estan obteniendo menos de 3 vaciados para consultar las empresas incluidas", e);
			}
		}
	}

	private Vaciado obtenerVaciado(Long idVaciado) {
		GestorVaciados gestorVac = new GestorVaciadosImpl();
		return gestorVac.buscarVaciado(idVaciado);
	}

	private void obtenerListaSegmentos() {
		IGestorInfoComplementaria gic = new GestorInfoComplementaria();
		List segmentos = gic.buscarInfoSegmento(getVac3(), getVac2(), getVacOrig());
		setListaSegmentos(segmentos);
	}

	private void obtenerInfoAdicionalMercado() {
		IGestorInfoComplementaria gic = new GestorInfoComplementaria();
		setConceptoInfoAdicMercadoVenta(gic.buscarInfoAdicionalMercado(getVac3(), getVac2(), getVacOrig()));
	}

	private void obtenerEstructuraCostos() {
		IGestorInfoComplementaria gic = new GestorInfoComplementaria();
		setConceptoEstructuraCosto(gic.buscarInfoAdicionalEstructura(getVac3(), getVac2(), getVacOrig()));
	}

	private void obtenerInfoAdicional() {
		IGestorInfoComplementaria gic = new GestorInfoComplementaria();
		setLstInfoAdicional(gic.buscarInfoAdicionalReporte(getVac3(), getVac2(), getVacOrig()));
	}

	private void obtenerInfoEspecifica() {
		IGestorInfoComplementaria gic = new GestorInfoComplementaria();
		setLstInfoEspecifica(gic.buscarInfoEspecificaReporte(getVac3(), getVac2(), getVacOrig()));
	}

	public Map crearDataSources(Map parametros) {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public Map crearImagenes() {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public SEFEDataSource obtenerDataSource() {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public Vaciado getVacOrig() {
		return vacOrig;
	}

	public void setVacOrig(Vaciado vacOrig) {
		this.vacOrig = vacOrig;
	}

	public Vaciado getVac2() {
		return vac2;
	}

	public void setVac2(Vaciado vac2) {
		this.vac2 = vac2;
	}

	public Vaciado getVac3() {
		return vac3;
	}

	public void setVac3(Vaciado vac3) {
		this.vac3 = vac3;
	}

	public List getListaSegmentos() {
		return listaSegmentos;
	}

	public void setListaSegmentos(List listaSegmentos) {
		this.listaSegmentos = listaSegmentos;
	}

	public String getRutCliente() {
		return rutCliente;
	}

	public void setRutCliente(String rutCliente) {
		this.rutCliente = rutCliente;
	}

	public Integer getIdMonedaDestino() {
		return idMonedaDestino;
	}

	public void setIdMonedaDestino(Integer idMonedaDestino) {
		this.idMonedaDestino = idMonedaDestino;
	}

	public Integer getIdUnidadDestino() {
		return idUnidadDestino;
	}

	public void setIdUnidadDestino(Integer idUnidadDestino) {
		this.idUnidadDestino = idUnidadDestino;
	}

	public List getLstIdsVac() {
		return lstIdsVac;
	}

	public void setLstIdsVac(List lstIdsVac) {
		this.lstIdsVac = lstIdsVac;
	}

	public List getLstVacOrdenados() {
		return lstVacOrdenados;
	}

	public void setLstVacOrdenados(List lstVacOrdenados) {
		this.lstVacOrdenados = lstVacOrdenados;
	}

	public ConceptoInfoComplementaria getConceptoInfoAdicMercadoVenta() {
		return conceptoInfoAdicMercadoVenta;
	}

	public void setConceptoInfoAdicMercadoVenta(ConceptoInfoComplementaria conceptoInfoAdicMercadoVenta) {
		this.conceptoInfoAdicMercadoVenta = conceptoInfoAdicMercadoVenta;
	}

	public ConceptoInfoComplementaria getConceptoEstructuraCosto() {
		return conceptoEstructuraCosto;
	}

	public void setConceptoEstructuraCosto(ConceptoInfoComplementaria conceptoEstructuraCosto) {
		this.conceptoEstructuraCosto = conceptoEstructuraCosto;
	}

	public List getLstInfoAdicional() {
		return lstInfoAdicional;
	}

	public void setLstInfoAdicional(List lstInfoAdicional) {
		this.lstInfoAdicional = lstInfoAdicional;
	}

	public List getLstInfoEspecifica() {
		return lstInfoEspecifica;
	}

	public void setLstInfoEspecifica(List lstInfoEspecifica) {
		this.lstInfoEspecifica = lstInfoEspecifica;
	}

	public Map getMapDiaHabil() {
		return mapDiaHabil;
	}

	/**
	 * Ingresa un valor al HashMap.
	 * 
	 * @param clave
	 *            ,Fecha del vaciado consultado.
	 * @param date
	 *            ,Fecha dia habil.
	 */
	public void setMapDiaHabil(String clave, Date date) {
		this.mapDiaHabil.put(clave, date);
	}

}
