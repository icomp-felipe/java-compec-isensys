package compec.ufam.isensys.io;

import java.io.*;

import compec.ufam.isensys.model.*;
import compec.ufam.isensys.constants.*;

/** Contém métodos de salvamento e recuperação de dados do arquivo de propriedades do sistema.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.1, 26/04/2021
 *  @since 3.0, 22/04/2021 */
public class SystemConfigs {
	
	/** Salva as configurações do sistema em disco.
	 *  @param configs - objeto de configurações do sistema
	 *  @throws IOException caso o arquivo não possa ser escrito por algum motivo. */
	public static void save(final Configs configs) throws IOException {
		
		// Abrindo arquivo para escrita
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(Constants.SYS_CONFIGS_FILE));
				
		// Escrevendo dados
		stream.writeObject(configs);
				
		// Liberando recursos
		stream.close();
		
	}
	
	/** Recupera as configurações do sistema do disco.
	 *  @return Objeto de configurações do sistema.
	 *  @throws ClassNotFoundException caso as classes {@link Configs} ou {@link Instituicao} não estejam disponíveis, ou mudaram de versão;
	 *  @throws FileNotFoundException quando o arquivo de configuração não é encontrado;
	 *  @throws IOException caso haja alguma falha na leitura do arquivo. */
	public static Configs retrieve() throws ClassNotFoundException, FileNotFoundException, IOException {
		
		// Abrindo arquivo para leitura
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(Constants.SYS_CONFIGS_FILE));
				
		// Recuperando dados
		Configs configs = (Configs) stream.readObject();
		
		// Liberando recursos
		stream.close();
		
		return configs;
	}

}