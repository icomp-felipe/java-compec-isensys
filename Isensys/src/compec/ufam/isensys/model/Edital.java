package compec.ufam.isensys.model;

import java.io.*;

import org.joda.time.*;
import org.joda.time.format.*;

import compec.ufam.isensys.view.*;
import compec.ufam.isensys.constants.*;
import compec.ufam.isensys.model.retorno.*;

/** Armazena dados referentes ao edital e instituição utilizadora do Sistac.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.8, 21/JUN/2023
 *  @since 3.5, 24/04/2021 */
public class Edital {
	
	private String cnpj, edital, dataEdital;
	private int sequencia, nextSequencia;
	
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
		this.dataEdital = getDataSistac();
		this.sequencia  = sequencia;
		
	}

	/** Construtor utilizado nas views {@link TelaRetornoPreliminar} e {@link TelaRetornoDefinitivo}, aqui os dados são obtidos do nome do <code>arquivo</code>.
	 *  Este construtor está ignorando possíveis erros que podem acontecer, como <code>NullPointerException</code>
	 *  ou <code>ArrayIndexOutOfBoundsException</code>, nesses casos, o objeto pode ficar com atributos nulos.
	 *  @param arquivo - planilha de retorno ou erros ou arquivo de compilação */
	public Edital(final File arquivo) {
		
		try {
		
			final String[] dados = arquivo.getName().split("_");
		
			this.cnpj       = dados[1];
			this.edital     = dados[2];
			this.dataEdital = dados[3].substring(0,8);
			
			if (dados.length == 5)
				this.nextSequencia = this.sequencia = Integer.parseInt(dados[4].substring(0,3));
			
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
		
		String filename = String.format(Constants.StringFormat.ERROS_FILENAME_FORMAT, this.cnpj, this.edital, this.dataEdital);
		
		return new File(parent, filename);
	}
	
	/** Monta o nome do próximo arquivo de retorno, sempre incrementando o próximo número de sequência.
	 *  @param currentFile - arquivo atualmente processado, utilizado para encontrar os outros no mesmo diretório
	 *  @return Um arquivo com o nome no formato 'SISTAC_RETV_FILENAME_FORMAT.txt'
	 *  @since 3.5, 24/04/2021 */
	public File getNextRetornoFile(final File currentFile) {
		
		String filename = String.format(Constants.StringFormat.SISTAC_RETV_FILENAME_FORMAT, this.cnpj, this.edital, this.dataEdital, ++this.nextSequencia);
		
		return new File(currentFile.getParentFile(), filename);
	}
	
	/** Monta o nome do arquivo de compilação, com os dados internos desta classe.
	 *  @return Um arquivo com o nome no formato 'COMPILACAO_CNPJ_EDITAL_DATA.xlsx' */
	public File getCompilationFilename() {
		
		String filename = String.format(Constants.StringFormat.COMPILACAO_FILENAME_FORMAT, this.cnpj, this.edital, this.dataEdital);
		
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
	
	/** Retorna a data atual do Sistac (UTC ou GMT+0) no formato DDMMYYYY. 
	 *  @return Uma String contendo a data atual do Sistac no formato DDMMYYYY. */
	private String getDataSistac() {
		
		DateTime sistac = DateTime.now().withZone(DateTimeZone.forID("UTC"));
		DateTimeFormatter formatter = DateTimeFormat.forPattern("ddMMyyyy");
		
	    return sistac.toString(formatter);
	}
	
	/** Imprime todos os atributos dessa classe, útil para debug. */
	public void print() {
		System.out.format("CNPJ: %s - Edital: %s - Data do Edital: %s\n", this.cnpj, this.edital, this.dataEdital);
	}

}