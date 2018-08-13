package resources.structure.writers;
import datastructures.structure.StructureVariableDescription;
import datastructures.structure.CharacterVariableDescription;
import datastructures.structure.FloatVariableDescription;
import datastructures.structure.IntegerVariableDescription;
import datastructures.structure.BooleanVariableDescription;
import datastructures.structure.StructureType;
import datastructures.structure.Structure;
import java.io.BufferedOutputStream;
import java.nio.charset.StandardCharsets;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.OutputStream;
import java.io.IOException;

/**
 * This will write XML files corresponding to a given type system.
 * @author Benjamin
 */
public class XMLDataWriter extends DataWriter{
	
	/**
	 * The root element for files written with this writer.
	 */
	protected String rootName;
	
	/**
	 * This sets up a XML writer.
	 * @param rootName The root element for files written with this writer.
	 */
	public XMLDataWriter(final String rootName) {
		super();
		this.rootName = rootName;
	}
	
	@Override
	public StructureOutputStream prepStructureFile(final OutputStream toWrite) throws IOException {
		return new XMLDataOutputStream(toWrite, rootName);
	}
}

/**
 * This will write a stream of structures as an XML file.
 * @author Benjamin
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals") //lots of text
class XMLDataOutputStream extends StructureOutputStream{
	
	/**
	 * The file to write structures to.
	 */
	protected Writer write;
	
	/**
	 * The name of the root element.
	 */
	protected String rootName;
	
	/**
	 * This will prep an XML data file.
	 * @param writeLoc The file to write to.
	 * @param rootName The name of the root element.
	 * @throws IOException If there is a problem setting up.
	 */
	public XMLDataOutputStream(final OutputStream writeLoc, final String rootName) throws IOException{
		super();
		this.write = new OutputStreamWriter(new BufferedOutputStream(writeLoc), StandardCharsets.UTF_8);
		this.rootName = rootName;
		
		write.write("<" + rootName + ">\n");
	}
	
	@Override
	public void writeNextEntry(final Structure toWrite) throws IOException {
		writeNextEntry(toWrite, "	");
	}
	
	/**
	 * This will write a structure in XML, tabbed over a given amount.
	 * @param toWrite The structure to write.
	 * @param tabs The amount of space to tab the entry over.
	 * @throws IOException If there is a problem writing.
	 */
	public void writeNextEntry(final Structure toWrite, final String tabs) throws IOException {
		final StructureType type = toWrite.getType();
		
		write.write(tabs + "<" + type.getName() + ">\n");
		
		//check that the type is consistent
		checkType(toWrite, type);
		
		writeEntryData(toWrite, tabs);
		
		write.write(tabs + "</" + type.getName() + ">\n");
	}
	
	/**
	 * This will write the data of a structure, without writing the type name.
	 * @param toWrite The structure to write.
	 * @param tabs The current indentation.
	 * @throws IOException If there is a problem writing.
	 */
	protected void writeEntryData(final Structure toWrite, final String tabs) throws IOException {
		final StructureType type = toWrite.getType();
		
		//write data
		final BooleanVariableDescription boolVars = type.getBooleans();
		for(int i = 0; i<toWrite.booleanVals.length; i++){
			final String varName = boolVars.getBooleanVariableName(i);
			for(int j = 0; j<toWrite.booleanVals[i].length; j++){
				final String val = toWrite.booleanVals[i][j] ? "True" : "False";
				write.write(tabs + "	<" + varName + ">" + val + "</" + varName + ">\n");
			}
		}
		
		final IntegerVariableDescription intVars = type.getIntegers();
		for(int i = 0; i<toWrite.integerVals.length; i++){
			final String varName = intVars.getIntegerVariableName(i);
			for(int j = 0; j<toWrite.integerVals[i].length; j++){
				final String val = Long.toString(toWrite.integerVals[i][j].longValue());
				write.write(tabs + "	<" + varName + ">" + val + "</" + varName + ">\n");
			}
		}
		
		final FloatVariableDescription floatVars = type.getFloats();
		for(int i = 0; i<toWrite.floatVals.length; i++){
			final String varName = floatVars.getFloatVariableName(i);
			for(int j = 0; j<toWrite.floatVals[i].length; j++){
				final String val = Double.toString(toWrite.floatVals[i][j].doubleValue());
				write.write(tabs + "	<" + varName + ">" + val + "</" + varName + ">\n");
			}
		}
		
		final CharacterVariableDescription charVars = type.getCharacters();
		for(int i = 0; i<toWrite.characterVals.length; i++){
			final String varName = charVars.getStringVariableName(i);
			@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") //immutable
			final String val = new String(toWrite.characterVals[i], StandardCharsets.UTF_8);
			write.write(tabs + "	<" + varName + ">" + val + "</" + varName + ">\n");
		}
		
		final StructureVariableDescription structVars = type.getStructures();
		for(int i = 0; i<toWrite.structVals.length; i++){
			final String varName = structVars.getStructureVariableName(i);
			for(int j = 0; j<toWrite.structVals[i].length; j++){
				write.write(tabs + "	<" + varName + ">\n");
				if("".equals(structVars.getStructureVariableType(i))){
					writeNextEntry(toWrite.structVals[i][j], tabs + "		");
				}
				else{
					writeEntryData(toWrite.structVals[i][j], tabs + "	");
				}
				write.write(tabs + "	</" + varName + ">\n");
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		write.write("</" + rootName + ">");
		write.close();
	}
}