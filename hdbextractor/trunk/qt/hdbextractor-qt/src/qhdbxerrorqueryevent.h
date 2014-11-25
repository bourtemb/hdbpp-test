#ifndef QHDBXERRORQUERYEVENT_H
#define QHDBXERRORQUERYEVENT_H

#include <qhdbxevent.h>

class QHdbXErrorQueryEvent : public QHdbXEvent
{
public:
    QHdbXErrorQueryEvent();

    QString source;

    double startTime, stopTime;
};

#endif // QHDBXERRORQUERYEVENT_H
