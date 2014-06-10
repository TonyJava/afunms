package com.bpm.system.action;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;
import com.bpm.system.utils.StringUtil;

 
@Controller
@Scope("prototype")
public class UserGroupEntityQueryAction extends BaseAction {

	private String group;
	private String grouphtml;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		List<GroupEntity> list = systemService.queryAllGroup();
		grouphtml = "<table><tr><td>";
		GroupEntity o = new GroupEntity();
		if (StringUtil.isBlank(group)) {
			for (int i = 0; i < list.size(); i++) {
				o = list.get(i);
				grouphtml = grouphtml + "<input type='checkbox' value='" + o.getId() + "' title='" + o.getName() + "' name='groups' />" + o.getName() + "&nbsp;&nbsp;&nbsp;";
				if (i != 0 && i % 5 == 0) {
					grouphtml = grouphtml + "<br/>";
				}
			}
		} else {
			String[] groups = group.substring(0, group.length() - 1).split(",");
			for (int i = 0; i < list.size(); i++) {
				o = list.get(i);
				boolean flag = false;
				for (String temp : groups) {
					if (temp.equals(o.getId())) {
						flag = true;
						break;
					}
				}
				if (flag) {
					grouphtml = grouphtml + "<input type='checkbox' value='" + o.getId() + "' title='" + o.getName() + "' name='groups' checked />" + o.getName()
							+ "&nbsp;&nbsp;&nbsp;";
				} else {
					grouphtml = grouphtml + "<input type='checkbox' value='" + o.getId() + "' title='" + o.getName() + "' name='groups' />" + o.getName() + "&nbsp;&nbsp;&nbsp;";
				}

				if (i != 0 && i % 5 == 0) {
					grouphtml = grouphtml + "<br/>";
				}

			}
		}
		grouphtml = grouphtml + "</td></tr></table>";

		return SUCCESS;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGrouphtml() {
		return grouphtml;
	}

	public void setGrouphtml(String grouphtml) {
		this.grouphtml = grouphtml;
	}

}
