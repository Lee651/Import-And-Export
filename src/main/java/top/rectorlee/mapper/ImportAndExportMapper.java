package top.rectorlee.mapper;

import org.springframework.stereotype.Repository;
import top.rectorlee.entity.User;

import java.util.List;

@Repository
public interface ImportAndExportMapper {
    int count();

    List<User> findLimit(int index, int pageSize);
}
