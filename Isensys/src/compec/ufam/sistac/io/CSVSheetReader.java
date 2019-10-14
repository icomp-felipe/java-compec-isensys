package compec.ufam.sistac.io;

import java.io.*;
import com.phill.libs.*;
import compec.ufam.sistac.model.*;
import compec.ufam.sistac.exception.*;

/** Classe que lê e processa os dados de um arquivo .csv proveniente do PSCONCURSOS
 *  @author Felipe André
 *  @version 2.50, 07/07/2018 */
public class CSVSheetReader {

	/** Processa o arquivo .csv 'planilha' e retorna um 'ParseResult' que é um ArrayList com todas as entradas de sucesso e erro */
	public static ParseResult read(File planilha, int[] indexes) throws IOException {
		
		int linha = 2;
		String row,csvDelimiter;
		
		BufferedReader stream  = new BufferedReader(new InputStreamReader(new FileInputStream(planilha),"UTF8"));
		ParseResult resultados = new ParseResult();
		
		csvDelimiter = CSVUtils.getCSVDelimiter(stream);
		
		while ( (row = stream.readLine()) != null ) {
			
			String[] args = readLine(row,csvDelimiter,indexes);
			
			try {
				Candidato candidato = CandidatoBuilder.parse(linha,args);
				resultados.addCandidato(candidato);
			} catch (RowParseException exception) {
				resultados.addExcecao(exception);
			}
			finally {
				linha++;
			}
			
		}
		
		stream.close();
		
		return resultados;
		
	}
	
	/** Processa cada linha do arquivo .csv */
	private static String[] readLine(String row, String csvDelimiter, int[] indexes) {
		
		String[] args  = new String[9];		// Array que armazena os dados lidos e formatados de cada linha do arquivo .csv
		String[] split;						// Array que armazena temporariamente os dados lidos do .csv
		
		split = row.trim().toUpperCase().split(csvDelimiter);
		
		// Copia as colunas descritas por 'indexes' para 'args'
		for (short i=0; i<indexes.length; i++) {
			int currentField = indexes[i];
			args[i] = split[currentField];
		}
		
		return args;
	}
	
}
