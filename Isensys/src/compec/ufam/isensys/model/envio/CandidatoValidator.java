package compec.ufam.isensys.model.envio;

import java.util.Arrays;

import com.phill.libs.StringUtils;
import com.phill.libs.br.CPFParser;

import compec.ufam.isensys.exception.FieldParseException;
import compec.ufam.isensys.exception.RowParseException;
import compec.ufam.isensys.model.Candidato;

public class CandidatoValidator {

	public static void validate(final Candidato candidato, final int linha) throws RowParseException {
		
		RowParseException exceptions = null;
		
		if (!StringUtils.isAlphaStringOnly(candidato.getNome())) {
			
			exceptions = new RowParseException(candidato, linha);
			exceptions.addException(new FieldParseException("Nome inválido", candidato.getNome()));
			
		}
		
		if (candidato.getNome().length() > 100) {
			
			if (exceptions == null)
				exceptions = new RowParseException(candidato, linha);
			
			exceptions.addException(new FieldParseException("Nome contém mais de 100 caracteres", candidato.getNome()));
			
		}
		
		if (candidato.getCpf() == null) {
			
			if (exceptions == null)
				exceptions = new RowParseException(candidato, linha);
			
			exceptions.addException(new FieldParseException("CPF vazio", candidato.getNome()));
			
		}
		
		if (!CPFParser.parse(candidato.getCpf())) {
			
			if (exceptions == null)
				exceptions = new RowParseException(candidato, linha);
			
			exceptions.addException(new FieldParseException("CPF inválido", candidato.getNome()));
			
		}
		
		if (candidato.getDataNascimento() == null) {
			
			if (exceptions == null)
				exceptions = new RowParseException(candidato, linha);
			
			exceptions.addException(new FieldParseException("Data de nascimento inválida ou vazia", ""));
			
		}
		
		if (exceptions != null) {
			System.out.println(Arrays.toString(exceptions.getErrorSummaryArray()));
			throw exceptions;
		}
		
	}
	
}
