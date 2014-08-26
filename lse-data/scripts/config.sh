
BASEDIR=`dirname $0`
export BASEDIR=$BASEDIR/..

export LSEDATA_VERSION=0.12
export SCALA_VERSION=2.11

# Configure Java class path
export CLASSPATH=$BASEDIR/etc:$BASEDIR/target/scala-$SCALA_VERSION/lse-data-assembly-$LSEDATA_VERSION.jar
