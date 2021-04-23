package compec.ufam.isensys.io;

import java.io.*;

import com.phill.libs.files.*;

import compec.ufam.isensys.model.*;
import compec.ufam.isensys.model.envio.*;
import compec.ufam.isensys.model.retorno.*;
import compec.ufam.isensys.constants.*;
import compec.ufam.isensys.exception.*;

/** Classe que lê e processa os dados de um arquivo csv pré-formatado (no formato Sistac) com os dados necessários para solicitação de isenção.
 *  Aqui são realizadas verificações na planilha e geradas uma lista de candidatos aptos a serem exportados para o Sistac e uma lista de erros,
 *  útil para a construção do edital.
 *  Há um modelo válido deste arquivo em 'res/examples/input-sistac.csv'
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.5, 23/04/2021 */
public class CSVSheetReader {

	/** Processa o arquivo .csv 'planilha' e retorna para dentro de um objeto {@link ParseResult}.
	 *  @param planilha - caminho da planilha csv
	 *  @param indexes - índices dos campos de importação de dados
	 *  @return Objeto com TODAS as entradas lidas do arquivo csv.
	 *  @throws IOException quando há alguma falha na leitura da planilha. */
	public static ParseResult read(final File planilha, final int[] indexes) throws IOException {
		
		// Variável usada para controle de erros. Os dados começam sempre na linha 2 do arquivo csv
		int linha = 2;
		
		// Variável auxiliar ao loop de leitura do csv
		String row;
		
		// Abrindo planilha para leitura
		BufferedReader stream  = new BufferedReader(new InputStreamReader(new FileInputStream(planilha), "UTF8"));
		ParseResult resultados = new ParseResult();
		
		// Recuperando delimitador do .csv
		final String csvDelimiter = CSVUtils.getCSVDelimiter(stream);
		
		// Iterando as linhas do .csv
		while ( (row = stream.readLine()) != null ) {
			
			// Recuperando os dados de uma linha já separados em um array
			String[] dados = readLine(row, csvDelimiter, indexes);
			
			try {
				
				// Montando uma classe candidato com os 'dados' lidos da 'linha'
				Candidato candidato = CandidatoBuilder.parse(linha, dados);
				
				// Se não houve nenhum erro de processamento, o candidato é adicionado a uma lista própria
				resultados.addCandidato(candidato);
				
			}
			catch (RowParseException exception) {
				
				// Se houver um erro no processamento dos campos, este é adicionado a uma lista separada da lista de candidatos
				resultados.addExcecao(exception);
				
			}
			finally {
				
				// Incrementando a contagem de linhas de arquivo processadas
				linha++;
				
			}
			
		}
		
		// Fechando a planilha
		stream.close();
		
		return resultados;
		
	}
	
	/** Incorpora os retornos contidos na <code>planilha</code> do Sistac à <code>listaRetornos</code>.
	 *  @param planilha - arquivo de retorno do Sistac
	 *  @param listaRetornos - lista de retornos
	 *  @throws IOException quando a planilha não pode ser lida. */
	public static void readRetorno(final File planilha, final ListaRetornos listaRetornos) throws IOException {
		
		// Variável auxiliar ao loop de leitura do csv
		String row;
		
		// Abrindo planilha para leitura
		BufferedReader stream  = new BufferedReader(new InputStreamReader(new FileInputStream(planilha), "UTF8"));

		// Recuperando delimitador do csv
		final String csvDelimiter = CSVUtils.getCSVDelimiter(stream);
		
		// Lendo e processando as linhas de retorno do arquivo
		while ( (row = stream.readLine() ) != null) {
			
			// Recuperando os dados de uma linha já separados em um array
			String[] dados = readLine(row, csvDelimiter, Constants.SheetIndex.CSV_RETURN_SHEET);
			
			// Montando objeto 'Retorno'
			Retorno retorno = new Retorno( dados[0], dados[1], dados[2], dados[3], dados[4] );
			
			// Adicionando novo objeto retorno à lista recebida via parâmetro
			listaRetornos.add(retorno);
			
		}
		
		// Fechando a planilha
		stream.close();
		
	}
	
	/** Carrega os dados institucionais do cabeçalho do arquivo Sistac.
	 *  @param planilha - arquivo de texto do Sistac
	 *  @return Um objeto contendo os dados institucionais carregados do arquivo Sistac.
	 *  @throws IOException quando a planilha não pode ser lida.
	 *  @since 3.0, 21/04/2021 */
	public static Instituicao getInstituicao(final File planilha) throws IOException {
		
		// Abrindo planilha para leitura
		BufferedReader stream  = new BufferedReader(new InputStreamReader(new FileInputStream(planilha), "UTF8"));
		
		// Lendo cabeçalho
		final String firstLine = stream.readLine();
		final String csvDelimiter = CSVUtils.getCSVDelimiter(firstLine);
		
		// Fechando a planilha 
		stream.close();
		
		// Retornando os dados institucionais
		return new Instituicao(firstLine, csvDelimiter);
	}
	
	/** Monta um array de {@link String} com os dados extraídos da 'linha' e organizados de acordo com os 'indexes'.
	 *  @param linha - linha extraída do arquivo .csv
	 *  @param csvDelimiter - string delimitadora do .csv
	 *  @param indexes - índices de importação de dados
	 *  @return Um array de {@link String} com os dados extraídos de uma linha do .csv. */
	private static String[] readLine(final String linha, final String csvDelimiter, final int[] indexes) {
		
		String[] dados = new String[indexes.length];	// Array que armazena os dados lidos e formatados de cada linha do arquivo .csv
		String[] aux;									// Array que armazena temporariamente os dados lidos do .csv
		
		// Separando dados de uma linha em um array de Strings. Aqui os espaços em branco não são ignorados!
		aux = linha.trim().toUpperCase().split(csvDelimiter, -1);
		
		// Copia as colunas descritas por 'indexes' para 'dados'. A ordem do objeto de retornos é SEMPRE igual a descrita abaixo:
		// Nome, NIS, Data de Nascimento, Sexo, RG, Data de Emissão do RG, órgão Emissor do RG, CPF, Nome da Mãe
		for (short i=0; i<indexes.length; i++) {
			
			int currentField = indexes[i];		// Recupera o índice atual de 'indexes'
			dados[i] = aux[currentField];		// Copia os dados de 'aux' para 'dados' de acordo com os 'indexes'
			
		}
		
		return dados;
	}
	
}