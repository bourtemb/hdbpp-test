#include "qhdbxutils.h"
#include "datetimeutils.h"
#include "qhdbxutilsprivate.h"
#include <math.h>
#include <QtDebug>
#include <QDateTime>

QHdbXUtils::QHdbXUtils()
{
    d_ptr = new QHdbXUtilsPrivate();
    d_ptr->nullDataCount = -1;
}

/** \brief Returns the number of detected NULL data by either the toTimestampDataDoubleVector
 * or toTimestampDataDoubleVector methods.
 *
 * You can use this method before calling toTimestampErrorDataVector, to test whether an error
 * is likely to be recorded in the database as to the extracted data.
 * If this method returns 0, then toTimestampDataDoubleVector or toTimestampDataDoubleVector
 * haven't detected any NULL data value, and all the data fetched from the database should be
 * valid.
 *
 * \note To have a valid null data count, you must first call either toTimestampDataDoubleVector
 * or toTimestampDataDoubleVector method.
 * Otherwise, -1 will be returned.
 */
int QHdbXUtils::getNullDataCount() const
{
    return d_ptr->nullDataCount;
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
    d_ptr->nullDataCount = 0;
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
        if(v.isNull())
            d_ptr->nullDataCount++;
        if(v.isWNull())
            d_ptr->nullDataCount++;

        /* append data */
        if((v.isNull() && w == XVariant::RO) || !v.isValid())
            data.append(nan("NaN"));
        else if(v.isWNull() && w == XVariant::WO)
            data.append(nan("NaN"));
        else if(dt == XVariant::Double)
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
    d_ptr->nullDataCount = 0;
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

        if(v.isNull())
            d_ptr->nullDataCount++;
        if(v.isWNull())
            d_ptr->nullDataCount++;

        /* append data */
        if(dt == XVariant::Double)
        {
            if(!v.isNull() && v.isValid())
                rdata.append(v.toDouble(true));
            else
                rdata.append(nan("NaN"));

            if(!v.isWNull() && v.isValid())
                wdata.append(v.toDouble(false));
            else
                wdata.append(nan("NaN"));
        }
        else if(dt == XVariant::Int)
        {
            if(!v.isNull() && v.isValid())
                rdata.append((double) v.toLongInt());
            else
                rdata.append(nan("NaN"));

            if(!v.isWNull() && v.isValid())
                wdata.append((double) v.toLongInt(false));
            else
                wdata.append(nan("NaN"));
        }
        else if(dt == XVariant::UInt)
        {
            if(!v.isNull() && v.isValid())
                rdata.append((double) v.toULongInt());
            else
                rdata.append(nan("NaN"));

            if(!v.isWNull() && v.isValid())
                wdata.append((double) v.toULongInt(false));
            else
                wdata.append(nan("NaN"));
        }
        else if(dt == XVariant::Boolean)
        {
            if(!v.isNull() && v.isValid())
                rdata.append((double) v.toBool());
            else
                rdata.append(nan("NaN"));

            if(!v.isWNull() && v.isValid())
                wdata.append((double) v.toBool(false));
            else
                wdata.append(nan("NaN"));
        }
    }
}

void QHdbXUtils::toTimestampErrorDataVector(const std::vector<XVariant> &indata,
                                QVector<double> &timestamps,
                                QVector<int> &codes,
                                QStringList &messages)
{
    for(size_t i = 0; i < indata.size(); i++)
    {
        const XVariant &v = indata[i];
        timestamps.append(DateTimeUtils().toDouble(v.getTimestamp()));
        printf("\e[1;35m getQuality returns %d\e[0m\n", v.getQuality());
        codes.append(v.getQuality());
        messages.append(v.getError());
    }
}


