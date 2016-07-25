package com.bch.sefe.comun.utils;

/**
 * @author Raul Astudillo
 *
 */
public class Validador {
	
	private final String digit = "^-?\\d*[.]?\\d*$";
	private final String digitPositivo ="^[+]?\\d*[.]?\\d*$";
	private final String digitDecimal = "(^\\d*\\.?\\d*[0-9]+\\d*$)|(^[0-9]+\\d*\\.\\d*$)";
	//private final String porcentaje 		= "^?[0-9]{0,2}(\\.[0-9]{1,2})?$|^-?(100)(\\.[0]{1,2})?$";
	private final String porcentajeSinSigno = "^?[0-9]{0,2}(\\.[0-9]{1,2})?$|^?(100)(\\.[0]{1,2})?$";
	private final String nota = "^(([1]{1})(0{1})(\\.[0]){0,1}|([0-9]{1}(\\.[0|5]){0,1}))?$";
	// la nota en el modulo admin permite cualquier decimal; en mod. rating solamente multiplos de .5
	private final String notaAdmin = "^(([1]{1})(0{1})(\\.[0]){0,1}|([0-9]{1}(\\.(\\d){1,2}){0,1}))?$";
	private final String notaDecorada = "^(([1]{1})(0{1})(\\,[0]){0,1}|([1-9]{1}(\\,[0|5]){0,1}))?$";
	
	public Validador() {
	}
	
	/**
	 * Valida que la expresion contenga solo dígitos.
	 * 
	 * @param expr Expresión a validar
	 * @return Verdadero si la expresión cumple el criterio de validación.
	 */
	public boolean validarDigit(String expr) {
		
		return String.valueOf(expr).matches(digit);
	}
	
	/**
	 * Valida que la expresion contenga solo dígitos.
	 * 
	 * @param expr Expresión a validar
	 * @return Verdadero si la expresión cumple el criterio de validación.
	 */
	public boolean validarDigitPositivo(String expr) {
		
		return String.valueOf(expr).matches(digitPositivo);
	}
	
	
	
	/**
	 * Valida que la expresion contenga solo dígitos y decimales.
	 * 
	 * @param expr Expresión a validar
	 * @return Verdadero si la expresión cumple el criterio de validación.
	 */
	public boolean validarDecimal(String expr) {
		return String.valueOf(expr).matches(digitDecimal);
	}
	
	/**
	 * Valida que la expresion contenga valores porcentuales validos.
	 * 
	 * @param expr Expresión a validar
	 * @return Verdadero si la expresión cumple el criterio de validación.
	 */
	public boolean validarPorcentaje(String expr) {
		return String.valueOf(expr).matches(porcentajeSinSigno);
	}
	
	
	/**
	 * Valida que la expresion contenga valores porcentuales sin signo validos.
	 * 
	 * @param expr Expresión a validar
	 * @return Verdadero si la expresión cumple el criterio de validación.
	 */
	private native boolean validarPorcentajeSinSignoNative(String expr) /*-{
		var patron = /^(?:100(?:.0(?:0)?)?|\d{1,2}(?:.\d{1,2})?)$/
		
		return (patron.test(expr));
	}-*/;
	
	/**
	 * Valida que la expresion contenga valores porcentuales sin signo validos.
	 * 
	 * @param expr Expresión a validar
	 * @return Verdadero si la expresión cumple el criterio de validación.
	 */
	public boolean validarPorcentajeSinSigno(String expr) {
		boolean valid;
		try {
			valid = validarPorcentajeSinSignoNative(expr) && (Double.valueOf(expr).doubleValue() >= 0 && Double.valueOf(expr).doubleValue() <= 100);
		}
		catch (NumberFormatException nfe) {
			valid = false;
		}
		return valid;
	}
	
	/**
	 * Valida que la expresion contenga un valor nota de 1 a 10 incluyendo decimales.
	 * Para el m&oacute;dulo Admin, utilizar el m&eacute;todo {@link Validador#validarNotaAdmin(String)},
	 * ya que permite formato distinto para la nota.
	 * 
	 * @param expr Expresión a validar
	 * @return Verdadero si la expresión cumple el criterio de validación.
	 */
	public boolean validarNota(String expr) {
		return String.valueOf(expr).matches(nota);
	}
	
	/**
	 * Valida que la expresion contenga un valor nota de 1 a 10 incluyendo decimales.
	 * Esta validaci&oacute;n es utilizada por el m&oacute;dulo Admin.
	 * Para el resto de los m&oacute;dulos, utilizar el m&eacute;todo {@link Validador#validarNota(String)}
	 * 
	 * @param expr Expresión a validar
	 * @return Verdadero si la expresión cumple el criterio de validación.
	 */
	public boolean validarNotaAdmin(String expr) {
		return String.valueOf(expr).matches(notaAdmin);
	}
	
	/**
	 * Valida que la expresion contenga un valor nota de 1 a 10 incluyendo decimales decorados.
	 * 
	 * @param expr Expresión a validar
	 * @return Verdadero si la expresión cumple el criterio de validación.
	 */
	public boolean validarNotaDecorada(String expr) {
		return String.valueOf(expr).matches(notaDecorada);
	}
	
	/**
	 * Valida que el string sea un valor con un decimal que no supere 100.
	 * Se utiliza JSNI ya que las expresiones regulares Java no son del todo igual que en JS.
	 * @param expr
	 * @return
	 */
	public native boolean validarPorcentajeUnDecimal(String expr) /*-{
		var patron = /^100(,(0){0,1})?$|^([1-9]?[0-9])(,(\d{0,1}))?$/;
		var patron2 = /^(?:100(?:.0(?:0)?)?|\d{1,2}(?:.\d{1,2})?)$/

		return (patron.test(expr));
	}-*/;
	
	public native boolean validarNumeroDosDecimales(String expr) /*-{
		var patron = /^-?\d+\,?\d{0,2}$/;		
		return (patron.test(expr));
	}-*/;
	
	public native boolean validarNumeroTresDecimales(String expr) /*-{
	var patron = /^-?\d+\,?\d{0,3}$/;		
	return (patron.test(expr));
	}-*/;
	
	public native boolean validarNumeroSinDecimales(String expr) /*-{
		var patron = /^-?\d+$/;
		return (patron.test(expr));
	}-*/;
	
	public native boolean validarDecimalNativo(String expr) /*-{
		var patron = /^\d+\,?\d*$/;		
		return (patron.test(expr));
	}-*/;
}
