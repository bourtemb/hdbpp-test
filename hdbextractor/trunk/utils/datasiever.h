#ifndef DATAFILLER_H
#define DATAFILLER_H

#include <vector>
#include <string>
#include <map>
#include <xvariant.h>

class DataSieverPrivate;

class DataSiever
{
public:

    DataSiever();

    virtual ~DataSiever();

    void divide(const std::vector<XVariant> &rawdata);

    void fill();

    void clear();

    size_t getSize() const;

    std::vector<std::string> getSources() const;

    std::vector<XVariant> getData(std::string source) const;

    const std::map<std::string, std::vector<XVariant> > &getDataRef() const;

    std::map<std::string, std::vector<XVariant> > getData() const;

    bool contains(std::string source) const;

private:
    DataSieverPrivate *d_ptr;
};

#endif // DATAFILLER_H
