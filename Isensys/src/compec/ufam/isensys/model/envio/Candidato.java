package compec.ufam.isensys.model.envio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import compec.ufam.isensys.constants.Constants;

/** Classe que representa um candidato processado com sucesso pelo sistema. Se houve algum erro
 *  durante o processo de leitura e parse dos arquivos, o objeto criado será o 'RowParseException'
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 02/AGO/2025
 *  @see CandidatoBuilder */
public class Candidato {

	private final String nome, nis, rg, orgaoEmissorRG, cpf, nomeMae;
	private final LocalDate dataNascimento, dataEmissaoRG;
	private final char sexo;
	private final DateTimeFormatter formatter;
	
	/** Construtor da classe inicializando os atributos. */
	protected Candidato(final String nome, final String nis, final LocalDate dataNascimento, final char sexo, final String rg,
			            final LocalDate dataEmissaoRG, final String orgaoEmissorRG, final String cpf, final String nomeMae) {
		
		this.nome = nome;
		this.nis  = nis;
		this.sexo = sexo;
		this.rg   = rg;
		this.cpf  = cpf;
		this.nomeMae = nomeMae;
		this.dataNascimento = dataNascimento;
		this.dataEmissaoRG  = dataEmissaoRG;
		this.orgaoEmissorRG = orgaoEmissorRG;
		
		this.formatter = DateTimeFormatter.ofPattern("ddMMuuuu");
		
	}
	
	/** @return Nome do candidato. */
	public String getNome() {
		return this.nome;
	}

	/** Getter para a data de nascimento do candidato.
	 *  @return Uma string com a data de nascimento do candidato no formato Sistac (ddMMyyyy). */
	public String getDataNascimento() {
		return this.dataNascimento.format(formatter);
	}
	
	/** Getter para a data de emissão do RG do candidato.
	 *  @return Uma string com a data de emissão do RG do candidato no formato Sistac (ddMMyyyy). */
	public String getDataEmissaoRG() {
		return this.dataEmissaoRG.format(formatter);
	}
	
	/** Retorna uma String com os dados organizados e preparados para escrita no arquivo texto de acordo com o formato do Sistac.
	 *  @return Uma string com os dados desta classe no formato Sistac. */
	public String getDadosSistac() {
		return String.format(Constants.StringFormat.ROW_DATA_FORMAT,
				             this.nome, this.cpf, getDataNascimento());
	}
	
	/** Compara todos os atributos de um <code>object</code>, caso ele seja da classe {@link Candidato}, senão o método pai é chamado passando o objeto.
	 *  @param object - candidato a ser comparado com esta instância da classe
	 *  @return 'true' apenas se todos os atributos são iguais;<br>'false' caso pelo menos um atributo seja diferente.
	 *  @since 3.5, 23/04/2021 */
	@Override
	public boolean equals(final Object object) {
		
		// Se for um 'Candidato', todos os seus atributos são verificados
		if (object instanceof Candidato) {
			
			Candidato candidato = (Candidato) object;
			
			return (this.nome.equals(candidato.nome) && this.nis    .equals(candidato.nis    ) && this.rg.equals(candidato.rg) && this.orgaoEmissorRG.equals(candidato.orgaoEmissorRG) &&
					this.cpf .equals(candidato.cpf ) && this.nomeMae.equals(candidato.nomeMae) && this.dataNascimento.equals(candidato.dataNascimento) &&
					this.dataEmissaoRG.equals(candidato.dataEmissaoRG) && this.sexo == candidato.sexo);
			
		}
		
		return false;
	}
	
	/** Comparador de nome de candidato. Útil para métodos de ordenação.
	 *  @param candidato - candidato a ser comparado com esta instância
	 *  @since 3.0, 21/04/2021 */
	public int compareTo(final Candidato candidato) {
		return this.nome.compareTo(candidato.nome);
	}
	
}