package resources.structure.readers;
import java.io.InputStream;
import java.io.IOException;

/**
 * This presents a class that can open files for reading.
 * @author Benjamin
 */
public abstract class DataReader{
	
	/**
	 * This will start the reading process for a structure file.
	 * @param toRead The file to prep.
	 * @return The input stream.
	 * @throws IOException If the file cannot be located.
	 */
	public abstract StructureInputStream openStructureFile(InputStream toRead) throws IOException;
}