package com.bch.sefe.rating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.bch.sefe.rating.dao.RatingGrupalDAO;
import com.bch.sefe.rating.dao.impl.RatingGrupalDAOImpl;
import com.bch.sefe.rating.impl.CatalogoRatingImpl;
import com.bch.sefe.rating.vo.AgrupadorRatings;
import com.bch.sefe.rating.vo.ModIndRtgGrupal;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingGrupal;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.util.FormatUtil;

public class AdaptadorCatalogoRating {

	public XMLDataObject consultarRating(XMLData req) {
		CatalogoRating catalogoRating = new CatalogoRatingImpl();

		// Se obtiene el rol y el rut del cliente
		String rut = (String) ((XMLDataObject) req).getObject(ConstantesSEFE.CONSUL_RTG_KEY_CLIENTE);
		String rol = (String) ((XMLDataObject) req).getObject(ConstantesSEFE.CONSUL_RTG_KEY_ROL);
		Integer plantilla = ((XMLDataObject) req).getInteger(ConstantesSEFE.CONSUL_RTG_KEY_PLANTILLA);

		// Collection objDominio = catalogoRating.consultarRating(rol, rut);
		List agrupadoresRating = catalogoRating.consultarRating(rol, rut);

		XMLDataObject response = crearXMLDataConsultaRatings(agrupadoresRating, rol);

		// Se agrega la perfilacion GUI ya que cada vez que se ingrese al modulo
		// de rating se ejecuta esta operacion
		adjuntarPerfilacionGUI(rol, response);

		return response;
	}

	/*
	 * Busca mediante el CatalogoRating la perfilacion para las vistas de rating
	 * y agrega esa perfilacion a la respuesta generada por consulta de rating.
	 */
	private void adjuntarPerfilacionGUI(String rol, XMLDataObject response) {
		CatalogoRating catalogoRating = new CatalogoRatingImpl();
		List lista = ConfigDBManager.getValuesAsListString(ConstantesSEFE.LISTA_PLANTILLA_PERFILACION);
		
		/*
		 * marias 20121108 - se modifica para agregar en la respuesta
		 * la lista de plantillas a utilizar en la perfilacion y asi
		 * evitar tener la definicion duplicada en el front para la propiedad perfilacion.plantilla.activos
		 */
		XMLDataList listaPlantillas = new XMLDataList("");
		for (int i = 0; i < lista.size(); i++) {
			Integer plantilla = new Integer((String) lista.get(i));
			Map perfilacion_GUI = catalogoRating.obtenerPerfilacionGUI(rol, plantilla);
			agregarPerfilacionGUI(perfilacion_GUI, response);

			// se agrega la plantilla a la lista de plantillas perfilables
			XMLDataObject xmlPlantilla = new XMLDataObject();
			xmlPlantilla.put(ConstantesRating.ID_BANCA, plantilla);
			listaPlantillas.add(xmlPlantilla);
		}
		
		response.put(ConstantesRating.LISTA_MODELOS, listaPlantillas);
	}

	private void agregarPerfilacionGUI(Map perfilacion, XMLDataObject response) {
		if (perfilacion != null) {
			Iterator itPerfilacion = perfilacion.entrySet().iterator();
			while (itPerfilacion.hasNext()) {
				Entry entry = (Entry) itPerfilacion.next();
				String clave = (String) entry.getKey();
				Boolean valor = (Boolean) entry.getValue();
				response.put(clave, valor);
			}
		}
	}

	private XMLDataObject crearXMLDataConsultaRatings(List agrupadores, String rol) {
		XMLDataList listaRatings = new XMLDataList();
		XMLDataObject response = new XMLDataObject();

		// Se obtienen los ratings individuales y grupales
		if (agrupadores != null && !agrupadores.isEmpty()) {
			for (int i = 0; i < agrupadores.size(); i++) {
				AgrupadorRatings agrupador = (AgrupadorRatings) agrupadores.get(i);
				XMLDataObject registro;

				// Si hay rating grupales, se pinta para cada rating grupal el
				// rating individual que lo agrupa
				if (agrupador.hayRatingsGrupales()) {
					List rtgGrupales = agrupador.getRatingsGrupales();

					for (int j = 0; j < rtgGrupales.size(); j++) {
						RatingGrupal rtgGrupal = (RatingGrupal) rtgGrupales.get(j);

						registro = crearRegistroRatingIndividual(agrupador.getRatingIndividual(), rtgGrupal, agrupador.getRatingFinanciero(), rol);
						listaRatings.add(registro);
					}
				} else {
					registro = crearRegistroRatingIndividual(agrupador.getRatingIndividual(), null, agrupador.getRatingFinanciero(), rol);
					listaRatings.add(registro);
				}
			}

			response.put(ConstantesSEFE.CONSUL_RTG_KEY_LISTA_REGISTROS, listaRatings);
		}

		return response;
	}

	/*
	 * Crea una instancia de XMLDataObject con la estructura de un registro que
	 * para la tabla que muestra los rating individuales.
	 */
	private XMLDataObject crearRegistroRatingIndividual(RatingIndividual ratingInd, RatingGrupal ratingGrp, RatingFinanciero ratingFinanciero, String rol) {
		CatalogoRating catalogoRating = new CatalogoRatingImpl();
		XMLDataObject xmlRegistro = new XMLDataObject();
		XMLDataObject xmlRatingInd = new XMLDataObject();
		XMLDataObject xmlRatingGrupal = new XMLDataObject();
		XMLDataObject xmlRatingsParciales = new XMLDataObject();

		boolean hayRatingInd = (ratingInd != null);
		boolean hayRatingGrp = (ratingGrp != null);
		boolean hayRatingFinanciero = (ratingFinanciero != null);
		if (hayRatingInd) {

			// Se agregan los permisos para que en la vista se pueda realizar la
			// conbinatoria para mostrar link o texto en notas de ratings.
			// Se crea seccion del registro con la informacion del rating
			// individual
			xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_TIPO_RATING, ratingInd.getIdBanca());
			xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_TIPO_RATING, ratingInd.getBanca());

			if (ratingInd.getFechaCambioEstado() != null && !ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO.equals(ratingInd.getIdEstado())) {
				xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_FECHA_IND, FormatUtil.formatDate(ratingInd.getFechaCambioEstado()));
			} else {
				xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_FECHA_IND, null);
			}

			xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_NOTA_INDIVIDUAL, ratingInd.getRatingFinal());
			xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_RATING_IND_SUGERIDO, ratingInd.getRatingFinalSugerido());
			xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_ESTADO_IND, ratingInd.getEstado());
			xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_ESTADO_IND, ratingInd.getIdEstado());
			xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_RTG_IND, ratingInd.getIdRating());
			xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_RESPONSABLE, ratingInd.getNombreResponsable());
			xmlRatingInd.put(ConstantesSEFE.CONSUL_RTG_KEY_RTG_IND_CONFIRMADO, ratingInd.getRtgIndividualConfirmado());
		}

		// Se crea seccion del registro con la informacion del rating grupal
		if (hayRatingGrp) {
			if (ratingGrp.getFecha() != null)
				xmlRatingGrupal.put(ConstantesSEFE.CONSUL_RTG_KEY_FECHA_GRUP, FormatUtil.formatDate(ratingGrp.getFecha()));
			else
				xmlRatingGrupal.put(ConstantesSEFE.CONSUL_RTG_KEY_FECHA_GRUP, null);
			if (!hayRatingInd) {
				// Se agregan los permisos para que en la vista se pueda
				// realizar la
				// conbinatoria para mostrar link o texto en notas de ratings.
				// 20120822.jlmanriquez. Marcela dice que cuando un rating
				// grupal no esta asociado a un rating individual
				// siempre se muestra la nota en caso que tenga.
				xmlRatingGrupal.put(ConstantesSEFE.CONSUL_RTG_KEY_PERM_VISUALIZAR, Boolean.TRUE);
				xmlRatingGrupal.put(ConstantesSEFE.CONSUL_RTG_KEY_PERM_ACCESO, new Boolean(hayRatingInd));
			}
			xmlRatingGrupal.put(ConstantesSEFE.CONSUL_RTG_KEY_NOTA_GRUPAL, ratingGrp.getNota());
			xmlRatingGrupal.put(ConstantesRating.RATING_MANUAL, ratingGrp.getRatingManual());
			xmlRatingGrupal.put(ConstantesSEFE.CONSUL_RTG_KEY_ESTADO_GRUP, ratingGrp.getEstado());
			xmlRatingGrupal.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_ESTADO_GRUP, ratingGrp.getIdEstado());
			xmlRatingGrupal.put(ConstantesSEFE.CONSUL_RTG_KEY_RESPONSABLE, ratingGrp.getResponsable());
			xmlRatingGrupal.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_RTG_GRUP, ratingGrp.getIdRatingGrupal());
			xmlRatingGrupal.put(ConstantesSEFE.CONSUL_RTG_KEY_RTG_GRP_CONFIRMADO, ratingGrp.getRtgGrupalConfirmado());
			xmlRatingGrupal.put(ConstantesRating.ID_BANCA, ratingGrp.getIdBanca());
			
			if(ConstantesSEFE.ID_CLASIF_ESTADO_RATING_EN_CURSO.equals(ratingGrp.getIdEstado()) && ratingGrp.getIdRatingGrupal() == null)
			{			
				RatingGrupalDAO ratingGrupalDao = new RatingGrupalDAOImpl();
				
				ArrayList modelos = (ArrayList)ratingGrupalDao.obtenerModelosIndPorRatingGrupal(ConstantesSEFE.TIPO_RATING_GRUPAL_PYME);
				
				int existe = 0;
				
				for (int m = 0; m < modelos.size(); m++) {
					ModIndRtgGrupal modelo = (ModIndRtgGrupal)modelos.get(m);
					if(hayRatingInd && modelo.getComponenteId().intValue() == ratingInd.getIdBanca().intValue())
					{
						existe = 1;
						if(!modelo.getAplicaCalculo().booleanValue())
						{
							xmlRatingGrupal.put(ConstantesRating.BANCA_NO_HABILITADA, new Boolean(true));
						}
					}
				}
				
				if(existe == 0)
				{
					xmlRatingGrupal.put(ConstantesRating.BANCA_NO_HABILITADA, new Boolean(true));
				}
			}

			// Si es rating grupal PyME (Por lo que el id de grupo es nulo) se
			// obtiene la parte involucrada
			String rutCliRtgGrupal = null;
			if (ratingGrp.getIdRatingGrupal() == null) {
				ServicioClientes srvCli = new ServicioClientesImpl();
				Cliente cli = srvCli.obtenerParteInvolucradaPorId(ratingGrp.getIdParteInvolucrada());
				rutCliRtgGrupal = cli.getRut();
			}
			xmlRatingGrupal.put(ConstantesRating.RUT_CLIENTE, rutCliRtgGrupal);
			xmlRatingGrupal.put(ConstantesRating.ID_RATING_INDIVIDUAL, ratingGrp.getIdRatingIndividual());
			xmlRatingGrupal.put(ConstantesRating.ID_VERSION, ratingGrp.getIdVersion());
			if (ratingGrp.esRelacionado() != null) {
				xmlRatingGrupal.put(ConstantesRating.ES_RELACIONADO, ratingGrp.esRelacionado());
			}
		}

		if (hayRatingInd) {
			// Se crea seccion del registro con la informacion de los
			// componentes
			// del rating individual
			if (ratingInd.getFechaEEFF() != null) {
				xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_FECHA_VACIADO, FormatUtil.formatDateRating(ratingInd.getFechaEEFF()));
			}
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_NOTA_FINANCIERO, ratingInd.getRatingFinanciero());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_RTG_FIN, ratingInd.getIdRatingFinanciero());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_NOTA_PROYECTADO, ratingInd.getRatingProyectado());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_RTG_PROY, ratingInd.getIdRatingProyectado());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_NOTA_NEGOCIO, ratingInd.getRatingNegocio());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_RTG_NEG, ratingInd.getIdRatingNegocio());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_NOTA_COMPORTAMIENTO, ratingInd.getRatingComportamiento());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_RTG_COMP, ratingInd.getIdRatingComportamiento());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_RTG_FINAN_CONFIRMADO, ratingInd.getRtgFinancieroConfirmado());

			/*
			 * marias - 20121023 se considera que el rating es migrado si el id
			 * es < cero. En ese caso se toma como un rating confirmado.
			 */
			boolean ratingCmpEsConfirmado = false;
			ratingCmpEsConfirmado = (ratingInd.getRtgProyectadoConfirmado() != null && ratingInd.getRtgProyectadoConfirmado().booleanValue()) || ratingInd.getIdRating().longValue() < 0;
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_RTG_PROY_CONFIRMADO, new Boolean(ratingCmpEsConfirmado));
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_RTG_NEG_CONFIRMADO, ratingInd.getRtgNegocioConfirmado());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_RTG_COMP_CONFIRMADO, ratingInd.getRtgComportamientoConfirmado());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_NOTA_GARANTE, ratingInd.getRatingGarante());
			xmlRatingsParciales.put(ConstantesSEFE.CONSUL_RTG_KEY_ID_RTG_GTE, ratingInd.getIdRatingGarante());
			
		}

		// Se agregan atributos correspondientes al rating financiero
		if (hayRatingFinanciero) {
			xmlRatingsParciales.put(ConstantesRating.ID_VACIADO, ratingFinanciero.getIdVaciado0());
		}

		// Se agregan las secciones al registro
		xmlRegistro.put(ConstantesSEFE.CONSUL_RTG_KEY_DATA_RTG_IND, xmlRatingInd);
		xmlRegistro.put(ConstantesSEFE.CONSUL_RTG_KEY_DATA_RTG_GRUP, xmlRatingGrupal);
		xmlRegistro.put(ConstantesSEFE.CONSUL_RTG_KEY_DATA_RTG_PARCIAL, xmlRatingsParciales);

		return xmlRegistro;
	}

	// Busca los tipos de banca para rating
	public XMLDataObject buscarBancaRating(XMLData req) {
		XMLDataObject request = (XMLDataObject) req;
		CatalogoGeneral catalogo = new CatalogoGeneralImpl();
		Integer esquema = request.getInteger(ConstantesSEFE.SELECC_TPO_RTG_KEY_ESQUEMA);
		// Realiza una busqueda por la categoria del grupo de plantilla Rating
		Collection coll = catalogo.buscarClasificacionesPorCategVrs(esquema);
		XMLDataObject respuesta = crearXMLDataClasificaciones(coll, esquema);
		return respuesta;
	}

	private XMLDataObject crearXMLDataClasificaciones(Collection clasificaciones, Integer esquema) {
		XMLDataObject data = new XMLDataObject();
		XMLDataList tipos = new XMLDataList();
		tipos.setId(ConstantesSEFE.SELECC_TPO_RTG_KEY_LST_TIPOS_BANCAS); // AGREGAR
		// A
		// LOS
		// DOC
		// QUE
		// TIENE
		// UN
		// ID,
		// LST_TIPOS

		Iterator iter = clasificaciones.iterator();
		// Establece clasificaciones de Rating la cual estan compuesta por el
		// Rating, Esquema
		// y el nombre a cual pertenece la clasificacion
		while (iter.hasNext()) {
			Clasificacion clasifRating = (Clasificacion) iter.next();
			// Si la fecha fin no es null, quiere decir que la clasificacion es
			// no vigente por ende no se debe mostrar
			if (clasifRating.getFechaFin() == null) {
				XMLDataObject clasificacion = new XMLDataObject();
				clasificacion.put(ConstantesSEFE.SELECC_TPO_RTG_KEY_CLASIFICACION, clasifRating.getIdClasif());
				clasificacion.put(ConstantesSEFE.SELECC_TPO_RTG_KEY_ESQUEMA, esquema);
				clasificacion.put(ConstantesSEFE.SELECC_TPO_RTG_KEY_NOMBRE, clasifRating.getNombre());
				clasificacion.put(ConstantesSEFE.SELECC_TPO_RTG_KEY_VERSION, clasifRating.getVersion());
				tipos.add(clasificacion);
			}
		}
		data.put(ConstantesSEFE.SELECC_TPO_RTG_KEY_LST_TIPOS_BANCAS, tipos);

		return data;
	}

	// Busca los tipos de banca para rating
	public XMLDataObject obtenerFichaRating(XMLData req) {
		XMLDataObject request = (XMLDataObject) req;
		String rut = (String) request.getObject(ConstantesSEFE.VER_FICH_RTG_KEY_CLIENTE);
		Long idRatIndividual = request.getLong(ConstantesSEFE.VER_FICH_RTG_KEY_ID_RTG_INDIVIDUAL);
		Long idRatGrupal = request.getLong(ConstantesSEFE.VER_FICH_RTG_KEY_ID_RTG_GRUPAL);
		Integer idTipoRating = request.getInteger(ConstantesSEFE.VER_FICH_RTG_KEY_ID_TPO_RTG);
		CatalogoRating catalogo = new CatalogoRatingImpl();
		Collection ficha = catalogo.obtenerFichaRating(rut, idRatIndividual, idRatGrupal, idTipoRating);
		XMLDataObject respuesta = crearXMLDataVerFichaRating(ficha);
		return respuesta;
	}

	private XMLDataObject crearXMLDataVerFichaRating(Collection ficha) {

		return null;
	}

}
