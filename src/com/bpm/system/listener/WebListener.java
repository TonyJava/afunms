package com.bpm.system.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.bpm.process.dao.ExportExcelDao;

public class WebListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		ExportExcelDao.init();
	}

}
