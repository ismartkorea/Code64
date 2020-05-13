package net.hibiznet.libworks;

public class Code64 {

	public static StringBuilder encodeInt(int in) {
		long l = (long) in;
		if (l < 0) {
			l = Math.abs(l) + Integer.MAX_VALUE;
		}
		return encode(l, new StringBuilder("000000"));
	}

	public static StringBuilder encodeShort(short in) {
		long l = (long) in;
		if (l < 0) {
			l = Math.abs(l) + Short.MAX_VALUE;
		}
		return encode(l, new StringBuilder("000"));
	}

	public static StringBuilder encodeByte(byte in) {
		long l = (long) in;
		if (l < 0) {
			l = Math.abs(l) + Byte.MAX_VALUE;
		}
		return encode(l, new StringBuilder("00"));
	}

	public static StringBuilder encodeLong(long in) {
		StringBuilder sb = encode(Math.abs(in), new StringBuilder(
				"000000000000"));
		if (in < 0) {
			sb.setCharAt(0, '-');
		}
		return sb;
	}
	
	public static StringBuilder encodeBoolean(boolean in){
		if(in) return new StringBuilder("1");
		return new StringBuilder("0");
	}
	public static boolean decodeBoolean(String in){
		if(in.charAt(0)=='1') return true;
		return false;
	}

	public static long decodeLong(String in) {
		if (in.charAt(0) == '-') {
			in = "0" + in.substring(1);
			return decode(in) * -1;
		}
		return decode(in);
	}

	public static int decodeInt(String in) {
		long l = decode(in);
		if (l > Integer.MAX_VALUE) {
			l = -(l - Integer.MAX_VALUE);
		}
		return (int) l;
	}

	public static short decodeShort(String in) {
		long l = decode(in);
		if (l > Short.MAX_VALUE) {
			l = -(l - Short.MAX_VALUE);
		}
		return (short) l;
	}

	public static byte decodeByte(String in) {
		long l = decode(in);
		if (l > Byte.MAX_VALUE) {
			l = -(l - Byte.MAX_VALUE);
		}
		return (byte) l;
	}

	// 내부 계산에 사용하는 메소드
	// long형->64진수문자열 변환
	private static StringBuilder encode(long in, StringBuilder sb) {
		for (int i = sb.length() - 1; i > -1; i--) {
			long mod = in % 64;
			in /= 64;
			sb.setCharAt(i, (char) ((int) '0' + (int) mod));
			if (in == 0)
				break;
		}
		return sb;
	}

	// 64진수문자열->long형변환
	private static long decode(String in) {
		long l = 0;
		int len = in.length() - 1;
		for (int i = len; i > -1; i--) {
			int c = (int) (in.charAt(i)) - (int) ('0');
			l += c * Math.pow(64, (len - i));
		}
		return l;
	}
}
