/**
 * <p>Description:task of collecting fdb table</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-10-09
 */

package com.afunms.polling.task;

import com.afunms.polling.base.*;
import com.afunms.common.util.SysUtil;
import com.afunms.ipresource.util.CollectIPDetail;

public class CollectIP extends BaseTask
{
    public CollectIP()
    {
    }

    public void executeTask()
    {	    
    	CollectIPDetail cfd = new CollectIPDetail();
    	cfd.findDirectDevices();       	
    }
    
	public boolean timeRestricted()
	{
	    int hour = SysUtil.getCurrentHour();
	    //一天最多执行一次
	    if(hour > 11 && hour < 18) 
	       return true; 
	    else
	       return false;
	}
}
