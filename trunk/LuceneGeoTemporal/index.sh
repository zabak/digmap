#!/bin/bash
LGTE_DIR=.

##################################

BASE_LGTE=$LGTE_DIR/WEB-INF
CLASSES_LGTE=$BASE_LGTE/classes
LIB_LGTE=$BASE_LGTE/lib
LIBS_LGTE=

for i in `ls $BASE_LGTE/lib`; do
       LIBS_LGTE=$LIB_LGTE/$i:$LIBS_LGTE
done

JAVA_OPTS=-Xmx800m -Xms800m -XX:PermSize=256m -XX:MaxPermSize=256m -server
java -classpath "$CLASSES_LGTE:$LIBS_LGTE" digmapFrbr.DigmapFrbrExample
#java -classpath "$CLASSES_MITRA:$LIBS_MITRA" mitra.tasks.IndexAllResourcesMitra2
echo "IndexPendingResources.....finishing"

