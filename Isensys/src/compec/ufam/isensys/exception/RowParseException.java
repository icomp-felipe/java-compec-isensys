package compec.ufam.isensys.exception;

import java.util.*;

import com.phill.libs.*;

import compec.ufam.isensys.io.*;
import compec.ufam.isensys.model.envio.*;

/** Classe especial de exceção, utilizada pelo método {@link CandidatoBuilder#parse(int, String[])}.
 *  Representa as violações de requisitos que os dados de um candidato pode gerar. 
 *  Contém internamente dados que identificam o candidato, bem como uma lista de {@link FieldParseException}
 *  identificando cada uma das violações de requisitos.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.5, 23/04/2021 */
public class RowParseException extends Exception {

	// Serial de versionamento
	private static final long serialVersionUID = 3;
	
	// Atributos de processamento
	private final int linha;
	private String nis, cpf, nome;
	private final ArrayList<FieldParseException> listaExcecoes;
	
	/** Construtor padrão, setando atributos e inicializando a lista interna de {@link FieldParseException}.
	 *  @param linha - linha da planilha onde a violação ocorreu
	 *  @param nome - nome do candidato
	 *  @param nis - número de identificação social (NIS) do candidato
	 *  @param cpf - número de CPF do candidato */
	public RowParseException(int linha, String nome, String nis, String cpf) {
		
		this.linha = linha;
		this.nis   = parseNIS(nis);
		this.cpf   = parseCPF(cpf);
		this.nome  = nome;
		this.listaExcecoes = new ArrayList<FieldParseException>();
		
	}
	
	/**************************** Bloco de Setters *****************************/
	
	/** Setter do nome.
	 *  @param nome - nome do candidato */
	public void setNome(final String nome) {
		this.nome = nome;
	}
	
	/** Setter do número de identificação social (NIS) do candidato.
	 *  @param nis - número de identificação social (NIS) do candidato. */
	public void setNIS(final String nis) {
		this.nis = nis;
	}
	
	/** Tenta formatar o número de identificação social (NIS) do candidato.
	 *  @param nis - número de identificação social (NIS) do candidato */
	public String parseNIS(final String nis) {
		
		String novoNIS = StringUtils.extractNumbers(nis);
		
		if (novoNIS.length() > 11)
			novoNIS = novoNIS.substring(0,11);
		
		return novoNIS.isEmpty() ? nis : String.format("%011d", Long.parseLong(novoNIS));
	}
	
	/** Tenta formatar o número de CPF do candidato.
	 *  @return Uma String contendo o número de CPF do candidato. */
	private String parseCPF(final String cpf) {
		
		String novoCPF = StringUtils.extractNumbers(cpf);
		
		return novoCPF.isEmpty() ? cpf : String.format("%011d", Long.parseLong(novoCPF));
	}
	
	/************************ Bloco de Funcionalidades *************************/
	
	/** Adiciona uma nova exceção de requisito à lista interna desta instância de classe.
	 *  @param exception - exceção representando a violação de requisitos de um determinado dado de candidato */
	public void addException(final FieldParseException exception) {
		this.listaExcecoes.add(exception);
	}
	
	/** Compara todos os atributos de um <code>object</code>, caso ele seja da classe {@link RowParseException}, senão o método pai é chamado passando o objeto.<br>
	 *  Nota: apenas o número da linha do arquivo não é considerada na verificação!
	 *  @param object - exceção de linha a ser comparada com esta instância da classe
	 *  @return 'true' apenas se todos os atributos são iguais;<br>'false' caso pelo menos um atributo seja diferente.
	 *  @since 3.5, 23/04/2021 */
	@Override
	public boolean equals(final Object object) {
		
		// Se for um 'RowParseException', todos os seus atributos são verificados, menos a linha atual
		if (object instanceof RowParseException) {
			
			RowParseException exception = (RowParseException) object;
			
			return (this.nis.equals(exception.nis) && this.cpf.equals(exception.cpf) && this.nome.equals(exception.nome) && this.listaExcecoes.equals(exception.listaExcecoes));
			
		}
		
		return super.equals(object);
	}
	
	/** Verifica se há algum {@link FieldParseException} na lista interna desta classe.
	 *  @return 'true' apenas se a lista interna de exceções tiver pelo menos um elemento;<br>'false', caso contrário. */
	public boolean hasException() {
		return !this.listaExcecoes.isEmpty();
	}
	
	/** Monta uma mensagem personalizada com os detalhes da exceção.
	 *  @return Uma String contendo uma detalhes da exceção. */
	@Override
	public String getMessage() {
		return String.format("Linha: %d - NIS: '%s' - Nome: '%s' - CPF: '%s' - Situação: 'Indeferido' - Motivo(s) - '%s'", this.linha, this.nis, this.nome, this.cpf, listMotivos());
	}
	
	/** Método utilizado em {@link ExcelSheetWriter#write(ArrayList, java.io.File)} para montagem da planilha de erros.
	 *  @return Dados para preenchimento de uma linha de erros da planilha, já organizados em um array. */
	public String[] getErrorResume() {
		return new String[] { Integer.toString(this.linha), this.nis, this.nome, this.cpf, "Indeferido", listMotivos() };
	}
	
	/** Monta uma String com todos os motivos de violação de requisitos de um candidato.
	 *  @return Uma String com todos os motivos de violação de requisitos de um candidato. */
	private String listMotivos() {
		
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
		return this.nome.compareTo(exception.nome);
	}
	
}