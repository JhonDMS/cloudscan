package com.bch.sefe.rating;

public interface ConstantesRating {
	// identificadores de set de datos
	public final static String DATA_RATING_INDIVIDUAL 		   = "rtgInd";
	public final static String DATA_RATING_FINANCIERO 		   = "rtgFin";
	public final static String DATA_RATING_NEGOCIO	 		   = "rtgNeg";
	public final static String DATA_RATING_COMPORTAMIENTO 	   = "rtgCmp";
	public final static String DATA_RATING_PROYECTADO	 	   = "rtgPry";
	public final static String DATA_RATING_GARANTE		 	   = "rtgGar";
	public static final String DATA_DETALLE_RATING 			   = "detRtg";
	public static final String DATA_CANT_PROY_INMOB			   = "cantProyInmob";
	public static final String DATA_LISTA_MOTIVOS 		 	   = "lstMtv";
	public static final String DATA_LISTA_CLASIFICADORA_RIESGO = "lstRis";
	
	public static final String CLASIF_MODULO_RATING = "VTAOP00202";
	
	// datos en el contexto aplicativo de rating
	public static final String CTX_RUT_CLIENTE 			= "RUT_CLIENTE";
	public static final String CTX_TIPO_RATING 			= "TIPO_RATING";
	public static final String CTX_ID_RATING 			= "ID_RATING";
	public static final String CTX_ID_BANCA 			= "ID_BANCA";
	public static final String CTX_ID_COMPONENTE_RATING = "ID_COMP_RATING";
	public static final String CTX_ESTADO_RTG_INDIVIDUAL= "estadoRtgInd";
	public static final String CTX_ID_RATING_GARANTE 	= "ID_RATING_GARANTE";
	public static final String CTX_ID_VACIADO_ACTUAL_RTG_FINANCIERO = "idVacActRtgFinan";
	public static final String CTX_ID_CLIENTE			= "ID_CLIENTE";
	public static final String CTX_ID_GRUPO				= "ID_GRUPO";
	public static final String CTX_FECHA_EFECTIVA_RTG_GRUPO = "fecEfecRtgGrp";
	
	//constantes permiso
	public static final String CTX_RATING_GRUPAL_VISIBLE         = "rtgGrpVis";
	public static final String CTX_RATING_INDIVIDUAL_VISIBLE     = "rtgIndVis";
	public static final String CTX_RATING_FINANCIERO_VISIBLE     = "rtgFinVis";
	public static final String CTX_RATING_PROYECTADO_VISIBLE     = "rtgProVis";
	public static final String CTX_RATING_NEGOCIO_VISIBLE        = "rtgNegVis";
	public static final String CTX_RATING_COMPORTAMIENTO_VISIBLE = "rtgComVis";
	public static final String CTX_RATING_GARANTE_VISIBLE        = "rtgGarVis";
	
	// Dato para interactuar entre distintos componentes del rating financiero
	// Almacena el id del vaciado seleccionado dentro de la lista de vaciados.
	public static final String CTX_ID_VACIADO_SELECCIONADO_PARA_RTG_FINANCIERO = "idVacSelRtgFinan";
		
	// identificadores de campos
	public static final String KEY 						= "key";
	public static final String VALUE 					= "val";
	public static final String SIN_INFO_CHAR 			= "-";
	public static final String SPACE_CHAR 				= "&nbsp;";
	
	public final static String RATING_FINANCIERO		= "rtgFin";
	public final static String RATING_NEGOCIO			= "rtgNeg";
	public final static String RATING_COMPORTAMIENTO	= "rtgCmp";
	public final static String RATING_PROYECTADO		= "rtgPry";
	public final static String RATING_PONDERADO			= "rtgPnd";
	public final static String RATING_AJUSTADO			= "rtgAjt";
	public final static String RATING_MODELO			= "rtgMod";
	public final static String RATING_GARANTE			= "rtgGte";
	public final static String RATING_GRUPAL			= "rtgGpo";
	public final static String RATING_INDIVIDUAL		= "rtgInd";
	public final static String RATING_EXISTE_NUEVO_RTG_IND = "exNvoRtgInd";
	public final static String RUT_CLIENTE				= "rutCte";
	public final static String RUT_CLI				= "rutCli";
	public final static String CHK_HABILITADO			= "chkHabilitado";
	public final static String FECHA_AVANCE				= "fecAvance";
	public final static String ID_RATING				= "idRtg";
	public final static String REDIRECCION		        = "redir";
	public final static String ID_RATING_COMPORT        = "idRtgComp";
	public final static String TIPO_RATING				= "tpRtg";
	public static final String ID_CLIENTE 				= "idCte";
	public static final String FECHA 					= "fec";
	public static final String RESPONSABLE 				= "resp";
	public static final String TEMPORADA 				= "temporada";	
	public static final String PONDERACION 				= "pond";
	public static final String NOTA 					= "nota";
	public static final String ID_ESTADO 				= "idEst";
	public static final String FECHA_RATING 			= "fecRtg";
	public static final String FECHA_BALANCE 			= "fecBat";
	public static final String PATRIMONIO 				= "patr";
	public static final String PATRIMONIO_UF 			= "patrUF";
	public static final String VENTAS 					= "vtas";
	public static final String VENTAS_UF 				= "vtasUF";
	public static final String COMENTARIO 				= "com";
	public static final String ID_MOTIVO 				= "idMot";
	public static final String COMENTARIO_MODIFICACION 	= "comMot";	
	public static final String COMENTARIO_GENERAL    	= "comGen"; // Requerimiento 7.1.3 Spring 2
	public static final String NOTA_AJUSTADA_POR_CALIDAD_INFO 	= "notaAjt";
	public static final String PESO_RELATIVO_PERIODO 			= "peso";
	public static final String NOTA_AJUSTADA_POR_CALIDAD 		= "ajtCal";
	public static final String NOTA_AJUSTADA_POR_ESCALA 		= "ajtEsc";
	public static final String LOG_OPERADOR 					= "usr";
	public static final String ROL                      		= "rol";
	public static final String ID_BANCA							= "idBca";
	public static final String MODULO 							= "idMod";
	public static final String OPERACION 						= "idOper";
	public static final String USR_PUEDE_GUARDAR_INDIVIDUAL					= "puedeGuardar";
	public static final String USR_PUEDE_CONFIRMAR_INDIVIDUAL				= "puedeConfirmar";
	public static final String USR_PUEDE_CONFIRMAR_INDIVIDUAL_MODIFICADO	= "puedeConfModif";
	public static final String ID_RATING_INDIVIDUAL                         = "idRtgInd";
	public static final String ID_AGRICOLA                         			= "idRtgAgr";
	public static final String ESTADO                                       = "strEst";
	public static final String TIPO_VACIADO                                 = "tpoVac";
	public static final String VACIADO_AJUSTADO                             = "vacAju";
	public static final String PLAN_DE_CUENTAS                              = "planCta";
	public static final String ID_RATING_FINANCIERO                         = "idrtgfin";
	public static final String INFO_RATING_FINANCIERO                       = "infoRtgFin";
	public static final String ALERTAS                                      = "alert";
	public static final String ID_VACIADO                                   = "idVac";
	public static final String TIPO_BALANCE                                 = "tpoBal";
	public static final String ESTADO_VACIADO                               = "estVac";
	public static final String MONENA                                       = "mon";
	public static final String ID_ESTADO_VACIADO                            = "idEstVac";
	public static final String LISTA_VACIADOS                               = "lstVac";
	public static final String PARCIAL_SOPORTADO                            = "parcialSoportado";
	public static final String CLASIFICADORA                                = "clasif";
	public static final String CLASIFICADORAS                               = "lstClasif";
	public static final String CLASIFICACIONES_RIESGO                       = "claRgo";
	public static final String ID_CLASIFICADORA                             = "idCls";
	public static final String NOMBRE_CLASIFICADORA                         = "strCls";
	public static final String DESCRIPCION_CLASIFICACION                    = "desClasif";
	public static final String RATING_BCH			                        = "rtgBch";
	public static final String RATING_RELEVANTE		                        = "rtgRel";
	public static final String ID_RATING_PROYECTADO 						= "idProy";
	public static final String ID_VERSION 									= "idVer";
	public static final String CANTIDAD_PERIODOS 							= "cantPer";
	public static final String ES_RELACIONADO 								= "esRelacionado";
	
	//Clasificaciones de riesgo Rating Garante.
	public static final String EMPRESA_MATRIZ                      			= "empMatriz";
	public static final String CLASIF_1                         			= "clasif1";
	public static final String CLASIFIC_1                         			= "clasific1";
	public static final String CLASIF_2                         			= "clasif2";
	public static final String CLASIFIC_2                         			= "clasific2";	
	public static final String CLASIF_3                         			= "clasif3";
	public static final String CLASIFIC_3                         			= "clasific3";
	
	//reporte ficha rating
	public static final String REP_FR_RUT_CLIENTE 							= "rut";
	public static final String REP_FR_ID_RATING_INDIVIDUAL			 		= "idRatIndividual";
	
	//reporte rating negocio
	public static final String REP_NOMBRE_REPORTE 		= "nomRpt";
	public static final String REP_TIPO_REPORTE 		= "tipo";
	public static final String REP_RUT_CLIENTE 			= "rutCliente";
	public static final String REP_ID_RATING_NEG 		= "idRtgNeg";
	
	public static final String REP_TIPO_PDF				= "0";
	public static final String REP_TIPO_XLS				= "1";
	
	public static final Integer CLASIF_ID_RATING_EN_CURSO  = new Integer("4301");
	public static final Integer CLASIF_ID_RATING_VIGENTE   = new Integer("4302");
	public static final Integer CLASIF_ID_RATING_HISTORICO = new Integer("4303");
	
	public static final String CLASIF_RATING_VIGENTE   = "Vigente";
	public static final String CLASIF_RATING_EN_CURSO  = "En Curso";
	public static final String CLASIF_RATING_HISTORICO = "Histórico";
	
	public static final String CLASIF_TIPO_VACIADO_INDIVIDUAL   = "Individual";
	public static final String CLASIF_TIPO_VACIADO_CONSOLIDADO  = "Consolidado";
	public static final String CLASIF_TIPO_VACIADO_COMBINADO    = "Combinado";
	
	public static final Integer BANCA_CORPORATIVA_GGEE  	= new Integer("4201");
	public static final Integer BANCA_PYME 					= new Integer("4202");
	public static final Integer BANCA_INMOBILIARIA 			= new Integer("4203");
	public static final Integer BANCA_CONSTRUCTORA 			= new Integer("4204");
	public static final Integer BANCA_BANCOS 				= new Integer("4205");
	public static final Integer BANCA_AGRICOLA 				= new Integer("4206");
	public static final Integer BANCA_MULTINACIONAL 		= new Integer("4207");
	public static final Integer BANCA_SOCIEDADES_INVERSION 	= new Integer("4208");
	public static final String TIPO_EVALUACION_RATING_NEGOCIO = "tpoEval";
	
	/**
	 * Rating Garante.
	 */
	public static final Integer CLASIF_RIESGO_STANDARDPOOR  	= new Integer("4801");
	public static final Integer CLASIF_RIESGO_FITCH				= new Integer("4802");
	public static final Integer CLASIF_RIESGO_MOODY	 			= new Integer("4803");
	public final static String CLASIFICADORA_1 					= "clasif1";
	public final static String RIESGO_1 						= "clasific1";
	public final static String CLASIFICADORA_2 					= "clasif2";
	public final static String RIESGO_2 						= "clasific2";
	public final static String CLASIFICADORA_3 					= "clasif3";
	public final static String RIESGO_3 						= "clasific3";
	public final static String RIESGO							= "riesgo";
	
	public static final String FORMULARIO_ESTADO			= "estForm";
	public static final String FORMULARIO_ENABLED			= "enabled";
	public static final String FORMULARIO_DISABLED			= "disabled";
	
	public static final String REPORTE_RATING_NEGOCIO_ID = "rptRatingNegocio";
	
	/*
	 * Detalle Cuentas Consolidado
	 */
	public static final String PARTE_INVOL			 = "nombreParteInvol";
	public static final String PORCEN_PARTE_INVOL_N	 = "porcentPartN";
	public static final String PORCEN_PARTE_INVOL_N1 = "porcentPartN_1";
	public static final String PORCEN_PARTE_INVOL_N2 = "porcentPartN_2";
	
	// Rating proyectado
	public static final String CTX_ID_RATING_PROYECTADO = "idProy";
	public final static String HEADER_PROYECCION = "hdrProy";
	public final static String FLAG_AJUSTADO = "fAjt";
	public final static String LST_MENSAJES = "lstMsg";
	public final static String MENSAJE = "msg";
	public final static String DETALLE_PROYECCION = "detProy";
	public final static String MODO = "modo";
	public final static String TIPO_CAMBIO_ORIGEN = "kOri";
	public final static String UNIDAD_ORIGEN = "uniOri";
	public final static String LST_PERIODOS = "lstPer";
	public final static String PERIODO = "per";
	public final static String MONEDA = "mon";
	public final static String UNIDAD = "unid";
	public final static String UNIDAD_MEDIDA = "unidMed";
	public final static String TIPO_CAMBIO_DESTINO = "kDes";
	public final static String UNIDAD_DESTINO = "uniDes";
	public static final String FECHA_PERIODO = "fecPer";
	public final static String LST_PROYECCIONES = "lstProy";
	public static final String TIPO_CUENTA_INDICADOR = "titTbl";
	public final static String PROYECCION = "proy";
	public final static String ID_CTA = "idCta";
	public final static String TITULO = "tit";
	public final static String DESCRIPCION = "desc";
	public final static String VALOR_1 = "val1";
	public final static String VALOR_2 = "val2";
	public final static String VALOR_3 = "val3";
	public final static String VALOR_P = "valP";
	public final static String FLAG_REQUERIDO = "fReq";
	public final static String FLAG_DEFAULT = "fDef";
	public final static String LST_CUENTAS = "lstCtas";
	public final static String CUENTA = "cta";
	// modos de despliegue de la información de proyeccion
	public final static String MODO_CONSULTA_DEFAULT 		= "VISUALIZAR";
	public final static String MODO_CONSULTA_PROYECTAR 		= "PROYECTAR";
	public final static String MODO_CONSULTA_CONFIRMAR 		= "CONFIRMAR";
	public final static String MODO_CONSULTA_SOLO_LECTURA 	= "LECTURA";

	
	/*
	 * RATING COMPORTAMIENTO 
	 */
	public static final String KEY_RTG_COMP_ENCABEZADO 			= "encabezado";
	public static final String KEY_RTG_COMP_ENCAB_FECHA 		= "encabFecha";
	public static final String KEY_RTG_COMP_ENCAB_DEUDA_BANCO 	= "encabDeudaBanco";
	public static final String KEY_RTG_COMP_ENCAB_DEUDA_SBIF 	= "encabDeudaSBIF";
	public static final String KEY_RTG_COMP_ENCAB_PUNTAJE 		= "encabPuntaje";
	public static final String KEY_RTG_COMP_ENCAB_CIRCULO 		= "encabCirculo";
	public static final String KEY_RTG_COMP_ENCAB_NOTA_RTG 		= "encabNotaRTG";
	public static final String KEY_RTG_COMP_ENCAB_DESCRIPCION 	= "encabDescripcion";
	public static final String KEY_RTG_COMP_PJE_VERDE			= "V";
	public static final String KEY_RTG_COMP_PJE_AMARILLO		= "A";
	public static final String KEY_RTG_COMP_PJE_ROJO			= "R";
	public static final String KEY_RTG_COMP_ES_VIGENTE			= "esVigente";
	public static final String KEY_RTG_COMP_MENSAJE				= "msg";
	public static final String KEY_RTG_INDIV_ESTADO				= "estRtgIndiv";
	public static final String KEY_RTG_INDIV_ESTADO_CURSO		= "C";
	public static final String KEY_RTG_INDIV_ESTADO_VIGENTE		= "V";
	public static final String KEY_RTG_INDIV_ESTADO_HISTORICO	= "H";
	public static final String KEY_RTG_COMP						= "ratingComportamiento";
	public static final String MSG_RTG_COMP_NO_EXISTE_CPMTO 	= "error.no.existe.cpmto.seleccionado";
	
	/*
	 * RATING FINANCIERO
	 */
	public static final String MSG_CONFIRMACION			= "La nota de rating financiero será calculada a partir del vaciado seleccionado \n\n¿Desea continuar?";
	public static final String MSG_CONFIRMACION_SIN_TIER= "Este Banco no cuenta con Tier definido. Desea continuar la generación de rating para esta contraparte?";
	public static final String MSG_CONFIRACION_SUCCESS 	= "Rating Financiero Generado";
	public static final String CTX_RTG_FIN_VACIADOS_LST	= "ctxFinVaciadosLst";
	public static final String CTX_RTG_FIN_ALERTAS		= "ctxFinAlertas";
	public static final String CTX_RTG_FIN_DETALLE		= "ctxFinDetalle";
	public static final String CTX_RTG_FIN_DESHABILITA_REPORTE_DETALLE = "deshabilitarReporteDetalle";
	public static final String ID_ESTADO_RATING       	= "idEstRtg";
	
	public static final String KEY_RTG_FIN_ENCABEZADO_DETALLE 	= "encabezadoDetalle";
	public static final String KEY_RTG_FIN_LISTA_ALERTAS 		= "lstAlertas";
	public static final String KEY_RTG_FIN_LISTA_VACIADOS 	  	= "lstVaciados";
	//1.- ENCABEZADO DETALLE
	public static final String KEY_RTG_FIN_NOTA 				= "rtgFinNota";
	public static final String KEY_RTG_FIN_FECHA_EEFF 			= "rtgFinFecha";
	public static final String KEY_RTG_FIN_PLAN_CUENTA 			= "rtgFinCuenta";
	public static final String KEY_RTG_FIN_TIPO_VACIADO 		= "rtgFinTipoVaciado";
	public static final String KEY_RTG_FIN_AJUSTADO 			= "rtgFinAjustado";
	public static final String KEY_RTG_FIN_RESPONSABLE 			= "rtgFinResponsable";
	public static final String KEY_RTG_FIN_DETALLE 				= "rtgFinDetalle";
	//2.- ENCABEZADO ALERTAS
	public static final String KEY_RTG_FIN_ALERTA_DESCRIPCION 	= "rtgLstAlertaDescripcion";
	//3.- LISTA VACIADOS
	public static final String KEY_RTG_FIN_LST_VAC_DESC_FECHA_EEFF 		= "Fecha EEFF";
	public static final String KEY_RTG_FIN_LST_VAC_DESC_TIPO_VACIADO 	= "Tipo Vaciado";
	public static final String KEY_RTG_FIN_LST_VAC_DESC_AJUSTADO 		= "Ajustado";
	public static final String KEY_RTG_FIN_LST_VAC_DESC_TIPO_BALANCE 	= "Tipo Balance";
	public static final String KEY_RTG_FIN_LST_VAC_DESC_ESTADO 			= "Estado";
	public static final String KEY_RTG_FIN_LST_VAC_DESC_PLAN_CUENTA 	= "Plan de Cuenta";
	public static final String KEY_RTG_FIN_LST_VAC_DESC_MONEDA 			= "Moneda";
	public static final String KEY_RTG_FIN_LST_VAC_DESC_DETALLE 		= "Detalle";
	
	public static final String KEY_RTG_FIN_LST_VAC_FECHA_EEFF 	= "rtgLstVacFechaEEFF";
	public static final String KEY_RTG_FIN_LST_VAC_TIPO_VACIADO = "rtgLstVacTipo";
	public static final String KEY_RTG_FIN_LST_VAC_AJUSTADO 	= "rtgLstVacAjustado";
	public static final String KEY_RTG_FIN_LST_VAC_TIPO_BALANCE = "rtgLstVacTipoBalance";
	public static final String KEY_RTG_FIN_LST_VAC_ESTADO 		= "rtgLstVacEstado";
	public static final String KEY_RTG_FIN_LST_VAC_PLAN_CUENTA 	= "rtgLstVacPlanCuenta";
	public static final String KEY_RTG_FIN_LST_VAC_MONEDA 		= "rtgLstVacMoneda";
	public static final String KEY_RTG_FIN_LST_VAC_DETALLE 		= "rtgLstVacDetalle";	
	
	public static final String RAZON_SOCIAL = "razonSocial";
	public static final String PROBABILIDAD_DEFAULT = "probDefault";
	public static final String ACTIVOS = "activos";
	public static final String LST_RELACIONADOS = "relacionados";
	public static final String ID_PARTE_INVOL = "idParteInvol";
	public static final String CLASIFICACION = "clasificacion";
	public static final String DEUDA_BANCO = "deudaBanco";
	public static final String DEUDA_SBIF = "deudaSBIF";
	public static final String COMPORTAMIENTO_ACTUAL = "compActual";
	public static final String NOMBRE_CLIENTE = "nomCli";
	public static final String PORCENTAJE_PARTICIPACION = "prcPart";
	public static final String FLAG_PARTICIPA = "flgPart";
	public static final String FLAG_APLICA = "flgApl";
	public static final String RELACION = "rel";
	public static final String OPERA_COMO_EMPREA = "opEmp";
	public static final String BANCA_NO_HABILITADA = "bancaNoHab";
	public static final String FLG_RATING_INDIVIDUAL_VIGENTE = "flgRtgIndVig";
	public static final String ID_RELACION = "idRel";
	public static final String COMPORTAMIENTO_ACTUAL_FLG = "flgCircComp";
	public static final String ID_GRUPO = "idGrp";
	public static final String FLAG_ERROR = "flgErr";
	public static final String RUT_EMPRESA = "rutEmpresa";
	public static final String SELECCIONADO = "sel";
	public static final String SEGMENTOS_VENTA = "segVta";
	public static final String ID_SEG = "idSeg";
	public static final String LISTA_SEGMENTOS = "lstSeg";
	public static final String SEGMENTO = "seg";
	public static final String FECHA_NOTIFICACION_SIEBEL = "fecNotSiebel";
	public static final String RATING_MANUAL = "rtgMan";
	public static final String CODIGO_APP = null;
	public static final String FLAG_ACTIVOS_DESDE_VACIADO = "flgActVac";
	public static final String MESES_PER = "mesesPer";
	public static final String MES_PERIODO_BALANCE = "mesPerBal";
	public static final String CODIGO_CUENTA_PROYECCION_MOVIBLE = "P154";
	public static final String CODIGO_CUENTA_PROYECCION_MOVIBLE_X1_PL = "X1_PL154";
	public static final String CODIGO_CUENTA_PROYECCION_MOVIBLE_PL = "PL154";
	public static final String FLAG_TIENE_PARCIAL = "flagParcial";
	public static final String RATING_GARANTE_FECHA = "rtgGteFec";
	public static final String RATING_GARANTE_RESPONSABLE = "rtgGteResp";
	public static final String CABECERA_PERIODO = "CAB_PER_";
	public static final String LISTA_MODELOS = "lstMod";
	public static final String NOMBRE_ARCHIVO_CARGA_INMOBILIARIA = "xlsInmobiliaria.xls";
	public static final String NOMBRE_ARCHIVO_CARGA_CONSTRUCTORA = "xlsConstruccion.xls";
	public static final String NOMBRE_ARCHIVO_CARGA_AGRICOLA = "xlsAgricola.xls";
	public static final String NOMBRE_ARCHIVO_CARGA_PARAMETROS_AGRICOLA = "xlsParametrosAgricola.xls";
	public static final String PARAM_CARGA_DATOS = "Parametros_Cargar_Datos";
	
	// DATOS GENARAL DE RATING
	public static final String FLAG_BORRADO_RATING_CURSO = "FLAG_BORRADO";
	// Sprint 4 7.1.9 y 7.1.12 eliminar balance o cuadro de obras
	public static final String FLAG_BORRADO_BALANCE = "FLAG_BORRADO_BALANCE";
}