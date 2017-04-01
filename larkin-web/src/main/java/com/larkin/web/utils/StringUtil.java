package com.larkin.web.utils;

import org.apache.commons.lang.StringUtils;

public class StringUtil {

	public static String toCamelName(String field){
		return toCamelName(field, "_");
	}
	
	public static String toCamelName(String field, String divider){
		if (StringUtils.isBlank(field)) {
			return null;
		}
		String[] words = StringUtils.split(field.toLowerCase(), divider);
		StringBuilder str = new StringBuilder(words[0]);
		for (int i = 1; i < words.length; i++) {
			str.append(StringUtils.capitalize(words[i]));
		}
		return str.toString();
	}
	
	
}
