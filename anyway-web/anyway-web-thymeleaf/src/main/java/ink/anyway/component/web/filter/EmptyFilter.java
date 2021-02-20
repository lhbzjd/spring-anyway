package ink.anyway.component.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EmptyFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
