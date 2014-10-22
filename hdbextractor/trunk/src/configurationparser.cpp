#include "configurationparser.h"
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "hdbxmacros.h"

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
        char *param = NULL;
        char *conf_save = NULL;
        char *line_save = NULL;
        char key[32];
        int res;
        size_t len = 0;
        ssize_t read;
        int conf_len;

        while ((read = getline(&line, &len, fp)) != -1)
        {
            param = NULL;
            line_save = line; /* save pointer to buffer allocated by getline() */
            if(strchr(line, '#') == NULL)
                conf_len = read;
            else
                conf_len = strchr(line, '#') - line;

            if(conf_len > 0)
            {
                conf = (char *) realloc(conf_save, conf_len + 1);
                 /* save pointer to buffer allocated by realloc() */
                conf_save = conf;

                memset(conf, 0, conf_len + 1);

                /* in conf, put a copy of line without any space, tab, \n, \r */
                while(*line)
                {
                    if (!isspace(*line))
                    {
                        *conf = *line;
                        conf++;
                    }
                    line++;
                }
                line = line_save;
                conf = conf_save;
                // printf("parsing line \"%s\" -> trimmed \"%s\"\n", line, conf);
                if(strlen(conf) > 1)
                {
                    for (std::map<std::string,std::string>::iterator it = params.begin(); it != params.end(); ++it)
                    {
                        param = (char *) realloc(param, conf_len + 1);
                        //printf("SCANF on \"%s\"\n", conf);
                        //snprintf(format, 32, "%s=%%s", key.c_str());
                        res = sscanf(conf, "%[^=]=%s", key, param);
                        if(strcmp(key, it->first.c_str()) == 0)
                        {
                            printf("ConfigurationParser.read: \e[1;32m\"%s\" -> \"%s\"\e[0m\n",  key, param);
                            it->second = std::string(param);
                        }
                    }
                    if(param != NULL)
                        free(param);
                }
                conf = conf_save;
            }
        }

        if(conf)
            free(conf);
        if(line)
            free(line);

        fclose(fp);
    }

}

const char *ConfigurationParser::getError() const
{
    return m_error;
}
