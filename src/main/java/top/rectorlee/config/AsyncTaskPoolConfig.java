package top.rectorlee.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import top.rectorlee.util.NamedThreadFactory;

import java.util.concurrent.*;

/**
 * @author Lee
 * @description
 * @date 2023-05-12  15:34:08
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncTaskPoolConfig {
    @Bean("taskExecutor")
    public ThreadPoolExecutor taskExecutor() {
        int i = Runtime.getRuntime().availableProcessors();
        log.info("系统最大线程数：" + i);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(i, i+1, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>(200),new NamedThreadFactory("excel导出线程池"));
        log.info("excel导出线程池初始化完毕");
        return threadPoolExecutor;
    }
}
