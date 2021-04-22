package compec.ufam.sistac.model.retorno;

import java.io.File;

public class Edital {
	
	private String cnpj, edital, dataEdital;
	
	public Edital(final File arquivo) {
		
		try {
		
			final String[] dados = arquivo.getName().split("_");
		
			this.cnpj       = dados[1];
			this.edital     = dados[2];
			this.dataEdital = dados[3].substring(0,8);
			
		}
		catch (Exception exception) { }
		
	}
	
	public File getErrorFilename() {
		
		String filename = String.format("ERROS_%s_%s_%s.xlsx", this.cnpj, this.edital, this.dataEdital);
		
		return new File(filename);
	}
	
	public boolean equals(final Edital edital) {
		return (this.cnpj.equals(edital.cnpj)) && (this.edital.equals(edital.edital)) && (this.dataEdital.equals(edital.dataEdital)); 
	}
	
	public void print() {
		System.out.format("CNPJ: %s - Edital: %s - Data do Edital: %s\n", this.cnpj, this.edital, this.dataEdital);
	}

	public File getCompilationFilename() {
		String filename = String.format("COMPILACAO_%s_%s_%s.bsf", this.cnpj, this.edital, this.dataEdital);
		
		return new File(filename);
	}

}
