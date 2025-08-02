package compec.ufam.isensys.constants;

import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.phill.libs.ResourceManager;

/** Armazena as diversas constantes utilizadas em todo o sistema.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 3.8, 21/JUN/2023 */
public class Constants {
	
	/** Caminho do arquivo de configuração do sistema. */
	public static final File SYS_CONFIGS_FILE = ResourceManager.getResourceAsFile("config/program.dat");
	
	/** Formatos de arquivo utilizados nos métodos de seleção de arquivos.
	 *  @author Felipe André - felipeandre.eng@gmail.com
     *  @version 3.5, 14/11/2023 */
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
     *  @version 3.5, 23/04/2021 */
	public static class SheetIndex {
		
		/** Índices dos campos aproveitados do arquivo de retorno do Sistac. */
		public static final int[] CSV_RETURN_SHEET = {
				                                       1,   // Nome do solicitante
                                                       2,   // CPF
                                                       3,   // Data de nascimento
                                                       4,  // Situação ('N' para indeferido ou 'S' para deferido
                                                       5   // Código de situação (motivo), caso esta seja 'N' (ver manual do Sistac)
                                                     };
		
		/** Índices dos campos aproveitados da planilha de erros. */
		public static final int XLSX_ERROR_SHEET[] = { 2,   // Nome do solicitante
				                                       1,   // NIS
				                                       3    // CPF
				                                     };
		
		/** Cabeçalho da planilha de erros (especificação das colunas). */
		public static final String[] XLSX_COLUMN_TITLES = {"Linha", "NIS", "Nome", "CPF", "Situação", "Motivo"};
		
		/** Títulos das colunas de importação de dados */
		public static final String[] IMPORT_COLUMN_TITLES = {"Nome", "NIS", "Dt. Nascimento", "Sexo", "RG", "Data Emissão RG", "Órgão Emissor RG", "CPF", "Nome da Mãe"};
		
	}
	
	/** Formatos de String.
	 *  @author Felipe André - felipeandre.eng@gmail.com
     *  @version 4.0, 02/AGO/2025 */
	public static class StringFormat {
		
		/** Máscara de String de acordo com o formato Sistac. */
		public static final String ROW_DATA_FORMAT             = "1;%s;%s;%s;";
		public static final String SISTAC_SEND_FILENAME_FORMAT = "%s_%s_%s_%03d.txt";
		public static final String SISTAC_RETV_FILENAME_FORMAT = "RETORNO_%s_%s_%s_%03d.txt";
		public static final String ERROS_FILENAME_FORMAT       = "ERROS_%s_%s_%s.xlsx";
		public static final String COMPILACAO_FILENAME_FORMAT  = "COMPILACAO_%s_%s_%s.icf"; 
		
	}

}