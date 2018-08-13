package resources.structure.writers;
import datastructures.structure.StructureVariableDescription;
import datastructures.structure.CharacterVariableDescription;
import datastructures.structure.FloatVariableDescription;
import datastructures.structure.IntegerVariableDescription;
import datastructures.structure.BooleanVariableDescription;
import java.nio.charset.StandardCharsets;
import java.io.OutputStreamWriter;
import java.io.Writer;
import datastructures.structure.StructureType;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * This will write types as XML.
 * @author Benjamin
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals") //output to text file
public class XMLTypeWriter extends TypeWriter{

	@Override
	public void writeTypes(final OutputStream toWriteTo, final List<StructureType> toWrite) throws IOException {
		final Writer outFile = new OutputStreamWriter(toWriteTo, StandardCharsets.UTF_8);
		
		outFile.write("<TYPES>\n");
		
		for(final StructureType cur : toWrite){
			outFile.write("	<TYPE>\n");
			
			outFile.write("		<NAME>" + cur.getName() + "</NAME>\n");
			
			final BooleanVariableDescription boolVars = cur.getBooleans();
			for(int i = 0; i<boolVars.getNumberOfBooleans(); i++){
				outFile.write("		<BOOLEAN>\n");
				outFile.write("			<NAME>" + boolVars.getBooleanVariableName(i) + "</NAME>\n");
				outFile.write("			<LENGTH>" + boolVars.getBooleanVariableLength(i) + "</LENGTH>\n");
				outFile.write("		</BOOLEAN>\n");
			}
			
			final IntegerVariableDescription intVars = cur.getIntegers();
			for(int i = 0; i<intVars.getNumberOfIntegers(); i++){
				outFile.write("		<INTEGER>\n");
				outFile.write("			<NAME>" + intVars.getIntegerVariableName(i) + "</NAME>\n");
				outFile.write("			<DEPTH>" + intVars.getIntegerVariableDepth(i) +"</DEPTH>\n");
				outFile.write("			<LENGTH>" + intVars.getIntegerVariableLength(i) + "</LENGTH>\n");
				outFile.write("		</INTEGER>\n");
			}
			
			final FloatVariableDescription floatVars = cur.getFloats();
			for(int i = 0; i<floatVars.getNumberOfFloats(); i++){
				outFile.write("		<FLOAT>\n");
				outFile.write("			<NAME>" + floatVars.getFloatVariableName(i) + "</NAME>\n");
				outFile.write("			<MANTISSADEPTH>" + floatVars.getFloatVariableMantissaDepth(i) + "</MANTISSADEPTH>\n");
				outFile.write("			<EXPONENTDEPTH>" + floatVars.getFloatVariableExponentDepth(i) + "</EXPONENTDEPTH>\n");
				outFile.write("			<LENGTH>" + floatVars.getFloatVariableLength(i) + "</LENGTH>\n");
				outFile.write("		</FLOAT>\n");
			}
			
			final CharacterVariableDescription charVars = cur.getCharacters();
			for(int i = 0; i<charVars.getNumberOfStrings(); i++){
				outFile.write("		<CHARACTER>\n");
				outFile.write("			<NAME>" + charVars.getStringVariableName(i) + "</NAME>\n");
				outFile.write("			<LENGTH>" + charVars.getStringVariableLength(i) + "</LENGTH>\n");
				outFile.write("		</CHARACTER>\n");
			}
			
			final StructureVariableDescription structVars = cur.getStructures();
			for(int i = 0; i<structVars.getNumberOfStructures(); i++){
				outFile.write("		<STRUCTURE>\n");
				outFile.write("			<NAME>" + structVars.getStructureVariableName(i) + "</NAME>\n");
				outFile.write("			<TYPE>" + structVars.getStructureVariableType(i) + "</TYPE>\n");
				outFile.write("			<LENGTH>" + structVars.getStructureVariableLength(i) + "</LENGTH>\n");
				outFile.write("		</STRUCTURE>\n");
			}
			
			for(int i = 0; i<cur.numberOfTags(); i++){
				outFile.write("		<TAG>" + cur.getTag(i) + "</TAG>\n");
			}
			
			outFile.write("	</TYPE>\n");
		}

		outFile.write("</TYPES>");
		
		outFile.close();
	}
	
}