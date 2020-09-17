package compec.ufam.sistac.pdf;

import java.io.*;
import java.util.*;
import com.phill.libs.*;
import com.phill.libs.ui.*;

/** Classe que carrega os motivos de erro de processamento de um arquivo .csv.
 *  Esta lista foi extraída do manual do Sistac em anexo neste projeto (res/manual).
 *  @author Felipe André
 *  @version 2.50, 08/07/2018 */
public class SituacaoDAO {

	/** Carrega os motivos de erro de processamento do arquivo .csv para dentro de um ArrayList */
	public static ArrayList<Situacao> getErros() {
		
		String row;
		ArrayList<Situacao> listaSituacoes = new ArrayList<Situacao>();
		
		try {
			
			File csvSituacoes = new File(ResourceManager.getResource("situacoes/situacoes.csv"));
			BufferedReader stream  = new BufferedReader(new InputStreamReader(new FileInputStream(csvSituacoes),"UTF8"));
			
			while ( (row = stream.readLine()) != null ) {
				
				String[] readLine = row.split(";");
				listaSituacoes.add(new Situacao(readLine));
				
			}
			
			stream.close();
			
		} catch (IOException exception) {
			exception.printStackTrace();
			AlertDialog.error("Lendo arquivo de erros","Falha ao ler o arquivo de erros de processamento.");
		}
		
		return listaSituacoes;
	}
	
}
