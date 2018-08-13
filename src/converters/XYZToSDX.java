package converters;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.EOFException;

/**
 * This will convert an xyz file to an sdx file (if velocity data is present, will carry over, otherwise velocities are zero).
 * @author Benjamin
 *
 */
public final class XYZToSDX{
	
	/**
	 * This will convert an XYZ file to an SDX file.
	 * @param args {XYZ file to read, sdx file to write, dimensions, timestep size}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String dstNam = null;
		String dimStr = null;
		String timStr = null;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-dim=")){dimStr = curArg.substring(5);}
			else if(curArg.startsWith("-tim=")){timStr = curArg.substring(5);}
		}
		if(dstNam == null || srcNam==null || dimStr==null || timStr==null){
			throw new IllegalArgumentException("Usage: java converters.XYZToSDX -src=source_file -dst=output_file -dim=dimensionality -tim=timestep");
		}
		BufferedReader input = new BufferedReader(new FileReader(srcNam));
		BufferedWriter output = new BufferedWriter(new FileWriter(dstNam));
		int dimensions = Integer.parseInt(dimStr);
		double timestep = Double.parseDouble(timStr);
		
		output.write("<TRAJECTORY>\n");
		
		int curTimestep = 0;
		while(true){
			//read the atom count line
			String acountS = input.readLine();
			if(acountS == null){
				break;
			}
			output.write("\t<TIME><NEWTIME>" + (curTimestep * timestep) + "</NEWTIME></TIME>\n");
			int acount = Integer.parseInt(acountS);
			
			//read the comment line
			String comLine = input.readLine();
			if(comLine == null){
				throw new EOFException("Unexpected end of file.");
			}
			
			//read the atoms
			for(int i = 0; i<acount; i++){
				String atomSpec = input.readLine();
				if(atomSpec == null){
					throw new EOFException("Unexpected end of file.");
				}
				String[] atomInfo = atomSpec.split("\\s+");
				if(atomInfo.length < (dimensions + 1)){
					throw new IOException("Broken atom info.");
				}
				output.write("\t<PARTICLE>");
				for(int j = 0; j<dimensions; j++){
					output.write("<POSITION>" + atomInfo[i+1] + "</POSITION>");
				}
				if(atomInfo.length < (2*dimensions + 1)){
					for(int j = 0; j<dimensions; j++){
						output.write("<VELOCITY>0.0</VELOCITY>");
					}
				}
				else{
					for(int j = 0; j<dimensions; j++){
						output.write("<VELOCITY>" + atomInfo[i+dimensions+1] + "</VELOCITY>");
					}
				}
				output.write("</PARTICLE>\n");
			}
			
			//update timestep
			curTimestep++;
		}
		
		output.write("</TRAJECTORY>\n");
		input.close();
		output.close();
	}
	
	private XYZToSDX(){
		//force pure static
	}
}