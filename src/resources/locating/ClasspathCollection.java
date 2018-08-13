package resources.locating;
import datastructures.array.listwrap.ArrayIterator;
import java.io.IOException;
import java.util.Iterator;

/**
 * This allows getting resources from the classpath.
 * This does not support searching through the classpath for files (if you don't know what you want, don't bother).
 * @author Benjamin
 *
 */
public class ClasspathCollection extends ResourceCollection{
	
	/**
	 * The length of a path to represent the root.
	 */
	protected static final int ROOTSIZE = 0;
	
	/**
	 * The length of a path to represent a file.
	 */
	protected static final int LEAFSIZE = 1;
	
	/**
	 * The length of an empty path.
	 */
	protected static final int NOPREV = 0;
	
	/**
	 * The parent of this collection.
	 */
	protected ResourceCollection parent;
	
	/**
	 * The name of this collection.
	 */
	protected String name;
	
	/**
	 * The path this collection points to.
	 */
	protected String prevPath;
	
	/**
	 * This sets up access to resources on the classpath.
	 * @param parent The parent of this collection.
	 * @param prevPath The path to this collection (for the full classpath, should be the empty string).
	 * @param name The name of this collection.
	 */
	public ClasspathCollection(final ResourceCollection parent, final String prevPath, final String name){
		super();
		this.parent = parent;
		this.prevPath = prevPath;
		this.name = name;
	}
	
	@Override
	public ResourceCollection getParent() throws IOException {
		return parent;
	}
	
	@Override
	public String[] getPath() throws IOException {
		if(parent==null){
			return new String[]{name};
		}
		else{
			final String[] parentPath = parent.getPath();
			String[] fullPath = new String[parentPath.length + 1];
			System.arraycopy(parentPath, 0, fullPath, 0, parentPath.length);
			fullPath[fullPath.length-1] = name;
			return fullPath;
		}
	}
	
	@Override
	public String getName() throws IOException {
		return name;
	}
	
	@Override
	public Iterator<ResourceCollection> getSubCollections() throws IOException {
		//this will not allow probing the classpath
		return new ArrayIterator<>(new ResourceCollection[0]);
	}
	
	@Override
	public Iterator<Resource> getResources() throws IOException {
		//this will not allow probing the classpath
		return new ArrayIterator<>(new Resource[0]);
	}
	
	@Override
	public ResourceCollection getSubCollection(final String[] relativePath) throws IOException {
		if(relativePath.length == ROOTSIZE){
			return this;
		}
		String nextPath;
		if(prevPath.length()==NOPREV){
			nextPath = relativePath[0];
		}
		else{
			nextPath = prevPath + "/" + relativePath[0];
		}
		final ResourceCollection subCol = new ClasspathCollection(this, nextPath, relativePath[0]);
		final String[] nextSearch = new String[relativePath.length-1];
		System.arraycopy(relativePath, 1, nextSearch, 0, nextSearch.length);
		return subCol.getSubCollection(nextSearch);
	}
	
	@Override
	public Resource getResource(final String[] relativePath) throws IOException {
		if(relativePath.length > LEAFSIZE){
			final String[] subColPath = new String[relativePath.length-1];
			System.arraycopy(relativePath, 0, subColPath, 0, subColPath.length);
			final ResourceCollection subCol = getSubCollection(subColPath);
			return subCol.getResource(new String[]{relativePath[relativePath.length-1]});
		}
		else if(relativePath.length == LEAFSIZE){
			String fullPath;
			if(prevPath.length()==NOPREV){
				fullPath = relativePath[0];
			}
			else{
				fullPath = prevPath + "/" + relativePath[0];
			}
			return new ClasspathResource(this, relativePath[0], fullPath);
		}
		else{
			return null;
		}
	}
	
	@Override
	public ResourceCollection createSubCollection(final String name) throws IOException {
		throw new UnsupportedOperationException("Classpath is read only.");
	}
	
	@Override
	public Resource createResource(final String name) throws IOException {
		throw new UnsupportedOperationException("Classpath is read only.");
	}
	
	@Override
	public void delete() throws IOException {
		throw new UnsupportedOperationException("Classpath is read only.");
	}
}