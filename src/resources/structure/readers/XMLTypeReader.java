package resources.structure.readers;
import datastructures.structure.StructureVariableDescription;
import datastructures.structure.CharacterVariableDescription;
import datastructures.structure.FloatVariableDescription;
import datastructures.structure.IntegerVariableDescription;
import datastructures.structure.BooleanVariableDescription;
import datastructures.primitives.ArrayListInt;
import datastructures.primitives.ListInt;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import exceptions.CompilationException;
import exceptions.ParsingException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;
import resources.locating.ResourceCollection;
import datastructures.structure.StructureType;
import java.io.IOException;
import java.util.List;
import resources.locating.Resource;

/**
 * This will read types from XML.
 * @author Benjamin
 */
@SuppressWarnings({"PMD.GodClass", "PMD.AvoidDuplicateLiterals"}) //xml parsing
public class XMLTypeReader extends TypeReader{
	
	/**
	 * The result of modding by eight for something divisible by eight.
	 */
	protected static final int DIVIDABLEBYEIGHT = 0;
	
	/**
	 * The first tag.
	 */
	protected static final String TYPES = "TYPES";
	
	/**
	 * The location to look for imports in.
	 */
	protected ResourceCollection root;
	
	/**
	 * This sets up a type reader that looks for imports in the given location.
	 * @param root The location to look for imports in.
	 */
	public XMLTypeReader(final ResourceCollection root) {
		super();
		this.root = root;
	}
	
	@Override
	public List<StructureType> readTypes(final Resource target) throws IOException {
		final List<StructureType> toRet = new ArrayList<StructureType>();
		//the zeroth element is always the empty structure type
		toRet.add(StructureType.EMPTYTYPE);
		//open the file
		final InputStream source = new BufferedInputStream(target.read());
		//read the file
		try {
			readTypes(source, root, toRet);
		} catch (XMLStreamException | ParsingException | CompilationException e) {
			throw new IOException(e);
		}
		source.close();
		//and report
		return toRet;
	}
	
	/**
	 * This will read the types from an XML source stream.
	 * @param source The stream to read from.
	 * @param workingArea The area to search for files in.
	 * @param drain The list to add types to.
	 * @throws IOException If there is a problem reading.
	 * @throws XMLStreamException If there is a problem reading the XML.
	 * @throws ParsingException If there is a problem.
	 * @throws CompilationException If there is a problem.
	 */
	protected void readTypes(final InputStream source, final ResourceCollection workingArea, final List<StructureType> drain) throws IOException, XMLStreamException, ParsingException, CompilationException{
		final XMLInputFactory factory = XMLInputFactory.newFactory();
		final XMLEventReader toReadXML = factory.createXMLEventReader(new InputStreamReader(source, "UTF-8"));
		
		//read through to the start of the document
		XMLEvent cur = toReadXML.nextEvent();
		while(cur.getEventType()!=XMLEvent.START_ELEMENT){
			//check for premature end of file
			if(cur.getEventType()==XMLEvent.END_DOCUMENT){
				//the end has been hit, so nothing in the document
				return;
			}
			cur = toReadXML.nextEvent();
		}
		
		cur = toReadXML.nextEvent();
		while(cur.getEventType()!=XMLEvent.END_DOCUMENT){
			//cur is a start, so handle processing based on its contents
			if(cur.getEventType()==XMLEvent.START_ELEMENT){
				final String elementName = cur.asStartElement().getName().getLocalPart();
				switch (elementName) {
				case "TYPE":
					final StructureType nextType = readType(toReadXML);
					boolean needAdd = true;
					//make sure there's not already a type of the same name
					for(final StructureType existing : drain){
						if(existing.getName().equals(nextType.getName())){
							if(existing.equals(nextType)){
								needAdd = false;
								break;
							}
							else{
								throw new CompilationException("Incompatible type definition for " + nextType.getName());
							}
						}
					}
					if(needAdd){
						drain.add(nextType);
					}
					break;
				case "IMPORT":
					readImports(toReadXML, workingArea, drain);
					break;
				default:
					readThroughUnknown(toReadXML);
					break;
				}
			}
			cur = toReadXML.nextEvent();
		}
		toReadXML.close();
	}
	
	/**
	 * This will read the types from another XML file as if they were defined in the file calling this method.
	 * @param toReadXML The XML file currently being read.
	 * @param workingArea The working area to search through for files.
	 * @param drain The list to add types to.
	 * @throws XMLStreamException If there is a problem reading XML.
	 * @throws IOException If there is a problem reading.
	 * @throws ParsingException If there is a problem reading.
	 * @throws CompilationException If there is a problem reading.
	 */
	protected void readImports(final XMLEventReader toReadXML, final ResourceCollection workingArea, final List<StructureType> drain) throws XMLStreamException, IOException, ParsingException, CompilationException{
		//get the name of the import
		final String filename = readThroughPrimitive(toReadXML);
		//parse the file name
		final String[] filePath = workingArea.stringToPath(filename);
		final Resource target = workingArea.getResource(filePath);
		if(target==null){
			throw new FileNotFoundException(filename);
		}
		final InputStream toImport = target.read();
		//read in the new
		readTypes(toImport, workingArea, drain);
	}
	
	/**
	 * This will read a type, assuming the TYPE tag has been consumed.
	 * @param toReadXML The XML file being read.
	 * @return The read type.
	 * @throws XMLStreamException If there is a problem reading.
	 * @throws IOException If there is a problem reading.
	 * @throws ParsingException If there is a problem reading.
	 * @throws CompilationException If there is a problem reading.
	 */
	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	protected StructureType readType(final XMLEventReader toReadXML) throws XMLStreamException, IOException, ParsingException, CompilationException{
		//set up empty data for the type
		String typeName = "";
		final List<String> booleanNames = new ArrayList<>();
		final ListInt booleanLengths = new ArrayListInt();
		final List<String> intNames = new ArrayList<>();
		final ListInt intDepths = new ArrayListInt();
		final ListInt intLengths = new ArrayListInt();
		final List<String> floatNames = new ArrayList<>();
		final ListInt floatMDepths = new ArrayListInt();
		final ListInt floatEDepths = new ArrayListInt();
		final ListInt floatLengths = new ArrayListInt();
		final List<String> charNames = new ArrayList<>();
		final ListInt charLengths = new ArrayListInt();
		final List<String> structNames = new ArrayList<>();
		final List<String> structTypes = new ArrayList<>();
		final ListInt structLengths = new ArrayListInt();
		final List<String> tags = new ArrayList<>();
		
		XMLEvent cur = toReadXML.nextEvent();
		while(cur.getEventType() != XMLEvent.END_ELEMENT){
			if(cur.getEventType()==XMLEvent.START_ELEMENT){
				final String elementName = cur.asStartElement().getName().getLocalPart();
				switch (elementName) {
				case "NAME":
					typeName = readThroughPrimitive(toReadXML);
					break;
				case "BOOLEAN":
					readThroughBoolean(toReadXML, booleanNames, booleanLengths);
					break;
				case "INTEGER":
					readThroughInteger(toReadXML, intNames, intDepths, intLengths);
					break;
				case "FLOAT":
					readThroughFloat(toReadXML, floatNames, floatMDepths, floatEDepths, floatLengths);
					break;
				case "CHARACTER":
					readThroughCharacter(toReadXML, charNames, charLengths);
					break;
				case "STRUCTURE":
					readThroughStructure(toReadXML, structNames, structTypes, structLengths);
					break;
				case "TAG":
					tags.add(readThroughPrimitive(toReadXML));
					break;
				default:
					readThroughUnknown(toReadXML);
					break;
				}
			}
			cur = toReadXML.nextEvent();
		}
		
		final BooleanVariableDescription booleanVars = new BooleanVariableDescription(booleanNames.toArray(new String[booleanNames.size()]), booleanLengths.toArray());
		final IntegerVariableDescription intVars = new IntegerVariableDescription(intNames.toArray(new String[intNames.size()]), intDepths.toArray(), intLengths.toArray());
		final FloatVariableDescription floatVars = new FloatVariableDescription(floatNames.toArray(new String[floatNames.size()]), floatMDepths.toArray(), floatEDepths.toArray(), floatLengths.toArray());
		final CharacterVariableDescription charVars = new CharacterVariableDescription(charNames.toArray(new String[charNames.size()]), charLengths.toArray());
		final StructureVariableDescription structVars = new StructureVariableDescription(structNames.toArray(new String[structNames.size()]), structLengths.toArray(), structTypes.toArray(new String[structTypes.size()]));
		return new StructureType(typeName, booleanVars, intVars, floatVars, charVars, structVars, tags.toArray(new String[tags.size()]));
	}
	
	/**
	 * This will read through a boolean variable.
	 * @param toReadXML The file being read.
	 * @param booleanNames The names of the types boolean variables.
	 * @param booleanLengths The lenghts of the type's boolean variables.
	 * @throws XMLStreamException If there is a problem reading.
	 */
	protected void readThroughBoolean(final XMLEventReader toReadXML, final List<String> booleanNames, final ListInt booleanLengths) throws XMLStreamException{
		String varName = "";
		String varLength = "-1";
		XMLEvent cur = toReadXML.nextEvent();
		while(cur.getEventType() != XMLEvent.END_ELEMENT){
			if(cur.getEventType()==XMLEvent.START_ELEMENT){
				final String elementName = cur.asStartElement().getName().getLocalPart();
				switch (elementName) {
				case "NAME":
					varName = readThroughPrimitive(toReadXML);
					break;
				case "LENGTH":
					varLength = readThroughPrimitive(toReadXML);
					break;
				default:
					readThroughUnknown(toReadXML);
					break;
				}
			}
			cur = toReadXML.nextEvent();
		}
		booleanNames.add(varName);
		booleanLengths.add(Integer.parseInt(varLength));
	}
	
	/**
	 * This will read through an integer variable.
	 * @param toReadXML The file being read.
	 * @param intNames The names of the types integer variables.
	 * @param intDepths The depths of the types integer variables.
	 * @param intLengths The lenghts of the type's integer variables.
	 * @throws XMLStreamException If there is a problem reading.
	 * @throws IOException If there is a problem with the type.
	 */
	protected void readThroughInteger(final XMLEventReader toReadXML, final List<String> intNames, final ListInt intDepths, final ListInt intLengths) throws XMLStreamException, IOException{
		String varName = "";
		String varDepth = "32";
		String varLength = "-1";
		XMLEvent cur = toReadXML.nextEvent();
		while(cur.getEventType() != XMLEvent.END_ELEMENT){
			if(cur.getEventType()==XMLEvent.START_ELEMENT){
				final String elementName = cur.asStartElement().getName().getLocalPart();
				switch (elementName) {
				case "NAME":
					varName = readThroughPrimitive(toReadXML);
					break;
				case "DEPTH":
					varDepth = readThroughPrimitive(toReadXML);
					break;
				case "LENGTH":
					varLength = readThroughPrimitive(toReadXML);
					break;
				default:
					readThroughUnknown(toReadXML);
					break;
				}
			}
			cur = toReadXML.nextEvent();
		}
		final int depth = Integer.parseInt(varDepth);
		if(depth % 8 != DIVIDABLEBYEIGHT){
			throw new IOException("Variable " + varName + " has a non-byte aligned bit depth.");
		}
		intNames.add(varName);
		intDepths.add(depth);
		intLengths.add(Integer.parseInt(varLength));
	}
	
	/**
	 * This will read through a float variable.
	 * @param toReadXML The file being read.
	 * @param floatNames The names of the types float variables.
	 * @param floatMDepths The mantissa depths of the types float variables.
	 * @param floatEDepths The exponent depths of the types float variables.
	 * @param floatLengths The lenghts of the type's float variables.
	 * @throws XMLStreamException If there is a problem reading.
	 */
	protected void readThroughFloat(final XMLEventReader toReadXML, final List<String> floatNames, final ListInt floatMDepths, final ListInt floatEDepths, final ListInt floatLengths) throws XMLStreamException{
		String varName = "";
		String varMDepth = "52";
		String varEDepth = "11";
		String varLength = "-1";
		XMLEvent cur = toReadXML.nextEvent();
		while(cur.getEventType() != XMLEvent.END_ELEMENT){
			if(cur.getEventType()==XMLEvent.START_ELEMENT){
				final String elementName = cur.asStartElement().getName().getLocalPart();
				switch (elementName) {
				case "NAME":
					varName = readThroughPrimitive(toReadXML);
					break;
				case "MANTISSADEPTH":
					varMDepth = readThroughPrimitive(toReadXML);
					break;
				case "EXPONENTDEPTH":
					varEDepth = readThroughPrimitive(toReadXML);
					break;
				case "LENGTH":
					varLength = readThroughPrimitive(toReadXML);
					break;
				default:
					readThroughUnknown(toReadXML);
					break;
				}
			}
			cur = toReadXML.nextEvent();
		}
		final int mdepth = Integer.parseInt(varMDepth);
		final int edepth = Integer.parseInt(varEDepth);
		if(mdepth>=0 && edepth>=0 && (mdepth + edepth + 1)%8 != 0){
			throw new IllegalArgumentException("Variable " + varName + " has a non-byte aligned bit depth.");
		}
		floatNames.add(varName);
		floatMDepths.add(mdepth);
		floatEDepths.add(edepth);
		floatLengths.add(Integer.parseInt(varLength));
	}
	
	/**
	 * This will read through a character variable.
	 * @param toReadXML The file being read.
	 * @param charNames The names of the types character variables.
	 * @param charLengths The lenghts of the type's character variables.
	 * @throws XMLStreamException If there is a problem reading.
	 */
	protected void readThroughCharacter(final XMLEventReader toReadXML, final List<String> charNames, final ListInt charLengths) throws XMLStreamException{
		String varName = "";
		String varLength = "-1";
		XMLEvent cur = toReadXML.nextEvent();
		while(cur.getEventType() != XMLEvent.END_ELEMENT){
			if(cur.getEventType()==XMLEvent.START_ELEMENT){
				final String elementName = cur.asStartElement().getName().getLocalPart();
				switch (elementName) {
				case "NAME":
					varName = readThroughPrimitive(toReadXML);
					break;
				case "LENGTH":
					varLength = readThroughPrimitive(toReadXML);
					break;
				default:
					readThroughUnknown(toReadXML);
					break;
				}
			}
			cur = toReadXML.nextEvent();
		}
		charNames.add(varName);
		charLengths.add(Integer.parseInt(varLength));
	}
	
	/**
	 * This will read through a structure variable.
	 * @param toReadXML The file being read.
	 * @param structNames The names of the types structure variables.
	 * @param structTypes The types of the type's structure variables.
	 * @param structLengths The lenghts of the type's structure variables.
	 * @throws XMLStreamException If there is a problem reading.
	 */
	protected void readThroughStructure(final XMLEventReader toReadXML, final List<String> structNames, final List<String> structTypes, final ListInt structLengths) throws XMLStreamException{
		String varName = "";
		String structType = "";
		String varLength = "-1";
		XMLEvent cur = toReadXML.nextEvent();
		while(cur.getEventType() != XMLEvent.END_ELEMENT){
			if(cur.getEventType()==XMLEvent.START_ELEMENT){
				final String elementName = cur.asStartElement().getName().getLocalPart();
				switch (elementName) {
				case "NAME":
					varName = readThroughPrimitive(toReadXML);
					break;
				case "TYPE":
					structType = readThroughPrimitive(toReadXML);
					break;
				case "LENGTH":
					varLength = readThroughPrimitive(toReadXML);
					break;
				default:
					readThroughUnknown(toReadXML);
					break;
				}
			}
			cur = toReadXML.nextEvent();
		}
		structNames.add(varName);
		structTypes.add(structType);
		structLengths.add(Integer.parseInt(varLength));
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
	
	/**
	 * This will flush through an unknown tag.
	 * @param toReadXML The xml file being read.
	 * @throws XMLStreamException if there is a problem reading.
	 */
	protected void readThroughUnknown(final XMLEventReader toReadXML) throws XMLStreamException{
		XMLEvent cur = toReadXML.nextEvent();
		while(cur.getEventType()!=XMLEvent.END_ELEMENT){
			cur = toReadXML.nextEvent();
		}
	}
}