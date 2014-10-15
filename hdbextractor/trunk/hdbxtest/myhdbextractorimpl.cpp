#include "myhdbextractorimpl.h"

#include "../src/hdbextractor.h"
#include "../src/xvariant.h"

MyHdbExtractorImpl::MyHdbExtractorImpl()
{
    const char *dbuser = "giacomo";
    const char *dbpass = "";
    const char *dbhost = "scw-dev";
    const char *dbnam = "hdbpp";

    printf("\033[0;37mtrying to connect to host: \"%s\" db name: \"%s\" user: \"%s\"\033[0m\t", dbhost, dbnam, dbuser);

    mExtractor = new Hdbextractor(this);
    bool res = mExtractor->connect(Hdbextractor::HDBPPMYSQL, dbhost, dbnam, dbuser, dbpass);
    if(res)
    {
        printf("\e[1;32mOK\e[0m\n");
        mExtractor->setUpdateProgressStep(20);
    }
    else {
        printf("\e[1;31merror connecting to host: %s\e[0m\n", dbhost);
    }

}

void MyHdbExtractorImpl::getData(const char* source, const char* start_date, const char *stop_date)
{

    bool res = mExtractor->getData(source, start_date, stop_date);
    if(!res)
    {
        printf("\e[1;31merror fetching data: %s\e[0m\n", source);
    }
}

/** \brief this method is invoked according to the numRows value configured in setUpgradeProgressStep
 *         whenever numRows rows are read from the database.
 *
 * \note By default, if numRows is not set, onProgressUpdate is not invoked and the results
 *       are available when onFinished is invoked.
 *
 * @see onFinished
 */
void MyHdbExtractorImpl::onSourceProgressUpdate(const char *name, int step, int total)
{
    printf("\"%s\" data extraction: %.2f%% [%d/%d]\n", name, (float)step / total * 100.0, step, total);
    std::vector<XVariant> valuelist;
    mExtractor->get(valuelist);

    for(size_t i = 0; i < valuelist.size(); i++)
    {
        XVariant::DataFormat format = valuelist[i].getFormat();
        if(format == XVariant::Scalar)
        {
            printf("%s: %.2f, ", valuelist[i].getTimestamp(), valuelist[i].toDouble());
            if(i > 0 && i % 20 == 0)
                printf("\n");
        }
        else if(format == XVariant::Vector)
        {
            std::vector<double> values = valuelist[i].toDoubleVector();
            if(valuelist.size() > 0)
                printf("\e[1;33m[ %s\e[0m", valuelist[i].getTimestamp());
            for(size_t j = 0; j < values.size(); j++)
                printf("\e[0;35m%ld:\e[1;32m %g\e[0m ,", j, values[j]);
            printf(" \e[1;33m]\e[0m\n");

        }
    }
    printf("\n\n");
}

/** \brief this method is invoked when data extraction is fully accomplished.
 *
 */
void MyHdbExtractorImpl::onSourceExtracted(const char * name, int sourceStep, int sourcesTotal, double elapsed)
{
    printf("extraction completed: got %d rows from \"%s\" in %fs\n", sourceStep, name, elapsed);
}

