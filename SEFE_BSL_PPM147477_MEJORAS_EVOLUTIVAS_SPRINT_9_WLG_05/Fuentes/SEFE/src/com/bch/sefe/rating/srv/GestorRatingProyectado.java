package com.bch.sefe.rating.srv;

import java.util.Date;
import java.util.List;

import com.bch.sefe.rating.vo.BalanceInmobiliario;
import com.bch.sefe.rating.vo.HojaBalanceInmobiliario;
import com.bch.sefe.rating.vo.HojaSoe;
import com.bch.sefe.rating.vo.IndicadorBi;
import com.bch.sefe.rating.vo.IndicadorSoe;
import com.bch.sefe.rating.vo.RatingFinanciero;
import com.bch.sefe.rating.vo.RatingIndividual;
import com.bch.sefe.rating.vo.Soe;
import com.bch.sefe.vaciados.vo.Vaciado;

public interface GestorRatingProyectado {

	/**
	 * Crea una instancia de vaciado para utilizar en la proyeccion de rating.
	 * El vaciado se crea con el indicador de tipo de proyeccion igual a cero (proyeccion corta)
	 * 
	 * @param vac0
	 * @param idCte
	 * @param idUsr
	 * @param idRating
	 * @param idFinan
	 * @return
	 */
	Vaciado crearVaciadoProyectado(Vaciado vac0, Long idCte, Long idUsr, Long idRating, Long idFinan);

	
	/**
	 * Retorna la lista de cuentas utilizadas en el calculo de rating para el período del vaciado
	 * pasado como argumento
	 * 
	 * @param idVaciado
	 * @return
	 */
	List buscarCuentasPorPeriodo(Long idVaciado);

	
	/**
	 * Busca y retorna la lista de vaciados para utilizar en la determinacion del 
	 * rating proyectado, basado en los periodos del rating proyectado vigente
	 * pasado como argumento
	 * 
	 * @param rtgFinanciero - instancia de RatingProyectado en estado VIGENTE
	 * @param idBanca - identificador de la banca
	 * 
	 * @return - lista de instancias de Vaciado. El vaciado más reciente es el elemento 0; retorna
	 * 			 una lista vacia si no existen vaciado que cumplan con las reglas de negocio
	 * 
	 * @exception - lanza una excepcion de tipo SEFERatingProyectadoException si no se cumplen las
	 * reglas de negocio
	 */
	List buscarVaciadosRatingProyectado(RatingFinanciero rtgFinanciero, Integer idBanca, RatingIndividual ratingInd);

	
	/**
	 * Busca la lista de flags de opcionalidad asociada a las cuentas que se ingresan por el usuario
	 * para el calculo de las proyecciones
	 * @param idPlanCta - identificador del plan de cuenta
	 * @param idNombrePlan	- id del nombre de plan de cuentas
	 * @return
	 */
	List buscarFlagsIngresoCuentasProyeccion(Integer idPlanCta, Integer idNombrePlan);
	
	
	/**
	 * Busca la lista de flags de opcionalidad asociada a las cuentas que se ingresan por el usuario
	 * para el calculo de las proyecciones
	 * @param idPlanCta - identificador del plan de cuenta
	 * @param idNombrePlan	- id del nombre de plan de cuentas
	 * @return
	 */
	List buscarFlagsIngresoCuentasProyeccionLarga(Integer idPlanCta, Integer idNombrePlan);
	
	public boolean esVaciadoVigente(Vaciado vaciado, Integer idBanca, Date fechaAvance);
	
	/**
	 * Verifica si el vaciado corresponde a un vaciado de cierre anual. La validacion se realiza en base al periodo del vaciado.
	 * @param vac
	 * @return
	 */
	public boolean esCierreAnual(Vaciado vac);
	
	public List buscarVaciadosRatingProyectado(RatingFinanciero rtgFinanciero) ;
	
	public BalanceInmobiliario insertarBalanceInmobiliario(BalanceInmobiliario balanceInmobiliario);
	
	public void insertarHojaBalanceInmobiliario(HojaBalanceInmobiliario hojaBalanceInmobiliario);
	
	public Soe insertarSoe(Soe soe);
	
	public void insertarHojaSoe(HojaSoe hojaSoe);
	

	
	public List buscarBiXRut(String rutCliente);
	
	public void insertarIndicadorSoe(IndicadorSoe indicadorSoe);
	
	public void insertarIndicadorBi(IndicadorBi indicadorBi);
	
	public void actualizarUsuarioBalance(Long idUsuario, Long idParteInvol, Date fechaAvance, String tabla);
	
	public void borrarHojasEIndicadores(Long idParteInvol, Date fechaAvance, String tabla);
	
	public Double obtenerDeudaXFechaAvance(String rutCliente , Integer tipoConsulta, Date fechaAvance);
	
	public void actualizarDeudaBalanceInmobiliario(String rutConsulta, String deuda, Date fechaAvance);

}
