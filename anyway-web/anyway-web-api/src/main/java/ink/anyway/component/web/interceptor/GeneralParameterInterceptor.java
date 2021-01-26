package ink.anyway.component.web.interceptor;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

public class GeneralParameterInterceptor implements WebRequestInterceptor {  /**  继承HandlerInterceptor能够提供比WebRequestInterceptor更多的功能，但是此处只对request进行操作，不需要返回值影响请求，使用简单的拦截器即可。 */

    private String remoteStaticPath;
    private String portalWebTitle;

    public GeneralParameterInterceptor(String remoteStaticPath, String portalWebTitle) {
        super();
        this.remoteStaticPath = remoteStaticPath;
        this.portalWebTitle = portalWebTitle;
    }

    @Override
    public void preHandle(WebRequest webRequest) throws Exception {
        webRequest.setAttribute("remoteStaticPath", remoteStaticPath, 0);
        webRequest.setAttribute("portalWebTitle", portalWebTitle, 0);
    }

    @Override
    public void postHandle(WebRequest webRequest, ModelMap modelMap) throws Exception {}

    @Override
    public void afterCompletion(WebRequest webRequest, Exception e) throws Exception {}
}
