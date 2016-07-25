package com.bch.sefe.rating;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.CatalogoGeneral;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.CatalogoGeneralImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.comun.util.XMLData;
import com.bch.sefe.comun.util.XMLDataList;
import com.bch.sefe.comun.util.XMLDataObject;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.rating.dao.RatingFinancieroDAO;
import com.bch.sefe.rating.dao.RatingIndividualDAO;
import com.bch.sefe.rating.dao.RatingNegocioDAO;
import com.bch.sefe.rating.dao.impl.RatingFinancieroDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingIndividualDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingNegocioDAOImpl;
import com.bch.sefe.rating.impl.ServicioRatingFinancieroImpl;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.CatalogoVaciados;
import com.bch.sefe.vaciados.impl.CatalogoVaciadosImpl;
import com.bch.sefe.vaciados.vo.Vaciado;

public class AdaptadorRatingFinanciero {
	private ServicioRatingFinanciero servicioRtgFinanciero = new ServicioRatingFinancieroImpl();

	public XMLData generarRatingFinanciero(XMLData xmlData) {
		XMLDataObject request = (XMLDataObject) xmlData;

		Long idRatingIndividual = request.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		String loginUsuario = request.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = request.getString(ConstantesRating.RUT_CLIENTE);

		// Se obtiene el rating financiero
		Map infoRatingFinanciero = servicioRtgFinanciero.generarRating(idRatingIndividual, rutCliente, loginUsuario);
		RatingFinanciero rtgFinanciero = (RatingFinanciero) infoRatingFinanciero.get(ServicioRatingFinanciero.PARAM_RATING_FINANCIERO);
		// Se obtiene el listado de vaciados para rating
		List lstVaciados = (List) infoRatingFinanciero.get(ServicioRatingFinanciero.PARAM_LISTADO_VACIADOS);
		
		GestorRatingIndividual gestor = new GestorRatingIndividualImpl();
		ServicioClientes servicioCliente = new ServicioClientesImpl();

		Cliente cliente = servicioCliente.obtenerClientePorRut(rutCliente);
		Long idCliente = Long.valueOf(cliente.getClienteId());
		
		RatingIndividual ratingInd = gestor.buscarRatingIndividual(idCliente, idRatingIndividual);
		
		Collection alertas = (Collection)infoRatingFinanciero.get(ServicioRatingFinanciero.PARAM_ALERTAS);
		
		if(alertas != null && !alertas.isEmpty() && ratingInd.getIdRatingFinanciero() != null && ratingInd.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO))
		{
			// dejar en curso rating financiero
			RatingIndividualDAO ratingIndDao = new RatingIndividualDAOImpl();
			RatingFinancieroDAO finanDao = new RatingFinancieroDAOImpl();
			RatingFinanciero rtgFinan = finanDao.buscarRatingPorId(ratingInd.getIdRatingFinanciero());
			rtgFinan.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
			rtgFinan.setNotaFinanciera(null);
			finanDao.actualizarEstadoFinanciero(rtgFinan);
			ratingInd.setIdRatingFinanciero(null);
			ratingInd.setRatingFinanciero(null);
			ratingIndDao.desasociarRatingFinanciero(ratingInd.getIdCliente(), ratingInd.getIdRating());
			
			if(alertas.contains(MessageManager.getMessage(ConstantesSEFE.MSG2_INTEGRIDAD_RATING_FINANCIERO)) && ratingInd.getIdRatingNegocio() != null && 
					(ratingInd.getIdBanca().equals(ConstantesSEFE.BANCA_PYME) || ratingInd.getIdBanca().equals(ConstantesSEFE.BANCA_CONSTRUCTORAS) || ratingInd.getIdBanca().equals(ConstantesSEFE.BANCA_INMOBILIARIAS)))
			{
				// dejar en curso rating negocio
				RatingNegocioDAO negocioDao = new RatingNegocioDAOImpl();
				RatingNegocio rtgNeg = negocioDao.buscarRatingNegocioPorId(ratingInd.getIdRatingNegocio());
				rtgNeg.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
				negocioDao.actualizarEstadoRating(rtgNeg);
				ratingInd.setIdRatingNegocio(null);
				ratingInd.setRatingNegocio(null);
				ratingIndDao.desasociarRatingNegocio(ratingInd.getIdCliente(), ratingInd.getIdRating());
			}
			
			ratingInd.setRatingFinal(null);
			ratingIndDao.actualizarRatingIndividual(ratingInd);
		}

		return getRespuestaGenerarRatingFinancieroGenerar(idRatingIndividual, (Integer)infoRatingFinanciero.get(ConstantesRating.ID_ESTADO), rtgFinanciero, lstVaciados, alertas, ratingInd.getIdBanca());
	}

	public XMLData confirmarRatingFinanciero(XMLData xmlData) {
		XMLDataObject request = (XMLDataObject) xmlData;

		Long idRatingIndividual = request.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		String loginUsuario = request.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = request.getString(ConstantesRating.RUT_CLIENTE);
		Long idVaciado = request.getLong(ConstantesRating.ID_VACIADO);

		// Se obtiene el rating financiero
		Map infoRatingFinanciero = servicioRtgFinanciero.confirmarRating(rutCliente, idRatingIndividual, idVaciado, loginUsuario);
		RatingFinanciero rtgFinanciero = (RatingFinanciero) infoRatingFinanciero.get(ServicioRatingFinanciero.PARAM_RATING_FINANCIERO);
		// Se obtiene el listado de vaciados para rating
		List lstVaciados = (List) infoRatingFinanciero.get(ServicioRatingFinanciero.PARAM_LISTADO_VACIADOS);
				
		return getRespuestaGenerarRatingFinanciero(idRatingIndividual, (Integer)infoRatingFinanciero.get(ConstantesRating.ID_ESTADO), rtgFinanciero, lstVaciados, null);
	}
	
	public XMLData validarRating(XMLData xmlData) {
		XMLDataObject request = (XMLDataObject) xmlData;

		Long idRatingIndividual = request.getLong(ConstantesRating.ID_RATING_INDIVIDUAL);
		String loginUsuario = request.getString(ConstantesRating.LOG_OPERADOR);
		String rutCliente = request.getString(ConstantesRating.RUT_CLIENTE);
		Long idVaciado = request.getLong(ConstantesRating.ID_VACIADO);

		// Se obtiene el rating financiero
		Map infoRatingFinanciero = servicioRtgFinanciero.validarRating(rutCliente, idRatingIndividual, idVaciado, loginUsuario);
		
		RatingFinanciero rtgFinanciero = (RatingFinanciero) infoRatingFinanciero.get(ServicioRatingFinanciero.PARAM_RATING_FINANCIERO);
		// Se obtiene el listado de vaciados para rating
		List lstVaciados = (List) infoRatingFinanciero.get(ServicioRatingFinanciero.PARAM_LISTADO_VACIADOS);

		return getRespuestaGenerarRatingFinanciero(idRatingIndividual, (Integer)infoRatingFinanciero.get(ConstantesRating.ID_ESTADO), rtgFinanciero, lstVaciados, null);
	}

	/*
	 * Genera el XMLDataObject de salida con toda la data requerida. La estructura generada corresponde a:
	 */
	private XMLDataObject getRespuestaGenerarRatingFinancieroGenerar(Long idRatingIndividual, Integer estadoRatingIndividual, RatingFinanciero rtgFinanciero, List lstVaciados, Collection alertas, Integer idBanca) {
		XMLDataObject response = new XMLDataObject();
		CatalogoVaciados catalogoVac = new CatalogoVaciadosImpl();

		response.put(ConstantesRating.ID_RATING_INDIVIDUAL, idRatingIndividual);
		response.put(ConstantesRating.ID_ESTADO, estadoRatingIndividual);
		response.put(ConstantesRating.ID_VACIADO, null);

		// Se crea respuesta con informacion del rating financiero, si es que existe
		Vaciado vaciadoRating =  null;
		if (rtgFinanciero != null) {
			response.put(ConstantesRating.ID_VACIADO, rtgFinanciero.getIdVaciado0());
			
			XMLDataObject infoRating = new XMLDataObject();

			// Se obtiene el vaciado utilizado para el rating financiero para obtener el tipo de vaciado
			vaciadoRating = catalogoVac.buscarDatosGeneral(rtgFinanciero.getIdVaciado0());

			infoRating.put(ConstantesRating.RATING_FINANCIERO, rtgFinanciero.getNotaFinanciera());
			
			if (rtgFinanciero.getFechaBalance() != null)
				infoRating.put(ConstantesRating.FECHA, FormatUtil.formatDate(rtgFinanciero.getFechaBalance()));
			
			infoRating.put(ConstantesRating.PLAN_DE_CUENTAS, getNombreClasificacion(vaciadoRating.getIdNombrePlanCtas()));
			infoRating.put(ConstantesRating.TIPO_VACIADO, getNombreClasificacion(vaciadoRating.getIdTpoVaciado()));
			infoRating.put(ConstantesRating.RESPONSABLE, rtgFinanciero.getResponsable());
			infoRating.put(ConstantesRating.VACIADO_AJUSTADO, (vaciadoRating.getAjustadoFlg().equals(ConstantesSEFE.FLAG_VACIADO_NO_AJUSTADO)) ? "NO" : "SI");
			infoRating.put(ConstantesRating.ID_RATING_FINANCIERO, rtgFinanciero.getIdRating());

			response.put(ConstantesRating.INFO_RATING_FINANCIERO, infoRating);
		}

		// Se crea el objeto que contiene las alertas
		if (alertas != null) {
			XMLDataList xmlAlertas = new XMLDataList();

			Iterator itAlertas = alertas.iterator();
			for (; itAlertas.hasNext();) {
				XMLDataObject mensaje = new XMLDataObject();
				//mensaje.put(ServicioRatingFinanciero.PARAM_ALERTAS+String.valueOf(i), (String) itAlertas.next());
				mensaje.put(ConstantesRating.MENSAJE, (String) itAlertas.next());
				xmlAlertas.add(mensaje);
			}

			response.put(ConstantesRating.ALERTAS, xmlAlertas);
		}

		// Se crea el objecto que contiene los vaciados
		if (lstVaciados != null) {
			XMLDataList xmlLstVaciados = new XMLDataList();
			Vaciado vaciado = null;
			XMLDataObject xmlVaciado = null;

			for (int i = 0; i < lstVaciados.size(); i++) {
				xmlVaciado = new XMLDataObject();

				vaciado = (Vaciado) lstVaciados.get(i);
				if (vaciadoRating == null) {
					vaciadoRating = vaciado;
				}

				if (rtgFinanciero != null && rtgFinanciero.getNotaFinanciera() != null) {
					if (vaciadoRating.getIdVaciado().intValue() == vaciado.getIdVaciado().intValue()) {
						xmlVaciado.put(ConstantesRating.SELECCIONADO, Boolean.TRUE.toString());
					}
				}
				if(idBanca.intValue() == ConstantesSEFE.BANCA_PYME.intValue())
				{
					xmlVaciado.put(ConstantesRating.PARCIAL_SOPORTADO, new Boolean(ConfigDBManager.getValueAsString(ConstantesSEFE.RTG_FINANCIERO_CIERRE_PARCIAL_SOPORTADO, idBanca)));
				}
				xmlVaciado.put(ConstantesRating.ID_VACIADO, vaciado.getIdVaciado());
				xmlVaciado.put(ConstantesRating.FECHA_BALANCE, FormatUtil.formatDate(vaciado.getPeriodo()));
				xmlVaciado.put(ConstantesRating.TIPO_VACIADO, getNombreClasificacion(vaciado.getIdTpoVaciado()));
				xmlVaciado.put(ConstantesRating.VACIADO_AJUSTADO, (vaciado.getAjustadoFlg().equals(ConstantesSEFE.FLAG_VACIADO_NO_AJUSTADO)) ? "NO" : "SI");
				xmlVaciado.put(ConstantesRating.TIPO_BALANCE, getNombreClasificacion(vaciado.getIdTipoBalance()));
				xmlVaciado.put(ConstantesRating.ESTADO_VACIADO, getNombreClasificacion(vaciado.getIdEstado()));
				xmlVaciado.put(ConstantesRating.ID_ESTADO_VACIADO, vaciado.getIdEstado());
				xmlVaciado.put(ConstantesRating.PLAN_DE_CUENTAS, getNombreClasificacion(vaciado.getIdNombrePlanCtas()));
				xmlVaciado.put(ConstantesRating.MONENA, getNombreClasificacion(vaciado.getIdMoneda()));
				xmlVaciado.put(ConstantesRating.MESES_PER, vaciado.getMesesPer());
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.setTime(vaciado.getPeriodo());
				xmlVaciado.put(ConstantesRating.MES_PERIODO_BALANCE, new Integer((cal.get(Calendar.MONTH) + 1)));

				xmlLstVaciados.add(xmlVaciado);
			}

			response.put(ConstantesRating.LISTA_VACIADOS, xmlLstVaciados);
		}

		return response;
	}
	
	/*
	 * Genera el XMLDataObject de salida con toda la data requerida. La estructura generada corresponde a:
	 */
	private XMLDataObject getRespuestaGenerarRatingFinanciero(Long idRatingIndividual, Integer estadoRatingIndividual, RatingFinanciero rtgFinanciero, List lstVaciados, Collection alertas) {
		XMLDataObject response = new XMLDataObject();
		CatalogoVaciados catalogoVac = new CatalogoVaciadosImpl();

		response.put(ConstantesRating.ID_RATING_INDIVIDUAL, idRatingIndividual);
		response.put(ConstantesRating.ID_ESTADO, estadoRatingIndividual);
		response.put(ConstantesRating.ID_VACIADO, null);

		// Se crea respuesta con informacion del rating financiero, si es que existe
		Vaciado vaciadoRating =  null;
		if (rtgFinanciero != null) {
			response.put(ConstantesRating.ID_VACIADO, rtgFinanciero.getIdVaciado0());
			
			XMLDataObject infoRating = new XMLDataObject();

			// Se obtiene el vaciado utilizado para el rating financiero para obtener el tipo de vaciado
			vaciadoRating = catalogoVac.buscarDatosGeneral(rtgFinanciero.getIdVaciado0());

			infoRating.put(ConstantesRating.RATING_FINANCIERO, rtgFinanciero.getNotaFinanciera());
			
			if (rtgFinanciero.getFechaBalance() != null)
				infoRating.put(ConstantesRating.FECHA, FormatUtil.formatDate(rtgFinanciero.getFechaBalance()));
			
			infoRating.put(ConstantesRating.PLAN_DE_CUENTAS, getNombreClasificacion(vaciadoRating.getIdNombrePlanCtas()));
			infoRating.put(ConstantesRating.TIPO_VACIADO, getNombreClasificacion(vaciadoRating.getIdTpoVaciado()));
			infoRating.put(ConstantesRating.RESPONSABLE, rtgFinanciero.getResponsable());
			infoRating.put(ConstantesRating.VACIADO_AJUSTADO, (vaciadoRating.getAjustadoFlg().equals(ConstantesSEFE.FLAG_VACIADO_NO_AJUSTADO)) ? "NO" : "SI");
			infoRating.put(ConstantesRating.ID_RATING_FINANCIERO, rtgFinanciero.getIdRating());

			response.put(ConstantesRating.INFO_RATING_FINANCIERO, infoRating);
		}

		// Se crea el objeto que contiene las alertas
		if (alertas != null) {
			XMLDataList xmlAlertas = new XMLDataList();

			Iterator itAlertas = alertas.iterator();
			for (; itAlertas.hasNext();) {
				XMLDataObject mensaje = new XMLDataObject();
				//mensaje.put(ServicioRatingFinanciero.PARAM_ALERTAS+String.valueOf(i), (String) itAlertas.next());
				mensaje.put(ConstantesRating.MENSAJE, (String) itAlertas.next());
				xmlAlertas.add(mensaje);
			}

			response.put(ConstantesRating.ALERTAS, xmlAlertas);
		}

		// Se crea el objecto que contiene los vaciados
		if (lstVaciados != null) {
			XMLDataList xmlLstVaciados = new XMLDataList();
			Vaciado vaciado = null;
			XMLDataObject xmlVaciado = null;

			for (int i = 0; i < lstVaciados.size(); i++) {
				xmlVaciado = new XMLDataObject();

				vaciado = (Vaciado) lstVaciados.get(i);
				if (vaciadoRating == null) {
					vaciadoRating = vaciado;
				}

				if (rtgFinanciero != null && rtgFinanciero.getNotaFinanciera() != null) {
					if (vaciadoRating.getIdVaciado().intValue() == vaciado.getIdVaciado().intValue()) {
						xmlVaciado.put(ConstantesRating.SELECCIONADO, Boolean.TRUE.toString());
					}
				}
				
				xmlVaciado.put(ConstantesRating.ID_VACIADO, vaciado.getIdVaciado());
				xmlVaciado.put(ConstantesRating.FECHA_BALANCE, FormatUtil.formatDate(vaciado.getPeriodo()));
				xmlVaciado.put(ConstantesRating.TIPO_VACIADO, getNombreClasificacion(vaciado.getIdTpoVaciado()));
				xmlVaciado.put(ConstantesRating.VACIADO_AJUSTADO, (vaciado.getAjustadoFlg().equals(ConstantesSEFE.FLAG_VACIADO_NO_AJUSTADO)) ? "NO" : "SI");
				xmlVaciado.put(ConstantesRating.TIPO_BALANCE, getNombreClasificacion(vaciado.getIdTipoBalance()));
				xmlVaciado.put(ConstantesRating.ESTADO_VACIADO, getNombreClasificacion(vaciado.getIdEstado()));
				xmlVaciado.put(ConstantesRating.ID_ESTADO_VACIADO, vaciado.getIdEstado());
				xmlVaciado.put(ConstantesRating.PLAN_DE_CUENTAS, getNombreClasificacion(vaciado.getIdNombrePlanCtas()));
				xmlVaciado.put(ConstantesRating.MONENA, getNombreClasificacion(vaciado.getIdMoneda()));
				xmlVaciado.put(ConstantesRating.MESES_PER, vaciado.getMesesPer());
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.setTime(vaciado.getPeriodo());
				xmlVaciado.put(ConstantesRating.MES_PERIODO_BALANCE, new Integer((cal.get(Calendar.MONTH) + 1)));

				xmlLstVaciados.add(xmlVaciado);
			}

			response.put(ConstantesRating.LISTA_VACIADOS, xmlLstVaciados);
		}

		return response;
	}

	/*
	 * Retorna el nombre de la clasificacion buscada. En caso de no encontrar la clasificacion se retorna null.
	 */
	private String getNombreClasificacion(Integer idClasificacion) {
		CatalogoGeneral catalogoGeneral = new CatalogoGeneralImpl();

		Clasificacion clasificacion = catalogoGeneral.buscarClasificacionPorId(idClasificacion);

		return (clasificacion != null ? clasificacion.getNombre() : null);
	}
}
