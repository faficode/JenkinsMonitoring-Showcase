#!/bin/bash

export KUBECONFIG=./$1.yaml
kubectl config --kubeconfig=$1.yaml use-context standard-cluster-1
