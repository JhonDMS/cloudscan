package com.bch.sefe.vaciado.client.view;

import java.util.ArrayList;
import java.util.List;

import com.bch.sefe.comun.ui.CalculadoraDirectionalMovementHandler;
import com.bch.sefe.comun.ui.CalculadoraV2;
import com.bch.sefe.comun.ui.InputNumeroSefe;
import com.bch.sefe.comun.ui.TablaDescriptoraFooter;
import com.bch.sefe.comun.utils.PropertiesSEFEUtil;
import com.bch.sefe.comun.vaciados.ConstantesVaciados;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TablaInfoComplementaria extends HorizontalPanel implements ISourceTablaEditableEvent {
	private TablaDescriptoraFooter tabla;
	private Button btnAgregar;

	private List filas;
	private ITablaEditableListener listener;
	private boolean permiteAgregar;
	private boolean permiteEliminar;
	private ArrayList headers;
	private String titulo;
	private boolean esPaginable;
	private String id;
	private boolean tieneUnidad;
	private boolean tieneUnidadesMedida; //Req. 7.4.8.2 variable utilizada para el manejo de multiples und de med en una tabla
	private List inputsDireccionales;
	private VerticalPanel vp; 
	


	//
	private Image alertaImg = new Image();;
	private Label alertaTexto = new Label();
	
	
	public Image getAlertaImg() {
		return alertaImg;
	}

	public void setAlertaImg(Image alertaImg) {
		this.alertaImg = alertaImg;
	}

	public Label getAlertaTexto() {
		return alertaTexto;
	}

	public void setAlertaTexto(Label alertaTexto) {
		this.alertaTexto = alertaTexto;
	}
	
	
	///

	private static final String ALTO_BOTONES = "20px";
	private static final String STYLE_CAMPOS_DESACTIVADOS = "tablaCamposDesactivados";
	private static final String STYLE_FILA_DESACTIVADOS = "tablaDescriptoraData";
	private static final String IMG_BORRAR = "borrar.gif";
	private static final String PREFIJO_NEW_ROW = "row_";
	private static final int ANCHO_BOTON_AGREGAR = 50;
	private static final String ALERTA_3		= "¿Desea Eliminar la Apertura?";
	public TablaInfoComplementaria(String titulo, ArrayList headers, boolean paginable, boolean permiteEliminar, boolean permiteAgregar) {
		this.titulo = titulo;
		this.headers = new ArrayList(headers);
		this.esPaginable = paginable;
		this.permiteAgregar = permiteAgregar;
		this.permiteEliminar = permiteEliminar;
		this.filas = new ArrayList();
		this.inputsDireccionales = new ArrayList();

		pintar();
	}

	/**
	 * Permite agregar un nuevo registro a la tabla.
	 * 
	 * @param fila
	 */
	
	private boolean hasAperturaAjustesConsol() {
		int lastRow = tabla.getDataGrid().getRowCount();
		final String AJUSTE_CONSOLIDACION = "AJUSTES DE CONSOLIDACI\u00D3N";
		if (lastRow > 0) {
			Object widget = tabla.getDataGrid().getWidget(lastRow-1, 0);
			String aperturaGlosa = "";
			if (widget instanceof TextBox){
				aperturaGlosa = ((TextBox) tabla.getDataGrid().getWidget(lastRow-1, 0)).getText();
			}else if (widget instanceof Label){
				aperturaGlosa = ((Label) tabla.getDataGrid().getWidget(lastRow-1, 0)).getText();
			}

			if (AJUSTE_CONSOLIDACION.equals(aperturaGlosa)) {
				return true;
			}
		}
		return false;
	}
	public void agregar(FilaInfoComplementaria fila) {
		int lastRow = tabla.getDataGrid().getRowCount();
		boolean hasApertura = false;
		if (hasAperturaAjustesConsol()) {
			hasApertura = true;
			lastRow--;
			tabla.getDataGrid().insertRow(lastRow);
		}
		List columnas = fila.getColumnas();

		// Es una nueva fila, se le asigna un id
		if (fila.getId() == null || fila.getId().equals("NA")) {
			fila.id = PREFIJO_NEW_ROW + new Integer(lastRow).toString();
		}

		for (int i = 0; i < columnas.size(); i++) {
			ColumnaInfoComplementaria columna = (ColumnaInfoComplementaria) columnas.get(i);
			Widget w = columna.getWidget();

			w.setWidth("100%");

			tabla.getDataGrid().setWidget(lastRow, i, w);

			if (fila.getEsTitulo() || (!fila.getEsTitulo() && !columna.getEsEditable())) {
				tabla.getDataGrid().getRowFormatter().setStyleName(lastRow, STYLE_FILA_DESACTIVADOS);
				tabla.getDataGrid().getCellFormatter().setStyleName(lastRow, i, STYLE_CAMPOS_DESACTIVADOS);

				if (w instanceof TextBoxBase) {
					((TextBox) w).setReadOnly(true);
				}
			}
			
			// Si es la ultima columna con datos
			if (i == (columnas.size() - 1) && (w instanceof InputNumeroSefe)) {
				InputNumeroSefe in = (InputNumeroSefe) w;
				inputsDireccionales.add(in);
				
				CalculadoraV2 calc = new CalculadoraV2();
				calc.addDirectionalMovementListener(new CalculadoraDirectionalMovementHandler(inputsDireccionales));
				calc.registrarInput(in);
			}
		}

		// Si la tabla permite eliminar registros y la fila es eliminable
		if (permiteEliminar && fila.getSePuedeEliminar()) {
			tabla.getDataGrid().setWidget(lastRow, headers.size() - 1, new ImageBorrar(IMG_BORRAR, TablaInfoComplementaria.this, fila));
		} else if (permiteEliminar) {
			// La tabla permite eliminar registros, pero este registro en particular no es eliminable
			tabla.getDataGrid().setWidget(lastRow, headers.size() - 1, new HTML("&nbsp;"));
		}
		if (hasApertura){
			FilaInfoComplementaria filaInfo = null;
			filaInfo = (FilaInfoComplementaria) filas.get(lastRow);
			filas.add(filaInfo);
			filas.set(lastRow,fila);
		}else{
			filas.add(fila);
		}

	}

	/**
	 * Elimina una fila de la tabla a partir del identificador de la fila.
	 * 
	 * @param id
	 */
	public void eliminar(String id) {
		if (Window.confirm(ALERTA_3)) {
			int rows = tabla.getDataGrid().getRowCount();
	
			for (int i = 0; i < rows; i++) {
				FilaInfoComplementaria fila = (FilaInfoComplementaria) filas.get(i);
	
				if (fila.getId().equalsIgnoreCase(id)) {
					tabla.getDataGrid().removeRow(i);
					filas.remove(i);
					eliminarInputDireccionable(getWidgetUltimaColumna(fila));
					break;
				}
			}
		}
	}
	
	private Widget getWidgetUltimaColumna(FilaInfoComplementaria fila) {
		if (fila.getColumnas() != null) {
			ColumnaInfoComplementaria c = (ColumnaInfoComplementaria) fila.getColumna(fila.getColumnas().size() - 1);
			return c.getWidget();
		}
		return null;
	}
	
	private void eliminarInputDireccionable(Widget w) {
		if (w != null && (w instanceof InputNumeroSefe) && inputsDireccionales != null && inputsDireccionales.contains(w)) {
			inputsDireccionales.remove(w);
		}
	}

	private void pintar() {
		tabla = getTabla();
		add(tabla);

		if (permiteAgregar) {
			vp = new VerticalPanel();
			vp.setVerticalAlignment(HasAlignment.ALIGN_TOP);
			btnAgregar = getBtnAgregar();
			btnAgregar.setWidth(ANCHO_BOTON_AGREGAR + "px");
			alertaTexto.setWidth("200px");
			alertaTexto.setStyleName("bold");
			vp.add(btnAgregar);
			this.add(vp);
		}
	}

	private Button getBtnAgregar() {
		Button btn = new Button("Agregar");
		btn.setHeight(ALTO_BOTONES);
		btn.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				if (listener != null) {
					listener.onAgregarRegistroClick(TablaInfoComplementaria.this);
				}
			}
		});
		return btn;
	}

	private TablaDescriptoraFooter getTabla() {
		TablaDescriptoraFooter td = null;

		// Si permite eliminar registros, se agrega la columna
		if (permiteEliminar) {
			headers.add("");
		}

		td = new TablaDescriptoraFooter(titulo, headers, esPaginable);
		td.getTable().setScrollingEnabled(false);
		td.getTable().setAutoFitEnabled(false);
		for (int i = 0; i < headers.size(); i++) {
			td.getHeader().getCellFormatter().setHorizontalAlignment(1, i, HasAlignment.ALIGN_CENTER);
		}
		td.getHeader().getHTML(0, 0);
		return td;
	}

	public static class FilaInfoComplementaria {
		private String id;
		private List columnas;
		private boolean esTitulo;
		private boolean sePuedeEliminar;

		public FilaInfoComplementaria(String id) {
			this.id = id;
			this.columnas = new ArrayList();
		}

		public void setEsTitulo(boolean esTitulo) {
			this.esTitulo = esTitulo;
		}

		public boolean getEsTitulo() {
			return this.esTitulo;
		}

		public void setSePuedeEliminar(boolean sePuedeEliminar) {
			this.sePuedeEliminar = sePuedeEliminar;
		}

		public boolean getSePuedeEliminar() {
			return this.sePuedeEliminar;
		}

		public String getId() {
			return this.id;
		}

		public void add(ColumnaInfoComplementaria columna) {
			columnas.add(columna);
		}

		public ColumnaInfoComplementaria getColumna(int index) {
			return (ColumnaInfoComplementaria) columnas.get(index);
		}

		public List getColumnas() {
			return this.columnas;
		}
	}

	public static class ColumnaInfoComplementaria {
		private boolean esEditable;
		private Widget widget;

		public void setEsEditable(boolean esEditable) {
			this.esEditable = esEditable;
		}

		public boolean getEsEditable() {
			return this.esEditable;
		}

		public void setWidget(Widget widget) {
			this.widget = widget;
		}

		public Widget getWidget() {
			return this.widget;
		}
	}

	public List getFilas() {
		return this.filas;
	}

	public TablaDescriptoraFooter getTablaDescriptora() {
		return tabla;
	}

	public class ImageBorrar extends Image implements ClickListener {
		private FilaInfoComplementaria fic;
		private TablaInfoComplementaria tic;

		public ImageBorrar(String image, TablaInfoComplementaria tic, FilaInfoComplementaria fic) {
			super(image);
			addClickListener(this);
			this.fic = fic;
			this.tic = tic;
		}

		public void onClick(Widget sender) {
			if (listener != null) {
				listener.onEliminarRegistroClick(tic, fic);
			}
		}
	}

	public void addTablaEditableListener(ITablaEditableListener listener) {
		this.listener = listener;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public ArrayList getHeaders() {
		return this.headers;
	}

	public String getTitulo() {
		return this.titulo;
	}

	public boolean getTieneUnidad() {
		return tieneUnidad;
	}

	public void setTieneUnidad(boolean tieneUnidad) {
		this.tieneUnidad = tieneUnidad;
	}

	public VerticalPanel getVp() {
		return vp;
	}

	public void setVp(VerticalPanel vp) {
		this.vp = vp;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public boolean isTieneUnidadesMedida() {
		return tieneUnidadesMedida;
	}

	public void setTieneUnidadesMedida(boolean tieneUnidadesMedida) {
		this.tieneUnidadesMedida = tieneUnidadesMedida;
	}


}
