lmp_serial < long2.lis
java -cp ../../DiffResJ.jar converters.LAMMPSDumpToSDX -src=long2.dump -dst=long2.mdp.sdx -dim=3 -tim=0.01
java -cp ../../DiffResJ.jar converters.ConvertSDXtoSDB -src=long2.mdp.sdx -dst=long2.mdp.sdb
java -cp ../../DiffResJ.jar MSDnRAM -src=long2.mdp.sdb -dst=long2_fc.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=long2_fc.csv
java -cp ../../DiffResJ.jar MSDnRAM -src=long2.mdp.sdb -dst=long2_op.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=long2_op.csv -alg=onepass
