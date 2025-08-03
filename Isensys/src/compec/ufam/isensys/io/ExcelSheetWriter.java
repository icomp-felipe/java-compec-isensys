package compec.ufam.isensys.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import compec.ufam.isensys.constants.Constants;
import compec.ufam.isensys.exception.RowParseException;

/** Classe que escreve a planilha de erros de processamento no formato Excel.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 03/AGO/2025 */
public class ExcelSheetWriter {

	/** Cria uma nova <code>planilha</code> no formato Excel, com os dados vindos da <code>listaErros</code>.
	 *  @param listaErros - lista de erros de validação de dados de candidato
	 *  @param planilha - arquivo de saída da planilha Excel
	 *  @throws IOException caso a haja alguma falha de acesso ou escrita no arquivo da planilha. */
	public static void write(final ArrayList<RowParseException> listaErros, final File planilha) throws IOException {
		
		// Só prossegue se tanto o arquivo quanto a lista de erros não são vazios
		if ((planilha != null) && (!listaErros.isEmpty())) {
			
			try (XSSFWorkbook workbook = new XSSFWorkbook()) {
				
				XSSFSheet xssfSheet = workbook.createSheet("Lista de Erros");
				
				// Imprimindo o cabeçalho da planilha
				printHeader(xssfSheet);
				
				// Preenchendo a planilha com os dados de 'listaErros'
				for (int i=0; i<listaErros.size(); i++) {
					
					String[] dados = listaErros.get(i).getErrorSummaryArray();	// Recuperando dados da 'listaErros'
					XSSFRow  row   = xssfSheet.createRow(i+1);					// Criando nova linha na planilha 
					
					// Inserindo dados célula por célula
					for (int j=0; j<dados.length; j++) {
						
						XSSFCell cell = row.createCell(j);
						cell.setCellValue(dados[j]);
						
					}
					
				}
				
				// Calculando largura das colunas
				for (int i=0; i<Constants.SheetIndex.XLSX_COLUMN_TITLES.length; i++)
					xssfSheet.autoSizeColumn(i);
				
				// Escrevendo a planilha no disco
				try (FileOutputStream stream = new FileOutputStream(planilha)) {
					
					workbook.write(stream);
					
				}
				
			}
			
		}
		
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
