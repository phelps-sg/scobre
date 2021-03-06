#!/bin/bash

PIPE=/tmp/tickdata.pipe

# Import CSV files into Apache HBase.

if [ $# -lt 1 ]; then
    echo "Usage: importdata-lse.sh <DATE> <FILENAMES>"
    exit 1
fi

import_pipe() {
    TABLE=$1
    
    echo "Importing $TABLE using date $DATE"

#    java org.ccfea.tickdata.ImportData -b 2600 -r $TABLE -f $PIPE
    import-data -s $DATE -b 2600 -r $TABLE -f $PIPE
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
    
    DATE=`echo $FILE | perl -n -e'/([0-9]{4})([0-9]{2})/ && print "1/$2/$1"'`
    if [ -n DATE ]
    then
        DATE='2007/01/01'
    fi
    
    cat_data $FILE | dos2unix > $PIPE &
    
    case $FILE in
        *[Tt]rade[Rr]eport*)     import_pipe trade_reports_raw;;
        *[Oo]rder[Dd]etail*)     import_pipe order_detail_raw;;
        *[Oo]rder[Hh]istory*)     import_pipe order_history_raw;;
        *) echo "Unknown table for filename $FILE"
    esac
}

##################
# Main           #
##################

#source config.sh

# Remove previous named pipe
rm -f $PIPE

# Create a named pipe to the importing process
mkfifo $PIPE

for filename in $*
do
    echo "Processing $filename... "
    import $filename
    echo "done."
done

# Clean up
rm $PIPE

