package ink.anyway.component.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * [添加说明]
 * <br>@author : 李海博(haibo_li@neusoft.com)
 * <br>@date : 2018/1/11 10:44
 * <br>@version : 1.0
 */
public class AllowOriginFilter implements Filter {

    private String allowOrigin="*";
    private String allowMethods="*";
    private String allowHeaders="Origin, X-Requested-With, Content-Type, Authorization, Accept, Connection, User-Agent, Cookie";
    private String allowCredentials="true";

    public AllowOriginFilter() {
    }

    public AllowOriginFilter(String allowOrigin, String allowMethods, String allowHeaders, String allowCredentials) {
        if(allowOrigin!=null&&!"".equals(allowOrigin.trim())){
            this.allowOrigin = allowOrigin.trim();
        }

        if(allowMethods!=null&&!"".equals(allowMethods.trim())){
            this.allowMethods = allowMethods.trim();
        }

        if(allowHeaders!=null&&!"".equals(allowHeaders.trim())){
            this.allowHeaders = allowHeaders.trim();
        }

        if(allowCredentials!=null&&!"".equals(allowCredentials.trim())){
            this.allowCredentials = allowCredentials.trim();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ((HttpServletResponse)servletResponse).setHeader("Access-Control-Allow-Origin", allowOrigin);
        ((HttpServletResponse)servletResponse).setHeader("Access-Control-Allow-Methods", allowMethods);
        ((HttpServletResponse)servletResponse).setHeader("Access-Control-Allow-Headers", allowHeaders);
        ((HttpServletResponse)servletResponse).setHeader("Access-Control-Allow-Credentials", allowCredentials);
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}
