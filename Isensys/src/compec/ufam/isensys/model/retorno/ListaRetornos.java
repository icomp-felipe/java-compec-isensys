package compec.ufam.isensys.model.retorno;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import compec.ufam.isensys.model.*;

/** Armazena uma lista de {@link Retorno}, classe utilizada na construção dos editais
 *  e que também pode ser escrita em disco, por ser serializável. Também armazena alguns
 *  atributos referentes à instituição geradora dos arquivos + informações sobre o(s)
 *  arquivo(s) de retorno sendo atualmente processado(s).
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 3.8, 21/JUN/2023
 *  @see Retorno */
public class ListaRetornos implements Serializable {

	// Serial de versionamento da classe
	private static final transient long serialVersionUID = 3;
	
	// Lista de retornos
	private ArrayList<Retorno> listaRetornos;
	
	// Atributos da instituição geradora
	private String cnpj, nomeFantasia, razaoSocial;
	
	// Atributos do(s) arquivo(s) de retorno em processamento
	private String edital, dataEdital, cabecalho;
	
	/************************ Bloco de Construtores ****************************/
	
	/** Construtor regular, apenas inicializando atributos internos. */
	public ListaRetornos() {
		this.listaRetornos = new ArrayList<Retorno>();
	}
	
	/** Construtor alternativo, utilizado pelo método <code>clone()</code>.
	 *  @since 3.00, 18/04/2021 */
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
	
	/**************************** Bloco de Setters *****************************/
	
	/** Setter da instituição.
	 *  @param instituicao - dados institucionais a serem copiados para esta instância da classe */
	public void setInstituicao(final Instituicao instituicao) {
		
		if (instituicao != null) {
			
			this.cnpj         = instituicao.getCNPJ();
			this.nomeFantasia = instituicao.getNomeFantasia();
			this.razaoSocial  = instituicao.getRazaoSocial ();
			
		}
		
	}
	
	/** Setter do edital.
	 *  @param edital - dados de edital a serem copiados para esta instância da classe */
	public void setEdital(final Edital edital) {
		
		if (edital != null) {
			
			this.edital     = edital.getEdital    ();
			this.dataEdital = edital.getDataEdital();
			
		}
		
	}
	
	/** Setter do cabeçalho de edital.
	 *  @param cabecalho - cabeçalho de edital */
	public void setCabecalho(final String cabecalho) {
		this.cabecalho = cabecalho;
	}
	
	/**************************** Bloco de Getters *****************************/
	
	/** Getter da instituição.
	 *  @return Uma nova instituição com os dados internos desta instância. */
	public Instituicao getInstituicao() {
		return new Instituicao(this.cnpj, this.nomeFantasia, this.razaoSocial);
	}
	
	/** Getter do edital.
	 *  @return Um novo edital com os dados internos desta instância. */
	public Edital getEdital() {
		return new Edital(this.cnpj, this.edital, this.dataEdital);
	}
	
	/** Getter do cabeçalho de edital.
	 *  @return Uma String contendo o cabeçalho de edital. */
	public String getCabecalho() {
		return this.cabecalho;
	}
	
	/******************** Bloco de Getters (Funcionalidades) *******************/
	
	/** Recupera um <code>Retorno</code> da lista de acordo com sua posição na lista, indicada por <code>index</code>.
	 *  @param index - índice do retorno na lista
	 *  @return Um objeto {@link Retorno} da lista. */
	public Retorno get(final int index) {
		return this.listaRetornos.get(index);
	}
	
	/** Retorna o tamanho da lista de retornos.
	 *  @return Tamanho da lista de retornos. */
	public int size() {
		return this.listaRetornos.size();
	}
	
	/** Recupera a lista de retornos.
	 *  @return Lista de retornos. */
	public ArrayList<Retorno> getList() {
		return this.listaRetornos;
	}
	
	@Override
	/** Retorna uma clone desta classe.
	 *  @since 3.00, 18/04/2021 */
	public ListaRetornos clone() {
		return new ListaRetornos(this);
	}
	
	/*********************** Outras Funcionalidades ****************************/
	
	/** Adiciona um {@link Retorno} na lista de retornos.
	 *  @param retorno - objeto retorno a ser inserido na lista */
	public synchronized void add(final Retorno retorno) {
		
		this.listaRetornos.add(retorno);
		
	}
	
	/** Atualiza os dados do candidato indicado no objeto 'retorno' e sua situação de deferimento.
	 *  @param retorno - objeto retorno com os dados a serem atualizados
	 *  @return 'true' se, e somente se, o candidato já existir na lista, ou seja, o CPF do <code>retorno</code> já foi previamente inserido nesta instância de {@link ListaRetornos}. */
	public synchronized boolean update(final Retorno retorno) {
		
		// Aqui percorro a lista de retornos e, para cada objeto...
		for (Retorno presente: listaRetornos) {
			
			// ...identifico o candidato e...
			if (presente.equals(retorno)) {
			
				// ...se este encontra-se indeferido na listagem preliminar, mas foi deferido na final, o defiro.
				if ((!presente.deferido()) && (retorno.deferido()))
					presente.defere();

				// Aqui são atualizados os dados de acordo com o recurso
				presente.setCPF   (retorno.cpf   );
				presente.setNome  (retorno.nome  );
				presente.setMotivo(retorno.motivo);
				
				// Evita processamento desnecessário
				return true;
				
			}
			
		}
		
		System.err.println("Candidato(a) recursante, porém, não solicitante: " + retorno.getNome());
		add(retorno);
		
		return false;
	}
	
	/** Agrupa a lista de retornos de acordo com o status (deferido ou indeferido) e ordena alfabeticamente pelo nome do candidato. */
	public synchronized void sort() {
		
		// Criando o comparador para utilizar na função Collections.sort()
		Comparator<Retorno> comparador = (retorno1, retorno2) -> retorno1.compareTo(retorno2);
		
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

}