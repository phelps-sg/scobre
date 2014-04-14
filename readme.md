CCFEA Order-Book Rebuilder
==========================

This software allows the user to reconstruct the state of the limit order-book
from low-level tick-data provided by the London Stock-Exchange (LSE).  The
tick-data can be hosted in either mysql, or Apache HBase, and tools are
provided for loading to the data into either of these back-ends from the
compressed [raw files](file:./lse-data/doc/data.pdf)  provided by the LSE.
Once the data has been loaded, events corresponding to a particular asset and a
particular date-range can be replayed through an order-book simulator in order
to reconstruct the state of the book.  Variables such as the mid-price can then
be recorded as a time-series in CSV format.

The software is written in [Scala](http://www.scala-lang.org/) and Java, along
with various Unix [shell scripts](http://www.calpoly.edu/~rasplund/script.html) which automate the import and order-replay
process.

Pre-requisites
--------------

- [Oracle Java JVM 1.7.0 or higher](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase7-521261.html)

- If running on Windows you will need to install [Cygwin](http://cygwin.com) in order to execute the shell scripts.

- (Optional) In order to build the software from source, you will need the scala build tool (sbt); see the [sbt documentation](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html).

- (Optional) In order to host the data, you will need to install [Apache HBase
  version 0.94](https://www.apache.org/dyn/closer.cgi/hbase/).  The software
can optionally connect to an existing server which already hosts the data.

- (Optional) The best Integrated Development Environment (IDE) to use for working on the project is [IntelliJ IDEA](https://www.jetbrains.com/idea/) with the [Scala plugin](http://confluence.jetbrains.com/display/SCA/Scala+Plugin+for+IntelliJ+IDEA) installed.

Installation
------------

Open the file hbase-site.xml in the directory etc/ using a text-editor and
check that the hbase.master and hbase.zookeeper.quorum properties point to the
machine running Apache HBase.   For example, the configuration below can be
used to connect to the machine with hostname cseesp1.essex.ac.uk.
Alternatively to connect to your own laptop running HBase in stand-alone mode,
replace `cseesp1.essex.ac.uk` with `localhost`.

	<configuration>
		<property>
			<name>hbase.master</name>
			<value>cseesp1.essex.ac.uk</value>
		</property>
		<property>
			<name>hbase.zookeeper.quorum</name>
			<value>cseesp1.essex.ac.uk</value>
		</property>
	</configuration>

Running the reconstructor
-------------------------

The script replay.sh in the scripts directory can then be used retreive a
time-series of the mid-price.  

The following example will replay all recorded events for the asset with given
[ISIN](http://www.isin.org/isin-database/) and provide a GUI visualisation of
the order-book.

	cd scripts
	./replay.sh -t GB0009252882 --with-gui

The following will replay a subset of events over a given date-range:

	cd scripts
	./replay.sh -t GB0009252882 --with-gui \
		--start-date 5/6/2007 --end-date 6/6/2007

The following command will log the mid-price to a CSV file called hf.csv, but
will not provide a GUI:

	cd scripts
	./replay.sh -t GB0009252882 --property midPrice \
		--start-date 5/6/2007 --end-date 6/6/2007 -o hf.csv

The following command will log transaction prices to a CSV file called hf.csv:

	cd scripts
	./replay.sh -t GB0009252882 --property lastTransactionPrice -o hf.csv

Documentation
-------------

- The [data description](file:./lse-data/doc/data.pdf) provided by the LSE
- The [API documentation](file:./lse-data/target/scala-2.10/api/index.html)

Compiling and modifying the code
--------------------------------

To compile the source-code to separate .class files, execute the following command.

	sbt compile

To compile to a single JAR file use:

	sbt assembly

To generate all the files required for an IntelliJ IDEA project, use:

	sbt gen-idea

You should then be able to open the project in the IntelliJ IDEA environment.

Note that if you receive an error saying "SBT: scala-compiler: 2.10.3 [not
found]" then you should rename the modules "SBT: SBT: scala-compiler: 2.10.3"
to "SBT: scala-compiler: 2.10.3".  See the [bug
report](http://youtrack.jetbrains.com/issue/SCL-6320) and
[comments](http://blog.jetbrains.com/scala/2013/11/18/built-in-sbt-support-in-intellij-idea-13/)
on this issue.

Importing the raw data into Apache HBase
----------------------------------------

1. Install Apache HBase 0.94 in [standalone mode](https://archanaschangale.wordpress.com/2013/08/29/installing-apache-hbase-on-ubuntu-for-standalone-mode/).

2. Modify the file base-config.xml in the etc/ directory of the folder where you unpacked the lse-data distribution as follows:

		<configuration>
			<property>
				<name>hbase.master</name>
				<value>localhost</value>
			</property>
			<property>
				<name>hbase.zookeeper.quorum</name>
				<value>localhost</value>
			</property>
		</configuration>


3. Create an empty table called `events` with column family `data` using the HBase shell:

		cd /usr/local/hbase-0.94.12/bin
		./hbase shell
		create 'events', 'data'

4. Run the shell script `hbase-import.sh` specifying the raw files to import:

		cd ./scripts
		./import-hbase.sh ../data/*.CSV


