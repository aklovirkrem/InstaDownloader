#!/bin/sh
# Gradle wrapper script
set -e

SCRIPT_DIR=$(dirname "$(readlink -f "$0" 2>/dev/null || realpath "$0" 2>/dev/null || echo "$0")")
APP_HOME="$SCRIPT_DIR"
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

MAX_FD="maximum"

warn() { echo "$*"; }
die() { echo; echo "$*"; echo; exit 1; }

if [ "$APP_HOME" != "" ]; then
  APP_HOME=$(dirname "$APP_HOME/x")
fi

if [ -z "$JAVA_HOME" ]; then
  JAVACMD="java"
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

if ! command -v "$JAVACMD" > /dev/null 2>&1; then
  die "ERROR: JAVA_HOME is not set and 'java' command not found in PATH."
fi

CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

exec "$JAVACMD" \
  -classpath "$CLASSPATH" \
  org.gradle.wrapper.GradleWrapperMain \
  "$@"
