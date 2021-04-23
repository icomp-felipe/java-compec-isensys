package compec.ufam.isensys.io;

import com.phill.libs.*;

import compec.ufam.isensys.model.*;

/** Contém métodos de salvamento e recuperação de dados do arquivo de propriedades do sistema.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.0, 23/04/2021
 *  @since 3.0, 22/04/2021 */
public class SystemConfigs {
	
	/** Recupera os dados institucionais do arquivo de propriedades do sistema.
	 *  @return Uma {@link Instituicao} com os dados provenientes do arquivo de propriedades do sistema. */
	public static Instituicao getInstituicao() {
		
		// Recuperando dados da instituição
		final String cnpj         = PropertiesManager.getString("inst.cnpj" , null);
		final String nomeFantasia = PropertiesManager.getString("inst.nome" , null);
		final String razaoSocial  = PropertiesManager.getString("inst.razao", null);
		
		return new Instituicao(cnpj, nomeFantasia, razaoSocial);
		
	}

}