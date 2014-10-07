#ifndef XVARIANTPRIVATE_H
#define XVARIANTPRIVATE_H

#include <xvariant.h>

class XVariantPrivate
{
public:
    XVariantPrivate() {}

    XVariant::DataFormat mFormat;
    XVariant::DataType mType;
    XVariant::Writable mWritable;

    size_t mSize;

    char mTimestamp[TIMESTAMPLEN];

    char mError[ERRMSGLEN];

    char mSource[SRCLEN];

    bool mIsValid;

    bool mIsNull;

    bool mIsWNull;

    void * val;

    void * w_val;

};

#endif // XVARIANTPRIVATE_H
