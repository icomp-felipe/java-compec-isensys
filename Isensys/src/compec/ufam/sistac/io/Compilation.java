package compec.ufam.sistac.io;

import java.io.*;
import compec.ufam.sistac.model.*;

/** Classe de I/O da compilação do sistema
 *  @author Felipe André
 *  @version 2.50, 07/07/2018 */
public class Compilation {
	
	/** Salva o ArrayList 'listaRetornos' da compilação no 'arquivo' */
	public static void save(ListaRetornos listaRetornos, File arquivo) throws IOException {
		
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(arquivo));
		
		stream.writeObject(listaRetornos);
		
		stream.close();
		
	}
	
	/** Recupera a compilação do 'arquivo' para o ArrayList 'listaRetornos' */
	public static ListaRetornos retrieve(File arquivo) throws IOException, ClassNotFoundException {
		
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(arquivo));
		
		ListaRetornos listaRetornos   = (ListaRetornos) stream.readObject();
		
		stream.close();
		
		return listaRetornos;
	}
	
}
