package compec.ufam.isensys.model;

import java.io.*;
import java.text.*;
import java.util.*;

import compec.ufam.isensys.view.*;
import compec.ufam.isensys.model.retorno.*;

/** Armazena dados referentes ao edital e instituição utilizadora do Sistac.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 1.0, 23/04/2021
 *  @since 3.0, 22/04/2021 */
public class Edital {
	
	private String cnpj, edital, dataEdital;
	private int sequencia;
	
	/************************ Bloco de Construtores ****************************/
	
	/** Construtor utilizado na {@link ListaRetornos#getEdital()}, aqui não é realizada verificação alguma de integridade nos dados.
	 *  @param cnpj - CNPJ da instituição
	 *  @param edital - número do edital
	 *  @param dataEdital - data de envio do arquivo Sistac (no formato DDMMYYYY) */
	public Edital(final String cnpj, final String edital, final String dataEdital) {
		
		this.cnpj = cnpj;
		this.edital = edital;
		this.dataEdital = dataEdital;
		
	}
	
	/** Construtor utilizado na {@link TelaEnvio}. Incorpora dados completos do edital. A data do edital é definida no ato da construção dessa classe.
	 *  @param cnpj - CNPJ da instituição
	 *  @param edital - número do edital
	 *  @param sequencia - número de sequência do arquivo */
	public Edital(final String cnpj, final String edital, final int sequencia) {
		
		this.cnpj       = cnpj;
		this.edital     = edital;
		this.dataEdital = getDataAtual();
		this.sequencia  = sequencia;
		
	}

	/** Construtor utilizado nas views {@link TelaRetornoPreliminar} e {@link TelaRetornoFinal}, aqui os dados são obtidos do nome do <code>arquivo</code>.
	 *  Este construtor está ignorando possíveis erros que podem acontecer, como <code>NullPointerException</code>
	 *  ou <code>ArrayIndexOutOfBoundsException</code>, nesses casos, o objeto pode ficar com atributos nulos.
	 *  @param arquivo - planilha de retorno ou erros ou arquivo de compilação */
	public Edital(final File arquivo) {
		
		try {
		
			final String[] dados = arquivo.getName().split("_");
		
			this.cnpj       = dados[1];
			this.edital     = dados[2];
			this.dataEdital = dados[3].substring(0,8);
			
		}
		catch (Exception exception) { }
		
	}
	
	/**************************** Bloco de Getters *****************************/
	
	/** Getter do número de edital.
	 *  @return String contendo o número do edital. */
	public String getEdital() {
		return this.edital;
	}

	/** Getter da data de envio do arquivo Sistac.
	 *  @return String contendo a data de envio do arquivo Sistac. */
	public String getDataEdital() {
		return this.dataEdital;
	}
	
	/** Getter da sequência de arquivo.
	 *  @return Sequência de envio de arquivo. */
	public int getSequencia() {
		return this.sequencia;
	}
	
	/** Monta o nome do arquivo de erros, com os dados internos desta classe.
	 *  @param parent - diretório do arquivo
	 *  @return Um arquivo com o nome no formato 'ERROS_CNPJ_EDITAL_DATA.xlsx' */
	public File getErrorFilename(final File parent) {
		
		String filename = String.format("ERROS_%s_%s_%s.xlsx", this.cnpj, this.edital, this.dataEdital);
		
		return new File(parent, filename);
	}
	
	/** Monta o nome do arquivo de compilação, com os dados internos desta classe.
	 *  @return Um arquivo com o nome no formato 'COMPILACAO_CNPJ_EDITAL_DATA.xlsx' */
	public File getCompilationFilename() {
		
		String filename = String.format("COMPILACAO_%s_%s_%s.bsf", this.cnpj, this.edital, this.dataEdital);
		
		return new File(filename);
	}
	
	/** Verifica se o <code>edital</code> passado é exatamente igual a esta instância da classe.
	 *  @param edital - objeto edital a ser comparado
	 *  @return 'true' se o resultado da comparação de todos os campos dos objetos for 'true';<br>'false' caso algum dado seja diferente ou nulo. */
	public boolean equals(final Edital edital) {
		return (this.cnpj.equals(edital.cnpj)) && (this.edital.equals(edital.edital)) && (this.dataEdital.equals(edital.dataEdital));
	}
	
	/** Verifica se o CNPJ e número de edital do objeto <code>edital</code> passado é exatamente igual a esta instância da classe.
	 *  @param edital - objeto edital a ser comparado
	 *  @return 'true' se o resultado da comparação de CNPJ e edital dos objetos for 'true';<br>'false' caso algum dado seja diferente ou nulo. */
	public boolean equalsIgnoreDate(final Edital edital) {
		return (this.cnpj.equals(edital.cnpj)) && (this.edital.equals(edital.edital));
	}
	
	/*********************** Outras Funcionalidades ****************************/
	
	/** Retorna a data atual do sistema no formato DDMMYYYY.
	 *  @return Uma String contendo a data atual do sistema no formato DDMMYYYY. */
	private String getDataAtual() {        
	    return new SimpleDateFormat("ddMMyyyy").format(new Date(System.currentTimeMillis()));
	}
	
	/** Imprime todos os atributos dessa classe, útil para debug. */
	public void print() {
		System.out.format("CNPJ: %s - Edital: %s - Data do Edital: %s\n", this.cnpj, this.edital, this.dataEdital);
	}

}