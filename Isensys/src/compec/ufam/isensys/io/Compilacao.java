package compec.ufam.isensys.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import compec.ufam.isensys.constants.Reviewed;
import compec.ufam.isensys.model.retorno.ListaRetornos;
import compec.ufam.isensys.model.retorno.Retorno;

@Reviewed("2025-08-10")
/** Classe de I/O da compilação do sistema.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 10/AGO/2025 */
public class Compilacao {
	
	/** Salva a lista de retornos em um arquivo binário.
	 *  @param listaRetornos - lista de retornos a ser salva em arquivo
	 *  @param arquivo - caminho do arquivo .icf
	 *  @throws IOException caso haja alguma falha na escrita do arquivo. */
	public static void save(final ListaRetornos listaRetornos, final File arquivo) throws IOException {

		try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(arquivo))) {
			
			stream.writeObject(listaRetornos);
			
		}
		
	}
	
	/** Recupera a compilação do <code>arquivo</code> para o objeto {@link ListaRetornos}.
	 *  @param arquivo - caminho do arquivo .icf
	 *  @throws ClassNotFoundException caso as classes {@link ListaRetornos} ou {@link Retorno} não estejam disponíveis, ou mudaram de versão;
	 *  @throws IOException caso haja alguma falha na leitura do arquivo. */
	public static ListaRetornos load(final File arquivo) throws ClassNotFoundException, IOException {
		
		try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(arquivo))) {
			
			return (ListaRetornos) stream.readObject();
			
		}
		
	}
	
}
