package compec.ufam.isensys.model;

import java.time.LocalDate;
import java.util.Objects;

import compec.ufam.isensys.constants.Constants;

public class Candidato {

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
							 dataNascimento == null ? null : dataNascimento.format(Constants.DateFormatters.SISTAC_DATE));
	}
	
}
