#include <stdio.h>
#include <stdlib.h>
#include "myhdbextractorimpl.h"
#include "../src/hdbextractor.h"

using namespace std;

int main(int argc, char **argv)
{
    if(argc < 4)
    {
        printf("\e[1;31mUsage\e[0m \"%s domain/family/member/attribute 2014-07-20 10:00:00 2014-07-20 12:00:00\n",
               argv[0]);
        exit(EXIT_FAILURE);
    }
    else
    {
        MyHdbExtractorImpl *hdbxi = new MyHdbExtractorImpl();
        hdbxi->getData(argv[1], argv[2], argv[3]);
    }
    return 0;
}

