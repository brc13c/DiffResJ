lmp_serial < long1.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=long1.dump -dst=long1.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=long1.mdp.sdx -dst=long1.mdp.sdb
java -cp ../../DiffResJ.jar VACnRAM -src=long1.mdp.sdb -dst=long1_fc.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=long1_fc.csv
java -cp ../../DiffResJ.jar VACnRAM -src=long1.mdp.sdb -dst=long1_op.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=long1_op.csv -alg=onepass
