package resources.locating;
import java.io.FileNotFoundException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This represents a resource in the classpath.
 * @author Benjamin
 */
public class ClasspathResource extends Resource{

	/**
	 * The containing folder.
	 */
	protected ClasspathCollection parent;
	
	/**
	 * The name of this resource.
	 */
	protected String name;
	
	/**
	 * The java path to this resource.
	 */
	protected String path;
	
	@Override
	public ResourceCollection getParent() throws IOException {
		return parent;
	}
	
	/**
	 * This allows access to resources on the classpath.
	 * @param parent The containing folder.
	 * @param name The name of this resource.
	 * @param path The java path to this resource.
	 */
	public ClasspathResource(final ClasspathCollection parent, final String name, final String path) {
		super();
		this.parent = parent;
		this.name = name;
		this.path = path;
	}
	
	@Override
	public String[] getPath() throws IOException {
		final String[] parentPath = parent.getPath();
		String[] fullPath = new String[parentPath.length + 1];
		System.arraycopy(parentPath, 0, fullPath, 0, parentPath.length);
		fullPath[fullPath.length-1] = name;
		return fullPath;
	}

	@Override
	public String getName() throws IOException {
		return name;
	}

	@Override
	public InputStream read() throws IOException {
		final URL descriptor = ClassLoader.getSystemResource(path);
		if(descriptor == null){
			throw new FileNotFoundException(path);
		}
		else{
			return descriptor.openStream();
		}
	}

	@Override
	public OutputStream write() throws IOException {
		throw new UnsupportedOperationException("Classpath is read only.");
	}

	@Override
	public void delete() throws IOException {
		throw new UnsupportedOperationException("Classpath is read only.");
	}
}