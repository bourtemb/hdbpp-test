#ifndef DATAFILLERPRIVATE_H
#define DATAFILLERPRIVATE_H

#include <datasiever.h>

class DataSieverPrivate
{
public:
    DataSieverPrivate();

    std::map<std::string, std::vector<XVariant> > dataMap;

    double minTimestamp;
};

#endif // DATAFILLERPRIVATE_H
