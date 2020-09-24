package compec.ufam.sistac.model;

import org.joda.time.*;
import com.phill.libs.time.*;

/** Classe que representa um candidato processado com sucesso pelo sistema. Se houve algum erro
 *  durante o processo de leitura e parse dos arquivos, o objeto criado será o 'RowParseException'
 *  @author Felipe André
 *  @version 2.50, 08/07/2018
 *  @see RowParseException */
public class Candidato {

	private final String nome,rg,emissorRG,nomeMae;
	private final long nis,cpf;
	private final DateTime dataNascimento,dataRG;
	private final char sexo;
	
	/** Máscara de String de acordo com o arquivo Sistac */
	private static final String FORMAT = "1;%s;%s;%s;%c;%s;%s;%s;%s;%s;";
	
	/** Construtor da classe inicializando os atributos */
	public Candidato(String nome, long nis, DateTime dataNascimento, char sexo, String rg, DateTime dataRG,
					 String emissorRG, long cpf, String nomeMae) {
		this.nome = nome;
		this.rg = rg;
		this.emissorRG = emissorRG;
		this.nomeMae = nomeMae;
		this.nis = nis;
		this.cpf = cpf;
		this.dataNascimento = dataNascimento;
		this.dataRG = dataRG;
		this.sexo = sexo;
	}

	/** Getter para o nome do candidato */
	public String getNome() {
		return nome;
	}

	/** Getter para o RG do candidato */
	public String getRG() {
		return rg;
	}

	/** Getter para o órgão emissor do RG do candidato */
	public String getEmissorRG() {
		return emissorRG;
	}

	/** Getter para o nome da mãe do candidato */
	public String getNomeMae() {
		return nomeMae;
	}

	/** Getter para o Número de Identificação Social (NIS) do candidato */
	public String getNIS() {
		return String.format("%011d",nis);
	}

	/** Getter para o CPF do candidato */
	public String getCPF() {
		return String.format("%011d",cpf);
	}

	/** Getter para a data de nascimento do candidato */
	public String getDataNascimento() {
		return PhillsDateParser.retrieveDate(dataNascimento, PhillsDateFormatter.RAW_DATE);
	}

	/** Getter para a data de emissão do RG do candidato */
	public String getDataRG() {
		return PhillsDateParser.retrieveDate(dataRG, PhillsDateFormatter.RAW_DATE);
	}

	/** Getter para o sexo do candidato */
	public char getSexo() {
		return sexo;
	}
	
	/** Retorna uma String com os dados organizados e preparados para escrita no arquivo texto de acordo com o formato do Sistac */
	public String getResume() {
		return String.format(FORMAT,nome,getNIS(),getDataNascimento(),sexo,rg,getDataRG(),emissorRG,getCPF(),nomeMae);
	}
	
}
