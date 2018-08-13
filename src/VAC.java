import vac.AverageParticleCoefficients;
import vac.ResampleParticleDiffusion;
import vac.IntegrateAutocorrelation;
import vac.CalculateAutocorrelation;
import diffusion.DiffusionFileStatistics;
import diffusion.CollectDiffusionFiles;
import resample.SortResamples;
import resample.GenerateResamples;
import java.io.IOException;
import java.io.File;
import converters.ConvertDiffSamplesToCSV;

/**
 * This will perform an all in one calculation to get velocity autocorrelation information.
 * @author Benjamin
 */
public final class VAC{
	
	/**
	 * This will perform an all in one calculation to get mean square displacement information.
	 * @param args {binary trajectory file, working directory, output description file, number of resamples, dimension}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String wrkDir = null;
		String dstNam = null;
		String numStr = null;
		String dimStr = null;
		String disNam = "";
		double varRatio = 0.0;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-wrk=")){wrkDir = curArg.substring(5);}
			else if(curArg.startsWith("-num=")){numStr = curArg.substring(5);}
			else if(curArg.startsWith("-dim=")){dimStr = curArg.substring(5);}
			else if(curArg.startsWith("-adc=")){disNam = curArg.substring(5);}
			else if(curArg.startsWith("-vcr=")){varRatio = Double.parseDouble(curArg.substring(5));}
		}
		if(dstNam == null || srcNam==null || wrkDir==null || numStr==null || dimStr==null){
			throw new IllegalArgumentException("Usage: java VAC -src=trajectory -wrk=working_directory -dst=diffusion_summary -num=number_of_resamples -dim=data_dimensionality [-adc=diffusion_distribution_file] [-vcr=variance_cutoff_ratio]");
		}
		
		final File sourceFile = new File(srcNam);
		final File workingDir = new File(wrkDir);
		final File outputFile = new File(dstNam);
		String distDest = disNam;
		
		final String sourceFileName = sourceFile.getAbsolutePath();
		final String workingDirName = workingDir.getAbsolutePath();
		final String outputFileName = outputFile.getAbsolutePath();
		
		//calculate autocorrelation
		CalculateAutocorrelation.dataDim = Integer.parseInt(dimStr);
		CalculateAutocorrelation.main(new String[]{"-src="+sourceFileName, "-dst="+workingDirName + File.separatorChar + "Autocor.vac.sdb", "-wrk="+workingDirName + File.separatorChar + "working"});
		
		//integrate autocorrelation
		IntegrateAutocorrelation.main(new String[]{"-src="+workingDirName + File.separatorChar + "Autocor.vac.sdb", "-dst="+workingDirName + File.separatorChar + "SingleDiff.ivc.sdb", "-dim="+dimStr, "-vcr="+varRatio});
		File autocorFile = new File(workingDirName + File.separatorChar + "Autocor.vac.sdb");
		autocorFile.delete();
		
		//resample
		long numResamps = Long.parseLong(numStr);
		String collectedDiffFileName = runResamples(0, numResamps-1, 0, sourceFileName, workingDirName);
		File integcorFile = new File(workingDirName + File.separatorChar + "SingleDiff.ivc.sdb");
		integcorFile.delete();
		
		//get results
		DiffusionFileStatistics.main(new String[]{"-src="+collectedDiffFileName, "-dst="+outputFileName, "-xml"});
		if(!("".equals(distDest))){
			ConvertDiffSamplesToCSV.main(new String[]{"-src="+collectedDiffFileName, "-dst="+distDest});
		}
		File coldiffFile = new File(collectedDiffFileName);
		coldiffFile.delete();
	}
	
	/**
	 * This will run all the resamples in the given range.
	 * @param lowResamp The lowest resample to run (inclusive).
	 * @param highResamp The highest resample to run (inclusive).
	 * @param depth The current recursion depth.
	 * @param sourceFileName The name of the source data file.
	 * @param workingDirName The directory to put working files in.
	 * @return The name of the file created by this method.
	 * @throws IOException If there is a problem reading or writing.
	 */
	protected static String runResamples(long lowResamp, long highResamp, long depth, String sourceFileName, String workingDirName) throws IOException{
		if(lowResamp == highResamp){
			//run on the current resample only
			GenerateResamples.main(new String[]{"-for="+sourceFileName, "-nam="+workingDirName + File.separatorChar + "Resamp", "-num=1"});
			
			SortResamples.main(new String[]{"-spr="+workingDirName + File.separatorChar + "Resamp", "-low=0", "-hig=0", "-dpr="+workingDirName + File.separatorChar + "SortedResamp", "-wk1="+workingDirName + File.separatorChar + "working1", "-wk2="+workingDirName + File.separatorChar + "working2"});
			File rndresampFile = new File(workingDirName + File.separatorChar + "Resamp0.rsm.sdb");
			rndresampFile.delete();
			
			ResampleParticleDiffusion.main(new String[]{"-src="+workingDirName + File.separatorChar + "SingleDiff.ivc.sdb", "-rsi="+workingDirName + File.separatorChar + "SortedResamp0.srs.sdb", "-rso="+workingDirName + File.separatorChar + "ResampDiff.ivc.sdb"});
			File sortresampFile = new File(workingDirName + File.separatorChar + "SortedResamp0.srs.sdb");
			sortresampFile.delete();
			
			String fileName = workingDirName + File.separatorChar + "DifRnd" + depth + "Cyc" + lowResamp + ".dif.sdb";
			AverageParticleCoefficients.main(new String[]{"-dst="+fileName, "-src="+workingDirName + File.separatorChar + "ResampDiff.ivc.sdb"});
			File resampdiffFile = new File(workingDirName + File.separatorChar + "ResampDiff.ivc.sdb");
			resampdiffFile.delete();
			
			return fileName;
		}
		else{
			//run each resample individually
			long midResamp = ((highResamp - lowResamp)/2) + lowResamp;
			String first = runResamples(lowResamp, midResamp, depth+1, sourceFileName, workingDirName);
			String second = runResamples(midResamp+1, highResamp, depth+1, sourceFileName, workingDirName);
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
	
	private VAC(){
		//force pure static
	}
}