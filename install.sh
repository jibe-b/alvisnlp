#!/bin/bash

# Copyright 2016 Institut National de la Recherche Agronomique
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#         http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 INSTALL_DIRECTORY" >&2
  exit 1
fi

if ! [ -d "$1" ]; then
    echo "$1 does not exist"
    exit 1
fi

LIB_FILES="alvisnlp-core/target/lib/*.jar alvisnlp-core/target/*.jar alvisnlp-bibliome/target/lib/*.jar alvisnlp-bibliome/target/*.jar"

INSTALL_DIR="$(readlink -m $1)"
BIN_DIR="$INSTALL_DIR/bin"
DOC_DIR="$INSTALL_DIR/doc"
LIB_DIR="$INSTALL_DIR/lib"
SHARE_DIR="$INSTALL_DIR/share"

mkdir -p "$INSTALL_DIR"
mkdir -p "$BIN_DIR"
mkdir -p "$DOC_DIR"
mkdir -p "$LIB_DIR"
mkdir -p "$SHARE_DIR"

cp -f -u -r $LIB_FILES "$LIB_DIR"

if [ -f "share/default-param-values.xml" ]
then
    cp -f -u "share/default-param-values.xml" "$SHARE_DIR/default-param-values.xml"
fi

if [ -f "share/default-options.txt" ]
then
    cp -f -u "share/default-options.txt" "$SHARE_DIR/default-options.txt"
fi

read -r -d '' TEMPLATE <<'EOF'

#!/bin/bash

# qsub options
#$ -S /bin/bash
#$ -V
#$ -cwd

ulimit -c 0

export CLASSPATH="__INSTALL_DIR__/lib/*:$CLASSPATH"

JVMOPTS=
OPTERR=0
while getopts J: o; do
    case "$o" in
	J) JVMOPTS="$JVMOPTS $OPTARG";;
	?) break;;
    esac
done
shift $(($OPTIND-1))

OPTS=""
if [ -f "__INSTALL_DIR__/share/default-param-values.xml" ]
then
    OPTS="$OPTS ""-defaultParamValuesFile __INSTALL_DIR__/share/default-param-values.xml"
fi
if [ -f "__INSTALL_DIR__/share/default-options.txt" ]
then
    OPTS="$OPTS "$(cat __INSTALL_DIR__/share/default-options.txt)
fi

cmd="java $JVMOPTS fr.inra.maiage.bibliome.alvisnlp.core.app.cli.AlvisNLP ""$OPTS ""$@"
#echo $CLASSPATH
#echo $cmd
$cmd
EOF

BIN_FILE="$BIN_DIR/alvisnlp"
sed -e "s,__INSTALL_DIR__,$INSTALL_DIR," <<<"$TEMPLATE" >"$BIN_FILE"
chmod +x "$BIN_FILE"

