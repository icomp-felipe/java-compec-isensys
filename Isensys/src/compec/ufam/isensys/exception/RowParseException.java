package compec.ufam.isensys.exception;

import java.util.*;

import compec.ufam.isensys.constants.Constants;
import compec.ufam.isensys.io.*;
import compec.ufam.isensys.model.Candidato;

/** Classe especial de exceção, utilizada pelo método {@link CandidatoBuilder#parse(int, String[])}.
 *  Representa as violações de requisitos que os dados de um candidato pode gerar. 
 *  Contém internamente dados que identificam o candidato, bem como uma lista de {@link FieldParseException}
 *  identificando cada uma das violações de requisitos.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 3.5.1, 26/04/2021 */
public class RowParseException extends Exception {

	// Serial de versionamento
	private static final long serialVersionUID = 3;
	
	// Atributos de processamento
	private final int linha;
	private final Candidato candidato;
	private final ArrayList<FieldParseException> listaExcecoes;
	
	{
		this.listaExcecoes = new ArrayList<FieldParseException>();
	}
	
	public RowParseException(final Candidato candidato, final int linha) {
		this.candidato = candidato;
		this.linha = linha;
	}
	
	/************************ Bloco de Funcionalidades *************************/
	
	/** Adiciona uma nova exceção de requisito à lista interna desta instância de classe.
	 *  @param exception - exceção representando a violação de requisitos de um determinado dado de candidato */
	public void addException(final FieldParseException exception) {
		this.listaExcecoes.add(exception);
	}

	/** Verifica se há algum {@link FieldParseException} na lista interna desta classe.
	 *  @return 'true' apenas se a lista interna de exceções tiver pelo menos um elemento;<br>'false', caso contrário. */
	public boolean hasException() {
		return !this.listaExcecoes.isEmpty();
	}
	
	/** Método utilizado em {@link ExcelSheetWriter#write(ArrayList, java.io.File)} para montagem da planilha de erros.
	 *  @return Dados para preenchimento de uma linha de erros da planilha, já organizados em um array. */
	public String[] getErrorSummaryArray() {
		return new String[] { Integer.toString(linha),
							  candidato.getNome(),
							  candidato.getCpf(),
							  candidato.getDataNascimento().format(Constants.DateFormatters.BRAZILIAN_DATE),
							  "Indeferido", getErrorSummaryString()
							};
	}
	
	/** Monta uma String com todos os motivos de violação de requisitos de um candidato.
	 *  @return Uma String com todos os motivos de violação de requisitos de um candidato. */
	private String getErrorSummaryString() {
		
		StringBuilder builder = new StringBuilder();
		
		// Montando uma String com cada mensagem de violação de requisitos
		for (FieldParseException exception: this.listaExcecoes)
			builder.append(exception.getMessage());
		
		return builder.toString().trim();
	}
	
	/** Comparador de nome de candidato. Útil para métodos de ordenação.
	 *  @param exception - objeto de erros de processamento de dados de candidato a ser comparado com esta instância
	 *  @since 3.0, 21/04/2021 */
	public int compareTo(final RowParseException exception) {
		return candidato.getNome().compareTo(exception.candidato.getNome());
	}
	
}