package org.test.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class ZipUtil {

	public void zipIt(String filename, char[] password, byte[] context) {
		try {
			ZipFile zipFile = new ZipFile(filename);
			
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			parameters.setEncryptFiles(true);
			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
			parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
			parameters.setPassword(password);
			parameters.setFileNameInZip("data.xml");
			parameters.setSourceExternalStream(true);
			
			zipFile.addStream(new ByteArrayInputStream(context), parameters);
		} catch (ZipException e) {
			e.printStackTrace();
		} 
	}
	
	public static InputStream unzip(String filename, char[] password) throws IOException {
		ZipInputStream is = null;
		try {
			ZipFile zipFile = new ZipFile(filename);
			List<?> fileHeaderList = zipFile.getFileHeaders();
			if (fileHeaderList.size() > 1) {
				throw new IOException("Archives compromised, please reinstall software. Array size: " + fileHeaderList.size());
			}
			
		    FileHeader fileHeader = (FileHeader)fileHeaderList.get(0);
		    is = zipFile.getInputStream(fileHeader);
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return is;
	}
	
}
