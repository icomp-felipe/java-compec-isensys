package compec.ufam.isensys.model.retorno;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.phill.libs.StringUtils;
import com.phill.libs.br.CPFParser;

/** Entidade principal da parte de processamento de arquivos de retornos do sistema.
 *  Encapsula apenas neste objeto tanto os candidatos válidos (vindos do arquivo do Sistac),
 *  quanto os erros de processamento (vindos da planilha de erros do Excel).
 *  Implementa também alguns tratamentos de dados pertinentes a esta classe.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 02/AGO/2025 */
public class Retorno implements Serializable {

	// Serial de versionamento da classe
	private static final transient long serialVersionUID = 4;
	
	// Atributos serializáveis
	private Character situacao;
	protected String nome, cpf;
	protected LocalDate dataNascimento;
	protected int motivo;
	
	// Utilizado apenas pro cálculo de similaridade. Logo, não faz parte da serialização!
	private transient String nomeAnterior;

	public Retorno(final String row) {
		
		String[] splitted = row.split(";", -1);
		
		this.nome = splitted[1];
		this.cpf = splitted[2];
		this.dataNascimento = LocalDate.parse(splitted[3], DateTimeFormatter.ofPattern("ddMMuuuu"));
		this.situacao = splitted[4].charAt(0);
		this.motivo = splitted[5].isEmpty() ? 0 : Integer.parseInt(splitted[5]);
		
	}
	
	/*************************** Bloco de Setters ******************************/
	
	/** @param nis - número do CPF do candidato */
	public void setCPF(final String cpf) {
		this.cpf = cpf;
	}
	
	/** @param nome - nome do candidato */
	public void setNome(final String nome) {
		this.nomeAnterior = this.nome;
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
		
	}
	
	/*************************** Bloco de Getters ******************************/
	
	/** Verifica se o candidato teve seu pedido de isenção deferido.
	 *  @return 'true' se o pedido foi deferido (situacao == 'S') ou 'false' caso contrário. */
	public boolean deferido() {
		return (this.situacao == 'S');
	}
	
	/** @return Nome antes da atualização pelo método {@link #setNome(String)}. */
	public String getNomeAnterior() {
		return StringUtils.BR.normaliza(this.nomeAnterior);
	}
	
	public LocalDate getDataNascimento() {
		return this.dataNascimento;
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
	
	 /** @return Número de CPF do candidato (com máscara). */
	public String getCPF() {
		return CPFParser.format(this.cpf);
	}
	
	/** @return Número de CPF do candidato (LGPD). */
	public String getCPFOculto() {
		return CPFParser.oculta(this.cpf);
	}
	
	/** @return Nome do candidato (normalizado). */
	public String getNome() {
		return StringUtils.BR.normaliza(this.nome);
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

	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}
	
	@Override
	public String toString() {
		return String.format("[%s,%s,%s,%s,%s]", this.nome,
												this.cpf,
												this.dataNascimento.format(DateTimeFormatter.ofPattern("dd/MM/uuuu")),
												this.deferido() ? "deferido" : "indeferido",
												this.motivo);
	}
	
}