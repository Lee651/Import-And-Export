package top.rectorlee.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.rectorlee.entity.User;
import top.rectorlee.mapper.ImportAndExportMapper;
import top.rectorlee.service.ImportAndExportService;
import top.rectorlee.util.JDBCDruidUtils;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Lee
 * @description
 * @date 2023-05-12  13:08:18
 */
@Slf4j
@Service
public class ImportAndExportServiceImpl implements ImportAndExportService {
    @Autowired
    private ImportAndExportMapper importAndExportMapper;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private DataSource dataSource;

    @Override
    public void export(HttpServletResponse response) throws Exception {
        log.info("多线程开始执行导出");
        OutputStream outputStream = response.getOutputStream();
        long start = System.currentTimeMillis();
        // 设置响应内容
        response.setContentType("application/vnd.ms-excel");
        // 防止下载的文件名字乱码
        response.setCharacterEncoding("UTF-8");
        try {
            // 文件以附件形式下载
            response.setHeader("Content-disposition", "attachment;filename=down_" + URLEncoder.encode(System.currentTimeMillis() + ".xlsx", "utf-8"));
            // 获取总数据量
            int count = importAndExportMapper.count();
            // 每页多少个
            int pageSize = 10000;
            // 必须放到循环外，否则会刷新流
            ExcelWriter excelWriter = EasyExcel.write(outputStream).build();
            List<CompletableFuture> completableFutures = new ArrayList<>();
            for (int i = 0; i < (count / pageSize) + 1; i++) {
                int finalI = i;
                CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                    List<User> exportList = importAndExportMapper.findLimit(finalI * pageSize, pageSize);
                    if (!CollectionUtils.isEmpty(exportList)) {
                        WriteSheet writeSheet = EasyExcel.writerSheet(finalI, "用户" + (finalI + 1)).head(User.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).build();
                        synchronized(excelWriter) {
                            excelWriter.write(exportList, writeSheet);
                        }
                    }
                }, threadPoolExecutor);
                completableFutures.add(completableFuture);
            }
            for (CompletableFuture completableFuture : completableFutures) {
                completableFuture.join();
            }
            // 刷新流
            excelWriter.finish();

            log.info("本次导出excel任务执行完毕,共消耗: " + (System.currentTimeMillis() - start) + "ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        outputStream.flush();
        response.getOutputStream().close();
    }

    @Override
    public Map<String, Object> importDBFromExcel10w(List<Map<Integer, String>> dataList) {
        HashMap<String, Object> result = new HashMap<>();
        // 结果集中数据为0时,结束方法.进行下一次调用
        if (dataList.size() == 0) {
            result.put("empty", "0000");
            return result;
        }
        // JDBC分批插入+事务操作完成对10w数据的插入
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long startTime = System.currentTimeMillis();
            log.info(dataList.size() + "条, 开始导入到数据库, 开始时间为: " + startTime + "ms");
            conn = dataSource.getConnection();
            // 控制事务:默认不提交
            conn.setAutoCommit(false);
            String sql = "insert into user (id, name, phone, ceate_by, remark, birthday) values (?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            // 循环结果集:这里循环不支持"烂布袋"表达式
            for (int i = 0; i < dataList.size(); i++) {
                Map<Integer, String> item = dataList.get(i);
                ps.setString(1, item.get(0));
                ps.setString(2, item.get(1));
                ps.setString(3, item.get(2));
                ps.setString(4, item.get(3));
                ps.setString(5, item.get(4));
                ps.setString(6, item.get(5));

                // 将一组参数添加到此PreparedStatement对象的批处理命令中
                ps.addBatch();
            }
            // 执行批处理
            ps.executeBatch();
            // 手动提交事务
            conn.commit();

            long endTime = System.currentTimeMillis();
            log.info(dataList.size() + "条, 结束导入到数据库, 时间为: " + endTime + "ms");
            log.info(dataList.size() + "条, 导入用时为: " + (endTime - startTime) + "ms");
            result.put("success", "1111");
        } catch (Exception e) {
            result.put("exception", "0000");
            e.printStackTrace();
        } finally {
            // 关连接
            JDBCDruidUtils.close(conn, ps);
        }
        return result;
    }
}
