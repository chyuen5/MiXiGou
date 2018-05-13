package com.hsinfo.mixigou.Utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

	private String SDPATH;

	public String getSDPATH() {
		return SDPATH;
	}
	public FileUtils() {
		//得到当前外部存储设备的目录
		// /SDCARD
		SDPATH = Environment.getExternalStorageDirectory() + "/Download/";
	}
	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH +"com.cjwsjy.app/" + fileName);
		file.createNewFile();
		return file;
	}
	
	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName)
	{	
		File dir1 = new File(SDPATH + "com.cjwsjy.app/");
		if(!isFileExist(SDPATH + "com.cjwsjy.app/")){
			dir1.mkdir();
		}
		File dir = new File(SDPATH+"com.cjwsjy.app/" + dirName);
		if(!isFileExist(SDPATH+"com.cjwsjy.app/" + dirName)){
			dir.mkdir();
		}		
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String folderName){
		File file = new File(folderName);
		return file.exists();
	}
	
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path, String fileName, InputStream input)
	{
		File file = null;
		OutputStream output = null;
		
		try
		{
			creatSDDir(path);
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer [] = new byte[4 * 1024];

			int length = 0;
            while((length=(input.read(buffer)))!=-1)
			{
                  output.write(buffer,0,length);  
            } 
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}
}