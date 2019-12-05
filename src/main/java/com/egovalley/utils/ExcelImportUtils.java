package com.egovalley.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelImportUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportUtils.class);

    /**
     * 导入Excel
     * @param filePath excel文件路径
     * @return Workbook
     */
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
        } catch (IOException e) {
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

    /**
     * 导入Excel
     * @param file excel文件
     * @return Workbook
     */
    public static Workbook importExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String fileName = file.getOriginalFilename();
        String suf = fileName.substring(fileName.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(FileUtils.multipartFileToFile(file));
            if (".xls".equals(suf)) {
                return new HSSFWorkbook(is);
            } else if (".xlsx".equals(suf)) {
                return new XSSFWorkbook(is);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is .close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 读取表格数据
     * @param cell 一格
     * @return String
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        String cellValue;
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC:// 数字类型
                boolean cellDateFormatted = HSSFDateUtil.isCellDateFormatted(cell);
                if (cellDateFormatted) {// 处理日期格式、时间格式
                    SimpleDateFormat sdf;
                    short dataFormat = cell.getCellStyle().getDataFormat();
                    if (dataFormat == 14 || dataFormat == 179 || dataFormat == 177) {
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                    } else if (dataFormat == 21) {
                        sdf = new SimpleDateFormat("HH:mm:ss");
                    } else if (dataFormat == 22) {
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    } else {
                        try {
                            sdf = new SimpleDateFormat("yyyy-MM-dd");
                        } catch (Exception e) {
                            throw new RuntimeException(">>> 日期格式错误");
                        }
                    }
                    Date date = cell.getDateCellValue();
                    cellValue = sdf.format(date);
                } else {// 处理数值格式
                    double numericCellValue = cell.getNumericCellValue();
                    DecimalFormat df = new DecimalFormat("#.######");
                    cellValue = df.format(numericCellValue);
                }
                break;
            case STRING:// 字符串类型
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case BOOLEAN:// 布尔类型
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:// 公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case BLANK:// 空值
                cellValue = null;
                break;
            case _NONE:// 空值
                cellValue = null;
                break;
            case ERROR:// 异常
                cellValue = "#非法字符#";
                break;
            default:
                cellValue = "#未知类型#";
                break;
        }
        return cellValue;
    }

}
