package com.badou.project.common.webparams.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @CLassName DateUtil
 * @Decription 日期工具类
 * @Author lm
 * @Version 1.0
 * @Date 2021/06/28
 */
public class DateUtil {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat sdfMin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static String getDateStr(Date date){
        return sdf.format(date);
    }

    public static String getDateStrMin(Date date){
        return sdfMin.format(date);
    }

    //获取当前的时间 昨天的日期
    public static String getLastDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int day=calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE,day-1);
        String lastDay = sdf.format(calendar.getTime());
        return lastDay;
    }

    public static Date coverUsTime(String sourceTime){
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = sdf.parse(sourceTime);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String coverOffsetTime(OffsetDateTime offsetDateTime){
        String DATE_TIME_SECOND_STRING = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_SECOND_STRING);
        return dateTimeFormatter.format(offsetDateTime);
    }

    //判断某个时间 是否在在某个日期的前后 返回-1 则代表在之前 1代表之后
    public static int compareToDate(String sourceDate,String compareDate){
        if(StringUtils.isEmpty(sourceDate) || StringUtils.isEmpty(compareDate)){
            return 0;
        }
        Date d;
        try {
            d = sdf.parse(compareDate);
            int flag = d.compareTo(sdf.parse(sourceDate));
            return flag;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    //判断某个时间 是否在在某个日期的前后 返回-1 则代表在之前 1代表之后
    public static int compareToDate(Date sourceDate,Date compareDate){
        if(sourceDate==null || compareDate ==null){
            return 0;
        }
        return compareDate.compareTo(sourceDate);
    }

    //将日期对象转成年月日
    public static String coverDateStr(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = sdf.parse(date);
            return sdf.format(d);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

    //将日期对象转成年月日
    public static Date coverDate(String date) {
        try {
            Date d = sdf.parse(date);
            return d;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否超时
     * @param obj1 对比1
     * @param obj2 对比2
     * @param min 判断是否超过这个分钟
     * @return
     */
    public static boolean isTimeout(Date obj1,Date obj2, int min) {
        // 计算时间差（毫秒）
        long timeDifference = obj1.getTime() - obj2.getTime();
        // 将时间差转换为分钟
        long minutesDifference = timeDifference / (1000 * 60);
        // 判断是否处于 Running 状态且超过 X 分钟
        return minutesDifference > min;
    }

    /**
     * 获取时间差方法,返回年月日时分秒 某年某月某日 某时某分某秒
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回开始时间和结束之间的数据差距
     */
    public static String getTimeDiff(Date startTime,Date endTime) {
        // 得到的时间差值, 微秒级别
        long diff = endTime.getTime() - startTime.getTime();
        //当前系统时间转Calendar类型
        Calendar currentTimes = dataToCalendar(endTime);
        //查询的数据时间转Calendar类型
        Calendar firstTimes = dataToCalendar(startTime);
        //获取年
        int year = currentTimes.get(Calendar.YEAR) - firstTimes.get(Calendar.YEAR);
        int month = currentTimes.get(Calendar.MONTH) - firstTimes.get(Calendar.MONTH);
        int day = currentTimes.get(Calendar.DAY_OF_MONTH) - firstTimes.get(Calendar.DAY_OF_MONTH);
        if (day < 0) {
            month -= 1;
            currentTimes.add(Calendar.MONTH, -1);
            //获取日
            day = day + currentTimes.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        if (month < 0) {
            month = (month + 12) % 12;//获取月
            year--;
        }
        long days = diff / (1000 * 60 * 60 * 24);
        //获取时
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        //获取分钟
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        //获取秒
        long s = (diff / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60);
        String resultTime = "";
        if(year>0){
            resultTime += year+"年";
        }
        if(month>0){
            resultTime += month+"月";
        }
        if(day>0){
            resultTime += day+"天";
        }
        if(hours>0){
            resultTime += hours+"时";
        }
        if(minutes>0){
            resultTime += minutes+"分";
        }
        if(s>0){
            resultTime += s+"秒";
        }
        //String CountTime = "" + year + "年" + month + "月" + day + "天 " + hours + "时" + minutes + "分" + s + "秒";
        return resultTime;
    }

    // Date类型转Calendar类型
    public static Calendar dataToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

}
