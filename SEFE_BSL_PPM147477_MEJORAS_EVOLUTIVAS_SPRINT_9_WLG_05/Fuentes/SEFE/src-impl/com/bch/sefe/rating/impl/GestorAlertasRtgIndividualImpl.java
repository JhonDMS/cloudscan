package com.bch.sefe.rating.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.agricola.srv.GestorAgricola;
import com.bch.sefe.agricola.srv.impl.GestorAgricolaImpl;
import com.bch.sefe.agricola.vo.Agricola;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.srv.GestorAlertasRtgIndividual;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingComportamiento;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.GestorRatingNegocio;
import com.bch.sefe.rating.srv.GestorRatingProyectado;
import com.bch.sefe.rating.srv.impl.GestorRatingComportamientoImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingFinancieroImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingNegocioImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingProyectadoImpl;
import com.bch.sefe.rating.vo.ComponenteRating;
import com.bch.sefe.rating.vo.RatingComportamiento;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.RatingNegocio;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.vaciados.srv.GestorHojaIndependiente;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorHojaIndependienteImpl;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.Vaciado;

public class GestorAlertasRtgIndividualImpl implements GestorAlertasRtgIndividual {

	public List obtenerAlertasRtgIndividualModelo(Long idRating, String rutCliente, Integer idBanca) {
		List alertas = new ArrayList();

		GestorServicioClientes srvCtes = new GestorServicioClientesImpl();
		Cliente cte = srvCtes.obtenerClientePorRut(rutCliente);
		Long idCliente = Long.valueOf(cte.getClienteId());

		GestorRatingIndividual gestorRatingIndividual = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndividual = gestorRatingIndividual.buscarRatingIndividual(idCliente, idRating);

		alertas.addAll(validarVigenciaComponentesRating(ratingIndividual, cte, idBanca));
		alertas.addAll(validarVersionVaciados(ratingIndividual, cte, idBanca));
		alertas.addAll(validarTipoVaciadoRatingNegocio(ratingIndividual, cte));
		alertas.addAll(validarVigenciaVaciadoNegocio( ratingIndividual,  cte) );
		GestorHojaIndependiente gestorHoja = new GestorHojaIndependienteImpl();
		if (gestorHoja.bancaUsaHojaIndependiente(idBanca).booleanValue()) {
			alertas.addAll(validarVigenciaHojaIMD(idCliente, gestorHoja ));
		}
		return alertas;
	}
	private List validarVigenciaHojaIMD(Long idCliente, GestorHojaIndependiente gestorHoja){
		List alertas = new ArrayList();
		if (gestorHoja.hayDisponibleUnaHojaIndependiente(idCliente).booleanValue()) {
			alertas.add(ConstantesSEFE.MSG_ALERTA_VALIDACION_HOJA_IMD_VIGENTE);
		}
		return alertas;
	}

	private List validarVersionVaciados(RatingIndividual ratingIndividual, Cliente cliente, Integer idBanca) {
		List alertas = new ArrayList();
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
				alertas.add(ConstantesSEFE.MSG_ALERTA_VALIDACION_VERSION_VACIADOS);
			}
		}

		return alertas;
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

	private List validarVigenciaComponentesRating(RatingIndividual ratingIndividual, Cliente cliente, Integer idBanca) {
		GestorRatingComportamiento gestorRatingComp = new GestorRatingComportamientoImpl();
		GestorRatingNegocio gestorRatingNegocio = new GestorRatingNegocioImpl();
		GestorRatingFinanciero gestorRatingFinanciero = new GestorRatingFinancieroImpl();
		GestorRatingProyectado gestorRatingProy = new GestorRatingProyectadoImpl();
		GestorRating	gestorRating = new GestorRatingImpl();
		RatingFinanciero ratingFinanciero = null;
		RatingComportamiento ratingComportamiento = null;
		RatingNegocio ratingNegocio = null;
		RatingFinanciero ratingProy = null;
		Vaciado vacUltPeriodoProy = null;
		List alertas = new ArrayList();
		Date fechaValidacionProy = ratingIndividual.getFechaAvance();
		// recupera todos los componentes obligatorio para cerrar el rating individual
		List lstComponentesObligatorio = gestorRating.buscarComponentesRating(idBanca, null);
		if (lstComponentesObligatorio == null || lstComponentesObligatorio.isEmpty()) {
			alertas.add(ConstantesSEFE.MSG_ALERTA_VALIDACION_COMPONENTES_RTG);
			return alertas;
		}
		Long idCliente = Long.valueOf(cliente.getClienteId());

		// Se obtienen los componentes
		if (ratingIndividual.getIdRatingFinanciero() != null) {
			ratingFinanciero = gestorRatingFinanciero.obtenerRating(ratingIndividual.getIdRatingFinanciero());
		}

		if (ratingIndividual.getIdRatingNegocio() != null) {
			ratingNegocio = gestorRatingNegocio.buscarRatingNegocioPorId(ratingIndividual.getIdRatingNegocio());
		}

		if (ratingIndividual.getIdRatingComportamiento() != null) {
			ratingComportamiento = gestorRatingComp.obtenerRating(idCliente, ratingIndividual.getIdRatingComportamiento());
		}

		if (ratingIndividual.getIdRatingProyectado() != null) {
			
			if (ConstantesSEFE.BANCA_AGRICOLAS.equals(idBanca))
			{
				GestorAgricola gestorAgricola = new GestorAgricolaImpl();
				Agricola agricola = gestorAgricola.buscarVaciadoAgricola(ratingIndividual.getIdVaciadoAgricola());
				fechaValidacionProy = agricola.getFecha();
			}
			
			ratingProy = gestorRatingFinanciero.obtenerRating(ratingIndividual.getIdRatingProyectado());
			if (ratingProy != null && ratingProy.getIdVaciado0() != null) {
				GestorVaciados gestVaciados = new GestorVaciadosImpl();

				vacUltPeriodoProy = gestVaciados.buscarVaciado(ratingProy.getIdVaciado0());
			}
		}
		

		if ((componenteEsObligatorio(lstComponentesObligatorio, ConstantesSEFE.TIPO_RATING_FINANCIERO)) && (ratingFinanciero == null  || !gestorRatingFinanciero.estaVigente(ratingFinanciero, idBanca)
				|| (ConstantesSEFE.BANCA_PYME.equals(idBanca) && !gestorRatingFinanciero.validarIvas(ratingFinanciero)))) {
			alertas.add(ConstantesSEFE.MSG_ALERTA_VALIDACION_FINANCIERO);
		}

		// ... banca PyME no tiene proyectado
		if (!ConstantesSEFE.BANCA_PYME.equals(idBanca)) {
			if ((vacUltPeriodoProy == null || !gestorRatingProy.esVaciadoVigente(vacUltPeriodoProy, idBanca,fechaValidacionProy )) &&  componenteEsObligatorio(lstComponentesObligatorio, ConstantesSEFE.TIPO_RATING_NEGOCIO)) {
				alertas.add(ConstantesSEFE.MSG_ALERTA_VALIDACION_PROYECTADO);
			}
		}

		if ((ratingNegocio == null || !gestorRatingNegocio.esVigente(ratingNegocio, idBanca)) &&  componenteEsObligatorio(lstComponentesObligatorio, ConstantesSEFE.TIPO_RATING_NEGOCIO)) {
			alertas.add(ConstantesSEFE.MSG_ALERTA_VALIDACION_NEGOCIO);
		}

		if ((ratingComportamiento == null || !ratingComportamiento.esVigente().booleanValue()) &&   componenteEsObligatorio(lstComponentesObligatorio, ConstantesSEFE.TIPO_RATING_COMPORTAMIENTO)) {
			alertas.add(ConstantesSEFE.MSG_ALERTA_VALIDACION_COMPORTAMIENTO);
		}
		return alertas;
	}
	
	private boolean componenteEsObligatorio(List lstComponentesObligatorio, Integer componenteConsultado)
	{
		for (int i = 0; i < lstComponentesObligatorio.size(); ++i) {
			ComponenteRating componente = (ComponenteRating) lstComponentesObligatorio.get(i);
			if(componente.getIdComponente().equals(componenteConsultado))
			{
				return true;
			}
		}
		return false;
	}
	
	private List validarTipoVaciadoRatingNegocio(RatingIndividual ratingIndividual, Cliente cliente) {
		List alertas = new ArrayList();
		GestorRatingNegocio gestRtgNeg = new GestorRatingNegocioImpl();
		
		if (ratingIndividual.getIdRatingNegocio() == null) {
			return alertas;
		}
		
		Long idCliente = Long.valueOf(cliente.getClienteId());
		
		try {
			gestRtgNeg.validarEvaluacionRatingNegocio(idCliente, ratingIndividual.getIdRatingNegocio(), ratingIndividual.getIdRating());
		} catch (BusinessOperationException box) {
			alertas.add(ConstantesSEFE.MSG_ALERTA_VALIDACION_TPO_VACIADO_RTG_NEGOCIO);
		}
		
		return alertas;
	}
	
	/**
	 * metodo que valida si el vaciado utilizado en la generacion del rating de negocio a sufrido algun cambio en las ventas
	 * @param ratingIndividual
	 * @param cliente
	 * @return
	 */
	private List validarVigenciaVaciadoNegocio(RatingIndividual ratingIndividual, Cliente cliente) {
		List alertas = new ArrayList();
		GestorRatingNegocio gestRtgNeg = new GestorRatingNegocioImpl();
		
		if (ratingIndividual.getIdRatingNegocio() == null) {
			return alertas;
		}
		
	
		try {
			gestRtgNeg.validarVigenciaVaciado(ratingIndividual, cliente.getRut(), ratingIndividual.getIdEstado());
		} catch (BusinessOperationException box) {
			alertas.add(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_NEG_ALERTA_CAMBIO_SEGMENTO));
		}
		
		return alertas;
	}
}
