#include "mysqlhdbppschema.h"

#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/time.h>
#include "db/src/connection.h"
#include "db/src/row.h"
#include "db/src/result.h"
#include "mysql/src/mysqlconnection.h"
#include "../src/hdbxmacros.h"
#include "db/src/dbschemaprivate.h"
#include "xvariantlist.h"
#include <pthread.h>
#include <assert.h>
#include <map>

#define MAXQUERYLEN 4096

MySqlHdbppSchema::MySqlHdbppSchema(ResultListener *resultListenerI)
{
    assert(resultListenerI != NULL);

    d_ptr = new DbSchemaPrivate();
    d_ptr->resultListenerI = resultListenerI;
    d_ptr->variantList = NULL;
    d_ptr->sourceStep = 0;
    d_ptr->totalSources = 1;
    pthread_mutex_init(&d_ptr->mutex, NULL);
}

const char *MySqlHdbppSchema::getError() const
{
    return d_ptr->errorMessage;
}

bool MySqlHdbppSchema::hasError() const
{
    return strlen(d_ptr->errorMessage) > 0;
}

/** \brief The class destructor.
 *
 * Deallocates the mutex used for thread safety.
 */
MySqlHdbppSchema::~MySqlHdbppSchema()
{
    pthread_mutex_destroy(&d_ptr->mutex);
}

bool MySqlHdbppSchema::setQueryConfiguration(QueryConfiguration *queryConfiguration)
{
    d_ptr->queryConfiguration = queryConfiguration;
}

/** \brief empties the queue of partial or complete data already fetched from the database.
 *
 * @param variantlist a <strong>reference</strong> to a std::vector where data is copied.
 *
 * \note The caller is not in charge of freeing any memory used by MySqlHdbppSchema. The caller
 *       creates and manages the variantlist.
 */
int MySqlHdbppSchema::get(std::vector<XVariant>& variantlist)
{
    pthread_mutex_lock(&d_ptr->mutex);

    int size = -1;
    if(d_ptr->variantList != NULL)
    {
        size = (int) d_ptr->variantList->size();

        printf("\e[0;35mMySqlHdbppSchema.get: locketh xvarlist for writing... size %d \e[0m\t", size);

        for(int i = 0; i < size; i++)
        {
            //            printf("copying variant %d over %d\n", i, size);
            variantlist.push_back(XVariant(*(d_ptr->variantList->get(i))));
            printf("last timestamp %s\n", variantlist.at(variantlist.size() - 1).getTimestamp());
        }
        delete d_ptr->variantList;
        d_ptr->variantList = NULL;
    }

    pthread_mutex_unlock(&d_ptr->mutex);
    printf("\e[0;32munlocked: [copied %d]\e[0m\n", size);
    return size;
}

/** \brief Fetch attribute data from the database between a start and stop date/time.
 *
 * Fetch data from the database.
 * \note This method is used by HdbExtractor and it is not meant to be directly used by the library user.
 *
 * @param source A the tango attribute in the form domain/family/member/AttributeName
 * @param start_date the start date (begin of the requested data interval) as string, such as "2014-07-10 10:00:00"
 * @param stop_date the stop date (end of the requested data interval) as string, such as "2014-07-10 12:00:00"
 * @param connection the database Connection specific object
 * @param notifyEveryRows the number of rows that make up a block of data. Every time a block of data is complete
 *        notifications are sent to the listener of type ResultListener (HdbExtractor)
 *
 * @return true if the call was successful, false otherwise.
 */
bool MySqlHdbppSchema::getData(const char *source,
                               const char *start_date,
                               const char *stop_date,
                               Connection *connection,
                               int notifyEveryNumRows)
{
    bool success;
    char query[MAXQUERYLEN];
    char errmsg[256];
    char ch_id[16];
    char data_type[32];
    char timestamp[32];
    int id, datasiz = 1;
    int timestampCnt = 0;
    int index = 0;
    double elapsed = -1.0; /* query elapsed time in seconds.microseconds */
    struct timeval tv1, tv2;

    XVariant *xvar = NULL;
    strcpy(timestamp, ""); /* initialize an empty timestamp */

    gettimeofday(&tv1, NULL);

    /* clear error */
    strcpy(d_ptr->errorMessage, "");

    d_ptr->notifyEveryNumRows = notifyEveryNumRows;

    snprintf(query, MAXQUERYLEN, "SELECT att_conf_id,data_type from att_conf WHERE att_name like '%%/%s'", source);
    Result * res = connection->query(query);
    if(!res)
    {
        snprintf(d_ptr->errorMessage, MAXERRORLEN,
                 "MySqlHdbppSchema.getData: error in query \"%s\": \"%s\"", query, connection->getError());
        return false;
    }
    if(res->next() > 0)
    {
        Row* row = res->getCurrentRow();
        if(!row)
        {
            snprintf(d_ptr->errorMessage, MAXERRORLEN, "MySqlHdbppSchema.getData: error getting row");
            return false;
        }

        if(row->getFieldCount() == 2)
        {
            strncpy(ch_id, row->getField(0), 16);

            strncpy(data_type, row->getField(1), 32);

            /*
             * enum AttributeDataType { ATT_BOOL, ATT_SHORT, ATT_LONG, ATT_LONG64, ATT_FLOAT,
             * ATT_DOUBLE, ATT_UCHAR, ATT_USHORT, ATT_ULONG, ATT_ULONG64, ATT_STRING,
             * ATT_STATE, DEVICE_STATE,
             * ATT_ENCODED, NO_DATA ...
             */
            XVariant::DataType dataType;
            if(strstr(data_type, "double") != NULL)
                dataType = XVariant::Double;
            else if(strstr(data_type, "int64") != NULL)
                dataType = XVariant::Int;
            else if(strstr(data_type, "int8") != NULL)
                dataType = XVariant::Int;
            else if(strstr(data_type, "string") != NULL)
                dataType = XVariant::String;
            else if(strstr(data_type, "bool") != NULL)
                dataType = XVariant::Boolean;
            else
                dataType = XVariant::TypeInvalid;

            /* free memory */
            res->close();
            row->close();

            XVariant::Writable wri;
            if(strstr(data_type, "ro") != NULL)
                wri = XVariant::RO;
            else if(strstr(data_type, "rw") != NULL)
                wri = XVariant::RW;
            else if(strstr(data_type, "wo") != NULL)
                wri = XVariant::WO;
            else
                wri = XVariant::WritableInvalid;

            XVariant::DataFormat format;
            if(strstr(data_type, "scalar") != NULL)
                format = XVariant::Scalar;
            else if(strstr(data_type, "array") != NULL)
                format = XVariant::Vector;
            else if(strstr(data_type, "image") != NULL)
                format = XVariant::Matrix;
            else
                format = XVariant::FormatInvalid;

            if(dataType == XVariant::TypeInvalid || wri ==  XVariant::WritableInvalid ||
                    format == XVariant::FormatInvalid)
            {
                snprintf(d_ptr->errorMessage, MAXERRORLEN,
                         "MySqlHdbppSchema.getData: invalid type %d, format %d or writable %d",
                         dataType, format, wri);
                success = false;
            }
            else
            {
                /* now get data */
                id = atoi(ch_id);
                if(wri == XVariant::RO)
                {
                    if(format == XVariant::Vector)
                        snprintf(query, MAXQUERYLEN, "SELECT event_time,value_r,dim_x,idx FROM "
                                                     " att_%s WHERE att_conf_id=%d AND event_time >='%s' "
                                                     " AND event_time <= '%s' ORDER BY event_time,idx ASC",
                                 data_type, id, start_date, stop_date);
                    else if(format == XVariant::Scalar)
                        snprintf(query, MAXQUERYLEN, "SELECT event_time,value_r FROM "
                                                     " att_%s WHERE att_conf_id=%d AND event_time >='%s' "
                                                     " AND event_time <= '%s' ORDER BY event_time ASC",
                                 data_type, id, start_date, stop_date);
                    
                    pinfo("query: %s\n", query);


                    res = connection->query(query);
                    if(!res)
                    {
                        snprintf(d_ptr->errorMessage, MAXERRORLEN, "error in query \"%s\": \"%s\"",
                                 query, connection->getError());
                        return false;
                    }

                    while(res->next() > 0)
                    {
                        row = res->getCurrentRow();

                        if(!row)
                        {
                            snprintf(d_ptr->errorMessage, MAXERRORLEN, "MySqlHdbppSchema.getData: error getting row");
                            return false;
                        }



                        /* compare timestamp with previous one: if they differ, the row
                         * refers to the next value in time.
                         */
                        if(strcmp(timestamp, row->getField(0)) != 0)
                        {

                            if(format != XVariant::Scalar && timestampCnt > 0 &&
                                    d_ptr->notifyEveryNumRows > 0 &&
                                    (timestampCnt % d_ptr->notifyEveryNumRows == 0))
                            {
                                printf("\e[1;33mnotrifying vector!!\e[0m\n");
                                d_ptr->resultListenerI->onProgressUpdate(source,
                                                                         timestampCnt,
                                                                         res->getRowCount() / datasiz);
                            }

                            /* get timestamp */
                            strncpy(timestamp, row->getField(0), 32);

                            /* get data size of array */
                            if(format == XVariant::Vector)
                                datasiz = atoi(row->getField(2));
                            else
                                datasiz = 1;

                            /* create new XVariant for the timestamp */
                            xvar = new XVariant(source, timestamp, datasiz, format, dataType, wri);

                            timestampCnt++;

                            printf("+ xvar 0x%p: new source %s %s %s arr.cnt: %d data siz: %d entries cnt: %d)\n", xvar,
                                   source, row->getField(0), row->getField(1),
                                   timestampCnt, datasiz, res->getRowCount()/datasiz);

                            /*
                             * LOCK!
                             * modifying variantlist: acquire mutex
                             */
                            ///        printf("\e[1;35mMySqlHdbppSchema.getData: locking xvarlist for writing... \e[0m");
                            pthread_mutex_lock(&d_ptr->mutex);

                            if(d_ptr->variantList == NULL)
                                d_ptr->variantList = new XVariantList();
                            if(format == XVariant::Scalar)
                            {
                                xvar->add(row->getField(1), 0);
                            }
                            d_ptr->variantList->add(xvar);


                            /* UNLOCK
                             */
                            pthread_mutex_unlock(&d_ptr->mutex);

                            if(format == XVariant::Scalar && timestampCnt > 0 &&
                                    d_ptr->notifyEveryNumRows > 0 &&
                                    (timestampCnt % d_ptr->notifyEveryNumRows == 0))
                            {
                                d_ptr->resultListenerI->onProgressUpdate(source,
                                                                         timestampCnt,
                                                                         res->getRowCount() / datasiz);
                            }
                        }

                        if(format == XVariant::Vector)
                        {
                            index = atoi(row->getField(3));


                            //                        printf("\e[1;33m+ xvar 0x%p: adding %s %s %s (%d/%d[%d])\e[0m\n", xvar,
                            //                               source, row->getField(0), row->getField(1),
                            //                               index, datasiz, res->getRowCount()/datasiz);


                            ///        printf("\e[1;35mMySqlHdbppSchema.getData: locking xvarlist for writing... \e[0m");
                            pthread_mutex_lock(&d_ptr->mutex);

                            xvar->add(row->getField(1), index);

                            pthread_mutex_unlock(&d_ptr->mutex);
                        }


                        ///             printf("\t\e[1;32munlocked\e[0m\n");

                        row->close();

                    } /* end while(res->next) */

                    res->close();

                    success = true;
                }
                else if(wri == XVariant::RW)
                {
                    /*  */
                    if(format == XVariant::Vector)
                        snprintf(query, MAXQUERYLEN, "SELECT event_time,value_r,value_w,dim_x,idx FROM "
                                                     " att_%s WHERE att_conf_id=%d AND event_time >='%s' "
                                                     " AND event_time <= '%s' ORDER BY event_time,idx ASC",
                                 data_type, id, start_date, stop_date);
                    else
                        snprintf(query, MAXQUERYLEN, "SELECT event_time,value_r,value_w FROM "
                                                     " att_%s WHERE att_conf_id=%d AND  event_time >='%s' "
                                                     " AND event_time <= '%s' ORDER BY event_time ASC",
                                 data_type, id, start_date, stop_date);

                    pinfo("query: %s\n", query);


                    res = connection->query(query);
                    if(!res)
                    {
                        snprintf(d_ptr->errorMessage, MAXERRORLEN, "error in query \"%s\": \"%s\"",
                                 query, connection->getError());
                        return false;
                    }
                    while(res->next() > 0)
                    {
                        row = res->getCurrentRow();
                        if(!row)
                        {
                            snprintf(d_ptr->errorMessage, MAXERRORLEN, "MySqlHdbppSchema.getData: error getting row");
                            return false;
                        }

                        XVariant *xvar = NULL;

                        /* compare timestamp with previous one: if they differ, the row
                         * refers to the next value in time.
                         */
                        if(strcmp(timestamp, row->getField(0)) != 0)
                        {
                            if(timestampCnt > 0 &&
                                    d_ptr->notifyEveryNumRows > 0 &&
                                    (timestampCnt % d_ptr->notifyEveryNumRows == 0))
                            {
                                d_ptr->resultListenerI->onProgressUpdate(source, timestampCnt,
                                                                         res->getRowCount() / datasiz);
                            }
                            /* get timestamp */
                            strncpy(timestamp, row->getField(0), 32);

                            /* get data size of array */
                            if(format == XVariant::Vector)
                                datasiz = atoi(row->getField(3));
                            else
                                datasiz = 1;

                            /* create new XVariant for the timestamp */
                            xvar = new XVariant(source, timestamp, datasiz, format, dataType, wri);
                            /*  this means a new array is being associated to a timestamp */
                            timestampCnt++;

                            /*
                             * LOCK!
                             * modifying variantlist: acquire mutex
                             */
                            ///        printf("\e[1;35mMySqlHdbppSchema.getData: locking xvarlist for writing... \e[0m");
                            pthread_mutex_lock(&d_ptr->mutex);

                            if(d_ptr->variantList == NULL)
                                d_ptr->variantList = new XVariantList();

                            d_ptr->variantList->add(xvar);

                            /* UNLOCK
                             */
                            pthread_mutex_unlock(&d_ptr->mutex);

                        }

                        if(format == XVariant::Vector)
                            index = atoi(row->getField(4));
                        else
                            index = 0; /* scalar */

                        pthread_mutex_lock(&d_ptr->mutex);

                       // printf("\e[1;35m adding %s: %s %s\e[0m\n", timestamp, row->getField(1), row->getField(2));
                        xvar->add(row->getField(1), row->getField(2), index);

                        pthread_mutex_unlock(&d_ptr->mutex);
                        ///             printf("\t\e[1;32munlocked\e[0m\n");

                        row->close();
                    }
                    res->close();

                    success = true;
                }
            } /* else: valid data type, format, writable */

            if(timestampCnt > 0 &&
                    d_ptr->notifyEveryNumRows > 0)
            {
                d_ptr->resultListenerI->onProgressUpdate(source,
                                                         res->getRowCount() / datasiz,
                                                         res->getRowCount() / datasiz);
            }

            d_ptr->sourceStep++;
        }
    }
    else
    {
        success = false;
        snprintf(d_ptr->errorMessage, MAXERRORLEN, "MySqlHdbppSchema: no attribute \"%s\" in adt", source);
        perr(errmsg);
    }

    /* compute elapsed time */
    gettimeofday(&tv2, NULL);
    /* transform the elapsed time from a timeval struct to a double whose integer part
     * represents seconds and the decimal microseconds.
     */
    elapsed = tv2.tv_sec + 1e-6 * tv2.tv_usec - (tv1.tv_sec + 1e-6 * tv1.tv_usec);
    d_ptr->resultListenerI->onFinished(source, d_ptr->sourceStep, d_ptr->totalSources, elapsed);

    return success;
}

bool MySqlHdbppSchema::getData(const std::vector<std::string> sources,
                               const char *start_date,
                               const char *stop_date,
                               Connection *connection,
                               int notifyEveryNumRows)
{
    bool success = true;
    d_ptr->totalSources = sources.size();
    for(size_t i = 0; i < d_ptr->totalSources; i++)
    {
        d_ptr->sourceStep = i + 1;
        printf("MySqlHdbppSchema.getData %s %s %s\n", sources.at(i).c_str(), start_date, stop_date);
        success = getData(sources.at(i).c_str(), start_date, stop_date,
                          connection, notifyEveryNumRows);
        if(!success)
            break;
    }

    d_ptr->totalSources = 1;

    return success;

}
