package compec.ufam.sistac.io;

import java.io.*;
import java.text.*;
import java.util.*;
import compec.ufam.sistac.model.*;
import org.apache.poi.ss.usermodel.*;
import compec.ufam.sistac.exception.*;
import org.apache.poi.xssf.usermodel.*;

/** Classe que lê e processa os dados de um arquivo .csv proveniente do Google Drive
 *  @author Felipe André
 *  @version 2.50, 07/07/2018 */
public class ExcelSheetReader {

	public static final int RETURN_INDEXES[] = new int[]{0,1,2,4};
	
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static final DataFormatter    DATA_FORMATTER = new DataFormatter(Locale.getDefault());
	public static void readRetorno(ListaRetornos listaRetornos, File planilhaRetorno) throws IOException {
		
		FileInputStream stream = new FileInputStream(planilhaRetorno);
		
		XSSFWorkbook workbook = new XSSFWorkbook(stream);
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();
		
		while (rowIterator.hasNext()) {
			
			Row row = rowIterator.next();
			String[] args = readLine(row, RETURN_INDEXES);
			
			args[3] = "*";
			
			Retorno retorno = new Retorno(args);
			listaRetornos.add(retorno);
			
		}
		
		workbook.close();
		
	}
	
	public static ParseResult read(File planilha, int[] indexes) throws IOException {
		
		int linha = 2;
		
		FileInputStream stream = new FileInputStream(planilha);
		
		XSSFWorkbook workbook = new XSSFWorkbook(stream);
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();
		
		ParseResult resultados = new ParseResult();
		
		while (rowIterator.hasNext()) {
			
			Row row = rowIterator.next();
			String[] args = readLine(row, indexes);
			
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
		
		workbook.close();
		
		return resultados;
	}
	
	private static String[] readLine(Row row, int[] indexes) {
		String[] args = new String[indexes.length];
		
		for (short i=0; i<indexes.length; i++) {
			Cell currentCell = row.getCell(indexes[i]);
			args[i] = getCellContent(currentCell).trim().toUpperCase();
		}
		
		return args;
	}
	
	private static String getCellContent(Cell cell) {
		
		if (cell == null)
			return "null";
		
		switch (cell.getCellType()) {
		
			case STRING:
				return cell.getStringCellValue();
				
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return DATE_FORMATTER.format(cell.getDateCellValue());
				}
				else
					return DATA_FORMATTER.formatCellValue(cell);
			
			default:
				break;
				
		}
		
		return "null";
		
	}
	
}
