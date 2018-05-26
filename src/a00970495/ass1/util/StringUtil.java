package a00970495.ass1.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharacterCodingException;

public class StringUtil {
  private StringUtil() {}


  static CharsetEncoder asciiEncoder = 
	      Charset.forName("US-ASCII").newEncoder(); // or "ISO-8859-1" for ISO Latin 1

	  public static boolean isPureAscii(String v) {
	    return asciiEncoder.canEncode(v);
	  }
	  
	  public static boolean isAscii(String v) {
		    byte bytearray []  = v.getBytes();
		    CharsetDecoder d = Charset.forName("US-ASCII").newDecoder();
		    try {
		      CharBuffer r = d.decode(ByteBuffer.wrap(bytearray));
		      r.toString();
		    }
		    catch(CharacterCodingException e) {
		      return false;
		    }
		    return true;
		  }
  
}
