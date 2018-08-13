package resources.structure.readers;
import datastructures.structure.Structure;
import java.io.IOException;

/**
 * This represents a source of structures.
 * @author Benjamin
 */
public abstract class StructureInputStream{
	
	/**
	 * The minimum meaningful length.
	 */
	protected static final int MINVALIDSIZE = 0;
	
	/**
	 * The empty string.
	 */
	protected static final String EMPTYSTRING = "";
	
	/**
	 * This will read the next entry from the file.
	 * @return The next entry, or null if the end of the file has been reached.
	 * @throws IOException If there is a problem reading the structure.
	 */
	public abstract Structure readNextEntry() throws IOException;
	
	/**
	 * Closes the file.
	 * @throws IOException If there is a problem.
	 */
	public abstract void close() throws IOException;
}