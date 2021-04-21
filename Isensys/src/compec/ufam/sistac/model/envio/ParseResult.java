package compec.ufam.sistac.model.envio;

import java.util.*;
import compec.ufam.sistac.exception.*;

/** Objeto que concentra listas de {@link Candidato} (objetos lidos com sucesso dos arquivos)
 *  e {@link RowParseException} (objetos com alguma falha de processamento).
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 21/04/2021
 *  @see Candidato
 *  @see RowParseException */
public class ParseResult {

	private final ArrayList<Candidato> listaCandidatos;
	private final ArrayList<RowParseException> listaExcecoes;
	
	/** Construtor apenas inicializando as listas internas. */
	public ParseResult() {
		this.listaCandidatos = new ArrayList<Candidato>();
		this.listaExcecoes   = new ArrayList<RowParseException>();
	}

	/** Adiciona um candidato na lista.
	 *  @param candidato - candidato */
	public void addCandidato(final Candidato candidato) {
		this.listaCandidatos.add(candidato);
	}
	
	/** Adiciona um objeto de erros de processamento de dados de candidato na lista.
	 *  @param excecao - objeto de erros */
	public void addExcecao(final RowParseException excecao) {
		this.listaExcecoes.add(excecao);
	}
	
	/** Recupera a lista de candidatos processados com sucesso.
	 *  @return A lista interna de candidatos. */
	public ArrayList<Candidato> getListaCandidatos() {
		return this.listaCandidatos;
	}

	/** Recupera a lista de candidatos processados com erro.
	 *  @return A lista interna de erros de processamento. */
	public ArrayList<RowParseException> getListaExcecoes() {
		return this.listaExcecoes;
	}
	
	/** Ordena as listas internas em ordem crescente de acordo com o nome do candidato.
	 *  @since 3.0, 21/04/2021 */
	public void sort() {
		
		Comparator<Candidato>    comparaCandidato = (candidato1, candidato2) -> candidato1.compareTo(candidato2); 
		Comparator<RowParseException> comparaErro = (exception1, exception2) -> exception1.compareTo(exception2);
		
		Collections.sort(this.listaCandidatos, comparaCandidato);
		Collections.sort(this.listaExcecoes  , comparaErro);
		
	}
	
}