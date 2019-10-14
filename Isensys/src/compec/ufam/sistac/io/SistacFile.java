package compec.ufam.sistac.io;

import java.io.*;
import java.text.*;
import java.util.*;
import compec.ufam.sistac.model.*;

/** Classe de manipulação do arquivo Sistac
 *  @author Felipe André
 *  @version 2.50, 07/07/2018 */
public class SistacFile {

	/** Alguns dados (UFAM) */
	public static final String UFAM_HEADER = "0;04378626000197;UNIVERSIDADE FEDERAL DO AMAZONAS;FUNDACAO UNIVERSIDADE DO AMAZONAS;";
	public static final String UFAM_CNPJ   = "04378626000197";
	
	/** Escreve o arquivo para envio ao Sistac */
	public static boolean generate(ArrayList<Candidato> listaCandidatos, File arquivo) {
		
		try {
			
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
			
			return true;
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
		
		return false;
	}

	/** Lẽ e processa o arquivo de retorno do Sistac lendo suas entradas e inserindo-as na 'listaRetornos' */
	public static void readRetorno(ListaRetornos listaRetornos, File arquivo) throws IOException {
		
		String row;
		BufferedReader stream  = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo),"UTF8"));

		// Pulando a linha de cabeçalho
		stream.readLine();
		
		// Lendo e processando as linhas de retorno do arquivo
		while ( (row = stream.readLine() ) != null) {
			
			Retorno retorno = parseLine(row);
			listaRetornos.add(retorno);
				
		}
		
		// Fechando o arquivo
		stream.close();
		
	}
	
	/** Monta um objeto 'Retorno' de acordo com a linha 'infos' proveniente do arquivo Sistac */
	private static Retorno parseLine(String infos) {
		
		String[] splitted = infos.split(";");
		
		String nome = splitted[1];
		String nis  = splitted[2];
		String cpf  = splitted[8];
		String cod  = splitted[splitted.length-1];
		
		return new Retorno(nis, nome, cpf, cod);
	}

	/** Monta o nome de exportação do arquivo Sistac */
	public static File getSistacExportName(File path, String edital, String sequencia) {
		String filename = String.format("%s/%s_%s_%s_%s.txt",path.getParent(),UFAM_CNPJ,edital,getDataAtual(),sequencia);
		return new File(filename);
	}
	
	/** Retorna a data atual do sistema já formatada para montagem do nome do arquivo Sistac */
	private static String getDataAtual() {        
	    SimpleDateFormat sd = new SimpleDateFormat("ddMMyyyy");
	    Date dataAtual = new Date(System.currentTimeMillis());
	    return sd.format(dataAtual);        
	}
	
}
