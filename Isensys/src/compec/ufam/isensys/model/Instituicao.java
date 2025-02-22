package compec.ufam.isensys.model;

import java.io.Serializable;

/** Armazena dados referentes à instituição utilizadora do Sistac.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 3.8, 21/JUN/2023
 *  @since 3.0, 22/04/2021 */
public class Instituicao implements Serializable {

	// Serial de versionamento da classe
	private static final long serialVersionUID = 3L;
	
	// Atributos de texto
	private final String cnpj, nomeFantasia, razaoSocial;

	/************************ Bloco de Construtores ****************************/
	
	/** Construtor normal, aqui não é realizada verificação alguma de integridade nos dados.
	 *  @param cnpj - CNPJ da instituição
	 *  @param nomeFantasia - nome fantasia da instituição
	 *  @param razaoSocial - razão social da instituição */
	public Instituicao(final String cnpj, final String nomeFantasia, final String razaoSocial) {
		
		this.cnpj         = cnpj;
		this.nomeFantasia = nomeFantasia;
		this.razaoSocial  = razaoSocial;
		
	}
	
	/** Construtor alternativo, aqui os dados são obtidos da <code>primeiraLinha</code> do arquivo Sistac.
	 *  Este construtor NÃO está ignorando possíveis erros que podem acontecer, como <code>NullPointerException</code>
	 *  ou <code>ArrayIndexOutOfBoundsException</code>, nesses casos, as exceções serão lançadas!
	 *  @param primeiraLinha - primeira linha do arquivo Sistac
	 *  @param delimitador - delimitador do texto csv */
	public Instituicao(final String primeiraLinha, final String delimitador) {
		
		String[] dados = primeiraLinha.split(delimitador);
		
		this.cnpj         = dados[1];
		this.nomeFantasia = dados[2];
		this.razaoSocial  = dados[3];
		
	}

	/**************************** Bloco de Getters *****************************/
	
	/** Getter do CNPJ da instituição.
	 *  @return Uma String contendo o CNPJ da instituição. */
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
		return razaoSocial;
	}
	
	/** Monta uma String de cabeçalho do arquivo Sistac.
	 *  @return Uma String contendo o cabeçalho do arquivo Sistac. */
	public String getCabecalhoSistac() {
		return String.format("0;%s;%s;%s;", this.cnpj, this.nomeFantasia, this.razaoSocial);
	}
	
	/** Verifica se a <code>instituicao</code> passada é exatamente igual a esta instância da classe.
	 *  @param instituicao - instituicao a ser comparada
	 *  @return 'true' se o resultado da comparação de todos os campos dos objetos for 'true';<br>'false' caso algum dado seja diferente ou nulo. */
	public boolean equals(final Instituicao instituicao) {
		return (this.cnpj.equals(instituicao.cnpj) && this.nomeFantasia.equals(instituicao.nomeFantasia) && this.razaoSocial.equals(instituicao.razaoSocial));
	}
	
	/************************** Bloco de Validadores ***************************/
	
	/** Verifica se todos os dados internos à classe estão respeitando o formato Sistac.
	 *  @return Uma String com mensagens de erro de validação dos campos.<br>Retorna 'null' caso todos os dados sejam válidos. */
	public String validate() {
		
		StringBuilder builder = new StringBuilder();
		
		// Validando CNPJ
		if ((this.cnpj == null) || (this.cnpj.isEmpty()))
			builder.append("* CNPJ vazio\n");
		
		else if (this.cnpj.length() != 14)
			builder.append("* CNPJ inválido\n");
		
		// Validando nome fantasia
		if ((this.nomeFantasia == null) || (this.nomeFantasia.isEmpty()))
			builder.append("* Nome fantasia vazio\n");
		
		else if (this.nomeFantasia.length() > 100)
			builder.append("* Nome fantasia possui mais de 100 caracteres\n");
		
		// Validando razão social
		if ((this.razaoSocial == null) || (this.razaoSocial.isEmpty()))
			builder.append("* Razão social vazia\n");
		
		else if (this.razaoSocial.length() > 100)
			builder.append("* Razão social possui mais de 100 caracteres\n");
		
		// Preparando retorno
		final String msg = builder.toString().trim();
		
		return msg.isEmpty() ? null : msg;
	}
	
	/** Compara os dados da instância atual desta classe com a recebida via parâmetro.
	 *  @param instituicao - instituição a ser comparada
	 *  @return Uma String com o nome dos campos diferentes ou 'null' se todos os campos forem iguais. */
	public String compare(final Instituicao instituicao) {
		
		StringBuilder builder = new StringBuilder("(");
		
		// Verificando CNPJ
		if ((this.cnpj == null) || (!this.cnpj.equals(instituicao.cnpj)))
			builder.append("CNPJ");
		
		// Verificando nome fantasia
		if ((this.nomeFantasia == null) || (!this.nomeFantasia.equals(instituicao.nomeFantasia))) {
			
			if (builder.toString().length() > 1)
				builder.append(", ");
			
			builder.append("nome fantasia");
			
		}
		
		// Verificando razão social
		if ((this.razaoSocial == null) || (!this.razaoSocial.equals(instituicao.razaoSocial))) {
			
			if (builder.toString().length() > 1)
				builder.append(", ");
			
			builder.append("razão social");
			
		}
		
		builder.append(")");
		
		// Preparando retorno
		final String msg = builder.toString();
		
		return (msg.length() == 2) ? null : msg;
	}
	
}