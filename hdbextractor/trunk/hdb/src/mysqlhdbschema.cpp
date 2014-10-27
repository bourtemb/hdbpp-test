#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/time.h>
#include "mysqlhdbschema.h"
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

MySqlHdbSchema::MySqlHdbSchema(ResultListener *resultListenerI)
{
    assert(resultListenerI != NULL);

    d_ptr = new DbSchemaPrivate();
    d_ptr->resultListenerI = resultListenerI;
    d_ptr->variantList = NULL;
    d_ptr->sourceStep = 0;
    d_ptr->totalSources = 1;
    pthread_mutex_init(&d_ptr->mutex, NULL);
}

const char *MySqlHdbSchema::getError() const
{
    return d_ptr->errorMessage;
}

bool MySqlHdbSchema::hasError() const
{
    return strlen(d_ptr->errorMessage) > 0;
}

/** \brief The class destructor.
 *
 * Deallocates the mutex used for thread safety.
 */
MySqlHdbSchema::~MySqlHdbSchema()
{
    pthread_mutex_destroy(&d_ptr->mutex);
}

bool MySqlHdbSchema::setQueryConfiguration(QueryConfiguration *queryConfiguration)
{
    d_ptr->queryConfiguration = queryConfiguration;
}

/** \brief empties the queue of partial or complete data already fetched from the database.
 *
 * @param variantlist a <strong>reference</strong> to a std::vector where data is copied.
 *
 * \note The caller is not in charge of freeing any memory used by MySqlHdbSchema. The caller
 *       creates and manages the variantlist.
 */
int MySqlHdbSchema::get(std::vector<XVariant>& variantlist)
{
    pthread_mutex_lock(&d_ptr->mutex);

    int size = -1;
    if(d_ptr->variantList != NULL)
    {
        size = (int) d_ptr->variantList->size();

        printf("\e[0;35mMySqlHdbSchema.get: locketh xvarlist for writing... size %d \e[0m\t", size);

        for(int i = 0; i < size; i++)
        {
            //            printf("copying variant %d over %d\n", i, size);
            variantlist.push_back(XVariant(*(d_ptr->variantList->get(i))));
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
bool MySqlHdbSchema::getData(const char *source,
                             const char *start_date,
                             const char *stop_date,
                             Connection *connection,
                             int notifyEveryNumRows)
{
    bool success;
    char query[MAXQUERYLEN];
    char errmsg[256];
    char ch_id[16];
    char data_type[16];
    char data_format[16];
    char writable[16];
    int id;
    int rowCnt = 0;
    double elapsed = -1.0; /* query elapsed time in seconds.microseconds */
    struct timeval tv1, tv2;

    gettimeofday(&tv1, NULL);

    /* clear error */
    strcpy(d_ptr->errorMessage, "");

    d_ptr->notifyEveryNumRows = notifyEveryNumRows;

    snprintf(query, MAXQUERYLEN, "SELECT ID,data_type,data_format,writable from adt WHERE full_name='%s'", source);
    Result * res = connection->query(query);
    if(!res)
    {
        snprintf(d_ptr->errorMessage, MAXERRORLEN,
                 "MysqlHdbSchema.getData: error in query \"%s\": \"%s\"", query, connection->getError());
        return false;
    }
    if(res->next() > 0)
    {
        Row* row = res->getCurrentRow();
        if(!row)
        {
            snprintf(d_ptr->errorMessage, MAXERRORLEN, "MysqlHdbSchema.getData: error getting row %d", rowCnt);
            return false;
        }

        if(row->getFieldCount() == 4)
        {
            strncpy(ch_id, row->getField(0), 16);
            strncpy(data_type, row->getField(1), 16);
            strncpy(data_format, row->getField(2), 16);
            strncpy(writable, row->getField(3), 16);
            /* free memory */
            res->close();
            row->close();

            /*
                 * enum AttributeDataType { ATT_BOOL, ATT_SHORT, ATT_LONG, ATT_LONG64, ATT_FLOAT,
                 * ATT_DOUBLE, ATT_UCHAR, ATT_USHORT, ATT_ULONG, ATT_ULONG64, ATT_STRING, ATT_STATE, DEVICE_STATE,
                 * ATT_ENCODED, NO_DATA ...
                 */
            XVariant::DataType dataType;
            if(strcmp(data_type, "5") == 0)
                dataType = XVariant::Double;
            else if(strcmp(data_type, "4") == 0)
                dataType = XVariant::Double;
            else if(strcmp(data_type, "0") == 0)
                dataType = XVariant::Boolean;
            else if(strcmp(data_type, "1") == 0) /* short */
                dataType = XVariant::Int;
            else if(strcmp(data_type, "2") == 0) /* long */
                dataType = XVariant::Int;
            else
                dataType = XVariant::TypeInvalid;

            XVariant::Writable wri;
            if(!strcmp(writable, "0"))
                wri = XVariant::RO;
            else if(!strcmp(writable, "3"))
                wri = XVariant::RW;
            else
                wri = XVariant::WritableInvalid;

            XVariant::DataFormat format;
            if(!strcmp(data_format, "0"))
                format = XVariant::Scalar;
            else if(!strcmp(data_format, "1"))
                format = XVariant::Vector;
            else if(!strcmp(data_format, "2"))
                format = XVariant::Matrix;
            else
                format = XVariant::FormatInvalid;

            if(dataType == XVariant::TypeInvalid || wri ==  XVariant::WritableInvalid ||
                    format == XVariant::FormatInvalid)
            {
                snprintf(d_ptr->errorMessage, MAXERRORLEN,
                         "MySqlHdbSchema.getData: invalid type %d, format %d or writable %d",
                         dataType, format, wri);
                success = false;
            }
            else
            {
                /* now get data */
                id = atoi(ch_id);
                if(wri == XVariant::RO)
                {
                    snprintf(query, MAXQUERYLEN, "SELECT time,value FROM att_%05d WHERE time >='%s' "
                             " AND time <= '%s' ORDER BY time ASC", id, start_date, stop_date);
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
                        rowCnt++;
                        row = res->getCurrentRow();
                        if(!row)
                        {
                            snprintf(d_ptr->errorMessage, MAXERRORLEN, "MysqlHdbSchema.getData: error getting row %d", rowCnt);
                            return false;
                        }

                        XVariant *xvar = NULL;
                        printf("+ adding %s %s (row count %d)\n", row->getField(0), row->getField(1), res->getRowCount());

                        xvar = new XVariant(source, row->getField(0), row->getField(1), format, dataType, wri);

             ///           printf("\e[1;35mMySqlHdbSchema.getData: locking xvarlist for writing... \e[0m");
                        pthread_mutex_lock(&d_ptr->mutex);

                        if(d_ptr->variantList == NULL)
                            d_ptr->variantList = new XVariantList();

                        d_ptr->variantList->add(xvar);

                        pthread_mutex_unlock(&d_ptr->mutex);
           ///             printf("\t\e[1;32munlocked\e[0m\n");

                        row->close();
                        if(d_ptr->notifyEveryNumRows > 0 && (rowCnt % d_ptr->notifyEveryNumRows == 0
                                                             || rowCnt == res->getRowCount()) )
                        {
                            d_ptr->resultListenerI->onProgressUpdate(source, rowCnt, res->getRowCount());
                        }
                    }
                    res->close();

                    success = true;
                }
                else if(wri == XVariant::RW)
                {
                    /*  */
                    snprintf(query, MAXQUERYLEN, "SELECT time,read_value,write_value FROM att_%05d WHERE time >='%s' "
                             " AND time <= '%s' ORDER BY time ASC", id, start_date, stop_date);
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
                        rowCnt++;
                        row = res->getCurrentRow();
                        if(!row)
                        {
                            snprintf(d_ptr->errorMessage, MAXERRORLEN, "MysqlHdbSchema.getData: error getting row %d", rowCnt);
                            return false;
                        }

                        XVariant *xvar = NULL;
                        // printf("+ adding %s %s (row count %d)\n", row->getField(0), row->getField(1), res->getRowCount());

                        xvar = new XVariant(source, row->getField(0), row->getField(1),
                                            row->getField(2), format, dataType);

             ///           printf("\e[1;35mMySqlHdbSchema.getData: locking xvarlist for writing... \e[0m");
                        pthread_mutex_lock(&d_ptr->mutex);

                        if(d_ptr->variantList == NULL)
                            d_ptr->variantList = new XVariantList();

                        d_ptr->variantList->add(xvar);

                        pthread_mutex_unlock(&d_ptr->mutex);
           ///             printf("\t\e[1;32munlocked\e[0m\n");

                        row->close();
                        if(d_ptr->notifyEveryNumRows > 0 && (rowCnt % d_ptr->notifyEveryNumRows == 0
                                                             || rowCnt == res->getRowCount()) )
                        {
                            d_ptr->resultListenerI->onProgressUpdate(source, rowCnt, res->getRowCount());
                        }
                    }
                    res->close();

                    success = true;
                }
            } /* else: valid data type, format, writable */
        }
    }
    else
    {
        success = false;
        snprintf(d_ptr->errorMessage, MAXERRORLEN, "MysqlHdbSchema: no attribute \"%s\" in adt", source);
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

bool MySqlHdbSchema::getData(const std::vector<std::string> sources,
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
        printf("MySqlHdbSchema.getData %s %s %s\n", sources.at(i).c_str(), start_date, stop_date);
        success = getData(sources.at(i).c_str(), start_date, stop_date,
                                           connection, notifyEveryNumRows);
        if(!success)
            break;
    }

    d_ptr->totalSources = 1;

    return success;

}

