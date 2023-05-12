package top.rectorlee;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("top.rectorlee.mapper")
public class ImportAndExportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImportAndExportApplication.class, args);
    }
}
