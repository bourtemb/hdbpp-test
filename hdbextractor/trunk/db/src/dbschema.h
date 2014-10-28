#ifndef DBSCHEMA_H
#define DBSCHEMA_H

#include <vector>
#include <string>

class Connection;
class XVariantList;
class XVariant;
class QueryConfiguration;

/** \brief The interface representing a database schema. <em>Used internally</em>.
 *
 * \interface DbSchema
 *
 * This interface provides the main method to retrieve data from the database.
 * Any implementation of this interface is specific to a database (e.g. MySql, InfluxDB)
 * and a schema (hdb, hdb++, ...)
 *
 */
class DbSchema
{
public:
    DbSchema() {}

    virtual ~DbSchema() {}

    /** \brief Fetch data from the database.
     *
     * Fetch data from the database.
     * \note This method is used by HdbExtractor and it is not meant to be directly used by the library user.
     *
     * @param source the tango attribute in the form domain/family/member/AttributeName
     * @param start_date the start date (begin of the requested data interval) as string, such as "2014-07-10 10:00:00"
     * @param stop_date the stop date (end of the requested data interval) as string, such as "2014-07-10 12:00:00"
     * @param connection the database Connection specific object
     * @param notifyEveryRows the number of rows that make up a block of data. Every time a block of data is complete
     *        notifications are sent to the listener of type ResultListener (HdbExtractor)
     *
     *
     * @return true if the call was successful, false otherwise.
     */
    virtual bool getData(const char *source,
                                    const char *start_date,
                                    const char *stop_date,
                                    Connection *connection,
                                    int notifyEveryRows) = 0;

    virtual bool getData(const std::vector<std::string> sources,
                                 const char *start_date,
                                 const char *stop_date,
                                 Connection *connection,
                                 int notifyEveryNumRows) = 0;

    /** \brief Empties the queue where partial (or complete) data is stored
     *
     * \note This function is thread safe
     *
     * @return the number of XVariant elements appended to variantlist, -1 in case an error happened somewhere
     */
    virtual int get(std::vector<XVariant>& variantlist) = 0;

    /** \brief This method returns the last error message that occurred.
     *
     * If during the last operation no error occurred, this method should return an empty string
     * and hasError should return false.
     * The return value is not empty only if the last operation caused an error. This means that
     * previous error messages are lost.
     *
     * @return a string representing the error occurred during the last operation.
     *
     * @see hasError
     */
    virtual const char *getError() const = 0;

    /** Returns whether the last operation lead to an error or not
     *
     * @see getError
     */
    virtual bool hasError() const = 0;

};

#endif // DBSCHEMA_H
