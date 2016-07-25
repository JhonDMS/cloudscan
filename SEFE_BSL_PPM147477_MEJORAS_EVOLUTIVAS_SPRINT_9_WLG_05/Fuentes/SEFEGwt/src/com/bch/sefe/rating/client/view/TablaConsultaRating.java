/**
 * 
 */
package com.bch.sefe.rating.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bch.sefe.comun.client.ConstantesClienteSEFE;
import com.bch.sefe.comun.rating.ConstantesRating;
import com.bch.sefe.comun.ui.ReportSubmitter;
import com.bch.sefe.comun.ui.TablaDescriptora;
import com.bch.sefe.comun.utils.FormatUtil;
import com.bch.sefe.comun.utils.GeneradorRequestResponse;
import com.bch.sefe.comun.utils.PropertiesSEFEUtil;
import com.bch.sefe.comun.utils.SEFEAlert;
import com.bch.sefe.comun.utils.SEFEBusyRequestCallback;
import com.bch.sefe.comun.utils.URLBuilder;
import com.bch.sefe.comun.utils.XMLUtil;
import com.bch.sefe.comun.vo.Contexto;
import com.bch.sefe.comun.vo.XMLDataList;
import com.bch.sefe.comun.vo.XMLDataObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.libideas.logging.client.RemoteLogHandler;
import com.google.gwt.libideas.logging.shared.Level;
import com.google.gwt.libideas.logging.shared.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Raúl Astudillo
 * 
 */
public class TablaConsultaRating extends TablaDescriptora {

	final static String KEY_RATING_INDIVIDUAL = "ratingIndidivual";
	final static String KEY_RATING_GRUPAL = "ratingGrupal";
	final static String KEY_RATING_PARCIAL = "ratingParcial";
	final static String IMG_SRC_FICHA = "comentario.gif";
	final static String IMG_DELETE_RTG = "borrar.gif";

	private static final String MENOS = "-";

	private MediadorRating mediador = null;
	private Contexto ctx = null;
	private ArrayList listaRating;
	
	// add category logging
	public static final String MI_CATEGORIA = "com.bch.sefe.rating.client.view.TablaConsultaRating";

	public TablaConsultaRating(ArrayList headers, HashMap grupos, boolean isPaginable, MediadorRating mediador, Contexto ctx) {
		super(" ", headers, isPaginable);

		// Se actualizan los estilos seteados en la clase padre
		getHeader().getRowFormatter().removeStyleName(0, "tablaDescriptoraTitulo");
		getHeader().getRowFormatter().addStyleName(0, "tablaDescriptoraTitulo_2");

		// Se agrega estilo para la columna que despliega los iconos.
		getHeader().getCellFormatter().removeStyleName(0, 13, "tablaDescriptoraColumnaDescriptora_3");
		getHeader().getCellFormatter().addStyleName(0, 13, "tablaDescriptoraColumnaDescriptora_3");

		scrolltable.setStyleName("tablaConsultaRating");

		setMediador(mediador);
		inicializarTabla();
		if (!isPaginable) {
			this.setScrollTableSize("auto", "245px");
		}
		this.setMediador(mediador);
		this.setCtx(ctx);
		Log.addLogHandler(new RemoteLogHandler());
	}

	private void inicializarTabla() {
		this.setColumnWidth(0, 110);
		this.setColumnWidth(1, 70);
		this.setColumnWidth(2, 50);
		this.setColumnWidth(3, 60);
		this.setColumnWidth(4, 70);
		this.setColumnWidth(5, 70);
		this.setColumnWidth(6, 50);
		this.setColumnWidth(7, 60);
		this.setColumnWidth(8, 80);
		this.setColumnWidth(9, 80);
		this.setColumnWidth(10, 80);
		this.setColumnWidth(11, 80);
		this.setColumnWidth(12, 80);
		this.setColumnWidth(13, 100);
		this.setColumnWidth(14, 80);
		this.setColumnWidth(15, 30);
		this.setColumnWidth(16, 15);

		this.redrawResize();

		this.getHeader().getRowFormatter().addStyleName(1, "alignCenter");
		this.getHeader().getCellFormatter().addStyleName(1, 0, "alignLeft");

	}

	/**
	 * Se sobreescribe metodo
	 * {@link TablaDescriptora#setColumnaDescriptora(int, String, boolean)}.<br>
	 * Para {@link TablaConsultaRating} el parametro destacado no es utilizado
	 * por lo que siempre es false. Se cambian los estilos utilizados para las
	 * columnas y el nombre de la columna es agregado dentro de un elemento span
	 * con la clase h1_tdcd2.
	 */
	public void setColumnaDescriptora(int columna, String texto, boolean destacado) {
		getHeader().setText(0, columna, texto);
		getHeader().setHTML(0, columna, "<span class=\"h1_tdcd2\">" + texto + "</span>");

		if (columna >= getHeader().getColumnCount())
			throw new IndexOutOfBoundsException("Numero de columna inválido: " + columna);

		if (columna < 0)
			throw new IndexOutOfBoundsException("Numero de columna no puede ser menor que 0: " + columna);

		getHeader().getCellFormatter().addStyleName(0, columna, "tablaDescriptoraColumnaDescriptora_2");
	}

	public void llenarListaRating(XMLDataObject xmlDataObject) {
		this.listaRating = new ArrayList();
		XMLDataList xmlDataList = (XMLDataList) xmlDataObject.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyListaRegistros());

		if (xmlDataList != null) {
			for (int i = 0; i < xmlDataList.size(); i++) {
				this.listaRating.add(xmlDataList.get(i));
			}
		}

		llenarListaRating(this.listaRating);

		mediador.ponerEnContexto(ConstantesRating.CTX_ID_RATING_PROYECTADO, null);
		mediador.ponerEnContexto(ConstantesRating.CTX_ES_RELACIONADO, null);
	}

	private void llenarListaRating(List listaRating) {
		for (int i = 0; i < listaRating.size(); i++) {
			XMLDataObject rating = (XMLDataObject) listaRating.get(i);

			XMLDataObject ratingIndividual = (XMLDataObject) rating.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyDataRatInd());
			XMLDataObject ratingGrupal = (XMLDataObject) rating.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyDataRatGrup());
			XMLDataObject ratingsParciales = (XMLDataObject) rating.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyDataRatParcial());

			Long idRatingInd = ratingIndividual.getLong(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdRatingIndividual());
			Integer idEstadoRatingInd = ratingIndividual.getInteger(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdEstadoInd());
			Integer idBanca = ratingIndividual.getInteger(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdTipoRtg());
			Double notaSugRtgInd = ratingIndividual.getDouble(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyRatingIndSugerido());
			Double notaRtgInd = ratingIndividual.getDouble(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeynotaInd());

			boolean existeRatingSug = (notaSugRtgInd != null);
			boolean existeRating = (notaRtgInd != null);

			// Se configura el estilo de la tabla
			this.getDataGrid().getRowFormatter().setStyleName(i, "tablaDescriptoraData");

			// Se pinta la informacion del rating individual para el registro i
			// de la tabla
			pintarInfoRtgIndividual(i, ratingIndividual, existeRating, existeRatingSug);

			// Se pinta la informacion del rating individual para el registro i
			// de la tabla
			pintarInfoRtgGrupal(i, ratingGrupal, idBanca, idRatingInd);

			// Se pinta la informacion de los ratings parciales que componen el
			// rating individual para el registro i de la tabla
			pintarInfoRtgParciales(i, ratingsParciales, idBanca, idRatingInd, idEstadoRatingInd);

			// Se agrega el icono para el despliegue de la Ficha
			ImagenFicha imagenFicha = crearImagenFicha(idBanca, idRatingInd, ratingGrupal);
			getDataGrid().setWidget(i, 15, imagenFicha);
			
			// Se agrega el icono para el borrado de rating en curso
			if (idRatingInd != null) {
				Integer idEstadoInd = ratingIndividual.getInteger(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdEstadoInd());
				Integer idEstadoGrupal = ratingGrupal.getInteger(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdEstadoGrup());
				String esRelacionadoStr = ratingGrupal.getString(ConstantesRating.CTX_ES_RELACIONADO);
				Boolean esRelacionado = null;
				if (esRelacionadoStr != null) {
					esRelacionado = new Boolean(esRelacionadoStr);
				}
				ImageDelete imagenPapelera = new ImageDelete(idRatingInd, (ConstantesClienteSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO.equals(idEstadoInd) || ConstantesClienteSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO.equals(idEstadoGrupal)),(esRelacionado != null && esRelacionado.booleanValue()), i);
				getDataGrid().setWidget(i, 16, imagenPapelera);
			}

			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 1, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 2, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 3, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 5, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 6, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 7, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 8, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 9, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 10, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 11, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 12, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 13, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 14, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 15, HasAlignment.ALIGN_CENTER);
			this.getDataGrid().getCellFormatter().setHorizontalAlignment(i, 16, HasAlignment.ALIGN_CENTER);
		}
	}

	private void pintarInfoRtgIndividual(int fila, XMLDataObject infoRtgIndividual, boolean existeRatingInd, boolean existeRatingIndSugerido) {
		Integer idTipoBanca = infoRtgIndividual.getInteger(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdTipoRtg());
		String tipoBanca = (String) infoRtgIndividual.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyTipoRtg());
		String fechaInd = (String) infoRtgIndividual.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyFchInd());
		String notaInd = (String) infoRtgIndividual.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeynotaInd());
		String estadoInd = (String) infoRtgIndividual.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyEstadoInd());
		String responsableRtgInd = (String) infoRtgIndividual.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyResponsable());
		Integer idEstadoInd = infoRtgIndividual.getInteger(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdEstadoInd());
		Long idRatInd = infoRtgIndividual.getLong(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdRatingIndividual());
		// Se agrega el Link o texto segun permiso y estado del rating
		Widget label = obtenerLabelLink(obtenerNotaFormateada(notaInd), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_INDIVIDUAL_VIEW, idTipoBanca).booleanValue(), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_INDIVIDUAL_ACCESO, idTipoBanca).booleanValue());
		if (label instanceof LabelLink) {
			((LabelLink) label).addClickListener(new LinkHandlerRatingIndividual(PropertiesSEFEUtil.getKeyRating().TabRatingIndividual(), idTipoBanca, idRatInd, idRatInd, idEstadoInd, existeRatingInd, existeRatingIndSugerido));
		}
		getDataGrid().setWidget(fila, 2, label);
		getDataGrid().getCellFormatter().setHorizontalAlignment(fila, 2, HasAlignment.ALIGN_CENTER);

		getDataGrid().setText(fila, 0, tipoBanca);
		getDataGrid().setText(fila, 1, fechaInd);
		getDataGrid().setText(fila, 3, estadoInd);
		getDataGrid().setText(fila, 4, responsableRtgInd);
	}

	private void pintarInfoRtgGrupal(int fila, XMLDataObject infoRtgGrupal, Integer idTipoBanca, Long idRatingInd) {
		String fechaGrupal = (String) infoRtgGrupal.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyFechaGrup());
		String notaGrupal = (String) infoRtgGrupal.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeynotaGrup());
		String notaManual = (String) infoRtgGrupal.getObject(ConstantesRating.RATING_MANUAL);
		String estadoGrupal = (String) infoRtgGrupal.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyEstadoGrup());
		Integer idEstadoGrupal = infoRtgGrupal.getInteger(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdEstadoGrup());
		String responsable = (String) infoRtgGrupal.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyResponsable());
		Long idRatGrupal = infoRtgGrupal.getLong(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdRatingGrup().toString());
		String rutCliRtgGrp = infoRtgGrupal.getString(ConstantesRating.RUT_CLIENTE);
		Integer idBancaGrp = infoRtgGrupal.getInteger(ConstantesRating.ID_BANCA);
		Long idVersion = infoRtgGrupal.getLong(ConstantesRating.ID_VERSION);
		Long idRtgIndGrupo = infoRtgGrupal.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		String visualizarPrev = infoRtgGrupal.getString(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyVisualizar());
		String accederPrev = infoRtgGrupal.getString(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyAcceder());
		Boolean bancaNoAplica = null;
		String bancaNoAplicaStr = infoRtgGrupal.getString(ConstantesRating.BANCA_NO_HABILITADA);
		if (bancaNoAplicaStr != null) {
			bancaNoAplica = new Boolean(bancaNoAplicaStr);
		}
		
		Boolean acceder = null;
		Boolean visualizar = null;
		// si accederPrev es nulo entonces tiene asociado un rating individual
		if (accederPrev != null) {
			visualizar = new Boolean(visualizarPrev);
			acceder = new Boolean(accederPrev);
		} else { // el grupo tiene un rating individual
			visualizar = Boolean.TRUE;
			acceder = obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_INDIVIDUAL_ACCESO, idTipoBanca);
			if (acceder == null) {
				acceder = Boolean.FALSE;
			}
		}
		String esRelacionadoStr = infoRtgGrupal.getString(ConstantesRating.CTX_ES_RELACIONADO);
		Boolean esRelacionado = null;
		if (esRelacionadoStr != null) {
			esRelacionado = new Boolean(esRelacionadoStr);
		}

		Widget label = obtenerLabelLink(obtenerNotaFormateada(notaManual), visualizar.booleanValue(), acceder.booleanValue(), idEstadoGrupal);
		if (label instanceof LabelLink) {
			Double ratingGrupal = (notaGrupal == null ? null : new Double(notaGrupal));
			Double ratingManual = (notaManual == null ? null : new Double(notaManual));

			((LabelLink) label).addClickListener(new LinkHandlerRatingGrupal(PropertiesSEFEUtil.getKeyRating().TabRatingGrupal(), idBancaGrp, idRtgIndGrupo, idRatGrupal, fechaGrupal, idVersion, rutCliRtgGrp, null, idEstadoGrupal, ratingGrupal, ratingManual, esRelacionado, bancaNoAplica));
		}
		getDataGrid().setWidget(fila, 6, label);
		getDataGrid().getCellFormatter().setHorizontalAlignment(fila, 6, HasAlignment.ALIGN_CENTER);

		getDataGrid().setText(fila, 5, fechaGrupal);
		getDataGrid().setText(fila, 7, estadoGrupal);
		getDataGrid().setText(fila, 8, responsable);
	}

	private void pintarInfoRtgParciales(int fila, XMLDataObject infoRtgParciales, Integer tipoBanca, Long idRatingInd, Integer idEstadoRtgInd) {
		String fechaVaciado = (String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyFchVaciado());
		String notaRtgFinan = (String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyNotaFin());
		Long idRtgFinan = infoRtgParciales.getLong(PropertiesSEFEUtil.getKeyRating().ConsultaRatingkeyIdRatingFin());
		String notaRtgProy = (String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyNotaProy());
		Long idRtgNegocio = infoRtgParciales.getLong(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyidRatingNeg());
		String notaRtgNeg = (String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyNotaNeg());
		Long idRtgComport = infoRtgParciales.getLong(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyidRtgComp());
		String notaRtgComport = (String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyNotaComp());
		Long idRtgGarante = infoRtgParciales.getLong(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyidRtgGte());
		String notaRtgGarante = (String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyNotaGte());
		Long idRtgProyectado = infoRtgParciales.getLong(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyidRatingProy());
		Long idVaciado0 = infoRtgParciales.getLong(ConstantesRating.ID_VACIADO);
		Boolean rtgConfirmado = null;
		Widget label = null;

		// Fecha del vaciado utilizado por rating negocio
		getDataGrid().setText(fila, 9, fechaVaciado);

		// Rating financiero
		rtgConfirmado = Boolean.valueOf((String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRtgKeyRtgFinanConfirmado()));
		label = obtenerLabelLink(obtenerNotaFormateada(notaRtgFinan), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_FINANCIERO_VIEW, tipoBanca).booleanValue(), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_FINANCIERO_ACCESO, tipoBanca).booleanValue(), rtgConfirmado.booleanValue());

		if (label instanceof Linkeable && obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_FINANCIERO_ACCESO, tipoBanca).booleanValue()) {
			((SourcesClickEvents) label).addClickListener(new LinkHandlerRatingFinanciero(PropertiesSEFEUtil.getKeyRating().TabRatingFinanciero(), tipoBanca, idRatingInd, idRtgFinan, notaRtgFinan, idVaciado0, idEstadoRtgInd));
		}

		getDataGrid().setWidget(fila, 10, label);

		// Rating proyectado
		rtgConfirmado = Boolean.valueOf((String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRtgKeyRtgProyConfirmado()));
		label = obtenerLabelLink(obtenerNotaFormateada(notaRtgProy), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_PROYECTADO_VIEW, tipoBanca).booleanValue(), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_PROYECTADO_ACCESO, tipoBanca).booleanValue(), rtgConfirmado.booleanValue());

		if (label instanceof Linkeable && (obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_PROYECTADO_ACCESO, tipoBanca).booleanValue()) && !ConstantesRating.BANCA_PYME.equals(tipoBanca)) {
			((SourcesClickEvents) label).addClickListener(new LinkHandlerRatingProyectado(PropertiesSEFEUtil.getKeyRating().TabRatingProyectado(), tipoBanca, idRatingInd, idRtgProyectado, idEstadoRtgInd, rtgConfirmado));
		}

		getDataGrid().setWidget(fila, 11, label);

		// Rating Negocio
		rtgConfirmado = Boolean.valueOf((String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRtgKeyRtgNegConfirmado()));
		label = obtenerLabelLink(obtenerNotaFormateada(notaRtgNeg), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_NEGOCIO_VIEW, tipoBanca).booleanValue(), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_NEGOCIO_ACCESO, tipoBanca).booleanValue(), rtgConfirmado.booleanValue());

		if (label instanceof Linkeable && (obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_NEGOCIO_ACCESO, tipoBanca).booleanValue())) {
			((SourcesClickEvents) label).addClickListener(new LinkHandlerRatingNegocio(PropertiesSEFEUtil.getKeyRating().TabRatingNegocio(), tipoBanca, idRatingInd, idRtgNegocio, notaRtgNeg, idEstadoRtgInd));
		}

		getDataGrid().setWidget(fila, 12, label);

		// Rating Comportamiento
		rtgConfirmado = Boolean.valueOf((String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRtgKeyRtgCompConfirmado()));
		label = obtenerLabelLink(obtenerNotaFormateada(notaRtgComport), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_COMPORTAMIENTO_VIEW, tipoBanca).booleanValue(), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_COMPORTAMIENTO_ACCESO, tipoBanca).booleanValue(), rtgConfirmado.booleanValue());

		if (label instanceof Linkeable && (obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_COMPORTAMIENTO_ACCESO, tipoBanca).booleanValue())) {
			((SourcesClickEvents) label).addClickListener(new LinkHandlerBaseComponentesRating(PropertiesSEFEUtil.getKeyRating().TabRatingComportamiento(), tipoBanca, idRatingInd, idRtgComport, idEstadoRtgInd, true));
		}

		getDataGrid().setWidget(fila, 13, label);

		// Rating Garante
		if (notaRtgGarante != null) {
			rtgConfirmado = Boolean.TRUE;
		} else {
			rtgConfirmado = Boolean.valueOf((String) infoRtgParciales.getObject(PropertiesSEFEUtil.getKeyRating().ConsultaRtgKeyRtgGteConfirmado()));
		}
		label = obtenerLabelLink(obtenerNotaFormateada(notaRtgGarante), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_GARANTE_VIEW, tipoBanca).booleanValue(), obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_GARANTE_ACCESO, tipoBanca).booleanValue(), rtgConfirmado.booleanValue());

		if (label instanceof Linkeable && (obtenerPrivilegioAcceso(ConstantesRating.CTX_PERMISO_RATING_GARANTE_ACCESO, tipoBanca).booleanValue())) {
			((SourcesClickEvents) label).addClickListener(new LinkHandlerBaseComponentesRating(PropertiesSEFEUtil.getKeyRating().TabRatingGarante(), tipoBanca, idRatingInd, idRtgGarante, idEstadoRtgInd, true));
		}

		getDataGrid().setWidget(fila, 14, label);
	}

	/*
	 * Dependiendo del permiso se genera un label que puede ser texto, LabelLink
	 * etc.
	 */
	private Widget obtenerLabelLink(String lblText, boolean visualizar, boolean acceso, boolean confirmado) {
		Widget label = null;

		if ((!visualizar && !acceso && !confirmado) || (visualizar && !acceso && !confirmado)) {
			label = new Label(MENOS);
		}

		if (!visualizar && !acceso && confirmado) {
			label = new Image(ConstantesClienteSEFE.IMG_TICKET_VERDE);
		}

		if ((!visualizar && acceso && !confirmado) || (visualizar && acceso && !confirmado)) {
			label = new LabelLink(MENOS);
		}

		if (!visualizar && acceso && confirmado) {
			label = new ImageLink(ConstantesClienteSEFE.IMG_TICKET_VERDE);
		}

		if (visualizar && acceso && confirmado) {
			label = new LabelLink(lblText);
		}

		if (visualizar && !acceso && confirmado) {
			label = new Label(lblText);
		}

		return label;
	}

	/*
	 * Realiza validacion para el rating individual. Dependiendo de los permisos
	 * se genera el label que corresponda.
	 */
	private Widget obtenerLabelLink(String lblText, boolean visualizar, boolean acceso) {
		Widget label = null;

		// Se crea link con nota. En caso de no existir nota se utiliza el
		// simbolo -
		if (visualizar && acceso) {
			label = new LabelLink((lblText == null ? MENOS : lblText));
		}

		// Se crea label sin link. En caso de no existir nota se utiliza el
		// simbolo -
		if (visualizar && !acceso) {
			label = new Label((lblText == null ? MENOS : lblText));
		}

		// Se crea link con nota. En caso de no existir nota se utiliza el
		// simbolo -
		if (!visualizar && acceso) {
			label = new LabelLink((lblText == null ? MENOS : lblText));
		}

		// Se crea label sin link. En caso de no existir nota se utiliza el
		// simbolo -
		if (!visualizar && !acceso) {
			label = new Label((lblText == null ? MENOS : lblText));
		}

		return label;
	}

	/*
	 * Realiza la validacion para el rating grupal. Dependiendo de los permisos
	 * y el estado del rating grupal se genera el label que corresponda.
	 */
	private Widget obtenerLabelLink(String lblText, boolean visualizar, boolean acceso, Integer idEstadoRatGrupal) {
		Widget label = null;

		// Rating Grupal en Curso, rol puede visualizar y rol tiene acceso
		if (visualizar && acceso && ConstantesRating.CLASIF_ID_RATING_EN_CURSO.equals(idEstadoRatGrupal)) {
			label = new LabelLink((lblText == null ? MENOS : lblText));
		}

		// Rating Grupal en Curso, rol puede visualizar pero no tiene acceso
		if (visualizar && !acceso && ConstantesRating.CLASIF_ID_RATING_EN_CURSO.equals(idEstadoRatGrupal)) {
			label = new Label((lblText == null ? MENOS : lblText));
		}

		// Rating Grupal es vigente o historico y rol puede visualizar pero no
		// tiene acceso
		if (!ConstantesRating.CLASIF_ID_RATING_EN_CURSO.equals(idEstadoRatGrupal) && visualizar && !acceso) {
			label = new Label((lblText == null ? MENOS : lblText));
		}

		// Rating Grupal es vigente o historico y rol puede visualizar pero no
		// tiene acceso
		if (lblText != null && !ConstantesRating.CLASIF_ID_RATING_EN_CURSO.equals(idEstadoRatGrupal) && visualizar && acceso) {
			label = new LabelLink((lblText == null ? MENOS : lblText));
		}

		return label;
	}

	/*
	 * Interfaz que identifica a un Label como linkeable o no(notas de
	 * componentes de rating, rating individual y grupal).
	 */
	private interface Linkeable {
	}

	private class ImageLink extends Image implements Linkeable {
		public ImageLink(String url) {
			super(url);

			setStylePrimaryName(ConstantesClienteSEFE.NOMBRE_ESTILO_CURSOR_POINTER);
		}
	}

	private class LabelLink extends Label implements Linkeable {
		public LabelLink(String label) {
			super(label);
			this.setStyleName(ConstantesClienteSEFE.NOMBRE_ESTILO_CURSOR_POINTER);
		}
	}

	/*
	 * Handler para el manejo de los links (notas) de los componentes de rating,
	 * rating individual y grupal.
	 */
	private abstract class LinkHandler implements ClickListener {
		protected int tabIndex;
		protected Integer idBanca;
		protected Long idRatingIndividual;
		protected Long idComponenteRating;
		protected Integer idEstadoRatInd;

		public LinkHandler(int tabIndex, Integer idBanca, Long idRatingInd, Long idComponenteRating, Integer idEstado) {
			this.tabIndex = tabIndex;
			this.idBanca = idBanca;
			this.idRatingIndividual = idRatingInd;
			this.idComponenteRating = idComponenteRating;
			this.idEstadoRatInd = idEstado;

		}

		// El metodo de la interfaz debe ser implementado en las subclases
		public void onClick(Widget sender) {
			// se pone en el contexto aplicativo
			// el identificador de la plantilla
			if (idBanca != null) {
				ctx.setIdPlantilla(idBanca.toString());
			}
			ejecutar(sender);
		}

		// Se debe implementar el metodo con las operaciones que correspondan.
		// Este metodo es invocado por onClick de LinkHandler.
		public abstract void ejecutar(Widget sender);
	}

	private class LinkHandlerRatingIndividual extends LinkHandler {
		private boolean existeRatingInd;
		private boolean existeRatingIndSugerido;

		public LinkHandlerRatingIndividual(int tabIndex, Integer idBanca, Long idRatingInd, Long idComponenteRating, Integer idEstado, boolean existeRatingInd, boolean existeRatingIndSugerido) {
			super(tabIndex, idBanca, idRatingInd, idComponenteRating, idEstado);

			this.existeRatingInd = existeRatingInd;
			this.existeRatingIndSugerido = existeRatingIndSugerido;
		}

		public void ejecutar(Widget sender) {
			if (idRatingIndividual == null) {
				SEFEAlert.alert(ConstantesClienteSEFE.MSG_NAVEGADOR_NO_EXISTE_RTG_IND);
				return;
			}

			mediador.ponerEnContexto(ConstantesRating.CTX_ID_COMPONENTE_RATING, idComponenteRating);
			mediador.mostrarTabIngresoRating(tabIndex, idBanca, idRatingIndividual, false);
		}
	}

	private class LinkHandlerRatingGrupal extends LinkHandler {
		String fechaEfectiva = null;
		private Integer idEstadoRtgGrp;
		private Double rtgGrupal = null;
		private Double rtgManual = null;
		private Long idVersion = null;
		private String rutRtgGrupal = null;
		private Boolean esRelacionado = null;
		private Boolean bancaNoHabilitada = null;

		public LinkHandlerRatingGrupal(int tabIndex, Integer idBanca, Long idRatingInd, Long idComponenteRating, String fechaEfectiva, Long idVersion, String rutCliRtgGrp, Integer idEstadoRtgInd, Integer idEstadoRtgGrp, Double ratingGrupal, Double ratingManual, Boolean esRelacionado, Boolean bancaNoAplica) {
			super(tabIndex, idBanca, idRatingInd, idComponenteRating, idEstadoRtgInd);

			this.idEstadoRtgGrp = idEstadoRtgGrp;
			this.fechaEfectiva = fechaEfectiva;
			this.rtgGrupal = ratingGrupal;
			this.rtgManual = ratingManual;
			this.idVersion = idVersion;
			this.rutRtgGrupal = rutCliRtgGrp;
			this.esRelacionado = esRelacionado;
			this.bancaNoHabilitada = bancaNoAplica;
		}

		public void ejecutar(Widget sender) {
			mediador.ponerEnContexto(ConstantesRating.CTX_ID_GRUPO, idComponenteRating);
			mediador.ponerEnContexto(ConstantesRating.CTX_FECHA_EFECTIVA_RTG_GRUPO, fechaEfectiva);
			mediador.ponerEnContexto(ConstantesRating.CTX_ID_ESTADO_RATING_GRUPAL, this.idEstadoRtgGrp);
			mediador.ponerEnContexto(ConstantesRating.CTX_RATING_GRUPAL_CALCULADO, this.rtgGrupal);
			mediador.ponerEnContexto(ConstantesRating.CTX_RATING_GRUPAL, this.rtgManual);
			mediador.ponerEnContexto(ConstantesRating.CTX_ID_VERSION_RATING_GRUPAL, this.idVersion);
			mediador.ponerEnContexto(ConstantesRating.CTX_RUT_CLI_RTG_GRUPAL_PYME, this.rutRtgGrupal);
			mediador.ponerEnContexto(ConstantesRating.CTX_ID_RATING_INDIVIDUAL_RTG_GRP, idRatingIndividual);
			mediador.ponerEnContexto(ConstantesRating.CTX_ES_RELACIONADO, esRelacionado);
			mediador.ponerEnContexto(ConstantesRating.CTX_BANCA_NO_HABILITADA, bancaNoHabilitada);
			mediador.mostrarTabIngresoRating(tabIndex, idBanca, null, true);
		}
	}

	private class LinkHandlerBaseComponentesRating extends LinkHandler {
		private boolean forzarConsulta;
		protected String notaComponenteRtg;

		public LinkHandlerBaseComponentesRating(int tabIndex, Integer idBanca, Long idRatingInd, Long idComponenteRating, String notaComponenteRtg, Integer idEstado) {
			super(tabIndex, idBanca, idRatingInd, idComponenteRating, idEstado);
			this.forzarConsulta = false;
			this.notaComponenteRtg = notaComponenteRtg;
		}

		public LinkHandlerBaseComponentesRating(int tabIndex, Integer idBanca, Long idRatingInd, Long idComponenteRating, Integer idEstado, boolean forzarConsulta) {
			super(tabIndex, idBanca, idRatingInd, idComponenteRating, idEstado);
			this.forzarConsulta = forzarConsulta;
		}

		public void onClick(Widget sender) {
			// Para algunos ratings individuales migrados no existe referencia
			// hacia algunos de sus componentes de rating; como por ejemplo
			// rating de negocio; cuando no existe rating de negocio se lanza
			// mensaje y no se intenta generar reporte.
			// Si existe nota del componente pero no el identificador se lanza
			// error
			if (idRatingIndividual == null || (notaComponenteRtg != null && idComponenteRating == null && !this.forzarConsulta)) {
				SEFEAlert.alert(ConstantesClienteSEFE.MSG_NAVEGADOR_NO_EXISTE_INFO_COMP_RTG);
				return;
			}

			// se pone en el contexto aplicativo
			// el identificador de la plantilla
			if (idBanca == null) {
				ctx.setIdPlantilla(null);
			} else {
				ctx.setIdPlantilla(idBanca.toString());
			}

			ejecutar(sender);
		}

		public void ejecutar(Widget sender) {
			// Si no existe id de componente, se lanza el error y se aborta el
			// proceso
			if (idComponenteRating == null && idEstadoRatInd.intValue() != ConstantesRating.CLASIF_ID_RATING_EN_CURSO.intValue()) {
				SEFEAlert.alert(ConstantesClienteSEFE.MSG_NAVEGADOR_NO_EXISTE_INFO_COMP_RTG);
				return;
			}

			mediador.mostrarTabIngresoRating(tabIndex, idBanca, idEstadoRatInd, idRatingIndividual, idComponenteRating, false);
		}
	}

	/**
	 * Handler para manejar el evento on click para el link de nota del rating
	 * de negocio.
	 * 
	 * @author jlmanriq
	 * 
	 */
	private class LinkHandlerRatingNegocio extends LinkHandlerBaseComponentesRating {
		public LinkHandlerRatingNegocio(int tabIndex, Integer idBanca, Long idRatingInd, Long idComponenteRating, String notaComponenteRtg, Integer idEstado) {
			super(tabIndex, idBanca, idRatingInd, idComponenteRating, notaComponenteRtg, idEstado);
		}

		public void ejecutar(Widget sender) {
			// Si el estado del rating individual es en curso se ingresa a
			// modificar el rating de negocio, en caso contrario
			// se levanta el reporte de ratin de negocio.
			if (ConstantesRating.CLASIF_ID_RATING_EN_CURSO.equals(idEstadoRatInd)) {
				mediador.mostrarTabIngresoRating(tabIndex, idBanca, idEstadoRatInd, idRatingIndividual, idComponenteRating, false);
			} else {

				// Si no existe id de componente, se lanza el error y se aborta
				// el proceso
				if (notaComponenteRtg == null) {
					SEFEAlert.alert(ConstantesClienteSEFE.MSG_NAVEGADOR_NO_EXISTE_INFO_COMP_RTG);
					return;
				}

				imprimirReporteRatingNegocio(idComponenteRating, idRatingIndividual);
			}
		}
	}

	private class LinkHandlerRatingFinanciero extends LinkHandlerBaseComponentesRating {
		private Long idVaciado0;

		public LinkHandlerRatingFinanciero(int tabIndex, Integer idBanca, Long idRatingInd, Long idComponenteRating, String notaComponenteRtg, Long idVaciado0, Integer idEstado) {
			super(tabIndex, idBanca, idRatingInd, idComponenteRating, notaComponenteRtg, idEstado);

			this.idVaciado0 = idVaciado0;
		}

		public void ejecutar(Widget sender) {
			// Si el estado del rating individual es en curso se ingresa a
			// modificar el rating de negocio, en caso contrario
			// se levanta el reporte de ratin de negocio.
			if (ConstantesRating.CLASIF_ID_RATING_EN_CURSO.equals(idEstadoRatInd)) {
				mediador.mostrarTabIngresoRating(tabIndex, idBanca, idEstadoRatInd, idRatingIndividual, idComponenteRating, false);
			} else {
				if (idVaciado0 != null) {
					imprimirReporteFinanciero(idVaciado0, idRatingIndividual);
				} else {
					SEFEAlert.alert(ConstantesClienteSEFE.MSG_NAVEGADOR_NO_EXISTE_INFO_COMP_RTG);
				}
			}
		}
	}

	private void imprimirReporteFinanciero(Long idVaciado, Long idRatingInd) {
		if (idVaciado == null) {
			SEFEAlert.alert("No existe vaciado seleccionado para generar el Reporte Financiero");
			return;
		}

		ctx.setOpCatalogoGeneral(false);
		ctx.setOperacion(ConstantesRating.OPER_GENERAR_REPORTE_FINANCIERO);

		XMLDataObject xmlDataObject = new XMLDataObject();
		xmlDataObject.put(ConstantesRating.REP_TIPO_REPORTE, ConstantesRating.REP_TIPO_PDF);
		xmlDataObject.put(ConstantesRating.REP_RUT_CLIENTE, ctx.getRutCliente());
		xmlDataObject.put(ConstantesRating.ID_RATING_INDIVIDUAL, idRatingInd);
		xmlDataObject.put(ConstantesRating.ID_VACIADO, idVaciado);
		xmlDataObject.put(ConstantesRating.LOG_OPERADOR, ctx.getLogOperador());

		String xml = GeneradorRequestResponse.generarRequest(ctx, xmlDataObject);

		ReportSubmitter.submit(xml);
	}

	/*
	 * Invoca la operacion para la impresion del reporte de rating de negocio.
	 */
	private void imprimirReporteRatingNegocio(Long idRatingNegocio, Long idRatingIndividual) {
		// Para algunos ratings individuales migrados no existe referencia hacia
		// algunos de sus componentes de rating; como por ejemplo
		// rating de negocio; cuando no existe rating de negocio se lanza
		// mensaje y no se intenta generar reporte.
		if (idRatingNegocio == null) {
			SEFEAlert.alert(ConstantesClienteSEFE.MSG_NAVEGADOR_NO_EXISTE_INFO_COMP_RTG);
			return;
		}

		ctx.setOpCatalogoGeneral(false);
		ctx.setOperacion(ConstantesRating.OPER_GENERAR_REPORTE_NEGOCIO);

		XMLDataObject xmlDataObject = new XMLDataObject();
		xmlDataObject.put(ConstantesRating.REP_ID_RATING_NEG, idRatingNegocio);
		xmlDataObject.put(ConstantesRating.REP_RUT_CLIENTE, ctx.getRutCliente());
		xmlDataObject.put(ConstantesRating.REP_TIPO_REPORTE, ConstantesRating.REP_TIPO_PDF);
		xmlDataObject.put(ConstantesRating.REP_NOMBRE_REPORTE, ConstantesRating.REPORTE_RATING_NEGOCIO_ID);
		xmlDataObject.put(ConstantesRating.ID_RATING_INDIVIDUAL, idRatingIndividual);

		String xml = GeneradorRequestResponse.generarRequest(ctx, xmlDataObject);

		ReportSubmitter.submit(xml);
	}

	private class LinkHandlerRatingProyectado extends LinkHandler {
		private Integer idEstado;
		private Boolean rtgConfirmado;
		
		public LinkHandlerRatingProyectado(int tabIndex, Integer idBanca, Long idRatingInd, Long idComponenteRating, Integer idEstado, Boolean rtgConfirmado) {
			super(tabIndex, idBanca, idRatingInd, idComponenteRating, idEstado);
			this.idEstado = idEstado;
			this.rtgConfirmado = rtgConfirmado;
		}

		public void ejecutar(Widget sender) {
			// se pone en el contexto aplicativo
			// el identificador de la plantilla
			if (idBanca == null) {
				ctx.setIdPlantilla(null);
			} else {
				ctx.setIdPlantilla(idBanca.toString());
			}

			// Si no existe id de componente, se lanza el error y se aborta el
			// proceso
			if (idComponenteRating == null && !(new Integer(PropertiesSEFEUtil.getKeyRating().TipoEstadoRatingEnCurso()).equals(idEstadoRatInd))) {
				SEFEAlert.alert(ConstantesClienteSEFE.MSG_NAVEGADOR_NO_EXISTE_INFO_COMP_RTG);
				return;
			}
			
			// Si existe id de componente, pero la nota no se muestra y
			// el rating individual es vigente/historico
			// se lanza el error y se aborta el proceso
			if (idComponenteRating != null && !rtgConfirmado.booleanValue() && !(new Integer(PropertiesSEFEUtil.getKeyRating().TipoEstadoRatingEnCurso()).equals(idEstadoRatInd))) {
				SEFEAlert.alert(ConstantesClienteSEFE.MSG_NAVEGADOR_NO_EXISTE_INFO_COMP_RTG);
				return;
			}

			mediador.mostrarTabIngresoRating(tabIndex, idBanca, idEstadoRatInd, idRatingIndividual, idComponenteRating, false);
		}
	}

	private ImagenFicha crearImagenFicha(Integer idTipoRating, Long idRatingIndividual, XMLDataObject ratingGrupal) {

		ImagenFicha imagenFicha = new ImagenFicha(idTipoRating);
		imagenFicha.addClickListener(new ImageHandler(idRatingIndividual, ratingGrupal, idTipoRating));

		return imagenFicha;
	}

	private class ImagenFicha extends Image {
		private Integer idTipoRating = null;

		public ImagenFicha(Integer idTipoRating) {
			setIdTipoRating(idTipoRating);
			setUrl(IMG_SRC_FICHA);
		}

		/**
		 * @param idTipoRating
		 *            el idTipoRating a establecer
		 */
		public void setIdTipoRating(Integer idTipoRating) {
			this.idTipoRating = idTipoRating;
		}

		/**
		 * @return el idTipoRating
		 */
		public Integer idTipoRating() {
			return idTipoRating;
		}
	}
	
	private class ImageDelete extends Image implements ClickListener {
		private final Long idRating;
		private final boolean canDelete;
		private final boolean isAllied;
		private final int row;
		
		public ImageDelete(Long idRating, boolean canDelete, boolean isAllied, int row) {
			super(IMG_DELETE_RTG);
			this.idRating = idRating;
			this.canDelete = canDelete;
			this.isAllied = isAllied;
			this.row = row;
			this.addClickListener(this);
		}

		/**
		 * @return the idRating
		 */
		public Long getIdRating() {
			return idRating;
		}

		/**
		 * @return the canDelete
		 */
		public boolean isCanDelete() {
			return canDelete;
		}
		
		//sprint 2 req 7.1.4 alinear y borrar rating en curso
		private void borrarRating() {
			XMLDataObject parametros = new XMLDataObject();
			parametros.put(ConstantesClienteSEFE.ID_RATING, idRating);
			getCtx().setOpCatalogoGeneral(false);
			getCtx().setOperacion("300233");
			RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.POST, URLBuilder.getServerUrl());
			reqBuilder.setHeader("Content-Type", "text/xml");
			String xmlToSend = GeneradorRequestResponse.generarRequest(getCtx(), parametros);
			try {
				reqBuilder.sendRequest(xmlToSend, new SEFEBusyRequestCallback() {
					public void onSEFEBusyError(Request request, Throwable exception) {
						SEFEAlert.alert(exception.getMessage());
					}				
					public void onSEFEBusyResponseReceived(Request request, Response response) {
						//implementar
						String xmlResponse = response.getText();
						XMLDataObject xmlObjResponse = (XMLDataObject) GeneradorRequestResponse.generarResponse(xmlResponse).get(GeneradorRequestResponse.KEY_CONTENIDO);
						if (XMLUtil.getCodeFromResponse(xmlResponse).equalsIgnoreCase("0")) {
							Boolean flagRespuesta = new Boolean(xmlObjResponse.getString(ConstantesClienteSEFE.FLAG_BORRADO_RATING_CURSO));
							if (flagRespuesta != null && flagRespuesta.booleanValue()) {
								String fechaGrupal = TablaConsultaRating.this.getDataGrid().getText(row, 5);
								String estadoRatingInd = TablaConsultaRating.this.getDataGrid().getText(row, 3);
								if (fechaGrupal != null && fechaGrupal.indexOf('/') != -1 && TablaConsultaRating.this.listaRating.size() > row+1 && !estadoRatingInd.equals(TablaConsultaRating.this.getDataGrid().getText(row+1, 3))
										|| fechaGrupal != null && fechaGrupal.indexOf('/') != -1 && TablaConsultaRating.this.listaRating.size() == 1) {
									TablaConsultaRating.this.getDataGrid().setText(row, 5, "");
									TablaConsultaRating.this.getDataGrid().setText(row, 6, "");
									TablaConsultaRating.this.getDataGrid().setText(row, 7, "");
									TablaConsultaRating.this.getDataGrid().setText(row, 8, "");
								} else {
									TablaConsultaRating.this.listaRating.remove(row);
									TablaConsultaRating.this.clearTable();
									TablaConsultaRating.this.llenarListaRating(TablaConsultaRating.this.listaRating);
								}
								SEFEAlert.info(ConstantesClienteSEFE.MSG_RESPUESTA_RTG_CURSO_BORRAR);
							} else {
								SEFEAlert.alert(ConstantesClienteSEFE.MSG_RESPUESTA_RTG_CURSO_BORRAR_ERROR);
							}
						} else {
							SEFEAlert.alert(XMLUtil.getMessageFromResponse(xmlResponse));
						}
					}																													
				});
			} catch (RequestException e) {
				//e.printStackTrace();
				SEFEAlert.alert(e.getMessage());
				Log.log(e.getMessage(), Level.SEVERE, MI_CATEGORIA, e);
			} finally {
				getCtx().setOpCatalogoGeneral(false);
			}
		}

		public void onClick(Widget sender) {
			// TODO Auto-generated method stub
			if (this.canDelete) {
				if (this.isAllied) {
					SEFEAlert.alert(ConstantesClienteSEFE.MSG_RTG_CURSO_RELACIONADO_NO_BORRAR);
				} else if (Window.confirm(ConstantesClienteSEFE.MSG_CONFIRMAR_RTG_CURSO_BORRAR)) {
					borrarRating();
				}
			} else {
				SEFEAlert.alert(ConstantesClienteSEFE.MSG_NO_EXISTE_RTG_CURSO_BORRAR);
			}
		}
	}

	private class ImageHandler implements ClickListener {
		private Long idRatingIndividual = null;
		private XMLDataObject ratingGrupal = null;
		private Integer idTipoRating = null;

		public ImageHandler(Long idRatingIndividual, XMLDataObject ratingGrupal, Integer idTipoRating) {
			this.idRatingIndividual = idRatingIndividual;
			this.ratingGrupal = ratingGrupal;
			this.idTipoRating = idTipoRating;
		}

		public void onClick(Widget sender) {
			generarRptFichaRating();
		}

		private void generarRptFichaRating() {
			XMLDataObject parametros = new XMLDataObject();
			parametros.put("nombreRpt", "rptFichaRating");
			parametros.put("tipo", "0");
			parametros.put("rut", getCtx().getRutCliente());
			parametros.put("idRatIndividual", this.idRatingIndividual);
			parametros.put(ConstantesRating.ID_VERSION, this.ratingGrupal.getLong(ConstantesRating.ID_VERSION));
			parametros.put(ConstantesRating.ID_GRUPO, this.ratingGrupal.getLong(PropertiesSEFEUtil.getKeyRating().ConsultaRatingKeyIdRatingGrup().toString()));
			// parametros.put("tipoRating", getIdTipoRating());

			getCtx().setOpCatalogoGeneral(false);

			getCtx().setOperacion("300030");

			if (this.idTipoRating != null) {
				getCtx().setIdPlantilla(this.idTipoRating.toString());
			}

			String requestXML = GeneradorRequestResponse.generarRequest(ctx, parametros);

			ReportSubmitter.submit(requestXML);

		}

		/**
		 * @param idRatingIndividual
		 *            el idRatingIndividual a establecer
		 */
		public void setIdRatingIndividual(Long idRatingIndividual) {
			this.idRatingIndividual = idRatingIndividual;
		}

		/**
		 * @return el idRatingIndividual
		 */
		public Long idRatingIndividual() {
			return idRatingIndividual;
		}

		/**
		 * @param idTipoRating
		 *            el idTipoRating a establecer
		 */
		public void setIdTipoRating(Integer idTipoRating) {
			this.idTipoRating = idTipoRating;
		}

		/**
		 * @return el idTipoRating
		 */
		public Integer getIdTipoRating() {
			return idTipoRating;
		}
		
		/**
		 * @return el ratingGrupal
		 */
		public XMLDataObject getRatingGrupal() {
			return ratingGrupal;
		}

		/**
		 * @param ratingGrupal el ratingGrupal a establecer
		 */
		public void setRatingGrupal(XMLDataObject ratingGrupal) {
			this.ratingGrupal = ratingGrupal;
		}
	}

	/**
	 * @param mediador
	 *            el mediador a establecer
	 */
	public void setMediador(MediadorRating mediador) {
		this.mediador = mediador;
	}

	/**
	 * @return el mediador
	 */
	public MediadorRating getMediador() {
		return mediador;
	}

	public Contexto getCtx() {
		return ctx;
	}

	public void setCtx(Contexto ctx) {
		this.ctx = ctx;
	}

	private String obtenerNotaFormateada(String strNota) {
		String notaFormateada = null;

		try {
			notaFormateada = FormatUtil.formatNota(new Double(strNota));
		} catch (Exception e) {
			// Se retorna null
			Log.log(e.getMessage(), Level.SEVERE, MI_CATEGORIA, e);
		}

		return notaFormateada;
	}

	private Boolean obtenerPrivilegioAcceso(String keyCtx, Integer tipoBanca) {
		if (tipoBanca == null) {
			return Boolean.FALSE;
		}
		return (Boolean) mediador.buscarEnContexto(keyCtx + ConstantesRating.UNDERLINE + tipoBanca.toString());
	}

}
