lmp_serial < medium1.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=medium1.dump -dst=medium1.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=medium1.mdp.sdx -dst=medium1.mdp.sdb
java -cp ../../DiffResJ.jar MSDnRAM -src=medium1.mdp.sdb -dst=medium1_fc.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=medium1_fc.csv
java -cp ../../DiffResJ.jar MSDnRAM -src=medium1.mdp.sdb -dst=medium1_op.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=medium1_op.csv -alg=onepass
