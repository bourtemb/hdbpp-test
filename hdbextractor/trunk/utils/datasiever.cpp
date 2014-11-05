#include "datasiever.h"
#include "datasieverprivate.h"

#include <string.h>
#include <hdbxmacros.h>

#define MAXSRCLEN 256

DataSiever::DataSiever()
{
    d_ptr = new DataSieverPrivate();
    d_ptr->minTimestamp = 0.0;
}

DataSiever::~DataSiever()
{
    d_ptr->dataMap.clear();
    delete d_ptr;
}

void DataSiever::clear()
{
    d_ptr->dataMap.clear();
}

/** \brief Separates data coming from different sources, saving couples source name / data in an internal
 *         map.
 *
 * @param rawdata a vector of XVariant representing historical data. rawdata can contain the full data,
 *        after it has previously been completely fetched, or just a partial result. In the latter case,
 *        divide can be called multiple times and the result is equivalent.
 *
 * The only constraint is that rawdata contains data ordered by time for each one of the different sources.
 * The internal map used to save source names and their data values is not cleared by divide. For this reason,
 * it is possible to make multiple calls to this method whenever a new chunk of data is available from the
 * database.
 *
 */
void DataSiever::divide(const std::vector<XVariant> &rawdata)
{
    char source[MAXSRCLEN] = "";
    size_t i;
    size_t dataSize = rawdata.size();
    /* see which row's data is most dated */
    double timestamp;
    struct timeval tval;

    for(i = 0; i < dataSize; i++)
    {
        XVariant xv = rawdata[i];
        const char *src = xv.getSource();
        if(strcmp(src, source) != 0 && d_ptr->dataMap.count(std::string(src)) == 0)
        {
            d_ptr->dataMap[std::string(src)] = std::vector<XVariant>();
            strncpy(source, src, MAXSRCLEN);
            /* take the opportunity to save min timestamp */
            tval = xv.getTimevalTimestamp();
            timestamp = tval.tv_sec + tval.tv_usec * 1e-6;
            if(d_ptr->minTimestamp == 0.0 || timestamp < d_ptr->minTimestamp)
            {
                d_ptr->minTimestamp = timestamp;
            }
        }
        printf("pushing back on \"%s\"\n", source);
        d_ptr->dataMap[std::string(source)].push_back(xv);
    }

}

void DataSiever::fill()
{
    size_t srcCount = d_ptr->dataMap.size();
    size_t filledDataSize = 0;
    double nextTimestamp = d_ptr->minTimestamp;
    for(std::map<std::string, std::vector<XVariant> >::iterator it = d_ptr->dataMap.begin();
        it != d_ptr->dataMap.end(); ++it)
    {
        std::vector<XVariant> data = it->second;
        if(data.size() > filledDataSize)
            filledDataSize = it->second.size();
    }


}

size_t DataSiever::getSize() const
{
    return d_ptr->dataMap.size();
}

bool DataSiever::contains(std::string source) const
{
    return d_ptr->dataMap.count(source) > 0;
}

std::vector<std::string> DataSiever::getSources() const
{
    std::vector<std::string> srcs;
    for(std::map<std::string, std::vector<XVariant> >::iterator it = d_ptr->dataMap.begin();
        it != d_ptr->dataMap.end(); ++it)
        srcs.push_back(it->first);
    return srcs;
}

std::vector<XVariant> DataSiever::getData(std::string source) const
{
    std::vector<XVariant>  ret;
    if(d_ptr->dataMap.count(source) > 0)
        ret = d_ptr->dataMap[source];
    return ret;
}

const std::map<std::string, std::vector<XVariant> > & DataSiever::getDataRef() const
{
    const std::map<std::string, std::vector<XVariant> > &rData = d_ptr->dataMap;
    return rData;
}

std::map<std::string, std::vector<XVariant> > DataSiever::getData() const
{
    return d_ptr->dataMap;
}


