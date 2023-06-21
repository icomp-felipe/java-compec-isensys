package compec.ufam.isensys.io;

import java.io.*;
import java.util.*;

import compec.ufam.isensys.constants.*;
import compec.ufam.isensys.exception.*;

import org.apache.poi.xssf.usermodel.*;

/** Classe que escreve a planilha de erros de processamento no formato Excel.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 21/JUN/2023 */
public class ExcelSheetWriter {

	/** Cria uma nova <code>planilha</code> no formato Excel, com os dados vindos da <code>listaErros</code>.
	 *  @param listaErros - lista de erros de validação de dados de candidato
	 *  @param planilha - arquivo de saída da planilha Excel
	 *  @throws IOException caso a haja alguma falha de acesso ou escrita no arquivo da planilha. */
	public static void write(final ArrayList<RowParseException> listaErros, final File planilha) throws IOException {
		
		// Tratamento de arquivo nulo
		if (planilha == null) return;

		// Se a lista não contém erros, a planilha nem é criada
		if (listaErros.size() == 0) return;
		
		// Criando a planilha (RAM)
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet   xssfSheet = workbook.createSheet("Lista de Erros");
		
		// Imprimindo o cabeçalho da planilha
		printHeader(xssfSheet);
		
		// Preenchendo a planilha com os dados de 'listaErros'
		for (int i=0; i<listaErros.size(); i++) {
			
			String[] dados = listaErros.get(i).getErrorResume();	// Recuperando dados da 'listaErros'
			XSSFRow  row   = xssfSheet.createRow(i+1);				// Criando nova linha na planilha 
			
			// Inserindo dados célula por célula desta linha 
			for (int j=0; j<dados.length; j++) {
				
				XSSFCell cell = row.createCell(j);
				cell.setCellValue(dados[j]);
				
			}
			
		}
		
		// Calculando largura das colunas
		for (int i=0; i<Constants.SheetIndex.XLSX_COLUMN_TITLES.length; i++)
			xssfSheet.autoSizeColumn(i);

		// Escrevendo a planilha no disco
		FileOutputStream stream = new FileOutputStream(planilha);
		
		// Liberando recursos
		workbook.write(stream);
		workbook.close();
		
	}
	
	/** Imprime o cabeçalho com nomes das colunas na planilha.
	 *  @param sheet - planilha de trabalho (Apache POI) */
	private static void printHeader(final XSSFSheet sheet) {
		
		final XSSFRow row = sheet.createRow(0);
		final String[] header = Constants.SheetIndex.XLSX_COLUMN_TITLES;
		
		for (int i=0; i<header.length; i++) {
			
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(header[i]);
			
		}
		
	}
	
}