package com.example.vhr.mapper;

import com.example.vhr.model.Position;
import com.example.vhr.model.RespBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PositionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Position record);

    int insertSelective(Position record);

    Position selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Position record);

    int updateByPrimaryKey(Position record);

    List<Position> getAllPositions();

    RespBean addPosition();

    //给数组加上注解
    Integer deletePositionByIds(@Param("ids") Integer[] ids);
}