package net.hibiznet.libworks;

import java.nio.CharBuffer;
import java.util.UUID;

public class Test {

	/**
	 * @param args
	 */
	public static void smain(String[] args) {
		StringBuilder s = Code64.encodeInt(-46240012);
		System.out.println(s);
		System.out.println(Code64.decodeInt(s.toString()));
		s = Code64.encodeShort((short) -30012);
		System.out.println(s);
		System.out.println(Code64.decodeShort(s.toString()));
		s = Code64.encodeByte((byte) 126);
		System.out.println(s);
		System.out.println(Code64.decodeByte(s.toString()));
		s = Code64.encodeLong(-1234567890123L);
		System.out.println(s);
		System.out.println(Code64.decodeLong(s.toString()));
		long l = Double.doubleToLongBits(-657128.678595);
		System.out.println(l);
		s = Code64.encodeLong(l);
		System.out.println(s);
		l = Code64.decodeLong(s.toString());
		System.out.println(Double.longBitsToDouble(l));
		System.out.println(Code64.encodeShort((short) 63));

		UUID id = UUID.randomUUID();
		System.out.println(id.toString());

		// DATA 읽기, 데이타이면 메소드 빠져나옴.
		CharBuffer cbuf = CharBuffer.allocate(1024);

		cbuf.append((char) 02); // 개시 코드
		cbuf.append('1'); // 버젼
		cbuf.append("010"); // 커맨드 코드
		cbuf.append("000"); // 시점_ID
		cbuf.append("000"); // 종점_ID
		cbuf.append("000"); // 바디부 길이.
		cbuf.append((char) 03); // 종료 코드

		cbuf.append((char) 02); // 개시 코드
		cbuf.append('1'); // 버젼
		cbuf.append("001"); // 커맨드 코드
		cbuf.append("000"); // 시점 ID
		cbuf.append("000"); // 종점 ID
		// 바디 부분
		cbuf.append(Code64.encodeShort((short) (dummyID.length()
				+ dummyPD.length() + 1)));// 길이
		System.out.println(Code64.encodeShort((short) (dummyID.length()
				+ dummyPD.length() + 1)));
		cbuf.append(dummyID);
		cbuf.append('|');
		cbuf.append(dummyPD);
		cbuf.append((char) 03); // 종료 코드
		cbuf.flip();
		new Test().doRead(cbuf);
		System.out.println(Boolean.toString(false));
		System.out.println(Code64.encodeShort((short)-8));
	}

	public static final String dummyID = "12345678-1234-1234-1234-123456789ABC";
	public static final String dummyPD = "password";

	private void doRead(CharBuffer cbuf) {
		// 통신 데이타 분석 처리.
		int s = -1, e = -1;
		for (int i = 0; i < cbuf.length(); i++) {
			// 텍스트 개시 코드 02를 검색
			if (cbuf.charAt(i) == (char) 02) {
				s = i; // 시작 위치.
				e = s - 1; // 에러 대처.
				System.out.println("개시 위치" + s);
				i++;
				for (; i < cbuf.length(); i++) {
					if (cbuf.charAt(i) == (char) 03) {
						e = i;
						System.out.println("종료 위치" + e);
						this.decode(cbuf, s, e + 1);
						break;
					} else if (cbuf.charAt(i) == (char) 02) {
						// 종료코드 전에 다음 개시코드가 오면 에러.
						this.sendError(cbuf, s);
						i--;
						break;
					}
				}
			}
		}
		if (s > e)
			this.sendError(cbuf, s);
	}

	// 통신 데이타의 분석.
	void decode(CharBuffer cbuf, int start, int end) {
		System.out.println("디코드: " + cbuf.subSequence(start, end));
		String com = cbuf.subSequence(start+2, start + 5).toString();
		if(com.equals("001")){
			System.out.println("커맨드:게임로그인");
			//패스워드와 유저명 구분
			int bodysep;
			for(bodysep = start + 14; bodysep < end; bodysep++){
				if(cbuf.charAt(bodysep) == '|') break;
			}
			String gid = cbuf.subSequence(start + 14, bodysep).toString();
			String gpd = cbuf.subSequence(bodysep + 1, end-1).toString();
			System.out.println(gid);
			System.out.println(gpd);
		}
	}

	CharBuffer sendbuf = CharBuffer.allocate(1024);

	// 에러 통신
	// 버퍼와 개시 코드가 있는 위치을 지정.
	void sendError(CharBuffer cbuf, int start) {
		System.out.println("수신 에러 있음.");
		this.sendbuf.clear();
		this.sendbuf.append((char) 02);
		this.sendbuf.append("1000000---003");
		this.sendbuf.append(cbuf.subSequence(start + 2, start + 5));
		this.sendbuf.append((char) 03);
		this.sendbuf.flip();
		System.out.println(this.sendbuf);
	}
}
