package compec.ufam.isensys.model;

import java.io.*;
import java.util.*;

import compec.ufam.isensys.view.*;

/** Classe que armazena as configurações do sistema.
 *  @version 1.0, 26/APR/2021
 *  @since 3.5, 26/APR/2021 */
public class Configs implements Serializable {
	
	// Serial de versionamento da classe
	private static final long serialVersionUID = 3L;
	
	private Instituicao instituicao;
	private final int[] indices;
	
	/** Construtor utilizado única e exclusivamente na {@link TelaConfigs}. Inicializa os atributos e transforma
	 *  o <code>nomeFantasia</code> e <code>razaoSocial</code> em caixa alta.
	 *  @param cnpj - número de CNPJ da instituição
	 *  @param nomeFantasia - nome fantasia da instituição
	 *  @param razaoSocial - razão social da instituição
	 *  @param indices - índices de importação das planilhas (começando em 1) */
	public Configs(final Instituicao instituicao, final int[] indices) {
		
		this.instituicao = instituicao;
		this.indices     = indices;
		
	}
	
	public Instituicao getInstituicao() {
		return this.instituicao;
	}
	
	/** Getter dos índices de importação das planilhas. Aqui os índices começam em 0.
	 *  @return Vetor de int[] com os índices começando em 0. */
	public int[] getIndices() {
		return Arrays.stream(this.indices).map(index -> index-1).toArray();
	}
	
	/** Getter dos índices de importação das planilhas, no formato de linha de uma JTable. Aqui os índices começam em 1.
	 *  @return Linha de uma JTable 'Object[]' com os índices começando em 1. */
	public Object[] getIndicesTabela() {
		return Arrays.stream(this.indices).boxed().toArray(Object[]::new);
	}

	public void setInstituicao(final Instituicao instituicao) {
		this.instituicao = instituicao;
	}
	
}