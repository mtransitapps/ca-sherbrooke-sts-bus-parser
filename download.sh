#!/bin/bash
echo ">> Downloading..."
URL=`cat input_url`;
FILENAME=$(basename "$URL");
if [ -e input/gtfs.zip ]; then
    mv input/gtfs.zip $FILENAME;
    wget --header="User-Agent: MonTransit" --timeout=60 --tries=6 -N $URL;
else
    wget --header="User-Agent: MonTransit" --timeout=60 --tries=6 -S $URL;
fi;
if [ -e $FILENAME ]; then
	mv $FILENAME input/gtfs.zip;
fi;
URL=`cat input_url_next`;
FILENAME=$(basename "$URL");
if [ -e input/gtfs_next.zip ]; then
    mv input/gtfs_next.zip $FILENAME;
    wget --header="User-Agent: MonTransit" --timeout=60 --tries=6 -N $URL;
else
    wget --header="User-Agent: MonTransit" --timeout=60 --tries=6 -S $URL;
fi;
if [ -e $FILENAME ]; then
	mv $FILENAME input/gtfs_next.zip;
fi;
echo ">> Downloading... DONE"