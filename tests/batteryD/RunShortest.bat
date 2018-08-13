lmp_serial < shortest.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=shortest.dump -dst=shortest.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=shortest.mdp.sdx -dst=shortest.mdp.sdb
java -cp ../../DiffResJ.jar VACnRAM -src=shortest.mdp.sdb -dst=shortest_fc.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=shortest_fc.csv
java -cp ../../DiffResJ.jar VACnRAM -src=shortest.mdp.sdb -dst=shortest_op.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=shortest_op.csv -alg=onepass
