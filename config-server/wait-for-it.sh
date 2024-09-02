#!/usr/bin/env bash
# wait-for-it.sh

set -e

host="$1"
shift
port="$1"
shift
cmd="$@"

>&2 echo "Waiting for $host:$port to be available..."

while ! nc -z $host $port; do
  sleep 1
done

>&2 echo "$host:$port is available. Starting the service..."
exec $cmd
