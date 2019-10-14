package compec.ufam.sistac.model;

import org.joda.time.*;
import com.phill.libs.*;
import com.phill.libs.time.*;
import com.phill.libs.exception.*;
import compec.ufam.sistac.exception.*;

/** Classe que monta um 'Candidato' a partir das informações do vetor 'args'
 *  @author Felipe André
 *  @version 2.50, 07/07/2018 */
public class CandidatoBuilder {

	/** Monta a classe 'Candidato' de acordo com 'args' */
	public static Candidato parse(int linha, String[] args) throws RowParseException {
		
		// Preparando um possível erro com os parâmetros (nome,nis e cpf) respectivamente
		RowParseException exceptions = new RowParseException(linha, args[0], args[1], args[7]);

		long nis, cpf;
		String nome,emissorRG,nomeMae;
		DateTime dataNascimento, dataRG;
		
		// Inicialização das Variáveis
		nome = emissorRG = nomeMae = null; nis = cpf = 0L; dataNascimento = dataRG = null;
		
		// Tratamento do Nome
		try { nome = parseNome(args[0],false); exceptions.setNome(nome); }
		catch (FieldException exception) { exceptions.addException(exception); }
		
		// Tratamento do Órgão Emissor do RG
		emissorRG = parseOrgao(args[6]);
		
		// Tratamento do Nome da Mãe
		try { nomeMae = parseNome(args[8],true); }
		catch (FieldException exception) { exceptions.addException(exception); }
		
		// Tratamento do NIS
		try { nis = parseNIS(args[1]); exceptions.setNIS(Long.toString(nis)); }
		catch (FieldException exception) { exceptions.addException(exception); }
		
		// Tratamento do CPF
		try { cpf = parseCPF(args[7]); }
		catch (FieldException exception) { exceptions.addException(exception); }
		
		// Tratamento da Data de Nascimento
		dataNascimento = parseDate(args[2]);
		
		// Tratamento da Data de Emissão do RG
		dataRG  = parseDate(args[5]);
		
		// Tratamentos dos outros campos
		char sexo = parseSexo(args[3]);
		String rg = parseRG  (args[4]);
		
		// Se ocorreu alguma exceção, retorno a lista de erros
		if (exceptions.hasException())
			throw exceptions;
		
		// Senão retorno o candidato formatado
		return new Candidato(nome, nis, dataNascimento, sexo, rg, dataRG, emissorRG, cpf, nomeMae);
	}
	
	/** Remove caracteres especiais e espaços múltiplos entre nome e sobrenome */
	private static String parseNome(String nome, boolean eNomeMae) throws FieldException {
		
		nome = StringUtils.removeCaracteresEspeciais(nome);
		nome = StringUtils.removeEspacosMultiplos(nome);
		nome = nome.replace("'","");
		
		if (!StringUtils.isOnlyAlfaString(nome))
			throw (eNomeMae) ? new FieldException("Nome da mãe inválido",nome) : new FieldException("Nome inválido",nome);
			
		return nome;
	}
	
	/** Processa o órgão emissor */
	public static String parseOrgao(String orgaoEmissor) {
		
		orgaoEmissor = StringUtils.extraiAlfabeto(orgaoEmissor);
		
		// Força o retorno do órgão emissor 'SSP' em caso de erro, já que este não é um dado tão crítico
		if ((orgaoEmissor == null) || (orgaoEmissor.length() == 0) || (orgaoEmissor.length() > 10))
			return "SSP";
		
		return orgaoEmissor;
	}
	
	/** Processa o NIS */
	private static long parseNIS(String nis) throws FieldException {
		
		nis = StringUtils.extractNumbers(nis);
		
		if (nis.length() == 0)
			throw new FieldException("NIS inválido","-");
		
		if (nis.length() > 11)
			nis = nis.substring(0,11);
		
		return Long.parseLong(nis);
	}
	
	/** Processa o CPF */
	public static long parseCPF(String cpf) throws FieldException {
		
		cpf = StringUtils.extractNumbers(cpf);
		cpf = String.format("%011d",Long.parseLong(cpf));
		
		if (!CPFParser.parse(cpf))
			throw new FieldException("CPF inválido",cpf);
		
		return Long.parseLong(cpf);
	}
	
	/** Processa datas */
	private static DateTime parseDate(String date) {
		
		// Tratamento especial para datas processadas no Excel
		if (date.length() == 7)
			date = "0" + date;
		
		DateTime data = TimeParser.createDate(date);
		
		// Força o retorno da data '01/jan/2000' em caso de erro, já que este não é um dado tão crítico
		if (data == null)
			data = TimeParser.createDate("2000-01-01");
		
		return data;
	}
	
	/** Processa o sexo */
	private static char parseSexo(String sexo) {
		return sexo.charAt(0);
	}
	
	/** Processa o número de RG */
	public static String parseRG(String rg) {
		return StringUtils.extractNumbers(rg);
	}
	
}
