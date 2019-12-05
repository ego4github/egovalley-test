package com.egovalley.web;

import com.egovalley.utils.ExcelExportUtils;
import com.egovalley.utils.ExcelImportUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/excel")
@SuppressWarnings("unchecked")
public class ExcelController {

    private static final Logger logger = LoggerFactory.getLogger(ExcelController.class);

    @Value("${phone.ownership.url}")
    private String phoneOwnershipUrl;
    @Value("${phone.ownership.fileDir}")
    private String fileDir;
    @Value("${phone.ownership.suffix}")
    private String fileSuffix;

    @RequestMapping("/import")
    @ResponseBody
    public Map<String, Object> excelImportTest(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
        logger.info(">>> name = " + name + "; file = " + file);
        Map<String, Object> resultMap = new HashMap<>();
        if (file == null || file.isEmpty()) {
            resultMap.put("resCode", "301");
            resultMap.put("resMsg", "文件缺失, 请检查后重新上传");
            return resultMap;
        }
//        Workbook wb = ExcelImportUtils.importExcel("E:\\lky\\test.xlsx");
        Workbook wb = ExcelImportUtils.importExcel(file);
        if (wb != null) {
            List<String> list = new ArrayList<>();
            Sheet sheet = wb.getSheetAt(0);// 只读第一个sheet
            Row row;
            int rowNum = sheet.getPhysicalNumberOfRows();// 获取最大行数
            for (int i = 1; i < rowNum; i++) {// 第一行是抬头, 从第二行开始读
                row = sheet.getRow(i);
                if (row != null) {
                    int colNum = row.getPhysicalNumberOfCells();// 获取最大列数
                    if (colNum == 1) {
                        list.add(ExcelImportUtils.getCellValue(row.getCell(0)));
                    } else {
                        resultMap.put("resCode", "303");
                        resultMap.put("resMsg", "上传文件有误, 请检查后重试");
                        return resultMap;
                    }
                } else {
                    resultMap.put("resCode", "304");
                    resultMap.put("resMsg", "上传文件有误, 请检查后重试");
                    return resultMap;
                }
            }
            // 号码归属地过滤
            logger.info(">>> excel过滤号码集: " + list);
            Map<String, Object> filterMap = new HashMap<>();
            filterMap.put("telList", list);
            try {
                /*String resStr = HttpClientUtils.sendPostRequestByJson(phoneOwnershipUrl, filterMap, false);
                Map<String, Object> resMap = JsonUtils.jsonToMap(resStr);
                logger.info(">>> 过滤结果: " + resMap);*/

                // 模拟
                Map<String, Object> resMap = new HashMap<>();
                resMap.put("resCode", "200");
                Map<String, String> p1 = new HashMap<>();
                p1.put("telephone", "18800298711");
                p1.put("ownership", "上海-01");
                Map<String, String> p2 = new HashMap<>();
                p2.put("telephone", "18800298712");
                p2.put("ownership", "上海-02");
                Map<String, String> p3 = new HashMap<>();
                p3.put("telephone", "18800298713");
                p3.put("ownership", "上海-03");
                List<Map<String, String>> lists = new ArrayList<>();
                lists.add(p1);
                lists.add(p2);
                lists.add(p3);
                resMap.put("resultList", lists);
                logger.info(">>> 过滤结果: " + resMap);

                if ("200".equals(resMap.get("resCode"))) {
                    ExcelExportUtils export = new ExcelExportUtils(fileDir + "测试" + fileSuffix, "号码归属地");
                    List<Map<String, String>> resultList = (List<Map<String, String>>) resMap.get("resultList");
                    String[] titleName = {"电话号码", "归属地"};
                    String[] titleColumn = {"telephone", "ownership"};
                    int[] titleSize = {20, 20};
                    export.exportExcel(titleColumn, titleName, titleSize, resultList);
                    resultMap.put("resCode", "200");
                } else {
                    logger.info(">>> 号码归属地过滤失败");
                    resultMap.put("resCode", "400");
                    resultMap.put("resMsg", "号码归属地过滤失败, 请稍后再试");
                }
            } catch (Exception e) {
                logger.error(">>> 号码归属地过滤异常", e);
                resultMap.put("resCode", "500");
                resultMap.put("resMsg", "系统正忙, 请稍后再试");
            }
        } else {
            resultMap.put("resCode", "302");
            resultMap.put("resMsg", "上传文件有误, 请检查后重试");
        }
        return resultMap;
    }

}
