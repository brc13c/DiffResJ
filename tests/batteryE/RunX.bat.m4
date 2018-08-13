define(`S_ABS', `ifelse(eval($1<0),`1',`eval(-($1))',$1)')dnl
define(`S_RAND_SEED', `123456')dnl
define(`S_RAND',`define(`S_RAND_SEED',eval(S_RAND_SEED*1664525+1013904223))S_RAND_SEED')dnl
define(`CLASSPATH', `../../DiffResJ.jar')dnl
define(`RunOne', `m4 -DNUMTS=CURRUNTIME -DSEED=S_ABS(S_RAND) lamcom.lis.m4 > lamcom.lis
lmp_serial < lamcom.lis
java -cp CLASSPATH converters.LAMMPSDumpToSDX -src=melt.dump -dst=Data`'CURFLAVOR`'$1.mdp.sdx -dim=3 -tim=0.001
java -cp CLASSPATH converters.ConvertSDXtoSDB -src=Data`'CURFLAVOR`'$1.mdp.sdx -dst=Data`'CURFLAVOR`'$1.mdp.sdb
java -cp CLASSPATH MSDnRAM -src=Data`'CURFLAVOR`'$1.mdp.sdb -dst=MSDFC`'CURFLAVOR`'$1.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=MDSFCdist`'CURFLAVOR`'$1.csv
java -cp CLASSPATH MSDnRAM -src=Data`'CURFLAVOR`'$1.mdp.sdb -dst=MSDOP`'CURFLAVOR`'$1.dif.sdx -num=10000 -dim=3 -cur=0.01 -sig=0.0 -adc=MDSOPdist`'CURFLAVOR`'$1.csv -alg=onepass
java -cp CLASSPATH VACnRAM -src=Data`'CURFLAVOR`'$1.mdp.sdb -dst=VACFC`'CURFLAVOR`'$1.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=VACFCdist`'CURFLAVOR`'$1.csv
java -cp CLASSPATH VACnRAM -src=Data`'CURFLAVOR`'$1.mdp.sdb -dst=VACOP`'CURFLAVOR`'$1.dif.sdx -num=10000 -dim=3 -vcr=0.1 -adc=VACOPdist`'CURFLAVOR`'$1.csv -alg=onepass
')dnl
define(`RunMany',`ifelse($1,`0',`',`RunOne($1)
RunMany(eval($1-1))')')dnl
RunMany(200)
