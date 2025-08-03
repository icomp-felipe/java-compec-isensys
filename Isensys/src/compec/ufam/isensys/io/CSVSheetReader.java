package compec.ufam.isensys.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.phill.libs.files.CSVUtils;

import compec.ufam.isensys.exception.RowParseException;
import compec.ufam.isensys.model.CandidatoBuilder;
import compec.ufam.isensys.model.CandidatoValidator;
import compec.ufam.isensys.model.Instituicao;
import compec.ufam.isensys.model.ParseResult;
import compec.ufam.isensys.model.retorno.ListaRetornos;
import compec.ufam.isensys.model.retorno.Retorno;

/** Classe que lê e processa os dados de um arquivo csv pré-formatado (no formato Sistac) com os dados necessários para solicitação de isenção.
 *  Aqui são realizadas verificações na planilha e geradas uma lista de candidatos aptos a serem exportados para o Sistac e uma lista de erros,
 *  útil para a construção do edital.
 *  Há um modelo válido deste arquivo em 'res/examples/input-sistac.csv'
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 03/AGO/2025 */
public class CSVSheetReader {

	/** Extrai os dados de solicitações da <code>planilha</code> para dentro de um objeto {@link ParseResult}.
	 *  @param planilha - arquivo da planilha (modo texto)
	 *  @return Objeto com TODAS as entradas lidas da planilha.
	 *  @throws IOException quando há alguma falha na leitura da planilha. */
	public static ParseResult read(final File planilha) throws IOException {
		
		try (Stream<String> lines = Files.lines(planilha.toPath(), StandardCharsets.UTF_8)) {
			
			// Variável usada para controle de erros. Os dados começam sempre na linha 2 do arquivo
			final AtomicInteger contadorLinha = new AtomicInteger(2);
			
			ParseResult resultados = new ParseResult();
			
			lines.skip(1)											// Ignora a linha de cabeçalho
			 	 .map(linha -> linha.split(";", -1))				// Quebra cada linha por ; (considerando campos vazios)
			 	 .map(campos -> new String[] {campos[1].strip(),	// Extrai apenas os campos 'nome, CPF e data de nascimento' do arquivo original do PSConcursos
			 			 					  campos[8].strip(),
			 			 					  campos[3].strip()})
			 	 .map(CandidatoBuilder::build)						// Monta o objeto 'Candidato'
			 	 .forEach(candidato -> {
			 		 
			 		 try {
			 			 
			 			CandidatoValidator.validate(candidato, contadorLinha.get());
			 			 
			 			// Se não houve nenhum erro de processamento, o candidato é adicionado a uma lista própria
						if (!resultados.addCandidato(candidato))
							System.err.printf("* Ignorando candidato duplicado na linha %d: '%s'%n", contadorLinha.get(), candidato.getNome());
			 			 
			 		 }
			 		 catch (RowParseException exception) {
			 			 
			 			// Se houver um erro no processamento dos campos, este é adicionado a uma lista separada da lista de candidatos
						resultados.addExcecao(exception);
			 			 
			 		 }
			 		finally {
						
						// Incrementando a contagem de linhas de arquivo processadas
						contadorLinha.incrementAndGet();
						
					}

			});

			return resultados;
		}
		
	}
	
	/** Incorpora os retornos contidos na <code>planilha</code> do Sistac à <code>listaRetornos</code>.
	 *  @param planilha - arquivo de retorno do Sistac
	 *  @param listaRetornos - lista de retornos
	 *  @param listaRecursos - lista de recursos
	 *  @param preliminar - indica se o arquivo de retorno é pra confecção do resultado preliminar (true) ou definitivo (false)
	 *  @throws IOException quando a planilha não pode ser lida. */
	public static void readRetorno(final File planilha, final ListaRetornos listaRetornos, final ListaRetornos listaRecursos, final boolean preliminar) throws IOException {
		
		try (Stream<String> lines = Files.lines(planilha.toPath(), StandardCharsets.UTF_8)) {
			
			lines.skip(1)								// Ignora a linha de cabeçalho
				 .map(linha -> linha.split(";", -1))	// Quebra cada linha por ; (considerando campos vazios)
				 .map(Retorno::new)						// Cria um objeto 'Retorno' para cada linha
				 .forEach(retorno -> {
					 
					// Se o resultado é preliminar, acrescento o novo retorno APENAS na lista de retornos
					if (preliminar)
						listaRetornos.add(retorno);
						
					// Se o resultado é definitivo, o novo retorno é atualizado na lista já existente e cadastrado na lista de recursantes
					else {
							
						listaRetornos.update(retorno);
						listaRecursos.add   (retorno);
							
					}
					 
				 });
			
		}
		
	}
	
	/** Carrega os dados institucionais do cabeçalho do arquivo Sistac.
	 *  @param planilha - arquivo de texto do Sistac
	 *  @return Um objeto contendo os dados institucionais carregados do arquivo Sistac.
	 *  @throws IOException quando a planilha não pode ser lida.
	 *  @since 3.0, 21/04/2021 */
	public static Instituicao getInstituicao(final File planilha) throws IOException {
		
		// Abrindo planilha para leitura
		BufferedReader stream  = new BufferedReader(new InputStreamReader(new FileInputStream(planilha), StandardCharsets.UTF_8));
		
		// Lendo cabeçalho
		final String firstLine = stream.readLine();
		final String csvDelimiter = CSVUtils.getCSVDelimiter(firstLine);
		
		// Fechando a planilha 
		stream.close();
		
		// Retornando os dados institucionais
		return new Instituicao(firstLine, csvDelimiter);
	}
	
}
