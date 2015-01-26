#include "hdbextractorprivate.h"
#include <connection.h>
#include <configurabledbschema.h>
#include <queryconfiguration.h>

HdbExtractorPrivate::HdbExtractorPrivate()
{
}

HdbExtractorPrivate::~HdbExtractorPrivate()
{
    if(connection)
        delete connection;
    if(dbschema)
        delete dbschema;
    if(queryConfiguration)
        delete queryConfiguration;
}
