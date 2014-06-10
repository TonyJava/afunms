package com.bpm.system.action;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("prototype")
public class MenuTreeAction extends BaseAction {

	private String result;

	@Override
	public String execute() throws Exception {

		result = "[{ " + "  \"id\":1,   " + "  \"text\":\"Folder1\", " + "    \"iconCls\":\"icon-save\",   " + "    \"children\":[{   " + "        \"text\":\"File1\",   "
				+ "       \"checked\":true  " + "   },{   " + "     \"text\":\"Books\",   " + "     \"state\":\"open\",   " + "    \"attributes\":{   "
				+ "        \"url\":\"/demo/book/abc\", " + "         \"price\":100   " + "     },   " + "   \"children\":[{   " + "        \"text\":\"PhotoShop\",   "
				+ "         \"checked\":true  " + "    },{   " + "          \"id\": 8,   " + "         \"text\":\"Sub Bookds\",   " + "           \"state\":\"closed\"  "
				+ "       }]   " + "     }]   " + " },{   " + "    \"text\":\"Languages\",   " + "     \"state\":\"closed\",  " + "     \"children\":[{   "
				+ "        \"text\":\"Java\"  " + "     },{   " + "        \"text\":\"C#\"" + "     }]}]";
		ServletActionContext.getResponse().setContentType("application/json");
		ServletActionContext.getResponse().getWriter().write(result);
		return null;
	}

}
