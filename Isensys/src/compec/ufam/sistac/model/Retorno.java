package compec.ufam.sistac.model;

import java.io.Serializable;
import com.phill.libs.StringUtils;

/** Classe que representa um retorno do Sistac. Encapsula também
 *  alguns tratamentos de dados pertinentes a esta classe
 *  @author Felipe André
 *  @version 2.50, 08/07/2018 */
public class Retorno implements Serializable {

	private static final transient long serialVersionUID = 1L;
	private String nis,nome,cpf;
	private String situacao,codigo;
	
	/****************** Bloco de Construtores **********************************/
	
	/** Construtor utilizado em 'SistacFile.class' */
	public Retorno(String nis, String nome, String cpf, String codigo) {
		this(new String[]{nis,nome,cpf,codigo});
	}
	
	/** Construtor principal da classe, inicializa os atributos já com
	 *  os devidos tratamento de dados */
	public Retorno(String[] args) {
		this.nis      = parseNIS(args[0].trim());
		this.nome     = args[1].trim();
		this.cpf      = args[2].trim();
		this.codigo   = args[3].trim();
		this.situacao = parseSituacao(codigo);
	}
	
	/****************** Bloco de Parsers ***************************************/
	
	/** Trata o Número de Identicação Social (NIS) do candidato.
	 *  Caso este seja vazio, o preencho com um caractere '-' */
	private String parseNIS(String nis) {
		
		nis = StringUtils.extractNumbers(nis);
		
		return ((nis.length() == 0)) ? "-" : nis;
	}
	
	/** Trata a situação de defetimento do candidato em formato de texto */
	private String parseSituacao(String codigo) {
		return deferido() ? "Deferido" : "Indeferido";
	}
	
	/******************** Bloco de Getters *************************************/
	
	/** Recupera os dados para exibição no PDF */
	public String[] getResume() {
		return new String[] {nis,nome,situacao,codigo};
	}
	
	/** Getter para o nome do candidato */
	public String getNome() {
		return nome;
	}
	
	/** Getter para o Número de Identicação Social (NIS) do candidato */
	public String getNis() {
		return nis;
	}
	
	/** Getter para o CPF do candidato */
	public String getCPF() {
		return cpf;
	}
	
	/** Getter para a situação do candidato */
	public String getSituacao() {
		return situacao;
	}
	
	/** Getter para o código de indeferimento do candidato.
	 *  Caso este esteja deferido, não há código de retorno, sendo assim, retorno uma String vazia */
	public String getCodigo() {
		return deferido() ? "" : codigo;
	}
	
	/** Verifica se o candidato teve seu pedido de isenção deferido */
	public boolean deferido() {
		return codigo.equals("0") || codigo.equals("S");
	}
	
	/********************* Bloco de Setters *************************************/
	
	/** Setter para o Número de Identicação Social (NIS) do candidato */
	public void setNIS(String nis) {
		this.nis = parseNIS(nis);
	}
	
	/** Defere o pedido de isenção de um candidato */
	public void defere() {
		
		this.codigo   = "S";
		this.situacao = "Deferido";
		
		System.out.println("Deferido candidato: " + nome);
		
	}
	
}
