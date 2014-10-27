#include "queryconfiguration.h"
#include "hdbxmacros.h"
#include <stdlib.h>

QueryConfiguration::QueryConfiguration()
{

}

void QueryConfiguration::loadFromFile(const char *filename)
{
    mMap.clear();
}

void QueryConfiguration::add(const char* key, const char *value)
{
    mMap[std::string(key)] = std::string(value);
}

bool QueryConfiguration::hasKey(const char *key) const
{
    return mMap.count(std::string(key)) > 0;
}

long int QueryConfiguration::getInt(const char *key, bool *ok) const
{
    long int ret = 0;
    std::string val = mMap.at(key);
    char **endptr;
    if(val.size() > 0)
    {
        const char *c = val.c_str();
        ret = strtol(c, endptr, 10);
        if(ok != NULL && ret == 0 && *endptr == c) /* failed */
        {
            perr("QueryConfiguration::getInt: error converting \"%s\" to int", c);
            *ok = false;
        }
        else if(ok != NULL)
            *ok = true;
    }
    return ret;
}

std::string QueryConfiguration::get(const char *key) const
{

}

bool QueryConfiguration::getBool(const char *key) const
{

}

double QueryConfiguration::getDouble(const char* key) const
{

}
