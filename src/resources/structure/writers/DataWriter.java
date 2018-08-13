package resources.structure.writers;
import java.io.OutputStream;
import java.io.IOException;

/**
 * This presents a class that can open files for writing.
 * @author Benjamin
 */
public abstract class DataWriter{
	
	/**
	 * This will start the writing process for a structure file.
	 * @param toWrite The underlying file to write to.
	 * @return A structured output stream.
	 * @throws IOException If the file cannot be located.
	 */
	public abstract StructureOutputStream prepStructureFile(OutputStream toWrite) throws IOException;
}