#ifndef THDBCONFIGWIDGET_H
#define THDBCONFIGWIDGET_H

#include <QWidget>
#include "ui_configWidget.h"

class EPlotCurve;
class SourcesTreeWidget;

/** \brief a configuration widget for the historical database.
  *
  * This widget contains a set of controls to get data from the historical
  * database.
  */
class QHdbConfigWidget : public QWidget
{
    Q_OBJECT
public:
    explicit QHdbConfigWidget(QWidget *parent = 0);

    virtual ~QHdbConfigWidget();

    QDateTime startDateTime() const;

    QDateTime stopDateTime() const;

    QStringList sources() const;

    void setSources(const QStringList& src);

signals:

    void sourceRemoved(const QString& src);

    void viewClicked();

    void cancelClicked();


protected slots:

    void removeSourceClicked();

    void addSourceClicked();

    void lastDaysHoursChanged();

    void init();

private:
    Ui::ConfigWidget ui;

    void m_viewSourcesListChanged();

    QStringList m_sourcesFromTree(QTreeWidget *tree) const;
};

#endif // THDBCONFIGDIALOG_H
