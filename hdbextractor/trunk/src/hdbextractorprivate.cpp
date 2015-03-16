#include "hdbextractorprivate.h"
#include "db/connection.h"
#include "db/configurabledbschema.h"
#include "hdbxsettings.h"

HdbExtractorPrivate::HdbExtractorPrivate()
{
}

HdbExtractorPrivate::~HdbExtractorPrivate()
{
    if(connection)
        delete connection;
    if(dbschema)
        delete dbschema;
    if(hdbxSettings)
        delete hdbxSettings;
}
