package compec.ufam.isensys.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import compec.ufam.isensys.constants.Constants;

public class Candidato implements Serializable {

	// Versão da classe
	private static final long serialVersionUID = 4L;
	
	private String nome, cpf;
	private LocalDate dataNascimento;
	
	public Candidato(String nome, String cpf, LocalDate dataNascimento) {
		this.nome = nome;
		this.cpf = cpf;
		this.dataNascimento = dataNascimento;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getCpf() {
		return cpf;
	}
	
	public LocalDate getDataNascimento() {
		return dataNascimento;
	}
	
	public String getDataNascimento(final DateTimeFormatter formatter) {
		return dataNascimento == null ? null : dataNascimento.format(formatter);
	}

	/** Comparador de nome de candidato. Útil para métodos de ordenação.
	 *  @param candidato - candidato a ser comparado com esta instância */
	public int compareTo(final Candidato candidato) {
		return nome.compareTo(candidato.nome);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(cpf, dataNascimento, nome);
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Candidato other = (Candidato) obj;
		
		return Objects.equals(cpf, other.cpf) &&
			   Objects.equals(dataNascimento, other.dataNascimento) &&
			   Objects.equals(nome, other.nome);
		
	}

	public String getDadosSistac() {
		return String.format(Constants.StringFormat.ROW_DATA_FORMAT,
							 nome,
							 cpf,
							 getDataNascimento(Constants.DateFormatters.SISTAC_DATE));
	}

	@Override
	public String toString() {
		return String.format("Candidato [nome=%s, cpf=%s, dataNascimento=%s]", nome, cpf, getDataNascimento(Constants.DateFormatters.SISTAC_DATE));
	}

	/** @return 'true' se, e somente se, o <code>candidato</code> informado via parâmetro tiver o mesmo número de CPF declarado nesta instância, ou<br>
	 *          'false' quando o <code>candidato</code> informado é nulo ou seus CPF's são nulos.  */
	public boolean temMesmoCPF(final Candidato candidato) {
		return (candidato != null) && (cpf != null) && (candidato.cpf != null) && cpf.equals(candidato.cpf);
	}
	
}
