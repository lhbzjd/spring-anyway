package ink.anyway.component.web.filter;

import ink.anyway.component.web.pojo.LoginUser;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * [添加说明]
 * <br>@author : 李海博(haibo_li@neusoft.com)
 * <br>@date : 2018/1/11 10:44
 * <br>@version : 1.0
 */
public class SessionStatusFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LoginUser ou = null;
        SecurityContext securityContext = (SecurityContext) ((HttpServletRequest) servletRequest).getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        if(securityContext!=null&&securityContext.getAuthentication()!=null){
            ou = (LoginUser) securityContext.getAuthentication().getPrincipal();
        }

        if (ou == null)//判断session里是否有用户信息
        {
            if (((HttpServletRequest) servletRequest).getHeader("x-requested-with") != null && ((HttpServletRequest) servletRequest).getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest"))
            //如果是ajax请求响应头会有，x-requested-with；
            {
                ((HttpServletResponse) servletResponse).setHeader("sessionstatus", "timeout");//在响应头设置session状态
            }
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}
