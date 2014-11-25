#ifndef QHDBXERRORQUERYEVENT_H
#define QHDBXERRORQUERYEVENT_H

#include <QEvent>

class QHdbXErrorQueryEvent : public QEvent
{
public:
    QHdbXErrorQueryEvent();

    QString source;

    double startTime, stopTime;
};

#endif // QHDBXERRORQUERYEVENT_H
