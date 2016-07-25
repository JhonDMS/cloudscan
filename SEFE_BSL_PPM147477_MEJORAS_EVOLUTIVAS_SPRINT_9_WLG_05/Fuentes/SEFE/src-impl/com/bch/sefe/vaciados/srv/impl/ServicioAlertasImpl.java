/**
 * 
 */
package com.bch.sefe.vaciados.srv.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.ServicioCalculo;
import com.bch.sefe.comun.impl.ServicioCalculoImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.BalancePlanCta;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.vaciados.CatalogoVaciados;
import com.bch.sefe.vaciados.ServicioVaciados;
import com.bch.sefe.vaciados.impl.CatalogoVaciadosImpl;
import com.bch.sefe.vaciados.impl.ServicioInfoComplemetariaImpl;
import com.bch.sefe.vaciados.impl.ServicioVaciadosImpl;
import com.bch.sefe.vaciados.srv.GestorAlertas;
import com.bch.sefe.vaciados.srv.GestorPlanCuentas;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.ServicioAlertas;
import com.bch.sefe.vaciados.vo.Alerta;
import com.bch.sefe.vaciados.vo.AlertaIndicador;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Encabezado;
import com.bch.sefe.vaciados.vo.IndicadorAlerta;
import com.bch.sefe.vaciados.vo.Vaciado;

/**
 * @author IGutierrez
 * 
 */
public class ServicioAlertasImpl implements ServicioAlertas {
	final static Logger log = Logger.getLogger(ServicioAlertasImpl.class);

	private static String SIMB_ANGLE_BRACKET_L = "<";
	private static String SIMB_ANGLE_BRACKET_R = ">";
	private static String SIMB_EQUALS = "=";

	private static String MSG_IND_NO_CALCULADO_A = "El Indicador/Cuenta ";
	private static String MSG_IND_NO_CALCULADO_B = " no ha sido calculado.";
	

	private static String MSG_VAC_ANTERIOR_NO_VIGENTE = "El Vaciado Anterior, no se encuentra en estado \"Vigente\".";
	private static String MSG_NO_INGRESO_CORRECCION_MONETARIA = "No se han ingresado cuentas de correccion monetaria";
	private static String MSG_NO_INGRESO_RECONCILIACION = "No se han ingresado cuentas de reconciliacion";
	private static String MSG_DETALLE_CTAS = "Detalle de Cuentas Descuadrado";
	private static String MSG_INFO_COMPLEMENTARIA = "Informacion Complementaria Descuadrada";
	private static String MSG_TOTAL_ACTIVOS_CERO = "Vaciado Posee un Total de Activos de 0";
	private static String MSG_TOTAL_PASIVOS_CERO = "Vaciado Posee un Total de Pasivos de 0";
	
	private static final String MSG_UTILIDAD_DESCUADRADA = "Utilidad Descuadrada";
	private static final String MSG_BALANCE_DESCUADRADO = "Balance Descuadrado";
	private static final String MSG_CUENTAS_SVS = "Esta pendiente cargar cuadro SVS";

	private static Double VALOR_CTA_CERO = new Double(0.0);

	/*
	 * (sin Javadoc)
	 * 
	 * @see com.bch.sefe.vaciados.srv.ServicioAlertas#buscarAlertasIndicadores(java.lang.Long)
	 */
	public Collection buscarAlertasIndicadores(Long idVac) {

		// Se realiza la busqueda del vaciado
		GestorVaciados gestorVac = new GestorVaciadosImpl();
		Vaciado vac = gestorVac.buscarVaciado(idVac);

		// Previo a la busqueda de las alertas de indicadores, se realiza el calculo de las cuentas del vaciado.
		ServicioCalculo servicioVac = new ServicioCalculoImpl();
		servicioVac.calcularCuentasVaciado(idVac);

		if (log.isDebugEnabled()) {
			log.debug("Realizando la busqueda de alertas de indicadores.");
		}

		// Se realiza la busqueda de las alertas existente para el plan de cuentas del vaciado.

		ArrayList listadoAlertas = new ArrayList();

		if (log.isDebugEnabled()) {
			log.debug("Recorriendo las alertas obtenidas");
		}

		// Se validan las alertas de correccion monetaria.
		Alerta alertaCM = evaluarAlertaCorreccionMonetaria(vac);
		if (alertaCM != null) {
			listadoAlertas.add(alertaCM);
		}

		// Se validan las alertas de reconciliacion
		Alerta alertaREC = evaluarAlertaReconciliacion(vac);
		if (alertaREC != null) {
			listadoAlertas.add(alertaREC);
		}
		// Se valida el detalle de cuentas
		Alerta alertaDetCtas = evaluarAlertaDetCtas(vac);
		if (alertaDetCtas != null) {
			listadoAlertas.add(alertaDetCtas);
		}

		return listadoAlertas;
	}

	private Alerta evaluarAlertaDetCtas(Vaciado vac) {
		Alerta alertaREC = null;

		ServicioVaciados gestor = new ServicioVaciadosImpl();
		boolean result=gestor.verificarDescuadresDetaleCtas(vac.getIdVaciado());
		if (!result) {
			alertaREC = new Alerta();
			alertaREC.setNivel(ConstantesSEFE.ALERT_ADVERTENCIAS);
			alertaREC.setMensaje(MSG_DETALLE_CTAS);
		}

		return alertaREC;
	}
	
	private Alerta evaluarAlertaInfoComplementaria(Vaciado vac) {
	/*  Metodo que si existe al menos una cuenta descuadrada en informacion complementaria
	 * devuelve un mensaje de alerta (cambio de estado)
	 */
		Alerta alertaREC = null;
		if(vac.getIdUsuModifInfoComp()!= null){//la informacion complementaria fue modificada
			ServicioInfoComplemetariaImpl gestor = new ServicioInfoComplemetariaImpl();
			boolean result=gestor.verificarCuadratura(vac.getIdVaciado(), vac.getRutParteInv());
			if (!result) {

				alertaREC = new Alerta();
				alertaREC.setNivel(ConstantesSEFE.ALERT_ADVERTENCIAS);
				alertaREC.setMensaje(MSG_INFO_COMPLEMENTARIA);

			}
		}
		return alertaREC;
	}
	
	
	public Collection buscarAlertasInfoComplementaria(Long idVac) {

		// Se realiza la busqueda del vaciado
		GestorVaciados gestorVac = new GestorVaciadosImpl();
		Vaciado vac = gestorVac.buscarVaciado(idVac);

		// Previo a la busqueda de las alertas de indicadores, se realiza el calculo de las cuentas del vaciado.
		//ServicioCalculo servicioVac = new ServicioCalculoImpl();
		//servicioVac.calcularCuentasVaciado(idVac);

		if (log.isDebugEnabled()) {
			log.debug("Realizando la busqueda de alertas por informacion complementaria.");
		}

		// Se realiza la busqueda de las alertas existente para el plan de cuentas del vaciado.

		ArrayList listadoAlertas = new ArrayList();

		// Se valida el detalle de cuentas
		Alerta alertaInfoComp = evaluarAlertaInfoComplementaria(vac);
		if (alertaInfoComp != null) {
			listadoAlertas.add(alertaInfoComp);
		}

		return listadoAlertas;
	}
	
	
	/*
	 * (sin Javadoc)
	 * 
	 * @see com.bch.sefe.vaciados.srv.ServicioAlertas#buscarAlertasIndicadores(java.lang.Long)
	 */
	public Collection buscarAlertasIndicadoresParaAnalisis(Long idVac) {
		
		GestorAlertas gestorAlert = new GestorAlertasImpl();
		ArrayList listIndsAlrt = new ArrayList();

		// Se obtiene un mapa con las cuentas de las alertas calculadas al vuelo.
		Map ctasAlrt = calcularCuentasAlertas(idVac);

		if (log.isDebugEnabled()) {
			log.debug("Realizando la busqueda de alertas de indicadores.");
		}

		// Se realiza la busqueda de los indicadores y sus alertas asociadas.
		listIndsAlrt.addAll(gestorAlert.buscarIndicadoresPorAlerta(idVac));
		
		// Se procede a setear el valor de la alerta obtenido en el calculo.
		for(int i = 0; i < listIndsAlrt.size(); i++) {
			IndicadorAlerta ia = (IndicadorAlerta)listIndsAlrt.get(i);

			Cuenta cta = (Cuenta)ctasAlrt.get(ia.getCtaAlrtId());
			if(cta != null && cta.getMonto() != null) {
				int value = (int) Math.floor(cta.getMonto().doubleValue());
				ia.setAlrtValor(String.valueOf(value));	
				String mensaje=gestorAlert.buscarMensajeAlrtXIdyNota(ia.getAlrtId(), ia.getAlrtValor());
				if (mensaje!=null){
					ia.setAlrtDescripcion(mensaje);
				}
			} else if(cta == null || cta != null && cta.getMonto() == null) {
				ia.setAlrtDescripcion("");
			}
			
		}
		
		return eliminaRepetidos(listIndsAlrt);
	}
	private Collection eliminaRepetidos(ArrayList listIndsAlrt){
		for(int i=0;i<listIndsAlrt.size()-1;i++){
			for(int j=i+1;j<listIndsAlrt.size();j++){
				if(((IndicadorAlerta)listIndsAlrt.get(i)).getCodCuenta().equals(((IndicadorAlerta)listIndsAlrt.get(j)).getCodCuenta())){
					listIndsAlrt.remove(j);
				}
			}
		}
		return listIndsAlrt;
	}
	private Alerta evaluarAlertaCorreccionMonetaria(Vaciado vac) {
		Alerta alertaCM = null;

		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();

		// Se obtiene el tipo de plan de cuenta que esta asociado al vaciado
		BalancePlanCta relacionTipoPlan = gestor.obtenerRelBlcePlanCtas(vac.getIdTipoBalance(), vac.getIdNombrePlanCtas());

		// Se verifica si el vaciado tiene un plan de cuentas de tipo CHGAAP y que sea un vaciado en pesos chilenos, sino devuelve la alerta como null
		if (relacionTipoPlan.getIdTpoPlanCta().intValue() == ConstantesSEFE.CLASIF_ID_TPO_PLAN_CHGAAP.intValue()
				&& ConstantesSEFE.CLASIF_ID_TPO_MONEDA_CLP.equals(vac.getIdMoneda()) && !vac.esConsolidadoBCHSEFE().booleanValue()) {

			GestorVaciados gestorVac = new GestorVaciadosImpl();

			int flag = gestorVac.obtenerFlagControl(vac.getIdVaciado());

			if (ConstantesSEFE.MASK_CORRECCION_MONETARIA != (flag & ConstantesSEFE.MASK_CORRECCION_MONETARIA)) {
				alertaCM = new Alerta();
				alertaCM.setNivel(ConstantesSEFE.ALERT_ADVERTENCIAS);
				alertaCM.setMensaje(MSG_NO_INGRESO_CORRECCION_MONETARIA);
			}

		}

		return alertaCM;
	}

	private Alerta evaluarAlertaReconciliacion(Vaciado vac) {
		
		Alerta alertaREC = null;
		
		if (vac.isPlanConReconciliacion()) {
	
			GestorVaciados gestor = new GestorVaciadosImpl();
	
			int flag = gestor.obtenerFlagControl(vac.getIdVaciado());
	
			if (ConstantesSEFE.MASK_RECONCILIACION != (flag & ConstantesSEFE.MASK_RECONCILIACION)) {
				alertaREC = new Alerta();
				alertaREC.setNivel(ConstantesSEFE.ALERT_ADVERTENCIAS);
				alertaREC.setMensaje(MSG_NO_INGRESO_RECONCILIACION);
			}
		}
		return alertaREC;
	}

	
	public Collection buscarAlertasCuentasSVS(Long idVaciado){
		final List listadoAlertas = new ArrayList();
		
		CatalogoVaciados catalogo = new CatalogoVaciadosImpl();
		
		Vaciado vaciado = catalogo.buscarDatosGeneral(idVaciado);
		Integer tipoCuenta = null;
		if (vaciado.getIdTipoPlan().equals(ConstantesSEFE.CLASIF_ID_TPO_PLAN_IFRS_SEG_GRAL)
				||vaciado.getIdTipoPlan().equals(ConstantesSEFE.CLASIF_ID_TPO_PLAN_IFRS_SEG_VIDA)){
			if (vaciado.getIdTipoPlan().equals(ConstantesSEFE.CLASIF_ID_TPO_PLAN_IFRS_SEG_GRAL)){
				tipoCuenta = Integer.valueOf("6510");
			}else{
				tipoCuenta = Integer.valueOf("6505");
			}
			ArrayList cuentas = (ArrayList)catalogo.buscarCuentasEstadoFinanciero(idVaciado, tipoCuenta);
			for (int i = 0; i < cuentas.size(); i++){
				Cuenta cuenta = (Cuenta) cuentas.get(i);
				if (cuenta.getMonto() != null && cuenta.getMonto().doubleValue() > 0){
					return listadoAlertas;
				}
			}
			listadoAlertas.add(createAlertaInvalidante(MSG_CUENTAS_SVS));
		}
		return listadoAlertas;
	}
	
	/*
	 * (sin Javadoc)
	 * 
	 * @see com.bch.sefe.vaciados.srv.ServicioAlertas#buscarValidacionesCruzadas(java.lang.Long)
	 */
	public Collection buscarValidacionesCruzadas(Long idVac) {
		final List listadoAlertas = new ArrayList();
		
		CatalogoVaciados catalogo = new CatalogoVaciadosImpl();
		
		Encabezado enc = catalogo.buscarEncabezado(idVac);
		
		if (enc != null) {
			if (enc.isBalanceDescuadrado()) {
				listadoAlertas.add(createAlertaInvalidante(MSG_BALANCE_DESCUADRADO));
			}
			if (enc.isUtilidadDescuadrada()) {
				listadoAlertas.add(createAlertaInvalidante(MSG_UTILIDAD_DESCUADRADA));
			}
		}
		
		/* me ahorro toda esta parafernalia sin sentido:
		 * 
		boolean esVaciadoAjustado = vac.getAjustadoFlg().equals(ConstantesSEFE.FLAG_VACIADO_AJUSTADO) || ConstantesSEFE.CLASIF_ID_NO_APLICA.equals(vac.getIdTipoBalance());

		if (log.isDebugEnabled()) {
			log.debug("Realizando la busqueda de validaciones cruzadas");
		}

		// Se realiza la busqueda del listado de Validaciones correspondientes a ese vaciado y el plan de cuentas.
		GestorAlertas gestorAlert = new GestorAlertasImpl();
		ArrayList listValidaciones = (ArrayList) gestorAlert.buscarValidacionesCruzadas(idVac, vac.getIdNombrePlanCtas());

		GestorPlanCuentasImpl gestorPlan = new GestorPlanCuentasImpl();
		ArrayList listadoAlertas = new ArrayList();

		if (log.isDebugEnabled()) {
			log.debug("Iterando las Validaciones Cruzadas Obtenidas");
		}

		// Se realiza la iteracion de las validaciones obtenidas.
		Iterator iterador = listValidaciones.iterator();
		while (iterador.hasNext()) {
			ValidacionCruzada validacion = (ValidacionCruzada) iterador.next();

			if (log.isDebugEnabled()) {
				log.debug("Consultando el valor de la cuenta:" + validacion.getIdCuenta());
			}

			// Por cada validacion se realiza la consulta del valor de la Cuenta.
			Long idCuenta = validacion.getIdCuenta();
			Cuenta cuenta = gestorPlan.consultarValorCuentaVaciado(idVac, idCuenta);

			// Por cada validacion se realiza la consulta del valor de la Cuenta Par.
			Long idCuentaPar = validacion.getIdCuentaPar();
			if (log.isDebugEnabled()) {
				log.debug("Consultando el valor de la cuenta par:" + idCuentaPar);
			}
			Cuenta cuentaPar = gestorPlan.consultarValorCuentaVaciado(idVac, idCuentaPar);

			// Se realiza la evaluacion de la condicion de dicha validacion con los valores obtenidos.
			if (!evaluarCondicion(validacion.getCondicion(), cuenta, cuentaPar, esVaciadoAjustado)) {

				if (log.isDebugEnabled()) {
					if (cuenta == null || cuentaPar == null) {
						log.debug("No se cumplio la condicion, ya que una de las cuentas es null.");
					} else {
						log.debug("No se cumplio la condicion:" + cuenta.getMonto() + validacion.getCondicion() + cuentaPar.getMonto());
					}
				}

				// Si la condicion no se cumple, se instancia la alerta
				Alerta alert = new Alerta();
				alert.setMensaje(validacion.getMensaje());
				alert.setNivel(ConstantesSEFE.ALERT_INVALIDANTE);

				// Dicha alerta se agrega al listado de retorno.
				listadoAlertas.add(alert);

			}
		}
		*/

		// Se realiza la busqueda del vaciado.
		GestorVaciados gestorVac = new GestorVaciadosImpl();
		Vaciado vac = gestorVac.buscarVaciado(idVac); //Se corrige defecto que no validaba el balance descuadrado de un vaciado consolidado antes de cambiar de estado
		
		// Se agrega de ser necesario la alerta en caso que el vaciado anterior no se encuentre vigente aun.
		Alerta alertaVacAnterior = evaluarAlertaPoseeVaciadoAnterior(vac);
		if (alertaVacAnterior != null) {
			listadoAlertas.add(alertaVacAnterior);
		}

		Alerta alertaTotalActivos = evaluarAlertaTotalActivos(vac);
		if (alertaTotalActivos != null) {
			listadoAlertas.add(alertaTotalActivos);
		}

		Alerta alertaTotalPasivos = evaluarAlertaTotalPasivos(vac);
		if (alertaTotalPasivos != null) {
			listadoAlertas.add(alertaTotalPasivos);
		}

		return listadoAlertas;

	}

	private Alerta createAlertaInvalidante(final String msg) {
		Alerta alerta = new Alerta();
		alerta.setNivel(ConstantesSEFE.ALERT_INVALIDANTE);
		alerta.setMensaje(msg);
		return alerta;
	}

	private Alerta evaluarAlertaPoseeVaciadoAnterior(Vaciado vac) {
		Alerta alertaPoseeVacAnterior = null;

		// Se instancia el gestor de Vaciados.
		GestorVaciados gestor = new GestorVaciadosImpl();
		Vaciado vacAnt = gestor.buscarVaciadoAnterior(vac.getIdVaciado());

		if (vacAnt == null) {
			return alertaPoseeVacAnterior;
		} else {

			// Si el vaciado anterior se encuentra en estado vigente no hay alerta invalidante que desplegar.
			if (vacAnt.getIdEstado().equals(ConstantesSEFE.CLASIF_VACIADO_VIGENTE)) {
				return alertaPoseeVacAnterior;

			} else {
				// Si el vaciado anterior se encuentra en curso, debe generarse la alerta invalidante
				// para que el actual no pueda ser pasado a vigente.
				alertaPoseeVacAnterior = new Alerta();
				alertaPoseeVacAnterior.setMensaje(MSG_VAC_ANTERIOR_NO_VIGENTE);
				alertaPoseeVacAnterior.setNivel(ConstantesSEFE.ALERT_INVALIDANTE);
			}

		}

		return alertaPoseeVacAnterior;
	}

	private Alerta evaluarAlertaTotalActivos(Vaciado vac) {
		Alerta alertaTotalActivos = null;
		Long idVac = vac.getIdVaciado();
		Cuenta totalActivos;

		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();

		// Se obtiene el tipo de plan de cuenta que esta asociado al vaciado
		/* lo saco porque es al cuete:
		BalancePlanCta relacionTipoPlan = gestor.obtenerRelBlcePlanCtas(vac.getIdTipoBalance(),vac.getIdNombrePlanCtas());
		if (relacionTipoPlan == null) {
			throw new BusinessOperationException("No existe relaci\u00F3n definida entre tipo balance y tipo plan del vaciado");
		}
		*/

		StringBuffer llaveProp = new StringBuffer();
		llaveProp.append(ConstantesSEFE.KEY_PROP_ID_CUENTA_TOTAL_ACTIVOS).append(vac.getIdNombrePlanCtas());

		final String codCtaTotActivos = ConfigDBManager.getValueAsString(llaveProp.toString());
		
		if (codCtaTotActivos == null) {
			throw new BusinessOperationException("Par\u00E1metro " + llaveProp + " no definido (referencia a cuenta de total de activos correspondiente a este plan).");
		}
		
		totalActivos = gestor.consultarValorCuentaVaciado(idVac, codCtaTotActivos);
		
		if (totalActivos == null) {
			throw new BusinessOperationException("No se encontr\u00F3 cuenta de total de activos " + codCtaTotActivos +"; verifique valor de Par\u00E1metro " + llaveProp + ".");
		}

		if (totalActivos.getMonto().compareTo(VALOR_CTA_CERO) == 0) {
			// Si el total de activos es 0, se genera la alerta invalidante.
			alertaTotalActivos = new Alerta();
			alertaTotalActivos.setMensaje(MSG_TOTAL_ACTIVOS_CERO);
			alertaTotalActivos.setNivel(ConstantesSEFE.ALERT_INVALIDANTE);
		}

		return alertaTotalActivos;
	}

	private Alerta evaluarAlertaTotalPasivos(Vaciado vac) {
		Alerta alertaTotalPasivos = null;
		Long idVac = vac.getIdVaciado();
		Cuenta totalPasivos;

		GestorPlanCuentas gestor = new GestorPlanCuentasImpl();

		// Se obtiene el tipo de plan de cuenta que esta asociado al vaciado
		// lo saco porque es al cuete:
		//BalancePlanCta relacionTipoPlan = gestor.obtenerRelBlcePlanCtas(vac.getIdTipoBalance(), vac.getIdNombrePlanCtas());

		StringBuffer llaveProp = new StringBuffer();
		llaveProp.append(ConstantesSEFE.KEY_PROP_ID_CUENTA_TOTAL_PASIVOS).append(vac.getIdNombrePlanCtas());

		final String codCtaTotPasivos = ConfigDBManager.getValueAsString(llaveProp.toString());
		if (codCtaTotPasivos == null) {
			throw new BusinessOperationException("Par\u00E1metro " + llaveProp + " no definido (referencia a cuenta de total de pasivos correspondiente a este plan).");
		}
		
		totalPasivos = gestor.consultarValorCuentaVaciado(idVac, codCtaTotPasivos);

		if (totalPasivos == null) {
			throw new BusinessOperationException("No se encontr\u00F3 cuenta de total de pasivos " + codCtaTotPasivos + "; verifique valor de Par\u00E1metro " + llaveProp + ".");
		}

		if (totalPasivos.getMonto().compareTo(VALOR_CTA_CERO) == 0) {
			// Si el total de pasivos es 0, se genera la alerta invalidante.
			alertaTotalPasivos = new Alerta();
			alertaTotalPasivos.setMensaje(MSG_TOTAL_PASIVOS_CERO);
			alertaTotalPasivos.setNivel(ConstantesSEFE.ALERT_INVALIDANTE);
		}

		return alertaTotalPasivos;
	}

/**
	 * Metodo que realiza la evaluacion de la condicion entregada, para los dos valores suministrados.
	 *  
	 * @param condicion - String con la condicion a evaluar [<, >, =]
	 * @param monto - Monto base para la comparacion
	 * @param montoPar - El monto objetivo contra el cual realiza la comparacion.
	 * @return true - Si la condicion se cumple <br> false - Si la condicion no se cumple.
	 * <br><br>
	 * <b>Ejemplo de una Comparacion:</b>
	 * <br> monto < montoPar 
	 * <br>(Donde "<" puede ser el valor de cualquiera de las condiciones permitidas)   
	 */
	private boolean evaluarCondicion(String condicion, Cuenta cuenta, Cuenta cuentaPar, boolean esVaciadoAjustado) {

		if (cuenta == null || cuentaPar == null)
			return false;

		boolean montoEsAjustado = esCuentaAjustada(cuenta);
		boolean montoParEsAjustado = esCuentaAjustada(cuentaPar);

		// Double monto = null;
		// Double montoPar = null;
		BigDecimal monto = null;
		BigDecimal montoPar = null;
		Double ajuste;

		try {
			ajuste = (cuenta.getAjuste() != null ? cuenta.getAjuste() : new Double("0.0"));

			if (esVaciadoAjustado && montoEsAjustado) {
				monto = new BigDecimal(cuenta.getMonto().doubleValue() + ajuste.doubleValue());
				monto.setScale(2);
				// monto = new Double(cuenta.getMonto().doubleValue() + cuenta.getAjuste().doubleValue());
			} else {
				monto = new BigDecimal(cuenta.getMonto().doubleValue());
				monto.setScale(2);
				// monto = cuenta.getMonto();
			}
		} catch (Exception ex) {
			if (log.isInfoEnabled()) {
				log.info("Error al evaluar alerta de cuenta " + cuenta.getCodigoCuenta());
			}
		}

		try {
			ajuste = (cuentaPar.getAjuste() != null ? cuentaPar.getAjuste() : new Double("0.0"));

			if (esVaciadoAjustado && montoParEsAjustado) {
				montoPar = new BigDecimal(cuentaPar.getMonto().doubleValue() + ajuste.doubleValue());
				montoPar.setScale(2);
				// montoPar = new Double(cuentaPar.getMonto().doubleValue() + cuentaPar.getAjuste().doubleValue());
			} else {
				montoPar = new BigDecimal(cuentaPar.getMonto().doubleValue());
				montoPar.setScale(2);
				// montoPar = cuentaPar.getMonto();
			}
		} catch (Exception ex) {
			if (log.isInfoEnabled()) {
				log.info("Error al evaluar alerta de cuenta " + cuentaPar.getCodigoCuenta());
			}
		}

		if (monto == null || montoPar == null)
			return false;
		double monto1 = Math.floor(monto.doubleValue());;
		double monto2 =Math.floor(montoPar.doubleValue());
		if (condicion.equalsIgnoreCase(SIMB_ANGLE_BRACKET_L)) {
			return monto1 < monto2;

		} else if (condicion.equalsIgnoreCase(SIMB_ANGLE_BRACKET_R)) {
			return monto1> monto2;

		} else if (condicion.equalsIgnoreCase(SIMB_EQUALS)) {
			return monto1==monto2;
		}

		return false;
	}

	/**
	 * Metodo que realiza la evaluacion que corresponde a la alerta del Indicador en base a los parametros obtenidos.
	 * 
	 * @param alerta
	 *            - Alerta que contiene los rangos que la cuenta suministrada debe cumplir.
	 * @param cuenta
	 *            - La cuenta a evaluar.
	 * @return true - Si la condicion se cumple <br>
	 *         false - Si la condicion no se cumple.
	 *         
	 */
	private Integer evaluarAlertaIndicador(AlertaIndicador alerta, Cuenta cuenta) {
		// Si algun atributo es nulo, no se puede evaluar la alerta y por ello no se puede considerar como aprobada.
		if (alerta == null || cuenta == null  ||  cuenta.getMonto() == null)
			return ConstantesSEFE.ALERT_NO_VISIBLE;

		Double monto = cuenta.getMonto();
		if (monto.equals(alerta.getFlagAlerta()))
		{
			return ConstantesSEFE.ALERT_ADVERTENCIAS;
		}
		return ConstantesSEFE.ALERT_OK;
	}
	
	private Integer evaluarAlertaIndicadorAnalisis(AlertaIndicador alerta, Cuenta cuenta) {
		// Si algun atributo es nulo, no se puede evaluar la alerta y por ello no se puede considerar como aprobada.
		if (alerta == null || cuenta == null  ||  cuenta.getMonto() == null)
			return ConstantesSEFE.ANALISIS_ALERT_INVALIDANTE;

		Double monto = cuenta.getMonto();
		
		if(monto.intValue() == ConstantesSEFE.ANALISIS_ALERT_NO_VISIBLE.intValue()) {
			return ConstantesSEFE.ANALISIS_ALERT_NO_VISIBLE;
		}
		if (monto.equals(alerta.getFlagAlerta()))
		{
			return ConstantesSEFE.ANALISIS_ALERT_ADVERTENCIAS;
		}
		return ConstantesSEFE.ANALISIS_ALERT_OK;
	}

	private String armarError(String idCuenta) {
		StringBuffer error = new StringBuffer();
		error.append(MSG_IND_NO_CALCULADO_A).append(idCuenta).append(MSG_IND_NO_CALCULADO_B);
		return error.toString();
	}

	/**
	 * Determina si la cuenta tiene ajustes en base a la clasificacion de la cuenta y si el vaciado actual es ajustado
	 * 
	 * @param cuenta
	 *            - la cuenta a evaluar
	 * @return boolean
	 */
	private boolean esCuentaAjustada(Cuenta cuenta) {
		boolean esAjustada = false;
		Integer tipoCuenta = cuenta.getTipoCuenta();

		// son ajustables las cuentas de activos pasivos y eerr
		esAjustada = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_ACTIVOS).equals(tipoCuenta)
				|| ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_CIRCULANTE).equals(tipoCuenta)
				|| ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_PASIVOS_INTERES).equals(tipoCuenta)
				|| ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_EERR).equals(tipoCuenta);

		return esAjustada;
	}
	
	public Map calcularCuentasAlertas(Long idVaciado) {
		GestorAlertas gest = new GestorAlertasImpl();
		return gest.calcularCuentasAlertas(idVaciado, null);
	}	
}
