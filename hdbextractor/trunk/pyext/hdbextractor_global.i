%module(directors="1") hdbextractor
%feature("director") HdbExtractorListener; // generate directors for all classes that have virtual methods
%feature("director") DataSieverProgressListener;
%feature("director") ResultListenerInterface;

%include datasieverprogresslistener.i 
%include hdbxsettings.i 
%include result.i
%include resultlistenerinterface.i 
%include xvariantdatainfo.i 
%include xvariantlist.i 
%include datasiever.i 
%include datetimeutils.i 
%include hdbextractorlistener.i 
%include connection.i 
%include mysqlconnection.i
%include configurationparser.i
%include timeinterval.i 
%include xvariant.i 
%include xvariantprinter.i
%include hdbextractor.i
