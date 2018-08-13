javac GenToyData.java
java GenToyData 10 500 1337 Large.mdp.sdx
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=Large.mdp.sdx -dst=Large.mdp.sdb
java -cp ../../DiffResJ.jar resample.GenerateResamples -for=Large.mdp.sdb -nam=LResamp -num=10000
java -cp ../../DiffResJ.jar resample.SortResamples -spr=LResamp -low=00000 -hig=09999 -dpr=LResampSort -wk1=working1 -wk2=working2
java -cp ../../DiffResJ.jar splits.SplitData -src=Large.mdp.sdb -num=5 -dpr=LargeSub
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=LargeSub0.mdp.sdb -dst=LargeSub0.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=LargeSub1.mdp.sdb -dst=LargeSub1.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=LargeSub2.mdp.sdb -dst=LargeSub2.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=LargeSub3.mdp.sdb -dst=LargeSub3.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CalculateSquareDisplacement -src=LargeSub4.mdp.sdb -dst=LargeSub4.sd.sdb -wrk=working1
java -cp ../../DiffResJ.jar msd.CollectSquareDisplacement -dst=Large.sd.sdb -src=LargeSub0.sd.sdb -src=LargeSub1.sd.sdb -src=LargeSub2.sd.sdb -src=LargeSub3.sd.sdb -src=LargeSub4.sd.sdb
for v in LResampSort*
do
	java -cp ../../DiffResJ.jar msd.ResampleSquareDisplacement -src=Large.sd.sdb -rsi=$v -rso=a$v.sd.sdb
	java -cp ../../DiffResJ.jar msd.AverageMeanSquareDisplacement -src=a$v.sd.sdb -dst=a$v.msd.sdb
	java -cp ../../DiffResJ.jar msd.FitDiffusionCoefficient -cur=0.01 -sig=0.5 -dim=1 -dst=a$v.dif.sdb -src=a$v.msd.sdb
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
rm Large.sd.sdb
