#ifndef MYHDBEXTRACTORIMPL_H
#define MYHDBEXTRACTORIMPL_H

#include "../src/hdbextractorlistener.h"

class Hdbextractor;

/** \brief an <em>example</em> of an implementation of the HdbExtractorListener
 *
 */
class MyHdbExtractorImpl : public HdbExtractorListener
{
public:
    MyHdbExtractorImpl();

    void getData(const char* source, const char* start_date, const char *stop_date);

    virtual void onSourceProgressUpdate(int step, int total);

    virtual void onSourceExtracted(int totalRows);

private:
    Hdbextractor *mExtractor;
};

#endif // MYHDBEXTRACTORIMPL_H
