#ifndef CONFIGURABLEDBSCHEMAHELPER_H
#define CONFIGURABLEDBSCHEMAHELPER_H

class QueryConfiguration;

class ConfigurableDbSchemaHelper
{
public:

    /** \brief This enumeration reflects the options from the "fill from the past" feature
     *
     */
    enum FillFromThePastMode { None, KeepWindow, WidenWindow };

    ConfigurableDbSchemaHelper();

    FillFromThePastMode fillFromThePastMode(const QueryConfiguration* queryConfiguration,
                             const char *start_date, const char *stop_date,
                             const char *window_start_date) const;

};

#endif // CONFIGURABLEDBSCHEMAHELPER_H
