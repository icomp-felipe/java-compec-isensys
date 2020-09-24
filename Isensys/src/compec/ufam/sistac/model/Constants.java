package compec.ufam.sistac.model;

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

}
