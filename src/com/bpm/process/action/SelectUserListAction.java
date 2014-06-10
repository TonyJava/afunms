package com.bpm.process.action;

/**
 *  Description:
 * 已结束的流程列表
 * @author ywx
 *
 */

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.process.model.UserShipModel;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class SelectUserListAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7668599704209454250L;
	private List<UserShipModel> listUser;
	private List<UserShipModel> listGroup;

	@Override
	public String execute() throws Exception {
		// listUser=processService.queryAllUser();
		// listGroup=processService.queryAllGroup();
		return SUCCESS;
	}

	public List<UserShipModel> getListGroup() {
		return listGroup;
	}

	public List<UserShipModel> getListUser() {
		return listUser;
	}

	public void setListGroup(List<UserShipModel> listGroup) {
		this.listGroup = listGroup;
	}

	public void setListUser(List<UserShipModel> listUser) {
		this.listUser = listUser;
	}

	/*
	 * private String load() { listUser=processService.queryAllUser();
	 * StringBuffer sb = new StringBuffer(); sb.append("<script
	 * type=\"text/javascript\">"); sb.append("d = new dTree('d');");
	 * sb.append("d.config.selType='radio';"); if(listUser==null &&
	 * listUser.size()==0) return ""; UserShipModel model; int k=1; for(int
	 * i=0;i<listUser.size();i++) { model=listUser.get(i);
	 * sb.append("d.add("+k+","+i+",'"+model.getUserid()+"','"+model.getUserid()+"','"+k+"|"+i+"','userwar','','','false');");
	 *  } sb.append("document.write(d);"); sb.append("</script>"); return
	 * sb.toString(); }
	 */

}
