#include "qhdbxutils.h"
#include <math.h>
#include <QtDebug>
#include <QDateTime>

QHdbXUtils::QHdbXUtils()
{

}

/** \brief Converts input data into a vector of timestamps (converted to double)
 *         and a vector of values. Optionally, checks if the required conversion is
 *         in concert with the type of data stored by the XVariants.
 *         This method is tailored for read only or write only data (one data column
 *         into the database).
 *
 * @param indata the input data fetched from the database by Hdbextractor
 * @param timestamps the output vector of timestamps (converted to double)
 * @param data the output data as vector of double
 *
 * \note indata is passed as the const reference to a std:vector, while timestamps
 * and data are references to QVector<double> defined in the caller.
 *
 * \note timestamps and data vectors are not cleared by this method.
 *       Instead, data is appended to their end.
 *
 *
 */
void QHdbXUtils::toTimestampDataDoubleVector(const std::vector<XVariant> &indata,
                                             QVector<double> &timestamps,
                                             QVector<double> &data, bool *ok)
{
    XVariant::DataType dt = XVariant::TypeInvalid;
    XVariant::Writable w = XVariant::WritableInvalid;
    XVariant::DataFormat fmt = XVariant::FormatInvalid;
 //   qDebug() << "*** " << timestamps.size();
   // timestamps.clear();
    if(indata.size() > 0)
    {
        dt = indata[0].getType();
        w = indata[0].getWritable();
        fmt = indata[0].getFormat();
    }
    /* check indata type/format/writable compatibility only if the users wants to */
    if(ok != NULL)
    {
        if((dt == XVariant::Double || dt == XVariant::Int) && (fmt == XVariant::Scalar)
                && (w == XVariant::RO || w == XVariant::RW))
        {
            *ok = true;
        }
        else
            *ok = false;
    }
    /* try to extract data */
    for(size_t i = 0; i < indata.size(); i++)
    {
        const XVariant &v = indata[i];
        /* append timestamp */
        struct timeval tv = v.getTimevalTimestamp();
        double timestamp = (double) tv.tv_sec;
        timestamp += ((double) tv.tv_usec) * 1e-6;
        timestamps.append(timestamp);
        printf("timestamp: %f\n", timestamps.last());
        qDebug() << QDateTime::fromTime_t(v.getTime_tTimestamp()) << (double) v.getTime_tTimestamp();
        /* append data */
        if(dt == XVariant::Double)
            data.append(v.toDouble(true));
        else if(dt == XVariant::Int)
            data.append((double) v.toLongInt());
    }
}

/** \brief Converts input data into a vector of timestamps (converted to double)
 *         and, a vector of read values and a vector of write values.
 *         Optionally, checks if the required conversion is
 *         in concert with the type of data stored by the XVariants.
 *         This method is tailored for read write data (two data columns
 *         into the database, read and write).
 *
 * @param indata the input data fetched from the database by Hdbextractor
 * @param timestamps the output vector of timestamps (converted to double)
 * @param data the output data as vector of double
 *
 * \note indata is passed as the const reference to a std:vector, while timestamps
 * and data are references to QVector<double> defined in the caller.
 *
 * \note timestamps and data vectors are not cleared by this method.
 *       Instead, data is appended to their end.
 *
 *
 */
void QHdbXUtils::toTimestampDataDoubleVector(const std::vector<XVariant> &indata,
                                             QVector<double> &timestamps,
                                             QVector<double> &rdata,
                                             QVector<double> &wdata,
                                             bool *ok)
{
    XVariant::DataType dt = XVariant::TypeInvalid;
    XVariant::Writable w = XVariant::WritableInvalid;
    XVariant::DataFormat fmt = XVariant::FormatInvalid;
    if(indata.size() > 0)
    {
        dt = indata[0].getType();
        w = indata[0].getWritable();
        fmt = indata[0].getFormat();
    }
    /* check indata type/format/writable compatibility only if the users wants to */
    if(ok != NULL)
    {
        if((dt == XVariant::Double || dt == XVariant::Int ||
            dt == XVariant::UInt || dt == XVariant::Boolean)
                && (fmt == XVariant::Scalar)
                && w == XVariant::RW)
        {
            *ok = true;
        }
        else
            *ok = false;
    }
    /* try to extract data */
    for(size_t i = 0; i < indata.size(); i++)
    {
        const XVariant &v = indata[i];
        /* append timestamp */
        struct timeval tv = v.getTimevalTimestamp();
        double timestamp = (double) tv.tv_sec;
        timestamp += ((double) tv.tv_usec) * 1e-6;
        timestamps.append(timestamp);

        /* append data */
        if(dt == XVariant::Double)
        {
            rdata.append(v.toDouble(true));
            wdata.append(v.toDouble(false));
        }
        else if(dt == XVariant::Int)
        {
            rdata.append((double) v.toLongInt());
            wdata.append((double) v.toLongInt(false));
        }
        else if(dt == XVariant::UInt)
        {
            rdata.append((double) v.toULongInt());
            wdata.append((double) v.toULongInt(false));
        }
        else if(dt == XVariant::Boolean)
        {
            rdata.append((double) v.toBool());
            wdata.append((double) v.toBool(false));
        }
    }
}

