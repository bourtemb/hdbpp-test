#ifndef QHDBEXTRACTORTHREAD_H
#define QHDBEXTRACTORTHREAD_H

#include <QThread>
#include <QVector>
#include <QReadWriteLock>
#include <QQueue>
#include <hdbextractorlistener.h>

class QHdbXEvent;
class Hdbextractor;

class QHdbextractorThread : public QThread,
        public HdbExtractorListener
{
    Q_OBJECT
public:
    explicit QHdbextractorThread(QObject *parent);

    ~QHdbextractorThread();

    void addEvent(QHdbXEvent *hdbxe);

    virtual void onSourceProgressUpdate(const char *name, int step, int totalSteps);

    /** \brief this method is invoked when data extraction is fully accomplished.
     *
     */
    virtual void onSourceExtracted(const char * name, int totalRows, int totalSources, double elapsed);


    Hdbextractor *getHdbExtractor() const;

    void leaveLoop();

signals:

    void sourceExtractionProgress(const QString& srcname, int step, int totalSteps);

    void sourceExtractionFinished(const QString& name, int step, int totalSources, double elapsed);

    void exitLoop();

    void processRequest();

    void errorMessage(const QString& msg);

protected:
    virtual void run();

private slots:
    void process();

public slots:


private:

    QQueue<QHdbXEvent *> m_eventQueue;

    QReadWriteLock m_rwLock;

    Hdbextractor *m_extractor;

};

#endif // QHDBEXTRACTORTHREAD_H
