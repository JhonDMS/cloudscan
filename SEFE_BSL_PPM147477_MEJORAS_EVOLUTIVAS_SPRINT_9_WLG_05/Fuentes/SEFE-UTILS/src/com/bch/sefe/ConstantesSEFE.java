package com.bch.sefe;

import java.math.BigDecimal;
import java.util.List;

import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.servicios.impl.MessageManager;

public interface ConstantesSEFE {
	public static final String VERSION_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final String APP_PROPS = "APP_PROPS";


	/**
	 * Estado del Flag que indica que un vaciado es confindencial.
	 */
	public static final Integer FLAG_VACIADO_CONFIDENCIAL = new Integer("1");

	public static final BigDecimal BIG_DECIMAL_CERO = new BigDecimal("0.0");
	public static final BigDecimal BIG_DECIMAL_UNO = new BigDecimal("1.0");
	public static final BigDecimal BIG_DECIMAL_CIEN = new BigDecimal("100.0");

	public static final Double DOUBLE_CERO = new Double("0.0");
	public static final Double DOUBLE_UNO = new Double("1.0");
	public static final Double DOUBLE_DOS = new Double("2.0");
	public static final Double DOUBLE_TRES = new Double("3.0");
	public static final Double DOUBLE_CUATRO = new Double("4.0");
	public static final Double DOUBLE_CIEN = new Double("100.0");
	
	// Tipos Rating grupal
	public static final Integer TIPO_RATING_GRUPAL_PYME = new Integer(911013);
	public static final Integer TIPO_RATING_GRUPAL_MULTISEGMENTO = new Integer(911014);

	public static final Integer INTEGER_UNO = Integer.valueOf("1");
	public static final Integer INTEGER_DOS = Integer.valueOf("2");
	public static final Integer INTEGER_CERO = Integer.valueOf("0");
	public static final Integer DOCE_MESES = Integer.valueOf("12");

	public static final Integer FUENTE_CONSOL_SVS = Integer.valueOf("1206");
	public static final String PERIODICIDAD_VACIADO_ANO = "A";
	public static final Integer ID_AUDIT_NO_APLICA = Integer.valueOf("1701");
	
	
	public static final Integer MESES_PER = Integer.valueOf("12"); // REQ: 7.4.29 Sprint 4 Vigencia de rating
	/**
	 * Escala utilizada en las divisiones para los calculos de rating
	 * financiero.
	 */
	public static final int ESCALA_DIVISION = 4;

	// Mime types de archivos utilizados en SEFE

	public static final String MIME_TYPE_PDF = "application/pdf";
	public static final String MIME_TYPE_XLS = "application/excel";
	public static final String MIME_TYPE_DOC = "application/word";

	public static final String CARACTER_COMA = ",";
	public static final String CARACTER_PARENTESIS_DERECHO = ")";
	public static final String CARACTER_PARENTESIS_IZQUIERDO = "(";
	public static final String CARACTER_DOS_PUNTOS = ":";
	public static final String CARACTER_PUNTO_Y_COMA = ";";
	public static final String CARACTER_SLASH = "/";
	public static final String CARACTER_ESPACIO = " ";
	public static final String CARACTER_AMPERSAND = "&";

	public static final String DATE_FORMAT = "date.format";
	public static final String DATE_FORMAT_OSB = "date.format.osb";
	public static final String DATE_FORMAT_OSB_FERIADOS = "date.format.osb.feriados";
	public static final String TIME_FORMAT_OSB = "time.format.osb";
	public static final String DATE_FORMAT_RPT_FICHA_FINAN = "date.format.fichafinan";
	public static final String DATE_FORMAT_EEFF = "date.format.eeff";
	public static final String DATE_FORMAT_GUI_RATING = "date.format.gui.rating";
	public static final String DATE_FORMAT_HEADER_RATING = "date.format.header.rating";
	public static final String DATE_FORMAT_OSB_DECLARAR_IVA = "date.format.osb.declarar.iva";

	public static final String ID_ROL = "idRol";
	public static final String ID_OPERACION = "idOperacion";
	public static final String ID_MODULO = "idModulo";
	public static final String STR_FLAG_VACIADO_AJUSTADO = "STR_FLAG_VACIADO_AJUSTADO";

	public static final String ID_RUT = "rut";

	// Constantes de Alertas e Indicadores
	public static final Integer ALERT_INVALIDANTE = new Integer(0);
	public static final Integer ALERT_ADVERTENCIAS = new Integer(1);
	public static final Integer ALERT_OK = new Integer(2);
	public static final Integer ALERT_NO_VISIBLE = new Integer(3);
	public static final Integer ALERT_ID_TIPO_CTA = new Integer(1033);
	public static final Integer ALERT_TIPO_IND_DINA = new Integer(1091);
	public static final Integer ALERT_TIPO_IND_RESU = new Integer(1011);
	public static final Integer ALERT_SSECT_DINA = new Integer(-1);

	public static final Integer ANALISIS_ALERT_INVALIDANTE = new Integer(-2);
	public static final Integer ANALISIS_ALERT_NO_VISIBLE = new Integer(-1);
	public static final Integer ANALISIS_ALERT_ADVERTENCIAS = new Integer(0);
	public static final Integer ANALISIS_ALERT_OK = new Integer(1);
	public static final Integer ID_UNIDAD_PORCENTAJE = Integer.valueOf("3501");

	// Unidad de Medida de las cuentas
	public static final String UNIDAD_PORCENTAJE = "3501";
	public static final String UNIDAD_VCS = "3502";
	public static final String UNIDAD_MM = "3503";
	public static final String UNIDAD_DS = "3504";

	// Constante estado situacion persona
	public static final Integer ID_CLASIF_ESTADO_SITUACION_PERSONA = new Integer(
			1318);

	// Tipo de cambio de pesos chilenos a pesos chilenos
	public static final Double TIPO_CAMBIO_CLP_CLP = new Double("1.0000");

	// Glosa abreviada de peso chileno
	public static final String GLOSA_MONEDA_CLP = "CLP";

	public static final String CODIGO_OSB_PESOS_CHILENOS = "001";

	public static final Integer VERSION_VACIADO_VALIDA = new Integer(1);

	// estado de los vaciados
	public static final Integer VACIADO_EN_CURSO = new Integer(0);
	public static final Integer VACIADO_VIGENTE = new Integer(1);

	// Flags Vaciados D.A.I
	public static final Integer FLAG_VACIADO_ES_DAI = new Integer(1);
	public static final Integer FLAG_VACIADO_NO_ES_DAI = new Integer(0);

	public static final String FLAG_PERIODICIDAD_VACIADO_ANUAL = "1";

	// clasificacion de vaciado en curso y vigente
	public static final Integer CLASIF_VACIADO_EN_CURSO = new Integer(1401);
	public static final Integer CLASIF_VACIADO_VIGENTE = new Integer(1402);

	public static final Integer CLASIF_VACIADO_ORG_INFO_BCH_SEFE = new Integer(
			1216);

	// clasificaciones de calidad de informacion
	public static final Integer CLASIF_CALIDAD_RECONOCIADA = new Integer(1801);
	public static final Integer CLASIF_CALIDAD_CONFIABLE = new Integer(1802);
	public static final Integer CLASIF_CALIDAD_RESTRICCION = new Integer(1803);

	public static final Integer PESO_CALIDAD_RECONOCIADA = new Integer(1);
	public static final Integer PESO_CALIDAD_CONFIABLE = new Integer(2);
	public static final Integer PESO_CALIDAD_RESTRICCION = new Integer(0);

	// flags de correccion y reconciliacion
	public static int MASK_CORRECCION_MONETARIA = 0x0001;
	public static int MASK_RECONCILIACION = 0x0002;

	// tipo de proyeccion del vaciado 0 CORTA - 1 LARGA
	public static Integer FLAG_PROYECCION_ON = new Integer(1);
	public static Integer FLAG_PROYECCION_OFF = new Integer(0);

	public static String OPCION_SI = "Si";
	public static String OPCION_NO = "No";

	public static String FLAG_OPERA_COMO_EMPRESA = "S";
	public static String FLAG_NO_OPERA_COMO_EMPRESA = "N";

	public static Integer VALOR_1 = new Integer(1);
	public static Integer VALOR_0 = new Integer(0);

	// Tipos de rating (Ids de clasificacion)
	public static final Integer TIPO_RATING_INDIVIDUAL = new Integer(4501);
	public static final Integer TIPO_RATING_GRUPAL = new Integer(4502);
	public static final Integer TIPO_RATING_FINANCIERO = new Integer(4503);
	public static final Integer TIPO_RATING_PROYECTADO = new Integer(4504);
	public static final Integer TIPO_RATING_NEGOCIO = new Integer(4505);
	public static final Integer TIPO_RATING_COMPORTAMIENTO = new Integer(4506);

	/**
	 * Key de sefe.properties que contiene como valor el conjunto de roles
	 * separados por ','(coma) que pueden visualizar vaciados confidenciales sin
	 * ser el creador del vaciado.
	 */
	public static final String KEY_ROLES_VISUALIZAN_VAC_CONFIDENCIALES = "roles.visualizan.vaciados.confidenciales";

	/**
	 * Flag que indica si un Vaciado no es AJUSTADO.
	 */
	public static final Integer FLAG_VACIADO_NO_AJUSTADO = new Integer(0);

	/**
	 * Flag que indica si un Vaciado es AJUSTADO.
	 */
	public static final Integer FLAG_VACIADO_AJUSTADO = new Integer(1);

	public static final String KEY_VACIADO_AJUSTADO = "VACIADO_AJUSTADO";

	// controlan comportamiento del motor de calculo en caso de error
	public static final String SERVICIO_CALCULO_STOP_ON_ERROR = "servicio.calculo.stop.on.error";
	public static final String SERVICIO_CALCULO_UPDATE_ON_ERROR = "servicio.calculo.update.on.error";

	// controla si se actualiza la informacion del cliente empresa desde ficha
	// chica al ingresar al modulo
	public static final String ACTUALIZA_INFO_CLIENTE = "actualiza.info.cliente.ingreso.modulo";

	// constantes de los tipos de cuentas
	public static final String TIPO_CUENTA_ACTIVOS = "tipo.cuenta.activos";

	public static final String TIPO_CUENTA_PASIVOS = "tipo.cuenta.pasivos";
	public static final String TIPO_CUENTA_PASIVOS_CONTINGENTE = "tipo.cuenta.pasivos.circulante";
	public static final String TIPO_CUENTA_PASIVOS_CIRCULANTE = "tipo.cuenta.pasivos.circulante";
	public static final String TIPO_CUENTA_PASIVOS_INTERES = "tipo.cuenta.pasivos.interes";
	public static final String TIPO_CUENTA_EERR = "tipo.cuenta.eerr";
	public static final String TIPO_CUENTA_COMPONENTES = "tipo.cuenta.componentes";
	public static final String TIPO_CUENTA_FLUJO_CAJA = "tipo.cuenta.flujo.caja";
	public static final String TIPO_CUENTA_CORRECCION_MONETARIA = "tipo.cuenta.corr.monetaria";
	public static final String TIPO_CUENTA_REC_PATRIMONIO = "tipo.cuenta.rec.patrimonio";
	public static final String TIPO_CUENTA_REC_ACTIVO_FIJO = "tipo.cuenta.rec.activo.fijo";
	public static final String TIPO_CUENTA_REC_INVERSIONES_SOCIEDAD = "tipo.cuenta.rec.inversiones.sociedad";
	public static final String TIPO_CUENTA_REC_INTANGIBLES = "tipo.cuenta.rec.intangibles";
	public static final String TIPO_CUENTA_REC_EERR_INTEGRALES = "tipo.cuenta.rec.eerr.integrales";
	public static final String TIPO_CUENTA_INDICADORES = "tipo.cuenta.indicadores";
	public static final String TIPO_CUENTA_CONTROL = "tipo.cuenta.control";
	public static final String TIPO_CUENTA_IND_RESULTADOS = "indicadores.resultados";
	public static final String TIPO_CUENTA_IND_COBERTURA_GASTOS_FINAN = "indicadores.cobertura.gastos.financieros";
	public static final String TIPO_CUENTA_IND_CAPACIDAD_PAGO = "indicadores.capacidad.pago";
	public static final String TIPO_CUENTA_IND_LIQUIDEZ = "indicadores.liquidez";
	public static final String TIPO_CUENTA_IND_ENDEUDAMIENTO = "indicadores.endeudamiento";
	public static final String TIPO_CUENTA_IND_ESTRUCTURA = "indicadores.estructura";
	public static final String TIPO_CUENTA_MAPEO_VE_MAC = "tipo.cuenta.mapeo.ve.mac";
	public static final String TIPO_CUENTA_HIBRIDA = "tipo.com.hibridas";
	public static final String TIPO_CUENTA_AUX_FICHA_FINANCIERA = "tipo.cuenta.aux.ficha.financiera";
	public static final String TIPO_CUENTA_INDICES_ALERTAS = "tipo.cuenta.indices.alertas";
	public static final String TIPO_CUENTA_IND_RATING_FINANCIERO = "indicadores.rating.financiero";
	public static final String TIPO_CUENTA_IND_RATING_PROYECCTADO = "indicadores.rating.proyectado";
	public static final String TIPO_CUENTA_RATING = "tipo.cuenta.rating";
	public static final String TIPO_CUENTA_PROYECCION = "tipo.cuenta.proyeccion";

	public static final String ALRT_PARAM_MIN_CALCULO = "PAR_1";
	public static final String ALRT_PARAM_MAX_CALCULO = "PAR_2";

	// tipo cuentas para la evolucion trimestral
	public static final String TIPO_CUENTA_EVOLUCION_TRIMESTRAL = "tipo.cuenta.evolucion.trimestral";
	public static final String ID_CUENTA_FACTOR_IPC = "id.cuenta.factor.ipc";

	public static final String TIPO_INDICADOR_ETR_NOMINAL_ACUMULADO = "tipo.cuenta.etr.nominal.acumulado";
	public static final String TIPO_INDICADOR_ETR_REAL_ACUMULADO = "tipo.cuenta.etr.real.acumulado";
	public static final String TIPO_INDICADOR_ETR_UNIDAD_MONEDA = "tipo.cuenta.etr.unidad.moneda";
	public static final String TIPO_INDICADOR_ETR_VENTAS = "tipo.cuenta.etr.ventas";
	public static final String TIPO_INDICADOR_ETR_ROB = "tipo.cuenta.etr.rob";
	public static final String TIPO_INDICADOR_ETR_IND_MOVIL = "tipo.cuenta.etr.indicador.movil";

	public static final String TIPO_CUENTA_REPORTE_CONSOLIDADO_COMBINADO = "tipo.cuenta.rpt.consolidado.combinado";
	/**
	 * Key que contiene los codigos de cuentas (indicadores principalmente) que
	 * son afectas de tipo de cambio. A esta constante se le debe adjuntar el
	 * tipo de plan que corresponda.
	 */
	public static final String KEY_CUENTAS_AFECTAS_A_TIPO_CAMBIO = "cuentas.afectas.tipo.cambio.";

	/**
	 * Key de mensajes.properties para la propiedad que contiene el mensaje a
	 * desplegar cuando el servicio DAI no retorna la lista de cuentas.
	 */
	public static final String PROP_RESPUESTA_DAI_SIN_DATOS = "error.dai.retorna.lista.vacia";

	/**
	 * Key de mensajes.properties para la propiedad que contiene el mensaje a
	 * desplegar cuando el servicio DAI retorno las cuentas que no cumplen con
	 * los montos esperados en base a formulas.
	 */
	public static final String PROP_ERROR_REGLAS_MONTOS_DAI = "error.dai.reglas.negocio.montos";

	/**
	 * FLAG que indica que la deuda es DEUDA BANCO
	 */
	public static final Integer FLAG_DEUDA_TIPO_BANCO = ConfigManager
			.getValueAsInteger("tipo.deuda.banco");

	/**
	 * FLAG que indica que la deuda es DEUDA SBIF
	 */
	public static final Integer FLAG_DEUDA_TIPO_SBIF = ConfigManager
			.getValueAsInteger("tipo.deuda.sbif");

	/**
	 * FLAG que indica que la deuda es DEUDA ACHEL, este flag en particular no
	 * se utiliza en la base de datos y no esta en el sefe properties
	 */
	public static final Integer FLAG_DEUDA_TIPO_ACHEL = new Integer(3);

	/*
	 * Parametros de conexion para el content engine de FileNET
	 */
	public static final String FILENET_NOMBRE_USUARIO = ConfigManager
			.getValueAsString("filenet.conexion.content.usuario");
	public static final String FILENET_PASSWORD = ConfigManager
			.getValueAsString("filenet.conexion.content.password");
	public static final String FILENET_URI = ConfigManager
			.getValueAsString("filenet.conexion.content.uri");
	public static final String FILENET_OBJECT_STORE = ConfigManager
			.getValueAsString("filenet.conexion.content.objectsource");
	public static final String FILENET_PATH_ARCHIVOS = ConfigManager
			.getValueAsString("filenet.conexion.content.path");

	/*
	 * Alias utilizados en QA o PRODUCCION para los namespaces del servicio de
	 * feriados.
	 */
	public static final String ALIAS_NAMESPACE_DATOS_FERIADO = ConfigManager
			.getValueAsString("alias.namespace.datos.feriados");
	public static final String ALIAS_NAMESPACE_ENVELOPE = ConfigManager
			.getValueAsString("alias.namespace.envelope");
	public static final String ALIAS_NAMESPACE_BODY = ConfigManager
			.getValueAsString("alias.namespace.body");

	/*
	 * Codigos de cuenta retornadas por el servicio DAI.
	 */
	public static final String CUENTA_DAI_COD122 = "COD_122";
	public static final String CUENTA_DAI_COD123 = "COD_123";
	public static final String CUENTA_DAI_VENTAS = "COD_628";

	// constantes de cuentas totalizadoras
	public static final String CUENTA_TOTAL_ACTIVOS = "cta.total.activos.";
	public static final String CUENTA_TOTAL_PASIVOS = "cta.total.pasivos.";

	// constantes de cuentas de Control
	public static final String CUENTA_CONTROL_CTR01 = "CTR01";
	public static final String CUENTA_CONTROL_CTR02 = "CTR02";

	// Tipos de Plan de Cuenta
	public static final String NOMBRE_PLAN_CUENTAS_CHGAAP_ID = "2001";
	public static final String NOMBRE_PLAN_CUENTAS_IFRSCF_ID = "2005";
	public static final String NOMBRE_PLAN_CUENTAS_IFRSCN_ID = "2006";
	public static final String NOMBRE_PLAN_CUENTAS_IFRSLF_ID = "2007";
	public static final String NOMBRE_PLAN_CUENTAS_IFRSLN_ID = "2008";
	public static final String NOMBRE_PLAN_CUENTAS_IFRS_BANCOS_ID = "2010";
	public static final String NOMBRE_PLAN_CUENTAS_IFRS_SEG_VIDA = "2011";
	public static final String NOMBRE_PLAN_CUENTAS_IFRS_SEG_GEN = "2012";
	public static final String NOMBRE_PLAN_CUENTAS_IFRS_CORR_ID = "2013";
	public static final Integer NOMBRE_PLAN_CUENTAS_AGRIC = new Integer(2019);
	public static final Integer NOMBRE_PLAN_CUENTAS_INMOBILIARIA = new Integer(
			2024);

	public static final String MSG_NO_CORRECCION_MONETARIA = "msg.no.correcion.monetaria";
	public static final String MSG_NO_RECONCILIACION = "msg.no.reconciliacion";
	public static final String MSG_NO_RECONCILIACION_NO_CORRECCION = "msg.no.reconciliacion.no.correcion";

	// OPERACIONES DE VACIADO
	public static final String OPER_BUSCAR_CLASIFICACIONES = "CLASIFICACIONES";
	public static final String OPER_BUSCAR_GRUPO_CLASIFICACIONES = "000020";
	public static final String OPER_VISUALIZAR_VAC_CONFIDENCIALES = "013713";
	public static final String OPER_VISUALIZAR_FILENET_CONFIDENCIALES = "013714";
	public static final String USUARIO_ADM_FILENET = "013722";
	public static final String OPER_ACTUALIZAR_INFO_CLIENTE = "000021";
	public static final String OPER_BUSCAR_CLASIFICACION_POR_ID = "000022";
	/** @deprecated */
	public static final String OPER_ACTUALIZAR_PARAMETRIZACION = "000023";
	public static final String OPER_ACTIVAR_VERSION = "000023";
	public static final String OPER_SUFIJO_ACTUALIZAR_PARAMETRIZACION = "24";
	public static final String OPER_CONSULTAR_PARAMETRIZACION = "000024";
	public static final String OPER_SUFIJO_CONSULTAR_PARAMETRIZACION = "23";
	public static final String OPER_CONSULTAR_METADATA = "000025";
	public static final String OPER_BUSCAR_COMBO_ESPECIAL = "000100";
	public static final String OPER_BUSCAR_VACIADOS = "010002";
	public static final String OPER_BUSCAR_VACIADOS_STRING = "010003";
	public static final String OPER_BUSCAR_VACIADOS_BCH_SEFE = "010004";
	public static final String OPER_BUSCAR_EMPRESA = "010701";
	public static final String OPER_CREAR_VACIADO = "011101";
	public static final String OPER_CREAR_VACIADO_CONS_COMB = "011304";
	public static final String OPER_VERIFICAR_VACIADO_CONS_COMB = "011305";
	public static final String OPER_BUSCAR_EMPRESA_CONS_COMB = "011306";
	public static final String OPER_GUARDAR_DATOS_GENERALES = "011102";
	public static final String OPER_BUSCAR_DATOS_GENERALES = "011103";
	public static final String OPER_BUSCAR_RELACION_TIPO_BALANCE_PLAN_CTAS = "011104";
	public static final String OPER_BUSCAR_TIPO_PLAN_CTAS_VACIADO = "011105";
	public static final String OPER_BUSCAR_CUENTAS_ESTADOFINANCIERO = "011212";
	public static final String OPER_BUSCAR_CUENTAS_INFO_COMPLEMENTARIA = "011214";
	public static final String OPER_BUSCAR_CUENTAS_INFO_MERCADO = "011215";
	public static final String OPER_CALCULAR_VACIADO = "011230";
	public static final String OPER_GUARDAR_CUENTAS_ACTIVOS = "011211";
	public static final String OPER_AJUSTAR_VACIADOS = "011311";
	public static final String OPER_GUARDAR_CUENTAS_PASIVOS = "011221";
	public static final String OPER_GUARDAR_CUENTAS_ESTADOSRESULTADOS = "011231";
	public static final String OPER_OBTENER_ENCABEZADO = "011213";
	public static final String OPER_OBTENER_ENCABEZADO_PASIVOS_CONTINGENTES = "011510";
	public static final String OPER_GUARDAR_DETALLE_CORRECCION_MONETARIA = "011241";
	public static final String OPER_ANULAR_DETALLE_CORRECCION_MONETARIA = "011242";
	public static final String OPER_CALCULAR_RECONCILIACION = "011321";
	public static final String OPER_GUARDAR_RECONCILIACION = "011801";
	public static final String OPER_GUARDAR_FLUJO_CAJA = "011802";
	public static final String OPER_GUARDAR_NOTA_VACIADO = "011251";
	public static final String OPER_BUSCAR_NOTA = "011252";
	public static final String OPER_BUSCAR_FONDOS_CLIENTE = "010010";
	public static final String OPER_CREAR_VACIADO_DAI = "012210";
	public static final String OPER_EVALUAR_ALERTAS_VACIADO = "010601";
	public static final String OPER_CAMBIAR_ESTADO_VIGENTE = "010501";
	public static final String OPER_BUSCAR_PASIVOS_CONTINGENTES = "011501";
	public static final String OPER_GUARDAR_PASIVOS_CONTINGENTES = "011502";
	public static final String OPER_BUSCAR_HOJA_INDEPENDIENTE = "011503";
	public static final String OPER_GUARDAR_HOJA_INDEPENDIENTE = "011504";
	public static final String OPER_CONFIRMAR_HOJA_INDEPENDIENTE = "011506";
	public static final String OPER_BUSCAR_HOJAS_INDEPENDIENTES = "011507";
	public static final String OPER_GENERAR_REPORTE_HOJA_INDEPENDIENTE = "011505";
	public static final String OPER_BORRAR_HOJA_INDEPENDIENTE = "011511";
	public static final String OPER_GENERAR_REPORTE_EXPORT_EXCEL_CONS_COMB = "013711";
	public static final String OPER_OBTENER_EMPRESAS = "010701";
	public static final String OPER_CREAR_FECU = "014001";
	public static final String OPER_UPLOAD_FECU = "014002";
	public static final String OPER_UPLOAD_XBRL = "014003";
	public static final String OPER_UPLOAD_BANCOS = "014004";
	public static final String OPER_UPLOAD_AGRICOLA = "014005";
	public static final String OPER_UPLOAD_CORREDORAS = "014006";
	public static final String OPER_UPLOAD_SEG_GRAL = "014007";
	public static final String OPER_UPLOAD_SEG_VIDA = "014008";
	public static final String OPER_OBTENER_EMPRESAS_VINCULADAS_VAC = "010702";
	public static final String OPER_OBTENER_EMPRESAS_MALLA_RELACIONES = "010703";
	public static final String OPER_OBTENER_EMPRESAS_VINCULADAS_CLI = "010704";
	public static final String OPER_ACTUALIZAR_VINC_EMPRESAS_VAC_CONSOLIDADOS = "010710";
	public static final String OPER_AGREGAR_EMPRESAS = "010720";
	public static final String OPER_DESVINCULAR_EMPRESAS = "010730";
	public static final String OPER_TIENE_DCM = "011244";
	public static final String OPER_TIENE_RECONCILIACION = "011248";
	public static final String OPER_GENERAR_REPORTE_VACIADO_UN_PER = "013710";
	public static final String OPER_VALIDA_MSG_FLUJO_CAJA = "011300";
	public static final String OPER_BORRAR_VACIADOS = "011301";
	public static final String OPER_BUSCAR_ALERTA_INDICADOR = "011302";
	public static final String OPER_OBTENER_GRUPO_PROPUESTO = "011303";
	public static final String OPER_BUSCAR_ENCABEZADO_ALERTAS_INDICADORES = "013801";
	public static final String OPER_PERM_ACESSO_RATING_FINANCIERO = "400000";
	public static final String OPER_PERM_ACCESO_RATING_PROYECTADO = "400010";
	public static final String OPER_PERM_ACCESO_RATING_COMPORTAMIENTO = "400020";
	public static final String OPER_PERM_ACCESO_RATING_NEGOCIO = "400030";
	public static final String OPER_PERM_ACCESO_RATING_INDIVIDUAL = "400040";
	public static final String OPER_PERM_ACCESO_RATING_GARANTE = "400050";
	public static final String OPER_PERM_CONSULTA_RATING_VIZUALIZAR = "400060";
	public static final String OPER_PERM_CONSULTA_RATING_ACCESO = "400070";
	public static final String OPER_PERM_ACCESO_RATING_GRUPO = "400080";
	public static final String OPER_BUSCAR_INFO_COMPLEMENTARIA = "013800";
	public static final String OPER_GUARDAR_INFO_COMPLEMENTARIA = "013801";

	public static final String OPER_PERM_VIEW_RATING_FINANCIERO = "400090";
	public static final String OPER_PERM_VIEW_RATING_PROYECTADO = "400100";
	public static final String OPER_PERM_VIEW_RATING_COMPORTAMIENTO = "400110";
	public static final String OPER_PERM_VIEW_RATING_NEGOCIO = "400120";
	public static final String OPER_PERM_VIEW_RATING_INDIVIDUAL = "400130";
	public static final String OPER_PERM_VIEW_RATING_GARANTE = "400140";
	public static final String OPER_PERM_VIEW_RATING_GRUPO = "400150";
	public static final String OPER_VER_DET_RATING_FILTRADO = "400151";

	public static final String OSB_CONSULTA_UF_DIA_PARAM_UF = "UF";
	public static final String OSB_CONSULTA_UF_DIA_PARAM_IVP = "IVP";

	public static final String OPER_BUSCAR_MALLA_REL_RTG_GRUPAL = "300900";

	public static final String OPER_BUSCAR_CUENTAS_ADICIONALES = "011508";
	public static final String OPER_GUARDAR_CUENTAS_ADICIONALES = "011509";

	public static final String OPER_BUSCAR_OTROS_INDICADORES = "013802";
	public static final String OPER_GUARDAR_OTROS_INDICADORES = "013803";

	public static final String OPER_BUSCAR_CALIFICADORAS = "013804";
	public static final String OPER_BUSCAR_CALIFICADORAS_INGRESADAS = "013812";
	public static final String OPER_BUSCAR_CALIFICADORAS_INGRESADAS_NACIONAL = "013813";
	public static final String OPER_BUSCAR_CALIFICADORAS_INGRESADAS_INTERNACIONAL = "013814";
	public static final String OPER_BUSCAR_CLASIF_RIESGO_LOCALES = "013805";
	public static final String OPER_BUSCAR_CLASIF_RIESGO_NACIONALES = "013807";
	public static final String OPER_BUSCAR_CLASIF_RIESGO_INTERNACIONALES = "013808";
	public static final String OPER_INSERTAR_CLASIF_RIESGO_LOCALES = "013809";
	public static final String OPER_INSERTAR_CALIF_RIESGO_NACIONALES = "013810";
	public static final String OPER_INSERTAR_CALIF_RIESGO_INTERNACIONALES = "013811";
	public static final String OPER_BUSCAR_VALORES_RANGO = "013806";
	public static final String OPER_OBTENER_VALOR_COMBO_SELECCIONADO_CLASIFICADORA_LOCAL = "013818";
	public static final String OPER_OBTENER_VALOR_COMBO_SELECCIONADO_CLASIFICADORA_NACIONAL_INTERNACIONAL = "013815";
	public static final String OPER_OBTENER_VALOR_COMBO_SELECCIONADO_OUTLOOK_LOCAL = "013817";
	public static final String OPER_OBTENER_VALOR_COMBO_SELECCIONADO_OUTLOOK_NACIONAL_INTERNACIONAL = "013816";
	public static final String OPER_OBTENER_VALORES_TIER = "013819";
	public static final String OPER_OBTENER_VALOR_COMBO_SELECCIONADO_TIER = "013820";
	public static final String OPER_INSERTAR_TIER = "013821";
	public static final String OPER_OBTENER_VACIADOS_CORREDORAS = "013822";
	public static final String OPER_CARGAR_CUENTAS_SVS = "013823";
	public static final String OPER_CARGA_MASIVA_VACIADOS = "014010";
	public static final String OPER_OBTENER_VALORES_TIER1 = "013824";
	public static final String OPER_VALIDAR_RATING_FINANCIERO = "013825";
	/**
	 * Valor de entrada para el web services CS000464_IValMensuales que indica
	 * que se esta consultando por ano y mes.
	 */
	public static final String OSB_IVL_CONSULTA_POR_ANO_MES = "0";

	/**
	 * Parametro de entada para el web services CS000464_IValMensuales.
	 */
	public static final String OSB_IVL_MODO_CONSULTA = "modoConsulta";

	/**
	 * Parametro de entada para el web services CS000464_IValMensuales. El
	 * formato debe ser yyyymm.
	 */
	public static final String OSB_IVL_MES_ANO = "mesAno";

	public static final String OSB_IVL_OUT_FECHA = "fechaIVL";
	public static final String OSB_IVL_OUT_FUT = "futIVL";
	public static final String OSB_IVL_OUT_IMP = "impIVL";
	public static final String OSB_IVL_OUT_IPC = "ipcIVL";
	public static final String OSB_IVL_OUT_TRP = "trpIVL";
	public static final String OSB_IVL_OUT_TVP = "tvpIVL";
	public static final String OSB_IVL_OUT_UTM = "utmIVL";
	public static final String OSB_IVL_OUT_VIP = "vipIVL";

	/**
	 * Codigo de retorno que indica que la sesion de usuario es valida.
	 */
	public static final int SESION_VALIDA = 0;

	/**
	 * Codigo de retorno que indica que la sesion de usuario es
	 * invalida(caduco).
	 */
	public static final int SESION_INVALIDA = -1;

	/**
	 * Codigo de retorno que indica que se ha creado y asignado una nueva sesion
	 * al usuario.
	 */
	public static final int SESION_CREADA_Y_ASIGNADA = 1;

	/**
	 * Key del parametro con el que se configura el numero de reintentos que
	 * hara el proxy OSB para conectarse al endpoint.
	 */
	public static final String KEY_REINTENTOS_PROXY_OSB = "servicios.proxy.intentos.reconexion";

	/**
	 * Key del parametro con el que se activa o desactiva la configuracion de
	 * reintentos que hara el proxy OSB.
	 */
	public static final String KEY_REINTENTOS_PROXY_OSB_ACTIVACION = "servicios.proxy.intentos.reconexion.activado";

	// OPERACIONES DE ANALISIS
	public static final String OPER_GENERAR_FICHA_FINANCIERA_PDF = "013701";
	public static final String OPER_GENERAR_FICHA_FINANCIERA_XLS = "013702";

	public static final String OPER_GENERAR_RPT_INFO_COMPLEMENTARIA_PDF = "013708";
	public static final String OPER_GENERAR_RPT_INFO_COMPLEMENTARIA_XLS = "013707";

	public static final String OPER_GENERAR_RPT_MERCADO_CORREDORA = "013709";

	public static final String OPER_GENERAR_RPT_INDICADORES_SEG_GRALES = "013712";

	public static final String OPER_GENERAR_RPT_COMPOSICION_PRIMA_RETENIDA = "013715";
	public static final String OPER_GENERAR_RPT_IND_TECNICOS_X_RAMO = "013716";
	public static final String OPER_GENERAR_RPT_COMPOSICION_PRIMA_DIRECTA = "013717";
	public static final String OPER_GENERAR_RPT_MERCADO_Y_SINIESTRALIDAD = "013718";
	public static final String OPER_GENERAR_RPT_BENCHMARK_BANCO = "013719";

	public static final String OPER_GENERAR_DETALLE_CUENTAS_PDF = "013730";
	public static final String OPER_GENERAR_DETALLE_CUENTAS_XLS = "013731";

	public static final String OPER_GENERAR_REPORTE_INDICES_ALERTAS_PDF = "013732";
	public static final String OPER_GENERAR_REPORTE_INDICES_ALERTAS_XLS = "013733";
	
	public static final String OPER_GENERAR_REPORTE_VACIADO_VARIOS_PER_PDF = "013720";
	public static final String OPER_GENERAR_REPORTE_VACIADO_VARIOS_PER_XLS = "013721";

	public static final String OPER_GENERAR_REPORTE_CONSOLIDADO_COMBINADO_DETALLE_PDF = "013740";
	public static final String OPER_GENERAR_REPORTE_CONSOLIDADO_COMBINADO_DETALLE_XLS = "013741";
	public static final String OPER_GENERAR_REPORTE_CONSOLIDADO_COMBINADO_RESUMEN_PDF = "013742";
	public static final String OPER_GENERAR_REPORTE_CONSOLIDADO_COMBINADO_RESUMEN_XLS = "013743";

	public static final String OPER_GENERAR_REPORTE_EVOLUCION_TRIMESTRAL_PDF = "013750";
	public static final String OPER_GENERAR_REPORTE_EVOLUCION_TRIMESTRAL_XLS = "013751";

	// OPERACIONES DE RATING
	public static final String OPER_CONSULTAR_RATING = "300000";
	public static final String OPER_CONSULTAR_TIPOS_RATING = "300010";
	public static final String OPER_VER_FICHA_RATING = "300020";
	public static final String OPER_GENERAR_FICHA_RATING_PDF = "300030";
	public static final String OPER_GENERAR_RATING_IND_PDF = "300040";
	public static final String OPER_GENERAR_REPORTE_RATING_FINANCIERO = "300231";
	public static final String OPER_GENERAR_REPORTE_EVALUACION_FINAL = "300232";
	public static final String OPER_BORRAR_RATING_EN_CURSO = "300233";

//	public static final String OPER_CONSULTAR_PLANTILLA_RATING = "300430";
	public static final String OPER_CONSULTAR_RATING_INDIVIDUAL_SUGERIDO = "300100";
	public static final String OPER_GRABAR_RATING_INDIVIDUAL = "300110";
	public static final String OPER_GENERAR_RATING_INDIVIDUAL = "300130";
	public static final String OPER_GENERAR_RATING_INDEPENDIENTE = "300131"; // Requerimiento 7.4.26 Sprint 3 Rating Individual Transversal 
	public static final String OPER_OBTENER_RATING_COMPORTAMIENTO = "300510";
	public static final String OPER_GENERAR_RATING_NEGOCIO = "300060";
	public static final String OPER_CALCULAR_RATING_NEGOCIO = "300420";
	public static final String OPER_CONSULTA_RATING_COMPORTAMIENTO = "300500";
//	public static final String OPER_OBTENER_RATING_GRUPAL = "300600";
	public static final String OPER_OBTENER_GRUPO_RELACIONADO = "300670";
	public static final String OPER_CONFIRMAR_RATING_INDIVIDUAL = "300140";
	public static final String OPER_CONFIRMAR_RATING_INDIVIDUAL_MODIFICADO = "300150";
	public static final String OPER_CONSULTA_ALARMAS_RATING_PARCIALES = "300130";
	public static final String OPER_GENERAR_RATING_FINANCIERO = "300230";
	public static final String OPER_CALCULAR_RATING_FINANCIERO = "300200";
	public static final String OPER_CONFIRMAR_RATING_FINANCIERO = "300210";
	public static final String OPER_CONFIRMAR_VACIADO_VIGENTE_RATING_FINANCIERO = "300220";
	public static final String OPER_BUSCAR_EMPRESA_RATING_GRUPAL = "300610";
	public static final String OPER_GRABAR_RATING_GRUPAL = "300620";
	public static final String OPER_CONFIRMAR_RATING_GRUPAL_CALCULADO = "300630";
	public static final String OPER_CONFIRMAR_RATING_GRUPAL_MANUAL = "300631";
	public static final String OPER_AGREGAR_EMPRESA_RATING_GRUPAL = "300640";
	public static final String OPER_GUARDAR_RATING_NEGOCIO = "300190";
	public static final String OPER_CALCULAR_RATING_GRUPAL = "300650";
	public static final String OPER_INGRESAR_RATING_PROY_INMOB = "300300";
	public static final String OPER_BUSCAR_BALANCES = "400152";
	public static final String OPER_DESCARGAR_PLANTILLA = "400153";
	public static final String OPER_ACTUALIZAR_CARGA_BALANCE = "400154";
	public static final String OPER_CARGAR_PARAMETROS_AGRICOLA = "400155";
	public static final String OPER_DESCARGAR_PLANTILLA_PARAMETROS = "400156";
	public static final String OPER_CARGAR_PLANTILLA_AGRICOLA = "400157";
	public static final String OPER_CARGAR_PLANTILLA_INMOBILIARIA = "400158";
	public static final String OPER_CARGAR_PLANTILLA_CONSTRUCTORA = "400159";
	//sprint 4 req: 7.1.9 y 7.1.12 eliminar balances y cuadros de obras
	public static final String OPER_BORRAR_BALANCE = "400160";
	public static final String OPER_CALCULAR_RATING_GARANTE = "300700";
	public static final String OPER_CONFIRMAR_RATING_GARANTE = "300710";
	public static final String OPER_CONSULTAR_RATING_GARANTE = "300720";
	public static final String OPER_BUSCAR_CLASIFICACION_RIESGO = "300730";
	public static final String OPER_BUSCAR_DETALLE_CUENTAS_ESTADOFINANCIERO = "010731";
	public static final String OPER_INGRESAR_MOD_DETALLE_CTAS = "010732";
	public static final String OPER_CADUCAR_RATINGS_VENCIDOS = "390010";
	public static final String OPER_GENERAR_INTERFAZ_SIEBEL = "390011";
	public static final String OPER_REGENERAR_INTERFAZ_SIEBEL = "390012";
	public static final String OPER_EDITAR_RELACIONADOS_GRUPO_RTG = "300680";
	public static final String OPER_GENERAR_RATING_GRUPAL = "300690";
	public static final String OPER_ELIMINAR_RELACIONADO_GRUPO_RTG = "300660";

	/* OPERACIONES DE RATING PROYECTADO */
	public static final String OPER_CREAR_VACIADO_PROYECTADO = "300800";
	public static final String OPER_GENERAR_PROYECCION = "300810";
	public static final String OPER_CONFIRMAR_PROYECCION = "300820";
	public static final String OPER_CONSULTAR_PROYECCION = "300830";

	public static final String TXT_PARAM_CONFIG_OSB_ENDPOINT = "endpoint";
	public static final String TXT_PARAM_CONFIG_OSB_PARSER = "parser";
	public static final String TXT_PARAM_CONFIG_OSB_INVOKER = "invoker";

	// OPERACIONES REPORTES
	public static final String OPER_GENERAR_REPORTE_NEGOCIO = "300440";
	public static final String OPER_GENERAR_REPORTE_RATING_PROYECTADO = "300840";
	public static final String OPER_GENERAR_REPORTE_PROYECCION_RATING = "300850";
	public static final String OPER_GENERAR_REPORTE_BALANCE_AGRICOLA = "300851";
	public static final String OPER_GENERAR_REPORTE_FLUJOS_AGRICOLA = "300852";
	public static final String OPER_GENERAR_REPORTE_DEUDA_AGRICOLA = "300853";

	// Operaciones de Filenet
	public static final String OPER_BUSCAR_BIBLIOTECA_CLIENTE = "300930";
	public static final String OPER_ELIMINAR_ADJUNTO = "300940";
	public static final String OPER_UPLOAD_FILENET = "300950";
	public static final String OPER_DOWNLOAD_FILENET = "300960";

	// Codigos de operacion de Proyeccion larga
	public static final String OPER_CONSULTAR_PROYECCION_LARGA = "013760";
	public static final String OPER_BORRAR_PROYECCION_LARGA = "013761";
	public static final String OPER_INGRESAR_PROYECCION_LARGA = "013762";
	public static final String OPER_CREAR_PROYECCION_LARGA = "013763";
	public static final String OPER_GENERAR_PROYECCION_LARGA = "013764";
	public static final String OPER_MODIFICAR_PROYECCION_LARGA = "013765";
	public static final String OPER_CONFIRMAR_PROYECCION_LARGA = "013766";
	public static final String OPER_CARGAR_DATOS_PROYECCION_LARGA = "013767";
	public static final String OPER_ACTUALIZAR_NUMERO_PERIODOS_PROYECTADOS = "013768";
	public static final String OPER_DETALLE_PROYECCION_LARGA = "013769";
	public static final String OPER_EXPORTAR_DATOS_PROYECCION_LARGA = "013776";
	public static final String OPER_REPORTE_RESUMEN_PROYECCION_LARGA_PDF = "013770";
	public static final String OPER_REPORTE_RESUMEN_PROYECCION_LARGA_XSL = "013771";
	public static final String OPER_REPORTE_DETALLE_PROYECCION_LARGA_PDF = "013772";
	public static final String OPER_REPORTE_DETALLE_PROYECCION_LARGA_XSL = "013773";
	public static final String OPER_REPORTE_RESUMEN_SOC_PROYECCION_LARGA_PDF = "013774";
	public static final String OPER_REPORTE_RESUMEN_SOC_PROYECCION_LARGA_XSL = "013775";
	public static final String OPER_REPORTE_FICHA_FINAN_GRAFICA_XSL = "013703";
	public static final String OPER_REPORTE_FICHA_FINAN_GRAFICA_PDF = "013704";
	public static final String OPER_GENERAR_FICHA_CIFRAS_FINANCIERAS_ANUALES_PDF = "013705";
	public static final String OPER_GENERAR_FICHA_CIFRAS_FINANCIERAS_ANUALES_XLS = "013706";

	public static final String OPER_CARGAR_PERFIL_DEUDAS = "013777";

	// Constantes de MAPEOS
	public static final Integer MAPEO_DAI = new Integer(1);
	public static final Integer MAPEO_FECU = new Integer(2);
	public static final Integer MAPEO_SITUACION_PERSONA = new Integer(3);
	public static final Integer MAPEO_XBRL = new Integer(4);
	public static final Integer MAPEO_CARGA_MASIVA = new Integer(5);

	// configuracion de los cache
	public static final String CACHE_CONSULTA_IPC = "cache.consulta.ipc";
	public static final String CACHE_CONSULTA_TIPO_CAMBIO = "cache.consulta.tipo.cambio";
	public static final String CACHE_CONSULTA_FICHA_CHICA = "cache.consulta.ficha.chica";
	public static final String CACHE_CONSULTA_IVA = "cache.consulta.iva";
	public static final String CACHE_CONSULTA_DECLARACION_IVA = "cache.consulta.declaracion.iva";
	public static final String CACHE_CONSULTA_IPC_ENABLED = "cache.consulta.ipc.enabled";
	public static final String CACHE_CONSULTA_TIPO_CAMBIO_ENABLED = "cache.consulta.tipo.cambio.enabled";
	public static final String CACHE_CONSULTA_FICHA_CHICA_ENABLED = "cache.consulta.ficha.chica.enabled";
	public static final String CACHE_CONSULTA_IVA_ENABLED = "cache.consulta.iva.enabled";
	public static final String CACHE_CONSULTA_DECLARACION_IVA_ENABLED = "cache.consulta.declaracion.iva.enabled";
	public static final String CACHE_CONSULTA_MALLA_RELACIONES = "cache.consulta.malla.relaciones";
	public static final String CACHE_CONSULTA_MALLA_RELACIONES_ENABLED = "cache.consulta.malla.relaciones.enabled";
	public static final String CACHE_CONSULTA_FERIADOS = "cache.consulta.feriados";
	public static final String CACHE_CONSULTA_FERIADOS_ENABLED = "cache.consulta.feriados.enabled";
	public static final String CACHE_CONSULTA_COMPORTAMIENTO_CREDITICIO_ENABLED = "cache.consulta.comp.crediticio.enabled";
	public static final String CACHE_CONSULTA_COMPORTAMIENTO_CREDITICIO = "cache.consulta.comp.crediticio";
	public static final String CACHE_CONSULTA_PRE_EVALUACION_PERSONA_ENABLED = "cache.consulta.pre.eval.persona.enabled";
	public static final String CACHE_CONSULTA_PRE_EVALUACION_PERSONA = "cache.consulta.pre.eval.persona";
	public static final String CACHE_MAX_PERIODO_DEUDA = "cache.max.periodo.deuda";
	public static final String CACHE_MAX_PERIODO_DEUDA_ENABLED = "cache.max.periodo.deuda.enabled";

	// cache de las propiedades de la aplicacion que se almacenan en tabla de
	// clasificaciones
	public static final String CACHE_CLASIFICACIONES_PROPERTIES = "cache.clasificaciones.properties";
	public static final String CACHE_CLASIFICACIONES_PROPERTIES_ENABLED = "cache.clasificaciones.properties.enabled";

	/**
	 * Grupos de Clasificaciones para transformar IDs Siebel
	 */
	public static final String GRP_CLASIF_ESTADO_CIVIL = "3100";
	public static final String GRP_CLASIF_PROFESION = "3700";
	public static final String GRP_CLASIF_NACIONALIDAD = "3300";
	public static final String GRP_CLASIF_NIVEL_ESTUDIOS = "3400";
	public static final String GRP_CLASIF_INSTITUCION_ESTUDIOS = "3800";
	public static final String GRP_CLASIF_SUBSECTOR = "2500";
	public static final String GRP_CLASIF_BANCA = "2400";
	public static final String GRP_CLASIF_SEGMENTO_ECONOMICO = "2300";
	public static final String GRP_CLASIF_MODALIDAD_MATRIMONIO = "3200";

	/**
	 * Cuentas de DCM calculadas que definen si la DCM ha sido ingresada.
	 */
	public static final String CUENTA_DCM_EXISTENCIAS = "CM40";
	public static final String CUENTA_DCM_ACT_FIJO_NETO = "CM70";
	public static final String CUENTA_DCM_INV_EN_SOCIEDADES = "CM110";
	public static final String CUENTA_DCM_ACT_INTANGIBLES = "CM120";
	public static final String CUENTA_DCM_INTERES_MIN = "CM232";
	public static final String CUENTA_DCM_TOT_PAT_NETO = "CM290";

	public static final String CUENTA_1 = "1";
	public static final String CUENTA_5 = "5";
	public static final String CUENTA_10 = "10";
	public static final String CUENTA_335 = "335";
	public static final String CUENTA_R460 = "R460";
	public static final String CUENTA_COM051 = "COM051";
	public static final String CUENTA_COM110 = "COM110";
	public static final String CUENTA_COM111 = "COM111";
	public static final String CUENTA_COM112 = "COM112";
	public static final String CUENTA_COM113 = "COM113";
	public static final String CUENTA_COM072 = "COM072";
	public static final String CUENTA_340 = "340";
	public static final String CUENTA_90 = "90";
	public static final String CUENTA_341 = "341";
	public static final String CUENTA_G395 = "G395";
	public static final String CUENTA_G401 = "G401";
	public static final String CUENTA_G402 = "G402";
	public static final String CUENTA_G404 = "G404";
	public static final String CUENTA_135 = "135";
	public static final String CUENTA_140 = "140";
	public static final String CUENTA_142 = "142";
	public static final String CUENTA_143 = "143";
	public static final String CUENTA_144 = "144";
	public static final String CUENTA_145 = "145";
	public static final String CUENTA_146 = "146";
	public static final String CUENTA_147 = "147";
	public static final String CUENTA_195 = "195";
	public static final String CUENTA_201 = "201";
	public static final String CUENTA_190 = "190";
	public static final String CUENTA_40 = "40";
	public static final String CUENTA_25 = "25";
	public static final String CUENTA_20 = "20";
	public static final String CUENTA_15 = "15";
	public static final String CUENTA_31 = "31";
	public static final String CUENTA_21 = "21";
	public static final String CUENTA_225 = "225";
	public static final String CUENTA_228 = "228";
	public static final String CUENTA_270 = "270";
	public static final String CUENTA_290 = "290";
	public static final String CUENTA_310 = "310";
	public static final String CUENTA_315 = "315";
	public static final String CUENTA_321 = "321";
	public static final String CUENTA_322 = "322";
	public static final String CUENTA_65 = "65";
	public static final String CUENTA_45 = "45";
	public static final String CUENTA_230 = "230";
	public static final String CUENTA_P84 = "P84";
	public static final String CUENTA_P102 = "P102";
	public static final String CUENTA_P112 = "P112";
	public static final String CUENTA_P85 = "P85";
	public static final String CUENTA_P86 = "P86";
	public static final String CUENTA_P50 = "P50";
	public static final String CUENTA_P51 = "P51";
	public static final String CUENTA_P56 = "P56";
	public static final String CUENTA_P30 = "P30";
	public static final String CUENTA_P103 = "P103";

	/**
	 * Identificador de Clasificacion para moneda CLP
	 */
	public static final Integer ID_CLASIF_MONEDA_CLP = new Integer(1501);

	/**
	 * Identificador de Clasificacion para moneda USD
	 */
	public static final Integer ID_CLASIF_MONEDA_USD = new Integer(1502);

	/**
	 * Identificador de Clasificacion para moneda EUR
	 */
	public static final Integer ID_CLASIF_MONEDA_EUR = new Integer(1503);

	/**
	 * Identificador de Clasificacion para moneda UF
	 */
	public static final Integer ID_CLASIF_MONEDA_UF = new Integer(1504);

	/**
	 * Identificador de Clasificacion para unidad de moneda. UNIDAD (U)
	 */
	public static final Integer ID_CLASIF_UNIDAD = new Integer(1601);

	/**
	 * Identificador de Clasificacion para unidad de moneda. MILES (M)
	 */
	public static final Integer ID_CLASIF_MILES = new Integer(1602);

	/**
	 * Identificador de Clasificacion para unidad de moneda. MILLONES (MM)
	 */
	public static final Integer ID_CLASIF_MILLONES = new Integer(1603);

	/**
	 * Identificador de Clasificacion para unidad de moneda. MILES DE MILLORES
	 * (MMM)
	 */
	public static final Integer ID_CLASIF_MILES_DE_MILLONES = new Integer(1604);

	/**
	 * Key para obtener el factor de conversion de unidad. Para obtener el
	 * factor buscado a este String se le debe concatenar el identificador de
	 * clasificacion de la unidad buscada. (Ej: factor.conversion.unidad.1601
	 * para obtener el factor de conversion de U(unidad)
	 */
	public static final String KEY_FACTOR_CONVERSION_UNIDAD = "factor.conversion.unidad";

	/**
	 * Key para obtener el factor de conversion de codigo a moneda
	 */
	public static final String KEY_SIMBOLO_MONEDA_EN_REPORTE = "simbolo.reporte.moneda";

	/**
	 * Key para obtener el identificador utilizado por el servicio OSB Consulta
	 * valor Moneda, para las distintas monedas. Al utilizar esta constante se
	 * debe concatenar el identificador de clasificacion la moneda (id del
	 * sistema sefe). Ej: codigo.consulta.valor.moneda.1502 para obtener el
	 * codigo siebel de la moneda USD.
	 */
	public static final String KEY_CODIGO_MONEDA_CONSULTA_VALOR_MONEDA = "codigo.consulta.valor.moneda";

	/**
	 * Cuentas de reconciliacion que defininen si la Reconciliacion ha sido
	 * ingresada.
	 */
	public static final String CUENTA_REC_APORTE_CAPITAL = "R480";
	public static final String CUENTA_REC_DIVIDENDOS_RECIBIDOS = "R460";

	/**
	 * Identificador de clasificacion para el estado VIGENTE de un vaciado.
	 */
	public static final Integer ID_ESTADO_VIGENTE = ConfigManager
			.getValueAsInteger("vaciado.estado.vigente");

	/**
	 * Identificador de clasificacion para el estado EN CURSO de un vaciado.
	 */
	public static final Integer ID_ESTADO_EN_CURSO = ConfigManager
			.getValueAsInteger("vaciado.estado.encurso");

	/**
	 * Identificador de clasificacion para los pasivos contingentes
	 */
	public static final Integer ID_CLASIFICACION_PASIVOS_CONTINGENTES = new Integer(
			3900);

	public static final String KEY_MSG_ERR_USUARIO_NO_REGISTRADO = "error.usuario.noExiste";

	// Separador de archivos
	public static final String SEPARADOR_ARCHIVOS = "file.separator";

	/*
	 * Listado de constantes que hacen referencia a los Key de
	 * mensajes.properties
	 */
	public static final String KEY_MSG_ERROR_PARSEO_ENTIDAD = "error.parseo.entidad";
	public static final String KEY_MSG_ERROR_CONVERSION_STRING_TO_DATE = "error.conversion.stringToDate";
	public static final String KEY_MSG_ERROR_CREACION_STUB_OSB = "error.creacion.stub.osb";
	public static final String KEY_MSG_ERROR_INVOCACION_OPERACION = "error.invocacion.osb";
	public static final String KEY_MSG_ERROR_SERVICIO_DAI_NO_DISPONIBLE = "error.dai.servicio.no.disponible";
	public static final String KEY_MSG_ERROR_INVOCACION_OSB_CONSULTA_MONEDA = "error.consulta.moneda.osb";
	public static final String KEY_MSG_ERROR_DATOS_INPUT_REQ_OSB = "error.input.requerido.osb";
	public static final String KEY_MSG_ERROR_CLIENTE_NO_EXISTE_FICHACHICA_OSB = "error.cliente.no.existe.osb";
	public static final String KEY_MSG_ERROR_VACIADO_YA_EXISTE = "error.vaciado.yaExiste";
	public static final String KEY_MSG_ERROR_FICHA_INVALIDA = "error.ficha.invalida";
	public static final String KEY_MSG_ERROR_REPORTE_VACIADOS_INVALIDO = "error.reporteVac.invalido";
	public static final String KEY_MSG_ERROR_ROL_PERMISO_EJEC = "error.sefeserver.rol.permiso.ejec";
	public static final String KEY_MSG_ERROR_USUARIO_PRIVILEGIOS="error.sefeserver.usuario.privilegios"; // Req:7.4.26 rating individual transversal

	// Keys Rutas de Imagenes de Reportes

	public static final String KEY_RPT_RUTA_IMAGENES = "ruta.imagenes.reportes";

	public static final String KEY_SUBRPT_DETAPROYECCION_RUTA_IMG = "ruta.imagenes.subreportes.detaProyeccion";

	public static final String KEY_SUBRPT_DETACONSCOMB_RUTA_IMG = "ruta.imagenes.subreportes.detaConsComb";
	public static final String KEY_SUBRPT_DETACONSCOMBCX_RUTA_IMG = "ruta.imagenes.subreportes.detaConsCombIFRSCX";
	public static final String KEY_SUBRPT_DETACONSCOMBLX_RUTA_IMG = "ruta.imagenes.subreportes.detaConsCombIFRSLX";

	public static final String KEY_SUBRPT_RESUMCONSCOMB_RUTA_IMG = "ruta.imagenes.subreportes.rptResumenConsolidadoCombinado";
	public static final String KEY_SUBRPT_RESUMCONSCOMBLX_RUTA_IMG = "ruta.imagenes.subreportes.rptResumenConsolidadoCombinadoIFRSLX";

	public static final String KEY_SUBRPT_PTLLATRABAJOCONSCOMB_RUTA_IMG = "ruta.imagenes.subreportes.rptPtllaTrabajoConsComb";
	public static final String KEY_SUBRPT_PTLLATRABAJOCONSCOMBCX_RUTA_IMG = "ruta.imagenes.subreportes.rptPtllaTrabajoConsCombIFRSCX";
	public static final String KEY_SUBRPT_PTLLATRABAJOCONSCOMBLX_RUTA_IMG = "ruta.imagenes.subreportes.rptPtllaTrabajoConsCombIFRSLFLX";

	public static final String KEY_SUBRPT_FICHA_RUTA_IMG = "ruta.imagenes.subreportes.ficha";
	public static final String KEY_SUBRPT_IMG_RPTVACUNOPER_RUTA_IMG = "ruta.imagenes.subreportes.rptVacUnoPer";
	public static final String KEY_SUBRPT_IMG_RPTVACMULTI_RUTA_IMG = "ruta.imagenes.subreportes.rptVacMulti";

	public static final String KEY_RPT_IMG_LOGOBCH = "img.reportes.logobch";
	public static final String KEY_SUBRPT_IMG_FICHA_1FILA_ALTO = "img.ficha.hd.1FilaAlto";
	public static final String KEY_SUBRPT_IMG_FICHA_2FILAS_ALTO = "img.ficha.hd.2FilasAlto";
	public static final String KEY_SUBRPT_IMG_FICHA_DEUDA = "img.ficha.hd.deuda";
	public static final String KEY_SUBRPT_IMG_FICHA_NOTAS = "img.ficha.hd.notas";
	public static final String KEY_SUBRPT_IMG_FICHA_TOTAL_1FILA_ALTO = "img.ficha.hd.total1FilaAlto";
	public static final String KEY_SUBRPT_IMG_FICHA_HD_INFORMACION = "img.ficha.hd.info";
	public static final String KEY_SUBRPT_IMG_FICHA_DEUDA2 = "img.ficha.hd.deuda2"; //Req. 7.2.12 Combinados/Consolidados Sprint 6

	public static final String KEY_SUBRPT_IMG_RPTVACUNOPER_10x281 = "img.rptVacUnoPer.10x281";
	public static final String KEY_SUBRPT_IMG_RPTVACUNOPER_10x572 = "img.rptVacUnoPer.10x572";
	public static final String KEY_SUBRPT_IMG_RPTVACUNOPER_PLAIN_1Ox281 = "img.rptVacUnoPer.plain_10x281";
	public static final String KEY_SUBRPT_IMG_RPTVACUNOPER_LOGOBCH = "img.rptVacUnoPer.logo";

	public static final String KEY_SUBRPT_IMG_RPTVACMULTI_10x572 = "img.rptVacMulti.10x572";
	public static final String KEY_SUBRPT_IMG_RPTVACMULTI_PLAIN_1Ox572 = "img.rptVacMulti.plain_10x572";
	public static final String KEY_SUBRPT_IMG_RPTVACMULTI_LOGOBCH = "img.rptVacMulti.logo";

	public static final String KEY_SUBRPT_IMG_RPTNEGOCIO_LOGOBCH = "img.rptRatingNegocio.logo";
	public static final String KEY_SUBRPT_IMG_RPTNEGOCIO_572x10_AZUL = "img.rptRatingNegocio.572x10.Azul";

	public static final String KEY_SUBRPT_IMG_RPTFICHARATING_LOGOBCH = "img.rptFichaRating.logo";
	public static final String KEY_SUBRPT_IMG_RPTFICHARATING_572x10_AZUL = "img.rptFichaRating.572x10.Azul";
	public static final String KEY_SUBRPT_IMG_RPTFICHARATING_572x10_GRIS = "img.rptFichaRating.572x10.Gris";
	public static final String KEY_SUBRPT_IMG_RPTFICHARATING_572x20_GRIS = "img.rptFichaRating.572x20.Gris";
	public static final String KEY_SUBRPT_IMG_RPTFICHARATING_86x30_GRIS = "img.rptFichaRating.86x30.Gris";
	public static final String KEY_SUBRPT_IMG_RPTFICHARATING_OK = "img.rptFichaRating.ok";
	public static final String KEY_SUBRPT_IMG_RPTFICHARATING_ERROR = "img.rptFichaRating.error";

	public static final String KEY_SUBRPT_IMG_RPTFINANCIEROCORPORATIVA_LOGOBCH = "img.rptFinancieroCorporativas.logo";
	public static final String KEY_SUBRPT_IMG_RPTFINANCIEROCORPORATIVA_572x10_AZUL = "img.rptFinancieroCorporativas.572x10.Azul";
	public static final String KEY_SUBRPT_IMG_RPTFINANCIEROCORPORATIVA_572x10_GRIS = "img.rptFinancieroCorporativas.572x10.Gris";

	public static final String KEY_SUBRPT_IMG_RPTFINANCIEROPYME_LOGOBCH = "img.rptFinanPyme.logo";
	public static final String KEY_SUBRPT_IMG_RPTFINANCIEROPYME_572x10_AZUL = "img.rptFinanPyme.572x10.Azul";
	public static final String KEY_SUBRPT_IMG_RPTFINANCIEROPYME_572x10_GRIS = "img.rptFinanPyme.572x10.Gris";

	public static final String KEY_SUBRPT_IMG_RPTFINANSOCIEDADINVERSION_LOGOBCH = "img.rptFinancieroSocDeInv.logo";
	public static final String KEY_SUBRPT_IMG_RPTFINANSOCIEDADINVERSION_572x10_AZUL = "img.rptFinancieroSocDeInv.572x10.Azul";
	public static final String KEY_SUBRPT_IMG_RPTFINANSOCIEDADINVERSION_572x10_GRIS = "img.rptFinancieroSocDeInv.572x10.Gris";

	public static final String KEY_SUBRPT_IMG_RPTRATINGPROYECTADO_LOGOBCH = "img.rptRatingProyectado.logo";
	public static final String KEY_SUBRPT_IMG_RPTRATINGPROYECTADO_572x10_AZUL = "img.rptRatingProyectado.572x10.Azul";
	public static final String KEY_SUBRPT_IMG_RPTRATINGPROYECTADO_572x10_GRIS = "img.rptRatingProyectado.572x10.Gris";

	public static final String KEY_SUBRPT_IMG_RPTPROYECCIONRATING_LOGOBCH = "img.rptProyeccionRating.logo";
	public static final String KEY_SUBRPT_IMG_RPTPROYECCIONRATING_192x9_AZUL = "img.rptProyeccionRating.192x9.Azul";
	public static final String KEY_SUBRPT_IMG_RPTPROYECCIONRATING_502x9_GRIS = "img.rptProyeccionRating.502x9.Gris";
	public static final String KEY_SUBRPT_IMG_RPTPROYECCIONRATING_502x9_AZUL = "img.rptProyeccionRating.502x9.Azul";
	public static final String KEY_SUBRPT_IMG_RPTPROYECCIONRATING_502x18_AZUL = "img.rptProyeccionRating.502x18.Azul";
	public static final String KEY_SUBRPT_IMG_RPTPROYECCIONRATING_502x27_AZUL = "img.rptProyeccionRating.502x27.Azul";
	public static final String KEY_SUBRPT_IMG_RPTPROYECCIONRATING_572x9_AZUL = "img.rptProyeccionRating.572x9.Azul";

	// Keys Rutas de Sub Reportes

	public static final String KEY_SUBRPT_SUB_RPTNEGOCIO_TEMA = "sub.rptRatingNegocio.tema";
	public static final String KEY_SUBRPT_SUB_RPTNEGOCIO_ALTERNATIVA = "sub.rptRatingNegocio.alternativa";

	public static final String KEY_SUBRPT_SUB_RPTFICHARATING_DEBILIDADES = "sub.rptFichaRating.Debilidades";
	public static final String KEY_SUBRPT_SUB_RPTFICHARATING_DETALLEEVALUACION = "sub.rptFichaRating.DetalleEvaluacion";
	public static final String KEY_SUBRPT_SUB_RPTFICHARATING_RELACIONADOS = "sub.rptFichaRating.Relacionados";

	public static final String KEY_SUBRPT_SUB_RPTFINANCORPORATIVA_NOTA_INDICADOR = "sub.rptFinancieroCorporativas.NotaIndicador";
	public static final String KEY_SUBRPT_SUB_RPTFINANCORPORATIVA_VALOR_INDICADOR = "sub.rptFinancieroCorporativas.ValorIndicador";
	public static final String KEY_SUBRPT_SUB_RPTFINANCORPORATIVA_RTGFINAN_UNO = "sub.rptFinancieroCorporativas.RatingFinan.Uno";
	public static final String KEY_SUBRPT_SUB_RPTFINANCORPORATIVA_RTGFINAN_DOS = "sub.rptFinancieroCorporativas.RatingFinan.Dos";

	public static final String KEY_SUBRPT_SUB_RPTFINANPYME_NOTA_INDICADOR = "sub.rptFinancieroPyme.NotaIndicador";
	public static final String KEY_SUBRPT_SUB_RPTFINANPYME_VALOR_INDICADOR = "sub.rptFinancieroPyme.ValorIndicador";
	public static final String KEY_SUBRPT_SUB_RPTFINANPYME_RTGFINAN_UNO = "sub.rptFinancieroPyme.RatingFinan.Uno";
	public static final String KEY_SUBRPT_SUB_RPTFINANPYME_RTGFINAN_DOS = "sub.rptFinancieroPyme.RatingFinan.Dos";

	public static final String KEY_SUBRPT_SUB_RPTFINANSOCIEDADINVERSION_NOTA_INDICADOR = "sub.rptFinancieroSocDeInv.NotaIndicador";
	public static final String KEY_SUBRPT_SUB_RPTFINANSOCIEDADINVERSION_VALOR_INDICADOR = "sub.rptFinancieroSocDeInv.ValorIndicador";
	public static final String KEY_SUBRPT_SUB_RPTFINANSOCIEDADINVERSION_RTGFINAN_UNO = "sub.rptFinancieroSocDeInv.RatingFinan.Uno";
	public static final String KEY_SUBRPT_SUB_RPTFINANSOCIEDADINVERSION_RTGFINAN_DOS = "sub.rptFinancieroSocDeInv.RatingFinan.Dos";
	public static final String SP_BUSCAR_VAC_SIGUIENTE_PERIODO_MISMO_PLAN_CTA = "Vaciado.SP_BUSCAR_VAC_SIGUIENTE_PERIODO_MISMO_PLAN_CTA";
	public static final String KEY_SUBRPT_SUB_RPTRATINGPROYECTADO_NOTA_INDICADOR = "sub.rptRatingProyectado.NotaIndicador";
	public static final String KEY_SUBRPT_SUB_RPTRATINGPROYECTADO_VALOR_INDICADOR = "sub.rptRatingProyectado.ValorIndicador";
	public static final String KEY_SUBRPT_SUB_RPTRATINGPROYECTADO_RTGFINAN_UNO = "sub.rptRatingProyectado.RatingFinan.Uno";
	public static final String KEY_SUBRPT_SUB_RPTRATINGPROYECTADO_RTGFINAN_DOS = "sub.rptRatingProyectado.RatingFinan.Dos";

	public static final String KEY_DIR_SUBRPT_PROYECCIONPARARATING = "ruta.subreportes.rptProyeccionRating";

	public static final String KEY_DIR_SUBRPT_DETALLECONSOLIDADOCOMBINAD_CHGAAP = "ruta.subreportes.rptDetalleConsolidadoCombinado";
	public static final String KEY_DIR_SUBRPT_DETALLECONSOLIDADOCOMBINAD_IFRSCX = "ruta.subreportes.rptDetalleConsolidadoCombinadoIFRSCX";
	public static final String KEY_DIR_SUBRPT_DETALLECONSOLIDADOCOMBINAD_IFRSLX = "ruta.subreportes.rptDetalleConsolidadoCombinadoIFRSLX";
	public static final String KEY_DIR_SUBRPT_RESUMENCONSOLIDADOCOMBINADO = "ruta.subreportes.rptResumenConsolidadoCombinado";
	public static final String KEY_DIR_SUBRPT_RESUMENCONSOLIDADOCOMBINADO_IFRSLX = "ruta.subreportes.rptResumenConsolidadoCombinadoIFRSLX";
	public static final String KEY_DIR_SUBRPT_PTLLATRABAJOCONSOLIDADOCOMBINAD_CHGAAP = "ruta.subreportes.rptPtllaTrabajoConsolidadoCombinado";
	public static final String KEY_DIR_SUBRPT_PTLLATRABAJOCONSOLIDADOCOMBINAD_IFRSCX = "ruta.subreportes.rptPtllaTrabajoConsolidadoCombinadoIFRSCX";
	public static final String KEY_DIR_SUBRPT_PTLLATRABAJOCONSOLIDADOCOMBINAD_IFRSLX = "ruta.subreportes.rptPtllaTrabajoConsolidadoCombinadoIFRSLX";

	public static final String KEY_DIR_SUBRPT_INDICESALERTAS_VACIADOS_TABLA_HEADER = "sub.rptIndicesAlertas.vaciadoTabla";
	
	public static final String KEY_SUBRPT_IMG_RPTINDICESALERTAS_OK = "img.rptIndicesAlertas.ok";
	public static final String KEY_SUBRPT_IMG_RPTINDICESALERTAS_ALERTA = "img.rptIndicesAlertas.alerta";
	public static final String KEY_SUBRPT_IMG_RPTINDICESALERTAS_ERROR = "img.rptIndicesAlertas.error";
	public static final String KEY_SUBRPT_IMG_RPTINDICESALERTAS_BLANK = "img.rptIndicesAlertas.blank";
	public static final String KEY_SUBRPT_IMG_RPTINDICESALERTAS_GUION = "img.rptIndicesAlertas.guion";
	
	public static final String KEY_DIR_SUBRPT_DETALLEPROYECCION = "ruta.subreportes.rptDetalleProyeccion";
	public static final String KEY_DIR_SUBRPT_RESUMENPROYECCION = "ruta.subreportes.rptResumenProyeccion";
	
	// sprint 4 req:7.1.13 reporte de rating individual
	public static final String KEY_DIR_SUBRPT_RTG_INDIVIDUAL_DETALLE_RATING = "sub.rptRatingIndividual.detalleRating";
	public static final String KEY_DIR_SUBRPT_RTG_INDIVIDUAL_DETALLE_FINACIERO_PROYECTADO = "sub.rptRatingIndividual.detalleFinacieroProyectado";

	public static final String MSG_CLIENTE_SIN_INFORMACION = "mensaje.cliente.sin.info";

	public static final String PIPELINE = "|";
	public static final String DOS_PUNTOS = ":";
	public static final String PUNTO = ".";
	public static final String ESPACIO_VACIO = "";
	public static final String STR_GUION = "-";
	public static final String SLASH = "/";
	public static final String ASTERISCO = "*";
	public static final String MENOR_QUE = "<";
	public static final String MAYOR_QUE = ">";
	public static final String UNDERLINE = "_";

	public static final char CHR_GUION = '-';

	public static final String TXT_USUARIO = "Usuario";
	public static final String TXT_ROL = "Rol";
	public static final String TXT_OPERACION = "Id Operacion";

	public static final String TXT_PROXY = "proxy";
	public static final String TXT_ENDPOINT = "endpoint";

	public static final int INDEX_HOJA_XLS_FECU = 0;
	public static final int INDEX_COL_CLAVE_FECU = 0;
	public static final int INDEX_COL_VALOR_FECU = 2;

	public static final int INDEX_COL_CLAVE_PL = 1;
	public static final int INDEX_COL_VALOR_PL = 4;

	/**
	 * CodigoApp ficticio utilizado para identificar al Servicio EJB de Catalogo
	 * General.<br>
	 * Esto se realiza ya que funcionalmente el EJB de CatalogoGeneral no esta
	 * ligado a ningun m&oacute;dulo funcional.
	 */
	public static final String CODIGO_APP_CATALOGO_GENERAL = "CATALOGO";

	// Procedmientos almacenados
	public static final String SP_SELECT_VALORES_CUENTAS_APERTURABLES = "Vaciado.SP_SELECT_VAL_CTAS_APERTURABLE1";
	public static final String SP_VACIADO_AJUSTAR_CUENTA = "Vaciado.sp_ajustarCuenta";
	public static final String SP_VACIADO_UPDATE_VACIADO = "Vaciado.sp_updateVaciado";
	public static final String SP_VACIADO_INSERT_VACIADO = "Vaciado.sp_insertVaciado";
	public static final String SP_VACIADO_SELECT_VACIADO_POR_ID = "Vaciado.sp_selectVaciadoPorId";
	public static final String SP_VACIADO_SELECT_VACIADO_POR_ID_STR = "Vaciado.sp_selectVaciadoPorIdString";
	public static final String SP_VACIADO_SELECT_VACIADOS = "Vaciado.sp_selectVaciados";
	public static final String SP_VACIADO_SELECT_VACIADOS_STR = "Vaciado.sp_selectVaciadosString";
	public static final String SP_VACIADO_SELECT_VACIADOS_BCH_SEFE = "Vaciado.SP_SELECTVACIADOSBCHSEFE";
	public static final String SP_VACIADO_SELECT_CUENTAS_VACIADOS_POR_ID = "Vaciado.sp_selectCuentasVaciadosPorId";
	public static final String SP_VACIADO_SELECT_ENCABEZADO = "Vaciado.sp_selectEncabezado";
	public static final String SP_VACIADO_SELECT_CTAS_ENCABEZADO = "Vaciado.sp_selectCtasEncabezado";
	public static final String SP_VACIADO_BUSCAR_ANTERIOR = "Vaciado.sp_buscarVaciadoAnterior";
	public static final String SP_SEL_VAC_ANT_DET_CTA = "Vaciado.SP_SEL_VAC_ANT_DET_CTA";
	public static final String SP_VACIADO_BUSCAR_EQUIVALENTE = "Vaciado.sp_buscarVaciadoEquivalente";
	public static final String SP_BORRAR_VACIADOS = "Vaciado.sp_borrar_vaciados";
	public static final String SP_PL_ACTUALIZAR_ESTADO_PL = "ProyLarga.SP_PL_ACTUALIZAR_ESTADO_PL";
	public static final String SP_IMD_BUSCAR_HOJA_INDEPENDIENTE = "HojaIndependiente.SP_IMD_BUSCAR_HOJA_INDEP";
	public static final String SP_IMD_BUSCAR_INDICADOR = "HojaIndependiente.SP_IMD_BUSCAR_CUENTA_BANCO";
	public static final String SP_IMD_BUSCAR_CALIFICADORAS = "HojaIndependiente.SP_IMD_BUSCAR_CALIF_RIESGO";
	public static final String SP_IMD_CONFIRMAR_HOJA_INDEPENDIENTE = "HojaIndependiente.SP_IMD_CONFIRMAR_HOJA_INDEP";
	public static final String SP_IMD_INSERT_HOJA_INDEPENDIENTE = "HojaIndependiente.SP_IMD_INSERT_HOJA_INDEP";
	public static final String SP_IMD_INSERT_CUENTA_BANCO = "HojaIndependiente.SP_IMD_INSERT_CUENTA_BANCO";
	public static final String SP_IMD_INSERT_CUENTA_CALIF_RIESGO = "HojaIndependiente.SP_IMD_INSERT_CALIF_RGO";
	public static final String SP_IMD_OBT_CALIF_RGO_POR_TIPO = "HojaIndependiente.SP_IMD_OBT_CALIF_RGO_POR_TIPO";
	public static final String SP_IMD_OBT_IND_HOJA_BANCO = "HojaIndependiente.SP_IMD_OBT_IND_HOJA_BANCO";
	public static final String SP_IMD_UPDATE_HOJA_INDEPENDIENTE = "HojaIndependiente.SP_IMD_UPDATE_HOJA_INDEPENDIENTE";
	public static final String SP_IMD_OBT_CALIF_RGO_IMD = "HojaIndependiente.SP_IMD_OBT_CALIF_RGO_IMD";
	public static final String SP_IMD_OBT_LST_CALIF_RGO_IMD = "HojaIndependiente.SP_IMD_OBT_LST_CALIF_RGO_IMD";
	public static final String SP_IMD_OBT_LST_IND_HOJA_BANCO = "HojaIndependiente.SP_IMD_OBT_LST_IND_HOJA_BANCO";
	public static final String SP_IMD_SELECT_HOJA_INDEP_FILTR = "HojaIndependiente.SP_IMD_SELECT_HOJA_INDEP_FILTR";
	public static final String SP_IMD_SELECT_HOJA_INDEP_ID = "HojaIndependiente.SP_IMD_SELECT_HOJA_INDEP_ID";

	public static final String SP_IMD_OBT_HOJA_INDEP_ID_RATING = "HojaIndependiente.SP_IMD_OBT_HOJA_INDEP_ID_RATING";
	public static final String SP_IMD_HOJA_INDEPENDIENTE = "HojaIndependiente.SP_IMD_OBT_CALIF_RGO_POR_TIPO";
	public static final String SP_BUSCAR_CUENTA_POR_CODIGO = "Cuenta.sp_buscarCuentaPorCodigo";
	public static final String SP_OBTENER_REL_CTA_RPT_CTA = "Cuenta.SP_OBTENER_REL_CTA_RPT_CTA";
	public static final String SP_OBTENER_CTA_VACIADO_TPO_CTA = "Cuenta.SP_OBTENER_CTA_VACIADO_TPO_CTA";

	public static final String SP_BUSCAR_ID_CUENTAS_SEGMENTO = "Cuenta.sp_buscar_ctas_segmento";
	public static final String SP_BUSCAR_VACIADO_CONS_VIG_BANCO = "Vaciado.sp_buscar_vaciado_cons_vig_banco";
	public static final String SP_SELECT_VALORES_SEGMENTO = "Vaciado.sp_select_vals_sgm";
	public static final String SP_INSERT_VALORES_SEGMENTO = "Vaciado.sp_insert_vals_sgm";
	public static final String SP_DELETE_VALORES_SEGMENTO = "Vaciado.sp_delete_vals_sgm";
	public static final String SP_SELECT_VALORES_INFO_ADICIONAL = "Vaciado.sp_select_info_adc";
	public static final String SP_INSERT_VALORES_INFO_ADICIONAL = "Vaciado.sp_insert_info_adc";
	public static final String SP_DELETE_VALORES_INFO_ADICIONAL = "Vaciado.sp_delete_info_adc";
	public static final String SP_SELECT_MERCADO_DESTINO_VENTA = "Vaciado.sp_select_vta_mdo_dest";
	public static final String SP_INSERT_MERCADO_DESTINO_VENTA = "Vaciado.sp_insert_vta_mdo_dest";
	public static final String SP_DELETE_MERCADO_DESTINO_VENTA = "Vaciado.sp_delete_vta_mdo_dest";
	public static final String SP_SELECT_ESTRUCTURA_COSTOS = "Vaciado.sp_select_estrtra_de_ctos";
	public static final String SP_INSERT_ESTRUCTURA_COSTOS = "Vaciado.sp_insert_estrtra_de_ctos";
	public static final String SP_DELETE_ESTRUCTURA_COSTOS = "Vaciado.sp_delete_estrtra_de_ctos";
	public static final String SP_SELECT_VALORES_ESPECIFICOS = "Vaciado.sp_select_vals_espec";
	public static final String SP_INSERT_VALORES_ESPECIFICOS = "Vaciado.sp_insert_vals_espec";
	public static final String SP_DELETE_VALORES_ESPECIFICOS = "Vaciado.sp_delete_vals_espec";
	public static final String SP_UPDATE_RESPONSABLE_VAC = "Vaciado.SP_ACTUALIZAR_VAC_USURESP_ID"; // Requerimiento 7.4.40
	public static final String SP_BUSCAR_RTGFINAL_FINAN = "Vaciado.SP_BUS_RTG_FINAL_RTGFINAN_VAC"; // Requerimiento 7.4.29 Sprint 4

	public static final String SP_BUSCAR_CUENTA_POR_ID = "Cuenta.sp_buscarCuentaPorId";
	public static final String SP_BUSCAR_VALIDACIONES_CRUZADAS = "Alerta.sp_buscarValidacionesCruzadas";
	public static final String SP_BUSCAR_ALERTAS_INDICADORES = "Alerta.sp_buscarAlertasIndicadores";
	public static final String SP_BUSCAR_INDICADORES_POR_ALERTA = "Alerta.sp_buscarIndicadoresPorAlerta";
	public static final String SP_PARAM_SSECT = "Alerta.sp_ParamSSect";
	public static final String SP_BUSCAR_CUENTA_ALERTA = "Alerta.sp_buscarCuentaAlerta";
	public static final String SP_BUSCAR_CUENTA_ALERTA_POR_INDICADOR = "Alerta.sp_buscarCuentaAlertaPorIndicador";

	public static final String SP_SEL_MENSAJE_X_ALERT_Y_NOTA = "Alerta.sp_selMensajeXAlertYNota";

	public static final String SP_SELECT_PROBABILIDADES_DEFAULT = "Rating.sp_sel_probabilidad_default";

	public static final String SP_CLIENTE_SELECT_CLIENTE_POR_RUT = "Cliente.sp_selectClientePorRut";
	public static final String SP_CLIENTE_OBTENER_CLASIF_ID_SIEBEL = "Cliente.sp_obtenerClasifIdSiebel";
	public static final String SP_CLIENTE_INSERTAR_CLIENTE_EMPRESA = "Cliente.sp_insertarParteInvolucrada";
	public static final String SP_CLIENTE_ACTUALIZAR_CLIENTE_EMPRESA = "Cliente.sp_actualizarParteInvolucrada";
	public static final String SP_CLIENTE_INSERTAR_CLIENTE_PERSONA = "Cliente.sp_insertarPersona";
	public static final String SP_CLIENTE_ACTUALIZAR_CLIENTE_PERSONA = "Cliente.sp_actualizarPersona";

	public static final String SP_PASIVOSCONTINGENTES_OBTENER_PASIVOS_CONTINGENTES = "PasivosContingentes.sp_selectPasivosContingentes";
	public static final String SP_PASIVOSCONTINGENTES_INSERTAR_PASIVOS_CONTINGENTES = "PasivosContingentes.sp_insertarPasivosContingentes";
	public static final String SP_PASIVOSCONTINGENTES_ACT_PASIVOS_CONTINGENTES = "PasivosContingentes.sp_actualizarPasivosContingentes";

	public static final String SP_VACIADO_SELECT_NOTAS_VACIADOS_POR_ID = "Vaciado.sp_selectNotasVaciadosPorId";
	public static final String SP_VACIADO_SELECT_CTAS_RECONCILIACION_VAC_POR_ID = "Vaciado.sp_selectCuentasReconciliacionVaciadosPorId";
	public static final String SP_VACIADO_GUARDAR_CUENTAS = "Vaciado.sp_GuardarCuentas";
	public static final String SP_VACIADO_AJUSTAR_VACIADO = "Vaciado.sp_ajustarVaciado";
	public static final String SP_VACIADO_GUARDAR_DET_CUENTAS = "Vaciado.SP_VACIADO_GUARDAR_DET_CUENTAS";

	public static final String SP_VACIADO_SELECT_ID_VACIADO_ANTERIOR = "Vaciado.sp_selectIdVacAnterior";
	public static final String SP_VACIADO_BUSCAR_VACIADO_EXISTENTE = "Vaciado.sp_buscarVaciadoExistente";
	public static final String SP_VACIADO_BUSCAR_VACIADO_EXISTENTE_SIN_MONEDA = "Vaciado.sp_buscarVaciadoExistentesMon";
	public static final String SP_VACIADO_BUSCAR_ANTERIOR_ORG = "Vaciado.sp_buscarVaciadoAnteriorOrg";
	public static final String SP_VACIADO_BUSCAR_ULTIMO_VACIADO_CLIENTE = "Vaciado.sp_buscar_ultimo_vaciado_cliente";
	public static final String SP_SELECT_VAL_CTAS_APERTURABLE = "Vaciado.SP_SELECT_VAL_CTAS_APERTURABLE";
	public static final String SP_SELECT_CUENTA_REPORTE = "Vaciado.SP_SELECT_CUENTA_REPORTE";

	public static final String SP_SELECT_VAL_DET_CTAS_APERT = "Vaciado.SP_SELECT_VAL_DET_CTAS_APERT";
	public static final String SP_AGREGAR_MODIFICAR_NOTA_DET_CTAS = "Vaciado.SP_AGREGAR_MOD_NOTA_DET_CTAS";
	public static final String SP_GUARDAR_DET_CUENTAS = "Vaciado.SP_GUARDAR_DET_CUENTAS";
	public static final String SP_SELECT_INVERSION_SOC = "Vaciado.SP_SELECT_INVERSION_SOC";
	public static final String SP_BUSCAR_NOTA_DETALLE_CTAS = "Vaciado.SP_BUSCAR_NOTA_DETALLE_CTAS";
	public static final String SP_VACIADO_BUSCAR_ULTIMO_VACIADO_VIGENTE = "Vaciado.SP_VACIADO_BUSCAR_ULTIMO_VACIADO_VIGENTE";
	public static final String SP_VACIADO_BUSCAR_VACIADO_VIGENTE_O_EN_CURSO = "Vaciado.SP_VACIADO_BUSCAR_VACIADO_VIGENTE_O_EN_CURSO";
	public static final String SP_AGREGAR_RELACION_VACIADO_CONS = "Vaciado.SP_AGREGAR_REL_VACIADO_CONS";
	public static final String SP_ELIMINAR_RELACION_VACIADO_CONS = "Vaciado.SP_ELIMINAR_REL_VACIADO_CONS";
	public static final String SP_BUSCAR_REL_VACIADOS = "Vaciado.SP_BUSCAR_REL_VACIADOS";
	public static final String SP_BUSCAR_VAC_CONS_POR_ID = "Vaciado.SP_BUSCAR_VAC_CONS_POR_ID";
	public static final String SP_BUSCAR_VAC_SIGUIENTE_PERIODO = "Vaciado.SP_BUSCAR_VAC_SIGUIENTE_PERIODO";
	public static final String SP_BUSCAR_VAC_SIGUIENTE_PERIODO_DIFERENTE_PLAN_CTA = "Vaciado.SP_BUSCAR_VAC_SIGUIENTE_PERIODO_DIFERENTE_PLAN_CTA";
	public static final String SP_DUMMY_BUSCAR_VAC_SIGUIENTE_PERIODO_DIFERENTE_PLAN_CTA = "Vaciado.SP_DUMMY_BUSCAR_VAC_SIGUIENTE_PERIODO_DIFERENTE_PLAN_CTA";
	public static final String SP_SELECT_RESPONSABLE_DET_CTAS = "Vaciado.SP_SELECT_RESPONSABLE_DET_CTAS";
	public static final String SP_AGREGAR_MODIFICAR_INVERSION_SOCIEDAD = "Vaciado.SP_AGREGAR_MOD_INVERSION_SOC";
	public static final String SP_AGREGAR_MOD_NOTAS_GRAL_E_INVERSIONES = "Vaciado.SP_AGREGAR_MOD_NOTAS_GRAL_INV";
	public static final String SP_BUSCAR_DETALLE_CTAS = "Vaciado.SP_BUSCAR_DETALLE_CTAS";
	public static final String SP_VACIADO_GUARDAR_NOTA = "Vaciado.sp_guardarNota";
	public static final String SP_VACIADO_CONSULTAR_NOTA = "Vaciado.sp_consultarNota";
	public static final String SP_VACIADO_ACTUALIZAR_NOTA = "Vaciado.sp_actualizarNota";
	public static final String SP_COMUN_OBTENER_CLASIFICACIONES_POR_CATEGORIA = "Comun.sp_selectClasifPorCategoria";
	public static final String SP_COMUN_OBTENER_CLASIFICACIONES_POR_CATEG_VRS = "Comun.sp_selectClasifPorCategVrs";
	public static final String SP_COMUN_SELECT_CLASIFICACION_POR_NOMBRE_MODULO_PLAN_CTA = "Comun.sp_sel_clasif_x_nom_mod_plncta";
	public static final String SP_BUSCAR_INDICADORES = "Comun.SP_BUSCAR_INDICADORES";
	public static final String SP_COMUN_OBTENER_CLASIFICACION_POR_ID = "Comun.sp_selectClasifPorId";
	public static final String SP_COMUN_GUARDAR_CLASIF = "Comun.sp_actualizar_clasif";
	public static final String SP_COMUN_CREAR_CLASIF = "Comun.sp_crear_clasif";
	public static final String SP_COMUN_CREAR_CLASIF_SIEBEL = "Comun.sp_crear_clasif_siebel";
	public static final String SP_COMUN_ELIMINAR_CLASIF = "Comun.sp_eliminar_clasif";
	public static final String SP_COMUN_OBTENER_EMPRESAS_RELACIONADAS_POR_TIPO = "Comun.sp_selectPartesInvolPorTipoRel";
	public static final String SP_COMUN_BUSCAR_EMPRESAS_POR_TIPO_RELACION = "Comun.SP_BUSCAR_EMPRS_X_TIPO";
	public static final String SP_COMUN_CONSULTA_MAPEO_CUENTAS = "Comun.sp_consultaMapeoCuentas";
	public static final String SP_COMUN_OBTENER_OPERACIONES_POR_ROL = "Comun.sp_obtener_operaciones_por_rol";
	public static final String SP_CUENTA_BUSCAR_CUENTAS_VACIADO = "Cuenta.sp_buscarCuentasVaciado";
	public static final String SP_CUENTA_BUSCAR_CUENTAS_VACIADO_CONS = "Cuenta.sp_buscarCuentasVaciadoCons";
	public static final String SP_CUENTA_BUSCAR_CUENTAS_HIBRIDAS_VACIADO = "Cuenta.SP_CUENTA_BUSCAR_CUENTAS_HIBRIDAS_VACIADO";

	public static final String SP_CUENTA_ACTUALIZAR_CUENTA_VACIADO = "Cuenta.sp_actualizarCuentaVaciado";
	public static final String SP_CUENTA_OBTENER_RELACION_TPO_BALANCE_PLAN_CTAS = "Cuenta.sp_obtenerRelTpoBlcePlanCtas";
	public static final String SP_BUSCAR_CUENTAS_APERTURABLES = "Cuenta.SP_BUSCAR_CUENTAS_APERTURABLES";
	public static final String SP_BUSCAR_CUENTAS_APERT = "Cuenta.SP_BUSCAR_CUENTAS_APERT";
	public static final String SP_CUENTA_BORRAR_VALOR_DET_CTAS = "Cuenta.SP_BORRAR_VALOR_DET_CTAS";
	public static final String SP_COMUN_OBTENER_CLASIF_POR_CATEGORIA_NOMBRE = "Comun.sp_selectClasifPorCategNombre";
	public static final String SP_COMUN_OBTENER_CLASIF_POR_CATEGORIA_NOMBRE_PLANTILLA = "Comun.sp_selectClasifPorCategNombrePlantilla";
	public static final String SP_COMUN_OBTENER_CLASIF_POR_CATEGORIA_GRUPO_NOMBRE_PLANTILLA = "Comun.sp_selectClasifPorCategGrupNombrePlantilla";
	public static final String SP_VACIADO_INSERTAR_TIPO_CAMBIO_BALANCE = "Vaciado.sp_insertarTipoCambioBlce";
	public static final String SP_VACIADO_ACTUALIZAR_TIPO_CAMBIO_BALANCE = "Vaciado.sp_actualizarTipoCambioBlce";
	public static final String SP_CONSULTA_DEUDA_CLIENTE = "Cliente.sp_consulta_deuda_cliente";
	public static final String SP_CONSULTA_ULTIMA_DEUDA_CLIENTE = "Cliente.sp_consulta_ultima_deuda_cte";
	public static final String SP_CONSULTA_ULTIMA_DEUDA_CLIENTE_PERIODO = "Cliente.sp_consulta_ult_deuda_cte_per";
	public static final String SP_CONSULTA_DEUDA_CLIENTE_PERIODO = "Cliente.sp_consulta_deuda_cliente_periodo";
	public static final String SP_RTG_BUSCAR_RANGO_NOTA_POR_ID = "Rating.sp_rtg_busc_rgo_nota_x_id";
	public static final String SP_RTG_ADD_FORTALEZA_DEBILIDAD = "Rating.sp_rtg_add_fort_debl";
	public static final String SP_RTG_BORRAR_FORTALEZAS_DEBILIDADES = "Rating.sp_rtg_borrar_fort_debl";
	public static final String SP_RTG_GET_FORTALEZAS_DEBILIDADES_X_AMBITO = "Rating.sp_rtg_get_fort_debl_x_ambito";
	public static final String SP_RTG_BUSCAR_RTG_INDIVIDUAL_X_RTG_NEGOCIO = "Rating.sp_rtg_busc_rtg_ind_x_neg";
	public static final String SP_RTG_BUSCAR_RTG_INDIVIDUAL_X_RTG_COMPORTAMIENTO = "Rating.sp_rtg_busc_rtg_ind_x_comp";
	public static final String SP_RTG_BUSCAR_RTG_INDIVIDUAL_X_RTG_PROYECTADO = "Rating.sp_rtg_busc_rtg_ind_x_proy";
	public static final String SP_DEL_RATING_GRUPAL_PYME_X_PK = "Rating.sp_del_rtg_gp_pyme";
	public static final String SP_CONSULTA_FECHA_HOY = "Rating.SP_CONSULTA_FECHA_HOY";
	public static final String SP_VINCULAR_RTG_INDIVIDUAL_RTG_GRUPAL = "Rating.sp_vincular_rtg_ind_rtg_grp";			//sprint 2 req 7.1.4 alinear y borrar rating en curso
	public static final String SP_BUSCAR_MODIND_X_RTGGRUP = "Rating.SP_BUSCAR_MODIND_X_RTGGRUP";	
	public static final String SP_MAX_PERIODO_DEUDA = "Cliente.sp_max_periodo_deuda";

	public static final String SP_CONSULTA_USUARIO_POR_ID = "Cliente.sp_select_usuario_por_id";
	public static final String SP_CONSULTA_PARTE_INVOL_POR_ID = "Cliente.sp_select_parte_involucrada_por_id";

	public static final String SP_CONS_CORRE_X_TIPO_FILIAL = "Cliente.SP_CONS_CORRE_X_TIPO_FILIAL";

	public static final String SP_COMUN_CREAR_USUARIO = "Comun.sp_crearUsuario";
	public static final String SP_COMUN_ACTUALIZAR_USUARIO = "Comun.sp_actualizarUsuario";

	public static final String SP_COMUN_BUSCAR_NUEVO_ORDEN_CLASIFICACION = "Comun.sp_busc_orden_para_clasif";

	public static final String SP_BUSCAR_PARAMS_FORTALEZA_DEBILIDAD_POR_BANCA = "Rating.sp_rtg_get_conf_fort_deb_x_bca";

	public static final String SP_BUSCAR_RATING_INDIVIDUAL_POR_CLIENTE = "Rating.sp_buscar_rtg_ind_cliente";
	public static final String SP_BUSCAR_RATING_GRUPAL_PYME = "Rating.sp_buscar_rtg_grupal_pyme";
	public static final String SP_BUSCAR_RATING_GRUPAL_NO_PYME = "Rating.sp_buscar_rtg_grupal_no_pyme";
	public static final String SP_BUSCAR_RATING_GRUPAL_POR_CLIENTE = "Rating.sp_obtenerRatingGrupal";
	public static final String SP_BUSCAR_RATING_FINANCIERO_POR_CLIENTE = "Rating.sp_obtenerRatingsFinancieros";
	public static final String SP_BUSCAR_RATING_PROYECTADO_POR_CLIENTE = "Rating.sp_obtenerRatingsProyectados";
	public static final String SP_BUSCAR_RATING_NEGOCIO_POR_CLIENTE = "Rating.sp_obtenerRatingsNegocios";
	public static final String SP_BUSCAR_RATING_COMPORTAMIENTO_POR_CLIENTE = "Rating.sp_obtenerRatingsComportamientos";
	public static final String SP_BUSCAR_RATING_COMPORTAMIENTO_POR_ID = "Rating.sp_obtenerRatingComportamientoId";
	public static final String SP_ACTUALIZAR_RATING_COMPORTAMIENTO_POR_ID = "Rating.sp_rtg_act_comportamiento_id";
	public static final String SP_OBTENER_INDIVIDUAL_INFOMACION_Y_DETALLE = "Rating.sp_buscarIndInfoYDetalle";
	public static final String SP_OBTENER_RATING_ANTERIOR = "Rating.sp_obtenerRatingAnterior";
	public static final String SP_OBTENER_FORTALEZA_DEBILIDADES = "Rating.sp_obtenerFortalezaDebilidad";
	public static final String SP_BUSCAR_RELACIONADO_RPT_PYME = "Rating.sp_buscarRelacionadoRpt";
	public static final String SP_BUSCAR_RELACIONADO_RPT_NO_PYME = "Rating.sp_buscarRelacionadoNoPymeRpt";
	public static final String SP_BUSCAR_RELACIONADO_PYME_MALLA = "Rating.sp_buscarRelacionadoPymeMalla";
	public static final String SP_BUSCAR_RELACIONADO_GGEE = "Rating.sp_buscarRelacionadoGGEE";
	public static final String SP_RTG_BUSCAR_VACIADO_PYME = "Rating.sp_ratingBuscarVaciadoPyme";
	public static final String SP_RTG_BUSCAR_VACIADO_GGEE_SOCIEDADES_INVERSIONES = "Rating.sp_ratingBuscarVaciadoGrandesEmpresasYSocInversiones";
	public static final String SP_BUSCAR_PONDERACION_NIVEL_VENTAS = "Rating.sp_buscarPonderaconNivelVetas"; 
	public static final String SP_BUSCAR_COMPONENTES_ORDENADOS = "Rating.SP_BUSCAR_COMPONENTES_ORDENADOS";
	public static final String SP_BUSCAR_SEGMENTO = "Rating.sp_buscarSegmento";
	public static final String SP_OBT_SGM_BANCA_ID_FECHA = "Rating.SP_OBT_SGM_BANCA_ID_FECHA";
	public static final String SP_INSERTAR_CARITAS_SEMANAL = "Rating.sp_insertarCaritasSemanal";
	public static final String SP_OBTENER_SEGMENTO = "Rating.sp_obtenerSegmento";
	public static final String SP_OBTENER_SEGMENTO_POR_ID = "Rating.sp_obtenerSegmentoporid";
	public static final String SP_BUSCAR_RATING_NEGOCIO_VIGENTE = "Rating.sp_buscarRatingNegocioVigente";
	public static final String SP_BUSCAR_MATRIZ_NEGOCIO = "Rating.sp_buscarMatrizNegocio";
	public static final String SP_BUSCAR_HISTORICO6M = "Rating.sp_buscarHistorico6m";
	public static final String SP_BUSCAR_TEMA_NEGOCIO = "Rating.sp_buscarTemaNegocio";
	public static final String SP_BUSCAR_PREGUNTA_NEGOCIO = "Rating.sp_buscarPreguntaNegocio";
	public static final String SP_BUSCAR_MATRIZ_COMPORTAMIENTO = "Rating.sp_buscarMatrizComportamiento";
	public static final String SP_BUSCAR_ALTERNATIVA_NEGOCIO = "Rating.sp_buscarAlternativaNegocio";
	public static final String SP_BUSCAR_ALTERNATIVA_X_PREG = "Rating.SP_BUSCAR_ALTERNATIVA_X_PREG";
	public static final String SP_BUSCAR_ALTERNATIVA_NEGOCIO_POR_PK = "Rating.sp_buscarAlternativaNegocioPk";
	public static final String SP_INSERTAR_RATING_NEGOCIO = "Rating.sp_insertarRatingNegocio";
	public static final String SP_ACTUALIZAR_RATING_CON_RATING_NEGOCIO = "Rating.sp_actualizarRatingConRatingNegocio";
	public static final String SP_BUSCAR_RATING_NEGOCIO_POR_ID = "Rating.sp_buscarRatingNegocioPorId";
	public static final String SP_BUSCAR_MATRIZ_NEGOCIO_POR_ID = "Rating.sp_buscarMatrizNegocioPorId";
	public static final String SP_OBTENER_CLASIFICACIONES_RIESGO = "Rating.sp_obtenerClasificacionesRiesgo";
	public static final String SP_OBTENER_CLASIFICACIONES_RIESGO_HISTORICO = "Rating.sp_obtenerClasificacionesRiesgoHistorico";
	public static final String SP_BUSCAR_CLASIFICACION_POR_ID = "Rating.sp_buscarClasificacionPorId";
	public static final String SP_RTG_CONFIRMAR_RATING_GARANTE = "Rating.sp_confirmarRatingGarante";
	public static final String SP_RTG_CONSULTAR_RATING_GARANTE = "Rating.sp_consultarRatingGarante";
	public static final String SP_RTG_CALCULAR_RATING_GARANTE = "Rating.sp_calcularRatingGarante";
	public static final String SP_RTG_CREAR_RATING_GARANTE = "Rating.sp_rtg_crear_rating_garante";
	public static final String SP_RTG_BUSCAR_GARANTE_VIGENTE = "Rating.sp_rtg_buscar_garante_vigente";
	public static final String SP_RTG_ACTUALIZAR_GARANTE_INDIVIDUAL = "Rating.sp_rtg_actualizar_garante_individual";
	public static final String SP_RTG_BUSCAR_PREMIO_PATRIMONIO_POR_SEGMENTO = "Rating.sp_rtg_buscar_prem_patr_x_seg";
	public static final String SP_RTG_BUSCAR_EQUIVALENCIAS_RATING = "Rating.sp_rtg_buscar_equiv_rtg";
	public static final String SP_RTG_BUSCAR_MATRIZ_FINANCIERA_POR_ID = "Rating.sp_buscarMatrizFinancieraPorId";
	public static final String SP_RTG_ACTUALIZAR_RATING_FINANCIERO = "Rating.sp_rtg_update_rtg_financiero";
	public static final String SP_CONSULTA_DEUDA_CLIENTE_HASTA = "Cliente.sp_consulta_deuda_cliente_hasta";
	public static final String SP_RTG_BUSCAR_EVALUACIONES_FINANCIERAS_POR_RTG_FINANCIERO = "Rating.sp_rtg_buscar_eval_finan_x_rtg";
	public static final String SP_RTG_BUSCAR_VACIADOS_ANTERIORES_GENERICO = "Rating.sp_rtg_finan_vac_ant_generico";
	public static final String SP_RTG_BUSCAR_RELACIONADOS_GRUPO_PYME = "Rating.sp_rtg_busc_reldos_grp_pyme";
	public static final String SP_RTG_BUSCAR_RELACIONADOS_GRUPO_NO_PYME = "Rating.sp_rtg_busc_reldos_grp_nopyme";
	public static final String SP_RTG_AGREGAR_NUEVO_GRUPO_NO_PYME = "Rating.sp_rtg_add_nuevo_grp_nopyme";
	public static final String SP_RTG_AGREGAR_NUEVO_RTG_GRUPO_PYME = "Rating.sp_rtg_add_rtg_grp_pyme";
	public static final String SP_RTG_AGREGAR_NUEVO_RTG_GRUPO_NO_PYME = "Rating.sp_rtg_add_rel_gp_rtg";
	public static final String SP_RTG_BUSCAR_RELACIONADO_GRUPO_PYME = "Rating.sp_rtg_busc_rel_grp_pyme";
	public static final String SP_RTG_BUSCAR_RELACIONADO_GRUPO_NO_PYME = "Rating.sp_rtg_busc_rel_grp_nopyme";
	public static final String SP_RTG_ACTUALIZAR_RELACIONADO_NO_PYME = "Rating.sp_rtg_act_rel_grp_nopyme";
	public static final String SP_RTG_ACTUALIZAR_RELACIONADO_PYME = "Rating.sp_rtg_act_rel_grp_pyme";
	public static final String SP_RTG_AGREGAR_RELACIONADO_GRUPO_PYME = "Rating.sp_rtg_add_rel_rtg_grp_pyme";
	public static final String SP_RTG_AGREGAR_RELACIONADO_GRUPO_NO_PYME = "Rating.sp_rtg_add_rel_rtg_grp_nopyme";
	public static final String SP_RTG_BUSCAR_ULTIMO_GRUPO_NO_PYME = "Rating.sp_rtg_busc_ult_grp_nopyme";
	public static final String SP_RTG_BUSCAR_ULTIMO_GRUPO_PYME = "Rating.sp_rtg_busc_ult_grp_pyme";
	public static final String SP_RTG_BUSCAR_RTG_GRUPAL_POR_ESTADO_PYME = "Rating.sp_rtg_busc_rtg_grp_x_est_pyme";
	public static final String SP_RTG_BUSCAR_RTG_GRUPAL_POR_ESTADO_NO_PYME = "Rating.sp_rtg_busc_rtg_grp_x_est_nopy";
	public static final String SP_RTG_ELIMINAR_RELACIONADO_RTG_GRUPO_PYME = "Rating.sp_rtg_del_rel_rtg_grp_pyme";
	public static final String SP_RTG_ELIMINAR_RELACIONADO_RTG_GRUPO_NO_PYME = "Rating.sp_rtg_del_rel_rtg_grp_nopyme";
	public static final String SP_RTG_ACTUALIZAR_RTG_GRUPO_NO_PYME = "Rating.sp_rtg_act_rel_gp_rtg";
	public static final String SP_RTG_ACTUALIZAR_RTG_GRUPO_PYME = "Rating.sp_rtg_act_rtg_gp_pyme";
	public static final String SP_RTG_BUSCAR_RTG_GRUPAL_NO_PYME_POR_ID = "Rating.sp_rtg_busc_rel_gp_rtg";
	public static final String SP_RTG_AGREGAR_REL_RATING_GRP_NO_PYME_RATING_IND = "Rating.sp_rtg_add_rel_rtg_gp_rtg";
	public static final String SP_BUSCAR_SEGMENTOS_POR_BANCA = "Rating.sp_buscarSegmentosPorBanca";
	public static final String SP_BUSCAR_SEGMENTOS_POR_BANCA_Y_FECHA = "Rating.sp_buscarSegmentosPorBancaYFecha";
	public static final String SP_BUSCAR_COMPONENTE_RATING = "Rating.SP_BUSCAR_COMPONENTE_RATING";
	public static final String SP_BUSCAR_GRUPO_PYME_POR_ID = "Rating.sp_rtg_busc_rtg_gp_pyme_x_id";
	public static final String SP_VIGENCIA_RTG_GRUPAL_MULTI_SOLO_PARTICIPA = "Rating.SP_VIGENCIA_RTG_GRUPAL_MULTI_SOLO_PARTICIPA";
	public static final String SP_VIGENCIA_RTG_GRUPAL_MULTI = "Rating.SP_VIGENCIA_RTG_GRUPAL_MULTI"; // Sprint 8 Vigencia de rtg Grupal Multi req. 7.4.29
	public static final String SP_VIGENCIA_RTG_GRUPAL_PYME = "Rating.SP_RTG_BUS_RTG_GRP_PYME_REL_VI"; // Sprint 8 Vigencia de rtg Grupal PYME req. 7.4.29
	public static final String SP_VIGENCIA_RTG_GRUPAL_PARAM = "Rating.SP_BUSC_PARAM_RTG_GRUPAL_VIG"; // Sprint 8 Vigencia de rtg - Parametros Generales req. 7.4.29
	public static final String SP_VIGENCIA_PYME_MAN_NOINFORMADOS = "Rating.SP_RTG_PYME_MAN_NOINFORMADOS"; // Sprint 8 Vigencia de rtg Grupal PYME CERRADO MANUAL req. 7.4.29
	public static final String SP_VIGENCIA_MULTI_MAN_NOINFORMADOS = "Rating.SP_RTG_MULTI_MAN_NOINFORMADOS"; // Sprint 8 Vigencia de rtg Grupal MULTI CERRADO MANUAL req. 7.4.29
	public static final String SP_BUSCAR_RELACION_RATING_GRUPO_NO_PYME = "Rating.sp_rtg_busc_rel_rtg_gp_rtg";
	public static final String SP_RTG_BUSCAR_VACIADOS_RATING_GRUPAL = "Rating.sp_rtg_busc_vac_rtg_grupal";
	public static final String SP_RTG_BUSCAR_RTG_GRP_PYME_X_REL = "Rating.sp_rtg_busc_rtg_grp_pyme_x_rel";
	public static final String SP_RTG_BUSCAR_RTG_GRP_X_REL = "Rating.sp_rtg_busc_rtg_grp_x_rel";
	public static final String SP_RTG_BUSCAR_RTG_GRP_X_RATING_INDIVIDUAL = "Rating.sp_rtg_busc_rtg_grp_x_rtg_ind";
	public static final String SP_RTG_BUSCAR_RTG_GRP_PYME_X_RATING_INDIVIDUAL = "Rating.sp_rtg_busc_rtg_grp_pyme_x_ind";
	public static final String SP_RTG_GUARDAR_CALIFICACION_GARANTE = "Rating.sp_guardarCalificacionGarante";
	public static final String SP_RTG_BUSCAR_CALIFICACION_GARANTE = "Rating.sp_buscarCalificacionGarante";
	public static final String SP_OBTENER_MATRIZ_X_BANCA = "Rating.SP_OBTENER_MATRIZ_X_BANCA";
	
	//Sprint 3 req 7.4.29
	public static final String SP_ADMIN_SELECTPARAMETROSVIGENCIA= "Rating.SP_SELECTPARAMETROSVIGENCIA";

	// Operaciones utilizadas en rating
	public static final String SP_RTG_COMP_VIGENTE_POR_CLIENTE_BANCA = "Rating.sp_consultaCompVigentePorClienteBanca";
	public static final String SP_RTG_BORRAR_COMP_INDIV = "Rating.sp_rtg_borrar_comp_indiv";
	public static final String SP_RTG_OBTENER_NOTA_COMP = "Rating.sp_rtg_obtener_nota_comp";
	public static final String SP_RTG_OBTENER_NOTA_COMP_SGM = "Rating.sp_rtg_obtener_nota_comp_sgm";
	public static final String SP_INSERT_RTG_COMPORTAMIENTO = "Rating.sp_insert_rtg_comportamiento";
	public static final String SP_ACTUALIZAR_RTG_COMPORTAMIENTO = "Rating.sp_actualizar_rtg_comportamiento";
	public static final String SP_ACTUALIZAR_RTG_INDIVIDUAL = "Rating.sp_actualzar_rtg_individual";
	public static final String SP_RTG_FINANC_VIGENTE_POR_CLIENTE_BANCA = "Rating.sp_consultaFinancVigentePorClienteBanca";
	public static final String SP_RTG_FINANC_PROY_VIGENTE_POR_CLIENTE_BANCA = "Rating.sp_onsultaFinancProyVigentePorClienteBanca";
	public static final String SP_RTG_FIN_BUSCAR_AJUSTE_CALIDAD = "Rating.sp_rtg_fin_buscar_ajuste_calidad";
	public static final String SP_RTG_NEGOCIO_VIGENTE_POR_CLIENTE_BANCA = "Rating.sp_consultaNegocioVigentePorClienteBanca";
	public static final String SP_RTG_IND_CAMBIAR_ESTADO = "Rating.sp_rtgIndCambiarEstado";
	public static final String SP_RTG_CONSULTA_INDIVIDUAL = "Rating.sp_rtg_consulta_individual";
	public static final String SP_RTG_CONSULTA_FINANCIERO = "Rating.sp_rtg_consulta_financiero";
	public static final String SP_RTG_ACTUALIZAR_RATING_NEGOCIO = "Rating.sp_rtg_act_rating_neg";
	public static final String SP_RTG_ACTUALIZAR_ESTADO_RATING_NEGOCIO = "Rating.SP_RTG_ACTEST_RATING_NEG";
	public static final String SP_RTG_ACTUALIZAR_ESTADO_RATING_FINANCIERO = "Rating.SP_RTG_ACTEST_FINANCIERO";
	public static final String SP_RTGIND_DESASOC_FINAN = "Rating.SP_RTGIND_DESASOC_FINAN";
	public static final String SP_RTG_ELIM_PONDERACIONES = "Rating.SP_RTG_ELIM_PONDERACIONES";
	public static final String SP_RTGIND_DESASOC_NEG = "Rating.SP_RTGIND_DESASOC_NEG";
	public static final String SP_RTGIND_DESASOC_PROY = "Rating.SP_RTGIND_DESASOC_PROY";
	public static final String SP_BUSCAR_MOTIVOS_CAMBIO = "Comun.sp_buscar_lista_motivos_cambio";
	public static final String SP_RTG_CREAR_RATING_INDIV = "Rating.sp_rtg_crear_rating_individual";
	public static final String SP_RTG_BUSCAR_INDIVIDUAL_EN_CURSO = "Rating.sp_rtg_buscar_individual_curso";
	public static final String SP_RTG_BUSCAR_INDIVIDUAL_VIGENTE = "Rating.sp_rtg_buscar_individual_vigente";
	public static final String SP_RTG_OBTENER_RTG_COMPORTAMIENTO_POR_PK = "Rating.sp_rtg_obtener_rtg_comp";
	public static final String SP_ACTUALIZAR_COMENTARIO_RTG_INDIVIDUAL = "Rating.sp_rtg_actualizar_comentario_rtg_individual";
	public static final String SP_CONFIRMAR_RTG_INDIVIDUAL_MODIFICADO = "Rating.sp_rtg_confirmar_individual_modificado";
	public static final String SP_CONFIRMAR_RTG_INDIVIDUAL_MODELO = "Rating.sp_rtg_confirmar_individual_modelo";
	public static final String SP_RTG_OBTENER_CARITAS_SEMANAL = "Rating.sp_rtg_obtener_caritas_sem";
	public static final String SP_RTG_BUSCAR_MATRIZ_FINANCIERA_POR_BANCA_Y_SEGMENTO = "Rating.sp_rtg_buscar_matr_fin_bca_seg";
	public static final String SP_RTG_BUSCAR_TEMAS_FINANCIEROS_POR_MATRIZ = "Rating.sp_rtg_buscar_temas_finan";
	public static final String SP_RTG_BUSCAR_INDIC_FINANCIEROS_POR_TEMA = "Rating.sp_rtg_buscar_ind_finan_x_tema";
	public static final String SP_RTG_BUSCAR_RANGO_NOTA_POR_INDICADOR = "Rating.sp_rtg_buscar_rgo_nota_por_ind";
	public static final String SP_RTG_ACTUALIZAR_RTG_INDIVIDUAL_CON_FINANCIERO = "Rating.sp_rtg_act_indiv_con_finan";
	public static final String SP_RTG_INSERT_EVAL_FINANCIERO = "Rating.sp_rtg_insert_eval_financiero";
	public static final String SP_RTG_BUSCAR_VALOR_CUENTAS = "Rating.sp_rtg_buscar_valor_ctas";
	public static final String SP_RTG_BUSCAR_RATING_FINANCIERO_POR_VACIADO_ACTUAL = "Rating.sp_rtg_buscar_finan_x_vac_act";
	public static final String SP_RTG_BUSCAR_AJUSTE_X_CALIDAD = "Rating.sp_rtg_buscar_ajuste_x_calidad";
	public static final String SP_BUSCAR_FLAGS_CUENTAS_PROY = "Rating.sp_rtg_buscar_opc_ctas";
	public static final String SP_CONSULTA_SOE_X_RUT = "ConstructoraInmobilaria.SP_SOE_CONSULTA_X_RUT";
	public static final String SP_CONSULTA_BI_X_RUT = "Rating.SP_CONSULTA_BI_X_RUT";
	public static final String SP_CON_DEUDA_CTE_POR_FECHA = "Rating.SP_CON_DEUDA_CTE_POR_FECHA";
	public static final String SP_RTG_INSERT_RTG_PROYECTADO = "Rating.sp_rtg_insert_rtg_proyectado";
	public static final String SP_RTG_ACTUALIZAR_RTG_PROYECTADO = "Rating.sp_rtg_update_rtg_proyectado";
	public static final String SP_RTG_ACTUALIZAR_RTG_INDIVIDUAL_CON_PROYECTADO = "Rating.sp_rtg_act_indiv_con_proy";
	public static final String SP_RTG_ACT_EST_VACIADO_PROYECTADO = "Vaciado.sp_rtg_act_est_vaciado_proy";
	public static final String SP_ACTUALIZAR_VALOR_CUENTA = "Cuenta.SP_ACTUALIZAR_VALOR_CUENTA";
	public static final String SP_OBTENER_VALOR_CUENTA_POR_VAC_ID = "Cuenta.SP_OBTENER_VALOR_CUENTA_POR_VAC_ID";

	public static final String SP_OBTENER_MAPEO_CUENTA = "Comun.SP_OBTENER_MAPEO_CUENTA";
	public static final String SP_FORMULA_BUSCAR_OTRA_FORMULA = "ConstructoraInmobilaria.SP_FORMULA_BUSCAR_OTRA_FORMULA";
	public static final String SP_FORMULA_BUSCAR_LISTA_OTRAS_FORMULAS = "ConstructoraInmobilaria.SP_FORMULA_BUSCAR_LISTA_OTRAS_FORMULAS";
	public static final String SP_FORMULA_BUSCAR_LISTA_OTRAS_FORMULAS_POR_ORIGEN = "ConstructoraInmobilaria.SP_FORM_BUSCAR_LTA_OTRAS_FORM_ORIGEN_INF";

	public static final String SP_BUSCAR_INDIVIDUAL_POR_FINANCIERO = "Rating.sp_rtg_buscar_individual_por_financiero";
	public static final String SP_BUSCAR_COMPORTAMIENTO_HISTORICO_CLIENTE = "Rating.sp_rtg_buscar_historico_cliente";
	public static final String SP_BUSCAR_RTGFINAL_FINAN_VACXRUT = "Vaciado.SP_BUS_RTG_FINAL_FINAL_VACXRUT"; // Requerimiento 7.2.8 Sprint 4 - Reporte construccion
	public static final String SP_RTG_UPD_TPO_VAC_RATING_NEGOCIO = "Rating.sp_upd_tpo_vac_rating_negocio";
	public static final String SP_RTG_BUSCAR_PONDERACION_PERIODOS_POR_MATRIZ = "Rating.sp_buscar_pond_periodos_matriz";
	public static final String SP_BUSCAR_ULT_FECHA_VAC = "Vaciado.SP_BUSCAR_MAX_PER_VAC"; // Req, 7.4.29 Vigencia de rtg Sprint 7

	public static final String SP_INS_BALANCE_INMOBILIARIO = "Rating.sp_ins_balance_inmobiliario";

	public static final String SP_INS_SOE = "Rating.sp_ins_soe";

	public static final String SP_INS_HOJA_BALANCE_INMOBILIARIO = "Rating.sp_ins_hoja_balance_inmobiliario";

	public static final String SP_INS_HOJA_SOE = "Rating.sp_ins_hoja_soe";

	public static final String SP_INS_INDICADOR_SOE = "Rating.SP_INS_IND_SOE";

	public static final String SP_INS_INDICADOR_BI = "Rating.SP_INS_IND_BI";

	public static final String SP_ACT_USU_SOE_BI = "Rating.SP_ACT_USU_SOE_BI";
	
	public static final String SP_ACT_DEU_SOE_BI = "Rating.SP_ACT_DEU_SOE_BI";

	public static final String SP_INM_DEL_INDCS = "ConstructoraInmobilaria.SP_INM_DEL_INDCS";

	public static final String SP_DEL_DETALLE_SOE_BI = "Rating.SP_DEL_DETALLE_SOE_BI";
	
	public static final String SP_BORRAR_RATING_EN_CURSO = "Rating.SP_BORRAR_RATING_EN_CURSO";
	
	/*
	 * Inicio SPs Benchmark
	 */
	public static final String SP_BENCHMARK_BUSCAR_DATA_RTG_BANCA = "Benchmark.sp_bcm_rtg_banca";
	public static final String SP_BENCHMARK_BUSCAR_DATA_RTG_BANCA_SECTOR = "Benchmark.sp_bcm_rtg_bca_sec";
	public static final String SP_BENCHMARK_BUSCAR_DATA_RTG_BANCA_SECTOR_SEGMENTO = "Benchmark.sp_bcm_rtg_bca_sec_seg";
	public static final String SP_BENCHMARK_BUSCAR_DATA_RTG_SECTOR = "Benchmark.sp_bcm_rtg_sector";
	public static final String SP_BENCHMARK_BUSCAR_DATA_RTG_SECTOR_SEGMENTO = "Benchmark.sp_bcm_rtg_sec_seg";
	public static final String SP_BENCHMARK_BUSCAR_DATA_CLIENTE = "Benchmark.sp_bcm_busc_data_cliente";
	public static final String SP_BENCHMARK_BUSCAR_DATA_PEER_GROUP = "Benchmark.sp_bcm_busc_peer_group";
	public static final String SP_BENCHMARK_BUSCAR_INTEGRANTES_GRP = "Benchmark.sp_bcm_busc_cli_grp";

	public static final String SP_BENCHMARK_BUSCAR_DATA_IND_BANCA = "Benchmark.sp_bcm_ind_banca";
	public static final String SP_BENCHMARK_BUSCAR_DATA_IND_BANCA_SECTOR = "Benchmark.sp_bcm_ind_banca_sect";
	public static final String SP_BENCHMARK_BUSCAR_DATA_IND_BANCA_SECTOR_SEGMENTO = "Benchmark.sp_bcm_ind_banca_sect_segm";
	public static final String SP_BENCHMARK_BUSCAR_DATA_IND_SECTOR = "Benchmark.sp_bcm_ind_sector";
	public static final String SP_BENCHMARK_BUSCAR_DATA_IND_SECTOR_SEGMENTO = "Benchmark.sp_bcm_ind_sect_segm";
	public static final String SP_BENCHMARK_BUSCAR_DATA_PEER_GROUP_INDICADORES = "Benchmark.sp_bcm_busc_peer_group_ind";
	public static final String SP_BENCHMARK_BUSCAR_DATA_CLIENTE_INDICADORES = "Benchmark.sp_bcm_busc_data_cli_ind";
	public static final String SP_BENCHMARK_BUSCAR_VACIADO_PARA_BENCHMARK_INDICADORES = "Benchmark.sp_bcm_busc_vac_bench_ind";

	public static final String SP_BENCHMARK_BUSCAR_INDICADORES = "Benchmark.sp_bcm_busc_indicadores";
	public static final String SP_BENCHMARK_BUSCAR_GRUPOS = "Benchmark.sp_buscar_grupos";
	public static final String SP_BENCHMARK_BUSCAR_GRUPO_POR_ID = "Benchmark.sp_buscar_grp_por_id";
	public static final String SP_BENCHMARK_BUSCAR_CLIENTES_PARA_PEER_GRP = "Benchmark.sp_bcm_busc_cli_para_peer";
	public static final String SP_BENCHMARK_INSERTAR_CLIENTE_PEER_GROUP = "Benchmark.sp_bcm_ins_cli_grupo";
	public static final String SP_BENCHMARK_ELIMINAR_PEER_GROUP = "Benchmark.sp_bcm_eli_grp";
	public static final String SP_BENCHMARK_ACTUALIZAR_GRUPO = "Benchmark.sp_actualizar_grupo";
	public static final String SP_BENCHMARK_ELIMINAR_CLIENTES_DEL_PEER_GROUP = "Benchmark.sp_bcm_eli_clientes_grp";
	public static final String SP_BENCHMARK_BUSCAR_GRUPOS_DEL_CLIENTE = "Benchmark.sp_bcm_busc_grp_del_cli";
	public static final String SP_BENCHMARK_INSERTAR_NUEVO_PEER_GROUP = "Benchmark.sp_rtg_add_nuevo_grp_nopyme";
	public static final String SP_BENCHMARK_ELIMINAR_CLIENTE_DEL_GRUPO = "Benchmark.sp_rtg_del_rel_rtg_grp_nopyme";
	public static final String SP_BENCHMARK_BUSCAR_GRUPO_POR_NOMBRE = "Benchmark.sp_buscar_grp_por_nom";
	public static final String SP_BENCHMARK_BUSCAR_UMBRAL_POR_INDICADOR = "Benchmark.sp_bcm_busc_umbral_x_ind";

	/*
	 * Fin SPs Benchmark
	 */

	/**
	 * Identificador para procedimiento almacenado que graba una respuesta
	 * ingresada por el usuario, para el rating de negocio.
	 */
	public static final String SP_RATING_GRABAR_RESPUESTA_RATING_NEGOCIO = "Rating.sp_rtg_grabar_respta_negocio";

	/**
	 * Identificador para procedimiento almacenado que elimina las respuestas de
	 * negocio asociadas a un rating de negocio.
	 */
	public static final String SP_RATING_BORRAR_RESPUESTAS_RATING_NEGOCIO = "Rating.sp_rtg_borrar_rpta_neg";

	/**
	 * Identificador para Procedimiento almacenado que obtiene los rating
	 * financieros que utilizaron un vaciado en particular
	 */
	public static final String SP_RATING_OBTENER_RATING_ASOCIADOS = "Rating.sp_obtenerRatingAsociados";

	/**
	 * Identificador para procedimiento almacenado que obtiene a los usuarios
	 * que coinciden con un login de operador.
	 */
	public static final String SP_COMUN_OBTENER_USUARIO = "Comun.sp_obtenerUsuario";

	/**
	 * Identificador para procedimiento almacenado que obtiene a un usuario a
	 * partir de su identificador unico.
	 */
	public static final String SP_COMUN_OBTENER_USUARIO_POR_ID = "Comun.sp_obtener_usuario_por_id";

	/**
	 * Identificador para procedimiento almacenado que crea los registros de
	 * valores cuentas para un vaciado.
	 */
	public static final String SP_VACIADO_POBLAR_VALORES_CTAS_VACIADO = "Vaciado.sp_poblarValoresCtasVaciado";

	/**
	 * Identificador para procedimiento almacenado que busca el tipo de cambio
	 * utilizado por un vaciado.
	 */
	public static final String SP_OBTENER_TPO_CAMBIO_BAL = "Vaciado.sp_obtenerTpoCambioBal";

	public static final String SP_RTG_BUSCAR_VACIADOS_PARA_RATING = "Rating.sp_RtgBuscarVaciadosRating";
	public static final String SP_RTG_BUSCAR_VACIADOS_PARA_RATING_GENERICO = "Rating.sp_RtgBuscarVaciadosRatingGenerico";
	public static final String SP_RTG_INSERT_RTG_FINANCIERO = "Rating.sp_rtg_insert_rtg_financiero";

	public static final String SP_COMUN_BUSCAR_PARTE_INVOL = "Comun.sp_buscarParteInvol";
	public static final String SP_COMUN_BUSCAR_PARTE_INVOL_PERS = "Comun.sp_buscarParteInvolPers";
	public static final String SP_COMUN_BUSCAR_PARTE_INVOL_POR_RAZONSOCIAL = "Comun.sp_buscarParteInvolPorRazonSoc";
	public static final String SP_CONSOLIDADOS_BUSCAR_REL_FIC_FIN = "Consolidados.SP_BUSCARRELFICHAFIN"; // Requerimiento 7.2.12 Sprint 6 Consolidados-Combinados
	public static final String SP_CONSOLIDADOS_BUSCAR_PART_INV_REL_CONSOL = "Consolidados.sp_buscarEmpresaVinculadaConsolidados";
	public static final String SP_CONSOLIDADOS_BUSCAR_PART_INV_REL_CONSOL_NORMAL = "Consolidados.sp_buscarEmpresaVinculadaConsolidadosNormal";
	public static final String SP_CONSOLIDADOS_BUSCAR_PART_INV_REL_CONSOL_FICHA_FINAN = "Consolidados.sp_buscarEmpresaVinculadaConsolidadosFichaFinan";
	public static final String SP_CONSOLIDADOS_VINCULAR_EMPRESA_VAC_CONSOLIDADO = "Consolidados.sp_vincularEmpVacConsolidado";
	public static final String SP_CONSOLIDADOS_DESVINCULAR_EMPRESA_VAC_CONSOLIDADO = "Consolidados.sp_desVincularEmpVacConsolidado";
	public static final String SP_CONSOLIDADOS_BUSCAR_EMPRESA_POR_ID_VAC_CONSOLIDADO = "Consolidados.sp_buscarEmpresaVinculadaConsolidadosPorId";
	public static final String SP_CONSOLIDADOS_ACTUALIZAR_VINC_VAC_CONSOLIDADO = "Consolidados.sp_actualizarEmpVacConsolidado";
	public static final String SP_CONSOLIDADOS_INSERTAR_RELACION_CLIENTE_VINCULACION = "Consolidados.sp_insertarRelCteConsl";
	public static final String SP_CONSOLIDADOS_INSERTAR_RELACION_CLIENTE_VINCULACION_FONDO = "Consolidados.sp_insertarRelCteConslFondo";
	public static final String SP_CONSOLIDADOS_ACTUALIZAR_RELACION_CLIENTE_VINCULACION = "Consolidados.sp_actualizarRelCteConsl";
	public static final String SP_CONSOLIDADOS_ACTUALIZAR_RELACION_CLIENTE_VINCULACION_FONDO = "Consolidados.sp_actualizarRelCteConslFondo";
	public static final String SP_CONSOLIDADOS_BUSCAR_RELACION_CLIENTE_VINCULACION_FONDO = "Consolidados.sp_buscarRelCteConslFondo";
	public static final String SP_CONSOLIDADOS_BUSCAR_RELACION_CLIENTE_VINCULACION = "Consolidados.sp_buscarRelCteConsl";
	public static final String SP_CONSOLIDADOS_INSERTAR_EMPRESA_CONS_AGREGADA = "Consolidados.SP_INS_ACT_EMP_CONS_AGREGADA";
	public static final String SP_DETALLE_CUENTA_EMPRESA = "Consolidados.SP_DETALLE_CUENTA_EMPRESA";
	public static final String SP_DET_CUENTA_EMPRESA_AGREGADA = "Consolidados.SP_DET_CUENTA_EMPRESA_AGREGADA";
	public static final String SP_DET_CUENTA_EMPRESA_AGREGADA_VACIADO_ACTUAL = "Consolidados.SP_DET_CTA_EMP_AGR_VAC_ACTUAL";

	// genera un nuevo numero de version para utilizar en la operacion de
	// clonado de vaciados
	public static final String SP_GENERAR_NUMERO_VERSION = "Vaciado.SP_GenerarNumeroVersion";
	public static final String SP_GENERAR_NUMERO_VERSION_POR_CLIENTE = "Vaciado.SP_GenerarNumeroVersionPorCliente";

	/**
	 * Identificador para la propiedad que configura el tiempo que dura la
	 * sesion de usuario.
	 */
	public static final String ID_SESION_TIMEOUT = "sesion.timeout";

	/**
	 * Identificador para procedimiento almacenado que graba una nueva sesion
	 * asociada a un usuario.
	 */
	public static final String SP_SESIONES_GRABAR_SESION = "Sesiones.sp_grabarSesion";

	/**
	 * Identificador para procedimiento almacenado que busca la ultima sesion
	 * asociada a un usuario.
	 */
	public static final String SP_SESIONES_OBTENER_ULTIMA_SESION = "Sesiones.sp_obtenerUltimaSesion";

	/**
	 * Identificador para procedimiento almacenado que actualiza la fecha de
	 * cierre de la sesion de usuario.
	 */
	public static final String SP_SESIONES_CADUCAR_SESION = "Sesiones.sp_caducarSesion";

	/**
	 * Identificador para procedimiento almacenado que actualiza la fecha de
	 * creacion de la sesion de usuario.
	 */
	public static final String SP_SESIONES_ACTUALIZAR_FECHA_SESION = "Sesiones.sp_actualizarFechaSesion";

	/**
	 * Identificador para procedimiento almacenado que devuelve todas las
	 * etiquetas asociadas a una plantilla de reporte
	 */
	public static final String SP_REPORTES_OBTENER_ETIQUETA = "Reporte.sp_buscarEtiquetas";

	/**
	 * Identificador para procedimiento almacenado que devuelve todas las
	 * cuentas asociadas a un reporte
	 */
	public static final String SP_REPORTES_OBTENER_CUENTAS_REPORTE = "Reporte.sp_BuscarCuentasReporte";

	/**
	 * Identificador para procedimiento almacenado que devuelve todas las
	 * cuentas asociadas a grupo dentro del reporte
	 */
	public static final String SP_REPORTES_OBTENER_CUENTAS_GRUPO_REPORTE = "Reporte.sp_BuscarNombreCtasGrpReporte";

	/**
	 * Identificador para procedimiento almacenado que devuelve valores de un
	 * vaciado con respecto las cuentas de reporte
	 */
	public static final String SP_REPORTES_OBTENER_VALOR_CUENTAS_RPT = "Reporte.sp_ObtenerValCtasRptPorVac";

	/**
	 * Identificador para procedimiento almacenado que devuelve aperturas de
	 * cuentas para un vaciado para el reporte de detalle de cuentas
	 */
	public static final String SP_REPORTES_OBTENER_APERTURAS_RPT = "Reporte.sp_ObtenerAperturasRptPorVac";

	/**
	 * Identificador para el procedimiento almacenado que devuelve una lista de
	 * nombres de empresas unicas para 3 vaciados en el reporte de detalle de
	 * cuentas
	 */
	public static final String SP_REPORTES_OBTENER_NOMBRES_EMPRESAS_INV_SOC = "Reporte.sp_ObtenerNombresEmpresasDetCtaRpt";

	/**
	 * Identificador para el procedimiento almacenado que devuelve los valores
	 * de las empresas para 3 vaciados en el reporte de detalle de cuentas
	 */
	public static final String SP_REPORTES_OBTENER_VALORES_EMPRESAS_INV_SOC = "Reporte.sp_ObtenerValoresEmpresasDetCtaRpt";

	/**
	 * Identificador para el procedimiento almacenado que devuelve las notas del
	 * detalle de cuenta para los 3 vaciados seleccionados para el reporte.
	 */
	public static final String SP_REPORTES_OBTENER_NOTAS_DETALLE_CUENTAS = "Reporte.sp_ObtenerNotaDetCtaRpt";

	/**
	 * Clave que identifica dentro del map del metodo
	 * com.bch.sefe.comun.srv.impl.GestorReportesImpl.getEmpresasInvSocDetCta,
	 * la lista de nombres unicos de empresas
	 */
	public static final String KEYMAP_LISTA_NOMBRES_EMPRESAS_INV_SOC = "LISTA_NOMBRE_EMPRESAS";

	/**
	 * Clave que identifica dentro del map del metodo
	 * com.bch.sefe.comun.srv.impl.GestorReportesImpl.getEmpresasInvSocDetCta,
	 * la lista de valores de empresas
	 */
	public static final String KEYMAP_LISTA_VALORES_EMPRESAS_INV_SOC = "LISTA_VALORES_EMPRESAS";

	/**
	 * Identificador para procedimiento almacenado que devuelve todas las
	 * cuentas asociadas a un reporte
	 */
	public static final String SP_REPORTES_OBTENER_DETALLE_EVALUACION = "Reporte.sp_obtenerDetalleEvaluacion";

	/**
	 * Identificador para procedimiento almacenado que devuelve el nombre del
	 * archivo jasper asociado al id de la plantilla
	 */
	public static final String SP_REPORTES_OBTENER_NOMBRE_REPORTE_COMPILADO = "Reporte.sp_BuscarRptCompilado";

	/**
	 * Identificador para procedimiento almacenado que devuelve los valores y
	 * nota de la evaluacion finan asociadas a un grupo de cuenta del reporte
	 * Identificador para procedimiento almacenado que devuelve los valores de
	 * las cuentas asociadas al un grupo de subRpt
	 */
	public static final String SP_OBTENER_VALOR_NOTA_CUENTA_RPT = "Reporte.sp_ObtenerValorNotaCuentaRpt";

	/**
	 * Identificador para procedimiento almacenado que devuelve los valores de
	 * las cuentas del vaciado asociada a un grupo de cuenta del reporte
	 */
	public static final String SP_OBTENER_VALOR_CTA_VAC_RPT = "Reporte.sp_ObtenerValorCuentaVacRpt";

	/**
	 * Identificador para procedimiento almacenado que devuelve los valores de
	 * las cuentas del vaciado asociada a un grupo de cuenta del reporte y
	 * ademas referente al periodo Actual, Anterio, Ante Anterior o Proyectado
	 * (X_, X1_, X2_ o null)
	 */
	public static final String SP_OBTENER_VALOR_CTA_TP_PER_RPT = "Reporte.sp_ObtenerValorCuentaTipoPeriodoRpt";

	/**
	 * identificador para procedimiento almacenado que devuelve los valores de
	 * las cuentas del vaciado asociado a un grupo de cuentas del reporte. Estos
	 * valores vienen en una estructura de listas de objetos Cuenta.
	 */
	public static final String SP_OBTENER_CUENTA_VACIADO_POR_GRUPO = "Reporte.sp_ObtenerCuentasVaciadoPorGrupo";

	/**
	 * Identificador para procedimiento almacenado que devuelve los valores de
	 * las cuentas del vaciado asociada a un grupo de cuenta del reporte
	 */
	public static final String SP_OBTENER_VALOR_AJUSTE_CTA_VAC_RPT = "Reporte.sp_ObtenerValorAjusteCuentaVacRpt";

	/**
	 * Identificador para procedimiento almacenado que devuelve los valores de
	 * las cuentas del vaciado asociada a un grupo de cuenta del reporte
	 */
	public static final String SP_OBTENER_VALOR_TOTAL_CONSOLIDADO_CTA_VAC_RPT = "Reporte.sp_ObtenerValoTtalConsolidadoCuentaVacRpt";

	/**
	 * Identificador para procedimiento almacenado que devuelve la ponderacion
	 * de un indicador
	 */
	public static final String SP_OBTENER_POND_INDICE_FINAN = "Reporte.sp_ObtenerPondIndiceFinan";

	/**
	 * Identificador para procedimiento almacenado que devuelve los flags para
	 * un registro de cuenta reporte
	 */
	public static final String SP_OBTENER_FLAGS_CUENTAS_RPT = "Reporte.sp_ObtenerFlgsCuentasRpt";
	
	// sprint 4 req: 7.1.9 y 7.1.12 eliminar balances y cuadro de obra
	public static final String SP_BORRAR_BALANCES = "ConstructoraInmobilaria.SP_BORRAR_BALANCES";
	// sprint 4 req: 7.4.42-3 eliminar hojas IMD
	public static final String SP_BORRAR_IMD = "HojaIndependiente.SP_BORRAR_IMD";

	/**
	 * Identificador para procedimento almacenado que obtiene las cuentas
	 * aperturables de un vaciado para le reporte de detalle de cuentas.
	 * SP:SP_RPT_DET_CTA_OBT_CTAS_AP
	 */
	public static final String SP_DET_CTA_OBTENER_CTAS_APERTURABLES = "Reporte.sp_DetCtaObtenerCtasAperturables";

	/**
	 * Identificador del Servicio OSB; Consulta IVAs.
	 */
	public static final String ID_SRV_OSB_CONSULTA_IVAS = "servicio.consulta.ivas";

	/**
	 * Identificador del Servicio OSB; SGT: Consulta UTM Dia.
	 */
	public static final String ID_SRV_OSB_CONSULTA_UTM_DIA = "servicio.consulta.sgtConsultaUtmDia";

	/**
	 * Identificador del Servicio OSB; SGT: Consulta Moneda Dia.
	 */
	public static final String ID_SRV_OSB_CONSULTA_MONEDA_DIA = "servicio.consulta.sgtConsultaMonedaDia";

	/**
	 * Identificador del Servicio OSB; Consulta de Feriados.
	 */
	public static final String ID_SRV_OSB_CONSULTA_FERIADOS = "servicio.consulta.feriados";

	/**
	 * Identificador del Servicio OSB; SGT: Consulta UF Dia.
	 */
	public static final String ID_SRV_OSB_CONSULTA_UF_DIA = "servicio.consulta.sgtConsultaUfDia";

	/**
	 * Identificador del Servicio OSB; SGT: Consulta Malla de Relaciones.
	 */
	public static final String ID_SRV_OSB_CONSULTA_MALLAS_RELACIONES = "servicio.consulta.mallasRelaciones";

	/**
	 * Identificador del Servicio OSB; Consulta Comportamiento Crediticio
	 */
	public static final String ID_SRV_OSB_CONSULTA_COMPORTAMIENTO_CREDITICIO = "servicio.consulta.comp.crediticio";

	/**
	 * Identificador del Servicio OSB; Consulta Ultima Pre Evaluacion Persona.
	 */
	public static final String ID_SRV_OSB_CONSULTA_ULTIMA_PRE_EVALUACION_PERSONA = "servicio.consulta.comp.pre.eval.persona";

	/**
	 * Identificador del Servicio OSB; SGT: Consulta Personas Estado Situacion.
	 */
	public static final String ID_SRV_OSB_CONSULTA_PERSONA_ESTADOS_SITUACION = "servicio.consulta.personaeess";

	public static final String ID_SRV_OSB_CONSULTA_DECLARACION_IVAS = "servicio.declaracion.ivas";

	/**
	 * Identificador del Servicio OSB; Consulta Datos Ficha Chica.
	 */
	public static final String ID_SRV_OSB_CONSULTA_DATOS_FICHA_CHICA = "servicio.obtener.fichaChica";

	/**
	 * Identificador del Servicio OSB; Consulta Indicadores Economicos
	 * Mensuales.
	 */
	public static final String ID_SRV_OSB_CONSULTA_IND_ECON_MENSUALES = "servicio.consulta.indecon";

	/**
	 * Identificador del Servicio OSB; Consulta Indicadores Economicos
	 * Mensuales.
	 */
	public static final String ID_SRV_OSB_CONSULTA_IVAL_MENSUALES = "servicio.consulta.ival";

	/**
	 * Identificador del Servicio OSB; Consulta D.A.I.
	 */
	public static final String ID_SRV_OSB_CONSULTA_DAI = "servicio.consulta.dai";

	/**
	 * Identificador del Servicio OSB; Actualizar Rating
	 */
	public static final String ID_SRV_OSB_ACTUALIZAR_RATING = "servicio.actualizar.rating";

	/**
	 * Valor por defecto para los valores de cuenta en null. 0.0
	 */
	public static final Double VALOR_CTA_POR_DEFECTO = null;

	// Identificador de los idiomas de los reportes

	public static final Integer RPT_ID_IDIOMA_ESPANOL = new Integer(0);
	public static final Integer RPT_ID_IDIOMA_INGLES = new Integer(1);

	public static final Integer SCALA_FACTOR_CORRECCION_MONEDA_UNIDAD = new Integer(
			5);

	// Identificador de las clasificaciones de Tipo de Balance

	public static final Integer CLASIF_ID_TPO_BALANCE_CLASIFICADO = new Integer(
			1303);
	public static final Integer CLASIF_ID_TPO_BALANCE_TRIBUTARIO = new Integer(
			1304);
	public static final Integer CLASIF_ID_TPO_BALANCE_FECU = new Integer(1305);
	public static final Integer CLASIF_ID_TPO_BALANCE_DAI = new Integer(1306);
	public static final Integer CLASIF_ID_TPO_BALANCE_IFRSCF = new Integer(1307);
	public static final Integer CLASIF_ID_TPO_BALANCE_IFRSCN = new Integer(1308);
	public static final Integer CLASIF_ID_TPO_BALANCE_IFRSLF = new Integer(1309);
	public static final Integer CLASIF_ID_TPO_BALANCE_IFRSLN = new Integer(1310);

	// Identificador de la clasificaciones de Tipo de Vaciado

	public static final Integer CLASIF_ID_TPO_VACIADO_INDIVIDUAL = new Integer(
			1201);
	public static final Integer CLASIF_ID_TPO_VACIADO_CONSOLIDADO = new Integer(
			1202);
	public static final Integer CLASIF_ID_TPO_VACIADO_COMBINADO = new Integer(
			1203);
	public static final Integer CLASIF_ID_TPO_VACIADO_COMBINADO_PRO_FORMA = new Integer(
			1204);
	public static final Integer CLASIF_ID_TPO_VACIADO_CONSOLIDADO_PATRIMONIAL = new Integer(
			1205);
	// CLASIFICACION DE TIPO VACIADO PARA LOS VACIADOS CONSOLIDADOS COMBINADOS
	// BCH-SEFE
	// public static final Integer CLASIF_ID_NO_APLICA =
	// ConfigManager.getValueAsInteger("clasif.id.consolidado.no.aplica");//new
	// Integer(1319);
	public static final Integer CLASIF_ID_NO_APLICA = new Integer(1319);//

	public static final Integer CLASIF_ID_TPO_PROYECCION_CORTA = new Integer(
			1034);
	public static final Integer CLASIF_ID_TPO_PROYECCION_LARGA = new Integer(
			1056);
	public static final String CTX_CALCULAR_PROYECCION = "CALCULAR_PROYECCION";
	public static final String CTX_PRIMER_ID_VAC_PROYECCION = "PRIMER_ID_VAC_PROYECCION";
	public final static String CTX_PERIODO_X_3 = "PERIODO_X_3";
	public final static String CTX_PERIODO_X_2 = "PERIODO_X_2";
	public final static String CTX_PERIODO_X_1 = "PERIODO_X_1";
	public final static String CTX_PERIODO_X = "PERIODO_X";
	public final static String CTX_PERIODO_P = "PERIODO_P";
	// public final static String CTX_PERIODO_PIVOTE = "PER_ACTUAL";
	public final static String CTX_MODO_PROYECTADO = "MODO_PROYECTADO";

	// Tipos de carga de vaciado

	public static final Integer FLAG_TIPO_CARGA_MANUAL = new Integer(0);
	public static final Integer FLAG_TIPO_CARGA_SEMIAUTOMATICA = new Integer(1);
	public static final Integer FLAG_TIPO_CARGA_MASIVA = new Integer(2);

	// Tipos de tipo plan de cuenta

	public static final Integer CLASIF_ID_TPO_PLAN_CHGAAP = new Integer(1101);
	public static final Integer CLASIF_ID_TPO_PLAN_IFRSCF = new Integer(1102);
	public static final Integer CLASIF_ID_TPO_PLAN_IFRSCN = new Integer(1103);
	public static final Integer CLASIF_ID_TPO_PLAN_IFRSLF = new Integer(1104);
	public static final Integer CLASIF_ID_TPO_PLAN_IFRSLN = new Integer(1105);
	public static final Integer CLASIF_ID_TPO_PLAN_IFRS_BANCOS = new Integer(
			1106);
	public static final Integer CLASIF_ID_TPO_PLAN_IFRS_CORREDORAS = new Integer(
			1107);
	public static final Integer CLASIF_ID_TPO_PLAN_IFRS_SEG_GRAL = new Integer(
			1109);
	public static final Integer CLASIF_ID_TPO_PLAN_IFRS_SEG_VIDA = new Integer(
			1108);
	public static final Integer CLASIF_ID_TPO_PLAN_AGRICOLA = new Integer(1321);
	public static final Integer CLASIF_ID_TPO_PLAN_INMOBILIARIA = new Integer(
			1326);

	public static final Long TIPO_RELACION_SEG_VIDA = new Long(2103);
	public static final Long TIPO_RELACION_SEG_GENERALES = new Long(2104);
	public static final Long TIPO_RELACION_SEG_CREDITO = new Long(2105);
	public static final Long TIPO_RELACION_BANCOS = new Long(2106);

	public final static Integer FLAG_SEGUROS_GENERALES = new Integer(0);
	public final static Integer FLAG_SEGUROS_CREDITO = new Integer(1);
	public final static Integer FLAG_SEGUROS_AMBOS = new Integer(2);

	// Tipo de moneda

	public static final Integer CLASIF_ID_TPO_MONEDA_CLP = new Integer(1501);

	// Tipo segmento ventas para calculo rating individual
	public static final Integer CLASIF_ID_TPO_SEGMENTO_VENTAS = new Integer(
			4601);;

	// Colores Tipo Carita

	public static final Integer CARITAS_ROJO = new Integer(1);
	public static final Integer CARITAS_AMARILLO = new Integer(2);
	public static final Integer CARITAS_VERDE = new Integer(3);

	// Cuentas de correccion monetaria para alertas

	public static final String ALERTA_CTAS_CORR_MONETARIA = "alerta.ctas.corr.monetaria";
	public static final String ALERTA_CTAS_RECONCILIACION = "alerta.ctas.recon.";

	// Llave del properties para las cuenta total activos y total pasivos
	public static final String KEY_PROP_ID_CUENTA_TOTAL_ACTIVOS = "id.cuentas.total.activos.";
	public static final String KEY_PROP_ID_CUENTA_TOTAL_PASIVOS = "id.cuentas.total.pasivos.";

	// Nombres de campos del data source de deuda para la ficha financiera
	public final static String FIELD_NAME_DEU_BCH_PER1 = "bchPer1";
	public final static String FIELD_NAME_DEU_BCH_PER2 = "bchPer2";
	public final static String FIELD_NAME_DEU_BCH_PER3 = "bchPer3";

	public final static String FIELD_NAME_DEU_SBIF_PER1 = "sbifPer1";
	public final static String FIELD_NAME_DEU_SBIF_PER2 = "sbifPer2";
	public final static String FIELD_NAME_DEU_SBIF_PER3 = "sbifPer3";

	public final static String FIELD_NAME_DEU_ACHEL_PER1 = "achelPer1";
	public final static String FIELD_NAME_DEU_ACHEL_PER2 = "achelPer2";

	public final static String FIELD_NAME_DEU_NOMBRE_DEUDA = "nombreDeuda";
	public final static String FIELD_NAME_DEU_FLAG_ULT_DEU = "flagUltimaDeuda";

	// Llaves de los labels de los nombres de las deudas
	public final static String MAPKEY_DEUDA_DIRECTA_VIGENTE = "LABELF25";
	public final static String MAPKEY_DEUDA_DIRECTA_VENCIDA = "LABELF26";
	public final static String MAPKEY_TOTAL_DEUDA_DIRECTA = "LABELF28";
	public final static String MAPKEY_DEUDA_INDIRECTA_VIGENTE = "LABELF29";
	public final static String MAPKEY_DEUDA_INDIRECTA_VENCIDA = "LABELF30";
	public final static String MAPKEY_TOTAL_DEUDA_INDIRECTA = "LABELF31";

	// Llaves de los labels de las fechas de las deudas
	public final static String MAPKEY_DEUDA_SBIF_PERIODO_1 = "hdSBIFPer1";
	public final static String MAPKEY_DEUDA_SBIF_PERIODO_2 = "hdSBIFPer2";
	public final static String MAPKEY_DEUDA_SBIF_PERIODO_3 = "hdSBIFPer3";
	public final static String MAPKEY_DEUDA_BCH_PERIODO_1 = "hdBCHPer1";
	public final static String MAPKEY_DEUDA_BCH_PERIODO_2 = "hdBCHPer2";
	public final static String MAPKEY_DEUDA_BCH_PERIODO_3 = "hdBCHPer3";
	public final static String MAPKEY_DEUDA_ACHEL_PERIODO_1 = "hdACHELPer1";
	public final static String MAPKEY_DEUDA_ACHEL_PERIODO_2 = "hdACHELPer2";

	public static final String TAG_PERFIL = "perfil";
	public static final String TAG_OPERACION = "operacion";
	public static final String TAG_MODULO = "modulo";
	public static final String TAG_PRIVILEGIO = "privilegio";

	public static final String ID_TAG_PERFIL = "perfil";
	public static final String ID_TAG_MODULO = "modulo";
	public static final String ID_TAG_DESCRIPCION = "descripcion";
	public static final String ID_TAG_ENLACE = "enlace";
	public static final String ID_TAG_GLOSA = "glosa";
	public static final String ID_TAG_ID = "id";
	public static final String ID_TAG_LOG_OPERADOR = "logOperador";
	public static final String ID_TAG_RUT_OPERADOR = "rutOperador";
	public static final String ID_TAG_ROL_OPERADOR = "rolOperador";
	public static final String ID_TAG_CODIGO_APP = "codigoApp";
	public static final String ID_TAG_RUT_CLIENTE = "rutCliente";
	public static final String ID_TAG_RUT_EJECUTIVO = "rutEjecutivo";
	public static final String ID_TAG_OPERACION = "operacion";
	public static final String ID_TAG_ID_VACIADO = "idVac";
	public static final String ID_TAG_ID_SESION = "idSesion";
	public static final String ID_TAG_ID_ESQUEMA = "idEsquema";
	public static final String ID_TAG_ESQUEMA = "esquema";
	public static final String ID_TAG_MAPA = "mapa";
	public static final String ID_TAG_LISTA = "lista";
	public static final String ID_TAG_ID_ESQUEMA_PADRE = "idEsquemaPadre";
	public static final String ID_TAG_CODIGO_ESQUEMA = "codigoEsq";
	public static final String ID_TAG_NOMBRE_CORTO = "nombreCorto";
	public static final String ID_TAG_NOMBRE = "nombre";
	public static final String ID_TAG_NOMBRE_LARGO = "nombreLargo";
	public static final String ID_TAG_FECHA_EFECTIVA = "fechaEfectiva";
	public static final String ID_TAG_FECHA_FIN = "fechaFin";
	public static final String ID_TAG_CUENTA = "cuenta";
	public static final String ID_TAG_NUM_CUENTA = "numCuenta";

	// VO: Clasificacion
	public static final String ID_TAG_ID_CLASIFICACION = "idClasif";
	// public static final String ID_TAG_ID_ESQUEMA = "idEsquema"; ATRIBUTO YA
	// EXISTENTE EN OTRO VO
	public static final String ID_TAG_SECUENCIAL = "secuencial";
	public static final String ID_TAG_ID_CLASIFICACION_RAIZ = "idClasificacionRaiz";
	public static final String ID_TAG_ID_GRUPO_CLASIFICACION = "idGrupoClasificacion";
	public static final String ID_TAG_ID_FECHA_EFECTIVA = "fechaEfectiva";
	// public static final String ID_TAG_FECHA_FIN = "fechaFin"; ATRIBUTO YA
	// EXISTENTE EN OTRO VO
	public static final String ID_TAG_CODIGO = "codigo";
	// public static final String ID_TAG_NOMBRE_CORTO = "nombreCorto"; ATRIBUTO
	// YA EXISTENTE EN OTRO VO
	// public static final String ID_TAG_NOMBRE = "nombre"; ATRIBUTO YA
	// EXISTENTE EN OTRO VO
	// public static final String ID_TAG_DESCRIPCION = "descripcion"; ATRIBUTO
	// YA EXISTENTE EN OTRO VO
	public static final String ID_TAG_CODIGO_SVF = "codigoSVF";
	public static final String ID_TAG_CODIGO_SIEBEL = "codigoSiebel";
	public static final String ID_TAG_CODIGO_SRP = "codigoSRP";

	// VO: Vaciado
	// public static final String ID_TAG_ID_VACIADO= "idVac"; ATRIBUTO YA
	// EXISTENTE EN OTRO VO
	public static final String ID_TAG_UNIDAD_MEDIDA = "unid";
	public static final String ID_TAG_FECHA_EEFF = "fecEEFF";
	public static final String ID_TAG_FECHA_CREACION = "fecCre";
	public static final String ID_TAG_FECHA_ULTIMA_MODIF = "fecMod";
	public static final String ID_TAG_ID_PARTE_INVOLUCRADA = "idPartIn";
	public static final String ID_TAG_MESES_PER = "mesesPer";
	public static final String ID_TAG_TPO_CARGA_FLG = "tpCargaF";
	public static final String ID_TAG_ID_CLASIFICACION_CALIDAD = "idClaCal";
	public static final String ID_TAG_ID_TPO_BALANCE = "idTpBlce";
	public static final String ID_TAG_PERIODO = "per";
	public static final String ID_TAG_CODIGO_OPERACION = "codOp";
	public static final String ID_TAG_CONSOL_FLG = "consolF";
	public static final String ID_TAG_ID_FUENTE_CONSOL = "idFteCsl";
	public static final String ID_TAG_TPO_PROY = "tpProy";
	public static final String ID_TAG_PERIOCIDAD_FLG = "periodF";
	public static final String ID_TAG_AJUSTADO_FLG = "ajustF";
	public static final String ID_TAG_VERSION = "version";
	public static final String ID_TAG_ID_MONEDA = "idMoneda";
	public static final String ID_TAG_ID_AUDIT = "idAudit";
	public static final String ID_TAG_ID_ESTADO = "idEstado";
	public static final String ID_TAG_ID_USUARIO = "idUsu";
	public static final String ID_TAG_ID_ORIGEN_PROY = "idOrgPry";
	public static final String ID_TAG_DAI_FLG = "daiFlg";
	public static final String ID_TAG_ID_PLANES_DE_CUENTAS = "idPlnCts";
	public static final String ID_TAG_ID_TPO_VACIADO = "idTpVac";
	public static final String ID_TAG_CONFID_FLG = "confidF";
	public static final String ID_TAG_LOG_USUARIO = "logUsu";
	public static final String ID_TAG_NOTA = "nota";
	public static final String ID_TAG_FECHA_INGRESO = "fecIngr";
	public static final String ID_TAG_FECHA_ACTUALIZACION = "FecActuali";
	public static final String ID_TAG_ID_UNIDAD = "IdUnidad";

	public static final String INI_TAG_UNIDAD_MEDIDA = "<unid>";
	public static final String INI_TAG_FECHA_EEFF = "<fecEEFF>";
	public static final String INI_TAG_FECHA_CREACION = "<fecCre>";
	public static final String INI_TAG_FECHA_ULTIMA_MODIF = "<fecMod>";
	public static final String INI_TAG_ID_PARTE_INVOLUCRADA = "<idPartIn>";
	public static final String INI_TAG_MESES_PER = "<mesesPer>";
	public static final String INI_TAG_TPO_CARGA_FLG = "<tpCargaF>";
	public static final String INI_TAG_ID_CLASIFICACION_CALIDAD = "<idClaCal>";
	public static final String INI_TAG_ID_TPO_BALANCE = "<idTpBlce>";
	public static final String INI_TAG_PERIODO = "<per>";
	public static final String INI_TAG_CONSOL_FLG = "<consolF>";
	public static final String INI_TAG_ID_FUENTE_CONSOL = "<idFteCsl>";
	public static final String INI_TAG_TPO_PROY = "<tpProy>";
	public static final String INI_TAG_PERIOCIDAD_FLG = "<periodF>";
	public static final String INI_TAG_AJUSTADO_FLG = "<ajustF>";
	public static final String INI_TAG_VERSION = "<version>";
	public static final String INI_TAG_ID_MONEDA = "<idMoneda>";
	public static final String INI_TAG_ID_AUDIT = "<idAudit>";
	public static final String INI_TAG_ID_ESTADO = "<idEstado>";
	public static final String INI_TAG_ID_USUARIO = "<idUsu>";
	public static final String INI_TAG_ID_ORIGEN_PROY = "<idOrgPry>";
	public static final String INI_TAG_DAI_FLG = "<daiFlg>";
	public static final String INI_TAG_ID_PLANES_DE_CUENTAS = "<idPlnCts>";
	public static final String INI_TAG_ID_TPO_VACIADO = "<idTpVac>";
	public static final String INI_TAG_CONFID_FLG = "<confidF>";
	public static final String INI_TAG_LOG_USUARIO = "<logUsu>";
	public static final String INI_TAG_NUM_CUENTA = "<numCuenta>";
	public static final String INI_TAG_DESCRIP_CUENTA = "<descripCta>";
	public static final String INI_TAG_MONTO_ULT_BCE = "<montoUltBce>";
	public static final String INI_TAG_MONTO = "<monto>";
	public static final String INI_TAG_NOTA = "<nota>";
	public static final String INI_TAG_CALCULADO_FLG = "<calculadoFlg>";
	public static final String INI_TAG_FECHA_INGRESO = "<fecIngr>";

	public static final String FIN_TAG_UNIDAD_MEDIDA = "</unid>";
	public static final String FIN_TAG_FECHA_EEFF = "</fecEEFF>";
	public static final String FIN_TAG_FECHA_CREACION = "</fecCre>";
	public static final String FIN_TAG_FECHA_ULTIMA_MODIF = "</fecMod>";
	public static final String FIN_TAG_ID_PARTE_INVOLUCRADA = "</idPartIn>";
	public static final String FIN_TAG_MESES_PER = "</mesesPer>";
	public static final String FIN_TAG_TPO_CARGA_FLG = "</tpCargaF>";
	public static final String FIN_TAG_ID_CLASIFICACION_CALIDAD = "</idClaCal>";
	public static final String FIN_TAG_ID_TPO_BALANCE = "</idTpBlce>";
	public static final String FIN_TAG_PERIODO = "</per>";
	public static final String FIN_TAG_CONSOL_FLG = "</consolF>";
	public static final String FIN_TAG_ID_FUENTE_CONSOL = "</idFteCsl>";
	public static final String FIN_TAG_TPO_PROY = "</tpProy>";
	public static final String FIN_TAG_PERIOCIDAD_FLG = "</periodF>";
	public static final String FIN_TAG_AJUSTADO_FLG = "</ajustF>";
	public static final String FIN_TAG_VERSION = "</version>";
	public static final String FIN_TAG_ID_MONEDA = "</idMoneda>";
	public static final String FIN_TAG_ID_AUDIT = "</idAudit>";
	public static final String FIN_TAG_ID_ESTADO = "</idEstado>";
	public static final String FIN_TAG_ID_USUARIO = "</idUsu>";
	public static final String FIN_TAG_ID_ORIGEN_PROY = "</idOrgPry>";
	public static final String FIN_TAG_DAI_FLG = "</daiFlg>";
	public static final String FIN_TAG_ID_PLANES_DE_CUENTAS = "</idPlnCts>";
	public static final String FIN_TAG_ID_TPO_VACIADO = "</idTpVac>";
	public static final String FIN_TAG_CONFID_FLG = "</confidF>";
	public static final String FIN_TAG_LOG_USUARIO = "</logUsu>";
	public static final String FIN_TAG_NUM_CUENTA = "</numCuenta>";
	public static final String FIN_TAG_DESCRIP_CUENTA = "</descripCta>";
	public static final String FIN_TAG_MONTO_ULT_BCE = "</montoUltBce>";
	public static final String FIN_TAG_MONTO = "</monto>";
	public static final String FIN_TAG_NOTA = "</nota>";
	public static final String FIN_TAG_CALCULADO_FLG = "</calculadoFlg>";
	public static final String FIN_TAG_FECHA_INGRESO = "</fecIngr>";

	public static final String INI_TAG_CUENTA = "<cuenta>";
	public static final String FIN_TAG_CUENTA = "</cuenta>";
	public static final String INI_TAG_ID_ESQUEMA_PADRE = "<idEsquemaPadre>";
	public static final String FIN_TAG_ID_ESQUEMA_PADRE = "</idEsquemaPadre>";
	public static final String INI_TAG_CODIGO_ESQ = "<codigoEsq>";
	public static final String FIN_TAG_CODIGO_ESQ = "</codigoEsq>";
	public static final String INI_TAG_NOMBRE_CORTO = "<nombreCorto>";
	public static final String FIN_TAG_NOMBRE_CORTO = "</nombreCorto>";
	public static final String INI_TAG_NOMBRE = "<nombre>";
	public static final String FIN_TAG_NOMBRE = "</nombre>";
	public static final String INI_TAG_NOMBRE_LARGO = "<nombreLargo>";
	public static final String FIN_TAG_NOMBRE_LARGO = "</nombreLargo>";
	public static final String INI_TAG_FECHA_EFECTIVA = "<fechaEfectiva>";
	public static final String FIN_TAG_FECHA_EFECTIVA = "</fechaEfectiva>";
	public static final String INI_TAG_FECHA_FIN = "<fechaFin>";
	public static final String FIN_TAG_FECHA_FIN = "</fechaFin>";
	public static final String INI_TAG_REQUEST = "<request>";
	public static final String FIN_TAG_REQUEST = "</request>";
	public static final String INI_TAG_RESPONSE = "<response>";
	public static final String FIN_TAG_RESPONSE = "</response>";
	public static final String INI_TAG_CODIGO_OPERACION = "<codigoOperacion>";
	public static final String FIN_TAG_CODIGO_OPERACION = "</codigoOperacion>";
	public static final String INI_TAG_MENSAJE_OPERACION = "<mensajeOperacion>";
	public static final String FIN_TAG_MENSAJE_OPERACION = "</mensajeOperacion>";
	public static final String INI_TAG_MAPA = "<mapa>";
	public static final String FIN_TAG_MAPA = "</mapa>";
	public static final String INI_TAG_LISTA = "<lista>";
	public static final String FIN_TAG_LISTA = "</lista>";
	public static final String INI_TAG_ESQUEMA = "<esquema>";
	public static final String FIN_TAG_ESQUEMA = "</esquema>";
	public static final String INI_TAG_ID_ESQUEMA = "<idEsquema>";
	public static final String FIN_TAG_ID_ESQUEMA = "</idEsquema>";
	public static final String INI_TAG_MENSAJE = "<mensaje>";
	public static final String FIN_TAG_MENSAJE = "</mensaje>";
	public static final String INI_TAG_CABECERA = "<cabecera>";
	public static final String FIN_TAG_CABECERA = "</cabecera>";
	public static final String INI_TAG_LOG_OPERADOR = "<logOperador>";
	public static final String FIN_TAG_LOG_OPERADOR = "</logOperador>";
	public static final String INI_TAG_RUT_OPERADOR = "<rutOperador>";
	public static final String FIN_TAG_RUT_OPERADOR = "</rutOperador>";
	public static final String INI_TAG_ROL_OPERADOR = "<rolOperador>";
	public static final String FIN_TAG_ROL_OPERADOR = "</rolOperador>";
	public static final String INI_TAG_CODIGO_APP = "<codigoApp>";
	public static final String FIN_TAG_CODIGO_APP = "</codigoApp>";
	public static final String INI_TAG_RUT_CLIENTE = "<rutCliente>";
	public static final String FIN_TAG_RUT_CLIENTE = "</rutCliente>";
	public static final String INI_TAG_RUT_EJECUTIVO = "<rutEjecutivo>";
	public static final String FIN_TAG_RUT_EJECUTIVO = "</rutEjecutivo>";
	public static final String INI_TAG_VACIADO = "<vaciado>";
	public static final String FIN_TAG_VACIADO = "</vaciado>";
	public static final String INI_TAG_ID_VACIADO = "<idVac>";
	public static final String FIN_TAG_ID_VACIADO = "</idVac>";
	public static final String INI_TAG_ID_SESION = "<idSesion>";
	public static final String FIN_TAG_ID_SESION = "</idSesion>";
	public static final String INI_TAG_PRIVILEGIO = "<privilegio>";
	public static final String FIN_TAG_PRIVILEGIO = "</privilegio>";
	public static final String INI_TAG_PERFIL = "<perfil>";
	public static final String FIN_TAG_PERFIL = "</perfil>";
	public static final String INI_TAG_OPERACION = "<operacion>";
	public static final String FIN_TAG_OPERACION = "</operacion>";
	public static final String INI_TAG_MODULO = "<modulo>";
	public static final String FIN_TAG_MODULO = "</modulo>";
	public static final String INI_TAG_MENU = "<menu>";
	public static final String FIN_TAG_MENU = "</menu>";
	public static final String INI_TAG_ID = "<id>";
	public static final String FIN_TAG_ID = "</id>";
	public static final String INI_TAG_ENLACE = "<enlace>";
	public static final String FIN_TAG_ENLACE = "</enlace>";
	public static final String INI_TAG_GLOSA = "<glosa>";
	public static final String FIN_TAG_GLOSA = "</glosa>";
	public static final String INI_TAG_DESCRIPCION = "<descripcion>";
	public static final String FIN_TAG_DESCRIPCION = "</descripcion>";

	// Codigos que se utilizan en el consultaServicios.consultaDAI
	// Parametros de Entrada
	public static final String DAI_IN_CODIGO_158 = "cod158";
	public static final String DAI_IN_CODIGO_18 = "cod18";
	public static final String DAI_IN_CODIGO_25 = "cod25";
	public static final String DAI_IN_CODIGO_36 = "cod36";
	public static final String DAI_IN_DV_CLIENTE = "dv";
	public static final String DAI_IN_RUT_CLIENTE = "rutCliente";
	public static final String DAI_IN_FOLIO = "folio";
	public static final String DAI_IN_PERIODO = "periodo";
	public static final String DAI_IN_CODIGO_611 = "cod611";
	public static final String DAI_IN_CODIGO_305 = "cod305";
	// Parametros de Salida
	public static final String DAI_CODIGO_9928 = "COD_9928";
	public static final String DAI_CODIGO_9920 = "COD_9920";
	public static final String DAI_CODIGO_9306 = "COD_9306";
	public static final String DAI_CODIGO_8898 = "COD_8898";
	public static final String DAI_CODIGO_8892 = "COD_8892";
	public static final String DAI_CODIGO_8891 = "COD_8891";
	public static final String DAI_CODIGO_8876 = "COD_8876";
	public static final String DAI_CODIGO_8866 = "COD_8866";
	public static final String DAI_CODIGO_8811 = "COD_8811";
	public static final String DAI_CODIGO_780 = "COD_780";
	public static final String DAI_CODIGO_619 = "COD_619";
	public static final String DAI_CODIGO_618 = "COD_618";
	public static final String DAI_CODIGO_611 = "COD_611";
	public static final String DAI_CODIGO_547 = "COD_547";
	public static final String DAI_CODIGO_494 = "COD_494";
	public static final String DAI_CODIGO_492 = "COD_492";
	public static final String DAI_CODIGO_467 = "COD_467";
	public static final String DAI_CODIGO_461 = "COD_461";
	public static final String DAI_CODIGO_315 = "COD_315";
	public static final String DAI_CODIGO_312 = "COD_312";
	public static final String DAI_CODIGO_306 = "COD_306";
	public static final String DAI_CODIGO_305 = "COD_305";
	public static final String DAI_CODIGO_304 = "COD_304";
	public static final String DAI_CODIGO_301 = "COD_301";
	public static final String DAI_CODIGO_198 = "COD_198";
	public static final String DAI_CODIGO_170 = "COD_170";
	public static final String DAI_CODIGO_169 = "COD_169";
	public static final String DAI_CODIGO_162 = "COD_162";
	public static final String DAI_CODIGO_161 = "COD_161";
	public static final String DAI_CODIGO_158 = "COD_158";
	public static final String DAI_CODIGO_157 = "COD_157";
	public static final String DAI_CODIGO_155 = "COD_155";
	public static final String DAI_CODIGO_110 = "COD_110";
	public static final String DAI_CODIGO_98 = "COD_98";
	public static final String DAI_CODIGO_87 = "COD_87";
	public static final String DAI_CODIGO_85 = "COD_85";
	public static final String DAI_CODIGO_55 = "COD_55";
	public static final String DAI_CODIGO_53 = "COD_53";
	public static final String DAI_CODIGO_48 = "COD_48";
	public static final String DAI_CODIGO_15 = "COD_15";
	public static final String DAI_CODIGO_14 = "COD_14";
	public static final String DAI_CODIGO_13 = "COD_13";
	public static final String DAI_CODIGO_09 = "COD_09";
	public static final String DAI_CODIGO_08 = "COD_08";
	public static final String DAI_CODIGO_07 = "COD_07";
	public static final String DAI_CODIGO_06 = "COD_06";
	public static final String DAI_CODIGO_05 = "COD_05";
	public static final String DAI_CODIGO_03 = "COD_03";
	public static final String DAI_CODIGO_02 = "COD_02";
	public static final String DAI_CODIGO_01 = "COD_01";

	// Constantes para el manejo del map generado con los datos obtenidos del
	// servicio OSB DAI.
	public static final String IVA_PERIODO = new String("periodo");
	public static final String IVA_MES = new String("mes");
	public static final String IVA_CTA20 = new String("Cuenta20");
	public static final String IVA_CTA108 = new String("Cuenta108");
	public static final String IVA_CTA109 = new String("Cuenta109");
	public static final String IVA_CTA111 = new String("Cuenta111");
	public static final String IVA_CTA112 = new String("Cuenta112");
	public static final String IVA_CTA129 = new String("Cuenta129");
	public static final String IVA_CTA142 = new String("Cuenta142");
	public static final String IVA_CTA144 = new String("Cuenta144");
	public static final String IVA_CTA154 = new String("Cuenta154");
	public static final String IVA_CTA501 = new String("Cuenta501");
	public static final String IVA_CTA502 = new String("Cuenta502");
	public static final String IVA_CTA510 = new String("Cuenta510");
	public static final String IVA_CTA513 = new String("Cuenta513");
	public static final String IVA_CTA517 = new String("Cuenta517");
	public static final String IVA_CTA517RET = new String("Cuenta517Ret");
	public static final String IVA_CTA520 = new String("Cuenta520");
	public static final String IVA_CTA521 = new String("Cuenta521");
	public static final String IVA_CTA525 = new String("Cuenta525");
	public static final String IVA_CTA528 = new String("Cuenta528");
	public static final String IVA_CTA532 = new String("Cuenta532");
	public static final String IVA_CTA535 = new String("Cuenta535");
	public static final String IVA_CTA553 = new String("Cuenta553");
	public static final String IVA_CTA560 = new String("Cuenta560");
	public static final String IVA_CTA562 = new String("Cuenta562");
	public static final String IVA_CTA587 = new String("Cuenta587");
	public static final String IVA_CTA595 = new String("Cuenta595");
	public static final String IVA_FECHA_MODIF = new String("fechaModif");
	public static final String IVA_TOTAL_MODIF = new String("totalModif");

	// Llaves de los XML en rating

	// #>>>>>>>>>>>> RATING FINANCIERO <<<<<<<<<<<<<<<<

	// # Servicio 300020 // Nombre de Servicio: consulta plantilla rating//
	// Clase Invoca: Rating Financiero.
	// #REQUEST
	public static final String PLANT_RTG_FIN_KEY_TPO_RTG = "tpo_rat";
	public static final String PLANT_RTG_FIN_KEY_DUAL_CLIENTE = "cliente";
	// #RESPONSE
	// # Servicio 300130 // Nombre de Servicio:CONFIRMAR RATING FINANCIERO //
	// Clase Invoca: Rating Finanviero View.
	// #REQUEST
	public static final String CONF_FIN_KEY_DUAL_CLIENTE = "cliente";
	public static final String CONF_FIN_KEY_ID_VACIADO = "id_vac";
	// #RESPONSE
	public static final String CONF_FIN_KEY_FLAG_ACTUALIZADO = "flag_actl";
	public static final String CONF_FIN_KEY_ID_PLANTILLA = "id_plant";
	public static final String CONF_FIN_KEY_ID_TIPO = "id_tipo";

	// # Servicio 300140 // Nombre de Servicio: CONFIRMAR VACIADO VIGENTE RATING
	// financiero// Clase Invoca: RatingFinancieroView & RatingFinancieroView.
	// #REQUEST
	public static final String CONF_VIG_VAC_KEY_ID_PLANTILLA = "id_plant";
	public static final String CONF_VIG_VAC_KEY_ID_TIPO = "id_tipo";
	public static final String CONF_VIG_VAC_KEY_CLIENTE = "cliente";
	public static final String CONF_VIG_VAC_KEY_ID_VACIADO = "id_vac";
	// #RESPONSE
	public static final String CONF_VIG_VAC_KEY_FLAG_VACIADO_VIGENTE = "flg_vac_vig";

	// # Servicio 300120 // Nombre de Servicio: CALCULAR RATING FINANCIERO //
	// Clase Invoca: RatingFinancieroPanel & RatingFinancieroPanelPyme .
	// #REQUEST
	public static final String CALC_FIN_KEY_RESPONSABLE = "resp";
	public static final String CALC_FIN_KEY_CLIENTE = "cliente";
	public static final String CALC_FIN_KEY_ID_VACIADO = "id_vac";

	// #KEY BASES
	public static final String RTG_FIN_KEY_DATA_RATING_FIN = "rtg_fin";
	public static final String RTG_FIN_KEY_ID_RATING = "id_rtg";
	public static final String RTG_FIN_KEY_NOTA_FINANCIERA = "nt_fin";
	public static final String RTG_FIN_KEY_FECHA_FINANCIERA = "fch_fin";
	public static final String RTG_FIN_KEY_TIPO_VACIADO = "tp_vac";
	public static final String RTG_FIN_KEY_AJUSTADO = "ajust";

	public static final String RTG_FIN_KEY_LST_VACIADOS = "lst_vac";
	public static final String RTG_FIN_KEY_ID_VACIADO = "id_vac";
	public static final String RTG_FIN_KEY_FECHA_PERIODO = "fch_perio";
	public static final String RTG_FIN_KEY_LST_ID_VACIADO = "id_vac";
	public static final String RTG_FIN_KEY_ID_TIPO_VACIADO = "id_tp_vac";
	public static final String RTG_FIN_KEY_LST_VACIADO_AJUSTADO = "ajust";
	public static final String RTG_FIN_KEY_FECHA_PROYECCION = "fch_proyc";
	public static final String RTG_FIN_KEY_FUENTE = "fuente";
	public static final String RTG_FIN_KEY_ESTADO = "estado";
	// AGREGAR DOCUMENTOS
	public static final String RTG_FIN_KEY_ID_ESTADO = "id_est";
	public static final String RTG_FIN_KEY_FLAG_VAC_CAL = "flag_vac_cal";

	// #SOLO PARA RATING FINANCIERO GENERAL
	public static final String RTG_FIN_KEY_RATING_PROYECTADO = "rtg_proy";
	public static final String RTG_FIN_KEY_ID_RTG_PROYECTADO = "id_rtg";
	public static final String RTG_FIN_KEY_NOTA_PROYECTADO = "nt_proy";
	public static final String RTG_FIN_KEY_FECHA_PROYECTADO = "fch_proy";

	// #>>>>>>>>>>>>>>>>>>>>>>>>>>>> RATING NEGOCIO <<<<<<<<<<<<<<<<<<<<<<<
	// # Servicio 300060 // Nombre de Servicio: OBTENER MATRIZ RATING NEGOCIO //
	// Clase Invoca: .
	// #REQUEST
	public static final String OBT_MATRIZ_NEG_KEY_CLIENTE = "rut_cliente";
	public static final String OBT_MATRIZ_NEG_KEY_ID_BANCA = "id_banca";
	public static final String OBT_MATRIZ_NEG_KEY_LOG_OPE = "logOpe";
	// #RESPONSE
	public static final String OBT_MATRIZ_NEG_KEY_ID_MATRIZ = "id_matriz";
	public static final String OBT_MATRIZ_NEG_KEY_LISTA_TEMAS = "temas";
	public static final String OBT_MATRIZ_NEG_KEY_ID_TEMA = "idTem";
	public static final String OBT_MATRIZ_NEG_KEY_NOMBRE_TEMA = "nomTema";
	public static final String OBT_MATRIZ_NEG_KEY_ORDEN_TEMA = "orden_tema";
	public static final String OBT_MATRIZ_NEG_KEY_LISTA_PREGUNTAS = "lstPreg";
	public static final String OBT_MATRIZ_NEG_KEY_NOMBRE_PREGUNTA = "nom";
	public static final String OBT_MATRIZ_NEG_KEY_ID = "id";
	public static final String OBT_MATRIZ_NEG_KEY_ORDEN_PREGUNTA = "orden_preg";
	public static final String OBT_MATRIZ_NEG_KEY_LISTA_ALTERNATIVAS = "lstAlt";
	public static final String OBT_MATRIZ_NEG_KEY_NOMBRE_ALTERNATIVA = "nomAlt";
	public static final String OBT_MATRIZ_NEG_KEY_ID_ALTERNATIVA = "id_alt";
	public static final String OBT_MATRIZ_NEG_KEY_ORDEN_ALTERNATIVA = "orden_alt";
	public static final String OBT_MATRIZ_NEG_KEY_FECHA_RATING_NEG = "fecRatNeg";
	public static final String OBT_MATRIZ_NEG_KEY_RESPON_RATING_NEG = "respRatNeg";
	public static final String OBT_MATRIZ_NEG_KEY_TIPO_EVALUACION = "tipoEval";
	public static final String OBT_MATRIZ_NEG_KEY_MSG_ALERTA = "msgAlerta";
	public static final String OBT_MATRIZ_NEG_KEY_OPC_SELECCIONADA = "opcSel";
	public static final String OBT_MATRIZ_NEG_KEY_NOTA = "nota";

	// #RESPONSE

	// #Para este servicio su respons se compone de las key bases del modulo.

	// # Servicio 300070 // Nombre de Servicio: RECALCULAR RATING NEGOCIO //
	// Clase Invoca: Rating Negocio view.
	// #REQUEST
	public static final String CALCR_RTG_NEG_KEY_PLANTILLA = "plan";
	public static final String CALCR_RTG_NEG_KEY_DUAL_CLIENTE = "cliente";
	public static final String CALCR_RTG_NEG_KEY_ID_ESTADO = "id_est";
	public static final String CALCR_RTG_NEG_KEY_RESPONSABLE = "resp";
	public static final String CALCR_RTG_NEG_KEY_ID_PLANTILLA = "id_plant";
	public static final String CALCR_RTG_NEG_KEY_LISTA_TEMAS = "lst_tem";
	public static final String CALCR_RTG_NEG_KEY_ID_TEMA = "id_tem";
	public static final String CALCR_RTG_NEG_KEY_LST_PREG_RESP = "lst_preg_resp";
	public static final String CALCR_RTG_NEG_KEY_ID_PREGUNTA = "id_preg";
	public static final String CALCR_RTG_NEG_KEY_ID_RESPUESTA = "id_resp";

	// #RESPONSE

	// # Servicio 300190 // Nombre de Servicio: Consulta rating Negocio // Clase
	// Invoca: .
	// #REQUEST
	public static final String CONSUL_RTG_NEG_KEY_CLIENTE = "cliente";
	public static final String CONSUL_RTG_NEG_KEY_ID_RATING = "id_rtg";
	public static final String CONSUL_RTG_NEG_KEY_IDPLANTILLA = "id_plant";
	public static final String CONSUL_RTG_NEG_KEY_NOMBRE = "nombre";
	// #RESPONSE
	public static final String CONSUL_RTG_NEG_KEY_LST_TEMA = "lst_tem";
	public static final String CONSUL_RTG_NEG_KEY_ID_TEMA = "id_tem";
	public static final String CONSUL_RTG_NEG_KEY_LST_PREG_RESP = "lst_preg_resp";
	public static final String CONSUL_RTG_NEG_KEY_ID_PREGUNTA = "id_preg";
	public static final String CONSUL_RTG_NEG_KEY_ID_RESPUESTA = "id_resp";

	// #>>>>>KEY BASICAS RESPONS MODULO RATING NEGOCIO<<<<<

	public static final String RTG_NEG_KEY_ID_PARCIAL = "id_parc";

	public static final String RTG_NEG_KEY_RTG_ANTERIOR = "rtg_ant";
	public static final String RTG_NEG_KEY_RESPONSABLE = "resp";
	public static final String RTG_NEG_KEY_FECHA_ANTERIOR = "fch_ant";
	public static final String RTG_NEG_KEY_NOTA_ANTERIOR = "nt_neg";

	public static final String RTG_NEG_KEY_PLANTILLA_NEGOCIO = "plant_neg";
	public static final String RTG_NEG_KEY_NOMBRE_PLANTILLA = "nombre";
	public static final String RTG_NEG_KEY_VERSION_PLANTILLA = "version";
	public static final String RTG_NEG_KEY_USUARIO = "usuario";
	public static final String RTG_NEG_KEY_RTG_UTILIZAR = "rtg_utlz";
	public static final String RTG_NEG_KEY_UF_DESDE = "uf_desde";
	public static final String RTG_NEG_KEY_UF_HASTA = "uf_hasta";
	public static final String RTG_NEG_KEY_AJUSTE = "ajuste";
	public static final String RTG_NEG_KEY_NOTA_TOPE = "nt_tpe";
	public static final String RTG_NEG_KEY_TEMAS = "temas";
	// #
	public static final String RTG_NEG_KEY_LST_TEMAS = "lst_tem";
	public static final String RTG_NEG_KEY_DATA_TEMA = "dt_tem";
	public static final String RTG_NEG_KEY_NOMBRE_TEMA = "nom_tem";
	public static final String RTG_NEG_KEY_ID_TEMA = "id_tem";
	public static final String RTG_NEG_KEY_PESO_PORCENTUAL_TEMA = "ps_porc";
	// #
	public static final String RTG_NEG_KEY_LST_PREGUNTAS = "lst_preg";
	public static final String RTG_NEG_KEY_DATA_PREGUNTA = "dt_preg";
	public static final String RTG_NEG_KEY_PESO_PORCENTUAL_PREGUNTA = "ps_porc";
	public static final String RTG_NEG_KEY_NOMBRE_PREGUNTA = "nom_preg";
	public static final String RTG_NEG_KEY_ID_PREGUNTA = "id_preg";
	// #
	public static final String RTG_NEG_KEY_LST_ALTERNATIVA = "lst_alt";
	public static final String RTG_NEG_KEY_DATA_RESPUESTA = "resp";
	public static final String RTG_NEG_KEY_NOMBRE_RESPUESTA = "nom_resp";
	public static final String RTG_NEG_KEY_ID_RESPUESTA = "id_resp";
	public static final String RTG_NEG_KEY_PUNTAJE_RESPUESTA = "punt";

	public static final String RTG_NEG_KEY_PERFIL_CLIENTE = "pf_cli";
	public static final String RTG_NEG_KEY_ID_ESTADO = "id_est";

	public static final String RTG_NEG_KEY_DATA_PREG_SELEC = "preg_selc";

	// #>>>>>>>>RATING INDIVIDUAL<<<<<<<<<<

	// #Servicios del Modulo de Rating Individual

	// # Servicio 300030 // Nombre de Servicio: Consulta CONSULTAR RATING
	// INDIVIDUAL // Clase Invoca:Rating Individual View.
	// #REQUEST CASO GENERAL
	public static final String CONSUL_RTG_IND_GENERAL_KEY_DUAL_CLIENTE = "cliente";
	public static final String CONSUL_RTG_IND_GENERAL_KEY_ID_RTG = "id_rtg";
	public static final String CONSUL_RTG_IND_GENERAL_KEY_ID_TIPO_RATING = "id_tpo_rat";

	// #REQUEST CASO PYME
	public static final String CONSUL_RTG_IND_PYME_KEY_DUAL_CLIENTE = "cliente";
	public static final String CONSUL_RTG_IND_PYME_KEY_DUAL_ID_RATING = "id_rtg";
	public static final String CONSUL_RTG_IND_PYME_KEY_TIPO_RATING = "tp_rtg";
	public static final String CONSUL_RTG_IND_PYME_KEY_ID_PLANTILLA = "id_plant";

	// # Servicio 300040 // Nombre de Servicio: GRABAR RATING INDIVIDUAL //
	// Clase Invoca: Rating individual View.
	// #REQUEST
	public static final String GRAB_IND_GENERAL_KEY_ID_RATING = "id_rtg";
	public static final String GRAB_IND_GENERAL_KEY_COMENTARIO = "coment";
	public static final String GRAB_IND_GENERAL_KEY_ID_MOTIVO = "id_mot";
	public static final String GRAB_IND_GENERAL_KEY_TIPO_RATING = "tp_rtg";
	public static final String GRAB_IND_GENERAL_KEY_CLIENTE = "cliente";
	public static final String GRAB_IND_GENERAL_KEY_NOTA_INDIVIDUAL = "nt_ind";
	public static final String GRAB_IND_GENERAL_KEY_ID_PLANTILLA = "id_plant";

	// #REQUEST CASO PYME
	public static final String GRAB_IND_PYME_KEY_ID_RATING = "id_rtg";
	public static final String GRAB_IND_PYME_KEY_COMENTARIO = "coment";
	public static final String GRAB_IND_PYME_KEY_ID_MOTIVO = "id_mot";
	public static final String GRAB_IND_PYME_KEY_TIPO_RATING = "tp_rtg";
	public static final String GRAB_IND_PYME_KEY_CLIENTE = "cliente";
	public static final String GRAB_IND_PYME_KEY_NOTA_INDIVIDUAL = "nt_ind";
	public static final String GRAB_IND_PYME_KEY_ID_PLANTILLA = "id_plant";

	// # Servicio 300100 // Nombre de Servicio: CONFIRMAR RATING INDIVIDUAL //
	// Clase Invoca: Rating individual View.
	// #REQUEST
	public static final String CONFIR_RTG_IND_KEY_DUAL_CLIENTE = "cliente";
	public static final String CONFIR_RTG_IND_KEY_ID_RATING = "id_rat";
	public static final String CONFIR_RTG_IND_KEY_NOTA_INDIVIDUAL = "nt_ind";
	public static final String CONFIR_RTG_IND_KEY_TIPO_RATING = "tp_rtg";
	public static final String CONFIR_RTG_IND_KEY_ID_PLANTILLA = "id_plant";
	public static final String CONFIR_RTG_IND_KEY_COMENTARIO = "coment";
	public static final String CONFIR_RTG_IND_KEY_IDMOTIVO = "id_mot";

	// # Servicio 300110 // Nombre de Servicio: CONSULTA ALARMAS RATING
	// PARCIALES // Clase Invoca:
	// #REQUEST
	public static final String ALER_RTG_PARCIAL_KEY_DUAL_CLIENTE = "cliente";
	// #RESPONSE
	public static final String ALER_RTG_PARCIAL_KEY_LISTA_ALERTAS = "lst_aler";
	public static final String ALER_RTG_PARCIAL_KEY_DATA_ALERTA = "alerta";
	public static final String ALER_RTG_PARCIAL_KEY_COMENTARIO = "coment";
	public static final String ALER_RTG_PARCIAL_KEY_INDEX = "index";

	// #>>>>>KEY BASICAS RESPONS MODULO RATING INDIVIDUAL<<<<<

	public static final String RTG_IND_KEY_DETALLE_RATING = "dt_rtg";
	public static final String RTG_IND_KEY_DATA_RTG_FINANCIERO = "rtg_fin";
	public static final String RTG_IND_KEY_ID_PARCIAL_FINANCIERO = "id_parc";
	public static final String RTG_IND_KEY_NOMBRE_FINANCIERO = "nombre";
	public static final String RTG_IND_KEY_FECHA_FINANCIERA = "fch_fin";
	public static final String RTG_IND_KEY_RESPONSABLE_FINANCIERO = "resp";
	public static final String RTG_IND_KEY_PONDERADO_FINANCIERO = "pond_fin";
	public static final String RTG_IND_KEY_NOTA_FINANCIERA = "nt_fin";
	public static final String RTG_IND_KEY_ID_ESTADO_FIN = "id_est";
	// #
	public static final String RTG_IND_KEY_DATA_RTG_FINANCIERO_PROY = "rtg_fin_proy";
	public static final String RTG_IND_KEY_ID_PARCIAL_PROY = "id_parc";
	public static final String RTG_IND_KEY_NOMBRE_PROYECTADO = "nombre";
	public static final String RTG_IND_KEY_FECHA_FIN_PROYECTADO = "fch_fin_proy";
	public static final String RTG_IND_KEY_RESPONSABLE_PROYECTADO = "resp";
	public static final String RTG_IND_KEY_PONDERADO_PROYECTADO = "pond_proy";
	public static final String RTG_IND_KEY_NOTA_FIN_PROYECTADO = "nt_fin_proy";
	public static final String RTG_IND_KEY_ID_ESTADO_PROYECTADO = "id_est";
	// #
	public static final String RTG_IND_KEY_DATA_RATING_NEGOCIO = "rtg_neg";
	public static final String RTG_IND_KEY_ID_PARCIAL_NEG = "id_parc";
	public static final String RTG_IND_KEY_NOMBRE_NEGOCIO = "nombre";
	public static final String RTG_IND_KEY_FECHA_NEGOCIO = "fch_neg";
	public static final String RTG_IND_KEY_RESPONSABLE_NEGOCIO = "resp";
	public static final String RTG_IND_KEY_PONDERADO_NEGOCIO = "pond_neg";
	public static final String RTG_IND_KEY_NOTA_NEGOCIO = "nt_neg";
	public static final String RTG_IND_KEY_ID_ESTADO_NEGOCIO = "id_est";
	// #
	public static final String RTG_IND_KEY_DATA_RATING_COMP = "rtg_comp";
	public static final String RTG_IND_KEY_ID_PARCIAL_COMP = "id_parc";
	public static final String RTG_IND_KEY_NOMBRE_COMP = "nombre";
	public static final String RTG_IND_KEY_FECHA_COMP = "fch_comp";
	public static final String RTG_IND_KEY_RESPONSABLE_COMP = "resp";
	public static final String RTG_IND_KEY_PONDERADO_COMP = "pond_comp";
	public static final String RTG_IND_KEY_NOTA_COMP = "nt_comp";
	public static final String RTG_IND_KEY_ID_ESTADO_COMP = "id_est";
	// #
	public static final String RTG_IND_KEY_DATA_RTG_POND = "rtg_pond";
	public static final String RTG_IND_KEY_FECHA_POND = "fch_pond";
	public static final String RTG_IND_KEY_RESPONSABLE_POND = "resp";
	public static final String RTG_IND_KEY_TOTAL_PONDERADO = "pond";
	public static final String RTG_IND_KEY_NOTA_POND = "nt_pond";
	// #
	public static final String RTG_IND_KEY_DATA_RTG_AJUSTADO = "rtg_ajus";
	public static final String RTG_IND_KEY_FECHA_AJUSTADO = "fch_ajus";
	public static final String RTG_IND_KEY_RESPONSABLE_AJUSTADO = "resp";
	public static final String RTG_IND_KEY_PONDERADO_AJUSTADO = "pond";
	public static final String RTG_IND_KEY_NOTA_AJUSTADO = "nt_ajus";
	// #
	public static final String RTG_IND_KEY_DATA_RTG_IND_DUAL_MODELADO = "rtg_ind_mod";
	public static final String RTG_IND_KEY_FECHA_MODELADO = "fch_ind_mod";
	public static final String RTG_IND_KEY_RESPONSABLE_MODELADO = "resp";
	public static final String RTG_IND_KEY_PONDERADO_MODELADO = "pond";
	public static final String RTG_IND_KEY_NOTA_MODELADO = "nt_ind_mod";
	public static final String RTG_IND_KEY_ID_ESTADO_MODELADO = "id_est";
	// #
	public static final String RTG_IND_KEY_DATA_RTG_INDIVIDUAL = "rtg_ind";
	public static final String RTG_IND_KEY_FECHA_INDIVIDUAL = "fch_ind";
	public static final String RTG_IND_KEY_RESPONSABLE_INDIVIDUAL = "resp";
	public static final String RTG_IND_KEY_PONDERADO_INDIVIDUAL = "pond";
	public static final String RTG_IND_KEY_NOTA_INDIVIDUAL = "nt_ind";
	public static final String RTG_IND_KEY_ID_ESTADO_IND = "id_est";
	public static final String RTG_IND_KEY_ESTADO = "estado";
	// #
	public static final String RTG_IND_KEY_DATA_ULTIMO_BALANCE_ANUAL = "ult_bal_anual";
	public static final String RTG_IND_KEY_PATRIMONIO_BALANCE_ANUAL = "patrim";
	public static final String RTG_IND_KEY_VENTAS_BALANCE_ANUAL = "ventas";
	public static final String RTG_IND_KEY_ACTIVOS_BALANCE_ANUAL = "activos";
	public static final String RTG_IND_KEY_COLOCACIONES_BALANCE_ANUAL = "colc";
	public static final String RTG_IND_KEY_COLC_SBIF = "colc_sbif";
	public static final String RTG_IND_KEY_COMENTARIO_BALANCE_ANUAL = "coment";
	public static final String RTG_IND_KEY_LST_MOTIVOS_BALANCE_ANUAL = "lst_mot";
	public static final String RTG_IND_KEY_ID_MOTIVO_BALANCEANUAL = "id_mot";
	public static final String RTG_IND_KEY_MOTIVO_BALANCE_ANUAL = "motivo";
	public static final String RTG_IND_KEY_ESTADO_BALANCE_ANUAL = "estado";
	public static final String RTG_IND_KEY_ID_ESTADO_BALANCE_ANUAL = "id_est";
	public static final String RTG_IND_KEY_FCH_ULTIMO_BALANCE_ANUAL = "fch_ult_bal_anual";

	public static final String RTG_IND_KEY_DATA_DETALLE_RTG = "dt_rating";
	public static final String RTG_IND_KEY_DATA_DETALLE_EVAL = "dt_rating";
	public static final String RTG_IND_KEY_ID_PARCIAL = "id_parc";

	// >>>>>>>>>>>>>>>>>>>>RATING COMPORTAMIENTO<<<<<<<<<<<<<<<<<<<<<<<<

	// Servicio 300050 // Nombre de Servicio: Obtener Rating Comportamiento //
	// Clase Invoca: RatingComportamientoView, RatingComportamientoPanel
	// REQUEST
	public static final String OBT_RTG_COMP_KEY_CLIENTE = "cliente";
	public static final String OBT_RTG_COMP_KEY_ID_PLANTILLA = "id_plant";
	// RESPONSE
	public static final String OBT_RTG_COMP_KEY_FLG_VERIFICAR_ANTIGUEDAD_MAXIMA = "flag";

	// Servicio 300080 // Nombre de Servicio: Consulta Rating Comportamiento
	// Seleccionado // Clase Invoca: RatingComportamientoView,
	// RatingComportamientoPanel.
	// REQUEST
	public static final String CONS_RTG_COMP_KEY_CLIENTE = "cliente";
	public static final String CONS_RTG_COMP_KEY_ID_PLANTILLA = "id_plant";
	public static final String CONS_RTG_COMP_KEY_ID_RATING = "id_rat";
	public static final String CONS_RTG_COMP_KEY_ID_COMPORTAMIENTO = "id_comp";
	// RESPONSE
	public static final String CONS_RTG_COMP_KEY_COMPORTAMIENTO_ANTERIOR = "ant_comp";
	public static final String CONS_RTG_COMP_KEY_COMPORTAMIENTO_SELECCIONADO = "comp_selec";
	public static final String CONS_RTG_COMP_KEY_FLAG_EXISTE_COMPORTAMIENTO = "flg_exis";
	public static final String CONS_RTG_COMP_KEY_FLAG_OBTENIDO_COMPORTAMIENTO = "flg_obt";

	// #>>>>>KEY BASICAS RESPONS MODULO RATING COMPORTAMIENTO<<<<<

	public static final String RTG_COMP_KEY_LISTA_COMPORTAMIENTO = "lst_comp";
	public static final String RTG_COMP_KEY_DETALLE_COMPORTAMIENTO = "dt_comp";
	public static final String RTG_COMP_KEY_FECHA = "fecha";
	public static final String RTG_COMP_KEY_DEUDA_BANCO = "deu_banc";
	public static final String RTG_COMP_KEY_DEUDA_SBIF = "deu_sbif";
	public static final String RTG_COMP_KEY_PUNTAJE = "punt";
	public static final String RTG_COMP_KEY_NOTA_COMPORTAMIENTO = "nt_comp";
	public static final String RTG_COMP_KEY_DESCRIPCION = "descrip";
	public static final String RTG_COMP_KEY_DETALLE_NOTA_ACTUAL = "nt_act";
	public static final String RTG_COMP_KEY_NOTA_RATING_COMPORTAMIENTO = "nt_rat_comp";

	public static final String RTG_COMP_KEY_LISTA_MENSAJES = "lstMsg";
	public static final String RTG_COMP_KEY_MENSAJE = "msg";

	// #>>>>>>>>RATING GRUPAL<<<<<<<<<<

	// # Servicio 300090 // Nombre de Servicio: Obtener Rating Grupal // Clase
	// Invoca: RatingGrupalView.
	// #REQUEST // Clase Invoca: RatingCliente, GrupoEmpresas, RatingGrupal,
	// RatingClientePyme, GrupoEmpresasPyme
	public static final String OBT_RTG_GRP_KEY_CLIENTE = "cliente";
	public static final String OBT_RTG_GRP_KEY_ID_PLANTILLA = "id_plant";

	// #RESPONSE
	public static final String OBT_RTG_GRP_KEY_RTG_CLI_FLAG_RATING_INDIVIFUAL_VIGENTE = "flg_ind_vig";

	// # Servicio 300150 // Nombre de Servicio: Buscar Empresa Rating Grupal //
	// Clase Invoca: .
	// #REQUEST
	public static final String BSR_RTG_GRP_KEY_CLIENTE = "cliente";
	public static final String BSR_RTG_GRP_KEY_ID_PLANTILLA = "id_plant";
	public static final String BSR_RTG_GRP_KEY_RUT = "rut";
	public static final String BSR_RTG_GRP_KEY_RAZON_SOCIAL = "raz_soc";

	// #RESPONSE
	public static final String BSR_RTG_GRP_KEY_FLAG_BUSQUEDA = "flg_bsq";
	public static final String BSR_RTG_GRP_KEY_BUSQUEDA_EMPRESA = "bsq_emp";

	// # Servicio 300180 // Nombre de Servicio: Agregar Empresa Rating Grupal//
	// Clase Invoca: GrupoEmpresas, GrupoEmpresasPyme , BusquedaEmpresas . .
	// #REQUEST
	public static final String AGR_RTG_GRP_KEY_CLIENTE = "cliente";
	public static final String AGR_RTG_GRP_KEY_ID_PLANTILLA = "id_plant";
	public static final String AGR_RTG_GRP_KEY_LISTA_AGREGAR_EMPRESA = "lst_agr_emp";

	// #RESPONSE
	public static final String AGR_RTG_GRP_KEY_ID_PARTE_INVOLUCRADAS = "id_part_inv";

	// # Servicio 300200 // Nombre de Servicio: Calcular Rating Grupal// Clase
	// Invoca: GrupoEmpresas, GrupoEmpresasPyme.
	// #REQUEST
	public static final String CALR_RTG_GRP_KEY_CLIENTE = "cliente";
	public static final String CALR_RTG_GRP_KEY_ID_PLANTILLA = "id_plant";
	public static final String CALR_RTG_GRP_KEY_LISTA_AGREGAR_EMPRESA = "lst_agr_emp";

	// #RESPONSE
	public static final String CALR_RTG_GRP_KEY_FLAG_PARCIALES_VIGENTE = "flg_parc_vig";

	// # Servicio 300160 // Nombre de Servicio: Grabar Rating Grupal// Clase
	// Invoca:RatingGrupalView, GrupoEmpresas, GrupoEmpresasPyme,RatingCliente,
	// RatingGrupal,RatingClientePyme.
	// #REQUEST
	public static final String GRB_RTG_GRP_KEY_CLIENTE = "cliente";
	public static final String GRB_RTG_GRP_KEY_ID_PLANTILLA = "id_plant";
	public static final String GRB_RTG_GRP_KEY_LISTA_AGREGAR_EMPRESA = "lst_agr_emp";
	public static final String GRB_RTG_GRP_KEY_COMENTARIOS = "coment";
	public static final String GRB_RTG_GRP_KEY_ID_MOTIVO = "id_mot";
	public static final String GRB_RTG_GRP_KEY_NOTA_RATING_GRUPAL = "nt_rtg_grp";
	public static final String GRB_RTG_GRP_KEY_RATING_GRUPAL = "rtg_grp";

	// #RESPONSE
	public static final String GRB_RTG_GRP_KEY_FLAG_CALCULADO = "flg_cal";

	// # Servicio 300170 // Nombre de Servicio: Confirmar Rating Grupal// Clase
	// Invoca:RatingGrupalView, GrupoEmpresas, GrupoEmpresasPyme,RatingCliente,
	// RatingGrupal,RatingClientePyme.
	// #REQUEST
	public static final String CONF_RTG_GRP_KEY_CLIENTE = "cliente";
	public static final String CONF_RTG_GRP_KEY_ID_PLANTILLA = "id_plant";
	public static final String CONF_RTG_GRP_KEY_LISTA_AGREGAR_EMPRESA = "lst_agr_emp";
	public static final String CONF_RTG_GRP_KEY_COMENTARIOS = "coment";
	public static final String CONF_RTG_GRP_KEY_ID_MOTIVO = "id_mot";
	public static final String CONF_RTG_GRP_KEY_NOTA_RATING_GRUPAL = "nt_rtg_grp";
	public static final String CONF_RTG_GRP_KEY_RATING_GRUPAL = "rtg_grp";

	// #RESPONSE
	public static final String CONF_RTG_GRP_KEY_FLAG_CALCULADO = "flg_cal";
	public static final String CONF_RTG_GRP_KEY_VIGENCIA = "vigenc";

	// #KEY BASES GENERAL
	public static final String RTG_GRP_KEY_RTG_CLI_PROBABILIDAD_DEFAULT = "prob_def";
	public static final String RTG_GRP_KEY_RTG_CLI_ACTIVOS = "activos";

	public static final String RTG_GRP_KEY_GRP_EMP_PROBABILIDAD_DEFAULT = "prob_def";
	public static final String RTG_GRP_KEY_GRP_EMP_ACTIVOS = "activos";
	public static final String RTG_GRP_KEY_GRP_EMP_DEFAULT_PONDERADO_GRUPAL = "def_pond_grp";
	public static final String RTG_GRP_KEY_GRP_EMP_NOTA_RATING_GRUPAL = "nt_rat_grp";

	// #KEY BASES PYME
	public static final String RTG_GRP_KEY_RTG_CLI_NOMBRE = "nombre";
	public static final String RTG_GRP_KEY_RTG_CLI_CLASIFICACION = "clasif";
	public static final String RTG_GRP_KEY_RTG_CLI_TIPO = "tipo";
	public static final String RTG_GRP_KEY_RTG_CLI_PARTICIPACION = "part";
	public static final String RTG_GRP_KEY_RTG_CLI_NIVEL_VENTA = "nv_venta";
	public static final String RTG_GRP_KEY_RTG_CLI_DEUDA_BANCO = "deu_bco";
	public static final String RTG_GRP_KEY_RTG_CLI_DEUDA_SBIF = "deu_sbif";
	public static final String RTG_GRP_KEY_RTG_CLI_COMPORTAMIENTO_ACTUAL = "comp_actual";
	public static final String RTG_GRP_KEY_RTG_CLI_NOTA_COMPORTAMIENTO = "nt_comp";
	public static final String RTG_GRP_KEY_RTG_CLI_NOTA_NEGOCIO = "nt_neg";
	public static final String RTG_GRP_KEY_RTG_CLI_NOTA_FINANCIERA = "nt_fin";
	public static final String RTG_GRP_KEY_RTG_CLI_FECHA_GRUPAL = "fch_grp";

	public static final String RTG_GRP_KEY_GRP_EMP_NOMBRE = "nombre";
	public static final String RTG_GRP_KEY_GRP_EMP_CLASIFICACION = "clasif";
	public static final String RTG_GRP_KEY_GRP_EMP_ID_RELACION = "id_relac";
	public static final String RTG_GRP_KEY_GRP_EMP_PARTICIPACION = "partic";
	public static final String RTG_GRP_KEY_GRP_EMP_DEUDA_BANCO = "deu_bco";
	public static final String RTG_GRP_KEY_GRP_EMP_DEUDA_SBIF = "deu_sbif";
	public static final String RTG_GRP_KEY_GRP_EMP_COMPORTAMIENTO_ACTUAL = "comp_actual";
	public static final String RTG_GRP_KEY_GRP_EMP_NOTA_COMPORTAMIENTO = "nt_comp";
	public static final String RTG_GRP_KEY_GRP_EMP_NOTA_NEGOCIO = "nt_neg";
	public static final String RTG_GRP_KEY_GRP_EMP_NOTA_FINANCIERO = "nt_fin";
	public static final String RTG_GRP_KEY_GRP_EMP_FECHA_GRUPAL = "fch_grp";
	public static final String RTG_GRP_KEY_GRP_EMP_LISTA_TIPO_RELACION = "lst_tp_rel";
	public static final String RTG_GRP_KEY_GRP_EMP_TIPO_RELACION = "tp_rel";
	public static final String RTG_GRP_KEY_GRP_EMP_TIPO = "tipo";
	public static final String RTG_GRP_KEY_GRP_EMP_NIVEL_VENTA = "nv_venta";
	public static final String RTG_GRP_KEY_GRP_EMP_FECHA_INDIVIDUAL = "fch_ind";
	public static final String RTG_GRP_KEY_GRP_EMP_FECHA_COMPORTAMIENTO = "fch_comp";

	// #KEY BASES
	public static final String RTG_GRP_KEY_RTG_CLI = "rat_cliente";
	public static final String RTG_GRP_KEY_RTG_CLI_RUT = "rut";
	public static final String RTG_GRP_KEY_RTG_CLI_RAZON_SOCIAL = "raz_soc";
	public static final String RTG_GRP_KEY_RTG_CLI_NOTA_INDIVIDUAL = "nt_ind";
	public static final String RTG_GRP_KEY_RTG_CLI_ID_ESTADO = "id_est";
	public static final String RTG_GRP_KEY_RTG_CLI_ESTADO = "estado";

	public static final String RTG_GRP_KEY_GRP_EMP = "grp_emp";
	public static final String RTG_GRP_KEY_GRP_EMP_LISTA_EMPRESA = "lst_emp";
	public static final String RTG_GRP_KEY_GRP_EMP_RUT = "rut";
	public static final String RTG_GRP_KEY_GRP_EMP_RAZON_SOCIAL = "raz_soc";
	public static final String RTG_GRP_KEY_GRP_EMP_NOTA_INDIVIDUAL = "nt_ind";
	public static final String RTG_GRP_KEY_GRP_EMP_ID_PARTE_INVOLUCRADA = "id_par_inv";

	public static final String RTG_GRP_KEY_RTG_GRP = "rat_grp";
	public static final String RTG_GRP_KEY_RTG_GRP_NOTA_RATING_GRUPAL = "nt_rat_grp";
	public static final String RTG_GRP_KEY_RTG_GRP_LISTA_MOTIVOS = "lst_mot";
	public static final String RTG_GRP_KEY_RTG_GRP_ID_MOTIVO = "id_mot";
	public static final String RTG_GRP_KEY_RTG_GRP_MOTIVO = "motivo";
	public static final String RTG_GRP_KEY_RTG_GRP_COMENTARIOS = "coment";
	public static final String RTG_GRP_KEY_RTG_GRP_ESTADO = "estado";
	public static final String RTG_GRP_KEY_RTG_GRP_ID_ESTADO = "id_estado";

	// Servicio 300000 // Nombre de Servicio: Consulta tipos de rating // Clase
	// Invoca: .
	// REQUEST
	public static final String CONSUL_RTG_KEY_CLIENTE = "cliente";
	public static final String CONSUL_RTG_KEY_ROL = "rol";
	public static final String CONSUL_RTG_KEY_OPERACION = "operacion";
	public static final String CONSUL_RTG_KEY_PLANTILLA = "plantilla";

	// RESPONSE
	public static final String CONSUL_RTG_KEY_LST_RTG = "lst_rating";

	public static final String CONSUL_RTG_KEY_DATA_RTG_IND = "rat_ind";
	public static final String CONSUL_RTG_KEY_ID_TIPO_RATING = "id_tpo_rat";
	public static final String CONSUL_RTG_KEY_TIPO_RATING = "tpo_rat";
	public static final String CONSUL_RTG_KEY_FECHA_IND = "fechaInd";
	public static final String CONSUL_RTG_KEY_NOTA_INDIVIDUAL = "notaInd";
	public static final String CONSUL_RTG_KEY_RATING_IND_SUGERIDO = "rtgIndSug";
	public static final String CONSUL_RTG_KEY_ESTADO_IND = "est";
	public static final String CONSUL_RTG_KEY_ID_ESTADO_IND = "idEstInd";
	public static final String CONSUL_RTG_KEY_ID_RTG_IND = "id_rat_ind";

	public static final String CONSUL_RTG_KEY_DATA_RTG_GRUP = "rat_grup";
	public static final String CONSUL_RTG_KEY_FECHA_GRUP = "fecha";
	public static final String CONSUL_RTG_KEY_NOTA_GRUPAL = "notaGrp";
	public static final String CONSUL_RTG_KEY_ESTADO_GRUP = "est";
	public static final String CONSUL_RTG_KEY_ID_ESTADO_GRUP = "id_est";
	public static final String CONSUL_RTG_KEY_RESPONSABLE = "resp";
	public static final String CONSUL_RTG_KEY_ID_RTG_GRUP = "id_rat_grp";

	public static final String CONSUL_RTG_KEY_DATA_RTG_PARCIAL = "rat_parcial";

	public static final String CONSUL_RTG_KEY_DATA_RTG_FIN = "rat_fin";
	public static final String CONSUL_RTG_KEY_FECHA_VACIADO = "fechaVac";
	public static final String CONSUL_RTG_KEY_NOTA_FINANCIERO = "notaFin";
	public static final String CONSUL_RTG_KEY_ID_RTG_FIN = "id_rat_fin";
	public static final String CONSUL_RTG_KEY_NOTA_PROYECTADO = "notaProy";

	public static final String CONSUL_RTG_KEY_DATA_RTG_NEG = "rat_neg";
	public static final String CONSUL_RTG_KEY_NOTA_NEGOCIO = "notaNeg";
	public static final String CONSUL_RTG_KEY_ID_RTG_NEG = "id_rat_neg";

	public static final String CONSUL_RTG_KEY_DATA_RTG_COMP = "rat_comp";
	public static final String CONSUL_RTG_KEY_NOTA_COMPORTAMIENTO = "notaComp";
	public static final String CONSUL_RTG_KEY_ID_RTG_COMP = "id_rat_comp";

	public static final String CONSUL_RTG_KEY_DATA_RTG_GTE = "rat_gte";
	public static final String CONSUL_RTG_KEY_NOTA_GARANTE = "notaGte";
	public static final String CONSUL_RTG_KEY_ID_RTG_GTE = "id_rat_gte";

	public static final String CONSUL_RTG_KEY_PERM_VISUALIZAR = "permVer";
	public static final String CONSUL_RTG_KEY_PERM_ACCESO = "permAcc";
	public static final String CONSUL_RTG_KEY_LISTA_REGISTROS = "lstRegs";
	public static final String CONSUL_RTG_KEY_RTG_IND_CONFIRMADO = "rtgIndConf";
	public static final String CONSUL_RTG_KEY_RTG_GRP_CONFIRMADO = "rtgGrpConf";
	public static final String CONSUL_RTG_KEY_RTG_FINAN_CONFIRMADO = "rtgFinConf";
	public static final String CONSUL_RTG_KEY_RTG_PROY_CONFIRMADO = "rtgProyConf";
	public static final String CONSUL_RTG_KEY_RTG_NEG_CONFIRMADO = "rtgNegConf";
	public static final String CONSUL_RTG_KEY_RTG_COMP_CONFIRMADO = "rtgCompConf";

	// Constante seran borradas cuando se allan terminado los caso de uso que
	// los utilicen (ya que no corresponde a la estructura definida)
	public static final String CONSUL_RTG_KEY_DATA_RTG = "rtg";
	public static final String CONSUL_RTG_KEY_NOMBRE_TIPO_RTG = "nom_tpo_rtg";
	public static final String CONSUL_RTG_KEY_TIPO_RTG = "tpo_rtg";
	public static final String CONSUL_RTG_KEY_ID_RTG = "id_rtg";
	public static final String CONSUL_RTG_KEY_NOMBRE_FIN = "nom_fin";
	public static final String CONSUL_RTG_KEY_NOMBRE_PROY = "proyec";
	public static final String CONSUL_RTG_KEY_NOMBRE_NEG = "nom_neg";
	public static final String CONSUL_RTG_KEY_NOMBRE_COMP = "nom_comp";
	public static final String CONSUL_RTG_KEY_ID_ESTADO_NEG = "estadoNeg";
	public static final String CONSUL_RTG_KEY_FCH_COMP = "fch_comp";
	public static final String CONSUL_RTG_KEY_ID_ESTADO_COMP = "id_est";
	public static final String CONSUL_RTG_KEY_ID_ESTADO = "id_est";
	public static final String CONSUL_RTG_KEY_PONDERACION = "pond";
	public static final String CONSUL_RTG_KEY_ID_PARCIAL = "id_parc";
	public static final String CONSUL_RTG_KEY_FECHA_FIN = "fch_fin";
	public static final String CONSUL_RTG_KEY_AJUSTADO = "ajus";
	public static final String CONSUL_RTG_KEY_FECHA_PROY = "fch_proy";
	public static final String CONSUL_RTG_KEY_FECHA_NEG = "fch_neg";
	public static final String CONSUL_RTG_KEY_DATA_RTG_PROY = "rat_proy";
	public static final String CONSUL_RTG_KEY_ID_RTG_PROY = "id_rat_proy";

	// Servicio 300010 // Nombre de Servicio: Seleccionar tipos Bancas de rating
	// // Clase Invoca: .
	// REQUEST
	public static final String SELECC_TPO_RTG_KEY_ESQUEMA = "esquema";
	public static final String SELECC_TPO_RTG_KEY_CLIENTE = "cliente";
	// RESPONSE
	public static final String SELECC_TPO_RTG_KEY_LST_TIPOS_BANCAS = "lst_bancas";
	public static final String SELECC_TPO_RTG_KEY_CLASIFICACION = "clasif";
	public static final String SELECC_TPO_RTG_KEY_NOMBRE = "nombre";
	public static final String SELECC_TPO_RTG_KEY_VERSION = "version";

	// Servicio 300020 // Nombre de Servicio: Ver Ficha Rating // Clase Invoca:
	// REQUEST
	public static final String VER_FICH_RTG_KEY_CLIENTE = "cliente";
	public static final String VER_FICH_RTG_KEY_ID_RTG_INDIVIDUAL = "id_rat_ind";
	public static final String VER_FICH_RTG_KEY_ID_RTG_GRUPAL = "id_rat_grp";
	public static final String VER_FICH_RTG_KEY_ID_TPO_RTG = "id_tpo_rtg";

	// RESPONSE
	public static final String VER_FICH_RTG_KEY_DATA_HEADER = "header";
	public static final String VER_FICH_RTG_KEY_VENTAS = "ventas";
	public static final String VER_FICH_RTG_KEY_ACTIVOS = "activos";
	public static final String VER_FICH_RTG_KEY_COLOCACION = "coloc";
	public static final String VER_FICH_RTG_KEY_ID_EST_RTG_INDIVIDUAL = "est_rat_id";
	public static final String VER_FICH_RTG_KEY_EST_RTG_INDIVIDUAL = "est_rat";
	public static final String VER_FICH_RTG_KEY_COLOCACION_SBIF = "coloc_sbif";
	public static final String VER_FICH_RTG_KEY_PATRIMONIO = "patrim";
	public static final String VER_FICH_RTG_KEY_FCH_ULT_BALANCE = "fch_ult_bce";
	public static final String VER_FICH_RTG_KEY_ID_TPO_BALANCE = "tpo_bce_id";
	public static final String VER_FICH_RTG_KEY_TPO_BALANCE = "tpo_bce";

	public static final String VER_FICH_RTG_KEY_MOTIVOS = "motivos";
	public static final String VER_FICH_RTG_KEY_ID_MOTIVO = "id_motivo";
	public static final String VER_FICH_RTG_KEY_MOTIVO = "motivo";

	public static final String VER_FICH_RTG_KEY_DATA_DETALLE = "detalle";
	public static final String VER_FICH_RTG_KEY_DATA_DETALLE_ANTERIOR = "detalle_anterior";

	public static final String VER_FICH_RTG_KEY_ID_TPO_RATING = "tpo_rat_id";
	public static final String VER_FICH_RTG_KEY_TPO_RATING = "tpo_rat";

	public static final String VER_FICH_RTG_KEY_DATA_RTG_NEGOCIO = "rat_neg";

	public static final String VER_FICH_RTG_KEY_DATA_RTG_FINANCIERO = "rat_fin";

	public static final String VER_FICH_RTG_KEY_DATA_RTG_FIN_PROYECTADO = "rat_fin_proy";

	public static final String VER_FICH_RTG_KEY_DATA_RTG_COMP = "rat_comp";

	public static final String VER_FICH_RTG_KEY_DATA_RTG_INDIVIDUAL = "rat_ind";

	public static final String VER_FICH_RTG_KEY_DATA_RTG_GRUPAL = "rat_grp";

	public static final String VER_FICH_RTG_KEY_LISTA_FORTALEZAS = "fortalezas";
	public static final String VER_FICH_RTG_KEY_ATRIBUTOS = "ambito";
	public static final String VER_FICH_RTG_KEY_FORTALEZA_DEBILIDAD = "fort_deb";
	public static final String VER_FICH_RTG_KEY_PREGUNTA = "preg";
	public static final String VER_FICH_RTG_KEY_RESPUESTA = "resp";

	public static final String VER_FICH_RTG_KEY_LISTA_RELACIONADOS = "relacionados";
	public static final String VER_FICH_RTG_KEY_RUT = "rut";
	public static final String VER_FICH_RTG_KEY_NOMBRE = "nombre";
	public static final String VER_FICH_RTG_KEY_CLASIFICACION = "clasif";
	public static final String VER_FICH_RTG_KEY_REL_COLOCACION = "coloc";
	public static final String VER_FICH_RTG_KEY_REL_ACTIVOS = "activos";
	public static final String VER_FICH_RTG_KEY_RELACION_ID = "rel_id";
	public static final String VER_FICH_RTG_KEY_RELACION = "rel";
	public static final String VER_FICH_RTG_KEY_PARTICIPACION = "part";
	public static final String VER_FICH_RTG_KEY_COMPORTAMIENTO = "comp";
	public static final String VER_FICH_RTG_KEY_NOTA_RTG_INDIVIDUAL = "rat_ind";

	// #KEY BASES

	public static final String VER_FICH_RTG_KEY_NOTA = "nota";
	public static final String VER_FICH_RTG_KEY_RESPONSABLE = "resp";
	public static final String VER_FICH_RTG_KEY_FECHA = "fecha";

	// Clasificaciones de la banca
	public static final Integer BANCA_CGE = new Integer(4201);
	public static final Integer BANCA_PYME = new Integer(4202);
	public static final Integer BANCA_INMOBILIARIAS = new Integer(4203);
	public static final Integer BANCA_CONSTRUCTORAS = new Integer(4204);
	public static final Integer BANCA_BANCOS = new Integer(4205);
	public static final Integer BANCA_AGRICOLAS = new Integer(4206);
	public static final Integer BANCA_SOCIEDADES_INVERSION = new Integer(4208);
	public static final Integer BANCA_SEGUROS_GRLES = new Integer(4211);

	// Este id no se encuentra registrada en la tabla de clasificaciones solo se
	// utiliza a nivel aplicativo
	public static final Integer BANCA_DEFAULT_NO_PYME = new Integer(4299);

	// Clasificaciones de los segmentos
	public static final Integer SEGMENTO_VENTAS = new Integer(4602);
	public static final Integer SEGMENTO_ECONOMICO = new Integer(4402);
	public static final Integer SEGMENTO_VENTAS_RATING_INDIVIDUAL = new Integer(
			4601);
	public static final Integer SEGMENTO_VENTAS_RATING_COMPORTAMIENTO = new Integer(
			4604);
	public static final Integer SEGMENTO_CONDICION_BORDE_PYME_LEVERAGE = new Integer(
			4605); // TODO en realidad es segmento ventas rating financiero.
	public static final Integer SEGMENTO_PREMIO_POR_PATRIMONIO = new Integer(
			4603);

	// Clave de entrada para obtener el codigo de la cuenta de ventas por plan
	// de cuentas
	public static final String CODIGO_CUENTA_VENTAS = "codigo.cuenta.ventas";
	public static final String CODIGO_CUENTA_PATRIMONIO = "codigo.cuenta.patrimonio";

	// Estados de un rating de negocio EN CURSO / VIGENTE
	public static final Integer ESTADO_RATING_EN_CURSO = new Integer(0);
	public static final Integer ESTADO_RATING_VIGENTE = new Integer(1);
	public static final Integer ESTADO_RATING_HISTORICO = new Integer(2);

	// Reportes
	public static final String KEY_REPORTES_RATING = "reportes.rating.";

	// Flags con los permisos de un rol sobre las notas de los componentes de
	// rating y rating grupal. Vista Consulta Rating.
	public static final String PERMISO_CONSULTA_RATING_TIENE_PERMISO = "1";
	public static final String PERMISO_CONSULTA_RATING_NO_TIENE_PERMISO = "0";

	// perfila visualizacion ponderacion componentes de rating en vista rating
	// individual pyme
	public static final String KEY_ROL_PERFIL_PYME_COMERCIAL = "rol.perfil.pyme.comercial";

	public static final Integer ID_CLASIF_ESTADO_RATING_VIGENTE = Integer
			.valueOf("4302");
	public static final Integer ID_CLASIF_ESTADO_RATING_EN_CURSO = Integer
			.valueOf("4301");
	public static final Integer ID_CLASIF_ESTADO_RATING_HISTORICO = Integer
			.valueOf("4303");
	// Mensajes de validacion integridad rating financiero y financ proy
	public static final String MSG1_VALIDAR_RATING_FINANCIERO = "msg1.validar.rating.financiero";
	public static final String MSG2_VALIDAR_RATING_FINANCIERO = "msg2.validar.rating.financiero";
	public static final String MSG3_VALIDAR_RATING_FINANCIERO = "msg3.validar.rating.financiero";
	public static final String MSG4_VALIDAR_RATING_FINANCIERO = "msg4.validar.rating.financiero";
	public static final String MSG5_VALIDAR_RATING_FINANCIERO = "msg5.validar.rating.financiero";
	public static final String MSG6_VALIDAR_RATING_FINANCIERO = "msg6.validar.rating.financiero";
	public static final String MSG7_VALIDAR_RATING_FINANCIERO = "msg7.validar.rating.financiero";
	// mensajes validacion seleccion modelo rating individual
	public static final String MSG1_MODELO_RATING_INDIVIDUAL = "msg1.modelo.rating.individual";
	public static final String MSG2_MODELO_RATING_INDIVIDUAL = "msg2.modelo.rating.individual";
	public static final String MSG3_MODELO_RATING_INDIVIDUAL = "msg3.modelo.rating.individual";
	public static final String MSG4_MODELO_RATING_INDIVIDUAL = "msg4.modelo.rating.individual";
	public static final String MSG5_MODELO_RATING_INDIVIDUAL = "msg5.modelo.rating.individual";
	public static final String MSG6_MODELO_RATING_INDIVIDUAL = "msg6.modelo.rating.individual";
	public static final String MSG7_MODELO_RATING_INDIVIDUAL = "msg7.modelo.rating.individual";
	public static final String MSG8_MODELO_RATING_INDIVIDUAL = "msg8.modelo.rating.individual";
	public static final String MSG9_MODELO_RATING_INDIVIDUAL = "msg9.modelo.rating.individual";
	public static final String MSG10_MODELO_RATING_INDIVIDUAL = "msg10.modelo.rating.individual";
	public static final String MSG12_MODELO_RATING_INDIVIDUAL = "msg12.modelo.rating.individual";
	public static final String MSG13_MODELO_RATING_INDIVIDUAL = "msg13.modelo.rating.individual";
	public static final String MSG14_MODELO_RATING_INDIVIDUAL = "msg14.modelo.rating.individual";
	public static final String MSG15_MODELO_RATING_INDIVIDUAL = "msg15.modelo.rating.individual";
	public static final String MSG16_MODELO_RATING_INDIVIDUAL = "msg16.modelo.rating.individual";

	// mensajes validacion seleccion modelo rating individual
	public static final String MSG1_INTEGRIDAD_RATING_FINANCIERO = "msg1.validar.integridad.rating.financiero";
	public static final String MSG2_INTEGRIDAD_RATING_FINANCIERO = "msg2.validar.integridad.rating.financiero";
	public static final String MSG3_INTEGRIDAD_RATING_FINANCIERO = "msg3.validar.integridad.rating.financiero";
	public static final String MSG4_INTEGRIDAD_RATING_FINANCIERO = "msg4.validar.integridad.rating.financiero";
	public static final String MSG5_INTEGRIDAD_RATING_FINANCIERO = "msg5.validar.integridad.rating.financiero";
	public static final String MSG6_INTEGRIDAD_RATING_FINANCIERO = "msg6.validar.integridad.rating.financiero";
	public static final String MSG7_INTEGRIDAD_RATING_FINANCIERO = "msg7.validar.integridad.rating.financiero";
	public static final String MSG8_INTEGRIDAD_RATING_FINANCIERO = "msg8.validar.integridad.rating.financiero";
	public static final String MSG9_INTEGRIDAD_RATING_FINANCIERO = "msg9.validar.integridad.rating.financiero";
	// mensajes validacion seleccion modelo rating garante
	public static final String MSG1_MODELO_RATING_GARANTE = "msg1.validar.rating.garante";
	public static final String MSG2_MODELO_RATING_GARANTE = "msg2.validar.rating.garante";

	// mensajes validacion seleccion modelo rating proyectado
	public static final String MSG1_MODELO_RATING_PROYECTADO = "msg1.modelo.rating.proyectado";
	public static final String MSG2_MODELO_RATING_PROYECTADO = "msg2.modelo.rating.proyectado";
	public static final String MSG3_MODELO_RATING_PROYECTADO = "msg3.modelo.rating.proyectado";
	public static final String MSG4_MODELO_RATING_PROYECTADO = "msg4.modelo.rating.proyectado";
	public static final String MSG5_MODELO_RATING_PROYECTADO = "msg5.modelo.rating.proyectado";
	public static final String MSG6_MODELO_RATING_PROYECTADO = "msg6.modelo.rating.proyectado";

	// Parametros consulta para rating
	public static final String PARAM_AMBITO = "AMBITO";
	public static final String PARAM_RATING_FINANCIERO = "RTG_FINANCIERO";
	public static final String PARAM_ID_BANCA = "ID_BANCA";
	public static final String PARAM_ARCHIVO = "ARCHIVO";
	public static final String PARAM_ID_OPE = "ID_OPE";
	public static final String PARAM_ID_MATRIZ = "ID_MATRIZ";
	public static final String PARAM_ID_CLIENTE = "ID_CLIENTE";
	public static final String PARAM_ID_SEGMENTO = "ID_SEGMENTO";
	public static final String PARAM_ID_SECTOR = "ID_SECTOR";
	public static final String PARAM_RUT_CLIENTE = "RUT_CLIENTE";
	public static final String PARAM_ID_MODULO = "ID_MODULO";
	public static final String PARAM_ID_RATING = "ID_RATING";
	public static final String PARAM_ID_RATING_OLD = "ID_RATING_OLD";
	public static final String PARAM_ID_RATING_NEGOCIO = "ID_RTG_NEG";
	public static final String PARAM_ID_RATING_COMPORTAMIENTO = "ID_RTG_COMP";
	public static final String PARAM_ID_PREGUNTA = "ID_PREG";
	public static final String PARAM_ID_USUARIO = "ID_USUARIO";
	public static final String PARAM_COMENTARIO = "COMENT";
	public static final String PARAM_ID_MOTIVO = "ID_MOTIVO";
	public static final String PARAM_COMENTARIO_MOTIVO = "COMENTARIO_MOTIVO";
	public static final String PARAM_DEUDA_BANCO = "DEUDA_BANCO";
	public static final String PARAM_DEUDA_SBIF = "DEUDA_SBIF";
	public static final String PARAM_NOTA_RATING = "NOTA_RATING";
	public static final String PARAM_FECHA = "FECHA";
	public static final String PARAM_FECHA_ACT = "FECHA_ACT";
	public static final String PARAM_ID_CONTRATO = "ID_CONTRATO";
	public static final String PARAM_ID_PROYECTO = "ID_PROYECTO";
	public static final String PARAM_VALOR_GENERICO_CALCULADO = "VAL_GENERICO_CALC";
	public static final String PARAM_ID_TEMA = "ID_TEMA";
	public static final String PARAM_RATING_FINAL_SUGERIDO = "RATING_FIN_SUG";
	public static final String PARAM_FECHA_MODIFICACION = "FECHA_MOD";
	public static final String PARAM_RATING_FINAL = "RATING_FINAL";
	public static final String PARAM_RATING_APROX_FINAL = "RATING_APROX_FINAL";
	public static final String PARAM_PREMIO_TAMANO = "PREMIO_TAMANO";
	public static final String PARAM_RATING_PRELIMINAR_1 = "RATING_PRELIMINAR_1";
	public static final String PARAM_RATING_PRELIMINAR_2 = "RATING_PRELIMINAR_2";
	public static final String PARAM_MONTO_VENTA = "MONTO_VENTA";
	public static final String PARAM_MONTO_PATRIMONIO = "MONTO_PATRIMONIO";
	public static final String PARAM_POND_FINANCIERO = "POND_FINANCIERO";
	public static final String PARAM_POND_PROYECTADO = "POND_PROYECTADO";
	public static final String PARAM_POND_COMPORTAMIENTO = "POND_COMPORTAMIENTO";
	public static final String PARAM_POND_NEGOCIO = "POND_NEGOCIO";
	public static final String PARAM_RUT_RELACIONADO = "RUT_REL";
	public static final String PARAM_ANTIGUEDAD = "ANTIGUEDAD";
	public static final String PARAM_ID_RGO_NOTA = "ID_RGO_NOTA";
	public static final String PARAM_NRO_PERIODOS = "NRO_PERIODOS";
	public static final String PARAM_FLAG_TIPO_PERIODO = "FLAG_TIPO_PERIODO";
	public static final String PARAM_INFO_MAS_NUEVA = "INFO_MAS_NUEVA";
	public static final String PARAM_FLAG = "FLAG";
	public static final String PARAM_ORIGEN_INFO = "ORIGEN_INFO";

	public static final String PARAM_APERTURA = "APERTURA";
	public static final String PARAM_CONCEPTO = "CONCEPTO";
	public static final String PARAM_VALOR_2 = "VALOR_2";
	public static final String PARAM_VALOR_3 = "VALOR_3";

	public static final String PARAM_CODIGO_CUENTA = "COD_CUENTA";
	public static final String PARAM_COD = "COD";
	public static final String PARAM_VALOR = "VALOR";
	public static final String PARAM_ID_AJTE_CALIDAD = "ID_AJTE_CAL";
	public static final String PARAM_INDICE_MIN = "INDICE_MIN";
	public static final String PARAM_INDICE_MAX = "INDICE_MAX";
	public static final String PARAM_RIESGO1 = "RIESGO1";
	public static final String PARAM_CALIFICADORA1 = "CALIFICADORA1";
	public static final String PARAM_RIESGO2 = "RIESGO2";
	public static final String PARAM_CALIFICADORA2 = "CALIFICADORA2";
	public static final String PARAM_RIESGO3 = "RIESGO3";
	public static final String PARAM_CALIFICADORA3 = "CALIFICADORA3";
	public static final String PARAM_RATING_BCH = "RATING_BCH";
	public static final String PARAM_RATING_RLV = "RATING_RLV";
	public static final String PARAM_EMP_MATRIZ = "EMP_MATRIZ";
	public static final String PARAM_RIESGO = "RIESGO";
	public static final String PARAM_CALIFICADORA = "CALIFICADORA";
	public static final Object PARAM_ID_RATING_GTE = "ID_GTE";
	public static final Object PARAM_FECHA_RATING_FINANCIERO = "FECHA_RTG_FINAN";
	public static final String PARAM_PUNTAJE_COMPORTAMIENTO = "puntaje";
	public static final String PARAM_ID_RATING_FINANCIERO = "ID_RATING_FINAN";
	public static final String PARAM_PERIODO_VACIADO_0 = "PERIODO_VAC_0";
	public static final String PARAM_AJUSTE = "AJUSTE";
	public static final String PARAM_MESES = "MESES";
	public static final Object PARAM_MONTO_DEUDA_BANCO = "MONTO_DEUDA_BANCO";
	public static final Object PARAM_MONTO_DEUDA_SBIF = "MONTO_DEUDA_SBIF";
	public static final Object PARAM_MONTO_DEUDA_SIN_HIP_BANCO = "MONTO_DEUDA_SIN_HIP_BANCO";
	public static final Object PARAM_MONTO_DEUDA_SIN_HIP_SBIF = "MONTO_DEUDA_SIN_HIP_SBIF";
	public static final Object PARAM_MONTO_DEUDA_ACHEL = "MONTO_DEUDA_ACHEL";
	public static final Object PARAM_PERIODO_VACIADO_1 = "PERIODO_VAC_1";
	public static final Object PARAM_PERIODO_VACIADO_2 = "PERIODO_VAC_2";
	public static final Object PARAM_NIVEL_DE_VENTAS = "NIVEL_VENTAS";
	public static final Object PARAM_MONTO_DE_ACTIVOS = "MONTO_ACTIVOS";
	public static final Object PARAM_ID_PLAN_CTA = "ID_PLAN_CTA";
	public static final String PARAM_ID_NOMBRE_PLAN = "ID_NOMBRE_PLAN";
	public static final String PARAM_ID_CUENTA = "ID_CTA";
	public static final String PARAM_ID_RATING_INDIVIDUAL = "idRatInd";
	public static final Object PARAM_ID_ESTADO = "ID_ESTADO";
	public static final Object PARAM_ID_PROYECTADO = "ID_PROY";
	public static final Object PARAM_ID_VACIADO = "ID_VAC";
	public static final Object PARAM_ID_VACIADO_2 = "ID_VAC_2";
	public static final Object PARAM_ID_VACIADO_3 = "ID_VAC_3";
	public static final Object PARAM_LIMITE_MESES = "LIMITE_MESES";
	public static final String PARAM_RELACION = "RELACION";
	public static final String PARAM_ID_GRUPO = "ID_GRUPO";
	public static final Object PARAMA_RUT_RELACIONADO = "RUT_REL";
	public static final String PARAM_ID_RELACION = "ID_RELACION";
	public static final String PARAM_CTA_ID = "CTA_ID";
	public static final String PARAM_TIPO_CUENTA_ACTIVOS = "TPO_CTA_ACTIVOS";
	public static final String PARAM_TIPO_CLASIF = "TIPO_CLASIF";
	public static final String PARAM_TIPO_CUENTA_PASIVOS_CONTINGENTE = "TPO_CTA_PAS_CONT";
	public static final String PARAM_TIPO_CUENTA_PASIVOS_INTERES = "TPO_CTA_PAS_INT";
	public static final String PARAM_TIPO_CUENTA_EERR = "TPO_CTA_EERR";
	public static final String PARAM_TIPO_CUENTA_CORRECCION_MONETARIA = "TPO_CTA_CORR_MON";
	public static final String PARAM_TIPO_MAPEO_SITUACION_PERSONA = "TPO_SIT_PER";
	public static final String PARAM_ID_TPO_PLAN_CTA = "ID_TPO_PLAN_CTA";
	public static final String PARAM_ID_TPO_INDICADOR = "ID_TPO_INDICADOR";
	public static final String PARAM_ID_TPO_CTA = "ID_TPO_CTA";
	public static final String PARAM_ID_VERSION = "ID_VERSION";
	public static final String PARAM_ID_CATEGORIA = "ID_CATEGORIA";
	public static final String PARAM_NOMBRE = "NOMBRE";
	public static final String PARAM_TIPO_DEUDA = "TIPO_DEUDA";
	public static final String PARAM_FECHA_DESDE = "FECHA_DESDE";
	public static final String PARAM_FECHA_HASTA = "FECHA_HASTA";
	public static final String PARAM_ID_PLANTILLA = "ID_PLANTILLA";
	public static final String PARAM_ID_TPO_VAC = "ID_TPO_VAC";
	public static final String PARAM_NOMBRE_PLAN_CTA = "NOMBRE_PLAN_CTA";
	public static final String PARAM_FECHA_MIN = "FECHA_MIN";
	public static final String PARAM_ID_TIPO_GRUPO = "ID_TIPO_GRUPO";
	public static final String PARAM_ID_GRUPO_CLASIF = "ID_GRUPO_CLASIF";
	public static final String PARAM_NUEVO_ORDEN = "NUEVO_ORDEN";
	public static final String PARAM_ID_TIPO_INFORMACION = "ID_TIPO_INFO";
	public static final String PARAM_ID_UNIDAD = "ID_UNIDAD";
	public static final String PARAM_ID_CONCEPTO = "ID_CONCEPTO";
	public static final Object PARAM_ID_MERCADO = "ID_MERCADO";
	public static final Object PARAM_ID_ESTRUCTURA = "ID_ESTRUCTURA";
	public static final String PARAM_ID_AGRICOLA = "ID_AGRICOLA";
	public static final String PARAM_ID_PRODUCTO = "ID_PRODUCTO";
	public static final String PARAM_ID_ACREEDOR = "ID_ACREEDOR";
	public static final String PARAM_ID_CLASIF = "ID_CLASIF";
	public static final String PARAM_ID_TIPO_PLAN = "ID_TIPO_PLAN";

	// PARAMETROS PARA CONSTRUCTORA E INMOBILIARIA

	public static final String KEY_MENSAJE_NO_EXISTE_SEGMENTO_VENTAS = "error.existencia.segmento.ventas";

	public static final String PARAM_TPO_CONSULTA = "tipoConsulta";
	/**
	 * Procedimiento almacenado que busca las respuestas ingresadas por un
	 * usuario en rating de negocio.
	 */
	public static final String SP_RATING_OBTENER_RESPUESTAS_RATING_NEGOCIO = "Rating.sp_obtener_respuestas_rtg_neg";

	public static final String KEY_RTG_NEG_ERROR_VACIADO_NO_EXISTE = "error.vaciado.no.existe";
	public static final String KEY_RTG_NEG_ERROR_VACIADO_NO_CUMPLE_REGLAS = "error.vaciado.no.cumple.reglas";
	public static final String KEY_RTG_NEG_ALERTA_MATRIZ_CON_MODIFICACIONES = "alerta.matriz.modificada";
	public static final String KEY_RTG_NEG_ERROR_MATRIZ_VIGENTE_NO_EXISTE = "error.matriz.vigente.no.existe";
	public static final String KEY_RTG_NEG_ALERTA_CAMBIO_SEGMENTO = "alerta.cambio.segmento";
	public static final String KEY_RTG_NEG_ASOCIADO_NO_ES_VIGENTE = "alerta.rating.negocio.no.vigente";
	public static final String KEY_RTG_NEG_ERROR_CUESTIONARIO_INCOMPLETO = "error.calculo.cuestionario.incompleto";

	// Mensajes para rating financiero
	public static final String KEY_RTG_FINAN_ALERTA_NO_EXISTEN_VACIADOS = "alerta.rating.financiero.no.existen.vaciados";

	public static final String KEY_RTG_FINAN_ALERTA_NO_EXISTEN_VACIADOS_ANTERIORES = "alerta.rating.financiero.no.existen.vaciados.anteriores";

	public static final String KEY_RTG_FINAN_ALERTA_NO_PUEDE_UTILIZAR_VACIADO = "alerta.rating.financiero.no.puede.utilizar.vaciado.seleccionado";

	public static final String KEY_RATING_ANTIGUEDAD_MAX_VACIADO_VIGENTE = "rating.antiguedad.vaciado.vigente";
	public static final String KEY_RATING_ANTIGUEDAD_MAX_SOE_VIGENTE = "rating.antiguedad.soe.vigente";
	public static final String KEY_RATING_ANTIGUEDAD_MAX_BI_VIGENTE = "rating.antiguedad.bi.vigente";

	public static final String KEY_RATING_ALERTA_NO_EXISTE_MATRIZ_CONFIGURACION = "msg8.validar.rating.financiero";

	public static final String KEY_RATING_MSG_NO_EXISTEN_VACIADOS_VIGENTES = "msg9.validar.rating.financiero";
	public static final String KEY_RATING_MSG_NO_EXISTEN_MATRIZ_VIGENTE = "msg10.validar.rating.financiero";

	public static final String KEY_RATING_IND_MSG_NO_EXISTEN_SEGMENTOS_VENTAS = "msg11.modelo.rating.individual";

	public static final String KEY_RATING_MSG_NO_EXISTEN_SEGMENTOS_VENTAS = "alerta.rating.financiero.no.existe.segmento.venta";

	/*
	 * Operacion que se ejecuta en cada vista
	 */
	public static final String PERFILACION_OPERACION_GUI_RATING_INDIVIDUAL = ConfigManager
			.getValueAsString("codigo.operacion.gui.rating.individual");
	public static final String PERFILACION_OPERACION_GUI_RATING_FINANCIERO = ConfigManager
			.getValueAsString("codigo.operacion.gui.rating.financiero");
	public static final String PERFILACION_OPERACION_GUI_RATING_PROYECTADO = ConfigManager
			.getValueAsString("codigo.operacion.gui.rating.proyectado");
	public static final String PERFILACION_OPERACION_GUI_RATING_NEGOCIO = ConfigManager
			.getValueAsString("codigo.operacion.gui.rating.negocio");
	public static final String PERFILACION_OPERACION_GUI_RATING_COMPORTAMIENTO = ConfigManager
			.getValueAsString("codigo.operacion.gui.rating.comportamiento");
	public static final String PERFILACION_OPERACION_GUI_RATING_GARANTE = ConfigManager
			.getValueAsString("codigo.operacion.gui.rating.garante");
	public static final String PERFILACION_OPERACION_GUI_RATING_GRUPAL = ConfigManager
			.getValueAsString("codigo.operacion.gui.rating.grupal");

	/*
	 * RATING COMPORTAMIENTO
	 */
	public static final String KEY_RTG_COMP_ENCABEZADO = "encabezado";
	public static final String KEY_RTG_COMP_ENCAB_FECHA = "encabFecha";
	public static final String KEY_RTG_COMP_ENCAB_DEUDA_BANCO = "encabDeudaBanco";
	public static final String KEY_RTG_COMP_ENCAB_DEUDA_SBIF = "encabDeudaSBIF";
	public static final String KEY_RTG_COMP_ENCAB_PUNTAJE = "encabPuntaje";
	public static final String KEY_RTG_COMP_ENCAB_CIRCULO = "encabCirculo";
	public static final String KEY_RTG_COMP_ENCAB_NOTA_RTG = "encabNotaRTG";
	public static final String KEY_RTG_COMP_ENCAB_DESCRIPCION = "encabDescripcion";

	public static final String KEY_VIGENCIA_EVALUACION_CARITAS_RTG_COMPORTAMIENTO = "tiempo.vigente.evaluacion.caritas";

	public static final String KEY_RTG_COMPORT_ERROR_VIGENCIA_EVALUACION_CARITAS = "error.vigencia.evaluacion.caritas";
	public static final String KEY_RTG_COMPORT_ERROR_NO_EXISTE_EVALUACION_CARITAS = "error.no.existe.evaluacion.caritas";

	public static final String ID_RATING = "idRating";
	public static final String ID_RATING_INDIVIDUAL = "idRatInd";
	public static final String RUT_CLIENTE = "rutCliente";
	public static final String ID_PREGUNTA = "idPreg";

	// Topes maximos y minimos para identificar el color que le corresponde al
	// puntaje siebel obtenido en rating de comportamiento.
	// public static final Double RTG_COMPORTAMIENTO_PJE_VERDE_MIN =
	// ConfigManager.getValueAsDouble("rating.comport.puntaje.siebel.verde.min");
	// public static final Double RTG_COMPORTAMIENTO_PJE_VERDE_MAX =
	// ConfigManager.getValueAsDouble("rating.comport.puntaje.siebel.verde.max");
	// public static final Double RTG_COMPORTAMIENTO_PJE_AMARILLO_MIN =
	// ConfigManager.getValueAsDouble("rating.comport.puntaje.siebel.amarillo.min");
	// public static final Double RTG_COMPORTAMIENTO_PJE_AMARILLO_MAX =
	// ConfigManager.getValueAsDouble("rating.comport.puntaje.siebel.amarillo.max");
	// public static final Double RTG_COMPORTAMIENTO_PJE_ROJO_MIN =
	// ConfigManager.getValueAsDouble("rating.comport.puntaje.siebel.rojo.min");
	// public static final Double RTG_COMPORTAMIENTO_PJE_ROJO_MAX =
	// ConfigManager.getValueAsDouble("rating.comport.puntaje.siebel.rojo.max");
	public static final String RTG_COMPORTAMIENTO_PJE_VERDE_MIN = "rating.comport.puntaje.siebel.verde.min";
	public static final String RTG_COMPORTAMIENTO_PJE_VERDE_MAX = "rating.comport.puntaje.siebel.verde.max";
	public static final String RTG_COMPORTAMIENTO_PJE_AMARILLO_MIN = "rating.comport.puntaje.siebel.amarillo.min";
	public static final String RTG_COMPORTAMIENTO_PJE_AMARILLO_MAX = "rating.comport.puntaje.siebel.amarillo.max";
	public static final String RTG_COMPORTAMIENTO_PJE_ROJO_MIN = "rating.comport.puntaje.siebel.rojo.min";
	public static final String RTG_COMPORTAMIENTO_PJE_ROJO_MAX = "rating.comport.puntaje.siebel.rojo.max";

	public static final String KEY_RATING_NEGOCIO_VIGENTE = "rating.negocio.vigencia";
	public static final String CONSULTA_SERVICIOS_OSB_CLASS = "consulta.servicios.osb.class";

	/**
	 * Key que permite acceder a la implementacion del command que calcula el
	 * indicador para el rating financiero.<br>
	 * Este key debe ser concatenado con el identificador del plan de cuenta y
	 * el identificador de la banca.
	 */
	public static final String KEY_COMMAND_INDICADOR_RATING = "command.indicador.rating";

	/**
	 * Kye que permite obtener la implementacion de AlgoritmoRatingFinanciero
	 * segun banca. El identificador de banca se debe adjuntar separado por un
	 * punto.
	 */
	public static final String KEY_ALGORITMO_RTG_FINANCIERO = "rating.financiero.algoritmo";
	public static final String KEY_ALGORITMO_RTG_PROYECTADO = "rating.proyectado.algoritmo";

	// Implementacion de la ordenacion de cuentas para calculo rating proyectado
	public static final String COMPARADOR_CUENTAS_RATING_PROYECTADO = "rating.proyectado.comparador";
	public static final String DEFAULT_COMPARADOR_CUENTAS_RATING_PROYECTADO = "rating.proyectado.comparador.default";

	public static final Integer PRIMER_PERIODO_RTG_FINANCIERO = Integer
			.valueOf("1");
	public static final Integer SEGUNDO_PERIODO_RTG_FINANCIERO = Integer
			.valueOf("2");
	public static final Integer TERCER_PERIODO_RTG_FINANCIERO = Integer
			.valueOf("3");

	/*
	 * Factores utilizados por el calculo de rating financiero PyME para obtener
	 * el ajuste por ventas y deudas.
	 */
	// public static final Double FACTOR_RATING_FINANCIERO_CAV1 =
	// ConfigManager.getValueAsDouble("rating.financiero.factor.CAV1");
	// public static final Double FACTOR_RATING_FINANCIERO_CAV2 =
	// ConfigManager.getValueAsDouble("rating.financiero.factor.CAV2");
	// public static final Double FACTOR_RATING_FINANCIERO_CAV3 =
	// ConfigManager.getValueAsDouble("rating.financiero.factor.CAV3");
	// public static final Double FACTOR_RATING_FINANCIERO_CAD1 =
	// ConfigManager.getValueAsDouble("rating.financiero.factor.CAD1");
	// public static final Double FACTOR_RATING_FINANCIERO_CAD2 =
	// ConfigManager.getValueAsDouble("rating.financiero.factor.CAD2");
	// public static final Double FACTOR_RATING_FINANCIERO_CAD3 =
	// ConfigManager.getValueAsDouble("rating.financiero.factor.CAD3");
	// public static final Double FACTOR_RATING_FINANCIERO_CAX =
	// ConfigManager.getValueAsDouble("rating.financiero.factor.CAX");
	public static final String FACTOR_RATING_FINANCIERO_CAV1 = "rating.financiero.factor.CAV1";
	public static final String FACTOR_RATING_FINANCIERO_CAV2 = "rating.financiero.factor.CAV2";
	public static final String FACTOR_RATING_FINANCIERO_CAV3 = "rating.financiero.factor.CAV3";
	public static final String FACTOR_RATING_FINANCIERO_CAD1 = "rating.financiero.factor.CAD1";
	public static final String FACTOR_RATING_FINANCIERO_CAD2 = "rating.financiero.factor.CAD2";
	public static final String FACTOR_RATING_FINANCIERO_CAD3 = "rating.financiero.factor.CAD3";
	public static final String FACTOR_RATING_FINANCIERO_CAX = "rating.financiero.factor.CAX";
	/**
	 * Constante con el numero minimo de meses requeridos para calcular la
	 * variacion de ventas en el ajuste de ventas y deuda para el rating
	 * financiero.
	 */
	// public static final Integer MINIMO_MESES_IVAS_PARA_AJUSTE_VENTAS_DEUDA =
	// ConfigManager.getValueAsInteger("rating.financiero.ajuste.ventas.minimo.meses");
	public static final String MINIMO_MESES_IVAS_PARA_AJUSTE_VENTAS_DEUDA = "rating.financiero.ajuste.ventas.minimo.meses";

	// public static final Double RATING_FINANCIERO_MAX_NOTA_FINANCIERA_PYME =
	// ConfigManager.getValueAsDouble("rating.financiero.nota.max.default");

	/**
	 * Limite de variacon positiva o negativa. Si la variacion es menor a
	 * -LIMITE o la variacion es superior a LIMITE, la variacion sera -LIMITE o
	 * LIMITE respectivamente.
	 */
	// public static final Double RATING_FINANCIERO_LIMITE_VARIACION_VENTAS_IVAS
	// = ConfigManager.getValueAsDouble("rating.financiero.limite.variacion");
	public static final String RATING_FINANCIERO_LIMITE_VARIACION_VENTAS_IVAS = "rating.financiero.limite.variacion";

	public static final String KEY_RATING_FINANCIERO_NOTA_CASO_BORDE_PYME_LEVERAGE = "rating.financiero.pyme.leverage";

	/**
	 * Factor utilizado en el calculo financiero (PyME).
	 */
	// public static final Double FACTOR_CTE_CP =
	// ConfigManager.getValueAsDouble("rating.financiero.factor.CTE_CP");
	public static final String FACTOR_CTE_CP = "rating.financiero.factor.CTE_CP";
	/**
	 * Factor utilizado en el calculo financiero (PyME)
	 */
	// public static final Double FACTOR_CTE_DEU =
	// ConfigManager.getValueAsDouble("rating.financiero.factor.CTE_DEU");
	public static final String FACTOR_CTE_DEU = "rating.financiero.factor.CTE_DEU";

	/**
	 * Constante que se pasa al contexto de calculo del Servicio de Calculo y
	 * que es utilizado al momento de calcular los indicadores de rating
	 * financiero. Esta constante contiene un monto que es referenciado uno o
	 * mas indicadores.
	 */
	public static final String CTX_CALCULO_TOTAL_PASIVO_AJUSTADO = "TOTAL_PASIVO_AJUSTADO";

	/**
	 * Constante que se pasa al contexto de calculo del Servicio de Calculo y
	 * que es utilizado al momento de calcular los indicadores de rating
	 * financiero. Esta constante contiene un monto que es referenciado uno o
	 * mas indicadores.
	 */
	public static final String CTX_CALCULO_PASIVO_CP_AJUSTADO = "PASIVO_CP_AJUSTADO";

	/**
	 * Constante que se pasa al contexto de calculo del Servicio de Calculo y
	 * que es utilizado al momento de calcular los indicadores de rating
	 * financiero. Esta constante contiene un monto que es referenciado uno o
	 * mas indicadores.
	 */
	public static final String CTX_CALCULO_PASIVO_LP_AJUSTADO = "PASIVO_LP_AJUSTADO";
	public static final String CODIGO_CUENTA_REPORTE = "013";

	/**
	 * constate pasada al contexto de calculo para ser utilizado en el proceso
	 * calculo.
	 * 
	 */
	public static final String CTX_DSBIF = "DSBIF";
	/**
	 * constantes crear para el ambito del calculo de la nota de rating
	 * proyectado contructora e inmobiliaria
	 */
	public static final String CTX_TOTAL_MONTO_CONTRATO = "T_IRCC6";
	public static final String CTX_TOTAL_MONTO_AVANCE_OBRA = "TOTAL_MONTO_AVANCE_OBRA";
	public static final String CTX_TOTAL_PORCENT_AVANCE_OBRA = "TOTAL_PORCENT_AVANCE_OBRA";
	public static final String CTX_TOTAL_PORCENT_SOE = "TOTAL_PORCENT_SOE";
	public static final String CTX_TOTAL_MONTO_SOE = "T_IRCC3";
	public static final String CTX_T_SOE = "T_SOE";
	public static final String CTX_T_SOE_1 = "T_SOE_1";
	public static final String CTX_TOTAL_PORCENT_POR_COBRAR = "TOTAL_PORCENT_POR_COBRAR";
	public static final String CTX_TOTAL_MONTO_POR_COBRAR = "TOTAL_MONTO_POR_COBRAR";
	public static final String CTX_TOTAL_INDICADOR_POR_COBRAR = "TOTAL_INDICADOR_POR_COBRAR";
	public static final String CTX_TOTAL_MONTO_SOE_1_ANO = "T_IRCC4";
	public static final String CTX_TOTAL_PORCENTSOE_1_ANO = "TOTAL_PORCENTSOE_1_ANO";
	public static final String CTX_SP_IDXC1_NTAC1 = "SP_IDXC1_NTAC1";
	public static final String CTX_SP_NTAC2_IRCC4 = "SP_NTAC2_IRCC4";
	public static final String CTX_SP_IDXC1_NTAC6 = "SP_NTAC3_IRCC6";
	public static final String CTX_SP_NTAC5_IRCC3 = "SP_NTAC5_IRCC3";
	public static final String CTX_SP_IDXC4_IRCC3 = "SP_IDXC4_IRCC3";
	public static final String CTX_SP_IRCC10_IRCC6 = "SP_IRCC10_IRCC6";
	public static final String CTX_SOE_MANDATE = "IRCC5";
	public static final String CTX_MTO_SOE_1 = "IRCC4";
	public static final String CTX_MTO_SOE = "IRCC3";
	public static final String CTX_PRC_AVANCE_LINEAL = "IRCC2";
	public static final String CTX_PRC_AVANCE_OBRA = "IRCC1";
	public static final String CTX_MTO_CONTRATO = "IRCC6";
	public static final String CTX_CPX_OBRA = "IRCC8";
	public static final String CTX_KH_CLIENTE = "IRCC9";
	public static final String CTX_PLAZO = "IRCC10";
	public static final String CTX_FACTOR = "FACTOR";
	// Inmobiliarias
	public static final String CTX_TOTAL_FLUJOS_POR_RECIBIR = "T_IDXI4";
	public static final String CTX_SP_IDXI12_IDXI4 = "SP_IDXI12_IDXI4";
	public static final String CTX_TOTAL_VTA_TOT_PROYECTADA = "T_IRCI11";
	public static final String CTX_TOTAL_LINEA_APROBADA = "T_IRCI19";
	public static final String CTX_TOTAL_DEUDA_MAXIMA = "T_IRCI10";
	public static final String CTX_TOTAL_DEUDA_VIGENTE = "T_IRCI20";
	public static final String CTX_TOTAL_DMAX_PONDERADO = "T_IDXI8";
	public static final String CTX_TOTAL_FLUJOS_PONDERADOS = "T_IDXI7";
	public static final String CTX_TOTAL_SALDO_PROMESAS_X_VENDER = "T_IRCI9";
	public static final String CTX_TOTAL_POR_GIRAR = "T_IRCI21";
	public static final String CTX_SP_IDXI5_IDXI4 = "SP_IDXI5_IDXI4";
	public static final String CTX_TOTAL_CARTAS_RESGUARDO = "T_IRCI7";
	public static final String CTX_TOTAL_PROMESAS_RECIBIR = "T_IRCI8";
	public static final String CTX_TOTAL_RGO_CONSTRUCCION = "T_IDXI10";
	public static final String CTX_FECHA_AVANCE = "IRCI1";
	public static final String CTX_FECHA_TERMINO = "IRCI2";
	public static final String CTX_DURACION = "IRCI3";
	public static final String CTX_AVANCE_OBRA = "IRCI4";
	public static final String CTX_TOTAL_PROMESAS_VTA = "IRCI5";
	public static final String CTX_VAL_PROMEDIO = "IRCI6";
	public static final String CTX_CARTAS_RESGUARDO = "IRCI7";
	public static final String CTX_PROMESAS_POR_RECIBIR = "IRCI8";
	public static final String CTX_SALDO_PROMESAS_POR_VENDER = "IRCI9";
	public static final String CTX_DEUDA_MAXIMA = "IRCI10";
	public static final String CTX_VTA_TOTAL_PROYECTADA = "IRCI11";
	public static final String CTX_CONSTRUCTORA = "IRCI12";
	public static final String CTX_EXPERIENCIA_CONSTRUCTORA = "IRCI13";
	public static final String CTX_TPO_PRODUCTO = "IRCI14";
	public static final String CTX_PRIMERA_O_SEGUNDA_VIV = "IRCI15";
	public static final String CTX_CONSOLIDADO_MERCADO = "IRCI16";
	public static final String CTX_EXPERIENCIA_MERCADO = "IRCI17";
	public static final String CTX_OFERTA_COMPETENCIA = "IRCI18";
	public static final String CTX_LINEA_APROBADA = "IRC19";
	public static final String CTX_DEUDA_VIGENTE = "IRCI20";
	public static final String CTX_POR_GIRAR = "IRCI21";
	public static final String CTX_VTAS_PROM_PCT = "IRCI22";
	public static final String DEU_SBIF = "DEUSBIF";

	// agricola

	public static final String CTX_ANOS_PROYECTADOS = "ANOS_PROYECTADOS";
	public static final String CTX_SUM_PRODUCT_ANO_X_REND = "SUM_PRODUCT_ANO_X_REND";
	public static final String CTX_SUM_SUPERFICIE = "SUMA_HA";
	public static final String CTX_AGRICOLA_REND = "REND";
	public static final String CTX_HA_PRODUCTO = "IPL1A";
	public static final String CTX_COSTO = "COSTO";
	public static final String CTX_PRC_EXPORTACION = "PRC_EXPORTACION";
	public static final String CTX_PRECIO_PRODUCTO = "PRECIO_PRODUCTO";
	public static final String CTX_PRECIO_NACIONAL = "PRECIO_NACIONAL";
	public static final String CTX_SUMA_COSTO_TOTAL = "SUMA_COSTO_TOTAL";
	public static final String CTX_SUMA_INGRESO_TOTAL = "SUMA_INGRESO_TOTAL";
	public static final String CTX_MONEDA = "MONEDA";

	// parametros para el flujo resumen

	public static final String CTX_SUMA_ING_TOTALES_PL = "SUMA_ING_TOTALES_PL";
	public static final String CTX_SUMA_INGRESOS_ANIMALES = "SUMA_INGRESOS_ANIMALES";
	public static final String CTX_SUMA_COSTO_TOTAL_GAN = "SUMA_COSTO_TOTAL_GAN";
	public static final String CTX_SUMA_INGRESO_TOTAL_GAN = "SUMA_INGRESOS_TOT";
	public static final String CTX_SUMA_COST_TOTALES_PL = "SUMA_COST_TOTALES_PL";
	public static final String CTX_COSTOS_TOTALES_ANIMALES = "COSTOS_TOTALES_ANIMALES";
	public static final String CTX_OTROS_ING_OPER = "OTROS_ING_OPER";
	public static final String CTX_OTROS_EGRE_OPER = "OTROS_EGRE_OPER";
	public static final String CTX_INGRESOS_NO_OPERAC = "INGRESOS_NO_OPERAC";
	public static final String CTX_EGRESOS_NO_OPERAC = "EGRESOS_NO_OPERAC";
	public static final String CTX_GASTOS_ADMINISTRACION = "GASTOS_ADMINISTRACION";
	public static final String CTX_CULTIVOS_SUP = "CULTIVOS_SUP";
	public static final String CTX_CULTIVOS_VAL = "CULTIVOS_VAL";
	public static final String CTX_FRUTALES_SUP = "FRUTALES_SUP";
	public static final String CTX_FRUTALES_VAL = "FRUTALES_VAL";
	public static final String CTX_OTR_ARR_AGR = "OTR_ARR_AGR";
	public static final String CTX_TASA_INT = "TASA_INT";
	public static final String CTX_INTERES_CONSOLIDADO = "INTERES_CONSOLIDADO";
	public static final String CTX_UF_ANO_ANTERIOR = "UF_ANO_ANTERIOR";
	public static final String CTX_TOTAL_MONTO_APROB_CP = "TOTAL_MONTO_APROB_CP";
	public static final String CTX_TOTAL_MONTO_SOLIC_CP = "TOTAL_MONTO_SOLIC_CP";
	public static final String CTX_RET_ANUALES_CLI = "RET_ANUALES_CLI";
	public static final String CTX_AMORT_CONSOLIDADO_DEU = "AMORT_CONSOLIDADO_DEU";
	public static final String CTX_IVA_ANO_ANTERIOR = "IVA_ANO_ANTERIOR";
	public static final String CTX_IVA_ANO_ANTEANTERIOR = "IVA_ANO_ANTEANTERIOR";

	// parametros para indicadores financieros
	public static final String CTX_SUMA_INGRESOS_RES_FLU = "SUMA_INGRESOS_RES_FLU";
	public static final String CTX_SUMA_ROB_VTA_RES_FLU = "SUMA_ROB_VTA_RES_FLU";
	public static final String CTX_AIB1 = "AIB1";
	public static final String CTX_DIVISOR = "DIVISOR";
	public static final String CTX_TASA_DESC = "TASA_DESC";
	public static final String CTX_RAN_FLJ_DIP = "RAN_FLJ_DIP";
	public static final String CTX_EVOLUCION_VENTAS = "EVOLUCION_VENTAS";
	public static final String CTX_PASIVOS_EXIGIBLES = "PASIVOS_EXIGIBLES";
	public static final String CTX_PATRIMONIO_NETO = "PATRIMONIO_NETO";
	public static final String CTX_LEVERAGE = "LEVERAGE";

	public static final String CTX_SUMA_AMORT = "SUMA_AMORT";
	public static final String CTX_SUMA_AMORT_ACRE_CP = "SUMA_AMORT_ACRE_CP";
	public static final String CTX_RUBRO = "RUBRO";
	public static final String KEY_ACREEDOR_PROVEEDOR = "PROVEEDOR";
	public static final String KEY_ACREEDOR_OTROS = "OTROS";
	public static final String KEY_ACREEDOR_BANCO = "BANCO";
	public static final String CTX_OTR_ACRE_CP = "OTR_ACRE_CP";
	public static final String CTX_OTR_ACRE_LP = "OTR_ACRE_LP";
	public static final String CTX_BANCO_LP = "BANCO_LP";
	public static final String CTX_PROD_80 = "PROD_80";
	public static final String CTX_EXPORTACIONES_TOTAL = "EXPORTACIONES_TOTAL";
	public static final String CTX_ING_PROM = "ING_PROM";
	public static final String CTX_VENT_CONTR_TOTAL = "VENT_CONTR_TOTAL";
	public static final String CTX_MAX_OBM = "MAX_OBM";
	public static final String CTX_MAX_VUL_CLI = "MAX_VUL_CLI";
	public static final String CTX_RENTA_PRESUNTA_EFECTIVA = "RENTA_PRESUNTA_EFECTIVA";
	public static final String CTX_IMPUESTO_RENTA_I = "IMPUESTO_RENTA_I";
	public static final String CTX_BANCO_CP_BCH = "BANCO_CP_BCH";
	public static final String CTX_BANCO_CP_OTROS = "BANCO_CP_OTROS";
	public static final String CTX_OTR_ACT_CIRC = "OTR_ACT_CIRC";
	public static final String CTX_TOT_PLANTACIONES = "TOT_PLANTACIONES";
	public static final String CTX_TOTAL_ANIMALES = "TOTAL_ANIMALES";
	public static final String CTX_REINVERSIONES_FRUTICOLAS = "REINVERSIONES_FRUTICOLAS";
	public static final String CTX_REINVERSIONES_GANADERAS = "REINVERSIONES_GANADERAS";
	public static final String CTX_TOTAL_PROPIEDADES = "TOTAL_PROPIEDADES";
	public static final String CTX_TOT_OTR_ACT_FIJ = "TOT_OTR_ACT_FIJ";
	public static final String CTX_ACTIVOS_TOTALES = "ACTIVOS_TOTALES";
	public static final String CTX_CAPITAL_TRABAJO = "CAPITAL_TRABAJO";
	public static final String CTX_VAL_TOT_PROPIEDADES = "VAL_TOT_PROPIEDADES";
	public static final String CTX_SUMA_SUPERFICIE_FRUT = "SUMA_SUPERFICIE_FRUT";
	public static final String CTX_SUMA_SUPERFICIE_DEMAS = "SUMA_SUPERFICIE_DEMAS";

	public static final String CTX_LST_ING_TOT = "LST_ING_TOT";
	public static final String CTX_RZON_VENTA_FRUTAL = "RZON_VENTA_FRUTAL";
	public static final String CTX_PRJ_VENTA_FRUTAL = "PRJ_VENTA_FRUTAL";
	public static final String CTX_IND_FRUTAL = "IND_FRUTAL";
	public static final String CTX_SUMA_VENTAS_FRUT = "SUMA_VENTAS_FRUT";
	public static final String CTX_ANIMALES_PRODUCTORES = "ANIMALES_PRODUCTORES";
	public static final String CTX_RENDIMIENTO = "RENDIMIENTO";
	public static final String CTX_SUELO_DESTINADO_EMP = "SUELO_DESTINADO_EMP";
	public static final String CTX_MASA_GANADERA_OVEJA = "MASA_GANADERA_OVEJA";
	public static final String CTX_SUMA_VENTAS_DEMAS = "SUMA_VENTAS_DEMAS";
	public static final String CTX_IND_DEMAS = "IND_DEMAS";

	public static final Integer AGRICOLA_RUBRO_FRUTICOLA = Integer.valueOf("1");

	/**
	 * Constante que contiene la key a la que se le debe adjuntar el id de banca
	 * (id de clasificacion) para obtener la clase que implementa el algoritmo
	 * de rating individual.
	 */
	public static final String KEY_IMPLEMENTACION_ALGORITMO_RATING_INDIVIDUAL = "rating.individual.calculo.impl.";

	public static final String KEY_CALCULO_RATING_INDIVIDUAL_LIMITES_PYME = "rating.individual.calculo.limites";
	public static final String KEY_INDICE_LIMITES_CALCULO_RATING_IND_PYME = "rating.individual.calculo.limite.";
	/**
	 * Codigo reporte para obtener el nombre de la ficha
	 */
	public static final String CODIGO_REPORTE_CHGAAP_IFRSLF_IFRSLN = "013";
	public static final String CODIGO_REPORTE_IFRSCN_IFRSCF = "014";

	/*
	 * constantes agregadas al contexto de calculo para el procesamiento de las
	 * cuentas de rating proyectado
	 */
	// INDICA EL PERIODO PROYECTADO
	public static final String CTX_RATING_ES_PROYECCION = "ES_PROYECCION";
	// SE UTILIZA PARA INGRESAR DESDE EL APLICATIVO, LOS VALORES DE IVA
	public static final String CTX_RATING_IPC_PREV_X1 = "IPC_PREV_X1";
	public static final String CTX_RATING_IPC_PREV_X2 = "IPC_PREV_X2";
	// AJUSTE DE IVA CALCULADO PARA PROYECCIONES EN CLP
	public static final String CTX_RATING_PROY_AJUSTE_IVA = "PROY_AJUSTE_IVA";

	// KEY PARA IDENTIFICAR SI LA PROYECCION TIENE UN VACIADO PARCIAL
	public static final String CTX_TIENE_VAC_PARCIAL = "TIENE_VAC_PARCIAL";
	// INDICA EL ULTIMO CIERRE ANUAL
	public static final String CTX_RATING_PR_PER_DICX1 = "PR_PER_DICX1";
	// INDICA EL CIERRE ANUAL ANTERIOR
	public static final String CTX_RATING_PR_PER_DICX2 = "PR_PER_DICX2";
	// vigencia de pyme es de 18 meses pero se convierte a dias
	// dis de vigencia =18 x 30 = 540 dias
	public static final String CONSOLIDADO_VIGENCIA_PYME = "vac.consol.vig.max.sit.pers";

	public static final String CLIENTE_TIPO_EMPRESA = "E";

	public static final Integer TIPO_CUENTA_INGRESO = new Integer(0);
	public static final Integer TIPO_CUENTA_EGRESO = new Integer(1);

	// prefijo para las cuentas entregadas por consulta situacion persona;

	public static final String PREF_ACTIVO = "ACT";
	public static final String PREF_PASIVO = "PAS";
	public static final String PREF_INGRESO = "ING";
	public static final String PREF_EGRESO = "EGR";
	public static final String PREF_PATRIMONIO = "PAT";
	public static final Integer ID_TIPO_VACIADO_INDIVIDUAL = new Integer(1201);

	public static final String MSG_ERR_RATING_GRP_CLIENTE_NO_ENCONTRADO_FICHA_CHICA = "error.rating.grupal.fichachica.cliente.noexiste";
	public static final String MSG_ERR_RATING_GRP_RATING_IND_VIGENTE_REQUERIDO = "error.rating.grupal.rating.individual.vigente.req";
	public static final String MSG_ERR_RATING_GRP_MALLA_RELACIONES = "error.rating.grupal.servicio.malla.relaciones";
	public static final String MSG_ERR_RATING_GRP_BUSQUEDA_SIN_PARAMETROS = "error.rating.grupal.busqueda.sin.parametros";
	public static final String MSG_ERR_RATING_GRP_BUSQUEDA_CON_DEMASIADOS_RESULTADOS = "error.rating.grupal.busqueda.demasiados.registros";
	public static final String MSG_ERR_RATING_GRP_EXISTE_RATING_GRUPAL_EN_CURSO = "error.rating.grupal.rating.en.cursos.existente";
	public static final String MSG_ERR_RATING_GRP_RATING_IND_NO_PUEDE_SER_UTILIZADO = "error.rating.grupal.rating.no.puede.ser.utilizado";
	public static final String MSG_ERR_RATING_GRP_VALIDACION_RTG_COMPORTAMIENTO_REQUERIDO = "error.rating.grupal.validacion.rating.comp.requerido";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG1 = "error.rating.grupal.pyme.msg1";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG2 = "error.rating.grupal.pyme.msg2";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG3 = "error.rating.grupal.pyme.msg3";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG4 = "error.rating.grupal.pyme.msg4";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG5 = "error.rating.grupal.pyme.msg5";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG6 = "error.rating.grupal.pyme.msg6";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG7 = "error.rating.grupal.pyme.msg7";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG8 = "error.rating.grupal.pyme.msg8";
	public static final String MSG_ERR_RATING_GPR_PARTICIPACION_EMPRESA_CABECERA = "error.rating.grupal.pyme.opera.empresa.cabecera.participacion";
	public static final String MSG_ERR_RATING_GPR_VENTAS_EMPRESA_CABECERA = "error.rating.grupal.pyme.opera.empresa.cabecera.ventas";
	public static final String MSG_ERR_RATING_GPR_EMPRESA_CABECERA_REQ_RTG_IND = "error.rating.grupal.pyme.opera.empresa.cabecera.req.individual";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_DEBE_TENER_PARTICIPACION = "error.rating.grupal.pyme.opera.empresa.participacion";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_DEBE_TENER_PARTICIPACION_SOCIO="error.rating.grupal.pyme.opera.empresa.participacion.socio";
	public static final String MSG_ERR_RATING_GRP_PYME_CODIGO_ACT_ECONOMICA_NO_EXISTE = "error.rating.grupal.pyme.cod.act.economica.no.existe";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG9 = "error.rating.grupal.pyme.msg9";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG10 = "error.rating.grupal.pyme.msg10";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG11 = "error.rating.grupal.pyme.msg11";
	public static final String MSG_ERR_RATING_GPR_PYME_VALIDACION_MSG12 = "error.rating.grupal.pyme.msg12";
	public static final String MSG_ERR_RATING_GPR_PYME_VALIDACION_MSG13 = "error.rating.grupal.pyme.msg13";
	public static final String MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG14 = "error.rating.grupal.pyme.msg14";

	// public static final Integer
	// RATING_GRP_BUSQUEDA_EMPRESAS_PERSONAS_MAX_RESULTADO =
	// ConfigManager.getValueAsInteger("rating.grupal.busqueda.empresas.max.resultado");
	public static final String RATING_GRP_BUSQUEDA_EMPRESAS_PERSONAS_MAX_RESULTADO = "rating.grupal.busqueda.empresas.max.resultado";

	/*
	 * Color del comportamiento segun resultado obtenenido desde pre evaluacion
	 * persona
	 */
	// public static final String RESULTADO_VERDE_PRE_EVALUACION_PERSONA =
	// ConfigManager.getValueAsString("rating.grupal.resultado.pre.eval.persona.verde");
	public static final String RESULTADO_VERDE_PRE_EVALUACION_PERSONA = "rating.grupal.resultado.pre.eval.persona.verde";
	// public static final String RESULTADO_AMARILLO_PRE_EVALUACION_PERSONA =
	// ConfigManager.getValueAsString("rating.grupal.resultado.pre.eval.persona.amarillo");
	public static final String RESULTADO_AMARILLO_PRE_EVALUACION_PERSONA = "rating.grupal.resultado.pre.eval.persona.amarillo";
	// public static final String RESULTADO_ROJO_PRE_EVALUACION_PERSONA =
	// ConfigManager.getValueAsString("rating.grupal.resultado.pre.eval.persona.rojo");
	public static final String RESULTADO_ROJO_PRE_EVALUACION_PERSONA = "rating.grupal.resultado.pre.eval.persona.rojo";

	public static final String RELACION_SOCIO = "Socio";
	public static final String RELACION_EMPRESA_HIJA = "Empresa Hija";
	public static final String RELACION_EMPRESA_HERMANA = "Empresa Hermana";
	public static final String RELACION_CONYUGE = "Conyuge";
	public static final String RELACION_SIN_PARTICIPACION_DIRECTA = "Sin ParticipaciÃ³n Directa";
	public static final String RELACION_EMPRESA_MADRE = "Empresa Madre";

	public static final Integer ID_RELACION_CONYUGE = new Integer(5000);
	public static final Integer ID_RELACION_EMPRESA_HERMANA = new Integer(5001);
	public static final Integer ID_RELACION_EMPRESA_HIJA = new Integer(5002);
	public static final Integer ID_RELACION_SIN_PARTICIPACION = new Integer(
			5003);
	public static final Integer ID_RELACION_SOCIO = new Integer(5004);
	public static final Integer ID_RELACION_EMPRESA_MADRE = new Integer(5005);
	public static final Integer ID_RELACION_SELECCIONE = new Integer(5006);

	public static final String FLAG_RUT_NO_ENCONTRADO_SIEBEL = "Sin Informacion";
	// public static final Integer
	// LIMITE_CODIGO_ACTIVIDAD_ECONOMICA_OPERA_COMO_EMPRESA =
	// ConfigManager.getValueAsInteger("ficha.chica.actividad.economica.si.opera.empresa");
	public static final String LIMITE_CODIGO_ACTIVIDAD_ECONOMICA_OPERA_COMO_EMPRESA = "ficha.chica.actividad.economica.si.opera.empresa";
	// public static final Integer
	// ANTIGUEDAD_MAX_RATING_INDIVIDUAL_VIGENTE_UTILIZADO_EN_RTG_GRUPAL =
	// ConfigManager.getValueAsInteger("rating.grupal.antiguedad.maxima.rtg.individual.vigente");
	public static final String ANTIGUEDAD_MAX_RATING_INDIVIDUAL_VIGENTE_UTILIZADO_EN_RTG_GRUPAL = "rating.grupal.antiguedad.maxima.rtg.individual.vigente";

	public static final String KEY_RTG_COMP_PJE_VERDE = "V";
	public static final String KEY_RTG_COMP_PJE_AMARILLO = "A";
	public static final String KEY_RTG_COMP_PJE_ROJO = "R";

	public static final String KEY_NOTA_RATING_COMPORTAMIENTO_SEGUN_COLOR = "rating.grupal.nota.rating.comportamiento.";

	public static final Integer FLAG_NO_APLICA_EN_RTG_GRUPO = Integer
			.valueOf("0");
	public static final Integer FLAG_APLICA_EN_RTG_GRUPO = Integer.valueOf("1");
	public static final Integer FLAG_NO_PARTICIPA_EN_RTG_GRUPO = Integer
			.valueOf("0");
	public static final Integer FLAG_PARTICIPA_EN_RTG_GRUPO = Integer
			.valueOf("1");

	public static final Integer GRUPO_TIPO_RATING = Integer.valueOf("4901");
	public static final Integer GRUPO_TIPO_PEER_GROUP = Integer.valueOf("4902");

	// public static final Double
	// LIMITE_PORCENTAJE_PARTICIPACION_SOCIOS_EN_ROJO_RATING_GRUPAL_PYME =
	// ConfigManager.getValueAsDouble("rating.grupal.pyme.limite.participacion.socios.rojo");
	public static final String LIMITE_PORCENTAJE_PARTICIPACION_SOCIOS_EN_ROJO_RATING_GRUPAL_PYME = "rating.grupal.pyme.limite.participacion.socios.rojo";

	// public static final Double PONDERACION_EMPRESAS_RATING_GRUPAL_PYME =
	// ConfigManager.getValueAsDouble("rating.grupal.pyme.empresas.ponderacion");
	// public static final Double PONDERACION_PERSONAS_RATING_GRUPAL_PYME =
	// ConfigManager.getValueAsDouble("rating.grupal.pyme.personas.ponderacion");
	public static final String PONDERACION_EMPRESAS_RATING_GRUPAL = "rating.grupal.pyme.empresas.ponderacion";
	public static final String PONDERACION_PERSONAS_RATING_GRUPAL = "rating.grupal.pyme.personas.ponderacion";

	// public static final Integer
	// MIN_DIAS_VIGENTE_RATING_INDIVIDUAL_PARA_RTG_GRUPAL_NO_PYME =
	// ConfigManager.getValueAsInteger("rating.grupal.dias.vigente.rating.individual.min");
	// public static final Integer
	// MAX_DIAS_VIGENTE_RATING_INDIVIDUAL_PARA_RTG_GRUPAL_NO_PYME =
	// ConfigManager.getValueAsInteger("rating.grupal.dias.vigente.rating.individual.max");
	public static final String MIN_DIAS_VIGENTE_RATING_INDIVIDUAL_PARA_RTG_GRUPAL_NO_PYME = "rating.grupal.dias.vigente.rating.individual.min";
	public static final String MAX_DIAS_VIGENTE_RATING_INDIVIDUAL_PARA_RTG_GRUPAL_NO_PYME = "rating.grupal.dias.vigente.rating.individual.max";

	// public static final Double
	// LIMITE_RTG_IND_RELACIONADO_PARA_HEREDAR_PRC_PARTICIPACION =
	// ConfigManager.getValueAsDouble("rating.grupal.limite.hereda.porcentaje.part");
	public static final String LIMITE_RTG_IND_RELACIONADO_PARA_HEREDAR_PRC_PARTICIPACION = "rating.grupal.limite.hereda.porcentaje.part";

	// public static final Integer
	// ANTIGUEDAD_MAX_VACIADO_PARA_CALCULO_ACTIVOS_RTG_GRUPAL =
	// ConfigManager.getValueAsInteger("rating.grupal.dias.antiguedad.vaciado");
	public static final String ANTIGUEDAD_MAX_VACIADO_PARA_CALCULO_ACTIVOS_RTG_GRUPAL = "rating.grupal.dias.antiguedad.vaciado";

	// public static final Integer
	// VIGENCIA_EN_DIAS_EVALUACION_COMPORTAMIENTO_RTG_GRUPAL =
	// ConfigManager.getValueAsInteger("rating.grupal.vigencia.comportamiento.siebel");
	public static final String VIGENCIA_EN_DIAS_EVALUACION_COMPORTAMIENTO_RTG_GRUPAL = "rating.grupal.vigencia.comportamiento.siebel";

	/*
	 * Constantes con los valores entregados por Ficha Chica en el campo Tipo de
	 * Cliente.
	 */
	public static final String CLIENTE_EMPRESA = "E";
	public static final String CLIENTE_PERSONA = "P";

	public static final String CODIGO_MALLA_RELACIONES_TIPO_CLIENTE_PERSONA_NATURAL = ConfigManager
			.getValueAsString("malla.relaciones.tipo.cliente.persona.natural");

	public static final String KEY_IDS_TPOS_VACIADOS_CONSOLIDADOS_RATING_GRUPAL_NO_PYME = "rating.grupal.no.pyme.tipos.vaciados.consolidados";

	// public static final Double
	// LIMITE_INFERIOR_NOTA_MINIMA_COMPORTAMIENTO_RATING_GRUPAL_PYME =
	// ConfigManager.getValueAsDouble("rating.grupal.limite.inferior.nota.minima.comportamiento");
	// public static final Double
	// LIMITE_SUPERIOR_NOTA_MINIMA_COMPORTAMIENTO_RATING_GRUPAL_PYME =
	// ConfigManager.getValueAsDouble("rating.grupal.limite.superior.nota.minima.comportamiento");
	public static final String LIMITE_INFERIOR_NOTA_MINIMA_COMPORTAMIENTO_RATING_GRUPAL_PYME = "rating.grupal.limite.inferior.nota.minima.comportamiento";
	public static final String LIMITE_SUPERIOR_NOTA_MINIMA_COMPORTAMIENTO_RATING_GRUPAL_PYME = "rating.grupal.limite.superior.nota.minima.comportamiento";
	// public static final Double
	// CASTIGO_MINIMO_NOTA_EMPRESAS_POR_EMPRESAS_SIN_RATING =
	// ConfigManager.getValueAsDouble("rating.grupal.castigo.min.empresas.sin.rating.ind");
	// public static final Double
	// CASTIGO_MAXIMO_NOTA_EMPRESAS_POR_EMPRESAS_SIN_RATING =
	// ConfigManager.getValueAsDouble("rating.grupal.castigo.max.empresas.sin.rating.ind");
	public static final String CASTIGO_MINIMO_NOTA_EMPRESAS_POR_EMPRESAS_SIN_RATING = "rating.grupal.castigo.min.empresas.sin.rating.ind";
	public static final String CASTIGO_MAXIMO_NOTA_EMPRESAS_POR_EMPRESAS_SIN_RATING = "rating.grupal.castigo.max.empresas.sin.rating.ind";
	// public static final Double LIMITE_NOTA_RATING_CUARTO_AJUSTE =
	// ConfigManager.getValueAsDouble("rating.grupal.cuarto.ajuste.nota.tope");
	public static final String LIMITE_NOTA_RATING_CUARTO_AJUSTE = "rating.grupal.cuarto.ajuste.nota.tope";
	// public static final Double LIMITE_NOTA_PERSONAS_NATURALES_TERCER_AJUSTE =
	// ConfigManager.getValueAsDouble("rating.grupal.tercer.ajuste.limite.notas");
	public static final String LIMITE_NOTA_PERSONAS_NATURALES_TERCER_AJUSTE = "rating.grupal.tercer.ajuste.limite.notas";
	// lista de clasificaciones de cuentas/indicadores auxiliares que deben ser
	// calculados por el motor
	// cada vez que se calcula el vaciado
	public static final String LISTA_CLASIFICACIONES_CALCULO_OPCIONALES = "codigo.clasificaciones.calculo.opcionales";
	// key factor de correccion de moneda que es utilizada por las formulas de
	// evolucion trimestral
	public static final String ETR_FACTOR_CORR = "ETR_FACTOR_CORR";
	public static final String ETR_FACTOR_IPC = "ETR_FACTOR_IPC";

	public static final Integer ETR_INFO_ORIGINAL = new Integer("5901");
	public static final Integer ETR_INFO_AJUSTADO = new Integer("5902");
	public static final Integer ETR_FLG_ORIGINAL = new Integer("0");
	public static final Integer ETR_FLG_AJUSTADO = new Integer("1");

	// caracter separador utilizado para la cncatenacion de operacion y
	// plantilla
	public static final String SEPARADOR_OPERACION_PLANTILLA = "perfilacion.plantilla.separador";
	public static final String PERFILACION_POR_PLANTILLA_HABILITADO = "perfilacion.plantilla.habilitado";

	// identificador del motivo de cambio rating individual que se utiliza en
	// conjuno con nota garante para
	// validar confirmacion rating individual modificado
	public static final String ID_MOTIVO_CAMBIO_RATING_INDIVIDUAL = "rating.individual.identificador.motivo.cambio";
	// valor minimo de nota garante correspondiente a Invesment Grade
	public static final String NOTA_MINIMA_INVESTMENT_GRADE = "rating.individual.minimo.investment.grade";

	// valor del periodo trimestral para la evolucion trimestral
	public static final Integer ETR_PERIODO_TRIMESTRAL = ConfigManager
			.getValueAsInteger("etr.periodo.trimestral.valor");
	public static final Integer ETR_PERIODO_ANUAL = ConfigManager
			.getValueAsInteger("etr.periodo.anual.valor");

	public static final Integer ETR_PERIODOS_A_RECUPERAR = ConfigManager
			.getValueAsInteger("etr.periodo.trimestral.periodos.recuperar.valor");
	public static final Integer ES_PERIODO_INICIAL = new Integer(0);
	public static final Integer ES_PERIODO_FINAL = new Integer(3);
	public static final String PERIODO_INICIAL = "PERIODO";

	public static final String MSG_ETR_ERROR_FECHA_ACT_MONEDA_REP = "error.evolucion.trimestral.fecha.actualizacion.moneda";

	public static final String KEY_USO_SERVICIO_SIEBEL_ACTUALIZAR_RATING_HABILITADO = "servicio.actualiza.rating.siebel.habilitado";
	// key para recuperar los meses validos para la evolucion trimestral
	public static final String ETR_MESES_VALIDOS = "etr.periodo.valido.lista";

	public static final String MSG_ERR_CONSOLIDAR_COMBINAR_CODIGO_ACT_ECONOMICA_NO_EXISTE = "error.vaciados.consolidar.combinar.cod.act.economica.no.existe";

	// parametros utilizados como limites adicionales en el calculo del rating
	// individual pyme
	public static final String RATING_MAX_PERIODO_CONSULTA_DEUDA = "rating.pyme.deuda.max.periodo";
	public static final String RATING_MAX_NOTA_SIN_DEUDA = "rating.pyme.deuda.max.nota";

	public static final String MSG_RTG_GRUPAL_BUSQUEDA_CLIENTE_SIN_COD_ACT_ECON = "error.rating.grupal.busqueda.cliente.cod.act.economica.no.existe";

	// calculo de rating comportamiento - control de cambio #1 rating
	// comportamiento PyME
	public static final String RATING_COMP_HISTORICO_LIM_INFERIOR = "rating.comp.hist.lim.inf";
	public static final String RATING_COMP_HISTORICO_LIM_SUPERIOR = "rating.comp.hist.lim.sup";
	public static final String RATING_COMP_AJUSTE_NOTA_ACTUAL = "rating.comp.ajuste.nota.actual";
	public static final String RATING_COMP_AJUSTE_NOTA_PARAM = "rating.comp.ajuste.nota.param";
	public static final String RATING_COMP_AJUSTE_NOTA_CON_HISTORIA = "rating.comp.ajuste.nota.historia";
	public static final String RATING_COMP_NOTA_PUNTAJE_NO_DISPONIBLE = "rating.comp.nota.sin.puntaje";
	// KEY PARA RECUPERAR LAS PLANTILLAS ACTIVAS A PERFILAR
	public static final String LISTA_PLANTILLA_PERFILACION = "perfilacion.plantilla.activas";

	public static final String RATING_FINANCIERO_INICIO_BUSQUEDA_IVAS = "rating.finan.ini.busqueda.ivas";
	public static final String RATING_FINANCIERO_TERMINO_BUSQUEDA_IVAS = "rating.finan.fin.busqueda.ivas";
	public static final String RATING_FINANCIERO_AJUSTE_IVA_MIN_MESES_REQ = "rating.finan.pyme.iva.min.meses";

	public static final String RATING_FINANCIERO_PYME_MINIMO_MESES_IVA_AJUSTE_X_VENTA = "error.rating.financiero.pyme.no.existen.ivas.ajuste.ventas";

	public static final String PARAM_RUT_OSB_CONSULTAR_DECL_IVA = "rutCliente";
	public static final String PARAM_DV_OSB_CONSULTAR_DECL_IVA = "dvRutCliente";
	public static final String PARAM_PERIODO_OSB_CONSULTAR_DECL_IVA = "periodo";
	public static final String PARAM_MES_OSB_CONSULTAR_DECL_IVA = "mes";

	public static final String CONSOLIDADO_COMBINADO_REPORTE_DETALLE = "d";
	public static final String CONSOLIDADO_COMBINADO_REPORTE_RESUMEN = "r";

	// indice del periodo que se evalua para las proyecciones
	public static final String PERIODO_EVALUACION_FORMULA = "PER_ACTUAL";
	public static final String CTX_PERIODO_TRIMESTRAL = "PERIODO_TRIMESTRAL";
	public static final String CTX_BUSCAR_VAC_CON_AJUSTE = "BUSCAR_VAC_CON_AJUSTE";

	public static final Integer PERIODO_DIC_X2 = new Integer(0);
	public static final Integer PERIODO_DIC_X1 = new Integer(1);
	public static final Integer PERIODO_MES_X = new Integer(2);
	public static final Integer PERIODO_PROYECTADO = new Integer(3);

	// por defecto las formula de proyeccion hacen referencia al periodo 1
	public static final Integer PERIODO_POR_DEFECTO_PROYECTADO = ConfigManager
			.getValueAsInteger("periodo.proyectado.default.value");
	public static final Integer PERIODO_N_MAS_UNO_PROYECTADO = ConfigManager
			.getValueAsInteger("periodo.proyectado.n_mas_uno.value");

	public static final String SP_PL_CONSULTAR_PL = "ProyLarga.SP_PL_CONSULTAR_PL";
	public static final String SP_PL_CREAR_PROYECCION_LARGA = "ProyLarga.SP_PL_CREAR_PROYECCION_LARGA";
	public static final String SP_INGRESAR_PROYECCION_LARGA = "ProyLarga.SP_INGRESAR_PROYECCION_LARGA";
	public static final String SP_PL_OBTENER_PROYECCION_LARGA = "ProyLarga.SP_PL_OBTENER_PROYECCION_LARGA";
	public static final String SP_PL_MODIFICAR_REGISTRO_PL = "ProyLarga.SP_PL_MODIFICAR_REGISTRO_PL";
	public static final String SP_PL_OBTENER_CTA_PL = "ProyLarga.SP_PL_OBTENER_CTA_PL";
	public static final String SP_PL_EXISTE_COMBINACION = "ProyLarga.SP_PL_EXISTE_COMBINACION";
	public static final String SP_PL_BUSCAR_VAC_RELACIONADO = "ProyLarga.SP_PL_BUSCAR_VAC_RELACIONADO";
	public static final String SP_PL_BORRAR_PROYECCION_LARGA = "ProyLarga.SP_PL_BORRAR_PROYECCION_LARGA";
	public static final String SP_PL_OBTENER_PL_RESUMIDO = "ProyLarga.SP_PL_OBTENER_PL_RESUMIDO";
	public static final String SP_PL_OBTENER_EEFF = "ProyLarga.SP_PL_OBTENER_EEFF";

	public static final String IMPL_COMPARADOR_CUENTAS_PROYECTADAS = "comparador.cuentas.proyectadas.impl";

	/**
	 * Clave con la que se obtiene el listado de tipos de cuenta que requieren
	 * ser ordenadas previo al calculo.
	 */
	public static final List TIPOS_CUENTA_REQUIEREN_ORDEN_CALCULO = ConfigManager
			.getValuesAsListString("tipos.cuenta.requieren.orden.calculo");

	public static final String PROPIEDAD_RTG_PROY_UMBRAL_CALCULO = "rating.proy.umbral.calculo";
	public static final String PROPIEDAD_RTG_PROY_CUENTA_CONVERGENCIA = "rating.proy.converge.cuenta";
	public static final String PROPIEDAD_RTG_PROY_NRO_ITERACIONES_CALCULO = "rating.proy.nro.iter.calculo";
	public static final String PARAM_VERSION = "VERSION";

	public static final String PROPIEDAD_RTG_GRP_PYME_PARTICIPACION_MINIMA_SOCIOS = "rating.grp.pyme.part.min";
	public static final String PROPIEDAD_RTG_GRP_PYME_PARTICIPACION_MAXIMA_SOCIOS = "rating.grp.pyme.part.max";
	// constante que define desde que valor se debe repetir la formula de
	// proyeccion larga
	public static final Integer INICIO_PERIODO_PROYECCION_LARGA = new Integer(3);
	public static final Integer PERIODO_USAR_FORMULA_CUARTO = new Integer(3);
	public static final Integer PERIODO_USAR_FORMULA_QUINTO = new Integer(3);
	public static final String PERIODO_ULTIMO_PROYECTADO = "ULTIMO_PERIODO_PROYECTADO";
	public static final String PERIODO_PREVIO = "PERIODO_PREVIO";
	public static final String PERIODO_ACTUAL = "PERIODO_ACTUAL";
	public static final String PROPIEDAD_RTG_IND_MESES_CALCULO_DISMINUCION_VENTAS = "rating.ind.pyme.meses.dism.vtas";
	public static final String PROPIEDAD_RTG_IND_NOTA_MAX_SIN_DISMINUCION_VENTAS = "rtg.ind.pyme.max.nota.dis.vta";
	public static final String PROPIEDAD_RTG_IND_TOPE_MAX_DISMINUCION_VENTAS = "rtg.ind.pyme.max.prc.dis.vtas";
	public static final String PROPIEDAD_RTG_IND_MESES_RETRONCES_CALCULO_DISMINUCION_VENTAS = "rtg.ind.pyme.meses.busq.ivas";
	public static final String PROPIEDAD_RTG_PROY_EXCESO_PERMITIDO = "rtg.proy.exceso.limite";

	// habilita/deshabilita el uso de la reutilizacion del rating financiero
	public static final boolean REUTILIZAR_RATING_FINANCIERO_HABILITADO = false;
	public static final String AMBITO_COMPORTAMIENTO = "Comportamiento";
	public static final String AMBITO_FINANCIERO = "Financiero";
	public static final String AMBITO_NEGOCIO = "Negocio";
	public static final String AMBITO_PROYECTADO = "Proyectado";
	public static final String GLOSA_PREGUNTA_COMPORTAMIENTO_FORTALEZAS_DEBILIDADES = "rtg.fort.deb.glosa.preg.comp";

	public static final String GLOSA_COLUMNA_RESPUESTA_FORTALEZAS_Y_DEBILIDADES = MessageManager
			.getMessage("fortaleza.debilidad.glosa.respuesta");

	public static final String MSG_ALERTA_VALIDACION_FINANCIERO = MessageManager
			.getMessage("rtg.ind.confirmacion.validacion.financiero");
	public static final String MSG_ALERTA_VALIDACION_COMPORTAMIENTO = MessageManager
			.getMessage("rtg.ind.confirmacion.validacion.comportamiento");
	public static final String MSG_ALERTA_VALIDACION_NEGOCIO = MessageManager
			.getMessage("rtg.ind.confirmacion.validacion.negocio");
	public static final String MSG_ALERTA_VALIDACION_PROYECTADO = MessageManager
			.getMessage("rtg.ind.confirmacion.validacion.proyectado");
	public static final String MSG_ALERTA_VALIDACION_VERSION_VACIADOS = MessageManager
			.getMessage("rtg.ind.confirmacion.validacion.version.vaciados");
	public static final String MSG_ALERTA_VALIDACION_HOJA_IMD_VIGENTE = MessageManager
			.getMessage("rtg.ind.confirmacion.validacion.hoja.imd.vigente");
	public static final String MSG_ALERTA_VALIDACION_TPO_VACIADO_RTG_NEGOCIO = MessageManager
			.getMessage("rtg.ind.confirmacion.validacion.tipo.vaciado.negocio");
	public static final String MSG_ALERTA_VALIDACION_COMPONENTES_RTG = MessageManager
			.getMessage("rtg.ind.confirmacion.validacion.componentes.rating");
	public static final String SEFE_CTX_ID_PLANTILLA = "SEFE_CTX_ID_PLANTILLA";
	public static final String SEFE_CTX_ID_RATING_IND = "SEFE_CTX_ID_RATING_IND";
	public static final String SEFE_CTX_MODO_ESPECIAL = "SEFE_CTX_MODO_ESPECIAL";
	public static final String SEFE_CTX_ID_VACIADO = "SEFE_CTX_ID_VACIADO";

	public static final Integer CUENTA_PROYLARGA_X2 = new Integer(5801);
	public static final Integer CUENTA_PROYLARGA_X1 = new Integer(5802);
	public static final Integer CUENTA_PROYLARGA_X = new Integer(5803);
	public static final Integer CUENTA_PROY_CORTA_X2 = new Integer(5701);
	public static final Integer CUENTA_PROY_CORTA_X1 = new Integer(5702);
	public static final Integer CUENTA_PROY_CORTA_X = new Integer(5703);
	public static final Integer TIENE_VACIADO_PARCIAL = new Integer(0);
	public static final String SP_OBTENER_CTA_VAC_CTA_INDC = "Cuenta.SP_OBTENER_CTA_VAC_CTA_INDC";
	public static final String SP_OBTENER_CTA_X_TPO_PLAN = "Cuenta.SP_OBTENER_CTA_X_TPO_PLAN";
	public static final String SP_OBTENER_CTA_VACIADO_TPO_CT1 = "Cuenta.SP_OBTENER_CTA_VACIADO_TPO_CT1";

	public static final String KEY_RTG_PROY_ERROR_MATRIZ_VIGENTE_NO_EXISTE = "error.rating.proyectado.matriz.vigente.no.existe";

	// CONSTANTES ADMIN
	public static final String CONSTANTE_ADMIN_USU_ID = "${usu_id}";
	public static final String CTX_NO_PERIODO_PARCIAL = "CTX_NO_PERIODO_PARCIAL";
	public static final String SEFE_CTX_ID_CLIENTE = "SEFE_CTX_ID_CLIENTE";
	public static final String SEFE_CTX_RUT_CLIENTE = "SEFE_CTX_RUT_CLIENTE";

	public static final Integer SEGUNDOS_TIMEOUT_PROCESO_MASIVO_CADUCA_RATINGS = ConfigManager
			.getValueAsInteger("proc.masivo.caduca.rtgs.timeout");

	/*
	 * Inicio constantes BENCHMARK
	 */
	public static final String KEY_NOMBRE_REPORTE_BENCHMARK = "benchmark.nombre.rpt";
	public static final String MSG_ERR_BENCH_CONFIGURACION_REPORTE = "benchmark.err.config.reporte";
	public static final String MSG_ERR_BENCH_NUMERO_CLIENTES_SUPERA_LIMITE = "benchmark.err.nro.clientes.supera.limite";
	public static final String MSG_ERR_BENCH_NUMERO_GRUPOS_SUPERA_LIMITE = "benchmark.err.nro.grupos.supera.limite";
	public static final String MSG_ERR_BENCH_NOMBRE_PEER_GROUP_EXISTE = "benchmark.err.nom.peer.group.existe";
	public static final String GRAFICO_BENCHMARK_LABEL_SERIE_MUESTRA = "benchmark.grafico.label.serie.muestra";
	public static final String GRAFICO_BENCHMARK_LABEL_SERIE_CLIENTE = "benchmark.grafico.label.serie.cliente";
	public static final String GRAFICO_BENCHMARK_LABEL_SERIE_PEER_GROUP = "benchmark.grafico.label.serie.peergroup";
	public static final String KEY_NOTA_LIMITE_INFERIOR_BENCHMARK_RATING = "bench.rtg.nota.lim.inf";
	public static final String KEY_NOTA_LIMITE_SUPERIOR_BENCHMARK_RATING = "bench.rtg.nota.lim.sup";
	public static final String KEY_NOTA_LIMITE_INFERIOR_BENCHMARK_INDICADORES = "bench.ind.nota.lim.inf";
	public static final String KEY_NOTA_LIMITE_SUPERIOR_BENCHMARK_INDICADORES = "bench.ind.nota.lim.sup";
	public static final String KEY_TITULO_GRAFICOS_BENCHMARK_INDICADORES = "benchmark.titulo.seccion.graficos.indicadores";
	public static final String KEY_TITULO_GRAFICOS_BENCHMARK_RATINGS = "benchmark.titulo.seccion.graficos.ratings";
	public static final String NOMBRE_GRAFICO_BENCHMARK_RATING_INDIVIDUAL = ConfigManager
			.getValueAsString("benchmark.grafico.nombre.rating.ind");
	public static final String NOMBRE_GRAFICO_BENCHMARK_RATING_FINANCIERO = ConfigManager
			.getValueAsString("benchmark.grafico.nombre.rating.fin");
	public static final String NOMBRE_GRAFICO_BENCHMARK_RATING_PROYECTADO = ConfigManager
			.getValueAsString("benchmark.grafico.nombre.rating.pro");
	public static final String NOMBRE_GRAFICO_BENCHMARK_RATING_COMPORTAMIENTO = ConfigManager
			.getValueAsString("benchmark.grafico.nombre.rating.com");
	public static final String KEY_NOMBRE_TABLA_BENCHMARK_INDICADORES = "benchmark.indicadores.tabla.nombre";
	public static final String NOMBRE_GRAFICO_BENCHMARK_RATING_NEGOCIO = ConfigManager
			.getValueAsString("benchmark.grafico.nombre.rating.neg");
	public static final Integer CODIGO_UNIDAD_VENTAS_GRAFICO_BENCHMARK = ConfigManager
			.getValueAsInteger("benchmark.grafico.ventas.unidad");
	public static final Integer CODIGO_MONEDA_VENTAS_GRAFICO_BENCHMARK = ConfigManager
			.getValueAsInteger("benchmark.grafico.ventas.moneda");
	public static final String KEY_CRITERIO_TIPO_PERIODO = "benchmark.criterio.periodo.";
	public static final String GRAFICO_BENCHMARK_LABEL_SERIE_LIMITE_SUPERIOR = "benchmark.grafico.label.serie.limite.superior";
	public static final String GRAFICO_BENCHMARK_LABEL_SERIE_LIMITE_INFERIOR = "benchmark.grafico.label.serie.limite.inferior";
	public static final String REPORTE_BENCHMARK_TABLA_RATINGS_MUESTRA = "benchmark.reporte.tabla.ratings.muestra";
	public static final String REPORTE_BENCHMARK_TABLA_RATINGS_PEER_GROUP = "benchmark.reporte.tabla.ratings.peergroup";
	public static final String REPORTE_BENCHMARK_TABLA_RATINGS_LIMITES = "benchmark.reporte.tabla.ratings.limites";
	public static final String KEY_DIR_SUBRPT_BENCHMARK = "ruta.subreportes.rptBenchmark";
	public static final Integer GRAFICO_BENCHMARK_ANCHO = ConfigManager
			.getValueAsInteger("benchmark.grafico.size.ancho");
	public static final Integer GRAFICO_BENCHMARK_ALTO = ConfigManager
			.getValueAsInteger("benchmark.grafico.size.alto");
	public static final String KEY_TITULO_SUB_REPORTE_LISTA_TABLAS_RATINGS = "benchmark.titulo.subreporte.tablas.ratings";
	public static final String KEY_TITULO_SUB_REPORTE_LISTA_TABLAS_PEER_GROUP = "benchmark.titulo.subreporte.tablas.peer.group";
	public static final String KEY_TITULO_SUB_REPORTE_LISTA_TABLAS_INDICADORES = "benchmark.titulo.subreporte.tablas.indicadores";
	public static final String KEY_TITULO_SUB_REPORTE_LISTA_TABLAS_PEER_GROUP_INDICADORES = "benchmark.titulo.subreporte.tablas.peer.group.indicadores";
	public static final String GRAFICO_BENCHMARK_LABEL_EJE_X = ConfigManager
			.getValueAsString("benchmark.grafico.label.eje.x");
	public static final String GRAFICO_BENCHMARK_LABEL_EJE_Y = ConfigManager
			.getValueAsString("benchmark.grafico.label.eje.y");
	public static final String GRAFICO_BENCHMARK_INDICADORES_LABEL_EJE_X = ConfigManager
			.getValueAsString("benchmark.indicadores.grafico.label.eje.x");
	public static final String GRAFICO_BENCHMARK_INDICADORES_LABEL_EJE_Y = ConfigManager
			.getValueAsString("benchmark.indicadores.grafico.label.eje.y");
	public static final Integer BENCHMARK_TIPO_PERIODO_ANUAL = ConfigManager
			.getValueAsInteger("benchmark.tipo.periodo.anual");
	public static final Integer BENCHMARK_TIPO_PERIODO_TRIMESTRAL = ConfigManager
			.getValueAsInteger("benchmark.tipo.periodo.trimestral");
	public static final Integer FLAG_BUSCAR_DATA_PARA_EL_PERIODO = Integer
			.valueOf("0");
	public static final Integer FLAG_BUSCAR_ULTIMA_DATA_EXISTENTE = Integer
			.valueOf("1");
	public static final String KEY_CODIGO_INDICADORES_BENCHMARK = "benchmark.cod.indicadores";
	public static final String BENCHMARK_CODIGO_ERROR_SIN_INFORMACION = "-110";
	public static final String BENCHMARK_TODOS = ConfigManager
			.getValueAsString("benchmark.criterio.todos");

	public static final Integer ESTADO_PROYECCION_LARGA_CREACION = new Integer(
			0);
	public static final Integer ESTADO_PROYECCION_LARGA_CONFIRMAR = new Integer(
			1);
	public static final Integer ESTADO_PROYECCION_LARGA_VIGENTE = new Integer(2);
	public static final String CHACHE_CTAS_PROYECTADAS = "conxtexCtasProy";
	public static final String CHACHE_LTA_CTAS_PROYECTADAS = "conxtexLtaCtasProy";

	// periodos utilizados en el rating financiero
	public static final Integer PERIODO_0 = new Integer(0);
	public static final Integer PERIODO_1 = new Integer(1);
	public static final Integer PERIODO_2 = new Integer(2);

	// clave default utilizada cuando se concatenan propiedades con valores por
	// defecto
	public static final String KEY_DEFAULT = "default";

	// marias 20121113 - restricciones aplicadas a la seleccion de vaciados para
	// rating financiero
	// de acuerdo al modelo generico de calculo de rating
	public static final String RTG_FINANCIERO_TIPO_VACIADO_SOPORTADO = "rtg.finan.vac.tpo.vac";
	public static final String RTG_FINANCIERO_TIPO_BALANCE_SOPORTADO = "rtg.finan.vac.tpo.bal";
	public static final String RTG_FINANCIERO_PLAN_CUENTA_SOPORTADO = "rtg.finan.vac.plan.cta";
	public static final String RTG_NEGOCIO_DIAS_MAX_EVALUACION = "rtg.negoc.dias.max.eval";
	public static final String RTG_FINANCIERO_CIERRE_PARCIAL_SOPORTADO = "rtg.finan.vac.cierre.parcial";
	public static final String RTG_FINANCIERO_SOLO_VIGENTE_SOPORTADO = "rtg.finan.solo.vaciados.vigente";
	public static final String RTG_NUMERO_MINIMO_VACIADO_SOPORTADO = "rtg.numero.min.vac.soportado";
	public static final String RTG_NUMERO_MAXIMO_VACIADO_SOPORTADO = "rtg.numero.max.vac.soportado";
	// key que permite recuperar la cuenta por banca para obtener el segmento
	public static final String RTG_FINANCIERO_COD_CTA_SEGMENTO = "rtg.finan.cod.cta.sgmt";
	public static final String RTG_FINANCIERO_TPO_SEGMENTO = "rtg.finan.tpo.sgmt";
	public static final String RTG_PROYECTADO_TPO_SEGMENTO = "rtg.proy.tpo.sgmt";
	public static final String RTG_COMPORTAMIENTO_USA_TPO_SEGMENTO = "rtg.comp.tpo.sgmt";
	public static final String RTG_FINANCIERO_INDICADORES_FINANCIEROS = "rtg.finan.ind.finan";
	public static final String RTG_AJUSTE_POR_CALIDAD = "rtg.ajuste.calidad";
	public static final String RTG_AJUSTE_POR_CALIDAD_PROY = "rtg.proy.ajuste.calidad";
	public static final String ID_BANCAS_USAN_HOJA_INDEP = "rtg.indv.list.imd";
	public static final String ID_ORIGEN_INFO_ESPECIALES = "rtg.finan.indc.especiales";
	public static final String ID_ORIGEN_INFO_SOE = "rtg.finan.indc.soe";
	// key que recupera los imds necesarios para ser utilizado en el calculo del
	// rating financiero
	public static final String ID_IND_IMD_RTG = "rtg.finan.imd.especiales";

	public static final Integer BANCO_LOCAL_F = new Integer(0);

	public static final String CTX_ULTIMO_PERIODO = "CTX_ULTIMO_PERIODO";

	public static final List IMD_CALIF_RIESGO_LIST = ConfigManager
			.getValuesAsListString("imd.clasif.riesgo");
	public static final List IMD_CTS_RTG_LIST = ConfigManager
			.getValuesAsListString("idm.cts.rtg");
	public static final List IMD_IND_FORMULA_LIST = ConfigManager
			.getValuesAsListString("idm.indic.formula");

	public static final int IMD_MATRIZ_TIPO_BANCO = 0;
	public static final int IMD_MATRIZ_TIPO_RIESGO = 1;

	public static final Integer IMD_EST_VIGENTE = new Integer("6622");
	public static final Integer IMD_EST_EN_CURSO = new Integer("6621");
	public static final Integer IMD_EST_HISTORICO = new Integer("6623");

	// indicadores que deben ser actualizados en la tabla de la hoja
	// independiente luego de ser insertados
	public static final String IMD_IND_E100 = "E100";
	public static final String IMD_IND_IMD03 = "IMD03";
	public static final String IMD_IND_I200 = "I200";

	//DGJO INICIO IMD
	public static final String IMD_IND_IMD02 = "IMD02";
	public static final String IMD_E200 = "E200";
	//DGJO FIN IMD
	
	public static final String IMD_MATRICES = "idm.matriz";
	public static final String IMD_DESC = "desc";
	public static final String IMD_CTAS = "cts";
	public static final String IMD_TIER = "tier";
	public static final String IMD_COD = "cod";
	public static final String IMD = "imd";
	public static final String IMD_MTR = "mtr";
	public static final String IMD_CALIFICACION = "calificacion";
	public static final String IMD_OUTLOOK = "outlook";
	public static final String IMD_BANCO_EXT = "bancoExt";
	public static final String IMD_UNIDAD_MEDIDA = "umedida";
	public static final String IMD_LABEL = "label";
	public static final String IMD_TEXTBOX = "textBox";
	public static final Integer IMD_COD_FUENTE = new Integer("6550");
	public static final Integer IMD_COD_TIER = new Integer("6610");
	public static final Integer IMD_COD_OUTLOOK = new Integer("6570");
	public static final Integer IMD_COD_BANCO_EXT = new Integer("6590");
	public static final Integer IMD_COD_UNIDAD_MEDIDA = new Integer("1500");
	public static final Integer IMD_COD_CALIF_LOCAL = new Integer("4850");
	public static final Integer IMD_COD_CALIF_INTER = new Integer("4800");
	public static final int IMD_CTA_CODIGO = 0;
	public static final int IMD_CTA_DESCRIPCION = 1;
	public static final int IMD_CTA_UNIDAD = 2;
	public static final int IMD_TIPO_CALIF = 1;
	public static final int IMD_TIPO_OUTLOOK = 2;

	public static final String IMD_LST_HOJAS = "lstHojas";
	public static final String IMD_RUT_CLIENTE = "rutCte";
	public static final String IMD_NOMBRE_CLIENTE = "nomCli";
	public static final String IMD_RUT_OPE = "rutOpe";
	public static final String IMD_NOMBRE_OPE = "usuDig";
	public static final String IMD_FLAG_CON_RATING = "flgConRtg";
	public static final String IMD_MATRIZ_GRP = "matGrp";
	public static final String IMD_FECHA = "fecEmi";
	public static final String IMD_FECHA_ULT_ACT = "fecUltAct";
	public static final String IMD_ESTADO = "estado";
	public static final String IMD_ESTADO_ID = "estadoId";
	public static final String IMD_INDIC = "indic";
	public static final String IMD_CALIF_RIESGO = "calif_riesgo";

	public static final String IMD_COD_MTR = "codMtr";
	public static final String IMD_DESC_MTR = "descMtr";
	public static final String IMD_MTR_F = "mtrF";
	public static final String IMD_NOMBRE_MATRIZ = "NombreMatriz";

	public static final String IMD_COD_CUENTA = "codigoHI";
	public static final String IMD_DESC_CUENTA = "glosaHI";
	public static final String IMD_ID_UNIDAD_CUENTA = "unidadHI";
	public static final String IMD_UNIDAD_CUENTA = "unidadHI";
	public static final String IMD_VALOR_CUENTA = "valorHI";
	public static final String IMD_OUTLOOK_CUENTA = "outlookHI";
	public static final String IMD_FEC_CUENTA = "fecHI";
	public static final String IMD_FTE_SEL_CUENTA = "fteSelHI";
	public static final String IMD_TPO_CAMP_CUENTA = "tpoCampVal";
	public static final String IMD_TPO_CAMP_UNIDAD = "tpoCampUni";
	public static final String IMD_ELIMINAR_F = "eliminarF";
	public static final String IMD_FLAG_ELIMINAR_HOJA = "eliminarHojaIMD";

	public static final String IMD_FTE_HI = "fteHI";
	public static final String IMD_FTE_ID_HI = "fteIdHI";
	public static final String IMD_ITEM_HI = "itemHI";
	public static final String IMD_ITEM_ID_HI = "itemIdHI";

	public static final String ACTUALIZAR_CLASIFICACIONES_SEFE = "actualizar.clasificaciones.sefe";

	public static final Long RATING_GRUPAL_PYME_RUT_EXIGEN_SOCIOS = ConfigManager
			.getValueAsLong("rating.grupal.pyme.rut.exige.socios");

	public static final String KEY_ERR_COMPORTAMIENTO_NO_EXISTE_VACIADO_PARA_CALCULO_SEGMENTO = "error.vaciado.no.existe";
	public static final String KEY_ERR_COMPORTAMIENTO_NO_EXISTE_SEGMENTO = "error.comp.no.existe.segmento";
	public static final String KEY_VPP = "vac.dte.cta.vpp";
	public static final String R_PROPORC = "vac.dte.cta.rprop";
	public static final Integer RTG_GRUPO_COND_BORDE = new Integer(3030);

	public static final String KEY_SOE = "SOE";
	public static final String KEY_BI = "BI";
	// condicion de borde para las otras formulas: 0: no condicion de borde-->
	// 1: condicion de borde
	public static final Integer OTRAS_FORMULAS_FLAG_COND_BORDE_ON = new Integer(
			1);
	public static final Integer OTRAS_FORMULAS_FLAG_COND_BORDE_OFF = new Integer(
			0);
	public static final Integer ID_CLASIF_TIPO_INFO_ADICIONAL = Integer
			.valueOf("5020");
	public static final Integer ID_CLASIF_INFO_ADICIONAL_MERCADO_DESTINO = Integer
			.valueOf("5030");
	public static final Integer ID_CLASIF_INFO_ADICIONAL_ESTRUCTURA_COSTOS = Integer
			.valueOf("5040");
	public static final String SP_SOE_OBTENER_SOE = "ConstructoraInmobilaria.SP_SOE_OBTENER_SOE";
	public static final String SP_BI_OBTENER_BI = "ConstructoraInmobilaria.SP_BI_OBTENER_BI";
	public static final String SP_SOE_OBTENER_CABECERA_HOJA_SOE = "ConstructoraInmobilaria.SP_SOE_OBTENER_CABECERA_HOJA_SOE";
	public static final String SP_BI_OBTENER_CABECERA_HOJA_BI = "ConstructoraInmobilaria.SP_BI_OBTENER_CABECERA_HOJA_BI";
	public static final String SP_SOE_UPDATE_VALOR_HOJA_SOE = "ConstructoraInmobilaria.SP_SOE_UPDATE_VALOR_HOJA_SOE";
	public static final String SP_BI_UPDATE_VALOR_HOJA_BI = "ConstructoraInmobilaria.SP_BI_UPDATE_VALOR_HOJA_BI";
	public static final String SP_SOE_OBTENER_SOE_POR_AVANCE = "ConstructoraInmobilaria.SP_SOE_OBTENER_SOE_POR_AVANCE";
	public static final String SP_BI_OBTENER_BI_POR_AVANCE = "ConstructoraInmobilaria.SP_BI_OBTENER_BI_POR_AVANCE";
	public static final String SP_SOE_ASOCIAR_SOE_A_RATING = "ConstructoraInmobilaria.SP_SOE_ASOCIAR_SOE_A_RATING";
	public static final String SP_SOE_ACT_ESTADO_SOE_CALCULO = "ConstructoraInmobilaria.SP_SOE_ACT_ESTADO_SOE_CALCULO";
	public static final String SP_BI_ASOCIAR_BI_A_RATING = "ConstructoraInmobilaria.SP_BI_ASOCIAR_BI_A_RATING";
	public static final String SP_SOE_UPDATE_ESTADO_SOE = "ConstructoraInmobilaria.SP_SOE_UPDATE_ESTADO_SOE";
	public static final String SP_BI_UPDATE_ESTADO_BI = "ConstructoraInmobilaria.SP_BI_UPDATE_ESTADO_BI";
	public static final String SP_OBTENER_INDC_BAL_INMOB = "ConstructoraInmobilaria.SP_OBTENER_INDC_BAL_INMOB";

	public static final String ID_MODULO_VACIADO = "VTAOP00200";

	public static final String SP_SOE_OBTENER_LISTA_HOJA_SOE = "ConstructoraInmobilaria.SP_SOE_OBTENER_LISTA_HOJA_SOE";

	public static final String SP_BI_OBTENER_LISTA_HOJA_BI = "ConstructoraInmobilaria.SP_BI_OBTENER_LISTA_HOJA_BI";

	public static final String KEY_GRUPO_CUENTAS_ADICIONALES_PLAN = "grp.ctas.adic.plan";
	public static final String KEY_GRUPO_OTROS_INDICADORES_PLAN = "grp.otros.indic.plan";
	public static final String KEY_NOMBRE_GRUPO_CUENTAS_ADICIONALES_PLAN = "grp.ctas.adic.plan.nom";
	public static final String NOMBRE_DEFAULT_AGRUPACION_CUENTAS_ADICIONALES = "Cuentas Adicionales";

	// Sps agricola
	public static final String SP_AGR_INSERTVACIADO = "Agricola.sp_agr_insertVaciado";
	public static final String SP_CLONA_VAC_AGRIC = "Agricola.SP_CLONA_VAC_AGRIC";
	public static final String SP_AGR_INSERT_PRODUCTO_GANADERO = "Agricola.sp_agr_insertProductoGanadero";
	public static final String SP_AGR_INSERT_PRODUCTO_AGRICOLA = "Agricola.sp_agr_insertProductoAgricola";
	public static final String SP_AGR_INSERT_ARRIENDO_SUELO = "Agricola.sp_agr_insertArriendoSuelo";
	public static final String SP_AGR_INSERT_INGRESO_EGRESO = "Agricola.sp_agr_insertIngresoEgreso";
	public static final String SP_AGR_INSERT_PROPIEDAD = "Agricola.sp_agr_insertPropiedad";
	public static final String SP_AGR_INSERT_OTRO_CONCEPTO = "Agricola.sp_agr_insertOtroConcepto";
	public static final String SP_AGR_INSERT_OTRO_ACTIVO_FIJO = "Agricola.sp_agr_insertOtroActivoFijo";
	public static final String SP_AGR_INSERT_DEUDA_LARGO_PLAZO = "Agricola.sp_agr_insertDeudaLargoPlazo";
	public static final String SP_AGR_INSERT_DEUDA_CORTO_PLAZO = "Agricola.sp_agr_insertDeudaCortoPlazo";
	public static final String SP_AGR_INSERT_FLUJO_AGRICOLA = "Agricola.sp_agr_insertFlujoAgricola";
	public static final String SP_AGR_INSERT_FLUJO_GANADERO = "Agricola.sp_agr_insertFlujoGanadero";
	public static final String SP_AGR_INSERT_FLUJO_VENTA_GANADERO = "Agricola.sp_agr_insertFlujoVentaGanadero";
	public static final String SP_AGR_BUSCAR_PRODUCTOS_AGRICOLAS = "Agricola.sp_agricola_buscar_productos_agricolas";
	public static final String SP_AGR_BUSCAR_PRODUCTO_AGRICOLA = "Agricola.SP_AGR_BUSCAR_PRODUCTO_AGRICOLA";
	public static final String SP_AGR_BUSCAR_PRODUCTO_GANADERO_PARAM = "Agricola.SP_AGR_BUSCAR_PRODUCTO_GANADERO_PARAM";
	public static final String SP_AGR_BUSCAR_PROD_MASA_GANADERA = "Agricola.SP_AGR_BUSCAR_PROD_MASA_GANADERA";
	public static final String SP_BUSCAR_PLANTACIONES = "ParametrosAgricola.SP_BUSCAR_PLANTACIONES";
	public static final String SP_BUSCAR_PARAMETROS_GANADERIA = "ParametrosAgricola.SP_BUSCAR_PARAMETROS_GANADERIA";
	public static final String SP_BUSCAR_PARAM_PROD_GANAD = "ParametrosAgricola.SP_BUSCAR_PARAM_PROD_GANAD";

	public static final String SP_AGR_BUSCAR_CABECERA_PRODUCTOS_AGRICOLAS = "Agricola.sp_agricola_buscar_cabecera_prod_agr";
	public static final String SP_AGR_BUSCAR_CAB_EGRE_INGR = "Agricola.SP_AGR_BUSCAR_CAB_EGRE_INGR";
	public static final String SP_AGR_BUSCAR_CABECERA_FLUJO_AGRICOLAS = "Agricola.sp_agricola_buscar_cabecera_flujo_agr";
	public static final String SP_AGR_BUSCAR_VACIADO_AGRICOLA = "Agricola.sp_buscar_vaciado_agricola";
	public static final String SP_AGR_ASOCIAR_VACIADO_RATING = "Agricola.SP_AGR_ASOCIAR_VACIADO_RATING";
	public static final String SP_AGR_BUSCAR_SUM_FLJ_VENTA_GANAD = "Agricola.SP_AGR_BUSCAR_SUM_FLJ_VENTA_GANAD";
	public static final String SP_AGR_BUSCAR_ARRIENDO_SUELO_POR_TIPO = "Agricola.SP_AGR_BUSCAR_ARRIENDO_SUELO_POR_TIPO";
	public static final String SP_AGR_BUSCAR_DEUDA_LARGO_PLAZO = "Agricola.SP_AGR_BUSCAR_DEUDA_LARGO_PLAZO";
	public static final String SP_AGR_BUSCAR_CALEN_DEU_LP = "Agricola.SP_AGR_BUSCAR_CALEN_DEU_LP";
	public static final String SP_AGR_BUSCAR_SUM_CALEN_DEU_LP = "Agricola.SP_AGR_BUSCAR_SUM_CALEN_DEU_LP";
	public static final String SP_AGR_BUSCAR_SUM_DEU_CP = "Agricola.SP_AGR_BUSCAR_SUM_DEU_CP";
	public static final String SP_AGR_BUSCAR_SUM_DEU_CP_POR_TPO_DEUDA = "Agricola.SP_AGR_BUSCAR_SUM_DEU_CP_POR_TPO_DEUDA";

	public static final String SP_AGR_BUSCAR_SUM_FLJ_RESUMEN = "Agricola.SP_AGR_BUSCAR_SUM_FLJ_RESUMEN";

	public static final String SP_AGR_BUSCAR_SUM_FLJ_GANAD = "Agricola.SP_AGR_BUSCAR_SUM_FLJ_GANAD";
	public static final String SP_AGR_INSERT_CALENDARIO_DEUDA_PL = "Agricola.SP_AGR_INSERT_CALENDARIO_DEUDA_PL";
	public static final String SP_AGR_BORRAR_FLUJOS = "Agricola.SP_AGR_BORRAR_FLUJOS";
	public static final String SP_AGR_INSERT_FLUJO_RESUMEN = "Agricola.SP_AGR_INSERT_FLUJO_RESUMEN";
	public static final String SP_AGR_UPDATE_VAC_AGRICOLA = "Agricola.SP_AGR_UPDATE_VAC_AGRICOLA";
	public static final String SP_AGR_BUSCAR_VAC_AGRICOLA_POR_ID_CLIENTE = "Agricola.SP_AGR_BUSCAR_VAC_AGRICOLA_POR_ID_CLIENTE";
	public static final String SP_AGR_BUSCAR_CAB_OTROS_CONCEPTOS = "Agricola.SP_AGR_BUSCAR_CAB_OTROS_CONCEPTOS";
	public static final String SP_AGR_BUSCAR_FLUJOS_AGRICOLAS = "Agricola.SP_AGR_BUSCAR_FLUJOS_AGRICOLAS";
	public static final String SP_AGR_BUSCAR_RESUMEN_FLUJOS_AGRICOLAS = "Agricola.SP_AGR_BUSCAR_RESUMEN_FLUJOS_AGRICOLAS";
	public static final String SP_AGR_BUSCAR_SUM_EXPORTACIONES = "Agricola.SP_AGR_BUSCAR_SUM_EXPORTACIONES";
	public static final String SP_AGR_BUSCAR_LST_FLUJO_RESUMEN = "Agricola.SP_AGR_BUSCAR_LST_FLUJO_RESUMEN";
	public static final String SP_AGR_BUSCAR_PROPIEDADES = "Agricola.SP_AGR_BUSCAR_PROPIEDADES";
	public static final String SP_AGR_BUSCAR_OTR_ACT_FJOS = "Agricola.SP_AGR_BUSCAR_OTR_ACT_FJOS";
	public static final String SP_AGR_BUSCAR_CAB_FLJ_VTA_GAN = "Agricola.SP_AGR_BUSCAR_CAB_FLJ_VTA_GAN";
	public static final String SP_CON_ULT_VERS_PLANT_RTG = "Agricola.SP_CON_ULT_VERS_PLANT_RTG";

	// Reporte plantaciones agricola
	public static final String SP_AGR_CON_PLANT_REP = "Agricola.SP_AGR_CON_PLANT_REP";
	public static final String SP_AGR_CON_ANIM_REP = "Agricola.SP_AGR_CON_ANIM_REP";
	public static final String SP_AGR_CON_OAF_REP = "Agricola.SP_AGR_CON_OAF_REP";
	public static final String SP_AGR_CON_CAL_DEU_REP = "Agricola.SP_AGR_CON_CAL_DEU_REP";

	public static final String SP_CONSULTA_AGR_X_RUT = "Agricola.SP_CONSULTA_AGR_X_RUT";
	public static final String SP_CONSULTA_AGR_X_RUT_Y_RTG = "Agricola.SP_CONSULTA_AGR_X_RUT_Y_RTG";

	public static final String SP_AGR_INSERT_CLASIF = "ParametrosAgricola.SP_AGR_INSERT_CLASIF";
	public static final String SP_AGR_INSERT_PAR_AGRIC = "ParametrosAgricola.SP_AGR_INSERT_PAR_AGRIC";
	public static final String SP_AGR_INSERT_PAR_AGRIC_ANO = "ParametrosAgricola.SP_AGR_INSERT_PAR_AGRIC_ANO";
	public static final String SP_AGR_INSERT_PAR_GANAD = "ParametrosAgricola.SP_AGR_INSERT_PAR_GANAD";
	public static final String SP_AGR_INSERT_PAR_PROD_GANAD = "ParametrosAgricola.SP_AGR_INSERT_PAR_PROD_GANAD";
	public static final String SP_AGR_CADUCAR_PARAMETROS = "ParametrosAgricola.SP_AGR_CADUCAR_PARAMETROS";
	public static final String SP_ADM_INS_AUDITORIA = "ParametrosAgricola.SP_ADM_INS_AUDITORIA";
	public static final String SP_SUBIR_PLANTILLA = "ParametrosAgricola.SP_SUBIR_PLANTILLA";
	public static final String SP_BAJAR_PLANTILLA = "Agricola.SP_BAJAR_PLANTILLA";
	public static final String SP_ACT_USU_AGR = "Agricola.SP_ACT_USU_AGR";
	public static final String SP_DEL_DETALLE_AGR = "Agricola.SP_DEL_DETALLE_AGR";

	// SPs Otros Indicadores
	public static final String SP_SELECT_CALIFICACION_RIESGO_VACIADO = "Vaciado.sp_sel_calif_rgo_vac";
	public static final String SP_SELECT_REL_CALIFICACION_RGO_CALIFICADORA = "Vaciado.sp_sel_rel_calif_rgo_califica";
	public static final String SP_OBTENER_RELACION_CALIFICACION = "Vaciado.sp_obt_relacion_calificacion";
	public static final String SP_INS_CALIFICACION_RIESGO_VACIADO = "Vaciado.sp_ins_calif_rgo_vac";
	public static final String SP_INS_CLASIFICACION_RIESGO_VACIADO = "Vaciado.SP_INS_CLASIF_RGO_VAC";
	public static final String SP_DELETE_CALIFICACION_RIESGO_VACIADO_POR_VACIADO = "Vaciado.sp_del_calif_rgo_vac_x_vac";
	public static final String SP_SEL_REL_CALIFICADORA_CLASI = "Vaciado.SP_SEL_REL_CALIFICADORA_CLASI";
	public static final Integer ID_INDICADORES_NORMATIVOS = Integer
			.valueOf("1096");
	public static final String PARAM_ID_CALIFICADORA = "ID_CALIFICADORA";
	public static final String PARAM_ID_CALIFICACION = "ID_CALIFICACION";
	public static final String PARAM_CALIFICACION = "CALIFICACION";
	public static final String PARAM_ID_TIPO_CALIFICACION = "ID_TIPO_CALIFICACION";
	// por definir
	public static final Integer CLASIF_ORIGEN_INFO_PRODUCTO_AGRICOLA = Integer
			.valueOf("6915");
	public static final Integer CLASIF_ORIGEN_INFO_FLUJO_RESUMEN = Integer
			.valueOf("6929");

	public static final String AGRI_COD_HA = "IFA1";
	public static final String AGRI_COD_REND_TOTAL = "IFA2";
	public static final String AGRI_COD_REND_EXP = "IFA3";
	public static final String AGRI_COD_PRECIO_PROD = "IFA4";
	public static final String AGRI_COD_REND_INTERNO = "IFA5";
	public static final String AGRI_COD_PRECIO_INTERNO = "IFA6";
	public static final String AGRI_COD_INGR_TOTALES = "IFA7";
	public static final String AGRI_COD_CTOS_DIRECTOS = "IFA8";
	public static final String AGRI_COD_MARGEN_BRUTO = "IFA10";
	public static final String AGRI_COD_MARGEN_BRUTO_UND_SUPERF = "IFA11";
	public static final String AGRI_COD_PRC_EXPORTACION = "IFA12";
	public static final String AGRI_COD_COSTO_TOTAL = "IFA9";

	// IFG2 INGRESOS_OPER
	// {SUMA_ING_TOTALES_PL}+{SUMA_INGRESOS_ANIMALES}+{SUMA_INGRESOS_TOT}
	public static final String AGRI_COD_INGRESOS_OPER = "IFG2";
	// IFG3 OTROS_INGRESOS_OPER {OTROS_ING_OPER}
	public static final String AGRI_COD_OTROS_INGRESOS_OPER = "IFG3";
	// INGRESOS {IFG2}+{IFG3}
	public static final String AGRI_COD_FR_INGRESOS = "IFG1";
	// IFG5 CTOS_OPER {SUMA_COST_TOTALES_PL}+{COSTOS_TOTALES_ANIMALES}
	public static final String AGRI_COD_FR_CTOS_OPER = "IFG5";
	// IFG6 OTROS_CTOS_OPER {OTROS_EGRE_OPER} --->
	// TA_SEFE_AGR_INGRESOS_EGRESOS.VAL_ING_EGR cuando
	// TA_SEFE_AGR_INGRESOS_EGRESOS.TPO_INGRESO_EGRESO_ID = 6879 y ANO (que
	// corresponda)
	public static final String AGRI_COD_FR_OTROS_CTOS_OPER = "IFG6";
	// IFG4 CTOS {IFG5}+{IFG6}
	public static final String AGRI_COD_FR_CTOS = "IFG4";
	// IFG7 MARGEN_BRUTO {IFG1}-{IFG4}
	public static final String AGRI_COD_FR_MARGEN_BRUTO = "IFG7";
	// IFG8 MARGEN_BRUTO_PRC {IFG7}/{IFG1}
	public static final String AGRI_COD_FR_MARGEN_BRUTO_PRC = "IFG8";
	// IFG9 GASTOS_GENERALES {IFG4}*{GASTOS
	// ADMINISTRACION}+(CULTIVOS_SUP*CULTIVOS_VAL)+(FRUTALES_SUP*FRUTALES_VAL)+OTR_ARR_AGR
	public static final String AGRI_COD_FR_GASTOS_GENERALES = "IFG9";
	// IFG10 ROB {IFG7}-{IFG9}
	public static final String AGRI_COD_FR_ROB = "IFG10";
	// IFG11 ROB_PRC {IFG10}/{IFG1}
	public static final String AGRI_COD_FR_ROB_PRC = "IFG11";
	// IFG12 GASTOS_FINANS
	// (({TOTAL_MONTO_APROB_CP}+{TOTAL_MONTO_SOLIC_CP})*{TASA_INT})+{INTERES_CONSOLIDADO}
	public static final String AGRI_COD_FR_GASTOS_FINANS = "IFG12";
	// IFG13 RESULTADO_OPER_NETO {IFG10}-{IFG12}
	public static final String AGRI_COD_FR_RESULTADO_OPER_NETO = "IFG13";
	// IFG14 INGR_FUERA_EXPL {INGRESOS NO OPERAC}
	public static final String AGRI_COD_FR_INGR_FUERA_EXPL = "IFG14";
	// IFG15 EGR_FUERA_EXPL {EGRESOS NO OPERAC}
	public static final String AGRI_COD_FR_EGR_FUERA_EXPL = "IFG15";
	// IFG16 UTIL_ANTES_IMP {IFG13}+{IFG14}-{IFG15}
	public static final String AGRI_COD_FR_UTIL_ANTES_IMP = "IFG16";
	// IFG17 IMPUESTO_RENTA
	public static final String AGRI_COD_FR_IMPUESTO_RENTA = "IFG17";
	// IFG18 UTIL_NETA {IFG16}-{IFG17}
	public static final String AGRI_COD_UTIL_NETA = "IFG18";
	// IFG19 GBO {IFG18}+{IFG15}-{IFG14}
	public static final String AGRI_COD_GBO = "IFG19";
	// IFG20 REINVERSIONES
	public static final String AGRI_COD_REINVERSIONES = "IFG20";
	// IFG21 RETIROS {RET_ANUALES_CLI}
	public static final String AGRI_COD_RETIROS = "IFG21";
	// IFG22 FLJ_DISP_DEU_LP {IFG19}-{IFG20}-{IFG21}
	public static final String AGRI_FLJ_DISP_DEU_LP = "IFG22";
	// IFG23 AMORTIZACIONES {AMORT_CONSOLIDADO_DEU}
	public static final String AGRI_FLJ_AMORTIZACIONES = "IFG23";
	// IFG24 RESULTADO_NO_OPER_NETO {IFG14}-{IFG15}
	public static final String AGRI_FLJ_RESULTADO_NO_OPER_NETO = "IFG24";
	// IFG25 SALDO_CAJA {IFG22}-{IFG23}+{IFG24}
	public static final String AGRI_FLJ_SALDO_CAJA = "IFG25";
	// IFG26 SALDO_CAJA_ACUM {IFG25}
	public static final String SALDO_CAJA_ACUM = "IFG26";
	// SALDO ANTERIOR ACUMULADO
	public static final String SALDO_CAJA_ACUM_ANT = "IFG26_ANT";

	public static final String SUMA_ING_TOTALES_PL = "SUMA_ING_TOTALES_PL";
	public static final String OTROS_ING_OPER = "OTROS_ING_OPER";
	public static final Integer TIPO_CALIFICACION_LARGO_PLAZO = Integer
			.valueOf("4921");

	public static final String SP_ELIMINAR_CALIFICACIONES_LOC = "Vaciado.sp_eliminar_calificaciones_loc";
	public static final String SP_ELIMINAR_CALIFICACIONES_NAC = "Vaciado.sp_eliminar_calificaciones_nac";
	public static final String SP_ELIMINAR_CALIFICACIONES_INT = "Vaciado.sp_eliminar_calificaciones_int";
	public static final String SP_OBTENER_CALIFICADORAS = "Vaciado.sp_obtener_calificadoras";
	public static final String SP_OBTENER_CALIF_INGR_LOC = "Vaciado.sp_obtener_calif_ingr_loc";
	public static final String SP_OBTENER_CALIF_INGR_NAC = "Vaciado.sp_obtener_calif_ingr_nac";
	public static final String SP_OBTENER_CALIF_INGR_INT = "Vaciado.sp_obtener_calif_ingr_int";
	public static final String SP_OBTENER_CLASIF_RIESGO_LOC = "Vaciado.sp_obtener_clasif_riesgo_loc";
	public static final String SP_OBTENER_CLASIF_RIESGO_NAC = "Vaciado.sp_obtener_clasif_riesgo_nac";
	public static final String SP_OBTENER_CLASIF_RIESGO_INT = "Vaciado.sp_obtener_clasif_riesgo_int";
	public static final String SP_INSERTAR_CLASIF_RIESGO_LOC = "Vaciado.sp_insertar_clasif_rgo_loc";
	public static final String SP_INSERTAR_CALIF_RIESGO_OUTLOOK = "Vaciado.sp_insertar_calif_rgo_outlook";
	public static final String SP_OBTENER_RANGO_CALIFICACION = "Vaciado.sp_obtener_rango_calificacion";
	public static final String SP_CLASIFICADORA_VAL_SELECT_LOC = "Vaciado.sp_clasif_val_selec_loc";
	public static final String SP_CLASIFICADORA_VAL_SELECT_NAC_INT = "Vaciado.sp_clasif_val_selec_nac_int";
	public static final String SP_OUTLOOK_VAL_SELECT_NAC_INT = "Vaciado.sp_outlook_val_selec_nac_int";
	public static final String SP_OUTLOOK_VAL_SELECT_LOC = "Vaciado.sp_outlook_val_selec_loc";
	public static final String SP_OBTENER_VALORES_TIER = "Vaciado.sp_obtener_valores_tier";
	public static final String SP_TIER_VAL_SELECT = "Vaciado.sp_obtener_val_selec_tier";
	public static final String SP_INSERTAR_TIER = "Vaciado.sp_insertar_tier";
	public static final String SP_OBTENER_VACIADO_AGRICOLA = "Vaciado.SP_OBTENER_VACIADO_AGRICOLA";
	public static final String SP_SEL_CUENTA_X_ID = "Vaciado.SP_SEL_CUENTA_X_ID";
	public static final String SP_OBT_CALIF_RGO_VAC = "Vaciado.SP_OBT_CALIF_RGO_VAC";
	public static final String SP_OBT_CLASIF_RGO_VAC = "Vaciado.SP_OBT_CLASIF_RGO_VAC";
	public static final String SP_UPDATE_USER_INFO_COMP_VAC = "Vaciado.SP_UPDATE_USER_INFO_COMP_VAC";

	// 6794
	public static final Integer CLASIF_ID_MASA_OVEJAS = new Integer(6794);
	// 3.3 Masa Ganadera (BOVINA - OVINA)
	public static final Integer CLASIF_ID_MASA_GANA_BOVINA = new Integer(6828);
	// 3.1 Suelos destinados empastadas y/o cultivos forrajeros
	public static final Integer CLASIF_ID_SUELO_DESTINADO_EMPASTADAS = new Integer(
			6826);
	public static final Integer CLASIF_ID_TPO_VENTA_LANAS = new Integer(6836);
	public static final Integer CLASIF_ID_TPO_VENTA_LECHE = new Integer(6837);
	public static final Integer CLASIF_ID_OTROS_INGR_EXPLOT = new Integer(6878);
	public static final Integer CLASIF_ID_OTROS_EGRE_EXPLOT = new Integer(6879);
	public static final Integer CLASIF_ID_INGR_NO_OPERACIONALES = new Integer(
			6880);
	public static final Integer CLASIF_ID_EGRE_NO_OPERACIONALES = new Integer(
			6881);
	// Factores de Correccion Otros Activos Circulantes - Otros Activos
	// Circulantes Corto Plazo (%)
	public static final Integer CLASIF_ID_FACTOR_CORRECCION_OTROS_ACTIVOS = new Integer(
			900452);
	public static final String KEY_GASTOS_ADMINISTRACION = "rtg.proy.agricola.gastos.admin";// OK
	public static final String KEY_DIVISOR_ROB_VTAS = "rtg.proy.agricola.divisor";// ok
	public static final String KEY_TASA_DESCUENTO = "rtg.proy.agricola.tasa.descuento";// ok
	public static final String KEY_REINVERSIONES_FRUTICOLAS = "rtg.proy.agricola.reinv.fruticolas";// OK
	public static final String KEY_REINVERSIONES_GANADERAS = "rtg.proy.agricola.reinv.ganaderas";// OK

	public static final Integer CLASIF_ID_MAQUINARIAS = new Integer(6896);
	public static final Integer CLASIF_ID_VEHICULOS = new Integer(6897);
	public static final Integer CLASIF_ID_EDIFICIOS = new Integer(6898);
	public static final Integer CLASIF_ID_INSTALACIONES = new Integer(6899);
	public static final Integer CLASIF_ID_OTROS_ACTIVOS_FIJOS = new Integer(
			6900);
	public static final Integer CLASIF_ID_OTROS_ACTIVOS_FIJOS_DERIVADOS = new Integer(
			6901);

	public static final String KEY_PARAM_RENTA_PRESUNTA_EFECTIVA = "rtg.proy.agricola.imp.renta.ren.uf";
	// {RENTA_PRESUNTA_EFECTIVA} = TA_SEFE_CLASIF.NOMBRE_CORTO where
	// TA_SEFE_CLASIF.CLASIF_ID = 900451
	public static final String CLASIF_ID_IMPUESTO_RENTA_I = "rtg.proy.agricola.imp.renta.prje";
	// PARAMETRO ACT. FIJO - Maquinarias (%) 900453
	public static final Integer CLASIF_ID_PARAM_MAQUINARIAS = new Integer(
			900453);
	// PARAMETRO ACT. FIJO - VehÃ­culos (%) 900454
	public static final Integer CLASIF_ID_PARAM_VEHICULO = new Integer(900454);
	// PARAMETRO ACT. FIJO - Edificios (%) 900455
	public static final Integer CLASIF_ID_PARAM_EDIFICIO = new Integer(900455);
	// PARAMETRO ACT. FIJO - Instalaciones (%) 900456
	public static final Integer CLASIF_ID_PARAM_INSTALACIONES = new Integer(
			900456);
	// PARAMETRO ACT. FIJO - Otros Activos Fijos (%) 900457
	public static final Integer CLASIF_ID_FACTOR_PARAM_OTROS_ACTIVOS_FIJOS = new Integer(
			900457);
	// PARAMETRO ACT. FIJO - Otros Activos Fijos Derivados Deuda Propuesta (%)
	// 900458
	public static final Integer CLASIF_ID_PARAM_OTROS_ACTIVOS_FIJOS_DERIVADOS = new Integer(
			900458);

	public static final Integer CLASIF_ID_OTROS_CONCEPTOS_ACTIVOS = new Integer(
			6891);
	public static final Integer CLASIF_ID_MASA_GANADERA_BOVINA_OVINA = new Integer(
			6828);
	public static final Integer CLASIF_ID_OTROS_CONCEPTOS = new Integer(6893);
	public static final Integer CLASIF_ID_OTR_ACRE_LP = new Integer(6892);
	// DEUDA CORTO PLAZO PROPUESTA
	public static final Integer CLASIF_ID_DEUDA_CP_PROPUESTA = new Integer(6911);
	// 11.4.2 DEUDAS CORTO PLAZO VIGENTES
	public static final Integer CLASIF_ID_DEUDA_CP_VIGENTE = new Integer(6912);
	public static final String KEY_FACTOR_DE_CORRECCION = "rtg.proy.agricola.multiplicador.prc"; // OK
	public static final String CLASIF_ID_PER_CRITICO_MO = "rtg.proy.agricola.critico";
	public static final String CLASIF_ID_VULNERABILIDAD = "rtg.proy.agricola.vulnerabilidad";
	public static final Integer CLASIF_ID_PER_CRITICO_MO_ID_C = new Integer(
			6753);
	public static final Integer CLASIF_ID_PER_CRITICO_MO_ID_D = new Integer(
			6754);
	public static final Integer CLASIF_ID_PER_CRITICO_MO_ID_E = new Integer(
			6755);
	public static final String CTX_OCHENTA_PR = "OCHENTA_PR";
	public static final String KEY_RECALCULO_ACTIVO = "vaciado.com.recalculo.flag";
	public static final String KEY_RANGO_CTA_COM = "vaciado.com.rango.com";

	public static final String KEY_TASA_INTERES_GASTOS_FINANCIEROS = "rtg.proy.agricola.tasa.int";
	public static final Integer ID_CULTIVOS = new Integer(6871);
	public static final Integer ID_FRUTALES = new Integer(6872);
	public static final Integer ID_OTR_ARR_AGR = new Integer(6873);
	public static final Integer ID_DEUDA_LARGO_PLAZO = new Integer(6906);

	public static final Integer ID_INFO_ADICIONAL_INGRESO_POR_LINEA_PRODUCTO_NEG = Integer
			.valueOf("5021");
	public static final String RTG_INDIVIDUAL_COD_CTA_SEGMENTO = "rtg.indiv.cod.cta.sgmt";
	public static final String RTG_INDIVIDUAL_TPO_SEGMENTO = "rtg.indiv.tpo.sgmt";
	public static final String CODIGO_OP_CARGA_PLANTILLA_AGRICOLA = "400157";
	public static final String CODIGO_OP_CARGA_PLANTILLA_INMOBILIARIA = "400158";
	public static final String CODIGO_OP_CARGA_PLANTILLA_CONSTRUCTORA = "400159";
	public static final String SP_BIB_BUSCAR_ARCHIVOS_ASOC_VACIADO = "Biblioteca.SP_BIB_BUSCAR_ARCHIVOS_ASOC_VACIADO";
	public static final String SP_BIB_BORRAR_ARCHIVOS_ASOC_VACIADO = "Biblioteca.SP_BIB_BORRAR_ARCHIVOS_ASOC_VACIADO";
	public static final String SP_BIB_BUSCAR_ARCHIVOS_ASOC_RATING = "Biblioteca.SP_BIB_BUSCAR_ARCHIVOS_ASOC_RATING";
	public static final String SP_BIB_BORRAR_ARCHIVO_FILENET = "Biblioteca.SP_BIB_BORRAR_ARCHIVO_FILENET";
	public static final String SP_BIB_INSERTAR_METADATA = "Biblioteca.SP_BIB_INSERTAR_METADATA";
	public static final String SP_BIB_BUSCAR_ARCHIVOS_POR_CRITERIO_BUSQUEDA = "Biblioteca.SP_BIB_BUSCAR_ARCHIVOS_POR_CRITERIO_BUSQUEDA";

	public static final Integer ID_CLASIF_TIPO_ARCHIVOS_FILENET = Integer
			.valueOf("6940");
	public static final String SP_BIB_ACTUALIZAR_METADATA = "Biblioteca.SP_BIB_ACTUALIZAR_METADATA";

	public static final String SP_BIB_BORRAR_METADATA_POR_ID_FILENET = "Biblioteca.SP_BIB_BORRAR_METADATA_POR_ID_FILENET";
	public static final Integer ID_CLASIF_CONFIDENCIAL_NO = Integer
			.valueOf("5402");
	public static final Integer ID_CLASIF_CONFIDENCIAL_SI = Integer
			.valueOf("5401");
	public static final String MODULO_BILIOTECA = "VTAOP00205";
	public static final String MODULO_RATING = "VTAOP00202";

	public static final String RUT_MERCADO_RATING_BANCOS = "rating.reporte.evaluacion.final.rut.mercado";
	
	public static final String NIVEL_VENTA_AGRICOLA = "Agrícolas"; //Requerimiento 7.4.15 Sprint 1
	
	// constantes para la identificacióe la operacion subir archivo
	public static final String KEY_COD_SUBIR_ARCHIVO = "COD_SUBIR_ARCHIVO";
	public static final String COD_ARCHIVO_NUEVO = "N001";
	public static final String COD_ARCHIVO_ACTUALIZADO = "A001";
	
	// Sprint 3 7.4.2 ajuste de nota minima
	// parametros nota minima por rating y banca
	public static final String PARAM_NOTA_MIMINA_INDIVIDUAL = "nota.minima.individual";
	public static final String PARAM_NOTA_MIMINA_FINANCIERO = "nota.minima.financiero";
	public static final String PARAM_NOTA_MIMINA_PROYECTADO = "nota.minima.proyectado";
	public static final String PARAM_NOTA_MIMINA_NEGOCIO = "nota.minima.negocio";
	public static final String PARAM_NOTA_MIMINA_COMPORTAMIENTO = "nota.minima.comportamiento";
	public static final String PARAM_NOTA_MIMINA_GRUPAL_PYME = "nota.minima.grupal.pyme";
	public static final String PARAM_NOTA_MIMINA_GRUPAL_MULTISEGMENTO = "nota.minima.grupal.multisegmento";
}
