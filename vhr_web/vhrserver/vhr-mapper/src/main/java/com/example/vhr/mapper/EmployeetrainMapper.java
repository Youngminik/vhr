package com.example.vhr.mapper;

import com.example.vhr.model.Employeetrain;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeetrainMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Employeetrain record);

    int insertSelective(Employeetrain record);

    Employeetrain selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Employeetrain record);

    int updateByPrimaryKey(Employeetrain record);
}