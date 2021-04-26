package compec.ufam.isensys.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.phill.libs.*;

import compec.ufam.isensys.model.*;

/** Contém métodos de salvamento e recuperação de dados do arquivo de propriedades do sistema.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.1, 26/04/2021
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
	
	private static final File CONFIGS_FILE = ResourceManager.getResourceAsFile("config/program.dat");
	
	public static void save(final Configs configs) throws IOException {
		
		// Abrindo arquivo para escrita
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(CONFIGS_FILE));
				
		// Escrevendo dados
		stream.writeObject(configs);
				
		// Liberando recursos
		stream.close();
		
	}
	
	public static Configs retrieve() throws ClassNotFoundException, FileNotFoundException, IOException {
		
		// Abrindo arquivo para leitura
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(CONFIGS_FILE));
				
		// Recuperando dados
		Configs configs = (Configs) stream.readObject();
		
		// Liberando recursos
		stream.close();
		
		return configs;
	}

}