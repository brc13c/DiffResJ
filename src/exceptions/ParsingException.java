package exceptions;

/**
 * This marks an error in parsing an abstract syntax tree.
 * @author Benjamin
 */
public class ParsingException extends ASTException{
	
	/**
	 * Default ID.
	 */
	private static final long serialVersionUID = 5497898046082992957L;
	
	/**
	 * This will create an exception with the given message.
	 * @param message The message to give to the user.
	 */
	public ParsingException(final String message){
		super(message);
	}
}