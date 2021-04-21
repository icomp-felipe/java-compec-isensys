package compec.ufam.sistac.model.retorno;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.joda.time.*;

/** Armazena uma lista de {@link Retorno}, classe utilizada na construção dos editais
 *  e que também pode ser escrita em disco, por ser serializável. Também armazena alguns
 *  atributos referentes à instituição geradora dos arquivos + informações sobre o(s)
 *  arquivo(s) de retorno sendo atualmente processado(s).
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 21/04/2021
 *  @see Retorno */
public class ListaRetornos implements Serializable {

	// Serial de versionamento da classe
	private static final transient long serialVersionUID = 3;
	
	// Lista de retornos
	private ArrayList<Retorno> listaRetornos;
	
	// Atributos da instituição geradora
	private final String cnpj, nomeFantasia, razaoSocial;
	
	// Atributos do(s) arquivo(s) de retorno em processamento
	private final String   edital;
	private final DateTime dataEdital;
	
	/************************ Bloco de Construtores ****************************/
	
	public ListaRetornos(final String cnpj, final String nomeFantasia, final String razaoSocial, final String edital, final DateTime dataEdital) {
		
		// Inicializando atributos
		this.cnpj         = cnpj;
		this.nomeFantasia = nomeFantasia;
		this.razaoSocial  = razaoSocial;
		
		this.edital       = edital;
		this.dataEdital   = dataEdital;
		
		// Inicializando lista de retornos
		this.listaRetornos = new ArrayList<Retorno>();
		
	}
	
	/** Construtor usado pelo método <code>clone()</code>.
	 *  @since 3.00 */
	private ListaRetornos(final ListaRetornos listaRetornos) {
		
		// Inicializando atributos
		this.cnpj         = listaRetornos.cnpj;
		this.nomeFantasia = listaRetornos.nomeFantasia;
		this.razaoSocial  = listaRetornos.razaoSocial;
		
		this.edital       = listaRetornos.edital;
		this.dataEdital   = listaRetornos.dataEdital;
		
		// Inicializando lista de retornos (tamanho otimizado)
		this.listaRetornos = new ArrayList<Retorno>(listaRetornos.size());
		
		// Copiando dados
		for (Retorno retorno: listaRetornos.getList())
			add(retorno);
		
	}
	
	/** Recupera um 'Retorno' da lista de acordo com sua posição na lista, indicada por 'index' */
	public Retorno get(int index) {
		return listaRetornos.get(index);
	}
	
	/** Retorna o tamanho da lista de 'Retorno' */
	public int size() {
		return listaRetornos.size();
	}
	
	/** Recupera a lista de 'Retorno' */
	public ArrayList<Retorno> getList() {
		return listaRetornos;
	}
	
	@Override
	/** Retorna uma clone desta classe.
	 *  @since 3.00 */
	public ListaRetornos clone() {
		return new ListaRetornos(this);
	}
	
	/** Adiciona um 'Retorno' na lista de 'Retorno' já tratando o deferimento do candidato */
	public synchronized void add(Retorno retorno) {
		
		// Aqui percorro a lista de 'Retorno' e, para cada objeto...
		for (Retorno presente: listaRetornos) {
			
			// ...identifico o candidato e...
			if (presente.getCPF().equals(retorno.getCPF())) {
			
				// ...se este encontra-se indeferido na listagem preliminar, mas foi deferido na final, o defiro e atualizo o NIS
				if ((!presente.deferido()) && (retorno.deferido())) {
					presente.defere();
					presente.setNIS(retorno.getNis());
				}
				
				// Evita que eu insira dados duplicados na lista
				return;
				
			}
		}
		
		// Se não há dados duplicados, insiro um novo 'Retorno' na lista
		listaRetornos.add(retorno);
	}
	
	/** Método mais lindo do programa :)
	 *  Agrupa a lista de retornos de acordo com o status (deferido ou indeferido) e ordena alfabeticamente
	 *  pelo nome do candidato */
	public synchronized void sort() {
		
		// Criando o comparador para utilizar na função Collections.sort()
		Comparator<Retorno> comparador = new Comparador();
		
		// Aqui, derivo dois 'ArrayList<Retorno>', um só com os deferidos e outro com os indeferidos
		Map<Boolean,List<Retorno>> map = listaRetornos.stream().collect(Collectors.groupingBy(Retorno::deferido));
		
		// Recuperando as listas do 'Map'
		List<Retorno>   deferidos = map.get(true );
		List<Retorno> indeferidos = map.get(false);
		
		// Criando a nova lista de retornos (estará agrupada e ordenada no fim deste método)
		ArrayList<Retorno> novaLista = new ArrayList<Retorno>();
		
		// Fazendo a ordenação e cópia dos dados (primeiro deferidos)...
		if (deferidos != null) {
			
			Collections.sort(deferidos,comparador);
			for (Retorno retorno: deferidos)
				novaLista.add(retorno);
			
		}
		
		// ...depois os indeferidos
		if (indeferidos != null) {
			
			Collections.sort(indeferidos,comparador);
			for (Retorno retorno: indeferidos)
				novaLista.add(retorno);
		}
		
		// Atualizando a lista da classe
		this.listaRetornos = novaLista;
		
	}
	
	/** Implementa o comparador de 'Retorno'. Trabalha com ordem alfabética do nome do candidato */
	private class Comparador implements Comparator<Retorno> {

		@Override
		public int compare(Retorno retorno1, Retorno retorno2) {
			return retorno1.getNome().compareTo(retorno2.getNome());
		}
		
	}

}
