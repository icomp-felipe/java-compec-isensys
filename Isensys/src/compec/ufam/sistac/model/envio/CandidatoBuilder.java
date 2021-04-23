package compec.ufam.sistac.model.envio;

import org.joda.time.*;
import com.phill.libs.*;
import com.phill.libs.br.*;
import com.phill.libs.time.*;
import compec.ufam.sistac.exception.*;

/** Classe que monta um {@link Candidato} e realiza uma série de validações de dados nos campos.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.5, 23/04/2021
 *  @see Candidato */
public class CandidatoBuilder {

	/** Monta um candidato de acordo com o vetor de 'dados'. Também realiza uma série de
	 *  validações nos dados, caso haja pelo menos uma inconsistência, uma exceção com
	 *  detalhes desta inconsistência é lançada, do contrário, o método conseguiu
	 *  construir um objeto respeitando todos os requisitos do Sistac.<br>
	 *  Nota: a ordem do vetor de dados deve ser SEMPRE esta:<br>
	 *  Nome, NIS, Data de Nascimento, Sexo, RG, Data de Emissão do RG, Órgão Emissor do RG, CPF, Nome da Mãe
	 *  @param linha - número da linha que está sendo processada
	 *  @param dados - vetor de dados de candidato extraídos de uma planilha (csv ou xlsx)
	 *  @return Um {@link Candidato} com todos os dados devidamente validados no formato Sistac.
	 *  @throws RowParseException caso algum dado viole os requisitos do Sistac. */
	public static Candidato parse(final int linha, final String[] dados) throws RowParseException {
		
		// Preparando a classe de exceções com (linha, nome, nis e cpf) respectivamente
		RowParseException exceptions = new RowParseException(linha, dados[0], dados[1], dados[7]);

		char sexo = ' ';
		String nome, nis, rg, orgaoEmissorRG, cpf, nomeMae;
		DateTime dataNascimento, dataEmissaoRG;
		
		// Inicialização das Variáveis
		nome = nomeMae = nis = rg = cpf = null;
		
		/************************* Validação de Dados ******************************/
		
		// Tratamento do Nome
		try { nome = parseNome(dados[0], false); exceptions.setNome(nome); }
		catch (FieldException exception) { exceptions.addException(exception); }
		
		// Tratamento do NIS
		try { nis = parseNIS(dados[1]); exceptions.setNIS(nis); }
		catch (FieldException exception) { exceptions.addException(exception); }
		
		// Tratamento da Data de Nascimento
		dataNascimento = parseData(dados[2]);
		
		// Tratamento do Sexo
		try { sexo = parseSexo(dados[3]); }
		catch (FieldException exception) { exceptions.addException(exception); }
		
		// Tratamento do número de RG
		try { rg = parseRG(dados[4]); }
		catch (FieldException exception) { exceptions.addException(exception); }
		
		// Tratamento da Data de Emissão do RG
		dataEmissaoRG  = parseData(dados[5]);
		
		// Tratamento do Órgão Emissor do RG
		orgaoEmissorRG = parseOrgao(dados[6]);
		
		// Tratamento do número de CPF
		try { cpf = parseCPF(dados[7]); }
		catch (FieldException exception) { exceptions.addException(exception); }
		
		// Tratamento do Nome da Mãe
		try { nomeMae = parseNome(dados[8], true); }
		catch (FieldException exception) { exceptions.addException(exception); }
		
		/******************** Fim da Validação de Dados ****************************/
		
		// Se ocorreu alguma exceção, retorno a lista de erros
		if (exceptions.hasException())
			throw exceptions;
		
		// Senão retorno o objeto candidato
		return new Candidato(nome, nis, dataNascimento, sexo, rg, dataEmissaoRG, orgaoEmissorRG, cpf, nomeMae);
	}
	
	/*************************** Bloco de Validadores de Dados *********************************/
	
	/** Remove caracteres especiais e espaços múltiplos entre nome e sobrenome.
	 *  @param nome - nome do solicitante ou nome da mãe
	 *  @param isNomeMae - indica se o nome recebido é do solicitante 'false' ou da mãe 'true'
	 *  @return Um nome válido no formato Sistac.
	 *  @throws FieldException quando, mesmo após os tratamentos, o nome continua violando o formato Sistac. */
	private static String parseNome(String nome, final boolean isNomeMae) throws FieldException {
		
		nome = StringUtils.wipeSpecialCharacters(nome);
		nome = StringUtils.extractAlphabet      (nome, " ", false);
		nome = StringUtils.wipeMultipleSpaces   (nome);
		
		if (!StringUtils.isAlphaStringOnly(nome))
			throw (isNomeMae) ? new FieldException("Nome da mãe inválido", nome) : new FieldException("Nome inválido", nome);
		
		if (nome.length() > 100)
			throw (isNomeMae) ? new FieldException("Nome da mãe contém mais de 100 caracteres", nome) : new FieldException("Nome contém mais de 100 caracteres", nome);
		
		return nome;
	}
	
	/** Extrai apenas os 11 primeiros dígitos do NIS.
	 *  @param nis - número de identificação social (NIS)
	 *  @return Um número de NIS válido no formato Sistac.
	 *  @throws FieldException apenas se o NIS for vazio. */
	private static String parseNIS(String nis) throws FieldException {
		
		nis = StringUtils.extractNumbers(nis);
		
		if (nis.isEmpty())
			throw new FieldException("NIS inválido","-");
		
		if (nis.length() > 11)
			nis = nis.substring(0,11);
		
		return String.format("%011d", Long.parseLong(nis));
	}
	
	/** Verifica e formata datas.
	 *  @param data - data com ou sem máscara
	 *  @return Um {@link DateTime} com a <code>data</code> informada. */
	private static DateTime parseData(String data) {
		
		// Tratamento especial para datas processadas no Excel
		if (data.length() == 7)
			data = "0" + data;
		
		DateTime dateTime = PhillsDateParser.createDate(data);
		
		// Força o retorno da data '01/jan/2000' em caso de erro, já que este não é um dado tão crítico
		if (dateTime == null)
			dateTime = PhillsDateParser.createDate("2000-01-01");
		
		return dateTime;
	}
	
	/** Verifica o sexo.
	 *  @param sexo - sexo no formato 'M' ou 'F'
	 *  @return Um char contendo 'M' para masculino ou 'F' para feminino.
	 *  @throws FieldException caso o sexo seja diferente de 'M' ou 'F'. */
	private static char parseSexo(final String sexo) throws FieldException {
		
		char sexoChar = sexo.isEmpty() ? ' ' : sexo.charAt(0);
		
		if ((sexoChar != 'M') && (sexoChar != 'F'))
			throw new FieldException("Sexo inválido ou não informado", Character.toString(sexoChar));
		
		return sexoChar;
	}
	
	/** Verifica o número de RG.
	 *  @param rg - número de RG
	 *  @return Um número de RG válido no formato Sistac.
	 *  @throws FieldException caso o RG seja vazio ou tenha mais de 16 caracteres */
	public static String parseRG(String rg) throws FieldException {
		
		rg = StringUtils.extractAlphaNumeric(rg, "", true);
		
		if (rg.isEmpty() || (rg.length() > 16))
			throw new FieldException("RG inválido",rg);
		
		return rg;
	}
	
	/** Extrai apenas os dígitos alfanuméricos do órgão emissor do RG.
	 *  @param orgaoEmissor - órgão emissor do RG
	 *  @return Um órgão emissor válido no formato Sistac. */
	public static String parseOrgao(String orgaoEmissor) {
		
		orgaoEmissor = StringUtils.extractAlphabet(orgaoEmissor, "", true);
		
		// Força o retorno do órgão emissor 'SSP' em caso de erro, já que este não é um dado tão crítico
		if (orgaoEmissor.isEmpty() || (orgaoEmissor.length() > 30))
			return "SSP";
		
		return orgaoEmissor;
	}
	
	/** Verifica e formata o número de CPF.
	 *  @param cpf - número de CPF
	 *  @return Um número de CPF válido no formato Sistac.
	 *  @throws FieldException  */
	public static String parseCPF(String cpf) throws FieldException {
		
		cpf = StringUtils.extractNumbers(cpf);
		cpf = String.format("%011d", Long.parseLong(cpf));
		
		if (!CPFParser.parse(cpf))
			throw new FieldException("CPF inválido", cpf);
		
		return cpf;
	}
	
}