package resources.structure.writers;
import datastructures.structure.StructureVariableDescription;
import datastructures.structure.CharacterVariableDescription;
import datastructures.structure.FloatVariableDescription;
import datastructures.structure.IntegerVariableDescription;
import datastructures.structure.BooleanVariableDescription;
import datastructures.structure.StructureType;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * This will write types as binary.
 * @author Benjamin
 */
public class BinaryTypeWriter extends TypeWriter{
	
	@Override
	public void writeTypes(final OutputStream toWriteTo, final List<StructureType> toWrite) throws IOException {
		final DataOutputStream drain = new DataOutputStream(new BufferedOutputStream(toWriteTo));
		
		//write the version of type writer used
		drain.writeInt(0);
		
		//write the number of types (excluding zero)
		drain.writeInt(toWrite.size() - 1);
		
		//now write the types, skipping type zero
		for(int i = 1; i<toWrite.size(); i++){
			final StructureType cur = toWrite.get(i);
			drain.writeUTF(cur.getName());
			
			final BooleanVariableDescription boolVars = cur.getBooleans();
			drain.writeShort(boolVars.getNumberOfBooleans());
			for(int j = 0; j<boolVars.getNumberOfBooleans(); j++){
				drain.writeUTF(boolVars.getBooleanVariableName(j));
				drain.writeShort(boolVars.getBooleanVariableLength(j));
			}
			
			final IntegerVariableDescription intVars = cur.getIntegers();
			drain.writeShort(intVars.getNumberOfIntegers());
			for(int j = 0; j<intVars.getNumberOfIntegers(); j++){
				drain.writeUTF(intVars.getIntegerVariableName(j));
				drain.writeShort(intVars.getIntegerVariableDepth(j));
				drain.writeShort(intVars.getIntegerVariableLength(j));
			}
			
			final FloatVariableDescription floatVars = cur.getFloats();
			drain.writeShort(floatVars.getNumberOfFloats());
			for(int j = 0; j<floatVars.getNumberOfFloats(); j++){
				drain.writeUTF(floatVars.getFloatVariableName(j));
				drain.writeShort(floatVars.getFloatVariableMantissaDepth(j));
				drain.writeShort(floatVars.getFloatVariableExponentDepth(j));
				drain.writeShort(floatVars.getFloatVariableLength(j));
			}
			
			final CharacterVariableDescription charVars = cur.getCharacters();
			drain.writeShort(charVars.getNumberOfStrings());
			for(int j = 0; j<charVars.getNumberOfStrings(); j++){
				drain.writeUTF(charVars.getStringVariableName(j));
				drain.writeShort(charVars.getStringVariableLength(j));
			}
			
			final StructureVariableDescription structVars = cur.getStructures();
			drain.writeShort(structVars.getNumberOfStructures());
			for(int j = 0; j<structVars.getNumberOfStructures(); j++){
				drain.writeUTF(structVars.getStructureVariableName(j));
				drain.writeUTF(structVars.getStructureVariableType(j));
				drain.writeShort(structVars.getStructureVariableLength(j));
			}
			
			drain.writeShort(cur.numberOfTags());
			for(int j = 0; j<cur.numberOfTags(); j++){
				drain.writeUTF(cur.getTag(j));
			}
		}
		
		drain.close();
	}
}