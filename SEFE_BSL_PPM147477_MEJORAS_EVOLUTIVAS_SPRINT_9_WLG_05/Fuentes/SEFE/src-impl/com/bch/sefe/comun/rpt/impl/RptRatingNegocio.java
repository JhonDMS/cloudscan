package com.bch.sefe.comun.rpt.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.rpt.ConstantesReportes;
import com.bch.sefe.comun.rpt.SEFEDataSource;
import com.bch.sefe.comun.rpt.SEFEReporte;
import com.bch.sefe.comun.srv.GestorClasificaciones;
import com.bch.sefe.comun.srv.GestorReportes;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.impl.GestorClasificacionesImpl;
import com.bch.sefe.comun.srv.impl.GestorReportesImpl;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.rating.ServicioRatingNegocio;
import com.bch.sefe.rating.impl.ServicioRatingNegocioImpl;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.GestorRatingNegocio;
import com.bch.sefe.rating.srv.impl.GestorRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingNegocioImpl;
import com.bch.sefe.rating.vo.MatrizNegocio;
import com.bch.sefe.rating.vo.PreguntaNegocio;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.rating.vo.TemaNegocio;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.util.FormatUtil;

public class RptRatingNegocio implements SEFEReporte {

	private String rut = null;
	private Long idRatingNegocio = null;
	private Long idRatingIndividual = null;

	public Map crearDataSources(Map parametros) {

		setRut((String) parametros.get(ConstantesReportes.RUT_CLIENTE));
		setIdRatingNegocio((Long) parametros.get(ConstantesReportes.ID_RATING_NEG));
		setIdRatingIndividual((Long) parametros.get(ConstantesReportes.ID_RATING_IND));

		GestorReportes gestorRpt = new GestorReportesImpl();
		GestorClasificaciones gestorCla = new GestorClasificacionesImpl();
		GestorRatingNegocio gestorRtgNeg =  new GestorRatingNegocioImpl();

		Cliente cliente = getClienteRatingNegocio();
		MatrizNegocio matrizNeg = getMatrizNegocio();

		RatingIndividual rtgInd = getRatingIndividual(new Long(cliente.getClienteId()));
		RatingNegocio rtgNegocio = getRatingNegocio();
		
		String tipoVaciado = gestorCla.buscarClasificacionPorId(rtgInd.getIdTipoVaciadoRatingNegocio()).getNombre();

		parametros = gestorRpt.obtenerEtiquetas(ConstantesReportes.COD_RPT_RTG_NEGOCIO, ConstantesSEFE.RPT_ID_IDIOMA_ESPANOL);
		parametros.put(ConstantesReportes.NOM_CLIENTE, cliente.getNombreCliente());
		parametros.put(ConstantesReportes.RUT_CLIENTE, cliente.getRut());
		parametros.put(ConstantesReportes.FCH_RTG, rtgInd.getFechaCambioEstado() != null ? FormatUtil.formatDate(rtgInd.getFechaCambioEstado()) : null);
		parametros.put(ConstantesReportes.FCH_RTG_NEG, rtgNegocio.getFechaEvaluacion() != null ? FormatUtil.formatDate(rtgNegocio.getFechaEvaluacion()) : null);
		parametros.put(ConstantesReportes.ESTADO, gestorCla.buscarClasificacionPorId(rtgInd.getIdEstado()).getNombre());
		parametros.put(ConstantesReportes.MOD_RTG, gestorCla.buscarClasificacionPorId(rtgInd.getIdBanca()).getNombre());
		parametros.put(ConstantesReportes.SEGM_VENTA, (matrizNeg.getIdSegmento() != null ? getNombreSegmento(matrizNeg.getIdSegmento()) : null));
		parametros.put(ConstantesReportes.RESPONSABLE, getResponsable(rtgNegocio));
		//parametros.put(ConstantesReportes.TIPO_VACIADO, gestorRtgNeg.buscarTipoEvaluacionRating(getRut(), rtgInd.getIdBanca(), getIdRatingIndividual()));		
		parametros.put(ConstantesReportes.TIPO_VACIADO, tipoVaciado);
		parametros.put(ConstantesReportes.COD_BANCA_INDIV, rtgInd.getIdBanca());
		parametros.put(ConstantesReportes.COD_BANCA_PYME, ConstantesSEFE.BANCA_PYME);
		parametros.put(ConstantesReportes.COD_ID_EST, ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		parametros.put(ConstantesReportes.COD_ID_EST_INDIV, rtgInd.getIdEstado());
		parametros.put(ConstantesReportes.ACT_ECONOMI, (cliente.getSubSectorId() != null ? getActividadEconomica(cliente.getSubSectorId()) : null));
		parametros.put(ConstantesReportes.SUBREPORT_DIR_TEMA, System.getProperty(ConstantesSEFE.APP_PROPS) + ConfigManager.getValueAsString(ConstantesSEFE.KEY_SUBRPT_SUB_RPTNEGOCIO_TEMA));
		parametros.put(ConstantesReportes.SUBREPORT_DIR_ALTERNATIVA, System.getProperty(ConstantesSEFE.APP_PROPS) + ConfigManager.getValueAsString(ConstantesSEFE.KEY_SUBRPT_SUB_RPTNEGOCIO_ALTERNATIVA));

		return parametros;
	}

	public Map crearImagenes() {

		Map parametros = new HashMap();

		parametros.put(ConstantesReportes.IMG_LOGO_BCH, System.getProperty(ConstantesSEFE.APP_PROPS) + ConfigManager.getValueAsString(ConstantesSEFE.KEY_SUBRPT_IMG_RPTNEGOCIO_LOGOBCH));
		parametros.put(ConstantesReportes.IMG_BARRA_AZUL_572X10, System.getProperty(ConstantesSEFE.APP_PROPS) + ConfigManager.getValueAsString(ConstantesSEFE.KEY_SUBRPT_IMG_RPTNEGOCIO_572x10_AZUL));
		//parametros.put(ConstantesReportes.IMG_OK_RTG_NEGOCIO, System.getProperty(ConstantesSEFE.APP_PROPS) + ConfigManager.getValueAsString(ConstantesSEFE.KEY_SUBRPT_IMG_RPTNEGOCIO_OK));

		Date fechaActual = new Date();
		parametros.put(ConstantesReportes.FECH_EMISION, FormatUtil.formatDate(fechaActual));

		return parametros;
	}

	public SEFEDataSource obtenerDataSource() {

		MatrizNegocio matrizNegocio = getMatrizNegocio();
		List alternativaSeleccionada = getListAlternativasSeleccionada(getIdRatingNegocio());

		// lista de los temas
		List listTemaNegocio = matrizNegocio.getTemasNegocio();

		Collection collecDsReportePregunta = new ArrayList();

		for (int i = 0; i < listTemaNegocio.size(); i++) {

			TemaNegocio temaNegocio = (TemaNegocio) listTemaNegocio.get(i);
			List listPregunta = temaNegocio.getPreguntas();
			Collection listDSRespuesta = new ArrayList();

			for (int j = 0; j < listPregunta.size(); j++) {

				// Genera N respuesta por Pregunta
				PreguntaNegocio preguntaNegocio = (PreguntaNegocio) listPregunta.get(j);
				List listaRespuesta = preguntaNegocio.getAlternativas();

				DSReporteNegocioRespuesta dsRespuesta = new DSReporteNegocioRespuesta(listaRespuesta, alternativaSeleccionada);

				listDSRespuesta.add(dsRespuesta);
			}
			DSReporteNegocioTema dsTemaPregunta = new DSReporteNegocioTema(listDSRespuesta, listPregunta);
			collecDsReportePregunta.add(dsTemaPregunta);

		}

		DSReporteNegocio dsRptNegocio = new DSReporteNegocio(collecDsReportePregunta, listTemaNegocio);

		return dsRptNegocio;
	}

	private String getNombreSegmento(Integer sgmId) {
		GestorRating gestorRtg = new GestorRatingImpl();

		return gestorRtg.obtenerSegmentoPorId(sgmId).getNombreSegmento();
	}

	private String getActividadEconomica(Integer SubSectorId) {

		GestorClasificaciones gestorCla = new GestorClasificacionesImpl();
		Clasificacion actividadEconomica = gestorCla.buscarClasificacionPorId(SubSectorId);

		return actividadEconomica.getDescripcion();
	}

	private Cliente getClienteRatingNegocio() {

		GestorServicioClientes gestorCliente = new GestorServicioClientesImpl();

		return gestorCliente.obtenerClientePorRut(getRut());
	}

	private RatingNegocio getRatingNegocio() {
		GestorRatingNegocio gestorRatingNegocio = new GestorRatingNegocioImpl();
		return gestorRatingNegocio.buscarRatingNegocioPorId(getIdRatingNegocio());
	}

	private String getResponsable(RatingNegocio rtgNegocio) {
		GestorServicioClientes servicioCliente = new GestorServicioClientesImpl();
		Usuario usuario = servicioCliente.obtenerUsuarioPorId(rtgNegocio.getIdUsuario());

		return usuario.getCodigoUsuario();
	}

	private RatingIndividual getRatingIndividual(Long parteInvol) {
		GestorRatingIndividual gestorInd = new GestorRatingIndividualImpl();

		return gestorInd.buscarRatingIndividual(parteInvol, getIdRatingIndividual());
	}

	private MatrizNegocio getMatrizNegocio() {
		Date fechaEvalDate;
		GestorRatingNegocio gestorRatingNeg = new GestorRatingNegocioImpl();
		RatingNegocio rtgNegocio = (RatingNegocio) gestorRatingNeg.buscarRatingNegocioPorId(getIdRatingNegocio());

		GestorRatingIndividual gestorRatingIndividual = new GestorRatingIndividualImpl();
		
		RatingIndividual rating = gestorRatingIndividual.buscarRatingIndividualPorNegocio(rtgNegocio.getIdCliente(), rtgNegocio.getIdRating());
		
		//evaluacion de la fecha para impedir fechas null correccion por caso rtgNegocio
		if (rtgNegocio.getFechaEvaluacion() != null) {
			fechaEvalDate = rtgNegocio.getFechaEvaluacion();
		}else{
			fechaEvalDate = rating.getFechaCreacion();// en caso que sea null se toma la fecha de creacion como referencia
		}
		ServicioRatingNegocio servRtgNegocio = new ServicioRatingNegocioImpl();
		return servRtgNegocio.obtenerMatrizNegocio(rtgNegocio.getIdMatriz(), rating.getIdEstado(), fechaEvalDate);
	}

	private List getListAlternativasSeleccionada(Long idRatingNegocio) {

		ServicioRatingNegocio servRtgNegocio = new ServicioRatingNegocioImpl();
		List respuestasSelec = servRtgNegocio.buscarRespuestas(idRatingNegocio);

		return respuestasSelec;
	}
	
	/**
	 * @param idRatingNegocio
	 *            el idRatingNegocio a establecer
	 */
	public void setIdRatingNegocio(Long idRatingNegocio) {
		this.idRatingNegocio = idRatingNegocio;
	}

	/**
	 * @param rut
	 *            el rut a establecer
	 */
	public void setRut(String rut) {
		this.rut = rut;
	}

	/**
	 * @return el rut
	 */
	public String getRut() {
		return rut;
	}

	/**
	 * @return el idRatingNegocio
	 */
	public Long getIdRatingNegocio() {
		return idRatingNegocio;
	}

	/**
	 * @param idRatingIndividual
	 *            el idRatingIndividual a establecer
	 */
	public void setIdRatingIndividual(Long idRatingIndividual) {
		this.idRatingIndividual = idRatingIndividual;
	}

	/**
	 * @return el idRatingIndividual
	 */
	public Long getIdRatingIndividual() {
		return idRatingIndividual;
	}
}
