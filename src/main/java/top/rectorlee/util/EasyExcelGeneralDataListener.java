package top.rectorlee.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import top.rectorlee.service.ImportAndExportService;

import java.util.*;

/**
 * @author Lee
 * @description
 * @date 2023-05-12  16:02:45
 */
public class EasyExcelGeneralDataListener extends AnalysisEventListener<Map<Integer, String>> {
    /**
     * 处理业务逻辑的Service,也可以是Mapper
     */
    private ImportAndExportService importAndExportService;

    /**
     * 用于存储读取的数据
     */
    private List<Map<Integer, String>> dataList = new ArrayList<Map<Integer, String>>();

    public EasyExcelGeneralDataListener() {
    }

    public EasyExcelGeneralDataListener(ImportAndExportService importAndExportService) {
        this.importAndExportService = importAndExportService;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        // 数据add进入集合
        dataList.add(data);
        // size是否为200000条:这里其实就是分批.当数据等于20w的时候执行一次插入
        if (dataList.size() >= 200000) {
            // 存入数据库:数据小于1w条使用Mybatis的批量插入即可;
            saveData();
            // 清理集合便于GC回收
            dataList.clear();
        }
    }

    /**
     * 保存数据到DB
     */
    private void saveData() {
        importAndExportService.importDBFromExcel10w(dataList);
        dataList.clear();
    }

    /**
     * Excel中所有数据解析完毕会调用此方法
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        dataList.clear();
    }
}
