#!/usr/bin/env bash
REPOSITORY="posts_server-all"
TAG="1.0.0"

SOURCE_CONTAINER="$REPOSITORY:$TAG"
TARGET_CONTAINER="207.246.102.100:5000/nikeo/${SOURCE_CONTAINER}"

docker build -t ${SOURCE_CONTAINER} .
if [ $? -eq 0 ]; then
  echo "Build an image $SOURCE_CONTAINER from Dockerfile OK."

  docker tag ${SOURCE_CONTAINER} ${TARGET_CONTAINER}
  if [ $? -eq 0 ]; then
    echo "Create a tag $TARGET_CONTAINER that refers to $SOURCE_CONTAINER OK."

    echo "Start push $TARGET_CONTAINER to remote registry."
    docker push ${TARGET_CONTAINER}
  else
    echo "Create a tag $TARGET_CONTAINER that refers to $SOURCE_CONTAINER FAIL."
  fi

else
  echo "Build an image $SOURCE_CONTAINER from Dockerfile FAIL."
fi