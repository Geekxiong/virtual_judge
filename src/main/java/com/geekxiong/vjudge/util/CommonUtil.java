package com.geekxiong.vjudge.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

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

}
