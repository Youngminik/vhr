package com.example.vhr.mapper;

import com.example.vhr.model.Hr;
import com.example.vhr.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HrMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Hr record);

    int insertSelective(Hr record);

    Hr selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Hr record);

    int updateByPrimaryKey(Hr record);

    Hr loadUserByUsername(String username);

    List<Role> getHrRolesById(Integer id);

    List<Hr> getAllHrs(@Param("hrid") Integer hrid,@Param("keywords") String keywords);

    List<Hr> getAllHrsExceptCurrentHr( Integer id);
    /*获取其他hr权限时，要把自己的信息除去*/
//    更新用户状态为在线
    void setUserStateToOn(Integer id);

//更新用户状态为离线
    void setUserStateToLeave(Integer id);
}