#ifndef HDBEXTRACTORPRIVATE_H
#define HDBEXTRACTORPRIVATE_H

#include "hdbextractor.h" /* for db and schema type enum values */

#define MAXERRORLEN 512

class Connection;
class DbSchema;
class HdbExtractorListener;

class HdbExtractorPrivate
{
public:
    HdbExtractorPrivate();

    Hdbextractor::DbType dbType;

    Connection * connection;

    DbSchema *dbschema;

    HdbExtractorListener* hdbXListenerI;

    int updateEveryRows;

    char errorMessage[MAXERRORLEN];
};

#endif // HDBEXTRACTORPRIVATE_H
