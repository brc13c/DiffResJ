package resources.locating;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This represents a resource (file) that may be used.
 * @author Benjamin
 */
public abstract class Resource{
	
	/**
	 * This returns the collection this resource is a part of.
	 * @return This resource's parent.
	 * @throws IOException If there is a problem.
	 */
	public abstract ResourceCollection getParent() throws IOException;
	
	/**
	 * This gets the full path of this resource (relative to root).
	 * @return The full path of this resource (relative to root).
	 * @throws IOException If there is a problem.
	 */
	public abstract String[] getPath() throws IOException;
	
	/**
	 * This returns the name of this resource (the last entry in the path).
	 * @return The name of this resource.
	 * @throws IOException If there is a problem
	 */
	public abstract String getName() throws IOException;
	
	/**
	 * This opens this resource for reading.
	 * @return The stream to read from.
	 * @throws IOException If there is a problem opening the resource.
	 */
	public abstract InputStream read() throws IOException;
	
	/**
	 * This opens this resource for writing.
	 * @return The stream to write to.
	 * @throws IOException If there is a problem opening the resource.
	 */
	public abstract OutputStream write() throws IOException;
	
	/**
	 * This deletes the resource.
	 * @throws IOException If there is a problem.
	 */
	public abstract void delete() throws IOException;
}