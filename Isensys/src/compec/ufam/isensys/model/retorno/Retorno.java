package compec.ufam.isensys.model.retorno;

import java.io.Serializable;

import com.phill.libs.*;
import com.phill.libs.br.*;

/** Entidade principal da parte de processamento de arquivos de retornos do sistema.
 *  Encapsula apenas neste objeto tanto os candidatos válidos (vindos do arquivo do Sistac),
 *  quanto os erros de processamento (vindos da planilha de erros do Excel).
 *  Implementa também alguns tratamentos de dados pertinentes a esta classe.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 21/JUN/2023 */
public class Retorno implements Serializable {

	// Serial de versionamento da classe
	private static final transient long serialVersionUID = 3;
	
	// Atributos serializáveis
	private char situacao;
	protected String nome, nis, cpf;
	protected int motivo;

	/** Construtor principal e obrigatório desta classe.
	 *  @param nome - nome do candidato
	 *  @param nis - Número de Identicação Social (NIS) do candidato
	 *  @param cof - número de CPF do candidato
	 *  @param situacao - situação de deferimento do pedido de isenção ("N" para indeferido ou "S" para deferido)
	 *  @param motivo - código de motivo da situação:<br>"-1" para erros de processamento<br>"" para deferido<br>">0" para indeferimentos de acordo com manual do Sistac. */
	public Retorno(final String nome, final String nis, final String cpf, final String situacao, final String motivo) {
		
		this.nome = nome;
		this.nis  = nis ;
		this.cpf  = cpf ;
		
		this.situacao = situacao.charAt(0);
		this.motivo   = motivo.isEmpty() ? 0 : Integer.parseInt(motivo);
		
	}
	
	/*************************** Bloco de Setters ******************************/
	
	/** Setter para o número do NIS.
	 *  @param nis - número do NIS do candidato */
	public void setNIS(final String nis) {
		this.nis = nis;
	}
	
	/** Setter pro nome.
	 *  @param nome - nome do candidato */
	public void setNome(final String nome) {
		this.nome = nome;
	}
	
	/** Setter do motivo de indeferimento, utilizado apenas no resultado definitivo, após os recursos.
	 *  @param motivo - motivo de indeferimento */
	public void setMotivo(final int motivo) {
		this.motivo = motivo;
	}
	
	/** Defere o pedido de isenção de um candidato. */
	public void defere() {
		
		this.situacao = 'S';
		this.motivo   =  0 ;
		
		System.out.println("Deferido(a) candidato(a): " + nome);
		
	}
	
	/*************************** Bloco de Getters ******************************/
	
	/** Verifica se o candidato teve seu pedido de isenção deferido.
	 *  @return 'true' se o pedido foi deferido (situacao == 'S') ou 'false' caso contrário. */
	public boolean deferido() {
		return (this.situacao == 'S');
	}
	
	/** Comparador de objetos de retorno. Útil para métodos de ordenação. Usa o nome do candidato como base nos cálculos.
	 *  @param retorno - retorno a ser comparado com esta instância
	 *  @since 3.5, 23/04/2021 */
	public int compareTo(final Retorno retorno) {
		return this.nome.compareTo(retorno.nome);
	}
	
	/** Verifica se dois retornos são iguais (seus CPF's são os mesmos).
	 *  @param retorno - retorno a ser comparado
	 *  @return 'true' caso os CPF's sejam iguais, 'false' caso contrário.
	 *  @since 3.5, 23/04/2021 */
	public boolean equals(final Retorno retorno) {
		return this.cpf.equals(retorno.cpf);
	}
	
	/******************** Bloco de Getters (Jasper) ****************************/
	
	/** Getter para o CPF do candidato.
	 *  @return Número de CPF do candidato. */
	public String getCPF() {
		return CPFParser.oculta(this.cpf);
	}
	
	/** Getter para o nome do candidato.
	 *  @return Nome do candidato. */
	public String getNome() {
		return StringUtils.BR.normaliza(this.nome);
	}
	
	/** Getter para o Número de Identicação Social (NIS) do candidato.
	 *  @return Número do NIS do candidato. */
	public String getNIS() {
		return this.nis;
	}
	
	/** Getter para o Número de Identicação Social (NIS) do candidato, descaracterizado.
	 *  @return Número do NIS do candidato com a máscara de descaracterização aplicada. */
	public String getNISOculto() {
		return PISParser.oculta(this.nis);
	}
	
	/** Getter para a situação de odeferimento do pedido de isenção do candidato.
	 *  @return Situação de deferimento do pedido de isenção do candidato. */
	public char getSituacao() {
		return this.situacao;
	}
	
	/** Getter para o motivo de indeferimento do candidato.
	 *  @return -1 para erros de processamento<br>0 para deferido<br>>0 para indeferimentos de acordo com manual do Sistac. */
	public int getMotivo() {
		return this.motivo;
	}

}