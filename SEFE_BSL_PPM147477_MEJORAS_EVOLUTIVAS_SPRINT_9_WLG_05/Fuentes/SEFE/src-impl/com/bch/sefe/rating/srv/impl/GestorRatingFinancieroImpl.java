package com.bch.sefe.rating.srv.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.dao.ParametrizadorIbatis;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.ServicioConsultaDeuda;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.srv.impl.ServicioConsultaDeudaODS;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.DeudaCliente;
import com.bch.sefe.comun.vo.PeriodoRating;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.dao.RatingFinancieroDAO;
import com.bch.sefe.rating.dao.impl.RatingFinancieroDAOImpl;
import com.bch.sefe.rating.impl.ComparadorVaciadosRatingFinancieroCGE;
import com.bch.sefe.rating.srv.AlgoritmoRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.ValidadorRating;
import com.bch.sefe.rating.vo.AjusteCalidad;
import com.bch.sefe.rating.vo.EvaluacionFinanciera;
import com.bch.sefe.rating.vo.IndicadorFinanciero;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.PonderacionMatrizFinanciera;
import com.bch.sefe.rating.vo.RangoNotaFinanciera;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.vaciados.srv.GestorPlanCuentas;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorPlanCuentasImpl;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;

public class GestorRatingFinancieroImpl implements GestorRatingFinanciero {

	public Vaciado buscarVaciadoAnteriorRating(Vaciado vaciado, Integer idBanca) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();
		List vaciadosAnteriores = null;
		vaciadosAnteriores = rtgFinancieroDAO.buscarVaciadosAnterioresGenerico(vaciado.getIdVaciado());
		// Si hay vaciados anteriores se retorna el primero, que corresponde al
		// vaciado que cumple de mejor forma las reglas de negocio
//		if (vaciadosAnteriores.size()>1) {
//			for (int i=0; i< vaciadosAnteriores.size();i++) {
//				Vaciado vaciadoAnt = (Vaciado)vaciadosAnteriores.get(i);
//				if( vaciadoAnt.getIdNombrePlanCtas().equals(vaciado.getIdNombrePlanCtas()) && vaciadoAnt.getIdTipoPlan().equals(vaciado.getIdTipoPlan()) ) {
//					return vaciadoAnt;
//				}
//			}			
//		}
		return (vaciadosAnteriores != null && !vaciadosAnteriores.isEmpty() ? (Vaciado) vaciadosAnteriores.get(0) : null);
			
	}

	public RatingFinanciero calcularRating(String rutCliente, Integer idBanca, Long idVaciado, String loginUsuario) {
		// Se crea la key que permite obtener de manera dinamica la
		// implementacion del algoritmo de calculo segun banca
		String keyImplementacionAlgoritmo =  ConstantesSEFE.KEY_ALGORITMO_RTG_FINANCIERO + ConstantesSEFE.PUNTO + idBanca;
		
		/*
		 * marias - 20121109
		 * Se incorpora el uso de un algoritmo por defecto, si no se encuentra uno configurado
		 * para la plantilla (modelo) pasada como argumento
		 */
		if (ConfigManager.getValueAsString(keyImplementacionAlgoritmo) == null 
			||	ConfigManager.getValueAsString(keyImplementacionAlgoritmo).trim().equals("")) {
			keyImplementacionAlgoritmo = ConstantesSEFE.KEY_ALGORITMO_RTG_FINANCIERO + ConstantesSEFE.PUNTO + ConstantesSEFE.KEY_DEFAULT;
		}
		
		// Se obtiene la implementacion del calculo segun la banca
		AlgoritmoRatingFinanciero algoritmo = (AlgoritmoRatingFinanciero) ConfigManager.getInstanceOf(keyImplementacionAlgoritmo);
		// Se realiza el calculo del rating financiero
		RatingFinanciero ratingFinanciero = algoritmo.calcularRating(idBanca, idVaciado);
		//Sprint 3 7.4.2 ajuste de nota minima
		Double notaMinima = RatingUtil.getNotaMinima(idBanca, ConstantesSEFE.TIPO_RATING_FINANCIERO, ratingFinanciero.getNotaFinanciera());
		if (ratingFinanciero.getNotaFinanciera() != null && ratingFinanciero.getNotaFinanciera().compareTo(notaMinima) < 0) {
			ratingFinanciero.setNotaFinanciera(notaMinima);
		}
		GestorUsuarios gestorUsuario = new GestorUsuariosImpl();
		Usuario usuario = gestorUsuario.obtenerPrimerUsuario(loginUsuario);

		ratingFinanciero.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);
		ratingFinanciero.setResponsable(loginUsuario);
		ratingFinanciero.setIdUsuario(usuario.getUsuarioId());

		// Se guardan las deudas en el rating financiero para que sean
		// actualizados en el rating individual
		ServicioConsultaDeuda consultaDeuda = new ServicioConsultaDeudaODS();
		DeudaCliente deudaClienteSBIF = consultaDeuda.buscarDeudaUltimoPeriodoCache(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF);
		DeudaCliente deudaClienteBanco = consultaDeuda.buscarDeudaUltimoPeriodoCache(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_BANCO);

		if (deudaClienteSBIF != null) {
			ratingFinanciero.setDeudaACHEL(deudaClienteSBIF.getDeudaLeasing());
			ratingFinanciero.setDeudaSBIF(deudaClienteSBIF.getTotalDeudaDirecta());
			ratingFinanciero.setDeudaSinHipotecarioSBIF(deudaClienteSBIF.getDeudaHipotecaria());
		}

		if (deudaClienteBanco != null) {
			ratingFinanciero.setDeudaBanco(deudaClienteBanco.getTotalDeudaDirecta());
			ratingFinanciero.setDeudaSinHipotecarioBanco(deudaClienteBanco.getDeudaHipotecaria());
		}

		// Se obtienen las ventas y activos del vaciado
		GestorPlanCuentas gestorPlanCtas = new GestorPlanCuentasImpl();
		GestorVaciados gestorVaciados = new GestorVaciadosImpl();

		Vaciado vac = gestorVaciados.buscarVaciado(ratingFinanciero.getIdVaciado0());
		if (!esVaciadoAnual(vac)) {
			if (ratingFinanciero.getIdVaciado1() != null) {
				vac = gestorVaciados.buscarVaciado(ratingFinanciero.getIdVaciado1());
			} else {
				vac = null;
			}
		}

		Cuenta ctaVentas = null;
		Cuenta ctaActivos = null;

		if (vac != null) {
			ctaVentas = gestorPlanCtas.consultarValorCuentaVaciado(vac.getIdVaciado(), ConfigDBManager.getValueAsString(ConstantesSEFE.CODIGO_CUENTA_VENTAS ));
			ctaActivos = gestorPlanCtas.consultarValorCuentaVaciado(vac.getIdVaciado(), ConfigDBManager.getValueAsString(ConstantesSEFE.CUENTA_TOTAL_ACTIVOS + vac.getIdNombrePlanCtas()));

			if (ctaVentas != null) {
				ratingFinanciero.setMontoVentas(ctaVentas.getMontoMasAjuste());
				ConversorMoneda convMoneda = new ConversorMonedaImpl();
				Double montoHomologadoMilesPesos = new Double(0);
				if (ctaVentas.getMontoAjustado() != null) {
					// convertir el monto a miles de pesos para ser guardado en
					// la db
					montoHomologadoMilesPesos = convMoneda.convertirMoneda(ctaVentas.getMontoMasAjuste(), vac.getIdMoneda(), vac.getUnidMedida(), ConstantesSEFE.ID_CLASIF_MONEDA_CLP,
							ConstantesSEFE.ID_CLASIF_MILES, convMoneda.buscarDiaHabilSiguiente(vac.getPeriodo()));
				} else {
					montoHomologadoMilesPesos = convMoneda.convertirMoneda(ctaVentas.getMonto(), vac.getIdMoneda(), vac.getUnidMedida(), ConstantesSEFE.ID_CLASIF_MONEDA_CLP,
							ConstantesSEFE.ID_CLASIF_MILES, convMoneda.buscarDiaHabilSiguiente(vac.getPeriodo()));
				}
				ratingFinanciero.setMontoVentaMMUF(montoHomologadoMilesPesos);

			}
			
			if (ctaActivos != null) {
				ConversorMoneda convMoneda = new ConversorMonedaImpl();
				Double montoHomologadoMilesPesos = new Double(0);
				if (ctaActivos.getMontoAjustado() != null) {
					// convertir el monto a miles de pesos para ser guardado en
					// la db
					montoHomologadoMilesPesos = convMoneda.convertirMoneda(ctaActivos.getMontoMasAjuste(), vac.getIdMoneda(), vac.getUnidMedida(), ConstantesSEFE.ID_CLASIF_MONEDA_CLP,
							ConstantesSEFE.ID_CLASIF_MILES, convMoneda.buscarDiaHabilSiguiente(vac.getPeriodo()));
				} else {
					montoHomologadoMilesPesos = convMoneda.convertirMoneda(ctaActivos.getMonto(), vac.getIdMoneda(), vac.getUnidMedida(), ConstantesSEFE.ID_CLASIF_MONEDA_CLP,
							ConstantesSEFE.ID_CLASIF_MILES, convMoneda.buscarDiaHabilSiguiente(vac.getPeriodo()));
				}
				ratingFinanciero.setMontoActivos(montoHomologadoMilesPesos);
			}
			
			String codigoCta = ConfigDBManager.getValueAsString(ConstantesSEFE.CODIGO_CUENTA_PATRIMONIO);

			Cuenta ctaPatrimonio = gestorPlanCtas.consultarValorCuentaVaciado(vac.getIdVaciado(), codigoCta);
			Double montoPatrimonio = ConstantesSEFE.DOUBLE_CERO;
			ConversorMoneda convMoneda = new ConversorMonedaImpl();
			Double montoHomologadoMilesPesos = new Double(0);
			Date diaHabilSiguienteVaciado = convMoneda.buscarDiaHabilSiguiente(vac.getPeriodo());
			if (ctaPatrimonio != null) {
				if (ctaPatrimonio.getMontoMasAjuste() != null) {
					montoPatrimonio = ctaPatrimonio.getMontoMasAjuste();
					// convertir el monto a miles de pesos para ser guardado en
					// la db
					montoHomologadoMilesPesos = convMoneda.convertirMoneda(montoPatrimonio, vac.getIdMoneda(), vac.getUnidMedida(), ConstantesSEFE.ID_CLASIF_MONEDA_CLP,
							ConstantesSEFE.ID_CLASIF_MILES, convMoneda.buscarDiaHabilSiguiente(vac.getPeriodo()));
				} else {
					montoPatrimonio = ctaPatrimonio.getMonto();
					montoHomologadoMilesPesos = convMoneda.convertirMoneda(montoPatrimonio, vac.getIdMoneda(), vac.getUnidMedida(), ConstantesSEFE.ID_CLASIF_MONEDA_CLP,
							ConstantesSEFE.ID_CLASIF_MILES, diaHabilSiguienteVaciado);
				}
				ratingFinanciero.setMontoPatrimonioMMUF(montoHomologadoMilesPesos);
			}
		}

		return ratingFinanciero;
	}

	/*
	 * Determina si el vaciado pasado como argumento es un cierre anual con 12
	 * meses
	 */
	private boolean esVaciadoAnual(Vaciado vac) {
		return (vac.getMesesPer().intValue() == 12);
	}

	public RatingFinanciero grabarRating(RatingFinanciero rating) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();

		return rtgFinancieroDAO.insertarRating(rating);
	}

	private void grabarEvaluaciones(RatingFinanciero rating, List evaluacionesFinancieras) {
		if (evaluacionesFinancieras != null) {
			// Recorre y guarda cada una de las evaluaciones asociadas al rating
			// financiero
			for (int i = 0; i < evaluacionesFinancieras.size(); i++) {
				EvaluacionFinanciera evaluacion = (EvaluacionFinanciera) evaluacionesFinancieras.get(i);

				evaluacion.setIdRating(rating.getIdRating());

				// Se graba la evalucion financiera
				this.grabarEvaluacionFinanciera(evaluacion);
			}
		}
	}
	
	public void grabarEvaluaciones(RatingFinanciero ratingFinanciero) {
		ArrayList evaluacionesFinancierasRating = new ArrayList();
		// Se obtienen los periodos utilizados en el rating financiero
		List periodos = ratingFinanciero.getPeriodos();

		PeriodoRating periodo;
		List evaluacionesPeriodo;

		// Se recorren para obtener todas las instancias de evaluacion
		// financiera
		for (int i = 0; i < periodos.size(); i++) {
			periodo = (PeriodoRating) periodos.get(i);

			// Se obtienen las evaluaciones del periodo de rating y se guardan
			// en una lista comun
			evaluacionesPeriodo = periodo.getEvaluacionesFinancieras();

			if (evaluacionesPeriodo != null && !evaluacionesPeriodo.isEmpty()) {
				evaluacionesFinancierasRating.addAll(evaluacionesPeriodo);
			}
		}
		
		grabarEvaluaciones(ratingFinanciero, evaluacionesFinancierasRating);
	}

	

	public RatingFinanciero grabarEvaluacionesProyectado(RatingFinanciero ratingFinanciero) {
		ArrayList evaluacionesFinancierasRating = new ArrayList();

		// Se obtienen los periodos utilizados en el rating financiero
		List periodos = ratingFinanciero.getPeriodos();

		PeriodoRating periodo;
		List evaluacionesPeriodo;

		// Se recorren para obtener todas las instancias de evaluacion
		// financiera
		for (int i = 0; i < periodos.size(); i++) {
			periodo = (PeriodoRating) periodos.get(i);

			// Se obtienen las evaluaciones del periodo de rating y se guardan
			// en una lista comun
			evaluacionesPeriodo = periodo.getEvaluacionesFinancieras();

			if (evaluacionesPeriodo != null && !evaluacionesPeriodo.isEmpty()) {
				evaluacionesFinancierasRating.addAll(evaluacionesPeriodo);
			}
		}

		return grabarRatingProyectado(ratingFinanciero, evaluacionesFinancierasRating);
	}

	private RatingFinanciero grabarRatingProyectado(RatingFinanciero rating, List evaluacionesFinancieras) {
		RatingFinanciero nuevoRatingFinanciero = rating;

		if (evaluacionesFinancieras != null) {
			// Recorre y guarda cada una de las evaluaciones asociadas al rating
			// financiero
			for (int i = 0; i < evaluacionesFinancieras.size(); i++) {
				EvaluacionFinanciera evaluacion = (EvaluacionFinanciera) evaluacionesFinancieras.get(i);

				evaluacion.setIdRating(nuevoRatingFinanciero.getIdRating());

				// Se graba la evalucion financiera
				this.grabarEvaluacionFinanciera(evaluacion);
			}
		}

		return nuevoRatingFinanciero;
	}

	public RatingFinanciero buscarRatingFinancieroPorIdVaciadoActual(Long idVaciado) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();
		if (idVaciado != null) {
			return rtgFinancieroDAO.buscarRatingPorIdVaciadoActual(idVaciado);
		}
		return null;
	}

//	public List buscarVaciadosParaRating(String rutCliente, Integer idBanca) {
//		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();
//
//		List vaciados = rtgFinancieroDAO.buscarVaciadosParaRating(rutCliente, idBanca);
//
//		if (vaciados == null || vaciados.isEmpty())
//			return null;
//
//		return aplicarFiltros(vaciados);
//	}

	/**
	 * Permite filtrar los vaciados de un mismo periodo haciendo uso del
	 * siguiente criterio de ordenación: 1.- Fecha EEFF, orden ascendente 2.-
	 * Estado (vigente, curso) 3.- Tipo de vaciado (Individual, Consolidado,
	 * Combinado) 4.- Vaciado ajustado prima sobre no ajustado 5.- Tipo de
	 * Balance (Clasificado, Tributario, FECU, IFRSCF, IFRSCN, DAI) 6.- Plan de
	 * Cuenta (CHGAAP, IFRSCN, IFRSCF) 7.- Moneda (CLP, USD, EUR), el resto de
	 * las monedas orden alfabético
	 * 
	 * @param lstVaciados
	 * @return
	 */
	private List aplicarFiltros(List lstVaciados) {
		List vaciadosFiltrados = new ArrayList();
		List keysAgregados = new ArrayList();
		Vaciado vaciado;
		String key;

		Collections.sort(lstVaciados, new ComparadorVaciadosRatingFinancieroCGE());

		for (int i = 0; i < lstVaciados.size(); i++) {
			vaciado = (Vaciado) lstVaciados.get(i);
			key = "" + vaciado.getIdParteInv() + vaciado.getMesesPer() + vaciado.getVersion() + vaciado.getIdTpoVaciado() + vaciado.getIdParteInvFondo() + vaciado.getIdMoneda() + vaciado.getPeriodo()
					+ vaciado.getTipoProyeccion() + vaciado.getIdNombrePlanCtas();

			// Si no existe el key se agrega el registro
			if (!keysAgregados.contains(key)) {
				vaciadosFiltrados.add(vaciado);
				keysAgregados.add(key);
			}
		}

		return vaciadosFiltrados;
	}

	public MatrizFinanciera obtenerMatrizFinanciera(Integer idBanca, Integer idSegmento, Boolean esAnual) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();

		return rtgFinancieroDAO.buscarMatrizFinanciera(idBanca, idSegmento);
	}

	/* marias 20121030
	 * Se modifica para retornar la matriz financiera con los porcentajes ponderados
	 * basados en la configuracion de la combinacion de periodos
	 */
	public MatrizFinanciera obtenerMatrizFinanciera(Integer idBanca, Integer idSegmento, Boolean esAnual, String combinacion, int mesesPer) {
		try {
			RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();
	
			MatrizFinanciera matriz = rtgFinancieroDAO.buscarMatrizFinanciera(idBanca, idSegmento);
			return obtenerMatrizFinanciera(matriz.getIdMatriz(), combinacion, mesesPer);
		} catch (Exception ex) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RATING_MSG_NO_EXISTEN_MATRIZ_VIGENTE));
		}
		
	}
	
	public MatrizFinanciera obtenerMatrizFinanciera(Long idMatriz) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();

		return rtgFinancieroDAO.buscarMatrizFinancieraPorId(idMatriz);
	}
	
	
	/* marias 20121030
	 * Se modifica para retornar la matriz financiera con los porcentajes ponderados
	 * basados en la configuracion de la combinacion de periodos
	 */
	public MatrizFinanciera obtenerMatrizFinanciera(Long idMatriz, String combinacion, int mesesPer) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();
		List ponderacionesMatriz = rtgFinancieroDAO.buscarPonderacionesMatriz(idMatriz);
		
		// la ponderacion buscada
		Map ponderaciones = new HashMap();
		
		// se recorre la lista de ponderaciones hasta encontrar la combinacion correcta
		for (int i = 0; i < ponderacionesMatriz.size(); ++i) {
			PonderacionMatrizFinanciera p = (PonderacionMatrizFinanciera) ponderacionesMatriz.get(i);
			
			// se encontro la ponderacion buscada y se pone en el buffer
			if (p.getCombinacionPonderacion().getCombinacion().equalsIgnoreCase(combinacion)) {
				
				// y se pone en el mapa para cada uno de los meses del periodo
				// indexando por la clave idPeriodo
				if (mesesPer >= p.getMesesPeriodoInf().intValue() && mesesPer <= p.getMesesPeriodoSup().intValue()) {
					ponderaciones.put(p.getIdPeriodo(), p);
				}
			}
		}
		
		MatrizFinanciera matriz = rtgFinancieroDAO.buscarMatrizFinancieraPorId(idMatriz);
		
		// se modifican las ponderaciones en la matriz
		if (!ponderaciones.isEmpty()) {
			matriz.setExistePonderacion(true);
			PonderacionMatrizFinanciera p = null;
			// si es una combinacion de cierre anual
			if (combinacion.endsWith("A")) {
				p = (PonderacionMatrizFinanciera) ponderaciones.get(ConstantesSEFE.PERIODO_0);
				matriz.setPonderacionAnual0(p.getPonderacion());
				
				p = (PonderacionMatrizFinanciera) ponderaciones.get(ConstantesSEFE.PERIODO_1);
				if (p != null) {
					matriz.setPonderacionAnual1(p.getPonderacion());
				}
				p = (PonderacionMatrizFinanciera) ponderaciones.get(ConstantesSEFE.PERIODO_2);
				if (p != null) {
					matriz.setPonderacionAnual2(p.getPonderacion());
				}
			} else {
				p = (PonderacionMatrizFinanciera) ponderaciones.get(ConstantesSEFE.PERIODO_0);
				matriz.setPonderacionNoAnual0(p.getPonderacion());
				
				p = (PonderacionMatrizFinanciera) ponderaciones.get(ConstantesSEFE.PERIODO_1);
				if (p != null) {
					matriz.setPonderacionNoAnual1(p.getPonderacion());
				}
				p = (PonderacionMatrizFinanciera) ponderaciones.get(ConstantesSEFE.PERIODO_2);
				if (p != null) {
					matriz.setPonderacionNoAnual2(p.getPonderacion());
				}
			}
		}else{
			matriz.setExistePonderacion(false);
		}
		
		return matriz;
	}

	public List obtenerTemasFinancieros(Long idMatriz, Date fechaRating, Integer estadoRating) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();

		return rtgFinancieroDAO.buscarTemasMatrizFinanciera(idMatriz, fechaRating, estadoRating);
	}

	public List obtenerIndicadoresFinancieros(Long idMatriz, Long idTema, Date fechaRating, Integer estado) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();
		return  rtgFinancieroDAO.buscarIndicadoresFinancieros(idMatriz, idTema, fechaRating, estado);

	}
	

	public RatingFinanciero obtenerRating(Long idRatingFinanciero) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();

		return rtgFinancieroDAO.buscarRatingPorId(idRatingFinanciero);
	}

	public boolean estaVigente(RatingFinanciero rtgFinanciero, Integer idBanca) {
		ValidadorRating validador = new ValidadorRatingFinancieroImpl();
		GestorVaciados gestorVac = new GestorVaciadosImpl();

		Vaciado vac = gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado0());

		return validador.vaciadoEsVigente(vac, idBanca);
	}
	
	public boolean validarIvas(RatingFinanciero rtgFinanciero) {
		ValidadorRating validador = new ValidadorRatingFinancieroImpl();

		return validador.validarIvas(rtgFinanciero.getFechaRating());
	}

	public RangoNotaFinanciera obtenerRangoNota(IndicadorFinanciero indicador, Double valor, Integer notaInferior, Integer notaSuperior, Date fechaRating, Integer estado) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();

		return rtgFinancieroDAO.buscarRangoNota(indicador.getIdMatriz(), indicador.getIdTema(), indicador.getCodigoCuentaId(), valor, notaInferior, notaSuperior, fechaRating, estado);
	}

	public AjusteCalidad buscarAjustePorCalidad(Integer idCalidadVaciado) {
		Integer idPlantilla = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		// retorna la lista plantillas que necesitan ajuste por calidad.
		List lstIdAjustes = ConfigDBManager.getValuesAsListString(ConstantesSEFE.RTG_AJUSTE_POR_CALIDAD);
		if (lstIdAjustes != null && lstIdAjustes.contains(idPlantilla.toString())) {
			RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();
			return rtgFinancieroDAO.buscarAjustePorCalidad(idCalidadVaciado);
		}
		// no hay ajuste por calidad.
		AjusteCalidad ajusteCalidad = new AjusteCalidad();
		ajusteCalidad.setFactor(ConstantesSEFE.DOUBLE_CERO);
		return ajusteCalidad;
	}

	public EvaluacionFinanciera grabarEvaluacionFinanciera(EvaluacionFinanciera eval) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();

		rtgFinancieroDAO.insertarEvaluacionFinanciera(eval);

		return eval;
	}

	/*
	 * (sin Javadoc)
	 * 
	 * @see
	 * com.bch.sefe.rating.srv.GestorRatingFinanciero#grabarRatingProyectado
	 * (com.bch.sefe.rating.vo.RatingFinanciero)
	 */
	public RatingFinanciero grabarRatingProyectado(RatingFinanciero rating) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();

		RatingFinanciero ratingProy = rtgFinancieroDAO.guardarProyectado(rating);

		return ratingProy;
	}

	/*
	 * (sin Javadoc)
	 * 
	 * @see
	 * com.bch.sefe.rating.srv.GestorRatingFinanciero#grabarRatingProyectado
	 * (com.bch.sefe.rating.vo.RatingFinanciero)
	 */
	public RatingFinanciero actualizarRatingProyectado(Integer idBanca, RatingFinanciero rating) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();
		//Sprint 3 7.4.2 ajuste de nota minima
		Double notaMinima = RatingUtil.getNotaMinima(idBanca, ConstantesSEFE.TIPO_RATING_PROYECTADO, rating.getNotaFinanciera());
		if (rating.getNotaFinanciera() != null && rating.getNotaFinanciera().compareTo(notaMinima) < 0) {
			rating.setNotaFinanciera(notaMinima);
		}
		RatingFinanciero ratingProy = rtgFinancieroDAO.actualizarProyectado(rating);

		return ratingProy;
	}

	public RatingFinanciero actualizarRating(RatingFinanciero rating) {
		RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();

		return rtgFinancieroDAO.actualizarFinanciero(rating);
	}

	public Vaciado cambiarProyectadoAVigente(Long idVaciado) {
		RatingFinancieroDAO dao = new RatingFinancieroDAOImpl();
		Map map = new HashMap();
		map.put(ConstantesSEFE.PARAM_ID_VACIADO, idVaciado);

		ParametrizadorIbatis param = new ParametrizadorIbatis(map);
		Vaciado vac = dao.cambiarProyectadoAVigente(idVaciado);

		return vac;
	}

	public List buscarEvaluacionesFinancieras(Long idRatingFinanciero) {
		RatingFinancieroDAO ratingFinancieroDAO = new RatingFinancieroDAOImpl();

		return ratingFinancieroDAO.buscarEvaluacionesFinancieras(idRatingFinanciero);
	}

	public RangoNotaFinanciera buscarRangoNota(Long idRangoNota, Long idMatriz, Long idTema, Long codCuenta, Date fechaRating, Integer estado) {
		RatingFinancieroDAO rtgFinanDAO = new RatingFinancieroDAOImpl();		
		return rtgFinanDAO.buscarRangoNota(idRangoNota, idMatriz, idTema, codCuenta, fechaRating, estado);
	}

	public List buscarPonderacionesMatriz(Long idMatriz) {
		RatingFinancieroDAO ratingFinancieroDAO = new RatingFinancieroDAOImpl();

		return ratingFinancieroDAO.buscarPonderacionesMatriz(idMatriz);
	}

	public AjusteCalidad buscarAjustePorCalidadParaProyecciones(Integer idCalidadVaciado) {
		Integer idPlantilla = SEFEContext.getValueAsInteger(ConstantesSEFE.SEFE_CTX_ID_PLANTILLA);
		// retorna la lista plantillas que necesitan ajuste por calidad.
		List lstIdAjustes = ConfigDBManager.getValuesAsListString(ConstantesSEFE.RTG_AJUSTE_POR_CALIDAD_PROY);
		if (lstIdAjustes != null && lstIdAjustes.contains(idPlantilla.toString())) {
			RatingFinancieroDAO rtgFinancieroDAO = new RatingFinancieroDAOImpl();
			return rtgFinancieroDAO.buscarAjustePorCalidad(idCalidadVaciado);
		}
		// no hay ajuste por calidad.
		AjusteCalidad ajusteCalidad = new AjusteCalidad();
		ajusteCalidad.setFactor(ConstantesSEFE.DOUBLE_CERO);
		return ajusteCalidad;
	}


}
