package cc.fish.fishhttp.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by fish on 17-6-7.
 */

public class MD5Utils {
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static final String SECRET = "c8ee721a8a723528edc723a77a52";
    public static String md5Encrypt(String src) {
        return md5Encrypt(src, 8);
    }
    public static String md5Encrypt(String src, int len) {
        ZLog.e("md5 src is", src);
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes("UTF-8"));
            return toHexString(bs, len);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] md5Encrypt2ByteArray(String src) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            return md5.digest(src.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[]{};

    }

    private static String toHexString(byte[] b, int len) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < len; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static String sortParams(Map<String, String> params) {
        boolean isFirst = true;
        ArrayList<String> keys = new ArrayList<>();
        keys.addAll(params.keySet());
        Collections.sort(keys, new ParamComparator());
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if ("".equals(key))
                continue;
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append("&");
            }
            sb.append(key).append("=").append(params.get(key));
        }
        sb.append(SECRET);
        return ZLog.e("SORT PARAMS", sb.toString());
    }

    static class ParamComparator implements Comparator<String> {

        @Override
        public int compare(String lhs, String rhs) {
            byte[] lbs = lhs.getBytes();
            byte[] rbs = rhs.getBytes();
            for (int i = 0; i < minLen(lhs, rhs); i++) {
                if (lbs[i] != rbs[i]) {
                    return lbs[i] - rbs[i];
                }
                continue;
            }
            return lhs.length() - rhs.length();
        }

        private int minLen(String lhs, String rhs) {
            return lhs.length() - rhs.length() > 0 ? rhs.length() : lhs.length();
        }
    }
}
