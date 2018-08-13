import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintStream;
import java.util.Random;

/**
 * This generates data for the toy model.
 * @author Benjamin
 */
public final class GenToyData{
	
	/**
	 * This generates data for the toy reflection model.
	 * @param args {number of reflections, number of particles, seed, output file <.mdp.sdx>}
	 * @throws FileNotFoundException If there is a problem.
	 */
	public static void main(String[] args) throws FileNotFoundException{
		final double delT = 0.1;
		
		final int numSteps = Integer.parseInt(args[0]);
		final int numParts = Integer.parseInt(args[1]);
		final Random rand = new Random(Long.parseLong(args[2]));
		final PrintStream log = new PrintStream(new File(args[3]));
		
		log.println("<TRAJECTORY>");
		
		double time = 0;
		double[] partPos = new double[numParts];
		double[] partVel = new double[numParts];
		for(int i = 0; i<numParts; i++){
			partPos[i] = 0;
			partVel[i] = 1;
		}
		
		for(int n = 0; n<numSteps; n++){
			for(int t = 0; t<10; t++){
				printTraj(log, time, partPos, partVel);
				
				time += delT;
				for(int i = 0; i<numParts; i++){
					partPos[i] = partPos[i] + partVel[i] * delT;
				}
			}
			
			for(int i = 0; i<numParts; i++){
				partVel[i] = rand.nextBoolean() ? 1 : -1;
			}
		}
		
		log.println("</TRAJECTORY>");
		log.close();
	}
	
	/**
	 * This will write the current trajectory data.
	 * @param log The location to write the data to.
	 * @param time The current simulation time.
	 * @param positions THe positions of the particles.
	 * @param velocities The velocities of the particles.
	 */
	private static void printTraj(PrintStream log, double time, double[] positions, double[] velocities){
		log.println("	<TIME>");
		log.println("		<NEWTIME>" + time + "</NEWTIME>");
		log.println("	</TIME>");
		
		for(int i = 0; i<positions.length; i++){
			log.println("	<PARTICLE>");
			log.println("		<POSITION>" + positions[i] + "</POSITION>");
			log.println("		<VELOCITY>" + velocities[i] + "</VELOCITY>");
			log.println("	</PARTICLE>");
		}
	}
	
	private GenToyData(){
		//force pure static
	}
}