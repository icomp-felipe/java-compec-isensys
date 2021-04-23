package compec.ufam.sistac.constants;

import javax.swing.filechooser.FileNameExtensionFilter;

public class Constants {
	
	public static class FileFormat {
		
		public static final FileNameExtensionFilter CSV_N = new FileNameExtensionFilter("Texto Separado por Vírgula (.csv)", "csv");
		public static final FileNameExtensionFilter BSF   = new FileNameExtensionFilter("Arquivo de Compilação (.bsf)", "bsf");
		public static final FileNameExtensionFilter XLSX  = new FileNameExtensionFilter("Planilha do Excel 2007/2010/2013 (.xlsx)","xlsx");
		public static final FileNameExtensionFilter TSV   = new FileNameExtensionFilter("Texto Separado por Tabulação (.tsv)", "tsv");
		public static final FileNameExtensionFilter SISTAC_SEND = new FileNameExtensionFilter("Arquivo de Envio Sistac (.txt)","txt");
		public static final FileNameExtensionFilter SISTAC_RETV = new FileNameExtensionFilter("Arquivo de Retorno Sistac (.txt)","txt");
		
		public static final FileNameExtensionFilter[] SISTAC_INPUT = new FileNameExtensionFilter[]{CSV_N,TSV,XLSX};
		
	}
	
	public static class SheetIndex {
		
		/** Índices dos campos aproveitados do arquivo de retorno do Sistac. */
		public static final int[] CSV_RETURN_SHEET = {
				                                       1,   // Nome do solicitante
                                                       2,   // NIS
                                                       8,   // CPF
                                                       10,  // Situação ('N' para indeferido ou 'S' para deferido
                                                       11   // Código de situação (motivo), caso esta seja 'N' (ver manual do Sistac)
                                                     };
		
		/** Índices dos campos aproveitados da planilha de erros. */
		public static final int XLSX_ERROR_SHEET[] = { 1,   // Nome do solicitante
				                                       0,   // NIS
				                                       2    // CPF
				                                     };
		
		/** Cabeçalho da planilha de erros (especificação das colunas). */
		public static final String[] XLSX_COLUMN_TITLES = {"Linha", "NIS", "Nome", "CPF", "Situação", "Motivo"};
		
	}
	
	public static class StringFormat {
		
		/** Máscara de String de acordo com o formato Sistac. */
		public static final String ROW_DATA_FORMAT = "1;%s;%s;%s;%c;%s;%s;%s;%s;%s;";
		
	}

}
