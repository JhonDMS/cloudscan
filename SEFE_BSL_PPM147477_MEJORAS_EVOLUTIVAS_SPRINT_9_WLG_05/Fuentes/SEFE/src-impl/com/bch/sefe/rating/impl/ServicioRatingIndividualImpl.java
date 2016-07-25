package com.bch.sefe.rating.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.ConsultaServicios;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.ServicioConsultaDeuda;
import com.bch.sefe.comun.srv.impl.ConsultaServiciosImplCache;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.srv.impl.ServicioConsultaDeudaODS;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.DeudaCliente;
import com.bch.sefe.comun.vo.ParteInvolucrada;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.exception.SEFEException;
import com.bch.sefe.rating.ServicioRatingIndividual;
import com.bch.sefe.rating.dao.RatingDAO;
import com.bch.sefe.rating.dao.impl.RatingDAOImpl;
import com.bch.sefe.rating.srv.AlgoritmoRatingFinanciero;
import com.bch.sefe.rating.srv.GestorComponentesRating;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingGrupal;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.GestorRatingNegocio;
import com.bch.sefe.rating.srv.impl.AlgoritmoRatingFinancieroGenerico;
import com.bch.sefe.rating.srv.impl.GestorComponentesRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingFinancieroImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingGrupalImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingNegocioImpl;
import com.bch.sefe.rating.srv.impl.RatingUtil;
import com.bch.sefe.rating.vo.EvaluacionFinanciera;
import com.bch.sefe.rating.vo.IntegranteRatingGrupo;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.RatingComportamiento;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingGarante;
import com.bch.sefe.rating.vo.RatingGrupal;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.rating.vo.RespuestaNegocio;
import com.bch.sefe.rating.vo.Segmento;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.vaciados.srv.GestorHojaIndependiente;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorHojaIndependienteImpl;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.HojaIndependiente;
import com.bch.sefe.vaciados.vo.Vaciado;

public class ServicioRatingIndividualImpl implements ServicioRatingIndividual {
	final static Logger log = Logger.getLogger(ServicioRatingIndividualImpl.class);

	final static Integer ID_LISTA_MOTIVOS = new Integer(4700);
	final static Integer ID_MOTIVO_NO_SELECCIONADO = new Integer(-1);
	final static long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;

	public RatingIndividual guardarRatingIndividual(String rutCliente, Long idRating, Integer idBanca, String logOper, String comm) {

		GestorRatingIndividual gestor = new GestorRatingIndividualImpl();

		// Obtenemos desde el servicio de clientes la id del cliente.
		ServicioClientes srvClientes = new ServicioClientesImpl();
		Cliente cliente = srvClientes.obtenerClientePorRut(rutCliente);
		Long idCliente = new Long(cliente.getClienteId());

		// Obtenemos desde el Gestor de usuarios la id del usuario.
		GestorUsuarios gestUsuario = new GestorUsuariosImpl();
		Usuario usuario = gestUsuario.obtenerPrimerUsuario(logOper);
		Long idUsuario = usuario.getUsuarioId();

		// Llamamos al metodo encargado de actualizar el comentario.
		RatingIndividual rating = gestor.actualizarComentario(idCliente, idRating, comm, idUsuario);

		return rating;
	}

	public RatingIndividual confirmarRatingIndividualModificado(String rutCliente, Long idRating, Integer idBanca, String logOper, Double nota, String comm, Integer idMotivo, String comMotivo) {
		// se recupera el id parte involucrada del cliente
		ServicioClientes srvCtes = new ServicioClientesImpl();
		Cliente cte = srvCtes.obtenerClientePorRut(rutCliente);
		Long idCliente = new Long(cte.getClienteId());

		// se recupera el id parte involucrada del usuario
		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerPrimerUsuario(logOper);
		Long idUsr = usr.getUsuarioId();
		// se aplican reglas de negocio sobre la data enviada
		if (nota == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.MSG8_MODELO_RATING_INDIVIDUAL));
		}

		Double investmentGrade = ConfigDBManager.getValueAsDouble(ConstantesSEFE.NOTA_MINIMA_INVESTMENT_GRADE);

		// es mandatorio agregar un comentario que detalle el motivo del cambio
		if (comMotivo == null || ConstantesSEFE.ESPACIO_VACIO.equals(comMotivo.trim())) {
			//throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.MSG8_MODELO_RATING_INDIVIDUAL));
		}

		// se invoca el servicio de negocio
		GestorRatingIndividual gestorInd = new GestorRatingIndividualImpl();
		RatingIndividual rating = gestorInd.buscarRatingIndividual(idCliente, idRating);
		
		Double notaGte = new Double("0.0");
		if (rating.getRatingGarante() != null) {
			notaGte = rating.getRatingGarante();
		}
		
		// es mandatorio seleccionar un motivo para el cambio
		if (idMotivo.equals(ConfigDBManager.getValueAsInteger(ConstantesSEFE.ID_MOTIVO_CAMBIO_RATING_INDIVIDUAL)) && (notaGte.compareTo(investmentGrade) < 0)) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.MSG10_MODELO_RATING_INDIVIDUAL));
		}
		
		Double deudaBanco = obtenerTotalDeudaDirecta(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_BANCO);
		Double deudaSbif = obtenerTotalDeudaDirecta(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF);
		rating = gestorInd.confirmarRatingIndividualModificado(idCliente, idRating, idBanca, idUsr, nota, comm, idMotivo, comMotivo, deudaBanco, deudaSbif);
		//modifica el id de rating por el nuevo id de rating generado
		modificarIdRatingHojaIndependiente(idBanca,idRating, idCliente, rating.getIdRating() );
		// se actualiza la nota de rating en siebel
		 ConsultaServicios servicioOSB = new ConsultaServiciosImplCache();
		 servicioOSB.actualizarRating(rutCliente, rating.getRatingFinal().toString(), null);

		return rating;
	}
	
	private Double obtenerTotalDeudaDirecta(String rutCliente, Integer idTipoDeuda) {
		ServicioConsultaDeuda servicioDeudaODS = new ServicioConsultaDeudaODS();

		DeudaCliente deuda = servicioDeudaODS.buscarDeudaUltimoPeriodoCache(rutCliente, idTipoDeuda);

		return (deuda != null ? deuda.getTotalDeudaDirecta() : null);
	}

	public RatingIndividual confirmarRatingIndividualModelo(String rutCliente, Long idRating, Integer idBanca, String logOper, Double nota, String comm, Integer idMotivo, String comMotivo) {
		ServicioClientes srvCtes = new ServicioClientesImpl();
		Cliente cte = srvCtes.obtenerClientePorRut(rutCliente);
		Long idCliente = new Long(cte.getClienteId());

		// se recupera el id parte involucrada del usuario
		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerPrimerUsuario(logOper);
		Long idUsr = usr.getUsuarioId();
		//validarVigenciaComponentesRating(idRating, idCliente,idBanca);
		// se aplican reglas de negocio sobre la data enviada
		if (nota == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.MSG8_MODELO_RATING_INDIVIDUAL));
		}

		// es mandatorio seleccionar un motivo para el cambio
		if (idMotivo.equals(ID_MOTIVO_NO_SELECCIONADO)) {
			idMotivo=null;
		}

		// es mandatorio agregar un comentario que detalle el motivo del cambio
		if (comMotivo == null || ConstantesSEFE.ESPACIO_VACIO.equals(comMotivo.trim())) {
			
		}
		
		
		//List alertas = this.validarVigenciaComponentesRating(idRating, rutCliente, idBanca);
		GestorAlertasRtgIndividualImpl gestAlertas = new GestorAlertasRtgIndividualImpl();
		List alertas = gestAlertas.obtenerAlertasRtgIndividualModelo(idRating, rutCliente, idBanca);
		if (alertas != null && !alertas.isEmpty()) {
			// si existen componentes del rating individual que no cumplen con la vigencia se retorna una nueva instancia de RatingIndividual
			// que solo contiene las alertas
			RatingIndividual rating = new RatingIndividual();
			rating.setAlertasComponentes(alertas);			
			return rating;
		}

		// se invoca el servicio de negocio
		GestorRatingIndividual gestorInd = new GestorRatingIndividualImpl();
		Double deudaBanco = obtenerTotalDeudaDirecta(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_BANCO);
		Double deudaSbif = obtenerTotalDeudaDirecta(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF);
		RatingIndividual rating = gestorInd.confirmarRatingIndividualModelo(idCliente, idRating, idBanca, idUsr, nota, comm, idMotivo, comMotivo, deudaBanco, deudaSbif);

		// se actualiza la nota de rating en siebel
		 ConsultaServicios servicioOSB = new ConsultaServiciosImplCache();
		 servicioOSB.actualizarRating(rutCliente, rating.getRatingFinal().toString(), null);
		 
		return rating;
	}

	/**
	 * Busca el rating calculado de acuerdo al modelo.
	 * 
	 */
	public Collection consultaRatingSugerido(String rutCliente, Long idRating, Integer idTipoRating, String usuario) {
		Vector respuesta = new Vector();

		// Se obtiene el identificador de parte involucrada del cliente
		if (log.isDebugEnabled()) {
			log.debug("Buscando parte involucrada cliente rut " + rutCliente);
		}

		
		ServicioClientes srvCtes = new ServicioClientesImpl();
		Cliente cte = srvCtes.obtenerClientePorRut(rutCliente);
		Long idCliente = new Long(cte.getClienteId());

		if (log.isDebugEnabled()) {
			log.debug("Buscando rating individual para cliente " + rutCliente);
		}
		GestorRatingIndividual gestorInd = new GestorRatingIndividualImpl();
		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRating);
		RatingIndividual rtgInd = gestorInd.consultaRatingSugerido(idCliente, idRating);
		
		// actualiza la informacion del rating de comportamiento cuando el rating individual esta en curso.
		if (rtgInd.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO)) {

			GestorComponentesRating gestorComponentes = new GestorComponentesRatingImpl();
			RatingComportamiento ratingComp = null;
			try {
				ratingComp = gestorComponentes.buscarRatingComportamientoVigente(idCliente, idRating, usuario);
			} catch (Exception ex) {
				if (log.isInfoEnabled()) {
					// arroja una excepcion de negocio al no poder encontrar nota desde el servicio.
					log.debug("No fue posible obtener comportamiento");
				}
			}

			if (ratingComp != null && ratingComp.esVigente().booleanValue()) {
				if (log.isDebugEnabled()) {
					log.debug("Asociando rating de comportamiento vigente #" + ratingComp.getIdRating() + " a rating individual");
				}
				rtgInd = gestorInd.actualizarRatingComportamiento(idRating, ratingComp);
			} else {
				gestorInd.borrarRtgComportamientoParaRtgIndividualCurso(idCliente, idRating);
				rtgInd = gestorInd.consultaRatingSugerido(idCliente, idRating);
			}

			RatingIndividual ratingIndividualCalculado = null;
			if (rtgInd != null) {
				ratingIndividualCalculado = gestorInd.calcularRating(idCliente, rtgInd.getIdRating());
				if (ratingIndividualCalculado != null) {
					rtgInd = ratingIndividualCalculado;
				}
			}
		}
		
		// se agrega el rating individual a la respuesta
		respuesta.add(0, rtgInd);

		if (log.isDebugEnabled()) {
			log.debug("Buscando rating financiero para cliente " + rutCliente);
		}
		GestorComponentesRating gestorCmp = new GestorComponentesRatingImpl();
		RatingFinanciero rtgFinan = null;
		if (rtgInd.getIdRatingFinanciero() != null) {
			//TODO agregar parametro al metodo para que reciba el id vaciado correspondiente al primer periodo, con el objetivo de eliminar la llamada a GestorComponentesRatingImpl.consultarRatingFinancieroPorId
			rtgFinan = gestorCmp.consultarRatingFinancieroPorId(rtgInd.getIdRatingFinanciero());
			if (rtgInd.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO)){
				rtgFinan = (new GestorRatingFinancieroImpl()).calcularRating(rutCliente, rtgInd.getIdBanca(), rtgFinan.getIdVaciado0(), usuario);
			}else{
				List lst = new ArrayList();
				GestorVaciados gstVac = new GestorVaciadosImpl();
				lst.add(gstVac.buscarVaciado(rtgFinan.getIdVaciado0()));
				lst.add(gstVac.buscarVaciado(rtgFinan.getIdVaciado1()));
				lst.add(gstVac.buscarVaciado(rtgFinan.getIdVaciado2()));
				Vaciado vaciadoCabecera = (Vaciado) lst.get(0);
				int mesesVacCierre = vaciadoCabecera.getMesesPer().intValue();
				
				// se determina el tipo de combinacion
				String combinacion = RatingUtil.getCombinacionPeriodos(lst);

				MatrizFinanciera matriz =new GestorRatingFinancieroImpl().obtenerMatrizFinanciera(rtgFinan.getIdMatriz(), combinacion, mesesVacCierre);
				AlgoritmoRatingFinanciero algoritmo = new AlgoritmoRatingFinancieroGenerico();
				Vaciado vacAnterior = null;
				Vaciado vacAnteAnterior = null;
				if (rtgFinan.getIdVaciado1()!=null){
					vacAnterior = gstVac.buscarVaciado(rtgFinan.getIdVaciado1());
				}
				if (rtgFinan.getIdVaciado2()!=null){
					vacAnteAnterior = gstVac.buscarVaciado(rtgFinan.getIdVaciado2());
				}
				rtgFinan = algoritmo.calcularRating(vaciadoCabecera, vacAnterior, vacAnteAnterior, rtgInd.getIdBanca(), matriz, rtgFinan);
			}
		}
		// se agrega el rating financiero a la respuesta
		respuesta.add(1, rtgFinan);

		Boolean esComercialPyme = Boolean.FALSE;
		// se agrega el rol a la respuesta
		respuesta.add(2, esComercialPyme);

		// se extrae la lista de motivos de cambio desde las clasificaciones
		Collection clasif = gestorCmp.buscarListaMotivosCambioRating(rtgInd.getIdEstado());
		if (clasif == null) {
			clasif = new ArrayList();
		}

		// se agrega la lista de motivos a la respuesta
		respuesta.add(3, clasif);
		
		// se busca la lista de clasificaciones
		GestorRating gestorRating = new GestorRatingImpl();
		Integer tpoSegmento = ConfigDBManager.getValueAsInteger(ConstantesSEFE.RTG_INDIVIDUAL_TPO_SEGMENTO,  rtgInd.getIdBanca());
		List segmentos;
		if (!rtgInd.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO)){
			segmentos = gestorRating.obtenerSegmentosPorTipoBancaYFecha(rtgInd.getIdBanca(),tpoSegmento, rtgInd.getIdRating());
		}else{
			segmentos = gestorRating.obtenerSegmentosPorTipoYBanca(rtgInd.getIdBanca(),tpoSegmento);
		}
		if (segmentos != null && !segmentos.isEmpty()) {
			// se agrega la lista de segmentos
			respuesta.add(segmentos);
		}
		Vaciado vac = null;
		if (rtgFinan != null) {
			GestorVaciados gestorVaciados = new GestorVaciadosImpl();
			vac = gestorVaciados.buscarVaciado(rtgFinan.getIdVaciado0());
			
			/*
			 * marias 20121108 - se valida que sea cierre anual
			 * antes de utilizar el vaciado
			 */
			if (vac != null && vac.getMesesPer().intValue() != 12 && !rtgInd.getIdBanca().equals(ConstantesSEFE.BANCA_PYME)) {
				vac = gestorVaciados.buscarVaciado(rtgFinan.getIdVaciado1());
			}
				
			
			
		}
		Segmento seg = gestorRating.obtenerSegmentoRatingIndividual(vac, rtgInd.getIdBanca(),tpoSegmento, rtgInd);
		if (seg != null) {
			// y se agrega el segmento correspondiente
			respuesta.add(seg.getIdSegmento());
		}

		return respuesta;
	}

	/**
	 * Crea una nueva instancia del rating; si ya existe un ratin en curso, lo
	 * retorna
	 */
	public RatingIndividual generarRating(String rutCliente, Integer idBanca, String logOper) {
		if (log.isDebugEnabled()) {
			log.debug("Iniciando la creacion de un nuevo rating individual...");
		}

		RatingIndividual rating = null;

		// se busca el id parte invol para rut cliente empresa
		ServicioClientes srvClientes = new ServicioClientesImpl();
		Cliente cliente = srvClientes.obtenerClientePorRut(rutCliente);

		// TODO si el cliente no existe, gestionar el error
		if (cliente == null) {
			if (log.isInfoEnabled()) {
				log.info("Cliente " + rutCliente + " no existe. No se puede crear rating individual!");
			}
			throw new SEFEException("Cliente no existe!!!");
		}

		Long idCliente = new Long(cliente.getClienteId());

		// se busca el id relacionado usuario
		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerPrimerUsuario(logOper);
		// se valida que existe rating individual en curso
		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();
		rating = gestorRating.buscarRatingEnCurso(idCliente, idBanca);

		// si existe un rating en curso...
		if (rating != null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.MSG5_MODELO_RATING_INDIVIDUAL));
		}

		// se crean el rating individual y los componentes de rating
		rating = gestorRating.crearRating(idCliente, idBanca, usr.getUsuarioId());
		
		// se crean o reutilizan los componentes de rating
		crearUtilizaComponentesRating(rating, idCliente, idBanca, logOper);

		// se calcula el rating individual sugerido, en base a los componentes
		// de rating vigentes encontrados
		// aviso.
		RatingIndividual ratingIndividualCalculado;
		if (rating != null) {
			ratingIndividualCalculado = gestorRating.calcularRating(idCliente, rating.getIdRating());
			return (ratingIndividualCalculado != null ? ratingIndividualCalculado : rating);
		}

		return rating;
	}

	/**
	 * Crea o reutiliza los componentes asociados al rating individual
	 */
	private void crearUtilizaComponentesRating(RatingIndividual rating, Long idCliente, Integer idBanca, String logOper) {
		GestorComponentesRating gestorComponentes = new GestorComponentesRatingImpl();
		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();
//		GestorFortalezasDebilidades gestFortDebl = new GestorFortalezasDebilidadesImpl();

		Long idRatingInd = rating.getIdRating();

		// busca rating financiero vigente
		RatingFinanciero ratingFinanciero = gestorComponentes.buscarRatingFinacieroVigente(idCliente, idBanca);
		
		if (ConstantesSEFE.REUTILIZAR_RATING_FINANCIERO_HABILITADO && ratingFinanciero != null) {
			if (log.isDebugEnabled()) {
				log.debug("Reutilizando rating financiero vigente #" + ratingFinanciero.getIdRating());
			}

			// Se clona el rating financiero a reutilizar
			RatingFinanciero ratingFinanClonado = clonarRatingFinanciero(ratingFinanciero);
			
			try {
				// busca el rating indiv asociado al financiero para extrar monto ventas y patrimonio
				GestorRatingIndividual gestorIndividual = new GestorRatingIndividualImpl();
				RatingIndividual ratinIndivBase = gestorIndividual.buscarRatingIndividualPorFinanciero(idCliente, idBanca, ratingFinanciero.getIdRating());
				
				ratingFinanClonado.setMontoVentaMMUF(ratinIndivBase.getMontoVenta());
				ratingFinanClonado.setMontoPatrimonioMMUF(ratinIndivBase.getMontoPatrimonio());
				
				// Si se reutiliza el componente se deben clonar las fortalezas y debilidades para ese ambito
//				gestFortDebl.clonarFortalezasYDebilidades(idCliente, ratinIndivBase.getIdRating(), ConstantesSEFE.AMBITO_FINANCIERO, idRatingInd);
			} catch (Exception ex) {
				if (log.isInfoEnabled()) {
					log.info("No se puede recuperar monto venta y patrimonio para rating financiero #" + ratingFinanciero.getIdRating());
				}
			}

			gestorRating.actualizarRatingFinanciero(idCliente, idRatingInd, ratingFinanClonado);
		}

		// busca rating de negocio vigente
		RatingNegocio ratingNegocio = null;
		try {
			ratingNegocio = gestorComponentes.buscarRatingNegocioVigente(idCliente, idBanca);
		} catch (Exception ex) {
			if (log.isInfoEnabled()) {
				log.info("No existe rating financiero que cumpla criterio para reutilizacion: " + ex.getMessage());
			}
		}

		if (ratingNegocio != null) {
			if (log.isDebugEnabled()) {
				log.debug("Asociando rating de negocio vigente #" + ratingNegocio.getIdRating() + " a rating individual");
			}

			Long idRating = ratingNegocio.getIdRating();

			// Se recupera las respuestas de negocio.
			// Clonar o crear una copia del rating de nogocio.
			GestorRatingNegocio gestorNegocio = new GestorRatingNegocioImpl();
			ratingNegocio = gestorNegocio.guardarRating(ratingNegocio);

			// Se recupera las respuestas de negocio.
			List respuestas = gestorNegocio.buscarRespuestasPorRating(idRating);

			// Se itera sobre la lista para actualizar con nuevo rating.
			Iterator it = respuestas.iterator();

			while (it.hasNext()) {
				RespuestaNegocio respuesta = (RespuestaNegocio) it.next();
				respuesta.setIdRatingNegocio(ratingNegocio.getIdRating());
				// Guardamos las respuestas de negocio por cada iteracion de la
				// lista obtenida.
				gestorNegocio.guardarRespuesta(respuesta);
			}

			// Asociar nuevo rating de negocio a rating individual.
			rating = gestorRating.actualizarRatingNegocio(idRatingInd, ratingNegocio);
			
			// Si se reutiliza el componente se deben clonar las fortalezas y debilidades para ese ambito
			RatingIndividual rtgIndDelNegocio = gestorRating.buscarRatingIndividualPorNegocio(idCliente, idRating);
//			gestFortDebl.clonarFortalezasYDebilidades(rtgIndDelNegocio.getIdCliente(), rtgIndDelNegocio.getIdRating(), ConstantesSEFE.AMBITO_NEGOCIO, idRatingInd);
		}

		// busca rating de comportamiento vigente
		RatingComportamiento ratingComp = null;
		try {
			ratingComp = gestorComponentes.buscarRatingComportamientoVigente(idCliente, idRatingInd, logOper);
		} catch (Exception ex) {
			if (log.isInfoEnabled()) {
				//arroja una excepcion de negocio al no poder encontrar nota desde el servicio.
				log.debug("No fue posible obtener comportamiento");
			}
		}
		
		if (ratingComp != null && ratingComp.esVigente().booleanValue()) {
			if (log.isDebugEnabled()) {
				log.debug("Asociando rating de comportamiento vigente #" + ratingComp.getIdRating() + " a rating individual");
			}
			rating = gestorRating.actualizarRatingComportamiento(idRatingInd, ratingComp);
			
			// Si se reutiliza el componente se deben clonar las fortalezas y debilidades para ese ambito
			RatingIndividual rtgIndDelComp = gestorRating.buscarRatingIndividualPorComportamiento(idCliente, ratingComp.getIdRating());
//			gestFortDebl.clonarFortalezasYDebilidades(rtgIndDelComp.getIdCliente(), rtgIndDelComp.getIdRating(), ConstantesSEFE.AMBITO_COMPORTAMIENTO, idRatingInd);
		}

		// busca rating proyectado vigente
		RatingFinanciero ratingProy = gestorComponentes.buscarRatingProyectadoVigente(idCliente, idBanca);

		if (ratingProy != null) {
			if (log.isDebugEnabled()) {
				log.debug("Asociando rating proyectado vigente #" + ratingProy.getIdRating() + " a rating individual");
			}
			rating = gestorRating.actualizarRatingProyectado(idCliente, ratingProy.getIdRating(), ratingProy);
			
			// Si se reutiliza el componente se deben clonar las fortalezas y debilidades para ese ambito
			RatingIndividual rtgIndDelProy = gestorRating.buscarRatingIndividualPorProyectado(ratingProy.getIdProyectado());
//			gestFortDebl.clonarFortalezasYDebilidades(rtgIndDelProy.getIdCliente(), rtgIndDelProy.getIdRating(), ConstantesSEFE.AMBITO_PROYECTADO, idRatingInd);
		}

		// busca rating garante vigente
		RatingGarante ratingGte = gestorComponentes.buscarRatingGaranteVigente(idCliente, idBanca);
		if (ratingGte != null) {
			if (log.isDebugEnabled()) {
				log.debug("Asociando rating garante vigente #" + ratingGte.getSecuenciaRating() + " a rating individual");
			}
			rating = gestorRating.actualizarRatingGarante(idRatingInd, ratingGte);
		}

	}

	/*
	 * Retorna el rating financiero clonado.
	 */
	private RatingFinanciero clonarRatingFinanciero(RatingFinanciero rtgFinancieroOriginal) {
		GestorRatingFinanciero gestorRatingFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinancieroClonado = null;

		// Se respalda la fecha de rating del original
		Date fechaRatingOriginal = new Date(rtgFinancieroOriginal.getFechaRating().getTime());

		// Se clona el nuevo rating financiero con una nueva fecha de rating
		rtgFinancieroOriginal.setFechaRating(new Date());
		rtgFinancieroClonado = gestorRatingFinanciero.grabarRating(rtgFinancieroOriginal);

		// Se buscan las evaluaciones del rating financiero original para luego
		// clonarlas
		List evaluaciones = gestorRatingFinanciero.buscarEvaluacionesFinancieras(rtgFinancieroOriginal.getIdRating());
		EvaluacionFinanciera evaluacion;
		for (int i = 0; i < evaluaciones.size(); i++) {
			evaluacion = (EvaluacionFinanciera) evaluaciones.get(i);

			// Se asocia al nuevo rating financiero
			evaluacion.setIdRating(rtgFinancieroClonado.getIdRating());

			// Se graba la evaluacion clonada
			gestorRatingFinanciero.grabarEvaluacionFinanciera(evaluacion);
		}

		// se vuelve a asignar la fecha de rating financiero
		rtgFinancieroOriginal.setFechaRating(fechaRatingOriginal);

		return rtgFinancieroClonado;
	}

	public RatingIndividual buscarRatingPorId(Long idCliente, Long idRtgInd) {
		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();
		RatingIndividual rating = gestorRating.buscarRatingIndividual(idCliente, idRtgInd);

		return rating;
	}

	/**
	 * Se procese a caducar los ratings individuales y grupales desde la fecha
	 * indicada hasta la fecha actual. no se procesaran los ratings en una fecha
	 * futura. se procese a: obtener la lista de ratings individuales vigentes
	 * vencidos y se cambian a estado historico obtener cada uno de sus grupos
	 * obtener la lista de ratings grupales vencidos juntar los ratings grupales
	 * sin repetirlos en forma ordenada y se les cambia el estado a historico.
	 * informar la cantidad de ratings individuales y grupales caducados.
	 * 
	 */
	public String caducarRatingsVencidos(Date desde, Date hasta, String logOper) {
		Date hoy = new Date();
		if (hoy.compareTo(hasta) <= 0) {
			return "ERROR: fecha actual menor que fecha futura solicitada";
		}
		//RATING GRUPALES CERRADOS MANUAL REQ. 7.4.29 VIGENCIA DE RATING
		pasarHistoricosRatingGrupales();
		GestorRatingIndividual gri = new GestorRatingIndividualImpl();
		GestorRatingGrupal grp = new GestorRatingGrupalImpl();
		RatingIndividual[] ratingsIndividuales;

		List vgrupales = new Vector();

		ratingsIndividuales = gri.obtenerRatingsIndividualesNoInformados(true, desde, hasta);
		ratingsIndividuales = filtrarRatingsNoVencidos(ratingsIndividuales, hasta);

		if (ratingsIndividuales == null || ratingsIndividuales.length == 0)
			return "ERROR: no existe rating individual que informar";

		int count_ind = 0;
		RatingGrupal rgrupal;
		RatingIndividual ri;
		ServicioClientes srvClientes = new ServicioClientesImpl();
		for (int i = 0; i < ratingsIndividuales.length; i++) {
			ri = ratingsIndividuales[i];
			if (ri != null && ri.getIdRating() != null) { 
				List rg = grp.rtgGrupalMultiVigSoloAplica(ri);
				for (int v = 0; v < rg.size(); v++){
					rgrupal = (RatingGrupal) rg.get(v);
						if (rgrupal != null && rgrupal.getIdRatingGrupal() != null) {							
							pasarHistoricoRatingsVigentes(rgrupal, false);
						}  
					}
				} 
				//PYME
				RatingGrupal rtgGrupalPyme = new RatingGrupal();
				if (ri.getIdCliente() != null) {
				Cliente cliente = srvClientes.obtenerParteInvolucradaPorId(ri.getIdCliente());
				ri.setRut(cliente.getRut());
				List rtgNew = grp.getRatingGrupalPymePorRelacionado(ri);
				for (int p = 0 ; p < rtgNew.size(); p++){
					rtgGrupalPyme = (RatingGrupal) rtgNew.get(p);
					if (rtgGrupalPyme != null) {
						pasarHistoricoRatingsVigentes(rtgGrupalPyme,true);
					}
				}
			}
			gri.cambiarEstadoHistoricoSiebel(ri, logOper);
			count_ind++;
		}
		String msg = MessageFormat.format("OK: ratings vencidos caducados: {0,number,integer} individuales, {1,number,integer} grupales", new Object[] { new Integer(count_ind), new Integer(vgrupales.size()) });
		return msg;
	}
	
	/*
	 * Metodo pasar ratign grupales con nota manual a historicos
	 * Sprint 8
	 * Req. 7.4.29 - Vigencia de Rtg - Malla
	 */
	private void pasarHistoricosRatingGrupales(){

		List listMulti;
		List listPyme;
		Calendar creferencia = Calendar.getInstance();	
		Calendar crating = Calendar.getInstance();
		GestorRatingGrupal grp = new GestorRatingGrupalImpl();
		RatingGrupal rtgGrupal = new RatingGrupal(); 
		listMulti = grp.getBuscarRtgMultiNoInformados();
		
		for (int x = 0;x < listMulti.size(); x++){
			rtgGrupal = (RatingGrupal) listMulti.get(x);
			if (rtgGrupal != null && rtgGrupal.getFecha() != null) {
				crating.setTime(rtgGrupal.getFecha());
				long mesesValidacion = calculaDias(crating, creferencia);
				Integer val = ConfigDBManager.getValueAsInteger("rtg.grupal.multi.manual");
				
				if(val != null ) {

						if(!vigenciaUtil(mesesValidacion,val.intValue())){	 
							pasarHistoricoRatingsVigentes(rtgGrupal, false);
						 }
					
				}					
			}
		}
		listPyme = grp.getBuscarRtgPymeNoInformados();
		for (int y = 0; y < listPyme.size(); y++) {
			rtgGrupal = (RatingGrupal) listPyme.get(y);
			if (rtgGrupal != null && rtgGrupal.getFecha() != null) {
				crating.setTime(rtgGrupal.getFecha());
				long mesesValidacion = calculaDias(crating, creferencia);
				Integer val = ConfigDBManager.getValueAsInteger("rtg.grupal.pyme.manual");
				
				if(val != null) {

						if(!vigenciaUtil(mesesValidacion,val.intValue())){	 
							pasarHistoricoRatingsVigentes(rtgGrupal, true);
						 }
				}					
			}
		}

	}
	
	/*
	 * Req. 7.4.29 Vigencia de Rtg Sprint 8
	 */
	private void pasarHistoricoRatingsVigentes(RatingGrupal rtgGrpAVigentear, boolean esPyME) {
		GestorRatingGrupal gestRtgGrupal = new GestorRatingGrupalImpl();
		List lstRel = null;

		// Se obtienen los relacionados y para cada uno de ellos, se busca si tiene un rating grupal en estado vigente,
		// para pasarlo a historico
		if (esPyME) {
			lstRel = gestRtgGrupal.buscarRelacionadosGrupoPyME(rtgGrpAVigentear.getIdParteInvolucrada(), rtgGrpAVigentear.getIdRatingIndividual(), rtgGrpAVigentear.getIdVersion());
		} else {
			lstRel = gestRtgGrupal.buscarRelacionadosGrupoNoPyME(rtgGrpAVigentear.getIdRatingGrupal());
		}

		if (lstRel != null && !lstRel.isEmpty()) {
			for (int i = 0; i < lstRel.size(); i++) {
				IntegranteRatingGrupo rel = (IntegranteRatingGrupo) lstRel.get(i);
				// Se busca un rating grupal vigente en donde ya participe el relacionado
				List lstRtgGrpVig = gestRtgGrupal.buscarRatingsGrupales(rel.getRutRelacionado(), ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);
				if (lstRtgGrpVig != null && !lstRtgGrpVig.isEmpty()) {
					for (int j = 0; j < lstRtgGrpVig.size(); j++) {
						RatingGrupal rtgGrp = (RatingGrupal) lstRtgGrpVig.get(j);
						rtgGrp.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_HISTORICO);
						// DGJO rtgGrp.setFecha(new Date());

						// Si el id del rating grupal esta seteado es un rating grupal No PyME.
						if (rtgGrp.getIdRatingGrupal() != null) {
							// Se actualiza el rating grupal, con el nuevo estado y nueva fecha
							gestRtgGrupal.actualizarRatingGrupalNoPyME(rtgGrp);
						} else {
							// Se actualiza el rating grupal, con el nuevo estado y nueva fecha
							gestRtgGrupal.actualizarRatingGrupalPyME(rtgGrp);
						}
					}
				}
			}
		}
	}

	public String generarInterfazSiebel(Date desde, Date hasta, String logOper) {
		Date hoy = new Date();
		if (hoy.compareTo(hasta) <= 0) {
			return "ERROR: fecha actual menor que fecha futura solicitada";
		}
		GestorRatingIndividual gri = new GestorRatingIndividualImpl();
		GestorRatingGrupal grp = new GestorRatingGrupalImpl();

		RatingIndividual[] ratingIndividuales = gri.obtenerRatingsIndividualesNoInformados(false, desde, hasta);
		RatingGrupal[] ratingGrupales = grp.obtenerRatingsGrupalesNoInformados(desde, hasta);

		RatingIndividual ri;
		RatingGrupal rg;

		Map ratings = new LinkedHashMap();

		Double deprecado = new Double(999);
		// Llena todos los RI caducados por informar
		for (int i = 0; i < ratingIndividuales.length; i++) {
			ri = ratingIndividuales[i];
			Map record = (Map) ratings.get(ri.getRut());
			if (record == null) {
				record = new Hashtable();
				ratings.put(ri.getRut(), record);
			}
			record.put("DET_RUT", ri.getRut().split("-")[0]);
			record.put("DET_DV", ri.getRut().split("-")[1]);
			record.put("DET_NOTA_INDIV", deprecado);
			gri.establecerInformadoSiebel(ri, hasta, logOper);
		}

		ParteInvolucrada pi;
		ParteInvolucrada[] partesInvolucradas;
		// Llena todos los RG caducados por informar
		for (int i = 0; i < ratingGrupales.length; i++) {
			rg = ratingGrupales[i];

			partesInvolucradas = gri.obtenerPartesInvolucradasParaGrupal(rg);
			for (int j = 0; j < partesInvolucradas.length; j++) {
				pi = partesInvolucradas[j];
				Map record = (Map) ratings.get(pi.getRut());
				if (record == null) {
					record = new Hashtable();
					ratings.put(pi.getRut(), record);
				}
				record.put("DET_RUT", pi.getRut().split("-")[0]);
				record.put("DET_DV", pi.getRut().split("-")[1]);
				record.put("DET_NOTA_GRUPAL", deprecado);
			}
			grp.establecerInformado(rg, hasta, logOper);
		}

		List listaRegistros = new Vector();
		Iterator itRecords = ratings.keySet().iterator();
		while (itRecords.hasNext()) {
			listaRegistros.add(ratings.get(itRecords.next()));
		}

		String interfaz = generarInterfazSiebel(new Date(), hasta, listaRegistros);
		return interfaz;
	}

	public String regenerarInterfazSiebel(Date desde, Date hasta, String logOper) {
		Date hoy = new Date();
		if (hoy.compareTo(hasta) <= 0) {
			return "ERROR: fecha actual menor que fecha futura solicitada";
		}
		GestorRatingIndividual gri = new GestorRatingIndividualImpl();
		GestorRatingGrupal grp = new GestorRatingGrupalImpl();

		RatingIndividual[] ratingIndividuales = gri.obtenerRatingsIndividualesInformados(hasta);
		RatingGrupal[] ratingGrupales = grp.obtenerRatingsGrupalesInformados(hasta);

		RatingIndividual ri;
		RatingGrupal rg;

		Map ratings = new LinkedHashMap();

		Double deprecado = new Double(999);
		// Llena todos los RI caducados por informar
		for (int i = 0; i < ratingIndividuales.length; i++) {
			ri = ratingIndividuales[i];
			Map record = (Map) ratings.get(ri.getRut());
			if (record == null) {
				record = new Hashtable();
				ratings.put(ri.getRut(), record);
			}
			record.put("DET_RUT", ri.getRut().split("-")[0]);
			record.put("DET_DV", ri.getRut().split("-")[1]);
			record.put("DET_NOTA_INDIV", deprecado);
		}

		ParteInvolucrada pi;
		ParteInvolucrada[] partesInvolucradas;
		// Llena todos los RG caducados por informar
		for (int i = 0; i < ratingGrupales.length; i++) {
			rg = ratingGrupales[i];

			partesInvolucradas = gri.obtenerPartesInvolucradasParaGrupal(rg);
			for (int j = 0; j < partesInvolucradas.length; j++) {
				pi = partesInvolucradas[j];
				Map record = (Map) ratings.get(pi.getRut());
				if (record == null) {
					record = new Hashtable();
					ratings.put(pi.getRut(), record);
				}
				record.put("DET_RUT", pi.getRut().split("-")[0]);
				record.put("DET_DV", pi.getRut().split("-")[1]);
				record.put("DET_NOTA_GRUPAL", deprecado);
			}
		}

		List listaRegistros = new Vector();
		Iterator itRecords = ratings.keySet().iterator();
		while (itRecords.hasNext()) {
			listaRegistros.add(ratings.get(itRecords.next()));
		}

		String interfaz = generarInterfazSiebel(new Date(), hasta, listaRegistros);
		return interfaz;
	}

	// req:7.4.29 version base

//	private boolean calculaVigencia12_24(Calendar referencia, Calendar vaciado,
//			Calendar rating) {
//		long mesesReferencia = Math
//				.min(360, 720 - calculaDias(vaciado, rating));
//		long mesesValidacion = calculaDias(rating, referencia);
//		return mesesValidacion <= mesesReferencia;
//	}

	/**
	 * Se modica formula para calcular vigencia Req: 7.4.29 Sprint 3/4
	 */

	private boolean calculaVigencia12_24(long mesesValidacion, long difer,double a, double b, double X, double Y) {
	 // long mesesReferencia = Math.min(360, 720 - difer);
	 //long mesesReferencia = (long) Math.min(((xa.divide((xa1)) ).multiply((xa2)).add(Y)).doubleValue() ,	 Y.doubleValue()); 
	 long mesesReferencia = (long) Math.min(((X - Y) / (b - a)) * (difer - a) + Y,	 Y);
	 //long mesesReferencia = Math.min(((X.subtract(Y)).divide((b.subtract(a))) ).multiply((difer.subtract(a))).add(Y) ,	 Y); 
	 // se cambia formula por req 7.4.29
	 return mesesValidacion <= mesesReferencia;
	 }
	private long calculaDias(Calendar desde, Calendar hasta) {
		Date dhasta = hasta.getTime();
		Date ddesde = desde.getTime();
		long diferencia = (dhasta.getTime() - ddesde.getTime()) / MILLSECS_PER_DAY;
		return diferencia;
	}
	// req:7.4.29 version base
//	private boolean calculaVigencia12(Calendar referencia, Calendar ratingValido) {
//		long diasValidacion = calculaDias(ratingValido, referencia);
//		return diasValidacion < 360;
//	}

	/**
	 * Se modifica negocio para vigencia de rating. Req: 7.4.29 Sprint 3/4
	 * 
	 * @param lista
	 * @param referencia
	 * @return
	 */
	private RatingIndividual[] filtrarRatingsNoVencidos(
			RatingIndividual[] lista, Date referencia) {


		  RatingIndividual ri = null; 
		  List lst = new Vector(); 
		  Calendar creferencia = Calendar.getInstance();
		  Calendar cvaciado = Calendar.getInstance();
		  Calendar crating = Calendar.getInstance();
		  creferencia.setTime(referencia); 
		  Integer idbanca; Collection
		  parametros = new ArrayList();
		  for (int i = 0; i < lista.length;i++) {
			  ri = (RatingIndividual) lista[i];
			  idbanca = ri.getIdBanca();
		  // req 7.4.29 Sprint 3
			  RatingDAO adminDao = new RatingDAOImpl();
		  
		 parametros = adminDao.consultarParametrosVigencia(idbanca.toString());
		 

		  if (parametros != null && !parametros.isEmpty() && parametros.size() >= 5) { 
			  ri.setParametroA(((RatingIndividual) ((ArrayList) parametros) .get(0)).getNombreCorto()); 
			  ri.setParametroB(((RatingIndividual) ((ArrayList) parametros) .get(1)).getNombreCorto());	  
			  ri.setParametroX(((RatingIndividual) ((ArrayList) parametros) .get(3)).getNombreCorto());
			  ri.setParametroY(((RatingIndividual) ((ArrayList) parametros) .get(4)).getNombreCorto());
			  ri.setParametroZ(((RatingIndividual) ((ArrayList) parametros) .get(5)).getNombreCorto());
  
			  Double a = Double.valueOf(ri.getParametroA().trim());
			  Double b = Double.valueOf(ri.getParametroB().trim()); 
			  Double X = Double.valueOf(ri.getParametroX().trim()); 
			  Double Y = Double.valueOf(ri.getParametroY().trim());
			  String Z = ri.getParametroZ().trim(); 
			  
			  if (ri.getFechaCambioEstado() == null) { 
					if (log.isInfoEnabled()) log.error("Rating " + ri.getIdCliente() + " " + ri.getIdRating() + " sin fecha de cambio de estado");
							  continue; 
						} 
			  	  Calendar hoy = Calendar.getInstance();	
				  crating.setTime(ri.getFechaCambioEstado());
			  
			  long mesesValidacion = calculaDias(crating, hoy);
			  //RTG cerrados a mano
			  if(ri.getIdMotivoModifRating() != null) {
				  // setear valor defecto para  losrtg cerrados a mano modelo dependiendo del modelo
				  String rtgCerradoMan = ((RatingIndividual) ((ArrayList) parametros) .get(2)).getNombreCorto();
				  int cerradoManual = Integer.parseInt((rtgCerradoMan.trim()));
				 if(!vigenciaUtil(mesesValidacion,cerradoManual)){
					 lst.add(ri);
					  
				 }
				 continue;	
			  }
			  
			  // false  -  no considera el rating  financiero 
			  if (Z.equals("false")){
				  int vigenciaSinRating = 0;
				  ri.setVigenciaSinRating(((RatingIndividual) ((ArrayList) parametros)  .get(6)).getNombreCorto());
				  vigenciaSinRating = Integer.parseInt(ri .getVigenciaSinRating().trim()); 
				  if(!vigenciaUtil(mesesValidacion,vigenciaSinRating)){
						 lst.add(ri);
						 	 
					 }
				  continue;
			  }
			  long difer = 0;
			  
			  if (ri.getPeriodoVac0() == null) { 
				if (log.isInfoEnabled())
					  log.error("Rating " + ri.getIdCliente() + " " + ri.getIdRating() + " sin periodo de vaciado"); 
						  continue; 
					} 
			  if (ri.getFechaCambioEstado() == null) { 
				if (log.isInfoEnabled()) log.error("Rating " + ri.getIdCliente() + " " + ri.getIdRating() + " sin fecha de cambio de estado");
						  continue; 
					}
			  cvaciado.setTime(ri.getPeriodoVac0());  
			  crating.setTime(ri.getFechaCambioEstado());
				  
				 
		      difer = calculaDias(cvaciado, crating);
	
		      if (!calculaVigencia12_24(mesesValidacion, difer, a.doubleValue(), b.doubleValue(), X.doubleValue(), Y.doubleValue())) {
		    	  	lst.add(ri); 
		    	  }
				  
			  
			  } 
		} 
		return (RatingIndividual[]) lst .toArray(new
		RatingIndividual[lst.size()]);
		 

	}
	
	/*
	 * Metodo para validar vigencia de rtg cerrados a mano y sin rtg finan
	 * @Sprint7 - req. 7.4.29
	 */
	public boolean vigenciaUtil (long mesesValidacion, int valorDefinido) {
		return mesesValidacion <= valorDefinido;
	}

	/**
	 * Genera la interfaz de Siebel segun la defincion del documento con Fecha
	 * Envío: 02 de Abril del 2012 TODO VALIDAR LA FECHA, si es Date o String
	 * 
	 * @param fechaProc
	 * @param fechaDatos
	 * @param detalle
	 * @return
	 */
	private String generarInterfazSiebel(Date fechaProc, Date fechaDatos, List detalle) {
		StringBuffer sb = new StringBuffer();

		sb.append("C");
		sb.append(nomalizeField(fechaProc, 8));
		sb.append(nomalizeField(fechaDatos, 8));
		sb.append(nomalizeField("", 108));
		sb.append("\n");

		Iterator itDatos = detalle.iterator();
		Map registro = null;
		int iregistros = 0;
		long lsumdetrut = 0;
		while (itDatos.hasNext()) {
			iregistros++;
			registro = (Map) itDatos.next();

			Double dNotaIndividual = (Double) registro.get("DET_NOTA_INDIV");
			Double dNotaGrupal = (Double) registro.get("DET_NOTA_GRUPAL");
			String sdetrut = (String) registro.get("DET_RUT");
			Long ldetrut = new Long(sdetrut);
			lsumdetrut += ldetrut.longValue();

			String sNotaIndividual = (dNotaIndividual != null) ? "999" : "";
			String sNotaGrupal = (dNotaGrupal != null) ? "999" : "";

			sb.append("D");
			sb.append(nomalizeField(ldetrut, 9));
			sb.append(nomalizeField((String) registro.get("DET_DV"), 1));
			sb.append(nomalizeField(sNotaIndividual, 3));
			sb.append(nomalizeField(sNotaGrupal, 3));
			sb.append("        ");
			sb.append(nomalizeField("", 100));
			sb.append("\n");
		}

		sb.append("T");
		sb.append(nomalizeField(new Integer(iregistros), 9));
		sb.append(nomalizeField(new Long(lsumdetrut), 15));
		sb.append(nomalizeField("", 100));

		return sb.toString();
	}

	private static String nomalizeField(Date fechaProc, int len) {
		Calendar c = new GregorianCalendar();
		c.setTime(fechaProc);
		StringBuffer sb = new StringBuffer();
		sb.append(nomalizeField(new Integer(c.get(Calendar.YEAR)), 4));
		sb.append(nomalizeField(new Integer(c.get(Calendar.MONTH)), 2));
		sb.append(nomalizeField(new Integer(c.get(Calendar.DAY_OF_MONTH)), 2));
		return sb.toString();
	}

	private static String nomalizeField(String value, int len) {
		int padLen = len - value.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < padLen; i++) {
			sb.append(' ');
		}
		sb.append(value);
		return sb.toString();
	}

	private static String nomalizeField(Number value, int len) {
		int padLen = len - value.toString().length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < padLen; i++) {
			sb.append('0');
		}
		sb.append(value);
		return sb.toString();
	}

	// private Collection confirmarRating(RatingIndividual rating, String
	// logOper, boolean ignoraWarnings) {
	// if (log.isDebugEnabled()) {
	// log.debug("Iniciando confirmacion de rating...");
	// }
	//
	// // validar la vigencia de los componentes del rating solo si hay rating
	// // sugerido
	// List warnings = new Vector();
	// if (rating.tieneRatingSugerido()) {
	// if (log.isDebugEnabled()) {
	// log.debug("Validando condiciones de negocio para rating sugerido");
	// }
	//
	// // TODO validar vigencia componentes de rating...!!!
	//
	// if (!ignoraWarnings && warnings != null && !warnings.isEmpty()) {
	// return warnings;
	// }
	// }
	//
	// GestorRatingIndividual gestorRatingInd = new
	// GestorRatingIndividualImpl();
	//
	// // si existe un rating individual VIGENTE, al confirmar el nuevo
	// // rating individual, el rating previo pasa a histórico
	// if (log.isDebugEnabled()) {
	// log.debug("Recuperando rating vigente");
	// }
	//
	// RatingIndividual vigente =
	// gestorRatingInd.buscarRatingVigente(rating.getRut(),
	// rating.getIdBanca());
	// if (vigente != null) {
	// gestorRatingInd.cambiarEstadoHistorico(vigente, logOper);
	// }
	//
	// // se cambia a vigente el rating actual
	// gestorRatingInd.cambiarEstadoVigente(rating, logOper);
	// // se pone el rating actualizado al final de la lista
	// warnings.add(rating);
	//
	// return warnings;
	// }

	
	
	 
	 private void modificarIdRatingHojaIndependiente(Integer idBanca, Long idRating, Long idCliente, Long nuevoIdRating) {
			GestorHojaIndependiente gestorHoja = new GestorHojaIndependienteImpl();
			if (gestorHoja.bancaUsaHojaIndependiente(idBanca).booleanValue()) {
				// se recupera la hoja actual asociada al rating individual
				HojaIndependiente hojaIMDActual = gestorHoja.obtenerHojaIndependiente(idCliente, idRating);
				// se asigna el nuevo id rating individual al IMD asociado
				if (hojaIMDActual!=null){
					hojaIMDActual.setIdRating(nuevoIdRating);
					gestorHoja.actualizarHojaIndependiente(hojaIMDActual);
				}
			}
		}
	 
	 public RatingIndividual consultaPrcRating(String rutCliente, Long idRating){
			
		 // Se obtiene el identificador de parte involucrada del cliente
			if (log.isDebugEnabled()) {
				log.debug("Buscando datos Rating (porcentajes) " + rutCliente);
			}

			
			ServicioClientes srvCtes = new ServicioClientesImpl();
			Cliente cte = srvCtes.obtenerClientePorRut(rutCliente);
			Long idCliente = new Long(cte.getClienteId());

			if (log.isDebugEnabled()) {
				log.debug("Buscando rating individual para cliente " + rutCliente);
			}
			GestorRatingIndividual gestorInd = new GestorRatingIndividualImpl();
			SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRating);
			RatingIndividual rtgInd = gestorInd.consultaRatingSugerido(idCliente, idRating);
			
			

			return rtgInd;
		 
	 }
}
