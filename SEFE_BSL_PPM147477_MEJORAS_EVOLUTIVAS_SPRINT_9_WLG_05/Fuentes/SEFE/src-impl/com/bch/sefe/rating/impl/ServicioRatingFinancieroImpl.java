package com.bch.sefe.rating.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.ConstantesRating;
import com.bch.sefe.rating.ServicioRatingFinanciero;
import com.bch.sefe.rating.dao.RatingIndividualDAO;
import com.bch.sefe.rating.dao.impl.RatingIndividualDAOImpl;
import com.bch.sefe.rating.srv.GestorFortalezasDebilidades;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.ValidadorRating;
import com.bch.sefe.rating.srv.impl.GestorFortalezasDebilidadesImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingFinancieroImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.RatingUtil;
import com.bch.sefe.rating.srv.impl.ValidadorRatingFinancieroImpl;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.ValidacionRatingFinanciero;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.vaciados.CatalogoVaciados;
import com.bch.sefe.vaciados.impl.CatalogoVaciadosImpl;
import com.bch.sefe.vaciados.srv.GestorCalificadoras;
import com.bch.sefe.vaciados.srv.GestorHojaIndependiente;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorCalificadorasImpl;
import com.bch.sefe.vaciados.srv.impl.GestorHojaIndependienteImpl;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.HojaIndependiente;
import com.bch.sefe.vaciados.vo.Vaciado;


public class ServicioRatingFinancieroImpl implements ServicioRatingFinanciero {
	final static Integer IDBANCA_TP_BANCO = new Integer(4205); //Requerimiento 7.4.42-4 Alerta para Bancos con vacions sin Tier definidos.
	
	public Map confirmarRating(String rutCliente, Long idRatingInd, Long idVacCabecera, String loginUsuario) {
		GestorRatingIndividual gestorRtgIndividual = new GestorRatingIndividualImpl();
		GestorRatingFinanciero gestorRtgFinanciero = new GestorRatingFinancieroImpl();
		GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();
		RatingFinanciero rtgFinanciero = null;
		Map retorno = new HashMap();
		
		
		Cliente cliente = gestorClientes.obtenerClientePorRut(rutCliente);
		Long idCliente = Long.valueOf(cliente.getClienteId());

		RatingIndividual rtgIndividual = gestorRtgIndividual.buscarRatingIndividual(idCliente, idRatingInd);

		GestorVaciados gestorVaciados = new GestorVaciadosImpl();
		
		// se obtiene la lista de vaciados candidatos para rating
		List vaciadosCandidatos = this.buscarVaciadosParaRating(cliente.getRut(), rtgIndividual.getIdBanca());
		
		// se valida que el vaciado cabecera sea el primero por tipo de vaciado (el mas reciente)
		// si el vaciado no es el más reciente se lanza una excepcion
		Vaciado vacCabecera = gestorVaciados.buscarVaciado(idVacCabecera);
		boolean esElMasReciente = validarVaciadoEsSeleccionable(vacCabecera, vaciadosCandidatos, rtgIndividual.getIdBanca());
		
		if (!esElMasReciente) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_FINAN_ALERTA_NO_PUEDE_UTILIZAR_VACIADO));
		}
		// recupera las bancas que utilizan la hoja independiente para la generacion del rating Proyectado
		List lstBancasHojaIndependiente =  ConfigDBManager.getValuesAsListString(ConstantesSEFE.ID_BANCAS_USAN_HOJA_INDEP);
		if (lstBancasHojaIndependiente != null && !lstBancasHojaIndependiente.isEmpty() && lstBancasHojaIndependiente.contains(rtgIndividual.getIdBanca().toString())) {
			GestorHojaIndependiente gestorHojaInd = new GestorHojaIndependienteImpl();	
			HojaIndependiente hojaIndependienteExistente = gestorHojaInd.obtenerHojaIndependiente(idCliente, rtgIndividual.getIdRating());
			if (hojaIndependienteExistente==null){
				if (!gestorHojaInd.hayDisponibleUnaHojaIndependiente(idCliente).booleanValue()) {
					throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.MSG14_MODELO_RATING_INDIVIDUAL));
				}
				HojaIndependiente hojaIndependiente = null;
				hojaIndependiente = gestorHojaInd.buscarHojaIndependiente(idCliente);
				hojaIndependiente.setIdRating(rtgIndividual.getIdRating());
				gestorHojaInd.actualizarHojaIndependiente(hojaIndependiente);
			}else {
				if (!ConstantesSEFE.IMD_EST_VIGENTE.equals(Integer.valueOf(hojaIndependienteExistente.getEstado()))){
					//valida si hay disponible una hoja independiente para generar el rating individual
					if (!gestorHojaInd.hayDisponibleUnaHojaIndependiente(idCliente).booleanValue()) {
						throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.MSG14_MODELO_RATING_INDIVIDUAL));
					}
					hojaIndependienteExistente.setIdRating(null);
					gestorHojaInd.actualizarHojaIndependiente(hojaIndependienteExistente);
					HojaIndependiente hojaIndependiente = null;
					hojaIndependiente = gestorHojaInd.buscarHojaIndependiente(idCliente);
					hojaIndependiente.setIdRating(rtgIndividual.getIdRating());
					gestorHojaInd.actualizarHojaIndependiente(hojaIndependiente);
				}
			}
		}
		// Se obtienen las alertas en caso que existan
		ArrayList alertas = new ArrayList();
		ValidadorRating validador = new ValidadorRatingFinancieroImpl();
		ValidacionRatingFinanciero validacion;

		// Se realizan las validaciones respectivas, dependiendo de si es una modificacion a un rating financiero ya existente o una nuevo rating
		// financiero
		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRatingInd);
		if (esUnaModificacion(rtgIndividual, idVacCabecera)) {
			validacion = validador.validarModificacion(idCliente, idRatingInd, idVacCabecera, rtgIndividual.getIdBanca());

			alertas.addAll(validacion.getMensajes());
		} else {
			validacion = validador.validarIngreso(idCliente, idRatingInd, idVacCabecera, rtgIndividual.getIdBanca());

			alertas.addAll(validacion.getMensajes());
		}

		if (!alertas.isEmpty()) {
			// retorno.put(PARAM_ALERTAS, alertas);
			throw new BusinessOperationException((String) alertas.get(0));
		}

		// Se busca un rating financiero ya existente para el vaciado cabecera; en caso de existir uno se reutiliza; en caso contrario se genera un
		// nuevo rating
		rtgFinanciero = gestorRtgFinanciero.buscarRatingFinancieroPorIdVaciadoActual(idVacCabecera);
		// se pone el idrating individual en contexto para ser ocupado en el algoritmo generico
		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, rtgIndividual.getIdRating());
		// Se calcula el rating financiero, se setea a vigente el estado y se guarda
		
		RatingFinanciero rtgFinancieroCalculado = gestorRtgFinanciero.calcularRating(rutCliente, rtgIndividual.getIdBanca(), idVacCabecera, loginUsuario);
		//rtgFinancieroCalculado.setIdProyectado(null);
		// si no hay rating financiero para el vaciado cabecera entonces se generar un nuevo calculo y nuevo rating financiero
//		if (rtgFinanciero == null) {
			// Se guarda tanto el ratin financiero 
			rtgFinanciero = gestorRtgFinanciero.grabarRating(rtgFinancieroCalculado);
			rtgFinancieroCalculado.setIdRating(rtgFinanciero.getIdRating());
//		} else {			
//			// se actualiza solo el rating financiero actual con los nuevos calculos
//			rtgFinancieroCalculado.setIdRating(rtgFinanciero.getIdRating());
//			rtgFinanciero = gestorRtgFinanciero.actualizarRating(rtgFinancieroCalculado);
//		}		
	
		// se graba o actualiza las evaluaciones calculadas en el rating financiero
		gestorRtgFinanciero.grabarEvaluaciones(rtgFinancieroCalculado);
		rtgFinanciero.setMontoPatrimonioMMUF(rtgFinancieroCalculado.getMontoPatrimonioMMUF());
		rtgFinanciero.setMontoVentaMMUF(rtgFinancieroCalculado.getMontoVentaMMUF());
		rtgFinanciero.setMontoActivos(rtgFinancieroCalculado.getMontoActivos());
		gestorRtgIndividual.actualizarRatingFinanciero(idCliente, idRatingInd, rtgFinanciero);
		/* 
		 * 20120927 - marias
		 * Se desvincula el rating proyectado desde el rating individual
		 * Este proceso se raliza ANTES del calculo individual
		 */
		// se setean a null los valores de proyectado
		if (!ConstantesSEFE.BANCA_INMOBILIARIAS.equals(rtgIndividual.getIdBanca())&&!ConstantesSEFE.BANCA_CONSTRUCTORAS.equals(rtgIndividual.getIdBanca())){
			RatingFinanciero ratingProy = new RatingFinanciero();
			ratingProy.setFechaRating(new Date());
			Double notaProy = null;
			ratingProy.setNotaFinanciera(notaProy);
			ratingProy.setIdUsuario(rtgFinanciero.getIdUsuario());
			Long idProy = null;
			ratingProy.setIdProyectado(idProy);
			
			// se actualizan los valores en el individual
			gestorRtgIndividual.actualizarRatingProyectado(idCliente, idRatingInd, ratingProy);
			/*
			 * Fin desvinculacion rating proyectado
			 */
		}
		
		//Requerimiento Fortaleza y Debilidades

//		RatingIndividual ratingCalculado = gestorRtgIndividual.calcularRating(idCliente, idRatingInd);
		
//		GestorFortalezasDebilidades gestFortDeb = new GestorFortalezasDebilidadesImpl();
//		gestFortDeb.borrarFortalezasYDebilidades(rtgIndividual.getIdCliente(), rtgIndividual.getIdRating(), ConstantesSEFE.AMBITO_FINANCIERO);
//		gestFortDeb.agregarFortalezasDebilidadesRtgFinanciero(ratingCalculado);
		retorno.put(ConstantesRating.ID_ESTADO, rtgIndividual.getIdEstado());
		retorno.put(PARAM_LISTADO_VACIADOS, vaciadosCandidatos);
		retorno.put(PARAM_RATING_FINANCIERO, rtgFinanciero);
		
		gestorRtgIndividual.calcularRating(idCliente, idRatingInd);

		return retorno;
	}

	//Spring 5 Req. 7.4.42-4
	public Map validarRating(String rutCliente, Long idRatingInd, Long idVacCabecera, String loginUsuario) {
		GestorRatingIndividual gestorRtgIndividual = new GestorRatingIndividualImpl();
		GestorRatingFinanciero gestorRtgFinanciero = new GestorRatingFinancieroImpl();
		GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();
		RatingFinanciero rtgFinanciero = null;
		Map retorno = new HashMap();
		
		
		Cliente cliente = gestorClientes.obtenerClientePorRut(rutCliente);
		Long idCliente = Long.valueOf(cliente.getClienteId());

		RatingIndividual rtgIndividual = gestorRtgIndividual.buscarRatingIndividual(idCliente, idRatingInd);

		GestorVaciados gestorVaciados = new GestorVaciadosImpl();
		
		// se obtiene la lista de vaciados candidatos para rating
		List vaciadosCandidatos = this.buscarVaciadosParaRating(cliente.getRut(), rtgIndividual.getIdBanca());
		
		// se valida que el vaciado cabecera sea el primero por tipo de vaciado (el mas reciente)
		// si el vaciado no es el más reciente se lanza una excepcion
		Vaciado vacCabecera = gestorVaciados.buscarVaciado(idVacCabecera);
		boolean esElMasReciente = validarVaciadoEsSeleccionable(vacCabecera, vaciadosCandidatos, rtgIndividual.getIdBanca());
		
		if (!esElMasReciente) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_FINAN_ALERTA_NO_PUEDE_UTILIZAR_VACIADO));
		}
		// recupera las bancas que utilizan la hoja independiente para la generacion del rating Proyectado
		List lstBancasHojaIndependiente =  ConfigDBManager.getValuesAsListString(ConstantesSEFE.ID_BANCAS_USAN_HOJA_INDEP);
		if (lstBancasHojaIndependiente != null && !lstBancasHojaIndependiente.isEmpty() && lstBancasHojaIndependiente.contains(rtgIndividual.getIdBanca().toString())) {
			GestorHojaIndependiente gestorHojaInd = new GestorHojaIndependienteImpl();	
			HojaIndependiente hojaIndependienteExistente = gestorHojaInd.obtenerHojaIndependiente(idCliente, rtgIndividual.getIdRating());
			if (hojaIndependienteExistente==null){
				if (!gestorHojaInd.hayDisponibleUnaHojaIndependiente(idCliente).booleanValue()) {
					throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.MSG14_MODELO_RATING_INDIVIDUAL));
				}
				
			}else {
				if (!ConstantesSEFE.IMD_EST_VIGENTE.equals(Integer.valueOf(hojaIndependienteExistente.getEstado()))){
					//valida si hay disponible una hoja independiente para generar el rating individual
					if (!gestorHojaInd.hayDisponibleUnaHojaIndependiente(idCliente).booleanValue()) {
						throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.MSG14_MODELO_RATING_INDIVIDUAL));
					}
				
				}
			}
		}
		// Se obtienen las alertas en caso que existan
		ArrayList alertas = new ArrayList();
		ValidadorRating validador = new ValidadorRatingFinancieroImpl();
		ValidacionRatingFinanciero validacion;

		// Se realizan las validaciones respectivas, dependiendo de si es una modificacion a un rating financiero ya existente o una nuevo rating
		// financiero
		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRatingInd);
		if (esUnaModificacion(rtgIndividual, idVacCabecera)) {
			validacion = validador.validarModificacion(idCliente, idRatingInd, idVacCabecera, rtgIndividual.getIdBanca());

			alertas.addAll(validacion.getMensajes());
		} else {
			validacion = validador.validarIngreso(idCliente, idRatingInd, idVacCabecera, rtgIndividual.getIdBanca());

			alertas.addAll(validacion.getMensajes());
		}

		if (!alertas.isEmpty()) {
			// retorno.put(PARAM_ALERTAS, alertas);
			throw new BusinessOperationException((String) alertas.get(0));
		}

		// Se busca un rating financiero ya existente para el vaciado cabecera; en caso de existir uno se reutiliza; en caso contrario se genera un
		// nuevo rating
		rtgFinanciero = gestorRtgFinanciero.buscarRatingFinancieroPorIdVaciadoActual(idVacCabecera);
		// se pone el idrating individual en contexto para ser ocupado en el algoritmo generico
		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, rtgIndividual.getIdRating());

		retorno.put(ConstantesRating.ID_ESTADO, rtgIndividual.getIdEstado());
		retorno.put(PARAM_LISTADO_VACIADOS, vaciadosCandidatos);
		retorno.put(PARAM_RATING_FINANCIERO, rtgFinanciero);

		return retorno;
	}
	
	/*
	 * 20120615.marias
	 * 
	 * Valida que el vaciado seleccionado para el calculo del rating sea el mas reciente
	 * del tipo de vaciado seleccionado (el mas reciente individual, consolidado, u otro tipo)
	 */
	private boolean validarVaciadoEsSeleccionable(Vaciado vacCabecera, List vaciadosCandidatos, Integer idBanca) {
		// tomamos el tipo del vaciado cabecera
		Integer tipo = vacCabecera.getIdTpoVaciado();

		// se recorre la lista de vaciados candidatos
		Vaciado vaciadoMasReciente = null;
		Iterator iterator = vaciadosCandidatos.iterator();
		while (iterator.hasNext()) {
			Vaciado vac = (Vaciado) iterator.next();

			// para PYME solo se utilizan vaciados Diciembre con 6 o más meses
			//TODO: SE DEBE PARAMETRIZAR, ESTOY EN ESPERA DE LAS PARAMETRIZACION POR PARTE DE EDUARDO.
			if (ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
				boolean vaciadoMenor6Meses = vac.getMesesPer().intValue() < 6;

				Calendar cal = Calendar.getInstance();
				cal.setTime(vac.getPeriodo());
				boolean vaciadoEsDiciembre = (cal.get(Calendar.MONTH) == Calendar.DECEMBER);
				
				boolean parcialSoportado = new Boolean(ConfigDBManager.getValueAsString(ConstantesSEFE.RTG_FINANCIERO_CIERRE_PARCIAL_SOPORTADO, idBanca)).booleanValue();

				if (!vaciadoMenor6Meses && vac.getIdTpoVaciado().equals(tipo) && ConstantesSEFE.CLASIF_VACIADO_VIGENTE.equals(vac.getIdEstado())
						&& (vaciadoEsDiciembre || (!vaciadoEsDiciembre && parcialSoportado))) {
					vaciadoMasReciente = vac;
					break;
				}
			} else {
				// se identifica el mas reciente del tipo buscado
				if (vac.getIdTpoVaciado().equals(tipo) && ConstantesSEFE.CLASIF_VACIADO_VIGENTE.equals(vac.getIdEstado())) {
					vaciadoMasReciente = vac;
					break;
				}
			}
		}
		// es el vaciado cabecera el mas reciente de su tipo???
		return vacCabecera.getIdVaciado().equals(vaciadoMasReciente.getIdVaciado());
	}

	
	public Map generarRating(Long idRatingIndividual, String rutCliente, String loginUsuario) {
		GestorRating gestorRating = new GestorRatingImpl();
		GestorRatingFinanciero gestorRatingFinanciero = new GestorRatingFinancieroImpl();
		GestorRatingIndividual gestorRatingInd = new GestorRatingIndividualImpl();
		ServicioClientes servicioCliente = new ServicioClientesImpl();
		Vaciado vaciadoCalculoRating = null;
		Map retorno = new HashMap();
		Collection alertas = new ArrayList();
		

		Cliente cliente = servicioCliente.obtenerClientePorRut(rutCliente);
		Long idCliente = Long.valueOf(cliente.getClienteId());
		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRatingIndividual);
		RatingIndividual rtgIndividual = gestorRatingInd.buscarRatingIndividual(idCliente, idRatingIndividual);

		retorno.put(ConstantesRating.ID_ESTADO, rtgIndividual.getIdEstado());
		
		// Si el estado del rating individual es vigente o historico solo se muestra la informacion del rating financiero ya asociado
		if (!ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO.equals(rtgIndividual.getIdEstado())) {
			if(rtgIndividual == null || rtgIndividual.getIdRatingFinanciero() == null)
				throw new BusinessOperationException("No existe un rating financiero asociado al rating individual");
				
			retorno.put(PARAM_RATING_FINANCIERO, gestorRatingFinanciero.obtenerRating(rtgIndividual.getIdRatingFinanciero()));
			retorno.put(PARAM_LISTADO_VACIADOS, this.buscarVaciadosParaRating(rutCliente, rtgIndividual.getIdBanca()));
			return retorno;
		}

		// Si hay rating financiero asociado al rating individual, se obtiene ese rating y se realizan las validaciones
		if (rtgIndividual.getIdRatingFinanciero() != null) {
			Map params = getRatingFinancieroAsociado(cliente, rtgIndividual);
			RatingFinanciero rtgFinan = (RatingFinanciero) params.get(PARAM_RATING_FINANCIERO);

			// Se valida que el vaciado actualmente utilizado para el rating financiero, no haya sido modificado. En caso de ser un modificado
			// se genera un nuevo rating financiero
			if (rtgFinan.getIdVaciado0() != null) {
				ValidadorRating validador = new ValidadorRatingFinancieroImpl();

				Vaciado vac = getVaciado(rtgFinan.getIdVaciado0());
				Vaciado vac1 = getVaciado(rtgFinan.getIdVaciado1());
				Vaciado vac2 = getVaciado(rtgFinan.getIdVaciado2());

				if (validador.versionVaciadoValida(vac) && validador.versionVaciadoValida(vac1)  && validador.versionVaciadoValida(vac2)) {
					// Se retorna el componente de rating financiero encontrado y su informacion si es que es valido y su vaciado no ha sido modificado,
					// en caso contrario se continua con el flujo para que se genere un nuevo rating financiero con el vaciado modificado
					return params;
				}
			} else {
				// Si por alguna rara razon existe un componente rating financiero sin un vaciado se retorna lo que se encontro
				return params;
			}
			
		}

		// Se obtiene el vaciado a utilizar para generar rating financiero en caso que no exista
		if (ConstantesSEFE.BANCA_PYME.equals(rtgIndividual.getIdBanca())){
			vaciadoCalculoRating = gestorRating.buscarVaciadoParaRatingPYME(rutCliente, rtgIndividual.getIdBanca(), rtgIndividual);
		}
		// Se obtiene el vaciado a utilizar para generar rating financiero en caso que no exista
		else{
			vaciadoCalculoRating = gestorRating.buscarVaciadoParaRating(rutCliente, rtgIndividual.getIdBanca());
		}
		
		
		// Si no existen vaciados no se sigue con la funcionalidad y se retorna la coleccion de alertas
		if (vaciadoCalculoRating == null) {
			List vaciadosList = this.buscarVaciadosParaRating(rutCliente, rtgIndividual.getIdBanca());
			if(vaciadosList != null && vaciadosList.size() > 0){
				alertas.add(MessageManager.getError(ConstantesSEFE.KEY_RATING_MSG_NO_EXISTEN_VACIADOS_VIGENTES));
			}else{
				alertas.add(MessageManager.getError(ConstantesSEFE.KEY_RTG_FINAN_ALERTA_NO_EXISTEN_VACIADOS));
			}		

			retorno.put(PARAM_ALERTAS, alertas);
			retorno.put(PARAM_LISTADO_VACIADOS, vaciadosList);

			return retorno;
		}
		GestorHojaIndependiente gestorHoja = new GestorHojaIndependienteImpl();
		if (gestorHoja.bancaUsaHojaIndependiente(rtgIndividual.getIdBanca()).booleanValue()) {
			HojaIndependiente hojaIndependiente = gestorHoja.obtenerHojaIndependiente(idCliente, rtgIndividual.getIdRating());
			if (hojaIndependiente==null){
				if (!gestorHoja.hayDisponibleUnaHojaIndependiente(idCliente).booleanValue()) {
					alertas.add(MessageManager.getError(ConstantesSEFE.MSG14_MODELO_RATING_INDIVIDUAL));
					List vaciadosList = this.buscarVaciadosParaRating(rutCliente, rtgIndividual.getIdBanca());
					retorno.put(PARAM_LISTADO_VACIADOS, vaciadosList);
					retorno.put(PARAM_ALERTAS, alertas);
					if (IDBANCA_TP_BANCO.equals(rtgIndividual.getIdBanca())){	
						Integer val = new Integer(0);
						GestorCalificadoras gestor = new GestorCalificadorasImpl();
						if (vaciadoCalculoRating != null && vaciadoCalculoRating.getIdVaciado() != null){
							List valoresAnterior = gestor.obtenerValorComboSeleccionadoTier(vaciadoCalculoRating.getIdVaciado());
							if (valoresAnterior != null && !valoresAnterior.isEmpty()) {
								val = new Integer(1);
							}
							if (new Integer(0).equals(val)) {
								List vaciadosList1 = this.buscarVaciadosParaRating(rutCliente, rtgIndividual.getIdBanca());
								alertas.add(MessageManager.getError(ConstantesSEFE.MSG16_MODELO_RATING_INDIVIDUAL));
								retorno.put(PARAM_ALERTAS, alertas);
								retorno.put(PARAM_LISTADO_VACIADOS, vaciadosList1);
							return retorno;
							}
						} 
					}
					return retorno;
				}
				hojaIndependiente = gestorHoja.buscarHojaIndependiente(idCliente);
				hojaIndependiente.setIdRating(rtgIndividual.getIdRating());
				gestorHoja.actualizarHojaIndependiente(hojaIndependiente);
			}
		}
		
		//Requerimiento 7.4.42-4 Alerta para Bancos con vacions sin Tier definidos.
		if (IDBANCA_TP_BANCO.equals(rtgIndividual.getIdBanca())){	
			Integer val = new Integer(0);
			GestorCalificadoras gestor = new GestorCalificadorasImpl();
			if (vaciadoCalculoRating != null && vaciadoCalculoRating.getIdVaciado() != null){
				List valoresAnterior = gestor.obtenerValorComboSeleccionadoTier(vaciadoCalculoRating.getIdVaciado());
				if (valoresAnterior != null && !valoresAnterior.isEmpty()) {
					val = new Integer(1);
				}
				if (new Integer(0).equals(val)) {
					List vaciadosList = this.buscarVaciadosParaRating(rutCliente, rtgIndividual.getIdBanca());
					alertas.add(MessageManager.getError(ConstantesSEFE.MSG16_MODELO_RATING_INDIVIDUAL));
					retorno.put(PARAM_ALERTAS, alertas);
					retorno.put(PARAM_LISTADO_VACIADOS, vaciadosList);
				return retorno;
				}
			} 
		}
		
		// Se genera un nuevo rating financiero para el vaciado encontrado
		return generarNuevoRating(cliente, rtgIndividual, vaciadoCalculoRating, loginUsuario);
	}
	
	/*
	 * Busca un vaciado pero primero valida que el id del vaciado se distinto null.
	 */
	private Vaciado getVaciado(Long idVaciado) {
		if (idVaciado == null)
			return null;
		
		CatalogoVaciados cv = new CatalogoVaciadosImpl();
		return cv.buscarDatosGeneral(idVaciado);
	}

	/*
	 * Retorna el rating financiero asociado al rating individual y el conjunto de alertas, si es que existen
	 */
	private Map getRatingFinancieroAsociado(Cliente cliente, RatingIndividual ratingIndividual) {
		GestorRatingFinanciero gestorRatingFinanciero = new GestorRatingFinancieroImpl();
		ValidadorRating validadorRating = new ValidadorRatingFinancieroImpl();
		ValidacionRatingFinanciero validacion;
		Long idCliente = Long.valueOf(cliente.getClienteId());
		Collection alertas = new ArrayList();
		Map retorno = new HashMap();

		RatingFinanciero rtgFinanciero = gestorRatingFinanciero.obtenerRating(ratingIndividual.getIdRatingFinanciero());

		validacion = validadorRating.validarIngreso(idCliente, ratingIndividual.getIdRating(), rtgFinanciero.getIdVaciado0(), ratingIndividual.getIdBanca());

		alertas.addAll(validacion.getMensajes());
		GestorHojaIndependiente gestorHoja = new GestorHojaIndependienteImpl();
		if (gestorHoja.bancaUsaHojaIndependiente(ratingIndividual.getIdBanca()).booleanValue()) {
			HojaIndependiente hojaIndependiente = gestorHoja.obtenerHojaIndependiente(idCliente, ratingIndividual.getIdRating());
			if (!ConstantesSEFE.IMD_EST_VIGENTE.equals(Integer.valueOf(hojaIndependiente.getEstado()))){
				//valida si la hoja asociada al rating esta vigente
				alertas.add(MessageManager.getError(ConstantesSEFE.MSG15_MODELO_RATING_INDIVIDUAL));
			}
		}

		retorno.put(ConstantesRating.ID_ESTADO, ratingIndividual.getIdEstado());
		retorno.put(PARAM_LISTADO_VACIADOS, this.buscarVaciadosParaRating(cliente.getRut(), ratingIndividual.getIdBanca()));
		retorno.put(PARAM_RATING_FINANCIERO, rtgFinanciero);
		retorno.put(PARAM_ALERTAS, alertas);

		return retorno;
	}

	/*
	 * Genera un nuevo rating financiero utilizando el vaciado
	 */
	private Map generarNuevoRating(Cliente cliente, RatingIndividual ratingIndividual, Vaciado vaciado, String loginUsuario) {
		GestorRatingFinanciero gestorRatingFinanciero = new GestorRatingFinancieroImpl();
		ValidadorRating validadorRating = new ValidadorRatingFinancieroImpl();
		ValidacionRatingFinanciero validacion;
		Collection alertas = new ArrayList();
		Long idCliente = Long.valueOf(cliente.getClienteId());
		Map retorno = new HashMap();
		RatingFinanciero rtgFinanciero = null;

		validacion = validadorRating.validarIngreso(idCliente, ratingIndividual.getIdRating(), vaciado.getIdVaciado(), ratingIndividual.getIdBanca());

		// Si el nuevo ingreso es por que el vaciado actualmente utilizado en el rating financiero sufrio cambios (se genero clonado)
		// se le informa al usuario
		if (ratingIndividual.getIdRatingFinanciero() != null) {
			RatingFinanciero rtgFinanAct = gestorRatingFinanciero.obtenerRating(ratingIndividual.getIdRatingFinanciero());
			if (rtgFinanAct.getIdVaciado0() != null) {				
				Vaciado vac0 = getVaciado(rtgFinanAct.getIdVaciado0());
				Vaciado vac1 = getVaciado(rtgFinanAct.getIdVaciado1());
				Vaciado vac2 = getVaciado(rtgFinanAct.getIdVaciado2());
				
				if (!validadorRating.versionVaciadoValida(vac0) || !validadorRating.versionVaciadoValida(vac1) || !validadorRating.versionVaciadoValida(vac2)) {
					alertas.add(MessageManager.getMessage(ConstantesSEFE.MSG2_INTEGRIDAD_RATING_FINANCIERO));
				}
			}
			GestorHojaIndependiente gestorHoja = new GestorHojaIndependienteImpl();
			if (gestorHoja.bancaUsaHojaIndependiente(ratingIndividual.getIdBanca()).booleanValue()) {
				HojaIndependiente hojaIndependiente = gestorHoja.obtenerHojaIndependiente(idCliente, ratingIndividual.getIdRating());
				if (!ConstantesSEFE.IMD_EST_VIGENTE.equals(Integer.valueOf(hojaIndependiente.getEstado()))){
					//valida si la hoja asociada al rating esta vigente
					alertas.add(MessageManager.getError(ConstantesSEFE.MSG15_MODELO_RATING_INDIVIDUAL));
				}
			}
		}
		
		// Se agregan las alertas en caso que existan
		alertas.addAll(validacion.getMensajes());

		if (!alertas.isEmpty()) {
			retorno.put(PARAM_ALERTAS, alertas);
		}
		
		GestorUsuarios gestorUsuarios = new GestorUsuariosImpl();
		Usuario usr = gestorUsuarios.obtenerPrimerUsuario(loginUsuario);

		if(validacion.getExistenPeriodosVaciado() && validacion.getExisteSegmentoVenta()){
			rtgFinanciero = gestorRatingFinanciero.calcularRating(cliente.getRut(), ratingIndividual.getIdBanca(), vaciado.getIdVaciado(), loginUsuario);
	
			rtgFinanciero.setResponsable(loginUsuario);
			rtgFinanciero.setIdUsuario(usr.getUsuarioId());
			rtgFinanciero.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		} else {
			rtgFinanciero = new RatingFinanciero();
			rtgFinanciero.setIdVaciado0(vaciado.getIdVaciado());
			rtgFinanciero.setResponsable(usr.getCodigoUsuario());
			rtgFinanciero.setIdUsuario(usr.getUsuarioId());
			rtgFinanciero.setFechaBalance(vaciado.getPeriodo());
		}
		retorno.put(ConstantesRating.ID_ESTADO, ratingIndividual.getIdEstado());
		retorno.put(PARAM_LISTADO_VACIADOS, this.buscarVaciadosParaRating(cliente.getRut(), ratingIndividual.getIdBanca()));
		retorno.put(PARAM_RATING_FINANCIERO, rtgFinanciero);

		return retorno;
	}

	/*
	 * Se evalua si lo que se esta realizando es una modificacion al rating financiero ya asociado, es decir se esta confirmando el rating financiero
	 * ya confirmado previamente.
	 * 
	 * Si el vaciado informado como cabecera es el mismo que el del rating financiero ya asociado al rating individual, es una modificacion, en caso
	 * contrario es un nuevo ingreso que reemplazara al rating financiero existente.
	 */
	private boolean esUnaModificacion(RatingIndividual ratingIndividual, Long idVaciadoSeleccionado) {
		GestorRatingFinanciero gestorRatingFinanciero = new GestorRatingFinancieroImpl();

		if (ratingIndividual.getIdRatingFinanciero() != null) {
			RatingFinanciero rtgFinanciero = gestorRatingFinanciero.obtenerRating(ratingIndividual.getIdRatingFinanciero());

			// Es una modificacion si el vaciado informado es igual al vaciado del rating financiero ya asociado al individual.
			return (rtgFinanciero.getIdVaciado0().equals(idVaciadoSeleccionado));
		}

		return false;
	}

	public List buscarVaciadosParaRating(String rutCliente, Integer idBanca) {
		return (new GestorRatingImpl()).buscarListaVaciadosParaRating(rutCliente, idBanca);
	}

	public List generarReporte(String rutCliente, Long idRatingInd, Long idVacCabecera) {
		// TODO Auto-generated method stub
		return null;
	}

	public Long confirmarProyeccion(String rutCte, Long idRtgInd, Long idRtgFin, Long idRtgProy, String logUsu) {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public Vaciado consultarProyeccion(Long idRtgProy) {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public Vaciado crearVaciadoProyectado(String rutCte, Long idRtgInd, Long idRtgFin, String logUsu) {
		// TODO Apéndice de método generado automáticamente
		return null;
	}

	public Vaciado generarProyeccion(String rutCte, Long idRtgInd, Long idRtgFin, Long idRtgProy, String logUsu, Long idVac, Collection cuentas) {
		// TODO Apéndice de método generado automáticamente
		return null;
	}
	

}
