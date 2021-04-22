package compec.ufam.sistac.io;

import com.phill.libs.PropertiesManager;

import compec.ufam.sistac.model.retorno.Instituicao;

public class Config {
	
	public static Instituicao getInstituicao() {
		
		// Recuperando dados da instituição
		final String cnpj         = PropertiesManager.getString("inst.cnpj" , null);
		final String nomeFantasia = PropertiesManager.getString("inst.nome" , null);
		final String razaoSocial  = PropertiesManager.getString("inst.razao", null);
		
		return new Instituicao(cnpj, nomeFantasia, razaoSocial);
		
	}

}
