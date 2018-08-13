package resources.structure.writers;
import datastructures.structure.StructureType;
import java.io.IOException;
import java.util.List;
import java.io.OutputStream;

/**
 * This will write types to file.
 * @author Benjamin
 */
public abstract class TypeWriter{
	
	/**
	 * This will write the types to file.
	 * @param toWriteTo The file to write to.
	 * @param toWrite The types to write.
	 * @throws IOException If there is a problem writing.
	 */
	public abstract void writeTypes(OutputStream toWriteTo, List<StructureType> toWrite) throws IOException;
}