#include "xvariantprivate.h"

XVariantPrivate::XVariantPrivate()
{
    mError = NULL;
    val = NULL;
    w_val = NULL;

    mQuality = 0;

    mFormat = XVariant::FormatInvalid;
    mType = XVariant::TypeInvalid;
    mIsValid = false;
    mSize = 0;
}
