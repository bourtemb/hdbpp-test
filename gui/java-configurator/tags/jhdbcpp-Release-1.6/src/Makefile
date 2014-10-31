#+======================================================================
# $Source: /segfs/tango/cvsroot/jclient/jblvac/src/jblvac/Makefile,v $
#
# Project:      Tango Device Server
#
# Description:  Makefile to generate the JAVA Tango classes package
#
# $Author: verdier $
#
# $Version$
#
# $Log: Makefile,v $
# Revision 1.2  2014-04-22 13:53:29  verdier
# Line/device disable added.
#
# Revision 1.1.1.1  2014-04-02 12:52:19  verdier
# Initial Revision
#
# copyleft :    European Synchrotron Radiation Facility
#               BP 220, Grenoble 38043
#               FRANCE
#
#-======================================================================

MAJOR_RELEASE=1
MINOR_RELEASE=6
APPLI_VERS	=	$(MAJOR_RELEASE).$(MINOR_RELEASE)
TAG_VERS	=	Release_$(MAJOR_RELEASE)_$(MINOR_RELEASE)

PACKAGE      = org.tango.hdbcpp
PACKAGE_HOME = org/tango/hdbcpp
JAR_NAME = jhdbcpp

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
JAVAFLAGS = -d $(BIN_DIR)
JAVAC     = javac  $(JAVAFLAGS)

#-------------------------------------------------------------------


#-----------------------------------------------------------------

all:	 tools  common config diags

trace:
	@echo $(CLASSPATH)

tools:
	@echo "-----------------------------------------"
	@echo "	Building $(PACKAGE).tools"
	@echo "-----------------------------------------"
	javac -d $(BIN_DIR) $(PACKAGE_HOME)/tools/*.java

common:
	@echo "-----------------------------------------"
	@echo "	Building $(PACKAGE).common"
	@echo "-----------------------------------------"
	javac -d $(BIN_DIR) $(PACKAGE_HOME)/common/*.java

table:
	@echo "-----------------------------------------"
	@echo "	Building $(PACKAGE).atktable"
	@echo "-----------------------------------------"
	javac -d $(BIN_DIR) $(PACKAGE_HOME)/atktable/*.java

config:
	@echo "-----------------------------------------"
	@echo "	Building $(PACKAGE)/"configurator
	@echo "-----------------------------------------"
	javac -d $(BIN_DIR) $(PACKAGE_HOME)/configurator/*.java

diags:
	@echo "-----------------------------------------"
	@echo "	Building $(PACKAGE)/diagnostics"
	@echo "-----------------------------------------"
	javac -d $(BIN_DIR) $(PACKAGE_HOME)/diagnostics/*.java


img:
	cp  $(PACKAGE_HOME)/img/* $(BIN_DIR)/$(PACKAGE_HOME)

clean:
	rm  -f $(BIN_DIR)/$(PACKAGE)/tools/*.class
	rm  -f $(BIN_DIR)/$(PACKAGE)/common/*.class
	rm  -f $(BIN_DIR)/$(PACKAGE)/atktable/*.class
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
	cd $(PACKAGE_HOME)/tools; upd_rev  -f SplashUtils.java  -r $(APPLI_VERS)
	$(JAVA) -classpath $(CLP)  pogo.make_util.ReleaseNote2html -java  "$(PACKAGE).tools Release Note"   $(PACKAGE).common
	mv ReleaseNote.java  $(PACKAGE_HOME)/common


OPERATION = /operation/dserver/java/appli
install_op:
	@segfs2operation $(JAR_DIR) $(JAR_NAME) $(APPLI_VERS) $(OPERATION)




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
	-group "HDB++ tools classes"           "org.tango.hdbcpp.tools"   \
	-overview overview.html	\
		org/tango/hdbcpp/configurator/*.java \
		org/tango/hdbcpp/diagnostics/*.java  \
		org/tango/hdbcpp/common/*.java  \
		org/tango/hdbcpp/tools/*.java
	java -classpath $(POGO) pogo.make_util.ReleaseNote2html -html  "HDB++ Configurator Release Note"
	@mv ReleaseNote.html $(DOC_DIR)



tag:
	@echo "Tagging  $(PACKAGE)  for $(SVN_TAG_REV)"
	svn copy  $(SVN_CONFIGURATOR_PATH)/trunk \
	          $(SVN_CONFIGURATOR_PATH)/tags/$(JAR_NAME)-$(SVN_TAG_REV)

