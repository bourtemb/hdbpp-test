#ifndef QHDBXUTILS_H
#define QHDBXUTILS_H

#include <xvariant.h>
#include <vector>
#include <QVector>

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

    /** \brief Converts input data into a vector of timestamps (converted to double)
     *         and a vector of values. Optionally, checks if the required conversion is
     *         in concert with the type of data stored by the XVariants.
     *
     * @param indata the input data fetched from the database by Hdbextractor
     * @param timestamps the output vector of timestamps (converted to double)
     * @param data the output data as vector of double
     *
     * \note
    void toTimestampDataDoubleVector(const std::vector<XVariant> &indata,
                                     QVector<double> &timestamps,
                                     QVector<double> &data, bool *ok = NULL);

    void toTimestampDataDoubleVector(const std::vector<XVariant> &indata,
                                     QVector<double> &timestamps,
                                     QVector<double> &rdata,
                                     QVector<double> &wdata,
                                     bool *ok = NULL);
};

#endif // QHDBXUTILS_H
