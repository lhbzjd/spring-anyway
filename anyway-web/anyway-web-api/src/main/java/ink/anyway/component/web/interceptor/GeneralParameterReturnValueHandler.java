package ink.anyway.component.web.interceptor;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class GeneralParameterReturnValueHandler implements HandlerMethodReturnValueHandler {

    private String remoteStaticPath;
    private String portalWebTitle;

    public GeneralParameterReturnValueHandler(String remoteStaticPath, String portalWebTitle) {
        this.remoteStaticPath = remoteStaticPath;
        this.portalWebTitle = portalWebTitle;
    }

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return methodParameter.getParameterType() == String.class;
    }

    @Override
    public void handleReturnValue(Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
        nativeWebRequest.setAttribute("remoteStaticPath", remoteStaticPath, 1);
        nativeWebRequest.setAttribute("portalWebTitle", portalWebTitle, 1);
    }
}
