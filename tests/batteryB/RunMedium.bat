javac GenToyData.java
java GenToyData 5 100 1337 Medium.mdp.sdx
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=Medium.mdp.sdx -dst=Medium.mdp.sdb
java -cp ../../DiffResJ.jar resample.GenerateResamples -for=Medium.mdp.sdb -nam=MResamp -num=10000
java -cp ../../DiffResJ.jar resample.SortResamples -spr=MResamp -low=00000 -hig=09999 -dpr=MResampSort -wk1=working1 -wk2=working2
java -cp ../../DiffResJ.jar splits.SplitData -src=Medium.mdp.sdb -num=5 -dpr=MediumSub
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=MediumSub0.mdp.sdb -dst=MediumSub0.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=MediumSub1.mdp.sdb -dst=MediumSub1.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=MediumSub2.mdp.sdb -dst=MediumSub2.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=MediumSub3.mdp.sdb -dst=MediumSub3.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.CalculateAutocorrelation -src=MediumSub4.mdp.sdb -dst=MediumSub4.vac.sdb -wrk=working
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=MediumSub0.vac.sdb -dst=MediumSub0.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=MediumSub1.vac.sdb -dst=MediumSub1.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=MediumSub2.vac.sdb -dst=MediumSub2.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=MediumSub3.vac.sdb -dst=MediumSub3.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.IntegrateAutocorrelation -src=MediumSub4.vac.sdb -dst=MediumSub4.ivc.sdb -dim=1 -vcr=0.1
java -cp ../../DiffResJ.jar vac.CollectAutocorrelations -dst=Medium.ivc.sdb -src=MediumSub0.ivc.sdb -src=MediumSub1.ivc.sdb -src=MediumSub2.ivc.sdb -src=MediumSub3.ivc.sdb -src=MediumSub4.ivc.sdb
for %%v in (MResampSort*) do (
	java -cp ../../DiffResJ.jar vac.ResampleParticleDiffusion -src=Medium.ivc.sdb -rsi=%%v -rso=a%%v.ivc.sdb
	java -cp ../../DiffResJ.jar vac.AverageParticleCoefficients -src=a%%v.ivc.sdb -dst=a%%v.dif.sdb
)
for %%v in (aMResampSort*.dif.sdb) do (
	if exist MediumFull.dif.sdb (
		java -cp ../../DiffResJ.jar diffusion.CollectDiffusionFiles -dst=temp.dif.sdb -src=MediumFull.dif.sdb -src=%%v
		del MediumFull.dif.sdb
		rename temp.dif.sdb MediumFull.dif.sdb
	)
	if not exist MediumFull.dif.sdb copy %%v MediumFull.dif.sdb
)
java -cp ../../DiffResJ.jar diffusion.DiffusionFileStatistics -src=MediumFull.dif.sdb -dst=MediumDiffCoeff.dfs.sdx -xml
del aMResamp*
del MResamp*
del MediumSub*
del Medium.ivc.sdb
