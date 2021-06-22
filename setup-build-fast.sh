#!/usr/bin/env bash
./mvnw clean install -Dskip.check=true -Dcheck.skip=true -Ddisable.checks=true -Dmaven.javadoc.skip=true -DskipTests -fae -f stream-applications-build
./mvnw clean install -Dskip.check=true -Dcheck.skip=true -Ddisable.checks=true -Dmaven.javadoc.skip=true -DskipTests -fae -f functions -N
./mvnw clean install -Dskip.check=true -Dcheck.skip=true -Ddisable.checks=true -Dmaven.javadoc.skip=true -DskipTests -fae -f functions/function-dependencies -N
./mvnw clean install -Dskip.check=true -Dcheck.skip=true -Ddisable.checks=true -Dmaven.javadoc.skip=true -DskipTests -fae -f applications/stream-applications-core -N

