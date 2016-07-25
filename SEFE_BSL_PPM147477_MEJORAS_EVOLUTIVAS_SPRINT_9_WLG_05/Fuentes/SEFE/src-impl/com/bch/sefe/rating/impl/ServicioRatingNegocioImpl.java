package com.bch.sefe.rating.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.CatalogoGeneral;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.CatalogoGeneralImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.ConstantesRating;
import com.bch.sefe.rating.ServicioRatingNegocio;
import com.bch.sefe.rating.dao.RatingNegocioDAO;
import com.bch.sefe.rating.dao.impl.RatingNegocioDAOImpl;
import com.bch.sefe.rating.srv.GestorComponentesRating;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.GestorRatingNegocio;
import com.bch.sefe.rating.srv.impl.GestorComponentesRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingFinancieroImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingNegocioImpl;
import com.bch.sefe.rating.srv.impl.RatingUtil;
import com.bch.sefe.rating.vo.AlternativaNegocio;
import com.bch.sefe.rating.vo.MatrizNegocio;
import com.bch.sefe.rating.vo.PreguntaNegocio;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.rating.vo.RespuestaNegocio;
import com.bch.sefe.rating.vo.Segmento;
import com.bch.sefe.rating.vo.TemaNegocio;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.vaciados.srv.GestorPlanCuentas;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorPlanCuentasImpl;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;

public class ServicioRatingNegocioImpl implements ServicioRatingNegocio {
	
	private Logger log = Logger.getLogger(ServicioRatingNegocioImpl.class);
	
	public MatrizNegocio obtenerMatrizNegocio(Long idMatrizNegocio, Integer idEstado, Date fechaRating) {
		GestorRatingNegocio gesRatingNeg = new GestorRatingNegocioImpl();
		MatrizNegocio matrizNegocio = null;
		
		matrizNegocio = gesRatingNeg.buscarMatrizNegocioPorId(idMatrizNegocio, idEstado, fechaRating);
		
		return matrizNegocio;
	}

	public Map generarRating(String rutCliente, Long idRatingInd, Integer idBanca, String loginUsuario) {
		GestorRatingIndividual gestorRatingInd = new GestorRatingIndividualImpl();
		GestorRatingNegocio gestorNegocio      = new GestorRatingNegocioImpl();
		GestorUsuarios gestorUsuario           = new GestorUsuariosImpl();
		ServicioClientes clientes              = new ServicioClientesImpl();
		Map retorno 					       = new HashMap();
		RatingNegocio ratingNegocio            = null;
		Segmento segmentoVta                   = null;
		Integer idSegmento                     = null;
		MatrizNegocio matriz                   = null;
		Usuario usuario                        = null;
		Integer idBancaRatingIndividual        = null;
		Long idCliente                         = null;
		Date fechaEvalDate;

		Cliente cliente = clientes.obtenerClientePorRut(rutCliente);
		idCliente = new Long(cliente.getClienteId());

		RatingIndividual ratingIndividual = gestorRatingInd.buscarRatingIndividual(idCliente, idRatingInd);
		idBancaRatingIndividual = ratingIndividual.getIdBanca();
		
		// se determina si es banca PyME
		final boolean esBancaPyME = ConstantesSEFE.BANCA_PYME.equals(idBancaRatingIndividual);
		
		segmentoVta = obtenerSegmentoVentas(rutCliente,ratingIndividual);
		idSegmento  = segmentoVta != null ? segmentoVta.getIdSegmento() : null;
		
		// el tipo de vaciado utilizado en la evaluzacion de negocio
		Integer tipoEvaluacion = ratingIndividual.getIdTipoVaciadoRatingNegocio();
		
		// Se obtiene el rating de negocio asociado al rating individual en caso de que exista 
		ratingNegocio = obtenerRatingNegocioAsociado(ratingIndividual);
		
		// Si ya existe un rating de negocio asociado al rating individual, se valida si esta vigente
		if (ratingNegocio != null) {	
			if (ratingNegocio.getFechaEvaluacion() != null && ratingNegocio.getNotaNegocio() != null) {
				if (!gestorNegocio.esVigente(ratingNegocio, idBancaRatingIndividual) && ratingIndividual.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO)) {
					if (log.isInfoEnabled()) {
						log.info(MessageFormat.format("El rating de negocio [{0}] no es vigente. Fecha de evaluacion es [{1}]", new Object[] { ratingNegocio.getIdRating(),
								ratingNegocio.getFechaEvaluacion() }));
					}
		
					retorno.put(PARAM_MSG_VIGENCIA_MATRIZ, MessageManager.getMessage(ConstantesSEFE.KEY_RTG_NEG_ASOCIADO_NO_ES_VIGENTE));
					
					// Se busca rating vigente para reutilizar
					ratingNegocio = gestorNegocio.buscarRatingVigente(rutCliente, ratingIndividual.getIdBanca());
				}
			}
		} else {
			// Se busca rating vigente para reutilizar
			ratingNegocio = gestorNegocio.buscarRatingVigente(rutCliente, ratingIndividual.getIdBanca());
		}
		
		boolean existeRatingNegocio = (ratingNegocio != null);
		boolean matrizVigente       = true;
		
		// Se verifica si existe rating de negocio
		if (existeRatingNegocio) {
			Integer idSegmentoActual;
			//Si el rating individual y de negocio se encuentran en curso y la fecha del rtg de negocio es null se toma la fecha de creacion para obtener la matriz de negocio
			if (ratingIndividual.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO) && ratingNegocio.getEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO) && ratingNegocio.getFechaEvaluacion()== null){
				matriz           = obtenerMatrizNegocio(ratingNegocio.getIdMatriz(), ratingIndividual.getIdEstado(), ratingIndividual.getFechaCreacion());
			}else{
				matriz           = obtenerMatrizNegocio(ratingNegocio.getIdMatriz(), ratingIndividual.getIdEstado(), ratingNegocio.getFechaEvaluacion());
			}

			idSegmentoActual = matriz.getIdSegmento();
			usuario          = gestorUsuario.obtenerUsuario(ratingNegocio.getIdUsuario());
			
			if (ratingNegocio.getFechaEvaluacion() != null) {
				ratingNegocio.setLoginUsuario(usuario.getCodigoUsuario());
				fechaEvalDate = ratingNegocio.getFechaEvaluacion();
			}else{
				fechaEvalDate = ratingIndividual.getFechaCreacion();
			}
			
			// Si es banca PyME se compara el segmento sacado con el ultimo vaciado vigente existente y se compara con el segmento asociado
			// al rating de negocio existente. Si segmentos son distintos se lanza excepcion de negocio.
			// En caso que el segmento no haya cambiado se valida que la matriz siga vigente.
			Date fechaSistema = gestorNegocio.buscarFechaHoy();
			//if (esBancaPyME && !idSegmentoActual.equals(idSegmento)) { //Se modifica validación ya que deja nulas las notas de ratings individuales en estado vigente e historico
			if (esBancaPyME && ratingIndividual.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO) && !idSegmentoActual.equals(idSegmento)) {
					if (log.isInfoEnabled()) {
					log.info(MessageFormat.format("El Segmento de Ventas para la Matriz de Negocio [{0}] del cliente [{1}] a cambiado", new Object[] { ratingNegocio.getIdRating(),
							rutCliente }));
				}
				
				retorno.put(PARAM_MSG_VIGENCIA_MATRIZ, MessageManager.getMessage(ConstantesSEFE.KEY_RTG_NEG_ALERTA_CAMBIO_SEGMENTO));
				matrizVigente = false;
			} else if ((matriz.getFechaFin() != null || matriz.getFechaEfectiva().after(fechaSistema)) || !validarCuestionario(matriz, fechaEvalDate, rutCliente, idBancaRatingIndividual)) {
				if (ratingIndividual.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO)){
					retorno.put(PARAM_MSG_VIGENCIA_MATRIZ, MessageManager.getMessage(ConstantesSEFE.KEY_RTG_NEG_ALERTA_MATRIZ_CON_MODIFICACIONES));
					
					// se debe generar un nuevo rating de negocio con una nueva matriz, ya que la matriz actual no esta vigente
					matrizVigente = false;
				}
			}
		} else {			
			// Para un nuevo rating de negocio el usuario es el usuario registrado
			usuario = gestorUsuario.obtenerPrimerUsuario(loginUsuario);
		}
		
		String mensajeVaciadoModificado = validarVersionVaciados(ratingIndividual, cliente, idBanca);
		
		if(mensajeVaciadoModificado != null)
		{
			retorno.put(PARAM_MSG_VIGENCIA_MATRIZ, mensajeVaciadoModificado);
		}
		
		// si no existe un rating vigente o la matriz del rating vigente ya no es valida se debe generar un nuevo rating
		if ((!existeRatingNegocio || !matrizVigente) && ratingIndividual.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO)) {			
			ratingNegocio = crearRatingNegocio(idBancaRatingIndividual, idSegmento, usuario, cliente, ratingIndividual.getIdEstado());
		}

		// Se guarda el rating de negocio en el mapa que se retorna 
		retorno.put(PARAM_RATING_NEGOCIO, ratingNegocio);
		retorno.put(ConstantesRating.FORMULARIO_ESTADO, obtenerEstadoFormulario(ratingIndividual.getIdEstado()));
		
		
		// ya existe el rating de negocio??
		if (ratingIndividual.getIdRatingNegocio() != null && ratingIndividual.getIdRatingNegocio().longValue() == ratingNegocio.getIdRating().intValue()) {
			tipoEvaluacion = ratingIndividual.getIdTipoVaciadoRatingNegocio();
			
			try {
				gestorNegocio.validarEvaluacionRatingNegocio(idCliente, ratingIndividual.getIdRatingNegocio(), ratingIndividual.getIdRating());
			} catch (BusinessOperationException bex) {
				retorno.put(PARAM_MSG_VIGENCIA_MATRIZ, bex.getMessage());
			}
		} else {
			tipoEvaluacion = gestorNegocio.buscarTipoEvaluacionRating(rutCliente, idBanca, idRatingInd);
			
			// se guarda el tipo de vaciado utilizado
			gestorRatingInd.guardarTipoVaciadoRatingNegocio(idRatingInd, tipoEvaluacion);
		}
		
		
		// se asocia el rating de negocio con el rating individual(final)
		gestorRatingInd.actualizarRatingNegocio(idRatingInd, ratingNegocio);
		
		if (ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO==ratingIndividual.getIdEstado() 
				&& ratingNegocio != null 
				&& ratingNegocio.getNotaNegocio() != null) {
			// se recalcula el rating individual/final
			RatingIndividual ratingIndividualCalculado = gestorRatingInd.calcularRating(new Long(cliente.getClienteId()), idRatingInd);
		}

		//String tipoEvaluacion = gestorNegocio.buscarTipoEvaluacionRating(rutCliente, idBanca, idRatingInd);
		CatalogoGeneral catalogo = new CatalogoGeneralImpl();
		retorno.put(ConstantesRating.TIPO_EVALUACION_RATING_NEGOCIO, catalogo.buscarClasificacionPorId(tipoEvaluacion).getNombre());

		return retorno;
	}
	
	private String validarVersionVaciados(RatingIndividual ratingIndividual, Cliente cliente, Integer idBanca) {
		GestorRatingFinanciero gestRtgFinanciero = new GestorRatingFinancieroImpl();

		// Esto no deberia pasar, ya que se esta validando un rating individual modelo!!! por lo que este componente debe existir y por ende debe ser
		// validado en otra instancia...
		if (ratingIndividual.getIdRatingFinanciero() != null) {
			RatingFinanciero rtgFinanciero = gestRtgFinanciero.obtenerRating(ratingIndividual.getIdRatingFinanciero());

			Long idVac0 = rtgFinanciero.getIdVaciado0();
			Long idVac1 = rtgFinanciero.getIdVaciado1();
			Long idVac2 = rtgFinanciero.getIdVaciado2();

			boolean vac0Mod = false;
			boolean vac1Mod = false;
			boolean vac2Mod = false;

			if (idVac0 != null) {
				vac0Mod = vaciadoHaSidoModificado(idVac0);
			}

			if (idVac1 != null) {
				vac1Mod = vaciadoHaSidoModificado(idVac1);
			}

			if (idVac2 != null) {
				vac2Mod = vaciadoHaSidoModificado(idVac2);
			}

			if (vac0Mod || vac1Mod || vac2Mod) {
				return (ConstantesSEFE.MSG_ALERTA_VALIDACION_VERSION_VACIADOS);
			}
		}

		return null;
	}
	
	private boolean vaciadoHaSidoModificado(Long idVac) {
		GestorVaciados gestVac = new GestorVaciadosImpl();

		Vaciado vac = gestVac.buscarVaciado(idVac);
		 // si es nulo entonces puede ser un modelo que ocupa menos de 3 vaciados para generar el rating.
		if (vac == null) {
			return false;
		}
			
		// si la version no es 1, quiere decir que el vaciado fue modificado y no se han actualizado los componentes que los usan
		return (!ConstantesSEFE.VERSION_VACIADO_VALIDA.equals(vac.getVersion()));
	}
	
	private boolean validarCuestionario(MatrizNegocio matriz, Date fechaRating, String rut, Integer idBanca)
	{
		RatingNegocioDAO ratingNegocioDao = new RatingNegocioDAOImpl();
		boolean validacionCuestionario = true;
	
		// Se valida que el cuestionario no haya sufrido cambios desde la generación del nuevo rating de negocio
		for (int t = 0; t < matriz.getTemasNegocio().size(); t++) {
			TemaNegocio tema = (TemaNegocio)matriz.getTemasNegocio().get(t);
			if(tema.getFechaEfectiva().after(fechaRating) && tema.getVigenteModificado() != null)
			{
				validacionCuestionario = false;
			}
			for (int c = 0; c < tema.getPreguntas().size(); c++) {
				PreguntaNegocio pregunta = (PreguntaNegocio)tema.getPreguntas().get(c);
				if(pregunta.getFechaEfectiva().after(fechaRating) && pregunta.getVigenteModificado() != null)
				{
					validacionCuestionario = false;
				}
				
				ArrayList alternativasVigHis = (ArrayList)ratingNegocioDao.buscarAlternativaNegocioVigHis(pregunta.getIdMatriz(), pregunta.getIdTema(), pregunta.getIdPregunta());
				
				for (int a = 0; a < alternativasVigHis.size(); a++) {
					AlternativaNegocio alternativa = (AlternativaNegocio)alternativasVigHis.get(a);
					if(alternativa.getEstado().intValue() == 4302)
					{
						if(alternativa.getFechaEfectiva().after(fechaRating))
						{
							return false;
						}
					}
					else
					{
						if(alternativa.getFechaFin().after(fechaRating))
						{
							return false;
						}
					}
				}
				
				// valida modificaciones en alternativas vigentes
				for (int a = 0; a < pregunta.getAlternativas().size(); a++) {
//					cantidadAlternativasActuales ++;
					AlternativaNegocio alternativa = (AlternativaNegocio)pregunta.getAlternativas().get(a);
					if(alternativa.getFechaEfectiva().after(fechaRating) && alternativa.getVigenteModificado() != null)
					{
						validacionCuestionario = false;
					}
				}
			}
		}

		//ArrayList alternativas = (ArrayList)ratingNegocioDao.
//		
//		if(cantidadAlternativasActuales != cantidadAlternativasAnteriores)
//		{
//			validacionCuestionario = false;
//		}

		return validacionCuestionario;
	}
	
	private RatingNegocio obtenerRatingNegocioAsociado(RatingIndividual ratingIndividual) {
		GestorRatingNegocio gestorNegocio = new GestorRatingNegocioImpl();
		RatingNegocio ratingNegocio = null;
		
		// Si el rating individual ya tiene un rating de negocio asociado
		if (ratingIndividual.getIdRatingNegocio() != null) {
			ratingNegocio = gestorNegocio.buscarRatingNegocioPorId(ratingIndividual.getIdRatingNegocio());
		}
		
		return ratingNegocio;
	}
	
	/*
	 * Crea una nueva instancia de cliente y la guarda.
	 * En caso de no encontrar una matriz de negocio para asociar al Rating de Negocio se lanza una BusinessOperationException.
	 * Retorna la instancia recien creada.
	 */
	private RatingNegocio crearRatingNegocio(Integer idBanca, Integer idSegmento, Usuario usuario, Cliente cliente, Integer idEstado) {
		GestorRatingNegocio gestorNegocio = new GestorRatingNegocioImpl();
		RatingNegocio ratingNegocio = null;
		MatrizNegocio matriz;
		
		matriz = gestorNegocio.buscarMatrizNegocio(idBanca, idSegmento, idEstado, new Date());
		
		// Si bien, no puede NO EXISTIR una matriz vigente se considera este caso y se lanza excepcion de negocio
		if (matriz == null) {
			log.error(MessageFormat.format("No se ha encontrado una matriz de negocio vigente para el calculo del rating de negocio para la banca [{0}] y segmento [{1}]",
					new Object[] { idBanca, idSegmento }));
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.KEY_RTG_NEG_ERROR_MATRIZ_VIGENTE_NO_EXISTE));
		}
		
		ratingNegocio = new RatingNegocio();
		ratingNegocio.setIdUsuario(usuario.getUsuarioId());
		ratingNegocio.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		ratingNegocio.setIdMatriz(matriz.getIdMatriz());
		ratingNegocio.setIdCliente(new Long(cliente.getClienteId()));
		ratingNegocio.setLoginUsuario(usuario.getCodigoUsuario());
		
		// se guarda el nuevo rating
		return gestorNegocio.guardarRating(ratingNegocio);
	}
	
	/*
	 * Retorna el segmento de ventas para el vaciado vigente utilizado para el calculo del rating de negocio. 
	 */
	private Segmento obtenerSegmentoVentas(String rutCliente, RatingIndividual ri) {
		GestorRating gestorRating = new GestorRatingImpl();
		Vaciado vaciado   	 = null;
		Segmento segmentoVta = null;
		Integer idBanca = ri.getIdBanca();
		List sgmList = gestorRating.obtenerSegmentosPorTipoYBanca(idBanca, ConstantesSEFE.SEGMENTO_VENTAS);
		if (sgmList != null && !sgmList.isEmpty()){
			if (ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
				// PYME, el cuestionario y calculos asociados a la matriz de configuracion
				// vigente mas actual con un maximo de 18 meses de antiguedad respecto a la fecha actual.
				// Esta seleccion debe privilegiar tipo Ajustado sobre Original y fuente Balancesobre DAI y
				// considerar los planes de cuenta CHGAAP, IFRSCN e IFRSCF.
				//vaciado = gestorRating.buscarVaciadoParaRating(rutCliente, ConstantesSEFE.BANCA_PYME);
				vaciado = gestorRating.buscarVaciadoParaRatingPYME(rutCliente, ConstantesSEFE.BANCA_PYME, ri);
				//vaciado = gestorRating.buscarVaciadoParaRatingPYME(rutCliente, ConstantesSEFE.BANCA_PYME);
				
				if (vaciado == null) {
					// No existe vaciado anual vigente.
					// Sistema debe enviar mensaje de error (Mensaje 1) al usuario,
					// finalizando acciones sobre la pestana Rating de Negocio
					throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.KEY_RTG_NEG_ERROR_VACIADO_NO_EXISTE));
				}
			} else if (!ConstantesSEFE.BANCA_AGRICOLAS.equals(idBanca)) {
				vaciado = RatingUtil.buscarVaciado(ri);
			}
			// nivel de ventas se obtiene del vaciado
			Double montoVentas = obtenerMontoVentas(vaciado);
			if (ConstantesSEFE.BANCA_AGRICOLAS.equals(idBanca)) {
				segmentoVta = gestorRating.obtenerSegmentoVentasRatingComportamiento(ri, ConstantesSEFE.SEGMENTO_VENTAS, idBanca); //se utiliza el mismo del rating de comportamiento pero con el tipo de segmento de negocio, ya que realiza la misma consulta
			} else {
				segmentoVta = gestorRating.buscarSegmento(ConstantesSEFE.SEGMENTO_VENTAS, montoVentas, idBanca);
			}
			if (segmentoVta == null) {
				if (log.isInfoEnabled()) {
					log.info("No se ha encontrado segmento de ventas para el monto '" + montoVentas + "' para el calculo del rating de negocio");
				}
				throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.KEY_MENSAJE_NO_EXISTE_SEGMENTO_VENTAS));
			}
		}
		return segmentoVta;
	}
	
	private void guardarRating(List respuestas) {
		GestorRatingNegocio gestorRtgNegocio = new GestorRatingNegocioImpl();
		
		Iterator itRespuestas = respuestas.iterator();
		for (int i = 0; itRespuestas.hasNext(); i++) {
			RespuestaNegocio respuesta = (RespuestaNegocio) itRespuestas.next();
			
			// Solo la primera vez, se eliminan todas las que existen actualmente asociadas al rating de negocio del cliente
			if (i == 0) {
				gestorRtgNegocio.borrarRespuestas(respuesta.getIdCliente(), respuesta.getIdRatingNegocio());
			}
			
			gestorRtgNegocio.guardarRespuesta(respuesta);
		}	
	}
	
	public RatingNegocio guardarRating(String loginUsuario, Long idRatingNegocio, Long idRatingIndividual, List respuestas) {
		GestorRatingNegocio gestorRtgNegocio       = new GestorRatingNegocioImpl();
		GestorRatingIndividual gestorRtgIndividual = new GestorRatingIndividualImpl();
		GestorUsuarios gestorUsuario               = new GestorUsuariosImpl();
		
		// Se obtiene el usuario asociado al login de usuario
		Usuario usuario = gestorUsuario.obtenerPrimerUsuario(loginUsuario);
		
		// Se busca el rating de negocio(cabecera) que se va a modificar y se actualiza la infomacion
		RatingNegocio rtgNeg = gestorRtgNegocio.buscarRatingNegocioPorId(idRatingNegocio);
		rtgNeg.setIdUsuario(usuario.getUsuarioId());
		//rtgNeg.setFechaEvaluacion(new Date());
		rtgNeg.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		rtgNeg.setNotaNegocio(null);
		rtgNeg.setNotaNegocioSinTope(null);
		
		List respuestasConNota = actualizarNotaRespuesta(respuestas);
		
		// Se guarda la modificacion de la cabecera del rating de negocio
		rtgNeg = gestorRtgNegocio.actualizarRating(rtgNeg);
		
		// Se actualizan las respuestas del cuestionario
		guardarRating(respuestasConNota);
		
		// Se actualiza la informacion en el rating individual
		gestorRtgIndividual.actualizarRatingNegocio(idRatingIndividual, rtgNeg);
		
		rtgNeg.setLoginUsuario(loginUsuario);
		
		RatingIndividual rtgIndividual = gestorRtgIndividual.buscarRatingIndividual(rtgNeg.getIdCliente(), idRatingIndividual);
		
		//Requerimiento Fortaleza y Debilidades

//		GestorFortalezasDebilidades gestFortDeb = new GestorFortalezasDebilidadesImpl();
//		gestFortDeb.borrarFortalezasYDebilidades(rtgIndividual.getIdCliente(), rtgIndividual.getIdRating(), ConstantesSEFE.AMBITO_NEGOCIO);
//		
		ServicioClientes srvCtes = new ServicioClientesImpl();
		Cliente cte = srvCtes.obtenerParteInvolucradaPorId(rtgIndividual.getIdCliente());
		
		String rutCte = cte.getRut();
		Integer tipoEvaluacion = gestorRtgNegocio.buscarTipoEvaluacionRating(rutCte, rtgIndividual.getIdBanca(), rtgIndividual.getIdRating());
		gestorRtgIndividual.guardarTipoVaciadoRatingNegocio(idRatingIndividual, tipoEvaluacion);
		
		return rtgNeg;
	}
	
	private List actualizarNotaRespuesta(List respuestas) {
		List lstRespuestas = new ArrayList();
		GestorRatingNegocio gestorRtgNegocio = new GestorRatingNegocioImpl();
		
		Iterator itRespuestas = respuestas.iterator();
		while (itRespuestas.hasNext()) {
			RespuestaNegocio respuesta = (RespuestaNegocio) itRespuestas.next();
			
			AlternativaNegocio alternativa = gestorRtgNegocio.buscarAlternativa(respuesta.getIdRespuesta(), respuesta.getIdMatriz(), respuesta.getIdTema(), respuesta.getIdPregunta());
			
			respuesta.setNota(alternativa.getNotaAltenativa());
			
			lstRespuestas.add(respuesta);
		}
		
		return lstRespuestas;
	}

	public RatingNegocio buscarRating(Long idRatingNegocio) {
		GestorRatingNegocio gestor = new GestorRatingNegocioImpl();
		
		RatingNegocio rating = gestor.buscarRatingNegocioPorId(idRatingNegocio);
		
		// Si se encontro rating, se agrega como parte de la informacion el LOGIN del usuario.
		if (rating != null) {
			GestorUsuarios gestorUsr = new GestorUsuariosImpl();
			
			Usuario usr = gestorUsr.obtenerUsuario(rating.getIdUsuario());
			
			rating.setLoginUsuario(usr.getCodigoUsuario());
		}
		
		return rating;
	}

	public List buscarRespuestas(Long idRatingNegocio) {
		GestorRatingNegocio gestor = new GestorRatingNegocioImpl();
		
		return gestor.buscarRespuestasPorRating(idRatingNegocio);
	}
	
	public Map calcular(Long idRatingNegocio, Long idRatingIndividual, String usuario, List respuestas) {
		GestorRatingNegocio gestorNegocio = new GestorRatingNegocioImpl();
		Long idCte = (Long) SEFEContext.getValue(ConstantesSEFE.SEFE_CTX_ID_CLIENTE);
		
		//gestorNegocio.validarEvaluacionRatingNegocio(idCte, idRatingNegocio, idRatingIndividual);
		
		//guardarRating(usuario, idRatingNegocio, idRatingIndividual, respuestas);
		//DGJO INICIO;
		List respuestasConNota = actualizarNotaRespuesta(respuestas);
		guardarRating(respuestasConNota);
		//DGJO FIN;
		
		return calcular(idRatingNegocio, idRatingIndividual, usuario);
	}

	private Map calcular(Long idRatingNegocio, Long idRatingIndividual, String loginUsuario) {
		GestorRatingNegocio gestor = new GestorRatingNegocioImpl();
		Map retorno                = new HashMap();

		RatingNegocio rating = gestor.buscarRatingNegocioPorId(idRatingNegocio);

		GestorServicioClientes cliente = new GestorServicioClientesImpl();
		Cliente clienteVO              = cliente.obtenerParteInvolucradaPorId(rating.getIdCliente());

		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();	
		RatingIndividual ratingIndividual = gestorRating.buscarRatingIndividualPorNegocio(rating.getIdCliente(), idRatingNegocio);
		
		// se valida que la matriz estaï¿½ vigente
		MatrizNegocio matriz = gestor.buscarMatrizNegocioPorId(rating.getIdMatriz(), ratingIndividual.getIdEstado(), rating.getFechaEvaluacion());

		// Si la matriz no es vigenete se genera un nuevo rating de negocio
		if (!matriz.esVigente()) {
			// Se genera un nuevo rating de negocio ya que la matriz no es vigente
			return generarRating(clienteVO.getRut(), idRatingIndividual, matriz.getIdBanca(), loginUsuario);
		} else {
			// Se calcula el rating de negocio
			rating = gestor.calcularRating(idRatingNegocio, ratingIndividual.getIdEstado(), rating.getFechaEvaluacion());

			// si el rating estaï¿½ en curso, se actualiza
			rating.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);
			//rating.setFechaEvaluacion(new Date());

			GestorUsuarios gestorUsr = new GestorUsuariosImpl();
			Usuario usuario          = gestorUsr.obtenerPrimerUsuario(loginUsuario);
			
			rating.setIdUsuario(usuario.getUsuarioId());
			
			rating = gestor.actualizarRating(rating);

			GestorRatingIndividual gestorRatingInd = new GestorRatingIndividualImpl();

			// se actualiza la nota de negocio en el rating individual
			gestorRatingInd.actualizarRatingNegocio(idRatingIndividual, rating);

			// se recalcula el rating individual
			gestorRatingInd.calcularRating(new Long(clienteVO.getClienteId()), idRatingIndividual);

			retorno.put(PARAM_RATING_NEGOCIO, rating);
			
			RatingIndividual rtgIndividual = gestorRatingInd.buscarRatingIndividual(new Long(clienteVO.getClienteId()), idRatingIndividual);
			
			//String tipoEvaluacion = gestorNegocio.buscarTipoEvaluacionRating(rutCliente, idBanca, idRatingInd);
			CatalogoGeneral catalogo = new CatalogoGeneralImpl();
			retorno.put(ConstantesRating.TIPO_EVALUACION_RATING_NEGOCIO, catalogo.buscarClasificacionPorId(rtgIndividual.getIdTipoVaciadoRatingNegocio()).getNombre());

			
//			GestorFortalezasDebilidades gestFortDeb = new GestorFortalezasDebilidadesImpl();
//			gestFortDeb.borrarFortalezasYDebilidades(rtgIndividual.getIdCliente(), rtgIndividual.getIdRating(), ConstantesSEFE.AMBITO_NEGOCIO);
//			gestFortDeb.agregarFortalezasDebilidadesRtgNegocio(rtgIndividual);

			return retorno;
		}
	}

	private Double obtenerMontoVentas(Vaciado vaciado) {
		String claveVentas 	= ConstantesSEFE.CODIGO_CUENTA_VENTAS ;
		String codCuentaVentas = ConfigDBManager.getValueAsString(claveVentas);

		// Se recupera la cuenta desde el gestor de plan de cuentas
		GestorPlanCuentas gestorCuentas = new GestorPlanCuentasImpl();
		Cuenta cuenta = gestorCuentas.consultarValorCuentaVaciado(vaciado.getIdVaciado(), codCuentaVentas);
		Double ventas = cuenta.getMonto();
		Double ajuste = cuenta.getAjuste();

		// se obtiene el valor de las ventas del vaciado
		if (ajuste != null) {
			ventas = new Double(ventas.doubleValue() + ajuste.doubleValue());
		}

		// Se convierte la moneda y unidad del vaciado a M UF
		if (ventas != null) {
			ConversorMoneda convMoneda = new ConversorMonedaImpl();
			
			ventas = convMoneda.convertirMonedaSegunReglas(ventas, vaciado.getIdMoneda(), vaciado.getUnidMedida(),
					ConstantesSEFE.ID_CLASIF_MONEDA_UF, ConstantesSEFE.ID_CLASIF_MILES, 
					vaciado.getPeriodo());
				
				/*convMoneda.convertirMoneda(ventas, vaciado.getIdMoneda(), vaciado.getUnidMedida(), 
					ConstantesSEFE.ID_CLASIF_MONEDA_UF, ConstantesSEFE.ID_CLASIF_MILES, 
					convMoneda.buscarDiaHabilSiguiente(vaciado.getPeriodo()));*/
		}

		return ventas;
	}


	
	private String obtenerEstadoFormulario(Integer estado){
		if(ConstantesRating.CLASIF_ID_RATING_EN_CURSO.equals(estado)){
			return ConstantesRating.FORMULARIO_ENABLED;
		}		
		return ConstantesRating.FORMULARIO_DISABLED;
	}
}
