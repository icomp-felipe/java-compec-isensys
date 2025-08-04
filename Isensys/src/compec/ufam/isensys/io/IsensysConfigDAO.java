package compec.ufam.isensys.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import compec.ufam.isensys.constants.Constants;
import compec.ufam.isensys.model.IsensysConfig;

/** Contém métodos de salvamento e recuperação de dados do arquivo de propriedades do sistema.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 04/AGO/2025 */
public class IsensysConfigDAO {
	
	/** Salva as configurações do sistema em disco.
	 *  @param configs - objeto de configurações do sistema
	 *  @throws IOException caso o arquivo não possa ser escrito por algum motivo. */
	public static void save(final IsensysConfig configs) throws IOException {
		
		// Abrindo arquivo para escrita
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(Constants.SYS_CONFIGS_FILE));
				
		// Escrevendo dados
		stream.writeObject(configs);
				
		// Liberando recursos
		stream.close();
		
	}
	
	/** Recupera as configurações do sistema do disco.
	 *  @return Objeto de configurações do sistema.
	 *  @throws ClassNotFoundException caso as classes {@link IsensysConfig} não estejam disponíveis, ou mudaram de versão;
	 *  @throws FileNotFoundException quando o arquivo de configuração não é encontrado;
	 *  @throws IOException caso haja alguma falha na leitura do arquivo. */
	public static IsensysConfig retrieve() throws ClassNotFoundException, FileNotFoundException, IOException {
		
		// Abrindo arquivo para leitura
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(Constants.SYS_CONFIGS_FILE));
				
		// Recuperando dados
		IsensysConfig configs = (IsensysConfig) stream.readObject();
		
		// Liberando recursos
		stream.close();
		
		return configs;
	}

}
