package top.rectorlee.entity;

import lombok.*;

import java.util.Date;

/**
 * @author Lee
 * @description
 * @date 2023-05-12  13:12:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    private String name;

    private String phone;

    private String ceateBy;

    private String remark;

    private Date birthday;
}
