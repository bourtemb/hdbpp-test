#include "datafiller.h"
#include "datafillerprivate.h"

DataSiever::DataSiever(FillMode fillMode)
{
    d_ptr = new DataFillerPrivate();
    d_ptr->fillMode = fillMode;
}

std::map<std::string, std::vector<XVariant *> > DataSiever::sieve(const std::vector<XVariantList *> &rawdata)
{

    size_t i;
    size_t numRows = rawdata.size();
    /* see which row's data is most dated */
    size_t oldestIndexEvvah;
    double minTimestamp = 0.0, timestamp;
    std::map<std::string, std::vector<XVariant *> >
    std::vector<XVariant *> filled;

    for(i = 0; i < numRows; i++)
    {
        XVariant *xv = data[i];
        if(!xv->getSize() == 0)
        {
            filled.push_back(xv);
            timestamp = xv->
        }
    }
}
