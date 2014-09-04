#include "xvariant.h"
#include <errno.h>
#include <stdlib.h>
#include <limits.h>
#include <stdio.h>
#include <time.h>
#include <string.h> /* strerror */
#include "hdbxmacros.h"
#include "xvariantprivate.h"

XVariant::~XVariant()
{
    // pinfo("~XVariant destructor: %p", this);
    if(d->mSize > 0 && d->mType == String && d->val != NULL)
    {
        char **ssi = (char **) d->val;
        for(size_t i = 0; i < d->mSize; i++)
        {
            char *si = (char *) ssi[i];
            delete si;
        }
    }
    delete_rdata();


    if(d->mSize > 0 && d->mType == String && d->w_val != NULL)
    {
        char **ssi = (char **) d->val;
        for(size_t i = 0; i < d->mSize; i++)
        {
            char *si = (char *) ssi[i];
            delete si;
        }
    }
    delete_wdata();

    delete d;
}

/** \brief The XVariant object is the Hdbextractor data container.
 *
 * This class allows storing every kind of data pertaining to an historical database.
 * It can contain, in turn, scalar values of different types (boolean, double, integer, string...)
 * and the bi dimensional counterpart, named Vector.
 *
 * You normally do not need to know how XVariant is created. From the user point of view, XVariant is
 * the data container from which you want to extract data.
 *
 * You usually will retrieve a list of XVariant objects. Each one of them represents the data contained
 * in a row of the historical database. XVariant has a timestamp marking the date and time data was saved,
 * a bunch of fields aimed at defining the type of data stored (Scalar or Vector, Double or Int or String,
 * read only or read write) and the size of the data stored. In the case of Vector, the data size indicates the
 * number of elements of the array. In the case of Scalar format, the size will always be 1.
 *
 * A validity flag is also used to indicate that the data memorized by XVariant has been correctly detected
 * when XVariant was constructed from the data stored into the database.
 *
 * @param source domain/family/member/attribute_name
 * @param timestam a timestamp in the format "2014-07-10 10:00:00"
 * @param strdataR a string representing the data read from the database
 * @param strdataW a string representing the data read from the database
 *        (write value of a read write quantity)
 * @param df the data format
 * @param dt the data type
 *
 * \note The writable property is assumed to be read only
 *
 * @see getFormat
 * @see getType
 * @see isValid
 * @see getWritable
 * @see toDouble
 * @see toDoubleVector
 */
XVariant::XVariant(const char* source, const char *timestamp, const char *strdata, DataFormat df, DataType dt, Writable wri)
{
    d = new XVariantPrivate();
    init_common(source, timestamp, df, dt);
    init_data();
    d->mWritable = wri;
    parse(strdata); /* at the end, after setting up other fields */
}

/** \brief The constructor for read write data.
 *
 * @param source domain/family/member/attribute_name
 * @param timestam a timestamp in the format "2014-07-10 10:00:00"
 * @param strdataR a string representing the data read from the database
 * @param strdataW a string representing the data read from the database
 *        (write value of a read write quantity)
 * @param df the data format
 * @param dt the data type
 *
 * \note The writable property is assumed to be read only
 *
 */
XVariant::XVariant(const char* source, const char *timestamp, const char *strdataR, const char *strdataW, DataFormat df, DataType dt)
{
    d = new XVariantPrivate();
    init_common(source, timestamp, df, dt);
    init_data();
    d->mWritable = RW;
    parse(strdataR, strdataW); /* at the end, after setting up other fields */
}

/** \brief copy constructor
 *
 * Create a new variant initialized from the values of the other parameter
 *
 * @param other the XVariant to be cloned.
 *
 */
XVariant::XVariant(const XVariant &other)
{
    d = new XVariantPrivate();
    init_data(); /* NULLify all pointers to data */

    d->mWritable = other.getWritable();
    d->mFormat  = other.getFormat();
    d->mType = other.getType();
    d->mSize = other.getSize();
    d->mIsValid = other.isValid();

    strncpy(d->mSource, other.getSource(), SRCLEN);
    strncpy(d->mError, other.getError(), ERRMSGLEN);
    strncpy(d->mTimestamp, other.getTimestamp(), TIMESTAMPLEN);

    if(d->mWritable != WO)
    {
        if(d->mType == XVariant::Double)
        {
            double *vd = new double[d->mSize];
            for(size_t i = 0; i < d->mSize; i++)
                vd[i] =  other.toDoubleP()[i];
            d->val = vd;
        }
        else if(d->mType == XVariant::Int)
        {
            long int *vi =  new long int[d->mSize];

            for(size_t i = 0; i < d->mSize; i++)
                vi[i] = other.toLongIntP()[i];
            d->val = vi;
        }
        else if(d->mType == XVariant::UInt)
        {
            unsigned long int *vi =  new unsigned long int[d->mSize];

            for(size_t i = 0; i < d->mSize; i++)
                vi[i] = other.toULongIntP()[i];
            d->val = vi;
        }
        else if(d->mType == XVariant::Boolean)
        {
            bool *vb = new bool[d->mSize];
            for(size_t i = 0; i < d->mSize; i++)
                vb[i] = other.toBoolP()[i];
            d->val = vb;
        }
    }

    /* write part */
    if(d->mWritable == XVariant::RW || d->mWritable == XVariant::WO)
    {
        if(d->mType == XVariant::Double)
        {
            double *vd = new double[d->mSize];
            for(size_t i = 0; i < d->mSize; i++)
                vd[i] =  other.toDoubleP()[i];
            d->w_val = vd;
        }
        else if(d->mType == XVariant::Int)
        {
            long int *vi =  new long int[d->mSize];
            for(size_t i = 0; i < d->mSize; i++)
                vi[i] = other.toLongIntP()[i];
            d->w_val = vi;
        }
        else if(d->mType == XVariant::UInt)
        {
            unsigned long int *vi =  new unsigned long int[d->mSize];
            for(size_t i = 0; i < d->mSize; i++)
                vi[i] = other.toULongIntP()[i];
            d->w_val = vi;
        }
        else if(d->mType == XVariant::Boolean)
        {
            bool *vb = new bool[d->mSize];
            for(size_t i = 0; i < d->mSize; i++)
                vb[i] = other.toBoolP()[i];
            d->w_val = vb;
        }
    }
}

/** \brief Returns the source name (tango full attribute name)
 */
const char* XVariant::getSource() const
{
    return d->mSource;
}

/** \brief Query the format of the data stored in the XVariant
 *
 * @return the DataFormat (XVariant::Vector, XVariant::Scalar, XVariant::Matrix)
 */
XVariant::DataFormat XVariant::getFormat() const
{
    return d->mFormat;
}

/** \brief Returns the DataType stored by XVariant
 *
 */
XVariant::DataType XVariant::getType() const
{
    return d->mType;
}

/** \brief Returns the Writable property, which tells if the attribute is read only, read write,
 *         write only and so on.
 *
 * @see XVariant::Writable
 */
XVariant::Writable XVariant::getWritable() const
{
    return d->mWritable;
}

/** \brief Returns whether the data stored by XVariant is valid
 *
 * @return true the data contained by XVariant is valid
 * @return false the data contained by XVariant is not valid (see getError)
 *
 * @see getError
 */
bool XVariant::isValid() const
{
    return d->mIsValid;
}

/** \brief Returns the description of the error reported by the last operation.
 *
 * \note All errors occurred before the last operation are discarded.
 */
const char *XVariant::getError() const
{
    return d->mError;
}

/** \brief Returns the size of the data stored by the XVariant
 *
 * @return the size of the data stored by the XVariant. This method is useful to
 *         know the size of a vector of data, in case XVariant encloses spectrum
 *         Tango attributes.
 */
size_t XVariant::getSize() const
{
    return d->mSize;
}

/** \brief This method parses a string in order to extract the data and convert it to the
 *         correct internal representation. This is <em>used internally</em>.
 *
 * @param s The string representation of the data, as fetched from the database.
 */
void XVariant::parse(const char *s)
{
    errno = 0;
    d->mIsValid = true;

    //   QHdbextractorThread("PARSING %s\n", s);
    if(d->mFormat == Scalar && d->mWritable == RO)
    {

        if(d->mType == Double)
        {
            double *v = new double[1];
            *v  = strtod(s, NULL);
            d->val = v;
            d->mSize = 1;
        }
        else if(d->mType == Int)
        {

            d->mSize = 1;
        }
        else if(d->mType == UInt)
        {

            d->mSize = 1;
        }
        else if(d->mType == Boolean)
        {

            d->mSize = 1;
        }
        else if(d->mType == String)
        {

            d->mSize = 1;
        }
        else
            d->mIsValid = false;
    }
    else if(d->mFormat == Vector && d->mWritable == RO)
    {
        size_t i = 0;
        d->mSize = 0;
        char *saveptr;
        char *copy = new char[strlen(s) + 1];
        char *val = NULL;
        const char *delim = ", ";
        /* make a copy of s cuz strtok_r wants char * not const char *.
         * It will be deleted at the end
         */
        strncpy(copy, s, strlen(s) + 1);
        /* count the number of separators in the data */
        val = strtok_r(copy, delim, &saveptr);
        while(val != NULL)
        {
            d->mSize++;
            val = strtok_r(NULL, delim, &saveptr);
        }
        strncpy(copy, s, strlen(s) + 1);

        if(d->mType == Double)
        {
            double *d_array = new double[d->mSize];
            val = strtok_r(copy, delim, &saveptr);
            /* split result returned by hdb (comma separated doubles) */
            while(val != NULL && errno == 0 && i < d->mSize)
            {
                *(d_array + i) = strtod(val, NULL);
                i++;
                val = strtok_r(NULL, delim, &saveptr);
            }
            d->val = d_array;
        }
        else if(d->mType == Int)
        {
            long int *li_array = new long int[d->mSize];
            val = strtok_r(copy, delim, &saveptr);
            /* split result returned by hdb (comma separated doubles) */
            while(val != NULL && errno == 0 && i < d->mSize)
            {
                *(li_array + i) = strtol(val, NULL, 10);
                i++;
                val = strtok_r(NULL, delim, &saveptr);
            }
            d->val = li_array;
        }
        else if(d->mType == UInt)
        {
            unsigned long int *uli_array = new unsigned long int[d->mSize];
            val = strtok_r(copy, delim, &saveptr);
            /* split result returned by hdb (comma separated doubles) */
            while(val != NULL && errno == 0 && i < d->mSize)
            {
                *(uli_array + i) = strtoul(val, NULL, 10);
                i++;
                val = strtok_r(NULL, delim, &saveptr);
            }
            d->val = uli_array;
        }
        else if(d->mType == Boolean)
        {
            bool *b_array = new bool[d->mSize];
            val = strtok_r(copy, delim, &saveptr);
            /* split result returned by hdb (comma separated doubles) */
            while(val != NULL && errno == 0 && i < d->mSize)
            {
                *(b_array + i) = (strcasecmp(s, "true") == 0 || strtol(s, NULL, 10) != 0);
                i++;
                val = strtok_r(NULL, delim, &saveptr);
            }
            d->val = b_array;
        }
        else if(d->mType == String)
        {

        }

        /* delete the copy of the string */
        delete copy;
    }

    /* Check for errors */
    if (errno != 0)
    {
        perr("XVariant.parse: error converting \"%s\" -> \"%s\": \"%s\"", d->mSource, s, strerror(errno));
        mMakeError(errno);
        d->mIsValid = false;
    }

    if(!d->mIsValid)
        perr("XVariant.parse(s): \"%s\": format %d writable %d type %d not supported",
             d->mSource, d->mFormat, d->mWritable, d->mType);
}

void XVariant::parse(const char *sr, const char *sw)
{
    errno = 0;
    d->mIsValid = true;

    //   QHdbextractorThread("PARSING %s\n", s);
    if(d->mFormat == Scalar && d->mWritable == RW)
    {
        if(d->mType == Double)
        {
            double *v = new double[1];
            double *wv = new double[1];
            *v  = strtod(sr, NULL);
            *wv = strtod(sw, NULL);
            d->val = v;
            d->w_val = wv;
            d->mSize = 1;
        }
        else if(d->mType == Int)
        {
            long int *i = new long int[1];
            long int *wi = new long int[1];
            *i = strtol(sr, NULL, 10);
            *wi = strtol(sw, NULL, 10);
            d->val = i;
            d->w_val = wi;
            d->mSize = 1;
        }
        else if(d->mType == UInt)
        {
            unsigned long int *ui = new unsigned long int[1];
            unsigned long int *wui = new unsigned long int[1];
            *ui = strtoul(sr, NULL, 10);
            *wui = strtoul(sw, NULL, 10);
            d->val = ui;
            d->w_val = wui;
            d->mSize = 1;
        }
        else if(d->mType == Boolean)
        {
            bool *b = new bool[1];
            bool *wb = new bool[1];
            *b = (strcasecmp(sr, "true") == 0 || strtol(sr, NULL, 10) != 0);
            *wb = (strcasecmp(sw, "true") == 0 || strtol(sw, NULL, 10) != 0);
            d->val = b;
            d->w_val = wb;
            d->mSize = 1;
        }
        else if(d->mType == String)
        {

            d->mSize = 1;
        }
        else
            d->mIsValid = false;
    }
    else if(d->mFormat == Vector && d->mWritable == RO)
    {
        size_t i = 0;
        d->mSize = 0;

        /* 1. read part */

        char *saveptr;
        char *copy = NULL;
        char *val = NULL;
        const char *delim = ", ";

        /* make a copy of s cuz strtok_r wants char * not const char *.
         * It will be deleted at the end
         */
        copy = new char[strlen(sr) + 1];
        strncpy(copy, sr, strlen(sr) + 1);
        /* count the number of separators in the data */
        val = strtok_r(copy, delim, &saveptr);
        while(val != NULL)
        {
            d->mSize++;
            val = strtok_r(NULL, delim, &saveptr);
        }
        strncpy(copy, sr, strlen(sr) + 1);

        if(d->mType == Double)
        {
            double *d_array = new double[d->mSize];
            val = strtok_r(copy, delim, &saveptr);
            /* split result returned by hdb (comma separated doubles) */
            while(val != NULL && errno == 0 && i < d->mSize)
            {
                *(d_array + i) = strtod(val, NULL);
                i++;
                val = strtok_r(NULL, delim, &saveptr);
            }
            d->val = d_array;
        }
        else if(d->mType == Int)
        {
            long int *li_array = new long int[d->mSize];
            val = strtok_r(copy, delim, &saveptr);
            /* split result returned by hdb (comma separated doubles) */
            while(val != NULL && errno == 0 && i < d->mSize)
            {
                *(li_array + i) = strtol(val, NULL, 10);
                i++;
                val = strtok_r(NULL, delim, &saveptr);
            }
            d->val = li_array;
        }
        else if(d->mType == UInt)
        {
            unsigned long int *uli_array = new unsigned long int[d->mSize];
            val = strtok_r(copy, delim, &saveptr);
            /* split result returned by hdb (comma separated doubles) */
            while(val != NULL && errno == 0 && i < d->mSize)
            {
                *(uli_array + i) = strtoul(val, NULL, 10);
                i++;
                val = strtok_r(NULL, delim, &saveptr);
            }
            d->val = uli_array;
        }
        else if(d->mType == Boolean)
        {
            bool *b_array = new bool[d->mSize];
            val = strtok_r(copy, delim, &saveptr);
            /* split result returned by hdb (comma separated doubles) */
            while(val != NULL && errno == 0 && i < d->mSize)
            {
                *(b_array + i) = (strcasecmp(sr, "true") == 0 || strtol(sr, NULL, 10) != 0);
                i++;
                val = strtok_r(NULL, delim, &saveptr);
            }
            d->val = b_array;
        }
        else if(d->mType == String)
        {

        }

        /* delete the copy of the string */
        delete copy;

        /* =========================================================================
         * 2. write part
         * =========================================================================
         */
        copy = new char[strlen(sw) + 1];
        size_t wri_size = 0;

        /* make a copy of s cuz strtok_r wants char * not const char *.
         * It will be deleted at the end
         */
        strncpy(copy, sw, strlen(sw) + 1);
        /* count the number of separators in the data */
        val = strtok_r(copy, delim, &saveptr);
        while(val != NULL)
        {
            wri_size++;
            val = strtok_r(NULL, delim, &saveptr);
        }
        strncpy(copy, sw, strlen(sw) + 1);
        if(wri_size == d->mSize)
        {
            if(d->mType == Double)
            {
                double *d_array = new double[wri_size];
                val = strtok_r(copy, delim, &saveptr);
                /* split result returned by hdb (comma separated doubles) */
                while(val != NULL && errno == 0 && i < d->mSize)
                {
                    *(d_array + i) = strtod(val, NULL);
                    i++;
                    val = strtok_r(NULL, delim, &saveptr);
                }
                d->w_val = d_array;
            }
            else if(d->mType == Int)
            {
                long int *li_array = new long int[d->mSize];
                val = strtok_r(copy, delim, &saveptr);
                /* split result returned by hdb (comma separated doubles) */
                while(val != NULL && errno == 0 && i < d->mSize)
                {
                    *(li_array + i) = strtol(val, NULL, 10);
                    i++;
                    val = strtok_r(NULL, delim, &saveptr);
                }
                d->w_val = li_array;
            }
            else if(d->mType == UInt)
            {
                unsigned long int *uli_array = new unsigned long int[d->mSize];
                val = strtok_r(copy, delim, &saveptr);
                /* split result returned by hdb (comma separated doubles) */
                while(val != NULL && errno == 0 && i < d->mSize)
                {
                    *(uli_array + i) = strtoul(val, NULL, 10);
                    i++;
                    val = strtok_r(NULL, delim, &saveptr);
                }
                d->w_val = uli_array;
            }
            else if(d->mType == Boolean)
            {
                bool *b_array = new bool[d->mSize];
                val = strtok_r(copy, delim, &saveptr);
                /* split result returned by hdb (comma separated doubles) */
                while(val != NULL && errno == 0 && i < d->mSize)
                {
                    *(b_array + i) = (strcasecmp(sw, "true") == 0 || strtol(sw, NULL, 10) != 0);
                    i++;
                    val = strtok_r(NULL, delim, &saveptr);
                }
                d->w_val = b_array;
            }
            else if(d->mType == String)
            {

            }
        } /* if(wri_size == d->mSize) */
        else
        {
            perr("XVariant.parse: error converting \"%s\":\n read and write sizes are different!", d->mSource);
            d->mIsValid = false;
        }

        /* delete the copy of the string */
        delete copy;

    }

    /* Check for string to number conversion errors */
    if (errno != 0)
    {
        perr("XVariant.parse: error converting \"%s\":\n    READ: \"%s\";\n    WRITE: %s: \"%s\"",
             d->mSource, sr, sw, strerror(errno));
        mMakeError(errno);
        d->mIsValid = false;
    }

    if(!d->mIsValid)
        perr("XVariant.parse(s): \"%s\": format %d writable %d type %d not supported or r/w size mismatch",
             d->mSource, d->mFormat, d->mWritable, d->mType);
}

void XVariant::mMakeError(int errnum)
{
    strncpy(d->mError, strerror(errnum), ERRMSGLEN);
    d->mIsValid = false;
}

void XVariant::init_common(const char* source, const char *timestamp, DataFormat df, DataType dt)
{
    d->mFormat = df;
    d->mType = dt;
    d->mIsValid = false;
    d->mSize = 0;

    strncpy(d->mTimestamp, timestamp, TIMESTAMPLEN);
    strncpy(d->mSource, source, SRCLEN);
}

void XVariant::init_data()
{
    d->val = NULL;
    d->w_val = NULL;
}

void XVariant::delete_rdata()
{
    if(d->val != NULL)
    {
        if(d->mType == Double)
            delete (double *) d->val;
        else if(d->mType == Int)
            delete (int *) d->val;
        else if(d->mType == Boolean)
            delete (bool *) d->val;
        else if(d->mType == String)
            delete (char *) d->val;
        d->val = NULL;
    }
}

void XVariant::delete_wdata()
{
    if(d->w_val != NULL)
    {
        if(d->mType == Double)
            delete (double *) d->w_val;
        else if(d->mType == Int)
            delete (int *) d->w_val;
        else if(d->mType == Boolean)
            delete (bool *) d->w_val;
        else if(d->mType == String)
            delete (char *) d->w_val;
        d->w_val = NULL;
    }
}

/** \brief Returns the timestamp associated to the data stored by XVariant, in the form
 *         of a string.
 *
 * @return A string representation of the timestamp associated to the data.
 */
const char *XVariant::getTimestamp() const
{
    return  d->mTimestamp;
}

/** \brief Returns the timestamp associated to the data stored by XVariant, in the form
 *         of a time_t data type
 *
 * @return A time_t value containing the date/time associated to the data.
 */
time_t XVariant::getTime_tTimestamp() const
{
    struct tm mtm;
    char *p = strptime(d->mTimestamp, "%Y-%m-%d %H:%M:%S", &mtm);
    return mktime(&mtm);
}

/** \brief The conversion method that tries to convert the stored data into a vector of double
 *
 * @return a std vector of double representing the data saved into XVariant
 * @param read true (the default if not specified) returns the <em>read</em> value saved into the database.
 * @param read false returns the <em>write</em> value saved into the database.
 *
 */
std::vector<double> XVariant::toDoubleVector(bool read) const
{
    double *d_val;
    if(read)
        d_val = (double *) d->val;
    else
        d_val = (double *) d->w_val;

    std::vector<double> dvalues(d_val, d_val + d->mSize);
    return dvalues;
}

/** \brief The conversion method that tries to convert the stored data into a vector of
 *         unsigned long integers
 *
 * @return a std vector of int representing the data saved into XVariant
 * @param read true (the default if not specified) returns the <em>read</em> value saved into the database.
 * @param read false returns the <em>write</em> value saved into the database.
 *
 * \note unsigned shorts and unsigned ints are mapped to unsigned long.
 *
 */
std::vector<unsigned long int> XVariant::toULongIntVector(bool read) const
{
    unsigned long int *i_val;
    if(read)
        i_val = (unsigned long int *) d->val;
    else
        i_val = (unsigned long int *) d->w_val;

    std::vector<unsigned long int> ivalues(i_val, i_val + d->mSize);
    return ivalues;
}

/** \brief The conversion method that tries to convert the stored data into a vector of
 *         unsigned long integers
 *
 * @return a std vector of int representing the data saved into XVariant
 * @param read true (the default if not specified) returns the <em>read</em> value saved into the database.
 * @param read false returns the <em>write</em> value saved into the database.
 *
 * \note unsigned shorts and unsigned ints are mapped to unsigned long.
 *
 */
std::vector<long int> XVariant::toLongIntVector(bool read) const
{
    long int *i_val;
    if(read)
        i_val = (long int *) d->val;
    else
        i_val = (long int *) d->w_val;

    std::vector<long int> ivalues(i_val, i_val + d->mSize);
    return ivalues;
}

/** \brief The conversion method that tries to convert the stored data into a vector of booleans
 *
 * @return a std vector of bool representing the data saved into XVariant
 * @param read true (the default if not specified) returns the <em>read</em> value saved into the database.
 * @param read false returns the <em>write</em> value saved into the database.
 *
 */
std::vector<bool> XVariant::toBoolVector(bool read) const
{
    bool *b_val;
    if(read)
        b_val = (bool *) d->val;
    else
        b_val = (bool *) d->w_val;

    std::vector<bool> bvalues(b_val, b_val + d->mSize);
    return bvalues;
}

/** \brief The conversion method that tries to convert the stored data into a double scalar
 *
 * @return a double representing the data saved into XVariant
 * @param read true (the default if not specified) returns the <em>read</em> value saved into the database.
 * @param read false returns the <em>write</em> value saved into the database.
 *
 * \note If the data cannot be converted to a double scalar value, then isValid will return false.
 * On the other hand, no error message is set by this method.
 *
 *
 */
double XVariant::toDouble(bool read, bool *ok) const
{
    double v = -9876.543210;
    if(read && d->mType == Double && d->mFormat == Scalar && (d->mWritable == RO || d->mWritable == RW) && d->val != NULL)
        v = *((double *)d->val);
    else if(!read && d->mType == Double && d->mFormat == Scalar && (d->mWritable == RW || d->mWritable == WO) && d->w_val != NULL)
        v = *((double *)d->w_val);
    if(ok)
        *ok = d->mIsValid && (d->val != NULL || d->w_val != NULL);
    return v;
}

/** \brief The conversion method that tries to convert the stored data into a long scalar integer
 *
 * @return an int representing the data saved into XVariant
 * @param read true (the default if not specified) returns the <em>read</em> value saved into the database.
 * @param read false returns the <em>write</em> value saved into the database.
 *
 * \note If the data cannot be converted to a integer scalar value, then isValid will return false.
 * On the other hand, no error message is set by this method.
 *
 */
long int XVariant::toLongInt(bool read, bool *ok) const
{
    long int i = -9999L;
    if(read && d->mType == Int && d->mFormat == Scalar && (d->mWritable == RO || d->mWritable == RW) && d->val != NULL)
        i = *((long int *)d->val);
    else if(read && d->mType == Int && d->mFormat == Scalar && (d->mWritable == RO || d->mWritable == RW) && d->w_val != NULL)
        i = *((long int *)d->w_val);
    if(ok)
        *ok = d->mIsValid && (d->val != NULL || d->w_val != NULL);
    return i;
}

/** \brief The conversion method that tries to convert the stored data into a long unsigned scalar integer
 *
 * @return an int representing the data saved into XVariant
 * @param read true (the default if not specified) returns the <em>read</em> value saved into the database.
 * @param read false returns the <em>write</em> value saved into the database.
 *
 * \note If the data cannot be converted to a integer scalar value, then isValid will return false.
 * On the other hand, no error message is set by this method.
 *
 */
unsigned long int XVariant::toULongInt(bool read, bool *ok) const
{
    unsigned long int i = -9999UL;
    if(read && d->mType == UInt && d->mFormat == Scalar && (d->mWritable == RO || d->mWritable == RW) && d->val != NULL)
        i = *((unsigned long int *)d->val);
    else if(read && d->mType == Int && d->mFormat == Scalar && (d->mWritable == RO || d->mWritable == RW) && d->w_val != NULL)
        i = *((unsigned long int *)d->w_val);
    if(ok)
        *ok = d->mIsValid && (d->val != NULL || d->w_val != NULL);
    return i;
}


/** \brief The conversion method that tries to convert the stored data into a scalar boolean
 *
 * @return a bool representing the data saved into XVariant
 * @param read true (the default if not specified) returns the <em>read</em> value saved into the database.
 * @param read false returns the <em>write</em> value saved into the database.
 *
 * \note If the data cannot be converted to a boolean scalar value, then isValid will return false.
 * On the other hand, no error message is set by this method.
 *
 */
bool XVariant::toBool(bool read, bool *ok) const
{
    bool b = false;
    if(read && d->mType == Boolean && d->mFormat == Scalar && (d->mWritable == RO || d->mWritable == RW) && d->val != NULL)
        b = *((bool *)d->val);
    else if(!read && d->mType == Boolean && d->mFormat == Scalar && (d->mWritable == RO || d->mWritable == RW) && d->w_val != NULL)
        b = *((bool *)d->w_val);
    if(ok)
        *ok = d->mIsValid && (d->val != NULL || d->w_val != NULL);
    return b;
}

/** \brief The conversion method that tries to convert the stored data into a scalar string
 *
 * @return a string representing the data saved into XVariant
 * @param read true (the default if not specified) returns the <em>read</em> value saved into the database.
 * @param read false returns the <em>write</em> value saved into the database.
 *
 * \note If the data cannot be converted to a boolean scalar value, then isValid will return false.
 * On the other hand, no error message is set by this method.
 *
 */
std::string XVariant::toString(bool read, bool *ok) const
{

}

/** \brief The conversion method that tries to convert the stored data into a vector of strings
 *
 * @return a std::vector of string representing the data saved into XVariant
 * @param read true (the default if not specified) returns the <em>read</em> value saved into the database.
 * @param read false returns the <em>write</em> value saved into the database.
 *
 * \note If the data cannot be converted to a vector of strings, then isValid will return false.
 * On the other hand, no error message is set by this method.
 *
 */
std::vector<std::string> XVariant::toStringVector() const
{

}

/** \brief Returns a pointer to a double addressing the start of data.
 *
 * Used with getSize allows to get the stored data in a "C" style.
 *
 * @see getSize
 * @see toLongIntP
 *
 * @param read true (default) get the pointer to the read data
 * @param read false get the pointer to the write data
 *
 * \note Check the return value of this method: if null, no data is currently
 *       stored or you are trying to extract a type of data different from the
 *       one memorized in XVariant.
 */
double *XVariant::toDoubleP(bool read) const
{
    if(read)
        return (double *) d->val ;
    return (double *) d->w_val;
}


/** \brief Returns a pointer to a long unsigned int addressing the start of data.
 *
 * Used with getSize allows to get the stored data in a "C" style.
 *
 * @see getSize
 * @see toDoubleP
 * @see toULongIntP
 *
 * @param read true (default) get the pointer to the read data
 * @param read false get the pointer to the write data
 *
 * \note Check the return value of this method: if null, no data is currently
 *       stored or you are trying to extract a type of data different from the
 *       one memorized in XVariant.
 *
 * \note shorts and ints are mapped to longs.
 */
unsigned long int *XVariant::toULongIntP(bool read) const
{
    if(read)
        return (unsigned long int *) d->val ;
    return (unsigned long int *) d->w_val;
}


/** \brief Returns a pointer to an int addressing the start of data.
 *
 * Used with getSize allows to get the stored data in a "C" style.
 *
 * @see getSize
 * @see toDoubleP
 *
 * @param read true (default) get the pointer to the read data
 * @param read false get the pointer to the write data
 *
 * \note Check the return value of this method: if null, no data is currently
 *       stored or you are trying to extract a type of data different from the
 *       one memorized in XVariant.
 *
 * \note shorts and ints are mapped to longs.
 */
long int *XVariant::toLongIntP(bool read) const
{
    if(read)
        return (long int *) d->val ;
    return (long int *) d->w_val;
}

/** \brief Returns a pointer to a boolean addressing the start of data.
 *
 * Used with getSize allows to get the stored data in a "C" style.
 *
 * @see getSize
 * @see toLongIntP for notes
 * @see toDoubleP for notes
 *
 */
bool *XVariant::toBoolP(bool read) const
{
    if(read)
        return (bool *) d->val ;
    return (bool *) d->w_val;
}

/** \brief Returns a char pointer addressing the start of data.
 *
 * Used with getSize allows to get the stored data in a "C" style.
 *
 * @see getSize
 * @see toLongIntP for notes
 * @see toDoubleP for notes
 *
 */
char **XVariant::toCharP(bool read) const
{
    if(read)
        return (char **) d->val ;
    return (char **) d->w_val;
}
