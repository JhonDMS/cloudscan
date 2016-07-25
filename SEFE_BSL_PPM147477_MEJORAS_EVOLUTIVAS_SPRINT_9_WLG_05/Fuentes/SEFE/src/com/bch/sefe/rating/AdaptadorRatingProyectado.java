package com.bch.sefe.rating;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.agricola.dao.AgricolaDAO;
import com.bch.sefe.agricola.dao.impl.AgricolaDAOImpl;
import com.bch.sefe.agricola.vo.Agricola;
import com.bch.sefe.comun.CatalogoGeneral;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.CatalogoGeneralImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.ConsultaServicios;
import com.bch.sefe.comun.srv.impl.ConsultaServiciosImplCache;
import com.bch.sefe.comun.util.XMLDataList;
import com.bch.sefe.comun.util.XMLDataObject;
import com.bch.sefe.comun.vo.Archivo;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.PeriodoRating;
import com.bch.sefe.comun.vo.RatingProyectado;
import com.bch.sefe.comun.vo.RatingProyectadoCGE;
import com.bch.sefe.comun.vo.RatingProyectadoESP;
import com.bch.sefe.comun.vo.TipoCambio;
import com.bch.sefe.constructora.dao.ConstructoraInmobilariaDAO;
import com.bch.sefe.constructora.dao.impl.ConstructoraInmobilariaDAOImpl;
import com.bch.sefe.rating.dao.RatingFinancieroDAO;
import com.bch.sefe.rating.dao.RatingIndividualDAO;
import com.bch.sefe.rating.dao.RatingNegocioDAO;
import com.bch.sefe.rating.dao.impl.RatingFinancieroDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingIndividualDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingNegocioDAOImpl;
import com.bch.sefe.rating.impl.GestorAlertasRtgIndividualImpl;
import com.bch.sefe.rating.impl.ServicioRatingAgricolaImpl;
import com.bch.sefe.rating.impl.ServicioRatingProyectadoConstructoraImpl;
import com.bch.sefe.rating.impl.ServicioRatingProyectadoImpl;
import com.bch.sefe.rating.impl.ServicioRatingProyectadoInmobiliariaImpl;
import com.bch.sefe.rating.srv.GestorAlertasRtgIndividual;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.GestorRatingProyectado;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingProyectadoImpl;
import com.bch.sefe.rating.vo.BalanceInmobiliario;
import com.bch.sefe.rating.vo.FlagCuentaProyeccion;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.rating.vo.Soe;
import com.bch.sefe.servicios.Contexto;
import com.bch.sefe.servicios.XMLObject;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.ServicioVaciados;
import com.bch.sefe.vaciados.impl.ServicioVaciadosImpl;
import com.bch.sefe.vaciados.vo.Cuenta;
import com.bch.sefe.vaciados.vo.Vaciado;

public class AdaptadorRatingProyectado {
	final static Logger log = Logger.getLogger(AdaptadorRatingProyectado.class);
	final static String MONEDA_CLP = "CLP";
	/**
	 * Crea una nueva instancia del rating proyectado, a partir de la informaci�n del rating financiero 
	 * 
	 */
	public XMLDataObject crearProyectado(XMLDataObject msg) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingProyectado servicio	= new ServicioRatingProyectadoImpl();
		
		String rutCte 		= msg.getString(ConstantesRating.RUT_CLIENTE);
		Long idRating 		= msg.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		Long idFinan 		= msg.getLong(ConstantesRating.ID_RATING_FINANCIERO);
		String logUsu		= msg.getString(ConstantesRating.LOG_OPERADOR);

		RatingProyectadoCGE rating = (RatingProyectadoCGE) servicio.crearProyectado(rutCte, idRating, idFinan, logUsu);
		
		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();
		ServicioClientes srvCtes = new ServicioClientesImpl();
		Long idCte = srvCtes.obtenerIdClientePorRut(rutCte);
		RatingIndividual ratingInd = gestorRating.buscarRatingIndividual(idCte, idRating);
		
		rating = validarVigenciaProyectadoCGE(ratingInd, rutCte, rating);
		
		XMLDataObject header 	= crearXMLHeader(rating, rating.getModoConsulta());
		if ( rating.getPeriodoProy() != null ) {
			header.put(ConstantesRating.RESPONSABLE, rating.getRatingFinanciero().getResponsable());
		}
		XMLDataObject detalle 	= crearXMLDetalle(rating);
		
		xmlConsulta.put(ConstantesRating.HEADER_PROYECCION, header);
		xmlConsulta.put(ConstantesRating.DETALLE_PROYECCION, detalle);
		
		return xmlConsulta;		
	}
	
	
	/**
	 * Calcula los valores corespondientes a un vaciado proyectado, en base a los per�odos anteriores y los 
	 * ingresos de cuentas del usuario
	 */
	public XMLDataObject generarProyeccion(XMLDataObject msg) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingProyectado servicio	= new ServicioRatingProyectadoImpl();
		
		String rutCte 		= msg.getString(ConstantesRating.RUT_CLIENTE);
		Long idRating 		= msg.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		Long idProy 		= msg.getLong(ConstantesRating.ID_RATING_PROYECTADO);
		String logUsu		= msg.getString(ConstantesRating.LOG_OPERADOR);
		
		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRating);

		XMLDataList xmlCuentas = (XMLDataList) msg.getObject(ConstantesRating.LST_CUENTAS);
		List cuentas		   = crearListaCuentas(xmlCuentas);
		
		RatingProyectadoCGE rating = (RatingProyectadoCGE) servicio.generarProyeccion(rutCte, idRating, idProy, logUsu, cuentas);
		
		XMLDataObject header 	= crearXMLHeader(rating, ConstantesRating.MODO_CONSULTA_CONFIRMAR);
		header.put(ConstantesRating.RESPONSABLE, rating.getRatingFinanciero().getResponsable());
		XMLDataObject detalle 	= crearXMLDetalle(rating);
		xmlConsulta.put(ConstantesRating.HEADER_PROYECCION, header);
		xmlConsulta.put(ConstantesRating.DETALLE_PROYECCION, detalle);
		
		return xmlConsulta;	
	}
	
	
	/**
	 * Indica al sistema que el rating financiero proyectado y el vaciado proyectado deben cambiar
	 * su estado a VIGENTE
	 */
	public XMLDataObject confirmarProyeccion(XMLDataObject msg) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingProyectado servicio	= new ServicioRatingProyectadoImpl();
		
		String rutCte 		= msg.getString(ConstantesRating.RUT_CLIENTE);
		Long idRating 		= msg.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		Long idProy 		= msg.getLong(ConstantesRating.ID_RATING_PROYECTADO);
		String logUsu		= msg.getString(ConstantesRating.LOG_OPERADOR);

		RatingProyectadoCGE rating = (RatingProyectadoCGE) servicio.confirmarProyeccion(rutCte, idRating, idProy, logUsu);
		
		XMLDataObject header 	= crearXMLHeader(rating, ConstantesRating.MODO_CONSULTA_DEFAULT);
		header.put(ConstantesRating.RESPONSABLE, rating.getRatingFinanciero().getResponsable());
		xmlConsulta.put(ConstantesRating.HEADER_PROYECCION, header);
		
		return xmlConsulta;	
	}
	
	
	/**
	 * Realiza la busqueda de un rating financiero proyectado a partir del identificador del rating
	 * proyectado
	 */
	public XMLDataObject consultarProyeccion(XMLDataObject msg) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingProyectado servicio	= new ServicioRatingProyectadoImpl();
		
		String rutCte 		= msg.getString(ConstantesRating.RUT_CLIENTE);
		Long idRating 		= msg.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		Long idProy 		= msg.getLong(ConstantesRating.ID_RATING_PROYECTADO);
		String logUsu		= msg.getString(ConstantesRating.LOG_OPERADOR);
		String modo			= msg.getString(ConstantesRating.MODO);

		RatingProyectadoCGE rating = (RatingProyectadoCGE) servicio.consultarProyeccion(rutCte, idRating, idProy, logUsu, modo);
		
		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();
		ServicioClientes srvCtes = new ServicioClientesImpl();
		Long idCte = srvCtes.obtenerIdClientePorRut(rutCte);
		RatingIndividual ratingInd = gestorRating.buscarRatingIndividual(idCte, idRating);
		
		rating = validarVigenciaProyectadoCGE(ratingInd, rutCte, rating);
		
		XMLDataObject header 	= crearXMLHeader(rating, rating.getModoConsulta());
		XMLDataObject detalle 	= crearXMLDetalle(rating);
		header.put(ConstantesRating.RESPONSABLE, rating.getRatingFinanciero().getResponsable());
		
		xmlConsulta.put(ConstantesRating.HEADER_PROYECCION, header);
		xmlConsulta.put(ConstantesRating.DETALLE_PROYECCION, detalle);
		
		return xmlConsulta;		
	}
	
	public XMLDataObject ingresarRatingProyectadoAgricola(XMLDataObject request, Archivo archivo ) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingAgricola servicio	= new ServicioRatingAgricolaImpl();
		XMLDataObject object = (XMLDataObject)request.getObject(ConstantesRating.PARAM_CARGA_DATOS);
		String logOperador = object.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = object.getString(ConstantesRating.RUT_CLIENTE);
		// id rating individual
		Long idRatingInd = object.getLong(ConstantesRating.ID_RATING);
		Boolean existe=servicio.ingresarRatingProyectadoAgricola(Boolean.FALSE, archivo, logOperador,rutCliente, idRatingInd);
		xmlConsulta.put("existeRating".concat(existe.toString().concat("X")), existe);
		return xmlConsulta;
	}
	
	public XMLDataObject ingresarRatingProyectadoConstructora(XMLDataObject request, Archivo archivo ) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingProyectadoConstructora servicio	= new ServicioRatingProyectadoConstructoraImpl();
		XMLDataObject object = (XMLDataObject)request.getObject(ConstantesRating.PARAM_CARGA_DATOS);
		String logOperador = object.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = object.getString(ConstantesRating.RUT_CLIENTE);
		Boolean existe=servicio.ingresarRatingProyectadoConstructora(archivo, logOperador,rutCliente);
		xmlConsulta.put("existeRating".concat(existe.toString().concat("X")), existe);
		return xmlConsulta;
	}
	
	
	public XMLDataObject ingresarRatingProyectadoInmobiliario(XMLDataObject request, Archivo archivo ) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingProyectadoInmobiliaria servicio	= new ServicioRatingProyectadoInmobiliariaImpl();
		XMLDataObject object = (XMLDataObject)request.getObject(ConstantesRating.PARAM_CARGA_DATOS);
		String logOperador = object.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = object.getString(ConstantesRating.RUT_CLIENTE);
		Boolean existe=servicio.ingresarRatingProyectadoInmobiliario( archivo, logOperador,  rutCliente);		
		xmlConsulta.put("existeRating".concat(existe.toString().concat("X")), existe);
		return xmlConsulta;
	}
	public XMLDataObject cargarParametrosAgricola(XMLDataObject request, Archivo archivo ) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingAgricola servicio	= new ServicioRatingAgricolaImpl();
		XMLDataObject object = (XMLDataObject)request.getObject(ConstantesRating.PARAM_CARGA_DATOS);
		String logOperador = object.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = object.getString(ConstantesRating.RUT_CLIENTE);
		// id rating individual
		servicio.cargarParametrosAgricola( archivo, logOperador,  rutCliente);
		return xmlConsulta;
	}
	public XMLDataObject actualizarCargaBalanceInmobiliario(XMLDataObject request, Archivo archivo) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingProyectadoInmobiliaria servicio	= new ServicioRatingProyectadoInmobiliariaImpl();
		XMLDataObject object = (XMLDataObject)request.getObject(ConstantesRating.PARAM_CARGA_DATOS);
		String logOperador = object.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = object.getString(ConstantesRating.RUT_CLIENTE);
		servicio.actualizarCargaBalanceInmobiliario(archivo, logOperador,rutCliente);
		xmlConsulta.put("existeRating".concat(Boolean.FALSE.toString().concat("X")), Boolean.FALSE);
		return xmlConsulta;
	}
	
	public XMLDataObject actualizarCargaBalanceConstructora(XMLDataObject request, Archivo archivo) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingProyectadoConstructora servicio	= new ServicioRatingProyectadoConstructoraImpl();
		XMLDataObject object = (XMLDataObject)request.getObject(ConstantesRating.PARAM_CARGA_DATOS);
		String logOperador = object.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = object.getString(ConstantesRating.RUT_CLIENTE);
		servicio.actualizarCargaBalanceConstructora(archivo, logOperador,rutCliente);
		xmlConsulta.put("existeRating".concat(Boolean.FALSE.toString().concat("X")), Boolean.FALSE);
		return xmlConsulta;
	}
	
	public XMLDataObject actualizarCargaBalanceAgricola(XMLDataObject request, Archivo archivo) {
		XMLDataObject xmlConsulta           = new XMLDataObject();
		ServicioRatingAgricola servicio	= new ServicioRatingAgricolaImpl();
		XMLDataObject object = (XMLDataObject)request.getObject(ConstantesRating.PARAM_CARGA_DATOS);
		String logOperador = object.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = object.getString(ConstantesRating.RUT_CLIENTE);
		// id rating individual
		Long idRatingInd = object.getLong(ConstantesRating.ID_RATING);
		servicio.ingresarRatingProyectadoAgricola(Boolean.TRUE,archivo, logOperador,rutCliente, idRatingInd);
		xmlConsulta.put("existeRating".concat(Boolean.FALSE.toString().concat("X")), Boolean.FALSE);
		return xmlConsulta;
	}
	
	
	public Collection buscarBalances(Contexto ctx,XMLDataObject request) {

		Long idRating = null;
		String rutCliente = request.getString(ConstantesRating.RUT_CLI);
		
		ArrayList xmlConsulta           = new ArrayList();
		
		if (ctx.getIdPlantilla().equals(ConstantesSEFE.BANCA_INMOBILIARIAS.toString())){
			idRating = request.getLong(ConstantesRating.DATA_RATING_INDIVIDUAL);
			ServicioRatingProyectadoInmobiliaria servicio	= new ServicioRatingProyectadoInmobiliariaImpl();
			xmlConsulta=(ArrayList)servicio.buscarBiXRut(ctx.getRutCliente(),idRating);
		}else if (ctx.getIdPlantilla().equals(ConstantesSEFE.BANCA_CONSTRUCTORAS.toString())){
			idRating = request.getLong(ConstantesRating.DATA_RATING_INDIVIDUAL);
			ServicioRatingProyectadoConstructora servicio	= new ServicioRatingProyectadoConstructoraImpl();
			xmlConsulta=(ArrayList)servicio.buscarSoeXRut(ctx.getRutCliente(),idRating);
		}else if (ctx.getIdPlantilla().equals(ConstantesSEFE.BANCA_AGRICOLAS.toString())){
			rutCliente = request.getString(ConstantesRating.RUT_CLIENTE);
			idRating = request.getLong(ConstantesRating.ID_RATING);
			ServicioRatingAgricola servicio	= new ServicioRatingAgricolaImpl();
			xmlConsulta=(ArrayList)servicio.buscarAgricolaXRutYRating(ctx.getRutCliente(), idRating);
		}
		
		if(idRating != null && !xmlConsulta.isEmpty())
		{
			GestorRatingIndividual gestor = new GestorRatingIndividualImpl();
			ServicioClientes servicioCliente = new ServicioClientesImpl();
			Cliente cliente = servicioCliente.obtenerClientePorRut(rutCliente);
			Long idCliente = Long.valueOf(cliente.getClienteId());
			
			RatingIndividual ratingInd = gestor.buscarRatingIndividual(idCliente, idRating);
			
			xmlConsulta = validarVigenciaProyectado(ratingInd, rutCliente, xmlConsulta);
		}
		
		return xmlConsulta;
	}
	
	private ArrayList validarVigenciaProyectado(RatingIndividual rtgInd, String rutCliente, ArrayList xmlConsulta)
	{		
		GestorAlertasRtgIndividual gestorAlertas = new GestorAlertasRtgIndividualImpl();
		RatingIndividualDAO ratingIndDao = new RatingIndividualDAOImpl();
		List alertas = gestorAlertas.obtenerAlertasRtgIndividualModelo(rtgInd.getIdRating(), rutCliente, rtgInd.getIdBanca());
		boolean validacionProy = true;
		ArrayList xmlRetorno = xmlConsulta;
		
		Integer estadoBalance = verificarEstadoBalance(xmlConsulta, rtgInd.getIdBanca(), rtgInd);
				
		if(rtgInd.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO) && rtgInd.getIdRatingProyectado() != null && estadoBalance.intValue() != 4301)
		{
			if((alertas.contains(ConstantesSEFE.MSG_ALERTA_VALIDACION_PROYECTADO) || 
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
				
				if(rtgInd.getIdBanca().equals(ConstantesSEFE.BANCA_INMOBILIARIAS))
				{
					ConstructoraInmobilariaDAO inmobDao = new ConstructoraInmobilariaDAOImpl();
					BalanceInmobiliario balance = null;
					
					for (int i = 0; i < xmlConsulta.size(); ++i) {
						BalanceInmobiliario balInmob = (BalanceInmobiliario) xmlConsulta.get(i);
						if(balInmob.getSeqRtg() != null && balInmob.getSeqRtg().equals(rtgInd.getIdRating()))
						{
							balance = balInmob;
							break;
						}
					}
					balance.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
					inmobDao.actualizarBi(balance);
				}
				
				if(rtgInd.getIdBanca().equals(ConstantesSEFE.BANCA_CONSTRUCTORAS))
				{
					ConstructoraInmobilariaDAO consDao = new ConstructoraInmobilariaDAOImpl();
					Soe soeModificada = null;
					
					for (int i = 0; i < xmlConsulta.size(); ++i) {
						Soe soe = (Soe) xmlConsulta.get(i);
						if(soe.getSeqRtg() != null && soe.getSeqRtg().equals(rtgInd.getIdRating()))
						{
							soeModificada = soe;
						}
					}
					
					soeModificada.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
					consDao.actualizarSoe(soeModificada);
				}
				
				if(rtgInd.getIdBanca().equals(ConstantesSEFE.BANCA_AGRICOLAS))
				{
					AgricolaDAO agricolaDao = new AgricolaDAOImpl();
					Agricola agricolaModificado = null;
					
					for (int i = 0; i < xmlConsulta.size(); ++i) {
						Agricola agricola = (Agricola) xmlConsulta.get(i);
						if(agricola.getIdRatingInd() != null && agricola.getIdRatingInd().equals(rtgInd.getIdRating()))
						{
							agricolaModificado = agricola;
						}
					}
					
					agricolaModificado.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
					agricolaDao.actualizarVaciadoAgricola(agricolaModificado);
				}
			}
			
			if(!validacionProy)
			{				
				xmlRetorno = borrarNotaProyectado(xmlConsulta, rtgInd.getIdBanca(), rtgInd);
				rtgInd.setRatingFinal(null);
				rtgInd = ratingIndDao.actualizarRatingIndividual(rtgInd);
			}
		}
		return xmlRetorno;
		
	}
	
	private RatingProyectadoCGE validarVigenciaProyectadoCGE(RatingIndividual rtgInd, String rutCliente, RatingProyectadoCGE rating)
	{		
		GestorAlertasRtgIndividual gestorAlertas = new GestorAlertasRtgIndividualImpl();
		RatingIndividualDAO ratingIndDao = new RatingIndividualDAOImpl();
		List alertas = gestorAlertas.obtenerAlertasRtgIndividualModelo(rtgInd.getIdRating(), rutCliente, rtgInd.getIdBanca());
		boolean validacionProy = true;
						
		if(rtgInd.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO) && rtgInd.getIdRatingProyectado() != null)
		{
			for (int i = 0; i < alertas.size(); ++i) {
				String mensaje = (String) alertas.get(i);
				if(mensaje.equals(ConstantesSEFE.MSG_ALERTA_VALIDACION_PROYECTADO) || mensaje.equals(ConstantesSEFE.MSG_ALERTA_VALIDACION_VERSION_VACIADOS))
				{
					RatingFinancieroDAO finanDao = new RatingFinancieroDAOImpl();
					RatingFinanciero rtgFinan = finanDao.buscarRatingPorId(rtgInd.getIdRatingProyectado());
					rtgFinan.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
					finanDao.actualizarEstadoFinanciero(rtgFinan);
					rtgInd.setIdRatingProyectado(null);
					rtgInd.setRatingProyectado(null);
					rtgInd = ratingIndDao.desasociarRatingProyectado(rtgInd.getIdCliente(), rtgInd.getIdRating());
					validacionProy = false;
					rating.agregarMensaje(mensaje);
					break;
				}
			}
			
			if(!validacionProy)
			{				
				rating.setPeriodoProy(null);
				rating.getRatingFinanciero().setResponsable(null);
				rtgInd.setRatingFinal(null);
				rtgInd = ratingIndDao.actualizarRatingIndividual(rtgInd);
			}
		}
		return rating;
	}
	
	
	private ArrayList borrarNotaProyectado(ArrayList xml, Integer banca, RatingIndividual rtgInd)
	{
		if(banca.equals(ConstantesSEFE.BANCA_INMOBILIARIAS))
		{
			for (int i = 0; i < xml.size(); ++i) {
				BalanceInmobiliario balInmob = (BalanceInmobiliario) xml.get(i);
				if(balInmob.getSeqRtg() != null && balInmob.getSeqRtg().equals(rtgInd.getIdRating()))
				{
					balInmob.setEstadoRating(null);
					balInmob.setNota(null);
					balInmob.setSeqRtg(null);
					xml.remove(i);
					xml.add(i, balInmob);
				}
			}
		}
		
		if(banca.equals(ConstantesSEFE.BANCA_CONSTRUCTORAS))
		{
			for (int i = 0; i < xml.size(); ++i) {
				Soe soe = (Soe) xml.get(i);
				if(soe.getSeqRtg() != null && soe.getSeqRtg().equals(rtgInd.getIdRating()))
				{
					soe.setEstadoRating(null);
					soe.setNota(null);
					soe.setSeqRtg(null);
					xml.remove(i);
					xml.add(i, soe);
				}
			}
		}
		
		if(banca.equals(ConstantesSEFE.BANCA_AGRICOLAS))
		{
			for (int i = 0; i < xml.size(); ++i) {
				Agricola agricola = (Agricola) xml.get(i);
				if(agricola.getIdRatingInd() != null && agricola.getIdRatingInd().equals(rtgInd.getIdRating()))
				{
					agricola.setEstado(null);
					agricola.setNota(null);
					agricola.setIdRatingInd(null);
					xml.remove(i);
					xml.add(i, agricola);
				}
			}
		}
		
		return xml;
	}
	
	private Integer verificarEstadoBalance(ArrayList xml, Integer banca, RatingIndividual rtgInd)
	{
		if(banca.equals(ConstantesSEFE.BANCA_INMOBILIARIAS))
		{
			for (int i = 0; i < xml.size(); ++i) {
				BalanceInmobiliario balInmob = (BalanceInmobiliario) xml.get(i);
				if(balInmob.getSeqRtg() != null && balInmob.getSeqRtg().equals(rtgInd.getIdRating()))
				{
					return balInmob.getIdEstado();
				}
			}
		}
		
		if(banca.equals(ConstantesSEFE.BANCA_CONSTRUCTORAS))
		{
			for (int i = 0; i < xml.size(); ++i) {
				Soe soe = (Soe) xml.get(i);
				if(soe.getSeqRtg() != null && soe.getSeqRtg().equals(rtgInd.getIdRating()))
				{
					return soe.getIdEstado();
				}
			}
		}
		
		if(banca.equals(ConstantesSEFE.BANCA_AGRICOLAS))
		{
			for (int i = 0; i < xml.size(); ++i) {
				Agricola agricola = (Agricola) xml.get(i);
				if(agricola.getIdRatingInd() != null && agricola.getIdRatingInd().equals(rtgInd.getIdRating()))
				{
					return agricola.getIdEstado();
				}
			}
		}
		return new Integer(0);
	}
	
	public Archivo descargarPlantillaRatingProyectado(Contexto ctx){
		Archivo file=null;
		ServicioRatingProyectado servicio = new ServicioRatingProyectadoImpl();
		file=servicio.descargarPlantillaRatingProyectado(ctx);
		return file;
	}
	public Archivo descargarPlantillaParametros(Contexto ctx){
		Archivo file=null;
		ServicioRatingProyectado servicio = new ServicioRatingProyectadoImpl();
		try{
			file=servicio.descargarPlantillaParametros(ctx);
		}catch (Exception e){
			log.debug("Error Obteniendo Archivo");
		}
		return file;
	}
	public XMLDataObject calcularProyeccionConstructora(XMLDataObject request){
		XMLDataObject xmlConsulta           = new XMLDataObject();
		String logOperador = request.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = request.getString(ConstantesRating.RUT_CLIENTE);
		String fechaAvance = request.getString(ConstantesRating.FECHA_AVANCE);
		Long idRatingInd = request.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		ServicioRatingProyectadoConstructora servicioRatingProyectadoSOEInm = new ServicioRatingProyectadoConstructoraImpl();
		try {
			RatingProyectadoCGE rtgProy = (RatingProyectadoCGE) servicioRatingProyectadoSOEInm.generaProyeccion(rutCliente, idRatingInd, logOperador,FormatUtil.parseDate(fechaAvance),ConstantesSEFE.BANCA_CONSTRUCTORAS);
			return respuestaCalcularProyeccion(rtgProy);
		} catch (ParseException e) {
			log.debug("No se pudo castear la fecha");
		}
		return xmlConsulta;
	}
	
	public XMLDataObject calcularProyeccionAgricola(XMLDataObject request){
		String logOperador = request.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = request.getString(ConstantesRating.RUT_CLIENTE);
		Long idRatingInd = request.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		Long idVaciadoAgricola = request.getLong(ConstantesRating.ID_VACIADO);
		ServicioRatingAgricola agricola = new ServicioRatingAgricolaImpl();
		RatingProyectadoESP rtgProy = agricola.generaProyeccionAgricola(rutCliente, idRatingInd, logOperador, idVaciadoAgricola);
		return respuestaCalcularProyeccion(rtgProy);
	}
	public XMLDataObject calcularProyeccionInmobiliaria(XMLDataObject request){
		XMLDataObject xmlConsulta           = new XMLDataObject();
		String logOperador = request.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = request.getString(ConstantesRating.RUT_CLIENTE);
		String fechaAvance = request.getString(ConstantesRating.FECHA_AVANCE);
		Long idRatingInd = request.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		ServicioRatingProyectadoInmobiliaria servicioRatingProyectadoSOEInm = new ServicioRatingProyectadoInmobiliariaImpl();
		try {
			RatingProyectadoCGE rtgProy = (RatingProyectadoCGE) servicioRatingProyectadoSOEInm.generaProyeccion(rutCliente, idRatingInd, logOperador,FormatUtil.parseDate(fechaAvance), ConstantesSEFE.BANCA_INMOBILIARIAS);
			return respuestaCalcularProyeccion(rtgProy);
		} catch (ParseException e) {
			log.debug("No se pudo castear la fecha");
		}
		return xmlConsulta;
	}
	
	private XMLDataObject respuestaCalcularProyeccion(RatingProyectadoCGE rtgProy) {
		XMLDataObject header = new XMLDataObject();
		header.put(ConstantesRating.ID_RATING_PROYECTADO, rtgProy.getIdRating());
		header.put(ConstantesRating.NOTA, rtgProy.getRatingFinanciero().getNotaFinanciera());
		header.put(ConstantesRating.MODO, rtgProy.getModoConsulta());
		return header;
	}
	private XMLDataObject respuestaCalcularProyeccion(RatingProyectadoESP rtgProy) {
		XMLDataObject header = new XMLDataObject();
		CatalogoGeneral catalogo = new CatalogoGeneralImpl();
		Clasificacion estado = catalogo.buscarClasificacionPorId(rtgProy.getRatingFinanciero().getEstado());
		Clasificacion moneda = catalogo.buscarClasificacionPorId(rtgProy.getIdMoneda());
		Clasificacion unidad = catalogo.buscarClasificacionPorId(rtgProy.getIdUnidad());
		header.put(ConstantesRating.ID_RATING_PROYECTADO, rtgProy.getRatingFinanciero().getIdRating());
		header.put(ConstantesRating.NOTA, rtgProy.getRatingFinanciero().getNotaFinanciera());
		header.put(ConstantesRating.MODO, rtgProy.getModoConsulta());
		header.put(ConstantesRating.FECHA, FormatUtil.formatDateRating(rtgProy.getFechaBalance()));
		header.put(ConstantesRating.TEMPORADA, rtgProy.getTemporada());
		header.put(ConstantesRating.ESTADO,estado.getDescripcion());
		header.put(ConstantesRating.RESPONSABLE, rtgProy.getRatingFinanciero().getResponsable());
		header.put(ConstantesRating.DESCRIPCION, rtgProy.getDescripcion());
		header.put(ConstantesRating.UNIDAD, unidad != null ? unidad.getDescripcion(): "");
		header.put(ConstantesRating.MONEDA, moneda != null ? moneda.getDescripcion(): "");
		header.put(ConstantesRating.ID_VACIADO, rtgProy.getIdVaciadoAgricola());
		return header;
	}

	
	public XMLDataObject confirmarProyeccionInmobiliaria(XMLDataObject request){
		XMLDataObject xmlConsulta           = new XMLDataObject();
		String logOperador = request.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = request.getString(ConstantesRating.RUT_CLIENTE);
		String fechaAvance = request.getString(ConstantesRating.FECHA_AVANCE);
		Long idRatingInd = request.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		Long idRatingProy = request.getLong(ConstantesRating.ID_RATING_PROYECTADO);
		ServicioRatingProyectadoInmobiliaria servicioRatingProyectadoSOEInm = new ServicioRatingProyectadoInmobiliariaImpl();
		RatingProyectado rtgProy = servicioRatingProyectadoSOEInm.confirmarProyeccion(rutCliente, logOperador , idRatingInd, idRatingProy, fechaAvance);
		return xmlConsulta;
	}
	
	public XMLDataObject confirmarProyeccionConstructora(XMLDataObject request){
		String logOperador = request.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = request.getString(ConstantesRating.RUT_CLIENTE);
		String fechaAvance = request.getString(ConstantesRating.FECHA_AVANCE);
		Long idRatingInd = request.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		Long idRatingProy = request.getLong(ConstantesRating.ID_RATING_PROYECTADO);
		ServicioRatingProyectadoConstructora servicioRatingProyectadoSOEInm = new ServicioRatingProyectadoConstructoraImpl();
		RatingProyectadoESP rtgProy = servicioRatingProyectadoSOEInm.confirmarProyeccion(rutCliente, logOperador , idRatingInd, idRatingProy, fechaAvance);
		return respuestaCalcularProyeccion( rtgProy);
	}
	
	public XMLDataObject confirmarProyeccionAgricola(XMLDataObject request){
		String logOperador = request.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = request.getString(ConstantesRating.RUT_CLIENTE);
		Long idAgricola = request.getLong(ConstantesRating.ID_AGRICOLA);
		Long idRatingInd = request.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		Long idRatingProy = request.getLong(ConstantesRating.ID_RATING_PROYECTADO);
		ServicioRatingAgricola servicioAricola = new ServicioRatingAgricolaImpl();
		RatingProyectadoESP rtgProy =servicioAricola.confirmarProyeccion(rutCliente, logOperador , idRatingInd, idRatingProy, idAgricola);
		return respuestaCalcularProyeccion(rtgProy);
	}
	/*
	 * Crea una lista de cuentas a contar del xml del mensaje de entrada
	 */
	private List crearListaCuentas(XMLDataList xmlCuentas) {
		List cuentas = new ArrayList();
		
		int n = 0;
		if (xmlCuentas != null) {
			n = xmlCuentas.size();
		}

		for (int i = 0; i < n; ++i) {
			XMLDataObject xmlCta = (XMLDataObject) xmlCuentas.get(i);
			Cuenta cta = new Cuenta();
			cta.setMonto(xmlCta.getDouble(ConstantesRating.VALUE));
			cta.setNumCta(xmlCta.getLong(ConstantesRating.ID_CTA));
			cta.setCodigoCuenta(xmlCta.getString(ConstantesRating.CUENTA));
			cuentas.add(cta);
		}
		
		return cuentas;
	}

	
	/*
	 * Crea el xml de encabezado de la respuesta
	 */
	private XMLDataObject crearXMLDetalle(RatingProyectadoCGE proyeccion) {
		GestorRatingProyectado gestorProy 	= new GestorRatingProyectadoImpl();
		XMLDataObject detalle = new XMLDataObject();
		
		if (proyeccion == null || proyeccion.getRatingFinanciero() == null || proyeccion.getPeriodoProy() == null) {
			return detalle;
		}
		
		if (proyeccion.getPeriodo0() == null) {
			return detalle;
		}
		Vaciado cierre = proyeccion.getPeriodo0().getVaciado(); //getVaciadoCierreAnual();
		
		// no existe cierre anual para proyectar
		if (cierre == null) {
			return detalle;
		}
		
		CatalogoGeneral catalogo = new CatalogoGeneralImpl();
		//Clasificacion cl = null;
		
		Integer moneda = cierre.getIdMoneda();
		Clasificacion clMoneda = catalogo.buscarClasificacionPorId(moneda);
		String monedaDestino = clMoneda.getNombreCorto();
		String codMonedaDestino = clMoneda.getCodigo();
		detalle.put(ConstantesRating.MONEDA, monedaDestino);
		
		Integer unidad = cierre.getUnidMedida();
		Clasificacion clUnidad = catalogo.buscarClasificacionPorId(unidad);
		detalle.put(ConstantesRating.UNIDAD_DESTINO, clUnidad.getNombre());
		
		XMLDataList periodos = new XMLDataList();
		detalle.put(ConstantesRating.LST_PERIODOS, periodos);
		
		XMLDataObject periodoP = crearXMLPeriodo(proyeccion.getPeriodoProy(), codMonedaDestino);
		periodos.add(periodoP);
		
		XMLDataObject periodo0 = crearXMLPeriodo(proyeccion.getPeriodo0(), codMonedaDestino);
		periodos.add(periodo0);
		
		XMLDataObject periodo1 = crearXMLPeriodo(proyeccion.getPeriodo1(), codMonedaDestino);
		periodos.add(periodo1);
		
		if (proyeccion.getPeriodo2() != null && proyeccion.getPeriodo2().getVaciado() != null) {
			XMLDataObject periodo2 = crearXMLPeriodo(proyeccion.getPeriodo2(), codMonedaDestino);
			periodos.add(periodo2);
		}
		
		XMLDataList listaProyecciones = crearXMLCuentas(proyeccion);
		detalle.put(ConstantesRating.LST_PROYECCIONES, listaProyecciones);
		//0 tiene parcial: 1 no tiene parcial
		Integer tieneParcial = new Integer(0);
		if (gestorProy.esCierreAnual(proyeccion.getPeriodo0().getVaciado())) {
			 tieneParcial = new Integer(1);
		}
		detalle.put(ConstantesRating.FLAG_TIENE_PARCIAL, tieneParcial);
		return detalle;
	}

	/*
	 * Crea la lista de las cuentas de proyeccion agrupadas por tipo de indicador
	 */
	private XMLDataList crearXMLCuentas(RatingProyectadoCGE proyeccion) {
		XMLDataList listaProyecciones = new XMLDataList();
		
		// genera un mapa con los flgas indexado por codigo cuenta
		Map flags = new HashMap();
		for (int i = 0; i < proyeccion.getFlags().size(); ++i) {
			FlagCuentaProyeccion flag = (FlagCuentaProyeccion) proyeccion.getFlags().get(i);
			flags.put(flag.getCodigoCuenta(), flag);
		}
		
		Map mapP = crearMapaCuentas(proyeccion.getPeriodoProy().getCuentas());
		Map map0 = crearMapaCuentas(proyeccion.getPeriodo0().getCuentas());
		Map map1 = crearMapaCuentas(proyeccion.getPeriodo1().getCuentas());
		Map map2 = new HashMap();
		
		if (proyeccion.getPeriodo2() != null) {
			map2 = crearMapaCuentas(proyeccion.getPeriodo2().getCuentas());
		}
		
		Integer ID_CTAS_RATING = ConfigManager.getValueAsInteger(ConstantesSEFE.TIPO_CUENTA_RATING);
		CatalogoGeneral catalogo = new CatalogoGeneralImpl();
		Collection ctasRating = catalogo.buscarClasificacionesPorCategoria(ID_CTAS_RATING);
		
		// por cada identificador se crea una proyeccion
		Iterator it = ctasRating.iterator();
		Clasificacion clasif 		= null;
		while (it.hasNext()) {
			clasif = (Clasificacion) it.next();
			XMLDataObject xmlProy = new XMLDataObject();
			xmlProy.put(ConstantesRating.TIPO_CUENTA_INDICADOR, clasif.getDescripcion());
			
			String titulo = null;
			List listaXML = crearProyeccionesGrupo(clasif.getIdClasif(), mapP, map0, map1, map2, flags, titulo);
			XMLDataList xmlProyecciones = new XMLDataList();
			for (int i = 0; i < listaXML.size(); ++i) {
				xmlProyecciones.add((XMLObject) listaXML.get(i));
			}
			
			// se pone la lista de proyecciones en la proyeccion del tipo de cuenta
			xmlProy.put(ConstantesRating.LST_PROYECCIONES, xmlProyecciones);
			
			// y todo se agrega a la lista de resultado
			if (xmlProyecciones.size() > 0) {
				listaProyecciones.add(xmlProy);
			}
		}
		
		return listaProyecciones;
	}

	
		
	/*
	 * Crea la lista de proyecciones para cada grupo
	 */
	private List crearProyeccionesGrupo(Integer idClasif, Map mapP, Map map0, Map map1, Map map2, Map flags, String titulo) {
		List proyecciones = new ArrayList();
		final String CTA_CORRECCION_IPC = "P66";
		
		// se obtienen las cuentas del tipo idClasif ordenadas
		List ctasOrdenadas = getListaCuentasOrdenadas(idClasif, mapP);
		
		// primero se crea la lista de cuentas del tipo de la clasificacion
		for (int i = 0; i < ctasOrdenadas.size(); i++) {
			Cuenta cta = (Cuenta) ctasOrdenadas.get(i);
			//Long key = cta.getNumCta();
			String key = cta.getCodigoCuenta();

			XMLDataObject data = new XMLDataObject();
			Cuenta cp = (Cuenta) mapP.get(key);
			Double vp = aproximarMonto(cp);
			
			Cuenta c0 = (Cuenta) map0.get(key);
			Double v0 = aproximarMonto(c0);
			
			Cuenta c1 = (Cuenta) map1.get(key);
			Double v1 = aproximarMonto(c1);
			
			Cuenta c2 = (Cuenta) map2.get(key);
			if (c2 != null) {
				Double v2 = aproximarMonto(c2);
				
				// la correccion IPC no se pinta en el penultimo cierre
				if (!CTA_CORRECCION_IPC.equals(c2.getCodigoCuenta())) {
					data.put(ConstantesRating.VALOR_3, v2);
				} else {
					data.put(ConstantesRating.VALOR_2, v2);
				}
			}
			
			data.put(ConstantesRating.ID_CTA, cp.getNumCta());
			data.put(ConstantesRating.TITULO, titulo);
			data.put(ConstantesRating.DESCRIPCION, cp.getDescripCta());
			
			// la correccion IPC se pinta en el ultimo cierre
			if (!CTA_CORRECCION_IPC.equals(c1.getCodigoCuenta())) {
				data.put(ConstantesRating.VALOR_2, v1);
			} else {
				data.put(ConstantesRating.VALOR_1, v1);
			}
			
			data.put(ConstantesRating.VALOR_1, v0);
			data.put(ConstantesRating.VALOR_P, vp);
			data.put(ConstantesRating.CUENTA, cp.getCodigoCuenta());
			data.put(ConstantesRating.UNIDAD_MEDIDA, cp.getIdUnidad());
			if (flags.containsKey(cp.getCodigoCuenta())) {
				FlagCuentaProyeccion flag = (FlagCuentaProyeccion) flags.get(cp.getCodigoCuenta());
				
				// 1 es requerido ; 0 es opcional
				if (ConstantesSEFE.VALOR_1.equals(flag.getFlagOpcionalidad())) {
					data.put(ConstantesRating.FLAG_REQUERIDO, ConstantesSEFE.OPCION_SI);
				} else {
					data.put(ConstantesRating.FLAG_REQUERIDO, ConstantesSEFE.OPCION_NO);
				}
				
				if (ConstantesSEFE.VALOR_1.equals(flag.getFlagDefault())) {
					data.put(ConstantesRating.FLAG_DEFAULT, ConstantesSEFE.OPCION_SI);
				} else {
					data.put(ConstantesRating.FLAG_DEFAULT, ConstantesSEFE.OPCION_NO);
				}
			}
			
			proyecciones.add(data);
		}
		
		CatalogoGeneral catalogo 	= new CatalogoGeneralImpl();
		Collection subGrupos 		= catalogo.buscarClasificacionesPorCategoria(idClasif);
		Iterator itGrupos = subGrupos.iterator();
		while (itGrupos.hasNext()) {
			Clasificacion cl = (Clasificacion) itGrupos.next();
			if (log.isInfoEnabled()) {
				log.info("Procesando sub grupo " + cl.getDescripcion());
			}
			List proyeccionesGrupo = crearProyeccionesGrupo(cl.getIdClasif(), mapP, map0, map1, map2, flags, cl.getDescripcion());
			proyecciones.addAll(proyeccionesGrupo);
		}
		
		return proyecciones;
	}
	
	private Double aproximarMonto(Cuenta cp) {
		if (cp != null ) {
			return FormatUtil.aproximarMonto(cp.getMonto(),cp.getIdUnidad());
			
		}
		return null;
	}


	private List getListaCuentasOrdenadas(Integer idClasif, Map mapCtas) {
		ArrayList lstCtas = new ArrayList();

		Iterator itCtas = mapCtas.values().iterator();
		Cuenta cta;
		Integer tipoIndicador;
		while (itCtas.hasNext()) {
			cta = (Cuenta) itCtas.next();
			tipoIndicador = cta.getTipoIndicador();
			if (idClasif.equals(tipoIndicador)) {
				lstCtas.add(cta);
			}
		}
		// La implementacion del comparador se externaliza, en caso que se quiera cambiar
		Comparator comparador = (Comparator) ConfigManager.getInstanceOf(ConstantesSEFE.IMPL_COMPARADOR_CUENTAS_PROYECTADAS);
		Collections.sort(lstCtas, comparador);
		return lstCtas;
	}

	/*
	 * Crea un mapa de las cuentas indexados por id de cuenta
	 */
	private Map crearMapaCuentas(List cuentas) {
		Map map = new HashMap();
		
		if (cuentas == null) {
			return map;
		}
		
		for (Iterator iterator = cuentas.iterator(); iterator.hasNext();) {
			Cuenta cta = (Cuenta) iterator.next();
			//map.put(cta.getNumCta(), cta);
			map.put(cta.getCodigoCuenta(), cta);
		}
		
		return map;
	}


	/*
	 * Crear la representacion XML de cada uno de los periodos que se muestran 
	 * en el encabezado
	 */
	private XMLDataObject crearXMLPeriodo(PeriodoRating periodo, String codMonedaDestino) {
		XMLDataObject xmlPeriodo = new XMLDataObject();
		Vaciado vac = periodo.getVaciado();
		
		CatalogoGeneral catalogo = new CatalogoGeneralImpl();
		
		xmlPeriodo.put(ConstantesRating.PLAN_DE_CUENTAS, catalogo.buscarClasificacionPorId(vac.getIdNombrePlanCtas()).getNombre());
		xmlPeriodo.put(ConstantesRating.ESTADO, catalogo.buscarClasificacionPorId(vac.getIdEstado()).getNombre());
		
		Clasificacion moneda = catalogo.buscarClasificacionPorId(vac.getIdMoneda());
		xmlPeriodo.put(ConstantesRating.MONEDA, moneda.getNombreCorto());
		xmlPeriodo.put(ConstantesRating.UNIDAD_ORIGEN, catalogo.buscarClasificacionPorId(vac.getUnidMedida()).getNombre());
		//DGJO; 25062014 inicio ; 
			xmlPeriodo.put(ConstantesRating.CANTIDAD_PERIODOS, vac.getMesesPer());
		//DGJO; 25062014 FIN ; 
		ConsultaServicios srvConsulta = new ConsultaServiciosImplCache();
		ServicioVaciados servicioVaciados = new ServicioVaciadosImpl();
		TipoCambio tc = new TipoCambio();
		tc.setValorObservado(new Double("1.00"));
		try {
			if (!moneda.getNombreCorto().equalsIgnoreCase(MONEDA_CLP)) {
			tc = srvConsulta.consultaTipoCambio(servicioVaciados.buscarDiaHabilSiguiente(vac.getPeriodo()), moneda.getCodigo());
			}
		} catch (Exception ex) {
			if (log.isDebugEnabled()) {
    			log.debug("No fue posible obtener el tipo de cambio");
    		}
		}
		
		xmlPeriodo.put(ConstantesRating.TIPO_CAMBIO_ORIGEN, tc.getValorObservado());
		xmlPeriodo.put(ConstantesRating.FECHA_PERIODO, FormatUtil.formatDateRptFF(vac.getPeriodo()));
		
		try {
			//String clp = ConfigManager.getValueAsString(ConstantesSEFE.KEY_CODIGO_MONEDA_CONSULTA_VALOR_MONEDA + ConstantesSEFE.PUNTO + ConstantesSEFE.CLASIF_ID_TPO_MONEDA_CLP);
			if (!codMonedaDestino.equalsIgnoreCase(MONEDA_CLP)) {
				tc = srvConsulta.consultaTipoCambio(servicioVaciados.buscarDiaHabilSiguiente(vac.getPeriodo()), codMonedaDestino);
			}
		} catch (Exception ex) {
			 log.error("Hay que invocar al servicio de consulta moneda!!!!", ex);
		}
		xmlPeriodo.put(ConstantesRating.TIPO_CAMBIO_DESTINO, tc.getValorObservado());
		return xmlPeriodo;
	}


	/*
	 * Crear el xml de detalle de la respuesta
	 */
	private XMLDataObject crearXMLHeader(RatingProyectadoCGE rating, String modo) {
		XMLDataObject header = new XMLDataObject();
		header.put(ConstantesRating.MODO, modo);
		header.put(ConstantesRating.ID_RATING_PROYECTADO, rating.getRatingFinanciero().getIdRating());
		
		Integer tipoVac 		= null;
		Integer flagAjustado 	= null;
		Integer planCuenta 		= null;
		Double nota				= null;
		if (rating.getPeriodoProy() != null) {
			
			header.put(ConstantesRating.FECHA_RATING, FormatUtil.formatDateRating(rating.getPeriodoProy().getPeriodo()));
			nota = rating.getRatingFinanciero().getNotaFinanciera();
			header.put(ConstantesRating.NOTA, nota);
			tipoVac			= rating.getPeriodoProy().getVaciado().getIdTpoVaciado();
			flagAjustado 	= rating.getPeriodoProy().getVaciado().getAjustadoFlg();
			planCuenta 		= rating.getPeriodoProy().getVaciado().getIdNombrePlanCtas();
		}
		
		CatalogoGeneral catalogo = new CatalogoGeneralImpl();
		Clasificacion cl = null;
		if (tipoVac != null) {
			cl = catalogo.buscarClasificacionPorId(tipoVac);
			header.put(ConstantesRating.TIPO_VACIADO, cl.getNombre());
		}
		
		if (rating.getPeriodoProy() != null) {
			if (flagAjustado != null && flagAjustado.equals(ConstantesSEFE.FLAG_VACIADO_AJUSTADO)) {
				header.put(ConstantesRating.FLAG_AJUSTADO, ConstantesSEFE.OPCION_SI);
			} else {
				header.put(ConstantesRating.FLAG_AJUSTADO, ConstantesSEFE.OPCION_NO);
			}
		}
		
		if (planCuenta != null) {
			cl = catalogo.buscarClasificacionPorId(planCuenta);
			header.put(ConstantesRating.PLAN_DE_CUENTAS, cl.getNombre());
		}
		
		XMLDataList msgs = new XMLDataList();
		
		List warnings = rating.getWarnings();
		if (warnings != null && !warnings.isEmpty()) {
			for (int i = 0; i < rating.getWarnings().size(); ++i) {
				XMLDataObject msg = new XMLDataObject();
				msg.put(ConstantesRating.MENSAJE, rating.getWarnings().get(i));
				msgs.add(msg);
			}
		}
		
		header.put(ConstantesRating.LST_MENSAJES, msgs);
		return header;
	}
	
	/**
	 * método para eliminar balances
	 * @param request solicitud del clienta
	 * @return respuesta del servidor
	 */
	public XMLDataObject eliminarBalance(Contexto ctx, XMLDataObject request){
		XMLDataObject response = new XMLDataObject();
		Boolean result = null;
		String rutCliente = request.getString(ConstantesSEFE.IMD_RUT_CLIENTE);
		String fechaAvance = request.getString(ConstantesRating.FECHA_AVANCE);
		if (ctx.getIdPlantilla().equals(ConstantesSEFE.BANCA_INMOBILIARIAS.toString())){
			ServicioRatingProyectadoInmobiliaria servicio	= new ServicioRatingProyectadoInmobiliariaImpl();
			result = servicio.eliminarBalance(rutCliente, fechaAvance);
		} else if (ctx.getIdPlantilla().equals(ConstantesSEFE.BANCA_CONSTRUCTORAS.toString())){
			ServicioRatingProyectadoConstructora servicio	= new ServicioRatingProyectadoConstructoraImpl();
			result = servicio.eliminarCuadroObra(rutCliente, fechaAvance);
		}
		response.put(ConstantesRating.FLAG_BORRADO_BALANCE, result);
		return response;
	}
}
