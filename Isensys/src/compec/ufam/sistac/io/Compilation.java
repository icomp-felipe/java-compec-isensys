package compec.ufam.sistac.io;

import java.io.*;
import compec.ufam.sistac.model.*;

/** Classe de I/O da compilação do sistema
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 19/04/2021 */
public class Compilation {
	
	/** Salva a lista de retornos em um arquivo binário.
	 *  @param listaRetornos - lista de retornos a ser salva em arquivo
	 *  @param arquivo - caminho do arquivo .bsf
	 *  @throws IOException caso haja alguma falha na escrita do arquivo. */
	public static void save(final ListaRetornos listaRetornos, final File arquivo) throws IOException {

		// Abrindo arquivo para escrita
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(arquivo));
		
		// Escrevendo dados
		stream.writeObject(listaRetornos);
		
		// Liberando recursos
		stream.close();
		
	}
	
	/** Recupera a compilação do 'arquivo' para o objeto ListaRetornos.
	 *  @param arquivo - caminho do arquivo .bsf
	 *  @throws ClassNotFoundException quando a classe {@link ListaRetornos} ou {@link Retorno} não esteja disponível, ou mudou de versão;
	 *  @throws IOException caso haja alguma falha na leitura do arquivo */
	public static ListaRetornos retrieve(final File arquivo) throws ClassNotFoundException, IOException {
		
		// Abrindo arquivo para leitura
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(arquivo));
		
		// Recuperando dados
		ListaRetornos listaRetornos   = (ListaRetornos) stream.readObject();
		
		// Liberando recursos
		stream.close();
		
		return listaRetornos;
	}
	
}