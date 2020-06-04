package com.code.community.mapper;

import com.code.community.model.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface QuestionExtraMapper {
    int incView(Question record);
    int incCommentCount(Question record);
    List<Question> selectRelated(Question question);
    List<Question> list(@Param("offset")Integer offset, @Param("size")Integer size);
    List<Question> listByUserId(@Param("userId") Long userId,@Param("offset")Integer offset, @Param("size")Integer size);
}