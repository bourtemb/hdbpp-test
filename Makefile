include ./Make-hdb++.in

default: trunk

trunk:
ifeq ($(HDB++MYSQL),1)
	cd libhdb++mysql/trunk ; $(MAKE)
endif
ifeq ($(HDBMYSQL),1)
	cd libhdbmysql/trunk ; $(MAKE)  
endif
ifeq ($(HDB++CASSANDRA),1)
	cd libhdb++cassandra/trunk ; $(MAKE)  
endif
	cd libhdb++/trunk ; $(MAKE)
	cd hdb++es/trunk ; $(MAKE)
	cd hdb++cm/trunk ; $(MAKE)  

clean: clean_trunk

clean_trunk:
	cd hdb++es/trunk ; $(MAKE) clean  
	cd hdb++cm/trunk ; $(MAKE) clean
	cd libhdb++/trunk ; $(MAKE) clean
ifeq ($(HDB++MYSQL),1)
	cd libhdb++mysql/trunk ; $(MAKE) clean
endif
ifeq ($(HDBMYSQL),1)
	cd libhdbmysql/trunk ; $(MAKE) clean 
endif
ifeq ($(HDB++CASSANDRA),1)
	cd libhdb++cassandra/trunk ; $(MAKE) clean
endif



