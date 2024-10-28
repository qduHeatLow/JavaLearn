package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.*;
import java.lang.reflect.Type;

/**
 * 自定义注解，自动填充公共字段 Mapper方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操作类型：UPDATE、INSERT
    OperationType value();
}
