/**
 * 
 */
package com.bch.sefe.comun.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.grafo.Grafo;
import com.bch.grafo.OrdenTopologico;
import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioCalculo;
import com.bch.sefe.comun.srv.ConsultaServicios;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.GestorClasificaciones;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.MotorCalculo;
import com.bch.sefe.comun.srv.calc.Expresion;
import com.bch.sefe.comun.srv.calc.FunctionMultiArgs;
import com.bch.sefe.comun.srv.calc.Token;
import com.bch.sefe.comun.srv.impl.ConsultaServiciosImplCache;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.srv.impl.GestorClasificacionesImpl;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.srv.impl.MotorCalculo2Impl;
import com.bch.sefe.comun.srv.impl.MotorCalculoImpl;
import com.bch.sefe.comun.srv.impl.ServicioConsultaDeudaODS;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.BalancePlanCta;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.DeudaCliente;
import com.bch.sefe.comun.vo.PeriodoProyeccionLarga;
import com.bch.sefe.comun.vo.ProyeccionLargaFinal;
import com.bch.sefe.comun.vo.TipoCambio;
import com.bch.sefe.proyeccion.GestorProyeccionLarga;
import com.bch.sefe.proyeccion.impl.GestorProyeccionLargaImpl;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.CatalogoVaciados;
import com.bch.sefe.vaciados.ServicioVaciados;
import com.bch.sefe.vaciados.impl.CatalogoVaciadosImpl;
import com.bch.sefe.vaciados.impl.ServicioVaciadosImpl;
import com.bch.sefe.vaciados.srv.GestorPlanCuentas;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorPlanCuentasImpl;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;


/**
 * Implementacion del <b>"Servicio de Calculo"</b>.</br>
 * Provee los metodos para el calculo automatico de cuentas.
 * 
 * @author IGutierrez
 * 
 */
public class ServicioCalculoImpl implements ServicioCalculo {
	
	
    private static final boolean DEFAULT_NUEVA_VERSION_MOTOR = false;
    
    final static Logger log = Logger.getLogger(ServicioCalculoImpl.class);
    final static Logger logMotor = Logger.getLogger("track.motor");
    
    final static int CTA_CALCULADA = 1;
    final static Integer FLAG_VACIADO_AJUSTADO = new Integer(1);
    final static String VACIADO_AJUSTADO = "VACIADO_AJUSTADO";
    final static boolean STOP_ON_ERROR 		= Boolean.valueOf(ConfigManager.getValueAsString(ConstantesSEFE.SERVICIO_CALCULO_STOP_ON_ERROR, Boolean.FALSE.toString())).booleanValue();
    final static boolean UPDATE_ON_ERROR 	= Boolean.valueOf(ConfigManager.getValueAsString(ConstantesSEFE.SERVICIO_CALCULO_UPDATE_ON_ERROR, Boolean.TRUE.toString())).booleanValue();
    
    private String deudaSistema = null;
    
    private Map contextoCalculo = null;
    private Collection cuentasVaciado = null;
    // lista de cuentas que no pertenecen al modelo de cuentas. Ellas son sacadas de Otras formulas
    private Collection listaOtrasCuentas= null;
    // en este contexto se guardan los valores pasados a treves del metodo ponerEnContexto
    private Map contextoAplicativo = null;
    
    private MotorCalculo motor = null;
    private static  ServicioCalculo instancia = null;
    
    /*
     * (sin Javadoc)
     * 
     * @see com.bch.sefe.comun.ServicioCalculo#calcularBalance(java.lang.Long)
     */
    
    
    
    public void calcularBalance(Long idVaciado) {
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de balance para vaciado " + idVaciado);
        }
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
    	this.setCuentasVaciado(cuentas);
        // Se realiza el calculo de los Activos.
        calcularActivos(idVaciado);
        this.setCuentasVaciado(cuentas);
        // Se realiza el calculo de los Pasivos.
        calcularPasivos(idVaciado);
        this.setCuentasVaciado(cuentas);
        // Se realiza el calculo de los EERR.
        calcularEERR(idVaciado);
        this.setCuentasVaciado(cuentas);
       	List clasificaciones = ConfigDBManager.getValuesAsListString(ConstantesSEFE.LISTA_CLASIFICACIONES_CALCULO_OPCIONALES);
       
       	// se calcula cada una de las cuentas configuradas
       	// en esta lista se deberá incluir, por ejemplo, las cuentas de componentes
       	for (int i = 0; i < clasificaciones.size(); ++i) {
       		Integer clasificacion = Integer.valueOf((String) clasificaciones.get(i));
       		this.calcularCuentas(idVaciado, clasificacion);
       		this.setCuentasVaciado(cuentas);
       	}
    }
    
    public String getDeudaSistema() {
		return deudaSistema;
	}

	public void setDeudaSistema(String deudaSistema) {
		this.deudaSistema = deudaSistema;
	}

	public void calcularBalanceCons(Long idVaciado) {
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de balance para vaciado " + idVaciado);
        }
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciadoCons(idVaciado);
    	this.setCuentasVaciado(cuentas);
        // Se realiza el calculo de los Activos.
        calcularActivosCons(idVaciado);
        this.setCuentasVaciado(cuentas);
        // Se realiza el calculo de los Pasivos.
        calcularPasivosCons(idVaciado);
        this.setCuentasVaciado(cuentas);
        // Se realiza el calculo de los EERR.
        calcularEERRCons(idVaciado);
        this.setCuentasVaciado(cuentas);
       	List clasificaciones = ConfigDBManager.getValuesAsListString(ConstantesSEFE.LISTA_CLASIFICACIONES_CALCULO_OPCIONALES);
       
       	// se calcula cada una de las cuentas configuradas
       	// en esta lista se deberá incluir, por ejemplo, las cuentas de componentes
       	for (int i = 0; i < clasificaciones.size(); ++i) {
       		Integer clasificacion = Integer.valueOf((String) clasificaciones.get(i));
       		this.calcularCuentasCons(idVaciado, clasificacion);
       		this.setCuentasVaciado(cuentas);
       	}
    }
    
    /*
     * (sin Javadoc)
     * 
     * @see com.bch.sefe.comun.ServicioCalculo#calcularActivos(java.lang.Long)
     */
    public void calcularActivos(Long idVaciado) {
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de activos");
        }
    	
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_ACTIVOS));
	}
    
    public void calcularActivosCons(Long idVaciado) {
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de activos");
        }
    	
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_ACTIVOS));
	}
    
    /*
     * (sin Javadoc)
     * 
     * @see com.bch.sefe.comun.ServicioCalculo#calcularPasivos(java.lang.Long)
     */
	public void calcularPasivos(Long idVaciado) {
		if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de pasivos");
        }
		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
    	this.setCuentasVaciado(cuentas);
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_CONTINGENTE));
        this.setCuentasVaciado(cuentas);
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_INTERES));
	}
	
	public void calcularPasivosCons(Long idVaciado) {
		if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de pasivos");
        }
		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciadoCons(idVaciado);
    	this.setCuentasVaciado(cuentas);
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_CONTINGENTE));
        this.setCuentasVaciado(cuentas);
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_INTERES));
	}
    
    /*
     * (sin Javadoc)
     * 
     * @see com.bch.sefe.comun.ServicioCalculo#calcularCorrecionMonetaria(java.lang .Long)
     * 
     * La Reconciliación sólo se puede calcular siempre que esté ingresado el periodo anterior, 
     * en el caso de ingresar por primera vez el vaciado, no se puede calcular la Reconciliación.  
     * Esta misma consideración aplica para el Flujo de Caja.
     * 
     * El Periodo Anterior (n-1) se necesita para el cálculo de los deltas (todos), 
     * que sólo pueden ser calculados si se dispone del periodo n-1 completo (12 meses) 
     * y el periodo n pueda ser parcial o completo, pero consecutivo.
     */
    public void calcularCorrecionMonetaria(Long idVaciado) {
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Correccion Monetaria para vaciado " + idVaciado);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Correccion Monetaria.");
        }
        
        this.calcularCuentas(idVaciado, new Integer(0));
        this.calcularReconciliacion(idVaciado);
        Long idVacPrev = null;
        
        // Se respalda el ID_VAC_PREV
        if (contextoCalculo.get(ID_VAC_PREV) != null) {
        	idVacPrev = (Long) contextoCalculo.get(ID_VAC_PREV);
        }        
        
        try { 
        	
        	// Se obtiene del contexto el valor existente para el ID_VAC_PREV_ORIG
        	Long idVacPrevOrig = null;
        	if (contextoCalculo.get(ID_VAC_PREV_ORIG) != null) {        		
            	idVacPrevOrig = (Long) contextoCalculo.get(ID_VAC_PREV_ORIG);            	
        	}
        	
        	// Se reemplaza el ID_VAC_PREV con el valor de ID_VAC_PREV_ORIG
        	contextoCalculo.put(ID_VAC_PREV, idVacPrevOrig);
        
        
	        CatalogoVaciados catalogo = new CatalogoVaciadosImpl();
	        Vaciado vac = catalogo.buscarDatosGeneral(idVaciado);
	
	        BalancePlanCta bal = gestor.obtenerRelBlcePlanCtas(vac.getIdTipoBalance(), vac.getIdNombrePlanCtas());
	        
	        // solo se calculan cuantas de corr monetaria para CHGAAP
	        if (ConstantesSEFE.CLASIF_ID_TPO_PLAN_CHGAAP.equals(bal.getIdTpoPlanCta())) {
	            this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_CORRECCION_MONETARIA));
	            this.setCuentasVaciado(cuentas);
	        }
	        
        } finally {
        	// Se reestablece el valor del ID_VAC_PREV al inicial
        	contextoCalculo.put(ID_VAC_PREV, idVacPrev);
        }
    	this.calcularReconciliacion(idVaciado);
    	this.setCuentasVaciado(cuentas);
        this.calcularFlujoCaja(idVaciado);
        this.setCuentasVaciado(cuentas);
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_COMPONENTES));
        this.setCuentasVaciado(cuentas);
        this.calcularIndicadores(idVaciado);
        this.setCuentasVaciado(cuentas);
	        
    	    
    }
    
    public void calcularCorrecionMonetariaCons(Long idVaciado) {
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciadoCons(idVaciado);
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Correccion Monetaria para vaciado " + idVaciado);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Correccion Monetaria.");
        }
        
        this.calcularCuentasCons(idVaciado, new Integer(0));
        this.calcularReconciliacionCons(idVaciado);
        Long idVacPrev = null;
        
        // Se respalda el ID_VAC_PREV
        if (contextoCalculo.get(ID_VAC_PREV) != null) {
        	idVacPrev = (Long) contextoCalculo.get(ID_VAC_PREV);
        }        
        
        try { 
        	
        	// Se obtiene del contexto el valor existente para el ID_VAC_PREV_ORIG
        	Long idVacPrevOrig = null;
        	if (contextoCalculo.get(ID_VAC_PREV_ORIG) != null) {        		
            	idVacPrevOrig = (Long) contextoCalculo.get(ID_VAC_PREV_ORIG);            	
        	}
        	
        	// Se reemplaza el ID_VAC_PREV con el valor de ID_VAC_PREV_ORIG
        	contextoCalculo.put(ID_VAC_PREV, idVacPrevOrig);
        
        
	        CatalogoVaciados catalogo = new CatalogoVaciadosImpl();
	        Vaciado vac = catalogo.buscarDatosGeneral(idVaciado);
	
	        BalancePlanCta bal = gestor.obtenerRelBlcePlanCtas(vac.getIdTipoBalance(), vac.getIdNombrePlanCtas());
	        
	        // solo se calculan cuantas de corr monetaria para CHGAAP
	        if (ConstantesSEFE.CLASIF_ID_TPO_PLAN_CHGAAP.equals(bal.getIdTpoPlanCta())) {
	            this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_CORRECCION_MONETARIA));
	            this.setCuentasVaciado(cuentas);
	        }
	        
        } finally {
        	// Se reestablece el valor del ID_VAC_PREV al inicial
        	contextoCalculo.put(ID_VAC_PREV, idVacPrev);
        }
    	this.calcularReconciliacionCons(idVaciado);
    	this.setCuentasVaciado(cuentas);
        this.calcularFlujoCajaCons(idVaciado);
        this.setCuentasVaciado(cuentas);
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_COMPONENTES));
        this.setCuentasVaciado(cuentas);
        this.calcularIndicadoresCons(idVaciado);
        this.setCuentasVaciado(cuentas);
	        
    	    
    }
    
    
    /*
     * (sin Javadoc)
     * 
     * @see com.bch.sefe.comun.ServicioCalculo#calcularEERR(java.lang.Long)
     */
    public void calcularEERR(Long idVaciado) {
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de EERR para vaciado " + idVaciado);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de EERR");
        }
        this.setCuentasVaciado(cuentas);
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_EERR));
        this.setCuentasVaciado(cuentas);
        //this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_COMPONENTES));
        this.calcularCorrecionMonetaria(idVaciado);
        this.setCuentasVaciado(cuentas);
		this.calcularCuentasAuxiliares(idVaciado);
		this.setCuentasVaciado(cuentas);
		this.calcularCuentasControl(idVaciado);
		this.setCuentasVaciado(cuentas);
	//	this.calcularIndicesAlertas(idVaciado);
    }
    
    public void calcularEERRCons(Long idVaciado) {
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciadoCons(idVaciado);
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de EERR para vaciado " + idVaciado);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de EERR");
        }
        this.setCuentasVaciado(cuentas);
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_EERR));
        this.setCuentasVaciado(cuentas);
        //this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_COMPONENTES));
        this.calcularCorrecionMonetariaCons(idVaciado);
        this.setCuentasVaciado(cuentas);
		this.calcularCuentasAuxiliaresCons(idVaciado);
		this.setCuentasVaciado(cuentas);
		this.calcularCuentasControlCons(idVaciado);
		this.setCuentasVaciado(cuentas);
	//	this.calcularIndicesAlertas(idVaciado);
    }
    
 
    /*
     * (sin Javadoc)
     * 
     * @see com.bch.sefe.comun.ServicioCalculo#calcularFlujoCaja(java.lang.Long)
     */
    public void calcularFlujoCaja(Long idVaciado) {
    	
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Flujo de Caja para vaciado " + idVaciado);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Flujo de Caja");
        }
        
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_FLUJO_CAJA));
        
    }
    
    public void calcularFlujoCajaCons(Long idVaciado) {
    	
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Flujo de Caja para vaciado " + idVaciado);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Flujo de Caja");
        }
        
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_FLUJO_CAJA));
        
    }

    
    /*
     * (sin Javadoc)
     * 
     * @see com.bch.sefe.comun.ServicioCalculo#calcularIndicadores(java.lang.Long)
     */
    public void calcularIndicadores(Long idVaciado) {
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Indicadores para vaciado " + idVaciado);
        }
  
    	this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_INDICADORES));
    }
    
    public void calcularIndicadoresCons(Long idVaciado) {
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Indicadores para vaciado " + idVaciado);
        }
  
    	this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_INDICADORES));
    }
    
    
    public void calcularCuentasPorTipo(Long idVaciado, Integer tipoIndicador) {
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de cuentas por tipo para vaciado " + idVaciado);
        }
    	
    	this.calcularCuentas(idVaciado, tipoIndicador);
    }
    
    public void calcularCuentasPorTipoCons(Long idVaciado, Integer tipoIndicador) {
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de cuentas por tipo para vaciado " + idVaciado);
        }
    	
    	this.calcularCuentasCons(idVaciado, tipoIndicador);
    }
    

    public void calcularCuentasRezagadas(List listIdVaciados) {
    	if (listIdVaciados != null) {
    		for (int i=0; i< listIdVaciados.size(); i++) {
    			Long idVaciado = (Long)listIdVaciados.get(i);
    			if (idVaciado != null) {
	    			calcularCuentasRezagadas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_COMPONENTES));
	    			this.setCuentasVaciado(null);
	    			this.setContextoCalculo(null);
    			}
    		}
    	}
    	
    }
    
    public void calcularCuentasRezagadasCons(List listIdVaciados) {
    	if (listIdVaciados != null) {
    		for (int i=0; i< listIdVaciados.size(); i++) {
    			Long idVaciado = (Long)listIdVaciados.get(i);
    			if (idVaciado != null) {
	    			calcularCuentasRezagadasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_COMPONENTES));
	    			this.setCuentasVaciado(null);
	    			this.setContextoCalculo(null);
    			}
    		}
    	}
    	
    }
  
    
	/*
     * (sin Javadoc)
     * 
     * @see com.bch.sefe.comun.ServicioCalculo#calcularReconciliacion(java.lang.Long )
     */
    public void calcularReconciliacion(Long idVaciado) {
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
    	this.setCuentasVaciado(cuentas);
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Reconciliacion para vaciado " + idVaciado);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Reconciliacion de Patrimonio.");
        }
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_REC_PATRIMONIO));
        this.setCuentasVaciado(cuentas);
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Reconciliacion de Activo Fijo.");
        }
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_REC_ACTIVO_FIJO));
        this.setCuentasVaciado(cuentas);
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Reconciliacion de Inversiones de Sociedad.");
        }
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_REC_INVERSIONES_SOCIEDAD));
        this.setCuentasVaciado(cuentas);
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Reconciliacion de Intangibles.");
        }
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_REC_INTANGIBLES));
        this.setCuentasVaciado(cuentas);
        GestorVaciados gestorVaciados = new GestorVaciadosImpl();
        Vaciado vaciado = gestorVaciados.buscarVaciado(idVaciado);
        Integer idTpoPlan = vaciado.getIdTipoPlan();
        
        if (!ConstantesSEFE.CLASIF_ID_TPO_PLAN_CHGAAP.equals(idTpoPlan)) {
        	if (log.isDebugEnabled()) {
                log.debug("Iniciando el calculo de Reconciliacion de Estado de Resultados Integrales.");
            }
            this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_REC_EERR_INTEGRALES));
            this.setCuentasVaciado(cuentas);
        }
    }
    
    public void calcularReconciliacionCons(Long idVaciado) {
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciadoCons(idVaciado);
    	this.setCuentasVaciado(cuentas);
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Reconciliacion para vaciado " + idVaciado);
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Reconciliacion de Patrimonio.");
        }
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_REC_PATRIMONIO));
        this.setCuentasVaciado(cuentas);
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Reconciliacion de Activo Fijo.");
        }
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_REC_ACTIVO_FIJO));
        this.setCuentasVaciado(cuentas);
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Reconciliacion de Inversiones de Sociedad.");
        }
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_REC_INVERSIONES_SOCIEDAD));
        this.setCuentasVaciado(cuentas);
        if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Reconciliacion de Intangibles.");
        }
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_REC_INTANGIBLES));
        this.setCuentasVaciado(cuentas);
        GestorVaciados gestorVaciados = new GestorVaciadosImpl();
        Vaciado vaciado = gestorVaciados.buscarVaciado(idVaciado);
        Integer idTpoPlan = vaciado.getIdTipoPlan();
        
        if (!ConstantesSEFE.CLASIF_ID_TPO_PLAN_CHGAAP.equals(idTpoPlan)) {
        	if (log.isDebugEnabled()) {
                log.debug("Iniciando el calculo de Reconciliacion de Estado de Resultados Integrales.");
            }
            this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_REC_EERR_INTEGRALES));
            this.setCuentasVaciado(cuentas);
        }
    }
    
    public void calcularCuentasControl(Long idVaciado) {
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de cuentas de control");
        }
        this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_CONTROL));
		
	}
    
    public void calcularCuentasControlCons(Long idVaciado) {
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de cuentas de control");
        }
        this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_CONTROL));
		
	}
    
    public void calcularIndicesAlertas(Long idVaciado) {
		if (log.isDebugEnabled())
		{
			log.debug("Iniciando el calculo de cuentas de indices y alertas");
		}
		this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_INDICES_ALERTAS));
    }
    
    public void calcularIndicesAlertasCons(Long idVaciado) {
		if (log.isDebugEnabled())
		{
			log.debug("Iniciando el calculo de cuentas de indices y alertas");
		}
		this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_INDICES_ALERTAS));
    }
    
    public Cuenta calcularCuentaIndependiente(Long idVaciado, Cuenta cta) {
    	String strKey = "";
    	calcularCuentasIndependientes(idVaciado, Boolean.TRUE);
    	// si idVaciado es null significa que no se esta utilizando vaciado
    	if (idVaciado != null) {
    		
    		strKey = idVaciado.toString() + ConstantesSEFE.UNDERLINE;
    	}
    	
		BigDecimal valor = null;		
         try {
        	//se obtienen los parametros que se necesitan pasar por ctx a la formula  
        	 valor = getMotor().calcularCuenta(cta);
        	 if(valor == null) {
        		 cta.setMonto(null);
        	 } else {
        		 cta.setMonto(new Double(valor.doubleValue()));
        	 }         	
        	
			// se actualiza el valor de la cuenta calculada en el contexto
			this.getContextoCalculo().put(strKey+cta.getCodigoCuenta(), cta);
         } catch (RuntimeException ex) {
        		log.info("Error al calcular cuenta " +  cta.getCodigoCuenta() + " causado por " + ex.getMessage());
         }
	    return cta;
	}
    
    public Cuenta calcularCuentaIndependienteCons(Long idVaciado, Cuenta cta) {
    	String strKey = "";
    	calcularCuentasIndependientesCons(idVaciado, Boolean.TRUE);
    	// si idVaciado es null significa que no se esta utilizando vaciado
    	if (idVaciado != null) {
    		
    		strKey = idVaciado.toString() + ConstantesSEFE.UNDERLINE;
    	}
    	
		BigDecimal valor = null;		
         try {
        	//se obtienen los parametros que se necesitan pasar por ctx a la formula  
        	 valor = getMotor().calcularCuenta(cta);
        	 if(valor == null) {
        		 cta.setMonto(null);
        	 } else {
        		 cta.setMonto(new Double(valor.doubleValue()));
        	 }         	
        	
			// se actualiza el valor de la cuenta calculada en el contexto
			this.getContextoCalculo().put(strKey+cta.getCodigoCuenta(), cta);
         } catch (RuntimeException ex) {
        		log.info("Error al calcular cuenta " +  cta.getCodigoCuenta() + " causado por " + ex.getMessage());
         }
	    return cta;
	}
    

    public void calcularCuentasVaciado(Long idVaciado) {
		
		if (log.isDebugEnabled()) {
			String msg = MessageFormat.format("Realizando Calculo Automatico de Cuentas para el Vaciado #{0}", new String[] {idVaciado.toString()});
			log.debug(msg);
        }
		
		// Se realiza el llamado al calculo de todos los tipos de cuentas para el vaciado en cuestion.
		calcularBalance(idVaciado);
	}
    
    public void calcularCuentasVaciadoCons(Long idVaciado) {
		
		if (log.isDebugEnabled()) {
			String msg = MessageFormat.format("Realizando Calculo Automatico de Cuentas para el Vaciado #{0}", new String[] {idVaciado.toString()});
			log.debug(msg);
        }
		
		// Se realiza el llamado al calculo de todos los tipos de cuentas para el vaciado en cuestion.
		calcularBalanceCons(idVaciado);
	}

	public void calculoParcialCuentasVaciado(Long idVaciado) {
		if (log.isDebugEnabled()) {
			String msg = MessageFormat.format("Realizando Calculo de Cuentas, que no requieren vaciado anterior, para el Vaciado #{0}", new String[] {idVaciado.toString()});
			log.debug(msg);
        }
		
		// Se realiza el llamado al calculo de todos los tipos de cuentas, que no requieren vaciado anterior, para el vaciado en cuestion.
		calcularBalance(idVaciado);
		calcularCuentasAuxiliares(idVaciado);
	}
	
	public void calculoParcialCuentasVaciadoCons(Long idVaciado) {
		if (log.isDebugEnabled()) {
			String msg = MessageFormat.format("Realizando Calculo de Cuentas, que no requieren vaciado anterior, para el Vaciado #{0}", new String[] {idVaciado.toString()});
			log.debug(msg);
        }
		
		// Se realiza el llamado al calculo de todos los tipos de cuentas, que no requieren vaciado anterior, para el vaciado en cuestion.
		calcularBalanceCons(idVaciado);
		calcularCuentasAuxiliaresCons(idVaciado);
	}

	public void calcularCuentasAuxiliares(Long idVaciado) {
		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
    	this.setCuentasVaciado(cuentas);
		if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Cuentas Auxiliares para vaciado " + idVaciado);
        }
		
		calcularCuentasVeMac(idVaciado);
		this.setCuentasVaciado(cuentas);
		calcularCuentasFichaFinanciera(idVaciado);
		this.setCuentasVaciado(cuentas);
		
	}
	
	public void calcularCuentasAuxiliaresCons(Long idVaciado) {
		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	Collection cuentas = gestor.buscarCuentasVaciadoCons(idVaciado);
    	this.setCuentasVaciado(cuentas);
		if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Cuentas Auxiliares para vaciado " + idVaciado);
        }
		
		calcularCuentasVeMacCons(idVaciado);
		this.setCuentasVaciado(cuentas);
		calcularCuentasFichaFinancieraCons(idVaciado);
		this.setCuentasVaciado(cuentas);
		
	}

	public void calcularCuentasFichaFinanciera(Long idVaciado) {
		if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Cuentas Auxiliares de Ficha Chica para vaciado " + idVaciado);
        }        
		this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_AUX_FICHA_FINANCIERA));
	}
	
	public void calcularCuentasFichaFinancieraCons(Long idVaciado) {
		if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Cuentas Auxiliares de Ficha Chica para vaciado " + idVaciado);
        }        
		this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_AUX_FICHA_FINANCIERA));
	}

	public void calcularCuentasVeMac(Long idVaciado) {
		if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Cuentas Auxiliares de Mapeo VE-MAC para vaciado " + idVaciado);
        }
        
		this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_MAPEO_VE_MAC));
    }		
	
	public void calcularCuentasVeMacCons(Long idVaciado) {
		if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de Cuentas Auxiliares de Mapeo VE-MAC para vaciado " + idVaciado);
        }
        
		this.calcularCuentasCons(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_MAPEO_VE_MAC));
    }		
	
	public void calcularCuentasHibridas(Long idVaciado) {
		this.calcularCuentasHibridas(idVaciado,  ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_COMPONENTES), ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_HIBRIDA));
	}

 
    
    /**
     * Metodo que realiza el calculo de las cuentas calculables de un tipo especifico, para el vaciado identificados.
     * 
     * @param idVaciado - Identificador del Vaciado.
     * @param tipoCuenta - Identificador del tipo de cuenta a ser calculada.
     */
    private void calcularCuentas(Long idVaciado, Integer tipoCuenta) { 		
    	// buscar cuentas del balance
     	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	if (this.getCuentasVaciado() == null) {
    		Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
    		this.setCuentasVaciado(cuentas);
    	}
        
        // crear el contexto de calculo y lo pasa al motor de calculo
         if (this.getContextoCalculo() == null) {
        	Map ctx = this.crearContextoCalculo(idVaciado, this.getCuentasVaciado());
        	this.setContextoCalculo(ctx);
        }
        
        // se valida si existe contexto aplicativo...
        if (this.contextoAplicativo != null && !this.contextoAplicativo.isEmpty()) {
        	this.getContextoCalculo().putAll(this.contextoAplicativo);
        	
        	// se limpia el contexto aplicativo
        	this.contextoAplicativo.clear();
        	this.contextoAplicativo = null;
        }
         // Si el tipo de cuenta se encuentra configurado, para que se realice un orden previo al calculo
       if (ConstantesSEFE.TIPOS_CUENTA_REQUIEREN_ORDEN_CALCULO.contains(tipoCuenta.toString())) {
        	Map mapaCuentas = this.crearMapaCuentas(this.getCuentasVaciado());        
        	this.procesarCuentas(idVaciado, tipoCuenta, mapaCuentas, false, null);
        } else {
        	this.calcularCuentasServicio(idVaciado, tipoCuenta, false,  null);
        }
        // fin del proceso de calculo
        return;
    }
    
    private void calcularCuentasCons(Long idVaciado, Integer tipoCuenta) { 		
    	// buscar cuentas del balance
     	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	if (this.getCuentasVaciado() == null) {
    		Collection cuentas = gestor.buscarCuentasVaciadoCons(idVaciado);
    		this.setCuentasVaciado(cuentas);
    	}
        
        // crear el contexto de calculo y lo pasa al motor de calculo
         if (this.getContextoCalculo() == null) {
        	Map ctx = this.crearContextoCalculoCons(idVaciado, this.getCuentasVaciado());
        	this.setContextoCalculo(ctx);
        }
         
        // se valida si existe contexto aplicativo...
        if (this.contextoAplicativo != null && !this.contextoAplicativo.isEmpty()) {
        	this.getContextoCalculo().putAll(this.contextoAplicativo);
        	
        	// se limpia el contexto aplicativo
        	this.contextoAplicativo.clear();
        	this.contextoAplicativo = null;
        }
         // Si el tipo de cuenta se encuentra configurado, para que se realice un orden previo al calculo
       if (ConstantesSEFE.TIPOS_CUENTA_REQUIEREN_ORDEN_CALCULO.contains(tipoCuenta.toString())) {
        	Map mapaCuentas = this.crearMapaCuentas(this.getCuentasVaciado());        
        	this.procesarCuentas(idVaciado, tipoCuenta, mapaCuentas, false, null);
        } else {
        	this.calcularCuentasServicioCons(idVaciado, tipoCuenta, false,  null);
        }
        // fin del proceso de calculo
        return;
    }
    
    private void calcularCuentasHibridas(Long idVaciado, Integer tipoCuenta, Integer idHibrido) { 		
    	// buscar cuentas del balance
     	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
     	this.setCuentasVaciado(null);
    	if (this.getCuentasVaciado() == null) {
    		Collection cuentas = gestor.buscarCuentasHibridasVaciado(idVaciado);
    		this.setCuentasVaciado(cuentas);
    	}
    	this.setContextoCalculo(null);
        // crear el contexto de calculo y lo pasa al motor de calculo
         if (this.getContextoCalculo() == null) {
        	Map ctx = this.crearContextoCalculo(idVaciado, this.getCuentasVaciado());
        	this.setContextoCalculo(ctx);
        }
        
        // se valida si existe contexto aplicativo...
        if (this.contextoAplicativo != null && !this.contextoAplicativo.isEmpty()) {
        	this.getContextoCalculo().putAll(this.contextoAplicativo);
        	
        	// se limpia el contexto aplicativo
        	this.contextoAplicativo.clear();
        	this.contextoAplicativo = null;
        }
         // Si el tipo de cuenta se encuentra configurado, para que se realice un orden previo al calculo
       if (ConstantesSEFE.TIPOS_CUENTA_REQUIEREN_ORDEN_CALCULO.contains(tipoCuenta.toString())) {
        	Map mapaCuentas = this.crearMapaCuentas(this.getCuentasVaciado());        
        	this.procesarCuentas(idVaciado, tipoCuenta, mapaCuentas, false, idHibrido);
        } else {
        	this.calcularCuentasServicio(idVaciado, tipoCuenta, false,idHibrido);
        }
        // fin del proceso de calculo
        return;
    }
    
    
    
    
    private void calcularCuentasRezagadas(Long idVaciado, Integer tipoCuenta) { 		
    	// buscar cuentas del balance
     	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	if (this.getCuentasVaciado() == null) {
    		Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
    		this.setCuentasVaciado(cuentas);
    	}
        
        // crear el contexto de calculo y lo pasa al motor de calculo
         if (this.getContextoCalculo() == null) {
        	Map ctx = this.crearContextoCalculo(idVaciado, this.getCuentasVaciado());
        	this.setContextoCalculo(ctx);
        }
        
        // se valida si existe contexto aplicativo...
        if (this.contextoAplicativo != null && !this.contextoAplicativo.isEmpty()) {
        	this.getContextoCalculo().putAll(this.contextoAplicativo);
        	
        	// se limpia el contexto aplicativo
        	this.contextoAplicativo.clear();
        	this.contextoAplicativo = null;
        }
         // Si el tipo de cuenta se encuentra configurado, para que se realice un orden previo al calculo
       if (ConstantesSEFE.TIPOS_CUENTA_REQUIEREN_ORDEN_CALCULO.contains(tipoCuenta.toString())) {
        	Map mapaCuentas = this.crearMapaCuentas(this.getCuentasVaciado());        
        	this.procesarCuentas(idVaciado, tipoCuenta, mapaCuentas, true,null);
        } else {
        	this.calcularCuentasServicio(idVaciado, tipoCuenta,true,null);
        }
        // fin del proceso de calculo
        return;
    }
    
    private void calcularCuentasRezagadasCons(Long idVaciado, Integer tipoCuenta) { 		
    	// buscar cuentas del balance
     	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	if (this.getCuentasVaciado() == null) {
    		Collection cuentas = gestor.buscarCuentasVaciadoCons(idVaciado);
    		this.setCuentasVaciado(cuentas);
    	}
        
        // crear el contexto de calculo y lo pasa al motor de calculo
         if (this.getContextoCalculo() == null) {
        	Map ctx = this.crearContextoCalculo(idVaciado, this.getCuentasVaciado());
        	this.setContextoCalculo(ctx);
        }
        
        // se valida si existe contexto aplicativo...
        if (this.contextoAplicativo != null && !this.contextoAplicativo.isEmpty()) {
        	this.getContextoCalculo().putAll(this.contextoAplicativo);
        	
        	// se limpia el contexto aplicativo
        	this.contextoAplicativo.clear();
        	this.contextoAplicativo = null;
        }
         // Si el tipo de cuenta se encuentra configurado, para que se realice un orden previo al calculo
       if (ConstantesSEFE.TIPOS_CUENTA_REQUIEREN_ORDEN_CALCULO.contains(tipoCuenta.toString())) {
        	Map mapaCuentas = this.crearMapaCuentas(this.getCuentasVaciado());        
        	this.procesarCuentas(idVaciado, tipoCuenta, mapaCuentas, true,null);
        } else {
        	this.calcularCuentasServicioCons(idVaciado, tipoCuenta,true,null);
        }
        // fin del proceso de calculo
        return;
    }
    

    /**
     * Metodo que realiza el calculo de las cuentas calculables de un tipo especifico, para el vaciado identificados.
     * 
     * @param idVaciado - Identificador del Vaciado.
     *          si el idVaciado es nulo entonces se calcula las formulas en forma individual.
     * @param calcUnicaFormula 
     *      - True : calcular una unica formula
     *      - Flase: calcular una lista de formulas
     */
    private void calcularCuentasIndependientes(Long idVaciado, Boolean calcUnicaFormula ) { 		

    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	if (this.getCuentasVaciado() == null && idVaciado != null) {
    		Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
    		this.setCuentasVaciado(cuentas);
    	}
    	Map ctx = new HashMap();
        if (this.getContextoCalculo() == null && idVaciado != null) {
        	ctx = this.crearContextoCalculo(idVaciado, this.getCuentasVaciado());
        	this.setContextoCalculo(ctx);
        	getMotor().inicializarContexto(this.getContextoCalculo());	
        }        
        // se valida si existe contexto aplicativo...
        if (this.contextoAplicativo != null && !this.contextoAplicativo.isEmpty()) {
        	if (this.getContextoCalculo()== null) {
        		this.setContextoCalculo(ctx);
        		getMotor().inicializarContexto(this.getContextoCalculo());	
        	}
        	this.getContextoCalculo().putAll(this.contextoAplicativo);
        	
        	// se limpia el contexto aplicativo
        	this.contextoAplicativo.clear();
        	this.contextoAplicativo = null;
        }
        // se comienza a calcular las otras formulas
        if (!calcUnicaFormula.booleanValue() && idVaciado != null) {
        	calcularOtrasCuentasServicio(idVaciado); 
        	
        }
	
    }
    
    private void calcularCuentasIndependientesCons(Long idVaciado, Boolean calcUnicaFormula ) { 		

    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	if (this.getCuentasVaciado() == null && idVaciado != null) {
    		Collection cuentas = gestor.buscarCuentasVaciadoCons(idVaciado);
    		this.setCuentasVaciado(cuentas);
    	}
    	Map ctx = new HashMap();
        if (this.getContextoCalculo() == null && idVaciado != null) {
        	ctx = this.crearContextoCalculo(idVaciado, this.getCuentasVaciado());
        	this.setContextoCalculo(ctx);
        	getMotor().inicializarContexto(this.getContextoCalculo());	
        }        
        // se valida si existe contexto aplicativo...
        if (this.contextoAplicativo != null && !this.contextoAplicativo.isEmpty()) {
        	if (this.getContextoCalculo()== null) {
        		this.setContextoCalculo(ctx);
        		getMotor().inicializarContexto(this.getContextoCalculo());	
        	}
        	this.getContextoCalculo().putAll(this.contextoAplicativo);
        	
        	// se limpia el contexto aplicativo
        	this.contextoAplicativo.clear();
        	this.contextoAplicativo = null;
        }
        // se comienza a calcular las otras formulas
        if (!calcUnicaFormula.booleanValue() && idVaciado != null) {
        	calcularOtrasCuentasServicio(idVaciado); 
        	
        }
	
    }
    
    
    
	/**
     * Metodo que realiza el calculo de las cuentas calculables de un tipo especifico, para el vaciado identificados.
     * 
     * @param idVaciado - Identificador del Vaciado.
     * @param tipoCuenta - Identificador del tipo de cuenta a ser calculada.
     * @param esRezagada - para calcular todas las COM rezagadas 
     */
    private void calcularCuentasServicio(Long idVaciado, Integer tipoCuenta, boolean esRezagado, Integer hibrido) {
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();   	
        getMotor().inicializarContexto(this.getContextoCalculo());
        Integer idComponentes = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_COMPONENTES);
        // se calcula cada uno de las cuentas del balance
        Iterator iterator = this.getCuentasVaciado().iterator();
        boolean errorEnCalculoCuenta = false;
        Long rangoCta=null; 
        if (esRezagado ) {
    		rangoCta = this.obtenerRangoCtasCalcular();
    	}
        while (iterator.hasNext()) {
        	

            Cuenta cta = (Cuenta) iterator.next();  
            String deudaSistema = getDeudaSistema();
            //req 7.4.23
            if((cta.getCodigoCuenta().equals("IR88") || cta.getCodigoCuenta().equals("IR89")) && (deudaSistema != null && !deudaSistema.equals("")))
            {
            	String formulaReeplazada = cta.getFormula().replaceAll("\\{DEUSBIF\\}", deudaSistema);
				cta.setFormula(formulaReeplazada);
            }
            if ((cta.isCalculada()) && (cta.getTipoCuenta().intValue() == tipoCuenta.intValue())) {            	
            	BigDecimal valor = null;
                if(rangoCta == null || (rangoCta != null && cta.getTipoCuenta().intValue() == idComponentes.intValue() &&   cta.getNumCta().longValue() >= rangoCta.longValue())) {
                	if (hibrido == null || (hibrido !=null && cta.getTipoIndicador()!= null && cta.getTipoIndicador().intValue()==hibrido.intValue() ))
		                try {
		                	valor = getMotor().calcularCuenta(cta);
		                	if(log.isDebugEnabled()) {
		                		log.debug("ETR - VAC: "+idVaciado+" - CTA: "+cta.getCodigoCuenta()+" - VALOR:"+valor);
		                	}
		                } catch (RuntimeException ex) {
		                	errorEnCalculoCuenta = true;
		                	
		                	if (STOP_ON_ERROR) {
		                		log.info("Proceso de calculo detenido. Error al calcular cuenta " +  cta.getCodigoCuenta() + " causado por " + ex.getMessage());
		                		throw ex;
		                	}
		                	
		               		log.info("Error al calcular cuenta " +  cta.getCodigoCuenta() + " causado por " + ex.getMessage());
		                }
		                
		                boolean esCuentaAjustable = false;
		                
		                // la cuenta es ajustable solo si es activo - pasivo - eerr
		        		esCuentaAjustable = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_ACTIVOS).equals(tipoCuenta) 
						||  ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_CIRCULANTE).equals(tipoCuenta) 
						||  ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_INTERES).equals(tipoCuenta)	
						||  ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_EERR).equals(tipoCuenta);
		                
		            	try {
		            		 if (!errorEnCalculoCuenta || (errorEnCalculoCuenta && UPDATE_ON_ERROR)) {
		                        // se actualiza el valor de la cuenta o ajuste según corresponda
		            			if (!FLAG_VACIADO_AJUSTADO.equals(getContextoCalculo().get(VACIADO_AJUSTADO))) {
			                        if (valor == null) {
			                            cta.setMonto(null);
			                        } else {
			                            cta.setMonto(new Double(valor.doubleValue()));
			                        }
		            			} else {
		            				if (esCuentaAjustable) {
		            					// se actualizan solo los ajustes
		                				if (valor == null) {
		     	                            cta.setAjuste(null);
		     	                        } else {
		     	                            cta.setAjuste(new Double(valor.doubleValue()));
		     	                        }
		            				} else {
		            					// se actualiza el monto
		    	                        if (valor == null) {
		    	                            cta.setMonto(null);
		    	                        } else {
		    	                            cta.setMonto(new Double(valor.doubleValue()));
		    	                        }
		            				}
		            			}
		            			if (!esModoProyeccion().booleanValue()) {
			                        // se actualiza el valor de la cuenta en el repositorio
									gestor.actualizarValorCuenta(idVaciado, cta);
		            			} else { // actualizar la tabla proyeccion
		            				Integer periodoPivote = (Integer) this.obtenerValorContexto(ConstantesSEFE.PERIODO_EVALUACION_FORMULA);
		            				//Integer tieneVacParcial = (Integer) this.obtenerValorContexto(ConstantesSEFE.CTX_TIENE_VAC_PARCIAL);            				
		            				//Long idPrimerVacProy = (Long) this.obtenerValorContexto(ConstantesSEFE.CTX_PRIMER_ID_VAC_PROYECCION);	
		            				// recupera las ctas proyectadas
		            				Map  matrizCuentas = (HashMap) SEFEContext.getValue(ConstantesSEFE.CHACHE_CTAS_PROYECTADAS);
		            				// recupera la lista de ctas proyectas
		            				Map  matrizListaCuentas = (HashMap) SEFEContext.getValue(ConstantesSEFE.CHACHE_LTA_CTAS_PROYECTADAS);
		            				if (matrizCuentas ==  null) {
		            					matrizCuentas= new HashMap();
		            					SEFEContext.setValue(ConstantesSEFE.CHACHE_CTAS_PROYECTADAS, matrizCuentas);
		            				}
		            				if (matrizListaCuentas ==  null) {
		            					matrizListaCuentas= new HashMap();
		            					SEFEContext.setValue(ConstantesSEFE.CHACHE_LTA_CTAS_PROYECTADAS, matrizListaCuentas);
		            				}
		            				// crea la matriz de lista de cuentas proyectadas para evitar ir a la base de datos- cambio performance
		            				crearMatrizListaCuentasProyectadas(matrizListaCuentas, periodoPivote, cta);
		            				// crear matriz de valores por periodo par aevitar ir a la base de datos por cada periodo -- cambio performance
		            				crearMatrizCuentasProyectadas(matrizCuentas, cta.getNumCta(), cta.getMonto(), periodoPivote);	
		            			//	gestorProy.actualizarValorCuentaProyectada(idPrimerVacProy, cta, periodoPivote, tipoCuenta, tieneVacParcial);
		            			}
								// se agrega la key del vaciado al contexto para que soporte el calculo de las proyecciones
								String strKey = idVaciado.toString() + ConstantesSEFE.UNDERLINE;
								// se actualiza el valor de la cuenta calculada en el contexto
								this.getContextoCalculo().put(strKey+cta.getCodigoCuenta(), cta);
		                     }
	            	} finally {
	            		errorEnCalculoCuenta = false;
	            	}
                }
            }
        }
    }
    
    private void calcularCuentasServicioCons(Long idVaciado, Integer tipoCuenta, boolean esRezagado, Integer hibrido) {
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();   	
        getMotor().inicializarContexto(this.getContextoCalculo());
        Integer idComponentes = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_COMPONENTES);
        // se calcula cada uno de las cuentas del balance
        Iterator iterator = this.getCuentasVaciado().iterator();
        boolean errorEnCalculoCuenta = false;
        Long rangoCta=null; 
        if (esRezagado ) {
    		rangoCta = this.obtenerRangoCtasCalcular();
    	}
        while (iterator.hasNext()) {
            Cuenta cta = (Cuenta) iterator.next();           
            if ((cta.isCalculada()) && (cta.getTipoCuenta().intValue() == tipoCuenta.intValue())) {            	
                BigDecimal valor = null;
                if(rangoCta == null || (rangoCta != null && cta.getTipoCuenta().intValue() == idComponentes.intValue() &&   cta.getNumCta().longValue() >= rangoCta.longValue())) {
                	if (hibrido == null || (hibrido !=null && cta.getTipoIndicador()!= null && cta.getTipoIndicador().intValue()==hibrido.intValue() ))
		                try {
		                	valor = getMotor().calcularCuenta(cta);
		                	if(log.isDebugEnabled()) {
		                		log.debug("ETR - VAC: "+idVaciado+" - CTA: "+cta.getCodigoCuenta()+" - VALOR:"+valor);
		                	}
		                } catch (RuntimeException ex) {
		                	errorEnCalculoCuenta = true;
		                	
		                	if (STOP_ON_ERROR) {
		                		log.info("Proceso de calculo detenido. Error al calcular cuenta " +  cta.getCodigoCuenta() + " causado por " + ex.getMessage());
		                		throw ex;
		                	}
		                	
		               		log.info("Error al calcular cuenta " +  cta.getCodigoCuenta() + " causado por " + ex.getMessage());
		                }
		                
		                boolean esCuentaAjustable = false;
		                
		                // la cuenta es ajustable solo si es activo - pasivo - eerr
		        		esCuentaAjustable = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_ACTIVOS).equals(tipoCuenta) 
						||  ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_CIRCULANTE).equals(tipoCuenta) 
						||  ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_INTERES).equals(tipoCuenta)	
						||  ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_EERR).equals(tipoCuenta);
		                
		            	try {
		            		 if (!errorEnCalculoCuenta || (errorEnCalculoCuenta && UPDATE_ON_ERROR)) {
		                        // se actualiza el valor de la cuenta o ajuste según corresponda
		            			if (!FLAG_VACIADO_AJUSTADO.equals(getContextoCalculo().get(VACIADO_AJUSTADO))) {
			                        if (valor == null) {
			                            cta.setMonto(null);
			                        } else {
			                            cta.setMonto(new Double(valor.doubleValue()));
			                        }
		            			} else {
		            				if (esCuentaAjustable) {
		            					// se actualizan solo los ajustes
		                				if (valor == null) {
		     	                            cta.setAjuste(null);
		     	                        } else {
		     	                            cta.setAjuste(new Double(valor.doubleValue()));
		     	                        }
		            				} else {
		            					// se actualiza el monto
		    	                        if (valor == null) {
		    	                            cta.setMonto(null);
		    	                        } else {
		    	                            cta.setMonto(new Double(valor.doubleValue()));
		    	                        }
		            				}
		            			}
		            			if (!esModoProyeccion().booleanValue()) {
			                        // se actualiza el valor de la cuenta en el repositorio
									gestor.actualizarValorCuenta(idVaciado, cta);
		            			} else { // actualizar la tabla proyeccion
		            				Integer periodoPivote = (Integer) this.obtenerValorContexto(ConstantesSEFE.PERIODO_EVALUACION_FORMULA);
		            				//Integer tieneVacParcial = (Integer) this.obtenerValorContexto(ConstantesSEFE.CTX_TIENE_VAC_PARCIAL);            				
		            				//Long idPrimerVacProy = (Long) this.obtenerValorContexto(ConstantesSEFE.CTX_PRIMER_ID_VAC_PROYECCION);	
		            				// recupera las ctas proyectadas
		            				Map  matrizCuentas = (HashMap) SEFEContext.getValue(ConstantesSEFE.CHACHE_CTAS_PROYECTADAS);
		            				// recupera la lista de ctas proyectas
		            				Map  matrizListaCuentas = (HashMap) SEFEContext.getValue(ConstantesSEFE.CHACHE_LTA_CTAS_PROYECTADAS);
		            				if (matrizCuentas ==  null) {
		            					matrizCuentas= new HashMap();
		            					SEFEContext.setValue(ConstantesSEFE.CHACHE_CTAS_PROYECTADAS, matrizCuentas);
		            				}
		            				if (matrizListaCuentas ==  null) {
		            					matrizListaCuentas= new HashMap();
		            					SEFEContext.setValue(ConstantesSEFE.CHACHE_LTA_CTAS_PROYECTADAS, matrizListaCuentas);
		            				}
		            				// crea la matriz de lista de cuentas proyectadas para evitar ir a la base de datos- cambio performance
		            				crearMatrizListaCuentasProyectadas(matrizListaCuentas, periodoPivote, cta);
		            				// crear matriz de valores por periodo par aevitar ir a la base de datos por cada periodo -- cambio performance
		            				crearMatrizCuentasProyectadas(matrizCuentas, cta.getNumCta(), cta.getMonto(), periodoPivote);	
		            			//	gestorProy.actualizarValorCuentaProyectada(idPrimerVacProy, cta, periodoPivote, tipoCuenta, tieneVacParcial);
		            			}
								// se agrega la key del vaciado al contexto para que soporte el calculo de las proyecciones
								String strKey = idVaciado.toString() + ConstantesSEFE.UNDERLINE;
								// se actualiza el valor de la cuenta calculada en el contexto
								this.getContextoCalculo().put(strKey+cta.getCodigoCuenta(), cta);
		                     }
	            	} finally {
	            		errorEnCalculoCuenta = false;
	            	}
                }
            }
        }
    }

    private void calcularOtrasCuentasServicio(Long idVaciado) {
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();   	
        getMotor().inicializarContexto(this.getContextoCalculo());
        
        // se calcula cada uno de las cuentas del balance
        Iterator iterator = this.getCuentasVaciado().iterator();
        while (iterator.hasNext()) {
            Cuenta cta = (Cuenta) iterator.next();
            if ((cta.isCalculada())) {
                BigDecimal valor = null;
                try {
                	
                	valor = getMotor().calcularCuenta(cta);
                	if(log.isDebugEnabled()) {
                		log.debug("ETR - VAC: "+idVaciado+" - CTA: "+cta.getCodigoCuenta()+" - VALOR:"+valor);
                	}
                } catch (RuntimeException ex) {               	
                	if (STOP_ON_ERROR) {
                		log.info("Proceso de calculo detenido. Error al calcular cuenta " +  cta.getCodigoCuenta() + " causado por " + ex.getMessage());
                		throw ex;
                	}
                	
               		log.info("Error al calcular cuenta " +  cta.getCodigoCuenta() + " causado por " + ex.getMessage());
                }
				// se agrega la key del vaciado al contexto para que soporte el calculo de las proyecciones
				String strKey = idVaciado.toString() + ConstantesSEFE.UNDERLINE;
				// se actualiza el valor de la cuenta calculada en el contexto
				this.getContextoCalculo().put(strKey+cta.getCodigoCuenta(), cta);
            }
        }
    }
    
    
    /**
     * metodo que guarda en cache las cuentas calculadas desde el periodo p2 haciea adelante con el objetivo de hacer una sola vez la actualizacion sobre la
     * tabla ta_sefe_proy_larga
     * @param matrizCuentas
     * @param key
     * @param valor
     */
    private void crearMatrizCuentasProyectadas(Map matrizCuentas, Long key, Double valor, Integer pivote) {
		Object object = matrizCuentas.get(key);
		String newValor = "null";
		if (valor != null) {
			newValor =valor.toString();
		}
		if (object != null) {			
			StringBuffer buffer = (StringBuffer)object;		
			String  valores[] = buffer.toString().split(ConstantesSEFE.CARACTER_AMPERSAND);
			// verifica si ya se ha ingresado un valor en el periodo pivote 
			if (valores.length== (pivote.intValue())) {// si se cumple la condicion entonces se debe actualizar el valor recalculado				
				valores[pivote.intValue()-1] = newValor; // se resta 1 al periodo pivote porque la proy larga comienza en el p2
				StringBuffer nuevoTexto = new StringBuffer();
				for (int i=0; i<valores.length; i++) {
					if (i!=0 ){
						nuevoTexto.append(ConstantesSEFE.CARACTER_AMPERSAND);
					}			
					nuevoTexto.append(valores[i]);
				}
				matrizCuentas.put(key, nuevoTexto);
			} else {
				buffer.append(ConstantesSEFE.CARACTER_AMPERSAND);
				buffer.append(newValor);
			}
			//matrizCuentas.put(key, buffer);
		}else {
			StringBuffer buffer = new StringBuffer();
			buffer.append(newValor);				
			matrizCuentas.put(key, buffer);				
		}
	}
    
    /**
     * metodo que guarda la cuentas por periodo en cache para evitar ir a la DB cada vez.
     * @param matrizListaCuentas
     * @param key
     * @param valor
     */
    private void crearMatrizListaCuentasProyectadas(Map matrizListaCuentas, Integer key, Cuenta valor) {
		Object object = matrizListaCuentas.get(key);
		if (object != null) {
			
			Map lista = (Map)object;		
			lista.put(valor.getNumCta(), valor);			
			//matrizListaCuentas.put(key, lista);
		}else {
			Map lista =new  HashMap();
			lista.put(valor.getNumCta(),valor);				
			matrizListaCuentas.put(key, lista);				
		}
	}
    
    
    /**
     * Crea un contexto para ser utilizado en esta sesion de calculo
     * de las cuentas
     * 
     * @param idVaciado - identificador del vaciao
     * @param cuentas - tipo de cuenta a calcular
     * @return - mapa con el contexto requerido para el calculo
     */
    /**
     * Crea un contexto para ser utilizado en esta sesion de calculo
     * de las cuentas
     * 
     * @param idVaciado - identificador del vaciao
     * @param cuentas - tipo de cuenta a calcular
     * @return - mapa con el contexto requerido para el calculo
     */
    private Map crearContextoCalculo(Long idVaciado, Collection cuentas) {
        Map ctx 										= new HashMap();   
        Boolean isVacActualCLP							= Boolean.TRUE;
        Vaciado vacPerTrimestralAnt 					= null;
        Vaciado vacPerTrimestralAnoAnt					= null;
        // todas la cuentas se indexan por codigo de cuenta
        Iterator iterator = cuentas.iterator();
        while (iterator.hasNext()) {
            Cuenta cta = (Cuenta) iterator.next();
            ctx.put(cta.getCodigoCuenta(), cta);
        }
         // se agregan constantes e id de vaciados anterior y equivalente
        GestorVaciados gestor = new GestorVaciadosImpl();
        Vaciado vaciado = gestor.buscarVaciado(idVaciado);
        motor = createMotorCalculo(vaciado);
        Vaciado vacAnterior = gestor.buscarVaciadoAnterior(idVaciado);
        Integer meses 			   	  = ConstantesSEFE.ETR_PERIODO_TRIMESTRAL;
        Integer ano 			   	  = ConstantesSEFE.ETR_PERIODO_ANUAL;
        // atributo para validar si es neceario recuperar las cuentas de evolucion trimestral
        Object  esEvolucionTrimetral	  = null;
        // verificar si se debe calcular la proyeccion trimestral
        if (this.getContextoApplication() !=null) {
        	esEvolucionTrimetral = this.getContextoApplication().get(ConstantesSEFE.CTX_PERIODO_TRIMESTRAL);
        }
        Date siguientePeriodo = FormatUtil.ultimoDiaCalendario(FormatUtil.obtenerSiguientePeriodo(vaciado.getPeriodo(), new Float( meses.intValue()*-1) ));
        Date anoAnteriorPeriodo = FormatUtil.ultimoDiaCalendario(FormatUtil.obtenerSiguientePeriodo(vaciado.getPeriodo(), new Float( ano.intValue()*-1) ));
        // verificar si se debe calcular la proyeccion trimestral 
        if (esEvolucionTrimetral != null) {
        	Integer ajuste = (Integer)this.getContextoApplication().get(ConstantesSEFE.CTX_BUSCAR_VAC_CON_AJUSTE);
	        // recupera el vaciado trimestral anterior.
	        vacPerTrimestralAnt = gestor.obtenerVaciadoPorPeriodoConDiferentePlanCta(idVaciado, siguientePeriodo, ajuste);
	        // recupera el vaciado trimestral del ano anterior
	        vacPerTrimestralAnoAnt = gestor.obtenerVaciadoPorPeriodoConDiferentePlanCta(idVaciado, anoAnteriorPeriodo, ajuste);
        }
        // crea el contexto para calcular la proyeccion corta y larga
        this.crearContextoCalculoProyeccion(idVaciado, ctx);
        Vaciado vacEquivalente 		= gestor.buscarVaciadoEquivalente(idVaciado);
        Vaciado vacAnteriorOriginal = gestor.buscarVaciadoAnteriorNoAjustado(idVaciado);
        // calcular el factor correccion monedad unidad para el vaciado anterior
        BigDecimal factorCorreccionAnterior 	= calcularFactorCorreccionMonedaUnidad(vacAnterior, vaciado);
        // calcular el factor correccion monedad unidad para el vaciado equivalente
        BigDecimal factorCorreccionEquivalente 	= calcularFactorCorreccionMonedaUnidad(vacEquivalente, vaciado);
        // calcula el factor correccion moneda unidad para  el vaciado trimestral anterior
        BigDecimal factorCorreccionPerAnt 	= calcularFactorCorreccionMonedaUnidadEvoTrimestral(vacPerTrimestralAnt, vaciado);
        BigDecimal factorCorreccionPerAnoAnt 	= calcularFactorCorreccionMonedaUnidadEvoTrimestral(vacPerTrimestralAnoAnt, vaciado);
        ctx.put(K_MON_UNI_ANTERIOR, factorCorreccionAnterior);
        ctx.put(ID_VAC_ACTUAL, idVaciado);        
        ctx.put(K_MON_UNI_EQUIVALENTE, factorCorreccionEquivalente);
        ctx.put(K_MON_UNI_PER_ANT, factorCorreccionPerAnt);
        ctx.put(K_MON_UNI_PER_ANO_ANT, factorCorreccionPerAnoAnt);
        ctx.put(NUM_MESES, vaciado.getMesesPer());
        ctx.put(CAR_MAS, vaciado.getTipoCargaFlg());	
        ctx.put(NUM_MESES, vaciado.getMesesPer());
        // se agrega el contexto la deuda del cliente, tal deuda esta homologada a la moneda y unidad del vaciado pivote
        ctx.put(DSBIF, obtenerDeudaCliente(vaciado));
        // si el vaciado actual usa moneda CLP entonces se debe calcular el IPC para los vaciados anterior/equivalente
        // independientemente si usan moneda extranjera
        if (!ConstantesSEFE.ID_CLASIF_MONEDA_CLP.equals(vaciado.getIdMoneda())) {
        	isVacActualCLP = Boolean.FALSE;
        }
        
        if (vaciado != null) {
	        Map mapCuentas = crearMapCtasVaciado(vaciado.getIdVaciado());
	        ctx.putAll(mapCuentas);
        }
        
        // si existe vaciado periodo trimestral anterior se recuperan todas las cuentas del vaciado
        // y se ponen en contexto, contatenando ID_VAC + "_" + cod_cuenta
        if (vacPerTrimestralAnt != null) {
        	ctx.put(ID_VAC_PER_ANT, vacPerTrimestralAnt.getIdVaciado());
	        Date periodoTrim = vacPerTrimestralAnt.getPeriodo();
	
	        Map mapCuentas = crearMapCtasVaciado(vacPerTrimestralAnt.getIdVaciado());
	        ctx.putAll(mapCuentas);
	        
	        // se determina si el vaciado anterior es ajustado
	        if (vacPerTrimestralAnt.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()) {
	        	ctx.put(vacPerTrimestralAnt.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, vacPerTrimestralAnt.getAjustadoFlg());
	        }
	        
	        Double ipcTrimAnterior = buscarIPC(vacPerTrimestralAnt, periodoTrim, isVacActualCLP);
	        ctx.put(IPC_PREV, ipcTrimAnterior);
	        if (log.isInfoEnabled()) {
	        	log.info("Vac TrimesreAnt="+ vacPerTrimestralAnt.getIdVaciado());
	        	log.info("IPC trimestre anterior="+ipcTrimAnterior);
	 	   }
        	
        }
        // si existe vaciado periodo trimestral anterior se recuperan todas las cuentas del vaciado
        // y se ponen en contexto, contatenando ID_VAC + "_" + cod_cuenta
        if (vacPerTrimestralAnoAnt != null) {
        	ctx.put(ID_VAC_PER_ANO_ANT, vacPerTrimestralAnoAnt.getIdVaciado());
	        Date periodoTrim = vacPerTrimestralAnoAnt.getPeriodo();
	
	        Map mapCuentas = crearMapCtasVaciado(vacPerTrimestralAnoAnt.getIdVaciado());
	        ctx.putAll(mapCuentas);
	        
	        // se determina si el vaciado anterior es ajustado
	        if (vacPerTrimestralAnoAnt.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()) {
	        	ctx.put(vacPerTrimestralAnoAnt.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, vacPerTrimestralAnoAnt.getAjustadoFlg());
	        }
	        
	        Double ipcTrimAnterior = buscarIPC(vacPerTrimestralAnoAnt, periodoTrim, isVacActualCLP);
	        ctx.put(IPC_PREV, ipcTrimAnterior);
	        if (log.isInfoEnabled()) {
	        	log.info("Vac TrimesreAnt="+ vacPerTrimestralAnoAnt.getIdVaciado());
	        	log.info("IPC trimestre anterior="+ipcTrimAnterior);
	 	   }
        	
        }
    
       // 20120611 marias - si existe vaciado anterior se recuperan todas las cuentas del vaciado
       // y se ponen en contexto, contatenando ID_VAC + "_" + cod_cuenta
	   if (vacAnterior != null) {
	        ctx.put(ID_VAC_PREV, vacAnterior.getIdVaciado());
	        Date perAnterior = vacAnterior.getPeriodo();
	        
	        Map mapCuentas = crearMapCtasVaciado(vacAnterior.getIdVaciado());
	        ctx.putAll(mapCuentas);
	        
	        // se determina si el vaciado anterior es ajustado
	        if (vacAnterior.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue() || vacAnterior.getIdTipoBalance().intValue()== ConstantesSEFE.CLASIF_ID_NO_APLICA.intValue() ) {
	        	ctx.put(vacAnterior.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, FLAG_VACIADO_AJUSTADO);
	        }
	        Double ipcAnterior = buscarIPC(vacAnterior, perAnterior, isVacActualCLP);
	        //Double ipcAnterior = new Double(102.35);
	        ctx.put(IPC_PREV, ipcAnterior);
	        if (log.isInfoEnabled())
	 	   {
	        	log.info("Vac anterior="+ vacAnterior.getIdVaciado());
	        	log.info("IPC anterior="+ipcAnterior);
	 	   }
	   }else  {
		   log.info("Vac anterior="+ vacAnterior);
		   log.info("vac actual ="+ idVaciado);
	   }
	   
       // 20120611 marias - si existe vaciado anterior original se recuperan todas las cuentas del vaciado
       // y se ponen en contexto, contatenando ID_VAC + "_" + cod_cuenta
	   if (vacAnteriorOriginal != null) {
	    	ctx.put(ID_VAC_PREV_ORIG, vacAnteriorOriginal.getIdVaciado());
	    	
	        Map mapCuentas = crearMapCtasVaciado(vacAnteriorOriginal.getIdVaciado());
	        ctx.putAll(mapCuentas);
	        
	        // se determina si el vaciado anterior original es ajustado
	        if (vacAnteriorOriginal.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()  || vacAnteriorOriginal.getIdTipoBalance().intValue()== ConstantesSEFE.CLASIF_ID_NO_APLICA.intValue() ) {
	        	ctx.put(vacAnteriorOriginal.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, FLAG_VACIADO_AJUSTADO);
	        }
	   } 
	   
       // 20120611 marias - si existe vaciado equivalente se recuperan todas las cuentas del vaciado
       // y se ponen en contexto, contatenando ID_VAC + "_" + cod_cuenta
	   if (vacEquivalente != null) {
	        ctx.put(ID_VAC_EQUIV, vacEquivalente.getIdVaciado());
	        Date perVacEquivalente = vacEquivalente.getPeriodo();
	    	
	        Map mapCuentas = crearMapCtasVaciado(vacEquivalente.getIdVaciado());
	        ctx.putAll(mapCuentas);
	        
	        // se determina si el vaciado equivalente es ajustado
	        if (vacEquivalente.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()  || vacEquivalente.getIdTipoBalance().intValue()== ConstantesSEFE.CLASIF_ID_NO_APLICA.intValue() ) {
	        	ctx.put(vacEquivalente.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, FLAG_VACIADO_AJUSTADO);
	        }
	        
	    	Double ipcEquiv = buscarIPC(vacEquivalente, perVacEquivalente, isVacActualCLP);
	    	if (log.isInfoEnabled())
	    	{
	    		log.info("id vaciado equivalente="+vacEquivalente.getIdVaciado() );
	    		log.info("IPC equivalente="+ipcEquiv);
	    	}
	        ctx.put(IPC_EQUIV, ipcEquiv);
	   }     	    
		    
	   if (FLAG_VACIADO_AJUSTADO.equals(vaciado.getAjustadoFlg()) || vaciado.getIdTipoBalance().intValue()== ConstantesSEFE.CLASIF_ID_NO_APLICA.intValue()){
	    	ctx.put(VACIADO_AJUSTADO, FLAG_VACIADO_AJUSTADO);
	   }
	    
	   Double ipcActual = buscarIPC(vaciado, vaciado.getPeriodo(), isVacActualCLP);
	   if (log.isInfoEnabled())
	   {
		   log.info("id vaciado actual="+vaciado.getIdVaciado() );
		   if (ipcActual != null)
		   {
			   log.info("IPC actual="+ipcActual.doubleValue());
		   }
	   }
	   ctx.put(IPC, ipcActual);
	
	   // Se revisa si existe Detalle Correccion Monetaria y se agrega al contexto de calculo.
	   if (tieneCorrecionMonetaria(idVaciado)) {
	    	ctx.put(DET_CM, Boolean.TRUE);
	    	if (log.isInfoEnabled()) {
	    		log.info("Vaciado tiene correccion monetaria...");
	    	}
	   } else {
	    	ctx.put(DET_CM, Boolean.FALSE);
	    	if (log.isInfoEnabled()) {
	    		log.info("Vaciado no tiene correccion monetaria...");
	    	}
	   }
	   
	   return ctx;
    }
    
    private Map crearContextoCalculoCons(Long idVaciado, Collection cuentas) {
        Map ctx 										= new HashMap();   
        Boolean isVacActualCLP							= Boolean.TRUE;
        Vaciado vacPerTrimestralAnt 					= null;
        Vaciado vacPerTrimestralAnoAnt					= null;
        // todas la cuentas se indexan por codigo de cuenta
        Iterator iterator = cuentas.iterator();
        while (iterator.hasNext()) {
            Cuenta cta = (Cuenta) iterator.next();
            ctx.put(cta.getCodigoCuenta(), cta);
        }
         // se agregan constantes e id de vaciados anterior y equivalente
        GestorVaciados gestor = new GestorVaciadosImpl();
        Vaciado vaciado = gestor.buscarVaciado(idVaciado);
        motor = createMotorCalculo(vaciado);
        Vaciado vacAnterior = gestor.buscarVaciadoAnterior(idVaciado);
        Integer meses 			   	  = ConstantesSEFE.ETR_PERIODO_TRIMESTRAL;
        Integer ano 			   	  = ConstantesSEFE.ETR_PERIODO_ANUAL;
        // atributo para validar si es neceario recuperar las cuentas de evolucion trimestral
        Object  esEvolucionTrimetral	  = null;
        // verificar si se debe calcular la proyeccion trimestral
        if (this.getContextoApplication() !=null) {
        	esEvolucionTrimetral = this.getContextoApplication().get(ConstantesSEFE.CTX_PERIODO_TRIMESTRAL);
        }
        Date siguientePeriodo = FormatUtil.ultimoDiaCalendario(FormatUtil.obtenerSiguientePeriodo(vaciado.getPeriodo(), new Float( meses.intValue()*-1) ));
        Date anoAnteriorPeriodo = FormatUtil.ultimoDiaCalendario(FormatUtil.obtenerSiguientePeriodo(vaciado.getPeriodo(), new Float( ano.intValue()*-1) ));
        // verificar si se debe calcular la proyeccion trimestral 
        if (esEvolucionTrimetral != null) {
        	Integer ajuste = (Integer)this.getContextoApplication().get(ConstantesSEFE.CTX_BUSCAR_VAC_CON_AJUSTE);
	        // recupera el vaciado trimestral anterior.
	        vacPerTrimestralAnt = gestor.obtenerVaciadoPorPeriodoConDiferentePlanCta(idVaciado, siguientePeriodo, ajuste);
	        // recupera el vaciado trimestral del ano anterior
	        vacPerTrimestralAnoAnt = gestor.obtenerVaciadoPorPeriodoConDiferentePlanCta(idVaciado, anoAnteriorPeriodo, ajuste);
        }
        // crea el contexto para calcular la proyeccion corta y larga
        this.crearContextoCalculoProyeccion(idVaciado, ctx);
        Vaciado vacEquivalente 		= gestor.buscarVaciadoEquivalente(idVaciado);
        Vaciado vacAnteriorOriginal = gestor.buscarVaciadoAnteriorNoAjustado(idVaciado);
        // calcular el factor correccion monedad unidad para el vaciado anterior
        BigDecimal factorCorreccionAnterior 	= calcularFactorCorreccionMonedaUnidad(vacAnterior, vaciado);
        // calcular el factor correccion monedad unidad para el vaciado equivalente
        BigDecimal factorCorreccionEquivalente 	= calcularFactorCorreccionMonedaUnidad(vacEquivalente, vaciado);
        // calcula el factor correccion moneda unidad para  el vaciado trimestral anterior
        BigDecimal factorCorreccionPerAnt 	= calcularFactorCorreccionMonedaUnidadEvoTrimestral(vacPerTrimestralAnt, vaciado);
        BigDecimal factorCorreccionPerAnoAnt 	= calcularFactorCorreccionMonedaUnidadEvoTrimestral(vacPerTrimestralAnoAnt, vaciado);
        ctx.put(K_MON_UNI_ANTERIOR, factorCorreccionAnterior);
        ctx.put(ID_VAC_ACTUAL, idVaciado);        
        ctx.put(K_MON_UNI_EQUIVALENTE, factorCorreccionEquivalente);
        ctx.put(K_MON_UNI_PER_ANT, factorCorreccionPerAnt);
        ctx.put(K_MON_UNI_PER_ANO_ANT, factorCorreccionPerAnoAnt);
        ctx.put(NUM_MESES, vaciado.getMesesPer());
        ctx.put(CAR_MAS, vaciado.getTipoCargaFlg());	
        ctx.put(NUM_MESES, vaciado.getMesesPer());
        // se agrega el contexto la deuda del cliente, tal deuda esta homologada a la moneda y unidad del vaciado pivote
        ctx.put(DSBIF, obtenerDeudaCliente(vaciado));
        // si el vaciado actual usa moneda CLP entonces se debe calcular el IPC para los vaciados anterior/equivalente
        // independientemente si usan moneda extranjera
        if (!ConstantesSEFE.ID_CLASIF_MONEDA_CLP.equals(vaciado.getIdMoneda())) {
        	isVacActualCLP = Boolean.FALSE;
        }
        
        if (vaciado != null) {
	        Map mapCuentas = crearMapCtasVaciadoCons(vaciado.getIdVaciado());
	        ctx.putAll(mapCuentas);
        }
        
        // si existe vaciado periodo trimestral anterior se recuperan todas las cuentas del vaciado
        // y se ponen en contexto, contatenando ID_VAC + "_" + cod_cuenta
        if (vacPerTrimestralAnt != null) {
        	ctx.put(ID_VAC_PER_ANT, vacPerTrimestralAnt.getIdVaciado());
	        Date periodoTrim = vacPerTrimestralAnt.getPeriodo();
	
	        Map mapCuentas = crearMapCtasVaciadoCons(vacPerTrimestralAnt.getIdVaciado());
	        ctx.putAll(mapCuentas);
	        
	        // se determina si el vaciado anterior es ajustado
	        if (vacPerTrimestralAnt.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()) {
	        	ctx.put(vacPerTrimestralAnt.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, vacPerTrimestralAnt.getAjustadoFlg());
	        }
	        
	        Double ipcTrimAnterior = buscarIPC(vacPerTrimestralAnt, periodoTrim, isVacActualCLP);
	        ctx.put(IPC_PREV, ipcTrimAnterior);
	        if (log.isInfoEnabled()) {
	        	log.info("Vac TrimesreAnt="+ vacPerTrimestralAnt.getIdVaciado());
	        	log.info("IPC trimestre anterior="+ipcTrimAnterior);
	 	   }
        	
        }
        // si existe vaciado periodo trimestral anterior se recuperan todas las cuentas del vaciado
        // y se ponen en contexto, contatenando ID_VAC + "_" + cod_cuenta
        if (vacPerTrimestralAnoAnt != null) {
        	ctx.put(ID_VAC_PER_ANO_ANT, vacPerTrimestralAnoAnt.getIdVaciado());
	        Date periodoTrim = vacPerTrimestralAnoAnt.getPeriodo();
	
	        Map mapCuentas = crearMapCtasVaciadoCons(vacPerTrimestralAnoAnt.getIdVaciado());
	        ctx.putAll(mapCuentas);
	        
	        // se determina si el vaciado anterior es ajustado
	        if (vacPerTrimestralAnoAnt.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()) {
	        	ctx.put(vacPerTrimestralAnoAnt.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, vacPerTrimestralAnoAnt.getAjustadoFlg());
	        }
	        
	        Double ipcTrimAnterior = buscarIPC(vacPerTrimestralAnoAnt, periodoTrim, isVacActualCLP);
	        ctx.put(IPC_PREV, ipcTrimAnterior);
	        if (log.isInfoEnabled()) {
	        	log.info("Vac TrimesreAnt="+ vacPerTrimestralAnoAnt.getIdVaciado());
	        	log.info("IPC trimestre anterior="+ipcTrimAnterior);
	 	   }
        	
        }
    
       // 20120611 marias - si existe vaciado anterior se recuperan todas las cuentas del vaciado
       // y se ponen en contexto, contatenando ID_VAC + "_" + cod_cuenta
	   if (vacAnterior != null) {
	        ctx.put(ID_VAC_PREV, vacAnterior.getIdVaciado());
	        Date perAnterior = vacAnterior.getPeriodo();
	        
	        Map mapCuentas = crearMapCtasVaciadoCons(vacAnterior.getIdVaciado());
	        ctx.putAll(mapCuentas);
	        
	        // se determina si el vaciado anterior es ajustado
	        if (vacAnterior.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue() || vacAnterior.getIdTipoBalance().intValue()== ConstantesSEFE.CLASIF_ID_NO_APLICA.intValue() ) {
	        	ctx.put(vacAnterior.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, FLAG_VACIADO_AJUSTADO);
	        }
	        Double ipcAnterior = buscarIPC(vacAnterior, perAnterior, isVacActualCLP);
	        //Double ipcAnterior = new Double(102.35);
	        ctx.put(IPC_PREV, ipcAnterior);
	        if (log.isInfoEnabled())
	 	   {
	        	log.info("Vac anterior="+ vacAnterior.getIdVaciado());
	        	log.info("IPC anterior="+ipcAnterior);
	 	   }
	   }else  {
		   log.info("Vac anterior="+ vacAnterior);
		   log.info("vac actual ="+ idVaciado);
	   }
	   
       // 20120611 marias - si existe vaciado anterior original se recuperan todas las cuentas del vaciado
       // y se ponen en contexto, contatenando ID_VAC + "_" + cod_cuenta
	   if (vacAnteriorOriginal != null) {
	    	ctx.put(ID_VAC_PREV_ORIG, vacAnteriorOriginal.getIdVaciado());
	    	
	        Map mapCuentas = crearMapCtasVaciadoCons(vacAnteriorOriginal.getIdVaciado());
	        ctx.putAll(mapCuentas);
	        
	        // se determina si el vaciado anterior original es ajustado
	        if (vacAnteriorOriginal.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()  || vacAnteriorOriginal.getIdTipoBalance().intValue()== ConstantesSEFE.CLASIF_ID_NO_APLICA.intValue() ) {
	        	ctx.put(vacAnteriorOriginal.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, FLAG_VACIADO_AJUSTADO);
	        }
	   } 
	   
       // 20120611 marias - si existe vaciado equivalente se recuperan todas las cuentas del vaciado
       // y se ponen en contexto, contatenando ID_VAC + "_" + cod_cuenta
	   if (vacEquivalente != null) {
	        ctx.put(ID_VAC_EQUIV, vacEquivalente.getIdVaciado());
	        Date perVacEquivalente = vacEquivalente.getPeriodo();
	    	
	        Map mapCuentas = crearMapCtasVaciadoCons(vacEquivalente.getIdVaciado());
	        ctx.putAll(mapCuentas);
	        
	        // se determina si el vaciado equivalente es ajustado
	        if (vacEquivalente.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()  || vacEquivalente.getIdTipoBalance().intValue()== ConstantesSEFE.CLASIF_ID_NO_APLICA.intValue() ) {
	        	ctx.put(vacEquivalente.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, FLAG_VACIADO_AJUSTADO);
	        }
	        
	    	Double ipcEquiv = buscarIPC(vacEquivalente, perVacEquivalente, isVacActualCLP);
	    	if (log.isInfoEnabled())
	    	{
	    		log.info("id vaciado equivalente="+vacEquivalente.getIdVaciado() );
	    		log.info("IPC equivalente="+ipcEquiv);
	    	}
	        ctx.put(IPC_EQUIV, ipcEquiv);
	   }     	    
		    
	   if (FLAG_VACIADO_AJUSTADO.equals(vaciado.getAjustadoFlg()) || vaciado.getIdTipoBalance().intValue()== ConstantesSEFE.CLASIF_ID_NO_APLICA.intValue()){
	    	ctx.put(VACIADO_AJUSTADO, FLAG_VACIADO_AJUSTADO);
	   }
	    
	   Double ipcActual = buscarIPC(vaciado, vaciado.getPeriodo(), isVacActualCLP);
	   if (log.isInfoEnabled())
	   {
		   log.info("id vaciado actual="+vaciado.getIdVaciado() );
		   if (ipcActual != null)
		   {
			   log.info("IPC actual="+ipcActual.doubleValue());
		   }
	   }
	   ctx.put(IPC, ipcActual);
	
	   // Se revisa si existe Detalle Correccion Monetaria y se agrega al contexto de calculo.
	   if (tieneCorrecionMonetaria(idVaciado)) {
	    	ctx.put(DET_CM, Boolean.TRUE);
	    	if (log.isInfoEnabled()) {
	    		log.info("Vaciado tiene correccion monetaria...");
	    	}
	   } else {
	    	ctx.put(DET_CM, Boolean.FALSE);
	    	if (log.isInfoEnabled()) {
	    		log.info("Vaciado no tiene correccion monetaria...");
	    	}
	   }
	   
	   return ctx;
    }
    
    /**
     * metodo que pone en contexto las cuentas utilizadas en los reportes hibridos
     * @param idVaciado
     * @param cuentas
     * @return
     */
    
    private Map crearContextoCalculoHibridos(Long idVaciado, Collection cuentas) {
        Map ctx 										= new HashMap();   
        Boolean isVacActualCLP							= Boolean.TRUE;
        // todas la cuentas se indexan por codigo de cuenta
        Iterator iterator = cuentas.iterator();
        while (iterator.hasNext()) {
            Cuenta cta = (Cuenta) iterator.next();
            ctx.put(cta.getCodigoCuenta(), cta);
        }
         // se agregan constantes e id de vaciados anterior y equivalente
        GestorVaciados gestor = new GestorVaciadosImpl();
        Vaciado vaciado = gestor.buscarVaciado(idVaciado);
        motor = createMotorCalculo(vaciado);

        ctx.put(ID_VAC_ACTUAL, idVaciado);        
        ctx.put(NUM_MESES, vaciado.getMesesPer());
        ctx.put(CAR_MAS, vaciado.getTipoCargaFlg());	
        ctx.put(NUM_MESES, vaciado.getMesesPer());
        // se agrega el contexto la deuda del cliente, tal deuda esta homologada a la moneda y unidad del vaciado pivote
        ctx.put(DSBIF, obtenerDeudaCliente(vaciado));
        // si el vaciado actual usa moneda CLP entonces se debe calcular el IPC para los vaciados anterior/equivalente
        // independientemente si usan moneda extranjera
        if (!ConstantesSEFE.ID_CLASIF_MONEDA_CLP.equals(vaciado.getIdMoneda())) {
        	isVacActualCLP = Boolean.FALSE;
        }
        
        if (vaciado != null) {
	        Map mapCuentas = crearMapCtasHibridasVaciado(vaciado.getIdVaciado());
	        ctx.putAll(mapCuentas);
        }
		    
	   if (FLAG_VACIADO_AJUSTADO.equals(vaciado.getAjustadoFlg()) || vaciado.getIdTipoBalance().intValue()== ConstantesSEFE.CLASIF_ID_NO_APLICA.intValue()){
	    	ctx.put(VACIADO_AJUSTADO, FLAG_VACIADO_AJUSTADO);
	   }
	    
	   Double ipcActual = buscarIPC(vaciado, vaciado.getPeriodo(), isVacActualCLP);
	   if (log.isInfoEnabled())
	   {
		   log.info("id vaciado actual="+vaciado.getIdVaciado() );
		   if (ipcActual != null)
		   {
			   log.info("IPC actual="+ipcActual.doubleValue());
		   }
	   }
	   ctx.put(IPC, ipcActual);
	
	   // Se revisa si existe Detalle Correccion Monetaria y se agrega al contexto de calculo.
	   if (tieneCorrecionMonetaria(idVaciado)) {
	    	ctx.put(DET_CM, Boolean.TRUE);
	    	if (log.isInfoEnabled()) {
	    		log.info("Vaciado tiene correccion monetaria...");
	    	}
	   } else {
	    	ctx.put(DET_CM, Boolean.FALSE);
	    	if (log.isInfoEnabled()) {
	    		log.info("Vaciado no tiene correccion monetaria...");
	    	}
	   }
	   
	   return ctx;
    }
    
    /**
     * Crea instancia de motor de c&aacute;lculo para el vaciado dado
     * @param vaciado
     * @return
     */
    private MotorCalculo createMotorCalculo(Vaciado vaciado) {
    	MotorCalculo motor = null;
    	if (DEFAULT_NUEVA_VERSION_MOTOR || vaciado != null && vaciado.isPlanCuentaEspecial()) {
    		motor = new MotorCalculo2Impl();
    	}
    	else {
    		motor = new MotorCalculoImpl();
    	}
		
		return motor;
	}
    
    /**
     * Crea instancia de motor de c&aacute;lculo.
     * @param vaciado
     * @return
     */
    private MotorCalculo createMotorCalculo() {
    	MotorCalculo motor = null;
    	if (DEFAULT_NUEVA_VERSION_MOTOR) {
    		motor = new MotorCalculo2Impl();
    	}
    	else {
    		motor = new MotorCalculoImpl();
    	}
		return motor;
	}
    
    /**
     * @return Instancia de motor de c&aacute;lculo. Si no existe, primero la crea.
     */
    private MotorCalculo getMotor() {
    	if (motor == null) {
    		motor = createMotorCalculo();
    	}
    	return motor;
    }

	/**
     * Metodo especial para crear el contexto de la proyeccion larga y corta
     * S
     * @param idVaciado
     * @param ctx
     */
    
    private void crearContextoCalculoProyeccion(Long idVaciado, Map ctx) {
    	GestorVaciados gestor 	= new GestorVaciadosImpl();
    	Vaciado	periodoX3		= null;
    	Vaciado	periodoX2		= null;
    	Vaciado	periodoX1		= null;
    	Vaciado	periodoX		= null;
    	Vaciado	periodoP		= null;
//    	BigDecimal tipoCambioPromedioX2 = new BigDecimal(1);
//    	BigDecimal tipoCambioPromedioX1 = new BigDecimal(1);
//    	BigDecimal tipoCambioPromedioX  = new BigDecimal(1);
    	Boolean	isVacActualCLP	= Boolean.TRUE;
    	if (this.getContextoApplication() == null || this.getContextoApplication().get(ConstantesSEFE.CTX_CALCULAR_PROYECCION) == null) {
    		// no es necesario cargar en contexto la informacion para la proyecion corta o larga
    		return;
    	}
    	Long objectX3 	= (Long) this.getContextoApplication().get(CTX_PERIODO_X_3);
    	Long objectX2 	= (Long) this.getContextoApplication().get(CTX_PERIODO_X_2);
    	Long objectX1 	= (Long) this.getContextoApplication().get(CTX_PERIODO_X_1);
    	Long objectX 	= (Long) this.getContextoApplication().get(CTX_PERIODO_X);
    	//ConversorMoneda conversor = new ConversorMonedaImpl();
    	periodoP 		= gestor.buscarVaciado(idVaciado);
        // si el vaciado actual usa moneda CLP entonces se debe calcular el IPC para los vaciados anterior/equivalente
        // independientemente si usan moneda extranjera
    	if (objectX3 != null) {
    		periodoX3 		= gestor.buscarVaciado(objectX3);
    		ctx.put(periodoX3.getIdVaciado().toString()+ ConstantesSEFE.UNDERLINE +NUM_MESES, periodoX3.getMesesPer());
    	}
    	if (objectX2 != null) {
    		periodoX2 		= gestor.buscarVaciado(objectX2);
    		ctx.put(periodoX2.getIdVaciado().toString()+ ConstantesSEFE.UNDERLINE +NUM_MESES, periodoX2.getMesesPer());
    		// tipo cammbio promedio para x2
    	//	tipoCambioPromedioX2 = conversor.obtenerTipoCambioPromedio(periodoX2.getIdMoneda(), periodoX2.getUnidMedida(), periodoP.getIdMoneda(), periodoP.getUnidMedida(), periodoX2.getPeriodo());
    	}
    	if (objectX1 != null) {
    		// tipo cammbio promedio para x2
    		periodoX1 		= gestor.buscarVaciado(objectX1);
    		ctx.put(periodoX1.getIdVaciado().toString()+ ConstantesSEFE.UNDERLINE +NUM_MESES, periodoX1.getMesesPer());
    		//tipoCambioPromedioX1 = conversor.obtenerTipoCambioPromedio(periodoX1.getIdMoneda(), periodoX1.getUnidMedida(), periodoP.getIdMoneda(), periodoP.getUnidMedida(), periodoX1.getPeriodo());
    	}
    	if (objectX != null) {
    		// tipo cammbio promedio para x2
    		periodoX 		= gestor.buscarVaciado(objectX);
            ctx.put(periodoX.getIdVaciado().toString()+ ConstantesSEFE.UNDERLINE +NUM_MESES, periodoX.getMesesPer());
    //        tipoCambioPromedioX = conversor.obtenerTipoCambioPromedio(periodoX.getIdMoneda(), periodoX.getUnidMedida(), periodoP.getIdMoneda(), periodoP.getUnidMedida(), periodoX.getPeriodo());
    	}
   		
   		BigDecimal factorCorreccionX2 	= calcularFactorCorreccionMonedaUnidad(periodoX2, periodoP);
   		BigDecimal factorCorreccionX1 	= calcularFactorCorreccionMonedaUnidad(periodoX1, periodoP);
   		BigDecimal factorCorreccionX 	= calcularFactorCorreccionMonedaUnidad(periodoX, periodoP);

   		ctx.put(K_MON_UNI_X2, factorCorreccionX2);
   	  	ctx.put(K_MON_UNI_X1, factorCorreccionX1);
   	  	ctx.put(K_MON_UNI_X, factorCorreccionX);
   	  	
   	  	//ctx.put(K_MON_UNI_PROM_X2, tipoCambioPromedioX2);
	  	//ctx.put(K_MON_UNI_PROM_X1, tipoCambioPromedioX1);
	  	//ctx.put(K_MON_UNI_PROM_X, tipoCambioPromedioX);
	  	
    	if (!ConstantesSEFE.ID_CLASIF_MONEDA_CLP.equals(periodoP.getIdMoneda())) {
        	isVacActualCLP = Boolean.FALSE;
        }
    	if (periodoX3 != null) {
			ctx.put(ID_VAC_PERIODO_X3, periodoX3);
			Date fechaPeriodoX3 = periodoX3.getPeriodo();
			
			Map mapCuentas = crearMapCtasVaciado(periodoX3.getIdVaciado());
			ctx.putAll(mapCuentas);
			
			// se determina si el vaciado anterior es ajustado
			if (periodoX3.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()) {
				ctx.put(periodoX3.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, periodoX3.getAjustadoFlg());
			}
			
			Double ipcPeriodoX3 = buscarIPC(periodoX3, fechaPeriodoX3, isVacActualCLP);
			//Double ipcPeriodoX3 = new Double(1);
			ctx.put(periodoX3.getIdVaciado().toString()+ ConstantesSEFE.UNDERLINE +IPC_PERIODO_X3, ipcPeriodoX3);
			if (log.isInfoEnabled()) {
				log.info("Vac periodoX2="+ periodoX3.getIdVaciado());
				log.info("IPC periodoX2="+ipcPeriodoX3);
			 }
		  	
		}
		if (periodoX2 != null) {
			ctx.put(ID_VAC_PERIODO_X2, objectX2);
			Date fechaPeriodoX2 = periodoX2.getPeriodo();
			
			Map mapCuentas = crearMapCtasVaciado(periodoX2.getIdVaciado());
			ctx.putAll(mapCuentas);
			
			// se determina si el vaciado anterior es ajustado
			if (periodoX2.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()) {
				ctx.put(periodoX2.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, periodoX2.getAjustadoFlg());
			}
			
			Double ipcPeriodoX2 = buscarIPC(periodoX2, fechaPeriodoX2, isVacActualCLP);
			//Double ipcPeriodoX2 =  new Double(99.81);
			ctx.put(periodoX2.getIdVaciado().toString()+ ConstantesSEFE.UNDERLINE +IPC_PERIODO_X2, ipcPeriodoX2);
			if (log.isInfoEnabled()) {
				log.info("Vac periodoX2="+ periodoX2.getIdVaciado());
				log.info("IPC periodoX2="+ipcPeriodoX2);
			 }
		  	
		}
		if (periodoX1 != null) {
			ctx.put(ID_VAC_PERIODO_X1, objectX1);
			Date fechaPeriodoX1 = periodoX1.getPeriodo();
			
			Map mapCuentas = crearMapCtasVaciado(periodoX1.getIdVaciado());
			ctx.putAll(mapCuentas);
			
			// se determina si el vaciado anterior es ajustado
			if (periodoX1.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()) {
				ctx.put(periodoX1.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, periodoX1.getAjustadoFlg());
			}
			
			Double ipcPeriodoX1 = buscarIPC(periodoX1, fechaPeriodoX1, isVacActualCLP);
			//Double ipcPeriodoX1 =  new Double(102.35);
			ctx.put(periodoX1.getIdVaciado().toString()+ ConstantesSEFE.UNDERLINE +IPC_PERIODO_X1, ipcPeriodoX1);
			if (log.isInfoEnabled()) {
				log.info("Vac periodoX1="+ periodoX1.getIdVaciado());
				log.info("IPC periodoX1="+ipcPeriodoX1);
			 }
		  	
		}
		if (periodoX != null) {
			ctx.put(ID_VAC_PERIODO_X, objectX);
			Date fechaPeriodoX = periodoX.getPeriodo();
			
			Map mapCuentas = crearMapCtasVaciado(periodoX.getIdVaciado());
			ctx.putAll(mapCuentas);
			
			// se determina si el vaciado anterior es ajustado
			if (periodoX.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()) {
				ctx.put(periodoX.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, periodoX.getAjustadoFlg());
			}
			
			Double ipcPeriodoX = buscarIPC(periodoX, fechaPeriodoX, isVacActualCLP);
			//Double ipcPeriodoX = new Double(1);
			ctx.put(periodoX.getIdVaciado().toString()+ ConstantesSEFE.UNDERLINE +IPC_PERIODO_X, ipcPeriodoX);
			if (log.isInfoEnabled()) {
				log.info("Vac periodoX="+ periodoX.getIdVaciado());
				log.info("IPC periodoX="+ipcPeriodoX);
			 }
		  	
		}
		if (periodoP != null) {
			ctx.put(ID_VAC_PERIODO_P, idVaciado);
	
			Map mapCuentas = crearMapCtasVaciado(periodoP.getIdVaciado());
			ctx.putAll(mapCuentas);
			
			// se determina si el vaciado anterior es ajustado
			if (periodoP.getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()) {
				ctx.put(periodoP.getIdVaciado().toString() + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO, periodoP.getAjustadoFlg());
			}
			// no se calcula el ipc porque es un periodo futuro
			Double ipcPeriodoP =  new Double(1);
			ctx.put(periodoP.getIdVaciado().toString()+ ConstantesSEFE.UNDERLINE +IPC_PERIODO_P, ipcPeriodoP);
			if (log.isInfoEnabled()) {
				log.info("Vac periodoP="+ periodoP.getIdVaciado());
				log.info("IPC periodoP="+ipcPeriodoP);
			 }
		  	
		}
    	
    }
    
    private Map crearContextoCalculoProyeccionSinVaciados(Long idVaciado, ProyeccionLargaFinal proyeccionFinal) {

    	Integer pivote = (Integer) this.obtenerValorContexto(ConstantesSEFE.PERIODO_EVALUACION_FORMULA);
    	Long idPrimerVacProy = (Long) this.obtenerValorContexto(ConstantesSEFE.CTX_PRIMER_ID_VAC_PROYECCION);
    	

    	PeriodoProyeccionLarga	periodoP		= (PeriodoProyeccionLarga) proyeccionFinal.getPeriodos().get(pivote.intValue());
        Map ctx 								= new HashMap(); 
        	ctx.put(ID_VAC_ACTUAL, idVaciado); 
			ctx.put(ID_VAC_PERIODO_P, idPrimerVacProy);
			ctx.put(NUM_MESES, periodoP.getVaciado().getMesesPer());
			Date fechaPeriodoP = periodoP.getFechaCierre();			
			Map mapCuentas = crearMapCtasVaciado(idVaciado, this.getCuentasVaciado());
			ctx.putAll(mapCuentas);			
			// se determina si el vaciado anterior es ajustado
			if (periodoP.getVaciado().getAjustadoFlg().intValue() == ConstantesSEFE.FLAG_VACIADO_AJUSTADO.intValue()) {
				ctx.put(idVaciado + ConstantesSEFE.UNDERLINE + ConstantesSEFE.STR_FLAG_VACIADO_AJUSTADO,idVaciado);
			}			
			Double ipcPeriodo = new Double(1);
			ctx.put(idVaciado.toString()+ ConstantesSEFE.UNDERLINE +IPC_PERIODO_P, ipcPeriodo);
			if (log.isInfoEnabled()) {
				log.info("Vac periodoP="+ idVaciado);
				log.info("IPC periodoP="+fechaPeriodoP);
			 }
		return ctx;
    	
    }
    
    
   
    /**
     * metodo que recupera el contexto aplicativo
     * @return
     */
    private Map getContextoApplication() {
		return this.contextoAplicativo;
	}

	/*
     * Crea un mapa de cuentas indexadas por ID_VAC + "_" + cod_cuenta
     */
    private Map crearMapCtasVaciado(Long idVaciado) {
    	Map mapCtas = new HashMap();
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	String strIdVac = idVaciado.toString() + ConstantesSEFE.UNDERLINE;

		Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
		Object[] arrayCtas = cuentas.toArray();
		for (int i = 0; i < cuentas.size(); ++i) {
			Cuenta cta = (Cuenta) arrayCtas[i];
			String key = new StringBuffer(strIdVac).append(cta.getCodigoCuenta()).toString();
			
			mapCtas.put(key, cta);
		}
    	
		return mapCtas;
	}
    
    private Map crearMapCtasVaciadoCons(Long idVaciado) {
    	Map mapCtas = new HashMap();
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	String strIdVac = idVaciado.toString() + ConstantesSEFE.UNDERLINE;

		Collection cuentas = gestor.buscarCuentasVaciadoCons(idVaciado);
		Object[] arrayCtas = cuentas.toArray();
		for (int i = 0; i < cuentas.size(); ++i) {
			Cuenta cta = (Cuenta) arrayCtas[i];
			String key = new StringBuffer(strIdVac).append(cta.getCodigoCuenta()).toString();
			
			mapCtas.put(key, cta);
		}
    	
		return mapCtas;
	}
    
    /*
     * Crea un mapa de cuentas indexadas por ID_VAC + "_" + cod_cuenta
     */
    private Map crearMapCtasHibridasVaciado(Long idVaciado) {
    	Map mapCtas = new HashMap();
    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
    	String strIdVac = idVaciado.toString() + ConstantesSEFE.UNDERLINE;

		Collection cuentas = gestor.buscarCuentasHibridasVaciado(idVaciado);
		Object[] arrayCtas = cuentas.toArray();
		for (int i = 0; i < cuentas.size(); ++i) {
			Cuenta cta = (Cuenta) arrayCtas[i];
			String key = new StringBuffer(strIdVac).append(cta.getCodigoCuenta()).toString();
			
			mapCtas.put(key, cta);
		}
    	
		return mapCtas;
	}
	
    /*
     * Crea un mapa de cuentas indexadas por ID_VAC + "_" + cod_cuenta
     */
    private Map crearMapCtasVaciado(Long idVaciado, Collection cuentas) {
    	Map mapCtas = new HashMap();
    	String strIdVac = idVaciado.toString() + ConstantesSEFE.UNDERLINE;
		Object[] arrayCtas = cuentas.toArray();
		for (int i = 0; i < cuentas.size(); ++i) {
			Cuenta cta = (Cuenta) arrayCtas[i];
			String key = new StringBuffer(strIdVac).append(cta.getCodigoCuenta()).toString();			
			mapCtas.put(key, cta);
		}    	
		return mapCtas;
	}

	    
    
    /**
     * Invoca el servicio de consulta de indicadores economicas mensuales
     * para obtener el IPC del periodo del vaciado
     * 
     * @param periodo - fecha del período de consulta
     * @return - el valor del indicador IPC para el mes/ano del periodo consultado
     */
    private Double buscarIPC(Vaciado vac, Date periodo, Boolean isVacActualCLP) {
    	Double ipc = new Double("1.0");

		// Si la moneda del vaciado ES CLP, se busca el IPC para el periodo y se utiliza para el calculo.
    	// En caso de ser moneda extranjera (se incluye UF) se asume IPC = 1 ya que no se utiliza para el calculo.
    	// si el vaciado actual es CLP entonces se debe calcular el IPC independientemente de la moneda para los vaciados prev/equiv
		//if (ConstantesSEFE.ID_CLASIF_MONEDA_CLP.equals(vac.getIdMoneda()) || isVacActualCLP.booleanValue()) {
    	if (isVacActualCLP.booleanValue()) {
			try {
				ConsultaServicios servicios = new ConsultaServiciosImplCache();

				// se modifica el mes, restando uno del período actual
				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTime(periodo);
				calendar.add(Calendar.MONTH, -1);

				ipc = servicios.consultaIPC(calendar.getTime());
			} catch (Exception e) {
				log.info("Error en consulta IPC", e);
				log.info("NO FUE POSIBLE OBTENER EL IPC DESDE EL SERVICIO RESUMEN INDICADORES ECONOMICOS");
				log.info("CUENTAS QUE UTILICEN IPC NO SERAN CALCULADAS");
				ipc = null;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Vaciado id: {0} || Moneda del Vaciado: {1} || IPC: {2}", new Object[] { vac.getIdVaciado(), vac.getIdMoneda(), ipc }));
		}

		return ipc;
    }
    
    public boolean tieneCorrecionMonetaria(Long idVac) {
    	ServicioVaciados servicio = new ServicioVaciadosImpl();
    	return servicio.tieneCorreccionMonetaria(idVac).getBooleanValue().booleanValue();
    }


	public void setContextoCalculo(Map contextoCalculo) {
		this.contextoCalculo = contextoCalculo;
	}


	public Map getContextoCalculo() {
		return contextoCalculo;
	}

	public Collection getCuentasVaciado() {		
		return cuentasVaciado;
	}
	
	

	public void setCuentasVaciado(Collection cuentas) {
		this.cuentasVaciado = cuentas;
	}
	
	private void actualizarPorcentaje(Cuenta cta) {
		try {
			Cuenta num = (Cuenta) this.getContextoCalculo().get(this.getNumerador(cta.getFormulaRatio()));
			Cuenta den = (Cuenta) this.getContextoCalculo().get(this.getDenominador(cta.getFormulaRatio()));
			
			Double mtoNum = obtenerMontoParaPorcentajes(num);
			Double mtoDen = obtenerMontoParaPorcentajes(den);
			
			if (mtoDen != null && mtoDen.doubleValue() != 0.0) {
				BigDecimal ratio = new BigDecimal(mtoNum.doubleValue() / mtoDen.doubleValue() * 100.00).setScale(2, BigDecimal.ROUND_HALF_UP);
				cta.setRatio(new Double(ratio.doubleValue()));
				
				if (log.isDebugEnabled()) {
					log.debug(MessageFormat.format("* * * Cta {0} {1} %{2} * * *", new String[]{cta.getCodigoCuenta(), cta.getFormulaRatio(), cta.getRatio().toString()}));
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("No se puede determinar porcentaje de participacion de la cuenta " + cta.getCodigoCuenta() + " causado por denominador null");
				}
			}

		} catch (Exception ex) {
			log.info("Error al calcular porcentaje de participacion de la cuenta " + cta.getCodigoCuenta() + " casusado por " + ex.getMessage());
		}
	}
	
	public String getNumerador(String fmla) {
		int posOper = fmla.indexOf(ConstantesSEFE.SLASH);
		
		return fmla.substring(0, posOper).trim();
	}
	
	public String getDenominador(String fmla) {
		int posOper = fmla.indexOf(ConstantesSEFE.SLASH);
		
		return fmla.substring(posOper + 1).trim();
	}

	public List calcularPorcentajes(Collection cuentas) {
		return calcularPorcentajes(cuentas, false);
	}
	
	public List calcularPorcentajes(Collection cuentas, boolean esAjustado) {
        // crear el contexto de calculo
        Map ctx = new HashMap();

        // se pone el glag ajustado en el contexto de calculo
        if (esAjustado) {
        	ctx.put(VACIADO_AJUSTADO, ConstantesSEFE.FLAG_VACIADO_AJUSTADO);
        } 
        
        // todas la cuentas se indexan por codigo de cuenta
        Iterator iterator = cuentas.iterator();
        while (iterator.hasNext()) {
            Cuenta cta = (Cuenta) iterator.next();
            ctx.put(cta.getCodigoCuenta(), cta);
        }
        
        // se sobreescribe el contexto de calculo con las cuentas
        this.setContextoCalculo(ctx);
        
        iterator = cuentas.iterator();
        while (iterator.hasNext()) {
        	Cuenta cta = (Cuenta) iterator.next();
        	
        	if (cta.getFormulaRatio() == null) {
        		continue;
        	}
        	
        	this.actualizarPorcentaje(cta);
        }
        
		return new ArrayList(cuentas);
	}
	
	private Double obtenerMontoParaPorcentajes(Cuenta cta) {
		Double mto = null;
		double ajuste = 0.0;
		
		if (cta != null && cta.getMonto() != null) {
			mto = cta.getMonto();
		}

		if (FLAG_VACIADO_AJUSTADO.equals(getContextoCalculo().get(VACIADO_AJUSTADO))) {
			// se recupera el ajuste de la cuenta
			try {
				ajuste = cta.getAjuste().doubleValue();
			} catch (Exception ex) {
				if (log.isDebugEnabled()) {
					log.debug("El ajuste de la cuenta es 0");
				}
			}
			
			// si el monto es nulo entonces es igual al ajuste
			if (mto == null) {
				mto = new Double(ajuste);
			} else {
				// en caso contrario se le suma el ajuste
				mto = new Double(mto.doubleValue() + ajuste);
			}
		}
		
		return mto;
	}
	/**
	 * metodo que calcula el factor correccion moneda unidad.
	 * 
	 * @param vacAnterior - vaciado anterior o equivalente
	 * @param vacActual - vaciado actual
	 * @return
	 */
	public BigDecimal calcularFactorCorreccionMonedaUnidad(Vaciado vacAnterior, Vaciado vacActual)
	{
		ServicioVaciados servicioVaciados = new ServicioVaciadosImpl();
        GestorClasificaciones gestorClasificacion 		= new GestorClasificacionesImpl();
        TipoCambio 	tipoCambioVaciadoActual				= null;
        TipoCambio 	tipoCambioVaciadoAnterior			= null;
		 // 1.	Crear instancia Servicio Consulta 
        ConsultaServicios consultaServicios 			= new ConsultaServiciosImplCache();
        // 2.	Hacer factor cambio moneda vaciado actual Tc0 igual a 1 
        Double	factorCambioVacActual						= new Double(1);
        // 3.	Hacer factor cambio moneda vaciado anterior Tc1 igual a 1 
        Double  factorCambioVacAnterior					= new Double(1);
        
        Clasificacion clasificacionVaciadoActual=gestorClasificacion.buscarClasificacionPorId(vacActual.getIdMoneda()) ;
        // 4. verificar si la moneda es diferente a CLP
        if ( (vacAnterior != null && !vacActual.getIdMoneda().equals(vacAnterior.getIdMoneda())))
        {
        	Date fechaConsultaCambioVacActual = servicioVaciados.buscarDiaHabilSiguiente(vacAnterior.getPeriodo());
	        if (clasificacionVaciadoActual != null && !clasificacionVaciadoActual.getIdClasif().equals(ConstantesSEFE.CLASIF_ID_TPO_MONEDA_CLP  ))
	        {	
	        	//tipoCambioVaciadoActual = consultaServicios.consultaTipoCambio(fechaConsultaCambioVacActual, clasificacionVaciadoActual.getCodigo());  
	        	tipoCambioVaciadoActual = consultaServicios.consultaTipoCambio(fechaConsultaCambioVacActual, clasificacionVaciadoActual.getCodigo());
	        	factorCambioVacActual = tipoCambioVaciadoActual.getValorObservado();
	        }
        }
        //  5.	Hacer factor cambio unidad vaciado actual Ku igual a 1 OK
        BigDecimal factorCambioUnidadVacActual		   	= new BigDecimal(1);
        Clasificacion clasificacionVaciadoAnterior 		= null;
        // 6.	Si existe vaciado anterior/equivalente
        if (vacAnterior != null) {	
        	clasificacionVaciadoAnterior = gestorClasificacion.buscarClasificacionPorId(vacAnterior.getIdMoneda()) ;
        	// 6.b.	Si (unidad vaciado anterior DISTINTO unidad vaciado actual)
        	if ( vacActual.getUnidMedida().doubleValue() != vacAnterior.getUnidMedida().doubleValue())
        	{
        		BigDecimal fcuVac1 = new BigDecimal(ConfigManager.getValueAsString(ConstantesSEFE.KEY_FACTOR_CONVERSION_UNIDAD+ConstantesSEFE.PUNTO+vacAnterior.getUnidMedida().intValue()));
        		BigDecimal fcuVac0 = new BigDecimal(ConfigManager.getValueAsString(ConstantesSEFE.KEY_FACTOR_CONVERSION_UNIDAD+ConstantesSEFE.PUNTO+vacActual.getUnidMedida().intValue()));
        		//i.	Ku = Unidad Vac Anterior / Unidad Vac Actual
        		factorCambioUnidadVacActual = new BigDecimal(fcuVac1.floatValue() / fcuVac0.floatValue()); 
        		if (log.isDebugEnabled())
        	    {
        			log.debug("ku= UnidaVacAnterior("+fcuVac1.floatValue()+")/UnidadVacActual("+ fcuVac0.floatValue()+")="+ factorCambioUnidadVacActual);
        	    }
        		
        	}
        }
        //	6.a.	Si (moneda vaciado anterior NO es CLP) entonce 	consultar tipo cambio moneda vaciado anterior
       if (clasificacionVaciadoAnterior != null && !clasificacionVaciadoAnterior.getIdClasif().equals(ConstantesSEFE.CLASIF_ID_TPO_MONEDA_CLP))
    	   // if (clasificacionVaciadoAnterior != null && !clasificacionVaciadoActual.getIdClasif().equals(ConstantesSEFE.CLASIF_ID_TPO_MONEDA_CLP))
        {
        	if ( !vacActual.getIdMoneda().equals(vacAnterior.getIdMoneda()))
        	{
	        	// i consultar tipo cambio moneda vaciado anterior/equivalente
	        	Date fechaConsultaCambioVacAnt = servicioVaciados.buscarDiaHabilSiguiente(vacAnterior.getPeriodo());
	        	//tipoCambioVaciadoAnterior = consultaServicios.consultaTipoCambio(fechaConsultaCambioVacAnt, clasificacionVaciadoActual.getCodigo()); 
	        	tipoCambioVaciadoAnterior = consultaServicios.consultaTipoCambio(fechaConsultaCambioVacAnt, clasificacionVaciadoAnterior.getCodigo());
	        	// ii.	Asignar tipo cambio a Tc1
	        	factorCambioVacAnterior=tipoCambioVaciadoAnterior.getValorObservado();
        	}
        }
        // 7.	Hacer Km = Tc1 / Tc0
        BigDecimal km = new BigDecimal( factorCambioVacAnterior.doubleValue()/factorCambioVacActual.doubleValue());
       // 8.	Hacer factor correccion moneda/unidad FC = Km x Ku
       BigDecimal  factorCorrecionMonedaUnidad	= new BigDecimal( km.floatValue() * factorCambioUnidadVacActual.floatValue());
       // 9.	retorna el factor correccion moneda/unidad  
       if (log.isDebugEnabled())
       {
    	   log.debug("KM= TC1("+factorCambioVacAnterior.doubleValue()+")/TC0("+ factorCambioVacActual.doubleValue()+")="+ km);
    	   log.debug("FC=km("+ km+") x ku("+factorCambioUnidadVacActual+") ="+factorCorrecionMonedaUnidad.floatValue());
       }
       return factorCorrecionMonedaUnidad;
	}
	
	
	/**
	 * metodo que calcula el factor correccion moneda unidad.
	 * 
	 * @param vacAnterior - vaciado anterior o equivalente
	 * @param vacActual - vaciado actual
	 * @return
	 */
	public BigDecimal calcularFactorCorreccionMonedaUnidadEvoTrimestral(Vaciado vacAnterior, Vaciado vacActual)
	{
		ServicioVaciados servicioVaciados = new ServicioVaciadosImpl();
        GestorClasificaciones gestorClasificacion 		= new GestorClasificacionesImpl();
        TipoCambio 	tipoCambioVaciadoActual				= null;
        TipoCambio 	tipoCambioVaciadoAnterior			= null;
		 // 1.	Crear instancia Servicio Consulta 
        ConsultaServicios consultaServicios 			= new ConsultaServiciosImplCache();
        // 2.	Hacer factor cambio moneda vaciado actual Tc0 igual a 1 
        Double	factorCambioVacActual						= new Double(1);
        // 3.	Hacer factor cambio moneda vaciado anterior Tc1 igual a 1 
        Double  factorCambioVacAnterior					= new Double(1);
        
        Clasificacion clasificacionVaciadoActual=gestorClasificacion.buscarClasificacionPorId(vacActual.getIdMoneda()) ;
        // 4. verificar si la moneda es diferente a CLP
        if ( (vacAnterior != null && !vacActual.getIdMoneda().equals(vacAnterior.getIdMoneda())))
        {
        	Date fechaConsultaCambioVacActual = servicioVaciados.buscarDiaHabilSiguiente(vacAnterior.getPeriodo());
	        if (clasificacionVaciadoActual != null && !clasificacionVaciadoActual.getIdClasif().equals(ConstantesSEFE.CLASIF_ID_TPO_MONEDA_CLP  ))
	        {	
	        	//tipoCambioVaciadoActual = consultaServicios.consultaTipoCambio(fechaConsultaCambioVacActual, clasificacionVaciadoActual.getCodigo());  
	        	tipoCambioVaciadoActual = consultaServicios.consultaTipoCambio(fechaConsultaCambioVacActual, clasificacionVaciadoActual.getCodigo());
	        	factorCambioVacActual = tipoCambioVaciadoActual.getValorObservado();
	        }
        }
        //  5.	Hacer factor cambio unidad vaciado actual Ku igual a 1 OK
        BigDecimal factorCambioUnidadVacActual		   	= new BigDecimal(1);
        Clasificacion clasificacionVaciadoAnterior 		= null;
        // 6.	Si existe vaciado anterior/equivalente
        if (vacAnterior != null) {	
        	clasificacionVaciadoAnterior = gestorClasificacion.buscarClasificacionPorId(vacAnterior.getIdMoneda()) ;
        	// 6.b.	Si (unidad vaciado anterior DISTINTO unidad vaciado actual)
        	if ( vacActual.getUnidMedida().doubleValue() != vacAnterior.getUnidMedida().doubleValue())
        	{
        		BigDecimal fcuVac1 = new BigDecimal(ConfigManager.getValueAsString(ConstantesSEFE.KEY_FACTOR_CONVERSION_UNIDAD+ConstantesSEFE.PUNTO+vacAnterior.getUnidMedida().intValue()));
        		BigDecimal fcuVac0 = new BigDecimal(ConfigManager.getValueAsString(ConstantesSEFE.KEY_FACTOR_CONVERSION_UNIDAD+ConstantesSEFE.PUNTO+vacActual.getUnidMedida().intValue()));
        		//i.	Ku = Unidad Vac Anterior / Unidad Vac Actual
        		factorCambioUnidadVacActual = (new BigDecimal(fcuVac1.floatValue() / fcuVac0.floatValue())).setScale(8, BigDecimal.ROUND_HALF_UP); 
        		if (log.isInfoEnabled())
        	    {
        			log.info("ku= UnidaVacAnterior("+fcuVac1.floatValue()+")/UnidadVacActual("+ fcuVac0.floatValue()+")="+ factorCambioUnidadVacActual);
        	    }
        		
        	}
        }
        //	6.a.	Si (moneda vaciado anterior NO es CLP) entonce 	consultar tipo cambio moneda vaciado anterior
       if (clasificacionVaciadoAnterior != null && !clasificacionVaciadoAnterior.getIdClasif().equals(ConstantesSEFE.CLASIF_ID_TPO_MONEDA_CLP))
    	   // if (clasificacionVaciadoAnterior != null && !clasificacionVaciadoActual.getIdClasif().equals(ConstantesSEFE.CLASIF_ID_TPO_MONEDA_CLP))
        {
        	if ( !vacActual.getIdMoneda().equals(vacAnterior.getIdMoneda()))
        	{
	        	// i consultar tipo cambio moneda vaciado anterior/equivalente
	        	Date fechaConsultaCambioVacAnt = servicioVaciados.buscarDiaHabilSiguiente(vacActual.getPeriodo());
	        	//tipoCambioVaciadoAnterior = consultaServicios.consultaTipoCambio(fechaConsultaCambioVacAnt, clasificacionVaciadoActual.getCodigo()); 
	        	tipoCambioVaciadoAnterior = consultaServicios.consultaTipoCambio(fechaConsultaCambioVacAnt, clasificacionVaciadoAnterior.getCodigo());
	        	// ii.	Asignar tipo cambio a Tc1
	        	factorCambioVacAnterior=tipoCambioVaciadoAnterior.getValorObservado();
        	}
        }
        // 7.	Hacer Km = Tc1 / Tc0
        BigDecimal km = (new BigDecimal( factorCambioVacAnterior.doubleValue()/factorCambioVacActual.doubleValue())).setScale(5, BigDecimal.ROUND_HALF_UP);
       // 8.	Hacer factor correccion moneda/unidad FC = Km x Ku
       BigDecimal  factorCorrecionMonedaUnidad	= (new BigDecimal( km.floatValue() * factorCambioUnidadVacActual.floatValue())).setScale(8, BigDecimal.ROUND_HALF_UP);
       // 9.	retorna el factor correccion moneda/unidad  
       if (log.isDebugEnabled())        {
    	   log.debug("KM= TC1("+factorCambioVacAnterior.doubleValue()+")/TC0("+ factorCambioVacActual.doubleValue()+")="+ km);
    	   log.debug("FC=km("+ km+") x ku("+factorCambioUnidadVacActual+") ="+factorCorrecionMonedaUnidad.floatValue());
       }
       return factorCorrecionMonedaUnidad;
	}

	public void calcularIndicadoresRatingFinanciero(Long idVaciado, boolean esProyectado) {
		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Iniciando el calculo de cuentas de rating financiero para el vaciado [{0}]", new Object[] { idVaciado }));
		}

		// Se obtienen las cuentas del vaciado, si las cuentas no tienen valor en la base las trae con null
		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
		Collection cuentas = gestor.buscarCuentasVaciado(idVaciado, null);
		this.setCuentasVaciado(cuentas);
		
		Integer tipoCuentaInd;
		if (esProyectado) {
			tipoCuentaInd = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_IND_RATING_PROYECCTADO);
		} else {
			tipoCuentaInd = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_IND_RATING_FINANCIERO);
		}

		this.calcularCuentas(idVaciado, tipoCuentaInd);
	}
	
	  /**
     * metodo que calcula las cuentas de los tres periodos historicos de la proyeccion
     */
    public void calcularCuentasProyeccionLarga(Long idVaciado) {
    	if (log.isDebugEnabled()) {
            log.debug("Iniciando el calculo de cuentas por tipo para vaciado " + idVaciado);
        }    	
    	Integer periodoPivote = (Integer) this.obtenerValorContexto(ConstantesSEFE.PERIODO_EVALUACION_FORMULA);
		if (logMotor.isInfoEnabled()) {
			logMotor.info("Calculando el periodo ="+periodoPivote.intValue());
		}
    	if (!esModoProyeccion().booleanValue()) {
	    	GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
	    	Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
	    	this.setCuentasVaciado(cuentas);
	    	this.calcularCuentas(idVaciado, ConstantesSEFE.CUENTA_PROYLARGA_X2);	
	    	this.setCuentasVaciado(cuentas);
	    	this.calcularCuentas(idVaciado, ConstantesSEFE.CUENTA_PROYLARGA_X1);
	    	this.setCuentasVaciado(cuentas);
	    	this.calcularCuentas(idVaciado, ConstantesSEFE.CUENTA_PROYLARGA_X);
	    	this.setCuentasVaciado(cuentas);
	    	this.calcularCuentas(idVaciado, ConstantesSEFE.CLASIF_ID_TPO_PROYECCION_LARGA);
    	}else {
	    	this.calcularCuentasProyectadas(idVaciado, ConstantesSEFE.CLASIF_ID_TPO_PROYECCION_LARGA);
    	}
	}
    
    private Object obtenerValorContexto( String key) {
    	Object object = null;
    	if (this.getContextoApplication() != null) {
    		object = this.getContextoApplication().get(key);
    	}
    	if (object == null && this.getContextoCalculo()!= null ) {
    		object =this.getContextoCalculo().get(key);
    	}
    	return object;
    }
	/**
	 * Metodo que permite calcular las cuentas proyectadas desde la tabla de proyecciones
	 * Para realizar el calculo, primero se debe convertir los datos a objetos cuentas.
	 * @param idVaciado
	 * @param mapaCtasProyectadas
	 * @param proyecciones
	 */
    public void calcularCuentasProyectadas(Long idVaciado, Integer tipoCuenta) {
		GestorProyeccionLarga proyeccion = new GestorProyeccionLargaImpl();
		
		Long idPrimerVacProy = (Long) this.obtenerValorContexto(ConstantesSEFE.CTX_PRIMER_ID_VAC_PROYECCION);

		ProyeccionLargaFinal proyeccionFinal = proyeccion.obtenerProyeccionLarga(idPrimerVacProy);

		  // crear el contexto de calculo y lo pasa al motor de calculo
        if (this.getContextoCalculo() == null) {
			Map ctx = this.crearContextoCalculoProyeccionSinVaciados(idVaciado, proyeccionFinal);
	        this.setContextoCalculo(ctx);
        }
        // se valida si existe contexto aplicativo...
        if (this.contextoAplicativo != null && !this.contextoAplicativo.isEmpty()) {
        	this.getContextoCalculo().putAll(this.contextoAplicativo);
        	
        	// se limpia el contexto aplicativo
        	this.contextoAplicativo.clear();
        	this.contextoAplicativo = null;
        }

        // Si el tipo de cuenta se encuentra configurado, para que se realice un orden previo al calculo
        if (ConstantesSEFE.TIPOS_CUENTA_REQUIEREN_ORDEN_CALCULO.contains(tipoCuenta.toString())) {
        	Map mapaCuentas = this.crearMapaCuentas(this.getCuentasVaciado());        
        	this.procesarCuentas(idVaciado, tipoCuenta, mapaCuentas, false, null);
        } else {
        	this.calcularCuentasServicio(idVaciado, tipoCuenta,false,null);
        }
    }
    
    
	public void calcularIndicadoresRating(Long idVaciado) {
		if (log.isDebugEnabled()) {
			log.debug(MessageFormat.format("Iniciando el calculo de cuentas de rating y proyectados para el vaciado [{0}]", new Object[] { idVaciado }));
		}
		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
		Collection cuentas = gestor.buscarCuentasVaciado(idVaciado);
		this.setCuentasVaciado(cuentas);
		Integer  codCtaRating = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_RATING);
		this.setCuentasVaciado(cuentas);
		// null el orden topologico para no dejarlo cargado en memoria
		this.calcularCuentas(idVaciado, ConstantesSEFE.CUENTA_PROY_CORTA_X2);
		this.setCuentasVaciado(cuentas);
		this.calcularCuentas(idVaciado, ConstantesSEFE.CUENTA_PROY_CORTA_X1);
		this.setCuentasVaciado(cuentas);
		this.calcularCuentas(idVaciado, ConstantesSEFE.CUENTA_PROY_CORTA_X);
		this.setCuentasVaciado(cuentas);	
		this.calcularCuentas(idVaciado, codCtaRating);
	}

	
	public void ponerEnContexto(String key, Object val) {
		if (this.getContextoCalculo() != null) {
			this.getContextoCalculo().put(key, val);
		} else {
			if (this.contextoAplicativo == null) {
				this.contextoAplicativo = new HashMap();
			}
			this.contextoAplicativo.put(key, val);
		}
	}
	
	private Map crearMapaCuentas(Collection cuentas) {
		Map mapCuentas = new HashMap();
		
		Iterator iter = cuentas.iterator();
        while (iter.hasNext()) {
        	Cuenta cta = (Cuenta) iter.next();
        	mapCuentas.put(cta.getCodigoCuenta(), cta);
        }
        
        return mapCuentas;
	}
	

	private void procesarCuentas(Long idVaciado, Integer tipoCuenta, Map cuentasVaciado, boolean esRezagado, Integer idHibrido) {
		Map mf = new HashMap();
		Iterator ctaIter = cuentasVaciado.values().iterator();
		while (ctaIter.hasNext()) {
			Cuenta c = (Cuenta) ctaIter.next();
			if (c.getTipoCuenta().intValue() == tipoCuenta.intValue()) {
				mf.put(c.getCodigoCuenta(), c);
			}
		}
		if (mf.isEmpty()) {
			return;
		}
		
		Map pend = new HashMap(mf);
		
		// el grafo de dependencias
		Grafo  grafo = this.crearGrafoCuentas(cuentasVaciado, tipoCuenta);
	
		// el orden de calculo de las cuentas
		List cuentasPorCalcular = new ArrayList();
		List cuentasPendientes	= new ArrayList(pend.values());
		OrdenTopologico orden = null; 
		orden = new OrdenTopologico(grafo);
	
		while (orden.hasNext()) {
			Object cta = orden.next();
			cuentasPorCalcular.add(cta);
			
			// se quitan las cuentas que se procesan ya
			cuentasPendientes.remove(cta);
		}
		
		if (logMotor.isInfoEnabled()) {
			logMotor.info("Iniciando calculo cuentas proyectadas...");
			logMotor.info("Calculando indicadores independientes");
		}
		
		
		this.setCuentasVaciado(cuentasPorCalcular);
		this.calcularCuentasServicio(idVaciado, tipoCuenta, esRezagado,idHibrido);
		
				
		List ciclos = grafo.buscarLoops();
		//List ciclos = grafo.buscarCiclos();
		
		// si no hay ciclos, entonces las cuentas se calculan en su orden natural
		if (ciclos.isEmpty()) {
			this.calcularCuentasServicio(idVaciado, tipoCuenta, esRezagado,idHibrido);
			return;
		} 		
		if (logMotor.isInfoEnabled()) {
			logMotor.info("Se detectan ciclos en el calculo de la proyeccion...");
		}
		
	    for (int i = 0; i < ciclos.size(); ++i) {
	    	
			// si quedan aristas por procesar
			if (grafo.getAristas().size() < 2) {
				continue;
			}
	    	
	    	List ciclo = (List) ciclos.get(i);
	    	Object[] ctas = ciclo.toArray();
			
			if (ctas.length < 2) {
				continue;
			}
	    	
			Cuenta cta0 = (Cuenta) ctas[0];
			Cuenta cta1 = (Cuenta) ctas[1];
			
			if (logMotor.isInfoEnabled()) {
				logMotor.info("Cuenta de referencia: " + cta0);
			}
			
			// se remueve la arista que cierra el ciclo
			grafo.removerArista(cta0, cta1);
	        
			// se re-ordenan despues de romper el loop
			OrdenTopologico orden2 = new OrdenTopologico(grafo);
			
			List nuevoOrden = new ArrayList();
			while (orden2.hasNext()) {
				Object cta = orden2.next();

				nuevoOrden.add(cta);

				// se agrega un extremo extra por la arista rota
				if (cta.equals(cta1)) {
					nuevoOrden.add(cta0);
				}
			}
			
			// si no quedan cuentas por calcular, fin
			if (nuevoOrden.isEmpty())
				continue;
			
			// ahora se remuevan las cuentas del nuevo orden de las cuentas pendientes
			cuentasPendientes.removeAll(nuevoOrden);
			
	        // se inicializa la cuenta de entrada al ciclo
	        double valorAnterior = 0.0;
	        
        	//ctaConvergencia.setMonto(new Double(valorAnterior));
    
	        double valorActual = valorAnterior;
	        
	        // limite para detener el calculo en iteraciones y precision
	        double umbral = ConfigDBManager.getValueAsDouble(ConstantesSEFE.PROPIEDAD_RTG_PROY_UMBRAL_CALCULO).doubleValue();
	        int numIteraciones = ConfigDBManager.getValueAsInteger(ConstantesSEFE.PROPIEDAD_RTG_PROY_NRO_ITERACIONES_CALCULO).intValue();

	        // el grupo de cuentas a calcular
	        this.setCuentasVaciado(nuevoOrden);
	        
	        
	        for ( int loop = 1 ; ; ++loop) {
	        	if (logMotor.isInfoEnabled()) {
	        		logMotor.info("Calculando iteracion #" + loop);
	        	}
	        	
	        	this.calcularCuentasServicio(idVaciado, tipoCuenta, esRezagado,idHibrido);
        	
	        	try {
	        		//valorActual = ctaConvergencia.getMonto().doubleValue()
	        		valorActual = cta0.getMonto().doubleValue();
	        		
	        		if (logMotor.isInfoEnabled()) {
	        			logMotor.info(MessageFormat.format("Valor de la cuenta despues de la iteracion #{0}: {1}", new String[] {String.valueOf(loop), String.valueOf(valorActual)}));
		        	}
	        		
	        		if (Math.abs(valorActual - valorAnterior) <= umbral) {
	        			if (logMotor.isInfoEnabled()) {
	        				logMotor.info(MessageFormat.format("Fin de iteraciones. Precision alcanzada: {0}", new String[] {String.valueOf(Math.abs(valorActual - valorAnterior))}));
	        			}
	        			break;
	        		}
	        		
	        		valorAnterior = valorActual;
	        	} catch (Exception ex) {
	        		if (logMotor.isInfoEnabled()) {
	        			logMotor.info(MessageFormat.format("* * * Valor cuenta de referencia {0}: null * * *", new String[] {cta0.toString()}));
	        		}
	        	}
	        	
	        	if (loop == numIteraciones) {
        			if (logMotor.isInfoEnabled()) {
        				logMotor.info("Se alcanzo el numero max de iteraciones. No hay convergencia en el calculo");
        			}
	        		break;
	        	}
	        }
	    }
	}
	

	

	private Grafo crearGrafoCuentas(Map ctas, Integer tpoCuentas) {
		Grafo grafo = new Grafo();
		
        // se determina el indice del periodo que se está calculando
//        Integer periodo = (Integer) this.getContextoCalculo().get(ConstantesSEFE.PERIODO_EVALUACION_FORMULA);
//        // si no se especifica se usa el periodo a proyectar
//        if (periodo == null) {
//        	periodo = ConstantesSEFE.PERIODO_PROYECTADO;
//        }
		
	       // se evalua la formula correspondiente a un solo periodo
       // int indicePeriodo = periodo.intValue();
        int indicePeriodo = 0;
	
		Object[] cuentas = ctas.values().toArray();
		
		for (int i = 0; i < cuentas.length; ++i) {		
			Cuenta cta = (Cuenta) cuentas[i];
			
			// solo las cuentas del tipo especifico	se procesan
			if (cta.getTipoCuenta().intValue() != tpoCuentas.intValue())
				continue;
			
			// las formuals null se bypasean
			if (cta.getFormula() == null) 
				continue;
			
			// la formual como expresion algebraica
			Expresion exp = new Expresion((String) cta.getFormula());
			if (exp.getTokens() != null && !exp.getTokens().isEmpty()) {
				Token token = (Token) exp.getTokens().toArray()[0];
				if (token instanceof FunctionMultiArgs) {
					exp = ((FunctionMultiArgs) token).getArguments()[indicePeriodo];
				}
			}
			
			// se agrega la cuenta base como vertice
			grafo.agregarVertice(cta);
			
			Iterator it = exp.getDependencies().iterator();
			while (it.hasNext()) {
				Object vtx = it.next();
				
				// si el objeto dependiente es una cuenta se agrega como vertice y arista
				if (ctas.containsKey(vtx)) {
					Object obj = ctas.get(vtx);
					
					grafo.agregarVertice(obj);
					grafo.agregarArista(cta, obj);
				}
			}
		}
		return grafo;
	}
	
	/**
	 * Metodo que valida si la proyeccion larga esta proyectando desde el periodo P1 hacia adelante porque desde ese periodo ya no existen vaciados
	 * @return
	 */
	private Boolean esModoProyeccion() {
		if (this.obtenerValorContexto(ConstantesSEFE.CTX_MODO_PROYECTADO) !=null) {
			Integer modoProyectado = (Integer) this.obtenerValorContexto(ConstantesSEFE.CTX_MODO_PROYECTADO);
			// 0 es modo proyectado sin uso de vaciados
			if (modoProyectado != null && modoProyectado.intValue() ==0) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public void calcularComponentes(Long idVaciado) {
		if (log.isDebugEnabled()) {
			log.debug("Iniciando el calculo de cuentas por tipo para vaciado " + idVaciado);
		}

		// Se obtienen las cuentas del vaciado, si las cuentas no tienen valor en la base las trae con null
		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();
		Collection cuentas = gestor.buscarCuentasVaciado(idVaciado, null);
		this.setCuentasVaciado(cuentas);

		this.calcularCuentas(idVaciado, ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_COMPONENTES));
		this.calcularIndicadores(idVaciado);
	}

	/*
	 * se obtiene la deudadel cliente Para calcular el rating financiera o cualquiera otra funcionalidad. 
	 */
	private Double obtenerDeudaCliente(Vaciado vaciado) {
		GestorServicioClientes gestor = new GestorServicioClientesImpl();
		Cliente cliente = gestor.obtenerParteInvolucradaPorId(vaciado.getIdParteInv());
		DeudaCliente deudaSistema = new ServicioConsultaDeudaODS().buscarDeudaClienteHasta(cliente.getRut(),vaciado.getPeriodo()); 		
		Double deudaDirecta = deudaSistema == null ||  deudaSistema.getTotalDeudaDirecta()==null? new Double(0.0) : deudaSistema.getTotalDeudaDirecta();				
		ConversorMoneda conversor = new ConversorMonedaImpl();
		Double montoHomologado = null;
		try {
			montoHomologado = conversor.convertirMonedaSegunReglas(deudaDirecta, ConstantesSEFE.ID_CLASIF_MONEDA_CLP, ConstantesSEFE.ID_CLASIF_MILES, vaciado.getIdMoneda(), vaciado.getUnidMedida(), vaciado.getPeriodo());
		}catch (Exception e) {
			log.warn("",e);
			
		}
			
		return montoHomologado;
	}
	

	public Cuenta calcularCuentaAlertas(Long idVaciado, Cuenta cta) {
		this.ponerEnContexto(ID_VAC_ACTUAL, idVaciado);
    	this.getContextoApplication().putAll(crearMapCtasVaciado(idVaciado));
		getMotor().inicializarContexto(this.getContextoApplication());		
		BigDecimal valor = null;		
         try {
        	//se obtienen los parametros que se necesitan pasar por ctx a la formula  
        	 valor = getMotor().calcularCuenta(cta);
        	 if(valor == null) {
        		 cta.setMonto(null);
        	 } else {
        		 cta.setMonto(new Double(valor.doubleValue()));
        	 }         	
         } catch (RuntimeException ex) {
        		log.info("Error al calcular cuenta " +  cta.getCodigoCuenta() + " causado por " + ex.getMessage());
         }

	    return cta;
	}
	


	/**
	 * se setea las cuentas que contiene otras formulas que no pertenecen a vaciado y al modelo de ctas.
	 */
	public void setOtrasCuentas(Collection obtenerListaOtrasFormulas) {
		this.listaOtrasCuentas=obtenerListaOtrasFormulas;
		
	}
	
	public void getOtrasCuentas(Collection obtenerListaOtrasFormulas) {
		this.listaOtrasCuentas=obtenerListaOtrasFormulas;
		
	}

	public Cuenta calcularCuenta(Cuenta cta) {
		if (log.isDebugEnabled()) {
			log.debug("Iniciando Calculo de cuenta, independiente del vac");
		}

		// crear el contexto de calculo y lo pasa al motor de calculo
		if (this.getContextoCalculo() == null) {
			this.setContextoCalculo(new HashMap());
		}

		// se valida si existe contexto aplicativo...
		if (this.contextoAplicativo != null && !this.contextoAplicativo.isEmpty()) {
			this.getContextoCalculo().putAll(this.contextoAplicativo);

			// se limpia el contexto aplicativo
			this.contextoAplicativo.clear();
			this.contextoAplicativo = null;
		}
		
		// Se agrega este flag ya que el motor intenta buscar cuentas asociadas a un vaciado dentro del contexto
		getContextoCalculo().put(ID_VAC_ACTUAL, Long.valueOf("0"));

		getMotor().inicializarContexto(this.getContextoCalculo());

		// se calcula cada uno de las cuentas del balance
		boolean errorEnCalculoCuenta = false;

		if (cta.isCalculada()) {
			BigDecimal valor = null;
			try {
				valor = getMotor().calcularCuenta(cta);
				if (log.isDebugEnabled()) {
					log.debug("CALCULANDO CUENTA: " + cta.getCodigoCuenta() + " - VALOR:" + valor);
				}
			} catch (RuntimeException ex) {
				errorEnCalculoCuenta = true;

				if (STOP_ON_ERROR) {
					log.info("Proceso de calculo detenido. Error al calcular cuenta " + cta.getCodigoCuenta() + " causado por " + ex.getMessage());
					throw ex;
				}

				log.info("Error al calcular cuenta " + cta.getCodigoCuenta() + " causado por " + ex.getMessage());
			}
			
			Integer tipoCuenta = cta.getTipoCuenta();

			 // la cuenta es ajustable solo si es activo - pasivo - eerr
			boolean esCuentaAjustable = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_ACTIVOS).equals(tipoCuenta) 
			||  ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_CIRCULANTE).equals(tipoCuenta) 
			||  ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_INTERES).equals(tipoCuenta)	
			||  ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_EERR).equals(tipoCuenta);

			try {
				if (!errorEnCalculoCuenta || (errorEnCalculoCuenta && UPDATE_ON_ERROR)) {
					// se actualiza el valor de la cuenta o ajuste según corresponda
					if (esCuentaAjustable) {
						// se actualizan solo los ajustes
						if (valor == null) {
							cta.setAjuste(null);
						} else {
							cta.setAjuste(new Double(valor.doubleValue()));
						}
					} else {
						// se actualiza el monto
						if (valor == null) {
							cta.setMonto(null);
						} else {
							cta.setMonto(new Double(valor.doubleValue()));
						}
					}
				}
			} finally {
				errorEnCalculoCuenta = false;
			}
		}

		return cta;
	}
	
	private Long obtenerRangoCtasCalcular() {
		Boolean activoRecalculo = ConfigDBManager.getValueAsBoolean(ConstantesSEFE.KEY_RECALCULO_ACTIVO); 
		Object rangoCta = null;
		if (activoRecalculo != null && activoRecalculo.booleanValue()) {
			rangoCta = ConfigDBManager.getValueAsObject(ConstantesSEFE.KEY_RANGO_CTA_COM); 
		}
		if (rangoCta != null )
			return new Long(rangoCta.toString());
		return null;
	}
}
