package com.afunms.emc.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.emc.model.Agent;

public class AgentParser {


	public static Agent parse(String str) {
		String regex = "(Agent Rev:\\s*.*\\r\\n)" + "(Name:\\s*.*\\r\\n)" + "(Desc:\\s*.*\\r\\n)" + "(Node:\\s*.*\\r\\n)" + "(Physical Node:\\s*.*\\r\\n)"
				+ "(Signature:\\s*.*\\r\\n)" + "(Peer Signature:\\s*.*\\r\\n)" + "(Revision:\\s*.*\\r\\n)" + "(SCSI Id:\\s*.*\\r\\n)" + "(Model:\\s*.*\\r\\n)"
				+ "(Model Type:\\s*.*\\r\\n)" + "(Prom Rev:\\s*.*\\r\\n)" + "(SP Memory:\\s*.*\\r\\n)" + "(Serial No:\\s*.*\\r\\n)" + "(SP Identifier:\\s*.*\\r\\n)"
				+ "(Cabinet:\\s*.*\\r\\n)";
		Agent agent = new Agent();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		if (m.find()) {
			agent.setAgentRev(m.group(1).substring(m.group(1).indexOf(":") + 1).trim());
			agent.setName(m.group(2).substring(m.group(2).indexOf(":") + 1).trim());
			agent.setDescr(m.group(3).substring(m.group(3).indexOf(":") + 1).trim());
			agent.setNode(m.group(4).substring(m.group(4).indexOf(":") + 1).trim());
			agent.setPhysicalNode(m.group(5).substring(m.group(5).indexOf(":") + 1).trim());
			agent.setSignature(m.group(6).substring(m.group(6).indexOf(":") + 1).trim());
			agent.setPeerSignature(m.group(7).substring(m.group(7).indexOf(":") + 1).trim());
			agent.setRevision(m.group(8).substring(m.group(8).indexOf(":") + 1).trim());
			agent.setSCSIId(m.group(9).substring(m.group(9).indexOf(":") + 1).trim());
			agent.setModel(m.group(10).substring(m.group(10).indexOf(":") + 1).trim());
			agent.setModelType(m.group(11).substring(m.group(11).indexOf(":") + 1).trim());
			agent.setPromRev(m.group(12).substring(m.group(12).indexOf(":") + 1).trim());
			agent.setSPMemory(m.group(13).substring(m.group(13).indexOf(":") + 1).trim());
			agent.setSerialNo(m.group(14).substring(m.group(14).indexOf(":") + 1).trim());
			agent.setSPIdentifier(m.group(15).substring(m.group(15).indexOf(":") + 1).trim());
			agent.setCabinet(m.group(16).substring(m.group(16).indexOf(":") + 1).trim());
		}
		return agent;
	}

}
