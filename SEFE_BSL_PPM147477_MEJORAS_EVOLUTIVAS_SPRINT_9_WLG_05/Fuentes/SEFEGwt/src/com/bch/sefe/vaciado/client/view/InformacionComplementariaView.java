package com.bch.sefe.vaciado.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bch.sefe.comun.ui.ContenedorDescriptor;
import com.bch.sefe.comun.ui.InputNumeroSefe;
import com.bch.sefe.comun.ui.SEFEModalConfirmDialogCallback;
import com.bch.sefe.comun.ui.TablaDescriptoraFooter;
import com.bch.sefe.comun.utils.FormatUtil;
import com.bch.sefe.comun.utils.SEFEAlert;
import com.bch.sefe.comun.vaciados.ConstantesVaciados;
import com.bch.sefe.comun.vo.Contexto;
import com.bch.sefe.comun.vo.XMLData;
import com.bch.sefe.comun.vo.XMLDataList;
import com.bch.sefe.comun.vo.XMLDataObject;
import com.bch.sefe.vaciado.client.view.TablaInfoComplementaria.ColumnaInfoComplementaria;
import com.bch.sefe.vaciado.client.view.TablaInfoComplementaria.FilaInfoComplementaria;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Corresponde a la vista principal en donde se pintara la GUI para el ingreso y despliegue de la información complementaria.
 * 
 * @author jlmanriq
 * 
 */
public class InformacionComplementariaView extends VerticalPanel implements IInformacionComplementariaView, ChangeListener {
	private ControladorInformacionComplementaria controller;
	private Contexto ctx;
	private ArrayList headersPeriodos;
	private boolean infoModificada;

	private InformacionComplementariaUIBuilder tablasUIBuilder;
	private HorizontalPanel hpEncabezado;
	private TablaInfoComplementaria ticMercadoDestino;
	private TablaInfoComplementaria ticEstructuraCostos;
	private ContenedorDescriptor cdInfoEspecifica;
	private Button btnAgregarConcepto;
	private Button btnEditarConcepto;
	private TextBox tbNomConceptoInfoEsp;
	private Button btnGuardar;
	private List tablasInfoSegmento;
	private List tablasInfoAdicional;
	private List tablasInfoEspecifica;

	private static final int INFO_ENCABEZADO_PLAN_CTAS = 0;
	private static final int INFO_ENCABEZADO_CON_AJUSTE = 1;
	private static final int INFO_ENCABEZADO_RESPONSABLE = 2;
	private static final int INFO_ENCABEZADO_UNIDAD_MONEDA = 3;
	private static final int INFO_ENCABEZADO_MESES = 4;

	private static final String ANCHO_ELEMENTOS_ENCABEZADO = "100px";
	private static final String ALTO_BOTON = "20px";

	private static final String TEXTO_BTN_AGREGAR_CONCEPTO = "Agregar Concepto";
	private static final String TEXTO_BTN_GUARDAR = "Guardar";
	private static final int COLUMNA_UNIDAD = 0;

	public InformacionComplementariaView(Contexto ctx) {
		this.controller = new ControladorInformacionComplementaria(this);
		this.ctx = ctx;
		this.tablasUIBuilder = new InformacionComplementariaUIBuilder(this);
	}

	private void init() {
		setWidth("100%");
		infoModificada = false;
		hpEncabezado = getEncabezado();
		add(hpEncabezado);
	}

	public void refresh() {
		controller.buscarInformacionComplementaria(ctx.getIdVaciado(), ctx.getRutCliente());
	}

	private HorizontalPanel getEncabezado() {
		final int nroFilas = 5;
		final int nroColumnas = 4;
		Grid grid = new Grid(nroFilas, nroColumnas);
		HorizontalPanel hp = new HorizontalPanel();

		grid.setWidget(0, 0, getLabelEncabezado("Plan de Cuenta:"));
		grid.getCellFormatter().setHorizontalAlignment(0, 0, HasAlignment.ALIGN_RIGHT);
		grid.setWidget(1, 0, getLabelEncabezado("Con Ajuste:"));
		grid.getCellFormatter().setHorizontalAlignment(1, 0, HasAlignment.ALIGN_RIGHT);
		grid.setWidget(2, 0, getLabelEncabezado("Ingresado Por:"));
		grid.getCellFormatter().setHorizontalAlignment(2, 0, HasAlignment.ALIGN_RIGHT);
		grid.setWidget(3, 0, getLabelEncabezado("Unidad Moneda:"));
		grid.getCellFormatter().setHorizontalAlignment(3, 0, HasAlignment.ALIGN_RIGHT);
		grid.setWidget(4, 0, getLabelEncabezado("Meses:"));
		grid.getCellFormatter().setHorizontalAlignment(4, 0, HasAlignment.ALIGN_RIGHT);

		for (int i = 0; i < nroFilas; i++) {
			for (int j = 1; j < nroColumnas; j++) {
				TextBox tb = new TextBox();
				tb.setReadOnly(true);
				tb.setWidth(ANCHO_ELEMENTOS_ENCABEZADO);

				grid.setWidget(i, j, tb);
			}
		}

		hp.setWidth("100%");
		hp.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		hp.add(grid);

		return hp;
	}

	private Label getLabelEncabezado(String txt) {
		Label lbl = new Label(txt);
		lbl.setWidth(ANCHO_ELEMENTOS_ENCABEZADO);
		return lbl;
	}

	/*
	 * Pinta la seccion de información adicional y sus respectivas tablas.
	 */
	private void pintarInfoAdicional(XMLDataList xdlInfo) {
		ContenedorDescriptor descriptorEncabezado = new ContenedorDescriptor("Información Adicional");

		if (xdlInfo == null || xdlInfo.size() == 0) {
			return;
		}

		descriptorEncabezado.setWidth("100%");
		DOM.setStyleAttribute(descriptorEncabezado.getElement(), "padding", "10px 0 0 0");

		tablasInfoAdicional = new ArrayList();

		VerticalPanel vp = new VerticalPanel();

		for (int i = 0; i < xdlInfo.size(); i++) {
			XMLDataObject xdoConcepto = (XMLDataObject) xdlInfo.get(i);
			TablaInfoComplementaria tabla = null;

			// PARCHE. Si es la tabla de 'Informacion por linea de productos de negocios' ahora esta tabla no tiene unidad.
			if (esIngresoXLineaProductosNegocios(xdoConcepto.getId())) {
				// Es una tabla con totalizador pero sin columna para seleccionar Unidad
				tabla = tablasUIBuilder.getTablaConTotalizador(xdoConcepto, headersPeriodos);
			} else {
				tabla = tablasUIBuilder.getTablaInformacionAdicional(xdoConcepto, headersPeriodos, true);
			}

			tablasInfoAdicional.add(tabla);
			vp.add(tabla);
		}

		descriptorEncabezado.setContenido(vp);
		this.add(descriptorEncabezado);
	}

	private boolean esIngresoXLineaProductosNegocios(String idConcepto) {
		return "5021".equals(idConcepto);
	}
	
	private boolean esPreciosPorLineaDeProductosNegocios(String idConcepto) {
		return "5022".equals(idConcepto);
	}

	/*
	 * Pinta para cada concepto de la lista 'info' se crea una tabla que contiene la informacion requerida.
	 */
	private void pintarInfoSegmento(XMLDataList info, XMLDataObject xdoInfoComp) {
		ContenedorDescriptor descriptorEncabezado = null;

		if (info == null || info.size() == 0) {
			return;
		}
		XMLDataObject infoPeriodo = (XMLDataObject) xdoInfoComp.getObject(ConstantesVaciados.CABERCA_INFO_COMPLEMENTARIA + ConstantesVaciados.PRIMER_PERIODO);
		String planCta = infoPeriodo.getString(ConstantesVaciados.PLAN_CUENTA);
		descriptorEncabezado = new ContenedorDescriptor("Informaci\u00F3n por Segmento");
		descriptorEncabezado.setWidth("100%");
		DOM.setStyleAttribute(descriptorEncabezado.getElement(), "padding", "10px 0 0 0");

		tablasInfoSegmento = new ArrayList();

		VerticalPanel vp = new VerticalPanel();

		// Se agrega al header un texto en blanco para la primera columna
		ArrayList auxHeaders = new ArrayList(headersPeriodos);
		auxHeaders.add(0, "");

		for (int i = 0; i < info.size(); i++) {
			TablaInfoComplementaria tabla = tablasUIBuilder.getTablaInformacionSegmento((XMLDataObject) info.get(i), auxHeaders, planCta);

			tablasInfoSegmento.add(tabla);
			vp.add(tabla);
		}

		descriptorEncabezado.setContenido(vp);
		this.add(descriptorEncabezado);
	}

	private void pintarInfoMercadoDestino(XMLDataObject xdoInfo) {
		this.ticMercadoDestino = tablasUIBuilder.getTablasInfoAdicionalMercadoOEstructura(xdoInfo, headersPeriodos);
		this.add(ticMercadoDestino);
	}

	private void pintarInfoEspecifica(XMLData info) {
		XMLDataList xdlInfo = (XMLDataList) info;
		cdInfoEspecifica = new ContenedorDescriptor("Información Específica");

		cdInfoEspecifica.setWidth("100%");
		DOM.setStyleAttribute(cdInfoEspecifica.getElement(), "padding", "10px 0 0 0");

		VerticalPanel vp = new VerticalPanel();
		tablasInfoEspecifica = new ArrayList();

		tbNomConceptoInfoEsp = getTbNomConceptoInfoEsp();
		btnAgregarConcepto = getBtnAgregarConcepto();

		Grid grid = new Grid(1, 3);
		grid.setWidget(0, 0, new Label("Ingrese el nombre del nuevo Concepto: "));
		grid.setWidget(0, 1, tbNomConceptoInfoEsp);
		grid.setWidget(0, 2, btnAgregarConcepto);
		grid.setCellSpacing(5);

		vp.add(grid);

		// Existe informacion
		if (xdlInfo != null) {
			// Por cada concepto existente se pinta su respectiva tabla
			for (int i = 0; i < xdlInfo.size(); i++) {
				XMLDataObject xdoConcepto = (XMLDataObject) xdlInfo.get(i);
				String auxConcepto = xdoConcepto.getString(ConstantesVaciados.NOMBRE_CONCEPTO);
				xdoConcepto.put(ConstantesVaciados.NOMBRE_CONCEPTO, "TABESP"+auxConcepto);
				TablaInfoComplementaria tabla = tablasUIBuilder.getTablaInfoEspecifica(xdoConcepto, headersPeriodos);

				tablasInfoEspecifica.add(tabla);
				vp.add(tabla);
			}
		}

		cdInfoEspecifica.setContenido(vp);

		this.add(cdInfoEspecifica);
	}

	private void pintarInfoEstructuraCostos(XMLDataObject xdoInfo) {
		this.ticEstructuraCostos = tablasUIBuilder.getTablasInfoAdicionalMercadoOEstructura(xdoInfo, headersPeriodos);
		this.add(ticEstructuraCostos);
	}

	/*
	 * Retorna un nuevo boton.
	 */
	private Button getBtnAgregarConcepto() {
		Button b = new Button(TEXTO_BTN_AGREGAR_CONCEPTO);
		b.setHeight(ALTO_BOTON);
		b.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				if (tbNomConceptoInfoEsp.getText().trim().length() == 0) {
					procesarError(null, "Debe ingresar el nombre para el Concepto que desea agregar", null);
					return;
				}

				if (existeConcepto(tbNomConceptoInfoEsp.getText().trim())) {
					procesarError(null, "El Concepto que intenta ingresar ya existe", null);
					return;
				}

				agregarTablaConceptoInfoEspecifica();
			}
		});
		return b;
	}

	private boolean existeConcepto(final String nuevoNombre) {
		if (tablasInfoEspecifica != null && !tablasInfoEspecifica.isEmpty()) {
			for (int i = 0; i < tablasInfoEspecifica.size(); i++) {
				TablaInfoComplementaria tabla = (TablaInfoComplementaria) tablasInfoEspecifica.get(i);

				if (tabla.getTitulo().equalsIgnoreCase("TABESP"+nuevoNombre)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean existeConceptoEditado(final String nuevoNombre) {
		if (tablasInfoEspecifica != null && !tablasInfoEspecifica.isEmpty()) {
		boolean duplicado = false;
		int nroGrilla = 0;
		String titulo= "TABESP"+nuevoNombre.trim();
			for (int i = 0; i < tablasInfoEspecifica.size()-1; i++) {
				
				TablaInfoComplementaria tabla = (TablaInfoComplementaria) tablasInfoEspecifica.get(i+1);
				
				if (titulo.equalsIgnoreCase("TABESP"+tabla.getTitulo())){
					nroGrilla++;
				}
			}
			if(nroGrilla>1){
				procesarError(null, "El Concepto editado que intenta ingresar ya existe", null);
				return true;
			}			
		}
		return false;
	}
	

	private Button getBtnGuardar() {
		Button b = new Button(TEXTO_BTN_GUARDAR);
		b.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				guardarInformacionComplementaria();
			}
		});
		return b;
	}

	/*
	 * Obtiene toda la informacion complementaria desde la vista y genera la estructura XML que se le pasara al controlador para invocar la operacion
	 * de guardado.
	 */
	private void guardarInformacionComplementaria() {
		SEFEAlert.confirm("Las modificaciones pueden afectar la informaci\u00F3n de los periodos posteriores.\n¿Desea guardar?", new CallbackConfirm());

	}
	protected class CallbackConfirm implements SEFEModalConfirmDialogCallback{
		public void onAffirmative() {
		
			actualizarTitulosInfoEspecifica(tablasInfoEspecifica);
			controller.guardarInformacionComplementaria(getInfoSegmentoToSend(),
														getInfoComplementariaToSend(tablasInfoAdicional),
														getInfoMercadoOEstructuraToSend(ticMercadoDestino),
														getInfoMercadoOEstructuraToSend(ticEstructuraCostos),
														getInfoComplementariaToSend(tablasInfoEspecifica));
		}
		public void onCancel() {
			// TODO Apéndice de método generado automáticamente
			
		}
	}
	
	private void actualizarTitulosInfoEspecifica(List tablas){
		
		for (int i = 0; i < tablas.size(); i++) {
			TablaInfoComplementaria tabla = (TablaInfoComplementaria) tablas.get(i);
			TextBox auxTitulo = (TextBox) tabla.getTablaDescriptora().getHeader().getWidget(0, 0);	
			
			if(!existeConceptoEditado(auxTitulo.getText())){
				
			
			
			if(tabla.getTitulo().substring(0, 6).equals("TABESP")){
				String auxiliar= tabla.getTitulo().substring(6, tabla.getTitulo().length());
				String textoEnPantalla = auxTitulo.getText().toUpperCase();
				//if (!textoEnPantalla.equals(auxiliar)){
					tabla.setTitulo(textoEnPantalla);//nuevo nombre para el concepto
			//	}
			}
			}else{
				boolean cancelarGuardar= true;
				if (cancelarGuardar){
					
				}
				  
			}

		}
	}
	
	
	
	private XMLDataObject getInfoMercadoOEstructuraToSend(TablaInfoComplementaria tabla) {
		XMLDataObject xdoConcepto;

		if (tabla == null) {
			return null;
		}

		xdoConcepto = getInfoFromTablasSinTitulo(tabla);

		return xdoConcepto;
	}

	private XMLDataList getInfoComplementariaToSend(List tablas) {
		XMLDataList lstConceptos = null;

		if (tablas == null || tablas.isEmpty()) {
			return null;
		}

		lstConceptos = new XMLDataList();

		for (int i = 0; i < tablas.size(); i++) {
			TablaInfoComplementaria tabla = (TablaInfoComplementaria) tablas.get(i);
			XMLDataObject xdoConcepto = getInfoFromTablasSinTitulo(tabla);

			if (xdoConcepto != null)
				lstConceptos.add(xdoConcepto);
		}

		return lstConceptos;
	}

	private XMLDataList getInfoSegmentoToSend() {
		XMLDataList lstConceptos = null;

		if (tablasInfoSegmento == null || tablasInfoSegmento.isEmpty()) {
			return null;
		}

		lstConceptos = new XMLDataList();

		for (int i = 0; i < tablasInfoSegmento.size(); i++) {
			TablaInfoComplementaria tabla = (TablaInfoComplementaria) tablasInfoSegmento.get(i);
			XMLDataObject xdoConcepto = getInfoFromTablasConTitulos(tabla, false);

			if (xdoConcepto != null)
				lstConceptos.add(xdoConcepto);
		}

		return lstConceptos;
	}

	private TextBox getTbNomConceptoInfoEsp() {
		TextBox tb = new TextBox();
		tb.setWidth("300px");
		tb.setMaxLength(ConstantesVaciados.MAX_LONGITUD_APERTURAS);
		tb.setTitle(ConstantesVaciados.TITLE_LONGITUD_MAXIMA);
		return tb;
	}

	private void agregarTablaConceptoInfoEspecifica() {
		XMLDataObject xdoConceptoNuevo = new XMLDataObject();
		xdoConceptoNuevo.put(ConstantesVaciados.NOMBRE_CONCEPTO, "TABESP"+tbNomConceptoInfoEsp.getText().toUpperCase());
		
		if (tablasInfoEspecifica == null) {
			tablasInfoEspecifica = new ArrayList();
		}

		TablaInfoComplementaria tabla = tablasUIBuilder.getTablaInfoEspecifica(xdoConceptoNuevo, headersPeriodos);

		tablasInfoEspecifica.add(tabla);

		cdInfoEspecifica.getPanel().add(tabla);
	}
	
	//-------------------------------------------------------------------
	private TextBox getTxtConcepto(String titulo, TablaInfoComplementaria tabla) {
		final TablaDescriptoraFooter tablaDescriptora =tabla.getTablaDescriptora();
		TextBox b = new TextBox();
		TextBox tbNombre = new TextBox();
		tbNombre.setWidth("300px");
		tbNombre.setMaxLength(75);
		tbNombre.setText(titulo);
		tbNombre.addFocusListener(new FocusListener(){
	
			public void onLostFocus(Widget sender) {
				tablaDescriptora.getHeader();
			}

			public void onFocus(Widget sender) {
				tablaDescriptora.getHeader();
				// TODO Auto-generated method stub
				
			}});
		return b;
	}
	//-------------------------------------------------------------------

	

	/*
	 * Saca de una tabla la información que esta contiene y genera la estructura xml correspondiente. Este metodo sirve para las tablas que NO tienen
	 * registros titulos.
	 */
	private XMLDataObject getInfoFromTablasSinTitulo(TablaInfoComplementaria tabla) {
		XMLDataObject xdoConcepto = null;
		XMLDataList aperturas = null;

		List filas = tabla.getFilas();

		if (filas == null || filas.isEmpty()) {
			return xdoConcepto;
		}
		xdoConcepto = new XMLDataObject();
		xdoConcepto.setId(tabla.getId());
		xdoConcepto.put(ConstantesVaciados.NOMBRE_CONCEPTO, tabla.getTitulo());
		xdoConcepto.put(ConstantesVaciados.FLAG_TABLA_TIENE_UNIDAD, new Boolean(tabla.getTieneUnidad()));

		if (tabla.getTieneUnidad() && !tabla.isTieneUnidadesMedida()) {
			xdoConcepto.put(ConstantesVaciados.ID_UNIDAD, getIdUnidadFromTabla(tabla));
		}
		aperturas = null;

		for (int i = 0; i < filas.size(); i++) {
			FilaInfoComplementaria filaSubConcepto = (FilaInfoComplementaria) filas.get(i);

			if (filaSubConcepto.getColumnas() != null && !filaSubConcepto.getColumnas().isEmpty()) {
				XMLDataObject xdoApertura = getXMLConcepto(filaSubConcepto, tabla.getTieneUnidad());
				if (xdoApertura!=null){
					if (aperturas==null){
						aperturas = new XMLDataList();
					}
					aperturas.add(xdoApertura);
				}
			}
		}
		if (aperturas!=null){
			xdoConcepto.put(ConstantesVaciados.LST_SUB_CONCEPTOS, aperturas);
		}else{
			return null;
		}

		return xdoConcepto;
	}

	private String getIdUnidadFromTabla(TablaInfoComplementaria tabla) {
		ArrayList headers = tabla.getHeaders();
		ListBox lb = (ListBox) headers.get(COLUMNA_UNIDAD);
		return (lb.getValue(lb.getSelectedIndex()));
	}

	/*
	 * Saca de una tabla la información que esta contiene y genera la estructura xml correspondiente. Este metodo sirve para las tablas que contiene
	 * registros titulos, es decir que en uno de los registros corresponde a la informacion del concepto que se muestra.
	 */
	private XMLDataObject getInfoFromTablasConTitulos(TablaInfoComplementaria tabla, boolean tieneUnidad) {
		FilaInfoComplementaria fila = null;
		XMLDataObject xdoConcepto = null;
		XMLDataList aperturas = null;

		List filas = tabla.getFilas();

		if (filas == null || filas.isEmpty()) {
			return null;
		}

		// Se recupera el registro titulo... el Concepto de la tabla
		fila = (FilaInfoComplementaria) filas.get(0);
		xdoConcepto = getXMLConcepto(fila, tieneUnidad);
		if (xdoConcepto==null){
			xdoConcepto = new XMLDataObject();
		}
		xdoConcepto.put(ConstantesVaciados.FLAG_TABLA_TIENE_UNIDAD, new Boolean(tabla.getTieneUnidad()));

		if (tieneUnidad) {
			xdoConcepto.put(ConstantesVaciados.UNIDAD, getIdUnidadFromTabla(tabla));
		}

		aperturas = new XMLDataList();

		for (int i = 1; i < filas.size(); i++) {
			FilaInfoComplementaria filaSubConcepto = (FilaInfoComplementaria) filas.get(i);

			if (filaSubConcepto.getColumnas() != null && !filaSubConcepto.getColumnas().isEmpty()) {
				XMLDataObject xdoApertura = getXMLConcepto(filaSubConcepto, tieneUnidad);
				if (xdoApertura!=null){
					aperturas.add(xdoApertura);
				}
			}
		}

		xdoConcepto.put(ConstantesVaciados.LST_SUB_CONCEPTOS, aperturas);
		if (xdoConcepto.getId()==null|| xdoConcepto.getId().equals("NA")){
			return null;
		}else{
			return xdoConcepto;
		}
	}

	/*
	 * Genera un objeto XMLDataObject para la fila pasada como parametro
	 */
	private XMLDataObject getXMLConcepto(FilaInfoComplementaria fila, boolean tieneUnidad) {
		XMLDataObject xdoConcepto = new XMLDataObject();
		int indexCol = 0;

		xdoConcepto.setId(fila.getId());

		if (tieneUnidad) {
			xdoConcepto.put(ConstantesVaciados.ID_UNIDAD, getValueFromWidget((ColumnaInfoComplementaria) fila.getColumna(indexCol++)));
		}
		xdoConcepto.put(ConstantesVaciados.NOMBRE_CONCEPTO, getValueFromWidget((ColumnaInfoComplementaria) fila.getColumna(indexCol++)));
		String per3 = getValueFromWidget((ColumnaInfoComplementaria) fila.getColumna(indexCol++));
		String per2 = getValueFromWidget((ColumnaInfoComplementaria) fila.getColumna(indexCol++));
		String per1 = getValueFromWidget((ColumnaInfoComplementaria) fila.getColumna(indexCol++));
		if ((per3 == null && per2 == null && per1 == null )|| (per3.equals("")&&per2.equals("")&&per1.equals(""))){
			return null;
		}

		xdoConcepto.put(ConstantesVaciados.PREFIJO_PERIODO + 3, per3);
		xdoConcepto.put(ConstantesVaciados.PREFIJO_PERIODO + 2, per2);
		xdoConcepto.put(ConstantesVaciados.PREFIJO_PERIODO + 1, per1);

		return xdoConcepto;
	}

	private String getValueFromWidget(ColumnaInfoComplementaria c) {
		String value = null;
		Widget w = c.getWidget();

		if (w instanceof Label) {
			value = ((Label) w).getText();
		}

		if (w instanceof InputNumeroSefe) {
			value = doubleToString(((InputNumeroSefe) w).getDouble());
		} else if (w instanceof TextBox) {
			value = ((TextBox) w).getText();
		}

		if (w instanceof ListBox) {
			ListBox lb = (ListBox) w;
			int selected = lb.getSelectedIndex();
			value = lb.getValue(selected);
		}

		return value;
	}

	private String doubleToString(Double dValue) {
		return (dValue != null ? dValue.toString() : "");
	}

	private void pintarEncabezado(XMLDataObject xdoInfo) {
		for (int i = (ConstantesVaciados.TERCER_PERIODO - 1); i >= 0; i--) {
			XMLDataObject infoPeriodo;
			int indicePeriodo = i + 1;

			infoPeriodo = (XMLDataObject) xdoInfo.getObject(ConstantesVaciados.CABERCA_INFO_COMPLEMENTARIA + indicePeriodo);

			String planCta = infoPeriodo.getString(ConstantesVaciados.PLAN_CUENTA);
			String conAjuste = infoPeriodo.getString(ConstantesVaciados.FLAG_AJUSTE);
			String responsable = infoPeriodo.getString(ConstantesVaciados.RESPONSABLE);
			String unidadMedida = infoPeriodo.getString(ConstantesVaciados.UNIDAD);
			String moneda = infoPeriodo.getString(ConstantesVaciados.MONEDA);
			String meses = infoPeriodo.getString(ConstantesVaciados.MESES);

			String unidadMoneda = (unidadMedida != null ? (moneda != null ? unidadMedida + " " + moneda : unidadMedida) : (moneda != null ? moneda : null));

			// Ojo aca, en el grid del encabezado la columna 0 muestra el tercer periodo, y la columna 3 muestra el primer periodo
			pintarEncabezado(indicePeriodo, planCta, conAjuste, responsable, unidadMoneda, meses);
		}
	}

	private void pintarEncabezado(int indPeriodo, String planCta, String conAjuste, String responsable, String unidadMedida, String meses) {
		Grid grid = (Grid) hpEncabezado.getWidget(0);
		int columna;

		switch (indPeriodo) {
		case ConstantesVaciados.PRIMER_PERIODO:
			columna = 3;
			break;
		case ConstantesVaciados.SEGUNDO_PERIODO:
			columna = 2;
			break;
		default:
			columna = 1;
			break;
		}

		((TextBox) grid.getWidget(INFO_ENCABEZADO_PLAN_CTAS, columna)).setText(planCta);
		((TextBox) grid.getWidget(INFO_ENCABEZADO_CON_AJUSTE, columna)).setText(conAjuste);
		((TextBox) grid.getWidget(INFO_ENCABEZADO_RESPONSABLE, columna)).setText(responsable);
		((TextBox) grid.getWidget(INFO_ENCABEZADO_UNIDAD_MONEDA, columna)).setText(unidadMedida);
		((TextBox) grid.getWidget(INFO_ENCABEZADO_MESES, columna)).setText(meses);
	}

	private void pintarBotonGuardar() {
		this.btnGuardar = getBtnGuardar();

		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hp.add(btnGuardar);

		this.add(hp);
	}

	public void procesarError(String codigo, String msg, Throwable e) {
		if (e != null)
			SEFEAlert.alert(msg, e);
		else
			SEFEAlert.alert(msg);
	}

	public void pintarInfoComplementaria(XMLData data) {
		XMLDataObject xdoInfoComp = (XMLDataObject) data;

		clear();
		init();

		cargarHeadersPeriodos(xdoInfoComp);

		pintarEncabezado(xdoInfoComp);

		XMLDataList xdlInfoSegmento = (XMLDataList) xdoInfoComp.getObject(ConstantesVaciados.INFORMACION_COMPLEMENTARIA_INFO_SEGMENTO);
		XMLDataList xdlInfoAdicional = (XMLDataList) xdoInfoComp.getObject(ConstantesVaciados.INFORMACION_COMPLEMENTARIA_INFO_ADICIONAL);
		XMLDataObject xdoInfoMercadoDestino = (XMLDataObject) xdoInfoComp.getObject(ConstantesVaciados.INFORMACION_COMPLEMENTARIA_INFO_MERCADO_DESTINO);
		XMLDataObject xdoInfoEstructuraCostos = (XMLDataObject) xdoInfoComp.getObject(ConstantesVaciados.INFORMACION_COMPLEMENTARIA_INFO_ESTRUCTURA_COSTOS);
		XMLDataList xdlInfoEspecifica = (XMLDataList) xdoInfoComp.getObject(ConstantesVaciados.INFORMACION_COMPLEMENTARIA_INFO_ESPECIFICA);

		// La regla de negocio dice que Info de Segmento solo se muestra para planes IFRSx pero en rigor esta seccion se mostrará dependiendo
		// de la configuracion que se haga, ya que se ingreso a un vaciado CHGAAP y existe una cuenta de CHGAAP configurada con el flag de segmento
		// la seccion se mostrara
		if (xdlInfoSegmento != null)
			pintarInfoSegmento(xdlInfoSegmento,xdoInfoComp);

		pintarInfoAdicional(xdlInfoAdicional);
		pintarInfoMercadoDestino(xdoInfoMercadoDestino);
		pintarInfoEstructuraCostos(xdoInfoEstructuraCostos);
		pintarInfoEspecifica(xdlInfoEspecifica);
		pintarBotonGuardar();
	}

	private void cargarHeadersPeriodos(XMLDataObject xdoResp) {
		this.headersPeriodos = new ArrayList();

		String periodo1 = xdoResp.getString(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.PRIMER_PERIODO);
		String periodo2 = xdoResp.getString(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.SEGUNDO_PERIODO);
		String periodo3 = xdoResp.getString(ConstantesVaciados.PREFIJO_PERIODO + ConstantesVaciados.TERCER_PERIODO);

		this.headersPeriodos.add(getPeriodo(periodo3));
		this.headersPeriodos.add(getPeriodo(periodo2));
		this.headersPeriodos.add(getPeriodo(periodo1));
	}

	private String getPeriodo(String strPeriodo) {
		if (strPeriodo == null || strPeriodo.length() == 0)
			return "";

		Date periodo = FormatUtil.parseDate(strPeriodo);
		return FormatUtil.formatDateMMMyyyy(periodo);
	}

	public boolean isModificadoFlg() {
		return infoModificada;
	}

	public Contexto getContexto() {
		return this.ctx;
	}

	public void mostrarMensaje(String msg) {
		SEFEAlert.info(msg);
	}

	public void onChange(Widget sender) {
		infoModificada = true;
	}
}
