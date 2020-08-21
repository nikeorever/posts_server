#!/usr/bin/env bash
REPOSITORY="posts_server-all"
TAG="1.0.0"

CONTAINER="$REPOSITORY:$TAG"
docker build -t ${CONTAINER} .
if [ $? -eq 0 ]; then
  echo "Build an image $CONTAINER from Dockerfile OK."

  docker run --env-file ./db_env -m512M --cpus 2 -it -p 8080:8080 --rm ${CONTAINER}
else
  echo "Build an image $CONTAINER from Dockerfile FAIL."
fi
