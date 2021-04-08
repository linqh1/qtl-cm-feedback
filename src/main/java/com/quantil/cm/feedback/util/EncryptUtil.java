package com.quantil.cm.feedback.util;

import org.apache.http.HttpHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 加密工具类
 * @author pm
 * @version 创建时间：2014年11月24日  下午3:44:04
 */
public class EncryptUtil {

	public final static String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
	
	public static String signHmacSHA1(String key, String data) {
		try {
			byte signData[] = signHmacSHA1(key.getBytes("UTF-8"), data.getBytes("UTF-8"));
			return toBase64String(signData);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	public static byte[] signHmacSHA1(byte key[], byte data[]) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(new SecretKeySpec(key, "HmacSHA1"));
		return mac.doFinal(data);
	}

	public static String toBase64String(byte binaryData[]) {
		return Base64.getEncoder().encodeToString(binaryData);
	}

	/**
	 * 生成quantil系统的认证请求头
	 * @param apiUser
	 * @param apiKey
	 * @return
	 */
	public static Map<String, String> quantilAuthHeader(String apiUser, String apiKey) {
		Map<String, String> result = new HashMap<>(2);
		SimpleDateFormat sdf = new SimpleDateFormat(RFC1123_PATTERN, Locale.US);
		sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
		Date now = new Date();
		String formatDate = sdf.format(now);
		String password = EncryptUtil.signHmacSHA1(apiKey,formatDate);
		String authorizationString = EncryptUtil.toBase64String((apiUser + ":" + password).getBytes(Charset.forName("UTF-8")));
		result.put(HttpHeaders.DATE,formatDate);
		result.put(HttpHeaders.AUTHORIZATION,"Basic " + authorizationString);
		return result;
	}
}
