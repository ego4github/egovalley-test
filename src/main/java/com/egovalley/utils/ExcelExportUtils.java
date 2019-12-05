package com.egovalley.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ExcelExportUtils {

    private HttpServletResponse response;
    private String fileName;// 文件名
    private String fileDir;// 文件路径
    private String sheetName;// sheet名
    private String titleFontType = "Arial Unicode MS";// 表头字体
    private String titleBackColor = "C1FBEE";// 表头背景色
    private short titleFontSize = 12;// 表头字号
    private String address = "";// 添加自动筛选的列, 如 A:M
    private String contentFontType = "Arial Unicode MS";// 正文字体
    private short contentFontSize = 12;// 正文字号
    private String floatDecimal = ".00";// Float类型数据小数位
    private String doubleDecimal = ".00";// Double类型数据小数位
    private String colFormula[] = null;// 设置列的公式
    private DecimalFormat floatDecimalFormat = new DecimalFormat(floatDecimal);
    private DecimalFormat doubleDecimalFormat = new DecimalFormat(doubleDecimal);
    private SXSSFWorkbook workbook;

    public ExcelExportUtils(String fileDir, String sheetName) {
        this.fileDir = fileDir;
        this.sheetName = sheetName;
        workbook = new SXSSFWorkbook(1000);
    }

    public ExcelExportUtils(HttpServletResponse response, String fileName, String sheetName) {
        this.response = response;
        this.fileName = fileName;
        this.sheetName = sheetName;
        workbook = new SXSSFWorkbook(1000);
    }

    /**
     * 设置表头字体
     */
    public void setTitleFontType(String tileFontType) {
        this.titleFontType = tileFontType;
    }

    /**
     * 设置表头背景色
     */
    public void setTitleBackColor(String titleBackColor) {
        this.titleBackColor = titleBackColor;
    }

    /**
     * 设置表头字体大小
     */
    public void setTitleFontSize(short titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    /**
     * 设置表头自动筛选栏位, 如 A:AC
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 设置正文字体
     */
    public void setContentFontType(String contentFontType) {
        this.contentFontType = contentFontType;
    }

    /**
     * 设置正文字号
     */
    public void setContentFontSize(short contentFontSize) {
        this.contentFontSize = contentFontSize;
    }

    /**
     * 设置float类型数据小数位, 默认 0.00
     */
    public void setFloatDecimal(String floatDecimal) {
        this.floatDecimal = floatDecimal;
    }

    /**
     * 设置double类型数据小数位, 默认 0.00
     */
    public void setDoubleDecimal(String doubleDecimal) {
        this.doubleDecimal = doubleDecimal;
    }

    /**
     * 设置列的公式
     */
    public void setColFormula(String[] colFormula) {
        this.colFormula = colFormula;
    }

    /**
     * 导出Excel
     *
     * @param titleName   每列抬头名, 如 电话号码 | 归属地
     * @param titleColumn 对应抬头名的属性名, 如 telephone | ownership
     * @param titleSize   每列宽度
     * @param dataList    每行数据
     */
    public void exportExcel(String titleName[], String titleColumn[], int titleSize[], List dataList) {
        Sheet sheet = workbook.createSheet(this.sheetName);
        OutputStream os = null;
        try {
            if (fileDir != null) {// 写到文件中
                os = new FileOutputStream(fileDir);
            } else {// 写到输出流中
                os = response.getOutputStream();
                fileName += ".xlsx";
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml");
                response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(this.fileName, "UTF-8"));
            }
            // 写表头
            Row titleNameRow = workbook.getSheet(sheetName).createRow(0);
            // 设置样式
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle = setFontAndBorder(titleStyle, titleFontType, titleFontSize);
            titleStyle = setColor(titleStyle, titleBackColor, (short) 9);
            for (int i = 0, len = titleName.length; i < len; i++) {// 编写表头
                sheet.setColumnWidth(i, titleSize[i] * 256);// 设置宽度
                Cell cell = titleNameRow.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(titleName[i]);
            }
            // 为表头添加自动筛选
            if (StringUtils.isNotBlank(address) && !"null".equals(address)) {
                CellRangeAddress cra = CellRangeAddress.valueOf(address);
                sheet.setAutoFilter(cra);
            }
            // 通过反射获取数据并写入excel
            if (CollectionUtils.isNotEmpty(dataList)) {
                titleStyle = setFontAndBorder(titleStyle, contentFontType, contentFontSize);
                if (titleColumn.length > 0) {
                    for (int rowIndex = 1, size = dataList.size(); rowIndex <= size; rowIndex++) {// 先行
                        Object obj = dataList.get(rowIndex - 1);// 获取第rowIndex个数据对象
                        Class clazz = dataList.get(rowIndex - 1).getClass();// 数据对象实例
                        Row dataRow = workbook.getSheet(sheetName).createRow(rowIndex);
                        for (int columnIndex = 0, len = titleColumn.length; columnIndex < len; columnIndex++) {
//                            String title = (columnIndex & 1) == 1 ? titleColumn[1] : titleColumn[0];
                            String title = titleColumn[columnIndex].trim();
                            if (StringUtils.isNotBlank(title) && !"null".equals(title)) {
                                String data;
                                String returnType;
                                if (obj instanceof Map) {
                                    data = ((Map) obj).get(title) + "";
                                    returnType = "String";
                                } else {
                                    // 首字母大写
                                    String UTitle = Character.toUpperCase(title.charAt(0)) + title.substring(1, title.length());
                                    String methodName = "get" + UTitle;
                                    // 设置要执行的方法
                                    Method method = clazz.getDeclaredMethod(methodName);
                                    // 获取返回类型
                                    returnType = method.getReturnType().getName();
                                    data = method.invoke(obj) == null ? "" : method.invoke(obj).toString();
                                }
                                Cell cell = dataRow.createCell(columnIndex);
                                if (StringUtils.isNotBlank(data) && !"null".equals(data)) {
                                    if ("int".equals(returnType)) {
                                        cell.setCellValue(Integer.parseInt(data));
                                    } else if ("long".equals(returnType)) {
                                        cell.setCellValue(Long.parseLong(data));
                                    } else if ("float".equals(returnType)) {
                                        cell.setCellValue(floatDecimalFormat.format(Float.parseFloat(data)));
                                    } else if ("double".equals(returnType)) {
                                        cell.setCellValue(doubleDecimalFormat.format(Double.parseDouble(data)));
                                    } else {
                                        cell.setCellValue(data);
                                    }
                                }
                            } else {// 字段为空, 检查该列是否为公式
                                if (colFormula != null) {
                                    String sixBuf = colFormula[columnIndex].replace("@", (rowIndex + 1) + "");
                                    Cell cell = dataRow.createCell(columnIndex);
                                    cell.setCellFormula(sixBuf);
                                }
                            }
                        }
                    }
                }
            }
            workbook.write(os);
        } catch (Exception e) {
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

    /**
     * 将16进制的颜色代码写入样式中来设置颜色
     *
     * @param style 保证style统一
     * @param color 颜色: 66FFDD
     * @param index 索引 8-64 使用时不可重复
     * @return CellStyle
     */
    private CellStyle setColor(CellStyle style, String color, short index) {
        if (StringUtils.isNotBlank(color) && !"null".equals(color)) {
            // 转为RGB码
            int r = Integer.parseInt(color.substring(0, 2), 16);// 转为16进制
            int g = Integer.parseInt(color.substring(2, 4), 16);
            int b = Integer.parseInt(color.substring(4, 6), 16);
            // 自定义cell颜色
//            HSSFPalette palette = workbook.getCustomPalette();
//            palette.setColorAtIndex(index, (byte) r, (byte) g, (byte) b);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(index);
        }
        return style;
    }

    /**
     * 设置字体并添加外边框
     *
     * @param fontStyle 字体样式
     * @param fontName  字体名
     * @param fontSize  字体大小
     * @return CellStyle
     */
    private CellStyle setFontAndBorder(CellStyle fontStyle, String fontName, short fontSize) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setFontName(fontName);
//        font.setBold(true);// 加粗
        fontStyle.setFont(font);
        fontStyle.setBorderTop(BorderStyle.THIN);// 上边框
        fontStyle.setBorderBottom(BorderStyle.THIN);// 下边框
        fontStyle.setBorderLeft(BorderStyle.THIN);// 左边框
        fontStyle.setBorderRight(BorderStyle.THIN);// 右边框
        return fontStyle;
    }

}
