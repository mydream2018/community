package com.code.community.mapper;

import com.code.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {
    @Insert("insert into user (name, account_id, token, gmt_create, gmt_modified, bio) values (#{name}, " +
            "#{accountId}, #{token}, #{gmtCreate}, #{gmtModified}, #{bio})")
    public void insert(User user);

    @Select("select * from user where token = #{token}")
    User findByToken(String token);
}
