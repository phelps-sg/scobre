#!/bin/sh

# Import CSV files into Apache HBase.

if [ $# -lt 1 ]; then
	echo "Usage: importdata.sh <FILENAMES>"
	exit 1
fi

import_pipe() {
	TABLE=$1
	
	echo "Importing $TABLE"

	java org.ccfea.tickdata.ParseRawData -b 5000 -r $TABLE -f /tmp/lsedata.txt
}

cat_data() {
	FILE=$1
	case $FILE in
		*.[Cc][Ss][Vv]) cat $FILE;;
		*.[Zz][Ii][Pp]) unzip -p $FILE;;
		*.[Cc][Ss][Vv].[Gg][Zz]) zcat $FILE;;
		*) echo "Unknown file type $FILE"; exit 1
	esac
}

# Import the specified CSV file into the appropriate table
import() {
	FILE=$1

	cat_data $FILE > /tmp/lsedata.txt &
	
	case $FILE in
		*[Tt]rade[Rr]eport*) 	import_pipe trade_reports_raw;;
		*[Oo]rder[Dd]etail*) 	import_pipe order_detail_raw;;
		*[Oo]rder[Hh]istory*) 	import_pipe order_history_raw;;
		*) echo "Unknown table for filename $FILE"
	esac
}

##################
# Main           #
##################

BASEDIR=`dirname $0`
export BASEDIR=$BASEDIR/..

# Configure Java class path
export CLASSPATH=$BASEDIR/etc:$BASEDIR/target/scala-2.10/lse-data-assembly-0.4.jar

# Remove previous named pipe
rm -f /tmp/lsedata.txt

# Create a named pipe to the importing process
mkfifo /tmp/lsedata.txt

for filename in $*
do
	echo "Processing $filename... "
	import $filename
	echo "done."
done

# Clean up
rm /tmp/lsedata.txt

