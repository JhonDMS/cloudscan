package com.bch.sefe.vaciado.client.view;

import com.bch.sefe.comun.utils.SEFEBusyRequestCallback;
import com.bch.sefe.comun.utils.ServiceInvoker;
import com.bch.sefe.comun.utils.XMLUtil;
import com.bch.sefe.comun.vaciados.ConstantesVaciados;
import com.bch.sefe.comun.vo.XMLDataList;
import com.bch.sefe.comun.vo.XMLDataObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

public class ControladorInformacionComplementaria {
	private IInformacionComplementariaView view;
	private Long idVaciado1;
	private Long idVaciado2;
	private Long idVaciado3;

	public ControladorInformacionComplementaria(IInformacionComplementariaView view) {
		this.view = view;
	}

	public void buscarInformacionComplementaria(String idVac, String rutCliente) {
		XMLDataObject xdoReq = new XMLDataObject();

		xdoReq.put(ConstantesVaciados.ID_VACIADO, idVac);
		xdoReq.put(ConstantesVaciados.RUT_CLIENTE, rutCliente);

		ServiceInvoker.sendXMLDataRequest(new BuscarInfoComplementariaCallback(), xdoReq, view.getContexto(), ConstantesVaciados.OPER_BUSCAR_INFO_COMPLEMENTARIA);
	}

	public void guardarInformacionComplementaria(XMLDataList infoSegmento, XMLDataList infoAdicional, XMLDataObject infoAdicionalMercado, XMLDataObject infoAdicionalEstructura,
			XMLDataList infoEspecifica) {
		XMLDataObject xdoReq = new XMLDataObject();

		xdoReq.put(ConstantesVaciados.RUT_CLIENTE, view.getContexto().getRutCliente());
		xdoReq.put(ConstantesVaciados.LOG_OPERADOR, view.getContexto().getLogOperador());
		xdoReq.put(ConstantesVaciados.ID_VACIADO, idVaciado1);
		xdoReq.put(ConstantesVaciados.ID_VACIADO_2, idVaciado2);
		xdoReq.put(ConstantesVaciados.ID_VACIADO_3, idVaciado3);

		if (infoSegmento != null) {
			if (!validarInformacion(infoSegmento, false)) {
				return;
			}
			xdoReq.put(ConstantesVaciados.INFORMACION_COMPLEMENTARIA_INFO_SEGMENTO, infoSegmento);
		}

		if (infoAdicional != null) {
			if (!validarInformacion(infoAdicional, false)) {
				
				return;
			}else if(!validarUnidMedidaInfoEspecifica(infoAdicional, true)){
				return;
			}
			xdoReq.put(ConstantesVaciados.INFORMACION_COMPLEMENTARIA_INFO_ADICIONAL, infoAdicional);
		}

		if (infoAdicionalMercado != null) {
			if (!validarPorcentajes(infoAdicionalMercado)) {
				return;
			}
			xdoReq.put(ConstantesVaciados.INFORMACION_COMPLEMENTARIA_INFO_MERCADO_DESTINO, infoAdicionalMercado);
		}

		if (infoAdicionalEstructura != null) {
			if (!validarPorcentajes(infoAdicionalEstructura)) {
				return;
			}
			xdoReq.put(ConstantesVaciados.INFORMACION_COMPLEMENTARIA_INFO_ESTRUCTURA_COSTOS, infoAdicionalEstructura);
		}

		if (infoEspecifica != null) {
			if (!validarInformacion(infoEspecifica, true)) {
				return;
			}
			xdoReq.put(ConstantesVaciados.INFORMACION_COMPLEMENTARIA_INFO_ESPECIFICA, infoEspecifica);
		}

		if (infoSegmento == null && infoAdicional == null && infoAdicionalMercado == null && infoAdicionalEstructura == null && infoEspecifica == null) {
			view.procesarError(null, "No existe Información Complementaria a guardar", null);
			return;
		}

		ServiceInvoker.sendXMLDataRequest(new GuardarInfoComplementariaCallback(), xdoReq, view.getContexto(), ConstantesVaciados.OPER_GUARDAR_INFO_COMPLEMENTARIA);
	}

	private boolean validarPorcentajes(XMLDataObject xdoInfo) {
		XMLDataList xdlAperturas = (XMLDataList) xdoInfo.getObject(ConstantesVaciados.LST_SUB_CONCEPTOS);

		if (xdlAperturas != null) {
			double total = 0;
			boolean existenValores = false;
			for (int i = 0; i < xdlAperturas.size(); i++) {
				XMLDataObject xdoApertura = (XMLDataObject) xdlAperturas.get(i);
				String sValor = xdoApertura.getString(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.PRIMER_PERIODO);
				Double valor;
				try {
					valor = new Double(sValor);
					existenValores = true;
					total += valor.doubleValue();
				} catch (Exception e) {
					GWT.log("No se ha ingresado ningnu valor", null);
				}
			}

			if (existenValores && total != 100.0) {
				view.procesarError(null, "La suma de los Porcentajes debe ser 100%", null);
				return false;
			}
		}
		return true;
	}

	private boolean validarInformacion(XMLDataList info, boolean validarUnidad) {
		for (int i = 0; i < info.size(); i++) {
			XMLDataObject xdoConcepto = (XMLDataObject) info.get(i);
			XMLDataList xdoAperturas = (XMLDataList) xdoConcepto.getObject(ConstantesVaciados.LST_SUB_CONCEPTOS);
			boolean tablaTieneUnidad = false;
			
			if (xdoConcepto.getString(ConstantesVaciados.FLAG_TABLA_TIENE_UNIDAD) != null) {
				tablaTieneUnidad = new Boolean(xdoConcepto.getString(ConstantesVaciados.FLAG_TABLA_TIENE_UNIDAD)).booleanValue();
			}

			// Se valida la informacion de los conceptos
			if (xdoAperturas != null && xdoAperturas.size() > 0) {
				if (validarUnidad && tablaTieneUnidad) {
					String idUnidad = xdoConcepto.getString(ConstantesVaciados.ID_UNIDAD);
					if (idUnidad == null || "-1".equals(idUnidad)) {
						view.procesarError(null, "Falta seleccionar la Unidad correspondiente", null);
						return false;
					}
				}

				for (int j = 0; j < xdoAperturas.size(); j++) {
					if (!validarApertura((XMLDataObject) xdoAperturas.get(j))) {
						return false;
					}
				}
			}
		}

		return true;
	}
	
	private boolean validarUnidMedidaInfoEspecifica(XMLDataList info, boolean validarUnidades) {
		for (int i = 0; i < info.size(); i++) {
			XMLDataObject xdoConcepto = (XMLDataObject) info.get(i);
			XMLDataList xdoAperturas = (XMLDataList) xdoConcepto.getObject(ConstantesVaciados.LST_SUB_CONCEPTOS);
			boolean tablaTieneUnidad = false;
			
			if (xdoConcepto.getString(ConstantesVaciados.FLAG_TABLA_TIENE_UNIDAD) != null) {
				tablaTieneUnidad = new Boolean(xdoConcepto.getString(ConstantesVaciados.FLAG_TABLA_TIENE_UNIDAD)).booleanValue();
			}

			// Se valida la informacion de los conceptos
			if (tablaTieneUnidad){
				if (xdoAperturas != null && xdoAperturas.size() > 0) {
					for (int j = 0; j < xdoAperturas.size(); j++) {
						if (!validarApertura((XMLDataObject) xdoAperturas.get(j))) {
							return false;
						}else if(!validarUnidades((XMLDataObject) xdoAperturas.get(j))){
							return false;
						}
					}
				}
			}
			
		}

		return true;
	}

	private boolean validarApertura(XMLDataObject xdoApertura) {
		if (xdoApertura==null){
			return true;
		}
		String nombre = xdoApertura.getString(ConstantesVaciados.NOMBRE_CONCEPTO);

		if (nombre == null || nombre.length() == 0) {
			view.procesarError(null, "Una o mas de las aperturas no tienen un nombre asignado", null);
			return false;
		}

		return true;
	}
	// Metodo que verifica si las unidades de cada una de las aperturas se encuentra selecciconada
	private boolean validarUnidades(XMLDataObject xdoApertura) {
		if (xdoApertura==null){
			return true;
		}
		String idUnidad = xdoApertura.getString(ConstantesVaciados.ID_UNIDAD);

		if (idUnidad == null || "-1".equals(idUnidad)) {
			view.procesarError(null, "Falta seleccionar la Unidad correspondiente", null);
			return false;
		}

		return true;
	}
	

	private class BuscarInfoComplementariaCallback extends SEFEBusyRequestCallback {
		public void onSEFEBusyError(Request request, Throwable exception) {
			view.procesarError("-1", "Ha ocurrido un error inesperado", exception);
		}

		public void onSEFEBusyResponseReceived(Request request, Response response) {
			String xmlResponse = response.getText();

			if (!XMLUtil.isResponseOK(xmlResponse)) {
				view.procesarError("-1", XMLUtil.getMessageFromResponse(xmlResponse), null);
				return;
			}

			pintarInfoComplementaria(response);
		}
	}

	private class GuardarInfoComplementariaCallback extends SEFEBusyRequestCallback {
		public void onSEFEBusyError(Request request, Throwable exception) {
			view.procesarError("-1", "Ha ocurrido un error inesperado", exception);
		}

		public void onSEFEBusyResponseReceived(Request request, Response response) {
			String xmlResponse = response.getText();

			if (!XMLUtil.isResponseOK(xmlResponse)) {
				view.procesarError("-1", XMLUtil.getMessageFromResponse(xmlResponse), null);
				return;
			}

			pintarInfoComplementaria(response);

			view.mostrarMensaje("Información Complementaria guardada correctamente");
		}
	}

	private void pintarInfoComplementaria(Response response) {
		XMLDataObject data = (XMLDataObject) ServiceInvoker.obtenerRespuesta(response);

		// Se guardan los ids de los vaciados
		idVaciado1 = data.getLong(ConstantesVaciados.ID_VACIADO);
		idVaciado2 = data.getLong(ConstantesVaciados.ID_VACIADO_2);
		idVaciado3 = data.getLong(ConstantesVaciados.ID_VACIADO_3);

		view.pintarInfoComplementaria(data);
	}
}
