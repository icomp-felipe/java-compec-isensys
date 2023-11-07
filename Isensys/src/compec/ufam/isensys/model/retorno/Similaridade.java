package compec.ufam.isensys.model.retorno;

import org.apache.commons.text.similarity.*;

/** Implementa a classe de cálculo de similaridade entre nomes de solicitantes de recurso de isenção.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 22/JUN/2023 */
public class Similaridade {

	private String nomeSolicitacao, nomeRecurso;
	private double distancia, similaridade;

	/** Inicializa os atributos já calculando a distância e a similaridade.
	 *  @param nomeSolicitacao - nome informado na solicitação de isenção
	 *  @param nomeRecurso - nome informado no recurso de isenção
	 *  @param algoDistancia - {@link JaroWinklerDistance}
	 *  @param algoSimilaridade - {@link JaroWinklerSimilarity} */
	public Similaridade(final String nomeSolicitacao, final String nomeRecurso, final double distancia, final double similaridade) {

		this.nomeSolicitacao = nomeSolicitacao;
		this.nomeRecurso    = nomeRecurso;
		
		this.distancia    = distancia;
		this.similaridade = similaridade;
		
	}

	/*************************** Bloco de Getters ******************************/
	
	/** @return Nome informado na solicitação de isenção. */
	public String getNomeSolicitacao() {
		return this.nomeSolicitacao;
	}
	
	/** @return Nome informado no recurso de isenção. */
	public String getNomeRecurso() {
		return this.nomeRecurso;
	}
	
	/** @return Porcentagem de similaridade entre os nomes. */
	public double getSimilaridade() {
		return this.similaridade;
	}
	
	/** @return Porcentagem de distância entre os nomes. */
	public double getDistancia() {
		return this.distancia;
	}
	
	@Override
	public String toString() {
		return String.format("Nome anterior [%s]; Nome atual [%s]; Distância: %.2f; Similaridade: %.2f", nomeSolicitacao, nomeRecurso, distancia, similaridade);
	}
	
}