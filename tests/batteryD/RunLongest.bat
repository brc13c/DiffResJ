lmp_serial < longest.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=longest.dump -dst=longest.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=longest.mdp.sdx -dst=longest.mdp.sdb
java -cp ../../DiffResJ.jar VACnRAM -src=longest.mdp.sdb -dst=longest_fc.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=longest_fc.csv
java -cp ../../DiffResJ.jar VACnRAM -src=longest.mdp.sdb -dst=longest_op.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=longest_op.csv -alg=onepass
