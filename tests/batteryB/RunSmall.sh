javac GenToyData.java
java GenToyData 2 50 1337 Small.mdp.sdx
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=Small.mdp.sdx -dst=Small.mdp.sdb
java -cp ../../DiffResJ.jar resample.GenerateResamples -for=Small.mdp.sdb -nam=SResamp -num=10000
java -cp ../../DiffResJ.jar resample.SortResamples -spr=SResamp -low=00000 -hig=09999 -dpr=SResampSort -wk1=working1 -wk2=working2
java -cp ../../DiffResJ.jar splits.SplitData -src=Small.mdp.sdb -num=5 -dpr=SmallSub
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=SmallSub0.mdp.sdb -dst=SmallSub0.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=SmallSub1.mdp.sdb -dst=SmallSub1.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=SmallSub2.mdp.sdb -dst=SmallSub2.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=SmallSub3.mdp.sdb -dst=SmallSub3.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=SmallSub4.mdp.sdb -dst=SmallSub4.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=SmallSub0.vac.sdb -dst=SmallSub0.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=SmallSub1.vac.sdb -dst=SmallSub1.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=SmallSub2.vac.sdb -dst=SmallSub2.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=SmallSub3.vac.sdb -dst=SmallSub3.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=SmallSub4.vac.sdb -dst=SmallSub4.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.CollectAutocorrelations -dst=Small.ivc.sdb -src=SmallSub0.ivc.sdb -src=SmallSub1.ivc.sdb -src=SmallSub2.ivc.sdb -src=SmallSub3.ivc.sdb -src=SmallSub4.ivc.sdb
for v in SResampSort*
do
	java -cp ../../DiffResJ.jar vac.ResampleParticleDiffusion -src=Small.ivc.sdb -rsi=$v -rso=a$v.ivc.sdb
	java -cp ../../DiffResJ.jar vac.AverageParticleCoefficients -src=a$v.ivc.sdb -dst=a$v.dif.sdb
done
for v in aSResampSort*.dif.sdb
do
	if [ -e "SmallFull.dif.sdb" ];
	then
		java -cp ../../DiffResJ.jar diffusion.CollectDiffusionFiles -dst=temp.dif.sdb -src=SmallFull.dif.sdb -src=$v
		rm SmallFull.dif.sdb
		mv temp.dif.sdb SmallFull.dif.sdb
	fi
	if [ ! -e "SmallFull.dif.sdb" ];
	then
		cp $v SmallFull.dif.sdb
	fi
done
java -cp ../../DiffResJ.jar diffusion.DiffusionFileStatistics -src=SmallFull.dif.sdb -dst=SmallDiffCoeff.dfs.sdx -xml
rm aSResamp*
rm SResamp*
rm SmallSub*
rm Small.ivc.sdb
