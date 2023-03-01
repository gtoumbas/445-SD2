# COS 445 SD2, Spring 2019
# Created by Andrew Wonnacott

all: Redistricting.class parties.txt

sd2.zip: Redistricting.java RedistrictingConfig.java Makefile Party.java Party_even_cut_pack_choose.java Party_even_cut_even_choose.java Party_pack_cut_pack_choose.java Tournament.java parties.txt Block.java README.txt
	zip sd2 Redistricting.java RedistrictingConfig.java Makefile Party.java Party_even_cut_pack_choose.java Party_even_cut_even_choose.java Party_pack_cut_pack_choose.java Tournament.java parties.txt Block.java README.txt

test: results.csv
	@cat results.csv

results.csv: all parties.txt
	java -ea Redistricting parties.txt > results.csv

Redistricting.class: *.java
	javac -Xlint Redistricting.java *.java

parties.txt: Party_*.java
	@touch parties.txt
	@ls | grep -e 'Party_.*\.java' | sed s/.*Party_// | sed s/\.java$$// > parties.txt

clean:
	rm -rf *.class results.csv sd2.zip parties.txt
