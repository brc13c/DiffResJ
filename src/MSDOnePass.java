import diffusion.DiffusionFileStatistics;
import diffusion.CollectDiffusionFiles;
import msd.CalculateSquareDisplacementOnePass;
import msd.FitDiffusionCoefficient;
import msd.AverageMeanSquareDisplacement;
import msd.ResampleSquareDisplacement;
import resample.SortResamples;
import resample.GenerateResamples;

import java.io.IOException;
import java.io.File;
import converters.ConvertDiffSamplesToCSV;

/**
 * This will perform an all in one calculation to get mean square displacement information.
 * @author Benjamin
 */
public final class MSDOnePass{
	
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
			throw new IllegalArgumentException("Usage: java MSDOnePass -src=trajectory -wrk=working_directory -dst=diffusion_summary -num=number_of_resamples -dim=data_dimensionality -cur=maximum_curvature -sig=minimum_signal_to_noise [-adc=diffusion_distribution_file]");
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
		CalculateSquareDisplacementOnePass.dataDim = dimension;
		CalculateSquareDisplacementOnePass.main(new String[]{"-src="+sourceFileName, "-dst="+workingDirName + File.separatorChar + "SquareDisp.sd.sdb"});
		
		//generate resamples
		String collectedName = MSD.runResamples(0, numResamps-1, 0, sourceFileName, workingDirName, curvature, signal, dimension);
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
	
	private MSDOnePass(){
		//force pure static
	}
}