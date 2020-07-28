package com.app
import com.app.DynamicTable

/**
 * Created by Adminlinken on 2015/11/13.
 * 自动生成相关文件后，清除Class目录下的所有文件。包括子文件夹下的文件
 */
class DeleteFolder {
    public void deleteFile(File myFile) {
        if (myFile.isDirectory()) {
            //System.out.println(""+myFile + "是文件夹--");
            File[] files = myFile.listFiles();
            if(files.size()==0){
                //myFile.delete()//目录为空，直接删除
            }else {
                for (File file1 : files) {
                    deleteFile(file1);
                }
            }
        } else {
            myFile.delete();
        }
    }
}