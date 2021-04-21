package compec.ufam.sistac.model.envio;

import java.util.*;
import compec.ufam.sistac.exception.*;

/** Objeto que armazena listas de 'Candidato' (objetos lidos com sucesso dos arquivos)
 *  e 'RowParseException' (objetos com alguma falha de processamento)
 *  @author Felipe André
 *  @version 2.50, 08/07/2018
 *  @see Candidato
 *  @see RowParseException */
public class ParseResult {

	private ArrayList<Candidato> listaCandidatos;
	private ArrayList<RowParseException> listaExcecoes;
	
	/** Construtor apenas criando as duas listas vazias */
	public ParseResult() {
		this.listaCandidatos = new ArrayList<Candidato>();
		this.listaExcecoes   = new ArrayList<RowParseException>();
	}

	/** Adiciona um 'Candidato' na lista */
	public void addCandidato(Candidato candidato) {
		this.listaCandidatos.add(candidato);
	}
	
	/** Adiciona uma 'RowParseException' na lista */
	public void addExcecao(RowParseException excecao) {
		this.listaExcecoes.add(excecao);
	}
	
	/** Recupera a lista de candidatos processados com sucesso */
	public ArrayList<Candidato> getListaCandidatos() {
		return listaCandidatos;
	}

	/** Recupera a lista de candidatos processados com erro */
	public ArrayList<RowParseException> getListaExcecoes() {
		return listaExcecoes;
	}
	
}
