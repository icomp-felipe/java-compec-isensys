package compec.ufam.isensys.io;

import java.io.*;
import java.util.*;

import java.nio.charset.*;

import compec.ufam.isensys.model.*;
import compec.ufam.isensys.model.Candidato;

/** Classe de manipulação do arquivo Sistac
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 3.8, 21/JUN/2023 */
public class CSVSheetWriter {

	/** Gera o(s) arquivo(s) de envio no formato Sistac. A cada 2.000 candidatos, um novo arquivo é gerado com o próximo número de sequência, devido a limitações no Sistac.
	 *  @param listaCandidatos - lista de candidatos a ser enviada ao Sistac
	 *  @param diretorio - diretório 'pai' onde serão escritos os arquivos
	 *  @param configs - configurações do sistema
	 *  @param edital - informações do edital
	 *  @throws IOException caso haja alguma falha na escrita do(s) arquivos. */
	public static void write(final ArrayList<Candidato> listaCandidatos, final File diretorio, final IsensysConfig configs, final String edital, int sequencia) throws IOException {
		
		// Calcula a quantidade de arquivos de envio necessária para abranger todos os candidatos, o limite do Sistac é 2.000 por arquivo 
		int i, qtdArquivos = (int) Math.ceil(listaCandidatos.size() / 2000f);
		
		// Loop dos arquivos
		for (int arquivoAtual = 0; qtdArquivos >= 1; qtdArquivos--, sequencia++, arquivoAtual++) {

			// Abrindo arquivo para escrita
			final File arquivo = FileNameUtils.getSistacFilename(diretorio, configs, edital, sequencia);
			
			try (PrintWriter stream = new PrintWriter(arquivo, StandardCharsets.UTF_8)) {
				
				// Imprimindo cabeçalho
				stream.println(configs.getCabecalhoSistac());
			
				// Imprimindo candidatos, de 2.000 em 2.000 até acabar a lista
				for (i = 2000 * arquivoAtual; (i < 1999 * (arquivoAtual + 1) + arquivoAtual) && (i < listaCandidatos.size() - 1); i++)
					stream.println(listaCandidatos.get(i).getDadosSistac());
			
				// Imprimindo a última linha do arquivo, para evitar '\n' ao final
				stream.print(listaCandidatos.get(i).getDadosSistac());
			
			}
			
		}
		
	}
	
}
