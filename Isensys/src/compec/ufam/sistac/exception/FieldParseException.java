package compec.ufam.sistac.exception;

public class FieldException extends Exception {

	private static final long serialVersionUID = 1L;

	public FieldException(String motivo, String campoMotivo) {
		super(String.format("%s (%s);",motivo,campoMotivo));
	}
	
}
