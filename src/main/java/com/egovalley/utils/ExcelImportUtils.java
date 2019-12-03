package com.egovalley.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelImportUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportUtils.class);

    public static Workbook importExcel(String filePath) {
        if (StringUtils.isBlank(filePath) || "null".equals(filePath)) {
            return null;
        }
        String suf = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if (".xls".equals(suf)) {
                return new HSSFWorkbook(is);
            } else if (".xlsx".equals(suf)) {
                return new XSSFWorkbook(is);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error(">>> Excel表格读取错误", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
