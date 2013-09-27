#!/bin/sh

if [ $# -lt 4 ]; then
	echo "Usage: importdata.sh <USER> <PASSWORD> <SOCKET> <FILENAMES>"
	exit 1
fi

USER=$1; shift;
PASSWORD=$1; shift;
SOCKET=$1; shift

import_pipe() {
	TABLE=$1
	
	echo "Importing into $TABLE"
	
	mysql --local_infile=1 -u$USER -p$PASSWORD --socket=$SOCKET <<EOF
	    USE lse_tickdata;
	    LOAD DATA LOCAL INFILE '/tmp/lsedata.txt' INTO TABLE $TABLE FIELDS TERMINATED BY ','
EOF
}

# Replace NULL with \N for use with MySql import
replace_null() {
	sed s/NULL/\\\\N/g   
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

# Import the specified CSV file into the appropriate mysql table
import() {
	FILE=$1

	cat_data $FILE | replace_null > /tmp/lsedata.txt &
	
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

if [ -z "$SOCKET" ]; then
	export SOCKET=/var/run/mysqld/mysqld.sock
fi

# Remove previous named pipe
rm -f /tmp/lsedata.txt

# Create a named pipe to the mysql command
mkfifo /tmp/lsedata.txt

for filename in $*
do
	echo "Processing $filename... "
	import $filename
	echo "done."
done

# Clean up
rm /tmp/lsedata.txt
