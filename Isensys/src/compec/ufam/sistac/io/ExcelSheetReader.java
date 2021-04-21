package compec.ufam.sistac.io;

import java.io.*;
import java.text.*;
import java.util.*;

import compec.ufam.sistac.model.envio.Candidato;
import compec.ufam.sistac.model.envio.CandidatoBuilder;
import compec.ufam.sistac.model.envio.ParseResult;
import compec.ufam.sistac.model.retorno.ListaRetornos;
import compec.ufam.sistac.model.retorno.Retorno;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import compec.ufam.sistac.constants.*;
import compec.ufam.sistac.exception.*;
import org.apache.poi.xssf.usermodel.*;

/** Classe que lê e processa os dados de um arquivo .xlsx com os dados necessários para solicitação de isenção.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 20/04/2021 */
public class ExcelSheetReader {

	// Forma
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static final DataFormatter    DATA_FORMATTER = new DataFormatter(Locale.getDefault());
	
	/** Processa o arquivo .xlsx 'planilha' e retorna para dentro de um objeto {@link ParseResult}.
	 *  @param planilha - caminho do arquivo .xlsx
	 *  @param indexes - índices dos campos de importação de dados
	 *  @return Objeto com TODAS as entradas lidas do arquivo .csv.
	 *  @throws IOException quando há alguma falha na leitura da planilha. 
	 *  @throws InvalidFormatException quando a planilha está com um formato desconhecido ou corrompida. */
	public static ParseResult read(final File planilha, final int[] indexes) throws IOException, InvalidFormatException {
		
		// Variável usada para controle de erros. Os dados começam sempre na linha 2 do arquivo .csv
		int linha = 2;
		
		// Abrindo planilha para leitura
		XSSFWorkbook  workbook = new XSSFWorkbook   (planilha);
		XSSFSheet    xssfSheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator = xssfSheet.iterator();	//Preparando loop
		rowIterator.next();									// Pulando a primeira linha (cabeçalho)
		
		ParseResult resultados = new ParseResult();			// Instanciando lista de resultados
		
		// Recuperando dados das linhas da planilha
		while (rowIterator.hasNext()) {
			
			Row row = rowIterator.next();				// Recuperando uma linha da planilha
			String[] dados = readLine(row, indexes);	// Recuperando os dados de uma linha já separados em um array
			
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
		
		// Liberando recursos
		workbook.close();
		
		return resultados;
		
	}

	
	public static void readErros(ListaRetornos listaRetornos, File planilhaRetorno) throws IOException {
		
		FileInputStream stream = new FileInputStream(planilhaRetorno);
		
		XSSFWorkbook workbook = new XSSFWorkbook(stream);
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();
		
		while (rowIterator.hasNext()) {
			
			Row row = rowIterator.next();
			String[] args = readLine(row, 3, Constants.Index.XLSX_ERROR_SHEET);

			Retorno retorno = new Retorno();
			retorno.setNIS ( args[0] );
			retorno.setNome( args[1] );
			retorno.setCPF ( args[2] );
			retorno.setSituacao("N");
			retorno.setMotivo  ("-1");
			
			listaRetornos.add(retorno);
			
		}
		
		workbook.close();
		
	}
	
	/** Monta um array de {@link String} com os dados extraídos da 'row' e organizados de acordo com os 'indexes'.
	 *  @param row - linha extraída do arquivo .xlsx
	 *  @param indexes - índices de importação de dados
	 *  @return Um array de {@link String} com os dados extraídos de uma linha do .xlsx. */
	private static String[] readLine(final Row row, final int[] indexes) {
		return readLine(row, indexes.length, indexes);
	}
	
	/** Monta um array de {@link String} com os dados extraídos da 'row' e organizados de acordo com os 'indexes'.
	 *  @param row - linha extraída do arquivo .xlsx
	 *  @param size - tamanho do vetor de Strings a ser retornado
	 *  @param indexes - índices de importação de dados
	 *  @return Um array de {@link String} com os dados extraídos de uma linha do .xlsx. */
	private static String[] readLine(final Row row, final int size, final int[] indexes) {
		
		// Instanciando o array de strings
		String[] dados = new String[size];
		
		// Copia as colunas descritas por 'indexes' para 'dados'. A ordem do objeto de retornos é SEMPRE igual a descrita abaixo:
		// Nome, NIS, Data de Nascimento, Sexo, RG, Data de Emissão do RG, órgão Emissor do RG, CPF, Nome da Mãe
		for (short i=0; i<size; i++) {
			
			Cell currentCell = row.getCell(indexes[i]);						// Recuperando a 'indexes-ésima' célula de uma linha
			dados[i] = getCellContent(currentCell).trim().toUpperCase();	// Recupera os dados da célula sempre como String
			
		}
		
		return dados;
	}
	
	/** Recuperando os dados de uma <code>célula</code> da planilha, sempre como {@link String}.
	 *  @param celula - uma célula de planilha
	 *  @return Uma String com os dados da <code>célula</code>, ou a String "null",
	 *  caso a <code>célula</code> seja nula ou de um tipo diferente de 'STRING' ou 'NUMERIC'.
	 *  @see CellType */
	private static String getCellContent(final Cell celula) {
		
		if (celula == null)
			return "null";
		
		switch (celula.getCellType()) {
		
			case STRING:
				return celula.getStringCellValue();
				
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(celula))
					return DATE_FORMATTER.format(celula.getDateCellValue());
				else
					return DATA_FORMATTER.formatCellValue(celula);
			
			default:
				break;
				
		}
		
		return "null";
		
	}
	
}
