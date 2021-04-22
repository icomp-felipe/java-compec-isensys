package compec.ufam.sistac.model.envio;

import org.joda.time.*;
import com.phill.libs.time.*;
import compec.ufam.sistac.constants.*;

/** Classe que representa um candidato processado com sucesso pelo sistema. Se houve algum erro
 *  durante o processo de leitura e parse dos arquivos, o objeto criado será o 'RowParseException'
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.0, 21/04/2021
 *  @see CandidatoBuilder */
public class Candidato {

	private final String nome, nis, rg, orgaoEmissorRG, cpf, nomeMae;
	private final DateTime dataNascimento, dataEmissaoRG;
	private final char sexo;
	
	/** Construtor da classe inicializando os atributos. */
	protected Candidato(final String nome, final String nis, final DateTime dataNascimento, final char sexo, final String rg,
			            final DateTime dataEmissaoRG, final String orgaoEmissorRG, final String cpf, final String nomeMae) {
		
		this.nome = nome;
		this.nis  = nis;
		this.sexo = sexo;
		this.rg   = rg;
		this.cpf  = cpf;
		this.nomeMae = nomeMae;
		this.dataNascimento = dataNascimento;
		this.dataEmissaoRG  = dataEmissaoRG;
		this.orgaoEmissorRG = orgaoEmissorRG;
		
	}

	/** Getter para a data de nascimento do candidato.
	 *  @return Uma string com a data de nascimento do candidato no formato Sistac (ddMMyyyy). */
	public String getDataNascimento() {
		return PhillsDateParser.retrieveDate(this.dataNascimento, PhillsDateFormatter.RAW_DATE);
	}
	
	/** Getter para a data de emissão do RG do candidato.
	 *  @return Uma string com a data de emissão do RG do candidato no formato Sistac (ddMMyyyy). */
	public String getDataEmissaoRG() {
		return PhillsDateParser.retrieveDate(this.dataEmissaoRG, PhillsDateFormatter.RAW_DATE);
	}
	
	/** Retorna uma String com os dados organizados e preparados para escrita no arquivo texto de acordo com o formato do Sistac.
	 *  @return Uma string com os dados desta classe no formato Sistac. */
	public String getResume() {
		return String.format(Constants.StringFormat.ROW_DATA_FORMAT,
				             this.nome, this.nis, getDataNascimento(), this.sexo, this.rg, getDataEmissaoRG(), this.orgaoEmissorRG, this.cpf, this.nomeMae);
	}
	
	/** Comparador de nome de candidato. Útil para métodos de ordenação.
	 *  @param candidato - candidato a ser comparado com esta instância
	 *  @since 3.0, 21/04/2021 */
	public int compareTo(final Candidato candidato) {
		return this.nome.compareTo(candidato.nome);
	}
	
}