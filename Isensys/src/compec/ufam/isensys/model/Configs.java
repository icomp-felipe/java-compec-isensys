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
	
	private final String cnpj, nomeFantasia, razaoSocial;
	private final int[] indices;
	
	/** Construtor utilizado única e exclusivamente na {@link TelaConfigs}. Inicializa os atributos e transforma
	 *  o <code>nomeFantasia</code> e <code>razaoSocial</code> em caixa alta.
	 *  @param cnpj - número de CNPJ da instituição
	 *  @param nomeFantasia - nome fantasia da instituição
	 *  @param razaoSocial - razão social da instituição
	 *  @param indices - índices de importação das planilhas (começando em 1) */
	public Configs(final String cnpj, final String nomeFantasia, final String razaoSocial, final int[] indices) {
		
		this.cnpj         = cnpj;
		this.nomeFantasia = nomeFantasia.toUpperCase();
		this.razaoSocial  = razaoSocial .toUpperCase();
		this.indices      = indices;
		
	}
	
	/** Getter do número de CNPJ da instituição.
	 *  @return Uma String contendo o número de CNPJ da instituição. */
	public String getCNPJ() {
		return this.cnpj;
	}
	
	/** Getter do nome fantasia da instituição.
	 *  @return Uma String contendo o nome fantasia da instituição. */
	public String getNomeFantasia() {
		return this.nomeFantasia;
	}
	
	/** Getter da razão social da instituição.
	 *  @return Uma String contendo a razão social da instituição. */
	public String getRazaoSocial() {
		return this.razaoSocial;
	}
	
	/** Monta uma String de cabeçalho do arquivo Sistac.
	 *  @return Uma String contendo o cabeçalho do arquivo Sistac. */
	public String getCabecalhoSistac() {
		return String.format("0;%s;%s;%s;", this.cnpj, this.nomeFantasia, this.razaoSocial);
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
	
}