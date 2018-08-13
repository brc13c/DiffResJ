package resources.structure.readers;
import java.nio.charset.StandardCharsets;

import datastructures.structure.StructureVariableDescription;
import datastructures.structure.CharacterVariableDescription;
import datastructures.structure.FloatVariableDescription;
import datastructures.structure.IntegerVariableDescription;
import datastructures.structure.BooleanVariableDescription;
import mathlibrary.number.ArbitraryInteger;
import mathlibrary.number.ArbitraryFloat;
import java.util.Arrays;
import exceptions.CompilationException;
import java.io.EOFException;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import datastructures.structure.Structure;
import datastructures.structure.StructureType;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;

/**
 * This will read XML files corresponding to a given type system.
 * @author Benjamin
 */
public class XMLDataReader extends DataReader{
	
	/**
	 * The types of entries to read from XML files.
	 */
	protected Map<String, StructureType> types;
	
	/**
	 * This will create a reader for XML files of a given type system.
	 * @param types The types of entries to read from XML files.
	 */
	public XMLDataReader(final Map<String, StructureType> types){
		super();
		this.types = types;
	}
	
	@Override
	public StructureInputStream openStructureFile(final InputStream toRead) throws IOException {
		try {
			return new XMLDataInputStream(toRead, types);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}
}

/**
 * This will present the contents of an XML file as a stream of structures.
 * @author Benjamin
 */
@SuppressWarnings("PMD.GodClass")  //fucking primitives
class XMLDataInputStream extends StructureInputStream{
	
	/**
	 * The minimum valid index for a variable.
	 */
	protected static final int MINVALIDVARIND = 0;
	
	/**
	 * The minimum value for which lengths make sense.
	 */
	protected static final int SPECIFIEDLENGTHLIMIT = 0;
	
	/**
	 * The empty string.
	 */
	protected static final String EMPTYSTRING = "";
	
	/**
	 * The zero integer to use for uninitialized values.
	 */
	protected final ArbitraryInteger INTEGERZERO = new ArbitraryInteger((byte)0);
	
	/**
	 * The zero float to use for uninitialized values.
	 */
	protected final ArbitraryFloat FLOATZERO = new ArbitraryFloat(0);
	
	/**
	 * The empty structure to use for uninitialized values.
	 */
	protected final Structure STRUCTZERO = new Structure(StructureType.EMPTYTYPE);
	
	/**
	 * The input stream to read from.
	 */
	protected InputStream source;
	
	/**
	 * The types of entries to read from XML files.
	 */
	protected Map<String, StructureType> types;
	
	/**
	 * The xml file to read.
	 */
	protected XMLEventReader toReadXML;
	
	/**
	 * Whether the end of the stream has been reached.
	 */
	protected boolean endOfStream;
	
	/**
	 * This will set up the XML file for reading.
	 * @param source The input stream to read from.
	 * @param types The types of entries to read from XML files.
	 * @throws XMLStreamException If there is a problem setting up.
	 * @throws UnsupportedEncodingException If there is a problem setting up.
	 */
	public XMLDataInputStream(final InputStream source, final Map<String, StructureType> types) throws UnsupportedEncodingException, XMLStreamException {
		super();
		this.source = new BufferedInputStream(source);
		this.types = types;
		endOfStream = false;
		
		final XMLInputFactory factory = XMLInputFactory.newFactory();
		toReadXML = factory.createXMLEventReader(new InputStreamReader(this.source, "UTF-8"));
		
		readToRoot();
	}
	
	@Override
	public Structure readNextEntry() throws IOException {
		try {
			return readNextEntryWrap();
		} catch (XMLStreamException | NumberFormatException | CompilationException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * This will read the next entry. This encapsulates any xml errors.
	 * @return The next entry.
	 * @throws XMLStreamException If there is a problem reading.
	 * @throws IOException If there is a problem reading.
	 * @throws CompilationException If there is a problem reading the file.
	 * @throws NumberFormatException If there is a problem reading the file.
	 */
	public Structure readNextEntryWrap() throws XMLStreamException, IOException, NumberFormatException, CompilationException{
		//check that the end hasn't been hit
		if(endOfStream){
			throw new EOFException("Overran the end of the file.");
		}
		//burn through elements until a start of a known type is reached
		XMLEvent next = toReadXML.nextEvent();
		StructureType type = null;
		while(type == null){
			while(next.getEventType() != XMLEvent.START_ELEMENT){
				if(next.getEventType()==XMLEvent.END_DOCUMENT || next.getEventType() == XMLEvent.END_ELEMENT){
					//hit close of document, or of root
					endOfStream = true;
					return null;
				}
				next = toReadXML.nextEvent();
			}
			//get the type to read
			final String typeName = next.asStartElement().getName().getLocalPart();
			type = types.get(typeName);
			if(type == null){
				throw new IOException("Unknown type: " + typeName + " at line " + next.getLocation().getLineNumber() + " column " + next.getLocation().getColumnNumber());
			}
		}
		return readStruct(type);
	}
	
	/**
	 * This will read a structure from the file once its type tag has been consumed.
	 * @param type The type of structure to read.
	 * @return The read structure.
	 * @throws XMLStreamException If there is a problem reading the file.
	 * @throws IOException If there is a problem reading the file.
	 * @throws CompilationException If there is a problem reading the file.
	 * @throws NumberFormatException If there is a problem reading the file.
	 */
	protected Structure readStruct(final StructureType type) throws XMLStreamException, IOException, NumberFormatException, CompilationException{
		//build the structure
		final Structure toBuild = new Structure(type);
		//read everything until an end element is reached
		XMLEvent next = toReadXML.nextEvent();
		while(next.getEventType()!=XMLEvent.END_ELEMENT){
			if(next.getEventType()==XMLEvent.START_ELEMENT){
				final String varName = next.asStartElement().getName().getLocalPart();
				readVariable(toBuild, varName);
			}
			next = toReadXML.nextEvent();
		}
		//if the lengths are too short, extend
		enforceLengths(toBuild, type);
		return toBuild;
	}
	
	/**
	 * This will enforce any length restrictions on the incoming data.
	 * @param toBuild The structure being built.
	 * @param type The type of structure being built.
	 * @throws IOException If there is a problem with the lengths.
	 */
	protected void enforceLengths(final Structure toBuild, final StructureType type) throws IOException{
		final BooleanVariableDescription boolVars = type.getBooleans();
		for(int i = 0; i<toBuild.booleanVals.length; i++){
			final int varLength = boolVars.getBooleanVariableLength(i);
			if(varLength >= SPECIFIEDLENGTHLIMIT){
				if(toBuild.booleanVals[i].length > varLength){
					throw new IOException("Too many booleans for variable " + boolVars.getBooleanVariableName(i));
				}
				else if(toBuild.booleanVals[i].length < varLength){
					toBuild.booleanVals[i] = Arrays.copyOf(toBuild.booleanVals[i], varLength);
				}
			}
		}
		
		final IntegerVariableDescription intVars = type.getIntegers();
		for(int i = 0; i<toBuild.integerVals.length; i++){
			final int varLength = intVars.getIntegerVariableLength(i);
			if(varLength >= SPECIFIEDLENGTHLIMIT){
				if(toBuild.integerVals[i].length > varLength){
					throw new IOException("Too many integers for variable " + intVars.getIntegerVariableName(i));
				}
				else if(toBuild.integerVals[i].length < varLength){
					final int fromIndex = toBuild.integerVals[i].length;
					toBuild.integerVals[i] = Arrays.copyOf(toBuild.integerVals[i], varLength);
					Arrays.fill(toBuild.integerVals[i], fromIndex, varLength, INTEGERZERO);
				}
			}
		}
		
		final FloatVariableDescription floatVars = type.getFloats();
		for(int i = 0; i<toBuild.floatVals.length; i++){
			final int varLength = floatVars.getFloatVariableLength(i);
			if(varLength >= SPECIFIEDLENGTHLIMIT){
				if(toBuild.floatVals[i].length > varLength){
					throw new IOException("Too many floats for variable " + floatVars.getFloatVariableName(i));
				}
				else if(toBuild.floatVals[i].length < varLength){
					final int fromIndex = toBuild.floatVals[i].length;
					toBuild.floatVals[i] = Arrays.copyOf(toBuild.floatVals[i], varLength);
					Arrays.fill(toBuild.floatVals[i], fromIndex, varLength, FLOATZERO);
				}
			}
		}
		
		final CharacterVariableDescription charVars = type.getCharacters();
		for(int i = 0; i<toBuild.characterVals.length; i++){
			final int varLength = charVars.getStringVariableLength(i);
			if(varLength >= SPECIFIEDLENGTHLIMIT){
				if(toBuild.characterVals[i].length > varLength){
					throw new IOException("Too many strings for variable " + charVars.getStringVariableName(i));
				}
				else if(toBuild.characterVals[i].length < varLength){
					toBuild.characterVals[i] = Arrays.copyOf(toBuild.characterVals[i], varLength);
				}
			}
		}
		
		final StructureVariableDescription structVars = type.getStructures();
		for(int i = 0; i<toBuild.structVals.length; i++){
			final int varLength = structVars.getStructureVariableLength(i);
			if(varLength >= SPECIFIEDLENGTHLIMIT){
				if(toBuild.structVals[i].length > varLength){
					throw new IOException("Too many structures for variable " + structVars.getStructureVariableName(i));
				}
				else if(toBuild.structVals[i].length < varLength){
					if(!EMPTYSTRING.equals(structVars.getStructureVariableType(i))){
						throw new IOException("Too few structures for variable " + structVars.getStructureVariableName(i));
					}
					final int fromIndex = toBuild.structVals[i].length;
					toBuild.structVals[i] = Arrays.copyOf(toBuild.structVals[i], varLength);
					Arrays.fill(toBuild.structVals[i], fromIndex, varLength, STRUCTZERO);
				}
			}
		}
	}
	
	/**
	 * This will read a variable from an XML file.
	 * @param beingBuilt The structure to read the variable for.
	 * @param variableName The name of the variable to read.
	 * @throws NumberFormatException If there is a problem reading.
	 * @throws XMLStreamException If there is a problem reading.
	 * @throws IOException If there is a problem reading.
	 * @throws CompilationException If there is a problem reading.
	 */
	protected void readVariable(final Structure beingBuilt, final String variableName) throws NumberFormatException, XMLStreamException, IOException, CompilationException{
		final StructureType type = beingBuilt.getType();
		if(type.getBooleans().getBooleanVariableIndex(variableName)>=MINVALIDVARIND){
			final int index = type.getBooleans().getBooleanVariableIndex(variableName);
			final boolean value = Boolean.parseBoolean(readThroughPrimitive(toReadXML).trim());
			beingBuilt.booleanVals[index] = Arrays.copyOf(beingBuilt.booleanVals[index], beingBuilt.booleanVals[index].length+1);
			beingBuilt.booleanVals[index][beingBuilt.booleanVals[index].length-1] = value;
		}
		else if(type.getIntegers().getIntegerVariableIndex(variableName)>=MINVALIDVARIND){
			final int index = type.getIntegers().getIntegerVariableIndex(variableName);
			final ArbitraryInteger value = new ArbitraryInteger(Long.parseLong(readThroughPrimitive(toReadXML).trim()));
			beingBuilt.integerVals[index] = Arrays.copyOf(beingBuilt.integerVals[index], beingBuilt.integerVals[index].length+1);
			beingBuilt.integerVals[index][beingBuilt.integerVals[index].length-1] = value;
		}
		else if(type.getFloats().getFloatVariableIndex(variableName)>=MINVALIDVARIND){
			final int index = type.getFloats().getFloatVariableIndex(variableName);
			final ArbitraryFloat value = new ArbitraryFloat(Double.parseDouble(readThroughPrimitive(toReadXML).trim()));
			beingBuilt.floatVals[index] = Arrays.copyOf(beingBuilt.floatVals[index], beingBuilt.floatVals[index].length+1);
			beingBuilt.floatVals[index][beingBuilt.floatVals[index].length-1] = value;
		}
		else if(type.getCharacters().getStringVariableIndex(variableName)>=MINVALIDVARIND){
			final int index = type.getCharacters().getStringVariableIndex(variableName);
			final byte[] toAdd = readThroughPrimitive(toReadXML).getBytes(StandardCharsets.UTF_8);
			final int startIndex = beingBuilt.characterVals[index].length;
			beingBuilt.characterVals[index] = Arrays.copyOf(beingBuilt.characterVals[index], beingBuilt.characterVals[index].length+toAdd.length);
			System.arraycopy(toAdd, 0, beingBuilt.characterVals[index], startIndex, toAdd.length);
		}
		else if(type.getStructures().getStructureVariableIndex(variableName)>=MINVALIDVARIND){
			final int index = type.getStructures().getStructureVariableIndex(variableName);
			final String expectedType = type.getStructures().getStructureVariableType(index);
			final Structure value;
			if("".equals(expectedType)){
				value = readNextEntryWrap();
				//burn through the close tag
				XMLEvent closer = toReadXML.nextEvent();
				while(closer.getEventType() != XMLEvent.END_ELEMENT){
					closer = toReadXML.nextEvent();
				}
			}
			else{
				final StructureType nextType = types.get(expectedType);
				if(nextType == null){
					throw new IOException("Unknown type: " + expectedType);
				}
				value = readStruct(nextType);
			}
			beingBuilt.structVals[index] = Arrays.copyOf(beingBuilt.structVals[index], beingBuilt.structVals[index].length+1);
			beingBuilt.structVals[index][beingBuilt.structVals[index].length-1] = value;
		}
		else{
			throw new CompilationException("Invalid variable " + variableName);
		}
	}
	
	@Override
	public void close() throws IOException {
		try {
			toReadXML.close();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
		source.close();
	}
	
	/**
	 * This will consume the enries of the xml file until the root element is reached (the root has no bearing on a type driven xml file).
	 * @throws XMLStreamException If there is a problem reading.
	 */
	protected final void readToRoot() throws XMLStreamException{
		XMLEvent next = toReadXML.nextEvent();
		if(next.getEventType()==XMLEvent.END_DOCUMENT){
			endOfStream = true;
			return;
		}
		while(next.getEventType()!=XMLEvent.START_ELEMENT){
			next = toReadXML.nextEvent();
			if(next.getEventType()==XMLEvent.END_DOCUMENT){
				endOfStream = true;
				return;
			}
		}
	}
	
	/**
	 * This will read a string from between xml tags (assuming the opening tag has already been consumed).
	 * @param toReadXML The xml file being read.
	 * @return The string representation of the primitive.
	 * @throws XMLStreamException If there is a problem reading the primitive.
	 */
	protected String readThroughPrimitive(final XMLEventReader toReadXML) throws XMLStreamException{
		XMLEvent next = toReadXML.nextEvent();
		while(next.getEventType() != XMLEvent.CHARACTERS){
			next = toReadXML.nextEvent();
		}
		final String toRet = next.asCharacters().getData();
		XMLEvent closer = toReadXML.nextEvent();
		while(closer.getEventType() != XMLEvent.END_ELEMENT){
			closer = toReadXML.nextEvent();
		}
		return toRet;
	}
}