#!/usr/bin/env bash

file=$1

export CURRENT_TIME=$(date +%Y-%M-%dT%H-%M-%S)

cat $1 | envsubst | kubectl apply -f -
