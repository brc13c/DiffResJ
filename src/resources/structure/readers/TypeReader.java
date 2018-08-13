package resources.structure.readers;
import datastructures.structure.StructureType;

import resources.locating.Resource;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.List;

/**
 * This will read types from a file.
 * @author Benjamin
 */
public abstract class TypeReader{
	
	/**
	 * This will provide a name to type mapping for the types in the given list.
	 * @param toWrap The types to generate a directory for.
	 * @return The name to type mapping.
	 */
	@SuppressWarnings("PMD.UseConcurrentHashMap")
	public Map<String, StructureType> wrapTypes(final List<StructureType> toWrap){
		final Map<String, StructureType> toRet = new HashMap<String, StructureType>();
		for(final StructureType s : toWrap){
			toRet.put(s.getName(), s);
		}
		return toRet;
	}
	
	/**
	 * This will read the types from a file and return them in the order they are encountered in the file.
	 * @param target The target to read data from.
	 * @return The read types.
	 * @throws IOException If there is a problem reading.
	 */
	public abstract List<StructureType> readTypes(Resource target) throws IOException;
}