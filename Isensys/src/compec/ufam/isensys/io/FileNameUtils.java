package compec.ufam.isensys.io;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import compec.ufam.isensys.constants.Constants;
import compec.ufam.isensys.model.IsensysConfig;

public class FileNameUtils {

	private static final Pattern pattern = Pattern.compile("(\\d+)(?!.*\\d)");
	
	public static boolean iguais(File arquivo1, File arquivo2) {
		
		try {
			
			final String[] dados1 = arquivo1.getName().split("_");
			final String[] dados2 = arquivo2.getName().split("_");
			
			return dados1[1].equals(dados2[1]) && dados1[2].equals(dados2[2]) && dados1[3].equals(dados2[3]);
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			return false;
		}
		
	}
	
	public static boolean iguaisSemData(String arquivo1, File arquivo2) {
		
		try {
			
			final String[] dados2 = arquivo2.getName().split("_");
			
			return arquivo1.equals(dados2[2]);
			
		}
		catch (Exception exception) {
			
			exception.printStackTrace();
			return false;
		}
		
	}
	
	/** Monta o nome do arquivo de erros, com os dados internos desta classe.
	 *  @param parent - diretório do arquivo
	 *  @return Um arquivo com o nome no formato 'ERROS_CNPJ_EDITAL_DATA.xlsx' */
	public static File getErrorFilename(final File parent, String cnpj, String edital) {
		
		String filename = String.format(Constants.StringFormat.ERROS_FILENAME_FORMAT, cnpj, edital, getDataSistac());
		
		return new File(parent, filename);
	}
	
	/** Monta o nome do arquivo de envio do Sistac.
	 *  @param diretorio - diretório 'pai' onde serão escritos os arquivos
	 *  @param configs - configurações do sistema
	 *  @param edital - informações do edital
	 *  @param sequencia - número de sequência de arquivo */
	public static File getSistacFilename(final File diretorio, final IsensysConfig configs, final String edital, final int sequencia) {
		
		final String filename = String.format(Constants.StringFormat.SISTAC_SEND_FILENAME_FORMAT, configs.getCNPJ(), edital, getDataSistac(), sequencia);
		
		return new File(diretorio, filename);
	}
	
	/** Retorna a data atual do Sistac (UTC ou GMT+0) no formato DDMMYYYY. 
	 *  @return Uma String contendo a data atual do Sistac no formato DDMMYYYY. */
	private static String getDataSistac() {
		
		LocalDate sistac = LocalDate.now(ZoneOffset.UTC);
		
	    return sistac.format(Constants.DateFormatters.SISTAC_DATE);
	}

	public static Integer getSequencia(File arqRetornoSistac) {
		
		if (arqRetornoSistac != null) {
			
			Matcher m = pattern.matcher(arqRetornoSistac.getName());
			
			if (m.find())
				return Integer.parseInt(m.group(1));
			
		}
		
		return null;
	}

	/** Monta o nome do arquivo de compilação, com os dados internos desta classe.
	 *  @return Nome no formato 'COMPILACAO_CNPJ_EDITAL_DATA.xlsx' */
	public static File getCompilationFilename(File parent, IsensysConfig config, String edital, String dataEdital) {
		return new File(parent, String.format(Constants.StringFormat.COMPILACAO_FILENAME_FORMAT, config.getCNPJ(), edital, dataEdital));
	}
	
	public static File getNextRetornoFile(File arqRetornoSistac, int sequencia) {
		
		if (arqRetornoSistac != null) {
			
			Matcher m = pattern.matcher(arqRetornoSistac.getName());
			
			if (m.find()) {
				
				String filename = String.format("%s%03d.txt", arqRetornoSistac.getName().substring(0, m.start(1)), sequencia);
				
				return new File(arqRetornoSistac.getParent(), filename);
			}
			
		}
		
		return null;
	}
	
}
