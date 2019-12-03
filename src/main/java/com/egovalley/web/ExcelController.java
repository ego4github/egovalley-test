package com.egovalley.web;

import com.egovalley.utils.ExcelImportUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/excel")
public class ExcelController {

    private static final Logger logger = LoggerFactory.getLogger(ExcelController.class);

    @RequestMapping("/import")
    @ResponseBody
    public Map<String, Object> excelImportTest(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
        Workbook wb = ExcelImportUtils.importExcel("E:\\lky\\test.xlsx");
        if (wb != null) {
            System.out.println("好了");
        } else {
            System.out.println("坏了");
        }
        return null;
    }

}
