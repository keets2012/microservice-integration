package com.blueskykong.demo.filter;

import com.blueskykong.demo.client.FeignAuthClient;
import com.blueskykong.demo.constants.AccessType;
import com.blueskykong.demo.constants.SecurityConstants;
import com.blueskykong.demo.entity.Permission;
import com.blueskykong.demo.security.CustomAuthentication;
import com.blueskykong.demo.security.SimpleGrantedAuthority;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author keets
 */
@Log
public class AuthorizationFilter implements Filter {

    @Autowired
    private FeignAuthClient feignAuthClient;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("初始化过滤器。");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Filter过滤器正在执行...");
        // pass the request along the filter chain
        String userId = ((HttpServletRequest) servletRequest).getHeader(SecurityConstants.USER_ID_IN_HEADER);

        if (StringUtils.isNotEmpty(userId)) {
            UserContext userContext = new UserContext(UUID.fromString(userId));
            userContext.setAccessType(AccessType.ACCESS_TYPE_NORMAL);

            List<Permission> permissionList = feignAuthClient.getUserPermissions(userId);
            List<SimpleGrantedAuthority> authorityList = new ArrayList();
            for (Permission permission : permissionList) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority();
                authority.setAuthority(permission.getPermission());
                authorityList.add(authority);
            }

            CustomAuthentication userAuth = new CustomAuthentication();
            userAuth.setAuthorities(authorityList);
            userContext.setAuthorities(authorityList);
            userContext.setAuthentication(userAuth);
            SecurityContextHolder.setContext(userContext);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
