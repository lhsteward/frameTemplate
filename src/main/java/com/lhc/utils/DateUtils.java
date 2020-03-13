package com.lhc.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/** 
*@Title DateUtils.java 
*@description:  时间工具类
*@author lihaichao
*@time 创建时间：2018年6月9日 上午10:54:02 
**/
public class DateUtils {

	/**
	 * @Title: getCurrentDate 
	 * @Description: 获取当前时间  
	 * @param @param pattern 格式 不需要可传空串(空串的String格式 默认为 yyyy-MM-dd HH:mm:ss)
	 * @param @param V   class对象
	 * @return V class对象类型
	 * @author lihaichao 
	 * @date createTime：2018年6月9日下午2:07:40
	 */
	@SuppressWarnings("unchecked")
	public static<V> V getCurrentDate(String pattern,Class<V> V){
		SimpleDateFormat sdf = new SimpleDateFormat((pattern == "" || pattern == null) ? "yyyy-MM-dd HH:mm:ss" :pattern);
		V v = null;
		switch (V.getName()) {
		case "java.lang.String":
			v = (V) sdf.format(new Date());
			break;
		case "java.util.Date":
			v = (V) new Date();
			break;
		case "java.sql.Date":
			v = (V) new java.sql.Date(0);
			break;
		case "java.sql.Timestamp":
			v = (V) new Timestamp(System.currentTimeMillis());
			break;
		}
		return v;
	}

	public static<V> V format(String pattern, Class<V> V, String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat((pattern == "" || pattern == null) ? "yyyy-MM-dd HH:mm:ss" :pattern);
		V v = null;
		switch (V.getName()) {
			case "java.lang.String":
				v = (V) sdf.format(sdf.parse(date));
				break;
			case "java.util.Date|| java.sql.Date":
				v = (V) sdf.parse(date);
				break;
			case "java.sql.Timestamp":
				v = (V) new Timestamp(sdf.parse(date).getTime());
				break;
		}
		return v;
	}


	//为传入的时间增加天数  day 要增加的天数  time String yyyy-MM-dd HH:mm:ss 格式的时间
	public static String addDayForDate(Integer day,String time){
		Date d = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dd = null;
		try {
			dd = format.parse(time);
			Calendar ca = Calendar.getInstance();
			ca.setTime(dd);
			ca.add(Calendar.DATE, day);// day为增加的天数，可以改变的
			d = ca.getTime();
			return format.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}


	//为传入的时间增加小时 hours 要增加的小时  time String yyyy-MM-dd HH:mm:ss 格式的时间
	public static String addHoursForDate(Integer hours,String time){
		Date d = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dd = null;
		try {
			dd = format.parse(time);
			Calendar ca = Calendar.getInstance();
			ca.setTime(dd);
			ca.add(Calendar.HOUR, hours);// hours为增加的小时，可以改变的
			d = ca.getTime();
			return format.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String addMinuteForDate(Integer minute,String time){
		Date d = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dd = null;
		try {
			dd = format.parse(time);
			Calendar ca = Calendar.getInstance();
			ca.setTime(dd);
			ca.add(Calendar.MINUTE, minute);// minute 增加分钟数，可以改变的
			d = ca.getTime();
			return format.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	/**
	 * @Title: getTimestamp 
	 * @Description: 获取当前时间戳
	 * @return Timestamp
	 * @author lihaichao 
	 * @date createTime：2018年7月7日上午10:20:43
	 */
	public static Timestamp getTimestamp(){
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * @Title: compareTime
	 * @Description:比较当前时间与传入的时间+hour的大小 传入的值大于等于当前时间时返回1 小于当前时间时返回-1
	 * @return int
	 * @author chenyan 
	 * @date 2019/4/9 14:09
	 **/
	 public static int compareTo(String startTime,int hour){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//日期转字符串
			Calendar calendarNew = Calendar.getInstance();
			Long newTime=calendarNew.getTimeInMillis();
			//字符串转日期
			Calendar calendar = Calendar.getInstance();
			Date dateParse = sdf.parse(startTime);
			calendar.setTime(dateParse);
			calendar.add(Calendar.HOUR_OF_DAY, hour);
			Long time=calendar.getTimeInMillis();
			return time>=newTime?1:-1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
