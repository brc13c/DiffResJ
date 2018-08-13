lmp_serial < medium3.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=medium3.dump -dst=medium3.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=medium3.mdp.sdx -dst=medium3.mdp.sdb
java -cp ../../DiffResJ.jar VACnRAM -src=medium3.mdp.sdb -dst=medium3_fc.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=medium3_fc.csv
java -cp ../../DiffResJ.jar VACnRAM -src=medium3.mdp.sdb -dst=medium3_op.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=medium3_op.csv -alg=onepass
