#+======================================================================
# $Source: /segfs/tango/cvsroot/jclient/jblvac/src/jblvac/Makefile,v $
#
# Project:      Configurator and diagnotics for Tango HDB++
#
# Description:  Makefile to generate the JAVA Tango classes package
#
# $Author: verdier $
#
# $Version$
#
# $Log: Makefile,v $
#
# copyleft :    European Synchrotron Radiation Facility
#               BP 220, Grenoble 38043
#               FRANCE
#
#-======================================================================

MAJOR_RELEASE = 1
MINOR_RELEASE = 0
APPLI_VERS	=	$(MAJOR_RELEASE).$(MINOR_RELEASE)
TAG_VERS	=	Release_$(MAJOR_RELEASE)_$(MINOR_RELEASE)

PACKAGE      = org.tango.hdb_configurator
PACKAGE_HOME = org/tango/hdb_configurator
JAR_NAME = hdb_configurator

TANGO_HOME	=	/segfs/tango
TANGO_LIB	=	$(TANGO_HOME)/lib/java
PACK_HOME	=	$(TANGO_HOME)/tools
JAR_DIR 	=	$(TANGO_JAR_HOME)/appli

SVN_CONFIGURATOR_PATH = $(SVN_TCS)/archiving/hdb++/gui/java-configurator
SVN_TAG_REV =	Release-$(MAJOR_RELEASE).$(MINOR_RELEASE)
# -----------------------------------------------------------------
#
#		The compiler flags
#
#------------------------------------------------------------------

BIN_DIR   = ../bin
JAVAFLAGS = -deprecation -Xlint:unchecked  -d $(BIN_DIR)
JAVAC     = javac  $(JAVAFLAGS)

#-------------------------------------------------------------------


#-----------------------------------------------------------------

all:	table common config diags

trace:
	@echo $(CLASSPATH)

table:
	@echo "-----------------------------------------"
	@echo "	Building $(PACKAGE).atktable"
	@echo "-----------------------------------------"
	$(JAVAC) -d $(BIN_DIR) $(PACKAGE_HOME)/atktable/*.java

common:
	@echo "-----------------------------------------"
	@echo "	Building $(PACKAGE).common"
	@echo "-----------------------------------------"
	$(JAVAC) -d $(BIN_DIR) $(PACKAGE_HOME)/common/*.java

config:
	@echo "-----------------------------------------"
	@echo "	Building $(PACKAGE)/"configurator
	@echo "-----------------------------------------"
	$(JAVAC) -d $(BIN_DIR) $(PACKAGE_HOME)/configurator/strategy/*.java
	$(JAVAC) -d $(BIN_DIR) $(PACKAGE_HOME)/configurator/*.java

diags:
	@echo "-----------------------------------------"
	@echo "	Building $(PACKAGE)/diagnostics"
	@echo "-----------------------------------------"
	$(JAVAC) -d $(BIN_DIR) $(PACKAGE_HOME)/diagnostics/*.java


img:
	cp  $(PACKAGE_HOME)/img/* $(BIN_DIR)/$(PACKAGE_HOME)/img

clean:
	rm  -f $(BIN_DIR)/$(PACKAGE)/tools/*.class
	rm  -f $(BIN_DIR)/$(PACKAGE)/common/*.class
	rm  -f $(BIN_DIR)/$(PACKAGE)/atktable/*.class
	rm  -f $(BIN_DIR)/$(PACKAGE)/configurator/strategy/*.class
	rm  -f $(BIN_DIR)/$(PACKAGE)/configurator/*.class
	rm  -f $(BIN_DIR)/$(PACKAGE)/diagnostics/*.class

jar :	version img
	@echo "-----------------------------------------"
	@echo "	Building jar file"
	@echo "-----------------------------------------"
	@make clean
	@make all
	@make_jar $(JAR_NAME) $(PACKAGE_HOME) $(APPLI_VERS) $(JAR_DIR)





JAVA=java
CLP=/segfs/tango/release/java/appli/Pogo.jar
version:
	@echo "-----------------------------------------"
	@echo "	Patching Version"
	@echo "-----------------------------------------"
	cd $(PACKAGE_HOME)/common; upd_rev  -f SplashUtils.java  -r $(APPLI_VERS)
	$(JAVA) -classpath $(CLP)  pogo.make_util.ReleaseNote2html -java  "$(PACKAGE).tools Release Note"   $(PACKAGE).common
	mv ReleaseNote.java  $(PACKAGE_HOME)/common


OPERATION = /operation/dserver/java/appli
install_op:
	@segfs2operation $(JAR_DIR) $(JAR_NAME) $(APPLI_VERS) $(OPERATION)


FTP_TARGET	=	ftpserv:/ftp/anonymous/pub/cs/tango/hdb++
JAR_FILE	=	$(JAR_NAME)-$(APPLI_VERS).jar
install_ftp:
	@echo "rcp $(JAR_FILE) $(FTP_TARGET)"
	@cd $(JAR_DIR); rcp $(JAR_FILE) $(FTP_TARGET)
	rcp ReleaseNote $(FTP_TARGET)


POGO=/segfs/tango/release/java/appli/org.tango.pogo.jar
DOC_HEADER=	"HDB++ Java Configurator"
DOC_DIR=../doc/reference
doc:
	javadoc 				\
	-version -author		\
	-public					\
	-windowtitle "HDB++ Java Classes" \
	-header $(DOC_HEADER)	\
	-d $(DOC_DIR)			\
	-link  .				\
	-group "HDB++ configurator classes"    "org.tango.hdbcpp.configurator" \
	-group "HDB++ diagnostics classes"     "org.tango.hdbcpp.diagnostics"  \
	-group "HDB++ common classes"          "org.tango.hdbcpp.common"  \
	-overview overview.html	\
		org/tango/hdbcpp/configurator/*.java \
		org/tango/hdbcpp/diagnostics/*.java  \
		org/tango/hdbcpp/common/*.java
	java -classpath $(POGO) pogo.make_util.ReleaseNote2html -html  "HDB++ Configurator Release Note"
	@mv ReleaseNote.html $(DOC_DIR)



tag:
	@echo "Tagging  $(PACKAGE)  for $(SVN_TAG_REV)"
	svn copy  $(SVN_CONFIGURATOR_PATH)/trunk \
	          $(SVN_CONFIGURATOR_PATH)/tags/$(JAR_NAME)-$(SVN_TAG_REV)

