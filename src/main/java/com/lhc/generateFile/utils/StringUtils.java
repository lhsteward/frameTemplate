package com.lhc.generateFile.utils;

import java.util.Arrays;

/** 
*@Title StringUtils.java 
*@description:  工具类
*@author lhc
**/
public class StringUtils {

	/**
	 * @Title: upperCase 
	 * @Description: String字符串首字母大写
	 * @param @param str
	 * @return String
	 * @author lhc 
	 */
	public static String upperCase(String str) {
	    char[] ch = str.toCharArray();
	    if (ch[0] >= 'a' && ch[0] <= 'z') {
	        ch[0] = (char) (ch[0] - 32);
	    }
	    return new String(ch);
	}

	public static boolean isNotNull(String str){
		if(null == str || "null".equals(str) || "".equals(str.trim())){
			return false;
		}
		return true;
	}

	/**
	 * 是否清除前缀
	 * @param str 需要清除的字符串
	 * @param prefix 前缀
	 * @return
	 */
	public static String clearPrefix(String str, String prefix){
		if(isNotNull(str)){
			str = str.replace(prefix, "");
		}
		return str;
	}
	
	/**
	 * @Title: toLowerCaseFirstOne 
	 * @Description: 首字母小写
	 * @param @param s
	 * @return String
	 * @author lhc 
	 */
	public static String toLowerCaseFirstOne(String s){
	    if(Character.isLowerCase(s.charAt(0)))
	      return s;
	    else
	      return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}
	
	
	/**
	 * @Title: useList 
	 * @Description: 判断数组中是否包含某个值
	 * @param @param arr
	 * @param @param value
	 * @return boolean
	 * @author lhc 
	 */
	public static boolean containsArr(Integer[] arr,Integer value){
		return Arrays.asList(arr).contains(value);
	}
	
	
}
