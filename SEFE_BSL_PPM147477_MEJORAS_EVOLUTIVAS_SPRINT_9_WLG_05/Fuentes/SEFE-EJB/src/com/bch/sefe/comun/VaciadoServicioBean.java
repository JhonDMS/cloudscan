package com.bch.sefe.comun;

import java.util.ArrayList;
import java.util.Collection;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.impl.CatalogoReportesImpl;
import com.bch.sefe.comun.impl.ServicioCalculoImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.rpt.impl.AdaptadorReportes;
import com.bch.sefe.comun.util.XMLData;
import com.bch.sefe.comun.util.XMLDataObject;
import com.bch.sefe.comun.util.XMLObjectFactory;
import com.bch.sefe.comun.vo.Archivo;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.CriterioBusqueda;
import com.bch.sefe.comun.vo.CriterioBusquedaDAI;
import com.bch.sefe.comun.vo.CriterioBusquedaEmpresas;
import com.bch.sefe.comun.vo.Parametro;
import com.bch.sefe.servicios.Contexto;
import com.bch.sefe.servicios.XMLObject;
import com.bch.sefe.vaciados.AdaptadorInfoComplementaria;
import com.bch.sefe.vaciados.AdaptadorVaciados;
import com.bch.sefe.vaciados.CatalogoVaciados;
import com.bch.sefe.vaciados.ServicioVaciados;
import com.bch.sefe.vaciados.impl.CatalogoVaciadosImpl;
import com.bch.sefe.vaciados.impl.ServicioVaciadosImpl;
import com.bch.sefe.vaciados.srv.ServicioAlertas;
import com.bch.sefe.vaciados.srv.impl.ServicioAlertasImpl;
import com.bch.sefe.vaciados.vo.Comentario;
import com.bch.sefe.vaciados.vo.Vaciado;
import com.bch.sefe.vaciados.vo.VaciadoCargaMasiva;

/**
 * Bean implementation class for Enterprise Bean: VaciadoServicio
 */
public class VaciadoServicioBean extends ServicioBase implements javax.ejb.SessionBean {

	static final long serialVersionUID = 3206093459760846163L;
	private javax.ejb.SessionContext mySessionCtx;

	/**
	 * getSessionContext
	 */
	public javax.ejb.SessionContext getSessionContext() {
		return mySessionCtx;
	}

	/**
	 * setSessionContext
	 */
	public void setSessionContext(javax.ejb.SessionContext ctx) {
		mySessionCtx = ctx;
	}

	/**
	 * ejbCreate
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * ejbActivate
	 */
	public void ejbActivate() {
	}

	/**
	 * ejbPassivate
	 */
	public void ejbPassivate() {
	}

	/**
	 * ejbRemove
	 */
	public void ejbRemove() {
	}

	protected Object ejecutarServicio(Contexto ctx, String xmlOperacion) {
		ServicioVaciados servVaciado = new ServicioVaciadosImpl();
		CatalogoVaciados catalogo = new CatalogoVaciadosImpl();
		ServicioClientes servCliente = new ServicioClientesImpl();
		CatalogoReportes catalogoReportes = new CatalogoReportesImpl();
		ServicioCalculo servCalc = new ServicioCalculoImpl();
		AdaptadorVaciados adaptadorVaciados = new AdaptadorVaciados();
		
		Object objRespuesta = null;

		// Busqueda de Vaciados.
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_VACIADOS)) {
			XMLObject criterioBusqueda = XMLObjectFactory.getInstance().createFromXml(xmlOperacion);

			objRespuesta = catalogo.buscarVaciados((CriterioBusqueda) criterioBusqueda);
		}

		// Busqueda de Vaciados String
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_VACIADOS_STRING)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject criterioBusqueda = XMLObjectFactory.getInstance().createFromXml(xml);

			objRespuesta = catalogo.buscarVaciadosString((CriterioBusqueda) criterioBusqueda, ctx.getLogOperador(), ctx.getRolOperador());
		}

		// Busqueda de la relacion entre Tipo de Balance y Plan de Cuentas
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_RELACION_TIPO_BALANCE_PLAN_CTAS)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject parametroEntrada = XMLObjectFactory.getInstance().createFromXml(xml);
			Integer tipoBalance = ((Parametro) parametroEntrada).getIntegerValue();
			objRespuesta = catalogo.buscarRelacionTpoBlcePlanCtas(tipoBalance);
		}
		
		// Busqueda de Tipo de plan de cuentas del Vaciado
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_TIPO_PLAN_CTAS_VACIADO)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject parametroEntrada = XMLObjectFactory.getInstance().createFromXml(xml);
			Long idVaciado = ((Parametro) parametroEntrada).getLongValue();
			objRespuesta = catalogo.buscarTpoPlanCtasVaciado(idVaciado);
		}

		// Creacion de vaciados.
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_CREAR_VACIADO)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject vaciado = XMLObjectFactory.getInstance().createFromXml(xml);

			objRespuesta = servVaciado.crearVaciado(ctx.getLogOperador(), ctx.getRutCliente(), (Vaciado) vaciado);
		}

		// Modificacion de los datos generales de un vaciado
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GUARDAR_DATOS_GENERALES)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject vaciado = XMLObjectFactory.getInstance().createFromXml(xml);

			objRespuesta = servVaciado.guardarDatosGenerales(ctx.getLogOperador(), ((Vaciado) vaciado).getIdVaciado(), (Vaciado) vaciado);
		}

		// Obtener datos Generales de Vaciado.
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_DATOS_GENERALES)) {
			objRespuesta = catalogo.buscarDatosGeneral(Long.valueOf(ctx.getIdVaciado()));
		}

		// Crear vaciado DAI
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_CREAR_VACIADO_DAI)) {
			ArrayList datosVaciado = (ArrayList) procesarXmlRequest(xmlOperacion);

			objRespuesta = servVaciado.crearVaciadoDAI(ctx.getRutCliente(), (Vaciado) datosVaciado.get(0), ctx.getLogOperador(), (CriterioBusquedaDAI) datosVaciado.get(1));
		}

		// Obtener los encabezados de Vaciado
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_OBTENER_ENCABEZADO)) {
			objRespuesta = catalogo.buscarEncabezado(Long.valueOf(ctx.getIdVaciado()));
		}

		// Busqueda de cuentas de estado financiero
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_CUENTAS_ESTADOFINANCIERO)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject clasificacion = XMLObjectFactory.getInstance().createFromXml(xml);			
			objRespuesta = servVaciado.buscarCuentasEstadoFinanciero(Long.valueOf(ctx.getIdVaciado()), ((Clasificacion) clasificacion).getIdClasif(), ((Clasificacion) clasificacion).getDescripcion());			
		}
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_DETALLE_CUENTAS_ESTADOFINANCIERO)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			objRespuesta = adaptadorVaciados.buscarDetalleCtasFinancieraAperturables(request);
		}
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_CUENTAS_INFO_COMPLEMENTARIA)) {
//			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
//			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
//			XMLObject clasificacion = XMLObjectFactory.getInstance().createFromXml(xml);
//			objRespuesta = servVaciado.buscarCuentasEstadoFinanciero(Long.valueOf(ctx.getIdVaciado()), new Integer(1072));
		}
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_CUENTAS_INFO_MERCADO)) {
			
		}
		
		// Calculo de las cuentas de un vaciado
		if(ConstantesSEFE.OPER_CALCULAR_VACIADO.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject parametro = XMLObjectFactory.getInstance().createFromXml(xml);
			
			servCalc.calcularCuentasVaciado(((Parametro)parametro).getLongValue());
		}

		// Guardar las cuentas de activos
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GUARDAR_CUENTAS_ACTIVOS)) {
			ArrayList cuentas = (ArrayList) procesarXmlRequest(xmlOperacion);
			Vaciado vaciadoResp = servVaciado.guardarCuentasActivos(Long.valueOf(ctx.getIdVaciado()), ctx.getLogOperador(), cuentas);
			ctx.setIdVaciado(vaciadoResp.getIdVaciado().toString());
			return vaciadoResp;
		}
		
		// Guardar los ajustes a las cuentas de activos
//		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GUARDAR_AJUSTES_ACTIVOS)) {
//			ArrayList cuentas = (ArrayList) procesarXmlRequest(xmlOperacion);
//			servVaciado.guardarAjustesActivos(Long.valueOf(ctx.getIdVaciado()), cuentas);
//		}

		// Guardar las cuentas de pasivos
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GUARDAR_CUENTAS_PASIVOS)) {
			ArrayList cuentas = (ArrayList) procesarXmlRequest(xmlOperacion);
			Vaciado vaciadoResp = servVaciado.guardarCuentasPasivos(Long.valueOf(ctx.getIdVaciado()), ctx.getLogOperador(), cuentas);
			ctx.setIdVaciado(vaciadoResp.getIdVaciado().toString());
			return vaciadoResp;
		}

		// Guardar las cuentas de eerr
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GUARDAR_CUENTAS_ESTADOSRESULTADOS)) {
			ArrayList cuentas = (ArrayList) procesarXmlRequest(xmlOperacion);
			Vaciado vaciadoResp = servVaciado.guardarCuentasEstadosResultados(Long.valueOf(ctx.getIdVaciado()), ctx.getLogOperador(), cuentas);
			ctx.setIdVaciado(vaciadoResp.getIdVaciado().toString());
			return vaciadoResp;
		}

		// Mostrar nota del vaciado
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_NOTA)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject nota = XMLObjectFactory.getInstance().createFromXml(xml);
			objRespuesta = catalogo.buscarNota((Comentario) nota);
		}

		// Guardar notas de vaciado
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GUARDAR_NOTA_VACIADO)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject nota = XMLObjectFactory.getInstance().createFromXml(xml);
			objRespuesta = servVaciado.guardarNota((Comentario) nota);
		}
		
		// Guardar las cuentas Detalle Corrección Monetaria
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GUARDAR_DETALLE_CORRECCION_MONETARIA)) {
			ArrayList cuentas = (ArrayList) procesarXmlRequest(xmlOperacion);
			Vaciado vaciadoResp = servVaciado.guardarCuentasCorreccionMonetaria(Long.valueOf(ctx.getIdVaciado()), ctx.getLogOperador(), cuentas);
			ctx.setIdVaciado(vaciadoResp.getIdVaciado().toString());
			return vaciadoResp;
		}
		
		// Calcular Reconciliación
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_CALCULAR_RECONCILIACION)) {
			ArrayList cuentas = (ArrayList) procesarXmlRequest(xmlOperacion);
		}

		// Guardar las cuentas de Reconciliación
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GUARDAR_RECONCILIACION)) {
			ArrayList cuentas = (ArrayList) procesarXmlRequest(xmlOperacion);
			Vaciado vaciadoResp = servVaciado.guardarCuentasReconciliacion(Long.valueOf(ctx.getIdVaciado()), ctx.getLogOperador(), cuentas);
			ctx.setIdVaciado(vaciadoResp.getIdVaciado().toString());
			return vaciadoResp;
		}
		
		// Guardar las cuentas Flujo de Caja
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GUARDAR_FLUJO_CAJA)) {
			ArrayList cuentas = (ArrayList) procesarXmlRequest(xmlOperacion);
			Vaciado vaciadoResp = servVaciado.guardarCuentasFlujoCaja(Long.valueOf(ctx.getIdVaciado()),ctx.getLogOperador(), cuentas);
			ctx.setIdVaciado(vaciadoResp.getIdVaciado().toString());
			return vaciadoResp;
		}

		// EVALUAR ALERTAS VACIADO
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_EVALUAR_ALERTAS_VACIADO)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			ServicioAlertas servAlert = new ServicioAlertasImpl();
			ServicioAlertasImpl servAlertInfoComp = new ServicioAlertasImpl();
			Collection listado1 = servAlert.buscarValidacionesCruzadas(((Parametro)param).getLongValue());
			Collection listado2 = servAlert.buscarAlertasIndicadores(((Parametro)param).getLongValue());
			Collection listado3 = servAlert.buscarAlertasCuentasSVS(((Parametro)param).getLongValue());
			Collection listado4 = servAlertInfoComp.buscarAlertasInfoComplementaria(((Parametro)param).getLongValue());
			//(linea anterior) carga de alertas de informacion complementaria
			listado1.addAll(listado2);
			listado1.addAll(listado3);
			listado1.addAll(listado4);
			objRespuesta = listado1;
		}
		// Cambiar Estado Vigente
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_CAMBIAR_ESTADO_VIGENTE)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			ServicioVaciados servVac = new ServicioVaciadosImpl();
			objRespuesta = servVac.cambiarEstadoVigente(((Parametro)param).getLongValue(), ctx);
		}
		
		// Ajustar Vaciado
		if(ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_AJUSTAR_VACIADOS)){
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			objRespuesta = servVaciado.ajustarVaciado(((Parametro)param).getLongValue());
		}
		
		// Buscar encabezado Pasivos Contingentes 
		if(ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_OBTENER_ENCABEZADO_PASIVOS_CONTINGENTES)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			objRespuesta = servCliente.obtenerClientePorRut(((Parametro)param).getStringValue());
		}
		
		// Buscar Pasivos Contingentes
		if(ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_PASIVOS_CONTINGENTES)){
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			objRespuesta = servVaciado.obtenerPasivosContingentesCliente(((Parametro)param).getStringValue());
		}
		
		// Guardar Pasivos Contingentes
		if(ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GUARDAR_PASIVOS_CONTINGENTES)){
			ArrayList pasivosContingentes = (ArrayList) procesarXmlRequest(xmlOperacion);
			servVaciado.guardarPasivosContingentesCliente(ctx.getRutCliente(), pasivosContingentes);
			
		}
		
		// Crear FECU
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_CREAR_FECU)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject vaciado = XMLObjectFactory.getInstance().createFromXml(xml);
			
			objRespuesta = servVaciado.crearVaciadoFECU(ctx.getRutCliente(), ctx.getLogOperador(), (Vaciado) vaciado);
		}
		// Cargar XBRL
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_UPLOAD_XBRL)) {
			XMLDataObject request = new XMLDataObject();
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject archivo = XMLObjectFactory.getInstance().createFromXml(xml);
			request.initFromXml(xml);
			XMLDataObject object = (XMLDataObject)request.getObject("Parametros_Cargar_Datos");
			String xmlVaciado=object.toString();
			xmlVaciado=xmlVaciado.substring(xmlVaciado.indexOf("=")+1 , xmlVaciado.indexOf("}"));
			XMLObject vaciado = XMLObjectFactory.getInstance().createFromXml(xmlVaciado);
			Vaciado vaciadoResp=servVaciado.crearVaciadoXBRL((Vaciado) vaciado,ctx.getRutCliente(),ctx.getLogOperador(),(Archivo) archivo);
			Parametro parametro=new Parametro();
			StringBuilder builder=new StringBuilder();
			builder.append("parametroIdVaciado:").append(vaciadoResp.getIdVaciado()).append(";");
			parametro.setStringValue(builder.toString());
			objRespuesta=parametro;
		}
		// Cargar FECU desde archivo
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_UPLOAD_FECU)) {
			XMLDataObject request = new XMLDataObject();
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject archivo = XMLObjectFactory.getInstance().createFromXml(xml);
			request.initFromXml(xml);
			XMLDataObject object = (XMLDataObject)request.getObject("Parametros_Cargar_Datos");
			String xmlVaciado=object.toString();
			xmlVaciado=xmlVaciado.substring(xmlVaciado.indexOf("=")+1 , xmlVaciado.indexOf("}"));
			XMLObject vaciado = XMLObjectFactory.getInstance().createFromXml(xmlVaciado);
			Vaciado vaciadoResp=servVaciado.crearVaciadoFECUArchivo((Vaciado) vaciado,ctx.getRutCliente(),ctx.getLogOperador(),(Archivo) archivo);
			Parametro parametro=new Parametro();
			StringBuilder builder=new StringBuilder();
			builder.append("parametroIdVaciado:").append(vaciadoResp.getIdVaciado()).append(";");
			parametro.setStringValue(builder.toString());
			objRespuesta=parametro;
		}
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_UPLOAD_BANCOS)) {
			XMLDataObject request = new XMLDataObject();
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject archivo = XMLObjectFactory.getInstance().createFromXml(xml);
			request.initFromXml(xml);
			XMLDataObject object = (XMLDataObject)request.getObject("Parametros_Cargar_Datos");
			String xmlVaciado=object.toString();
			xmlVaciado=xmlVaciado.substring(xmlVaciado.indexOf("=")+1 , xmlVaciado.indexOf("}"));
			XMLObject vaciado = XMLObjectFactory.getInstance().createFromXml(xmlVaciado);
			Vaciado vaciadoResp=servVaciado.crearVaciadoBancoDesdeArchivo((Vaciado) vaciado,ctx.getRutCliente(),ctx.getLogOperador(),(Archivo) archivo);
			Parametro parametro=new Parametro();
			StringBuilder builder=new StringBuilder();
			builder.append("parametroIdVaciado:").append(vaciadoResp.getIdVaciado()).append(";");
			parametro.setStringValue(builder.toString());
			objRespuesta=parametro;
		}
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_UPLOAD_CORREDORAS)) {
			XMLDataObject request = new XMLDataObject();
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject archivo = XMLObjectFactory.getInstance().createFromXml(xml);
			request.initFromXml(xml);
			XMLDataObject object = (XMLDataObject)request.getObject("Parametros_Cargar_Datos");
			String xmlVaciado=object.toString();
			xmlVaciado=xmlVaciado.substring(xmlVaciado.indexOf("=")+1 , xmlVaciado.indexOf("}"));
			XMLObject vaciado = XMLObjectFactory.getInstance().createFromXml(xmlVaciado);
			Vaciado vaciadoResp=servVaciado.crearVaciadoCorredoraDesdeArchivo((Vaciado) vaciado,ctx.getRutCliente(),ctx.getLogOperador(),(Archivo) archivo);
			Parametro parametro=new Parametro();
			StringBuilder builder=new StringBuilder();
			builder.append("parametroIdVaciado:").append(vaciadoResp.getIdVaciado()).append(";");
			parametro.setStringValue(builder.toString());
			objRespuesta=parametro;
		}
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_UPLOAD_SEG_GRAL)) {
			XMLDataObject request = new XMLDataObject();
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject archivo = XMLObjectFactory.getInstance().createFromXml(xml);
			request.initFromXml(xml);
			XMLDataObject object = (XMLDataObject)request.getObject("Parametros_Cargar_Datos");
			String xmlVaciado=object.toString();
			xmlVaciado=xmlVaciado.substring(xmlVaciado.indexOf("=")+1 , xmlVaciado.indexOf("}"));
			XMLObject vaciado = XMLObjectFactory.getInstance().createFromXml(xmlVaciado);
			Vaciado vaciadoResp=servVaciado.crearVaciadoSegurosDesdeArchivo((Vaciado) vaciado,ctx.getRutCliente(),ctx.getLogOperador(),(Archivo) archivo, ConstantesSEFE.CLASIF_ID_TPO_PLAN_IFRS_SEG_GRAL);
			Parametro parametro=new Parametro();
			StringBuilder builder=new StringBuilder();
			builder.append("parametroIdVaciado:").append(vaciadoResp.getIdVaciado()).append(";");
			parametro.setStringValue(builder.toString());
			objRespuesta=parametro;
		}
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_CARGAR_CUENTAS_SVS)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject archivo = XMLObjectFactory.getInstance().createFromXml(xml);
			servVaciado.cargarCuentasSVS(ctx.getRutCliente(),ctx.getLogOperador(),(Archivo) archivo, ctx.getIdVaciado());
		}
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_UPLOAD_SEG_VIDA)) {
			XMLDataObject request = new XMLDataObject();
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject archivo = XMLObjectFactory.getInstance().createFromXml(xml);
			request.initFromXml(xml);
			XMLDataObject object = (XMLDataObject)request.getObject("Parametros_Cargar_Datos");
			String xmlVaciado=object.toString();
			xmlVaciado=xmlVaciado.substring(xmlVaciado.indexOf("=")+1 , xmlVaciado.indexOf("}"));
			XMLObject vaciado = XMLObjectFactory.getInstance().createFromXml(xmlVaciado);
			Vaciado vaciadoResp=servVaciado.crearVaciadoSegurosDesdeArchivo((Vaciado) vaciado,ctx.getRutCliente(),ctx.getLogOperador(),(Archivo) archivo, ConstantesSEFE.CLASIF_ID_TPO_PLAN_IFRS_SEG_VIDA);
			Parametro parametro=new Parametro();
			StringBuilder builder=new StringBuilder();
			builder.append("parametroIdVaciado:").append(vaciadoResp.getIdVaciado()).append(";");
			parametro.setStringValue(builder.toString());
			objRespuesta=parametro;
		}
		// Buscar empresas
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_BUSCAR_EMPRESA)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject criterioBusquedaEmpresas = XMLObjectFactory.getInstance().createFromXml(xml);
			objRespuesta = servVaciado.buscarEmpresa((CriterioBusquedaEmpresas) criterioBusquedaEmpresas);
		}
		
		// Buscar Empresas Malla de relaciones
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_OBTENER_EMPRESAS_MALLA_RELACIONES)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			objRespuesta = servVaciado.buscarEmpresasMallaRel(((Parametro) param).getStringValue(), ((Parametro) param).getBooleanValue());
		}
		
		// Buscar empresas vinculadas a vaciado consolidado/combinado.
		if (ctx.getOperacion().equals(ConstantesSEFE.OPER_OBTENER_EMPRESAS_VINCULADAS_VAC)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			Parametro parametro = null;
			
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			parametro = (Parametro) XMLObjectFactory.getInstance().createFromXml(xml);
			// busca vaciados consolidados de la fase1 sin involucar vaciados
			objRespuesta = servVaciado.buscarVinculacionEmpVaciadosConsolidadosNormal(parametro.getLongValue());
		}
		
		// Buscar empresas vinculadas a vaciado consolidado/combinado.
		if (ctx.getOperacion().equals(ConstantesSEFE.OPER_OBTENER_EMPRESAS_VINCULADAS_CLI)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			Parametro parametro = null;
			
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			parametro = (Parametro) XMLObjectFactory.getInstance().createFromXml(xml);

			objRespuesta = servVaciado.buscarVinculacionEmpCliente(parametro.getStringValue(), parametro.getBooleanValue());
		}
		
		// Agregar Empresas para realizar la Vinculación
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_AGREGAR_EMPRESAS)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject cliente = XMLObjectFactory.getInstance().createFromXml(xml);
			objRespuesta = servCliente.actualizarCliente((Cliente) cliente);
		}
		
		// Actualizar Vinculacion de Empresas
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_ACTUALIZAR_VINC_EMPRESAS_VAC_CONSOLIDADOS)) {
			ArrayList listadoEmpresasAVincular = (ArrayList) procesarXmlRequest(xmlOperacion);
			servVaciado.actualizarVinculacionRelacionCliente(listadoEmpresasAVincular, Long.valueOf(ctx.getIdVaciado()), ctx.getRutCliente());
		}
		
		// Tiene correccion monetaria
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_TIENE_DCM)) {
			
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			
			objRespuesta = servVaciado.tieneCorreccionMonetaria(((Parametro) param).getLongValue());
		}
		
		// Tiene reconciliacion
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_TIENE_RECONCILIACION)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			
			objRespuesta = servVaciado.tieneReconciliacion(((Parametro) param).getLongValue());
		}
		
		// Generar Reporte Vaciado Un Periodo
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_GENERAR_REPORTE_VACIADO_UN_PER)) {
			AdaptadorReportes adaptadorRpt = new AdaptadorReportes();
			
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			
			// TODO Manejar el retorno del método.
			//objRespuesta = catalogoReportes.generarReportes((ParametroReporte) param, ctx);
			objRespuesta = adaptadorRpt.generarReporteVaciadoUnPer(param, ctx);
			
		}
		
		// Verifica los mensajes antes de entrar al flujo de caja
		if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_VALIDA_MSG_FLUJO_CAJA)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			
			objRespuesta = servVaciado.validarMensajeFlujoCaja(((Parametro) param).getLongValue());
		}
		
		// Anular correccion monetaria
		if(ConstantesSEFE.OPER_ANULAR_DETALLE_CORRECCION_MONETARIA.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			
			servVaciado.anularCorreccionMonetaria(((Parametro)param).getLongValue());
		}
		// borrar vaciados
		if(ConstantesSEFE.OPER_BORRAR_VACIADOS.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLObject param = XMLObjectFactory.getInstance().createFromXml(xml);
			
			servVaciado.borrarVaciado(((Parametro)param).getLongValue());
		}if (ctx.getOperacion().equalsIgnoreCase(ConstantesSEFE.OPER_INGRESAR_MOD_DETALLE_CTAS)) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);			
			objRespuesta = adaptadorVaciados.agregarModificarDetalleCtas(request);
			return objRespuesta;
		}if(ConstantesSEFE.OPER_OBTENER_GRUPO_PROPUESTO.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);			
			objRespuesta = adaptadorVaciados.obtenerGrupoPropuesto(request);
			return objRespuesta;
			
		}if(ConstantesSEFE.OPER_CREAR_VACIADO_CONS_COMB.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);			
			objRespuesta = adaptadorVaciados.crearVaciadoConsolCombinado(request, ctx.getLogOperador());
			return objRespuesta;
			
		}
		if(ConstantesSEFE.OPER_VERIFICAR_VACIADO_CONS_COMB.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);			
			return adaptadorVaciados.verificarAlertasParaVaciadoConsolidado(request);
			
		}
		if(ConstantesSEFE.OPER_BUSCAR_EMPRESA_CONS_COMB.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);			
			return objRespuesta = adaptadorVaciados.buscarEmpresasOPersonas(request);			
		}
		//Reporte Exportar a Excel un Vaciado Consolidado/Combinado
		if(ConstantesSEFE.OPER_GENERAR_REPORTE_EXPORT_EXCEL_CONS_COMB.equalsIgnoreCase(ctx.getOperacion())) {
			AdaptadorReportes adaptadorReporte = new AdaptadorReportes();
			XMLDataObject request = new XMLDataObject();
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			request.initFromXml(xml);			
			return objRespuesta = adaptadorReporte.generarReportePlantillaTrabajoConsolidadoCombinado(request, ctx);		
		}
		
		// Buscar hojas independientes
		if(ConstantesSEFE.OPER_BUSCAR_HOJAS_INDEPENDIENTES.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);			
			return objRespuesta = adaptadorVaciados.buscarHojasIndependientes(request);			
		}
		
		//Buscar hoja independiente
		if(ConstantesSEFE.OPER_BUSCAR_HOJA_INDEPENDIENTE.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);			
			return objRespuesta = adaptadorVaciados.consultarHojaIndependiente(request);			
		}
		
		//Buscar hoja independiente
		if(ConstantesSEFE.OPER_GUARDAR_HOJA_INDEPENDIENTE.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);			
			return objRespuesta = adaptadorVaciados.guardarHojaIndependiente(request);			
		}
		
		//Confirmar hoja independiente
		if(ConstantesSEFE.OPER_CONFIRMAR_HOJA_INDEPENDIENTE.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);			
			return objRespuesta = adaptadorVaciados.confirmarHojaIndependiente(request);			
		}
		
		// Borrar hojas independientes
		if(ConstantesSEFE.OPER_BORRAR_HOJA_INDEPENDIENTE.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);			
			return objRespuesta = adaptadorVaciados.borrarHojasIndependientes(request);			
		}
		
		// Buscar informacion complementaria
		if(ConstantesSEFE.OPER_BUSCAR_INFO_COMPLEMENTARIA.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);	
			return objRespuesta = new AdaptadorInfoComplementaria().buscarInfoComplementaria(request);			
		}
		
		// Guardar informacion complementaria
		if(ConstantesSEFE.OPER_GUARDAR_INFO_COMPLEMENTARIA.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);	
			return objRespuesta = new AdaptadorInfoComplementaria().guardarInfoComplementaria(request);			
		}
		
		// Buscar cuentas adicionales
		if(ConstantesSEFE.OPER_BUSCAR_CUENTAS_ADICIONALES.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);	
			return objRespuesta = new AdaptadorVaciados().buscarCuentasAdicionales(request);			
		}
		
		// Guardar cuentas adicionales
		if(ConstantesSEFE.OPER_GUARDAR_CUENTAS_ADICIONALES.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);			
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);	
			return objRespuesta = new AdaptadorVaciados().guardarCuentasAdicionales(request);			
		}
		
		// Buscar otros indicadores (EERR)
		if (ConstantesSEFE.OPER_BUSCAR_OTROS_INDICADORES.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().buscarOtrosIndicadores(request);
		}

		// Guardar otros indicadores
		if (ConstantesSEFE.OPER_GUARDAR_OTROS_INDICADORES.equalsIgnoreCase(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().guardarOtrosIndicadores(request);
		}
		if(ConstantesSEFE.OPER_BUSCAR_CALIFICADORAS.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerCalificadoras(request);
		}
		if(ConstantesSEFE.OPER_BUSCAR_CALIFICADORAS_INGRESADAS.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerCalificadorasIngresadas(request);
		}
		if(ConstantesSEFE.OPER_BUSCAR_CALIFICADORAS_INGRESADAS_NACIONAL.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerCalificadorasIngresadasNacional(request);
		}
		if(ConstantesSEFE.OPER_BUSCAR_CALIFICADORAS_INGRESADAS_INTERNACIONAL.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerCalificadorasIngresadasInternacional(request);
		}
		if(ConstantesSEFE.OPER_BUSCAR_CLASIF_RIESGO_LOCALES.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerClasificadorasRiesgoLocales(request);
		}		
		if(ConstantesSEFE.OPER_BUSCAR_CLASIF_RIESGO_NACIONALES.equals(ctx.getOperacion())) {			
			return new AdaptadorVaciados().obtenerClasificadorasNacionales();
		}
		if(ConstantesSEFE.OPER_BUSCAR_CLASIF_RIESGO_INTERNACIONALES.equals(ctx.getOperacion())) {			
			return new AdaptadorVaciados().obtenerClasificadorasInternacionales();
		}
		if(ConstantesSEFE.OPER_INSERTAR_CLASIF_RIESGO_LOCALES.equals(ctx.getOperacion())){
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return new AdaptadorVaciados().insertarClasificacionesRiesgoLocales(request);			
		}
		if(ConstantesSEFE.OPER_INSERTAR_CALIF_RIESGO_NACIONALES.equals(ctx.getOperacion())){
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return new AdaptadorVaciados().insertarCalificacionesRiesgoNacionales(request);
		}
		if(ConstantesSEFE.OPER_INSERTAR_CALIF_RIESGO_INTERNACIONALES.equals(ctx.getOperacion())){
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return new AdaptadorVaciados().insertarCalificacionesRiesgoInternacionales(request);
		}
		if(ConstantesSEFE.OPER_BUSCAR_VALORES_RANGO.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerValoresRango(request);
		}
		if(ConstantesSEFE.OPER_OBTENER_VALOR_COMBO_SELECCIONADO_CLASIFICADORA_LOCAL.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerValorComboSeleccionadoClasificadoraLocal(request);
		}
		if(ConstantesSEFE.OPER_OBTENER_VALOR_COMBO_SELECCIONADO_CLASIFICADORA_NACIONAL_INTERNACIONAL.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerValorComboSeleccionadoClasificadoraNacionalInternacional(request);
		}
		if(ConstantesSEFE.OPER_OBTENER_VALOR_COMBO_SELECCIONADO_OUTLOOK_LOCAL.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerValorComboSeleccionadoOutlookLocal(request);
		}
		if(ConstantesSEFE.OPER_OBTENER_VALOR_COMBO_SELECCIONADO_OUTLOOK_NACIONAL_INTERNACIONAL.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerValorComboSeleccionadoOutlookNacionalInternacional(request);
		}
		if(ConstantesSEFE.OPER_OBTENER_VALORES_TIER.equals(ctx.getOperacion())) {			
			return objRespuesta = new AdaptadorVaciados().obtenerValoresTier();
		}
		if(ConstantesSEFE.OPER_OBTENER_VALOR_COMBO_SELECCIONADO_TIER.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().obtenerValorComboSeleccionadoTier(request);
		}
		if(ConstantesSEFE.OPER_INSERTAR_TIER.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			XMLData request = new XMLDataObject();
			request.initFromXml(xml);
			return objRespuesta = new AdaptadorVaciados().insertarTier(request);
		}
		if(ConstantesSEFE.OPER_CARGA_MASIVA_VACIADOS.equals(ctx.getOperacion())) {
			String xml = xmlOperacion.replaceFirst(ConstantesSEFE.INI_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			xml = xml.replaceFirst(ConstantesSEFE.FIN_TAG_MENSAJE, ConstantesSEFE.ESPACIO_VACIO);
			VaciadoCargaMasiva vaciadoCargaMasiva = new VaciadoCargaMasiva();
			vaciadoCargaMasiva.initFromXml(xml);
			String xmlCuentas = xml.substring(xml.indexOf(ConstantesSEFE.INI_TAG_LISTA), xml.indexOf(ConstantesSEFE.FIN_TAG_LISTA) + ConstantesSEFE.FIN_TAG_LISTA.length());
			xmlCuentas = ConstantesSEFE.INI_TAG_MENSAJE.concat(xmlCuentas);
			xmlCuentas = xmlCuentas.concat(ConstantesSEFE.FIN_TAG_MENSAJE);
			ArrayList cuentas = (ArrayList) procesarXmlRequest(xmlCuentas);
			vaciadoCargaMasiva.setListaCuentas(cuentas);
			Vaciado vaciadoResp=servVaciado.cargarVaciadosCargaMasiva(vaciadoCargaMasiva);
			return vaciadoResp;
		}		
		
		return objRespuesta;
		
		
	}
	
}
