package com.bch.sefe.rating.srv.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.agricola.srv.GestorAgricola;
import com.bch.sefe.agricola.srv.impl.GestorAgricolaImpl;
import com.bch.sefe.agricola.vo.Agricola;
import com.bch.sefe.agricola.vo.FlujoResumen;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.dao.ClientesDAO;
import com.bch.sefe.comun.dao.impl.ClientesDAOImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.AlgoritmoRatingGrupal;
import com.bch.sefe.comun.srv.ConsultaServicios;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.GestorClasificaciones;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.ServicioConsultaDeuda;
import com.bch.sefe.comun.srv.impl.ConsultaServiciosImplCache;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.srv.impl.GestorClasificacionesImpl;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.srv.impl.ServicioConsultaDeudaODS;
import com.bch.sefe.comun.srv.osb.EvaluacionOsb;
import com.bch.sefe.comun.srv.osb.EvaluacionOsb.AtributoOsb;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.DeudaCliente;
import com.bch.sefe.comun.vo.FichaChica;
import com.bch.sefe.comun.vo.RelacionRatingIndRatingGrupoNoPyME;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.exception.SEFEException;
import com.bch.sefe.rating.ConstantesRating;
import com.bch.sefe.rating.dao.RatingGrupalDAO;
import com.bch.sefe.rating.dao.impl.RatingGrupalDAOImpl;
import com.bch.sefe.rating.impl.ComparadorVaciadosRatingGrupal;
import com.bch.sefe.rating.srv.GestorRatingComportamiento;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingGrupal;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.ValidadorRatingGrupal;
import com.bch.sefe.rating.vo.CalculoRatingGrupal;
import com.bch.sefe.rating.vo.Caritas;
import com.bch.sefe.rating.vo.GrupoRating;
import com.bch.sefe.rating.vo.IntegranteRatingGrupo;
import com.bch.sefe.rating.vo.MatrizPuntajeCaritas;
import com.bch.sefe.rating.vo.ModIndRtgGrupal;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingGrupal;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.util.TimerUtil;
import com.bch.sefe.vaciados.srv.GestorPlanCuentas;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorPlanCuentasImpl;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;

public class GestorRatingGrupalImpl implements GestorRatingGrupal {
	private static final Logger log = Logger.getLogger(GestorRatingGrupalImpl.class);

	public RatingGrupal buscarRatingGrupal(String rut, Long idRatingIndividual, Integer TipoRating) {

		RatingGrupal grp = new RatingGrupal();
		RatingGrupalDAO daoGrp = new RatingGrupalDAOImpl();
		grp = daoGrp.buscarRatingPorCliente(rut, TipoRating, idRatingIndividual);

		return grp;
	}

	public List buscarRatingGrupal(RatingIndividual ratingInd) {
		RatingGrupalDAO ratingGrpDAO = new RatingGrupalDAOImpl();
		List ratingsGrp;

		// Dependiendo de la banca se utiliza el procedimiento que corresponda
		// para buscar el rating grupal utilizado en el calculo del rating
		// individual
		if (ConstantesSEFE.BANCA_PYME.equals(ratingInd.getIdBanca())) {
			ratingsGrp = ratingGrpDAO.buscarRatingGrupalPyME(ratingInd.getRut(), ratingInd.getIdRating());
		} else {
			ratingsGrp = ratingGrpDAO.buscarRatingGrupalNoPyME(ratingInd.getRut(), ratingInd.getIdRating());
		}

		return ratingsGrp;
	}

	public void cambiarEstadoHistorico(RatingGrupal rg, String logOper) {
		RatingGrupalDAO ratingGrpDAO = new RatingGrupalDAOImpl();
		ratingGrpDAO.cambiarEstadoHistorico(rg);
	}

	public void establecerInformado(RatingGrupal ratingGrupal, Date hasta, String logOper) {
		RatingGrupalDAO ratingGrpDAO = new RatingGrupalDAOImpl();
		ratingGrpDAO.establecerInformadoSiebel(ratingGrupal, hasta);
	}

	public RatingGrupal[] obtenerRatingsGrupalesNoInformados(Date desde, Date hasta) {
		RatingGrupalDAO ratingGrpDAO = new RatingGrupalDAOImpl();
		List lst = ratingGrpDAO.obtenerRatingsGrupalesNoInformados(desde, hasta);
		return (RatingGrupal[]) lst.toArray(new RatingGrupal[lst.size()]);
	}

	public RatingGrupal[] obtenerRatingsGrupalesInformados(Date hasta) {
		RatingGrupalDAO ratingGrpDAO = new RatingGrupalDAOImpl();
		List lst = ratingGrpDAO.obtenerRatingsGrupalesInformados(hasta);
		return (RatingGrupal[]) lst.toArray(new RatingGrupal[lst.size()]);
	}

	public Double getActivosEnMMCLP(String rutCliente, Integer idBanca, Long idRatingFinanciero) {
		GestorRatingFinanciero gestorRtgFinanciero = new GestorRatingFinancieroImpl();
		GestorVaciados gestorVac = new GestorVaciadosImpl();
		ConversorMoneda conversorMoneda = new ConversorMonedaImpl();
		GestorPlanCuentas gestorPlanCtas = new GestorPlanCuentasImpl();
		GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();
		Vaciado vaciado = null;

		Cliente cliente = gestorClientes.obtenerClientePorRut(rutCliente);

		if (idRatingFinanciero != null) {
			RatingFinanciero rtgFinanciero = gestorRtgFinanciero.obtenerRating(idRatingFinanciero);
			vaciado = gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado0());
		} else {
			// Si no existe un rating financiero de donde obtener el monto de
			// activos se utiliza un vaciado que cumpla con las reglas de
			// negocio
			// particulares
			vaciado = buscarVaciadoParaObtenerActivos(rutCliente);
		}

		if (vaciado == null) {
			if (log.isInfoEnabled()) {
				log.info("No existe vaciado para poder obtener los Activos en MMCLP para el cliente [" + cliente.getRut() + "]");
			}
			return null;
		}

		// Se obtiene el codigo de cuenta dependiendo del id del nombre plan de
		// cuenta
		String codCtaActivos = ConfigDBManager.getValueAsString(ConstantesSEFE.CUENTA_TOTAL_ACTIVOS + vaciado.getIdNombrePlanCtas());
		Cuenta cta = gestorPlanCtas.consultarValorCuentaVaciado(vaciado.getIdVaciado(), codCtaActivos);

		if (cta != null) {
			// Se realiza la conversion a MM CLP utilizando el tipo de cambio
			// del periodo del vaciado
			return conversorMoneda.convertirMoneda(cta.getMontoMasAjuste(), vaciado.getIdMoneda(), vaciado.getUnidMedida(),
					ConstantesSEFE.ID_CLASIF_MONEDA_CLP, ConstantesSEFE.ID_CLASIF_MILLONES, conversorMoneda.buscarDiaHabilSiguiente(vaciado
							.getPeriodo()));
		}

		return null;
	}

	public List buscarRelacionadosGrupoPyME(Long idCliente, Long idRating, Long idVersion) {
		RatingGrupalDAO rtgGrupalDAO = new RatingGrupalDAOImpl();

		return rtgGrupalDAO.buscarRelacionadosGrupoPyME(idCliente, idRating, idVersion);
	}

	private String getFlagComportamiento(IntegranteRatingGrupo relacionado) {
		GestorRatingComportamiento gestorRtgComp = new GestorRatingComportamientoImpl();
		ServicioClientes srvCtes = new ServicioClientesImpl();
		Cliente cte = srvCtes.obtenerClientePorRut(relacionado.getRutRelacionado());
		
		Caritas caritas = null;
		
		if (cte != null) {
			Long idCliente = Long.valueOf(cte.getClienteId());
			Calendar hoy = Calendar.getInstance();
			int diasVigencia = ConfigDBManager.getValueAsInteger(ConstantesSEFE.KEY_VIGENCIA_EVALUACION_CARITAS_RTG_COMPORTAMIENTO).intValue();
			Calendar limiteVigencia = Calendar.getInstance();
			limiteVigencia.add(Calendar.DAY_OF_MONTH, -diasVigencia);
			caritas = gestorRtgComp.buscarHistoricoCliente(idCliente, hoy.getTime(), limiteVigencia.getTime());
		}
		
		if (caritas != null) {
			caritas.setPuntajeFinal(caritas.getPuntajePonderado());
			relacionado.setPuntajeCaritas(new Integer(caritas.getPuntajeFinal().intValue()));
			return gestorRtgComp.calcularColorPuntaje(caritas.getPuntajeFinal());
		} else {
			ConsultaServicios consultaServicios = new ConsultaServiciosImplCache();
			List evaluaciones = null;
			EvaluacionOsb evaluacion;
			String rut = relacionado.getRutRelacionado();

			if (log.isDebugEnabled()) {
				log
						.debug("Obteniendo color del comportamiento actual para rating grupal del rut ["
								+ rut + "]");
			}

			try {
				// Se consulta al comportamiento crediticio por el rut
				evaluaciones = consultaServicios
						.consultarComportamientoCrediticio(rut);
			} catch (SEFEException e) {
				log
						.info("No se encontro informacion en Comportamiento Crediticio para el rut ["
								+ rut + "]");
				// No se hace nada y mas adelante se intentara buscar en el
				// servicio
				// pre evaluacion persona
			}

			// Si se encuenta una evaluacion en comportamiento crediticio se
			// utiliza
			// esa, en caso contrario se busca
			// en el servicio de Pre Evaluacion persona
			if (evaluaciones != null && evaluaciones.size() > 0) {
				evaluacion = (EvaluacionOsb) evaluaciones.get(0);

				// Se valida la vigencia de la evaluacion realizada en siebel.
				// Si no
				// cumple vigencia el comportamiento no se puede calcular
				// comportamiento
				// actual
				if (!evaluacionSiebelCumpleVigencia(evaluacion.getFechaHora()
						.getTime())) {
					log
							.debug("La Informacion de Comportamiento Crediticio no cumple con la vigente requerida para Rating Grupal");
					return null;
				}

				if (evaluacion.getPuntajeEvaluacion() != null) {
					relacionado.setPuntajeCaritas(new Integer(evaluacion
							.getPuntajeEvaluacion()));
					return gestorRtgComp.calcularColorPuntaje(new Double(
							evaluacion.getPuntajeEvaluacion()));
				}
			} else {
				// En caso que no se haya encontrado evaluacion por medio de
				// comportamiento crediticio, se busca en pre evaluacion persona
				try {
					evaluaciones = consultaServicios
							.consultarPreEvaluacionPersona(rut);
				} catch (SEFEException e) {
					// Si no fue posible obtener infomacion desde el servicio
					// solo
					// se dega registrado en el log
					log
							.info("No se encontro informacion en Pre Evaluacion Persona para el rut ["
									+ rut + "]");
				}

				if (evaluaciones != null && evaluaciones.size() > 0) {
					evaluacion = (EvaluacionOsb) evaluaciones.get(0);

					// Se valida la vigencia de la evaluacion realizada en
					// siebel.
					// Si no cumple vigencia el comportamiento no se puede
					// calcular
					// comportamiento actual
					if (!evaluacionSiebelCumpleVigencia(evaluacion
							.getFechaHora().getTime())) {
						log
								.debug("La Informacion de Pre Evaluacion Persona no cumple con la vigente requerida para Rating Grupal");
						return null;
					}

					List lstAtributos = evaluacion.getAtributos();
					if (lstAtributos != null && !lstAtributos.isEmpty()) {
						// Se busca el atributo numero 9 para sacar el resultado
						// de
						// evaluacion con la cual se obtendra el color del
						// puntaje
						for (int i = 0; i < lstAtributos.size(); i++) {
							AtributoOsb atr = (AtributoOsb) lstAtributos.get(i);
							if (atr.getNumeroAtributo() != null
									&& atr.getNumeroAtributo().intValue() == 9) {
								return gestorRtgComp.calcularColorPuntaje(atr
										.getValor());
							}
						}
					}
				}
			}
		}

		return null;
	}

	/*
	 * Se evalua la vigencia de la evaluacion realizada en siebel.
	 */
	private boolean evaluacionSiebelCumpleVigencia(Date fechaEvalSiebel) {
		Integer vigenciaEnDias = ConfigDBManager.getValueAsInteger(ConstantesSEFE.VIGENCIA_EN_DIAS_EVALUACION_COMPORTAMIENTO_RTG_GRUPAL);

		Calendar calVigencia = GregorianCalendar.getInstance();
		calVigencia.set(Calendar.HOUR, 0);
		calVigencia.set(Calendar.MINUTE, 0);
		calVigencia.set(Calendar.SECOND, 0);
		calVigencia.set(Calendar.MILLISECOND, 0);
		// calVigencia.add(Calendar.DAY_OF_MONTH,
		// -ConstantesSEFE.VIGENCIA_EN_DIAS_EVALUACION_COMPORTAMIENTO_RTG_GRUPAL.intValue());
		calVigencia.add(Calendar.DAY_OF_MONTH, -vigenciaEnDias.intValue());

		Calendar calFechaEvalSiebel = GregorianCalendar.getInstance();
		calFechaEvalSiebel.set(Calendar.HOUR, 0);
		calFechaEvalSiebel.set(Calendar.MINUTE, 0);
		calFechaEvalSiebel.set(Calendar.SECOND, 0);
		calFechaEvalSiebel.set(Calendar.MILLISECOND, 0);
		calFechaEvalSiebel.setTime(fechaEvalSiebel);

		if (log.isDebugEnabled()) {
			log.debug("Fecha Evaluacion Siebel: " + calFechaEvalSiebel.get(Calendar.DAY_OF_MONTH) + "/"
					+ calFechaEvalSiebel.get(Calendar.MONTH) + "/" + calFechaEvalSiebel.get(Calendar.YEAR));
			log.debug("La evaluaciÃ³n Siebel no puede superar los " + vigenciaEnDias + " de antiguedad");
			log.debug("Fecha Actual menos los dias de vigencia: " + calVigencia.get(Calendar.DAY_OF_MONTH) + "/"
					+ calVigencia.get(Calendar.MONTH) + "/" + calVigencia.get(Calendar.YEAR));
		}

		return !calFechaEvalSiebel.before(calVigencia);
	}

	public List buscarRelacionadosGrupoNoPyME(Long idGrupo) {
		RatingGrupalDAO rtgGrupalDAO = new RatingGrupalDAOImpl();

		return rtgGrupalDAO.buscarRelacionadosGrupoNoPyME(idGrupo);
	}

	private String getNombreCompletoRelacionado(FichaChica ficha) {
		StringBuffer nomCompleto = new StringBuffer(ficha.getNombre());

		if (ficha.getApellidoPaterno() != null && ficha.getApellidoPaterno().length() > 0) {
			nomCompleto.append(" ").append(ficha.getApellidoPaterno());
		}

		if (ficha.getApellidoMaterno() != null && ficha.getApellidoMaterno().length() > 0) {
			nomCompleto.append(" ").append(ficha.getApellidoMaterno());
		}

		return nomCompleto.toString();
	}

	private boolean seNecesitaActualizaNombre(FichaChica fc, Cliente cli) {
		String nombreFicha = getNombreCompletoRelacionado(fc);
		return (nombreFicha != null && cli.getNombreCliente() != null && !nombreFicha.trim()
				.equalsIgnoreCase(cli.getNombreCliente().trim()));
	}

	private boolean seNecesitaActualizarActEconomica(FichaChica fc, Cliente cli) {
		ClientesDAO cliDao = new ClientesDAOImpl();
		String idClasifSiebel = fc.getCodigoActividadEconomica();

		// No hay cambios. El cliente sigue sin estar asociado a ninguna
		// actividad economica en Siebel
		if (idClasifSiebel == null && cli.getSubSectorId() == null) {
			return false;
		}

		// Hay cambios. Al cliente se le ha sacado de una actividad economica Ã³
		// al cliente se le ha asociado una actividad economica
		if ((idClasifSiebel == null && cli.getSubSectorId() != null) || (idClasifSiebel != null && cli.getSubSectorId() == null)) {
			return true;
		}

		// Se validaran que las actividades economicas coincidan
		if (idClasifSiebel != null && cli.getSubSectorId() != null) {
			GestorClasificaciones gc = new GestorClasificacionesImpl();
			Clasificacion clasificacion;

			String idClasifSiebelEnSefe = cliDao.obtenerIdClasificacion(ConstantesSEFE.GRP_CLASIF_SUBSECTOR, idClasifSiebel);

			// Hay cambios. Si SEFE no tiene la clasificacion asignada al
			// cliente en Siebel, se debe actualizar para que esta nueva
			// clasificacion se
			// cree en SEFE
			if (idClasifSiebelEnSefe == null) {
				return true;
			}

			clasificacion = gc.buscarClasificacionPorId(Integer.valueOf(idClasifSiebelEnSefe));

			// Si las clasificaciones nos distintas se debe actualizar ya que se
			// ha cambiado en siebel la actividad economica del cliente. Si las
			// clasificaciones son iguales, no se ha actualizado la actividad
			// economica del cliente en siebel.
			return (!clasificacion.getIdClasif().equals(cli.getSubSectorId()));
		}
		return false;
	}

	public void actualizarInfoRelacionadoPyME(IntegranteRatingGrupo integrante) {
		if (integrante != null) {
			GestorRatingIndividual gestorRatingInd = new GestorRatingIndividualImpl();
			GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();
			ConsultaServicios consultaServicios = new ConsultaServiciosImplCache();
			DeudaCliente deudaBanco;
			DeudaCliente deudaSbif;
			Cliente cliRelacionado = null;
			TimerUtil timerUtil = new TimerUtil();

			if (log.isDebugEnabled()) {
				timerUtil.startTime();
			}

			// Se actualiza informacion del relacionado desde ficha chica
			FichaChica fichaChica = consultaServicios.consultarFichaChica(integrante.getRutRelacionado());

			if (fichaChica == null || ConstantesSEFE.FLAG_RUT_NO_ENCONTRADO_SIEBEL.equalsIgnoreCase(fichaChica.getNombre())) {
				String msg = MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_CLIENTE_NO_ENCONTRADO_FICHA_CHICA,
						new Object[] { integrante.getRutRelacionado() });
				log.error(msg);
				throw new BusinessOperationException(msg);
			}

			// Se obtiene el rating individual para cada relacionado, en caso de
			// existir
			cliRelacionado = gestorClientes.obtenerClientePorRut(integrante.getRutRelacionado());

			integrante.setClasificacionRiesgo(fichaChica.getClasificacionRiesgo());

			// Se resuelve si el relacionado opera o no como empresa
			Integer codActEconomica = null;
			if (fichaChica.getCodigoActividadEconomica() != null && fichaChica.getCodigoActividadEconomica().length() > 0) {
				codActEconomica = new Integer(fichaChica.getCodigoActividadEconomica());

				if (this.operaComoEmpresa(codActEconomica)) {
					GestorServicioClientes gestorServCliente = new GestorServicioClientesImpl();

					integrante.setOperaComoEmpresa(ConstantesSEFE.FLAG_OPERA_COMO_EMPRESA.toUpperCase());

					if (cliRelacionado == null || seNecesitaActualizarActEconomica(fichaChica, cliRelacionado)
							|| seNecesitaActualizaNombre(fichaChica, cliRelacionado)) {
						// FIXME Ojo ya que el objeto retornado por
						// actualizarClienteDesdeFichaChica retorna solo una
						// nueva instancia
						// con los valores que fueron actualizados NO un objeto
						// completo llenado con lo de la base de datos. Como
						// en el metodo solo se usa el nombre que es un campo
						// que se actualiza sirve...
						cliRelacionado = gestorServCliente.actualizarClienteDesdeFichaChica(integrante.getRutRelacionado());
					}
				} else {
					integrante.setOperaComoEmpresa(ConstantesSEFE.FLAG_NO_OPERA_COMO_EMPRESA.toUpperCase());
				}
			} else {
				// No se puede determinar si opera como empresa o no, ya que
				// ficha chica no devolvio el codigo de actividad economica
				integrante.setOperaComoEmpresa(null);
			}

			// Se obtiene el rating de comportamiento
			integrante.setFlagComportamientoActual(getFlagComportamiento(integrante));

			RatingIndividual rtgInd = null;
			if (cliRelacionado != null) {
				rtgInd = gestorRatingInd.buscarRatingVigente(integrante.getRutRelacionado());
				// Si es cliente, se obtiene el nombre desde el objeto cliente
				// previamente actualizado con lo que devolvio la ficha chica
				integrante.setNombreRelacionado(cliRelacionado.getNombreCliente());
			} else {
				// Si no es cliente (osea es Persona Natural) el nombre se
				// conforma aca
				integrante.setNombreRelacionado(getNombreCompletoRelacionado(fichaChica));
			}

			// Se verifica que el rating individual sea valido para poder
			// actualizar info. en rating grupal
			if (this.esValidoParaRatingGrupal(rtgInd)) {
				// Se actualizan las ventas del relacionado
				
				/*
				 * Se agrega validacion para el Nivel de ventas agricola 
				 * Requerimiento 7.4.15 Sprint 1
				 * Se comenta por que se modifica por cambio de negocio
				 */
				if(ConstantesSEFE.NIVEL_VENTA_AGRICOLA.equals(rtgInd.getBanca())){
					GestorAgricola gestorAgricola = new GestorAgricolaImpl();
					Double ventas;
					Agricola agricola = gestorAgricola.obtenerVaciadoAgricola(rtgInd.getIdCliente(), rtgInd.getIdRating());
					Long idAgricola = agricola.getIdAgricola();
					FlujoResumen flujo = (FlujoResumen)gestorAgricola.buscarFlujoResumen(idAgricola).get(0);
					if (flujo.getIngresos() == null){
						ventas =  ConstantesSEFE.DOUBLE_CERO;
						integrante.setVentas(ventas);
					}else {
						ventas = flujo.getIngresos();
						BigDecimal b = new BigDecimal(ventas.doubleValue());
						b = b.setScale(2, BigDecimal.ROUND_DOWN);		
						b = b.setScale(0, BigDecimal.ROUND_HALF_UP); 
						integrante.setVentas(new Double(b.doubleValue()));
					}
				}else {
					integrante.setVentas(getVentasRelacionado(rtgInd));
				}
				
				integrante.setRatingComportamiento(rtgInd.getRatingComportamiento());
				integrante.setRatingNegocio(rtgInd.getRatingNegocio());
				integrante.setRatingFinanciero(rtgInd.getRatingFinanciero());
				integrante.setRatingIndividual(rtgInd.getRatingFinal());
				integrante.setFechaRatingIndividual(rtgInd.getFechaCambioEstado());
				integrante.setRatingProyectado(rtgInd.getRatingProyectado());//Spring 01 requerimiento REQ:7.1.5
				integrante.setModeloRating(rtgInd.getBanca());//Spring 01 requerimiento REQ:7.1.6
			} else if (integrante.getFlagComportamientoActual() != null) {
				integrante.setVentas(null);
				integrante.setRatingComportamiento(null);
				integrante.setRatingNegocio(null);
				integrante.setRatingFinanciero(null);
				integrante.setRatingIndividual(null);
				integrante.setFechaRatingIndividual(null);
				integrante.setRatingProyectado(null);//Spring 01 requerimiento REQ:7.1.5
				integrante.setRatingComportamiento(getNotaRatingComporamiento(integrante.getFlagComportamientoActual()));
				integrante.setModeloRating(null);//Spring 01 requerimiento REQ:7.1.6
			} else {
				integrante.setVentas(null);
				integrante.setRatingComportamiento(null);
				integrante.setRatingNegocio(null);
				integrante.setRatingFinanciero(null);
				integrante.setRatingIndividual(null);
				integrante.setFechaRatingIndividual(null);
				integrante.setRatingProyectado(null);//Spring 01 requerimiento REQ:7.1.5
				integrante.setRatingComportamiento(null);
				integrante.setModeloRating(null);//Spring 01 requerimiento REQ:7.1.6
			}

			if (log.isDebugEnabled()) {
				timerUtil.startTime();
				log.debug("Buscando Deudas: " + integrante.getRutRelacionado());
			}

			// Se obtienen las deudas banco y sbif
			ServicioConsultaDeuda srvConsultaDeuda = new ServicioConsultaDeudaODS();
			// jlmanriquez.20130307 - Solucion P37.
			deudaBanco = srvConsultaDeuda.buscarDeudaUltimoPeriodoCache(integrante.getRutRelacionado(),
					ConstantesSEFE.FLAG_DEUDA_TIPO_BANCO);
			deudaSbif = srvConsultaDeuda.buscarDeudaUltimoPeriodoCache(integrante.getRutRelacionado(), ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF);

			if (log.isDebugEnabled()) {
				log.debug("Deudas obtenidas en: " + timerUtil.endTime() + "ms");
			}

			// Se muestra la deuda mas actualizada que existe en el sistema para
			// el rut relacionado
			if (deudaBanco != null) {
				integrante.setDeudaBanco(deudaBanco.getTotalDeudaDirecta());
			}

			if (deudaSbif != null) {
				integrante.setDeudaSbif(deudaSbif.getTotalDeudaDirecta());
				integrante.setDeudaACHEL(deudaSbif.getDeudaLeasing());
			}

			integrante.setFechaModificacion(new Date());

			if (log.isDebugEnabled()) {
				log.debug("Tiempo total en la actualizacion del relacionado: " + timerUtil.endTime() + "ms");
			}
		}
	}

	public void actualizarInfoRelacionadoNoPyME(IntegranteRatingGrupo integrante, Integer idBanca) {
		ConsultaServicios consultaServicios = new ConsultaServiciosImplCache();
		GestorServicioClientes gestorCliente = new GestorServicioClientesImpl();

		// Se actualiza informacion del relacionado desde ficha chica
		FichaChica fichaChica = consultaServicios.consultarFichaChica(integrante.getRutRelacionado());

		if (fichaChica == null || ConstantesSEFE.FLAG_RUT_NO_ENCONTRADO_SIEBEL.equalsIgnoreCase(fichaChica.getNombre())) {
			String msg = MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_CLIENTE_NO_ENCONTRADO_FICHA_CHICA,
					new Object[] { integrante.getRutRelacionado() });
			log.error(msg);
			throw new BusinessOperationException(msg);
		}

		Cliente cliente = gestorCliente.obtenerClientePorRut(integrante.getRutRelacionado());

		if (seNecesitaActualizarActEconomica(fichaChica, cliente) || seNecesitaActualizaNombre(fichaChica, cliente)) {
			// Se actualiza informacion del cliente existente en SEFE o se
			// agrega en caso de no existir
			Cliente cliActualizado = gestorCliente.actualizarClienteDesdeFichaChica(integrante.getRutRelacionado());
			// FIXME Se utiliza una variable nueva cliActualizado ya que el
			// metodo actualizarClienteDesdeFichaChica no retorna lo ultimo
			// de la base sino que un objeto con los valores actualizados
			// solamente entonces el id del cliente no viene cargado
			cliente.setNombreCliente(cliActualizado.getNombreCliente());
		}

		integrante.setClasificacionRiesgo(fichaChica.getClasificacionRiesgo());
		integrante.setNombreRelacionado(cliente.getNombreCliente());
		integrante.setIdCliente(Long.valueOf(cliente.getClienteId()));

		GestorRatingIndividual gestorRatingInd = new GestorRatingIndividualImpl();

		// Se asocia el id del rating individual del relacionado
		RatingIndividual ratingIndividual = gestorRatingInd.buscarRatingVigente(integrante.getRutRelacionado());
		Long idRatingFinanciero = null;
		if (ratingIndividual != null) {
			this.actualizarRelacionadoConRatingInd(integrante, ratingIndividual);
			// Se obtiene el rating financiero asociado al rating individual
			// encontrado
			idRatingFinanciero = ratingIndividual.getIdRatingFinanciero();
			// si el vaciado financiero es null entonces se debe buscar si
			// existe un vaciado disponible que cumpla con la vigencial
			if (idRatingFinanciero == null) {
				if (idRatingFinanciero == null) {
					// si el vaciado es nulo entonces se debe utilizar el id del
					// rating proyectado
					Vaciado vaciado = buscarVaciadoParaObtenerActivos(integrante.getRutRelacionado());
					if (ratingIndividual != null && ratingIndividual.getIdRatingProyectado() != null
							&& (vaciado == null || ConstantesSEFE.BANCA_AGRICOLAS.equals(ratingIndividual.getIdBanca()))) {
						idRatingFinanciero = ratingIndividual.getIdRatingProyectado();
					}
				}
			}
		}

		// Se actualizan los activos en caso que se puedan calcular
		Double activos = getActivosEnMMCLP(integrante.getRutRelacionado(), idBanca, idRatingFinanciero);

		// Solo se actualizan los activos si es se pudieron obtener, en caso
		// contrario se mantiene lo que habÃ­a
		if (activos != null) {
			integrante.setActivos(activos);
			integrante.setActivosDesdeVaciado(true);
		} else {
			// Si no fue posible obtener el monto de activos desde un vaciado,
			// se setea la obtencion como manual
			integrante.setActivosDesdeVaciado(false);
		}
	}

	public boolean esValidoParaRatingGrupal(RatingIndividual ratingIndividual) {
		if (ratingIndividual == null)
		{
			return false;
		}
		
		RatingGrupalDAO ratingGrupalDao = new RatingGrupalDAOImpl();
		
		ArrayList modelos = (ArrayList)ratingGrupalDao.obtenerModelosIndPorRatingGrupal(ConstantesSEFE.TIPO_RATING_GRUPAL_PYME);
		
		int existe = 0;
		for (int i = 0; i < modelos.size(); i++) {
			ModIndRtgGrupal modelo = (ModIndRtgGrupal)modelos.get(i);
			if(modelo.getComponenteId().intValue() == ratingIndividual.getIdBanca().intValue())
			{
				existe = 1;
				if(!modelo.getAplicaCalculo().booleanValue())
				{
					return false;
				}
			}
		}
		
		if(existe == 0)
		{
			return false;
		}

		Calendar fechaVigenciaRating = new GregorianCalendar();
		fechaVigenciaRating.setTime(ratingIndividual.getFechaCambioEstado());

		Calendar antiguedadMaxima = new GregorianCalendar();
		Integer antigMaxRatingInd = ConfigDBManager
				.getValueAsInteger(ConstantesSEFE.ANTIGUEDAD_MAX_RATING_INDIVIDUAL_VIGENTE_UTILIZADO_EN_RTG_GRUPAL);
		antiguedadMaxima.add(Calendar.MONTH, -antigMaxRatingInd.intValue());

		// Fecha cambio de estado del rating no es menor a antiguedadMaxima
		return (!fechaVigenciaRating.before(antiguedadMaxima));
	}

	private Double getNotaRatingComporamiento(String colorRtgCompActual) {
		String keyNotaCompPorColor = ConstantesSEFE.KEY_NOTA_RATING_COMPORTAMIENTO_SEGUN_COLOR + colorRtgCompActual.toUpperCase();
		Double nota = null;
		try {
			nota = ConfigDBManager.getValueAsDouble(keyNotaCompPorColor);
		} catch (Exception e) {
			log.error("Error de configuracion en sefe.properties para la propiedad [" + keyNotaCompPorColor + "]");
			log.error("No fue posible obtener la nota de comportamiento a partir del color del comportamiento actual");
		}

		return nota;
	}

	/*
	 * Obtiene las ventas del relacionado a partir del rating individual
	 * vigente. El monto de las ventas es retornado en la moneda y unidad del
	 * vaciado.
	 */
	private Double getVentasRelacionado(RatingIndividual rtgIndividual) {
		// Si existe un rating financiero asociado (podria encontrar un migrado
		// que no tenga rtg financiero)
		if (rtgIndividual != null && rtgIndividual.getIdRatingFinanciero() != null) {
			GestorRatingFinanciero gestorRtgFinanciero = new GestorRatingFinancieroImpl();

			RatingFinanciero rtgFinanciero = gestorRtgFinanciero.obtenerRating(rtgIndividual.getIdRatingFinanciero());

			// Nos aseguramos que el rating financiero encontrado tiene el
			// vaciado asociado. Para los rtg. financieros ingresados por SEFE
			// estamos
			// seguros que tienen esta informacion pero para los migrados no.
			if (rtgFinanciero.getIdVaciado0() != null) {
				GestorVaciados gestorVaciados = new GestorVaciadosImpl();
				GestorPlanCuentas gestorPlanCtas = new GestorPlanCuentasImpl();
				String claveVentas;
				String codCuentaVentas;

				Vaciado vaciado = gestorVaciados.buscarVaciado(rtgFinanciero.getIdVaciado0());

				claveVentas = ConstantesSEFE.CODIGO_CUENTA_VENTAS;
				codCuentaVentas = ConfigDBManager.getValueAsString(claveVentas);

				// Se obtiene la cuenta y su valor
				Cuenta ctaVentas = gestorPlanCtas.consultarValorCuentaVaciado(vaciado.getIdVaciado(), codCuentaVentas);

				if (ctaVentas != null) {
					return ctaVentas.getMontoMasAjuste();
				}
			}
		}

		return null;
	}

	public RatingGrupal actualizarRatingGrupalNoPyME(RatingGrupal ratingGrupal) {
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();

		return ratingGrupalDAO.actualizarRatingGrupalNoPyME(ratingGrupal);
	}

	public IntegranteRatingGrupo editarRelacionadoPyME(IntegranteRatingGrupo integrante) {
		RatingGrupalDAO rtgGrupalDAO = new RatingGrupalDAOImpl();
		return rtgGrupalDAO.actualizarRelacionadoPyME(integrante);
	}

	public IntegranteRatingGrupo editarRelacionadoNoPyME(IntegranteRatingGrupo integrante) {
		RatingGrupalDAO rtgGrupalDAO = new RatingGrupalDAOImpl();
		return rtgGrupalDAO.actualizarRelacionadoNoPyME(integrante);
	}

	public IntegranteRatingGrupo buscarRelacionado(String rutCliente, Long idRatingInd, String rutRelacionado, Integer idRelacion,
			Long idVersion) {
		RatingGrupalDAO rtgGrupalDAO = new RatingGrupalDAOImpl();
		GestorServicioClientes gestorSrvClientes = new GestorServicioClientesImpl();

		Cliente cliente = gestorSrvClientes.obtenerClientePorRut(rutCliente);

		return rtgGrupalDAO.buscarRelacionado(Long.valueOf(cliente.getClienteId()), idRatingInd, idRelacion, rutRelacionado, idVersion);
	}

	public IntegranteRatingGrupo buscarRelacionado(Long idClienteRelacionado, Long idGrupo) {
		RatingGrupalDAO rtgGrupalDAO = new RatingGrupalDAOImpl();

		return rtgGrupalDAO.buscarRelacionado(idGrupo, idClienteRelacionado);
	}

	public RatingGrupal generarRating(String rutClienteCabecera, RatingIndividual rtgIndividualVigente, Integer idBanca, Long idUsuario) {
		RatingGrupal ratigGrupal = null;

		// Se diferencia la generacion por banca
		if (ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
			ratigGrupal = generarRatingPyME(rutClienteCabecera, rtgIndividualVigente, idUsuario);
		} else {
			ratigGrupal = generarRatingNoPyME(rutClienteCabecera, rtgIndividualVigente, idUsuario, idBanca);
		}

		return ratigGrupal;
	}

	/*
	 * Crea un nuevo rating de grupo para PyME, llenando los datos principales
	 * del nuevo rating.
	 */
	private RatingGrupal generarRatingPyME(String rutRelacionadoCabecera, RatingIndividual rtgIndividual, Long idUsuario) {
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();
		GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();

		// Se obtienen los relacionados del ultimo grupo creado para el cliente
		List pkUltimoGrupo = ratingGrupalDAO.buscarIdUltimoGrupoPyME(rtgIndividual.getIdCliente());
		List relacionados = null;
		if (pkUltimoGrupo != null && !pkUltimoGrupo.isEmpty()) {
			Long idClienteCab = (Long) pkUltimoGrupo.get(0);
			Long idRating = (Long) pkUltimoGrupo.get(1);
			Long idVersion = (Long) pkUltimoGrupo.get(2);

			relacionados = this.buscarRelacionadosGrupoPyME(idClienteCab, idRating, idVersion);
		}

		if (relacionados == null || relacionados.isEmpty()) {
			relacionados = new ArrayList();

			Cliente cliente = gestorClientes.obtenerParteInvolucradaPorId(rtgIndividual.getIdCliente());

			// Se agrega al cliente empresa logueado en el sistema
			IntegranteRatingGrupo relEmpresaLogueada = new IntegranteRatingGrupo();
			relEmpresaLogueada.setRutRelacionado(cliente.getRut());
			relEmpresaLogueada.setIdRelacion(ConstantesSEFE.ID_RELACION_EMPRESA_MADRE);
			relEmpresaLogueada.setPrcParticipacion(ConstantesSEFE.DOUBLE_CIEN);

			// Se agrega como el primer relacionado
			relacionados.add(relEmpresaLogueada);
		}

		// El id de version es un autogenerado
		RatingGrupal ratingGrupal = new RatingGrupal();
		ratingGrupal.setIdParteInvolucrada(rtgIndividual.getIdCliente());
		ratingGrupal.setIdRatingIndividual(rtgIndividual.getIdRating());
		ratingGrupal.setIdVersion(null);
		ratingGrupal.setIdUsuario(idUsuario);
		ratingGrupal.setFecha(new Date());
		ratingGrupal.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);

		// Se crea un rating grupal en curso
		ratingGrupal = ratingGrupalDAO.insertarRatingGrupalPyME(ratingGrupal);

		// En caso que exista un grupo para el cliente se actualiza la
		// informacion para cada relacionado
		for (int i = 0; i < relacionados.size(); i++) {
			IntegranteRatingGrupo integrante = (IntegranteRatingGrupo) relacionados.get(i);

			// Se asocia al relacionado con el nuevo rating grupo. La relacion y
			// el rut del relacionado siguen siendo el mismo
			integrante.setIdCliente(rtgIndividual.getIdCliente());
			integrante.setIdRating(rtgIndividual.getIdRating());
			integrante.setIdVersion(ratingGrupal.getIdVersion());

			// jlmanriquez.20130304 - La relaciÃ³n Conyuge ya no es vÃ¡lida dentro
			// de un Rating Grupal, es por esto que cuando se reutiliza un grupo
			// migrado y que venga con esta relaciÃ³n en alguno de los
			// relacionados, se cambia a sin selecciÃ³n obligando al usuario a
			// seleccionar
			// una nueva relaciÃ³n vÃ¡lida.
			if (ConstantesSEFE.ID_RELACION_CONYUGE.equals(integrante.getIdRelacion())) {
				integrante.setIdRelacion(ConstantesSEFE.ID_RELACION_SELECCIONE);
			}

			// Actualiza la instancia de cada integrante con la informacion
			// requerida
			this.actualizarInfoRelacionadoPyME(integrante);

			// Se agrega al relacionado al nuevo grupo
			this.agregarRelacionado(integrante, ConstantesSEFE.BANCA_PYME);
		}

		return ratingGrupal;
	}

	/*
	 * Genera un nuevo rating grupal para No PyME, creando un nuevo grupo.
	 */
	private RatingGrupal generarRatingNoPyME(String rutClienteCabecera, RatingIndividual rtgIndividual, Long idUsuario, Integer idBanca) {
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();
		GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();

		GrupoRating grupoRating = new GrupoRating();
		grupoRating.setIdTipoGrupo(ConstantesSEFE.GRUPO_TIPO_RATING);

		// Se crea un nuevo grupo (requerido para tener un rating grupal no
		// pyme)
		grupoRating = ratingGrupalDAO.insertarGrupoRatingNoPyME(grupoRating);

		// Se obtienen los relacionados del ultimo grupo creado par el cliente

		List pkUltGrupo = ratingGrupalDAO.buscarIdUltimoGrupoNoPyme(rtgIndividual.getIdCliente());
		List relacionados = null;
		if (pkUltGrupo != null && !pkUltGrupo.isEmpty()) {
			Long idGrupo = (Long) pkUltGrupo.get(0);

			relacionados = this.buscarRelacionadosGrupoNoPyME(idGrupo);
		}

		if (relacionados == null || relacionados.isEmpty()) {
			relacionados = new ArrayList();

			Cliente cliente = gestorClientes.obtenerParteInvolucradaPorId(rtgIndividual.getIdCliente());

			// Se agrega al cliente empresa logueado en el sistema
			IntegranteRatingGrupo relEmpresaLogueada = new IntegranteRatingGrupo();
			relEmpresaLogueada.setRutRelacionado(cliente.getRut());
			relEmpresaLogueada.setIdRelacion(null);
			relEmpresaLogueada.setPrcParticipacion(new Double(100.0));
			relEmpresaLogueada.setFlagAplica(ConstantesSEFE.FLAG_APLICA_EN_RTG_GRUPO);
			relEmpresaLogueada.setFlagParticipa(ConstantesSEFE.FLAG_PARTICIPA_EN_RTG_GRUPO);

			// Se agrega como primer relacionado
			relacionados.add(relEmpresaLogueada);
		}

		// Se crea instancia de rating grupal con el id del nuevo grupo. Solo
		// sirve como
		// contenedor del id y la fecha nada mas
		RatingGrupal ratingGrupal = new RatingGrupal();
		ratingGrupal.setIdRatingGrupal(grupoRating.getIdGrupo());
		ratingGrupal.setFecha(new Date());
		ratingGrupal.setIdUsuario(idUsuario);
		ratingGrupal.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);

		// Se inserta el rating grupal para no pyme
		ratingGrupal = ratingGrupalDAO.insertarRatingGrupalNoPyME(ratingGrupal);

		for (int i = 0; i < relacionados.size(); i++) {
			IntegranteRatingGrupo integrante = (IntegranteRatingGrupo) relacionados.get(i);

			// Al relacionado se el asocia al nuevo rating de grupo. El rut del
			// relacionado sigue siendo el mismo.
			integrante.setIdGrupo(ratingGrupal.getIdRatingGrupal());
			integrante.setFechaEfectiva(ratingGrupal.getFecha());
			integrante.setIdCliente(rtgIndividual.getIdCliente());

			// Actualiza la instancia de cada integrante con la informacion
			// requerida
			this.actualizarInfoRelacionadoNoPyME(integrante, idBanca);

			// Se graba al nuevo relacionado en el nuevo grupo
			this.agregarRelacionado(integrante, idBanca);
		}

		return ratingGrupal;
	}

	public RatingGrupal buscarRatingGrupalPyME(String rutCliente, Integer idEstado) {
		RatingGrupal ratingGrupal = null;
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();
		GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();

		Cliente cliente = gestorClientes.obtenerClientePorRut(rutCliente);
		ratingGrupal = ratingGrupalDAO.buscarRatingGrupalPyME(Long.valueOf(cliente.getClienteId()), idEstado);
		return ratingGrupal;
	}

	public RatingGrupal buscarRatingGrupalNoPyME(String rutCliente, Integer idEstado) {
		RatingGrupal ratingGrupal = null;
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();
		GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();

		Cliente cliente = gestorClientes.obtenerClientePorRut(rutCliente);
		ratingGrupal = ratingGrupalDAO.buscarRatingGrupalNoPyME(Long.valueOf(cliente.getClienteId()), idEstado);
		return ratingGrupal;
	}

	public IntegranteRatingGrupo agregarRelacionado(IntegranteRatingGrupo relacionado, Integer idBanca) {
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();
		IntegranteRatingGrupo nuevoRelacionado = null;

		if (ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
			nuevoRelacionado = ratingGrupalDAO.insertarRelacionadoPyME(relacionado);
		} else {
			// Se agrega la relacion entre el rating individual del relacionado
			// y el rating grupal, solo si es que existe rating individual
			// Esto se comenta ya que la desicion de relacionar un rating
			// individual al rating grupal solo se hace si es que fue utilizado
			// en el
			// calculo
			if (relacionado.getIdRating() != null) {
				agregarRelacionRatingIndRatingGrupalNoPyME(relacionado);
			}

			nuevoRelacionado = ratingGrupalDAO.insertarRelacionadoNoPyME(relacionado);
		}

		return nuevoRelacionado;
	}

	public RelacionRatingIndRatingGrupoNoPyME agregarRelacionRatingIndRatingGrupalNoPyME(IntegranteRatingGrupo relacionado) {
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();
		GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();

		// Se agrega la relacion entre el rating individual de cada relacionado
		// y el rating grupal
		Cliente cliRelacionado = gestorClientes.obtenerClientePorRut(relacionado.getRutRelacionado());

		RelacionRatingIndRatingGrupoNoPyME relacion = new RelacionRatingIndRatingGrupoNoPyME();
		relacion.setFechaEfectiva(new Date());
		relacion.setIdCliente(Long.valueOf(cliRelacionado.getClienteId()));
		relacion.setIdGrupo(relacionado.getIdGrupo());
		relacion.setIdRatingInd(relacionado.getIdRating());

		return ratingGrupalDAO.insertarRelacionRatingIndividualRatingGrupo(relacion);
	}

	public void eliminarRelacionado(IntegranteRatingGrupo relacionado, Integer idBanca) {
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();
		if (null != relacionado) {
			if (ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
				ratingGrupalDAO.eliminarRelacionadoPyME(relacionado.getIdCliente(), relacionado.getIdRating(), relacionado
						.getRutRelacionado(), relacionado.getIdRelacion(), relacionado.getIdVersion());
			} else {
				GestorServicioClientes gestorClientes = new GestorServicioClientesImpl();
				Cliente cliente;

				cliente = gestorClientes.obtenerClientePorRut(relacionado.getRutRelacionado());

				ratingGrupalDAO.eliminarRelacionadoNoPyME(relacionado.getIdGrupo(), Long.valueOf(cliente.getClienteId()), relacionado
						.getFechaEfectiva());
			}
		}
	}

	public CalculoRatingGrupal calcularRatingPyME(String rutCliente, List relacionados, String loginOperador) {
		AlgoritmoRatingGrupal algoritmo = new AlgoritmoRatingGrupalPyMEImpl();
		ValidadorRatingGrupal validador = new ValidadorRatingGrupalPyMEImpl();
		Map paramCalculo = new HashMap();
		Map notasEquivalentes = new HashMap();

		IntegranteRatingGrupo socioMinoritario = getSocioMinoritario(relacionados);
		IntegranteRatingGrupo socioPrincipal = getSocioPrincipal(relacionados);
		//BCHC-JRF-27-08-2015
		boolean esComportamientoRojo = false;
		for (int i = 0; i < relacionados.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) relacionados.get(i);

			// Se calcula el porcentaje de participacion del relacionado en caso
			// que operan como empresa y persona que tengan una relacion
			// del tipo 'Sin Participacion Directa'/'Empresa Hermana' o 'Sin
			// Participacion Directa' respectivamente
			calcularPrcParticipacion(relacionado, socioPrincipal, socioMinoritario);

			Double notaEquivPjeSiebel = getNotaEquivalentesPjeSiebel(relacionado, ConstantesSEFE.BANCA_PYME);
			if (notaEquivPjeSiebel != null) {
				notasEquivalentes.put(relacionado.getRutRelacionado(), notaEquivPjeSiebel);
			}
			
			if(relacionado.getIdRelacion().equals(ConstantesSEFE.ID_RELACION_EMPRESA_MADRE)){
				if(relacionado.getFlagComportamientoActual()!=null){
					if(relacionado.getFlagComportamientoActual().equalsIgnoreCase(ConstantesRating.KEY_RTG_COMP_PJE_ROJO)){
						esComportamientoRojo = true;
					}
				}
			}
		}

		paramCalculo.put(AlgoritmoRatingGrupal.PARAM_RELACIONADOS, relacionados);
		paramCalculo.put(AlgoritmoRatingGrupal.PARAM_NOTAS_EQUIVALENTES_PJE_SIEBEL, notasEquivalentes);
		//BCHC-JRF-27-08-2015
		paramCalculo.put(AlgoritmoRatingGrupal.COLOR_RELACIONADO_EMP_MADRE, String.valueOf(esComportamientoRojo));

		// Se genera la estructura requerida para el validador
		ArrayList paramsValidador = new ArrayList();
		paramsValidador.add(relacionados);

		// Se realiza la validacion previo al proceso de calculo
		validador.validar(paramsValidador);

		CalculoRatingGrupal resultadoCalculo = algoritmo.calcular(paramCalculo);

		return resultadoCalculo;
	}

	/*
	 * Realiza el calculo del porcentaje de participacion, para los siguientes
	 * casos: - Operan como Persona y la relacion es 'Sin Participacion
	 * Directa'. - Operan como Empresa y la relacion es 'Empresa Hermana' o 'Sin
	 * Participacion Directa'
	 */
	private void calcularPrcParticipacion(IntegranteRatingGrupo relacionado, IntegranteRatingGrupo socioPrincipal,
			IntegranteRatingGrupo socioMinoritario) {
		// Se evalua que las empresas tengan rating individual vigente
		if (relacionado.operaComoEmpresa()
				&& (ConstantesSEFE.ID_RELACION_SIN_PARTICIPACION.equals(relacionado.getIdRelacion()) || ConstantesSEFE.ID_RELACION_EMPRESA_HERMANA
						.equals(relacionado.getIdRelacion()))) {
			// Si es empresa sin participacion directa o empresa heramana se
			// calcula el porcentaje de participacion
			// Dependiendo de la nota de rating individual del relacionado se
			// utiliza el porcentaje de participacion que corresponda
			Double limRatingInd = ConfigDBManager
					.getValueAsDouble(ConstantesSEFE.LIMITE_RTG_IND_RELACIONADO_PARA_HEREDAR_PRC_PARTICIPACION);
			if (relacionado.getRatingIndividual() != null && relacionado.getRatingIndividual().doubleValue() >= limRatingInd.doubleValue()) {
				// Def. Marcela Cid con usuario Felipe. Cuando no existen socios
				// y se estaba buscando el socio minoritario se debe usar 0%
				Double porcParticipacion = ConstantesSEFE.DOUBLE_CERO;
				if (socioMinoritario != null) {
					porcParticipacion = new Double(socioMinoritario.getPrcParticipacion().doubleValue());
				}
				relacionado.setPrcParticipacion(porcParticipacion);
			} else {
				// Def. Marcela Cid con usuario Felipe. Cuando no existen socios
				// se usa el 100%
				Double porcParticipacion = ConstantesSEFE.DOUBLE_CIEN;
				if (socioPrincipal != null) {
					porcParticipacion = new Double(socioPrincipal.getPrcParticipacion().doubleValue());
				}
				relacionado.setPrcParticipacion(porcParticipacion);
			}
		} else if (ConstantesSEFE.ID_RELACION_SIN_PARTICIPACION.equals(relacionado.getIdRelacion())) {
			// Si el tipo de relacion que existe entre el relacionado y la
			// empresa cabecera es sin participacion directa, se debe calcular
			// el porcentaje de participacion.
			// Si opera como empresa y el comportamiento actual es rojo o
			// amarillo, la participaciÃ³n que se debe utilizar
			// es la del socio principal
			if (ConstantesSEFE.KEY_RTG_COMP_PJE_ROJO.equalsIgnoreCase(relacionado.getFlagComportamientoActual())
					|| ConstantesSEFE.KEY_RTG_COMP_PJE_AMARILLO.equalsIgnoreCase(relacionado.getFlagComportamientoActual())) {
				// Def. Marcela Cid con usuario Felipe. Cuando no existen socios
				// se usa el 100%
				Double porcParticipacion = ConstantesSEFE.DOUBLE_CIEN;
				if (socioPrincipal != null) {
					porcParticipacion = new Double(socioPrincipal.getPrcParticipacion().doubleValue());
				}
				relacionado.setPrcParticipacion(porcParticipacion);
			} else {
				relacionado.setPrcParticipacion(ConstantesSEFE.DOUBLE_CERO);
			}
		}
	}

	/*
	 * Retorna el relacionado SOCIO con mas participacion.
	 */
	private IntegranteRatingGrupo getSocioPrincipal(List relacionados) {
		IntegranteRatingGrupo socioPrincipal = null;
		for (int i = 0; i < relacionados.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) relacionados.get(i);

			// Si es SOCIO
			if (ConstantesSEFE.ID_RELACION_SOCIO.equals(relacionado.getIdRelacion())) {
				if ((socioPrincipal == null && relacionado.getPrcParticipacion() != null)
						|| (socioPrincipal != null && socioPrincipal.getPrcParticipacion() != null
								&& relacionado.getPrcParticipacion() != null && socioPrincipal.getPrcParticipacion().compareTo(
								relacionado.getPrcParticipacion()) < 0)) {
					socioPrincipal = relacionado;
				}
			}
		}

		return socioPrincipal;
	}

	/*
	 * Retorna el relacionado SOCIO con menos participacion.
	 */
	private IntegranteRatingGrupo getSocioMinoritario(List relacionados) {
		IntegranteRatingGrupo socioMinoritario = null;
		for (int i = 0; i < relacionados.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) relacionados.get(i);

			// Si es SOCIO
			if (ConstantesSEFE.ID_RELACION_SOCIO.equals(relacionado.getIdRelacion())) {
				if ((socioMinoritario == null && relacionado.getPrcParticipacion() != null)
						|| (socioMinoritario != null && socioMinoritario.getPrcParticipacion() != null
								&& relacionado.getPrcParticipacion() != null && socioMinoritario.getPrcParticipacion().compareTo(
								relacionado.getPrcParticipacion()) > 0)) {
					socioMinoritario = relacionado;
				}
			}
		}

		return socioMinoritario;
	}

	public CalculoRatingGrupal calcularRatingNoPyME(String rutCliente, List relacionados, String loginOperador) {
		List relParticipan = new ArrayList();
		AlgoritmoRatingGrupal algoritmo = new AlgoritmoRatingGrupalNoPyMEImpl();

		for (int i = 0; i < relacionados.size(); i++) {
			IntegranteRatingGrupo relacionado = (IntegranteRatingGrupo) relacionados.get(i);

			// Si el relacionado participa, se validan los requisitos
			if (ConstantesSEFE.FLAG_PARTICIPA_EN_RTG_GRUPO.equals(relacionado.getFlagParticipa())) {
				// Se guarda en una lista por separados para utilizarlos como
				// partes del calculo
				relParticipan.add(relacionado);
			}
		}

		HashMap paramCalculo = new HashMap();
		paramCalculo.put(AlgoritmoRatingGrupal.PARAM_RELACIONADOS, relParticipan);

		// Se genera la estructura requerida por el validador
		List paramValidador = new ArrayList();
		paramValidador.add(relParticipan);

		// Se realiza la validacion previo al calculo
		ValidadorRatingGrupal validador = new ValidadorRatingGrupalCorpYSocInvImpl();
		validador.validar(paramValidador);

		return algoritmo.calcular(paramCalculo);
	}

	public RatingGrupal buscarRatingGrupalNoPyMEPorId(Long idGrupo) {
		RatingGrupalDAO ratigGrupalDAO = new RatingGrupalDAOImpl();

		return ratigGrupalDAO.buscarRatringGrupalNoPyMEPorId(idGrupo);
	}

	public boolean estaEnCurso(RatingGrupal ratingGrupal) {
		return (ratingGrupal != null && ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO.equals(ratingGrupal.getIdEstado()));
	}

	public boolean operaComoEmpresa(Integer codigoActividadEconomica) {
		return (codigoActividadEconomica.intValue() >= ConfigDBManager.getValueAsInteger(
				ConstantesSEFE.LIMITE_CODIGO_ACTIVIDAD_ECONOMICA_OPERA_COMO_EMPRESA).intValue());
	}

	/*
	 * Retorna la nota de comportamiento a partir del puntaje siebel encontrado
	 * para el relacionado.
	 */
	private Double getNotaEquivalentesPjeSiebel(IntegranteRatingGrupo relacionado, Integer idBanca) {
		GestorRatingComportamiento gestorRatingComp = new GestorRatingComportamientoImpl();
		ServicioClientes srvCtes = new ServicioClientesImpl();
		Cliente cte = srvCtes.obtenerClientePorRut(relacionado.getRutRelacionado());
		
		Caritas caritas = null;

		if (cte != null) {
			Long idCliente = Long.valueOf(cte.getClienteId());
			Calendar hoy = Calendar.getInstance();
			int diasVigencia = ConfigDBManager.getValueAsInteger(ConstantesSEFE.KEY_VIGENCIA_EVALUACION_CARITAS_RTG_COMPORTAMIENTO).intValue();
			Calendar limiteVigencia = Calendar.getInstance();
			limiteVigencia.add(Calendar.DAY_OF_MONTH, -diasVigencia);
			caritas = gestorRatingComp.buscarHistoricoCliente(idCliente, hoy.getTime(), limiteVigencia.getTime());
		}
		Double puntaje = null;
		if (caritas != null) {
			caritas.setPuntajeFinal(caritas.getPuntajePonderado());
			puntaje = caritas.getPuntajeFinal();
		} else {
			ConsultaServicios consultaServicios = new ConsultaServiciosImplCache();
			List lstEvaluaciones = null;
			try {
				lstEvaluaciones = consultaServicios
						.consultarComportamientoCrediticio(relacionado
								.getRutRelacionado());
			} catch (Exception e) {
				log
						.warn("No ha sido posible encontrar informacion de Comportamiento Crediticio para el rut relacionado: "
								+ relacionado.getRutRelacionado());
			}
			if (lstEvaluaciones != null && !lstEvaluaciones.isEmpty()) {
				EvaluacionOsb evalOsb = (EvaluacionOsb) lstEvaluaciones.get(0);
				if (evalOsb.getPuntajeEvaluacion() != null) {
					puntaje = new Double(evalOsb.getPuntajeEvaluacion());
				}
			}
		}
		Double notaEquivalentePjeSiebel = null;
		if (puntaje != null) {
			MatrizPuntajeCaritas matriz = gestorRatingComp.buscarMatrizPuntajeCaritas(idBanca, puntaje);
			if (matriz != null) {
				notaEquivalentePjeSiebel = matriz.getNotaRatingComportamiento();
			}
		}
		return notaEquivalentePjeSiebel;
	}

	public RatingGrupal buscarRatingGrupalPyMEPorId(Long idCliente, Long idRatingInd, Long idVersion) {
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();

		return ratingGrupalDAO.buscarRatingGrupalPyMEPorId(idCliente, idRatingInd, idVersion);
	}

	public RatingGrupal actualizarRatingGrupalPyME(RatingGrupal ratingGrupal) {
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();

		ratingGrupalDAO.actualizarRatingGrupalPyME(ratingGrupal);

		return ratingGrupal;
	}

	public void actualizarNotaSiebel(RatingGrupal ratingGrupal, boolean esPyME, boolean notaManual) {
		ConsultaServicios consultaServicios = new ConsultaServiciosImplCache();
		GestorRatingGrupal gestorRtgGrupal = new GestorRatingGrupalImpl();
		List relacionados;

		if (esPyME) {
			relacionados = gestorRtgGrupal.buscarRelacionadosGrupoPyME(ratingGrupal.getIdParteInvolucrada(), ratingGrupal
					.getIdRatingIndividual(), ratingGrupal.getIdVersion());
		} else {
			relacionados = gestorRtgGrupal.buscarRelacionadosGrupoNoPyME(ratingGrupal.getIdRatingGrupal());
		}

		String rtgGrupal = null;
		if (notaManual) {
			rtgGrupal = ratingGrupal.getRatingManual().toString();
		} else {
			rtgGrupal = ratingGrupal.getNota().toString();
		}

		// Se actualiza la nota en siebel para cada integrante del grupo
		if (relacionados != null) {
			for (int i = 0; i < relacionados.size(); i++) {
				IntegranteRatingGrupo rel = (IntegranteRatingGrupo) relacionados.get(i);

				consultaServicios.actualizarRating(rel.getRutRelacionado(), null, rtgGrupal);
			}
		}
	}

	public RelacionRatingIndRatingGrupoNoPyME buscarRelacionRatingIndGrupoNoPyME(Long idCliente, Long idRatingInd, Long idGrupo) {
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();
		return ratingGrupalDAO.buscarRelacionRatingIndGrupoNoPyME(idCliente, idRatingInd, idGrupo);
	}

	public List buscarRelacionRatingIndGrupoNoPyME(Long idCliente, Long idGrupo) {
		RatingGrupalDAO ratingGrupalDAO = new RatingGrupalDAOImpl();
		return ratingGrupalDAO.buscarRelacionRatingIndGrupoNoPyME(idCliente, idGrupo);
	}

	public Vaciado obtenerVaciado(IntegranteRatingGrupo relacionado) {
		GestorVaciados gestorVaciados = new GestorVaciadosImpl();
		GestorRatingIndividual gestorRatingInd = new GestorRatingIndividualImpl();

		RatingIndividual rtgIndividual = gestorRatingInd.buscarRatingIndividual(relacionado.getIdCliente(), relacionado.getIdRating());

		// Migrados. Puede que no tengan asociado un rating financiero
		if (rtgIndividual.getIdRatingFinanciero() != null) {
			GestorRatingFinanciero gestorRatingFinanciero = new GestorRatingFinancieroImpl();

			RatingFinanciero ratingFinanciero = gestorRatingFinanciero.obtenerRating(rtgIndividual.getIdRatingFinanciero());

			Vaciado vaciado = gestorVaciados.buscarVaciado(ratingFinanciero.getIdVaciado0());

			return vaciado;
		}

		return null;
	}

	public boolean vaciadoAsociadoEsConsolidado(Vaciado vaciado) {
		List idsTposVaciados = ConfigDBManager
				.getValuesAsListString(ConstantesSEFE.KEY_IDS_TPOS_VACIADOS_CONSOLIDADOS_RATING_GRUPAL_NO_PYME);

		// Retorna true si el tipo de vaciado asociado al rating financiero, se
		// encuentra dentro de los ids configurados
		return (vaciado != null && idsTposVaciados.contains(vaciado.getIdTpoVaciado().toString()));
	}

	public void actualizarRelacionadoConRatingInd(IntegranteRatingGrupo relacionado, RatingIndividual ratingIndividual) {
		relacionado.setRatingIndividual(ratingIndividual.getRatingFinal());
		relacionado.setFechaRatingIndividual(ratingIndividual.getFechaCambioEstado());
		relacionado.setIdRating(ratingIndividual.getIdRating());
	}

	public RatingGrupal clonarRatingGrupalPyME(Long idCliente, Long idRatingInd, Long idVersion, Long idUsuario) {
		RatingGrupalDAO rtgGrupalDAO = new RatingGrupalDAOImpl();

		// Se obtiene el rating grupal a clonar y se asocia al nuevo rating
		// individual
		RatingGrupal rtgGrupalClon = this.buscarRatingGrupalPyMEPorId(idCliente, idRatingInd, idVersion);

		// Se crea un nuevo rating grupal el 'clonado' en estado en curso
		rtgGrupalClon.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		rtgGrupalClon.setIdVersion(null);

		// Se guarda el nuevo rating grupal(clonado)
		rtgGrupalClon = rtgGrupalDAO.insertarRatingGrupalPyME(rtgGrupalClon);

		// Se clonan los relacionados del grupo pyme
		List relacionados = this.buscarRelacionadosGrupoPyME(idCliente, idRatingInd, idVersion);
		if (relacionados != null) {
			for (int i = 0; i < relacionados.size(); i++) {
				IntegranteRatingGrupo rel = (IntegranteRatingGrupo) relacionados.get(i);
				rel.setFechaEfectiva(new Date());
				rel.setIdUsuario(idUsuario);
				rel.setIdVersion(rtgGrupalClon.getIdVersion());

				// Se inserta el relacionado asociado al nuevo grupo
				rtgGrupalDAO.insertarRelacionadoPyME(rel);
			}
		}
		return rtgGrupalClon;
	}

	public RatingGrupal clonarRatingGrupalNoPyME(Long idGrupo, Long idUsuario) {
		RatingGrupal rtgClonado = null;
		RatingGrupalDAO rtgGrupalDAO = new RatingGrupalDAOImpl();

		GrupoRating grupoRating = new GrupoRating();
		grupoRating.setIdTipoGrupo(ConstantesSEFE.GRUPO_TIPO_RATING);

		grupoRating = rtgGrupalDAO.insertarGrupoRatingNoPyME(grupoRating);

		// Busco el rating grupal a clonar y se asocia al nuevo grupo creado
		rtgClonado = this.buscarRatingGrupalNoPyMEPorId(idGrupo);
		rtgClonado.setIdRatingGrupal(grupoRating.getIdGrupo());
		rtgClonado.setFecha(new Date());
		rtgClonado.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		rtgClonado.setIdUsuario(idUsuario);

		rtgClonado = rtgGrupalDAO.insertarRatingGrupalNoPyME(rtgClonado);

		// Se clona cada relacionado del grupo
		List lstRelacionados = this.buscarRelacionadosGrupoNoPyME(idGrupo);
		RatingGrupalDAO rtgGrpDAO = new RatingGrupalDAOImpl();
		if (lstRelacionados != null) {
			for (int i = 0; i < lstRelacionados.size(); i++) {
				IntegranteRatingGrupo rel = (IntegranteRatingGrupo) lstRelacionados.get(i);
				rel.setIdGrupo(rtgClonado.getIdRatingGrupal());
				rel.setFechaEfectiva(new Date());
				rel.setIdUsuario(idUsuario);

				// Se inserta el relacionado asociado al nuevo grupo
				rtgGrupalDAO.insertarRelacionadoNoPyME(rel);

				// Por cada relacionado se clona el rating individual utilizado.
				List lstRelacionesRtgIndConGrp = this.buscarRelacionRatingIndGrupoNoPyME(rel.getIdCliente(), idGrupo);
				// Si se usaro un rating individual para el relacionado, se
				// clona
				if (lstRelacionesRtgIndConGrp != null && !lstRelacionesRtgIndConGrp.isEmpty()) {
					// Por grupo y cliente siempre habra como maximo un rating
					// individual
					RelacionRatingIndRatingGrupoNoPyME relRtgIndConRtgGrp = (RelacionRatingIndRatingGrupoNoPyME) lstRelacionesRtgIndConGrp
							.get(0);
					relRtgIndConRtgGrp.setIdGrupo(rtgClonado.getIdRatingGrupal());

					// Se agrega la relacion entre el rating individual y el
					// rating grupal clonado
					rtgGrpDAO.insertarRelacionRatingIndividualRatingGrupo(relRtgIndConRtgGrp);
				}
			}
		}
		return rtgClonado;
	}

	public Vaciado buscarVaciadoParaObtenerActivos(String rutCliente) {
		RatingGrupalDAO rtgGrupalDAO = new RatingGrupalDAOImpl();
		GestorServicioClientes gestCli = new GestorServicioClientesImpl();

		if (log.isDebugEnabled()) {
			log.debug("Buscando vaciado para obtener el monto de activos en MMCLP para rating grupal. Rut del Cliente: " + rutCliente);
		}

		Cliente cliente = gestCli.obtenerClientePorRut(rutCliente);
		Integer antigMaxVaciados = ConfigDBManager.getValueAsInteger(ConstantesSEFE.ANTIGUEDAD_MAX_VACIADO_PARA_CALCULO_ACTIVOS_RTG_GRUPAL);
		List lstVac = rtgGrupalDAO.buscarVaciadosRatingGrupal(Long.valueOf(cliente.getClienteId()), antigMaxVaciados);

		if (lstVac != null && !lstVac.isEmpty()) {
			// Se ordena segun criterio requerido por el negocio
			Collections.sort(lstVac, new ComparadorVaciadosRatingGrupal());
			Vaciado vac = (Vaciado) lstVac.get(0);

			if (log.isDebugEnabled()) {
				log.debug("Se encontro el vaciado: " + vac.getIdVaciado() + "del periodo: " + vac.getPeriodo());
			}

			return vac;
		}

		return null;
	}

	public List buscarRatingsGrupales(String rutRel, Integer idEstado) {
		ArrayList lstRtg = new ArrayList();
		RatingGrupalDAO rtgGrpDAO = new RatingGrupalDAOImpl();

		List lstRtgPyME = rtgGrpDAO.buscarRatingsGrupalesPyME(rutRel, idEstado);
		if (lstRtgPyME != null) {
			lstRtg.addAll(lstRtgPyME);
		}

		List lstRtgNoPyME = rtgGrpDAO.buscarRatingsGrupalesNoPyME(rutRel, idEstado);
		if (lstRtgNoPyME != null) {
			lstRtg.addAll(lstRtgNoPyME);
		}

		return lstRtg;
	}

	public List buscarRatingsGrupales(RatingIndividual rtgInd, Integer idEstado) {
		ArrayList lstRtg = new ArrayList();
		RatingGrupalDAO rtgGrpDAO = new RatingGrupalDAOImpl();

		Long idCli = rtgInd.getIdCliente();

		// Parche ya que cuando se si el rating individual se obtuvo mediante
		// GestorRatingIndividual.buscarRatingsIndividualesPorCliente no trae
		// cargado
		// el id de cliente pero si el rut
		if (idCli == null) {
			GestorServicioClientes gestCli = new GestorServicioClientesImpl();

			Cliente cli = gestCli.obtenerClientePorRut(rtgInd.getRut());
			idCli = Long.valueOf(cli.getClienteId());
		}

		List lstRtgPyME = rtgGrpDAO.buscarRatingsGrupalesPyME(idCli, rtgInd.getIdRating(), idEstado);
		if (lstRtgPyME != null) {
			lstRtg.addAll(lstRtgPyME);
		}

		List lstRtgNoPyME = rtgGrpDAO.buscarRatingsGrupalesNoPyME(idCli, rtgInd.getIdRating(), idEstado);
		if (lstRtgNoPyME != null) {
			lstRtg.addAll(lstRtgNoPyME);
		}

		return lstRtg;
	}

	public void cambiarEstadoRatingsGrupales(RatingIndividual rtgInd, Integer idEstadoOrigen, Integer idNuevoEstado) {
		List lstRtgGrp = this.buscarRatingsGrupales(rtgInd, idEstadoOrigen);

		if (lstRtgGrp != null) {
			for (int i = 0; i < lstRtgGrp.size(); i++) {
				RatingGrupal rtgGrp = (RatingGrupal) lstRtgGrp.get(i);

				rtgGrp.setIdEstado(idNuevoEstado);

				// Si este campo no es nulo quiere decir que es un rating grupal
				// pyme
				if (rtgGrp.getIdRatingIndividual() != null) {
					this.actualizarRatingGrupalPyME(rtgGrp);
				} else {
					this.actualizarRatingGrupalNoPyME(rtgGrp);
				}
			}
		}
	}

	public void cambiarEstadoRatingsGrupales(String rutRel, Integer idEstadoOrigen, Integer idNuevoEstado) {
		List lstRtgGrp = this.buscarRatingsGrupales(rutRel, idEstadoOrigen);

		if (lstRtgGrp != null) {
			for (int i = 0; i < lstRtgGrp.size(); i++) {
				RatingGrupal rtgGrp = (RatingGrupal) lstRtgGrp.get(i);

				rtgGrp.setIdEstado(idNuevoEstado);

				// Si este campo no es nulo quiere decir que es un rating grupal
				// pyme
				if (rtgGrp.getIdRatingIndividual() != null) {
					this.actualizarRatingGrupalPyME(rtgGrp);
				} else {
					this.actualizarRatingGrupalNoPyME(rtgGrp);
				}
			}
		}

	}

	public void borrarRatingGrupalPyME(Long idCliente, Long idRatingInd, Long idVersion) {
		new RatingGrupalDAOImpl().borrarRatingGrupalPyME(idCliente, idRatingInd, idVersion);
	}
	
	public void vincularRatingIndividual(RatingGrupal rtgGrp, RatingIndividual rtgInd) {
		RatingGrupalDAO rtgGrpDAO = new RatingGrupalDAOImpl();
		// Si el id de rating grupal existe, estamos en presencia de un rating grupal No PyME.
		if (rtgGrp.getIdRatingGrupal() != null) {
			rtgGrpDAO.vincularRatingIndividual(rtgInd.getIdCliente(), rtgInd.getIdRating(), rtgGrp.getIdRatingIndividual(), rtgGrp.getIdRatingGrupal());
		} else {
			rtgGrpDAO.vincularRatingIndividual(rtgInd.getIdCliente(), rtgInd.getIdRating(), rtgGrp.getIdRatingIndividual(), null);
		}
	}
	
	/*
	 * Req. 7.4.29 Sprint 8 Vigencia de Rtg Rtg Grupal Multisegmento 
	 */
	public List rtgGrupalMultiVig (RatingIndividual rtg){
		
		RatingGrupalDAO rtgGrpDAO = new RatingGrupalDAOImpl();
		
		List rtgGroup = rtgGrpDAO.buscarRtgVigenteMulti(rtg);
		
		return rtgGroup;
	}
	//Req S/N Spring 8 Vigencia de Rtg Grupal Multisegmento, busqueda de vigentes en los que la el relacionado no participe
	public List rtgGrupalMultiVigSoloAplica (RatingIndividual rtg){
		
		RatingGrupalDAO rtgGrpDAO = new RatingGrupalDAOImpl();
		
		List rtgGroup = rtgGrpDAO.buscarRtgVigenteMultiSoloAplica(rtg);
		
		return rtgGroup;
	}
	

	/*
	 * Req. 7.4.29 Sprint 8 Vigencia de Rtg Rtg Grupal PYME 
	 */
	public List getRatingGrupalPymePorRelacionado (RatingIndividual rtg){
		RatingGrupalDAO rtgGrpDAO = new RatingGrupalDAOImpl();
		
		List rtgGroup = rtgGrpDAO.buscarRtgVigentePyme(rtg);
		
		return rtgGroup;
	}
	
	/*
	 * Req. 7.4.29 Vigencia de RTG - Parametros Generales Malla
	 */
	public List getParamRtgGrupalVigencia(String banca) {
		
		RatingGrupalDAO rtgGrpDAO = new RatingGrupalDAOImpl();
		
		List rtgGroup = rtgGrpDAO.buscarValueRtgGrupalVigencia(banca);
		
		return rtgGroup;
	}
	
	/*
	 * Buscar Rating Pyme Cerrados Manual Multisegmento Req. 7.4.29 - Sprint 8
	 */
	public List getBuscarRtgMultiNoInformados() {
		
		RatingGrupalDAO rtgGrpDAO = new RatingGrupalDAOImpl();
		
		List rtgGroup = rtgGrpDAO.buscarRtgMultiNoInformados();
		
		return rtgGroup;
	}
	
	/*
	 * Buscar Rating Pyme Cerrados Manual Pyme Req. 7.4.29
	 */
	public List getBuscarRtgPymeNoInformados() {
		
		RatingGrupalDAO rtgGrpDAO = new RatingGrupalDAOImpl();
		
		List rtgGroup = rtgGrpDAO.buscarRtgPymeNoInformados();
		
		return rtgGroup;
	}
}