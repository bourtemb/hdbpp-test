include ./Make-hdb++.in

default: trunk

trunk:
ifeq ($(HDB++MYSQL),1)
	cd libhdb++mysql/trunk ; $(MAKE)
else
	cd libhdbmysql/trunk ; $(MAKE)  
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
else
	cd libhdbmysql/trunk ; $(MAKE) clean 
endif



