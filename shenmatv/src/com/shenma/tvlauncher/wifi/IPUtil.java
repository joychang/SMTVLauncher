package com.shenma.tvlauncher.wifi;

public class IPUtil {
	public static void main(String[] args) {
		System.out.println(getNetmaskLength("255.255.255.0"));
	}

	/**
	 * 检查是否是合法子网掩码
	 * 
	 * @param mask
	 * @return
	 */
	public static boolean checkMask(final String mask) {
		if (!isIPNum(mask))
			return false;
		int m = toInt(mask);
		m = ~m + 1;
		if ((m & (m - 1)) != 0) // 判断是否为2^n
			return false;
		return true;
	}

	/**
	 * 检查是否是有效IP 不能检查子网掩码和dns
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean checkIP(final String ip) {
		if (!isIPNum(ip))
			return false;
		int[] intIP = changeToInt(ip);
		if (intIP == null)
			return false;
		for (int i = 0; i < intIP.length; i++) {
			if (intIP[i] < 0 || intIP[i] > 255)
				return false;
		}
		if (intIP[0] == 0 || intIP[3] == 0)// 首尾不能为 0
			return false;
		if (intIP[0] > 223) // D类,E类地址不能作为IP
			return false;
		return true;
	}

	/**
	 * 判断是否是合法IP
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isIP(final String ip) {
		if (!isIPNum(ip))
			return false;
		int[] intIP = changeToInt(ip);
		if (intIP == null)
			return false;
		for (int i = 0; i < intIP.length; i++) {
			if (intIP[i] < 0 || intIP[i] > 255)
				return false;
		}
		if (intIP[0] == 0)
			return false;
		return true;
	}

	/**
	 * 检查ip，子网掩码，网关，是否正确
	 * 
	 * @param ip
	 * @param mask
	 * @param gateway
	 * @return
	 */
	public static boolean check(String ip, String mask, String gateway) {
		if (!checkIP(ip) || !checkMask(mask) || !checkIP(gateway))
			return false;
		int intIp = toInt(ip);
		int intMask = toInt(mask);
		int intGateway = toInt(gateway);
		if (intIp == 0 || intMask == 0 || intGateway == 0)
			return false;
		int a = intIp & intMask;
		int b = intGateway & intMask;
		if (a != b)
			return false;
		return true;
	}

	private static boolean isIPNum(final String str) {
		if (str == null)
			return false;
		// 判断是否存在非IP字符
		String ipComp = "0123456789.";
		// 判断是否存在非IP字符
		for (int i = 0; i < str.length(); i++) {
			if (ipComp.indexOf(str.charAt(i)) == -1) {
				return false;
			}
		}
		return true;
	}

	private static int[] changeToInt(final String ip) {
		if (ip == null)
			return null;
		String[] str = ip.split("\\.");
		if (str.length != 4)
			return null;
		try {
			int[] intIP = new int[4];
			for (int i = 0; i < str.length; i++) {
				intIP[i] = Integer.parseInt(str[i]);
			}
			return intIP;
		} catch (Exception e) {
		}
		return null;
	}

	private static int toInt(final String ip) {
		int[] intIP = changeToInt(ip);
		if (intIP != null) {
			int num = ((intIP[0] << 24) | (intIP[1] << 16) | (intIP[2] << 8) | intIP[3]);
			return num;
		}
		return 0;
	}

	public static int getNetmaskLength(String maskStr) {
		int mask = toInt(maskStr);
		int prefixLength = 0;
		for (int i = 1; i < Integer.SIZE; i++) {
			if (((mask >> i) & 1) == 1) {
				prefixLength++;
			}
		}
		return prefixLength;
	}
	/**
	 * 检测主机号是不是全1
	 * @param ip IP地址
	 * @param mask Mask地址
	 * @return 
	 */
	public static boolean checkHost255(String ip, String mask) {
		if (!checkIP(ip) || !checkMask(mask))
			return false;
		int intIp = toInt(ip);
		int intMask = toInt(mask);
		if (intIp == 0 || intMask == 0 )
			return false;
		int a = intIp | intMask;
		if (a == -1)
			return false;
		return true;
	}
	/**
     * 把int->ip地址
     * @param ipInt
     * @return String
     */
    public static String intToIp(int ipInt) {
        return new StringBuilder().append(((ipInt) & 0xff)).append('.')
                .append((ipInt >> 8) & 0xff).append('.').append(
                        (ipInt >> 16) & 0xff).append('.').append(((ipInt>>24) & 0xff))
                .toString();
    }
}
