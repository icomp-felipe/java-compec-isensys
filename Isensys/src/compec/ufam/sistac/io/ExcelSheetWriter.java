package compec.ufam.sistac.io;

import java.io.*;
import java.util.*;
import com.phill.libs.ui.*;
import compec.ufam.sistac.exception.*;
import org.apache.poi.xssf.usermodel.*;

/** Classe que escreve a planilha de erros de processamento no formato 'xlsx'
 *  @author Felipe André
 *  @version 2.50, 07/07/2018 */
public class ExcelSheetWriter {

	/** Cabeçalho da Planilha de Erros (especificação das colunas) */
	private static final String[] ERROR_HEADER = new String[]{"NIS","Nome","CPF","Situação","Motivo"};
	
	/** Escreve a 'listaErros' no arquivo 'planilha'.xlsx */
	public static void write(ArrayList<RowParseException> listaErros, File planilha) throws IOException {
		
		// Se o arquivo for inválido, pego minhas coisas e vou embora
		if (planilha == null)
			return;

		// Se a lista não contém erros, nem crio o arquivo 'xlsx'
		int size = listaErros.size();
		if (size == 0) {
			AlertDialog.info("Não há nenhum erro para escrever!");
			return;
		}
		
		// Criando a planilha (RAM)
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Lista de Erros");
		
		// Imprimindo o cabeçalho da planilha
		printHeader(sheet);
		
		// Preenchendo a planilha com os erros
		for (int i=0; i<size; i++) {
			
			String[] args = listaErros.get(i).getErrorResume();
			XSSFRow  row  = sheet.createRow(i+1);
			
			for (int j=0; j<args.length; j++) {
				
				XSSFCell cell = row.createCell(j);
				cell.setCellValue(args[j]);
				
			}
			
		}

		// Escrevendo a planilha no disco
		FileOutputStream stream = new FileOutputStream(planilha);
		
		// Limpando a casa
		workbook.write(stream);
		workbook.close();
		
	}
	
	/** Imprime o cabeçalho com nomes das colunas na planilha */
	private static void printHeader(XSSFSheet sheet) {
		
		XSSFRow row = sheet.createRow(0);
		
		for (int i=0; i<ERROR_HEADER.length; i++) {
			
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(ERROR_HEADER[i]);
			
		}
		
	}
	
}
