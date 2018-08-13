javac GenToyData.java
java GenToyData 10 500 1337 Large.mdp.sdx
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=Large.mdp.sdx -dst=Large.mdp.sdb
java -cp ../../DiffResJ.jar resample.GenerateResamples -for=Large.mdp.sdb -nam=LResamp -num=10000
java -cp ../../DiffResJ.jar resample.SortResamples -spr=LResamp -low=00000 -hig=09999 -dpr=LResampSort -wk1=working1 -wk2=working2
java -cp ../../DiffResJ.jar splits.SplitData -src=Large.mdp.sdb -num=5 -dpr=LargeSub
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=LargeSub0.mdp.sdb -dst=LargeSub0.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=LargeSub1.mdp.sdb -dst=LargeSub1.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=LargeSub2.mdp.sdb -dst=LargeSub2.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=LargeSub3.mdp.sdb -dst=LargeSub3.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=LargeSub4.mdp.sdb -dst=LargeSub4.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=LargeSub0.vac.sdb -dst=LargeSub0.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=LargeSub1.vac.sdb -dst=LargeSub1.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=LargeSub2.vac.sdb -dst=LargeSub2.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=LargeSub3.vac.sdb -dst=LargeSub3.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=LargeSub4.vac.sdb -dst=LargeSub4.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.CollectAutocorrelations -dst=Large.ivc.sdb -src=LargeSub0.ivc.sdb -src=LargeSub1.ivc.sdb -src=LargeSub2.ivc.sdb -src=LargeSub3.ivc.sdb -src=LargeSub4.ivc.sdb
for v in LResampSort*
do
	java -cp ../../DiffResJ.jar vac.ResampleParticleDiffusion -src=Large.ivc.sdb -rsi=$v -rso=a$v.ivc.sdb
	java -cp ../../DiffResJ.jar vac.AverageParticleCoefficients -src=a$v.ivc.sdb -dst=a$v.dif.sdb
done
for v in aLResampSort*.dif.sdb
do
	if [ -e "LargeFull.dif.sdb" ];
	then
		java -cp ../../DiffResJ.jar diffusion.CollectDiffusionFiles -dst=temp.dif.sdb -src=LargeFull.dif.sdb -src=$v
		rm LargeFull.dif.sdb
		mv temp.dif.sdb LargeFull.dif.sdb
	fi
	if [ ! -e "LargeFull.dif.sdb" ];
	then
		cp $v LargeFull.dif.sdb
	fi
done
java -cp ../../DiffResJ.jar diffusion.DiffusionFileStatistics -src=LargeFull.dif.sdb -dst=LargeDiffCoeff.dfs.sdx -xml
rm aLResamp*
rm LResamp*
rm LargeSub*
rm Large.ivc.sdb
