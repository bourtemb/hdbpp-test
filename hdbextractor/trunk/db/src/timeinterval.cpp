#include "timeinterval.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "datetimeutils.h"

TimeInterval::TimeInterval()
{
    memset(m_start, 0, MAXTIMESTAMPLEN);
    memset(m_stop, 0, MAXTIMESTAMPLEN);
}

TimeInterval::TimeInterval(const char *sta, const char *sto)
{
    strncpy(m_start, sta, MAXTIMESTAMPLEN);
    strncpy(m_stop,  sto, MAXTIMESTAMPLEN);
}

TimeInterval::TimeInterval(time_t start_tt, time_t stop_tt)
{
    DateTimeUtils dtu;
    dtu.toString(start_tt, m_start, MAXTIMESTAMPLEN);
    dtu.toString(stop_tt,  m_stop, MAXTIMESTAMPLEN);
}

TimeInterval::TimeInterval(const struct timeval* start_tv, const struct timeval* stop_tv)
{
    DateTimeUtils dtu;
    dtu.toString(start_tv, m_start, MAXTIMESTAMPLEN);
    dtu.toString(stop_tv,  m_stop, MAXTIMESTAMPLEN);
}

TimeInterval::TimeInterval(double start_d, double stop_d)
{
    DateTimeUtils dtu;
    dtu.toString(start_d, m_start, MAXTIMESTAMPLEN);
    dtu.toString(stop_d, m_stop, MAXTIMESTAMPLEN);
}

TimeInterval::TimeInterval(const TimeInterval& other)
{
    strncpy(m_start, other.start(), MAXTIMESTAMPLEN);
    strncpy(m_stop,  other.stop(), MAXTIMESTAMPLEN);
}

TimeInterval & TimeInterval::operator=(const TimeInterval& other)
{
    strncpy(m_start, other.start(), MAXTIMESTAMPLEN);
    strncpy(m_stop,  other.stop(), MAXTIMESTAMPLEN);
}

bool TimeInterval::operator==(const TimeInterval &other) const
{
    return strcmp(m_start, other.start()) == 0 && strcmp(m_stop, other.stop()) == 0;
}

bool TimeInterval::operator!=(const TimeInterval &other) const
{
    return !(*this == other);
}

time_t TimeInterval::start_time_t() const
{
    time_t ret = 0;
    if(strlen(m_start) > 0)
        ret = DateTimeUtils().toTime_t(m_start);
    return ret;
}

time_t TimeInterval::stop_time_t() const
{
    time_t ret = 0;
    if(strlen(m_stop) > 0)
        ret = DateTimeUtils().toTime_t(m_stop);
    return ret;
}

struct timeval TimeInterval::start_timeval() const
{
    struct timeval ret;
    memset(&ret, 0, sizeof(struct timeval));
    if(strlen(m_start) > 0)
        ret = DateTimeUtils().toTimeval(m_start);
    return ret;
}

struct timeval TimeInterval::stop_timeval() const
{
    struct timeval ret;
    memset(&ret, 0, sizeof(struct timeval));
    if(strlen(m_stop) > 0)
        ret = DateTimeUtils().toTimeval(m_stop);
    return ret;
}

