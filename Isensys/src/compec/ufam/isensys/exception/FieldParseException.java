package compec.ufam.isensys.exception;

import compec.ufam.isensys.model.envio.*;

/** Classe especial de exceção utilizada pelos métodos 'parse()' da classe {@link CandidatoBuilder}.
 *  @author Felipe André - felipeandresouza@hotmail.com
 *  @version 3.5, 23/04/2021 */
public class FieldParseException extends Exception {

	// Serial de versionamento
	private static final long serialVersionUID = 3;

	/** Repassa ao construtor pai {@link Exception} uma String personalizada com o motivo de violação de requisitos do Sistac e o campo que violou tal requisito.
	 *  @param motivo - descrição da violação de requisitos
	 *  @param campoMotivo - campo que violou os requisitos do Sistac */
	public FieldParseException(final String motivo, final String campoMotivo) {
		super(String.format("%s (%s); ", motivo, campoMotivo));
	}
	
	/** Compara todos os atributos de um <code>object</code>, caso ele seja da classe {@link FieldParseException}, senão o método pai é chamado passando o objeto.
	 *  @param object - exceção de campo a ser comparada com esta instância da classe
	 *  @return 'true' apenas se todos os atributos são iguais;<br>'false' caso pelo menos um atributo seja diferente.
	 *  @since 3.5, 23/04/2021 */
	@Override
	public boolean equals(final Object object) {
		
		// Se for um 'FieldParseException', todos os seus atributos são verificados
		if (object instanceof FieldParseException) {
			
			FieldParseException exception = (FieldParseException) object;
			
			return this.getMessage().equals(exception.getMessage());
			
		}
		
		return super.equals(object);
	}
	
}