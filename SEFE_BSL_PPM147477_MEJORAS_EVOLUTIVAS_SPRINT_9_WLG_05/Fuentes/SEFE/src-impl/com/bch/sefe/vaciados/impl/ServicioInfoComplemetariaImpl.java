package com.bch.sefe.vaciados.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bch.sefe.comun.CatalogoGeneral;
import com.bch.sefe.comun.impl.CatalogoGeneralImpl;
import com.bch.sefe.comun.util.XMLDataList;
import com.bch.sefe.comun.util.XMLDataObject;
import com.bch.sefe.comun.vo.Usuario;
import com.bch.sefe.comun.vo.ValorDetalleCtaPlanCtas;
import com.bch.sefe.vaciados.ConstantesVaciados;
import com.bch.sefe.vaciados.IServicioInfoComplementaria;
import com.bch.sefe.vaciados.srv.GestorVaciados;
import com.bch.sefe.vaciados.srv.IGestorInfoComplementaria;
import com.bch.sefe.vaciados.srv.impl.GestorInfoComplementaria;
import com.bch.sefe.vaciados.srv.impl.GestorVaciadosImpl;
import com.bch.sefe.vaciados.vo.ConceptoInfoComplementaria;
import com.bch.sefe.vaciados.vo.InformacionComplementaria;
import com.bch.sefe.vaciados.vo.Vaciado;

public class ServicioInfoComplemetariaImpl implements IServicioInfoComplementaria {

	public InformacionComplementaria buscarInformacionComplementaria(Long idVaciado, String rutCliente) {
		IGestorInfoComplementaria gic = new GestorInfoComplementaria();
		GestorVaciados gv = new GestorVaciadosImpl();
		InformacionComplementaria infoComplementaria = new InformacionComplementaria();
		Vaciado vac1 = null;
		Vaciado vac2 = null;
		Vaciado vac3 = null;

		vac1 = gv.buscarVaciado(idVaciado);
		infoComplementaria.setIdVaciado1(vac1.getIdVaciado());
		infoComplementaria.setPeriodo1(vac1.getPeriodo());

		vac2 = gv.buscarVaciadoAnteriorDetalleCuenta(vac1.getIdVaciado(),vac1.getIdTipoPlan());
		if (vac2 != null) {
			infoComplementaria.setIdVaciado2(vac2.getIdVaciado());
			infoComplementaria.setPeriodo2(vac2.getPeriodo());

			vac3 = gv.buscarVaciadoAnteriorDetalleCuenta(vac2.getIdVaciado(), vac2.getIdTipoPlan());
			if (vac3 != null) {
				infoComplementaria.setIdVaciado3(vac3.getIdVaciado());
				infoComplementaria.setPeriodo3(vac3.getPeriodo());
			}
		}

		agregarInfoVaciado(infoComplementaria, vac1, ConstantesVaciados.PRIMER_PERIODO);
		agregarInfoVaciado(infoComplementaria, vac2, ConstantesVaciados.SEGUNDO_PERIODO);
		agregarInfoVaciado(infoComplementaria, vac3, ConstantesVaciados.TERCER_PERIODO);

		infoComplementaria.setInfoSegmento(gic.buscarInfoSegmento(vac1, vac2, vac3));
		infoComplementaria.setInfoAdicional(gic.buscarInfoAdicional(vac1, vac2, vac3));
		infoComplementaria.setInfoAdicionalMercadoDest(gic.buscarInfoAdicionalMercado(vac1, vac2, vac3));
		infoComplementaria.setInfoAdicionalEstructuraCostos(gic.buscarInfoAdicionalEstructura(vac1, vac2, vac3));
		infoComplementaria.setInfoEspecifica(gic.buscarInfoEspecifica(vac1, vac2, vac3));

		return infoComplementaria;
	}

	public InformacionComplementaria guardarInformacionComplementaria(InformacionComplementaria infoComp, String rutCliente, String usuario) {
		IGestorInfoComplementaria gic = new GestorInfoComplementaria();
		GestorVaciados gv = new GestorVaciadosImpl();
		CatalogoGeneral catalogoGeneral = new CatalogoGeneralImpl();

		// Se obtiene el usuario asociado al login de operador.
		Usuario usr = catalogoGeneral.obtenerUsuarioPorLogOperador(usuario);
		gv.actualizarUsuarioModificacionInfoComplementaria(infoComp.getIdVaciado1(), usr.getUsuarioId());
		Vaciado vac1 = gv.buscarVaciado(infoComp.getIdVaciado1());

		if (infoComp.getInfoSegmento() != null) {
			gic.guardarInfoSegmento(vac1, infoComp.getInfoSegmento());
		}

		gic.guardarInfoAdicional(vac1, infoComp.getInfoAdicional());
		gic.guardarInfoAdicionalMercado(vac1, infoComp.getInfoAdicionalMercadoDest());
		gic.guardarInfoAdicionalEstructura(vac1, infoComp.getInfoAdicionalEstructuraCostos());

		// Esta informacion la ingresa el usuario inclusive los conceptos, por lo que puede que no exista informacion especifica
		if (infoComp.getInfoEspecifica() != null) {
			gic.guardarInfoEspecifica(vac1, infoComp.getInfoEspecifica());
		}

		return infoComp;
	}

	private void agregarInfoVaciado(InformacionComplementaria infoComp, Vaciado vac, final int periodo) {
		IGestorInfoComplementaria gic = new GestorInfoComplementaria();
		Map info = null;

		if (vac != null) {
			info = gic.getInformacionVaciado(vac);
		} else {
			info = new HashMap();
			info.put(ConstantesVaciados.PLAN_CUENTA, null);
			info.put(ConstantesVaciados.FLAG_AJUSTE, null);
			info.put(ConstantesVaciados.RESPONSABLE, null);
			info.put(ConstantesVaciados.UNIDAD, null);
			info.put(ConstantesVaciados.MESES, null);
			info.put(ConstantesVaciados.MONEDA, null);
		}

		switch (periodo) {
		case ConstantesVaciados.PRIMER_PERIODO:
			infoComp.setInfoPeriodo1(info);
			break;
		case ConstantesVaciados.SEGUNDO_PERIODO:
			infoComp.setInfoPeriodo2(info);
			break;
		case ConstantesVaciados.TERCER_PERIODO:
			infoComp.setInfoPeriodo3(info);
			break;
		}
	}

	public boolean verificarCuadratura(Long idVaciado, String rutCli){
		// Metodo que verifica si alguna de las cuentas de informacion complementaria se encuentra descuadrada
		ServicioInfoComplemetariaImpl srvInfComp = new ServicioInfoComplemetariaImpl();
		InformacionComplementaria infComp = srvInfComp.buscarInformacionComplementaria(idVaciado, rutCli);

		List lstConceptos = infComp.getInfoSegmento();

		for (int i = 0; i < lstConceptos.size(); i++) {
			ConceptoInfoComplementaria concepto = (ConceptoInfoComplementaria) lstConceptos.get(i);
			List aperturas = concepto.getSubConceptos();
			Double totalPeriodo = concepto.getTotalPeriodo1();
			Double valorPeriodo = concepto.getValorPeriodo1();

			if(!totalPeriodo.equals(valorPeriodo) ){
				//Balance descuadrado
				return false;
			}
		}
		return true;
	}
}
