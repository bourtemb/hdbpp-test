#ifndef MYSQLHDBPPSCHEMA_H
#define MYSQLHDBPPSCHEMA_H

#include <dbschema.h>
#include <dbschema.h>
#include <xvariantlist.h>
#include <resultlistenerinterface.h>
#include <vector>
#include <string>

class DbSchemaPrivate;

class MySqlHdbppSchema : public DbSchema
{
public:
    MySqlHdbppSchema(ResultListener *resultListenerI);

    virtual ~MySqlHdbppSchema();

    virtual bool getData(const char *source,
                                    const char *start_date,
                                    const char *stop_date,
                                    Connection *connection,
                                    int notifyEveryNumRows);

    virtual bool getData(const std::vector<std::string> sources,
                                 const char *start_date,
                                 const char *stop_date,
                                 Connection *connection,
                                 int notifyEveryNumRows);

    virtual int get(std::vector<XVariant>& variantlist);

    virtual const char *getError() const;

    virtual bool hasError() const;

    virtual bool setQueryConfiguration(QueryConfiguration *queryConfiguration);

private:
    DbSchemaPrivate *d_ptr;
};

#endif // MYSQLHDBPPSCHEMA_H
