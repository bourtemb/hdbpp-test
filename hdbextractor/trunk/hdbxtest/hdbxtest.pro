TEMPLATE = app
CONFIG += console
CONFIG -= app_bundle
CONFIG -= qt

SOURCES += main.cpp \
    myhdbextractorimpl.cpp

INCLUDEPATH += ../src ../db/src


LIBS += -L.. -lhdbextractor++

HEADERS += \
    myhdbextractorimpl.h
