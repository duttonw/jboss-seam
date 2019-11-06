#!/usr/bin/env bash
set -ex

#docker compose runs docker with commands to mount workspace and .m2 and run tests against wilfly10
docker-compose up --build
