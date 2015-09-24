package org.test.util;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static List<Integer> chunkNumber(Integer number, Integer chunkSize) {
		Integer numOfChunks = (int) Math.ceil((double) number / chunkSize);
		
		List<Integer> output = new ArrayList<Integer>();
		
		for (Integer i = 0; i < numOfChunks; ++i) {
			Integer start = i * chunkSize;
			Integer length = Math.min(number - start, chunkSize);
			
			output.add(length);
		}
		
		return output;
	}

}
