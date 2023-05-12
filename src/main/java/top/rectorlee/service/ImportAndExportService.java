package top.rectorlee.service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ImportAndExportService {

    void export(HttpServletResponse response) throws Exception;

    Map<String, Object> importDBFromExcel10w(List<Map<Integer, String>> dataList);
}
