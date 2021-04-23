package compec.ufam.sistac.io;

import java.io.*;
import java.text.*;
import java.util.*;
import compec.ufam.sistac.model.envio.Candidato;

/** Classe de manipulação do arquivo Sistac
 *  @author Felipe André
 *  @version 2.50, 07/07/2018 */
public class SistacFile {

	/** Alguns dados (UFAM) */
	public static final String UFAM_HEADER = "0;04378626000197;UNIVERSIDADE FEDERAL DO AMAZONAS;FUNDACAO UNIVERSIDADE DO AMAZONAS;";
	public static final String UFAM_CNPJ   = "04378626000197";
	
	/** Escreve o arquivo para envio ao Sistac 
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException */
	@SuppressWarnings("resource")
	public static void generate(ArrayList<Candidato> listaCandidatos, File arquivo) throws IOException {
		
			int i;
			PrintWriter stream = new PrintWriter(arquivo,"UTF8");
			
			// Imprimindo o cabeçalho
			stream.println(UFAM_HEADER);
			
			// Imprimindo as linhas (até a penúltima)
			for (i=0; i<listaCandidatos.size()-1; i++)
				stream.println(listaCandidatos.get(i).getResume());
			
			// Imprimindo a última linha (só pra não ficar com o \n no final do arquivo :)
			stream.print(listaCandidatos.get(i).getResume());
			stream.close();
			
	}

	/** Monta o nome de exportação do arquivo Sistac */
	public static File getSistacExportName(File path, String edital, String sequencia) {
		String filename = String.format("%s/%s_%s_%s_%s.txt",path,UFAM_CNPJ,edital,getDataAtual(),sequencia);
		return new File(filename);
	}
	
	/** Retorna a data atual do sistema já formatada para montagem do nome do arquivo Sistac */
	private static String getDataAtual() {        
	    SimpleDateFormat sd = new SimpleDateFormat("ddMMyyyy");
	    Date dataAtual = new Date(System.currentTimeMillis());
	    return sd.format(dataAtual);        
	}
	
}
