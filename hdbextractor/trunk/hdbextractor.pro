#-------------------------------------------------
#
# Project created by QtCreator 2014-03-06T10:53:55
#
#-------------------------------------------------

QT       -= core gui

INSTALL_ROOT = /runtime
LIB_DIR = $${INSTALL_ROOT}/lib
INC_DIR = $${INSTALL_ROOT}/include/hdbextractor
DOC_DIR = $${INSTALL_ROOT}/share/doc/hdbextractor

CONFIG += debug

TARGET = hdbextractor++

VERSION_HEX = 0x010000
VERSION = 1.0.0
VER_MAJ = 1
VER_MIN = 0
VER_FIX = 0

unix:INCLUDEPATH += mysql/src db/src db src ../src db/helpers utils utils/private

DEFINES += HDBEXTRACTOR_VERSION_STR=\"\\\"$${VERSION}\\\"\" \
    HDBEXTRACTOR_VERSION=$${VERSION_HEX} \
    VER_MAJ=$${VER_MAJ} \
    VER_MIN=$${VER_MIN} \
    VER_FIX=$${VER_FIX} \
    HDBEXTRACTORLIB_PRINTINFO

TEMPLATE = lib

DEFINES += HDBEXTRACTOR_LIBRARY

SOURCES += src/hdbextractor.cpp \
    src/hdbextractorprivate.cpp \
    src/mysql/mysqlconnection.cpp \
    src/db/row.cpp \
    src/mysql/mysqlresult.cpp \
    src/mysql/mysqlrow.cpp \
    src/db/result.cpp \
    src/db/xvariant.cpp \
    src/hdb/mysqlhdbschema.cpp \
    src/db/xvariantlist.cpp \
    src/hdbpp/mysqlhdbppschema.cpp \
    src/configurationparser.cpp \
    src/queryconfiguration.cpp \
    src/db/configurabledbschema.cpp \
    src/db/helpers/configurabledbschemahelper.cpp \
    src/utils/datasiever.cpp \
    src/utils/private/datasieverprivate.cpp \
    src/utils/xvariantprinter.cpp \
    src/utils/datasieverprogresslistener.cpp \
    src/db/timeinterval.cpp \
    src/db/datetimeutils.cpp \
    src/db/xvariantprivate.cpp

HEADERS += src/hdbextractor.h\
    src/hdbextractorprivate.h \
    src/hdbxmacros.h \
    src/db/connection.h \
    src/mysql/mysqlconnection.h \
    src/db/row.h \
    src/mysql/mysqlresult.h \
    src/db/result.h \
    src/mysql/mysqlrow.h \
    src/db/xvariant.h \
    src/db/dbschema.h \
    src/db/dbschemaprivate.h \
    src/hdb/mysqlhdbschema.h \
    src/db/xvariantlist.h \
    src/db/xvariantprivate.h \
    src/db/resultlistenerinterface.h \
    src/hdbextractorlistener.h \
    src/hdbpp/mysqlhdbppschema.h \
    src/configurationparser.h \
    src/queryconfiguration.h \
    src/db/configurabledbschema.h \
    src/db/helpers/configurabledbschemahelper.h \
    src/utils/datasiever.h \
    src/utils/private/datasieverprivate.h \
    src/utils/xvariantprinter.h \
    src/utils/datasieverprogresslistener.h \
    src/db/timeinterval.h \
    src/db/datetimeutils.h


lib.path = $${INSTALL_ROOT}/lib

LIBTARGET = lib$${TARGET}

lib.files = $${LIBTARGET}.so.$${VERSION}
lib.commands =  ln \
    -sf \
    $${LIBTARGET}.so.$${VERSION} \
    $${LIB_DIR}/$${LIBTARGET}.so.$${VER_MAJ} \
    && \
    ln \
    -sf \
    $${LIBTARGET}.so.$${VER_MAJ} \
    $${LIB_DIR}/$${LIBTARGET}.so

inc.files = $${HEADERS}
inc.path = $${INC_DIR}


doc.commands = doxygen \
    Doxyfile;
doc.files = doc/
doc.path = $${DOC_DIR}

LIBS += -lmysqlclient -lpthread

INSTALLS += lib inc doc

DISTFILES += \
    Makefile.am \
    src/Makefile.am \
    configure.in

