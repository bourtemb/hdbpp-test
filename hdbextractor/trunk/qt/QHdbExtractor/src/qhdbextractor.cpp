#include "qhdbextractor.h"
#include "ui_mainwindow.h"
#include <plotscenewidget.h>
#include <timescalelabel.h>
#include <markeritem.h>
#include <curveitem.h>
#include <linepainter.h>
#include <scenecurve.h>
#include <qhdbextractorproxy.h>

#include <QMessageBox>
#include <QtDebug>

QHdbExtractor::QHdbExtractor(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    QString host = "-", db = "-", u = "-", pass = "-";
    unsigned short port = 3306;
    ui->setupUi(this);
    ui->progressBar->setVisible(false);
    ui->pbSrcs->setVisible(false);
    /* register view widgets */
    connect(ui->configWidget, SIGNAL(viewClicked()), this, SLOT(slotViewClicked()));
    connect(ui->pbConfigure, SIGNAL(clicked()), this, SLOT(slotConfigureClicked()));

    /* for scalars, associate  a plot */

    PlotSceneWidget *plot = new PlotSceneWidget(this);
    plot->setObjectName("Historical Plot Scene Widget");
    TimeScaleLabel *timeScaleLabel = new TimeScaleLabel();
    timeScaleLabel->setShowDate(true);
    plot->xScaleItem()->installScaleLabelInterface(timeScaleLabel);
    plot->setSettingsKey(qApp->applicationName());
    //  plot->xScaleItem()->setAxisLabelsOutsideCanvas(true);
    plot->xScaleItem()->setAxisLabelsRotation(65);
    plot->xScaleItem()->setAxisTitle("Date/Time");
    //  plot->setDefaultYAxisOriginPosPercentage(0);
    plot->setMouseZoomEnabled(true);

    plot->yScaleItem()->setAxisLabelsFormat("%g");

    MarkerItem* marker = new MarkerItem(0);
    plot->installMouseEventListener(marker);
    plot->scene()->addItem(marker);
    /* !important! */
    connect(plot, SIGNAL(curveAboutToBeRemoved(SceneCurve*)), marker,
            SLOT(removeCurve(SceneCurve*)));


    ui->historicalViewWidget->registerWidget(plot, QHistoricalViewWidget::NumberScalar);

    /* get cmdline args */
    QStringList ar = qApp->arguments();
    if(qApp->arguments().count() > 4)
    {
        host = ar.at(1);
        db = ar.at(2);
        u = ar.at(3);
        pass = ar.at(4);
    }

    if(qApp->arguments().count() > 5)
        port = ar.at(5).toUShort();
    if(qApp->arguments().count() < 5)
    {
        printf("Usage: %s host database user password [port (=3306 by default)]\n\n",
               ar.first().toStdString().c_str());
        exit(EXIT_FAILURE);
    }

    QHdbextractorProxy *hdbxp = new QHdbextractorProxy(this);
    hdbxp->connect(Hdbextractor::HDBMYSQL, host, db, u, pass, port);
    hdbxp->setUpdateProgressStep(20);

    /* signal/slot connections */
    /* 1. data ready */
    connect(hdbxp, SIGNAL(dataReady(const QString &, const QVector<double>&, const QVector<double>&)),
            this, SLOT(onNewDataAvailable(const QString&, const QVector<double>&, const QVector<double>&)));
    /* 2. progress */
    connect(hdbxp, SIGNAL(sourceExtractionProgress(QString, int, int)),
            this, SLOT(onExtractionProgress(QString, int, int)));

    connect(hdbxp, SIGNAL(sourceExtractionFinished(QString, int, int, double)), this,
            SLOT(onExtractionFinished(QString, int, int, double)));
    /* 3. errors */
    connect(hdbxp, SIGNAL(errorOccurred(QString)), this, SLOT(onError(QString)));
}

QHdbExtractor::~QHdbExtractor()
{
    delete ui;
}

void QHdbExtractor::slotConfigureClicked()
{
    ui->stackedWidget->setCurrentIndex(1);
}

void QHdbExtractor::slotViewClicked()
{
    /* get the reference to the hdb extractor proxy */
    QHdbextractorProxy *hdbxp = findChild<QHdbextractorProxy *>();
    /* get the necessary parameters for the query */
    QDateTime startDt = ui->configWidget->startDateTime();
    QDateTime stopDt = ui->configWidget->stopDateTime();
    QStringList sources = ui->configWidget->sources();

    /* clear all curves from the plot */
    PlotSceneWidget *plot = findChild<PlotSceneWidget *>();
    foreach(SceneCurve *c, plot->getCurves())
        delete c;

    /* forward query
     *
     */
    if(sources.size() == 1)
        hdbxp->getData(sources.first(), startDt, stopDt);
    else if(sources.size() > 0)
        hdbxp->getData(sources, startDt, stopDt);

    /* switch stacked widget index */
    ui->stackedWidget->setCurrentIndex(0);

}

void QHdbExtractor::onError(const QString& message)
{
    QMessageBox::critical(this, "An error occurred", message);
}

void QHdbExtractor::onNewDataAvailable(const QString& source,
                                       const QVector<double>& timestamps,
                                       const QVector<double>& data)
{
    PlotSceneWidget *plot = findChild<PlotSceneWidget *>();
    if(!plot->findCurve(source))
    {
        QList<QColor> palette =
                QList<QColor> ()
                << Qt::darkRed
                << Qt::blue
                << Qt::green
                << Qt::gray
                << Qt::darkYellow
                << Qt::cyan << Qt::darkGreen
                << Qt::magenta
                << Qt::darkBlue << Qt::darkGreen
                << Qt::yellow;

        SceneCurve * c = plot->addCurve(source);
        CurveItem *curveItem = new CurveItem(c);
        curveItem->setObjectName("CurveItem_" + source);
        plot->scene()->addItem(curveItem);
        c->installCurveChangeListener(curveItem);
        LinePainter *lp = new LinePainter(curveItem);
        lp->setLineColor(palette.at(plot->getCurves().size() % palette.size()));
    }

    plot->appendData(source, timestamps, data);

}

void QHdbExtractor::onExtractionFinished(const QString& source, int srcStep, int srcTotal, double elapsed)
{
    qDebug() << __FUNCTION__ << source << srcStep << srcTotal << elapsed;
    ui->pbSrcs->setVisible(srcStep != srcTotal);
    ui->pbSrcs->setFormat(source + " %p ");
    ui->pbSrcs->setValue(srcStep);
    ui->pbSrcs->setMaximum(srcTotal);
}

void QHdbExtractor::onExtractionProgress(const QString& source, int step, int total)
{
    ui->progressBar->setVisible(step != total);
    ui->progressBar->setFormat(source + " %p ");
    ui->progressBar->setMaximum(total);
    ui->progressBar->setValue(step);
}
