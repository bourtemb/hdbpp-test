#ifndef HDBSCHEMA_H
#define HDBSCHEMA_H

#include <dbschema.h>
#include <xvariantlist.h>
#include <resultlistenerinterface.h>
#include <vector>
#include <string>

class DbSchemaPrivate;
class XVariantList;

/** \brief An implementation of the DbSchema interface specific to MySql. <em>Used internally</em>.
 *
 */
class MySqlHdbSchema : public DbSchema
{
public:
    MySqlHdbSchema(ResultListener *resultListenerI);

    virtual ~MySqlHdbSchema();

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

private:
    DbSchemaPrivate *d_ptr;
};

#endif // HDBSCHEMA_H
