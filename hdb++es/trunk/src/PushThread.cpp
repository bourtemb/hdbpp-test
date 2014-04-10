static const char *RcsId = "$Header: /home/cvsadm/cvsroot/fermi/servers/hdb++/hdb++es/src/PushThread.cpp,v 1.7 2014-03-06 15:21:43 graziano Exp $";
//+=============================================================================
//
// file :         HdbEventHandler.cpp
//
// description :  C++ source for the HdbDevice
// project :      TANGO Device Server
//
// $Author: graziano $
//
// $Revision: 1.7 $
//
// $Log: PushThread.cpp,v $
// Revision 1.7  2014-03-06 15:21:43  graziano
// StartArchivingAtStartup,
// start_all and stop_all,
// archiving of first event received at subscribe
//
// Revision 1.6  2014-02-20 14:59:47  graziano
// name and path fixing
// bug fixed in remove
//
// Revision 1.5  2013-09-24 08:42:21  graziano
// bug fixing
//
// Revision 1.4  2013-09-02 12:15:34  graziano
// libhdb refurbishing, cleanings
//
// Revision 1.3  2013-08-23 10:04:53  graziano
// development
//
// Revision 1.2  2013-08-14 13:10:07  graziano
// development
//
// Revision 1.1  2013-07-17 13:37:43  graziano
// *** empty log message ***
//
//
//-=============================================================================


#include <PushThread.h>


namespace HdbEventSubscriber_ns
{
//=============================================================================
//=============================================================================
PushThreadShared::PushThreadShared(string host, string user, string password, string dbname, int port)
{
	max_waiting=0; stop_it=false;

	try
	{
		mdb = new HdbClient(host, user, password, dbname, port);
	}
	catch (string &err)
	{
		cout << __func__ << ": error connecting DB: " << err << endl;
		exit(-1);
	}
	sig_lock = new omni_mutex();
}
//=============================================================================
//=============================================================================
PushThreadShared::~PushThreadShared()
{
	delete mdb;
	delete sig_lock;
}
//=============================================================================
//=============================================================================
void PushThreadShared::push_back_cmd(HdbCmdData *argin)
{

	omni_mutex_lock sync(*this);
	//	Add data at end of vector

	events.push_back(argin);

	//	Check if nb waiting more the stored one.
	if (events.size()>(unsigned )max_waiting)
		max_waiting = events.size();

	//	And awake thread
	signal();
}
//=============================================================================
//=============================================================================
void PushThreadShared::remove_cmd()
{
	omni_mutex_lock sync(*this);
	//	Remove first element of vector
	events.erase(events.begin());
}
//=============================================================================
//=============================================================================
int PushThreadShared::nb_cmd_waiting()
{
	omni_mutex_lock sync(*this);
	return events.size();
}
//=============================================================================
//=============================================================================
int PushThreadShared::get_max_waiting()
{
	omni_mutex_lock sync(*this);
	int	tmp_max_waiting = max_waiting;
	max_waiting = events.size();
	return tmp_max_waiting;
}
//=============================================================================
//=============================================================================
vector<string> PushThreadShared::get_sig_list_waiting()
{
	omni_mutex_lock sync(*this);
	vector<string>	list;
	for (unsigned int i=0 ; i<events.size() ; i++)
	{
		HdbCmdData *ev = events[i];
		string	signame(ev->ev_data->attr_name);
		list.push_back(signame);
	}
	return list;
}
//=============================================================================
//=============================================================================
void PushThreadShared::reset_statistics()
{
	sig_lock->lock();
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		signals[i].nokdb_counter = 0;
		signals[i].okdb_counter = 0;
		signals[i].store_time_avg = 0;
		signals[i].process_time_avg = 0;
	}
	max_waiting = 0;
	sig_lock->unlock();
}
//=============================================================================
//=============================================================================
HdbCmdData *PushThreadShared::get_next_cmd()
{
	omni_mutex_lock sync(*this);
	if (events.size()>0)
	{
		HdbCmdData *cmd = events[0];
		events.erase(events.begin());
		return cmd;
	}
	else
	{
		return NULL;
	}
}
//=============================================================================
//=============================================================================
void PushThreadShared::stop_thread()
{
	omni_mutex_lock sync(*this);
	stop_it = true;
	signal();
}
//=============================================================================
//=============================================================================
bool PushThreadShared::get_if_stop()
{
	omni_mutex_lock sync(*this);
	return stop_it;
}
//=============================================================================
/**
 *
 */
//=============================================================================
void  PushThreadShared::remove(string &signame)
{
	sig_lock->lock();
	unsigned int i;
	vector<HdbStat>::iterator pos = signals.begin();
	for (i=0 ; i<signals.size() ; i++, pos++)
	{
		if (signals[i].name==signame)
		{
			signals.erase(pos);
			sig_lock->unlock();
			return;
		}
	}
	pos = signals.begin();
	for (i=0 ; i<signals.size() ; i++, pos++)
	{
		if (compare_without_domain(signals[i].name,signame))
		{
			signals.erase(pos);
			sig_lock->unlock();
			return;
		}
	}
	sig_lock->unlock();
}

//=============================================================================
/**
 *	Return the list of signals on error
 */
//=============================================================================
vector<string>  PushThreadShared::get_sig_on_error_list()
{
	sig_lock->lock();
	vector<string>	list;
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].dbstate==Tango::ALARM)
		{
			string	signame(signals[i].name);
			list.push_back(signame);
		}
	}

	sig_lock->unlock();
	return list;
}
//=============================================================================
/**
 *	Return the number of signals on error
 */
//=============================================================================
int  PushThreadShared::get_sig_on_error_num()
{
	sig_lock->lock();
	int num=0;
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].dbstate==Tango::ALARM)
		{
			num++;
		}
	}
	sig_lock->unlock();
	return num;
}
//=============================================================================
/**
 *	Return the list of signals not on error
 */
//=============================================================================
vector<string>  PushThreadShared::get_sig_not_on_error_list()
{
	sig_lock->lock();
	vector<string>	list;
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].dbstate==Tango::ON)
		{
			string	signame(signals[i].name);
			list.push_back(signame);
		}
	}
	sig_lock->unlock();
	return list;
}
//=============================================================================
/**
 *	Return the number of signals not on error
 */
//=============================================================================
int  PushThreadShared::get_sig_not_on_error_num()
{
	sig_lock->lock();
	int num=0;
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].dbstate==Tango::ON)
		{
			num++;
		}
	}
	sig_lock->unlock();
	return num;
}
//=============================================================================
/**
 *	Return the db state of the signal
 */
//=============================================================================
Tango::DevState  PushThreadShared::get_sig_state(string signame)
{
	sig_lock->lock();
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].name==signame)
		{
			sig_lock->unlock();
			return signals[i].dbstate;
		}
	}
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (compare_without_domain(signals[i].name,signame))
		{
			sig_lock->unlock();
			return signals[i].dbstate;
		}
	}

	sig_lock->unlock();
	return Tango::ON;
	//	if not found
	/*Tango::Except::throw_exception(
				(const char *)"BadSignalName",
				"Signal NOT subscribed",
				(const char *)"SharedData::get_nok_db()");*/
}

//=============================================================================
/**
 *	Increment the error counter of db saving
 */
//=============================================================================
void  PushThreadShared::set_nok_db(string &signame)
{
	sig_lock->lock();
	unsigned int i;
	for (i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].name==signame)
		{
			signals[i].nokdb_counter++;
			signals[i].dbstate = Tango::ALARM;
			sig_lock->unlock();
			return;
		}
	}
	for (i=0 ; i<signals.size() ; i++)
	{
		if (compare_without_domain(signals[i].name,signame))
		{
			signals[i].nokdb_counter++;
			signals[i].dbstate = Tango::ALARM;
			sig_lock->unlock();
			return;
		}
	}
	if(i == signals.size())
	{
		HdbStat sig;
		sig.name = signame;
		sig.nokdb_counter = 1;
		sig.okdb_counter = 0;
		sig.store_time_avg = 0.0;
		sig.process_time_avg = 0.0;
		sig.dbstate = Tango::ALARM;
		signals.push_back(sig);
	}
	sig_lock->unlock();
}
//=============================================================================
/**
 *	Get the error counter of db saving
 */
//=============================================================================
uint32_t  PushThreadShared::get_nok_db(string &signame)
{
	sig_lock->lock();
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].name==signame)
		{
			sig_lock->unlock();
			return signals[i].nokdb_counter;
		}
	}
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (compare_without_domain(signals[i].name,signame))
		{
			sig_lock->unlock();
			return signals[i].nokdb_counter;
		}
	}
	sig_lock->unlock();
	return 0;
	//	if not found
	/*Tango::Except::throw_exception(
				(const char *)"BadSignalName",
				"Signal NOT subscribed",
				(const char *)"SharedData::get_nok_db()");*/
}
//=============================================================================
/**
 *	Get avg store time
 */
//=============================================================================
double  PushThreadShared::get_avg_store_time(string &signame)
{
	//omni_mutex_lock sync(*this);
	sig_lock->lock();
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].name==signame)
		{
			sig_lock->unlock();
			return signals[i].store_time_avg;
		}
	}
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (compare_without_domain(signals[i].name,signame))
		{
			sig_lock->unlock();
			return signals[i].store_time_avg;
		}
	}
	sig_lock->unlock();

	return -1;
}
//=============================================================================
/**
 *	Get avg process time
 */
//=============================================================================
double  PushThreadShared::get_avg_process_time(string &signame)
{
	//omni_mutex_lock sync(*this);
	sig_lock->lock();
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].name==signame)
		{
			sig_lock->unlock();
			return signals[i].process_time_avg;
		}
	}
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (compare_without_domain(signals[i].name,signame))
		{
			sig_lock->unlock();
			return signals[i].process_time_avg;
		}
	}
	sig_lock->unlock();

	return -1;
}
//=============================================================================
/**
 *	reset state
 */
//=============================================================================
void  PushThreadShared::set_ok_db(string &signame, double store_time, double process_time)
{
	sig_lock->lock();
	unsigned int i;
	for (i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].name==signame)
		{
			signals[i].dbstate = Tango::ON;
			signals[i].store_time_avg = ((signals[i].store_time_avg * signals[i].okdb_counter) + store_time)/(signals[i].okdb_counter+1);
			signals[i].process_time_avg = ((signals[i].process_time_avg * signals[i].okdb_counter) + process_time)/(signals[i].okdb_counter+1);
			signals[i].okdb_counter++;
			sig_lock->unlock();
			return;
		}
	}
	for (i=0 ; i<signals.size() ; i++)
	{
		if (compare_without_domain(signals[i].name,signame))
		{
			signals[i].dbstate = Tango::ON;
			signals[i].store_time_avg = ((signals[i].store_time_avg * signals[i].okdb_counter) + store_time)/(signals[i].okdb_counter+1);
			signals[i].process_time_avg = ((signals[i].process_time_avg * signals[i].okdb_counter) + process_time)/(signals[i].okdb_counter+1);
			signals[i].okdb_counter++;
			sig_lock->unlock();
			return;
		}
	}
	if(i == signals.size())
	{
		HdbStat sig;
		sig.name = signame;
		sig.nokdb_counter = 0;
		sig.okdb_counter = 1;
		sig.store_time_avg = store_time;
		sig.process_time_avg = process_time;
		sig.dbstate = Tango::ALARM;
		signals.push_back(sig);
	}
	sig_lock->unlock();
}

void  PushThreadShared::start_attr(string &signame)
{
	//------Configure DB------------------------------------------------
	int res = mdb->start_Attr(signame);
	if(res < 0)
	{
		//... TODO

	}
}

void  PushThreadShared::stop_attr(string &signame)
{
	//------Configure DB------------------------------------------------
	int res = mdb->stop_Attr(signame);
	if(res < 0)
	{
		//... TODO

	}
}

//=============================================================================
/**
 *	Return ALARM if at list one signal is not subscribed.
 */
//=============================================================================
Tango::DevState PushThreadShared::state()
{
	sig_lock->lock();
	Tango::DevState	state = Tango::ON;
	for (unsigned int i=0 ; i<signals.size() ; i++)
	{
		if (signals[i].dbstate==Tango::ALARM)
		{
			state = Tango::ALARM;
			break;
		}
	}
	sig_lock->unlock();
	return state;
}

string PushThreadShared::remove_domain(string str)
{
	string::size_type	end1 = str.find(".");
	if (end1 == string::npos)
	{
		return str;
	}
	else
	{
		string::size_type	start = str.find("tango://");
		if (start == string::npos)
		{
			start = 0;
		}
		else
		{
			start = 8;	//tango:// len
		}
		string::size_type	end2 = str.find(":", start);
		if(end1 > end2)	//'.' not in the tango host part
			return str;
		string th = str.substr(0, end1);
		th += str.substr(end2, str.size()-end2);
		return th;
	}
}
//=============================================================================
//=============================================================================
bool PushThreadShared::compare_without_domain(string str1, string str2)
{
	string str1_nd = remove_domain(str1);
	string str2_nd = remove_domain(str2);
	return (str1_nd==str2_nd);
}
//=============================================================================
//=============================================================================





//=============================================================================
/**
 * Execute the thread infinite loop.
 */
//=============================================================================
void *PushThread::run_undetached(void *ptr)
{

	while(shared->get_if_stop()==false)
	{
		//	Check if command ready
		HdbCmdData	*cmd;
		while ((cmd=shared->get_next_cmd())!=NULL)
		{
			try
			{
				timeval now;
				gettimeofday(&now, NULL);
				double	dstart = now.tv_sec + (double)now.tv_usec/1.0e6;
				//	Send it to DB
				int ret = shared->mdb->insert_Attr(cmd->ev_data, cmd->ev_data_type);
				if(ret < 0)
				{
					shared->set_nok_db(cmd->ev_data->attr_name);
				}
				else
				{
					gettimeofday(&now, NULL);
					double	dnow = now.tv_sec + (double)now.tv_usec/1.0e6;
					double	rcv_time = cmd->ev_data->get_date().tv_sec + (double)cmd->ev_data->get_date().tv_usec/1.0e6;
					shared->set_ok_db(cmd->ev_data->attr_name, dnow-dstart, dnow-rcv_time);
				}
			}
			catch(Tango::DevFailed  &e)
			{
				Tango::Except::print_exception(e);
			}
			delete cmd;

		}
		
		//	Wait until next command.
		if(shared->get_if_stop()==false)
		{
			omni_mutex_lock sync(*shared);
			shared->wait();
		}
	}
	cout <<"PushThread::"<< __func__<<": exiting..."<<endl;
	return NULL;
}



//=============================================================================
//=============================================================================
}	//	namespace
