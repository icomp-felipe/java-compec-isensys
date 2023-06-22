package compec.ufam.isensys.model;

import java.io.File;

public class ArquivoProcessado {

	private File arquivo;
	private String tipo;
	
	public ArquivoProcessado(final File arquivo, final String tipo) {
		
		this.arquivo = arquivo;
		this.tipo = tipo;
		
	}
	
	public String getNome() {
		return this.arquivo.getName();
	}
	
	public String getTipo() {
		return this.tipo;
	}
	
}
