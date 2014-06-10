/**
 * <p>Description:a artist who draw various chart</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-18
 */

package com.afunms.report.jfree;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;

import com.afunms.initialize.ResourceCenter;

public class Artist extends HttpServlet {
	private static final long serialVersionUID = 541128324260855824L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/jpeg");
		String seriesKey = request.getParameter("series_key");
		JFreeChartBrother jfb = (JFreeChartBrother) ResourceCenter.getInstance().getChartStorage().get(seriesKey);
		ChartUtilities.writeChartAsPNG(response.getOutputStream(), jfb.getChart(), jfb.getWidth(), jfb.getHeight(), null);
	}
}