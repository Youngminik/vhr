package com.example.vhr.mapper;

import com.example.vhr.model.EmpSalary;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmpSalaryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EmpSalary record);

    int insertSelective(EmpSalary record);

    EmpSalary selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EmpSalary record);

    int updateByPrimaryKey(EmpSalary record);
}