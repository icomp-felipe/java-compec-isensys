package compec.ufam.isensys.io;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.*;

import compec.ufam.isensys.constants.*;
import compec.ufam.isensys.exception.*;
import compec.ufam.isensys.model.Candidato;
import compec.ufam.isensys.model.CandidatoBuilder;
import compec.ufam.isensys.model.CandidatoValidator;
import compec.ufam.isensys.model.ParseResult;
import compec.ufam.isensys.model.envio.*;
import compec.ufam.isensys.model.retorno.*;

/** Classe que lê e processa os dados de um arquivo .xlsx com os dados necessários para solicitação de isenção.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 3.8, 21/JUN/2023 */
public class ExcelSheetReader {

	// Forma
	private static final SimpleDateFormat XLS_DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static final DataFormatter    RAW_DATE_FORMATTER = new DataFormatter(Locale.getDefault());
	
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
				Candidato candidato = CandidatoBuilder.build(dados);
				
				CandidatoValidator.validate(candidato, linha);
				
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

	/** Incorpora os erros contidos na <code>planilhaErros</code> à <code>listaRetornos</code>.
	 *  @param planilhaErros - arquivo da planilha de erros
	 *  @param listaRetornos - lista de retornos
	 *  @param listaRecursos - lista de recursos
	 *  @param preliminar - indica se o arquivo de retorno é pra confecção do resultado preliminar (true) ou definitivo (false)
	 *  @throws IOException quando há alguma falha na leitura da planilha.
	 *  @throws InvalidFormatException quando a planilha está com um formato desconhecido ou corrompida. */
	public static void readErros(final File planilhaErros, final ListaRetornos listaRetornos, final ListaRetornos listaRecursos, final boolean preliminar) throws IOException, InvalidFormatException {
		
		// Abrindo planilha para leitura
		XSSFWorkbook  workbook = new XSSFWorkbook   (planilhaErros);
		XSSFSheet    xssfSheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator = xssfSheet.iterator();	//Preparando loop
		rowIterator.next();									// Pulando a primeira linha (cabeçalho)
		
		// Recuperando dados das linhas da planilha
		while (rowIterator.hasNext()) {
			
			Row row = rowIterator.next();											// Recuperando uma linha da planilha
			String[] dados = readLine(row, Constants.SheetIndex.XLSX_ERROR_SHEET);	// Recuperando os dados de uma linha já separados em um array

			// Montando objeto 'Retorno'
			Retorno retorno = new Retorno( dados[0], dados[1], dados[2], "N", "1" );
						
			// Se o resultado é preliminar, acrescento o novo retorno APENAS na lista de retornos
			if (preliminar)
				listaRetornos.add(retorno);
			
			// Se o resultado é definitivo, o novo retorno é atualizado na lista já existente e cadastrado na lista de recursantes
			else {
				
				listaRetornos.update(retorno);
				listaRecursos.add   (retorno);
				
			}
			
		}
		
		// Liberando recursos
		workbook.close();
		
	}
	
	/** Monta um array de {@link String} com os dados extraídos da 'row' e organizados de acordo com os 'indexes'.
	 *  @param row - linha extraída do arquivo .xlsx
	 *  @param size - tamanho do vetor de Strings a ser retornado
	 *  @param indexes - índices de importação de dados
	 *  @return Um array de {@link String} com os dados extraídos de uma linha do .xlsx. */
	private static String[] readLine(final Row row, final int[] indexes) {
		
		// Instanciando o array de strings
		String[] dados = new String[indexes.length];
		
		// Copia as colunas descritas por 'indexes' para 'dados'. A ordem do objeto de retornos é SEMPRE igual a descrita abaixo:
		// Nome, NIS, Data de Nascimento, Sexo, RG, Data de Emissão do RG, órgão Emissor do RG, CPF, Nome da Mãe
		for (short i=0; i<indexes.length; i++) {
			
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
					return XLS_DATE_FORMATTER.format(celula.getDateCellValue());
				else
					return RAW_DATE_FORMATTER.formatCellValue(celula);
			
			default:
				break;
				
		}
		
		return "null";
		
	}
	
}