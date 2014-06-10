package com.afunms.polling.telnet;

public class ScriptHandler {

	private int matchPos; // current position in the match
	private byte[] match; // the current bytes to look for
	private boolean done = true; // nothing to look for!

	public boolean match(byte[] s, int length) {
		if (done) {
			return true;
		}
		for (int i = 0; !done && i < length; i++) {
			if (s[i] == match[matchPos]) {

				if (++matchPos >= match.length) {
					done = true;
					return true;
				}
			} else {
				matchPos = 0; // get back to the beginning
			}
		}
		return false;
	}

	public void setup(String match) {
		if (match == null) {
			return;
		}
		this.match = match.getBytes();
		matchPos = 0;
		done = false;
	}
}
