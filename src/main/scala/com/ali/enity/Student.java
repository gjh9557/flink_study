package com.ali.enity;

import lombok.*;
//注意这里的lombock添加依赖后去主面板将ann，这个参数勾选，就是
//启用注解，然后添加插件
@Data
//@NoArgsConstructor
@AllArgsConstructor
public class Student {
private String name;
private Double score;
}
