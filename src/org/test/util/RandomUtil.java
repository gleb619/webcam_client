package org.test.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RandomUtil {

	private static char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static char[] specialCharacter = "!@#$%^&*()_+=-{}[]:\"|\\\';<>?/.,".toCharArray();
	
	private static DateFormat format = new SimpleDateFormat("YYYY_MM_dd_hh_mm_ss");
	private static String text;
	
	public static String randomText() {
		return randomText(randomRange(chars.length / 2, chars.length), true, true);
	}
	
	public static String randomText(Integer size, Boolean allowNumber, Boolean allowSpecialCharacter) {
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			char c = chars[random.nextInt(chars.length)];
			if (allowNumber) {
				if(randomBoolean()){
					c = (char) ('0' + randomRange(0, 9));
				}
				
			}
			
			if (allowSpecialCharacter) {
				if(randomBoolean2()){
					c = specialCharacter[random.nextInt(specialCharacter.length)];
				}
			}
			
			sb.append(c);
		}
		String output = sb.toString();

		return output;
	}

	public static Integer randomRange(Integer min, Integer max) {
		return new Random().nextInt((max - min) + 1) + min;
	}
	
	public static Boolean randomBoolean() {
		return new Random().nextBoolean();
	}
	
	public static Boolean randomBoolean2() {
		return randomRange(0, 9) <= 3;
	}
	
	public static Integer randomPosition(String text) {
		return randomRange(10, text.length() - 10);
	}
	
	public static Integer randomPosition2(String text) {
		return randomRange(0, text.length() - 1);
	}
	
	public static String generateTrashSuperEasy() {
		return generateTrash(100);
	}
	
	public static String generateTrashEasy() {
		return generateTrash(1000);
	}
	
	public static String generateTrashMedium() {
		return generateTrash(5000);
	}
	
	public static String generateTrashStrong() {
		return generateTrash(10000);
	}
	
	public static String generateTrashSuperStrong() {
		return generateTrash(100000);
	}
	
	public static String generateTrash(Integer size) {
		ExecutorService executor = Executors.newWorkStealingPool(2);
		
		text = "";
		
		for (Integer number : Utils.chunkNumber(size, 1000)) {
			executor.execute(() -> {
				for (int index = 0; index < number; index++) {
					text += randomText();
				}
			});
		}
		
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		
		}
		
		return text;
	}
	
	public static String jpg(){
		return name("jpg");
	}
	
	public static String png(){
		return name("png");
	}
	
	public static String name(String extension){
		return format.format(new Date()) + "." + extension;
	}
	
	public static String filename(String extension) {
		return UUID.randomUUID().toString() + "." + extension;
	}

}
