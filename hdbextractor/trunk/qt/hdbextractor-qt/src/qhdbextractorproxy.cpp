#include "qhdbextractorproxy.h"
#include "qhdbextractorthread.h"
#include "qhdbextractorproxyprivate.h"
#include "qhdbxconnectionevent.h"
#include "qhdbxqueryevent.h"
#include "qhdbnewdataevent.h"
#include "qhdbxerrorevent.h"
#include "qhdbxutils.h"
#include <QDateTime>
#include <QStringList>
#include <QCoreApplication> /* for postevent */
#include <QtDebug>
#include <vector>
#include <hdbxmacros.h>

QHdbextractorProxy::~QHdbextractorProxy()
{
    qDebug() << __FUNCTION__ << "waiting for thread [20 secs]...";
    delete d_ptr->thread;
    delete d_ptr;
}


/** \brief The class constructor of the QHdbextractorProxy
 *
 * Use the constructor to allocate a QHdbextractorProxy. You can pass a QObject as
 * a parent for this class so that it gets automatically destroyed when the parent
 * is destroyed.
 *
 * @param parent a parent QObject or NULL.
 *
 * \par Example. A graphical user interface wants to retrieve data from an Hdb database and mysql server.
 *
 * In the constructor, allocate the QHdbextractorProxy and connect it to the database:
 * \code
  // Create a QHdbextractorProxy
  //
  QHdbextractorProxy *qhdbp = new QHdbextractorProxy(this); // this is a pointer to a QObject, e.g. a GUI
  // use hdb schema on a mysql server. Provide host, database name, user passwor and an optional port
  //
  qhdbp->connect(Hdbextractor::HDBMYSQL, host, db, u, pass, port);
  // Qt's signal/slot connections
  // 1. be notified when data is ready
  QObject::connect(qhdbp, SIGNAL(dataReady(const QString &, const QVector<double>&, const QVector<double>&)),
            this, SLOT(onNewDataAvailable(const QString&, const QVector<double>&, const QVector<double>&)));
  // 2. update a progress bar upon new data arrival
  qhdbp->setUpdateProgressStep(20); // be notified every 20 rows extracted
  QObject::connect(qhdbp, SIGNAL(sourceExtractionProgress(QString, int, int)),
            this, SLOT(onExtractionProgress(QString, int, int)));
  // 3. extraction finished
  QObject::connect(qhdbp, SIGNAL(sourceExtractionFinished(QString, int, int, double)), this,
            SLOT(onExtractionFinished(QString, int, int, double)));
  // 4. errors
  QObject::connect(qhdbp, SIGNAL(errorOccurred(QString)), this, SLOT(onError(QString)));
  \endcode
 *
 * Then, in response to a click of a button, forward a query to the database:
 \code
  // the elements of the UI provide the start date, stop date and the tango attribute name
  //
  QDateTime startDt = ui->startDateTimeEdit->startDateTime();
  QDateTime stopDt  = ui->stopDateTimeEdit->stopDateTime();
  QString   source  = ui->lineEditSource->text();

  // forward the query with the desired parameters
  qhdbp->getData(source, startDt, stopDt);

 \endcode
 *
 * Here's the slot in charge of updating a plot with the new data
 \code
  void QHdbExtractor::onNewDataAvailable(const QString& source,
                                       const QVector<double>& timestamps,
                                       const QVector<double>& data)
  {
      // Get the plot and append new data
      PlotSceneWidget *plot = findChild<PlotSceneWidget *>();
      plot->appendData(source, timestamps, data);
  }
 \endcode
 *
 * @see getData
 * @see connect
 * @see disconnect
 * @see sourceExtractionProgress
 * @see sourceExtractionFinished
 * @see dataReady
 */
QHdbextractorProxy::QHdbextractorProxy(QObject *parent) : QObject(parent)
{
    d_ptr = new QHdbextractorProxyPrivate();
    d_ptr->thread = new QHdbextractorThread(this);
    /* DirectConnection strikes the fact that the Hdbextractor library is thread safe! */
    QObject::connect(d_ptr->thread, SIGNAL(sourceExtractionProgress(QString,int,int)),
                     this, SLOT(onUpdate(QString,int,int)), Qt::DirectConnection);
    QObject::connect(d_ptr->thread, SIGNAL(sourceExtractionFinished(const QString& , int , int , double )),
                     this, SLOT(onFinish(const QString& , int , int , double)), Qt::DirectConnection);
    QObject::connect(d_ptr->thread, SIGNAL(errorMessage(QString)), this, SLOT(onError(QString)), Qt::DirectConnection);
    d_ptr->thread->start();
}

/** \brief set the number of rows after which a progress update must be triggered on the listener.
 *
 * @param numRows every numRows onProgressUpdate is invoked
 *
 * This is a convenience shortcut for getHdbExtractor()->setUpdateProgressStep(numRows)
 *
 * @see progress signal
 */
void QHdbextractorProxy::setUpdateProgressStep(int numRows)
{
    d_ptr->thread->getHdbExtractor()->setUpdateProgressStep(numRows);
}

/** \brief Returns the update notification progress step, in units of number of rows extracted.
 *
 * @see setUpdateProgressStep
 *
 * This is a convenience shortcut for getHdbExtractor()->updateProgressStep
 *
 */
int QHdbextractorProxy::updateProgressStep() const
{
    return d_ptr->thread->getHdbExtractor()->updateProgressStep();
}

/** \brief Try to establish a database connection with the specified host, user, password and database name.
 *         This is a proxy method on Hdbextractor::connect
 *
 * @param dbType A type of database as defined in the DbType enum
 * @param host the internet host name of the database server
 * @param user the user name of the database
 * @param passwd the password for that username
 * @param port the database server port (default 3306, thought for mysql)
 *
 * Throws an exception upon failure.
 */
void QHdbextractorProxy::connect(Hdbextractor::DbType dbType,
                                 const QString& host,
                                 const QString& db,
                                 const QString& user,
                                 const QString& passwd,
                                 unsigned short port)
{
    qDebug() << __FUNCTION__ << host << db << user << passwd;
    QHdbXConnectionEvent *connectionEvent = new QHdbXConnectionEvent(dbType, host, db, user, passwd, port);
    d_ptr->thread->addEvent(connectionEvent);
}

/** \brief Start fetching data from the database. When data is available, you can get it
 *         with the HdbExtractor::get method inside onFinished or onProgressUpdate, if
 *         partial data updates are preferred.
 *
 * @param source the name of a tango device attribute, full name, e.g. domain/family/member/attName
 * @param start_date the start date in the form "2014-07-10 10:00:04"
 * @param stop_date  the stop date in the form "2014-07-10 10:20:04"
 *
 * @see Hdbextractor::setUpdateProgressStep
 *
 */
void QHdbextractorProxy::getData(const QString& source, const QDateTime& start_date, const QDateTime& stop_date)
{
    QHdbXQueryEvent *qe = new QHdbXQueryEvent(QStringList() << source,
                                              start_date.toString("yyyy-MM-dd hh:mm:ss"),
                                              stop_date.toString("yyyy-MM-dd hh:mm:ss"));
    d_ptr->thread->addEvent(qe);
}

/** \brief Disconnect from the database
 *
 */
void QHdbextractorProxy::disconnect()
{
    d_ptr->thread->addEvent(new QHdbXEvent(QHdbXEvent::DISCONNECT));
}

/** \brief returns the reference to the Hdbextractor of which QHdbextractorProxy is the
 *         owner
 *
 * @return the reference to the Hdbextractor used by this proxy
 *
 * @see Hdbextractor
 */
Hdbextractor *QHdbextractorProxy::getHdbExtractor() const
{
    return d_ptr->thread->getHdbExtractor();
}

/** \brief get data for multiple sources
 *
 * To be implemented
 */
void QHdbextractorProxy::getData(const QStringList& sources,
                                      const QDateTime &start_date,
                                      const QDateTime &stop_date)
{
    QHdbXQueryEvent *qe = new QHdbXQueryEvent(sources,
                                              start_date.toString("yyyy-MM-dd hh:mm:ss"),
                                              stop_date.toString("yyyy-MM-dd hh:mm:ss"));
    d_ptr->thread->addEvent(qe);
}

/** This slot is invoked when data is ready (in partial update mode)
 *
 * This is a slot connected to the database thread through a direct connection.
 * This means that the slot is invoked on the database thread.
 * In order to safely use signal/slot connections between a graphical interface and
 * QHdbextractorProxy, an Event is posted and the signal is emitted from there.
 *
 */
void QHdbextractorProxy::onUpdate(const QString &srcname, int step,
                                  int totalSteps)
{
    qDebug() << __FUNCTION__ << QThread::currentThread() << step << totalSteps;
    Hdbextractor *hdbx = d_ptr->thread->getHdbExtractor();
    if(hdbx->updateProgressStep() > 0)
    {
        std::vector<XVariant> data;
        QHdbNewDataEvent *nd = new QHdbNewDataEvent(data, srcname, step, totalSteps);
        /* make just one copy */
        hdbx->get(nd->data);
        for(size_t i = 0; i < nd->data.size(); i++)
            printf("QHdbextractorProxy::onUpdate: %s (after get)\n", nd->data.at(i).getTimestamp());
        qApp->postEvent(this, nd);
     //   printData(data);

    }
}

void QHdbextractorProxy::onFinish(const QString &srcname, int srcStep, int totSrcs, double elapsed)
{
    qDebug() << __FUNCTION__ << QThread::currentThread() << srcStep;
    Hdbextractor *hdbx = d_ptr->thread->getHdbExtractor();
  //  if(hdbx->updateProgressStep() <= 0) /* WHY? */
    {
        /* copy whole data */
        std::vector<XVariant> data;
        QHdbNewDataEvent *nd = new QHdbNewDataEvent(data, srcname, srcStep, totSrcs, elapsed);
        /* make just one copy */
        hdbx->get(nd->data);
        qApp->postEvent(this, nd);

    //    printData(data);
    }
}

void QHdbextractorProxy::onError(const QString& message)
{
    QHdbXErrorEvent *ee = new QHdbXErrorEvent(message);
    qApp->postEvent(this, ee);
}

bool QHdbextractorProxy::event(QEvent *e)
{
    if(e->type() == QEvent::User + 1001)
    {
        QHdbNewDataEvent *nde = static_cast<QHdbNewDataEvent *>(e);
        // qDebug() << __FUNCTION__ << QThread::currentThread() << nde->step << nde->total;

//        for(size_t i = 0; i < nde->data.size(); i++)
//            printf("\e[1;33mQHdbextractorProxy::event: %s\e[0m\n", nde->data.at(i).getTimestamp());

       // printData(nde->data);
        /* extract data according to the type/format/writable and then emit the apt signals */
        dataNotify(nde);

//        QHdbXUtils utils;
//        QVector<double> timestamps, out_data;
//        utils.toTimestampDataDoubleVector(nde->data, timestamps, out_data);
//        QVector<QDateTime> dtv;
//        foreach(double ts, timestamps)
//            dtv << QDateTime::fromTime_t((uint) ts);

       // if(nde->data.size() > 0)
       //     qDebug() << "chunk" << nde->data[0].getSource() << dtv;

        return true;
    }
    else if(e->type() == QEvent::User + 1002) /* error */
    {
        emit errorOccurred(static_cast<QHdbXErrorEvent *>(e)->message);
    }
    return QObject::event(e);
}

void QHdbextractorProxy::dataNotify(QHdbNewDataEvent *e)
{
    const QString& source = e->source;
    /* discover the type of data carried by the argument */
    if(e->data.size() > 0)
    {
        QHdbXUtils utils;
        XVariant::DataType dt = e->data[0].getType();
        XVariant::Writable w = e->data[0].getWritable();
        XVariant::DataFormat fmt = e->data[0].getFormat();
        if((dt == XVariant::Double || dt == XVariant::Int || dt == XVariant::UInt)
                && fmt == XVariant::Scalar)
        {
            if(w == XVariant::RO || w == XVariant::WO)
            {
                QVector<double> timestamps, out_data;
                utils.toTimestampDataDoubleVector(e->data, timestamps, out_data);
                emit dataReady(source, timestamps, out_data);
            }
            else if(w == XVariant::RW)
            {
                QVector<double> timestamps, out_read_data, out_write_data;
                utils.toTimestampDataDoubleVector(e->data, timestamps, out_read_data, out_write_data);
                emit dataReady(source, timestamps, out_read_data, out_write_data);
            }

            /* progress update signals */
            if(e->updateType == QHdbNewDataEvent::Progress)
                emit sourceExtractionProgress(source, e->step, e->totalSteps);
            else if(e->updateType == QHdbNewDataEvent::Finish)
                emit sourceExtractionFinished(source, e->sourceStep, e->totalSources, e->elapsed);
        }
    }
}

void QHdbextractorProxy::printData(const std::vector<XVariant> &data)
{
    foreach(XVariant xv, data)
    {
        if(xv.getFormat() == XVariant::Vector && xv.getWritable() == XVariant::RO)
            qDebug() << "@" << xv.getTimestamp() << ":\n" << QVector<double>::fromStdVector(xv.toDoubleVector());
        else if(xv.getFormat() == XVariant::Scalar && xv.getWritable() == XVariant::RO)
            qDebug() << "@" << xv.getTimestamp() << ":" << xv.toDouble();
        else if(xv.getFormat() == XVariant::Scalar && xv.getWritable() == XVariant::RW)
            qDebug() << "@" << xv.getTimestamp() << ": R:" << xv.toDouble() << "W: " << xv.toDouble(false);
        else if(xv.getFormat() == XVariant::Vector && xv.getWritable() == XVariant::RW)
            qDebug() << "@" << xv.getTimestamp() << ":\n R:" << QVector<double>::fromStdVector(xv.toDoubleVector())
                     << "\n W: " << QVector<double>::fromStdVector(xv.toDoubleVector(false));
    }
}


