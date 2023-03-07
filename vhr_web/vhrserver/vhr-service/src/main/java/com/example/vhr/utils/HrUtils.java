package com.example.vhr.utils;

import com.example.vhr.model.Hr;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/*获得自己的hr*/
public class HrUtils {
    public static Hr getCurrentHr(){
return ((Hr)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
