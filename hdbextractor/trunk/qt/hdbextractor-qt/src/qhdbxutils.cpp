#include "qhdbxutils.h"
#include <math.h>
#include <QtDebug>

QHdbXUtils::QHdbXUtils()
{

}

void QHdbXUtils::toTimestampDataDoubleVector(const std::vector<XVariant> &indata,
                                             QVector<double> &timestamps,
                                             QVector<double> &data, bool *ok)
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
        timestamps.append(v.getTime_tTimestamp());
        /* append data */
        if(dt == XVariant::Double)
            data.append(v.toDouble(true));
        else if(dt == XVariant::Int)
            data.append((double) v.toLongInt());
    }
}

void QHdbXUtils::toTimestampDataDoubleVector(const std::vector<XVariant> &indata,
                                             QVector<double> &timestamps,
                                             QVector<double> &rdata,
                                             QVector<double> &wdata,
                                             bool *ok = NULL)
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
        timestamps.append(v.getTime_tTimestamp());

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

