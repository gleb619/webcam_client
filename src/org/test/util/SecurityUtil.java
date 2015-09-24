package org.test.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.test.config.Project;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

public class SecurityUtil {

	public static final String salt = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
	
	private static String lastKey = "";
	
	public static String hashSha256(String text) {
		return DigestUtils.sha256Hex(text);
	}

	public static byte[] saveKey(String key) throws GeneralSecurityException {
		Integer position = RandomUtil.randomPosition(key);
		key = key.substring(0, position) + salt + key.substring(position);
		
		String filename = keyStore();
		byte[] output = junk(lastKey, key);
		FileUtil.saveToFile(filename, output);
		lastKey = "";
		
		return output;
	}
	
	public static String loadKey() {
		String key = null;
		try {
			key = readKeyFromJunk(readJunk());
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		}
		
		return key;
	}
	
	private static String readJunk() throws UnsupportedEncodingException, GeneralSecurityException {
		List<String> files = FileUtil.scanPath(keyStorePath(), ".*\\.db");
		String decryptedText = null;
		if (files.size() > 0) {
			String key = new File(files.get(0)).getName();
			byte[] text = FileUtil.readFromFile2(files.get(0));
			decryptedText = SecurityUtil.decrypt(key, text);
		}
		
		return decryptedText;
	}
	
	private static String readKeyFromJunk(String text) throws NullPointerException {
		String[] information = text.substring(text.indexOf(Project.MANIFEST.value()) + Project.MANIFEST.value().length(), text.length() - 1).split("\\$");
		if (information.length != 2) {
			throw new NullPointerException("Data is corrupted");
		}
		Integer position = new Integer(information[0]);
		Integer size = new Integer(information[1]);
		
		return text.substring(position, position + size).replace(salt, "");
	}
	
	public static byte[] junk(String key, String context) throws GeneralSecurityException {
		String text = RandomUtil.generateTrashSuperEasy();
		Integer position = RandomUtil.randomPosition(text);
		byte[] output = null;
		
		String information = Project.MANIFEST.value() + position + "$" + context.length() + "#";
		
		text = text.substring(0, position) + context + text.substring(position);
		text = text + information;
		
		output = SecurityUtil.encrypt(key, text);
		
		return output;
	}
	
	private static String keyStore() {
		lastKey = FileUtil.randomFilename("db");
		FileUtil.checkDirExist(keyStorePath());
		return keyStorePath() + lastKey;
	}
	
	private static String keyStorePath() {
		return FileUtil.currentPath() + Project.DATA.value();
	}
	
	public static SecretKey getSecretKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
	    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	    // NOTE: last argument is the key length, and it is 128
	    
	    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 128);
	    SecretKey tmp = factory.generateSecret(spec);
	    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
	    return(secret);
	}

	public static byte[] encrypt(String password, String text) throws GeneralSecurityException {
	    SecretKey secret = getSecretKey(password);
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(new byte[cipher.getBlockSize()]));
	    return cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
	}
	
	public static String decrypt(String password, byte[] text) throws GeneralSecurityException, UnsupportedEncodingException {
		SecretKey secret = getSecretKey(password);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(new byte[cipher.getBlockSize()]));
		return new String(cipher.doFinal(text), "UTF-8");
	}
	
	public static String randomKey() {
		return new SecureRandom().generateSeed(16).toString();
	}

}
