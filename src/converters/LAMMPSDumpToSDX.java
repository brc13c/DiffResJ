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

/**
 * This will convert a lammps dump file (which outputs atom data in the order {id type xu yu zu vx vy vz}) to an sdx file.
 * @author Benjamin
 *
 */
public final class LAMMPSDumpToSDX{
	
	/**
	 * This will convert a lamps dump file to an SDX file.
	 * @param args {lammps dump file to read, sdx file to write, dimensions, timestep size}
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
			throw new IllegalArgumentException("Usage: java converters.LAMMPSDumpToSDX -src=source_file -dst=output_file -dim=dimensionality -tim=timestep");
		}
		
		BufferedReader input = new BufferedReader(new FileReader(srcNam));
		BufferedWriter output = new BufferedWriter(new FileWriter(dstNam));
		int dimensions = Integer.parseInt(dimStr);
		double timestep = Double.parseDouble(timStr);
		
		output.write("<TRAJECTORY>\n");
		
		List<PartDat> curParts = new ArrayList<>();
		PartComp comp = new PartComp();
		String cur = input.readLine();
		while(cur != null){
			if(cur.startsWith("ITEM: TIMESTEP")){
				String timestepNumber = input.readLine();
				double curTime = timestep * Integer.parseInt(timestepNumber);
				output.write("\t<TIME><NEWTIME>" + curTime + "</NEWTIME></TIME>\n");
				cur = input.readLine();
			}
			else if(cur.startsWith("ITEM: ATOMS")){
				curParts.clear();
				String partDat = input.readLine();
				while(partDat != null && !partDat.startsWith("ITEM:")){
					//try to parse; if can't parse, skip
					String[] parsed = partDat.trim().split("\\s+");
					partDat = input.readLine();
					if(parsed.length < (2*dimensions + 2)){
						continue;
					}
					int partID = Integer.parseInt(parsed[0]);
					double[] partLoc = new double[dimensions];
					double[] partVel = new double[dimensions];
					for(int i = 0; i<dimensions; i++){
						partLoc[i] = Double.parseDouble(parsed[2+i]);
						partVel[i] = Double.parseDouble(parsed[2+dimensions+i]);
					}
					PartDat curPart = new PartDat(partID, partLoc, partVel);
					curParts.add(curPart);
				}
				//sort the particles
				Collections.sort(curParts, comp);
				//write the particles
				for(PartDat curP : curParts){
					output.write("\t<PARTICLE>");
					for(int i = 0; i<curP.location.length; i++){
						output.write("<POSITION>" + curP.location[i] + "</POSITION>");
					}
					for(int i = 0; i<curP.velocity.length; i++){
						output.write("<VELOCITY>" + curP.velocity[i] + "</VELOCITY>");
					}
					output.write("</PARTICLE>\n");
				}
				cur = partDat;
			}
			else{
				//skip anything you don't understand
				cur = input.readLine();
			}
		}
		
		output.write("</TRAJECTORY>\n");
		input.close();
		output.close();
	}
	
	private LAMMPSDumpToSDX(){
		//force pure static
	}
}

/**
 * Quick storage for particle data.
 * @author Benjamin
 */
class PartDat{
	/**
	 * The identity of the particle.
	 */
	public int ident;
	/**
	 * The location of the particle.
	 */
	public double[] location;
	/**
	 * The velocity of the particle.
	 */
	public double[] velocity;
	/**
	 * This simply initialized particle data.
	 * @param ident The identity of the particle.
	 * @param loc The location of the particle.
	 * @param vel The velocity of the particle.
	 */
	public PartDat(int ident, double[] loc, double[] vel){
		this.ident = ident;
		this.location = loc;
		this.velocity = vel;
	}
}

/**
 * This compares two particles by identity for sorting.
 * @author Benjamin
 */
class PartComp implements Comparator<PartDat>{
	@Override
	public int compare(PartDat o1, PartDat o2) {
		return o1.ident - o2.ident;
	}
}