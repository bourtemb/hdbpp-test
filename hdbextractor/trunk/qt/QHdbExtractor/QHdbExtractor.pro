#-------------------------------------------------
#
# Project created by QtCreator 2011-06-01T15:01:49
#
#-------------------------------------------------

unix:INCLUDEPATH += \
    /runtime/include/qgraphicsplot \
    /runtime/include/hdbextractor/qt \
    /runtime/include/hdbextractor/ \
    src/widgets

QT       += core gui widgets

contains(QT_VERSION, ^5\\..*\\..*) {
    VER_SUFFIX = -qt5
} else {
    VER_SUFFIX =
}

UI_DIR = ui
OBJECTS_DIR = obj

TARGET = bin/qhdbextractor
TEMPLATE = app

QMAKE_CLEAN += core*

SOURCES += src/main.cpp\
    src/widgets/qhdbconfigwidget.cpp \
    src/widgets/qhistoricalviewwidget.cpp \
    src/widgets/qhistoricalviewwidgetprivate.cpp \
    src/qhdbextractor.cpp

HEADERS  += \
    src/widgets/qhdbconfigwidget.h \
    src/widgets/qhistoricalviewwidgetprivate.h \
    src/widgets/qhistoricalviewwidget.h \
    src/qhdbextractor.h

FORMS    += src/mainwindow.ui \
    src/widgets/configWidget.ui

unix:LIBS += -L../.. -lhdbextractor++ -lhdbextractor-qt$${VER_SUFFIX} \
     -L../hdbextractor-qt -L/runtime/lib -lQGraphicsPlot$${VER_SUFFIX}
