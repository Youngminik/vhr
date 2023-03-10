package com.example.vhr.mapper;

import com.example.vhr.model.Employeeremove;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeremoveMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Employeeremove record);

    int insertSelective(Employeeremove record);

    Employeeremove selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Employeeremove record);

    int updateByPrimaryKey(Employeeremove record);
}