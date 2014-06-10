package com.bpm.system.utils;

import java.util.UUID;

public class UUIDKey {

	public static String getKey() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
