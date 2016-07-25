package com.bch.sefe.rating.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bch.sefe.comun.rating.ConstantesRating;
import com.bch.sefe.comun.rating.vo.Agricola;
import com.bch.sefe.comun.ui.Botonera;
import com.bch.sefe.comun.ui.ContenedorDescriptor;
import com.bch.sefe.comun.ui.Espaciador;
import com.bch.sefe.comun.ui.InputFile;
import com.bch.sefe.comun.ui.Paginador;
import com.bch.sefe.comun.ui.ReportSubmitter;
import com.bch.sefe.comun.ui.TablaDescriptora;
import com.bch.sefe.comun.utils.FormatUtil;
import com.bch.sefe.comun.utils.GeneradorRequestResponse;
import com.bch.sefe.comun.utils.PropertiesSEFEUtil;
import com.bch.sefe.comun.utils.SEFEAlert;
import com.bch.sefe.comun.utils.SEFEBusyRequestCallback;
import com.bch.sefe.comun.utils.ServiceInvoker;
import com.bch.sefe.comun.utils.URLBuilder;
import com.bch.sefe.comun.utils.XMLUtil;
import com.bch.sefe.comun.vaciados.vo.CriterioBusqueda;
import com.bch.sefe.comun.vo.Contexto;
import com.bch.sefe.comun.vo.XMLDataObject;
import com.bch.sefe.rating.client.ui.InputFileRating;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.libideas.logging.shared.Level;
import com.google.gwt.libideas.logging.shared.Log;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RatingProyectadoAgricolaView extends Composite {
	private VerticalPanel root = new VerticalPanel();
	private ContenedorDescriptor contenedor = new ContenedorDescriptor("Seleccione el archivo");
	private ContenedorDescriptor contenedorTabla = null;
	private HorizontalPanel contenedorFileUploader = new HorizontalPanel();
	private InputFileRating fileUploader = null;
	private VerticalPanel contenedorBotonera = new VerticalPanel();
	private VerticalPanel contenedorBotoneraConfirmar = new VerticalPanel();
	private Botonera botonera = null;
	private Botonera botoneraConfirmar =null;
	private Contexto contexto = null;
	private MediadorRating mediador;
	TablaDescriptora tabla = null;
	ArrayList registrosParaTabla = new ArrayList();
	TablaDescriptora tablaBalances = null;
	VerticalPanel panelTablaBalances = new VerticalPanel();
	VerticalPanel balancesPanel = new VerticalPanel();
	private HorizontalPanel panelEncabezado = null;
	private EncabezadoRtgProyectadoDetalle encabezadoDetalle = null;
	List lista = new ArrayList();
	private EncabezadoRtgProyectadoAgricolaReportes tablaAlertas = null;
	private static final String LABEL_ALERTAS = "Alertas";
	final static String IMG_SRC_FICHA = "comentario.gif";
	private Paginador paginador;
	public static final String MI_CATEGORIA = "com.bch.sefe.rating.client.view.RatingProyectadoAgricolaView";
	
	public RatingProyectadoAgricolaView(Contexto ctx, MediadorRating mediadorRating) {
		this.contexto = ctx;
		this.mediador = mediadorRating;
		this.initWidget(root);
	}

	private void inicializarVista() {
		if (panelEncabezado!= null) {
			panelEncabezado.clear();
		}
		this.panelEncabezado = new HorizontalPanel();
		encabezadoDetalle = new EncabezadoRtgProyectadoDetalle(contexto, mediador);
		tablaAlertas = new EncabezadoRtgProyectadoAgricolaReportes(contexto, mediador);
		contenedor.setCSSPropertyTitulo("popupPanelNotaDescriptorTitulo");
		contenedor.getContenedor().getWidget(0).setStyleName("popupPanelNotaDescriptorTitulo");
		fileUploader = new InputFileRating(contexto,mediador,this);
		fileUploader.setFileExtensionAcceptanceCriteria(InputFile.EXTENSION_EXCEL);
		fileUploader.setSize("20px", "auto");
		contenedorFileUploader.clear();
		contenedorFileUploader.add(fileUploader);
		botonera = new Botonera();
		botoneraConfirmar = new Botonera();
		Button descargar = botonera.addBoton("Descargar Plantilla Vaciado");
		Button aceptar = botonera.addBoton("Cargar Vaciado Agricola");
		Button calcular = botoneraConfirmar.addBoton("Calcular");
		Button confirmar = botoneraConfirmar.addBoton("Confirmar");
		Integer estadoRatingInd = (Integer) mediador.buscarEnContexto(ConstantesRating.CTX_ESTADO_RTG_INDIVIDUAL);
		if (estadoRatingInd!=null && (estadoRatingInd.equals(ConstantesRating.CLASIF_ID_RATING_VIGENTE) || estadoRatingInd.equals(ConstantesRating.CLASIF_ID_RATING_HISTORICO)) ){
			aceptar.setEnabled(false);
			calcular.setEnabled(false);
			confirmar.setEnabled(false);
		}
		confirmar.addClickListener(new ConfirmarListener());
		aceptar.addClickListener(new CargarArchivoListener());
		descargar.addClickListener(new DescargarPlantillaListener());
		calcular.addClickListener(new CalcularListener());
		contenedorBotonera.clear();
		contenedorBotoneraConfirmar.clear();
		contenedorBotonera.add(botonera);
		contenedorBotoneraConfirmar.setWidth("68%");
		contenedorBotoneraConfirmar.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		contenedorBotoneraConfirmar.add(botoneraConfirmar);
		contenedor.setContenido(contenedorFileUploader);
		balancesPanel.clear();
		balancesPanel.setWidth("850px");
		panelTablaBalances.clear();
		this.paginador = new Paginador(5);
		tablaBalances = new TablaDescriptora("", inicializarEncabezados(), true);
		tablaBalances.getTable().setScrollingEnabled(false);

		tablaBalances.setColumnWidth(0, 30);
		tablaBalances.setColumnWidth(1, 60);
		tablaBalances.setColumnWidth(2, 45);
		tablaBalances.setColumnWidth(3, 60);
		tablaBalances.setColumnWidth(4, 70);
		tablaBalances.setColumnWidth(5, 105);
		tablaBalances.setColumnWidth(6, 70);
		tablaBalances.setColumnWidth(7, 80);
		tablaBalances.setColumnWidth(8, 70);
		tablaBalances.setColumnWidth(9, 70);
		tablaBalances.setColumnWidth(10, 70);
		tablaBalances.setColumnWidth(11, 100);
		tablaBalances.redrawResize();
		buscarBalances();
		panelEncabezado.add(encabezadoDetalle);
		panelEncabezado.add(new Espaciador("1em", "1em"));
		panelEncabezado.add(tablaAlertas);
		panelTablaBalances.add(tablaBalances);
		panelTablaBalances.add(paginador);
		balancesPanel.add(panelTablaBalances);
		contenedorTabla = new ContenedorDescriptor("Vaciados Agricolas");
		contenedorTabla.setContenido(balancesPanel);
		root.add(panelEncabezado);
		root.add(contenedorTabla);
		root.add(contenedorBotoneraConfirmar);
		root.add(new Espaciador("1em"));
		root.add(contenedor);
		root.add(contenedorBotonera);
		root.add(new Espaciador("1em"));
	}
	private EncabezadoRtgProyectadoAgricolaReportes inicializarTablaAlertas() {
		ArrayList headers = new ArrayList();
		headers.add("");
		headers.add("");
		EncabezadoRtgProyectadoAgricolaReportes encabezado = new EncabezadoRtgProyectadoAgricolaReportes(contexto, mediador);
		TablaDescriptora tabla = new TablaDescriptora("", headers, false);
		tabla.getTable().setScrollingEnabled(false);
		tabla.getHeader().getRowFormatter().setStyleName(0, "hidden");
		tabla.getHeader().getRowFormatter().setStyleName(1, "hidden");
		tabla.getDataGrid().getCellFormatter().setHorizontalAlignment(0, 0, HasAlignment.ALIGN_CENTER);
		tabla.getDataGrid().setText(0, 0, LABEL_ALERTAS);				
		DOM.setElementProperty(tabla.getDataGrid().getCellFormatter().getElement(0, 0), "rowSpan", "8");
		for (int i = 0; i <= 7 ; i++) {
			tabla.getDataGrid().getRowFormatter().setStyleName(i, "tablaDescriptoraData");			
		}
		tabla.getDataGrid().getColumnFormatter().setStyleName(1, "hidden");
		tabla.setScrollTableSize("469px", "179px");
		tabla.setColumnWidth(0, 100);
		tabla.setColumnWidth(1, 200);
		tabla.redrawResize();
		encabezado.setTablaEncabezadoRatingProyectado(tabla);
		return encabezado;
	}
	
	public void buscarBalances() {		
		contexto.setOpCatalogoGeneral(false);
		contexto.setOperacion(PropertiesSEFEUtil.getKeyRating().CodigoOperacionBuscarBalances());
		RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, URLBuilder.getServerUrl());
		reqBuilder.setHeader("Content-Type", "text/xml");
		CriterioBusqueda busqueda = new CriterioBusqueda();
		busqueda.setRutCliente(contexto.getRutCliente());
		XMLDataObject xml = new XMLDataObject();
		String idRatingInd = mediador.buscarEnContexto(ConstantesRating.CTX_ID_RATING).toString();
		xml.put(ConstantesRating.ID_RATING, idRatingInd);
		xml.put(ConstantesRating.RUT_CLIENTE, contexto.getRutCliente());
		String xmlToSend = GeneradorRequestResponse.generarRequest(contexto, xml);

		try {
			reqBuilder.sendRequest(xmlToSend, new SEFEBusyRequestCallback() {

				public void onSEFEBusyError(Request request, Throwable exception) {
					// TODO Apéndice de método generado automáticamente

				}

				public void onSEFEBusyResponseReceived(Request request, Response response) {
					// Aqui se recibiran los objetos contexto y VOs
					// (Vaciados en este caso)
					Long idRatingInd = (Long) mediador.buscarEnContexto(ConstantesRating.CTX_ID_RATING);
					HashMap parametros = new HashMap();
					registrosParaTabla.clear();

					// Se recibe el string XML desde el server y se
					// procesa en VOs
					String xmlResponse = response.getText();
					if (!XMLUtil.getCodeFromResponse(xmlResponse).equalsIgnoreCase("0")) {
						SEFEAlert.alert(XMLUtil.getMessageFromResponse(xmlResponse));
						return;
					}
					parametros = GeneradorRequestResponse.generarResponse(xmlResponse);
					lista = (List) parametros.get(GeneradorRequestResponse.KEY_CONTENIDO);

					if (lista == null || lista.isEmpty()) {
							Agricola agr=null;
							setearEncabezado(agr);
							setearCuadroReportes(agr);
					} else {
							setearEncabezado(null);
							setearCuadroReportes(null);
							for (int i = 0; i < lista.size(); i++) {
								
								Agricola agricola = (Agricola) lista.get(i);
								RadioButton radioButton = crearRadioButton(agricola.getIdAgricola(), agricola.getIdRatingProyectado());
								if (idRatingInd!=null && idRatingInd.equals(agricola.getIdRatingInd())) {
									mediador.ponerEnContexto(ConstantesRating.CTX_ID_RATING_PROYECTADO, agricola.getIdRatingProyectado());
									setearEncabezado(agricola);
									setearCuadroReportes(agricola);
									radioButton.setChecked(true);
								}
								ArrayList filas = new ArrayList();
								
								DOM.setElementAttribute(radioButton.getElement(), "id", agricola.getIdParteInv().toString() + ";"
										+ FormatUtil.formatDate(agricola.getFecha()));
								DOM.setElementProperty(radioButton.getElement(), ConstantesRating.PROPIEDAD_ID_PARTE_INVOL, agricola.getIdParteInv().toString());
								DOM.setElementProperty(radioButton.getElement(), ConstantesRating.PROPIEDAD_ID_AGRICOLA, agricola.getIdAgricola().toString());
								DOM.setElementProperty(radioButton.getElement(), ConstantesRating.PROPIEDAD_ID_PROYECTADO, agricola.getIdRatingProyectado() != null ? agricola.getIdRatingProyectado().toString():"");
								filas.add(radioButton);
	
								DOM.setElementAttribute(((CheckBox) filas.get(0)).getElement(), "idSoe", String.valueOf(agricola.getIdAgricola()));
								filas.add(FormatUtil.formatDate(agricola.getFecha()));
								filas.add(agricola.getEstado());
								filas.add(FormatUtil.formatDate(agricola.getFechaEstado()));
								filas.add(agricola.getUsuario());
								filas.add(agricola.getEjecutivo());
								filas.add(agricola.getOficinaRegion());
								filas.add(agricola.getRubro());
								filas.add(agricola.getTemporada().toString());
								filas.add(agricola.getNombreGrupo());
								filas.add(agricola.getTipoExportacionBovina());
								filas.add(agricola.getDescripcion());
								registrosParaTabla.add(filas);
							}
							tablaBalances.setData(registrosParaTabla);
							if(registrosParaTabla.size() > 0) {
								paginador.registrarComponentePaginable(tablaBalances);
							}
						}

				}

			});
		} catch (RequestException e) {
			Log.log(e.getMessage(), Level.SEVERE, MI_CATEGORIA, e);
		}
	}
	
	private ImagenFicha crearImagenFicha(Long idAgricola) {

		ImagenFicha imagenFicha = new ImagenFicha(idAgricola);
		imagenFicha.addClickListener(new ImageHandler(idAgricola));

		return imagenFicha;
	}
	private class ImagenFicha extends Image {
		private Long idAgricola  = null;
		public ImagenFicha(Long idAgricola) {
			setIdAgricola(idAgricola);
			setUrl(IMG_SRC_FICHA);
		}
		public Long getIdAgricola() {
			return idAgricola;
		}
		public void setIdAgricola(Long idAgricola) {
			this.idAgricola = idAgricola;
		}


		
	}
	
	private class ImageHandler implements ClickListener {
		private Long idAgricola  = null;

		public ImageHandler(Long idAgricola) {
			this.idAgricola = idAgricola;
		}

		public void onClick(Widget sender) {
			generarRptFichaRating();
		}

		private void generarRptFichaRating() {
			XMLDataObject parametros = new XMLDataObject();
			parametros.put(ConstantesRating.RUT_CLIENTE, contexto.getRutCliente());
			parametros.put(ConstantesRating.ID_VACIADO, this.idAgricola);
			parametros.put(ConstantesRating.ID_BANCA , contexto.getIdPlantilla());
			parametros.put("tipo", new Integer(1));
			contexto.setOpCatalogoGeneral(false);	

			contexto.setOperacion("300850");

			String requestXML = GeneradorRequestResponse.generarRequest(contexto, parametros);

			ReportSubmitter.submit(requestXML);

		}

		public Long getIdAgricola() {
			return idAgricola;
		}

		public void setIdAgricola(Long idAgricola) {
			this.idAgricola = idAgricola;
		}


	}
	
	private void setearEncabezado(Agricola agricola) {
		encabezadoDetalle.setDetalleTablaDescriptora(agricola);
	}
	private void setearCuadroReportes(Agricola agricola){
		tablaAlertas.setDetalleTablaDescriptora(agricola);
	}
	private ArrayList inicializarEncabezados() {
		ArrayList encabezados = new ArrayList();
		encabezados.add("");
		encabezados.add("Fecha");
		encabezados.add("Estado");
		encabezados.add("Fecha Estado");
		encabezados.add("Usuario");
		encabezados.add("Ejecutivo");
		encabezados.add("Oficina");
		encabezados.add("Rubro");
		encabezados.add("Temporada");
		encabezados.add("Nombre Grupo");
		encabezados.add("Tipo de Explotación");
		encabezados.add("Descripción");
		return encabezados;
	}

	private RadioButton crearRadioButton(Long idAgricola, Long idProyectado) {

		RadioButton radioButton = new RadioButton("");
		radioButton.addClickListener(new RadioButtonHandler(idAgricola,idProyectado));

		return radioButton;
	}

	private class RadioButtonHandler implements ClickListener {
		private Long idAgricola;
		private Long idProyectado;
		
		public RadioButtonHandler(Long idAgricola, Long idProyectado) {
				this.idAgricola=idAgricola;
				this.idProyectado=idProyectado;
		}

		public void onClick(Widget sender) {
			//mediador.ponerEnContexto(ConstantesRating.CTX_ID_RATING_PROYECTADO,idProyectado);
		}
	}

	class CargarArchivoListener implements ClickListener {
		public void onClick(Widget sender) {
			StringBuffer error = new StringBuffer();
			boolean errorFlg = false;

			if (fileUploader.getFile().getFilename().length() == 0) {
				errorFlg = true;
				error.append("ERROR - Debe seleccionar un Archivo.\n");
			}else if (!isExtensionValid(fileUploader.getFile().getFilename())){
				errorFlg = true;
				error.append("Tipo de archivo (extensión del archivo) no es soportado por la aplicación. \n");
			}
			if (!errorFlg) {
				fileUploader.setCount(0);
				contexto.setOperacion(PropertiesSEFEUtil.getKeyRating().CodigoOperacionIngresarRatingProyectado());
				contexto.setIdPlantilla(contexto.getIdPlantilla());
				fileUploader.setCtx(contexto.toXML());
				XMLDataObject xml = new XMLDataObject(ConstantesRating.PARAM_CARGA_DATOS);
				xml.put(ConstantesRating.LOG_OPERADOR, contexto.getLogOperador());
				xml.put(ConstantesRating.RUT_CLIENTE, contexto.getRutCliente());
				
				String idRatingInd = mediador.buscarEnContexto(ConstantesRating.CTX_ID_RATING).toString();
				String idRatingProyectado = null;
				if (null!=mediador.buscarEnContexto(ConstantesRating.CTX_ID_RATING_PROYECTADO)){
					idRatingProyectado = mediador.buscarEnContexto(ConstantesRating.CTX_ID_RATING_PROYECTADO).toString();
				}
				xml.put(ConstantesRating.ID_RATING, idRatingInd);
				xml.put(ConstantesRating.ID_RATING_PROYECTADO,idRatingProyectado );

				fileUploader.setRequest(xml.toXML());
				fileUploader.getForm().addFormHandler(new SendFileHandler());
				fileUploader.getForm().submit();
			} else {
				SEFEAlert.alert(error.toString());
			}
		}
	}
	public boolean isExtensionValid(String fileName){
		final String EXT_XLS = "xls";
		int pointPosition=fileName.lastIndexOf(".");
		String extension=fileName.substring(pointPosition+1);
		if (extension.equals(EXT_XLS)){
			return true;
		}else{
			return false;
		}
	}
	class CalcularListener implements ClickListener {
		private XMLDataObject xmlDataObjectResponse = null;
		public void onClick(Widget sender) {
			RadioButton radio = null;
			// Se recorre la tabla y en cada iteracion se extrae el radiobutton
			// de la fila.
			// Luego se verifica si se ha seleccionado y en caso positivo, se
			// extrae el id del vaciado correspondiente a ese radiobutton.
			String idAgricola = "";
			for (int i = 0; i < tablaBalances.getDataGrid().getRowCount(); i++) {
				radio = (RadioButton) tablaBalances.getDataGrid().getWidget(i, 0);
				if (radio.isChecked()) {
					idAgricola = DOM.getElementAttribute(radio.getElement(), ConstantesRating.PROPIEDAD_ID_AGRICOLA);
					break;
				}
			}
			if (idAgricola.equals("")){
				SEFEAlert.alert("Debe tener seleccionado un Vaciado para poder calcular");
			}else{
				contexto.setOpCatalogoGeneral(false);
				contexto.setOperacion(ConstantesRating.OPER_GENERAR_PROYECCION);
				RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, URLBuilder.getServerUrl());
				reqBuilder.setHeader("Content-Type", "text/xml");
				XMLDataObject xmlDataRequest = new XMLDataObject();
				xmlDataRequest.put(ConstantesRating.RUT_CLIENTE, contexto.getRutCliente());
				xmlDataRequest.put(ConstantesRating.ID_RATING_INDIVIDUAL, mediador.buscarEnContexto(ConstantesRating.CTX_ID_RATING));
				xmlDataRequest.put(ConstantesRating.ID_VACIADO, idAgricola);
				xmlDataRequest.put(ConstantesRating.LOG_OPERADOR, contexto.getLogOperador());
				String xmlToSend = GeneradorRequestResponse.generarRequest(contexto, xmlDataRequest);
	
				try {
					reqBuilder.sendRequest(xmlToSend, new SEFEBusyRequestCallback() {
	
						public void onSEFEBusyError(Request request, Throwable exception) {
							// TODO Apéndice de método generado automáticamente
	
						}
	
						public void onSEFEBusyResponseReceived(Request request, Response response) {
							String xmlResponse = response.getText();
							
							
							
							if (!XMLUtil.getCodeFromResponse(xmlResponse).equalsIgnoreCase("0")) {
								SEFEAlert.alert(XMLUtil.getMessageFromResponse(xmlResponse));
								return;
							}
							
							xmlDataObjectResponse = (XMLDataObject) GeneradorRequestResponse.generarResponse(response.getText()).get(GeneradorRequestResponse.KEY_CONTENIDO);
							if (xmlDataObjectResponse != null) {
								procesarHeader(xmlDataObjectResponse);
								final String MENSAJE_OPERACION = "Operaci\u00F3n realizada correctamente."; 
								SEFEAlert.info(MENSAJE_OPERACION);
							}
	
						}
	
					});
				} catch (RequestException e) {
					Log.log(e.getMessage(), Level.SEVERE, MI_CATEGORIA, e);
				}
			}
		}
	}
	private void procesarHeader(XMLDataObject xmlDataObjectResponse) {
		encabezadoDetalle.gettablaEncabezadoRatingProyectado().getDataGrid().setText(0, 1, xmlDataObjectResponse.getString(ConstantesRating.NOTA));
		mediador.ponerEnContexto(ConstantesRating.CTX_ID_RATING_PROYECTADO, xmlDataObjectResponse.getLong(ConstantesRating.ID_RATING_PROYECTADO));
		Agricola agricola = new Agricola();
		agricola.setIdRatingProyectado(xmlDataObjectResponse.getLong(ConstantesRating.ID_RATING_PROYECTADO));
		agricola.setFecha(FormatUtil.parseDate(xmlDataObjectResponse.getString(ConstantesRating.FECHA)));
		agricola.setTemporada(xmlDataObjectResponse.getInteger(ConstantesRating.TEMPORADA));
		agricola.setEstado(xmlDataObjectResponse.getString(ConstantesRating.ESTADO));
		agricola.setUsuario(xmlDataObjectResponse.getString(ConstantesRating.RESPONSABLE));
		agricola.setNota( xmlDataObjectResponse.getDouble(ConstantesRating.NOTA));
		agricola.setDescripcion( xmlDataObjectResponse.getString(ConstantesRating.DESCRIPCION));
		agricola.setDescripcion( xmlDataObjectResponse.getString(ConstantesRating.DESCRIPCION));
		agricola.setIdRatingInd((Long) mediador.buscarEnContexto(ConstantesRating.CTX_ID_RATING));		
		agricola.setIdAgricola(Long.valueOf(xmlDataObjectResponse.getString(ConstantesRating.ID_VACIADO)));
		setearEncabezado(agricola);
		setearCuadroReportes(agricola);
	}
	class ConfirmarListener implements ClickListener {
		private XMLDataObject xmlDataObjectResponse = null;
		public void onClick(Widget sender) {
			RadioButton radio = null;
			// Se recorre la tabla y en cada iteracion se extrae el radiobutton
			// de la fila.
			// Luego se verifica si se ha seleccionado y en caso positivo, se
			// extrae el id del vaciado correspondiente a ese radiobutton.
			String idAgricola = "";
			Long idProyectado=null;
			for (int i = 0; i < tablaBalances.getDataGrid().getRowCount(); i++) {
				radio = (RadioButton) tablaBalances.getDataGrid().getWidget(i, 0);
				if (radio.isChecked()) {
					idAgricola = DOM.getElementAttribute(radio.getElement(), ConstantesRating.PROPIEDAD_ID_AGRICOLA);
					idProyectado = (Long) mediador.buscarEnContexto(ConstantesRating.CTX_ID_RATING_PROYECTADO);
					break;
				}
			}
			if (idAgricola==null){
				SEFEAlert.alert("Debe tener seleccionado un Vaciado para poder calcular.");
			}else if (idProyectado== null || idProyectado.equals("")) {
				SEFEAlert.alert("El vaciado agricola no ha sido calculado. Favor, presione calcular antes de confirmar");
			}
			else {
				contexto.setOpCatalogoGeneral(false);
				contexto.setOperacion(ConstantesRating.OPER_CONFIRMAR_PROYECCION);
				RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, URLBuilder.getServerUrl());
				reqBuilder.setHeader("Content-Type", "text/xml");
				XMLDataObject xmlDataRequest = new XMLDataObject();
				xmlDataRequest.put(ConstantesRating.RUT_CLIENTE, contexto.getRutCliente());
				xmlDataRequest.put(ConstantesRating.ID_RATING_INDIVIDUAL, mediador.buscarEnContexto(ConstantesRating.CTX_ID_RATING));
				xmlDataRequest.put(ConstantesRating.ID_AGRICOLA, idAgricola);
				xmlDataRequest.put(ConstantesRating.LOG_OPERADOR, contexto.getLogOperador());
				xmlDataRequest.put(ConstantesRating.ID_RATING_PROYECTADO, mediador.buscarEnContexto(ConstantesRating.CTX_ID_RATING_PROYECTADO));
				String xmlToSend = GeneradorRequestResponse.generarRequest(contexto, xmlDataRequest);
	
				try {
					reqBuilder.sendRequest(xmlToSend, new SEFEBusyRequestCallback() {
	
						public void onSEFEBusyError(Request request, Throwable exception) {

	
						}
	
						public void onSEFEBusyResponseReceived(Request request, Response response) {
							String xmlResponse = response.getText();
							
							
							
							if (!XMLUtil.getCodeFromResponse(xmlResponse).equalsIgnoreCase("0")) {
								SEFEAlert.alert(XMLUtil.getMessageFromResponse(xmlResponse));
								return;
							}
							xmlDataObjectResponse = (XMLDataObject) GeneradorRequestResponse.generarResponse(response.getText()).get(GeneradorRequestResponse.KEY_CONTENIDO);
							if (xmlDataObjectResponse != null) {
								procesarHeader(xmlDataObjectResponse);
								//buscarBalances();
								final String MENSAJE_OPERACION = "Operaci\u00F3n realizada correctamente."; 
								buscarBalances();
								SEFEAlert.info(MENSAJE_OPERACION);
							}
	
						}
	
					});
				} catch (RequestException e) {
					Log.log(e.getMessage(), Level.SEVERE, MI_CATEGORIA, e);
				}
			}
		}
	}
	class DescargarPlantillaListener implements ClickListener {
		public void onClick(Widget sender) {

			// Logica para generar Reporte
			GWT.log("Lanzando Reporte Rating Proyectado...", null);
			XMLDataObject xmlDataRequest = new XMLDataObject();
			
			xmlDataRequest.put(ConstantesRating.REP_TIPO_REPORTE, ConstantesRating.REP_TIPO_XLS);
			xmlDataRequest.put(ConstantesRating.LOG_OPERADOR, contexto.getLogOperador());
			contexto.setOperacion(ConstantesRating.OPER_DESCARGAR_PLANTILLA);
			
			ServiceInvoker.sendReporteRequest(xmlDataRequest, contexto, ConstantesRating.OPER_DESCARGAR_PLANTILLA);
		
		}
	}
	protected class SendFileHandler implements FormHandler {

		private int count = 0;

		public void onSubmit(FormSubmitEvent event) {
			if (count > 0) {
				return;
			} else {
				GWT.log("Estamos cargando el archivo...", null);
			}
		}

		public void onSubmitComplete(FormSubmitCompleteEvent event) {
			// Esto se esta ejecutando dos veces por lo que se utiliza una
			// variable de control.
			GWT.log(String.valueOf(count), null);
			if (count++ > 0) {
				return;
			} else {

			}
		}
	}

	public Contexto getContexto() {
		return contexto;
	}

	public void setContexto(Contexto contexto) {
		this.contexto = contexto;
	}

	public MediadorRating getMediador() {
		return mediador;
	}

	public void setMediador(MediadorRating mediador) {
		this.mediador = mediador;
	}

	public void refresh() {
		if (root != null){
			root.clear();
		}
		
		inicializarVista();
		
	}
}
