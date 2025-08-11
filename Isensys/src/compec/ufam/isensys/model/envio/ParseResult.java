package compec.ufam.isensys.model.retorno;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import compec.ufam.isensys.exception.RowParseException;
import compec.ufam.isensys.model.Candidato;

/** Objeto que concentra listas de {@link Candidato} (objetos lidos com sucesso dos arquivos)
 *  e {@link RowParseException} (objetos com alguma falha de processamento).
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 03/AGO/2025
 *  @see Candidato
 *  @see RowParseException */
public class ParseResult {

	private final ArrayList<Candidato> listaCandidatos;
	private final ArrayList<RowParseException> listaExcecoes;
	
	{
		this.listaCandidatos = new ArrayList<Candidato>();
		this.listaExcecoes   = new ArrayList<RowParseException>();
	}

	/** Adiciona um candidato na lista (apenas se ele já não tiver sido inserido).
	 *  @param candidato - candidato
	 *  @return 'false' apenas se o candidato já foi previamente inserido na lista (duplicado) */
	public boolean addCandidato(final Candidato candidato) {
		
		final boolean unique = !listaCandidatos.contains(candidato);
		
		if (unique)
			this.listaCandidatos.add(candidato);
		
		return unique;
	}
	
	/** Adiciona um objeto de erros de processamento de dados de candidato na lista (apenas se ele já não tiver sido inserido).
	 *  @param excecao - objeto de erros */
	public void addExcecao(final RowParseException excecao) {
		
		if (!this.listaExcecoes.contains(excecao))
			 this.listaExcecoes.add     (excecao);
		
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
	
	/** Ordena as listas internas em ordem crescente de acordo com o nome do candidato. */
	public void sortLists() {
		
		Comparator<Candidato>    comparaCandidato = (candidato1, candidato2) -> candidato1.compareTo(candidato2); 
		Comparator<RowParseException> comparaErro = (exception1, exception2) -> exception1.compareTo(exception2);
		
		Collections.sort(this.listaCandidatos, comparaCandidato);
		Collections.sort(this.listaExcecoes  , comparaErro);
		
	}
	
}
