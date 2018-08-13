lmp_serial < short4.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=short4.dump -dst=short4.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=short4.mdp.sdx -dst=short4.mdp.sdb
java -cp ../../DiffResJ.jar MSDnRAM -src=short4.mdp.sdb -dst=short4_fc.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=short4_fc.csv
java -cp ../../DiffResJ.jar MSDnRAM -src=short4.mdp.sdb -dst=short4_op.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=short4_op.csv -alg=onepass
