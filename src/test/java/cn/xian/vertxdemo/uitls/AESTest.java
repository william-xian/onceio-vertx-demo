package cn.xian.vertxdemo.uitls;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

import cn.xian.vertxdemo.utils.AES;

public class AESTest {

	@Test
	public void encoding() throws UnsupportedEncodingException {
		
		String content = "hello";
		String encode = AES.encode(content, "p");
		String decode = AES.decode(encode, "p");
		
		Assert.assertNotEquals(content, encode);
		Assert.assertEquals(content, decode);
		
		
	}
}
