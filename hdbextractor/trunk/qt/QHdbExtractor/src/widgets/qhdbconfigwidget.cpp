#include "qhdbconfigwidget.h"
#include <hdbxmacros.h>
#include <QTimer>
#include <QtDebug>
#include <QSettings>
#include <QDateTime>

QHdbConfigWidget::~QHdbConfigWidget()
{
    qDebug() << __FUNCTION__;

    /* save items in views  */
    QSettings s;
    QStringList items;
    foreach(QTreeWidgetItem *it, ui.twSelected->findItems("*", Qt::MatchWildcard))
        items << it->text(0);
    s.setValue("SourcesList", items);

    QDate ds = ui.calStart->selectedDate();
    QTime ts = ui.teStart->time();
    QDateTime dts(ds, ts);
    QDate de = ui.calStop->selectedDate();
    QTime te = ui.teStop->time();
    QDateTime dte(de, te);

    s.setValue("startDate", dts);
    s.setValue("stopDate", dte);
}

QHdbConfigWidget::QHdbConfigWidget(QWidget *parent) :
    QWidget(parent)
{
    ui.setupUi(this);
    resize(minimumSizeHint());

    QTimer::singleShot(400, this, SLOT(init()));

    m_viewSourcesListChanged();
}

void QHdbConfigWidget::init()
{
    connect(ui.cbLastDaysHours, SIGNAL(toggled(bool)), ui.sbDaysHours, SLOT(setEnabled(bool)));
    connect(ui.cbLastDaysHours, SIGNAL(toggled(bool)), ui.cbDaysHours, SLOT(setEnabled(bool)));

    connect(ui.pbRemoveSrc, SIGNAL(clicked()), this, SLOT(removeSourceClicked()));
    connect(ui.pbAdd, SIGNAL(clicked()), this, SLOT(addSourceClicked()));

    connect(ui.pbView, SIGNAL(clicked()), this, SIGNAL(viewClicked()));
    connect(ui.pbCancel, SIGNAL(clicked()), this, SIGNAL(cancelClicked()));

    ui.cbLastDaysHours->setChecked(false);

    QDateTime dt = QDateTime::currentDateTime();
    ui.calStop->setSelectedDate(dt.date());
    ui.teStop->setTime(dt.time());
    dt = dt.addDays(-1);
    ui.calStart->setSelectedDate(dt.date());
    ui.teStart->setTime(dt.time());

    ui.sbDaysHours->setDisabled(true);
    ui.cbDaysHours->setDisabled(true);
    connect(ui.sbDaysHours, SIGNAL(valueChanged(int)), this, SLOT(lastDaysHoursChanged()));
    connect(ui.cbDaysHours, SIGNAL(currentIndexChanged(int)), this, SLOT(lastDaysHoursChanged()));

    /* restore previous values */
    QSettings s;
    QStringList savedItems = s.value("SourcesList").toStringList();
    foreach(QString src, savedItems)
        new QTreeWidgetItem(ui.twSelected, QStringList() << src);

    QDateTime dts = s.value("startDate").toDateTime();
    QDateTime dte = s.value("stopDate").toDateTime();

    ui.calStart->setSelectedDate(dts.date());
    ui.calStop->setSelectedDate(dte.date());
    ui.teStart->setTime(dts.time());
    ui.teStop->setTime(dte.time());

    m_viewSourcesListChanged();
}

QDateTime QHdbConfigWidget::startDateTime() const
{
    QDateTime dt;
    dt.setDate(ui.calStart->selectedDate());
    dt.setTime(ui.teStart->time());
    return dt;
}

QDateTime QHdbConfigWidget::stopDateTime() const
{
    QDateTime dt;
    dt.setDate(ui.calStop->selectedDate());
    dt.setTime(ui.teStop->time());
    return dt;
}

QStringList QHdbConfigWidget::m_sourcesFromTree(QTreeWidget *tree) const
{
    QStringList ret;
    QList<QTreeWidgetItem *> items;
    QTreeWidget *stw = findChild<QTreeWidget *>(tree->objectName());
    if(stw)
        items = stw->findItems("*", Qt::MatchWildcard);
    foreach(QTreeWidgetItem *it, items)
    {
        if(it->text(0).length() > 0)
            ret << it->text(0);
    }

    qDebug() << "QHdbConfigWidget::m_sourcesFromTree() " << ret;
    return ret;
}

QStringList QHdbConfigWidget::sources() const
{
    qDebug() << "sources() " << m_sourcesFromTree(ui.twSelected);
    return m_sourcesFromTree(ui.twSelected);
}

void QHdbConfigWidget::removeSourceClicked()
{
    QList<QTreeWidgetItem* > srcs = ui.twSelected->selectedItems();
    foreach(QTreeWidgetItem * it, srcs)
    {
        emit sourceRemoved(it->text(0));
        delete it;
    }
    m_viewSourcesListChanged();
}

void QHdbConfigWidget::addSourceClicked()
{
    QTreeWidgetItem *newIt = new QTreeWidgetItem(ui.twSelected);
    newIt->setFlags(newIt->flags()|Qt::ItemIsEditable);
    newIt->setSelected(true);
    ui.twSelected->editItem(newIt);
}

void QHdbConfigWidget::m_viewSourcesListChanged()
{
    ui.gbFrom->setEnabled(sources().size() > 0);
    ui.gbTo->setEnabled(sources().size() > 0);

    QStringList srcs = sources();
    int ssize = srcs.size();

    if(ssize > 1 )
        ui.labelSource->setText(QString("%1, %2...").arg(srcs.first(), srcs.at(1)));
    else if(ssize == 1)
        ui.labelSource->setText(srcs.first());
}

void QHdbConfigWidget::lastDaysHoursChanged()
{
    QDateTime dt = QDateTime::currentDateTime();
    ui.calStop->setSelectedDate(dt.date());
    ui.teStop->setTime(dt.time());
    switch(ui.cbDaysHours->currentIndex())
    {
    case 0: /* days */
        dt = dt.addDays(-ui.sbDaysHours->value());
        break;
       case 1: /* Hours */
        dt.addSecs(-ui.sbDaysHours->value() * 3600);
        break;
    case 2:
        dt.addSecs(-ui.sbDaysHours->value() * 60);
        break;
    case 3:
        dt.addSecs(-ui.sbDaysHours->value());
        break;
    default:
        break;
    }
    ui.calStart->setSelectedDate(dt.date());
    ui.teStart->setTime(dt.time());
}


