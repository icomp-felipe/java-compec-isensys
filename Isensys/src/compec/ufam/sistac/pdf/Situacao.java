package compec.ufam.sistac.pdf;

/** Classe que representa uma situação de processamento. Utilizada para a construção do relatório
 *  @author Felipe André
 *  @version 2.50, 08/07/2018 */
public class Situacao {

	private final String id;
	private final String motivo, descricao;

	/** Construtor da classe apenas inicializando os parâmetros */
	public Situacao(String[] args) {
		
		this.id        = args[0];
		this.motivo    = args[1];
		this.descricao = args[2];
		
	}
	
	/** Getter do motivo de processamento (ver manual do Sistac) */
	public String getMotivo() {
		return motivo;
	}

	/** Getter da descrição de motivo (ver manual do Sistac) */
	public String getDescricao() {
		return descricao;
	}
	
	/** Getter do ID de motivo (ver manual do Sistac) */
	public String getID() {
		return id;
	}

}
