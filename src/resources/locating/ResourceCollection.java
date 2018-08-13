package resources.locating;
import java.io.IOException;
import java.util.Iterator;

/**
 * This represents a collection of resources, addressable by name.
 * This generalizes file access to other interfaces (FTP for example).
 * @author Benjamin
 */
public abstract class ResourceCollection{
	
	/**
	 * This will return the parent collection of this collection.
	 * @return The parent of this collection (null if there is no parent).
	 * @throws IOException If there is a problem.
	 */
	public abstract ResourceCollection getParent() throws IOException;
	
	/**
	 * This returns the full path of this collection.
	 * @return The path of this collection, relative to the root.
	 * @throws IOException If there is a problem.
	 */
	public abstract String[] getPath() throws IOException;
	
	/**
	 * This returns the name of this collection (the last entry in the path).
	 * @return The name of this collection.
	 * @throws IOException If there is a problem
	 */
	public abstract String getName() throws IOException;
	
	/**
	 * This returns an iterator through the sub-collections of this collection.
	 * Note that this method may return an IOException; the iterator WILL NOT.
	 * @return An iterator through the sub-collections.
	 * @throws IOException If there is a problem.
	 */
	public abstract Iterator<ResourceCollection> getSubCollections() throws IOException;
	
	/**
	 * This will return an iterator through the resources of this collection.
	 * @return An iterator through the resources of this collection.
	 * @throws IOException If there is a problem.
	 */
	public abstract Iterator<Resource> getResources() throws IOException;
	
	/**
	 * This finds a sub collection by name (possibly many links down the hierarchy).
	 * @param relativePath The name of the collection relative to this. A zero length path means root (i.e. this).
	 * @return The requested collection, or null if it could not be found.
	 * @throws IOException If there is a problem.
	 */
	public abstract ResourceCollection getSubCollection(String[] relativePath) throws IOException;
	
	/**
	 * This finds a resource by name (possibly many links down the hierarchy).
	 * @param relativePath The name of the resource relative to this collection.
	 * @return The requested resource, or null if it could not be found.
	 * @throws IOException If there is a problem.
	 */
	public abstract Resource getResource(String[] relativePath) throws IOException;
	
	/**
	 * This creates a sub-collection in this collection.
	 * @param name The name of the new sub-collection.
	 * @return The created sub-collection.
	 * @throws IOException If there is a problem creating.
	 */
	public abstract ResourceCollection createSubCollection(String name) throws IOException;
	
	/**
	 * This creates a resource in this collection.
	 * @param name The name of the new resource.
	 * @return The created resource.
	 * @throws IOException If there is a problem creating.
	 */
	public abstract Resource createResource(String name) throws IOException;
	
	/**
	 * This deletes this collection. It is an error to interact with a collection after it has been deleted.
	 * @throws IOException If there is a problem.
	 */
	public abstract void delete() throws IOException;
	
	/**
	 * This will convert a path to a string.
	 * @param path The path.
	 * @return The string representation of the path.
	 */
	public String pathToString(final String[] path){
		final StringBuffer toBuild = new StringBuffer();
		for(final String level : path){
			toBuild.append('/');
			toBuild.append(level);
		}
		return toBuild.toString();
	}
	
	/**
	 * This will convert a string to a path.
	 * @param path The path to parse.
	 * @return The path.
	 */
	public String[] stringToPath(final String path){
		if(path.contains("/")){
			final String[] tokened = path.split("/");
			final String[] toRet = new String[tokened.length-1];
			System.arraycopy(tokened, 1, toRet, 0, toRet.length);
			return toRet;
		}
		else{
			return new String[]{path};
		}
	}
}