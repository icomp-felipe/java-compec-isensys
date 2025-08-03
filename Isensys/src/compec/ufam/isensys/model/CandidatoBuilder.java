package compec.ufam.isensys.model;

import java.time.LocalDate;

import com.phill.libs.StringUtils;

import compec.ufam.isensys.constants.Constants;

/** Contém métodos de extração e adequação de dados do candidato solicitante.
 *  @author Felipe André - felipeandre.eng@gmail.com
 *  @version 4.0, 02/AGO/2025 */
public class CandidatoBuilder {

	/** Extrai os dados do array para um objeto {@link Candidato}.
	 *  @param dados - array contendo os dados dispostos na seguinte ordem [nome, CPF, data de nascimento]
	 *  @return {@link Candidato} com os dados do array informado via parâmetro. */
	public static Candidato build(final String[] dados) {
		return new Candidato(parseNome(dados[0]),
							 parseCPF (dados[1]),
							 parseData(dados[2]));
	}
	
	/*************************** Bloco de Extratores de Dados **********************************/
	
	/** Remove caracteres especiais e espaços múltiplos entre nome e sobrenome.
	 *  @param nome - nome do candidato solicitante
	 *  @return Nome do candidato solicitante, após formatação. */
	private static String parseNome(String nome) {
		
		nome = StringUtils.wipeSpecialCharacters(nome);
		nome = StringUtils.extractAlphabet      (nome, " ", false);
		nome = StringUtils.wipeMultipleSpaces   (nome);
		
		return nome.toUpperCase();
	}
	
	/** Verifica e trata o número de CPF.
	 *  @param cpf - número de CPF
	 *  @return Número de CPF do candidato solicitante, após formatação. */
	public static String parseCPF(String cpf) {
		
		cpf = StringUtils.extractNumbers(cpf);
		
		if (cpf == null || cpf.isEmpty())
			return null;
		
		// Formatando CPF
		return String.format("%011d", Long.parseLong(cpf));
		
	}
	
	/** Verifica e formata datas.
	 *  @param data - data com ou sem máscara no formato brasileiro (dd/MM/aaaa)
	 *  @return Um objeto {@link LocalDate} contendo a <code>data</code> ou a data '01/01/2000' caso o parâmetro seja nulo ou inválido. */
	private static LocalDate parseData(final String data) {
		
		try {
			
			// Tratamento para datas lidas do Excel, via Apache POI
			if (data.length() == 7)
				return LocalDate.parse(data, Constants.DateFormatters.EXCEL_DATE);
			
			// Tratamento para datas lidas dos arquivos do Sistac
			if (data.length() == 8)
				return LocalDate.parse(data, Constants.DateFormatters.SISTAC_DATE);
			
			// Tratamento para as datas com máscara brasileira
			if (data.matches(".*/.*/.*"))
				return LocalDate.parse(data, Constants.DateFormatters.BRAZILIAN_DATE);
		
			return LocalDate.of(2000, 1, 1);
			
		}
		catch (Exception exception) {
			
			return LocalDate.of(2000, 1, 1);
			
		}
		
	}
	
}
