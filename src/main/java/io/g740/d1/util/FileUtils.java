package io.g740.d1.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {

	}

	public static String contact(String pathPrefix, String... endpoints) {
		StringBuilder sb = new StringBuilder();
		sb.append(pathPrefix);

		for (String endpoint : endpoints) {
			sb.append(File.separator + endpoint);
		}

		return sb.toString().replaceAll("[/|\\\\]+", "\\"+File.separator);
	}


	//判断文件是否存在,如果不存在则创建
	public static void initMkdir(String[] filePaths) throws Exception {
		try {
			for (int i = 0; i < filePaths.length; i++) {
				File file  = new File(filePaths[i]);
				if(!file.exists()) {
					file.mkdirs();
				}
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}


    /**
     * 删除指定文件夹下所有文件
     * param path 文件夹完整绝对路径
     *
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
               boolean deleted = temp.delete();
                if (!deleted) {
                    LOGGER.warn("failed to delete file: {}", temp.getAbsolutePath());
                }
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                boolean success = (new File(path + "/" + tempList[i])).delete();
                flag = success;
            }
        }
        return flag;
    }
}
