package compec.ufam.sistac.pdf;

/** Classe que representa uma situação de processamento. Utilizada para a construção do relatório.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 18/04/2021 */
public class Situacao {

	private final String id;
	private final String motivo, descricao;

	/** Construtor da classe apenas inicializando os parâmetros.
	 *  @param args - representa uma linha de dados extraídos do arquivo .csv */
	public Situacao(final String[] args) {
		
		this.id        = args[0];
		this.motivo    = args[1];
		this.descricao = args[2];
		
	}
	
	/** Getter do ID de motivo (ver manual do Sistac).
	 *  @return Código (int) do motivo de processamento. */
	public String getID() {
		return this.id;
	}
	
	/** Getter do motivo de processamento (ver manual do Sistac).
	 *  @return String contendo o motivo de processamento. */
	public String getMotivo() {
		return this.motivo;
	}

	/** Getter da descrição de motivo (ver manual do Sistac).
	 *  @return String contendo a descricao do motivo de processamento. */
	public String getDescricao() {
		return this.descricao;
	}
	
}
