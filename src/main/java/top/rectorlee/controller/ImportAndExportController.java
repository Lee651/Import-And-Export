package top.rectorlee.controller;

import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.rectorlee.service.ImportAndExportService;
import top.rectorlee.util.EasyExcelGeneralDataListener;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Lee
 * @description 导入导出
 * @date 2023-05-12  13:06:02
 */
@Slf4j
@RestController
@RequestMapping("/excel")
public class ImportAndExportController {
    @Autowired
    private ImportAndExportService importAndExportService;

    /**
     * 多线程实现百万数据导出到excel中
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        importAndExportService.export(response);
    }

    /**
     * 多线程实现百万数据导入到mysql中
     */
    @GetMapping("/import")
    public void importExcel() {
        String fileName = "C:\\Users\\Lee\\Desktop\\down_1683878146934.xlsx";
        // 记录开始读取Excel时间,也是导入程序开始时间
        long startReadTime = System.currentTimeMillis();
        log.info("开始读取Excel的Sheet时间(包括导入数据过程): " + startReadTime + "ms");
        // 读取所有Sheet的数据.每次读完一个Sheet就会调用这个方法
        EasyExcel.read(fileName, new EasyExcelGeneralDataListener(importAndExportService)).doReadAll();
        long endReadTime = System.currentTimeMillis();
        log.info("数据导入结束, 耗时为: " + (endReadTime-startReadTime) + "ms");
    }
}
