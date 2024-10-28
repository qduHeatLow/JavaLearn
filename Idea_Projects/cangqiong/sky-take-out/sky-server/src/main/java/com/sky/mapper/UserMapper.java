package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据Openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid=#{openid}")
    User getByOpenid(String openid);

    /**
     * 插入新用户
     * @param user
     */
    @Insert("insert into user (openid, name, phone, sex, id_number, avatar, create_time) values " +
            "(#{openid},#{name},#{phone},#{sex},#{idNumber},#{avatar},#{createTime})")
    void insert(User user);

    /**
     * 根据用户Id获得用户名
     * @param userId
     * @return
     */
    @Select("select name from user where id = #{userId}")
    String getUserNameByUserId(Long userId);

    /**
     * 统计用户数
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
