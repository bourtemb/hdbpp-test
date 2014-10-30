#include <stdio.h>
#include <stdlib.h>
#include "myhdbextractorimpl.h"
#include "queryconfiguration.h"
#include "../src/hdbextractor.h"
#include "../src/configurationparser.h"
#include <map>

using namespace std;

int main(int argc, char **argv)
{
    if(argc < 5)
    {
        printf("\e[1;31mUsage\e[0m \"%s configfile.dat domain/family/member/attribute 2014-07-20 10:00:00 2014-07-20 12:00:00\n",
               argv[0]);
        exit(EXIT_FAILURE);
    }
    else
    {
        std::map<std::string, std::string> confmap ;
        confmap["dbuser"] = "hdbbrowser";
        confmap["dbpass"] = "hdbbrowser";
        confmap["dbhost"] = "fcsproxy";
        confmap["dbname"] = "hdb";
        confmap["dbport"] = "3306";


        ConfigurationParser cp;
        cp.read(argv[1], confmap);

        QueryConfiguration *qc = new QueryConfiguration();
        qc->loadFromFile(argv[1]);

        MyHdbExtractorImpl *hdbxi = new MyHdbExtractorImpl(confmap["dbuser"].c_str(),
                confmap["dbpass"].c_str(), confmap["dbhost"].c_str(), confmap["dbname"].c_str());

        hdbxi->getHdbExtractor()->setQueryConfiguration(qc);
        hdbxi->getData(argv[2], argv[3], argv[4]);

        delete qc;
    }
    return 0;
}

