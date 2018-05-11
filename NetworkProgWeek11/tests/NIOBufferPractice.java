import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NIOBufferPractice {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_buffer1() {
		
		ByteBuffer buffer = ByteBuffer.allocate(32);
		buffer.put("SOMETHING".getBytes());
		debug(buffer);
		
		
		buffer.flip();
		byte[] bytes = new byte[buffer.limit()];
		buffer.get(bytes);
		
		debug(buffer);
		
		
	}
	
	public static void debug(ByteBuffer buffer) {
		System.out.println("POSITION: " + buffer.position() + ", LIMIT: " + buffer.limit());
		if (buffer.hasArray()) {
			System.out.println(Arrays.toString(buffer.array()));
		}
		
		System.out.println("String: " + new String(buffer.array()));
		System.out.println();
	}

}
