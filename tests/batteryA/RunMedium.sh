javac GenToyData.java
java GenToyData 5 100 1337 Medium.mdp.sdx
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=Medium.mdp.sdx -dst=Medium.mdp.sdb
java -cp ../../DiffResJ.jar resample.GenerateResamples -for=Medium.mdp.sdb -nam=MResamp -num=10000
java -cp ../../DiffResJ.jar resample.SortResamples -spr=MResamp -low=00000 -hig=09999 -dpr=MResampSort -wk1=working1 -wk2=working2
java -cp ../../DiffResJ.jar splits.SplitData -src=Medium.mdp.sdb -num=5 -dpr=MediumSub
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=MediumSub0.mdp.sdb -dst=MediumSub0.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=MediumSub1.mdp.sdb -dst=MediumSub1.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=MediumSub2.mdp.sdb -dst=MediumSub2.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=MediumSub3.mdp.sdb -dst=MediumSub3.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=MediumSub4.mdp.sdb -dst=MediumSub4.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CollectSquareDisplacement -dst=Medium.sd.sdb -src=MediumSub0.sd.sdb -src=MediumSub1.sd.sdb -src=MediumSub2.sd.sdb -src=MediumSub3.sd.sdb -src=MediumSub4.sd.sdb
for v in MResampSort*
do
	java -cp ../../DiffResJ.jar msd.ResampleSquareDisplacement -src=Medium.sd.sdb -rsi=$v -rso=a$v.sd.sdb
	java -cp ../../DiffResJ.jar msd.AverageMeanSquareDisplacement -src=a$v.sd.sdb -dst=a$v.msd.sdb
	java -cp ../../DiffResJ.jar msd.FitDiffusionCoefficient -cur=0.01 -sig=0.5 -dim=1 -dst=a$v.dif.sdb -src=a$v.msd.sdb
done
for v in aMResampSort*.dif.sdb
do
	if [ -e "MediumFull.dif.sdb" ];
	then
		java -cp ../../DiffResJ.jar diffusion.CollectDiffusionFiles -dst=temp.dif.sdb -src=MediumFull.dif.sdb -src=$v
		rm MediumFull.dif.sdb
		mv temp.dif.sdb MediumFull.dif.sdb
	fi
	if [ ! -e "MediumFull.dif.sdb" ];
	then
		cp $v MediumFull.dif.sdb
	fi
done
java -cp ../../DiffResJ.jar diffusion.DiffusionFileStatistics -src=MediumFull.dif.sdb -dst=MediumDiffCoeff.dfs.sdx -xml
rm aMResamp*
rm MResamp*
rm MediumSub*
rm Medium.sd.sdb
