
BASEDIR=`dirname $0`
export BASEDIR=$BASEDIR/..

export LSEDATA_VERSION=0.6
export SCALA_VERSION=2.10

# Configure Java class path
export CLASSPATH=$BASEDIR/etc:$BASEDIR/target/scala-$SCALA_VERSION/lse-data-assembly-$LSEDATA_VERSION.jar
