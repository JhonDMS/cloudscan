package com.bch.sefe.comun.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface ConstantesClienteSEFE {
	final static Integer VACIADO_AJUSTADO 		= new Integer(1);
	final static Integer VACIADO_SIN_AJUSTAR 	= new Integer(0);
	
	
	final static Integer VACIADO_EN_CURSO 	= new Integer(1401);
	final static Integer VACIADO_VIGENTE 	= new Integer(1402);
	
	final static Integer VACIADO_FLG_ES_DAI		= new Integer(1);
	final static Integer VACIADO_FLG_NO_ES_DAI	= new Integer(0);
	
	public static final String IMG_TICKET_VERDE = "ok.gif";
	
	public static final String NOMBRE_ESTILO_CURSOR_POINTER = "decoratorLinkPointer";
	
	public static final String ID_BANCA = "ID_BANCA";
	public static final String ID_SEGMENTO = "ID_SEGMENTO";
	public static final String NOMBRE_SEGMENTO = "NOMBRE_SEGMENTO";
	public static final String MSG_ALERTA = "msgAlerta";
	public static final String NOTA = "nota";
	public static final String FECHA_RTG_NEGOCIO = "fecRatNeg";
	public static final String RESPONSABLE_RTG_NEGOCIO = "respRatNeg";
	public static final String LOG_OPERADOR = "logOpe";
	
	public static final String TXT_MENSAJE_DE_ALERTA  = "Mensaje de Alerta";	
	public static final String TXT_TIPO_EVALUACION = "Tipo Evaluaci贸n";
	public static final String MSG_ERROR_INESPERADO = "Ha ocurrido un error inesperado";
	
	public static final String RTG_NEG_ID_RATING ="idRating";
	public static final String RTG_NEG_RUT_CLIENTE = "rutCliente";
	public static final String RTG_NEG_ID_MATRIZ = "id_matriz";
	public static final String RTG_NEG_ID_LISTA = "lstAlt";
	public static final String RTG_NEG_ID_TEMA = "idTem";
	public static final String RTG_NEG_ID_PREGUNTA = "idPreg";
	public static final String RTG_NEG_ID_ALTERNATIVA = "id_alt";
	public static final String RTG_NEG_KEY_ID = "id";
	public static final String RTG_NEG_ID_RATING_INDIVIDUAL = "idRatInd";

	// modulos de la aplicacion:
	public static final String MODULO_ADMIN 	= "com.bch.sefe.admin.Admin";
	public static final String MODULO_VACIADO	= "com.bch.sefe.vaciado.Vaciado";
	public static final String MODULO_ANALISIS 	= "com.bch.sefe.analisis.Analisis";
	public static final String MODULO_RATING 	= "com.bch.sefe.rating.Rating";
	public static final String MODULO_BENCHMARK = "com.bch.sefe.benchmark.Benchmark";
	public static final String MODULO_COMUN 	= "com.bch.sefe.comun.Comun";
	public static final String MODULO_SEFE 		= "com.bch.sefe.SEFE";
	public static final String MODULO_FILNET	= "com.bch.sefe.biblioteca.Biblioteca";
	public static final String COD_MODULO_FILNET	= "VTAOP00205";
	
	// mensajes validacion seleccion modelo rating garante
	public static final String MSG1_MODELO_RATING_GARANTE = "Debe seleccionar al menos una Clasificaci贸n de Rating para la Matriz";
	public static final String MSG2_MODELO_RATING_GARANTE = "Se ha modificado el Rating Garante, se debe volver a calcular";
	public static final String MSG3_MODELO_RATING_GARANTE = "Debe seleccionar al menos dos Clasificaciones de Rating para la Matriz";
	public static final String MSG4_MODELO_RATING_GARANTE = "No existe informaci贸n del componente Rating Garante seleccionado";
	
	public static final String RESPUESTA_OK = "0";
	// malla de relaciones no retorna valores
	public static final String RESPONSE_NO_RECORD = "-2";
	
	public static final int ID_CLASIF_MOTIVOS_MODIFICACION_RTG_GRUPAL  = 5100;
	public static final int ID_CLASIF_TIPOS_RELACION_INTEGRANTES_GRUPO = 5000;
	public static final int ID_RELACION_EMPRESA_MADRE = 5005;
	public static final int ID_RELACION_CONYUGE = 5000;
	public static final int ID_RELACION_SELECCIONE = 5006;

	/**
	 * Identificador de Clasificacion para moneda CLP
	 */
	public static final Integer ID_CLASIF_MONEDA_CLP = new Integer(1501);
	public static final String NOM_CLASIF_MONEDA_CLP = "Peso Chileno";
	
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
	public static final Integer ID_CLASIF_MONEDA_UF  = new Integer(1504);
	
	/**
	 * Identificador de Clasificacion para unidad de moneda. UNIDAD (U)
	 */
	public static final Integer ID_CLASIF_UNIDAD  = new Integer(1601);
	
	/**
	 * Identificador de Clasificacion para unidad de moneda. MILES (M)
	 */
	public static final Integer ID_CLASIF_MILES  = new Integer(1602);
	
	/**
	 * Identificador de Clasificacion para unidad de moneda. MILLONES (MM)
	 */
	public static final Integer ID_CLASIF_MILLONES  = new Integer(1603);
	
	/**
	 * Identificador de Clasificacion para unidad de moneda. MILES DE MILLORES (MMM)
	 */
	public static final Integer ID_CLASIF_MILES_DE_MILLONES  = new Integer(1604);
	
	public static final String MSG_ELIMINAR_RELACIONADO = "El Grupo no puede ser modificado";
	
	//public static final String MSG_VALIDACION_PORCENTAJE = "Porcentaje de participaci\u00F3n 0 no es v\u00E1lido"; //sprint 1 req 7.4.12  ;validaci騨 debe considerar participaci騨 de 0% como dato invalido
	public static final String MSG_VALIDACION_PORCENTAJE = "Porcentaje de participaci贸n 0 no es v谩lido"; //sprint 1 req 7.4.12  ;validaci騨 debe considerar participaci騨 de 0% como dato invalido
	
	public static final String MSG_NAVEGADOR_NO_EXISTE_RTG_IND = "No existe Rating Individual";
	public static final String MSG_NAVEGADOR_NO_EXISTE_INFO_COMP_RTG = "No existe informaci贸n del componente de Rating seleccionado";
	public static final String MSG_NO_EXISTE_RTG_CURSO_BORRAR = "No es posible eliminar el Rating, debe seleccionar uno en estado En Curso.";	//sprint 2 req 7.1.4 alinear y borrar rating en curso
	public static final String MSG_RTG_CURSO_RELACIONADO_NO_BORRAR = "No se puede eliminar el Rating Grupal PyME En Curso desde una empresa relacionada. Debe ser eliminado desde la empresa madre.";	//sprint 2 req 7.1.4 alinear y borrar rating en curso
	public static final String MSG_CONFIRMAR_RTG_CURSO_BORRAR = "Rating seleccionado ser谩 eliminado.\n驴Desea continuar?";			//sprint 2 req 7.1.4 alinear y borrar rating en curso
	public static final String MSG_RESPUESTA_RTG_CURSO_BORRAR = "Borrado fue realizado exitosamente";			//sprint 2 req 7.1.4 alinear y borrar rating en curso
	public static final String MSG_RESPUESTA_RTG_CURSO_BORRAR_ERROR = "No se pudo borrar el rating en curso. Int茅ntelo mas tarde.";			//sprint 2 req 7.1.4 alinear y borrar rating en curso
	
	// sprint 4 req: 7.1.9 y 7.1.12 eliminar balances o cuadro de obras
	public static final String MSG_NO_EXISTE_BALANCE_BORRAR = "No es posible eliminar este Balance Inmobiliario ya que ha sido utilizado en la confecci贸n de un Rating Individual";
	public static final String MSG_NO_EXISTE_CUADRO_BORRAR = "No es posible eliminar este Cuadro de Obras ya que ha sido utilizado en la confecci贸n de un Rating Individual";
	public static final String MSG_CONFIRMAR_BALANCE_BORRAR = "驴Est谩 seguro que desea eliminar este registro?";
	public static final String MSG_RESPUESTA_BALANCE_BORRAR = "Operaci贸n realizada correctamente";
	public static final String MSG_RESPUESTA_BALANCE_BORRAR_ERROR = "No se pudo borrar este Balance Inmobiliario. Intentelo mas tarde.";
	public static final String MSG_RESPUESTA_CUADRO_BORRAR_ERROR = "No se pudo borrar este Cuadro de Obras. Int茅ntelo mas tarde.";
	
	// sprint 4 req: 7.4.42-3 eliminar hojas IMD
	public static final String MSG_NO_EXISTE_HOJAS_IMD_BORRAR = "No es posible eliminar hoja IMD, ya que ha sido utilizada en la confecci贸n de un Rating Individual";
	public static final String MSG_CONFIRMAR_HOJAS_IMD_BORRAR = "驴Est谩 seguro que desea eliminar este registro?";
	public static final String MSG_RESPUESTA_HOJAS_IMD_BORRAR = "Operaci贸n realizada correctamente";
	public static final String MSG_RESPUESTA_HOJAS_IMD_BORRAR_ERROR = "No se pudo borrar hoja IMD. Int茅ntelo m谩s tarde.";
	
	public static final Integer ID_CLASIF_ESTADO_RATING_EN_CURSO = new Integer("4301");								//sprint 2 req 7.1.4 alinear y borrar rating en curso
	
	public static final Integer ID_PLAN_CTA_CHGAAP = new Integer(1101);
	
	public static final Integer ID_PLAN_CTA_IFRS_BANCOS = new Integer(1106);
	public static final Integer ID_PLAN_CTA_CORR_BOLSA 	= new Integer(1107);
	public static final Integer ID_PLAN_CTA_SEG_VIDA 	= new Integer(1108);
	public static final Integer ID_PLAN_CTA_SEG_GRALES 	= new Integer(1109);
	
	public static final Long ID_RELACION_EMPRESA_HIJA = new Long(5002L); //sprint 1 req 7.4.12  ;validaci騨 debe considerar participaci騨 de 0% como dato invalido
	public static final Long ID_RELACION_SOCIO = new Long(5004L); //sprint 1 req 7.4.12  ;validaci騨 debe considerar participaci騨 de 0% como dato invalido
	
	public static final Integer ID_NOMBRE_PLAN_CTA_SEG_VIDA   = new Integer(2011);
	public static final Integer ID_NOMBRE_PLAN_CTA_SEG_GRALES = new Integer(2012);
	
	public static final Set PLANES_CUENTA_SIN_FLUJO_CAJA = new HashSet(Arrays.asList(new Integer[] {ID_PLAN_CTA_IFRS_BANCOS, ID_PLAN_CTA_CORR_BOLSA, ID_PLAN_CTA_SEG_VIDA, ID_PLAN_CTA_SEG_GRALES}));
	public static final Set PLANES_CUENTA_SIN_RECONCIL 	= new HashSet(Arrays.asList(new Integer[] {ID_PLAN_CTA_IFRS_BANCOS, ID_PLAN_CTA_CORR_BOLSA, ID_PLAN_CTA_SEG_VIDA, ID_PLAN_CTA_SEG_GRALES}));
	public static final Set PLANES_CUENTA_CON_OTROS_INDICADORES = new HashSet(Arrays.asList(new Integer[] { ID_NOMBRE_PLAN_CTA_SEG_VIDA, ID_NOMBRE_PLAN_CTA_SEG_GRALES }));
	
	//public static final String MSG_VALIDACION_OPERA_COMO_EMPRESA = "El C\u00F3digo de Actividad Econ\u00F3mica de alg\u00FAn Relacionado Empresa es incorrecto"; //sprint 1 req 7.4.4 validacion opera como empresa sobre rut mayores a 50.000.000
	public static final String MSG_VALIDACION_OPERA_COMO_EMPRESA = "El C贸digo de Actividad Econ贸mica de alg煤n Relacionado Empresa es incorrecto"; //sprint 1 req 7.4.4 validacion opera como empresa sobre rut mayores a 50.000.000
	public static final String SI_OPERA_COMO_EMPRESA = "Si"; //sprint 1 req 7.4.4 validacion opera como empresa sobre rut mayores a 50.000.000
	/**
	 * limite del rut para que opere como empresa sprint 1 req 7.4.4 validacion opera como empresa sobre rut mayores a 50.000.000
	 */
	public static final Integer LIMITE_OPE_COMO_EMPRESA  = new Integer(50000000);
	
	// DATOS GENARAL DE RATING
	public final static String ID_RATING = "idRtg";								//sprint 2 req 7.1.4 alinear y borrar rating en curso
	public static final String FLAG_BORRADO_RATING_CURSO = "FLAG_BORRADO";		//sprint 2 req 7.1.4 alinear y borrar rating en curso
	public static final String FLAG_BORRADO_BALANCE = "FLAG_BORRADO_BALANCE";		//sprint 4 req 7.1.9 y 7.1.12 eliminar balance o cuadro de obra
	
	public static final String ID_EST_VIGENTE = "4302";	// sprint 4 req: 7.4.28 motivos de modificaci髇
}
