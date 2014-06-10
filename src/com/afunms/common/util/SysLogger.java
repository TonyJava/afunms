package com.afunms.common.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.afunms.initialize.ResourceCenter;

public class SysLogger {
	private static Logger logger = Logger.getLogger(SysLogger.class.getName());

	public static void error(String errorMessage) {
		if (ResourceCenter.getInstance().isLogError()) {
			init();
			logger.error(errorMessage);
		}
	}

	public static void error(String errorMessage, Exception ex) {
		if (ResourceCenter.getInstance().isLogError()) {
			init();
			if (ex.getMessage() == null) {
				logger.error(errorMessage);
			} else {
				logger.error(errorMessage + ex.getMessage());
			}
			ex.printStackTrace();
		}
	}

	public static void info(String infoMessage) {
		if (ResourceCenter.getInstance().isLogInfo()) {
			init();
			logger.info(infoMessage);
		}
	}

	private static void init() {
		PropertyConfigurator.configure(ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/log4j.properties");
	}

	private SysLogger() {
		if (ResourceCenter.getInstance().isLogInfo()) {
			init();
		}
	}
}
