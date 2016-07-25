package com.bch.sefe.rating.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.ErrorMessagesSEFE;
import com.bch.sefe.agricola.srv.GestorAgricola;
import com.bch.sefe.agricola.srv.impl.GestorAgricolaImpl;
import com.bch.sefe.comun.ServicioCalculo;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.ServicioCalculoImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.ConsultaServicios;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.impl.ConsultaServiciosImplCache;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Archivo;
import com.bch.sefe.comun.vo.PeriodoRating;
import com.bch.sefe.comun.vo.RatingProyectado;
import com.bch.sefe.comun.vo.RatingProyectadoCGE;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.exception.SEFEException;
import com.bch.sefe.rating.ConstantesRating;
import com.bch.sefe.rating.ServicioRatingProyectado;
import com.bch.sefe.rating.srv.AlgoritmoRatingFinanciero;
import com.bch.sefe.rating.srv.GestorAlertasRtgIndividual;
import com.bch.sefe.rating.srv.GestorComponentesRating;
import com.bch.sefe.rating.srv.GestorFortalezasDebilidades;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.GestorRatingProyectado;
import com.bch.sefe.rating.srv.impl.GestorComponentesRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorFortalezasDebilidadesImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingFinancieroImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingProyectadoImpl;
import com.bch.sefe.rating.srv.impl.RatingUtil;
import com.bch.sefe.rating.vo.BalanceInmobiliario;
import com.bch.sefe.rating.vo.FlagCuentaProyeccion;
import com.bch.sefe.rating.vo.IndicadorBi;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.PlantillaRating;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.servicios.Contexto;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.vaciados.ServicioVaciados;
import com.bch.sefe.vaciados.ValidadorVaciados;
import com.bch.sefe.vaciados.impl.ServicioVaciadosImpl;
import com.bch.sefe.vaciados.impl.ValidadorVaciadosImpl;
import com.bch.sefe.vaciados.srv.GestorPlanCuentas;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorPlanCuentasImpl;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.srv.impl.POIExcelReader;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;

public class ServicioRatingProyectadoImpl implements ServicioRatingProyectado {
	final static Logger log = Logger.getLogger(ServicioRatingProyectadoImpl.class);

	/*
	 * Crea una instancia de vaciado para ser utilizado en el rating proyectado
	 * (sin Javadoc)
	 * 
	 * @see
	 * com.bch.sefe.rating.ServicioRatingProyectado#crearProyectado(java.lang
	 * .String, java.lang.Long, java.lang.Long, java.lang.String)
	 */
	public RatingProyectado crearProyectado(String rutCliente, Long idRating, Long idFinan, String logUsu) {
		if (log.isDebugEnabled()) {
			log.debug("Creando vaciado para rating proyectado...");
		}
		GestorVaciados gestorVaciados = new GestorVaciadosImpl();
		RatingProyectadoCGE proyeccion = new RatingProyectadoCGE();
		Long idCte = buscarIdClientePorRut(rutCliente);
		Long idUsr = buscarIdUsuarioPorNombre(logUsu);
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndiv = gestorIndiv.consultaRatingSugerido(idCte, idRating);
		idFinan = ratingIndiv.getIdRatingFinanciero();

		if (ratingIndiv.getIdRatingProyectado() != null) {
			return consultarProyeccion(rutCliente, idRating, ratingIndiv.getIdRatingProyectado(), logUsu, null);
		}

		// se verifica el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		if (idFinan == null) {
			if (log.isInfoEnabled()) {
				log.info("Error al crear rating proyectado - " + MessageManager.getMessage(ConstantesSEFE.MSG1_MODELO_RATING_PROYECTADO));
			}
			proyeccion.agregarMensaje(MessageManager.getMessage(ConstantesSEFE.MSG1_MODELO_RATING_PROYECTADO));
			return proyeccion;
		}

		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idFinan);
		if (rtgFinanciero == null) {
			if (log.isInfoEnabled()) {
				log.info("Error al crear rating proyectado - " + MessageManager.getMessage(ConstantesSEFE.MSG1_MODELO_RATING_PROYECTADO));
			}
			proyeccion.agregarMensaje(MessageManager.getMessage(ConstantesSEFE.MSG1_MODELO_RATING_PROYECTADO));
			return proyeccion;
		}

		// se recupera la banca del rating
		Integer idBanca = ratingIndiv.getIdBanca();
		GestorRatingProyectado gestorProy = new GestorRatingProyectadoImpl();
		List vaciadosRatingProy = null;

		try {
			vaciadosRatingProy = gestorProy.buscarVaciadosRatingProyectado(rtgFinanciero, idBanca, ratingIndiv);
			String mensaje = validarVigenciaProyectado(ratingIndiv, rutCliente);
			 if(mensaje != null)
			 {
				 proyeccion.agregarMensaje(mensaje);
				 return proyeccion;
			 }
		} catch (SEFERatingProyectadoException ex) {
			if (log.isInfoEnabled()) {
				log.info("Error al determinar la base de proyeccion - " + ex);
			}

			proyeccion.agregarMensaje(MessageManager.getMessage(ex.getMessage()));
			return proyeccion;
		}

		// el ultimo vaciado anual es el primer elemento??
		Vaciado ultimoCierreAnual = (Vaciado) vaciadosRatingProy.get(0);
		Vaciado penultimoCierreAnual = (Vaciado) vaciadosRatingProy.get(1);
		Vaciado cierreParcial = null;
		Map ctxPeriodo = new HashMap();
		// con 3 vaciados en la lista, hay cierre parcial y el ultimo cierre
		// anual es el segundo item de la lista

		if (!gestorProy.esCierreAnual(ultimoCierreAnual)) {
			Vaciado vaciadoX3 = null;
			if (vaciadosRatingProy.size() == 3) {
				penultimoCierreAnual = (Vaciado) vaciadosRatingProy.get(2);
				vaciadoX3 = gestorVaciados.buscarVaciadoAnterior(penultimoCierreAnual.getIdVaciado());
				ctxPeriodo.put(ConstantesSEFE.CTX_PERIODO_X_2, penultimoCierreAnual.getIdVaciado());
			} else {
				penultimoCierreAnual = null;
			}
			ultimoCierreAnual = (Vaciado) vaciadosRatingProy.get(1);
			cierreParcial = (Vaciado) vaciadosRatingProy.get(0);
			if (vaciadoX3 != null) {
				ctxPeriodo.put(ConstantesSEFE.CTX_PERIODO_X_3, vaciadoX3.getIdVaciado());
			}
			ctxPeriodo.put(ConstantesSEFE.CTX_PERIODO_X_1, ultimoCierreAnual.getIdVaciado());
			ctxPeriodo.put(ConstantesSEFE.CTX_PERIODO_X, cierreParcial.getIdVaciado());

		} else {
			ctxPeriodo.put(ConstantesSEFE.CTX_NO_PERIODO_PARCIAL, new Integer(0));
			ctxPeriodo.put(ConstantesSEFE.CTX_PERIODO_X_2, penultimoCierreAnual.getIdVaciado());
			ctxPeriodo.put(ConstantesSEFE.CTX_PERIODO_X_1, ultimoCierreAnual.getIdVaciado());
		}
		Vaciado vaciadoProyectado = gestorProy.crearVaciadoProyectado(ultimoCierreAnual, idCte, idUsr, idRating, idFinan);

		ctxPeriodo.put(ConstantesSEFE.CTX_PERIODO_P, vaciadoProyectado.getIdVaciado());
		if (ultimoCierreAnual.getIdMoneda().equals(ConstantesSEFE.ID_CLASIF_MONEDA_CLP)) {
			ConsultaServicios consulta = new ConsultaServiciosImplCache();
			Double ipc1 = consulta.consultaIPC(obtenerFechaIPC(ultimoCierreAnual.getPeriodo()));
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X1, ipc1);
			if (penultimoCierreAnual != null) {
				Double ipc2 = consulta.consultaIPC(obtenerFechaIPC(penultimoCierreAnual.getPeriodo()));
				Double ajusteIPC = new Double(ipc1.doubleValue() / ipc2.doubleValue() - 1.0);
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X2, ipc2);
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_PROY_AJUSTE_IVA, ajusteIPC);
			} else {
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X2, null);
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_PROY_AJUSTE_IVA, null);
			}

		} else {
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X1, ConstantesSEFE.DOUBLE_UNO);
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X2, ConstantesSEFE.DOUBLE_UNO);
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_PROY_AJUSTE_IVA, ConstantesSEFE.DOUBLE_UNO);
		}
		if (log.isInfoEnabled()) {
			log.info("Calculando vaciado proyectado periodo " + vaciadoProyectado.getPeriodo());
		}
		// ctxPeriodo.put(ConstantesSEFE.PERIODO_EVALUACION_FORMULA,
		// ConstantesSEFE.PERIODO_PROYECTADO);
		this.calcularCuentasVaciados(vaciadoProyectado.getIdVaciado(), ctxPeriodo);

		// se crean los periodos y se pasan al rating proyectado
		PeriodoRating periodoP = new PeriodoRating();
		PeriodoRating periodo0 = new PeriodoRating();
		PeriodoRating periodo1 = new PeriodoRating();
		PeriodoRating periodo2 = new PeriodoRating();

		periodoP.setPeriodo(vaciadoProyectado.getPeriodo());
		periodoP.setVaciado(vaciadoProyectado);
		if (cierreParcial == null) {
			periodo0.setPeriodo(ultimoCierreAnual.getPeriodo());
			periodo0.setVaciado(ultimoCierreAnual);

			periodo1.setPeriodo(penultimoCierreAnual.getPeriodo());
			periodo1.setVaciado(penultimoCierreAnual);
		} else {
			periodo0.setPeriodo(cierreParcial.getPeriodo());
			periodo0.setVaciado(cierreParcial);

			periodo1.setPeriodo(ultimoCierreAnual.getPeriodo());
			periodo1.setVaciado(ultimoCierreAnual);
			if (penultimoCierreAnual != null) {
				periodo2.setPeriodo(penultimoCierreAnual.getPeriodo());
				periodo2.setVaciado(penultimoCierreAnual);
			}

		}

		proyeccion.setPeriodoProy(periodoP);
		proyeccion.setPeriodo0(periodo0);
		proyeccion.setPeriodo1(periodo1);
		proyeccion.setPeriodo2(periodo2);

		RatingFinanciero ratingBase = new RatingFinanciero();

		// se remueve el id de rating para que se genere uno nuevo para el
		// proyectado
		ratingBase.setIdUsuario(idUsr);
		ratingBase.setIdVaciado0(proyeccion.getPeriodo0().getVaciado().getIdVaciado());
		ratingBase.setIdVaciado1(proyeccion.getPeriodo1().getVaciado().getIdVaciado());
		if (proyeccion.getPeriodo2().getVaciado() != null) {
			ratingBase.setIdVaciado2(proyeccion.getPeriodo2().getVaciado().getIdVaciado());
		}
		ratingBase.setIdVacProyectado(periodoP.getVaciado().getIdVaciado());
		ratingBase.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		ratingBase.setFechaRating(new Date());
		ratingBase.setFechaBalance(proyeccion.getPeriodo0().getVaciado().getPeriodo());
		ratingBase.setFechaBalance1(proyeccion.getPeriodo1().getVaciado().getPeriodo());
		if (proyeccion.getPeriodo2().getVaciado() != null) {
			ratingBase.setFechaBalance2(proyeccion.getPeriodo0().getVaciado().getPeriodo());
		}
		ratingBase.setNumeroMeses(proyeccion.getPeriodo0().getVaciado().getMesesPer());

		MatrizFinanciera matriz = gestorFinanciero.obtenerMatrizFinanciera(rtgFinanciero.getIdMatriz());
		if (matriz.getIdMatrizProy() == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_PROY_ERROR_MATRIZ_VIGENTE_NO_EXISTE));
		}
		ratingBase.setIdMatriz(matriz.getIdMatrizProy());

		// se crea la instancia del rating financiero proyectado
		RatingFinanciero ratingProyectado = gestorFinanciero.grabarRatingProyectado(ratingBase);

		if (log.isInfoEnabled()) {
			log.info("Creando rating proyectado #" + ratingProyectado.getIdRating());
		}

		this.poblarCuentasProyectado(proyeccion, ConstantesRating.MODO_CONSULTA_PROYECTAR);
		proyeccion.setModoConsulta(ConstantesRating.MODO_CONSULTA_PROYECTAR);
		proyeccion.setRatingFinanciero(ratingProyectado);

		return proyeccion;
	}
	
	private String validarVigenciaProyectado(RatingIndividual rtgInd, String rutCliente)
	{		
		GestorAlertasRtgIndividual gestorAlertas = new GestorAlertasRtgIndividualImpl();
		List alertas = gestorAlertas.obtenerAlertasRtgIndividualModelo(rtgInd.getIdRating(), rutCliente, rtgInd.getIdBanca());
				
		if(rtgInd.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO) && rtgInd.getIdRatingProyectado() != null)
		{
			for (int i = 0; i < alertas.size(); ++i) {
				String mensaje = (String) alertas.get(i);
				if(mensaje.equals(ConstantesSEFE.MSG_ALERTA_VALIDACION_PROYECTADO) || mensaje.equals(ConstantesSEFE.MSG_ALERTA_VALIDACION_VERSION_VACIADOS))
				{
					return mensaje;
				}
			}
		}
		return null;
	}

	public RatingProyectado generarProyeccion(String rutCliente, Long idRating, Long idProy, String logUsu, Collection cuentas) {
		ValidadorVaciados validador = new ValidadorVaciadosImpl();
		GestorVaciados gestorVaciados = new GestorVaciadosImpl();
		RatingProyectadoCGE proyeccion = new RatingProyectadoCGE();
		GestorRatingProyectado gestorProy = new GestorRatingProyectadoImpl();

		if (log.isInfoEnabled()) {
			log.info("> > > > Iniciando generar proyeccion rating # " + idProy);
		}

		// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);
		List listaVaciados = new ArrayList();
		;

		if (log.isInfoEnabled()) {
			log.info("> > > > Recuperando rating proyectado # " + idProy + " con nota " + rtgFinanciero.getNotaFinanciera());
		}

		Vaciado vacProy = gestorVaciados.buscarVaciado(rtgFinanciero.getIdVacProyectado());
		Vaciado ultimoCierreAnual = null;
		Vaciado penultimoCierreAnual = null;
		// si hay 2 vaciados, el primero es el cierre anual; en caso contrario,
		// el segundo
		if (gestorProy.esCierreAnual(gestorVaciados.buscarVaciado(rtgFinanciero.getIdVaciado0()))) {
			ultimoCierreAnual = gestorVaciados.buscarVaciado(rtgFinanciero.getIdVaciado0());
			penultimoCierreAnual = gestorVaciados.buscarVaciado(rtgFinanciero.getIdVaciado1());
			listaVaciados.add(ultimoCierreAnual);
			listaVaciados.add(penultimoCierreAnual);
		} else {

			ultimoCierreAnual = gestorVaciados.buscarVaciado(rtgFinanciero.getIdVaciado1());
			penultimoCierreAnual = gestorVaciados.buscarVaciado(rtgFinanciero.getIdVaciado2());
			listaVaciados.add(gestorVaciados.buscarVaciado(rtgFinanciero.getIdVaciado0()));
			listaVaciados.add(ultimoCierreAnual);
			listaVaciados.add(penultimoCierreAnual);
		}
		// verifica si los vaiciados utilizados en la proyeccion tiene algun
		// cambio
		validador.validarVaciadosVigentesCambiados(listaVaciados, ErrorMessagesSEFE.MSG7_VALIDAR_RATING_PROYECTADO);
		if (log.isInfoEnabled()) {
			log.info("> > > > Vaciado proyectado id # " + idProy + " Periodo " + vacProy.getPeriodo());
		}

		// cuando se modifica el escenario del vaciado se modifica el vaciado a
		// estado EN CURSO
		ServicioVaciados servVac = new ServicioVaciadosImpl();
		vacProy = servVac.cambiarEstadoEncurso(vacProy.getIdVaciado());

		// se genera un mapa con las cuentas ingresadas por el usuario
		// las que se utlizarán en las operaciones de proyeccion
		if (log.isInfoEnabled()) {
			log.info("> > > > Poniendo cuentas en contexto de calculo....");
		}
		Map ctx = this.crearContextoDeCuentas(cuentas, vacProy);
		if (!gestorProy.esCierreAnual(gestorVaciados.buscarVaciado(rtgFinanciero.getIdVaciado0()))) {
			// if (rtgFinanciero.getIdVaciado2() != null) {
			Vaciado vaciaodX3 = null;
			if (rtgFinanciero.getIdVaciado2() != null) {
				vaciaodX3 = gestorVaciados.buscarVaciadoAnterior(rtgFinanciero.getIdVaciado2());
				ctx.put(ConstantesSEFE.CTX_PERIODO_X_2, rtgFinanciero.getIdVaciado2());
			}
			if (vaciaodX3 != null) {
				ctx.put(ConstantesSEFE.CTX_PERIODO_X_3, vaciaodX3.getIdVaciado());
			}
			ctx.put(ConstantesSEFE.CTX_PERIODO_X_1, rtgFinanciero.getIdVaciado1());
			ctx.put(ConstantesSEFE.CTX_PERIODO_X, rtgFinanciero.getIdVaciado0());
		} else {
			ctx.put(ConstantesSEFE.CTX_NO_PERIODO_PARCIAL, new Integer(0));
			ctx.put(ConstantesSEFE.CTX_PERIODO_X_2, rtgFinanciero.getIdVaciado1());
			ctx.put(ConstantesSEFE.CTX_PERIODO_X_1, rtgFinanciero.getIdVaciado0());
		}

		if (log.isInfoEnabled()) {
			log.info("> > > > Calculando las cuentas para el vaciado proyectado");
		}
		// se recalcula la proyeccion de vaciado
		Map ctxPeriodo = new HashMap();
		if (ultimoCierreAnual.getIdMoneda().equals(ConstantesSEFE.ID_CLASIF_MONEDA_CLP)) {
			ConsultaServicios consulta = new ConsultaServiciosImplCache();
			Double ipc1 = consulta.consultaIPC(obtenerFechaIPC(ultimoCierreAnual.getPeriodo()));
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X1, ipc1);
			if (penultimoCierreAnual != null) {
				Double ipc2 = consulta.consultaIPC(obtenerFechaIPC(penultimoCierreAnual.getPeriodo()));
				Double ajusteIPC = new Double(ipc1.doubleValue() / ipc2.doubleValue() - 1.0);
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X2, ipc2);
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_PROY_AJUSTE_IVA, ajusteIPC);
			} else {
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X2, null);
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_PROY_AJUSTE_IVA, null);
			}
		} else {
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X1, ConstantesSEFE.DOUBLE_UNO);
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X2, ConstantesSEFE.DOUBLE_UNO);
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_PROY_AJUSTE_IVA, ConstantesSEFE.DOUBLE_UNO);
		}

		if (log.isInfoEnabled()) {
			log.info("> > > > Calculando el vaciado proyectado > > > > ");
		}
		// se calculan los vaciados
		if (log.isInfoEnabled()) {
			log.info("Calculando vaciado proyectado periodo " + vacProy.getPeriodo());
		}
		ctxPeriodo.put(ConstantesSEFE.PERIODO_EVALUACION_FORMULA, ConstantesSEFE.PERIODO_PROYECTADO);
		ctx.putAll(ctxPeriodo);
		this.calcularCuentasVaciados(vacProy.getIdVaciado(), ctx);

		if (log.isInfoEnabled()) {
			log.info("> > > > Calculando el rating proyectado");
		}
		// aqui se calcula el rating proyectado....
		proyeccion = (RatingProyectadoCGE) this.calcularProyeccion(rutCliente, idRating, idProy, logUsu);

		aplicarAjustePorExcesoRespectoARtgFinanciero(buscarIdClientePorRut(rutCliente), idRating, proyeccion.getRatingFinanciero());

		if (log.isInfoEnabled()) {
			log.info("> > > > Calculando el rating proyectado...nota proyectada " + proyeccion.getRatingFinanciero().getNotaFinanciera());
		}

		Long idCte = buscarIdClientePorRut(rutCliente);
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual ratingInd = gestorIndiv.buscarRatingIndividual(idCte, idRating);
		
		proyeccion.getRatingFinanciero().setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		RatingFinanciero ratingFinanciero = gestorFinanciero.actualizarRatingProyectado(ratingInd.getIdBanca(), proyeccion.getRatingFinanciero());

		// se guardan los valores intermedios de los calculos y puntajes
		// si no hay rating financiero para el vaciado cabecera entonces se
		// generar un nuevo calculo y nuevo rating financiero
		rtgFinanciero = gestorFinanciero.grabarEvaluacionesProyectado(proyeccion.getRatingFinanciero());

		if (log.isInfoEnabled()) {
			log.info("> > > > Actualizando cuentas con los valores ingresados ");
		}
		// se sobreescriben la cuentas con los valores ingresados
		this.actualizarValoresCuentas(vacProy, ultimoCierreAnual, cuentas);

		if (log.isInfoEnabled()) {
			log.info("> > > > Rating proyectado # " + idProy);
		}

		proyeccion = (RatingProyectadoCGE) this.consultarProyeccion(rutCliente, idRating, idProy, logUsu,
				ConstantesRating.MODO_CONSULTA_CONFIRMAR);

		if (log.isInfoEnabled()) {
			log.info("> > > > Rating proyectado " + proyeccion.getRatingFinanciero().getNotaFinanciera());
		}

		// ============================================================================
		// se actualiza el rating individual con el valor del proyectado
		gestorIndiv.actualizarRatingProyectado(idCte, idRating, proyeccion.getRatingFinanciero());

		// el rating individual borra nota modelo, ponderado y ajustado por
		// tamaño
		ratingInd.setRatingFinalSugerido(null);
		ratingInd.setRatingPreliminar1(null);
		ratingInd.setRatingFinal(null);
		gestorIndiv.actualizarRatingIndividual(ratingInd);
		// ============================================================================

		return proyeccion;
	}

	/*
	 * Crea un mapa con las cuentas ingresadas por el usuario Estas cuentas se
	 * ponen en el contexto de calculo para el vaciado proyectado
	 */
	private Map crearContextoDeCuentas(Collection cuentas, Vaciado vac) {
		final String PREFIJO_CTX = "PROY_";
		Map ctx = new HashMap();

		if (cuentas == null || cuentas.isEmpty()) {
			return ctx;
		}

		GestorRatingProyectado gestor = new GestorRatingProyectadoImpl();
		List cuentasVac = gestor.buscarCuentasPorPeriodo(vac.getIdVaciado());
		// crea un mapa con las cuentas del vaciado
		Object[] arrayVac = cuentasVac.toArray();
		Map mapVac = new HashMap();
		for (int i = 0; i < arrayVac.length; ++i) {
			Cuenta cta = (Cuenta) arrayVac[i];
			mapVac.put(cta.getNumCta(), cta);
		}

		Object[] array = cuentas.toArray();
		for (int i = 0; i < array.length; ++i) {
			Cuenta cta = (Cuenta) array[i];
			// se busca la cuenta en el mapa del vaciado
			Cuenta ctaVac = (Cuenta) mapVac.get(cta.getNumCta());
			ctx.put(PREFIJO_CTX + ctaVac.getCodigoCuenta(), cta.getMonto());
		}

		return ctx;
	}

	/*
	 * Este metodo cambia el estado a VIGENTE para el vaciado y rating
	 * proyectado
	 */
	public RatingProyectado confirmarProyeccion(String rutCliente, Long idRating, Long idProy, String logUsu) {
		RatingProyectadoCGE proyeccion = new RatingProyectadoCGE();
		// ValidadorVaciados validador = new ValidadorVaciadosImpl();

		// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);
		GestorVaciados gestorVac = new GestorVaciadosImpl();
		Vaciado vacProy = gestorVac.buscarVaciado(rtgFinanciero.getIdVacProyectado());
		vacProy = gestorFinanciero.cambiarProyectadoAVigente(vacProy.getIdVaciado());

		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerPrimerUsuario(logUsu);

		rtgFinanciero.setResponsable(usr.getCodigoUsuario());
		rtgFinanciero.setIdUsuario(usr.getUsuarioId());
		rtgFinanciero.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);

		Long idCte = buscarIdClientePorRut(rutCliente);
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual rtgInd = gestorIndiv.buscarRatingIndividual(idCte, idRating);

		rtgFinanciero = gestorFinanciero.actualizarRatingProyectado(rtgInd.getIdBanca(), rtgFinanciero);

		if (log.isInfoEnabled()) {
			log.info(MessageFormat
					.format("Rating proyectado #{0} confirmado!", new String[] { rtgFinanciero.getIdProyectado().toString() }));
		}
		// ============================================================================
		// se actualiza el rating individual
		gestorIndiv.actualizarRatingProyectado(idCte, idRating, rtgFinanciero);

		// se calculan los valores del rating individual
		gestorIndiv.calcularRating(idCte, idRating);
		// ============================================================================

		proyeccion = (RatingProyectadoCGE) this.consultarProyeccion(rutCliente, idRating, idProy, logUsu,
				ConstantesRating.MODO_CONSULTA_DEFAULT);

		//Requerimiento Fortaleza y Debilidades
		
//		RatingIndividual rtgIndividual = gestorIndiv.buscarRatingIndividual(idCte, idRating);

//		GestorFortalezasDebilidades gestFortDeb = new GestorFortalezasDebilidadesImpl();
//		gestFortDeb.borrarFortalezasYDebilidades(rtgIndividual.getIdCliente(), rtgIndividual.getIdRating(),
//				ConstantesSEFE.AMBITO_PROYECTADO);
//		gestFortDeb.agregarFortalezasDebilidadesRtgProyectado(rtgIndividual);

		return proyeccion;
	}

	public void calcularHistoriaProyeccion(Vaciado penultimoCierreAnual, Vaciado ultimoCierreAnual, Vaciado cierreParcial) {
		Map ctxPeriodo = new HashMap();
		if (ultimoCierreAnual.getIdMoneda().equals(ConstantesSEFE.ID_CLASIF_MONEDA_CLP)) {
			ConsultaServicios consulta = new ConsultaServiciosImplCache();
			Double ipc1 = consulta.consultaIPC(obtenerFechaIPC(ultimoCierreAnual.getPeriodo()));
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X1, ipc1);
			if (penultimoCierreAnual != null) {
				Double ipc2 = consulta.consultaIPC(obtenerFechaIPC(penultimoCierreAnual.getPeriodo()));
				Double ajusteIPC = new Double(ipc1.doubleValue() / ipc2.doubleValue() - 1.0);
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X2, ipc2);
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_PROY_AJUSTE_IVA, ajusteIPC);
			} else {
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X2, null);
				ctxPeriodo.put(ConstantesSEFE.CTX_RATING_PROY_AJUSTE_IVA, null);
			}
		} else {
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X1, ConstantesSEFE.DOUBLE_UNO);
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X2, ConstantesSEFE.DOUBLE_UNO);
			ctxPeriodo.put(ConstantesSEFE.CTX_RATING_PROY_AJUSTE_IVA, ConstantesSEFE.DOUBLE_UNO);
		}

		// se calculan los vaciados
		if (log.isInfoEnabled()) {
			log.info("Calculando vaciado proyectado periodo DIC X-2");
		}

		// ctxPeriodo.put(ConstantesSEFE.PERIODO_EVALUACION_FORMULA,
		// ConstantesSEFE.PERIODO_DIC_X2);
		// this.calcularCuentasVaciados(penultimoCierreAnual.getIdVaciado(),
		// ctxPeriodo);

		// ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X1, null);
		// ctxPeriodo.put(ConstantesSEFE.CTX_RATING_IPC_PREV_X2, null);
		ctxPeriodo.put(ConstantesSEFE.CTX_RATING_PROY_AJUSTE_IVA, null);
		if (log.isInfoEnabled()) {
			log.info("Calculando vaciado proyectado periodo DIC X-1");
		}

		// ctxPeriodo.put(ConstantesSEFE.PERIODO_EVALUACION_FORMULA,
		// ConstantesSEFE.PERIODO_DIC_X1);
		// this.calcularCuentasVaciados(ultimoCierreAnual.getIdVaciado(),
		// ctxPeriodo);

		// existe el periodo de cierre parcial
		// if (cierreParcial != null) {
		// if (log.isInfoEnabled()) {
		// log.info("Calculando vaciado proyectado periodo MES X");
		// }
		// ctxPeriodo.put(ConstantesSEFE.PERIODO_EVALUACION_FORMULA,
		// ConstantesSEFE.PERIODO_MES_X);
		// calcularCuentasVaciados(cierreParcial.getIdVaciado(), ctxPeriodo);
		// }
	}

	public RatingProyectado consultarProyeccion(String rutCliente, Long idRating, Long idProy, String logUsu, String modo) {
		RatingProyectadoCGE proyeccion = new RatingProyectadoCGE();

		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();
		Long idCte = buscarIdClientePorRut(rutCliente);
		RatingIndividual ratingInd = gestorRating.buscarRatingIndividual(idCte, idRating);

		// se busca el financiero vigente para verificar cambios...
		GestorComponentesRating gestorComp = new GestorComponentesRatingImpl();
		RatingFinanciero finanVigente = gestorComp.buscarRatingFinacieroVigente(idCte, ratingInd.getIdBanca());

		// si el id de rating finan registrado en individual es diferente
		// del id de rating del financierio vigente, hay cambios
		// y el proyectado se debe generar nuevamente
		if (idProy == null) {
			return crearProyectado(rutCliente, idRating, finanVigente.getIdRating(), logUsu);
		}

		// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);

		GestorVaciados gestorVac = new GestorVaciadosImpl();
		Vaciado vacProy = gestorVac.buscarVaciado(rtgFinanciero.getIdVacProyectado());

		// se crean los periodos y se pasan al rating proyectado
		PeriodoRating periodoP = new PeriodoRating();
		PeriodoRating periodo0 = new PeriodoRating();
		PeriodoRating periodo1 = new PeriodoRating();
		PeriodoRating periodo2 = new PeriodoRating();
		Vaciado vac0 = gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado0());
		Vaciado vac1 = gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado1());
		Vaciado vac2 = null;
		if (rtgFinanciero.getIdVaciado2() != null) {
			vac2 = gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado2());
		}

		/*****************************************************************/
		// se calcula la histora de la proyeccion
		// no hay cierre parcial
		// if
		// (ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO.equals(ratingInd.getIdEstado()))
		// {
		// if (vac2 == null) {
		// calcularHistoriaProyeccion(vac1, vac0, vac2);
		// } else {
		// calcularHistoriaProyeccion(vac2, vac1, vac0);
		// }
		// }
		/*****************************************************************/

		periodoP.setPeriodo(vacProy.getPeriodo());
		periodoP.setVaciado(vacProy);
		// no hay vaciados con periodos parciales
		if (vac2 == null) {
			periodo0.setPeriodo(vac0.getPeriodo());
			periodo0.setVaciado(vac0);

			periodo1.setPeriodo(vac1.getPeriodo());
			periodo1.setVaciado(vac1);
		} else {
			periodo0.setPeriodo(vac0.getPeriodo());
			periodo0.setVaciado(vac0);

			periodo1.setPeriodo(vac1.getPeriodo());
			periodo1.setVaciado(vac1);
			if (vac2 != null) {
				periodo2.setPeriodo(vac2.getPeriodo());
				periodo2.setVaciado(vac2);
			}
		}

		proyeccion.setPeriodoProy(periodoP);
		proyeccion.setPeriodo0(periodo0);
		proyeccion.setPeriodo1(periodo1);
		proyeccion.setPeriodo2(periodo2);
		proyeccion.setRatingFinanciero(rtgFinanciero);

		String modoConsulta = modo;
		if (modoConsulta == null) {
			if (rtgFinanciero.getEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE)) {
				modoConsulta = ConstantesRating.MODO_CONSULTA_DEFAULT;

				if (ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE.equals(ratingInd.getIdEstado())
						|| ConstantesSEFE.ID_CLASIF_ESTADO_RATING_HISTORICO.equals(ratingInd.getIdEstado())) {
					modoConsulta = ConstantesRating.MODO_CONSULTA_SOLO_LECTURA;
				}
			} else {
				if (rtgFinanciero.getNotaFinanciera() != null) {
					modoConsulta = ConstantesRating.MODO_CONSULTA_CONFIRMAR;
				} else {
					modoConsulta = ConstantesRating.MODO_CONSULTA_PROYECTAR;
				}
			}
		}

		proyeccion.setModoConsulta(modoConsulta);
		this.poblarCuentasProyectado(proyeccion, modoConsulta);

		return proyeccion;
	}

	/*
	 * Obtiene la fecha para la consulta del IPC correpondiente al mes anterior
	 * a la fecha pasada como argumento
	 */
	private Date obtenerFechaIPC(Date periodo) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(periodo);
		calendar.add(Calendar.MONTH, -1);

		return calendar.getTime();
	}

	/*
	 * Actualiza los valores de las cuentas ingresados el usuario
	 */
	private void actualizarValoresCuentas(Vaciado vac, Vaciado ultimoCierreAnual, Collection cuentas) {
		GestorPlanCuentas gestorCtas = new GestorPlanCuentasImpl();
		GestorPlanCuentas gestPlanCtas = new GestorPlanCuentasImpl();
		Cuenta ctaP154 = gestPlanCtas.consultarValorCuentaVaciado(ultimoCierreAnual.getIdVaciado(),
				ConstantesRating.CODIGO_CUENTA_PROYECCION_MOVIBLE);

		Iterator it = cuentas.iterator();
		while (it.hasNext()) {
			Cuenta cta = (Cuenta) it.next();

			// Caso particular de la cuenta P154 la cual no es ingresada en el
			// periodo proyectado sino que en el ultimo cierre anual.
			// En este caso el valor se debe guardar en la cuenta del vaciado de
			// ultimo cierre anual
			if (ConstantesRating.CODIGO_CUENTA_PROYECCION_MOVIBLE.equalsIgnoreCase(cta.getCodigoCuenta())) {
				if (ctaP154 != null) {
					ctaP154.setMonto(cta.getMonto());
					gestorCtas.actualizarValorCuenta(ultimoCierreAnual.getIdVaciado(), ctaP154);
				}
				continue;
			}
			gestorCtas.actualizarValorCuenta(vac.getIdVaciado(), cta);
		}
	}

	/*
	 * Calcula el rating proyectado y guarda los valores en la base
	 */
	protected RatingProyectado calcularProyeccion(String rutCliente, Long idRating, Long idProy, String logUsu) {
		RatingProyectadoCGE proyeccion = new RatingProyectadoCGE();

		// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);

		GestorVaciados gestorVac = new GestorVaciadosImpl();
		Vaciado vacProy = gestorVac.buscarVaciado(rtgFinanciero.getIdVacProyectado());

		// se crean los periodos y se pasan al rating proyectado
		PeriodoRating periodoP = new PeriodoRating();
		PeriodoRating periodo0 = new PeriodoRating();
		PeriodoRating periodo1 = new PeriodoRating();
		PeriodoRating periodo2 = new PeriodoRating();

		Vaciado vac0 = gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado0());
		Vaciado vac1 = gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado1());
		Vaciado vac2 = null;

		if (rtgFinanciero.getIdVaciado2() != null) {
			vac2 = gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado2());
		}

		periodoP.setPeriodo(vacProy.getPeriodo());
		periodoP.setVaciado(vacProy);
		// no hay vaciados con periodos parciales
		if (vac2 == null) {
			periodo0.setPeriodo(vac0.getPeriodo());
			periodo0.setVaciado(vac0);

			periodo1.setPeriodo(vac1.getPeriodo());
			periodo1.setVaciado(vac1);
		} else {
			periodo0.setPeriodo(vac0.getPeriodo());
			periodo0.setVaciado(vac0);

			periodo1.setPeriodo(vac1.getPeriodo());
			periodo1.setVaciado(vac1);

			periodo2.setPeriodo(vac2.getPeriodo());
			periodo2.setVaciado(vac2);
		}

		proyeccion.setPeriodoProy(periodoP);
		proyeccion.setPeriodo0(periodo0);
		proyeccion.setPeriodo1(periodo1);
		proyeccion.setPeriodo2(periodo2);
		proyeccion.setRatingFinanciero(rtgFinanciero);

		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndiv = gestorIndiv.buscarRatingIndividual(buscarIdClientePorRut(rutCliente), idRating);

		/*
		 * marias - 20121109 Se incorpora el uso de un algoritmo por defecto, si
		 * no se encuentra uno configurado para la plantilla (modelo) pasada
		 * como argumento
		 */
		String keyImplementacionAlgoritmo =  ConstantesSEFE.KEY_ALGORITMO_RTG_PROYECTADO + ConstantesSEFE.PUNTO + ConstantesSEFE.KEY_DEFAULT;

		AlgoritmoRatingFinanciero algoritmo = (AlgoritmoRatingFinanciero) ConfigManager.getInstanceOf(keyImplementacionAlgoritmo);

		// lista de los vaciados para el financiero proyectado
		List vaciados = new ArrayList();
		vaciados.add(vacProy);
		vaciados.add(vac0);
		vaciados.add(vac1);
		vaciados.add(vac2);
		// los meses del vaciado de cierre
		int mesesVacCierre = vacProy.getMesesPer().intValue();
		// se determina el tipo de combinacion
		String combinacion = RatingUtil.getCombinacionPeriodos(vaciados);

		MatrizFinanciera matriz = gestorFinanciero.obtenerMatrizFinanciera(rtgFinanciero.getIdMatriz(), combinacion, mesesVacCierre);
		if (matriz == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_PROY_ERROR_MATRIZ_VIGENTE_NO_EXISTE));
		} else if (!ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE.equals(matriz.getEstadoId())) {
			matriz = gestorFinanciero.obtenerMatrizFinanciera(matriz.getIdBanca(), matriz.getIdSegmento(), null);
			matriz = gestorFinanciero.obtenerMatrizFinanciera(matriz.getIdMatrizProy());
			rtgFinanciero.setIdMatriz(matriz.getIdMatriz());
		}
		
		rtgFinanciero = gestorFinanciero.actualizarRatingProyectado(matriz.getIdBanca(), rtgFinanciero);
		proyeccion.setRatingFinanciero(rtgFinanciero);

		if (vac2 != null) {
			algoritmo.calcularRating(vacProy, vac1, vac2, ratingIndiv.getIdBanca(), matriz, rtgFinanciero);
		} else {
			algoritmo.calcularRating(vacProy, vac0, vac1, ratingIndiv.getIdBanca(), matriz, rtgFinanciero);
		}
		
		//Se evalua la nota tope del rating proyectado
		rtgFinanciero.setNotaFinanciera(RatingUtil.evalTope(matriz, rtgFinanciero.getNotaFinanciera()));

		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerPrimerUsuario(logUsu);

		proyeccion.getRatingFinanciero().setResponsable(usr.getCodigoUsuario());
		proyeccion.getRatingFinanciero().setIdUsuario(usr.getUsuarioId());

		return proyeccion;
	}

	private RatingFinanciero aplicarAjustePorExcesoRespectoARtgFinanciero(Long idCliente, Long idRtgInd, RatingFinanciero rtgProy) {
		GestorRatingFinanciero gestRtgFinan = new GestorRatingFinancieroImpl();
		GestorRatingIndividual gestRtgInd = new GestorRatingIndividualImpl();

		RatingIndividual rtgInd = gestRtgInd.buscarRatingIndividual(idCliente, idRtgInd);

		/*
		 * marias 20121022 - corrige tipo de parametro excesoLimite, cambiando
		 * de Integer a Double, para soportar valores decimales
		 */
		Double excesoLimite = ConfigDBManager.getValueAsDouble(ConstantesSEFE.PROPIEDAD_RTG_PROY_EXCESO_PERMITIDO);

		RatingFinanciero rtgFinanciero = gestRtgFinan.obtenerRating(rtgInd.getIdRatingFinanciero());
		Double notaRtgFinan = new Double(rtgFinanciero.getNotaFinanciera().doubleValue() + excesoLimite.doubleValue());

		Double notaRtgProy = rtgProy.getNotaFinanciera();

		if (notaRtgFinan.doubleValue() < notaRtgProy.doubleValue()) {
			rtgProy.setNotaFinanciera(notaRtgFinan);
		}

		return rtgProy;
	}

	/*
	 * Busca el identificador del cliente a partir del rut
	 */
	protected Long buscarIdUsuarioPorNombre(String logUsu) {
		GestorUsuarios gstUsr = new GestorUsuariosImpl();
		return gstUsr.obtenerPrimerUsuario(logUsu).getUsuarioId();
	}

	/*
	 * Busca el identificador de usuario a partir del log de usuario (nombre)
	 */
	protected Long buscarIdClientePorRut(String rutCliente) {
		ServicioClientes srvCtes = new ServicioClientesImpl();
		return srvCtes.obtenerIdClientePorRut(rutCliente);
	}

	/*
	 * Se puebla el rating proyectado con las cuentas de los diferentes
	 * periodos. El modo indica si se retornan todas las cuentas, o solamente
	 * las cuentas de ingreso
	 */
	private void poblarCuentasProyectado(RatingProyectadoCGE proyeccion, String modo) {

		GestorRatingProyectado gestor = new GestorRatingProyectadoImpl();
		GestorPlanCuentas gestorPlanCuentas = new GestorPlanCuentasImpl();

		// se recuperan los flags de opcionalidad y se ponen en la proyeccion
		Vaciado vacp = proyeccion.getPeriodoProy().getVaciado();
		List flags = gestor.buscarFlagsIngresoCuentasProyeccion(vacp.getIdTipoPlan(), vacp.getIdNombrePlanCtas());
		proyeccion.setFlags(flags);

		// genera un mapa con los flgas indexado por codigo cuenta
		Map fMap = new HashMap();
		for (int i = 0; i < flags.size(); ++i) {
			FlagCuentaProyeccion flag = (FlagCuentaProyeccion) flags.get(i);
			fMap.put(flag.getCodigoCuenta(), flag);
		}
		Collection ctasVaciadoProyectado = gestorPlanCuentas.buscarCuentasVaciado(proyeccion.getPeriodoProy().getVaciado().getIdVaciado(),
				null);
		List ctas = gestorPlanCuentas.obtenerCuentasProyectadas(ctasVaciadoProyectado, ConstantesSEFE.CLASIF_ID_TPO_PROYECCION_CORTA,
				proyeccion.getPeriodoProy().getVaciado().getIdTipoPlan());
		// List ctas =
		// gestor.buscarCuentasPorPeriodo(proyeccion.getPeriodoProy().getVaciado().getIdVaciado());
		if (modo.equals(ConstantesRating.MODO_CONSULTA_PROYECTAR)) {
			ctas = filtarCuentas(ctas, fMap);
		}
		proyeccion.getPeriodoProy().setCuentas(ctas);

		if (gestor.esCierreAnual(proyeccion.getPeriodo0().getVaciado())) {
			ctas = gestorPlanCuentas.obtenerCuentasProyectadas(ctasVaciadoProyectado, ConstantesSEFE.CUENTA_PROY_CORTA_X1, proyeccion
					.getPeriodoProy().getVaciado().getIdTipoPlan());
		} else {
			ctas = gestorPlanCuentas.obtenerCuentasProyectadas(ctasVaciadoProyectado, ConstantesSEFE.CUENTA_PROY_CORTA_X, proyeccion
					.getPeriodoProy().getVaciado().getIdTipoPlan());
		}

		// ctas =
		// gestor.buscarCuentasPorPeriodo(proyeccion.getPeriodo0().getVaciado().getIdVaciado());
		proyeccion.getPeriodo0().setCuentas(ctas);
		if (gestor.esCierreAnual(proyeccion.getPeriodo0().getVaciado())) {
			ctas = gestorPlanCuentas.obtenerCuentasProyectadas(ctasVaciadoProyectado, ConstantesSEFE.CUENTA_PROY_CORTA_X2, proyeccion
					.getPeriodoProy().getVaciado().getIdTipoPlan());
		} else {
			ctas = gestorPlanCuentas.obtenerCuentasProyectadas(ctasVaciadoProyectado, ConstantesSEFE.CUENTA_PROY_CORTA_X1, proyeccion
					.getPeriodoProy().getVaciado().getIdTipoPlan());
		}
		// ctas =
		// gestor.buscarCuentasPorPeriodo(proyeccion.getPeriodo1().getVaciado().getIdVaciado());
		proyeccion.getPeriodo1().setCuentas(ctas);

		// si periodo 2 es nulo, no hay cierre parcial
		if (proyeccion.getPeriodo2().getVaciado() != null) {
			ctas = gestorPlanCuentas.obtenerCuentasProyectadas(ctasVaciadoProyectado, ConstantesSEFE.CUENTA_PROY_CORTA_X2, proyeccion
					.getPeriodoProy().getVaciado().getIdTipoPlan());
			// ctas =
			// gestor.buscarCuentasPorPeriodo(proyeccion.getPeriodo2().getVaciado().getIdVaciado());
			proyeccion.getPeriodo2().setCuentas(ctas);
		}
	}

	/*
	 * remueve de la lista las cuentas que no son de ingreso y deja el valor por
	 * defecto cuando requerido
	 */
	private List filtarCuentas(List ctas, Map map) {
		List listaFiltrada = new ArrayList();

		for (int i = 0; i < ctas.size(); ++i) {
			Cuenta cta = (Cuenta) ctas.get(i);

			// es una cuenta de ingreso
			if (map.containsKey(cta.getCodigoCuenta())) {
				// se obtienen los flags de la cuenta
				FlagCuentaProyeccion flag = (FlagCuentaProyeccion) map.get(cta.getCodigoCuenta());

				listaFiltrada.add(cta);
			}
		}

		return listaFiltrada;
	}

	/*
	 * Calcula las cuentas de rating de los vaciados utilizados en la proyeccion
	 */
	private void calcularCuentasVaciados(Long idVaciado, Map mapCtx) {
		ServicioCalculo servicioCalculo = new ServicioCalculoImpl();
		if (mapCtx != null && !mapCtx.isEmpty()) {
			Iterator itCtx = mapCtx.keySet().iterator();
			while (itCtx.hasNext()) {
				String key = itCtx.next().toString();
				servicioCalculo.ponerEnContexto(key, mapCtx.get(key));
			}
		}
		servicioCalculo.ponerEnContexto(ConstantesSEFE.CTX_CALCULAR_PROYECCION, ConstantesSEFE.CTX_CALCULAR_PROYECCION);
		servicioCalculo.calcularIndicadoresRating(idVaciado);
	}

	public void insertarIndicadoresBi(InputStream streamXLS, BalanceInmobiliario balanceInmobiliario,POIExcelReader reader, GestorRatingProyectado gestor ){
		ArrayList indicadores = reader.getFileAsList("indicadoresBi", streamXLS);
		for (int i = 0; i < indicadores.size(); i++) {
			IndicadorBi indBi = (IndicadorBi) indicadores.get(i);
			indBi.setIdParteInvol(balanceInmobiliario.getIdParteInvol());
			indBi.setFechaAvance(balanceInmobiliario.getFechaAvance());
			gestor.insertarIndicadorBi(indBi);
		}
		try {
			streamXLS.close();
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug("no fue posible cerrar archivo rating proyectado Inmobiliaria. La aplicacion puede seguir operando");
			}
		}
	}
	
	
	
	
	public Archivo descargarPlantillaRatingProyectado(Contexto ctx) {
		Archivo file = new Archivo();
		String nomFile = null;
		if (ctx.getIdPlantilla().equals(ConstantesSEFE.BANCA_INMOBILIARIAS.toString())) {
			nomFile = ConstantesRating.NOMBRE_ARCHIVO_CARGA_INMOBILIARIA;
		} else if (ctx.getIdPlantilla().equals(ConstantesSEFE.BANCA_CONSTRUCTORAS.toString())) {
			nomFile = ConstantesRating.NOMBRE_ARCHIVO_CARGA_CONSTRUCTORA;
		}else if (ctx.getIdPlantilla().equals(ConstantesSEFE.BANCA_AGRICOLAS.toString())) {
			nomFile = ConstantesRating.NOMBRE_ARCHIVO_CARGA_AGRICOLA;
		}
		GestorAgricola gestorAgricola = new GestorAgricolaImpl();
		PlantillaRating plantilla = gestorAgricola.buscarPlantillaRating(Integer.valueOf(ctx.getIdPlantilla()));

		file.setBase64String(plantilla.getArchivo());
		file.setNombre(nomFile);
		file.setMimeType(ConstantesSEFE.MIME_TYPE_XLS);
		return file;
	}
	public Archivo descargarPlantillaParametros(Contexto ctx) throws Exception {
		Archivo file = new Archivo();
		InputStream isFile = null;
		String nomFile = null;
		nomFile = ConstantesRating.NOMBRE_ARCHIVO_CARGA_PARAMETROS_AGRICOLA;
		try {
			isFile = new FileInputStream(System.getProperty(ConstantesSEFE.APP_PROPS) + "\\sefe\\plantillas\\" + nomFile);

		} catch (FileNotFoundException e) {
			log.error("Error cargando archivo " + nomFile, e);

			throw new SEFEException("No se encontro el archivo", e);
		}
		int c = 0;
		ByteArrayOutputStream sos = new ByteArrayOutputStream();
		try {
			c = isFile.read();
			while (c != -1) {
				sos.write(c);
				c = isFile.read();
			}

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (isFile != null) {
				isFile.close();
			}
			if (sos != null) {
				sos.close();
				sos.flush();
			}
		}
		byte[] encodedBytes = Base64.encodeBase64(sos.toByteArray());

		String base64 = new String(encodedBytes);

		file.setBase64String(base64);
		file.setNombre(nomFile);
		file.setMimeType(ConstantesSEFE.MIME_TYPE_XLS);
		return file;
	}
}
