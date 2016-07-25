package com.bch.sefe.rating;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.srv.ControladorAcceso;
import com.bch.sefe.comun.srv.ConversorMoneda;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.impl.ControladorAccesoImpl;
import com.bch.sefe.comun.srv.impl.ConversorMonedaImpl;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.util.XMLData;
import com.bch.sefe.comun.util.XMLDataList;
import com.bch.sefe.comun.util.XMLDataObject;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.RelacionRatingIndRatingGrupoNoPyME;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.constructora.srv.GestorConstructoraInmobiliaria;
import com.bch.sefe.constructora.srv.impl.GestorConstructoraInmobilariaImpl;
import com.bch.sefe.rating.dao.RatingDAO;
import com.bch.sefe.rating.dao.RatingFinancieroDAO;
import com.bch.sefe.rating.dao.RatingIndividualDAO;
import com.bch.sefe.rating.dao.RatingNegocioDAO;
import com.bch.sefe.rating.dao.impl.RatingDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingFinancieroDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingIndividualDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingNegocioDAOImpl;
import com.bch.sefe.rating.impl.GestorAlertasRtgIndividualImpl;
import com.bch.sefe.rating.impl.ServicioRatingIndividualImpl;
import com.bch.sefe.rating.srv.GestorAlertasRtgIndividual;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingGarante;
import com.bch.sefe.rating.srv.GestorRatingGrupal;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.GestorRatingNegocio;
import com.bch.sefe.rating.srv.impl.GestorRatingFinancieroImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingGaranteImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingGrupalImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingNegocioImpl;
import com.bch.sefe.rating.srv.impl.RatingUtil;
import com.bch.sefe.rating.vo.BalanceInmobiliario;
import com.bch.sefe.rating.vo.ComponenteRating;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingGarante;
import com.bch.sefe.rating.vo.RatingGrupal;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.rating.vo.Segmento;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.CatalogoVaciados;
import com.bch.sefe.vaciados.impl.CatalogoVaciadosImpl;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Vaciado;

public class AdaptadorRatingIndividual {
	private static final Logger log = Logger.getLogger(AdaptadorRatingIndividual.class);
	
	/**
	 * OPER 
	 * Invoca el los servicios de consulta rating individual y y consulta de los rating
	 * financiero y proyectado
	 * 
	 * @param request
	 * @return
	 */
	public XMLDataObject consultaRatingSugerido(XMLDataObject request){
		XMLDataObject respuesta 		= null;
		
		String rutCliente 		= (String) request.getObject(ConstantesRating.RUT_CLIENTE);
		Long idRating 			= request.getLong(ConstantesRating.ID_RATING);
		Integer idTipoRating 	= request.getInteger(ConstantesRating.TIPO_RATING);
		String usuario			= (String) request.getObject(ConstantesRating.LOG_OPERADOR);
		
		// info adicional agragegada para "perfilar" los componentes visuales
		String rol = request.getString(ConstantesRating.ROL);
		String modulo = request.getString(ConstantesRating.MODULO);

		//Se crea una instancia del servicio de Rating Individual
		ServicioRatingIndividual servRatingInd = new ServicioRatingIndividualImpl();
		Collection datos = servRatingInd.consultaRatingSugerido(rutCliente, idRating, idTipoRating, usuario);
		 
		// se recupera la data retornada por el servicio de negocio
		Object datosRespuesta[] = datos.toArray();
		RatingIndividual rtgInd 	= (RatingIndividual) datosRespuesta[0];
		RatingFinanciero rtgFinan 	= (RatingFinanciero) datosRespuesta[1];
		Boolean esRolComercialPyme	= (Boolean) datosRespuesta[2];
		List motivosCambio 			= (List) datosRespuesta[3];

		/*		// INICIO DGJO Nota no es visible 
		if ((rtgInd.getRatingNegocio()==null)&&(rtgInd.getIdRatingNegocio()!=null)
				&&(rtgInd.getBanca().equals("PyME"))&&((ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE.equals(rtgInd.getIdEstado()))||(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_HISTORICO.equals(rtgInd.getIdEstado())))){
			
			String rc = rtgInd.getRatingComportamiento()+"";
			String prc = rtgInd.getPrcRatingComportamiento()+"";
			String rf = rtgInd.getRatingFinanciero()+"";
			String prf = rtgInd.getPrcRatingFinanciero()+"";
			//String  rF = rtgInd.getRatingFinal()+"";	
			String  rPre = rtgInd.getRatingPreliminar1()+"";	
			
			if((!prc.equals("null"))&&(!prc.equals(""))&&(!prf.equals("null"))&&(!prf.equals(""))){	
				double RComportamiento	= (Double.parseDouble(rc)*Double.parseDouble(prc))/100;
				double RFinanciero		= (Double.parseDouble(rf)*Double.parseDouble(prf))/100;
				double RPreliminar			= Double.parseDouble(rPre);
				double eRNegocio		= ((RPreliminar-(RComportamiento + RFinanciero))/(Double.parseDouble(rtgInd.getPrcRatingNegocio()+"")/100));
				//double RFinal 			= Double.parseDouble(rF);
				//double eRNegocio		= ((RFinal-(RComportamiento + RFinanciero))/(Double.parseDouble(rtgInd.getPrcRatingNegocio()+"")/100));
				
				
				rtgInd.setRatingNegocio(Double.valueOf(eRNegocio+""));
			}
		}
		
		// FIN DGJO Nota no es visible 
		*/
		
		// se crea el detalle de rating
		XMLDataObject detalleRating = new XMLDataObject(ConstantesRating.DATA_DETALLE_RATING);

		List listaRoles = ConfigDBManager.getValuesAsListString(ConstantesSEFE.KEY_ROL_PERFIL_PYME_COMERCIAL);
		
		// Sprint 7 responsable rating negocio
		rtgInd.setRtgNegocioConfirmado(Boolean.valueOf(rtgInd.getRatingNegocio() != null));
		
		// existen segmentos ....
		if (datosRespuesta.length > 4) {
			List segmentos = (List) datosRespuesta[4];
			Integer segmento = null;
			try {
				segmento = (Integer) datosRespuesta[5];
			
			} catch (Exception ex) {
				if (log.isDebugEnabled()) {
        			log.debug("No hay segmento identificado aun");
        		}
			}
			
			XMLDataObject xmlSegmentos = creaXMLSegmentos(segmentos, segmento);
			detalleRating.put(ConstantesRating.SEGMENTOS_VENTA, xmlSegmentos);
		}
		
		RatingFinanciero rtgProy = null;
		if (rtgInd != null) {
			GestorUsuarios gestorUsr = new GestorUsuariosImpl();
			Usuario usr   			 = null;
						
			// Se recuperan los componentes para buscar los responsables de financiero
			if(rtgInd.getIdRatingFinanciero() != null) {
				GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
				RatingFinanciero rating = gestorFinanciero.obtenerRating(rtgInd.getIdRatingFinanciero());
				
				usr = gestorUsr.obtenerUsuario(rating.getIdUsuario());
				rtgInd.setResponsableRatingFinanciero(usr.getCodigoUsuario());
			}
			
			// Se recuperan los componentes para buscar los responsables de negocio.
			if(rtgInd.getIdRatingNegocio() != null && rtgInd.getRtgNegocioConfirmado().booleanValue()==true ) {
				GestorRatingNegocio gestorNegocio = new GestorRatingNegocioImpl();
				RatingNegocio rating = gestorNegocio.buscarRatingNegocioPorId(rtgInd.getIdRatingNegocio());
				
				usr = gestorUsr.obtenerUsuario(rating.getIdUsuario());
				rtgInd.setResponsableRatingNegocio(usr.getCodigoUsuario());				
			}
			
			// Se recuperan los componentes para buscar los responsables de proyectado.
			if(rtgInd.getIdRatingProyectado() != null) {
				GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
				rtgProy = gestorFinanciero.obtenerRating(rtgInd.getIdRatingProyectado());
				
				// solo se muestran los rating confirmados
				if (ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE.intValue() == rtgProy.getEstado().intValue()) {
					usr = gestorUsr.obtenerUsuario(rtgProy.getIdUsuario());
					rtgInd.setResponsableRatingProyectado(usr.getCodigoUsuario());		
				} else {
					rtgInd.setFechaRatingProyectado(null);
					rtgInd.setRatingProyectado(null);
				}
			}
			
			// Srprint 9  logica verificación de componentes vigencia
			RatingIndividual rtgIndValidado = validarVigenciaComponentes(rtgInd, rutCliente);			
			XMLData detalleIndividual = crearDetalleRatingIndividual(rtgIndValidado);
			if (rtgFinan != null && rtgFinan.getFechaBalance() != null) {
											
				//GBRL - IR75890 CONTINUIDAD
				SelectFechaUltimoEstadoFinanciero fechaUEFA =  new SelectFechaUltimoEstadoFinanciero();
				String fecha = fechaUEFA.SelectFechaUltimoEstadoFinancieroAnual(rtgFinan, rtgProy);
				//GBRL - IR75890 CONTINUIDAD
				//String fecha = FormatUtil.formatDateRptFF(rtgFinan.getFechaBalance()); - IR75890 CONTINUIDAD	
				
										
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(rtgFinan.getFechaBalance());
				if (calendar.get(Calendar.MONTH) != Calendar.DECEMBER) {
					if (rtgFinan.getFechaBalance1() != null) {
						fecha = FormatUtil.formatDateRptFF(rtgFinan.getFechaBalance1());
					}
				}
				((XMLDataObject) detalleIndividual).put(ConstantesRating.FECHA_BALANCE, fecha);
			}
			detalleRating.put(ConstantesRating.RATING_INDIVIDUAL, detalleIndividual);
			
		}

		if (rtgFinan != null) {
			XMLData detalleFinanciero = crearDetalleRatingFinanciero(rtgFinan, rtgProy);
			detalleRating.put(ConstantesRating.RATING_FINANCIERO, detalleFinanciero);
		} else {
			//En el caso de los rating mmigrados, estos no tienen rating financiero asociado
			//por lo que se deben obtener los periodos del vaciado desde el rating individual.
			//esto es solo para el pintado en el frontend.
			XMLData detalleFinanciero = crearDetalleRatingFinanciero(rtgInd);
			detalleRating.put(ConstantesRating.RATING_FINANCIERO, detalleFinanciero);
		}
		
		if (motivosCambio != null) {
			XMLDataList motivos = new XMLDataList();
			for (int i = 0; i < motivosCambio.size(); ++i) {
				Clasificacion cl = (Clasificacion) motivosCambio.get(i);
				XMLDataObject motivo = new XMLDataObject();
				motivo.put(ConstantesRating.KEY, cl.getIdClasif());
				motivo.put(ConstantesRating.VALUE, cl.getDescripcion());
				motivo.put(ConstantesRating.ESTADO, cl.getEstId());
				motivos.add(motivo);
			}
			
			detalleRating.put(ConstantesRating.DATA_LISTA_MOTIVOS, motivos);
		}
		
		
		ControladorAcceso ctrlAcceso = new ControladorAccesoImpl();
		// usuario puede guardar rating individual
		Boolean puedeGuardar = Boolean.FALSE;
		try {
			puedeGuardar = new Boolean(ctrlAcceso.autorizarOperacion(usuario, rol, ConstantesSEFE.OPER_GRABAR_RATING_INDIVIDUAL, modulo));
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
    			log.debug("No existe autorizacion para poder guardar rating individual");
    		}
		}

		// usuario puede confirmar rating individual
		Boolean puedeConfirmar = Boolean.FALSE;
		try {
			puedeConfirmar = new Boolean(ctrlAcceso.autorizarOperacion(usuario, rol, ConstantesSEFE.OPER_CONFIRMAR_RATING_INDIVIDUAL, modulo));
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
    			log.debug("No existe autorizacion para poder confirmar rating individual");
    		}
		}
		
		// usuario puede confirmar rating individual modificado
		Boolean puedeConfirmarModificado = Boolean.FALSE;
		try {
			puedeConfirmarModificado = Boolean.FALSE;//new Boolean(ctrlAcceso.autorizarOperacion(usuario, rol, ConstantesSEFE.OPER_CONFIRMAR_RATING_INDIVIDUAL_MODIFICADO, modulo));
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
    			log.debug("No existe autorizacion para poder confirmar rating individual confirmado");
    		}
		}
		
		detalleRating.put(ConstantesRating.USR_PUEDE_GUARDAR_INDIVIDUAL, puedeGuardar);
		detalleRating.put(ConstantesRating.USR_PUEDE_CONFIRMAR_INDIVIDUAL, puedeConfirmar);
		detalleRating.put(ConstantesRating.USR_PUEDE_CONFIRMAR_INDIVIDUAL_MODIFICADO, puedeConfirmarModificado);
		
		respuesta = new XMLDataObject();
		respuesta.put(ConstantesRating.DATA_DETALLE_RATING, detalleRating);
		if (rtgInd.getIdRatingProyectado() != null && rtgInd.getIdBanca().equals(ConstantesSEFE.BANCA_INMOBILIARIAS)){
			GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
			RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(rtgInd.getIdRatingProyectado());
			if (rtgFinanciero.getEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE)){
				GestorConstructoraInmobiliaria gestorContru = new GestorConstructoraInmobilariaImpl();
				BalanceInmobiliario bi=gestorContru.obtenerBi(rtgInd.getIdCliente(), rtgInd.getIdRating(), null);
				if (null != bi){
					List hojasBi=gestorContru.obtenerListaHojaBi(bi, rtgInd.getIdCliente());
					respuesta.put(ConstantesRating.DATA_CANT_PROY_INMOB, new Integer(hojasBi.size()));
				}
			}
		}
		return respuesta;
	}
	
	private RatingIndividual validarVigenciaComponentes(RatingIndividual rtgInd, String rutCliente)
	{
		GestorAlertasRtgIndividual gestorAlertas = new GestorAlertasRtgIndividualImpl();
		RatingIndividualDAO ratingIndDao = new RatingIndividualDAOImpl();
		List alertas = gestorAlertas.obtenerAlertasRtgIndividualModelo(rtgInd.getIdRating(), rutCliente, rtgInd.getIdBanca());
		boolean validacionFinan = true;
		boolean validacionProy = true;
		boolean validacionNeg = true;
		Boolean respaldoRatingNegocioConfirmado = rtgInd.getRtgNegocioConfirmado();
		Boolean respaldoRatingFinancieroConfirmado = rtgInd.getRtgFinancieroConfirmado();
		Boolean respaldoRatingProyectadoConfirmado = rtgInd.getRtgProyectadoConfirmado();
		Boolean respaldoRatingComportamientoConfirmado = rtgInd.getRtgComportamientoConfirmado();
		Boolean respaldoRatingIndividualConfirmado = rtgInd.getRtgIndividualConfirmado();
		Double respaldoNotaProy = rtgInd.getRatingProyectado();
		Double respaldoNotaNeg = rtgInd.getRatingNegocio();
		Double respaldoNotaFin = rtgInd.getRatingFinanciero();
		Double respaldoNotaCcomp = rtgInd.getRatingComportamiento();
				
		if(rtgInd.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO))
		{
			if(rtgInd.getIdRatingFinanciero() != null && 
					(alertas.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_FINANCIERO) || 
					alertas.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_VERSION_VACIADOS) ||
					alertas.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_HOJA_IMD_VIGENTE)))
			{
				// dejar en curso rating financiero
				RatingFinancieroDAO finanDao = new RatingFinancieroDAOImpl();
				RatingFinanciero rtgFinan = finanDao.buscarRatingPorId(rtgInd.getIdRatingFinanciero());
				rtgFinan.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
				rtgFinan.setNotaFinanciera(null);
				finanDao.actualizarEstadoFinanciero(rtgFinan);
				rtgInd.setIdRatingFinanciero(null);
				rtgInd.setRatingFinanciero(null);
				rtgInd = ratingIndDao.desasociarRatingFinanciero(rtgInd.getIdCliente(), rtgInd.getIdRating());
				validacionFinan = false;
			}
			if(rtgInd.getIdRatingProyectado() != null && 
					(alertas.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_PROYECTADO) || 
							(!rtgInd.getIdBanca().equals(ConstantesSEFE.BANCA_CONSTRUCTORAS) &&
									 !rtgInd.getIdBanca().equals(ConstantesSEFE.BANCA_AGRICOLAS) &&
									 !rtgInd.getIdBanca().equals(ConstantesSEFE.BANCA_INMOBILIARIAS) && alertas.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_VERSION_VACIADOS))))
			{
				// dejar en curso rating proyectado
				RatingFinancieroDAO finanDao = new RatingFinancieroDAOImpl();
				RatingFinanciero rtgFinan = finanDao.buscarRatingPorId(rtgInd.getIdRatingProyectado());
				rtgFinan.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
				finanDao.actualizarEstadoFinanciero(rtgFinan);
				rtgInd.setIdRatingProyectado(null);
				rtgInd.setRatingProyectado(null);
				rtgInd = ratingIndDao.desasociarRatingProyectado(rtgInd.getIdCliente(), rtgInd.getIdRating());
				validacionProy = false;
			}
			if(rtgInd.getIdRatingNegocio() != null &&
					(alertas.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_NEGOCIO) || 
					alertas.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_VERSION_VACIADOS) ||
					alertas.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_TPO_VACIADO_RTG_NEGOCIO) ||
					alertas.contains("El Segmento para el Cliente ha cambiado. Debe responder nuevamente las preguntas para Rating de Negocio")))
			{
				// dejar en curso rating negocio
				RatingNegocioDAO negocioDao = new RatingNegocioDAOImpl();
				RatingNegocio rtgNeg = negocioDao.buscarRatingNegocioPorId(rtgInd.getIdRatingNegocio());
				rtgNeg.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
				negocioDao.actualizarEstadoRating(rtgNeg);
				rtgInd.setIdRatingNegocio(null);
				rtgInd.setRatingNegocio(null);
				validacionNeg = false;
				rtgInd = ratingIndDao.desasociarRatingNegocio(rtgInd.getIdCliente(), rtgInd.getIdRating());
			}
			
			if(!alertas.isEmpty())
			{
				//Se verifica si la banca es construcción o inmobiliaria para borrar las ponderaciones si corresponde
				if((rtgInd.getIdBanca().equals(ConstantesSEFE.BANCA_CONSTRUCTORAS) && !validacionFinan) || (rtgInd.getIdBanca().equals(ConstantesSEFE.BANCA_INMOBILIARIAS) && !validacionProy))
				{
					rtgInd = ratingIndDao.borrarPonderaciones(rtgInd.getIdCliente(), rtgInd.getIdRating());
				}
				
				rtgInd.setRatingFinal(null);
				rtgInd = ratingIndDao.actualizarRatingIndividual(rtgInd);
			}
			rtgInd.setVigenciaComponentes(new Boolean(alertas.isEmpty()));
		}
		
		//se restauran las confirmaciones y notas
		rtgInd.setRtgComportamientoConfirmado(respaldoRatingComportamientoConfirmado);
		rtgInd.setRtgFinancieroConfirmado(respaldoRatingFinancieroConfirmado);
		rtgInd.setRtgIndividualConfirmado(respaldoRatingIndividualConfirmado);
		rtgInd.setRtgNegocioConfirmado(respaldoRatingNegocioConfirmado);
		rtgInd.setRtgProyectadoConfirmado(respaldoRatingProyectadoConfirmado);
		rtgInd.setRatingProyectado(validacionProy ? respaldoNotaProy : null);
		rtgInd.setRatingNegocio(validacionNeg ? respaldoNotaNeg : null);
		rtgInd.setRatingFinanciero(validacionFinan ? respaldoNotaFin : null);
		rtgInd.setRatingComportamiento(respaldoNotaCcomp);
		
		return rtgInd;
		
	}
	
	
	
	private XMLDataObject creaXMLSegmentos(List segmentos, Integer segmento) {
		XMLDataObject xml = new XMLDataObject();
		xml.put(ConstantesRating.ID_SEG, segmento);
		
		XMLDataList xmlList = new XMLDataList();
		for (int i = 0; i < segmentos.size(); ++i) {
			Segmento seg = (Segmento) segmentos.get(i);
			XMLDataObject xmlSeg = new XMLDataObject(ConstantesRating.SEGMENTO);
			xmlSeg.put(ConstantesRating.ID_SEG, seg.getIdSegmento());
			xmlSeg.put(ConstantesRating.SEGMENTO, seg.getNombreSegmento());
			
			xmlList.add(xmlSeg);
		}
		xml.put(ConstantesRating.LISTA_SEGMENTOS, xmlList);

		return xml;
	}



	/**
	 * Invoca el servicio de generaciï¿½n de un nuevo rating individual.
	 * Si ya existe rating individual, informa mensaje y retorna rating en curso
	 * 
	 * @param request
	 * @return
	 * Requerimiento 7.4.26 Sprint 3 Rating Individual Transversal 
	 */
	public XMLDataObject generarRatingIndividual(XMLDataObject request) {
		XMLDataObject respuesta 		= null;
		
		String rutCliente 		= (String) request.getObject(ConstantesRating.RUT_CLIENTE);
		Integer idTipoRating 	= request.getInteger(ConstantesRating.ID_BANCA);
		String usuario			= (String) request.getObject(ConstantesRating.LOG_OPERADOR);
		String rol = request.getString(ConstantesRating.ROL);
		String modulo = request.getString(ConstantesRating.MODULO);
		GestorRatingIndividual gRtgIndiv = new GestorRatingIndividualImpl();
		ControladorAcceso ctrlAcceso = new ControladorAccesoImpl();
		ArrayList rtgIndiv = new ArrayList();
		rtgIndiv =  (ArrayList) gRtgIndiv.buscarRatingsIndividualesPorCliente(null, rutCliente);
		if (rtgIndiv != null && rtgIndiv.size() > 0 && !idTipoRating.equals(((RatingIndividual)rtgIndiv.get(0)).getIdBanca())){
			ctrlAcceso.autorizarOperacion(usuario, rol, ConstantesSEFE.OPER_GENERAR_RATING_INDEPENDIENTE, modulo);
		}
		
		//Se crea una instancia del servicio de Rating Individual
		ServicioRatingIndividual servRatingInd = new ServicioRatingIndividualImpl();
		RatingIndividual rtgInd = servRatingInd.generarRating(rutCliente, idTipoRating, usuario);
		
		//Sprint 8 req s/n redireccionamiento de componentes	
		RatingDAO ratingDao = new RatingDAOImpl();
		ArrayList componentes = (ArrayList)ratingDao.buscarComponentesRating(rtgInd.getIdBanca(), null);		
		ComponenteRating primerComponente = (ComponenteRating)componentes.get(0);
		
		respuesta = crearXMLRating(rtgInd, primerComponente.getIdComponente());
		
		return respuesta;
	}

	
	
	/**
	 * Guarda un rating individual dejandolo en estado EN CURSO, ya sea que 
	 * proviene de una nota genrada por el sistema o la nota modificada por 
	 * el usuario.
	 */
	public XMLDataObject grabarRatingIndividual(XMLDataObject request) {
		XMLDataObject respuesta 		= null;
		
		//Se transforma el mensaje XML en un objeto del dominio u Objeto Java.
		String  rutCliente 		= request.getString(ConstantesRating.RUT_CLIENTE);
		Long    idRating 		= request.getLong(ConstantesRating.ID_RATING);
		Integer idBca 	        = request.getInteger(ConstantesRating.ID_BANCA);
		String  usuario			= request.getString(ConstantesRating.LOG_OPERADOR);
		String  comentario      = request.getString(ConstantesRating.COMENTARIO);
		
		ServicioRatingIndividual servicioRatingIndividual = new ServicioRatingIndividualImpl();
		
		//Se invoca al servicio de negocio para guardar el rating.
		RatingIndividual rating = servicioRatingIndividual.guardarRatingIndividual(rutCliente, idRating, idBca, usuario, comentario);
		
		//Se crea la respuesta, tranformando la respuesta de negocio en un mensaje XML. 
		respuesta = new  XMLDataObject(ConstantesRating.DATA_RATING_INDIVIDUAL);
		
		respuesta.put(ConstantesRating.ID_RATING, rating.getIdRating());
		respuesta.put(ConstantesRating.ID_ESTADO, rating.getIdEstado());
		respuesta.put(ConstantesRating.ESTADO, rating.getEstado());
		if (rating.getFechaModificacion() != null) {
			respuesta.put(ConstantesRating.FECHA_RATING, FormatUtil.formatDateRating(rating.getFechaModificacion()));
		}
		
		//Se busca el logOper del responsable, quien fue el ultimo en modificar el rating.
		Long idResponsable = rating.getIdUsuarioModificacion();		
		GestorUsuarios gestorUsuarios = new GestorUsuariosImpl();		
		Usuario usr = gestorUsuarios.obtenerUsuario(idResponsable);
		
		//Se coloca el logOper en la respuesta.
		respuesta.put(ConstantesRating.RESPONSABLE, usr.getCodigoUsuario());
				
		return respuesta;
	}
	
	
	
	/**
	 * Deja en estado vigente el rating individual, ya sea que proviene de la nota
	 * sugerida por el sistema o la nota modificada por el usuario
	 * 
	 * @param request
	 * @return
	 */
	public XMLDataObject confirmarRatingIndividualModelo(XMLDataObject request) {
		XMLDataObject respuesta 		= null;
		
		String rutCliente 		= request.getString(ConstantesRating.RUT_CLIENTE);
		Long idRating 			= request.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		Integer idBanca 		= request.getInteger(ConstantesRating.ID_BANCA);
		String usuario			= request.getString(ConstantesRating.LOG_OPERADOR);
		Double notaRating		= request.getDouble(ConstantesRating.NOTA);
		String comentario		= request.getString(ConstantesRating.COMENTARIO);
		Integer motivo			= request.getInteger(ConstantesRating.ID_MOTIVO);
		String motivoComentario	= request.getString(ConstantesRating.COMENTARIO_MODIFICACION);
		
		//Se crea una instancia del servicio de Rating Individual
		ServicioRatingIndividual servRatingInd = new ServicioRatingIndividualImpl();
		
		RatingIndividual rtgInd = servRatingInd.confirmarRatingIndividualModelo(rutCliente, idRating, idBanca, usuario, notaRating, comentario, motivo, motivoComentario);
		
		if (rtgInd.getIdCliente() != null && rtgInd.getIdRating() != null) {
			vincularRatingIndividual(rutCliente, rtgInd);												//sprint 2 req 7.1.4 alinear y borrar rating en curso
		}
		
		if (rtgInd.existenAlertasComponentes()) {
			return this.generarRespuestaAlertasConfirmacionModelo(rtgInd.getAlertasComponentes()); 
		}
		
		// se busca el nombre del usuario responsable
		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerUsuario(rtgInd.getIdUsuarioModificacion());
		
		respuesta = new XMLDataObject(ConstantesRating.RATING_INDIVIDUAL);
		respuesta.put(ConstantesRating.ID_RATING, rtgInd.getIdRating());
		respuesta.put(ConstantesRating.ID_ESTADO, rtgInd.getIdEstado());
		respuesta.put(ConstantesRating.FECHA_RATING, FormatUtil.formatDateRating(rtgInd.getFechaCambioEstado()));
		respuesta.put(ConstantesRating.RESPONSABLE, usr.getCodigoUsuario());
		respuesta.put(ConstantesRating.ESTADO, rtgInd.getEstado());

		return respuesta;
	}

	private XMLDataObject generarRespuestaAlertasConfirmacionModelo(List alertas) {
		XMLDataObject respuesta = new XMLDataObject();
		respuesta.put(ConstantesRating.FLAG_ERROR, Boolean.TRUE);
		XMLDataList xdlAlertas = new XMLDataList();
		for (int i = 0; i < alertas.size(); i++) {
			XMLDataObject xdoAlerta = new XMLDataObject();
			xdoAlerta.put(ConstantesRating.MENSAJE, (String) alertas.get(i));

			xdlAlertas.add(xdoAlerta);
		}
		respuesta.put(ConstantesRating.ALERTAS, xdlAlertas);
		return respuesta;
	}
	
	/**
	 * Deja en estado vigente el rating individual, ya sea que proviene de la nota
	 * sugerida por el sistema o la nota modificada por el usuario
	 * 
	 * @param request
	 * @return
	 */
	public XMLDataObject confirmarRatingIndividualModificado(XMLDataObject request) {
		XMLDataObject respuesta 		= null;
		
		String rutCliente 		= request.getString(ConstantesRating.RUT_CLIENTE);
		Long idRating 			= request.getLong(ConstantesRating.ID_RATING);
		Integer idBanca 		= request.getInteger(ConstantesRating.ID_BANCA);
		String usuario			= request.getString(ConstantesRating.LOG_OPERADOR);
		Double notaRating		= request.getDouble(ConstantesRating.NOTA);
		String comentario		= request.getString(ConstantesRating.COMENTARIO);
		Integer motivo			= request.getInteger(ConstantesRating.ID_MOTIVO);
		String motivoComentario	= request.getString(ConstantesRating.COMENTARIO_MODIFICACION);
		
		//Se crea una instancia del servicio de Rating Individual
		ServicioRatingIndividual servRatingInd = new ServicioRatingIndividualImpl();		
		RatingIndividual rtgInd = servRatingInd.confirmarRatingIndividualModificado(rutCliente, idRating, idBanca, usuario, notaRating, comentario, motivo, motivoComentario);
		
		if (rtgInd.getIdCliente() != null && rtgInd.getIdRating() != null) {
			vincularRatingIndividual(rutCliente, rtgInd);												//sprint 2 req 7.1.4 alinear y borrar rating en curso
		}
		
		// se busca el nombre del usuario responsable
		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerUsuario(rtgInd.getIdUsuarioModificacion());
		
		respuesta = new XMLDataObject(ConstantesRating.RATING_INDIVIDUAL);
		respuesta.put(ConstantesRating.ID_RATING, rtgInd.getIdRating());
		respuesta.put(ConstantesRating.ID_ESTADO, rtgInd.getIdEstado());
		respuesta.put(ConstantesRating.FECHA_RATING, FormatUtil.formatDateRating(rtgInd.getFechaModificacion()));
		respuesta.put(ConstantesRating.RESPONSABLE, usr.getCodigoUsuario());
		respuesta.put(ConstantesRating.ESTADO, rtgInd.getEstado());
		
		return respuesta;
	}
	
	private XMLDataObject crearXMLRating(RatingIndividual rtgInd, Integer redireccion) {
		XMLDataObject xml = new XMLDataObject(ConstantesRating.RATING_INDIVIDUAL);
		xml.put(ConstantesRating.ID_RATING, rtgInd.getIdRating());
		xml.put(ConstantesRating.ID_ESTADO, rtgInd.getIdEstado());
		xml.put(ConstantesRating.FECHA_RATING, FormatUtil.formatDateRating(rtgInd.getFechaCreacion()));
		xml.put(ConstantesRating.REDIRECCION, redireccion);
		
		return xml;
	}
	
	// crea el mensaje con el detalle del rating individual
	private XMLData crearDetalleRatingIndividual(RatingIndividual rat) {
		XMLDataObject xml = new XMLDataObject();
		
		XMLDataObject rFin = new XMLDataObject();
		rFin.put(ConstantesRating.PONDERACION, rat.getPrcRatingFinanciero());
		if (rat.getRatingFinanciero() != null) {
			rFin.put(ConstantesRating.NOTA, RatingUtil.aproximarNota(rat.getRatingFinanciero()));
			rFin.put(ConstantesRating.RESPONSABLE, rat.getResponsableRatingFinanciero());
			if (rat.getFechaRatingFinanciero() != null) {
				rFin.put(ConstantesRating.FECHA, FormatUtil.formatDateRating(rat.getFechaRatingFinanciero()));
			}
		}
		xml.put(ConstantesRating.RATING_FINANCIERO, rFin);
		
		XMLDataObject rPry = new XMLDataObject();
		rPry.put(ConstantesRating.PONDERACION, rat.getPrcRatingProyectado());
		if (rat.getRatingProyectado() != null) {
			rPry.put(ConstantesRating.NOTA, RatingUtil.aproximarNota(rat.getRatingProyectado()));
			rPry.put(ConstantesRating.RESPONSABLE, rat.getResponsableRatingProyectado());
			if (rat.getFechaRatingProyectado() != null) {
				rPry.put(ConstantesRating.FECHA, FormatUtil.formatDateRating(rat.getFechaRatingProyectado()));
			}
		}
		xml.put(ConstantesRating.RATING_PROYECTADO, rPry);
		
		XMLDataObject rNeg = new XMLDataObject();
		rNeg.put(ConstantesRating.PONDERACION, rat.getPrcRatingNegocio());
		if (rat.getRatingNegocio() != null && rat.getRtgNegocioConfirmado().booleanValue()||(rat.getRatingNegocio() != null &&(
				ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE.equals(rat.getIdEstado())
				||ConstantesSEFE.ID_CLASIF_ESTADO_RATING_HISTORICO.equals(rat.getIdEstado()))) ) {
			rNeg.put(ConstantesRating.NOTA, RatingUtil.aproximarNota(rat.getRatingNegocio()));
			rNeg.put(ConstantesRating.RESPONSABLE, rat.getResponsableRatingNegocio());
			if (rat.getFechaRatingNegocio() != null) {
				rNeg.put(ConstantesRating.FECHA, FormatUtil.formatDateRating(rat.getFechaRatingNegocio()));
			}
		}
		xml.put(ConstantesRating.RATING_NEGOCIO, rNeg);
		
		XMLDataObject rCmp = new XMLDataObject();
		if (rat.getFechaRatingComportamiento() != null) {
			rCmp.put(ConstantesRating.FECHA, FormatUtil.formatDateRating(rat.getFechaRatingComportamiento()));
		}
		rCmp.put(ConstantesRating.PONDERACION, rat.getPrcRatingComportamiento());
		rCmp.put(ConstantesRating.NOTA, RatingUtil.aproximarNota(rat.getRatingComportamiento()));
		rCmp.put(ConstantesRating.RESPONSABLE, rat.getResponsableRatingComportamiento());
		xml.put(ConstantesRating.RATING_COMPORTAMIENTO, rCmp);
		XMLDataObject rPond = new XMLDataObject();
		rPond.put(ConstantesRating.NOTA, RatingUtil.aproximarNota(rat.getRatingPreliminar1()));
		
		// Sprint 9 vigencia de componentes: se valida si el rating individual tiene todos sus componentes vigentes, sólo en ese caso se setean las notas para el rating individual
		if((rat.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO) && rat.getVigenciaComponentes().booleanValue()) || !rat.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO))
		{
			xml.put(ConstantesRating.RATING_PONDERADO, rPond);
			try {
				// 20130404.jlmanriquez - Incidencia #44. Se quitan lineas que sumaban ratingPreliminar1 + premioTamano
				// ya que dentro del calculo existe un campo que ya almcena la nota como ratingPreliminar2 con todas
				// las reglas de negocio correspondientes.
				xml.put(ConstantesRating.RATING_AJUSTADO, RatingUtil.aproximarNota(rat.getRatingPreliminar2()));
			} catch (NullPointerException ex) {
				if (log.isDebugEnabled()) {
	    			log.debug("no fue posible calcular el rtgAjustado... rating preliminar 1 + premio por tamaÃ±o");
	    		}
			}
			xml.put(ConstantesRating.RATING_MODELO, rat.getRatingFinalSugerido());
			xml.put(ConstantesRating.RATING_INDIVIDUAL, rat.getRatingFinal());
		}
		
		
		
			/*
			 * marias 20121025 - se agregan fecha y responsable del rating garante
			 * 
			 */
			xml.put(ConstantesRating.RATING_GARANTE, RatingUtil.aproximarNota(rat.getRatingGarante()));
		
		
		
		
		if (rat.getRatingGarante() != null) {
			GestorRatingGarante srvGte = new GestorRatingGaranteImpl();
			RatingGarante gte = srvGte.consultarRating(rat.getIdRatingGarante(), rat.getIdCliente());
			xml.put(ConstantesRating.RATING_GARANTE_FECHA, FormatUtil.formatDateRating(rat.getFechaRatingGarante()));
			
			GestorUsuarios gstUsr = new GestorUsuariosImpl();
			Usuario usr = gstUsr.obtenerUsuario(gte.getUsuarioId());
			xml.put(ConstantesRating.RATING_GARANTE_RESPONSABLE, usr.getCodigoUsuario());
		}
		
		xml.put(ConstantesRating.ID_RATING, rat.getIdRating());
		xml.put(ConstantesRating.ID_ESTADO, rat.getIdEstado());
		xml.put(ConstantesRating.RESPONSABLE, rat.getNombreResponsable());
		/** Cambio Natalia Cerda 07-02-2014
		 * Se Modifica el campo fecha de modificación por el campo FechaCambioEstado 
		 * para que se muestre al consultar un rating individual la fecha en la que paso el rating a estado vigente
		 * y no la fecha en que se cambia el estado del rating 
		 * if (rat.getFechaModificacion() != null) {
		 *xml.put(ConstantesRating.FECHA_RATING, FormatUtil.formatDateRating(rat.getFechaModificacion()));//getFechaCambioEstado
		*/
		if (rat.getFechaCambioEstado() != null) {
			
			xml.put(ConstantesRating.FECHA_RATING, FormatUtil.formatDateRating(rat.getFechaCambioEstado()));
		}	
		if (rat.getFechaEEFF() != null) {
			xml.put(ConstantesRating.FECHA_BALANCE, FormatUtil.formatDateRating(rat.getFechaEEFF()));
		}
		xml.put(ConstantesRating.PATRIMONIO, rat.getMontoPatrimonio());
		xml.put(ConstantesRating.PATRIMONIO_UF, getMontoPatrimonioUF(rat));
		xml.put(ConstantesRating.VENTAS, rat.getMontoVenta());
		if (ConstantesSEFE.BANCA_AGRICOLAS.equals(rat.getIdBanca()) ){
			xml.put(ConstantesRating.VENTAS_UF, rat.getMontoVenta());
		}else {
			xml.put(ConstantesRating.VENTAS_UF, getMontoVentaUF(rat));
		}
			
		
		xml.put(ConstantesRating.COMENTARIO, rat.getComentario());
		xml.put(ConstantesRating.ID_MOTIVO, rat.getIdMotivoModifRating());
		xml.put(ConstantesRating.COMENTARIO_MODIFICACION, rat.getComentarioModificacion());
		xml.put(ConstantesRating.ESTADO, rat.getEstado());
		xml.put(ConstantesRating.ID_BANCA, rat.getIdBanca());
		
		return xml;
	}

	private Double getMontoVentaUF(RatingIndividual rat) {
		final Double monto = rat.getMontoVenta();
		final Date periodo = getPeriodoVaciado(rat);
		return convertirPesosAUF(monto, periodo);
	}

	private Double getMontoPatrimonioUF(RatingIndividual rat) {
		final Double monto = rat.getMontoPatrimonio();
		final Date periodoVaciado = getPeriodoVaciado(rat);
		return convertirPesosAUF(monto, periodoVaciado);
	}

	private Double convertirPesosAUF(final Double monto, Date periodo) {
		ConversorMoneda conversor = new ConversorMonedaImpl();
		Double montoUF;
		if (monto == null) {
			montoUF = null;
		}
		else {
			montoUF = conversor.convertirMonedaSegunReglas(
										monto, 
										ConstantesSEFE.ID_CLASIF_MONEDA_CLP, 
										ConstantesSEFE.ID_CLASIF_MILES, 
										ConstantesSEFE.ID_CLASIF_MONEDA_UF, 
										ConstantesSEFE.ID_CLASIF_MILES, 
										periodo
										);
		}
		return montoUF;
	}
	


	private Date getPeriodoVaciado(RatingIndividual rat) {
		// si no hay rating financiero, no hay fecha de referencia...en consecuencia se 
		// retorna la fecha de sistema
		Long idRatingFinanciero = null;
		if ((ConstantesSEFE.BANCA_AGRICOLAS.equals(rat.getIdBanca()) && rat.getIdRatingProyectado() == null) || (!ConstantesSEFE.BANCA_AGRICOLAS.equals(rat.getIdBanca()) && rat.getIdRatingFinanciero() == null )){
			return new Date(System.currentTimeMillis());
			
		}
		if (rat.getIdRatingFinanciero() != null) {
			idRatingFinanciero=rat.getIdRatingFinanciero();
		}else if (rat.getIdRatingProyectado() != null) {
			idRatingFinanciero=rat.getIdRatingProyectado();
		}
		GestorRatingFinanciero gestorFinan = new GestorRatingFinancieroImpl();
		RatingFinanciero ratingFin = gestorFinan.obtenerRating(idRatingFinanciero);
		
		GestorVaciados gestorVaciados = new GestorVaciadosImpl();

		Vaciado vac = gestorVaciados.buscarVaciado(ratingFin.getIdVaciado0());

		if (vac.getMesesPer().intValue() != 12 && !rat.getIdBanca().equals(ConstantesSEFE.BANCA_PYME) && !rat.getIdBanca().equals(ConstantesSEFE.BANCA_AGRICOLAS)  && !rat.getIdBanca().equals(ConstantesSEFE.BANCA_BANCOS)) {
			vac = gestorVaciados.buscarVaciado(ratingFin.getIdVaciado1());
		}
		
		Date periodo = null;
		if (vac != null) {
			periodo = vac.getPeriodo();
		}
		
		if (periodo == null) {
			// TODO ver si esta solucion es correcta para el caso periodo == null
			final Date fechaCambioEstado = rat.getFechaCambioEstado();
			if (fechaCambioEstado == null) {
				periodo = new Date(System.currentTimeMillis());
			}
			else {
				periodo = FormatUtil.convertirUltimoDiaMes(fechaCambioEstado);
			}
		}
		else {
			periodo = FormatUtil.convertirUltimoDiaMes(periodo);
		}
		return periodo;
	}

	// crea el mensaje con el detalle de rating financiero
	private XMLData crearDetalleRatingFinanciero(RatingFinanciero rat, RatingFinanciero rtgProy) {
		XMLDataList xml = new XMLDataList();
		
		CatalogoVaciados catalogoVaciados = new CatalogoVaciadosImpl();
		
		Vaciado vaciado0 = catalogoVaciados.buscarDatosGeneral(rat.getIdVaciado0());
		Vaciado vaciado1 = null;
		Vaciado vaciado2 = null;
		
		if (rat.getIdVaciado1() != null) {
			vaciado1 = catalogoVaciados.buscarDatosGeneral(rat.getIdVaciado1());
		}
		
		if (rat.getIdVaciado2() != null )
		{
			vaciado2 = catalogoVaciados.buscarDatosGeneral(rat.getIdVaciado2());
		}
		
		Vaciado vaciadoP = null;
		if (rtgProy != null && rtgProy.getIdVacProyectado() != null) {
			 vaciadoP = catalogoVaciados.buscarDatosGeneral(rtgProy.getIdVacProyectado());
		}
		
		// periodo 0
		double notaSinAjuste = 0;
		Double notaSAj = null;
		
		XMLDataObject r0 = new XMLDataObject();
		if (vaciado2 != null)
		{
			r0.put(ConstantesRating.FECHA, FormatUtil.formatDateHeaderRating(vaciado2.getPeriodo()));
			r0.put(ConstantesRating.NOTA, rat.getNotaPeriodo2());
			r0.put(ConstantesRating.NOTA_AJUSTADA_POR_CALIDAD_INFO, rat.getPeriodo(2).getNotaPeriodoConAjusteCalidadInformacion());
			r0.put(ConstantesRating.PESO_RELATIVO_PERIODO, rat.getPesoPeriodo2());
			
			notaSinAjuste += rat.getPesoPeriodo2().doubleValue() * rat.getPeriodo(2).getNotaPeriodoConAjusteCalidadInformacion().doubleValue();
		}
		
		// periodo 1
		XMLDataObject r1 = new XMLDataObject();
		if (vaciado1 != null) {
			r1.put(ConstantesRating.FECHA, FormatUtil.formatDateHeaderRating(vaciado1.getPeriodo()));
			r1.put(ConstantesRating.NOTA, rat.getNotaPeriodo1());
			r1.put(ConstantesRating.NOTA_AJUSTADA_POR_CALIDAD_INFO, rat.getPeriodo(1).getNotaPeriodoConAjusteCalidadInformacion());
			r1.put(ConstantesRating.PESO_RELATIVO_PERIODO, rat.getPesoPeriodo1());
		}
		
		try {
			notaSinAjuste += rat.getPesoPeriodo1().doubleValue() * rat.getPeriodo(1).getNotaPeriodoConAjusteCalidadInformacion().doubleValue();
			notaSinAjuste += rat.getPesoPeriodo0().doubleValue() * rat.getPeriodo(0).getNotaPeriodoConAjusteCalidadInformacion().doubleValue();
			notaSAj = new Double(notaSinAjuste / 100.00);
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
    			log.debug("no fue posible calcular: (la suma de peso periodo 1 * nota periodo con ajuste calidad informacion del periodo 0) + (la suma de peso periodo 0 * nota periodo con ajuste calidad informacion del periodo 0)");
    		}
		}
		
		// periodo 2
		XMLDataObject r2 = new XMLDataObject();
		if (vaciado0 != null) {
			r2.put(ConstantesRating.FECHA, FormatUtil.formatDateHeaderRating(vaciado0.getPeriodo()));
			r2.put(ConstantesRating.NOTA, rat.getNotaPeriodo0());
			r2.put(ConstantesRating.NOTA_AJUSTADA_POR_CALIDAD_INFO, rat.getPeriodo(0).getNotaPeriodoConAjusteCalidadInformacion());
			r2.put(ConstantesRating.PESO_RELATIVO_PERIODO, rat.getPesoPeriodo0());
			if (notaSAj != null) {
				r2.put(ConstantesRating.NOTA_AJUSTADA_POR_CALIDAD, notaSAj);
			}
			r2.put(ConstantesRating.NOTA_AJUSTADA_POR_ESCALA, rat.getNotaFinanciera());
		}
				
		// periodo proyectado (3)
		
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		
		XMLDataObject r3 = new XMLDataObject();
		if (rtgProy != null && vaciadoP != null) {
			boolean isEnCurso = ConstantesRating.CLASIF_ID_RATING_EN_CURSO.equals(rtgProy.getEstado());
			if(!isEnCurso) {
				Double ajusteProyectado = gestorFinanciero.buscarAjustePorCalidad(vaciadoP.getIdClasifCalidad()).getFactor();
				r3.put(ConstantesRating.FECHA, FormatUtil.formatDateHeaderRating(vaciadoP.getPeriodo())); //periodo del vaciado
				r3.put(ConstantesRating.NOTA, rtgProy.getNotaPeriodo0()); //Rating PerÃ­odo
				r3.put(ConstantesRating.NOTA_AJUSTADA_POR_CALIDAD_INFO, new Double(rtgProy.getNotaPeriodo0().doubleValue() + ajusteProyectado.doubleValue()));
				//el peso relativo para el rating financiero proyectado siempre es --
				r3.put(ConstantesRating.PESO_RELATIVO_PERIODO, null);
				r3.put(ConstantesRating.NOTA_AJUSTADA_POR_CALIDAD, new Double(rtgProy.getNotaPeriodo0().doubleValue() + ajusteProyectado.doubleValue()));
				r3.put(ConstantesRating.NOTA_AJUSTADA_POR_ESCALA, rtgProy.getNotaFinanciera()); // Rating con Ajuste de Escala
			}
		}

		xml.add(r0);
		xml.add(r1);
		xml.add(r2);
		xml.add(r3);
		
		return xml;
	}
	
	private XMLData crearDetalleRatingFinanciero(RatingIndividual rat) {
		XMLDataList xml = new XMLDataList();
		
		XMLDataObject r0 = new XMLDataObject();
		if (rat.getPeriodoVac2() != null) {
			r0.put(ConstantesRating.FECHA, FormatUtil.formatDateHeaderRating(rat.getPeriodoVac2()));
		}
		XMLDataObject r1 = new XMLDataObject();
		if (rat.getPeriodoVac1() != null) {
			r1.put(ConstantesRating.FECHA, FormatUtil.formatDateHeaderRating(rat.getPeriodoVac1()));
		}
		XMLDataObject r2 = new XMLDataObject();
		if (rat.getPeriodoVac0() != null) {
			r2.put(ConstantesRating.FECHA, FormatUtil.formatDateHeaderRating(rat.getPeriodoVac0()));
		}
		XMLDataObject r3 = new XMLDataObject();
		
		xml.add(r0);
		xml.add(r1);
		xml.add(r2);
		xml.add(r3);
		
		return xml;
	}

	public XMLDataObject caducarRatingsVencidos(XMLData req) {
		XMLDataObject request = (XMLDataObject) req;
		XMLDataObject response = new XMLDataObject();

		ServicioRatingIndividual srv = new ServicioRatingIndividualImpl();
		String usuario = request.getString("usuario");
		String sfdesde = request.getString("fdesde");
		String sfhasta = request.getString("fhasta");

		Date desde = parseDate(sfdesde);
		Date hasta = parseDate(sfhasta);
		String sresult = srv.caducarRatingsVencidos(desde, hasta, usuario);
		response.put("responseconsoledata", sresult);
		return response;
	}

	public XMLDataObject generarInterfazSiebel(XMLData req) {
		XMLDataObject request = (XMLDataObject) req;
		XMLDataObject response = new XMLDataObject();
		ServicioRatingIndividual srv = new ServicioRatingIndividualImpl();

		String usuario = (String) request.getObject(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_LOG_OPE);
		String sfdesde = request.getString("fdesde");
		String sfhasta = request.getString("fhasta");

		Date desde = parseDate(sfdesde);
		Date hasta = parseDate(sfhasta);
		String sresult = srv.generarInterfazSiebel(desde, hasta, usuario);
		response.put("responseconsoledata", sresult);
		return response;
	}

	public XMLDataObject regenerarInterfazSiebel(XMLData req) {
		XMLDataObject request = (XMLDataObject) req;
		XMLDataObject response = new XMLDataObject();
		ServicioRatingIndividual srv = new ServicioRatingIndividualImpl();

		String usuario = (String) request.getObject(ConstantesSEFE.OBT_MATRIZ_NEG_KEY_LOG_OPE);
		String sfdesde = request.getString("fdesde");
		String sfhasta = request.getString("fhasta");

		Date desde = parseDate(sfdesde);
		Date hasta = parseDate(sfhasta);
		String sresult = srv.regenerarInterfazSiebel(desde, hasta, usuario);
		response.put("responseconsoledata", sresult);
		return response;
	}

	private Date parseDate(String sdate) {
		if (sdate == null || sdate.length() != 8) {
			return null;
		}
		String sy = sdate.substring(0, 4);
		String sm = sdate.substring(4, 6);
		String sd = sdate.substring(6, 8);

		int iy;
		int im;
		int id;
		try {
			iy = Integer.valueOf(sy).intValue();
			im = Integer.valueOf(sm).intValue();
			id = Integer.valueOf(sd).intValue();
		} catch (NumberFormatException e) {
			return null;
		}

		Calendar c = GregorianCalendar.getInstance();
		c.set(iy, im -1, id, 0, 0);
		return c.getTime();
	}

	/**
	 * sprint 2 req 7.1.4 alinear y borrar rating en curso
	 * método para vincular un rating individual confirmado con un rating grupal en curso si lo hay
	 * @param rutCliente rut del cliente asociado con el rating
	 * @param rtgInd nuevo rating individual confirmado
	 */
	private void vincularRatingIndividual(String rutCliente, RatingIndividual rtgInd) {
		GestorRatingGrupal gestRtgGrp = new GestorRatingGrupalImpl();
		// Se obtienen los rating grupales en donde el rut es un relacionado. PyMEs y No PyMEs
		List lstRtgGrp = gestRtgGrp.buscarRatingsGrupales(rutCliente, ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		if (!lstRtgGrp.isEmpty()) {
			GestorServicioClientes gestCli = new GestorServicioClientesImpl();
			Cliente cli = gestCli.obtenerClientePorRut(rutCliente);
			Long idCliente = Long.valueOf(cli.getClienteId());
			for (int i = 0; i < lstRtgGrp.size(); i++) {
				RatingGrupal rtgGrupalVincular = null;
				RatingGrupal rtgGrupal = (RatingGrupal) lstRtgGrp.get(i);	
				// Si el id de rating grupal existe, estamos en presencia de un rating grupal No PyME.
				if (rtgGrupal.getIdRatingGrupal() != null) {
					List relaciones = gestRtgGrp.buscarRelacionRatingIndGrupoNoPyME(idCliente, rtgGrupal.getIdRatingGrupal());
					if (relaciones != null && !relaciones.isEmpty()) {
						// Se obtiene el primer elemento ya que siempre existira un rating individual por grupo y relacionado
						RelacionRatingIndRatingGrupoNoPyME rel = (RelacionRatingIndRatingGrupoNoPyME) relaciones.get(0);
						rtgGrupal.setIdRatingIndividual(rel.getIdRatingInd());
						rtgGrupalVincular = rtgGrupal;
					}
				} else if(idCliente.equals(rtgGrupal.getIdParteInvolucrada())) {
					rtgGrupalVincular = rtgGrupal;
				}
				if (rtgGrupalVincular != null) {
					gestRtgGrp.vincularRatingIndividual(rtgGrupalVincular, rtgInd);
				}
			}
		}
	}
	
}