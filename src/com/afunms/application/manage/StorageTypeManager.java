/**
 * <p>Description:Tomcat Manager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-12-07
 */

package com.afunms.application.manage;

import java.util.List;

import com.afunms.common.base.*;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.loader.*;
import com.afunms.application.model.StorageTypeVo;
import com.afunms.topology.util.*;
import com.afunms.application.dao.StorageTypeDao;
import com.afunms.application.util.DBPool;
import com.afunms.topology.dao.DiscoverCompleteDao;

public class StorageTypeManager extends BaseManager implements ManagerInterface
{
	private String list()
	{
		StorageTypeDao dao = new StorageTypeDao();
		List list = null;
		try{
			list = dao.loadAll();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
		request.setAttribute("list",list);				
		return "/application/storagetype/list.jsp";
	}

	private String add()
    {    	   
		StorageTypeVo vo = new StorageTypeVo();
    	//vo.setId(KeyGenerator.getInstance().getNextKey());
    	vo.setProducer(getParaIntValue("producer"));
    	vo.setModel(getParaValue("model"));
    	vo.setDescr(getParaValue("descr"));        
        
    	StorageTypeDao dao = new StorageTypeDao();
        try{
        	dao.save(vo);
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	dao.close();
        }
        return "/storagetype.do?action=list";
    } 
	
	private String readyedit()
    {    	   
		   String targetJsp = "/application/storagetype/edit.jsp"; 
		   StorageTypeVo vo = null;
	       StorageTypeDao dao = new StorageTypeDao();
	       try{
	    	   String ii=getParaValue("id");
	    	   vo = (StorageTypeVo)dao.findByID(getParaValue("id"));       
	       }catch(Exception e){
	    	   e.printStackTrace();
	       }finally{
	    	   
	       }
	       if(vo!=null)
	       {	   
	          request.setAttribute("vo",vo);
	       }
	       return targetJsp;
    }
	
	public String delete()
	{
		String id = getParaValue("radio"); 
		StorageTypeDao dao = new StorageTypeDao();
		try{
			dao.delete(id);	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dao.close();
		}
        
        return "/storagetype.do?action=list";
	}
	
	private String update()
    {    	   
		StorageTypeVo vo = new StorageTypeVo();
    	vo.setId(getParaIntValue("id"));
    	vo.setProducer(getParaIntValue("producer"));
    	vo.setModel(getParaValue("model"));
    	vo.setDescr(getParaValue("descr"));        
    	StorageTypeDao dao = new StorageTypeDao();
    	try{
    		dao.update(vo);	  
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		dao.close();
    	}
        return "/storagetype.do?action=list";
    }    
	
	public String execute(String action) 
	{	
        if(action.equals("list"))
            return list();    
        if(action.equals("ready_add"))
        	return "/application/storagetype/add.jsp";
        if(action.equals("add"))
        	return add();
        if(action.equals("delete"))
            return delete();
        if(action.equals("ready_edit"))
        	return readyedit();
        if(action.equals("update"))
            return update();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}	
}