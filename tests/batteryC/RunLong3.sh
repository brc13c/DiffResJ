lmp_serial < long3.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=long3.dump -dst=long3.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=long3.mdp.sdx -dst=long3.mdp.sdb
java -cp ../../DiffResJ.jar MSDnRAM -src=long3.mdp.sdb -dst=long3_fc.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=long3_fc.csv
java -cp ../../DiffResJ.jar MSDnRAM -src=long3.mdp.sdb -dst=long3_op.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=long3_op.csv -alg=onepass
