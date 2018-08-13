lmp_serial < short3.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=short3.dump -dst=short3.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=short3.mdp.sdx -dst=short3.mdp.sdb
java -cp ../../DiffResJ.jar VACnRAM -src=short3.mdp.sdb -dst=short3_fc.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=short3_fc.csv
java -cp ../../DiffResJ.jar VACnRAM -src=short3.mdp.sdb -dst=short3_op.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=short3_op.csv -alg=onepass
