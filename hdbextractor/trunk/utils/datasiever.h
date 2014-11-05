#ifndef DATAFILLER_H
#define DATAFILLER_H

#include <vector>
#include <map>
#include <xvariant.h>

class DataSieverPrivate;

class DataSiever
{
public:
    DataFiller(FillMode fillMode = DetachSources);

    enum FillMode { None = 0x0, DetachSources = 0x1,  DetachSourcesAndFill = 0x2};

    std::map<std::string, std::vector<XVariant *> >sieve(const std::vector<XVariant *> &rawdata);

private:
    DataSieverPrivate *d_ptr;
};

#endif // DATAFILLER_H
