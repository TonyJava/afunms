package com.afunms.serial.util;

public class SerialUtil {

	/**
	 * @param args
	 */

	public String getResult(Parameters parameters, String command) {
		String result = "";
		parameters.setBaudRate(9600);
		parameters.setDatabits(7);
		parameters.setStopbits(2);
		parameters.setParity(0);
		parameters.setSerialPortId("COM1");
		command = ":01040080000576" + '\r' + '\n';
		SerialBean serialBean = new SerialBean(parameters);
		serialBean.initialize();
		for (int i = 0; i < 5; i++) {
			serialBean.writeMsg(command);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String tempResult = serialBean.readMsg();
			if (tempResult != null && result.length() <= tempResult.length()) {
				result = tempResult;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		serialBean.closePort();
		return result;
	}


}
