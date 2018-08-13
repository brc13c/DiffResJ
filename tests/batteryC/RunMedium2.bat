lmp_serial < medium2.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=medium2.dump -dst=medium2.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=medium2.mdp.sdx -dst=medium2.mdp.sdb
java -cp ../../DiffResJ.jar MSDnRAM -src=medium2.mdp.sdb -dst=medium2_fc.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=medium2_fc.csv
java -cp ../../DiffResJ.jar MSDnRAM -src=medium2.mdp.sdb -dst=medium2_op.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=medium2_op.csv -alg=onepass
