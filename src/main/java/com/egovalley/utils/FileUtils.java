package com.egovalley.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FileUtils {

    public static File multipartFileToFile(MultipartFile srcFile) {
        if (srcFile == null || srcFile.getSize() <= 0) {
            return null;
        }
        File tarFile = null;
        InputStream is = null;
        try {
            is = srcFile.getInputStream();
            tarFile = new File(srcFile.getOriginalFilename());
            inputStreamToFile(is, tarFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tarFile;
    }

    private static void inputStreamToFile(InputStream is, File file) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            int readBytes;
            int len = 8192;
            byte[] buffer = new byte[len];
            while ((readBytes = is.read(buffer, 0, len)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            File delFile = new File(file.toURI());
            return delFile.delete();
        } else {
            return false;
        }
    }

    public static boolean deleteFile(String filePath) {
        if (StringUtils.isBlank(filePath) || "null".equals(filePath)) {
            return false;
        } else {
            File file = new File(filePath);
            return file.exists() && file.isFile() && file.delete();
        }
    }

}
