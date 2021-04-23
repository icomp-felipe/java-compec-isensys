package compec.ufam.sistac.model;

public class Instituicao {

	private final String cnpj, nomeFantasia, razaoSocial;

	public Instituicao(final String cnpj, final String nomeFantasia, final String razaoSocial) {
		this.cnpj = cnpj;
		this.nomeFantasia = nomeFantasia;
		this.razaoSocial = razaoSocial;
	}
	
	public Instituicao(final String firstLine, final String delimiter) {
		
		String[] dados = firstLine.split(delimiter);
		
		this.cnpj         = dados[1];
		this.nomeFantasia = dados[2];
		this.razaoSocial  = dados[3];
		
	}

	public String getCNPJ() {
		return cnpj;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}
	
	public boolean equals(final Instituicao instituicao) {
		return (this.cnpj.equals(instituicao.cnpj) && this.nomeFantasia.equals(instituicao.nomeFantasia) && this.razaoSocial.equals(instituicao.razaoSocial));
	}
	
	public String validate() {
		
		StringBuilder builder = new StringBuilder();
		
		if ((this.cnpj == null) || (this.cnpj.isEmpty()))
			builder.append("* CNPJ vazio\n");
		
		else if (this.cnpj.length() != 14)
			builder.append("* CNPJ inválido\n");
		
		if ((this.nomeFantasia == null) || (this.nomeFantasia.isEmpty()))
			builder.append("* Nome fantasia vazio\n");
		
		else if (this.nomeFantasia.length() > 100)
			builder.append("* Nome fantasia possui mais de 100 caracteres\n");
		
		if ((this.razaoSocial == null) || (this.razaoSocial.isEmpty()))
			builder.append("* Razão social vazia\n");
		
		else if (this.razaoSocial.length() > 100)
			builder.append("* Razão social possui mais de 100 caracteres\n");
		
		final String msg = builder.toString().trim();
		
		return msg.isEmpty() ? null : msg;
	}
	
	public String compare(final Instituicao instituicao) {
		
		StringBuilder builder = new StringBuilder("(");
		
		if ((this.cnpj == null) || (!this.cnpj.equals(instituicao.cnpj)))
			builder.append("CNPJ");
		
		if ((this.nomeFantasia == null) || (!this.nomeFantasia.equals(instituicao.nomeFantasia))) {
			
			if (builder.toString().length() > 1)
				builder.append(", ");
			
			builder.append("nome fantasia");
			
		}
		
		if ((this.razaoSocial == null) || (!this.razaoSocial.equals(instituicao.razaoSocial))) {
			
			if (builder.toString().length() > 1)
				builder.append(", ");
			
			builder.append("razão social");
			
		}
		
		builder.append(")");
		
		final String msg = builder.toString();
		
		return (msg.length() == 2) ? null : msg;
	}
	
}
