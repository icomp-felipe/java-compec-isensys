package compec.ufam.isensys.model.retorno;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.phill.libs.StringUtils;
import com.phill.libs.br.CPFParser;

import compec.ufam.isensys.constants.Constants;
import compec.ufam.isensys.model.Candidato;

/** Entidade principal da parte de processamento de arquivos de retornos do sistema.
 *  Encapsula apenas neste objeto tanto os candidatos válidos (vindos do arquivo do Sistac),
 *  quanto os erros de processamento (vindos da planilha de erros do Excel).
 *  Implementa também alguns tratamentos de dados pertinentes a esta classe.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 03/AGO/2025 */
public class Retorno implements Serializable {

	// Serial de versionamento da classe
	private static final long serialVersionUID = 4;
	
	// Atributos serializáveis
	private Character situacao;
	private Candidato candidato;
	protected int motivo;
	
	// Utilizado apenas pro cálculo de similaridade. Logo, não faz parte da serialização!
	private transient String nomeAnterior;

	public Retorno(final String[] data) {
		
		this.candidato = new Candidato(data[1], data[2], LocalDate.parse(data[3], DateTimeFormatter.ofPattern("ddMMuuuu")));
		this.situacao = data[4].charAt(0);
		this.motivo = data[5].isEmpty() ? 0 : Integer.parseInt(data[5]);
		
	}
	
	/*************************** Bloco de Setters ******************************/
	
	public void setCandidato(final Candidato candidato) {
		this.candidato = candidato;
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
	
	/** @return Candidato solicitante. */
	public Candidato getCandidato() {
		return this.candidato;
	}
	
	/** @return Nome antes da atualização pelo método {@link #setNome(String)}. */
	public String getNomeAnterior() {
		return StringUtils.BR.normaliza(this.nomeAnterior);
	}
	
	/** Comparador de objetos de retorno. Útil para métodos de ordenação. Usa o nome do candidato como base nos cálculos.
	 *  @param retorno - retorno a ser comparado com esta instância */
	public int compareTo(final Retorno retorno) {
		return candidato.compareTo(retorno.getCandidato());
	}
	
	/******************** Bloco de Getters (Jasper) ****************************/
	
	/** @return Nome do candidato (normalizado). */
	public String getNome() {
		return (candidato != null) ? StringUtils.BR.normaliza(candidato.getNome()) : null;
	}
	
	/** @return Número de CPF do candidato (LGPD). */
	public String getCPFOculto() {
		return (candidato != null) ? CPFParser.oculta(candidato.getCpf()) : null;
	}
	
	public String getNascimentoOculto() {
		return (candidato != null && candidato.getDataNascimento() != null) ? candidato.getDataNascimento().format(Constants.DateFormatters.BRAZILIAN_LGPD) : null;
	}
	
	/** @return Situação de deferimento do pedido de isenção do candidato. */
	public char getSituacao() {
		return this.situacao;
	}
	
	/** @return -1 para erros de processamento<br>0 para deferido<br>>0 para indeferimentos de acordo com manual do Sistac. */
	public int getMotivo() {
		return this.motivo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(candidato, motivo, situacao);
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Retorno other = (Retorno) obj;
		
		return Objects.equals(candidato, other.candidato) &&
			   motivo == other.motivo &&
			   Objects.equals(situacao, other.situacao);
	}

	@Override
	public String toString() {
		return String.format("Retorno [candidato={%s}, situacao=%c, motivo=%d", candidato.toString(), situacao, motivo);
	}
	
}
