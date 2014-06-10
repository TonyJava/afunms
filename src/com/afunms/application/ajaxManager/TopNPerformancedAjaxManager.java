package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;

public class TopNPerformancedAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	@Override
	public void execute(String action) {
		if (action.equals("getDateAndBid")) {
			getDateAndBid();
		}
	}

	private void getDateAndBid() {
		String dateFlag = getParaValue("dateFlag");
		User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = "";
		if (null != vo) {
			if (vo.getRole() == 0 || vo.getRole() == 1) {
				bids = "-1";
			} else {
				bids = vo.getBusinessids();
			}
		}
		String startTime = "";
		String toTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (dateFlag.equals("now")) {
			startTime = sdf.format(new Date());
		} else if (dateFlag.equals("day")) {
			startTime = sdf.format(new Date());
		} else if (dateFlag.equals("week")) {
			startTime = sdf.format(getDateBefore(new Date(), 6));
		} else if (dateFlag.equals("month")) {
			startTime = sdf.format(getDateBefore(new Date(), 30));
		}
		toTime = sdf.format(new Date());

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"bid\":\"");
		jsonString.append(bids);
		jsonString.append("\",");

		jsonString.append("\"startTime\":\"");
		jsonString.append(startTime);
		jsonString.append("\",");

		jsonString.append("\"toTime\":\"");
		jsonString.append(toTime);
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	public Date getDateBefore(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		return now.getTime();
	}
}
