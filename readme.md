CCFEA Order-Book Rebuilder
==========================

This software allows the user to reconstruct the state of the limit order-book
from low-level tick-data provided by the London Stock-Exchange (LSE).  The
tick-data can be hosted in either mysql, or Apache HBase, and tools are
provided for loading to the data into either of these back-ends from the
compressed CSV (Comma-separated Variables) files provided by the LSE.  Once the
data has been loaded, events corresponding to a particular asset and a
particular date-range can be replayed through an order-book simulator in order
to reconstruct the state of the book.  Variables such as the mid-price can then
be recorded as a time-series in CSV format.

The software is written in Scala and Java, along with various shell scripts
which automate the import and order-replay process.

Pre-requisites
--------------

- [Oracle Java JVM 1.7.0 or higher](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase7-521261.html)

- If running on windows you will need to install [Cygwin](http://cygwin.com) in order to execute the shell scripts.

- Optionally, in order to build the software from source, you will need the scala build tool (sbt); see the [sbt documentation](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html).

- Optionally, in order to host the data, you will need to install [Apache HBase
  version 0.94](https://www.apache.org/dyn/closer.cgi/hbase/).  The software
can optionally connect to an existing server which already hosts the data.

Installation
------------

Open the file hbase-site.xml in the directory etc/ using a text-editor and check that the hbase.master and hbase.zookeeper.quorum properties point to the machine running Apache HBase.   For example, the following configuration can be used to connect to the machine with hostname cseesp1.essex.ac.uk:

<code>
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
</code>

Running the reconstructor
-------------------------

The script replay.sh in the scripts directory can then be used retreive a
time-series of the mid-price.  

The following example will replay all recorded events for the asset with given
ISIN and provide a GUI visualisation of the order-book.

<code>
cd scripts
./replay.sh GB0009252882 --with-gui
<code>

The following will replay a subset of events over a given date-range:

<code>
cd scripts
./replay.sh GB0009252882 --with-gui -s 5/6/2007 -e 6/6/2007
</code>

The following command will log the mid-price to a CSV file called hf.csv, but
will not provide a GUI:

<code>
cd scripts
./replay.sh GB0009252882 -s 5/6/2007 -e 6/6/2007 -o hf.csv
</code>

