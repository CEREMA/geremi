#!/bin/sh

PREFIX="${APP_NAME}-${CI_ENVIRONMENT_SLUG}"

DEPLOYMENTS=$(kubectl -n $PREFIX get deployments -o jsonpath='{.items[*].metadata.name}' | tr " " "\n" | grep -v "pgadmin\|postgres")
for DEPLOYMENT in $DEPLOYMENTS
do
    case $DEPLOYMENT in $PREFIX*)
        kubectl -n $PREFIX scale deployment/$DEPLOYMENT --replicas=0
    esac
done
