package com.bch.sefe.rating.srv.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.comun.util.ConfigDBManager;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.ValidadorRatingGrupal;
import com.bch.sefe.rating.vo.IntegranteRatingGrupo;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.util.SEFEUtil;

public class ValidadorRatingGrupalPyMEImpl implements ValidadorRatingGrupal {
	private static final Logger log = Logger.getLogger(ValidadorRatingGrupalPyMEImpl.class);

	/**
	 * El parametro de entrada <b>parametros</b> corresponde a un {@link List} que contiene lo siguiente:
	 * <ul>
	 * <li>0 - String con el rut del cliente (empresa cabecera) para el que se genera el rating grupal</li>
	 * <li>1 - lista de {@link IntegranteRatingGrupo} que corresponden a los relacionados del grupo</li>
	 * </ul>
	 */
	public void validar(Object parametros) {
		List lstParametros = (List) parametros;
		List relacionados = (List) lstParametros.get(0);
		Double prcParticipacionSocios = null;
		IntegranteRatingGrupo relacionado;
		IntegranteRatingGrupo empMadre;
		boolean existenSocios;

		existenSocios = haySocios(relacionados);
		empMadre = getEmpresaMadre(relacionados);

		validarEmpresaMadre(empMadre, existenSocios);

		for (int i = 0; i < relacionados.size(); i++) {
			relacionado = (IntegranteRatingGrupo) relacionados.get(i);

			if (relacionado.getOperaComoEmpresa() == null) {
				throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_PYME_CODIGO_ACT_ECONOMICA_NO_EXISTE));
			} else if (relacionado.operaComoEmpresa()) {
				validarRequisitosOperaComoEmpresa(relacionado);
			} else {
				validarRequisitosOperaComoPersona(relacionado);
			}

			// Se suma la participacion de los socios y luego se valida
			if (ConstantesSEFE.ID_RELACION_SOCIO.equals(relacionado.getIdRelacion())) {
				// Si es socio se maneja el porcentaje de participacion de todos los socios
				if (prcParticipacionSocios == null) {
					prcParticipacionSocios = new Double("0.0");
				}

				if (relacionado.getPrcParticipacion() != null) {
					prcParticipacionSocios = new Double(prcParticipacionSocios.doubleValue() + relacionado.getPrcParticipacion().doubleValue());
				}
			}
		}

		if (existenSocios)
			validarPrcParticipacionSocios(prcParticipacionSocios);
	}

	/*
	 * Valida los requisitos de la empresa madre. Estos son: - El porcentaje de participacion de la empresa madre debe ser 100%. - Debe tener ventas.
	 * - Debe tener rating individual.
	 */
	private void validarEmpresaMadre(IntegranteRatingGrupo empMadre, boolean haySocios) {
		// 20130108 - jlm. Control de cambio nro.3
		if (!empMadre.operaComoEmpresa()) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("Error. Empresa Madre con rut [{0}] NO OPERA COMO EMPRESA", new Object[] { empMadre.getRutRelacionado() }));
			}
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG11));
		}

		// 20130108 - jlm. Control de cambio nro.2
		Long parteNum = SEFEUtil.obtenerRutSinDigito(empMadre.getRutRelacionado());
		if (parteNum != null) {
			if (parteNum.compareTo(ConstantesSEFE.RATING_GRUPAL_PYME_RUT_EXIGEN_SOCIOS) > 0 && !haySocios) {
				if (log.isDebugEnabled()) {
					log.debug(MessageFormat.format("El rut [{0}] de la Empresa Madre supera los [{1}] millones. Debe tener Socios", new Object[] { empMadre.getRutRelacionado(),
							ConstantesSEFE.RATING_GRUPAL_PYME_RUT_EXIGEN_SOCIOS }));
				}
				throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GPR_PYME_VALIDACION_MSG12));
			} else if (parteNum.compareTo(ConstantesSEFE.RATING_GRUPAL_PYME_RUT_EXIGEN_SOCIOS) <= 0 && haySocios) {
				if (log.isDebugEnabled()) {
					log.debug(MessageFormat.format("El rut [{0}] de la Empresa Madre no supera los [{1}] millones. No se permiten Socios", new Object[] {
							empMadre.getRutRelacionado(), ConstantesSEFE.RATING_GRUPAL_PYME_RUT_EXIGEN_SOCIOS }));
				}
				throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG9));
			}
		} else {
			// Si no fue posible determinar la parte numerica del rut es posible que se haya ingresado con un rut ficticio
			String numeroFormat = FormatUtil.formatNumero(new BigDecimal(ConstantesSEFE.RATING_GRUPAL_PYME_RUT_EXIGEN_SOCIOS.toString()), "###,###,###", '.', ',');
			throw new BusinessOperationException(MessageFormat.format(ConstantesSEFE.MSG_ERR_RATING_GPR_PYME_VALIDACION_MSG13, new Object[] { numeroFormat }));
		}

		if (empMadre.getPrcParticipacion() == null || empMadre.getPrcParticipacion().doubleValue() < 100.0) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("Relacionado cabecera [{0}] tiene un porcentaje de participacion menor al 100%", new Object[] { empMadre.getRutRelacionado() }));
			}
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GPR_PARTICIPACION_EMPRESA_CABECERA));
		}
		GestorRatingIndividual gestorRatingInd = new GestorRatingIndividualImpl();
		GestorRating	gestorRating = new GestorRatingImpl();
		RatingIndividual rtgInd = gestorRatingInd.buscarRatingVigente(empMadre.getRutRelacionado());
		if (rtgInd == null) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("Relacionado cabecera [{0}] no tiene rating individual", new Object[] { empMadre.getRutRelacionado() }));
			}
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GPR_EMPRESA_CABECERA_REQ_RTG_IND));
		}
		List lstComponentesObligatorio = gestorRating.buscarComponentesRatingSoloIdComponente(rtgInd.getIdBanca(), null);
		boolean esVentasObligatorio = false;
		if (lstComponentesObligatorio != null) {
			esVentasObligatorio = lstComponentesObligatorio.contains(ConstantesSEFE.TIPO_RATING_FINANCIERO) ||
								  lstComponentesObligatorio.contains(ConstantesSEFE.TIPO_RATING_PROYECTADO);
		}
		if (esVentasObligatorio && empMadre.getVentas() == null) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("Relacionado cabecera [{0}] no tiene un monto de ventas", new Object[] { empMadre.getRutRelacionado() }));
			}
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GPR_VENTAS_EMPRESA_CABECERA));
		}

		if (empMadre.getRatingIndividual() == null) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("Relacionado cabecera [{0}] no tiene rating individual", new Object[] { empMadre.getRutRelacionado() }));
			}
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GPR_EMPRESA_CABECERA_REQ_RTG_IND));
		}
	}

	/*
	 * Busca la empresa madre del grupo
	 */
	private IntegranteRatingGrupo getEmpresaMadre(List relacionados) {
		for (int i = 0; i < relacionados.size(); i++) {
			IntegranteRatingGrupo integrante = (IntegranteRatingGrupo) relacionados.get(i);
			if (ConstantesSEFE.ID_RELACION_EMPRESA_MADRE.equals(integrante.getIdRelacion())) {
				return integrante;
			}
		}
		return null;
	}

	private void validarRequisitosOperaComoEmpresa(IntegranteRatingGrupo relacionado) {
		if (log.isDebugEnabled()) {
			log.debug("Validando requisitos para calculo de rating grupal PyME - Relacionado Opera como Empresa");
		}

		// Todos los relacionados que operen como persona deben tener un rating de comportamiento
		if (relacionado.getRatingComportamiento() == null) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("Relacionado [{0}] no tiene un rating de comportamiento", new Object[] { relacionado.getRutRelacionado() }));
			}
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_VALIDACION_RTG_COMPORTAMIENTO_REQUERIDO));
		}

		// Se valida que exista una relación y en caso de existir, se validan particularidades de estas
		if (relacionado.getIdRelacion() == null) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("Relacionado [{0}] no tiene configurada la relacion que tiene con el cliente cabecera", new Object[] { relacionado
						.getRutRelacionado() }));
			}
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG3));
		} else if (ConstantesSEFE.ID_RELACION_SOCIO.equals(relacionado.getIdRelacion())) {
			if (relacionado.getPrcParticipacion() == null) {
				if (log.isDebugEnabled()) {
					log.debug(MessageFormat.format("Relacionado [{0}] que es SOCIO no tiene participacion y debería tener", new Object[] { relacionado.getRutRelacionado() }));
				}
				throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_PYME_VALIDACION_DEBE_TENER_PARTICIPACION_SOCIO));
			}
		} else if (ConstantesSEFE.ID_RELACION_EMPRESA_HIJA.equals(relacionado.getIdRelacion())) {
			if (relacionado.getPrcParticipacion() == null) {
				if (log.isDebugEnabled()) {
					log.debug(MessageFormat
							.format("Relacionado [{0}] que es Empresa Hija no tiene participacion y debería tener", new Object[] { relacionado.getRutRelacionado() }));
				}
				throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_PYME_VALIDACION_DEBE_TENER_PARTICIPACION));
			}
		}
	}

	private void validarPrcParticipacionSocios(Double prcParticipacionSocios) {
		Double minimo = ConfigDBManager.getValueAsDouble(ConstantesSEFE.PROPIEDAD_RTG_GRP_PYME_PARTICIPACION_MINIMA_SOCIOS);
		Double maximo = ConfigDBManager.getValueAsDouble(ConstantesSEFE.PROPIEDAD_RTG_GRP_PYME_PARTICIPACION_MAXIMA_SOCIOS);

		if (prcParticipacionSocios.doubleValue() < minimo.doubleValue()) {
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG1));
		}

		if (prcParticipacionSocios.doubleValue() > maximo.doubleValue()) {
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG2));
		}
	}

	/*
	 * Determina si dentro de los relacionados existen socios
	 */
	private boolean haySocios(List relacionados) {
		for (int i = 0; i < relacionados.size(); i++) {
			IntegranteRatingGrupo intRtg = (IntegranteRatingGrupo) relacionados.get(i);

			if (ConstantesSEFE.ID_RELACION_SOCIO.equals(intRtg.getIdRelacion())) {
				return true;
			}
		}
		return false;
	}

	private void validarRequisitosOperaComoPersona(IntegranteRatingGrupo relacionado) {
		if (log.isDebugEnabled()) {
			log.debug("Validando requisitos para calculo de rating grupal PyME - Relacionado Opera como Persona");
		}

		// Todos los relacionados que operen como persona deben tener un rating de comportamiento
		if (relacionado.getRatingComportamiento() == null) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("Relacionado [{0}] no tiene un rating de comportamiento", new Object[] { relacionado.getRutRelacionado() }));
			}
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_VALIDACION_RTG_COMPORTAMIENTO_REQUERIDO));
		}

		// Se valida que exista una relación y en caso de existir, se validan particularidades de estas
		if (relacionado.getIdRelacion() == null) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("Relacionado [{0}] no tiene configurada la relacion que tiene con el cliente cabecera", new Object[] { relacionado
						.getRutRelacionado() }));
			}
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG3));
		}

		// Para un relacionado que opera como persona no es permitida la relación Empresa Hija y Empresa Hermana
		if (ConstantesSEFE.ID_RELACION_EMPRESA_HIJA.equals(relacionado.getIdRelacion()) || ConstantesSEFE.ID_RELACION_EMPRESA_HERMANA.equals(relacionado.getIdRelacion())) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format("La relación [{0}] del relacionado [{1}] no es válida. Solo son válidas 'Socio' y 'Sin Participación'", new Object[] {
						relacionado.getIdRelacion(), relacionado.getRutRelacionado() }));
			}
			throw new BusinessOperationException(MessageManager.getError(ConstantesSEFE.MSG_ERR_RATING_GRP_PYME_VALIDACION_MSG14));
		}
	}
}
