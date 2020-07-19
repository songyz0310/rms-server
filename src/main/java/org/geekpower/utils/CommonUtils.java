package org.geekpower.utils;

import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

import org.geekpower.common.BaseError;
import org.geekpower.common.BaseException;

public class CommonUtils {

    /** default date time format pattern yyyy-MM-dd HH:mm:ss */
    public static final String DEFAULT_SDF = "yyyy-MM-dd HH:mm:ss";
    private static final String RANDOM_STR_BUF = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ0123456789";
    private static final char[] hexCode = "0123456789abcdef".toCharArray();

    /**
     * SimpleDateFormat is NOT thread-safe, we use ThreadLocal to avoid synchronize
     */
    private static ThreadLocal<Map<String, SoftReference<SimpleDateFormat>>> threadFormatter = new ThreadLocal<>();

    /**
     * Get SimpleDateFormat instance by certain date time format pattern
     * 
     * @return
     */
    private static SimpleDateFormat getDateFormatter(String dt) {
        SimpleDateFormat sdf = null;

        // get date formatter map from current thread
        Map<String, SoftReference<SimpleDateFormat>> map = threadFormatter.get();

        if (Objects.isNull(map)) {
            // create formatter map when it hasn't be created
            sdf = new SimpleDateFormat(dt);
            map = new HashMap<>();
            map.put(dt, new SoftReference<SimpleDateFormat>(sdf));
            threadFormatter.set(map);
        }
        else if (Objects.isNull(map.get(dt)) || Objects.isNull(map.get(dt).get())) {
            // create formatter for certain date pattern when it hasn't be created or has been release by JVM
            sdf = new SimpleDateFormat(dt);
            map.put(dt, new SoftReference<SimpleDateFormat>(sdf));
        }
        else {
            sdf = map.get(dt).get();
        }

        return sdf;
    }

    /**
     * parse string to Date object by using default date format: yyyy-MM-dd HH:mm:ss this method is
     * thread-safe
     * 
     * @param source
     * @return
     */
    public static Date parseDate(String source) {
        try {
            return getDateFormatter(DEFAULT_SDF).parse(source);
        }
        catch (ParseException exp) {
            throw new IllegalArgumentException("can't parse input parameter to Date object");
        }
    }

    /**
     * 将指定时区的时间字符串转换指定时区的Date对象
     * 
     * @param source
     * @param format
     * @param timeZone
     * @return
     */
    public static Date parseDate(String source, String format, int timeZone) {
        if (isNullOrEmpty(format))
            format = DEFAULT_SDF;

        try {
            TimeZone tz = TimeZone.getTimeZone("GMT" + (timeZone > 0 ? "-" + timeZone : "+" + (-timeZone)) + ":00");
            SimpleDateFormat sdf = getDateFormatter(format);
            sdf.setTimeZone(tz);
            return sdf.parse(source);
        }
        catch (ParseException exp) {
            throw new BaseException(BaseError.PARAM_FORMAT_WRONG, exp);
        }
    }

    /**
     * parse string to Date object by using special format this method is thread-safe
     * 
     * @param source
     * @param format default is 'yyyy-MM-dd HH:mm:ss' if input parameter is null
     * @return
     */
    public static Date parseDate(String source, String format) {
        if (isNullOrEmpty(format))
            format = DEFAULT_SDF;

        try {
            return getDateFormatter(format).parse(source);
        }
        catch (ParseException exp) {
            throw new BaseException(BaseError.PARAM_FORMAT_WRONG, exp);
        }
    }

    /**
     * convert Date object to String with default date format pattern:yyyy-MM-dd HH:mm:ss this method is
     * thread-safe
     * 
     * @param date default is current time is input parameter is null
     * @return
     */
    public static String formatDate(Date date) {
        if (Objects.isNull(date))
            date = new Date();

        return getDateFormatter(DEFAULT_SDF).format(date);
    }

    /**
     * convert Date object to String with special date format pattern this method is thread-safe
     * 
     * @param date default is current time if input parameter is null
     * @param format default is 'yyyy-MM-dd HH:mm:ss' if input parameter is null
     * @return
     */
    public static String formatDate(Date date, String format) {
        if (Objects.isNull(date))
            date = new Date();

        if (isNullOrEmpty(format))
            format = DEFAULT_SDF;

        return getDateFormatter(format).format(date);
    }

    public static String formatDate(Date date, int timeZone) {
        if (Objects.isNull(date))
            date = new Date();

        SimpleDateFormat sdf = getDateFormatter(DEFAULT_SDF);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT" + (timeZone <= 0 ? "+" + (-timeZone) : "-" + timeZone) + ":00"));
        return sdf.format(date);
    }

    /**
     * by using input data to generate MD5 string, this string is fixed-length upper hex, length is 32
     * 
     * @param source
     * @return
     */
    public static String makeMD5(String source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(source.getBytes("UTF8"));
            byte s[] = md5.digest();
            return toHex(s);
        }
        catch (Exception exp) {// this exception scarcely possible to meet, we can ignore it
            throw new RuntimeException(exp);
        }
    }

    /**
     * convert texture IPv4 to byte array
     *
     * @param ip if ip is empty return
     * @return
     */
    public static byte[] toIPBytes(String ip) {
        if (isNullOrEmpty(ip))
            throw new IllegalArgumentException("ip can't be null or empty");

        String[] tmp = ip.split("\\.");
        if (tmp.length != 4)
            throw new IllegalArgumentException("wrong format:" + ip);

        byte[] addr = new byte[4];
        for (int i = 0; i < tmp.length; i++)
            addr[i] = (byte) Short.parseShort(tmp[i]);
        return addr;
    }

    /**
     * convert byte array to HEX string
     *
     * @param data
     * @return
     */
    public static String toHex(byte[] data) {
        return toHex(data, false);
    }

    /**
     * Convert byte array to Hex code
     * 
     * @param data
     * @param isFormat, if true separate byte by space, one line contain 16 bytes
     * @return
     */
    public static String toHex(byte[] data, boolean isFormat) {
        if (data == null || data.length == 0)
            return "";

        StringBuilder sb = new StringBuilder(data.length * 2 + (isFormat ? data.length + data.length / 16 : 0));
        for (int i = 0; i < data.length; i++) {
            sb.append(hexCode[(data[i] >> 4) & 0xF]);
            sb.append(hexCode[data[i] & 0xF]);

            if (isFormat) {
                sb.append(" ");

                if ((i + 1) % 16 == 0)
                    sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Convert Hex string to byte array hex string format is: 2 char represent one byte,can use space as
     * separator for example, "12 1e 3d ee FF 09"
     * 
     * @param hex
     * @return
     */
    public static byte[] toBytes(String hex) {
        byte[] buff = new byte[hex.length() / 2];

        int s1, s2, count = 0;
        for (int i = 0; i < hex.length(); i++) {
            if (hex.charAt(i) >= '0' && hex.charAt(i) <= '9')
                s1 = hex.charAt(i) - 48;
            else if (hex.charAt(i) >= 'A' && hex.charAt(i) <= 'F')
                s1 = hex.charAt(i) - 55;
            else if (hex.charAt(i) >= 'a' && hex.charAt(i) <= 'f')
                s1 = hex.charAt(i) - 87;
            else
                continue;

            i++;
            if (hex.charAt(i) >= '0' && hex.charAt(i) <= '9')
                s2 = hex.charAt(i) - 48;
            else if (hex.charAt(i) >= 'A' && hex.charAt(i) <= 'F')
                s2 = hex.charAt(i) - 55;
            else if (hex.charAt(i) >= 'a' && hex.charAt(i) <= 'f')
                s2 = hex.charAt(i) - 87;
            else
                continue;

            buff[count] = ((byte) (s1 * 16 + s2));
            count++;
        }
        byte[] result = new byte[count];
        System.arraycopy(buff, 0, result, 0, result.length);
        return result;
    }

    public static long getLong(Object value, long def) {
        if (value == null)
            return def;
        else if (value instanceof Integer)
            return ((Integer) value).longValue();
        else if (value instanceof Long)
            return ((Long) value).longValue();
        if (value instanceof BigInteger)
            return ((BigInteger) value).longValue();
        else if (value instanceof BigDecimal)
            return ((BigDecimal) value).longValue();
        else
            return def;
    }

    /**
     * convert input data to double
     * 
     * @param value
     * @param def
     * @return
     */
    public static double getDouble(Object value, double def) {
        if (value == null)
            return def;
        else if (value instanceof Double)
            return ((Double) value).doubleValue();
        else if (value instanceof Float)
            return ((Float) value).doubleValue();
        else if (value instanceof Integer)
            return ((Integer) value).doubleValue();
        else if (value instanceof Long)
            return ((Long) value).doubleValue();
        if (value instanceof BigInteger)
            return ((BigInteger) value).doubleValue();
        else if (value instanceof BigDecimal)
            return ((BigDecimal) value).doubleValue();
        else
            return def;
    }

    /**
     * input string or collection is null or is empty
     * 
     * @param <T>
     * 
     * @param obj
     * @return
     */
    public static <T> boolean isNullOrEmpty(T obj) {
        if (Objects.isNull(obj))
            return true;

        if (String.class.isInstance(obj))
            return String.class.cast(obj).trim().isEmpty();
        else if (Collection.class.isInstance(obj))
            return Collection.class.cast(obj).isEmpty();
        else if (Map.class.isInstance(obj))
            return Map.class.cast(obj).isEmpty();
        else if (Set.class.isInstance(obj))
            return Set.class.cast(obj).isEmpty();
        else
            return false;
    }

    /**
     * input string is not null and not ""
     * 
     * @param obj
     * @return
     */
    public static <T> boolean nonNullAndEmpty(T obj) {
        if (Objects.isNull(obj))
            return false;

        if (String.class.isInstance(obj))
            return !String.class.cast(obj).isEmpty();
        else if (Collection.class.isInstance(obj))
            return !Collection.class.cast(obj).isEmpty();
        else if (Map.class.isInstance(obj))
            return !Map.class.cast(obj).isEmpty();
        else if (Set.class.isInstance(obj))
            return !Set.class.cast(obj).isEmpty();
        else
            return true;
    }

    public static boolean containNullOrEmpty(Object... params) {
        if (Objects.isNull(params) || params.length == 0)
            return true;

        for (int i = 0; i < params.length; i++) {
            if (isNullOrEmpty(params[i]))
                return true;
        }

        return false;
    }

    /**
     * Convert first character to upper
     * 
     * @param value
     * @return
     */
    public static String toCapitalLetters(String value) {
        char[] cs = value.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    public static String getRandomString(int length) {
        int number = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            number = ThreadLocalRandom.current().nextInt(RANDOM_STR_BUF.length());
            sb.append(RANDOM_STR_BUF.charAt(number));
        }
        return sb.toString();
    }

}
