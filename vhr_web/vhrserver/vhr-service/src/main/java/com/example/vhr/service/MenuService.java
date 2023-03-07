package com.example.vhr.service;

/*import com.example.vhr.config.SercurityConfig;*/
import com.example.vhr.mapper.MenuMapper;
import com.example.vhr.mapper.MenuRoleMapper;
import com.example.vhr.model.Hr;
import com.example.vhr.model.Menu;
import com.example.vhr.model.MenuRole;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {
    @Autowired
    MenuMapper menuMapper;
    @Autowired
    MenuRoleMapper menuRoleMapper;
    public List<Menu> getMenusByHrId() {
        return menuMapper.getMenusByHrId(((Hr)SecurityContextHolder.getContext().getAuthentication().getPrincipal()
         ).getId());
    }
    /**
    * 该方法每次使用时的sql调用方法其实不会变化太大
     * 所以此处可以添加使用缓存的注解
     * */
  //  @Cacheable
    public List<Menu> getAllMenusWithRole(){
        return menuMapper.getAllMenusWithRole();
    }

    public List<Menu> getAllMenus() {
        return menuMapper.getAllMenus();
    }

    public List<Integer> getMidsByRid(Integer rid) {
        return menuMapper.getMidsByRid(rid);
    }
//    更新操作中取消选中该框即删除，选中该框即插入
@Transactional
    public boolean updateMenuRole(Integer rid, Integer[] mids) {
        menuRoleMapper.deleteByRid(rid);
        if(mids==null||mids.length==0){
            return true;
        }
        Integer result=menuRoleMapper.insertRecord(rid,mids);

        return result==mids.length;
    }
}
