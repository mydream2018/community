package com.code.community.mapper;

import com.code.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
@Mapper
public interface QuestionMapper {
    @Insert("insert into question (title, description,gmt_create,gmt_modified,creator,tag) " +
            "values (#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    public void create(Question question);

    @Select("select * from question limit #{offset}, #{size}")
    List<Question> list(@RequestParam("offset") Integer offset,@RequestParam("size") Integer size);

    @Select("select count(1) from question")
    public Integer count();

    @Select("select * from question where creator = #{id} limit #{offset}, #{size}")
    List<Question> listByUserId(@RequestParam("id") Integer id, @RequestParam("offset") Integer offset, @RequestParam("size")Integer size);

    //根据id查询个人的问题
    @Select("select count(1) from question where creator = #{userId}")
    Integer countByUserId(@RequestParam("userId")Integer userId);
}
