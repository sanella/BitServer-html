import java.io.FileNotFoundException;
import java.util.HashMap;

import ba.bitcamp.loger.Logger;

public class Test {
	public static void main(String[] args) {
		// NAPRAVILI HASHMAPU
		HashMap<String, String> test = new HashMap<String, String>();
		test.put("aplication", "aplication");
		test.put("error", "error");
		test.put("warning", "warning");

		try {
			new Logger(test);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.log("aplication", "nesto");
		Logger.log("error", "nesto drugo");
		Logger.log("warning", "jos nesto");
	}
}
