package com.bch.sefe.rating.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.bch.sefe.ConstantesSEFE;
import com.bch.sefe.ErrorMessagesSEFE;
import com.bch.sefe.agricola.srv.GestorAgricola;
import com.bch.sefe.agricola.srv.impl.GestorAgricolaImpl;
import com.bch.sefe.agricola.vo.ParamGanaderia;
import com.bch.sefe.agricola.vo.ParamPlantacion;
import com.bch.sefe.agricola.vo.ParamPlantacionAno;
import com.bch.sefe.agricola.vo.ParamProductoGanaderia;
import com.bch.sefe.agricola.vo.ProductoAgricola;
import com.bch.sefe.comun.SEFEContext;
import com.bch.sefe.comun.ServicioCalculo;
import com.bch.sefe.comun.ServicioClientes;
import com.bch.sefe.comun.impl.ServicioCalculoImpl;
import com.bch.sefe.comun.impl.ServicioClientesImpl;
import com.bch.sefe.comun.srv.ConsultaServicios;
import com.bch.sefe.comun.srv.GestorClasificaciones;
import com.bch.sefe.comun.srv.GestorServicioClientes;
import com.bch.sefe.comun.srv.GestorUsuarios;
import com.bch.sefe.comun.srv.GestoresNegocioFactory;
import com.bch.sefe.comun.srv.impl.ConsultaServiciosImplCache;
import com.bch.sefe.comun.srv.impl.GestorClasificacionesImpl;
import com.bch.sefe.comun.srv.impl.GestorServicioClientesImpl;
import com.bch.sefe.comun.srv.impl.GestorUsuariosImpl;
import com.bch.sefe.comun.vo.Archivo;
import com.bch.sefe.comun.vo.Clasificacion;
import com.bch.sefe.comun.vo.Cliente;
import com.bch.sefe.comun.vo.PeriodoRating;
import com.bch.sefe.comun.vo.RatingProyectadoESP;
import com.bch.sefe.comun.vo.TipoCambio;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.constructora.srv.GestorConstructoraInmobiliaria;
import com.bch.sefe.constructora.srv.impl.GestorConstructoraInmobilariaImpl;
import com.bch.sefe.exception.BusinessOperationException;
import com.bch.sefe.rating.ConstantesRating;
import com.bch.sefe.rating.ServicioRatingProyectadoConstructora;
import com.bch.sefe.rating.dao.RatingFinancieroDAO;
import com.bch.sefe.rating.dao.RatingIndividualDAO;
import com.bch.sefe.rating.dao.impl.RatingFinancieroDAOImpl;
import com.bch.sefe.rating.dao.impl.RatingIndividualDAOImpl;
import com.bch.sefe.rating.srv.AlgoritmoRatingFinanciero;
import com.bch.sefe.rating.srv.GestorAlertasRtgIndividual;
import com.bch.sefe.rating.srv.GestorComponentesRating;
import com.bch.sefe.rating.srv.GestorRating;
import com.bch.sefe.rating.srv.GestorRatingFinanciero;
import com.bch.sefe.rating.srv.GestorRatingIndividual;
import com.bch.sefe.rating.srv.GestorRatingProyectado;
import com.bch.sefe.rating.srv.ValidadorRatingProyectado;
import com.bch.sefe.rating.srv.impl.GestorComponentesRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingFinancieroImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingIndividualImpl;
import com.bch.sefe.rating.srv.impl.GestorRatingProyectadoImpl;
import com.bch.sefe.rating.srv.impl.RatingUtil;
import com.bch.sefe.rating.srv.impl.ValidadorRatingProyectadoImpl;
import com.bch.sefe.rating.vo.BalanceInmobiliario;
import com.bch.sefe.rating.vo.HojaSoe;
import com.bch.sefe.rating.vo.IndicadorSoe;
import com.bch.sefe.rating.vo.MatrizFinanciera;
import com.bch.sefe.rating.vo.PlantillaRating;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.Soe;
import com.bch.sefe.servicios.impl.ConfigManager;
import com.bch.sefe.servicios.impl.MessageManager;
import com.bch.sefe.util.FormatUtil;
import com.bch.sefe.vaciados.ServicioVaciados;
import com.bch.sefe.vaciados.impl.ServicioVaciadosImpl;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.srv.impl.POIExcelReader;
import com.bch.sefe.vaciados.vo.Vaciado;

public class ServicioRatingProyectadoConstructoraImpl implements ServicioRatingProyectadoConstructora{
	
	GestorConstructoraInmobiliaria gestorConstr = new GestorConstructoraInmobilariaImpl();
	ServicioCalculo servicioCalculo = new ServicioCalculoImpl();
	
	final static Logger log = Logger.getLogger(ServicioRatingProyectadoConstructoraImpl.class);


	
	private RatingProyectadoESP calcularProyeccion(String rutCliente, Long idRating, Long idProy, String logUsu, String valorDefault) {
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();

		// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);

		GestorVaciados gestorVac = new GestorVaciadosImpl();
		
		// se crean los periodos y se pasan al rating proyectado
		PeriodoRating periodoP = new PeriodoRating();
		PeriodoRating periodo0 = new PeriodoRating();

		Vaciado vac0 = gestorVac.buscarVaciado(rtgFinanciero.getIdVaciado0());
		periodo0.setPeriodo(vac0.getPeriodo());
		proyeccion.setPeriodoProy(periodoP);
		proyeccion.setPeriodo0(periodo0);
		proyeccion.setRatingFinanciero(rtgFinanciero);

		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndiv = gestorIndiv.buscarRatingIndividual(buscarIdClientePorRut(rutCliente), idRating);

	
		String keyImplementacionAlgoritmo =  ConstantesSEFE.KEY_ALGORITMO_RTG_PROYECTADO + ConstantesSEFE.PUNTO + ConstantesSEFE.KEY_DEFAULT;

		AlgoritmoRatingFinanciero algoritmo = (AlgoritmoRatingFinanciero) ConfigManager.getInstanceOf(keyImplementacionAlgoritmo);

		// lista de los vaciados para el financiero proyectado
		List vaciados = new ArrayList();
			// los meses del vaciado de cierre
		int mesesVacCierre = vac0.getMesesPer().intValue();
		// se determina el tipo de combinacion
		String combinacion = RatingUtil.getCombinacionPeriodos(vaciados);

		MatrizFinanciera matriz = gestorFinanciero.obtenerMatrizFinanciera(rtgFinanciero.getIdMatriz(), combinacion, mesesVacCierre);
		if (matriz == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_PROY_ERROR_MATRIZ_VIGENTE_NO_EXISTE));
		}
		
		rtgFinanciero = gestorFinanciero.actualizarRatingProyectado(matriz.getIdBanca(), rtgFinanciero);
		proyeccion.setRatingFinanciero(rtgFinanciero);
		algoritmo.calcularRating(vac0, null, null, ratingIndiv.getIdBanca(), matriz, rtgFinanciero);
		
		//Se evalua la nota tope del rating proyectado
		rtgFinanciero.setNotaFinanciera(RatingUtil.evalTope(matriz, rtgFinanciero.getNotaFinanciera()));

		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerPrimerUsuario(logUsu);

		proyeccion.getRatingFinanciero().setResponsable(usr.getCodigoUsuario());
		proyeccion.getRatingFinanciero().setIdUsuario(usr.getUsuarioId());

		return proyeccion;
	}

	public RatingProyectadoESP confirmarProyeccion(String rutCliente,String logUsu, Long idRating, Long idProy, String fechaAvance) {
		if (idProy == null){
			throw new BusinessOperationException ("Para confirmar, primero se debe calcular");
		}
		Vaciado vacUltPeriodoProy = null;
		Long idCte = buscarIdClientePorRut(rutCliente);
		GestorRatingIndividual gestorRatingIndividual = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndividual = gestorRatingIndividual.buscarRatingIndividual(idCte, idRating);
		if (idProy != null) {
			GestorRatingFinanciero gestorRatingFinanciero = new GestorRatingFinancieroImpl();
			RatingFinanciero ratingProy = null;
			ratingProy = gestorRatingFinanciero.obtenerRating(idProy);
			if (ratingProy != null && ratingProy.getIdVaciado0() != null) {
				GestorVaciados gestVaciados = new GestorVaciadosImpl();

				vacUltPeriodoProy = gestVaciados.buscarVaciado(ratingProy.getIdVaciado0());
				GestorRatingProyectado gestorRatingProy = new GestorRatingProyectadoImpl();
				if ((vacUltPeriodoProy == null || !gestorRatingProy.esVaciadoVigente(vacUltPeriodoProy, ratingIndividual.getIdBanca(),ratingIndividual.getFechaAvance() ))) {
					throw new BusinessOperationException ("Cuadro de Obras con fecha avance".concat(FormatUtil.formatDate(ratingIndividual.getFechaAvance())).concat(" excede el tiempo de  vigencia"));
				}
			}
		}
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();
		GestorConstructoraInmobiliaria gestorContru = new GestorConstructoraInmobilariaImpl();
		// se actualiza el rating individual
		// ValidadorVaciados validador = new ValidadorVaciadosImpl();

		// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);
		GestorUsuarios gestorUsr = new GestorUsuariosImpl();
		Usuario usr = gestorUsr.obtenerPrimerUsuario(logUsu);
		Soe soe = gestorContru.obtenerSoe(idCte, idRating);
		soe.setIdEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);
		gestorContru.actualizarSoe(soe);
		rtgFinanciero.setResponsable(usr.getCodigoUsuario());
		rtgFinanciero.setIdUsuario(usr.getUsuarioId());
		rtgFinanciero.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE);
		rtgFinanciero.setFechaRating(new Date());
		rtgFinanciero = gestorFinanciero.actualizarRatingProyectado(ratingIndividual.getIdBanca(), rtgFinanciero);

		if (log.isInfoEnabled()) {
			log.info(MessageFormat
					.format("Rating proyectado #{0} confirmado!", new String[] { rtgFinanciero.getIdProyectado().toString() }));
		}
		// ============================================================================
		
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		gestorIndiv.actualizarRatingProyectado(idCte, idRating, rtgFinanciero);

		// se calculan los valores del rating individual
		gestorIndiv.calcularRating(idCte, idRating);
		// ============================================================================

		proyeccion =  this.consultarProyeccion(rutCliente, idRating, idProy, logUsu,
				ConstantesRating.MODO_CONSULTA_DEFAULT);
		proyeccion.setFechaBalance(soe.getFechaAvance());
		proyeccion.setIdUnidad(soe.getIdUnidadMedida());
		proyeccion.setIdMoneda(soe.getIdMoneda());
		//RatingIndividual rtgIndividual = gestorIndiv.buscarRatingIndividual(idCte, idRating);

		return proyeccion;
	}
	
	private void calcularHojaSoe(Soe soe, String rutCliente, Long idRatingInd) {
		GestorServicioClientes gestorCliente = new GestorServicioClientesImpl();
		GestorRating gestorRating = new GestorRatingImpl();
		// se verifica el rating financiero
		// se obtiene el cliente consultado
		Cliente cliente = gestorCliente.obtenerClientePorRut(rutCliente);
		Long idCliente=new Long(cliente.getClienteId());
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndiv = gestorIndiv.consultaRatingSugerido(idCliente, idRatingInd);
//		if (ratingIndiv.getIdRatingFinanciero() == null){
//			throw new BusinessOperationException(ErrorMessagesSEFE.ERR_PROYECTADO_NO_EXISTE_FINANCIERO);
//		}
		Vaciado vaciadosRatingProy = gestorRating.buscarVaciadoParaRating(rutCliente, ratingIndiv.getIdBanca());
		if (vaciadosRatingProy == null){
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.MSG3_MODELO_RATING_PROYECTADO));
		}
		gestorConstr.calcularHojaSOE(soe, idCliente,idRatingInd,vaciadosRatingProy.getIdVaciado());
		
	}
	
	

	public void cargarParametrosAgricola(Archivo archivo, String logOperador, String rutCliente) {
		final Integer ID_LECHE = Integer.valueOf("6837");
		final Integer ID_LANA = Integer.valueOf("6836");
		POIExcelReader reader = new POIExcelReader();
		GestorAgricola gestorAgricola = new GestorAgricolaImpl();
		gestorAgricola.caducarParametros();
		Archivo file = (Archivo) archivo;
		ArrayList parametrosPlantacion = reader.getFileAsList("ParamPlantacion", file);
		transformVoParametros(parametrosPlantacion,gestorAgricola);
		String repetidosPlantacion = checkRepetidos(parametrosPlantacion);
		if (!repetidosPlantacion.equals("")){
			throw new BusinessOperationException("Existen elementos repetidos en el cuadro Agricola");
		}
		for (Iterator iterator = parametrosPlantacion.iterator(); iterator.hasNext();) {
			ParamPlantacion paramPlantacion = (ParamPlantacion) iterator.next();
			Long idParamPlantacion = gestorAgricola.insertarParametroAgricola(paramPlantacion);
			ArrayList listParamPlantacionAno=getVosParamPlantacionAno(paramPlantacion, idParamPlantacion);
			for (Iterator iterator2 = listParamPlantacionAno.iterator(); iterator2.hasNext();) {
				ParamPlantacionAno paramPlantacionAno = (ParamPlantacionAno) iterator2.next();
				gestorAgricola.insertarParametroAgricolaAno(paramPlantacionAno);				
			}
		}
		ArrayList parametrosGanaderos = reader.getFileAsList("ParamGanaderia", file);
		transformVoParametrosGanaderos(parametrosGanaderos, gestorAgricola);
		String repetidosGanaderia = checkRepetidos(parametrosGanaderos);
		if (!repetidosGanaderia.equals("")){
			throw new BusinessOperationException("Existen elementos repetidos en el cuadro Ganadero");
		}
		for (Iterator iterator = parametrosGanaderos.iterator(); iterator.hasNext();) {
			ParamGanaderia paramGanaderia = (ParamGanaderia) iterator.next();
			gestorAgricola.insertarParametroGanadero(paramGanaderia);
		}
		ArrayList parametrosProdGanaderos = reader.getFileAsList("ParamProdGanaderia", file);
		transformVoParametrosProdGanderos(parametrosProdGanaderos , ID_LANA);
		ArrayList parametrosProdGanaderosB = reader.getFileAsList("ParamProdGanaderiaB", file);
		transformVoParametrosProdGanderos(parametrosProdGanaderosB , ID_LECHE);
		parametrosProdGanaderos.addAll(parametrosProdGanaderosB);
		for (Iterator iterator = parametrosProdGanaderos.iterator(); iterator.hasNext();) {
			ParamProductoGanaderia paramProductoGanaderia = (ParamProductoGanaderia) iterator.next();
			if (paramProductoGanaderia.getNombre()==null){
				throw new BusinessOperationException("Debe Ingresar la informacion de Productos Ganaderos ");
			}
			gestorAgricola.insertarParametroProductoGanadero(paramProductoGanaderia);
		}
	}
	public String checkRepetidos(ArrayList parametros){
		HashMap repetidos = new HashMap();
		HashMap elementos = new HashMap();
		for (int i = 0; i < parametros.size();i++){
			Object object = parametros.get(i);
			ParamPlantacion paramPlantacion = null;
			ParamGanaderia paramGanaderia = null;
			ProductoAgricola productoAgricola = null;
			if (object instanceof ParamPlantacion){
				paramPlantacion = (ParamPlantacion) object;
				if (elementos.get(paramPlantacion.getNombre()) == null){
					elementos.put(paramPlantacion.getNombre(), paramPlantacion.getNombre());
				}else{
					if (repetidos.get(paramPlantacion.getNombre()) == null){
						repetidos.put(paramPlantacion.getNombre(),paramPlantacion.getNombre());
					}
				}
			}else if (object instanceof ParamGanaderia){
				paramGanaderia = (ParamGanaderia) object;
				if (elementos.get(paramGanaderia.getNombre()) == null){
					elementos.put(paramGanaderia.getNombre(), paramGanaderia.getNombre());
				}else{
					if (repetidos.get(paramGanaderia.getNombre()) == null){
						repetidos.put(paramGanaderia.getNombre(),paramGanaderia.getNombre());
					}
				}
			}else if (object instanceof ProductoAgricola){
				productoAgricola = (ProductoAgricola) object;
				if (elementos.get(productoAgricola.getNombreProducto()) == null){
					elementos.put(productoAgricola.getNombreProducto(), productoAgricola.getNombreProducto());
				}else{
					if (repetidos.get(productoAgricola.getNombreProducto()) == null){
						repetidos.put(productoAgricola.getNombreProducto(),productoAgricola.getNombreProducto());
					}
				}
			}
		}
		StringBuffer sf = new StringBuffer();
		for (Iterator it = repetidos.entrySet().iterator();it.hasNext();){
			Entry e = (Entry) it.next();
			sf.append(e.getValue()).append(" ");
		}
		return sf.toString();
	}
	public void transformVoParametrosProdGanderos(ArrayList parametrosProdGanaderos, Integer tipo){
		for (Iterator iterator = parametrosProdGanaderos.iterator(); iterator.hasNext();) {
			ParamProductoGanaderia paramProductoGanaderia = (ParamProductoGanaderia) iterator.next();
			paramProductoGanaderia.setIdParam(tipo);
		}
	}
	public void transformVoParametrosGanaderos(ArrayList parametrosGanaderos, GestorAgricola gestorAgricola){
		final Integer GRUPO_PRODUCTOS = Integer.valueOf("6785");
		final Integer GRUPO_CATEGORIAS = Integer.valueOf("6810");
		GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
		for (Iterator iterator = parametrosGanaderos.iterator(); iterator.hasNext();) {
			ParamGanaderia paramGanaderia = (ParamGanaderia) iterator.next();
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_PRODUCTOS, paramGanaderia.getNombre());
			if (clasifProducto == null){
				Clasificacion clasificacion = new Clasificacion();
				clasificacion.setIdGrupoClasificacion(GRUPO_PRODUCTOS);
				clasificacion.setNombre(paramGanaderia.getNombre());
				Integer idClasif=gestorAgricola.insertarClasificacionProducto(clasificacion);
				paramGanaderia.setIdParam(idClasif);
			}else{
				paramGanaderia.setIdParam(clasifProducto.getIdClasif());
			}
			Clasificacion clasifCategoria = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_CATEGORIAS, paramGanaderia.getCategoria().toUpperCase());
			if (clasifCategoria != null){
				paramGanaderia.setIdCategoria(clasifCategoria.getIdClasif());
			}
		}
	}
	public ArrayList getVosParamPlantacionAno(ParamPlantacion paramPlantacion, Long idParamPlantacion){
		ArrayList finalList = new ArrayList();
		ParamPlantacionAno paramPlantacionAno = new ParamPlantacionAno();
		if (paramPlantacion.getRend1Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("1"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend1Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto1Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend2Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("2"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend2Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto2Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend3Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("3"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend3Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto3Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend4Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("4"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend4Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto4Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend5Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("5"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend5Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto5Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend6Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("6"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend6Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto6Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend7Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("7"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend7Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto7Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend8Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("8"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend8Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto8Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend9Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("9"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend9Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto9Ano());
			finalList.add(paramPlantacionAno);
		}
		if (paramPlantacion.getRend10Ano()!=null){
			paramPlantacionAno = new ParamPlantacionAno();
			paramPlantacionAno.setIdAno(Integer.valueOf("10"));
			paramPlantacionAno.setIdParamPlantacion(idParamPlantacion);
			paramPlantacionAno.setRendimiento(paramPlantacion.getRend10Ano());
			paramPlantacionAno.setCosto(paramPlantacion.getCosto10Ano());
			finalList.add(paramPlantacionAno);
		}
		return finalList;
	}
	public void transformVoParametros (ArrayList parametros,GestorAgricola gestorAgricola){
		final Integer GRUPO_PRODUCTOS = Integer.valueOf("6650");
		final Integer GRUPO_VULNERABILIDAD= Integer.valueOf("6760");
		final Integer GRUPO_PER_CRIT= Integer.valueOf("6750");
		for (Iterator iterator = parametros.iterator(); iterator.hasNext();) {
			ParamPlantacion paramPlantacion = (ParamPlantacion) iterator.next();
			GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
			Clasificacion clasifProducto = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_PRODUCTOS, paramPlantacion.getNombre());
			if (clasifProducto == null){
				Clasificacion clasificacion = new Clasificacion();
				clasificacion.setIdGrupoClasificacion(GRUPO_PRODUCTOS);
				clasificacion.setNombre(paramPlantacion.getNombre());
				Integer idClasif=gestorAgricola.insertarClasificacionProducto(clasificacion);
				paramPlantacion.setIdParam(idClasif);
			}else{
				paramPlantacion.setIdParam(clasifProducto.getIdClasif());
			}
			Clasificacion clasifVulner = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_VULNERABILIDAD, paramPlantacion.getClasifVulClim());
			if (clasifVulner != null){
				paramPlantacion.setIdVulClim(clasifVulner.getIdClasif());
			}
			Clasificacion clasifPerCrit = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_PER_CRIT, paramPlantacion.getPeriodoCriticoNecesidades());
			if (clasifPerCrit != null){
				paramPlantacion.setIdPerCrit(clasifPerCrit.getIdClasif());
			}
			if (paramPlantacion.getVentaContrato().equalsIgnoreCase("NO")){
				paramPlantacion.setIdVentaContrato(Integer.valueOf("0"));
			}else{
				paramPlantacion.setIdVentaContrato(Integer.valueOf("1"));
			}
		}
	}
	

	public RatingProyectadoESP generaProyeccion(String rutCliente, Long idRatingInd ,String logOperador, Date fechaAvance, Integer idBanca) {
		ValidadorRatingProyectado validador = new ValidadorRatingProyectadoImpl();
		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRatingInd);
		Long idCte = buscarIdClientePorRut(rutCliente);
		Long idUsr = buscarIdUsuarioPorNombre(logOperador);
		Soe soe = gestorConstr.obtenerSoe(idCte, fechaAvance);
		// si el soe está asociado a otro rating entonces se lanza una excepcion de negocio
		validador.esPosibleUtilizarSoe(soe.getSeqRtg(), idRatingInd, soe.getIdEstado());
		gestorConstr.asociarSoeRatingIndividual(idCte, fechaAvance, idRatingInd);
		gestorConstr.actualizarSoeCalculo(idCte, fechaAvance,idUsr);
		this.calcularHojaSoe(soe, rutCliente, idRatingInd);
		RatingProyectadoESP proyectado=  crearProyectado(rutCliente, idRatingInd ,logOperador);
		proyectado =  generarProyeccion(rutCliente, idRatingInd ,logOperador, proyectado.getIdRating());
		proyectado =  consultarProyeccion(rutCliente, idRatingInd,proyectado.getIdRating(), logOperador, null);
		return proyectado;
	}
	
	


	public RatingProyectadoESP crearProyectado(String rutCliente, Long idRating,  String logUsu) {
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		GestorRating gestorRating = new GestorRatingImpl();
		if (log.isDebugEnabled()) {
			log.debug("Creando vaciado para rating proyectado...");
		}
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();
		Long idCte = buscarIdClientePorRut(rutCliente);
		Long idUsr = buscarIdUsuarioPorNombre(logUsu);
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();
		RatingIndividual ratingIndiv = gestorIndiv.consultaRatingSugerido(idCte, idRating);

//		String mensaje = validarVigenciaProyectado(ratingIndiv, rutCliente);
//		 if(mensaje != null)
//		 {
//			 throw new BusinessOperationException(mensaje);
//		 }
		
		if (ratingIndiv.getIdRatingProyectado() != null) {
			RatingProyectadoESP proyectado = consultarProyeccion(rutCliente, idRating, ratingIndiv.getIdRatingProyectado(), logUsu, null); 
			if (proyectado.isMatrizVigente()) {
				return proyectado; 
			}
		}

		Vaciado vaciadosRatingProy = null;
		try {
			 vaciadosRatingProy = gestorRating.buscarVaciadoParaRating(rutCliente, ratingIndiv.getIdBanca());
			 if (vaciadosRatingProy == null ) {
					proyeccion.agregarMensaje(MessageManager.getMessage(ConstantesSEFE.MSG3_MODELO_RATING_PROYECTADO));
					return proyeccion;
			 }
			 
		} catch (SEFERatingProyectadoException ex) {
			if (log.isInfoEnabled()) {
				log.info("Error al determinar la base de proyeccion - " + ex);
			}

			proyeccion.agregarMensaje(MessageManager.getMessage(ex.getMessage()));
			return proyeccion;
		}
	
		RatingFinanciero rtgFinanciero = gestorFinanciero.calcularRating(rutCliente, ratingIndiv.getIdBanca(), vaciadosRatingProy.getIdVaciado(), logUsu);
		
		if (rtgFinanciero.getIdMatriz() == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_PROY_ERROR_MATRIZ_VIGENTE_NO_EXISTE));
		}
		RatingFinanciero ratingBase = new RatingFinanciero();
		ratingBase.setIdMatriz(rtgFinanciero.getIdMatriz());
		// se remueve el id de rating para que se genere uno nuevo para el
		// proyectado
		ratingBase.setIdUsuario(idUsr);
		if (vaciadosRatingProy !=null){
			ratingBase.setIdVaciado0(vaciadosRatingProy.getIdVaciado());
			ratingBase.setFechaBalance(vaciadosRatingProy.getPeriodo());
			ratingBase.setNumeroMeses(vaciadosRatingProy.getMesesPer());
		}
		ratingBase.setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		ratingBase.setFechaRating(new Date());

		MatrizFinanciera matriz = gestorFinanciero.obtenerMatrizFinanciera(rtgFinanciero.getIdMatriz());
		if (matriz.getIdMatrizProy() == null) {
			throw new BusinessOperationException(MessageManager.getMessage(ConstantesSEFE.KEY_RTG_PROY_ERROR_MATRIZ_VIGENTE_NO_EXISTE));
		}
		ratingBase.setIdMatriz(matriz.getIdMatrizProy());

		// se crea la instancia del rating financiero proyectado
		RatingFinanciero ratingProyectado = gestorFinanciero.grabarRatingProyectado(ratingBase);
		proyeccion.setIdRating(ratingProyectado.getIdProyectado());
		if (log.isInfoEnabled()) {
			log.info("Creando rating proyectado #" + ratingProyectado.getIdRating());
		}

		return proyeccion;
	}
	
	private String validarVigenciaProyectado(RatingIndividual rtgInd, String rutCliente)
	{		
		GestorAlertasRtgIndividual gestorAlertas = new GestorAlertasRtgIndividualImpl();
		List alertas = gestorAlertas.obtenerAlertasRtgIndividualModelo(rtgInd.getIdRating(), rutCliente, rtgInd.getIdBanca());
		boolean validacionFechaAvance = true;
		
		// se determina el periodo de vigencia del ultimo vaciado
		GestorConstructoraInmobiliaria constructora = new GestorConstructoraInmobilariaImpl();
		Integer diasVigencia = constructora.obtenerAntiguedadMaximaSoe(rtgInd.getIdBanca());
		
		Calendar vigenteHasta = Calendar.getInstance();
		vigenteHasta.setTime(rtgInd.getFechaAvance());
		vigenteHasta.add(Calendar.DAY_OF_MONTH, diasVigencia.intValue());
		vigenteHasta.set(Calendar.HOUR, 0);
		vigenteHasta.set(Calendar.MINUTE, 0);
		vigenteHasta.set(Calendar.SECOND, 0);
		vigenteHasta.set(Calendar.MILLISECOND, 0);
		
		Calendar fechaActual = Calendar.getInstance();
		fechaActual.set(Calendar.HOUR, 0);
		fechaActual.set(Calendar.MINUTE, 0);
		fechaActual.set(Calendar.SECOND, 0);
		fechaActual.set(Calendar.MILLISECOND, 0);		
		
		validacionFechaAvance = (vigenteHasta.after(fechaActual));
				
		if(rtgInd.getIdEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO))
		{
			for (int i = 0; i < alertas.size(); ++i) {
				String mensaje = (String) alertas.get(i);
				if(!validacionFechaAvance || mensaje.equals(ConstantesSEFE.MSG_ALERTA_VALIDACION_VERSION_VACIADOS))
				{
					return mensaje;
				}
			}
		}
		return null;
	}
	
	
	public RatingProyectadoESP consultarProyeccion(String rutCliente, Long idRating, Long idProy, String logUsu, String modo) {
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();	
		GestorRatingIndividual gestorIndiv= new GestorRatingIndividualImpl();
		Long idCte = buscarIdClientePorRut(rutCliente);
		RatingIndividual ratingInd = gestorIndiv.buscarRatingIndividual(idCte, idRating);
	
		// se busca el financiero vigente para verificar cambios...
		GestorComponentesRating gestorComp = new GestorComponentesRatingImpl();
	//	RatingFinanciero finanVigente = gestorComp.buscarRatingFinacieroVigente(idCte, ratingInd.getIdBanca());
			// se obtiene el rating financiero
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(idProy);
		MatrizFinanciera matriz = gestorFinanciero.obtenerMatrizFinanciera(rtgFinanciero.getIdMatriz());
		proyeccion.setMatrizVigente(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE.equals(matriz.getEstadoId()));

		proyeccion.setRatingFinanciero(rtgFinanciero);

		String modoConsulta = modo;
		if (modoConsulta == null) {
			if (rtgFinanciero.getEstado().equals(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE)) {
				modoConsulta = ConstantesRating.MODO_CONSULTA_DEFAULT;

				if (ConstantesSEFE.ID_CLASIF_ESTADO_RATING_VIGENTE.equals(ratingInd.getIdEstado())
						|| ConstantesSEFE.ID_CLASIF_ESTADO_RATING_HISTORICO.equals(ratingInd.getIdEstado())) {
					modoConsulta = ConstantesRating.MODO_CONSULTA_SOLO_LECTURA;
				}
			} else {
				if (rtgFinanciero.getNotaFinanciera() != null) {
					modoConsulta = ConstantesRating.MODO_CONSULTA_CONFIRMAR;
				} else {
					modoConsulta = ConstantesRating.MODO_CONSULTA_PROYECTAR;
				}
			}
		}

		proyeccion.setModoConsulta(modoConsulta);

		return proyeccion;
	}




	private RatingProyectadoESP generarProyeccion(String rutCliente, Long idRatingInd,  String logUsu, Long idRatingProy) {
		RatingProyectadoESP proyeccion = new RatingProyectadoESP();

		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		// se verifica el rating financiero
		GestorRatingIndividual gestorIndiv = new GestorRatingIndividualImpl();

		SEFEContext.setValue(ConstantesSEFE.SEFE_CTX_ID_RATING_IND, idRatingInd);
		// aqui se calcula el rating proyectado....
		proyeccion = (RatingProyectadoESP) calcularProyeccion(rutCliente, idRatingInd, idRatingProy, logUsu, null);

		Long idCte = buscarIdClientePorRut(rutCliente);
		RatingIndividual ratingInd = gestorIndiv.buscarRatingIndividual(idCte, idRatingInd);
		
		proyeccion.getRatingFinanciero().setEstado(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO);
		RatingFinanciero ratingFinanciero = gestorFinanciero.actualizarRatingProyectado(ratingInd.getIdBanca(), proyeccion.getRatingFinanciero());

		// se guardan los valores intermedios de los calculos y puntajes
		// si no hay rating financiero para el vaciado cabecera entonces se
		// generar un nuevo calculo y nuevo rating financiero
		gestorFinanciero.grabarEvaluacionesProyectado(proyeccion.getRatingFinanciero());

		if (log.isInfoEnabled()) {
			log.info("> > > > Actualizando cuentas con los valores ingresados ");
		}
		
	
		// ============================================================================
		// se actualiza el rating individual con el valor del proyectado
		gestorIndiv.actualizarRatingProyectado(idCte, idRatingInd, proyeccion.getRatingFinanciero());

		// el rating individual borra nota modelo, ponderado y ajustado por
		// tamaño
		ratingInd.setRatingFinalSugerido(null);
		ratingInd.setRatingPreliminar1(null);
		ratingInd.setRatingFinal(null);
		gestorIndiv.actualizarRatingIndividual(ratingInd);
		// ============================================================================

		return proyeccion;
	}
	
	/*
	 * Busca el identificador de usuario a partir del log de usuario (nombre)
	 */
	protected Long buscarIdClientePorRut(String rutCliente) {
		ServicioClientes srvCtes = new ServicioClientesImpl();
		return srvCtes.obtenerIdClientePorRut(rutCliente);
	}
	
	public void insertarIndicadoresSoe(InputStream streamXLS, Soe soe,POIExcelReader reader, GestorRatingProyectado gestor ){
		ArrayList indicadores = reader.getFileAsList("indicadoresSoe", streamXLS);
		for (int i = 0; i < indicadores.size(); i++) {
			IndicadorSoe indSoe = (IndicadorSoe) indicadores.get(i);
			indSoe.setIdParteInvol(soe.getIdParteInvol());
			indSoe.setFechaAvance(soe.getFechaAvance());
			gestor.insertarIndicadorSoe(indSoe);
		}
		try {
			streamXLS.close();
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug("no fue posible cerrar archivo rating proyectado constructora. La aplicacion puede seguir operando");
			}
		}
	}
	
	public List buscarSoeXRut(String rutCliente, Long idRatingIndividual) {
		GestorConstructoraInmobiliaria gestor = new GestorConstructoraInmobilariaImpl();
		GestorServicioClientes gestorClientes  = new GestorServicioClientesImpl();
		Cliente cliente = gestorClientes.obtenerClientePorRut(rutCliente);
		GestorRatingIndividual gestorRating = new GestorRatingIndividualImpl();
		GestorClasificaciones gestorClasif = new GestorClasificacionesImpl();
		List listaSoe = gestor.buscarSoeXRut(rutCliente);
		if (idRatingIndividual == null) {
			return listaSoe;
		}
		List ratingList = (List) gestorRating.buscarRatingsIndividualesPorCliente(null, rutCliente);
		RatingIndividual ratingIndividual = gestorRating.buscarRatingIndividual(new Long(cliente.getClienteId()), idRatingIndividual);
		GestorRatingFinanciero gestorFinanciero = new GestorRatingFinancieroImpl();
		RatingFinanciero rtgFinanciero = gestorFinanciero.obtenerRating(ratingIndividual.getIdRatingProyectado());
		for (int i=0; i < listaSoe.size(); i++) {
			Soe soe = (Soe)listaSoe.get(i);
			// el soe esta asociado al rating individual
			if (soe.getFechaAvance().equals(ratingIndividual.getFechaAvance()) && ratingIndividual.getIdRating().equals(idRatingIndividual)) {
				soe.setSeqRtg(ratingIndividual.getIdRating());
				soe.setIdRatingProyectado(ratingIndividual.getIdRatingProyectado());
				soe.setNota(ratingIndividual.getRatingProyectado());
				if (rtgFinanciero != null) {
					soe.setEstadoRating(gestorClasif.buscarClasificacionPorId(rtgFinanciero.getEstado()).getNombre());
				}
			}
			boolean puedeBorrar = true;
			for (int j = 0; j < ratingList.size(); j++) {
				RatingIndividual rtgIndividual = (RatingIndividual) ratingList.get(j);
				if (soe.getFechaAvance().equals(rtgIndividual.getFechaAvance()) && ConstantesSEFE.BANCA_CONSTRUCTORAS.equals(rtgIndividual.getIdBanca())) {
					puedeBorrar = ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO.equals(rtgIndividual.getIdEstado());
					if (!puedeBorrar) {
						break;
					}
				}
			}
			soe.setPuedeBorrar(new Boolean(puedeBorrar));
		}
		return listaSoe;
	}
	
	public boolean revisarVersionPlantilla(Soe soe, PlantillaRating plantilla){
		if (plantilla != null){
			if (soe.getVersion()!=null){
				if (plantilla.getVersion().equals(soe.getVersion())){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			return true;
		}
	}

	public Boolean ingresarRatingProyectadoConstructora(Archivo archivo,  String logOperador, String rutCliente) {
		final Integer GRUPO_MONEDA = Integer.valueOf("1500");
		final Integer GRUPO_UNIDAD = Integer.valueOf("1600");
		GestorRatingProyectado gestor = new GestorRatingProyectadoImpl();
		POIExcelReader reader = new POIExcelReader();
		Archivo file = (Archivo) archivo;
		String baseXLS = file.getBase64String();
		byte[] byteXLS = Base64.decodeBase64(baseXLS.getBytes());
		InputStream streamXLS = new ByteArrayInputStream(byteXLS);
		ArrayList bis = reader.getFileAsList("CO", streamXLS);
		Date fechaHoy = new Date();
		try {
			streamXLS.close();
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug("no fue posible cerrar archivo rating proyectado constructora. La aplicacion puede seguir operando");
			}
		}
		if (bis.size() > 0) {
			Soe soe = (Soe) bis.get(0);
			GestorAgricola gestorAgricola = new GestorAgricolaImpl();
			PlantillaRating plantilla = gestorAgricola.buscarPlantillaRating(ConstantesSEFE.BANCA_CONSTRUCTORAS);
			if (!revisarVersionPlantilla(soe, plantilla)){
				throw new BusinessOperationException("La plantilla que intenta cargar no corresponde a la \u00faltima versi\u00F3n cargada el : ".concat(FormatUtil.formatDate(plantilla.getFechaActualizacion())));	
			}
			ArrayList detalle = reader.getFileAsList("detalleCO", file);
			if (detalle.size() == 0 || detalle.isEmpty()){
				throw new BusinessOperationException("Cuadro de Obras no contiene informaci\u00F3n para calcular Nota de Rating Proyectado");
			}
			if (soe.getRut() == null || !soe.getRut().equals(rutCliente)){
				throw new BusinessOperationException("El rut del archivo no concuerda con el de la sesi\u00F3n");
			}
			//23-06-2014 ncerda; Se valida que fechaAvance no sea mayor a Fecha Actual.
			if (fechaHoy.before(soe.getFechaAvance())){
				throw new BusinessOperationException(ErrorMessagesSEFE.ERR_PROY_FEC_AV_BI);
			}
			
			if (soe.getFechaAvance() == null || soe.getFechaAvance().toString().equals("Sun Dec 31 00:00:00 CLT 1899")
					|| soe.getFechaAvance().toString().equals("Sun Dec 31 00:00:00 CST 1899")){
				throw new BusinessOperationException("La fecha de avance no puede ser nula");
			}
			List soesBd=buscarSoeXRut(rutCliente, null);
			if (soesBd.size()>0){
				Boolean existe=Boolean.FALSE;
				for (Iterator iterator = soesBd.iterator(); iterator.hasNext();) {
					Soe soeBd = (Soe) iterator.next();
					if (soeBd.getFechaAvance().equals(soe.getFechaAvance())){
						existe=Boolean.TRUE;
					}
				}
				if (existe.booleanValue()){
					return existe;
				}
			}
			Long idUsuario = buscarIdUsuarioPorNombre(logOperador);
			if (idUsuario.equals(null)) {
				throw new BusinessOperationException("El usuario: " + logOperador + "no existe en la aplicacion");
			}
			soe.setIdUsuario(idUsuario);
			Long idCliente = null;
			soe.setRut(rutCliente);
			try {
				idCliente = buscarIdClientePorRut(soe.getRut());
			} catch (NullPointerException e) {
				throw new BusinessOperationException("El rut: " + soe.getRut() + " no existe en la aplicaci�n");
			}
			Double totalDeuda=gestor.obtenerDeudaXFechaAvance(rutCliente, ConstantesSEFE.FLAG_DEUDA_TIPO_SBIF, soe.getFechaAvance());
			soe.setDeuSbif(totalDeuda);
			soe.setIdParteInvol(idCliente);
			GestorClasificaciones gestorClasificaciones = GestoresNegocioFactory.getGestorClasificaciones();
			Clasificacion clasifMoneda = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_MONEDA, soe.getMoneda());
			soe.setIdMoneda(clasifMoneda.getIdClasif());
		//	soe.setSeqRtg(idRatingInd);
			Clasificacion clasifUnidad = gestorClasificaciones.consultaPorCategoriayNombre(GRUPO_UNIDAD, soe.getUnidad());
			soe.setIdUnidadMedida(clasifUnidad.getIdClasif());
			ConsultaServicios servicios = new ConsultaServiciosImplCache();
			ServicioVaciados servicioVaciados = new ServicioVaciadosImpl();
			Date fechaConsultaCambio = servicioVaciados.buscarDiaHabilSiguiente(soe.getFechaAvance());
			if (!clasifMoneda.getIdClasif().equals(ConstantesSEFE.ID_CLASIF_MONEDA_CLP)) {
				TipoCambio tipoCambio = servicios.consultaTipoCambio(fechaConsultaCambio, clasifMoneda.getCodigo());
				soe.setTipoCambio(tipoCambio.getValorObservado());
			} else {
				soe.setTipoCambio(new Double("1"));
			}
			String codigoMonedaUf = ConfigManager.getValueAsString(ConstantesSEFE.KEY_CODIGO_MONEDA_CONSULTA_VALOR_MONEDA + ConstantesSEFE.PUNTO + ConstantesSEFE.ID_CLASIF_MONEDA_UF, null);
			TipoCambio tpoCambioDestino = servicios.consultaTipoCambio(soe.getFechaAvance(), codigoMonedaUf);
			soe.setUf(tpoCambioDestino.getValorObservado());
			gestor.insertarSoe(soe);
			streamXLS = new ByteArrayInputStream(byteXLS);
			insertarHojasSoe(streamXLS,soe, reader, gestor);
//			streamXLS = new ByteArrayInputStream(byteXLS);
//			insertarIndicadoresSoe(streamXLS,soe, reader, gestor);
		}
		return Boolean.FALSE;
	}
	public void actualizarCargaBalanceConstructora(Archivo archivo,  String logOperador, String rutCliente) {
		POIExcelReader reader = new POIExcelReader();
		Archivo file = (Archivo) archivo;
		String baseXLS = file.getBase64String();
		byte[] byteXLS = Base64.decodeBase64(baseXLS.getBytes());
		InputStream streamXLS = new ByteArrayInputStream(byteXLS);
		ArrayList bis = reader.getFileAsList("CO", streamXLS);
		try {
			streamXLS.close();
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug("no fue posible cerrar archivo rating proyectado constructora. La aplicaion puede seguir operando");
			}
		}
		if (bis.size() > 0) {
			Soe soe = (Soe) bis.get(0);
			Long idUsuario = buscarIdUsuarioPorNombre(logOperador);
			if (idUsuario.equals(null)) {
				throw new BusinessOperationException("El usuario: " + logOperador + "no existe en la aplicacion");
			}
			soe.setIdUsuario(idUsuario);
			GestorRatingProyectado gestor = new GestorRatingProyectadoImpl();
			Long idCliente = null;
			soe.setRut(rutCliente);
			try {
				idCliente = buscarIdClientePorRut(soe.getRut());
			} catch (NullPointerException e) {
				throw new BusinessOperationException("El rut: " + soe.getRut() + " no existe en la aplicaci�n");
			}
			soe.setIdParteInvol(idCliente);
			gestor.actualizarUsuarioBalance(idUsuario, idCliente, soe.getFechaAvance(), ConstantesSEFE.KEY_SOE);
			gestor.borrarHojasEIndicadores(idCliente,  soe.getFechaAvance(), ConstantesSEFE.KEY_SOE);
			streamXLS = new ByteArrayInputStream(byteXLS);
			insertarHojasSoe(streamXLS,soe, reader, gestor);
//			streamXLS = new ByteArrayInputStream(byteXLS);
//			insertarIndicadoresSoe(streamXLS,soe, reader, gestor);
		}
	}
	
	/*
	 * Busca el identificador del cliente a partir del rut
	 */
	protected Long buscarIdUsuarioPorNombre(String logUsu) {
		GestorUsuarios gstUsr = new GestorUsuariosImpl();
		return gstUsr.obtenerPrimerUsuario(logUsu).getUsuarioId();
	}
	
	public void insertarHojasSoe(InputStream streamXLS, Soe soe,POIExcelReader reader, GestorRatingProyectado gestor ){
		ArrayList detalle = reader.getFileAsList("detalleCO", streamXLS);
		if (detalle.size() == 0 || detalle.isEmpty()){
			throw new BusinessOperationException("Cuadro de Obras no contiene información para calcular Nota de Rating Proyectado");
		}
		if (detalle.size()>50){
			throw new BusinessOperationException("Cuadro de Obras no puede tener mas de 50 registros");
		}
		String errores = "";
		for (Iterator iterator = detalle.iterator(); iterator.hasNext();) {
			HojaSoe hoja = (HojaSoe) iterator.next();
			errores = errores.concat(validarHojaSoe(hoja, errores));
		}
		if (!errores.equals("")){
			throw new BusinessOperationException(errores);
		}
		for (Iterator iterator = detalle.iterator(); iterator.hasNext();) {
			HojaSoe hoja = (HojaSoe) iterator.next();
			if (!hoja.getNombre().equals("0.0")) {
				hoja.setIdParteInvol(soe.getIdParteInvol());
				hoja.setFechaAvance(soe.getFechaAvance());
				gestor.insertarHojaSoe(hoja);
			}
		}
		try {
			streamXLS.close();
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug("no fue posible cerrar archivo rating proyectado constructora. La aplicacion puede seguir operando");
			}
		}
	}
	public String validarHojaSoe(HojaSoe hojaSoe, String errores){
		String errorFila = "";
		if (hojaSoe.getNombreMandante() == null || hojaSoe.getNombreMandante().equals("-")){
			final String error = "Nombre Mandante no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaSoe.getMandante() == null || hojaSoe.getMandante().equals("-")){
			final String error = "Mandante no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaSoe.getMontoContrato() == null || hojaSoe.getMontoContrato().equals("-")){
			final String error = "Monto del Contrato no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getMontoContrato().doubleValue()<0){
				final String error = "Monto del Contrato debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getMontoAvanceObra() == null || hojaSoe.getMontoAvanceObra().equals("-")){
			final String error = "Monto Avance Obra no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getMontoAvanceObra().doubleValue()<0){
				final String error = "Monto Avance Obra debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getPrcAvanceObra() == null || hojaSoe.getPrcAvanceObra().equals("-")){
			final String error = "% Avance Obra no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getPrcAvanceObra().doubleValue()<0){
				final String error = "% Avance Obra debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getSaldoPorEjecutar() == null || hojaSoe.getSaldoPorEjecutar().equals("-")){
			final String error = "Saldo por Ejecutar no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getSaldoPorEjecutar().doubleValue()<0){
				final String error = "Saldo por Ejecutar debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getPrcPorEjecutar() == null || hojaSoe.getPrcPorEjecutar().equals("-")){
			final String error = "% por Ejecutar no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getPrcPorEjecutar().doubleValue()<0){
				final String error = "% por Ejecutar debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getSaldoPorCobrar() == null || hojaSoe.getSaldoPorCobrar().equals("-")){
			final String error = "Saldo por Cobrar no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getSaldoPorCobrar().doubleValue()<0){
				final String error = "Saldo por Cobrar debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getPrcPorCobrar() == null || hojaSoe.getPrcPorCobrar().equals("-")){
			final String error = "% por Cobrar no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getPrcPorCobrar().doubleValue()<0){
				final String error = "% por Cobrar debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getFechaTermino() == null || hojaSoe.getFechaTermino().equals("-")){
			final String error = "Fecha Termino no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaSoe.getFechaInicio() == null || hojaSoe.getFechaInicio().equals("-")){
			final String error = "Fecha Inicio no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaSoe.getPlazo() == null || hojaSoe.getPlazo().equals("-")){
			final String error = "Plazo no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaSoe.getAvanceLineal() == null || hojaSoe.getAvanceLineal().equals("-")){
			final String error = "Avance Lineal no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getAvanceLineal().doubleValue()<0){
				final String error = "Avance Lineal debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getPrcLineal() == null || hojaSoe.getPrcLineal().equals("-")){
			final String error = "% Avance Lineal no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getPrcLineal().doubleValue()<0){
				final String error = "% Avance Lineal debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getSoeA1Ano() == null || hojaSoe.getSoeA1Ano().equals("-")){
			final String error = "Soe 1 ano no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getSoeA1Ano().doubleValue()<0){
				final String error = "Soe 1 ano debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getPrcSoeA1Ano() == null || hojaSoe.getPrcSoeA1Ano().equals("-")){
			final String error = "% Soe 1 ano no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}else{
			if (hojaSoe.getPrcSoeA1Ano().doubleValue()<0){
				final String error = "% Soe 1 ano debe ser mayor o igual a cero";
				if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
					errorFila = errorFila.concat(error).concat(", ");
				}
			}
		}
		if (hojaSoe.getComplejidadObra() == null || hojaSoe.getComplejidadObra().equals("-")){
			final String error = "Complejidad Obra no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		if (hojaSoe.getKnowHow() == null || hojaSoe.getKnowHow().equals("-")){
			final String error = "KnowHow no puede ser nulo";
			if (errores.indexOf(error)<0 && errorFila.indexOf(error)<0){
				errorFila = errorFila.concat(error).concat(", ");
			}
		}
		return errorFila;
	}

	public Boolean eliminarCuadroObra(String rutCliente, String fechaAvance) {
		// TODO Auto-generated method stub
		GestorConstructoraInmobiliaria gestor = new GestorConstructoraInmobilariaImpl();
		return gestor.eliminarCuadroObras(rutCliente, fechaAvance);
	}


	

}
