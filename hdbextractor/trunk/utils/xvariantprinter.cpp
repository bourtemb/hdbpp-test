#include "xvariantprinter.h"
#include <stdio.h>

XVariantPrinter::XVariantPrinter()
{

}

void XVariantPrinter::print(const XVariant &xv, int maxVectorElements)
{
    XVariant::DataFormat format = xv.getFormat();
    if(format == XVariant::Scalar)
    {
        printf("\"%s\": %s -> \e[1;32m%.2f\e[0m: , ", xv.getSource(),  xv.getTimestamp(), xv.toDouble());
    }
    else if(format == XVariant::Vector)
    {
        std::vector<double> values = xv.toDoubleVector();
        printf("\e[1;33m[ \"%s\": %s:\n\e[0m", xv.getSource(), xv.getTimestamp());
        for(size_t j = 0; j < values.size() || (maxVectorElements > 0 && j < maxVectorElements); j++)
        {
            if((maxVectorElements > 0 && j >= maxVectorElements))
                break;
            printf("%.3f, ", values.at(j));
        }
        printf(" \e[1;33m]\e[0m\n");
    }
    else
        printf("\e[1;31m format %d unrecognized\e[0m\n", format);
}

void XVariantPrinter::printValueList(const std::vector<XVariant > &valuelist, int maxVectorElements)
{
    for(size_t i = 0; i < valuelist.size(); i++)
    {
        XVariant::DataFormat format = valuelist[i].getFormat();
        if(format == XVariant::Scalar)
        {
            printf("\"%s\": %s -> \e[1;32m%.2f\e[0m:\n, ", valuelist[i].getSource(),  valuelist[i].getTimestamp(), valuelist[i].toDouble());
            if(i > 0 && i % 20 == 0)
                printf("\n");
        }
        else if(format == XVariant::Vector)
        {
            std::vector<double> values = valuelist[i].toDoubleVector();
            if(valuelist.size() > 0)
                printf("\e[1;33m[ \"%s\": %s\e[0m:\n", valuelist[i].getSource(), valuelist[i].getTimestamp());
            for(size_t j = 0; j < values.size() || (maxVectorElements > 0 && j < maxVectorElements); j++)
            {
                if((maxVectorElements > 0 && j >= maxVectorElements))
                    break;
                printf("%.3f, ", values.at(j));
            }
            printf(" \e[1;33m]\e[0m\n");

        }
    }
    printf("\n\n");
}
