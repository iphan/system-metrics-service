#! /bin/bash

collectl -scmn -oD -P -A server

exec "$@"
