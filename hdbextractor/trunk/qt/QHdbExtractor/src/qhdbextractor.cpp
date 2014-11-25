#include "qhdbextractor.h"
#include "ui_mainwindow.h"
#include <math.h>
#include <plotscenewidget.h>
#include <datasiever.h>
#include <timescalelabel.h>
#include <markeritem.h>
#include <curveitem.h>
#include <linepainter.h>
#include <stepspainter.h>
#include <dotspainter.h>
#include <scenecurve.h>
#include <hdbxmacros.h>
#include <datasiever.h>
#include <qhdbextractorproxy.h>
#include <configurationparser.h>
#include <queryconfiguration.h>
#include <QTimer>
#include <QMessageBox>
#include <QtDebug>

QHdbExtractor::QHdbExtractor(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    QueryConfiguration *queryConf = NULL;
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
    connect(plot, SIGNAL(clicked(QPointF)), this, SLOT(plotClicked(QPointF)));

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
    if(qApp->arguments().count() == 2)
    {
        std::map<std::string, std::string> confmap ;
        confmap["dbuser"] = "hdbbrowser";
        confmap["dbpass"] = "hdbbrowser";
        confmap["dbhost"] = "fcsproxy";
        confmap["dbname"] = "hdb";
        confmap["dbport"] = "3306";


        bool ok;
        ConfigurationParser cp;
        printf("reading config from %s\n", qApp->arguments().at(1).toStdString().c_str());
        cp.read(qApp->arguments().at(1).toStdString().c_str(), confmap);
        printf("read\n");
        host = QString::fromStdString(confmap["dbhost"]);
        db = QString::fromStdString(confmap["dbname"]);
        u = QString::fromStdString(confmap["dbuser"]);
        pass = QString::fromStdString(confmap["dbpass"]);
        if(QString::fromStdString(confmap["dbport"]).toInt(&ok) > 0 && ok)
            port = QString::fromStdString(confmap["dbport"]).toInt();

        queryConf = new QueryConfiguration();
        queryConf->loadFromFile(qApp->arguments().at(1).toStdString().c_str());
    }
    else if(qApp->arguments().count() > 4)
    {
        host = ar.at(1);
        db = ar.at(2);
        u = ar.at(3);
        pass = ar.at(4);

        if(qApp->arguments().count() > 5)
            port = ar.at(5).toUShort();
    }
    else
    {
        QMessageBox::information(this, "Usage",
                QString("Usage: %1 host database user password [port (=3306 by default)]\nor %1 configfile\n")
                                 .arg(ar.first()));
        exit(EXIT_FAILURE);
    }

    QHdbextractorProxy *hdbxp = new QHdbextractorProxy(this);
    Hdbextractor::DbType dbt;
    if(db == "hdb")
        dbt = Hdbextractor::HDBMYSQL;
    else if(db == "hdbpp")
        dbt = Hdbextractor::HDBPPMYSQL;
    else
    {
        QMessageBox::information(this, "Available databases", "Available databases: \"hdb\", \"hdbpp\"");
        exit(EXIT_FAILURE);
    }
    hdbxp->connect(dbt, host, db, u, pass, port);
    hdbxp->setUpdateProgressStep(20);
    hdbxp->getHdbExtractor()->setQueryConfiguration(queryConf);

    ui->configWidget->setConfig(host, db, u);


    /* signal/slot connections */
    /* 1a. data ready (RO) */
    connect(hdbxp, SIGNAL(dataReady(const QString &, const QVector<double>&, const QVector<double>&)),
            this, SLOT(onNewDataAvailable(const QString&, const QVector<double>&, const QVector<double>&)));

    /* 1b. data ready (RO) */
    connect(hdbxp, SIGNAL(dataReady(const QString &,
                                    const QVector<double>&,
                                    const QVector<double>&,
                                    const QVector<double>&)),
            this, SLOT(onNewDataAvailable(const QString&,
                                          const QVector<double>&,
                                          const QVector<double>&,
                                          const QVector<double>&)));

    /* 2. progress */
    connect(hdbxp, SIGNAL(sourceExtractionProgress(QString, int, int)),
            this, SLOT(onExtractionProgress(QString, int, int)));

    connect(hdbxp, SIGNAL(sourceExtractionFinished(QString, int, int, double)), this,
            SLOT(onExtractionFinished(QString, int, int, double)));
    /* 3. errors */
    connect(hdbxp, SIGNAL(errorOccurred(QString)), this, SLOT(onError(QString)));

    connect(ui->rbDots, SIGNAL(toggled(bool)), this, SLOT(radioCurvesStyleToggled(bool)));
    connect(ui->rbLines, SIGNAL(toggled(bool)), this, SLOT(radioCurvesStyleToggled(bool)));
    connect(ui->rbSteps, SIGNAL(toggled(bool)), this, SLOT(radioCurvesStyleToggled(bool)));

    /* sources tree widget */
    connect(ui->configWidget, SIGNAL(buttonLoadSrcsFromDbClicked()), hdbxp, SLOT(getSourcesList()));
    connect(hdbxp, SIGNAL(sourcesListReady(QStringList)), this, SLOT(sourcesListReady(QStringList)));
    QTimer::singleShot(600, hdbxp, SLOT(getSourcesList()));

}

QHdbExtractor::~QHdbExtractor()
{
    delete ui;
}

void QHdbExtractor::sourcesListReady(const QStringList &srclist)
{
    ui->configWidget->updateSourcesList(srclist);
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

    ui->configWidget->updateHistory();

}

void QHdbExtractor::onError(const QString& message)
{
    QMessageBox::critical(this, "An error occurred", message);
    ui->configWidget->setState("An error occurred");
}

void QHdbExtractor::mAddCurve(const QString& source, const QColor& color, bool read)
{
    PlotSceneWidget *plot = findChild<PlotSceneWidget *>();
    SceneCurve * c = plot->addCurve(source);
    CurveItem *curveItem = new CurveItem(c);
    curveItem->setObjectName("CurveItem_" + source);
    plot->scene()->addItem(curveItem);
    c->installCurveChangeListener(curveItem);
    if(ui->rbDots->isChecked())
    {
        DotsPainter *dp  = new DotsPainter(curveItem);
        dp->setDotsColor(color);
        if(!read) /* a darker border for write */
            dp->setBorderColor(color.darker());
    }
    else if(ui->rbLines->isChecked())
    {
        LinePainter *lp = new LinePainter(curveItem);
        lp->setLineColor(color);
        if(!read)
            lp->setLinePen(QPen(color, 0, Qt::DashLine));
    }
    else if(ui->rbSteps->isChecked())
    {
        StepsPainter *sp = new StepsPainter(curveItem);
        sp->setLineColor(color);
        if(!read)
            sp->setLinePen(QPen(color, 0, Qt::DashLine));
    }

}

void QHdbExtractor::onNewDataAvailable(const QString& source,
                                       const QVector<double>& timestamps,
                                       const QVector<double>& read_data,
                                       const QVector<double>& write_data)
{
    qDebug() << __FUNCTION__ << "size " << read_data.size() << "w " << write_data.size();
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

    PlotSceneWidget *plot = findChild<PlotSceneWidget *>();
    /* divide by 2 curves size because in read/write case we add two curves at time
     */
    QColor color = palette.at((plot->getCurves().size() / 2) % palette.size());
    if(!plot->findCurve(source))
        mAddCurve(source, color, true);
    if(!plot->findCurve(source + "_WRI"))
        mAddCurve(source + "_WRI", color.darker(), false);

    plot->appendData(source, timestamps, read_data);
    plot->appendData(source + "_WRI", timestamps, write_data);
    qDebug() << "writable data size " << write_data.size() << "read data sz " << read_data.size() << write_data;
}

void QHdbExtractor::onNewDataAvailable(const QString& source,
                                       const QVector<double>& timestamps,
                                       const QVector<double>& data)
{
    qDebug() << __FUNCTION__ << "size " << data.size();
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

    PlotSceneWidget *plot = findChild<PlotSceneWidget *>();
    /* divide by 2 curves size because in read/write case we add two curves at time
     */
    QColor color = palette.at((plot->getCurves().size() / 2) % palette.size());
    if(!plot->findCurve(source))
        mAddCurve(source, color, true);
    foreach(double ts, timestamps)
        qDebug() << "->" << QDateTime::fromTime_t((time_t) ts);
    plot->appendData(source, timestamps, data);
}

void QHdbExtractor::onExtractionFinished(const QString& source, int srcStep, int srcTotal, double elapsed)
{
    qDebug() << __FUNCTION__ << source << srcStep << srcTotal << elapsed;
    ui->pbSrcs->setVisible(srcStep != srcTotal);
    ui->pbSrcs->setFormat(source + " %p ");
    ui->pbSrcs->setValue(srcStep);
    ui->pbSrcs->setMaximum(srcTotal);
    ui->statusBar->showMessage(QString("Extracted %1 sources in %2s").arg(srcTotal).arg(elapsed));
}

void QHdbExtractor::onExtractionProgress(const QString& source, int step, int total)
{
    qDebug() << __FUNCTION__ << source << source << step << total;
    ui->progressBar->setVisible(step != total);
    ui->progressBar->setFormat(source + " %p ");
    ui->progressBar->setMaximum(total);
    ui->progressBar->setValue(step);
}

void QHdbExtractor::radioCurvesStyleToggled(bool t)
{
    if(t)
    {
        QColor curveColor = Qt::black;
        PlotSceneWidget *plot = findChild<PlotSceneWidget *>();
        QList<SceneCurve *> curves = plot->getCurves();
        foreach(SceneCurve *c, curves)
        {
            CurveItem *ci = c->curveItem();
            ItemPainterInterface *ipi = ci->itemPainter();
            int type = ipi->type();
            if(type == ItemPainterInterface::Dot)
                curveColor = static_cast<DotsPainter *>(ipi)->borderColor();
            else if(type == ItemPainterInterface::Line || type == ItemPainterInterface::Step)
                curveColor = static_cast<LinePainter *>(ipi)->lineColor();

            ci->removeItemPainterInterface(ipi);
            delete ipi;

            if(sender()->objectName() == "rbDots" && type != ItemPainterInterface::Dot)
            {
                DotsPainter *dp = new DotsPainter(ci);
                dp->setBorderColor(curveColor);
            }
            else if(sender()->objectName() == "rbLines" && type != ItemPainterInterface::Line)
            {
                LinePainter *lp = new LinePainter(ci);
                lp->setLineColor(curveColor);
            }
            else if(sender()->objectName() == "rbSteps" && type != ItemPainterInterface::Step)
            {
                StepsPainter *sp = new StepsPainter(ci);
                sp->setLineColor(curveColor);
            }
        }
    }
}

void QHdbExtractor::plotClicked(const QPointF& point)
{
    PlotSceneWidget *plot = findChild<PlotSceneWidget *>();
    QPointF closestPos;
    int index;
    QList<SceneCurve*> curves = plot->getClosest(closestPos, &index, point);
    foreach(SceneCurve *sc, curves)
    {
        double y = sc->data()->yData.at(index);
        qDebug() << sc->name() << closestPos.x() << closestPos.y() << "x" << sc->data()->xData.at(index) <<
                    y;
        if(isnan(y))
        {
            QHdbextractorProxy *hdbxp = findChild< QHdbextractorProxy *>();

        }
    }
}

