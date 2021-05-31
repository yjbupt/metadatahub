package com.bupt.cad.metadatahub.util;


import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static Map<String, SimpleDateFormat> simpleDateFormatMap;

    static {
        simpleDateFormatMap = new HashMap<>();
        simpleDateFormatMap.put(yyyy_MM_dd_HH_mm_ss, new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss));
        simpleDateFormatMap.put(yyyy_MM_dd, new SimpleDateFormat(yyyy_MM_dd));
    }

    public static String format(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return simpleDateFormatMap.get(yyyy_MM_dd_HH_mm_ss).format(date);
    }

    public static String format(LocalDateTime date, String pattern) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(pattern)) {
            return format(date);
        }
        return simpleDateFormatMap.get(pattern).format(date);
    }
}
