package compec.ufam.sistac.model.retorno;

import java.io.Serializable;
import com.phill.libs.StringUtils;

/** Classe que representa um retorno do Sistac. Encapsula também
 *  alguns tratamentos de dados pertinentes a esta classe.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 20/04/2021 */
public class Retorno implements Serializable {

	private static final transient long serialVersionUID = 3;
	
	private String nome, nis, cpf;
	private char situacao;
	private int motivo;

	/*************************** Bloco de Setters ******************************/
	
	public void setNome(final String nome) {
		this.nome = StringUtils.trim(nome);
	}
	
	/** Setter para o Número de Identicação Social (NIS) do candidato.
	 *  Caso este seja vazio, é preenchido com um caractere '-'. */
	public void setNIS(final String nis) {
		
		final String aux = StringUtils.extractNumbers(nis);
		
		this.nis = aux.isEmpty() ? "-" : aux;
	}
	
	public void setCPF(final String cpf) {
		this.cpf = StringUtils.trim(cpf);
	}
	
	public void setSituacao(final String situacao) {
		this.situacao = situacao.charAt(0);
	}
	
	public void setMotivo(final String motivo) {
		this.motivo = motivo.isEmpty() ? 0 : Integer.parseInt(motivo);
	}
	
	/** Defere o pedido de isenção de um candidato */
	public void defere() {
		
		this.situacao = 'S';
		this.motivo   =  0 ;
		
		System.out.println("Deferido(a) candidato(a): " + nome);
		
	}
	
	/*************************** Bloco de Getters ******************************/
	
	/** Getter para o CPF do candidato */
	public String getCPF() {
		return cpf;
	}
	
	/** Verifica se o candidato teve seu pedido de isenção deferido */
	public boolean deferido() {
		return (this.situacao == 'S');
	}
	
	/******************** Bloco de Getters (Jasper) ****************************/
	
	/** Getter para o nome do candidato */
	public String getNome() {
		return this.nome;
	}
	
	/** Getter para o Número de Identicação Social (NIS) do candidato */
	public String getNis() {
		return this.nis;
	}
	
	/** Getter para a situação do candidato */
	public char getSituacao() {
		return this.situacao;
	}
	
	public int getMotivo() {
		return this.motivo;
	}
	
}