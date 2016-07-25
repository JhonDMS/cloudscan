package com.bch.sefe.vaciado.client.view;

import java.util.ArrayList;
import java.util.List;

import com.bch.sefe.comun.client.ComboCache;
import com.bch.sefe.comun.ui.Espaciador;
import com.bch.sefe.comun.ui.InputNumeroSefe;
import com.bch.sefe.comun.ui.InputNumeroSefeEntero;
import com.bch.sefe.comun.utils.FormatUtil;
import com.bch.sefe.comun.utils.PropertiesSEFEUtil;
import com.bch.sefe.comun.utils.SEFEAlert;
import com.bch.sefe.comun.vaciados.ConstantesVaciados;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.XMLData;
import com.bch.sefe.comun.vo.XMLDataList;
import com.bch.sefe.comun.vo.XMLDataObject;
import com.bch.sefe.vaciado.client.view.TablaInfoComplementaria.ColumnaInfoComplementaria;
import com.bch.sefe.vaciado.client.view.TablaInfoComplementaria.FilaInfoComplementaria;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ClickListenerCollection;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InformacionComplementariaUIBuilder {
	private static final String ANCHO_TABLA_INFO_SEGMENTO = "940px";
	private static final String ANCHO_TABLA_INFO_ADICIONAL = "940px";
	private static final String ANCHO_TABLA_FIJA = "550px";
	private static final String ALTO_TEXTBOX = "18px";
	private final String AJUSTE_CONSOLIDACION = "AJUSTES DE CONSOLIDACI\u00D3N";
	private static final String STYLE_TEXTBOX_VALORES = "tablaDescriptoraDataTextbox";
	private static final String STYLE_TEXTBOX_NOMBRES = "tablaDescriptoraDataTextboxLeft";
	private static final String STYLE_BOLD = "bold";

	private ChangeListener inputsModificadosListener;

	public InformacionComplementariaUIBuilder(ChangeListener cl) {
		this.inputsModificadosListener = cl;
	}

	/*
	 * Se encarga de generar tablas fijas que no pueden eliminar ni agregar registros. Estas tablas contienen una columna para el nombre de los
	 * conceptos y los valores de los periodos. Este metodo es utilizado por getTablaInformacionMercadoDestino y getTablaInformacionEstructuraCostos
	 */
	public TablaInfoComplementaria getTablasInfoAdicionalMercadoOEstructura(XMLData xdInfo, ArrayList headersPeriodos) {
		TablaInfoComplementaria tabla = null;
		XMLDataObject xdoInfo = (XMLDataObject) xdInfo;
		XMLDataList xdlAperturas;
		ArrayList footers = new ArrayList();
		ArrayList headers;
		Double total1;
		Double total2;
		Double total3;
		final int ANCHO_COL_NOMBRE = 200;
		final int ANCHO_COL_VALORES = 100;

		headers = new ArrayList(headersPeriodos);
		headers.add(0, new HTML(""));

	String nombreConcepto = xdoInfo.getString(ConstantesVaciados.NOMBRE_CONCEPTO);
		//GBRL - IR73258 - CONTINUIDAD
	if(nombreConcepto != null){
			nombreConcepto = nombreConcepto.replaceAll("%26amp;", "&");	
		}		

		//tabla = new TablaInfoComplementaria(xdoInfo.getString(ConstantesVaciados.NOMBRE_CONCEPTO), headers, false, false, false);
		//GBRL - IR73258 - CONTINUIDAD
		tabla = new TablaInfoComplementaria(nombreConcepto, headers, false, false, false);
		tabla.setId(xdoInfo.getId());
		tabla.getTablaDescriptora().getHeader().getFlexCellFormatter().setColSpan(0, 0, 2);
		tabla.getTablaDescriptora().getHeader().removeCell(0, 1);
		tabla.getTablaDescriptora().getHeader().removeCell(0, 1);
		tabla.getTablaDescriptora().getHeader().removeCell(0, 1);
		tabla.getTablaDescriptora().setColumnWidth(0, ANCHO_COL_NOMBRE);
		tabla.getTablaDescriptora().setColumnWidth(1, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(2, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(3, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setScrollTableSize(ANCHO_TABLA_FIJA, "auto");

		xdlAperturas = (XMLDataList) xdoInfo.getObject(ConstantesVaciados.LST_SUB_CONCEPTOS);

		if (xdlAperturas == null)
			return tabla;

		total1 = xdoInfo.getDouble(ConstantesVaciados.TOTAL_MONTO);
		total2 = xdoInfo.getDouble(ConstantesVaciados.TOTAL_MONTO_N_1);
		total3 = xdoInfo.getDouble(ConstantesVaciados.TOTAL_MONTO_N_2);

		footers = new ArrayList();
		footers.add(getLabelTotal());
		footers.add(getInputTotal(total3, true));
		footers.add(getInputTotal(total2, true));
		footers.add(getInputTotal(total1, true));

		configurarEstiloFooter(footers);

		tabla.getTablaDescriptora().setFooter(footers);
		tabla.getTablaDescriptora().getFooter().getRowFormatter().setStyleName(0, "tablaDescriptoraData");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 0, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 1, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 2, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 3, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().setColumnWidth(0, ANCHO_COL_NOMBRE);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(1, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(2, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(3, ANCHO_COL_VALORES);

		tabla.getTablaDescriptora().redrawResize();

		// Se recorren todos los registros menos el ultimo ya que corresponde al total
		for (int i = 0; i < xdlAperturas.size(); i++) {
			XMLDataObject xdoApertura = (XMLDataObject) xdlAperturas.get(i);
			String nombre = xdoApertura.getString(ConstantesVaciados.NOMBRE_CONCEPTO);
			String idApertura = xdoApertura.getId();

			FilaInfoComplementaria fila = new FilaInfoComplementaria(idApertura);

			ColumnaInfoComplementaria colNombre = new ColumnaInfoComplementaria();
			colNombre.setWidget(new Label(nombre));

			fila.add(colNombre);

			for (int j = (ConstantesVaciados.TERCER_PERIODO - 1); j >= 0; j--) {
				String valor = xdoApertura.getString(ConstantesVaciados.PREFIJO_PERIODO + (j + 1));
				ColumnaInfoComplementaria columna = new ColumnaInfoComplementaria();

				InputNumeroSefe in = new InputNumeroSefe();
				in.setDouble((valor != null ? new Double(valor) : null));
				in.setStyleName(STYLE_TEXTBOX_VALORES);
				in.setHeight(ALTO_TEXTBOX);

				columna.setWidget(in);
				columna.setEsEditable(true);

				// Si no es titulo se deben dejar como no editable los campos de periodos anteriores
				if (j > (ConstantesVaciados.PRIMER_PERIODO - 1)) {
					columna.setEsEditable(false);
				}

				fila.add(columna);
			}

			tabla.agregar(fila);
		}

		return tabla;
	}

	/*
	 * Retorna un InputNumeroSefe o InputNumeroSefeEntero dependiendo de si usa o no decimales. Ademas setea el input como solo lectura.
	 */
	private InputNumeroSefe getInputTotal(Double dValor, boolean usaDecimales) {
		InputNumeroSefe total = getInputNumeroSefe(dValor, usaDecimales);
		total.setReadOnly(true);
		total.setStyleName("prcTextBoxAlignRight");
		return total;
	}

	private Label getLabelTotal() {
		Label l = new Label("Total");
		l.addStyleName(STYLE_BOLD);
		return l;
	}

	/**
	 * Retorna un objeto de {@link TablaInfoComplementaria} con la estructura requerida para la Información Específica.<br>
	 * Estas tablas contiene las siguientes columnas:<br>
	 * <ul>
	 * <li>ListBox para unidad</li>
	 * <li>TextBox para nombre del concepto</li>
	 * <li>TextBox para valor del primer periodo</li>
	 * <li>TextBox para valor del segundo periodo</li>
	 * <li>TextBox para valor del tercer periodo</li>
	 * <li>Imágen que permite borrar</li>
	 * </ul>
	 * 
	 * @param xdInfo
	 * @param headersPeriodos
	 * @return
	 */
	public TablaInfoComplementaria getTablaInfoEspecifica(XMLDataObject xdoConcepto, ArrayList headersPeriodos) {
		return getTablaConUnidadTotalizador(xdoConcepto, headersPeriodos, true);
	}

	private void configurarEstiloFooter(ArrayList footer) {
		if (footer != null) {
			for (int i = 0; i < footer.size(); i++) {
				Object obj = footer.get(i);

				if (obj instanceof UIObject) {
					((UIObject) obj).addStyleName(STYLE_BOLD);
				}
			}
		}
	}

	/*
	 * Verifica si se tienen valores en periodos anteriores.
	 */
	private boolean existenDatosPeriodosAnteriores(XMLDataList xdlAperturas) {
		for (int i = 0; i < xdlAperturas.size(); i++) {
			XMLDataObject xdoApertura = (XMLDataObject) xdlAperturas.get(i);
			String valPer2 = xdoApertura.getString(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.SEGUNDO_PERIODO);
			String valPer3 = xdoApertura.getString(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.TERCER_PERIODO);

			if ((valPer2 != null && valPer2.length() > 0) || (valPer3 != null && valPer3.length() > 0)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean existenDatosPeriodosAnterioresInfoAdicional(Double valorP2, Double valorP3) {
		String valPer2 = null;
		String valPer3 = null;
		//	XMLDataObject xdoApertura = (XMLDataObject) xdlAperturas.get(i);
			if (valorP2 != null){
				valPer2 = valorP2.toString();
			}
		
			if (valorP3 != null){
				valPer3 = valorP3.toString();
			}
			
			

			if ((valPer2 != null && valPer2.length() > 0) || (valPer3 != null && valPer3.length() > 0)) {
				return true;
			}
		
		return false;
	}

	/**
	 * 
	 * @param xdoConcepto
	 * @param headersPeriodos
	 * @return
	 */
	public TablaInfoComplementaria getTablaInformacionAdicional(XMLDataObject xdoConcepto, ArrayList headersPeriodos, boolean usaDecimales) {
		return getTablaConUnidadSinTotalizador(xdoConcepto, headersPeriodos, usaDecimales);
	}

	/**
	 * Genera una tabla con un footer para totalizar y con las columnas para Nombre de Concepto y valores para 3 peridos.
	 * 
	 * @param xdoConcepto
	 * @param headersPeriodos
	 * @return
	 */
	public TablaInfoComplementaria getTablaConTotalizador(XMLDataObject xdoConcepto, ArrayList headersPeriodos) {
		TablaInfoComplementaria tabla = null;
		String nombreConcepto;
		String idConcepto;
		Double total1;
		Double total2;
		Double total3;
		final int ANCHO_COL_UNIDAD = 150;
		final int ANCHO_COL_NOMBRE = 390;
		final int ANCHO_COL_VALORES = 100;
		final int ANCHO_COL_BORRAR = 20;

		nombreConcepto = xdoConcepto.getString(ConstantesVaciados.NOMBRE_CONCEPTO);
		//GBRL - IR73258 - CONTINUIDAD
		if(nombreConcepto != null){
			nombreConcepto = nombreConcepto.replaceAll("%26amp;", "&");	
		}
		//GBRL - IR73258 - CONTINUIDAD	
		idConcepto = xdoConcepto.getId();
		total1 = xdoConcepto.getDouble(ConstantesVaciados.TOTAL_MONTO);
		total2 = xdoConcepto.getDouble(ConstantesVaciados.TOTAL_MONTO_N_1);
		total3 = xdoConcepto.getDouble(ConstantesVaciados.TOTAL_MONTO_N_2);

		// Se agregan en los headers los espacios en blanco para las primeras dos columnas
		ArrayList auxHeaders = new ArrayList(headersPeriodos);
		auxHeaders.add(0, new Label(""));

		ArrayList footers = new ArrayList();
		footers.add(getLabelTotal());
		footers.add(getInputTotal(total3, false));
		footers.add(getInputTotal(total2, false));
		footers.add(getInputTotal(total1, false));
		footers.add(new Label(""));

		configurarEstiloFooter(footers);

		tabla = new TablaInfoComplementaria(nombreConcepto, auxHeaders, false, true, true);
		tabla.setId(idConcepto);
		tabla.addTablaEditableListener(new TablaEditableIngresosXLineaProdNegHandler());
		tabla.getTablaDescriptora().getHeader().getFlexCellFormatter().setColSpan(0, 0, 2);
		tabla.getTablaDescriptora().setScrollTableSize(ANCHO_TABLA_INFO_ADICIONAL, "auto");
		tabla.getTablaDescriptora().getHeader().removeCell(0, 1);
		tabla.getTablaDescriptora().setColumnWidth(0, ANCHO_COL_NOMBRE + ANCHO_COL_UNIDAD);
		tabla.getTablaDescriptora().setColumnWidth(1, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(2, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(3, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(4, ANCHO_COL_BORRAR);

		tabla.getTablaDescriptora().setFooter(footers);
		tabla.getTablaDescriptora().getFooter().getRowFormatter().setStyleName(0, "tablaDescriptoraData");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 0, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 1, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 2, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 3, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 4, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().setColumnWidth(0, ANCHO_COL_NOMBRE + ANCHO_COL_UNIDAD);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(1, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(2, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(3, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(4, ANCHO_COL_BORRAR);

		// Se obtienen los sub conceptos de la cuenta en caso que existan
		XMLDataList xdlSubconceptos = (XMLDataList) xdoConcepto.getObject(ConstantesVaciados.LST_SUB_CONCEPTOS);

		if (xdlSubconceptos != null) {
			for (int j = 0; j < xdlSubconceptos.size(); j++) {
				XMLDataObject xdoSubConcepto = (XMLDataObject) xdlSubconceptos.get(j);


				Double valorP3 = xdoSubConcepto.getDouble(ConstantesVaciados.PREFIJO_PERIODO + (3));
				Double valorP2 = xdoSubConcepto.getDouble(ConstantesVaciados.PREFIJO_PERIODO + (2));
				boolean isNuevo = false;
				if ((valorP3 == null || valorP3.intValue()==0)&&(valorP2 == null || valorP2.intValue()==0)){
					isNuevo = true;
				}
				FilaInfoComplementaria filaSub = getConceptoTablaConTotalizador(xdoSubConcepto, false, isNuevo);

				filaSub.setSePuedeEliminar(isNuevo);
				tabla.agregar(filaSub);
			}
		}

		tabla.getTablaDescriptora().redrawResize();

		return tabla;
	}

	private FilaInfoComplementaria getConceptoTablaConTotalizador(XMLDataObject xdoConcepto, boolean esTitulo, boolean esNuevo) {
		FilaInfoComplementaria fila;
		String idConcepto = xdoConcepto.getId();
		String nombreConcepto = xdoConcepto.getString(ConstantesVaciados.NOMBRE_CONCEPTO);
		//GBRL - IR73258 - CONTINUIDAD
		if(nombreConcepto != null){
			nombreConcepto = nombreConcepto.replaceAll("%26amp;", "&");	
		}
		//GBRL - IR73258 - CONTINUIDAD
		boolean esCampoEditable = (esNuevo && !esTitulo);

		fila = new FilaInfoComplementaria(idConcepto);

		TextBox tbNombre = new TextBox();
		tbNombre.setText(nombreConcepto);
		tbNombre.setStyleName(STYLE_TEXTBOX_NOMBRES);
		tbNombre.setHeight(ALTO_TEXTBOX);
		tbNombre.setMaxLength(ConstantesVaciados.MAX_LONGITUD_APERTURAS);
		tbNombre.setTitle(ConstantesVaciados.TITLE_LONGITUD_MAXIMA);
		tbNombre.addChangeListener(new ChangeListenerTextBoxImpl());
		ColumnaInfoComplementaria colNombre = new ColumnaInfoComplementaria();
		colNombre.setWidget(tbNombre);
		colNombre.setEsEditable(esCampoEditable);

		fila.add(colNombre);

		for (int j = (ConstantesVaciados.TERCER_PERIODO - 1); j >= 0; j--) {
			// Por cada periodo a mostrar en el XML aparece per_1, per_2, per_n
			String valor = xdoConcepto.getString(ConstantesVaciados.PREFIJO_PERIODO + (j + 1));
			esCampoEditable = ((esNuevo || !esTitulo) && j == (ConstantesVaciados.PRIMER_PERIODO - 1));

			InputNumeroSefeEntero inValorPeriodo = (InputNumeroSefeEntero) getInputNumeroSefe((valor != null ? new Double(valor) : null), false);
			inValorPeriodo.setStyleName(STYLE_TEXTBOX_VALORES);
			inValorPeriodo.setHeight(ALTO_TEXTBOX);

			ColumnaInfoComplementaria colValorPeriodo = new ColumnaInfoComplementaria();
			colValorPeriodo.setWidget(inValorPeriodo);
			colValorPeriodo.setEsEditable(esCampoEditable);

			fila.add(colValorPeriodo);
		}

		return fila;
	}

	private TablaInfoComplementaria getTablaConUnidadTotalizador(XMLDataObject xdoConcepto, ArrayList headersPeriodos, boolean usaDecimales) {
		TablaInfoComplementaria tabla = null;
		String nombreConcepto;
		String idConcepto;
		String idUnidad;
		Double total1;
		Double total2;
		Double total3;
		final int ANCHO_COL_UNIDAD = 150;
		final int ANCHO_COL_NOMBRE = 390;
		final int ANCHO_COL_VALORES = 100;
		final int ANCHO_COL_BORRAR = 20;

		nombreConcepto = xdoConcepto.getString(ConstantesVaciados.NOMBRE_CONCEPTO);
		//GBRL - IR73258 - CONTINUIDAD
		if(nombreConcepto != null){
			nombreConcepto = nombreConcepto.replaceAll("%26amp;", "&");	
		}
		//GBRL - IR73258 - CONTINUIDAD
		
		
		idConcepto = xdoConcepto.getId();
		idUnidad = xdoConcepto.getString(ConstantesVaciados.ID_UNIDAD);
		total1 = xdoConcepto.getDouble(ConstantesVaciados.TOTAL_MONTO);
		total2 = xdoConcepto.getDouble(ConstantesVaciados.TOTAL_MONTO_N_1);
		total3 = xdoConcepto.getDouble(ConstantesVaciados.TOTAL_MONTO_N_2);

		ListBox lbUnidad = new ListBox();
		lbUnidad.setWidth("100%");
		cargarListBox(ConstantesVaciados.ID_ESQUEMA_UNIDADES_MEDIDAS_INDICADORES, lbUnidad, idUnidad);

		// Se agregan en los headers los espacios en blanco para las primeras dos columnas
		ArrayList auxHeaders = new ArrayList(headersPeriodos);
		auxHeaders.add(0, lbUnidad);
		auxHeaders.add(1, new Label(""));

		ArrayList footers = new ArrayList();
		footers.add(getLabelTotal());
		footers.add(new Label(""));
		footers.add(getInputTotal(total3, usaDecimales));
		footers.add(getInputTotal(total2, usaDecimales));
		footers.add(getInputTotal(total1, usaDecimales));
		footers.add(new Label(""));

		configurarEstiloFooter(footers);

		tabla = new TablaInfoComplementaria(nombreConcepto, auxHeaders, false, true, true);
		tabla.setTieneUnidad(true);
		tabla.setId(idConcepto);
		tabla.addTablaEditableListener(new TablaEditableInfoComplementariaHandler(usaDecimales));
		tabla.getTablaDescriptora().getHeader().getFlexCellFormatter().setColSpan(0, 0, 2);
		tabla.getTablaDescriptora().setScrollTableSize(ANCHO_TABLA_INFO_ADICIONAL, "auto");
		tabla.getTablaDescriptora().getHeader().removeCell(0, 1);
		tabla.getTablaDescriptora().setColumnWidth(0, ANCHO_COL_UNIDAD);
		tabla.getTablaDescriptora().setColumnWidth(1, ANCHO_COL_NOMBRE);
		tabla.getTablaDescriptora().setColumnWidth(2, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(3, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(4, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(5, ANCHO_COL_BORRAR);

		tabla.getTablaDescriptora().setFooter(footers);
		tabla.getTablaDescriptora().getFooter().getRowFormatter().setStyleName(0, "tablaDescriptoraData");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 0, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 1, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 2, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 3, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 4, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 5, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().setColumnWidth(0, ANCHO_COL_UNIDAD);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(1, ANCHO_COL_NOMBRE);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(2, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(3, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(4, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(5, ANCHO_COL_BORRAR);

		// Se obtienen los sub conceptos de la cuenta en caso que existan
		XMLDataList xdlSubconceptos = (XMLDataList) xdoConcepto.getObject(ConstantesVaciados.LST_SUB_CONCEPTOS);

		if (xdlSubconceptos != null) {
			lbUnidad.setEnabled(!existenDatosPeriodosAnteriores(xdlSubconceptos));

			for (int j = 0; j < xdlSubconceptos.size(); j++) {
				XMLDataObject xdoSubConcepto = (XMLDataObject) xdlSubconceptos.get(j);

				Double valorP3 = xdoSubConcepto.getDouble(ConstantesVaciados.PREFIJO_PERIODO + (3));
				Double valorP2 = xdoSubConcepto.getDouble(ConstantesVaciados.PREFIJO_PERIODO + (2));
				TextBox tbNombre = (TextBox) tabla.getTablaDescriptora().getHeader().getWidget(0, 0);
				tbNombre.setReadOnly(true);
				boolean isNuevo = false;
				if ((valorP3 == null || valorP3.intValue()==0)&&(valorP2 == null || valorP2.intValue()==0)){
					isNuevo = true;
					
					tbNombre.setReadOnly(false);

				}else if((valorP3 != null)||(valorP2 != null )){
					tbNombre.addClickListener(new ClickListener() {
				        public void onClick(Widget sender) {
				          SEFEAlert.alert(ConstantesVaciados.MSG_INFORMACION_COMPLEMENTARIA_DESCUADRADA);
				        }
				    });
				}
				FilaInfoComplementaria filaSub = getConceptoInfoAdicional(xdoSubConcepto, false, isNuevo, usaDecimales, false);
				filaSub.setSePuedeEliminar(isNuevo);
				tabla.agregar(filaSub);
			}
		}

		tabla.getTablaDescriptora().redrawResize();

		return tabla;
	}
	
	private TablaInfoComplementaria getTablaConUnidadSinTotalizador(XMLDataObject xdoConcepto, ArrayList headersPeriodos, boolean usaDecimales) {
		TablaInfoComplementaria tabla = null;
		String nombreConcepto;
		String idConcepto;
		String idUnidad;
		final int ANCHO_COL_UNIDAD = 150;
		final int ANCHO_COL_NOMBRE = 390;
		final int ANCHO_COL_VALORES = 100;
		final int ANCHO_COL_BORRAR = 20;

		nombreConcepto = xdoConcepto.getString(ConstantesVaciados.NOMBRE_CONCEPTO);
		//GBRL - IR73258 - CONTINUIDAD
		if(nombreConcepto != null){
			nombreConcepto = nombreConcepto.replaceAll("%26amp;", "&");	
		}
		//GBRL - IR73258 - CONTINUIDAD
		
		idConcepto = xdoConcepto.getId();
		idUnidad = xdoConcepto.getString(ConstantesVaciados.ID_UNIDAD);

		ListBox lbUnidad = new ListBox();
		lbUnidad.setWidth("100%");
		cargarListBox(ConstantesVaciados.ID_ESQUEMA_UNIDADES_MEDIDAS_INDICADORES, lbUnidad, idUnidad);

		// Se agregan en los headers los espacios en blanco para las primeras dos columnas
		ArrayList auxHeaders = new ArrayList(headersPeriodos);
		auxHeaders.add(0, "Unidad");
		auxHeaders.add(1, new Label(""));

		tabla = new TablaInfoComplementaria(nombreConcepto, auxHeaders, false, true, true);
		tabla.setTieneUnidad(true);
		tabla.setTieneUnidadesMedida(true);
		tabla.setId(idConcepto);
		tabla.addTablaEditableListener(new TablaEditableInfoAdicionalHandler(usaDecimales));
		tabla.getTablaDescriptora().getHeader().getFlexCellFormatter().setColSpan(0, 0, 2);
		tabla.getTablaDescriptora().setScrollTableSize(ANCHO_TABLA_INFO_ADICIONAL, "auto");
		tabla.getTablaDescriptora().getHeader().removeCell(0, 1);
		tabla.getTablaDescriptora().setColumnWidth(0, ANCHO_COL_UNIDAD);
		tabla.getTablaDescriptora().setColumnWidth(1, ANCHO_COL_NOMBRE);
		tabla.getTablaDescriptora().setColumnWidth(2, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(3, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(4, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().setColumnWidth(5, ANCHO_COL_BORRAR);

		// Se obtienen los sub conceptos de la cuenta en caso que existan
		XMLDataList xdlSubconceptos = (XMLDataList) xdoConcepto.getObject(ConstantesVaciados.LST_SUB_CONCEPTOS);

		if (xdlSubconceptos != null) {

			for (int j = 0; j < xdlSubconceptos.size(); j++) {
				XMLDataObject xdoSubConcepto = (XMLDataObject) xdlSubconceptos.get(j);

				
				Double valorP3 = xdoSubConcepto.getDouble(ConstantesVaciados.PREFIJO_PERIODO + (3));
				Double valorP2 = xdoSubConcepto.getDouble(ConstantesVaciados.PREFIJO_PERIODO + (2));
				boolean isNuevo = false;
				if ((valorP3 == null || valorP3.intValue()==0)&&(valorP2 == null || valorP2.intValue()==0)){
					isNuevo = true;
				}
				FilaInfoComplementaria filaSub = getConceptoInfoAdicional(xdoSubConcepto, false, isNuevo, usaDecimales,true);
				ListBox lbAux = (ListBox) filaSub.getColumna(0).getWidget();
				lbAux.setEnabled(!existenDatosPeriodosAnterioresInfoAdicional(valorP2,valorP3));
				filaSub.setSePuedeEliminar(isNuevo);
				tabla.agregar(filaSub);
			}
		}

		tabla.getTablaDescriptora().redrawResize();

		return tabla;
	}
	private FilaInfoComplementaria getConceptoInfoAdicional(XMLDataObject xdoConcepto, boolean esTitulo, boolean esNuevo, boolean usaDecimales, boolean MedidasFila) {
		FilaInfoComplementaria fila;
		String idConcepto = xdoConcepto.getId();
		String nombreConcepto = xdoConcepto.getString(ConstantesVaciados.NOMBRE_CONCEPTO);
		
		//GBRL - IR73258 - CONTINUIDAD
		if(nombreConcepto != null){
			nombreConcepto = nombreConcepto.replaceAll("%26amp;", "&");	
		}
		//GBRL - IR73258 - CONTINUIDAD
		
		boolean esCampoEditable = (esNuevo && !esTitulo);

		fila = new FilaInfoComplementaria(idConcepto);
		
		// 7.4.8.2 se agregan los listados de unidad de medida para cada una de las aperturas
		ColumnaInfoComplementaria colUnidad = new ColumnaInfoComplementaria();
		if (MedidasFila){
			String idUnidadSubConcepto = xdoConcepto.getString(ConstantesVaciados.ID_UNIDAD);
			ListBox lbUnidad = new ListBox();
			lbUnidad.setWidth("100%");
			cargarListBox(ConstantesVaciados.ID_ESQUEMA_UNIDADES_MEDIDAS_INDICADORES, lbUnidad, idUnidadSubConcepto);
			colUnidad.setWidget(lbUnidad);
			
		}else{
			colUnidad.setWidget(new Label(""));
		}

		TextBox tbNombre = new TextBox();
		tbNombre.setText(nombreConcepto);
		tbNombre.setStyleName(STYLE_TEXTBOX_NOMBRES);
		tbNombre.setHeight(ALTO_TEXTBOX);
		tbNombre.setMaxLength(ConstantesVaciados.MAX_LONGITUD_APERTURAS);
		tbNombre.setTitle(ConstantesVaciados.TITLE_LONGITUD_MAXIMA);
		tbNombre.addChangeListener(new ChangeListenerTextBoxImpl());
		ColumnaInfoComplementaria colNombre = new ColumnaInfoComplementaria();
		colNombre.setWidget(tbNombre);
		colNombre.setEsEditable(esCampoEditable);

		fila.add(colUnidad);
		fila.add(colNombre);

		for (int j = (ConstantesVaciados.TERCER_PERIODO - 1); j >= 0; j--) {
			// Por cada periodo a mostrar en el XML aparece per_1, per_2, per_n
			String valor = xdoConcepto.getString(ConstantesVaciados.PREFIJO_PERIODO + (j + 1));
			esCampoEditable = ((esNuevo || !esTitulo) && j == (ConstantesVaciados.PRIMER_PERIODO - 1));

			InputNumeroSefe inValorPeriodo = getInputNumeroSefe((valor != null ? new Double(valor) : null), usaDecimales);
			inValorPeriodo.setStyleName(STYLE_TEXTBOX_VALORES);
			inValorPeriodo.setHeight(ALTO_TEXTBOX);

			ColumnaInfoComplementaria colValorPeriodo = new ColumnaInfoComplementaria();
			colValorPeriodo.setWidget(inValorPeriodo);
			colValorPeriodo.setEsEditable(esCampoEditable);

			fila.add(colValorPeriodo);
		}

		return fila;
	}

	/*
	 * Carga un list box con las clasificaciones correspondientes. Si se pasa como parametro el idSelected deja ese item seleccionado en el list box.
	 */
	private void cargarListBox(int idCategoria, ListBox lb, String idSelected) {
		List categorias = ComboCache.get(idCategoria);
		lb.addItem("Seleccione", "-1");
		for (int i = 0; i < categorias.size(); i++) {
			Clasificacion clasif = (Clasificacion) categorias.get(i);
			lb.addItem(clasif.getDescripcion(), clasif.getIdClasif().toString());

			if (idSelected != null && idSelected.equalsIgnoreCase(clasif.getIdClasif().toString())) {
				lb.setSelectedIndex(i + 1);
			}
		}
	}

	public TablaInfoComplementaria getTablaInformacionSegmento(XMLDataObject xdoConcepto, ArrayList headersPeriodos, String planCta) {
		TablaInfoComplementaria tabla = null;
		HorizontalPanel panelAlerta = null;
		Double total1;
		Double total2;
		Double total3;
		final int ANCHO_COL_UNIDAD = 150;
		final int ANCHO_COL_NOMBRE = 390;
		final int ANCHO_COL_VALORES = 100;
		final int ANCHO_COL_BORRAR = 20;
		boolean permiteAgregar = true;
		if (planCta.equals("CHGAAP")){
			permiteAgregar = false;
		}
		tabla = new TablaInfoComplementaria("", headersPeriodos, false, permiteAgregar, permiteAgregar);
		total1 = xdoConcepto.getDouble(ConstantesVaciados.TOTAL_MONTO);
		total2 = xdoConcepto.getDouble(ConstantesVaciados.TOTAL_MONTO_N_1);
		total3 = xdoConcepto.getDouble(ConstantesVaciados.TOTAL_MONTO_N_2);
		ArrayList footers = new ArrayList();
		footers.add(getLabelTotal());
		footers.add(getInputTotal(total3, false));
		footers.add(getInputTotal(total2, false));
		footers.add(getInputTotal(total1, false));
		footers.add(new Label(""));

	    final String ALERTA_5 = "Cuenta Descuadrada";
		final String ALERTA_5_OK = "Cuenta Cuadrada";
		final String ALERTA_IMG_OK = PropertiesSEFEUtil.getKeyVaciados().ImgSrcAlertaOK();
		final String ALERTA_IMG_WARN = PropertiesSEFEUtil.getKeyVaciados().ImgSrcAlertaWarn();
		String num = xdoConcepto.getString("alertaGrillaTablaSegmento");
		if (num.equals("1")) {// condicion de diferencia entre los montos. si es 1 son diferentes
			tabla.getAlertaImg().setUrl(ALERTA_IMG_WARN);
			tabla.getAlertaTexto().setText(ALERTA_5);
		} else {
			tabla.getAlertaImg().setUrl(ALERTA_IMG_OK);
			tabla.getAlertaTexto().setText(ALERTA_5_OK);
		}
		panelAlerta = new HorizontalPanel();
		panelAlerta.add(new Espaciador("0em","0.5em"));
		panelAlerta.add(tabla.getAlertaImg());
		panelAlerta.add(new Espaciador("0em","0.5em"));
		panelAlerta.add(tabla.getAlertaTexto());
		tabla.getVp().add(new Espaciador("1em", "0em"));
		tabla.getVp().add(panelAlerta);
		
		configurarEstiloFooter(footers);
		tabla.setId(xdoConcepto.getId());
		tabla.addTablaEditableListener(new TablaEditableInfoSegmentoHandler());
		tabla.getTablaDescriptora().setScrollTableSize(ANCHO_TABLA_INFO_SEGMENTO, "auto");
		tabla.getTablaDescriptora().getHeader().getFlexCellFormatter().setColSpan(0, 0, 2);
		tabla.getTablaDescriptora().getHeader().removeCell(0, 1);
		tabla.getTablaDescriptora().getHeader().getRowFormatter().setStyleName(1, "tabla-Titulo");
		tabla.getTablaDescriptora().setColumnWidth(0, 560);
		tabla.getTablaDescriptora().setColumnWidth(1, 100);
		tabla.getTablaDescriptora().setColumnWidth(2, 100);
		tabla.getTablaDescriptora().setColumnWidth(3, 100);
		tabla.getTablaDescriptora().setColumnWidth(4, 20);
		tabla.getTablaDescriptora().setFooter(footers);
		tabla.getTablaDescriptora().getFooter().getRowFormatter().setStyleName(0, "tablaDescriptoraData");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 0, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 1, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 2, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 3, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 4, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().getCellFormatter().addStyleName(0, 5, "tablaCamposDesactivados");
		tabla.getTablaDescriptora().getFooter().setColumnWidth(0, ANCHO_COL_UNIDAD);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(1, ANCHO_COL_NOMBRE);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(2, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(3, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(4, ANCHO_COL_VALORES);
		tabla.getTablaDescriptora().getFooter().setColumnWidth(5, ANCHO_COL_BORRAR);
		FilaInfoComplementaria filaConcepto = getConceptoInfoSegmento(xdoConcepto, true, false);
		filaConcepto.setEsTitulo(true);

		tabla.agregar(filaConcepto);

		// Se obtienen los sub conceptos de la cuenta en caso que existan
		XMLDataList xdlSubconceptos = (XMLDataList) xdoConcepto.getObject(ConstantesVaciados.LST_SUB_CONCEPTOS);

		if (xdlSubconceptos != null) {
			for (int j = 0; j < xdlSubconceptos.size(); j++) {
				XMLDataObject xdoSubConcepto = (XMLDataObject) xdlSubconceptos.get(j);
				Double valorP3 = xdoSubConcepto.getDouble(ConstantesVaciados.PREFIJO_PERIODO + (3));
				Double valorP2 = xdoSubConcepto.getDouble(ConstantesVaciados.PREFIJO_PERIODO + (2));
				boolean isNuevo = false;
				boolean isTitulo = false;
				if ((valorP3 == null || valorP3.intValue()==0)&&(valorP2 == null || valorP2.intValue()==0)){
					isNuevo = true;
				}
				if (planCta.equals("CHGAAP")){
					isNuevo = false;
					isTitulo = true;
				}
				FilaInfoComplementaria filaSub = getConceptoInfoSegmento(xdoSubConcepto, isTitulo, isNuevo);

				filaSub.setSePuedeEliminar(isNuevo);
				if (AJUSTE_CONSOLIDACION.equals(xdoSubConcepto.getString(ConstantesVaciados.NOMBRE_CONCEPTO))){
					filaSub.setSePuedeEliminar(false);	
				}
				tabla.agregar(filaSub);
			}
		}

		tabla.getTablaDescriptora().redrawResize();

		return tabla;
	}

	private FilaInfoComplementaria getConceptoInfoSegmento(XMLDataObject xdoConcepto, boolean esTitulo, boolean esNuevo) {
		FilaInfoComplementaria fila;
		String idConcepto = xdoConcepto.getId();
		String nombreConcepto = xdoConcepto.getString(ConstantesVaciados.NOMBRE_CONCEPTO);

		fila = new FilaInfoComplementaria(idConcepto);
		
		//GBRL - IR73258 - CONTINUIDAD
		if(nombreConcepto != null){
			nombreConcepto = nombreConcepto.replaceAll("%26amp;", "&");	
		}
		//GBRL - IR73258 - CONTINUIDAD
		

		TextBox tbNombre = new TextBox();
		tbNombre.setText(nombreConcepto);
		tbNombre.setStyleName(STYLE_TEXTBOX_NOMBRES);
		tbNombre.setHeight(ALTO_TEXTBOX);
		tbNombre.setMaxLength(ConstantesVaciados.MAX_LONGITUD_APERTURAS);
		tbNombre.setTitle(ConstantesVaciados.TITLE_LONGITUD_MAXIMA);
		tbNombre.addChangeListener(new ChangeListenerTextBoxImpl());

		ColumnaInfoComplementaria colNombre = new ColumnaInfoComplementaria();
		colNombre.setWidget(tbNombre);
		colNombre.setEsEditable((esNuevo && !esTitulo));
		if (AJUSTE_CONSOLIDACION.equals(xdoConcepto.getString(ConstantesVaciados.NOMBRE_CONCEPTO))){
			colNombre.setEsEditable(false);
		}
		fila.add(colNombre);

		// Recorre desde el periodo mas antiguo hasta el mas nuevo
		for (int j = (ConstantesVaciados.TERCER_PERIODO - 1); j >= 0; j--) {
			// Por cada periodo a mostrar en el XML aparece per_1, per_2, per_n
			Double valor = xdoConcepto.getDouble(ConstantesVaciados.PREFIJO_PERIODO + (j + 1));
			boolean esEditable = ((esNuevo || !esTitulo) && j == (ConstantesVaciados.PRIMER_PERIODO - 1));
			InputNumeroSefe in;

			// Si es titulo, el input a utilizar dependera de la unidad de la cuenta a desplegar. Pero cuando es una apertura siempre
			// debe mostrar decimales.
			if (esTitulo) {
				in = getInputNumeroSefe(valor, FormatUtil.usaDecimales(xdoConcepto.getInteger(ConstantesVaciados.ID_UNIDAD)));
			} else {
				//se quitan decimales por defecto para que no muestre decimales en segmentos IC12
				in = getInputNumeroSefe((valor != null ? valor : null), false);
				in.setDouble(valor);
			}
			in.setStyleName(STYLE_TEXTBOX_VALORES);
			in.setHeight(ALTO_TEXTBOX);

			ColumnaInfoComplementaria colValorPeriodo = new ColumnaInfoComplementaria();
			colValorPeriodo.setWidget(in);
			colValorPeriodo.setEsEditable(esEditable);

			fila.add(colValorPeriodo);
		}

		return fila;
	}

	private InputNumeroSefe getInputNumeroSefe(Double dValor, boolean usaDecimales) {
		InputNumeroSefe in;

		if (usaDecimales) {
			in = new InputNumeroSefe();
		} else {
			in = new InputNumeroSefeEntero();
		}

		in.setDouble(dValor);

		return in;
	}

	private class ChangeListenerTextBoxImpl implements ChangeListener {
		public void onChange(Widget sender) {
			TextBox tb = (TextBox) sender;
			tb.setText(tb.getText().toUpperCase());
		}
	}

	/*
	 * Clase que maneja los eventos que gatilla la tabla TablaInfoComplementaria para la informacion de segmento
	 */
	private class TablaEditableInfoSegmentoHandler implements ITablaEditableListener {
		public void onAgregarRegistroClick(TablaInfoComplementaria tic) {
			FilaInfoComplementaria nuevaFila = getConceptoInfoSegmento(new XMLDataObject(), false, true);
			nuevaFila.setSePuedeEliminar(true);
			tic.agregar(nuevaFila);
		}

		public void onEliminarRegistroClick(TablaInfoComplementaria tic, FilaInfoComplementaria fic) {
			tic.eliminar(fic.getId());
		}
	}

	/*
	 * Clase que maneja los eventos que gatilla la tabla TablaInfoComplementaria para la informacion adicional
	 */
	private class TablaEditableInfoAdicionalHandler implements ITablaEditableListener {
		private boolean usaDecimales;

		public TablaEditableInfoAdicionalHandler(boolean usaDecimales) {
			this.usaDecimales = usaDecimales;
		}

		public void onAgregarRegistroClick(TablaInfoComplementaria tic) {
			FilaInfoComplementaria nuevaFila = getConceptoInfoAdicional(new XMLDataObject(), false, true, usaDecimales, true);
			nuevaFila.setSePuedeEliminar(true);
			tic.agregar(nuevaFila);
		}

		public void onEliminarRegistroClick(TablaInfoComplementaria tic, FilaInfoComplementaria fic) {
			tic.eliminar(fic.getId());
		}
	}
	
	/*
	 * Clase que maneja los eventos que gatilla la tabla TablaInfoComplementaria para la informacion complementaria
	 */
	private class TablaEditableInfoComplementariaHandler implements ITablaEditableListener {
		private boolean usaDecimales;

		public TablaEditableInfoComplementariaHandler(boolean usaDecimales) {
			this.usaDecimales = usaDecimales;
		}

		public void onAgregarRegistroClick(TablaInfoComplementaria tic) {
			FilaInfoComplementaria nuevaFila = getConceptoInfoAdicional(new XMLDataObject(), false, true, usaDecimales, false);
			nuevaFila.setSePuedeEliminar(true);
			tic.agregar(nuevaFila);
		}

		public void onEliminarRegistroClick(TablaInfoComplementaria tic, FilaInfoComplementaria fic) {
			tic.eliminar(fic.getId());
		}
	}

	/*
	 * Clase que maneja los eventos que gatilla la tabla TablaInfoComplementaria para la informacion adicional
	 */
	private class TablaEditableIngresosXLineaProdNegHandler implements ITablaEditableListener {
		public void onAgregarRegistroClick(TablaInfoComplementaria tic) {
			FilaInfoComplementaria nuevaFila = getConceptoTablaConTotalizador(new XMLDataObject(), false, true);
			nuevaFila.setSePuedeEliminar(true);
			tic.agregar(nuevaFila);
		}

		public void onEliminarRegistroClick(TablaInfoComplementaria tic, FilaInfoComplementaria fic) {
			tic.eliminar(fic.getId());
		}
	}
}
