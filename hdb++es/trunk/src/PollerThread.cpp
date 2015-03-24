static const char *RcsId = "$Header: /home/cvsadm/cvsroot/fermi/servers/hdb++/hdb++es/src/PollerThread.cpp,v 1.6 2014-03-06 15:21:43 graziano Exp $";
//+=============================================================================
//
// file :         PollerThread.cpp
//
// description :  C++ source for thread management
// project :      TANGO Device Server
//
// $Author: graziano $
//
// $Revision: 1.6 $
//
// $Log: PollerThread.cpp,v $
//
//
//
// copyleft :     European Synchrotron Radiation Facility
//                BP 220, Grenoble 38043
//                FRANCE
//
//-=============================================================================


#include <HdbDevice.h>
#include <HdbEventSubscriber.h>


namespace HdbEventSubscriber_ns
{


//=============================================================================
//=============================================================================
PollerThread::PollerThread(HdbDevice *dev)
{
	hdb_dev = dev;
	abortflag = false;
	period  = dev->poller_period;
	cout <<__func__<< "period="<<period<<" dev->poller_period="<<dev->poller_period<<endl;
	last_stat.tv_sec = 0;
	last_stat.tv_usec = 0;
}
//=============================================================================
//=============================================================================
void *PollerThread::run_undetached(void *ptr)
{
	cout << "PollerThread id="<<omni_thread::self()->id()<<endl;
	hdb_dev->AttributeRecordFreq = -1;
	hdb_dev->AttributeFailureFreq = -1;
	while(abortflag==false)
	{
		//cout << "PollerThread sleeping period="<<period<<endl;
		if(period > 0)
			abort_sleep((double)period);
		else
			abort_sleep(3.0);
		//cout << "PollerThread awake!"<<endl;

		//vector<string> attribute_list_tmp = hdb_dev->get_sig_list();

		//TODO: allocate AttributeRecordFreqList and AttributeFailureFreqList dynamically, but be careful to race conditions with read attribute
		/*if(hdb_dev->AttributeRecordFreqList != NULL)
			delete [] hdb_dev->AttributeRecordFreqList;
		hdb_dev->AttributeRecordFreqList = new Tango::DevDouble[attribute_list_tmp.size()];
		if(hdb_dev->AttributeFailureFreqList != NULL)
			delete [] hdb_dev->AttributeFailureFreqList;
		hdb_dev->AttributeFailureFreqList = new Tango::DevDouble[attribute_list_tmp.size()];*/

		try
		{
			(hdb_dev->_device)->push_change_event("AttributePendingNumber",&hdb_dev->AttributePendingNumber);
			(hdb_dev->_device)->push_archive_event("AttributePendingNumber",&hdb_dev->AttributePendingNumber);
		}
		catch(Tango::DevFailed &e){}
		usleep(1000);
		try
		{
			(hdb_dev->_device)->push_change_event("AttributeNumber",&hdb_dev->attr_AttributeNumber_read);
			(hdb_dev->_device)->push_archive_event("AttributeNumber",&hdb_dev->attr_AttributeNumber_read);
		}
		catch(Tango::DevFailed &e){}
		usleep(1000);
		try
		{
			(hdb_dev->_device)->push_change_event("AttributeStartedNumber",&hdb_dev->attr_AttributeStartedNumber_read);
			(hdb_dev->_device)->push_archive_event("AttributeStartedNumber",&hdb_dev->attr_AttributeStartedNumber_read);
		}
		catch(Tango::DevFailed &e){}
		usleep(1000);
		try
		{
			(hdb_dev->_device)->push_change_event("AttributeStoppedNumber",&hdb_dev->attr_AttributeStoppedNumber_read);
			(hdb_dev->_device)->push_archive_event("AttributeStoppedNumber",&hdb_dev->attr_AttributeStoppedNumber_read);
		}
		catch(Tango::DevFailed &e){}
		usleep(1000);
		try
		{
			(hdb_dev->_device)->push_change_event("AttributeMaxPendingNumber",&hdb_dev->AttributeMaxPendingNumber);
			(hdb_dev->_device)->push_archive_event("AttributeMaxPendingNumber",&hdb_dev->AttributeMaxPendingNumber);
		}
		catch(Tango::DevFailed &e){}

		if (hdb_dev->shared->is_initialized())
		{
			hdb_dev->attr_AttributeOkNumber_read = hdb_dev->get_sig_not_on_error_num();
		}
		else
			hdb_dev->attr_AttributeOkNumber_read = 0;
		try
		{
			(hdb_dev->_device)->push_change_event("AttributeOkNumber",&hdb_dev->attr_AttributeOkNumber_read);
			(hdb_dev->_device)->push_archive_event("AttributeOkNumber",&hdb_dev->attr_AttributeOkNumber_read);
		}
		catch(Tango::DevFailed &e){}
		usleep(1000);


		if (hdb_dev->shared->is_initialized())
		{
			hdb_dev->attr_AttributeNokNumber_read = hdb_dev->get_sig_on_error_num();
		}
		else
			hdb_dev->attr_AttributeNokNumber_read = 0;

		try
		{
			(hdb_dev->_device)->push_change_event("AttributeNokNumber",&hdb_dev->attr_AttributeNokNumber_read);
			(hdb_dev->_device)->push_archive_event("AttributeNokNumber",&hdb_dev->attr_AttributeNokNumber_read);
		}
		catch(Tango::DevFailed &e){}
		usleep(1000);

		/*if (hdb_dev->shared->is_initialized())
		{
			hdb_dev->get_numbers(
					&hdb_dev->attr_AttributeNumber_read,
					&hdb_dev->attr_AttributeStartedNumber_read,
					&hdb_dev->attr_AttributeStoppedNumber_read
				);
		}
		else
		{
			hdb_dev->attr_AttributeNumber_read = 0;
			hdb_dev->attr_AttributeStartedNumber_read = 0;
			hdb_dev->attr_AttributeStoppedNumber_read = 0;
		}*/


		/*try
		{
			(hdb_dev->_device)->push_change_event("AttributeRecordFreq",&hdb_dev->AttributeRecordFreq);
			(hdb_dev->_device)->push_change_event("AttributeFailureFreq",&hdb_dev->AttributeFailureFreq);
			(hdb_dev->_device)->push_archive_event("AttributeRecordFreq",&hdb_dev->AttributeRecordFreq);
			(hdb_dev->_device)->push_archive_event("AttributeFailureFreq",&hdb_dev->AttributeFailureFreq);
		}catch(Tango::DevFailed &e)
		{
			cout <<"PollerThread::"<< __func__<<": error pushing events="<<e.errors[0].desc<<endl;
		}*/
		/*hdb_dev->attribute_list_str = hdb_dev->get_sig_list();
		hdb_dev->attribute_started_list_str = hdb_dev->get_sig_started_list();
		hdb_dev->attribute_stopped_list_str = hdb_dev->get_sig_not_started_list();*/
		hdb_dev->attribute_list_str.clear();
		hdb_dev->attribute_started_list_str.clear();
		hdb_dev->attribute_stopped_list_str.clear();
		hdb_dev->get_lists(hdb_dev->attribute_list_str, hdb_dev->attribute_started_list_str, hdb_dev->attribute_stopped_list_str);

		bool changed = is_list_changed(hdb_dev->attribute_list_str, hdb_dev->old_attribute_list_str);
		if(changed)
		{
			for (unsigned int i=0 ; i<hdb_dev->attribute_list_str.size() ; i++)
				hdb_dev->attr_AttributeList_read[i] = (char *)hdb_dev->attribute_list_str[i].c_str();
			hdb_dev->attribute_list_str_size = hdb_dev->attribute_list_str.size();
			try
			{
				(hdb_dev->_device)->push_change_event("AttributeList",&hdb_dev->attr_AttributeList_read[0], hdb_dev->attribute_list_str_size);
				(hdb_dev->_device)->push_archive_event("AttributeList",&hdb_dev->attr_AttributeList_read[0], hdb_dev->attribute_list_str_size);
			}
			catch(Tango::DevFailed &e){}
			usleep(1000);
		}
		changed = is_list_changed(hdb_dev->attribute_started_list_str, hdb_dev->old_attribute_started_list_str);
		if(changed)
		{
			for (unsigned int i=0 ; i<hdb_dev->attribute_started_list_str.size() ; i++)
				hdb_dev->attr_AttributeStartedList_read[i] = (char *)hdb_dev->attribute_started_list_str[i].c_str();
			hdb_dev->attribute_started_list_str_size = hdb_dev->attribute_started_list_str.size();
			try
			{
				(hdb_dev->_device)->push_change_event("AttributeStartedList",&hdb_dev->attr_AttributeStartedList_read[0], hdb_dev->attribute_started_list_str_size);
				(hdb_dev->_device)->push_archive_event("AttributeStartedList",&hdb_dev->attr_AttributeStartedList_read[0], hdb_dev->attribute_started_list_str_size);
			}
			catch(Tango::DevFailed &e){}
			usleep(1000);
		}
		changed = is_list_changed(hdb_dev->attribute_stopped_list_str, hdb_dev->old_attribute_stopped_list_str);
		if(changed)
		{
			for (unsigned int i=0 ; i<hdb_dev->attribute_stopped_list_str.size() ; i++)
				hdb_dev->attr_AttributeStoppedList_read[i] = (char *)hdb_dev->attribute_stopped_list_str[i].c_str();
			hdb_dev->attribute_stopped_list_str_size = hdb_dev->attribute_stopped_list_str.size();
			try
			{
				(hdb_dev->_device)->push_change_event("AttributeStoppedList",&hdb_dev->attr_AttributeStoppedList_read[0], hdb_dev->attribute_stopped_list_str_size);
				(hdb_dev->_device)->push_archive_event("AttributeStoppedList",&hdb_dev->attr_AttributeStoppedList_read[0], hdb_dev->attribute_stopped_list_str_size);
			}
			catch(Tango::DevFailed &e){}
			usleep(1000);
		}

		hdb_dev->attribute_ok_list_str.clear();
		hdb_dev->attribute_ok_list_str = hdb_dev->get_sig_not_on_error_list();
		changed = is_list_changed(hdb_dev->attribute_ok_list_str, hdb_dev->old_attribute_ok_list_str);
		if(changed)
		{
			for (unsigned int i=0 ; i<hdb_dev->attribute_ok_list_str.size() ; i++)
				hdb_dev->attr_AttributeOkList_read[i] = (char *)hdb_dev->attribute_ok_list_str[i].c_str();
			hdb_dev->attribute_ok_list_str_size = hdb_dev->attribute_ok_list_str.size();
			try
			{
				(hdb_dev->_device)->push_change_event("AttributeOkList",&hdb_dev->attr_AttributeOkList_read[0], hdb_dev->attribute_ok_list_str_size);
				(hdb_dev->_device)->push_archive_event("AttributeOkList",&hdb_dev->attr_AttributeOkList_read[0], hdb_dev->attribute_ok_list_str_size);
			}
			catch(Tango::DevFailed &e){}
			usleep(1000);
		}

		hdb_dev->attribute_nok_list_str.clear();
		hdb_dev->attribute_nok_list_str = hdb_dev->get_sig_on_error_list();
		changed = is_list_changed(hdb_dev->attribute_nok_list_str, hdb_dev->old_attribute_nok_list_str);
		if(changed)
		{
			for (unsigned int i=0 ; i<hdb_dev->attribute_nok_list_str.size() ; i++)
				hdb_dev->attr_AttributeNokList_read[i] = (char *)hdb_dev->attribute_nok_list_str[i].c_str();
			hdb_dev->attribute_nok_list_str_size = hdb_dev->attribute_nok_list_str.size();
			try
			{
				(hdb_dev->_device)->push_change_event("AttributeNokList",&hdb_dev->attr_AttributeNokList_read[0], hdb_dev->attribute_nok_list_str_size);
				(hdb_dev->_device)->push_archive_event("AttributeNokList",&hdb_dev->attr_AttributeNokList_read[0], hdb_dev->attribute_nok_list_str_size);
			}
			catch(Tango::DevFailed &e){}
			usleep(1000);
		}

		hdb_dev->attribute_pending_list_str.clear();
		hdb_dev->attribute_pending_list_str = hdb_dev->get_sig_list_waiting();
		changed = is_list_changed(hdb_dev->attribute_pending_list_str, hdb_dev->old_attribute_pending_list_str);
		if(changed)
		{
			for (unsigned int i=0 ; i<hdb_dev->attribute_pending_list_str.size() ; i++)
				hdb_dev->attr_AttributePendingList_read[i] = (char *)hdb_dev->attribute_pending_list_str[i].c_str();
			hdb_dev->attribute_pending_list_str_size = hdb_dev->attribute_pending_list_str.size();
			try
			{
				(hdb_dev->_device)->push_change_event("AttributePendingList",&hdb_dev->attr_AttributePendingList_read[0], hdb_dev->attribute_pending_list_str_size);
				(hdb_dev->_device)->push_archive_event("AttributePendingList",&hdb_dev->attr_AttributePendingList_read[0], hdb_dev->attribute_pending_list_str_size);
			}
			catch(Tango::DevFailed &e){}
			usleep(1000);
		}

		hdb_dev->attribute_error_list_str.clear();
		hdb_dev->attribute_error_list_str = hdb_dev->get_error_list();
		changed = is_list_changed(hdb_dev->attribute_error_list_str, hdb_dev->old_attribute_error_list_str);
		if(changed)
		{
			for (unsigned int i=0 ; i<hdb_dev->attribute_error_list_str.size() ; i++)
				hdb_dev->attr_AttributeErrorList_read[i] = (char *)hdb_dev->attribute_error_list_str[i].c_str();
			hdb_dev->attribute_error_list_str_size = hdb_dev->attribute_error_list_str.size();
			try
			{
				(hdb_dev->_device)->push_change_event("AttributeErrorList",&hdb_dev->attr_AttributeErrorList_read[0], hdb_dev->attribute_error_list_str_size);
				(hdb_dev->_device)->push_archive_event("AttributeErrorList",&hdb_dev->attr_AttributeErrorList_read[0], hdb_dev->attribute_error_list_str_size);
			}
			catch(Tango::DevFailed &e){}
			usleep(1000);
		}


		hdb_dev->get_event_number_list();
		try
		{
			(hdb_dev->_device)->push_change_event("AttributeEventNumberList",&hdb_dev->AttributeEventNumberList[0], hdb_dev->attr_AttributeNumber_read);
			(hdb_dev->_device)->push_archive_event("AttributeEventNumberList",&hdb_dev->AttributeEventNumberList[0], hdb_dev->attr_AttributeNumber_read);
		}
		catch(Tango::DevFailed &e){}
		usleep(1000);

	}
	cout <<"PollerThread::"<< __func__<<": exiting..."<<endl;
	return NULL;
}
//=============================================================================
bool PollerThread::is_list_changed(vector<string> newlist, vector<string> &oldlist)
{
	bool ret=false;
	if(newlist.size() != oldlist.size())
	{
		oldlist = newlist;
		return true;
	}
	for(size_t i=0; i < newlist.size(); i++)
	{
		if(newlist[i] != oldlist[i])
		{
			ret = true;
			oldlist = newlist;
			break;
		}

	}
	return ret;
}
//=============================================================================
void PollerThread::abort_sleep(double time)
{
	for (int i = 0; i < (time/0.1); i++) {
		if (abortflag)
			break;
		omni_thread::sleep(0,100000000);
	}
}



}	//	namespace
