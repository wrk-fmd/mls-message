#!/bin/bash

# This script can be used to build the Docker image locally, since `docker-compose build` does not support Buildkit secrets

settings=${MLS_M2_SETTINGS:-~/.m2/settings.xml}

for type in fleetsync sms tetra; do
  module=message-${type}
  app=message-${type}
  tag=wrkfmdit/mls-message-${type}:${MLS_TAG:-latest}

  runtime=
  if [[ "$type" == "fleetsync" ]]; then
    # Building the fleetsync image: Use a Debian image with RXTX installed as runtime
    runtime="--build-arg RUNTIME=debian-rxtx"
  fi

  echo "Running Docker build for ${app} with secrets from '${settings}'..."

  DOCKER_BUILDKIT=1 docker build \
      --secret id=m2-settings,src="${settings}" \
      --build-arg MODULE=$module --build-arg APP=$app $runtime \
      -t "$tag" .
done
