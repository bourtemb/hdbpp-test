#ifndef QHDBXUTILS_H
#define QHDBXUTILS_H

#include <xvariant.h>
#include <vector>
#include <QVector>
#include <QStringList>

/** \page qhdbutils Utilities to arrange XVariant data fetch results into more convenient
 *        forms to be used with Qt plots and libraries.
 *
 * This class provides methods to convert inputs in the form of std::vector<XVariant> into
 * Qt QVector arrays that are more friendly for Qt application and QTango/qtcontrols/QGraphicsplot
 * libraries.
 */
class QHdbXUtils
{
public:
    QHdbXUtils();

public:

    void toTimestampDataDoubleVector(const std::vector<XVariant> &indata,
                                     QVector<double> &timestamps,
                                     QVector<double> &data, bool *ok = NULL);

    void toTimestampDataDoubleVector(const std::vector<XVariant> &indata,
                                     QVector<double> &timestamps,
                                     QVector<double> &rdata,
                                     QVector<double> &wdata,
                                     bool *ok = NULL);

    void toTimestampErrorDataVector(const std::vector<XVariant> &indata,
                                    QVector<double> &timestamps,
                                    QVector<int> &codes,
                                    QStringList &messages);
};

#endif // QHDBXUTILS_H
