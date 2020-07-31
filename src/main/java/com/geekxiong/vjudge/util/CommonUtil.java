package com.geekxiong.vjudge.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
    /**
     * 将map转换成url
     * @param map
     * @return
     */
    public static String getUrlParams(Map<String, String> map) {
        if (map == null||map.size()<1) {
            return "";
        }
        boolean first = true;
        StringBuffer sb = new StringBuffer("?");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(first){
                first=false;
            }else {
                sb.append("&");
            }
            sb.append(entry.getKey() + "=" + entry.getValue());
        }

        return sb.toString();
    }

    public static Date dateStr2DateYMD(String dateStr) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.parse(dateStr);
    }

    public static Date dateStr2Date(String dateStr) {
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getOneDaysStartTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static String regFind(String text, String reg, int i){
        Matcher m = Pattern.compile(reg, Pattern.CASE_INSENSITIVE).matcher(text);
        return m.find() ? m.group(i) : "";
    }

    public static String regFind(String text, String reg){
        return regFind(text, reg, 1);
    }


    public static String regFindCaseSensitive(String text, String reg, int i){
        Matcher m = Pattern.compile(reg).matcher(text);
        return m.find() ? m.group(i) : "";
    }

    public static String regFindCaseSensitive(String text, String reg){
        return regFindCaseSensitive(text, reg, 1);
    }

}
