#!/bin/sh
PORT=$1
VERSION=$2
echo Deploying version $VERSION to localhost:$PORT
echo VERSION=$VERSION > deploy-$PORT.sh
cat deployRemote.sh  >> deploy-$PORT.sh
sshpass -p 'vagrant' ssh -p $PORT -o StrictHostKeyChecking=no vagrant@10.0.2.2 'bash -s' < deploy-$PORT.sh
