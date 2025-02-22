package compec.ufam.isensys.pdf;

import java.io.*;
import java.util.*;
import java.nio.charset.*;

import com.phill.libs.*;
import com.phill.libs.files.*;

/** Classe que carrega os motivos de erro de processamento de um arquivo .csv com codificação UTF-8.
 *  Esta lista foi extraída do manual do Sistac em anexo neste projeto (res/development/manual-sistac-mds.pdf).
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 3.8, 21/JUN/2023 */
public class SituacaoDAO {

	/** Carrega os motivos de erro de processamento do arquivo 'res/situacoes/situacoes.csv' para dentro de um ArrayList.
	 *  @return Lista com os códigos de erro e duas devidas descrições.
	 *  @throws IOException quando o arquivo de situacoes não foi encontrado ou não pode ser lido */
	public static ArrayList<Situacao> load() throws IOException {
		
		String row;
		ArrayList<Situacao> listaSituacoes = new ArrayList<Situacao>();
		
			
		// Recuperando o arquivo de situacoes
		File csvSituacoes = ResourceManager.getResourceAsFile("situacoes/situacoes.csv");
		BufferedReader stream  = new BufferedReader(new InputStreamReader(new FileInputStream(csvSituacoes), StandardCharsets.UTF_8));
			
		// Pulando a primeira linha e recuperando o delimitador do CSV
		final String delimiter = CSVUtils.getCSVDelimiter(stream.readLine());
			
		// Lendo o arquivo CSV para dentro do ArrayList 
		while ( (row = stream.readLine()) != null ) {
				
			String[] rowData = row.split(delimiter);
			Situacao situacao = new Situacao(rowData);
				
			listaSituacoes.add(situacao);
			
		}
			
		// Liberando recursos
		stream.close();
		
		return listaSituacoes;
		
	}
	
}