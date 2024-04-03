#!/bin/bash

if [ "$#" -ne 5 ]; then
    echo "Usage: $0 file1 file2 file3 file4 file5"
    exit 1
fi

file1="$1"
file2="$2"
file3="$3"
file4="$4"
file5="$5"

javac *.java

java TransactionProcessorSample "$file1" "$file2" "$file3" "$file4" "$file5"