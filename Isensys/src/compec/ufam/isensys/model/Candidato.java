package compec.ufam.isensys.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import compec.ufam.isensys.constants.Constants;
import compec.ufam.isensys.constants.Reviewed;

@Reviewed("2025-08-11")
/** Representa um candidato do sistema.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 11/AGO/2025 */
public class Candidato implements Serializable {

	// Versão da classe
	private static final long serialVersionUID = 4L;
	
	private String nome, cpf;
	private LocalDate dataNascimento;
	
	/** Construtor padrão, apenas setando atributos, sem realizar qualquer validação nos dados.
	 *  @param nome - nome do candidato
	 *  @param cpf - número de CPF do candidato
	 *  @param dataNascimento - data de nascimento do candidato */
	public Candidato(final String nome, final String cpf, final LocalDate dataNascimento) {
		this.nome = nome;
		this.cpf = cpf;
		this.dataNascimento = dataNascimento;
	}

	/** @param nome - nome do candidato */
	public void setNome(final String nome) {
		this.nome = nome;
	}
	
	/** @param cpf - número de CPF do candidato */
	public void setCpf(final String cpf) {
		this.cpf = cpf;
	}
	
	/** @param dataNascimento - data de nascimento do candidato */
	public void setDataNascimento(final LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	/** @return String contendo o nome do candidato. */
	public String getNome() {
		return this.nome;
	}
	
	/** @return String contendo o número de CPF do candidato. */
	public String getCpf() {
		return this.cpf;
	}
	
	/** @return Objeto {@link LocalDate} contendo a data de nascimento do candidato. */
	public LocalDate getDataNascimento() {
		return this.dataNascimento;
	}
	
	/** @param formatter - formatador de data. Se 'null', a String retornada também será 'null'.
	 *  @return String contendo a data de nascimento do candidato, formatada de acordo com <code>formatter</code>. */
	public String getDataNascimento(final DateTimeFormatter formatter) {
		return this.dataNascimento == null ? null : this.dataNascimento.format(formatter);
	}

	/** Comparador de nome de candidato. Útil para métodos de ordenação.
	 *  @param candidato - candidato a ser comparado com esta instância */
	public int compareTo(final Candidato candidato) {
		return this.nome.compareTo(candidato.nome);
	}
	
	/** Calcula uma hash com base nos atributos deste objeto.
	 *  @return Um número inteiro que representa este objeto. */
	@Override
	public int hashCode() {
		return Objects.hash(this.nome, this.cpf, this.dataNascimento);
	}

	/** Verifica se o <code>objeto</code> informado via parâmetro é igual ao desta instância.
	 *  @param objeto - objeto a ser comparado
	 *  @return 'true' se:<br>1. o objeto possui o mesmo endereço de memória desta instância;<br>
	 *  					  2. todos os atributos dos dois objetos envolvidos são exatamente iguais.<br>
	 *        e 'false' caso o objeto informado seja nulo, ou de outro tipo, ou possuam atributos diferentes. */
	@Override
	public boolean equals(Object objeto) {
		
		if (this == objeto)
			return true;
		
		if (objeto == null)
			return false;
		
		if (getClass() != objeto.getClass())
			return false;
		
		Candidato other = (Candidato) objeto;
		
		return Objects.equals(cpf, other.cpf) &&
			   Objects.equals(dataNascimento, other.dataNascimento) &&
			   Objects.equals(nome, other.nome);
		
	}

	/** @return String contendo uma linha formatada com os dados para confecção do arquivo de envio do Sistac. */
	public String getDadosSistac() {
		return String.format(Constants.StringFormat.ROW_DATA_FORMAT,
							 this.nome,
							 this.cpf,
							 getDataNascimento(Constants.DateFormatters.SISTAC_DATE));
	}

	/** Retorna uma representação deste objeto em forma de String.
	 *  @return Uma String formatada contendo todos os dados deste objeto. */
	@Override
	public String toString() {
		return String.format("Candidato [nome=%s, cpf=%s, dataNascimento=%s]", this.nome, this.cpf, getDataNascimento(Constants.DateFormatters.SISTAC_DATE));
	}

	/** Verifica se o <code>candidato</code> informado via parâmetro possui o mesmo número de CPF que o contido neste objeto.
	 *  @param candidato - candidato a ser verificado
	 *  @return 'true' se, e somente se, o <code>candidato</code> informado via parâmetro tiver o mesmo número de CPF declarado nesta instância, ou<br>
	 *          'false' quando o <code>candidato</code> informado é nulo ou seus CPF's são nulos.  */
	public boolean temMesmoCPF(final Candidato candidato) {
		return (candidato != null) && (this.cpf != null) && (candidato.cpf != null) && this.cpf.equals(candidato.cpf);
	}
	
}
