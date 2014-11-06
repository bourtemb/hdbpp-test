#include "datasiever.h"
#include "datasieverprivate.h"

#include <string.h>
#include <set>
#include <time.h>
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
    size_t i, si, tstamps_size, ts_i;
    double nextTimestamp = d_ptr->minTimestamp;
    double timestamp, data_timestamp_0, data_timestamp_1;
    struct timeval tv;

    /* create a std set (always sorted following a specific strict weak
     * ordering criterion indicated by its internal comparison object)
     * with all the required timestamps that will fill the n vectors of
     * data.
     */
    std::set<double> timestamp_set;
    for(std::map<std::string, std::vector<XVariant> >::iterator it = d_ptr->dataMap.begin();
        it != d_ptr->dataMap.end(); ++it)
    {
        std::vector<XVariant> data = it->second;
        for(i = 0; i < data.size(); i++)
        {
            tv = data[i].getTimevalTimestamp();
            timestamp = tv.tv_sec + tv.tv_usec * 1e-6;
            /* insert timestamp in the set. If timestamp is duplicate, it's not inserted */
            timestamp_set.insert(timestamp);
        }
    }

    tstamps_size = timestamp_set.size();

    /* for each data row */
    for(std::map<std::string, std::vector<XVariant> >::iterator it = d_ptr->dataMap.begin();
        it != d_ptr->dataMap.end(); ++it)
    {
        std::set<double>::iterator ts_set_iterator = timestamp_set.begin();
        ts_i = 0;
        /* take data from the map */
        std::vector<XVariant> &data = it->second;
        std::vector<XVariant>::iterator datait = data.begin();
        datait = datait + 1; /* start with second element */
        while(datait != data.end())
        {
            tv = datait->getTimevalTimestamp();
            data_timestamp_1 = tv.tv_sec + tv.tv_usec * 1e-6;
            /* get previous data timestamp */
            tv = (datait - 1)->getTimevalTimestamp();
            data_timestamp_0 = tv.tv_sec + tv.tv_usec * 1e-6;

            /* iterate over the timestamps stored in the timestamp set. As we walk the set, avoid
             * searching the same interval multiple times. For this, keep ts_set_iterator as
             * start and update it in the last else if branch.
             */
            for(std::set<double>::iterator tsiter = ts_set_iterator; tsiter != timestamp_set.end(); tsiter++)
            {
                time_t tt = (time_t) (*tsiter);
                if((*tsiter) >  data_timestamp_0 && (*tsiter) < data_timestamp_1)
                {
                    printf("\e[0;32mfilling %s with %s\e[0m\n", datait->getSource(), ctime(&tt));
                    /* insert before the position of the iterator datait */
                    data.insert(datait, XVariant(*datait));
                }
                else if((*tsiter) == data_timestamp_1) /* simply skip */
                {
                    printf("\e[1;35mskipping element cuz equal to timestamp_1 (%s)", ctime(&tt));
                }
                else if((*tsiter) > data_timestamp_1)
                {
                    ts_set_iterator = tsiter; /* save to optimize next for */
                    printf("\e[1;35m going to next point after %s\e[0m\n", ctime(&tt));
                    break;
                }

            }

            datait++;
        }



    }


//    for(si = 0; si < timestamp_set.size(); si++)
//    {
//        timestamp = timestamp_set[si];
//        for(std::map<std::string, std::vector<XVariant> >::iterator it = d_ptr->dataMap.begin();
//            it != d_ptr->dataMap.end(); ++it)
//        {
//            std::vector<XVariant> data = it->second;
//            tv = data[i].
//            data_timestamp_0 = tv.tv_sec + tv.tv_usec * 1e-6;
//            tv = data[i - 1];
//        }
//    }
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


