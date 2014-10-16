#include "configurationparser.h"
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

ConfigurationParser::ConfigurationParser()
{


}

bool ConfigurationParser::read(const char *filepath, std::map<std::string, std::string>& params)
{
    bool res = false;
    errno = 0;
    FILE *fp = fopen(filepath, "r");
    if(fp == NULL)
    {
        perr("ConfigurationParser.read: error reading \"%s\": %s", filepath, strerror(errno));
        strncpy(m_error, strerror(errno), MAXERRORLEN);
    }
    else
    {
        char *line = NULL;
        char *conf = NULL;
        size_t len = 0;
        ssize_t read;
        int conf_len;

        while ((read = getline(&line, &len, fp)) != -1)
        {
            if(strchr(line, '#') == NULL)
                conf_len = read;
            else
                conf_len = strchr(line, '#') - line;

            conf = realloc(conf, conf_len + 1);
            memset(conf, 0, conf_len + 1);
            strncpy(conf, line, conf_len);


            for (std::map<char,int>::iterator it=params.begin(); it!=params.end(); ++it)

        }

    }

}

const char *ConfigurationParser::getError() const
{
    return m_error;
}
