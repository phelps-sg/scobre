# SCala OrderBook REconstructor (SCOBRE)

This software allows the user to reconstruct the state of the limit order-book 
from low-level tick-data provided by the London Stock-Exchange (LSE).  The 
tick-data can be hosted in either mysql, or Apache HBase, and tools are provided 
for loading to the data into either of these back-ends from the compressed [raw 
files](file:./lse-data/doc/data.pdf)  provided by the LSE. Once the data has 
been loaded, events corresponding to a particular asset and a particular 
date-range can be replayed through an order-book simulator in order to 
reconstruct the state of the book.  Variables such as the mid-price can then be 
recorded as a time-series in CSV format.  Alternatively the simulator can be run 
directly from a Python client using an [Apache Thrift 
API](https://thrift.apache.org/).

The software is written in [Scala](http://www.scala-lang.org/) and Java, along
with various Unix [shell scripts](http://www.calpoly.edu/~rasplund/script.html) 
which automate the import process.

## Pre-requisites

- [Oracle Java JVM 1.7.0 or higher](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase7-521261.html).  Note that the default JVM installed on MacOS or 
Linux needs to be replaced by the Oracle version in order for the software to 
work correctly.

- If running on Windows you will need to install [Cygwin](http://cygwin.com) in 
order to execute the shell scripts.

- (Optional) In order to build the software from source, you will need the scala build tool (sbt); see the [sbt documentation](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html).

- (Optional) In order to host the data, you will need to install [Apache HBase
  version 1.1.2](https://www.apache.org/dyn/closer.cgi/hbase/).  The software
can optionally connect to an existing server which already hosts the data.

- (Optional) The best Integrated Development Environment (IDE) to use for 
working on the project is [IntelliJ IDEA](https://www.jetbrains.com/idea/) with 
the [Scala 
plugin](https://confluence.jetbrains.com/display/SCA/Scala+Plugin+for+IntelliJ+IDEA)
 installed.

## Installation

### 1. Configure the HBase host

Open the file hbase-site.xml in the directory etc/ using a text-editor and
check that the hbase.master and hbase.zookeeper.quorum properties point to the
machine running Apache HBase.   For example, the configuration below can be
used to connect to the machine with hostname `cseesp1.essex.ac.uk`.
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
	
### 2. Compile the code

To compile the source-code to separate .class files, execute the following command:

~~~bash
sbt compile
~~~

To create jar files and the script files:

~~~bash
sbt pack
~~~~

### 3. Install the shell scripts

Execute the following commands in the shell to install the scripts into the directory `~/local/bin`:

~~~bash
cd target/pack/bin
make install
~~~

If `~/local/bin` is not already in your `PATH` environment variable, add a command similar to the following to
the file `~/.profile`:

~~~bash
export PATH=$PATH:~/local/bin
~~~

## Running the reconstructor from the shell

The script `replay-orders` can then be used retrieve a univariate time-series of prices.

The following example will replay all recorded events for the asset with given
[ISIN](http://www.isin.org/isin-database/) and provide a GUI visualisation of
the order-book.

~~~bash
replay-orders -t GB0009252882 --with-gui
~~~

The following will replay a subset of events over a given date-range:

~~~bash
replay-orders -t GB0009252882 --with-gui \
		--start-date 5/6/2007 --end-date 6/6/2007
~~~

The following command will log the mid-price to a CSV file called `hf.csv`, but
will not provide a GUI:

~~~bash
replay-orders -t GB0009252882 --property midPrice \
		--start-date 5/6/2007 --end-date 6/6/2007 -o hf.csv
~~~

The following command will log transaction prices to a CSV file called hf.csv:

~~~bash
replay-orders -t GB0009252882 --property lastTransactionPrice -o hf.csv
~~~~
	
To get the full list of options use the built-in help:

~~~bash
replay-orders --help
~~~~
    
## Accessing the simulator from a Python client

The simulator provides an [Apache Thrift API](https://thrift.apache.org/) which
allows clients written in non-JVM languages to call the reconstructor.  To
start the server, run the following script:

~~~bash
order-replay-service
~~~
    
By default the server will listen on TCP port 9090.  To see the configurations options, run:

~~~bash
start-replay-server.sh --help
~~~

To see an example of using the API from Python see the script 
[tickdata.py](lse-data/src/main/python/tickdata.py).

## Documentation

- The [data description](file:./lse-data/doc/data.pdf) provided by the LSE
- The [API documentation](file:./lse-data/target/scala-2.11/api/index.html)

## Working on the project using an IDE

To import the project as an IntelliJ IDEA project, first install the [Scala 
plugin](https://confluence.jetbrains.com/display/SCA/Scala+Plugin+for+IntelliJ+IDEA), and then directly import the `build.sbt` file as a new project.

## Importing the raw data into Apache HBase

1. Install Apache HBase 1.1.2 in [standalone mode](https://hbase.apache.org/book/quickstart.html).

2. Modify the file `base-config.xml` in the `etc/` directory of the folder where you unpacked the lse-data distribution as follows:

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

~~~bash
cd /opt/hbase/bin
./hbase shell
create 'events', 'data'
~~~

4. Run the shell script `hbase-import.sh` specifying the raw files to import:

~~~bash
cd ./scripts
./import-data-lse.sh ../data/lse/*.CSV.gz
~~~

## Contact

(C) [Steve Phelps](mailto:sphelps@sphelps.net) 2016

