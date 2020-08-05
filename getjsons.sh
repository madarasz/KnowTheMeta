#!/bin/bash
# you need "jq" installed to run this script

start=$SECONDS

rm -rf ./output/*
mkdir ./output/cards
mkdir ./output/decks

# download meta list
curl http://localhost:8080/stats > ./output/metas.json

metafiles=( $(cat ./output/metas.json | jq -r '.[].file' ) )
metanames=( $(cat ./output/metas.json | jq -r '.[].title' | sed "s/ /%20/g" ) )

# iterate over metas
i=0;
for metafile in "${metafiles[@]}"
do
    metaname=${metanames[$i]}
    echo "Getting meta: $metaname"
    curl "http://localhost:8080/stats/$metaname" > "./output/$metafile"
    curl "http://localhost:8080/stats/decks/$metaname" > "./output/decks/$metafile"
    ((i++))
done

# iterate over cards
titles=( $(curl https://netrunnerdb.com/api/2.0/public/cards | jq -r '.data[].title' | tr -s ' ' | tr ' ' '-' | tr '[:upper:]' '[:lower:]' | sed "s/[^a-z0-9-]//g" ) )
codes=( $(curl https://netrunnerdb.com/api/2.0/public/cards | jq -r '.data[].code' ) )

i=0;
for title in "${titles[@]}"
do
    code=${codes[$i]}
    echo "Getting card: $code - $title"
    curl "http://localhost:8080/stats/cards/$code-$title" > "./output/cards/$code-$title.json"
    ((i++))
done

secs=$(( $SECONDS-$start ))
echo "JSON export ran:"
printf '%02dh:%02dm:%02ds\n' $(($secs/3600)) $(($secs%3600/60)) $(($secs%60))