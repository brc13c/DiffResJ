package exceptions;

/**
 * This marks an error in handling an abstract syntax tree.
 * @author Benjamin
 */
public class ASTException extends Exception{
	
	/**
	 * Default ID.
	 */
	private static final long serialVersionUID = -7320513891930685189L;

	/**
	 * This will create an exception with the given message.
	 * @param message The message to give to the user.
	 */
	public ASTException(final String message){
		super(message);
	}
}