#include "datasiever.h"
#include "datasieverprivate.h"
#include "xvariantprinter.h"

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
    //d_ptr->dataMap.clear();
    //delete d_ptr;
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
        printf("pushing back on \"%s\" from %p\n", source, &xv);
        d_ptr->dataMap[std::string(source)].push_back(xv);
    }
    XVariantPrinter printer;
    for(std::map<std::string, std::vector<XVariant> >::iterator it = d_ptr->dataMap.begin();
        it != d_ptr->dataMap.end(); ++it)
    {
        printf("after fill: source %s\n", it->first.c_str());
        std::vector<XVariant> &data = it->second;
        for(int i = 0; i < data.size(); i++)
        {
            printf("variant %p: ", &data[i]);
            printer.print(data[i], 2);
        }

    }
}

void DataSiever::mMyAdd(std::vector<XVariant> &data, const XVariant &v, size_t at)
{
    XVariantPrinter printer;
    printf("\e[1;32mresizin' to %d! this is data BEFORE resizin'\n", data.size() + 1);
    for(int i = 0; i < data.size(); i++)
    {
        printf("variant %p: ", &data[i]);
        printer.print(data[i], 2);
    }
    data.resize(data.size() + 1);
    printf("\e[1;32mdone resizin'!\e[0m\n");
    for(size_t i = data.size() - 2; i >= at; i--)
    {
        printf("\e[1;32m+\e[0m copying from %d to %d size %d\n", i, i+1, data.size());
        data[i + 1] = data[i];
    }
    printf("\e[1;32m+ *\e[0m inserting at %d size %d\n", at, data.size());
    data[at] = v;
}

void DataSiever::fill()
{
    size_t i, tstamps_size, ts_i;
    double timestamp, data_timestamp_0, data_timestamp_1;
    struct timeval timeva, tv1, tv0;

    /* create a std set (always sorted following a specific strict weak
     * ordering criterion indicated by its internal comparison object)
     * with all the required timestamps that will fill the n vectors of
     * data.
     */
    std::set<double> timestamp_set;
    for(std::map<std::string, std::vector<XVariant> >::iterator it = d_ptr->dataMap.begin();
        it != d_ptr->dataMap.end(); ++it)
    {
        std::vector<XVariant> &data = it->second;
        for(i = 0; i < data.size(); i++)
        {
            timeva = data[i].getTimevalTimestamp();
            timestamp = timeva.tv_sec + timeva.tv_usec * 1e-6;
            /* insert timestamp in the set. If timestamp is duplicate, it's not inserted */
            timestamp_set.insert(timestamp);
        }
    }

    tstamps_size = timestamp_set.size();
    size_t tsidx = 0, dataidx;

    XVariantPrinter printer;

    /* for each data row */
    for(std::map<std::string, std::vector<XVariant> >::iterator it = d_ptr->dataMap.begin();
        it != d_ptr->dataMap.end(); ++it)
    {
        tsidx = 0;
        dataidx = 1;
        std::set<double>::iterator ts_set_iterator = timestamp_set.begin();
        ts_i = 0;
        /* take the vector of data from the map */
        std::vector<XVariant> &data = it->second;
    ///    printf("\e[1;35mbefore processin'\e[0m\n");
    ///    printer.printValueList(data, 2);

        /* create an iterator over data */
        std::vector<XVariant>::iterator datait;

        while(1)
        {
            if(dataidx == data.size())
                break;

            tv1 = data[dataidx].getTimevalTimestamp();
            data_timestamp_1 = tv1.tv_sec + tv1.tv_usec * 1e-6;
            /* start of interval */
            tv0 = data[dataidx - 1].getTimevalTimestamp();
            data_timestamp_0 = tv0.tv_sec + tv0.tv_usec * 1e-6;

            /* iterate over the timestamps stored in the timestamp set. As we walk the set, avoid
             * searching the same interval multiple times. For this, keep ts_set_iterator as
             * start and update it in the last else if branch.
             */
            std::set<double>::iterator tsiter = ts_set_iterator;
            while(tsiter != timestamp_set.end())
            {
                time_t tt = (time_t) (*tsiter);
                if((*tsiter) >  data_timestamp_0 && (*tsiter) < data_timestamp_1)
                {
                   // datait = data.begin() + dataidx;
                    printf("+++ inserting data siz %ld:\n", data.size());
                    XVariant xvariant(data[dataidx - 1]);
                    xvariant.setTimestamp((*tsiter));
                    printf("crated new dataidx %d data.size %d, ", dataidx, data.size());
                    printer.print(xvariant, 3);
                    printf("^^^^^^+--------- print ends above. Now myAdd()\n");
                   // data.insert(datait, XVariant(data[dataidx - 1]).setTimestamp((*tsiter)));
                    this->mMyAdd(data, xvariant, dataidx);
                  //  data.insert(data.begin(), XVariant(data[dataidx - 1]).setTimestamp((*tsiter)));
                    printf("+++++ inserted data siz %ld address %p:\n", data.size(), &data[dataidx]);
                }
                else if(*tsiter == data_timestamp_1) /* simply skip */
                {
                    printf("\e[1;35m - skipping element cuz equal to timestamp_1: %s\e[0m", ctime(&tt));
                    tsiter++;
                    ts_set_iterator = tsiter; /* point to next */
                    break;
                }
                else if((*tsiter) > data_timestamp_1)
                {
                  //  tsiter++;
                    ts_set_iterator = tsiter; /* save to optimize next for */
                    printf("\e[1;32m > going to next point after %s \e[0m", ctime(&tt));
                    break;
                }
                tsiter++;
                ts_set_iterator = tsiter; /* save to optimize next for */
            }
            dataidx++;

          //  printf("\e[1;32m============= RESULT FOR %s =================\e[0m\n", it->first.c_str());
          //  printer.printValueList(data, 2);
        }

//        while(datait != data.end())
//        {
//            /* end of interval */
//            tv1 = datait->getTimevalTimestamp();
//            data_timestamp_1 = tv1.tv_sec + tv1.tv_usec * 1e-6;
//            /* start of interval */
//            tv0 = (datait - 1)->getTimevalTimestamp();
//            data_timestamp_0 = tv0.tv_sec + tv0.tv_usec * 1e-6;

//            /* iterate over the timestamps stored in the timestamp set. As we walk the set, avoid
//             * searching the same interval multiple times. For this, keep ts_set_iterator as
//             * start and update it in the last else if branch.
//             */
//            std::set<double>::iterator tsiter = ts_set_iterator;
//            while(tsiter != timestamp_set.end())
//            {
//                time_t tt = (time_t) (*tsiter);
//                if((*tsiter) >  data_timestamp_0 && (*tsiter) < data_timestamp_1)
//                {
//                    printf("+++ inserting data siz %ld:\n", data.size());
////                    XVariant xvariant(*datait);
////                    printf("crated new, ");
////                    xvariant.setTimestamp((*tsiter));
//                    printf("timestamp changed. Really inserting now\n");
//                    datait_tmp = data.insert(datait, XVariant(*datait).setTimestamp((*tsiter)));
//                    printf("+++ inserted data siz %ld address %p:\n", data.size(), datait);
//                    datait = datait_tmp;
//                }
//                else if(*tsiter == data_timestamp_1) /* simply skip */
//                {
//                    printf("\e[1;35m - skipping element cuz equal to timestamp_1: %s\e[0m", ctime(&tt));
//                    tsiter++;
//                    ts_set_iterator = tsiter; /* point to next */
//                    break;
//                }
//                else if((*tsiter) > data_timestamp_1)
//                {
//                  //  tsiter++;
//                    ts_set_iterator = tsiter; /* save to optimize next for */
//                    printf("\e[1;32m > going to next point after %s \e[0m", ctime(&tt));
//                    break;
//                }
//                tsiter++;
//                ts_set_iterator = tsiter; /* save to optimize next for */
//            }
//            datait++;
//            printf("\e[1;34m data index %ld data size %ld data %p\e[0m\n", dataidx, data.size(), &data);
//        }

  //      printf("\e[1;32m============= RESULT FOR %s =================\e[0m\n", it->first.c_str());
    //    printer.printValueList(data, 2);
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


