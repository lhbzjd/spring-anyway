package ink.anyway.component.web.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import ink.anyway.component.web.pojo.LoginUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;

/**
 * [添加说明]
 * <br>@author : 李海博
 * <br>@date : 16-8-9 下午9:04
 * <br>@version : 1.0
 */
public class BaseAction {

    protected final Log logger = LogFactory.getLog(this.getClass());

    protected ModelAndView getJackson2JsonModelAndView(){
        MappingJackson2JsonView mappingJackson2JsonView = new MappingJackson2JsonView();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mappingJackson2JsonView.setObjectMapper(objectMapper);
        ModelAndView mav = new ModelAndView(mappingJackson2JsonView);
        return mav;
    }

    protected LoginUser getOnLineUser(HttpServletRequest request){
        SecurityContext securityContext = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        if(securityContext==null||securityContext.getAuthentication()==null)
            return null;
        LoginUser onLineUser = (LoginUser) securityContext.getAuthentication().getPrincipal();
        return onLineUser;
    }

}
