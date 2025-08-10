package compec.ufam.isensys.constants;

import java.io.File;
import java.time.format.DateTimeFormatter;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.phill.libs.ResourceManager;

@Reviewed("2025-08-10")
/** Armazena as diversas constantes utilizadas em todo o sistema.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 10/AGO/2025 */
public class Constants {
	
	/** Caminho do arquivo de configuração do sistema. */
	public static final File SYS_CONFIGS_FILE = ResourceManager.getResourceAsFile("config/isensys.bin");
	
	/** Formatos de arquivo utilizados nos métodos de seleção de arquivos.
	 *  @author Felipe André - felipeandre.eng@gmail.com
     *  @version 4.0, 10/AGO/2025 */
	public static class FileFormat {
		
		public static final FileNameExtensionFilter CSV = new FileNameExtensionFilter("Texto Separado por Vírgula (.csv)", "csv");
		public static final FileNameExtensionFilter TXT = new FileNameExtensionFilter("Texto Separado por Vírgula (.txt)", "txt");
		
		public static final FileNameExtensionFilter ICF   = new FileNameExtensionFilter("Arquivo de Compilação do IsenSys (.icf)" , "icf" );
		public static final FileNameExtensionFilter XLSX  = new FileNameExtensionFilter("Planilha do Excel 2007/2010/2013 (.xlsx)", "xlsx");
		
		public static final FileNameExtensionFilter SISTAC_SEND = new FileNameExtensionFilter("Arquivo de Envio Sistac (.txt)"  , "txt");
		public static final FileNameExtensionFilter SISTAC_RETV = new FileNameExtensionFilter("Arquivo de Retorno Sistac (.txt)", "txt");
		
		public static final FileNameExtensionFilter[] SISTAC_INPUT = new FileNameExtensionFilter[] { CSV, TXT, XLSX };
		
	}
	
	/** Índices de coluna de planilha.
	 *  @author Felipe André - felipeandre.eng@gmail.com
     *  @version 4.0, 10/AGO/2025 */
	public static class SheetIndex {
		
		/** Cabeçalho da planilha de erros (especificação das colunas). */
		public static final String[] XLSX_COLUMN_TITLES = {"Linha", "Nome", "CPF", "Data de Nascimento", "Situação", "Motivo"};
		
	}
	
	/** Formatos de data mais utilizados no sistema.
	 *  @author Felipe André - felipeandre.eng@gmail.com
     *  @version 4.0, 10/AGO/2025 */
	public static class DateFormatters {
		
		public static final DateTimeFormatter EXCEL_DATE = DateTimeFormatter.ofPattern("dMMuuuu");
		public static final DateTimeFormatter SISTAC_DATE = DateTimeFormatter.ofPattern("ddMMuuuu");
		public static final DateTimeFormatter BRAZILIAN_DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu");
		public static final DateTimeFormatter BRAZILIAN_LGPD = DateTimeFormatter.ofPattern("**/MM/uuuu");
		
	}
	
	/** Formatos de String.
	 *  @author Felipe André - felipeandre.eng@gmail.com
     *  @version 4.0, 10/AGO/2025 */
	public static class StringFormat {
		
		/** Máscara de String de acordo com o formato Sistac. */
		public static final String ROW_DATA_FORMAT             = "1;%s;%s;%s;";
		public static final String SISTAC_SEND_FILENAME_FORMAT = "%s_%s_%s_%03d.txt";
		public static final String SISTAC_RETV_FILENAME_FORMAT = "RETORNO_%s_%s_%s_%03d.txt";
		public static final String ERROS_FILENAME_FORMAT       = "ERROS_%s_%s_%s.xlsx";
		public static final String COMPILACAO_FILENAME_FORMAT  = "COMPILACAO_%s_%s_%s.icf"; 
		
	}

}
