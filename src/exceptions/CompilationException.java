package exceptions;

/**
 * This marks an error in compiling an abstract syntax tree to a locally useful description..
 * @author Benjamin
 */
public class CompilationException extends ASTException{
	
	/**
	 * Default ID.
	 */
	private static final long serialVersionUID = -4702120486147480968L;

	/**
	 * This will create an exception with the given message.
	 * @param message The message to give to the user.
	 */
	public CompilationException(final String message){
		super(message);
	}
}