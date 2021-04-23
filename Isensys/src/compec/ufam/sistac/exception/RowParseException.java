package compec.ufam.sistac.exception;

import java.util.*;
import com.phill.libs.*;

public class RowParseException extends Exception {

	private int linha;
	private String nis,cpf,nome,resposta;
	private ArrayList<FieldException> listaExcecoes;
	
	private static final long serialVersionUID = 1L;
	
	public RowParseException(int linha, String nome, String nis, String cpf) {
		this.linha = linha;
		this.nis = nis;
		this.cpf = setCPF(cpf);
		this.nome = nome;
		this.resposta = "Indeferido";
		this.listaExcecoes = new ArrayList<FieldException>();
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void setNIS(String nis) {
		this.nis = nis;
	}
	
	private String setCPF(String cpf) {
		
		cpf = StringUtils.extractNumbers(cpf);
		
		return cpf.isEmpty() ? cpf : String.format("%011d",Long.parseLong(cpf));
	}
	
	public void addException(FieldException exception) {
		listaExcecoes.add(exception);
	}
	
	public boolean hasException() {
		return (listaExcecoes.size() != 0);
	}
	
	@Override
	public String getMessage() {
		
		try { nis = String.format("%ld",Long.parseLong(nis)); }
		catch (Exception exception) { }
		
		return String.format("Linha: %d;%s;%s;%s;%s;%s",linha,nis,nome,cpf,resposta,listMotivos());
	}
	
	private String listMotivos() {
		StringBuilder builder = new StringBuilder();
		
		for (FieldException exception: listaExcecoes)
			builder.append(exception.getMessage());
		
		return builder.toString();
	}
	
	public String[] getErrorResume() {
		return new String[]{nis,nome,cpf,"Indeferido",listMotivos()};
	}
	
	/** Comparador de nome de candidato. Útil para métodos de ordenação.
	 *  @param exception - objeto de erros de processamento de dados de candidato a ser comparado com esta instância
	 *  @since 3.0, 21/04/2021 */
	public int compareTo(final RowParseException exception) {
		return this.nome.compareTo(exception.nome);
	}
	
}
