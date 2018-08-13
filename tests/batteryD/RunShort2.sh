lmp_serial < short2.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=short2.dump -dst=short2.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=short2.mdp.sdx -dst=short2.mdp.sdb
java -cp ../../DiffResJ.jar VACnRAM -src=short2.mdp.sdb -dst=short2_fc.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=short2_fc.csv
java -cp ../../DiffResJ.jar VACnRAM -src=short2.mdp.sdb -dst=short2_op.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=short2_op.csv -alg=onepass
