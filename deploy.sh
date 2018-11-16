#!/usr/bin/env bash
set -e

mvn clean test install package

mvn deploy:deploy-file \
    -Durl="file://${DC_JAVA_COMMAND_HOME}/common/ps.common/lib/" \
    -Dfile="target/EmailReplyParser-1.1.jar" \
    -DgroupId="com.edlio.emailreplyparser" \
    -DartifactId="EmailReplyParser" \
    -Dpacking=jar \
    -Dversion="1.1"