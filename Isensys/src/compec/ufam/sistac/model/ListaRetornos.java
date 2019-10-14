package compec.ufam.sistac.model;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/** Classe que armazena e trata uma lista de objetos do tipo 'Retorno', utilizada para
 *  operações de escrita e leitura do arquivo de compilação no disco.
 *  @author Felipe André
 *  @version 2.50, 08/07/2018
 *  @see Retorno */
public class ListaRetornos implements Serializable {

	private static final transient long serialVersionUID = 1L;
	private ArrayList<Retorno> listaRetornos;
	
	/** Construtor apenas criando uma lista vazia */
	public ListaRetornos() {
		this.listaRetornos = new ArrayList<Retorno>();
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
	
	/** Método utilizado apenas para testes com o Jaspersoft Studio */
	public static ArrayList<Retorno> getArray() {
		
		ArrayList<Retorno> listaRetornos = new ArrayList<Retorno>();
		
		listaRetornos.add(new Retorno(new String[]{"12345", "TESTE 1", "4"}));
		listaRetornos.add(new Retorno(new String[]{"67890", "TESTE 2", "5"}));
		listaRetornos.add(new Retorno(new String[]{"54321", "TESTE 3", "6"}));
		
		return listaRetornos;
	}

}
