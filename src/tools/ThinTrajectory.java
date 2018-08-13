package tools;
import datastructures.structure.Structure;
import resources.structure.writers.StructureOutputStream;
import resources.structure.writers.BinaryDataWriter;
import resources.structure.readers.StructureInputStream;
import resources.structure.readers.BinaryDataReader;
import datastructures.structure.StructureType;
import resources.structure.readers.XMLTypeReader;
import resources.locating.ClasspathCollection;
import resources.locating.ResourceCollection;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * This will thin a trajectory to only have a fraction of its timesteps.
 * @author Benjamin
 */
public final class ThinTrajectory{
	
	/**
	 * This takes an sdb file, and outputs every nth timestep (starting at zero).
	 * @param args {name of the file to thin, output file, timestep period}
	 * @throws IOException If there is a problem reading or writing the files.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String dstNam = null;
		String skpStr = null;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-skp=")){skpStr = curArg.substring(5);}
		}
		if(dstNam == null || srcNam==null || skpStr == null){
			throw new IllegalArgumentException("Usage: java tools.ThinTrajectory -src=input_file -dst=output_file -skp=timestep_skip");
		}
		//parse arguments
		String inputFile = srcNam;
		String outputFile = dstNam;
		int period = Integer.parseInt(skpStr);
		
		//read in the type definitions
		ResourceCollection classpath = new ClasspathCollection(null, "", "classpath");
		String[] typeFileName = new String[]{"typedefs", "TrajectoryData.stx"};
		XMLTypeReader typeRead = new XMLTypeReader(classpath);
		List<StructureType> particleTypes = typeRead.readTypes(classpath.getResource(typeFileName));
		
		//open the file
		BinaryDataReader inputManage = new BinaryDataReader(particleTypes);
		StructureInputStream input = inputManage.openStructureFile(new BufferedInputStream(new FileInputStream(inputFile)));
		
		//create the output files
		BinaryDataWriter outputManage = new BinaryDataWriter(particleTypes);
		StructureOutputStream output = outputManage.prepStructureFile(new BufferedOutputStream(new FileOutputStream(outputFile)));
		
		int curTime = 0;
		boolean currentlyWriting = false;
		Structure cur = input.readNextEntry();
		while(cur!=null){
			if("TIME".equals(cur.getType().getName())){
				currentlyWriting = (curTime == 0);
				curTime = (curTime + 1) % period;
				if(currentlyWriting){
					output.writeNextEntry(cur);
				}
			}
			else if("PARTICLE".equals(cur.getType().getName())){
				if(currentlyWriting){
					output.writeNextEntry(cur);
				}
			}
			//else skip anything I don't understand
			cur = input.readNextEntry();
		}
		
		input.close();
		output.close();
	}
	
	private ThinTrajectory(){
		//force pure static
	}
}