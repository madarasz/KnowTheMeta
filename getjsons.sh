#!/bin/bash
# you need "jq" installed to run this script
rm -rf ./output/*

# download meta list
curl http://localhost:8080/stats > ./output/metas.json

metafiles=( $(cat ./output/metas.json | jq -r '.[].file' ) )
metanames=( $(cat ./output/metas.json | jq -r '.[].title' | sed "s/ /%20/g" ) )

# iterate over cards
i=0;
for metafile in "${metafiles[@]}"
do
    metaname=${metanames[$i]}
    echo "http://localhost:8080/stats/$metaname"
    curl "http://localhost:8080/stats/$metaname" > "./output/$metafile"
    ((i++))
done

for metaname in "${metanames[@]}"
do
    echo $metaname
done