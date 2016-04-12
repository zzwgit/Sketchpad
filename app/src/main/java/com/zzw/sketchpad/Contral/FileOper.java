package com.zzw.sketchpad.Contral;

import java.io.File;

import android.graphics.Bitmap;


public class FileOper {
	/*取得指定目录下所有文件的绝对路径*/
	public Bitmap[] getStrokeFilePaths(){
		String strDir = getStrokeFilePath();
		Bitmap[] strDirs = new Bitmap[100] ;
		File file = new File(strDir);
		File[] files = file.listFiles();
		for(int i=0; i<files.length; i++){
			if(files[i].exists()){
				strDirs[i] = BitmapUtil.loadBitmapFromSDCard(files[i].getPath());
			}
		}
		return strDirs;
	}
	/*取得指定目录下所有文件的名称*/
	public String[] getStrokeFileNames(){
		String strDir = getStrokeFilePath();
		String[] strNames = new String[100];
		String[] filenames = null;
		File file = new File(strDir);
		File[] files = file.listFiles();
		for(int i=0; i<files.length; i++){
			if(files[i].exists()){
				String str = files[i].getName();
				strNames[i] = str;
			}
		}
		filenames = new String[files.length];
		for(int i=0; i<filenames.length; i++){
			filenames[i] = strNames[i];
		}
		return filenames;
	}
	/*取得指定目录的路径*/
	public String getStrokeFilePath()
    {
        File sdcarddir = android.os.Environment.getExternalStorageDirectory();
        String strDir = sdcarddir.getPath() + "/CANVAS/";
        File file = new File(strDir);
        if (!file.exists())
        {
            file.mkdirs();
        }
        return strDir;
    }

}
