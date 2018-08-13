javac GenToyData.java
java GenToyData 2 50 1337 Small.mdp.sdx
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=Small.mdp.sdx -dst=Small.mdp.sdb
java -cp ../../DiffResJ.jar resample.GenerateResamples -for=Small.mdp.sdb -nam=SResamp -num=10000
java -cp ../../DiffResJ.jar resample.SortResamples -spr=SResamp -low=00000 -hig=09999 -dpr=SResampSort -wk1=working1 -wk2=working2
java -cp ../../DiffResJ.jar splits.SplitData -src=Small.mdp.sdb -num=5 -dpr=SmallSub
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=SmallSub0.mdp.sdb -dst=SmallSub0.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=SmallSub1.mdp.sdb -dst=SmallSub1.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=SmallSub2.mdp.sdb -dst=SmallSub2.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=SmallSub3.mdp.sdb -dst=SmallSub3.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=SmallSub4.mdp.sdb -dst=SmallSub4.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CollectSquareDisplacement -dst=Small.sd.sdb -src=SmallSub0.sd.sdb -src=SmallSub1.sd.sdb -src=SmallSub2.sd.sdb -src=SmallSub3.sd.sdb -src=SmallSub4.sd.sdb
for %%v in (SResampSort*) do (
	java -cp ../../DiffResJ.jar msd.ResampleSquareDisplacement -src=Small.sd.sdb -rsi=%%v -rso=a%%v.sd.sdb
	java -cp ../../DiffResJ.jar msd.AverageMeanSquareDisplacement -src=a%%v.sd.sdb -dst=a%%v.msd.sdb
	java -cp ../../DiffResJ.jar msd.FitDiffusionCoefficient -cur=0.01 -sig=0.5 -dim=1 -dst=a%%v.dif.sdb -src=a%%v.msd.sdb
)
for %%v in (aSResampSort*.dif.sdb) do (
	if exist SmallFull.dif.sdb (
		java -cp ../../DiffResJ.jar diffusion.CollectDiffusionFiles -dst=temp.dif.sdb -src=SmallFull.dif.sdb -src=%%v
		del SmallFull.dif.sdb
		rename temp.dif.sdb SmallFull.dif.sdb
	)
	if not exist SmallFull.dif.sdb copy %%v SmallFull.dif.sdb
)
java -cp ../../DiffResJ.jar diffusion.DiffusionFileStatistics -src=SmallFull.dif.sdb -dst=SmallDiffCoeff.dfs.sdx -xml
del aSResamp*
del SResamp*
del SmallSub*
del Small.sd.sdb
