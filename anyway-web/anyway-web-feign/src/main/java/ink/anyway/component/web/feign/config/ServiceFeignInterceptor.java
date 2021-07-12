package ink.anyway.component.web.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import ink.anyway.component.common.Constants;
import ink.anyway.component.common.pojo.SessionUser;
import ink.anyway.component.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Configuration
public class ServiceFeignInterceptor implements RequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void apply(RequestTemplate requestTemplate) {
        logger.debug("ServiceFeignInterceptor......");

        try {
//            requestTemplate.header(jwtMeta.getServiceHeader(), JWTHelper.generateServiceToken(serviceInfo,JWTConstants.JWT_TOKEN_EXPIRE));
//            requestTemplate.header(jwtMeta.getTokenHeader(), BaseContextHandler.getToken());
//            requestTemplate.header(CommonConstants.CONTEXT_KEY_TENANT_ID,BaseContextHandler.getTenantId());
//            requestTemplate.header(CommonConstants.CONTEXT_KEY_ORGANIZATION_ID,BaseContextHandler.getCurrentOrganizationId());
//            requestTemplate.header(CommonConstants.CONTEXT_KEY_FUN_ID,BaseContextHandler.getFunId());

            requestTemplate.header("Content-Type", "application/json;charset=UTF-8");
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if(attributes==null)
                return;

            HttpServletRequest request = attributes.getRequest();

            if(request==null)
                return;

            String token = this.getLocalToken(request);

            if(!StringUtil.isValid(token)){
                token = this.getCookieValue(request, "token");
            }

            if(!StringUtil.isValid(token)){
                token = request.getParameter(Constants.SSO_TOKEN_NAME);
            }

            if(!StringUtil.isValid(token)){
                token = (String) request.getAttribute(Constants.SSO_TOKEN_NAME);
            }

            requestTemplate.header("Cookie", "token="+token);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLocalToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session!=null&&session.getAttribute(Constants.SESSION_USER)!=null){
            SessionUser sessionUser = (SessionUser)session.getAttribute(Constants.SESSION_USER);
            if(sessionUser!=null&&StringUtil.isValid(sessionUser.getToken())){
                return sessionUser.getToken().trim();
            }
        }
        return null;
    }

    private static String getCookieValue(HttpServletRequest request, String cookieName){
        if(request.getCookies()!=null){
            for(Cookie cookie:request.getCookies()){
                if(cookie!=null&&StringUtil.isValid(cookie.getName())&&cookie.getName().equals(cookieName)){
                    if(StringUtil.isValid(cookie.getValue())){
                        return cookie.getValue().trim();
                    }
                }
            }
        }
        return null;
    }
}
