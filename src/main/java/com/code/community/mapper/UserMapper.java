package com.code.community.mapper;

import com.code.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {
    @Insert("insert into user (name, account_id, token, gmt_create, gmt_modified) values (#{name}, " +
            "#{accountId}, #{token}, #{gmtCreate}, #{gmtModified})")
    public void insert(User user);
}
