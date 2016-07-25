package com.bch.sefe.rating.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RatingIndividual {
	private Long idCliente; 				// PARTE_INVOL_ID	NUMBER(15,0)
	private Long idRating;					// RTG_SEQ	NUMBER(15,0)
	private Double ratingFinal;				// RTG_FINAL	NUMBER(6,2)
	private Double ratingFinalSugerido;		// RTG_FINAL_SUGERIDO	NUMBER(6,2)
	private Date fechaCreacion;				// FECHA_CREACION	DATE
	private Date fechaCambioEstado;			// FECHA_EST	DATE
	private Date fechaModificacion;			// FECHA_MODIFICACION	DATE
	private Double ratingAproxFinal;		// RTG_APROXIMADO_FINAL	NUMBER(6,2)
	private Double prcDisminucionIVA;		// PRC_DISMINUCION_VTA	NUMBER(5,2)
	private Double ratingPreliminar1;		// RTG_PRELIMINAR_2	NUMBER(6,2)
	private Double ratingPreliminar2;		// RTG_PRELIMINAR_1	NUMBER(6,2)
	private Double prcRatingNegocio;		// POND_NEG	NUMBER(5,2)
	private Double prcRatingProyectado;		// POND_FINAN_PROYECTADA	NUMBER(5,2)
	private Double prcRatingComportamiento;	// POND_CPMTO	NUMBER(5,2)
	private Double prcRatingFinanciero;		// POND_FINAN	NUMBER(5,2)
	private Double premioTamano;			// PREM_TAMANO	NUMBER(6,2)
	private Double ajusteEscala;			// AJT_ESCALA	NUMBER(5,2)
	private Double montoVenta;				// MONTO_VTA	NUMBER(16,2)
	private Double montoPatrimonio;			// MONTO_PATRM	NUMBER(16,2)
	private Double ratingNegocio;			// RTG_NEG	NUMBER(6,2)
	private Double ratingComportamiento;	// RTG_CPMTO	NUMBER(6,2)
	private Double ratingFinanciero;		// RTG_FINAN	NUMBER(6,2)
	private Double ratingProyectado;		// RTG_FINAN_PROYECTADO	NUMBER(6,2)
	private Date fechaRatingFinanciero;		// FECHA_RTG_FINAN	DATE
	private Date fechaRatingProyectado;		// FECHA_RTG_FINAN_PROYECTADO	DATE
	private Date fechaRatingNegocio;		// FECHA_RTG_NEG	DATE
	private Date fechaRatingComportamiento;	// FECHA_RTG_CPMTO	DATE
	private String comentario;				// COMENTARIO	VARCHAR2(2000 BYTE)
	private Long idUsuario;					// USU_ID	NUMBER(15,0)
	private Long idUsuarioModificacion;		// USU_MODIF_ID	NUMBER(15,0)
	private Integer idEstado;				// EST_ID	NUMBER(6,0)
	private Long idRatingFinanciero;		// RTG_FINAN_ID	NUMBER(15,0)
	private Date periodoVac0;				// PER_VAC_ID	DATE
	private Double deudaBanco;				// MONTO_DEU_BANCO	NUMBER(16,2)
	private Double deudaSBIF;				// MONTO_DEU_SBIF	NUMBER(16,2)
	private Double deudaSinHipotBanco;		// MONTO_DEU_SIN_HIP_BANCO	NUMBER(16,2)
	private Double deudaSinHipotSBIF;		// MONTO_DEU_SIN_HIP_SBIF	NUMBER(16,2)
	private Double deudaACHEL;				// MONTO_DEU_ACHEL	NUMBER(16,2)
	private Date periodoVac1;				// PER_VAC_ANTERIOR_ID	DATE
	private Date periodoVac2;				// PER_VAC_ANTEANTERIOR_ID	DATE
	private String nivelVenta;				// NVL_DE_VTA	VARCHAR2(32 BYTE)
	private Integer idMotivoModifRating;	// MOTIVO_MODIF_RTG_ID	NUMBER(6,0)
	private Long idRatingNegocio;			// RTG_NEGOICIO_ID	NUMBER(15,0)
	private Integer idBanca;				// BANCA_ID	NUMBER(6,0)
	private Long idRatingComportamiento;	// RTG_CPMTO_ID	NUMBER(15,0)
	private String comentarioModificacion;	// COMENTARIO_MODIF	VARCHAR2(256 BYTE)
	private Double montoActivos;			// MONTO_ACTIVOS	NUMBER(16,2)
	private Long idRatingGarante;			// RTG_GARANTE_ID	NUMBER(15,0)
	private Double ratingGarante;			// RTG_GARANTE	NUMBER(6,2)
	private Date fechaRatingGarante;				// FECHA_RTG_GARANTE	DATE
	private Integer idTipoVaciadoRatingNegocio;		// TPO_VAC_RTG_NEG_ID	NUMBER(6,0)
	private Date fechaAvance;
	
	private Date fechaActualizacionSiebel;
		
	private String rut;
	private String estado;
	private String banca;
	private String nombreResponsable;
	private String responsableRatingFinanciero;
	private String responsableRatingNegocio;
	private String responsableRatingComportamiento;
	private String responsableRatingProyectado;
	private Long idRatingProyectado;
	private Date fechaEEFF;
	
	private Boolean rtgIndividualConfirmado 	= Boolean.FALSE;
	private Boolean rtgFinancieroConfirmado 	= Boolean.FALSE;
	private Boolean rtgProyectadoConfirmado 	= Boolean.FALSE;
	private Boolean rtgNegocioConfirmado 		= Boolean.FALSE;
	private Boolean rtgComportamientoConfirmado = Boolean.FALSE;
	
	/*
	 * Sprint 3 Req: 7.4.29
	 * Se agregan las variables de Admin - Parametros Generales - Caducar Vigencia
	 */
	private String parametroA;
	private String parametroB;
	private String parametroX;
	private String parametroY;
	private String parametroZ;
	private String vigenciaSinRating;
	private Long idVaciadoAgricola;
	
	public Long getIdVaciadoAgricola() {
		return idVaciadoAgricola;
	}
	public void setIdVaciadoAgricola(Long idVaciadoAgricola) {
		this.idVaciadoAgricola = idVaciadoAgricola;
	}

	// Sprint 9 vigencia de componentes
	private Boolean vigenciaComponentes;
	
	public Boolean getVigenciaComponentes() {
		return vigenciaComponentes;
	}
	public void setVigenciaComponentes(Boolean vigenciaComponentes) {
		this.vigenciaComponentes = vigenciaComponentes;
	}
	public String getVigenciaSinRating() {
		return vigenciaSinRating;
	}
	public void setVigenciaSinRating(String vigenciaSinRating) {
		this.vigenciaSinRating = vigenciaSinRating;
	}
	public String getParametroZ() {
		return parametroZ;
	}
	public void setParametroZ(String parametroZ) {
		this.parametroZ = parametroZ;
	}

	private String nombreCorto;
	
	public String getNombreCorto() {
		return nombreCorto;
	}
	public void setNombreCorto(String nombreCorto) {
		this.nombreCorto = nombreCorto;
	}

	public String getParametroA() {
		return parametroA;
	}
	public void setParametroA(String parametroA) {
		this.parametroA = parametroA;
	}
	public String getParametroB() {
		return parametroB;
	}
	public void setParametroB(String parametroB) {
		this.parametroB = parametroB;
	}
	public String getParametroX() {
		return parametroX;
	}
	public void setParametroX(String parametroX) {
		this.parametroX = parametroX;
	}
	public String getParametroY() {
		return parametroY;
	}
	public void setParametroY(String parametroY) {
		this.parametroY = parametroY;
	}
	
	/*
	 * Requerimiento 7.2.15 Ficha Financiera - Sprint 1
	 * @Param String que recibe el valor nombre de Clasif (Individual o Consolidado)
	 * @Param int recibe valor id_clasif (1201-Individual/1202-Consolidado)
	 */
	private String clasifNombre;
	private Integer idClasif;
	
	/*
	 * @return id de clasif
	 */
	public Integer getIdClasif() {
		return idClasif;
	}
	/*
	 * @param idclasif
	 */
	public void setIdClasif(Integer idClasif) {
		this.idClasif = idClasif;
	}

	/*
	 * @return el nombre de clasif
	 */
	public String getClasifNombre() {
		return clasifNombre;
	}

	/*
	 * @param nombre de clasif
	 */
	public void setClasifNombre(String clasifNombre) {
		this.clasifNombre = clasifNombre;
	}


	private List alertasComponentes = new ArrayList();
	
	public boolean tieneRatingSugerido() {
		return (this.getRatingFinalSugerido() != null);
	}
	
	
	/**
	 * @return el idCliente
	 */
	public Long getIdCliente() {
		return idCliente;
	}
	/**
	 * @param idCliente el idCliente a establecer
	 */
	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}
	/**
	 * @return el idRating
	 */
	public Long getIdRating() {
		return idRating;
	}
	/**
	 * @param idRating el idRating a establecer
	 */
	public void setIdRating(Long idRating) {
		this.idRating = idRating;
	}
	/**
	 * @return el ratingFinal
	 */
	public Double getRatingFinal() {
		return ratingFinal;
	}
	/**
	 * @param ratingFinal el ratingFinal a establecer
	 */
	public void setRatingFinal(Double ratingFinal) {
		this.ratingFinal = ratingFinal;
	}
	/**
	 * @return el ratingFinalSugerido
	 */
	public Double getRatingFinalSugerido() {
		return ratingFinalSugerido;
	}
	/**
	 * @param ratingFinalSugerido el ratingFinalSugerido a establecer
	 */
	public void setRatingFinalSugerido(Double ratingFinalSugerido) {
		this.ratingFinalSugerido = ratingFinalSugerido;
	}
	/**
	 * @return el fechaCreacion
	 */
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	/**
	 * @param fechaCreacion el fechaCreacion a establecer
	 */
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	/**
	 * @return el fechaCambioEstado
	 */
	public Date getFechaCambioEstado() {
		return fechaCambioEstado;
	}
	/**
	 * @param fechaCambioEstado el fechaCambioEstado a establecer
	 */
	public void setFechaCambioEstado(Date fechaCambioEstado) {
		this.fechaCambioEstado = fechaCambioEstado;
	}
	/**
	 * @return el fechaModificacion
	 */
	public Date getFechaModificacion() {
		return fechaModificacion;
	}
	/**
	 * @param fechaModificacion el fechaModificacion a establecer
	 */
	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}
	/**
	 * @return el ratingAproxFinal
	 */
	public Double getRatingAproxFinal() {
		return ratingAproxFinal;
	}
	/**
	 * @param ratingAproxFinal el ratingAproxFinal a establecer
	 */
	public void setRatingAproxFinal(Double ratingAproxFinal) {
		this.ratingAproxFinal = ratingAproxFinal;
	}
	/**
	 * @return el prcDisminucionIVA
	 */
	public Double getPrcDisminucionIVA() {
		return prcDisminucionIVA;
	}
	/**
	 * @param prcDisminucionIVA el prcDisminucionIVA a establecer
	 */
	public void setPrcDisminucionIVA(Double prcDisminucionIVA) {
		this.prcDisminucionIVA = prcDisminucionIVA;
	}
	/**
	 * @return el ratingPreliminar1
	 */
	public Double getRatingPreliminar1() {
		return ratingPreliminar1;
	}
	/**
	 * @param ratingPreliminar1 el ratingPreliminar1 a establecer
	 */
	public void setRatingPreliminar1(Double ratingPreliminar1) {
		this.ratingPreliminar1 = ratingPreliminar1;
	}
	/**
	 * @return el ratingPreliminar2
	 */
	public Double getRatingPreliminar2() {
		return ratingPreliminar2;
	}
	/**
	 * @param ratingPreliminar2 el ratingPreliminar2 a establecer
	 */
	public void setRatingPreliminar2(Double ratingPreliminar2) {
		this.ratingPreliminar2 = ratingPreliminar2;
	}
	/**
	 * @return el prcRatingNegocio
	 */
	public Double getPrcRatingNegocio() {
		return prcRatingNegocio;
	}
	/**
	 * @param prcRatingNegocio el prcRatingNegocio a establecer
	 */
	public void setPrcRatingNegocio(Double prcRatingNegocio) {
		this.prcRatingNegocio = prcRatingNegocio;
	}
	/**
	 * @return el prcRatingProyectado
	 */
	public Double getPrcRatingProyectado() {
		return prcRatingProyectado;
	}
	/**
	 * @param prcRatingProyectado el prcRatingProyectado a establecer
	 */
	public void setPrcRatingProyectado(Double prcRatingProyectado) {
		this.prcRatingProyectado = prcRatingProyectado;
	}
	/**
	 * @return el prcRatingComportamiento
	 */
	public Double getPrcRatingComportamiento() {
		return prcRatingComportamiento;
	}
	/**
	 * @param prcRatingComportamiento el prcRatingComportamiento a establecer
	 */
	public void setPrcRatingComportamiento(Double prcRatingComportamiento) {
		this.prcRatingComportamiento = prcRatingComportamiento;
	}
	/**
	 * @return el prcRatingFinanciero
	 */
	public Double getPrcRatingFinanciero() {
		return prcRatingFinanciero;
	}
	/**
	 * @param prcRatingFinanciero el prcRatingFinanciero a establecer
	 */
	public void setPrcRatingFinanciero(Double prcRatingFinanciero) {
		this.prcRatingFinanciero = prcRatingFinanciero;
	}
	/**
	 * @return el premioTamano
	 */
	public Double getPremioTamano() {
		return premioTamano;
	}
	/**
	 * @param premioTamano el premioTamano a establecer
	 */
	public void setPremioTamano(Double premioTamano) {
		this.premioTamano = premioTamano;
	}
	/**
	 * @return el ajusteEscala
	 */
	public Double getAjusteEscala() {
		return ajusteEscala;
	}
	/**
	 * @param ajusteEscala el ajusteEscala a establecer
	 */
	public void setAjusteEscala(Double ajusteEscala) {
		this.ajusteEscala = ajusteEscala;
	}
	/**
	 * @return el montoVenta
	 */
	public Double getMontoVenta() {
		return montoVenta;
	}
	/**
	 * @param montoVenta el montoVenta a establecer
	 */
	public void setMontoVenta(Double montoVenta) {
		this.montoVenta = montoVenta;
	}
	/**
	 * @return el montoPatrimonio
	 */
	public Double getMontoPatrimonio() {
		return montoPatrimonio;
	}
	/**
	 * @param montoPatrimonio el montoPatrimonio a establecer
	 */
	public void setMontoPatrimonio(Double montoPatrimonio) {
		this.montoPatrimonio = montoPatrimonio;
	}
	/**
	 * @return el ratingNegocio
	 */
	public Double getRatingNegocio() {
		return ratingNegocio;
	}
	/**
	 * @param ratingNegocio el ratingNegocio a establecer
	 */
	public void setRatingNegocio(Double ratingNegocio) {
		this.ratingNegocio = ratingNegocio;
	}
	/**
	 * @return el ratingComportamiento
	 */
	public Double getRatingComportamiento() {
		return ratingComportamiento;
	}
	/**
	 * @param ratingComportamiento el ratingComportamiento a establecer
	 */
	public void setRatingComportamiento(Double ratingComportamiento) {
		this.ratingComportamiento = ratingComportamiento;
	}
	/**
	 * @return el ratingFinanciero
	 */
	public Double getRatingFinanciero() {
		return ratingFinanciero;
	}
	/**
	 * @param ratingFinanciero el ratingFinanciero a establecer
	 */
	public void setRatingFinanciero(Double ratingFinanciero) {
		this.ratingFinanciero = ratingFinanciero;
	}
	/**
	 * @return el ratingProyectado
	 */
	public Double getRatingProyectado() {
		return ratingProyectado;
	}
	/**
	 * @param ratingProyectado el ratingProyectado a establecer
	 */
	public void setRatingProyectado(Double ratingProyectado) {
		this.ratingProyectado = ratingProyectado;
	}
	/**
	 * @return el fechaRatingFinanciero
	 */
	public Date getFechaRatingFinanciero() {
		return fechaRatingFinanciero;
	}
	/**
	 * @param fechaRatingFinanciero el fechaRatingFinanciero a establecer
	 */
	public void setFechaRatingFinanciero(Date fechaRatingFinanciero) {
		this.fechaRatingFinanciero = fechaRatingFinanciero;
	}
	/**
	 * @return el fechaRatingProyectado
	 */
	public Date getFechaRatingProyectado() {
		return fechaRatingProyectado;
	}
	/**
	 * @param fechaRatingProyectado el fechaRatingProyectado a establecer
	 */
	public void setFechaRatingProyectado(Date fechaRatingProyectado) {
		this.fechaRatingProyectado = fechaRatingProyectado;
	}
	/**
	 * @return el fechaRatingNegocio
	 */
	public Date getFechaRatingNegocio() {
		return fechaRatingNegocio;
	}
	/**
	 * @param fechaRatingNegocio el fechaRatingNegocio a establecer
	 */
	public void setFechaRatingNegocio(Date fechaRatingNegocio) {
		this.fechaRatingNegocio = fechaRatingNegocio;
	}
	/**
	 * @return el fechaRatingComportamiento
	 */
	public Date getFechaRatingComportamiento() {
		return fechaRatingComportamiento;
	}
	/**
	 * @param fechaRatingComportamiento el fechaRatingComportamiento a establecer
	 */
	public void setFechaRatingComportamiento(Date fechaRatingComportamiento) {
		this.fechaRatingComportamiento = fechaRatingComportamiento;
	}
	/**
	 * @return el comentario
	 */
	public String getComentario() {
		return comentario;
	}
	/**
	 * @param comentario el comentario a establecer
	 */
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	/**
	 * @return el idUsuario
	 */
	public Long getIdUsuario() {
		return idUsuario;
	}
	/**
	 * @param idUsuario el idUsuario a establecer
	 */
	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}
	/**
	 * @return el idUsuarioModificacion
	 */
	public Long getIdUsuarioModificacion() {
		return idUsuarioModificacion;
	}
	/**
	 * @param idUsuarioModificacion el idUsuarioModificacion a establecer
	 */
	public void setIdUsuarioModificacion(Long idUsuarioModificacion) {
		this.idUsuarioModificacion = idUsuarioModificacion;
	}
	/**
	 * @return el idEstado
	 */
	public Integer getIdEstado() {
		return idEstado;
	}
	/**
	 * @param idEstado el idEstado a establecer
	 */
	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}
	/**
	 * @return el idRatingFinanciero
	 */
	public Long getIdRatingFinanciero() {
		return idRatingFinanciero;
	}
	/**
	 * @param idRatingFinanciero el idRatingFinanciero a establecer
	 */
	public void setIdRatingFinanciero(Long idRatingFinanciero) {
		this.idRatingFinanciero = idRatingFinanciero;
	}
	/**
	 * @return el deudaBanco
	 */
	public Double getDeudaBanco() {
		return deudaBanco;
	}
	/**
	 * @param deudaBanco el deudaBanco a establecer
	 */
	public void setDeudaBanco(Double deudaBanco) {
		this.deudaBanco = deudaBanco;
	}
	/**
	 * @return el deudaSBIF
	 */
	public Double getDeudaSBIF() {
		return deudaSBIF;
	}
	/**
	 * @param deudaSBIF el deudaSBIF a establecer
	 */
	public void setDeudaSBIF(Double deudaSBIF) {
		this.deudaSBIF = deudaSBIF;
	}
	/**
	 * @return el deudaSinHipotBanco
	 */
	public Double getDeudaSinHipotBanco() {
		return deudaSinHipotBanco;
	}
	/**
	 * @param deudaSinHipotBanco el deudaSinHipotBanco a establecer
	 */
	public void setDeudaSinHipotBanco(Double deudaSinHipotBanco) {
		this.deudaSinHipotBanco = deudaSinHipotBanco;
	}
	/**
	 * @return el deudaSinHipotSBIF
	 */
	public Double getDeudaSinHipotSBIF() {
		return deudaSinHipotSBIF;
	}
	/**
	 * @param deudaSinHipotSBIF el deudaSinHipotSBIF a establecer
	 */
	public void setDeudaSinHipotSBIF(Double deudaSinHipotSBIF) {
		this.deudaSinHipotSBIF = deudaSinHipotSBIF;
	}
	/**
	 * @return el deudaACHEL
	 */
	public Double getDeudaACHEL() {
		return deudaACHEL;
	}
	/**
	 * @param deudaACHEL el deudaACHEL a establecer
	 */
	public void setDeudaACHEL(Double deudaACHEL) {
		this.deudaACHEL = deudaACHEL;
	}
	/**
	 * @return el nivelVenta
	 */
	public String getNivelVenta() {
		return nivelVenta;
	}
	/**
	 * @param nivelVenta el nivelVenta a establecer
	 */
	public void setNivelVenta(String nivelVenta) {
		this.nivelVenta = nivelVenta;
	}
	/**
	 * @return el idMotivoModifRating
	 */
	public Integer getIdMotivoModifRating() {
		return idMotivoModifRating;
	}
	/**
	 * @param idMotivoModifRating el idMotivoModifRating a establecer
	 */
	public void setIdMotivoModifRating(Integer idMotivoModifRating) {
		this.idMotivoModifRating = idMotivoModifRating;
	}
	/**
	 * @return el idRatingNegocio
	 */
	public Long getIdRatingNegocio() {
		return idRatingNegocio;
	}
	/**
	 * @param idRatingNegocio el idRatingNegocio a establecer
	 */
	public void setIdRatingNegocio(Long idRatingNegocio) {
		this.idRatingNegocio = idRatingNegocio;
	}
	/**
	 * @return el idBanca
	 */
	public Integer getIdBanca() {
		return idBanca;
	}
	/**
	 * @param idBanca el idBanca a establecer
	 */
	public void setIdBanca(Integer idBanca) {
		this.idBanca = idBanca;
	}
	/**
	 * @return el idRatingComportamiento
	 */
	public Long getIdRatingComportamiento() {
		return idRatingComportamiento;
	}
	/**
	 * @param idRatingComportamiento el idRatingComportamiento a establecer
	 */
	public void setIdRatingComportamiento(Long idRatingComportamiento) {
		this.idRatingComportamiento = idRatingComportamiento;
	}
	/**
	 * @return el comentarioModificacion
	 */
	public String getComentarioModificacion() {
		return comentarioModificacion;
	}
	/**
	 * @param comentarioModificacion el comentarioModificacion a establecer
	 */
	public void setComentarioModificacion(String comentarioModificacion) {
		this.comentarioModificacion = comentarioModificacion;
	}
	/**
	 * @return el montoActivos
	 */
	public Double getMontoActivos() {
		return montoActivos;
	}
	/**
	 * @param montoActivos el montoActivos a establecer
	 */
	public void setMontoActivos(Double montoActivos) {
		this.montoActivos = montoActivos;
	}
	/**
	 * @return el rtgIndividualConfirmado
	 */
	public Boolean getRtgIndividualConfirmado() {
		return rtgIndividualConfirmado;
	}
	/**
	 * @param rtgIndividualConfirmado el rtgIndividualConfirmado a establecer
	 */
	public void setRtgIndividualConfirmado(Boolean rtgIndividualConfirmado) {
		this.rtgIndividualConfirmado = rtgIndividualConfirmado;
	}
	/**
	 * @return el rtgFinancieroConfirmado
	 */
	public Boolean getRtgFinancieroConfirmado() {
		return rtgFinancieroConfirmado;
	}
	/**
	 * @param rtgFinancieroConfirmado el rtgFinancieroConfirmado a establecer
	 */
	public void setRtgFinancieroConfirmado(Boolean rtgFinancieroConfirmado) {
		this.rtgFinancieroConfirmado = rtgFinancieroConfirmado;
	}
	/**
	 * @return el rtgProyectadoConfirmado
	 */
	public Boolean getRtgProyectadoConfirmado() {
		return rtgProyectadoConfirmado;
	}
	/**
	 * @param rtgProyectadoConfirmado el rtgProyectadoConfirmado a establecer
	 */
	public void setRtgProyectadoConfirmado(Boolean rtgProyectadoConfirmado) {
		this.rtgProyectadoConfirmado = rtgProyectadoConfirmado;
	}
	/**
	 * @return el rtgNegocioConfirmado
	 */
	public Boolean getRtgNegocioConfirmado() {
		return rtgNegocioConfirmado;
	}
	/**
	 * @param rtgNegocioConfirmado el rtgNegocioConfirmado a establecer
	 */
	public void setRtgNegocioConfirmado(Boolean rtgNegocioConfirmado) {
		this.rtgNegocioConfirmado = rtgNegocioConfirmado;
	}
	/**
	 * @return el rtgComportamientoConfirmado
	 */
	public Boolean getRtgComportamientoConfirmado() {
		return rtgComportamientoConfirmado;
	}
	/**
	 * @param rtgComportamientoConfirmado el rtgComportamientoConfirmado a establecer
	 */
	public void setRtgComportamientoConfirmado(Boolean rtgComportamientoConfirmado) {
		this.rtgComportamientoConfirmado = rtgComportamientoConfirmado;
	}
	
	/**
	 * @return el rut
	 */
	public String getRut() {
		return rut;
	}
	/**
	 * @return el estado
	 */
	public String getEstado() {
		return estado;
	}
	/**
	 * @return el banca
	 */
	public String getBanca() {
		return banca;
	}
	/**
	 * @return el nombreResponsable
	 */
	public String getNombreResponsable() {
		return nombreResponsable;
	}
	/**
	 * @return el fechaEEFF
	 */
	public Date getFechaEEFF() {
		return fechaEEFF;
	}
	/**
	 * @return el responsableRatingFinanciero
	 */
	public String getResponsableRatingFinanciero() {
		return responsableRatingFinanciero;
	}
	/**
	 * @param responsableRatingFinanciero el responsableRatingFinanciero a establecer
	 */
	public void setResponsableRatingFinanciero(String responsableRatingFinanciero) {
		this.responsableRatingFinanciero = responsableRatingFinanciero;
	}
	/**
	 * @return el responsableRatingNegocio
	 */
	public String getResponsableRatingNegocio() {
		return responsableRatingNegocio;
	}
	/**
	 * @param responsableRatingNegocio el responsableRatingNegocio a establecer
	 */
	public void setResponsableRatingNegocio(String responsableRatingNegocio) {
		this.responsableRatingNegocio = responsableRatingNegocio;
	}
	/**
	 * @return el responsableRatingComportamiento
	 */
	public String getResponsableRatingComportamiento() {
		return responsableRatingComportamiento;
	}
	/**
	 * @param responsableRatingComportamiento el responsableRatingComportamiento a establecer
	 */
	public void setResponsableRatingComportamiento(String responsableRatingComportamiento) {
		this.responsableRatingComportamiento = responsableRatingComportamiento;
	}
	/**
	 * @param rut el rut a establecer
	 */
	public void setRut(String rut) {
		this.rut = rut;
	}
	/**
	 * @param estado el estado a establecer
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}
	/**
	 * @param banca el banca a establecer
	 */
	public void setBanca(String banca) {
		this.banca = banca;
	}
	/**
	 * @param nombreResponsable el nombreResponsable a establecer
	 */
	public void setNombreResponsable(String nombreResponsable) {
		this.nombreResponsable = nombreResponsable;
	}
	/**
	 * @param fechaEEFF el fechaEEFF a establecer
	 */
	public void setFechaEEFF(Date fechaEEFF) {
		this.fechaEEFF = fechaEEFF;
	}
	public void setIdRatingProyectado(Long idRatingProyectado) {
		this.idRatingProyectado = idRatingProyectado;
	}
	public Long getIdRatingProyectado() {
		return idRatingProyectado;
	}


	public Date getPeriodoVac0() {
		return periodoVac0;
	}


	public void setPeriodoVac0(Date periodoVac0) {
		this.periodoVac0 = periodoVac0;
	}


	public Date getPeriodoVac1() {
		return periodoVac1;
	}


	public void setPeriodoVac1(Date periodoVac1) {
		this.periodoVac1 = periodoVac1;
	}


	public Date getPeriodoVac2() {
		return periodoVac2;
	}


	public void setPeriodoVac2(Date periodoVac2) {
		this.periodoVac2 = periodoVac2;
	}


	public void setResponsableRatingProyectado(String responsableRatingProyectado) {
		this.responsableRatingProyectado = responsableRatingProyectado;
	}


	public String getResponsableRatingProyectado() {
		return responsableRatingProyectado;
	}


	/**
	 * @return el idRatingGarante
	 */
	public Long getIdRatingGarante() {
		return idRatingGarante;
	}


	/**
	 * @param idRatingGarante el idRatingGarante a establecer
	 */
	public void setIdRatingGarante(Long idRatingGarante) {
		this.idRatingGarante = idRatingGarante;
	}


	/**
	 * @return el ratingGarante
	 */
	public Double getRatingGarante() {
		return ratingGarante;
	}


	/**
	 * @param ratingGarante el ratingGarante a establecer
	 */
	public void setRatingGarante(Double ratingGarante) {
		this.ratingGarante = ratingGarante;
	}


	/**
	 * @return el fechaRatingGarante
	 */
	public Date getFechaRatingGarante() {
		return fechaRatingGarante;
	}


	/**
	 * @param fechaRatingGarante el fechaRatingGarante a establecer
	 */
	public void setFechaRatingGarante(Date fechaRatingGarante) {
		this.fechaRatingGarante = fechaRatingGarante;
	}
	
	public Date getFechaActualizacionSiebel() {
		return fechaActualizacionSiebel;
	}

	public void setFechaActualizacionSiebel(Date fechaActualizacionSiebel) {
		this.fechaActualizacionSiebel = fechaActualizacionSiebel;
	}
	
	public void addAlerta(String alerta) {
		alertasComponentes.add(alerta);
	}
	
	public void setAlertasComponentes(List alertas) {
		this.alertasComponentes = alertas;
	}
	
	public List getAlertasComponentes() {
		return alertasComponentes;
	}
	
	public boolean existenAlertasComponentes() {
		return (alertasComponentes != null && !alertasComponentes.isEmpty());
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ID-CLIENTE: ").append(idCliente).append('\n');
		sb.append("ID-RATING: ").append(idRating).append('\n');
		sb.append("ID-BANCA: ").append(idBanca).append('\n');
		return sb.toString();
	}


	public void setIdTipoVaciadoRatingNegocio(Integer idTipoVaciadoRatingNegocio) {
		this.idTipoVaciadoRatingNegocio = idTipoVaciadoRatingNegocio;
	}


	public Integer getIdTipoVaciadoRatingNegocio() {
		return idTipoVaciadoRatingNegocio;
	}


	public Date getFechaAvance() {
		return fechaAvance;
	}


	public void setFechaAvance(Date fechaAvance) {
		this.fechaAvance = fechaAvance;
	}
	
}
