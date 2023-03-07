package com.example.vhr.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {
    //这个configAttributes就是CustomFilterInvocationSecurityMetadataSource.java
    // 里面根据请求的Url获取到的所需对象
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {

            //如果用户自带的角色中有一个符合所需对象集合中的一个，那么就能访问请求的Url，如果不能则抛出异常
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                for (ConfigAttribute configAttribute:
                        configAttributes) {
                    String needRole = configAttribute.getAttribute();
                    if ("ROLE_LOGIN".equals(needRole)){
                        if(authentication instanceof AnonymousAuthenticationToken){
                            throw new AccessDeniedException("您还未登录，请先登录！");
                        }
                        return;
                    }
                if(authority.getAuthority().equals(needRole)){
                    return;
                }
            }
            throw new AccessDeniedException("权限不足，请联系管理员！");
        }


    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }
}
