package com.example.vhr.mapper;

import com.example.vhr.model.SysMsg;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysMsgMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysMsg record);

    int insertSelective(SysMsg record);

    SysMsg selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysMsg record);

    int updateByPrimaryKey(SysMsg record);
}