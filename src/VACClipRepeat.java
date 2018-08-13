import vac.AverageParticleCoefficients;
import vac.CalculateAutocorrelationClipRepeat;
import vac.ResampleParticleDiffusion;
import vac.IntegrateAutocorrelation;
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
public final class VACClipRepeat{
	
	/**
	 * This will perform an all in one calculation to get mean square displacement information.
	 * @param args {binary trajectory file, working directory, output description file, number of resamples, dimension, timestep clip}
	 * @throws IOException If there is a problem reading or writing.
	 */
	public static void main(String[] args) throws IOException{
		//parse arguments
		String srcNam = null;
		String wrkDir = null;
		String dstNam = null;
		String numStr = null;
		String dimStr = null;
		String clpStr = null;
		String disNam = "";
		double varRatio = 0.0;
		for(int i = 0; i<args.length; i++){
			String curArg = args[i];
			if(curArg.startsWith("-src=")){srcNam = curArg.substring(5);}
			else if(curArg.startsWith("-dst=")){dstNam = curArg.substring(5);}
			else if(curArg.startsWith("-wrk=")){wrkDir = curArg.substring(5);}
			else if(curArg.startsWith("-num=")){numStr = curArg.substring(5);}
			else if(curArg.startsWith("-dim=")){dimStr = curArg.substring(5);}
			else if(curArg.startsWith("-clp=")){clpStr = curArg.substring(5);}
			else if(curArg.startsWith("-adc=")){disNam = curArg.substring(5);}
			else if(curArg.startsWith("-vcr=")){varRatio = Double.parseDouble(curArg.substring(5));}
		}
		if(dstNam == null || srcNam==null || wrkDir==null || numStr==null || dimStr==null || clpStr==null){
			throw new IllegalArgumentException("Usage: java VACClipRepeat -src=trajectory -wrk=working_directory -dst=diffusion_summary -num=number_of_resamples -dim=data_dimensionality -clp=timestep_clip [-adc=diffusion_distribution_file] [-vcr=variance_cutoff_ratio]");
		}
		
		final File sourceFile = new File(srcNam);
		final File workingDir = new File(wrkDir);
		final File outputFile = new File(dstNam);
		String distDest = disNam;
		
		final String sourceFileName = sourceFile.getAbsolutePath();
		final String workingDirName = workingDir.getAbsolutePath();
		final String outputFileName = outputFile.getAbsolutePath();
		
		//calculate autocorrelation
		CalculateAutocorrelationClipRepeat.dataDim = Integer.parseInt(dimStr);
		CalculateAutocorrelationClipRepeat.main(new String[]{"-src="+sourceFileName, "-dst="+workingDirName + File.separatorChar + "Autocor.vac.sdb", "-wrk="+workingDirName + File.separatorChar + "working", "-clp="+clpStr});
		
		//integrate autocorrelation
		IntegrateAutocorrelation.main(new String[]{"-src="+workingDirName + File.separatorChar + "Autocor.vac.sdb", "-dst="+workingDirName + File.separatorChar + "SingleDiff.ivc.sdb", "-dim="+dimStr, "-vcr="+varRatio});
		File autocorFile = new File(workingDirName + File.separatorChar + "Autocor.vac.sdb");
		autocorFile.delete();
		
		//resample
		long numResamps = Long.parseLong(numStr);
		String collectedDiffFileName = VAC.runResamples(0, numResamps-1, 0, sourceFileName, workingDirName);
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
	
	private VACClipRepeat(){
		//force pure static
	}
}