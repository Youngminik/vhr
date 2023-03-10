package com.example.vhr.mapper;

import com.example.vhr.model.Employeeec;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeecMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Employeeec record);

    int insertSelective(Employeeec record);

    Employeeec selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Employeeec record);

    int updateByPrimaryKey(Employeeec record);
}