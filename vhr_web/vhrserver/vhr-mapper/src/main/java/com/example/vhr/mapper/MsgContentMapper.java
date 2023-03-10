package com.example.vhr.mapper;

import com.example.vhr.model.MsgContent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MsgContentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MsgContent record);

    int insertSelective(MsgContent record);

    MsgContent selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MsgContent record);

    int updateByPrimaryKey(MsgContent record);
}