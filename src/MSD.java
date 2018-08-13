import diffusion.DiffusionFileStatistics;
import diffusion.CollectDiffusionFiles;
import msd.FitDiffusionCoefficient;
import msd.AverageMeanSquareDisplacement;
import msd.ResampleSquareDisplacement;
import msd.CalculateSquareDisplacement;
import resample.SortResamples;
import resample.GenerateResamples;
import java.io.IOException;
import java.io.File;
import converters.ConvertDiffSamplesToCSV;

/**
 * This will perform an all in one calculation to get mean square displacement information.
 * @author Benjamin
 */
public final class MSD{
	
	/**
	 * This will perform an all in one calculation to get mean square displacement information.
	 * @param args {binary trajectory file, working directory, output description file, number of resamples, maximum curvature, minimum signal to noise, dimension}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String wrkDir = null;
		String dstNam = null;
		String numStr = null;
		String dimStr = null;
		String curStr = null;
		String sigStr = null;
		String disNam = "";
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-wrk=")){wrkDir = curArg.substring(5);}
			else if(curArg.startsWith("-num=")){numStr = curArg.substring(5);}
			else if(curArg.startsWith("-dim=")){dimStr = curArg.substring(5);}
			else if(curArg.startsWith("-cur=")){curStr = curArg.substring(5);}
			else if(curArg.startsWith("-sig=")){sigStr = curArg.substring(5);}
			else if(curArg.startsWith("-adc=")){disNam = curArg.substring(5);}
		}
		if(dstNam == null || srcNam==null || wrkDir==null || numStr==null || dimStr==null || curStr==null || sigStr==null){
			throw new IllegalArgumentException("Usage: java MSD -src=trajectory -wrk=working_directory -dst=diffusion_summary -num=number_of_resamples -dim=data_dimensionality -cur=maximum_curvature -sig=minimum_signal_to_noise [-adc=diffusion_distribution_file]");
		}
		
		final File sourceFile = new File(srcNam);
		final File workingDir = new File(wrkDir);
		final File outputFile = new File(dstNam);
		final long numResamps = Long.parseLong(numStr);
		final double curvature = Double.parseDouble(curStr);
		final double signal = Double.parseDouble(sigStr);
		final int dimension = Integer.parseInt(dimStr);
		String distDest = disNam;
		
		final String sourceFileName = sourceFile.getAbsolutePath();
		final String workingDirName = workingDir.getAbsolutePath();
		final String outputFileName = outputFile.getAbsolutePath();
		
		//calculate square displacement
		CalculateSquareDisplacement.dataDim = dimension;
		CalculateSquareDisplacement.main(new String[]{"-src="+sourceFileName, "-dst="+workingDirName + File.separatorChar + "SquareDisp.sd.sdb", "-wrk="+workingDirName + File.separatorChar + "working"});
		
		//generate resamples
		String collectedName = runResamples(0, numResamps-1, 0, sourceFileName, workingDirName, curvature, signal, dimension);
		File squareDispFile = new File(workingDirName + File.separatorChar + "SquareDisp.sd.sdb");
		squareDispFile.delete();
		
		//get results
		DiffusionFileStatistics.main(new String[]{"-src="+collectedName, "-dst="+outputFileName, "-xml"});
		if(!("".equals(distDest))){
			ConvertDiffSamplesToCSV.main(new String[]{"-src="+collectedName, "-dst="+distDest});
		}
		File coldiffFile = new File(collectedName);
		coldiffFile.delete();
	}
	

	
	/**
	 * This will run all the resamples in the given range.
	 * @param lowResamp The lowest resample to run (inclusive).
	 * @param highResamp The highest resample to run (inclusive).
	 * @param depth The current recursion depth.
	 * @param sourceFileName The name of the source data file.
	 * @param workingDirName The directory to put working files in.
	 * @param curvature The maximum curvature to accept.
	 * @param signal The minimum signal to noise ratio to allow.
	 * @param dimension The number of dimensions the data occupies.
	 * @return The name of the file created by this method.
	 * @throws IOException If there is a problem reading or writing.
	 */
	protected static String runResamples(long lowResamp, long highResamp, long depth, String sourceFileName, String workingDirName, double curvature, double signal, int dimension) throws IOException{
		if(lowResamp == highResamp){
			//run on the current resample only
			GenerateResamples.main(new String[]{"-for="+sourceFileName, "-nam="+workingDirName + File.separatorChar + "Resamp", "-num=1"});
			
			SortResamples.main(new String[]{"-spr="+workingDirName + File.separatorChar + "Resamp", "-low=0", "-hig=0", "-dpr="+workingDirName + File.separatorChar + "SortedResamp", "-wk1="+workingDirName + File.separatorChar + "working1", "-wk2="+workingDirName + File.separatorChar + "working2"});
			File rndresampFile = new File(workingDirName + File.separatorChar + "Resamp0.rsm.sdb");
			rndresampFile.delete();
			
			ResampleSquareDisplacement.main(new String[]{"-src="+workingDirName + File.separatorChar + "SquareDisp.sd.sdb", "-rsi="+workingDirName + File.separatorChar + "SortedResamp0.srs.sdb", "-rso="+workingDirName + File.separatorChar + "ResampDisp.sd.sdb"});
			File sortresampFile = new File(workingDirName + File.separatorChar + "SortedResamp0.srs.sdb");
			sortresampFile.delete();
			
			AverageMeanSquareDisplacement.main(new String[]{"-src="+workingDirName + File.separatorChar + "ResampDisp.sd.sdb", "-dst="+workingDirName + File.separatorChar + "ResampDisp.msd.sdb"});
			File resampdispFile = new File(workingDirName + File.separatorChar + "ResampDisp.sd.sdb");
			resampdispFile.delete();
			
			String fileName = workingDirName + File.separatorChar + "DifRnd" + depth + "Cyc" + lowResamp + ".dif.sdb";
			FitDiffusionCoefficient.main(new String[]{"-cur="+Double.toString(curvature), "-sig="+Double.toString(signal), "-dim="+Integer.toString(dimension), "-dst="+fileName, "-src="+workingDirName + File.separatorChar + "ResampDisp.msd.sdb"});
			File msdFile = new File(workingDirName + File.separatorChar + "ResampDisp.msd.sdb");
			msdFile.delete();
			
			return fileName;
		}
		else{
			//run each resample individually
			long midResamp = ((highResamp - lowResamp)/2) + lowResamp;
			String first = runResamples(lowResamp, midResamp, depth+1, sourceFileName, workingDirName, curvature, signal, dimension);
			String second = runResamples(midResamp+1, highResamp, depth+1, sourceFileName, workingDirName, curvature, signal, dimension);
			//then collect and delete
			String newName = workingDirName + File.separatorChar + "DifRnd" + depth + "Cyc" + lowResamp + ".dif.sdb";
			CollectDiffusionFiles.main(new String[]{"-dst="+newName, "-src="+first, "-src="+second});
			File firstFile = new File(first);
			File secondFile = new File(second);
			firstFile.delete();
			secondFile.delete();
			return newName;
		}
	}
	
	private MSD(){
		//force pure static
	}
}