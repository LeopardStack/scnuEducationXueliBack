package com.scnujxjy.backendpoint.util.filter;

import com.scnujxjy.backendpoint.constant.enums.announceMsg.AnnounceMsgUserTypeEnum;
import com.scnujxjy.backendpoint.model.ro.platform_message.AnnouncementMessageUsersRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.AnnouncementMsgUserFilterRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.ManagerRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.NewStudentRO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author leopard
 */
@Component
@Slf4j
public class UserFilterArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AnnouncementMessageUsersRO.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        AnnouncementMessageUsersRO announcementRO = new AnnouncementMessageUsersRO();
        String userType = webRequest.getParameter("userType");

        // 根据userType决定使用哪个子类实例
        AnnouncementMsgUserFilterRO filterRO = null;
        if (AnnounceMsgUserTypeEnum.MANAGER.getUserType().equalsIgnoreCase(userType)) {
            filterRO = new ManagerRO();
        } else if (AnnounceMsgUserTypeEnum.NEW_STUDENT.getUserType().equalsIgnoreCase(userType)) {
            filterRO = new NewStudentRO();
        } else if (AnnounceMsgUserTypeEnum.OLD_STUDENT.getUserType().equalsIgnoreCase(userType)) {
            filterRO = new NewStudentRO();
        }

        if (filterRO == null) {
            throw new IllegalArgumentException("Invalid userType");
        }

//        announcementRO.setAnnouncementMsgUserFilterRO(filterRO);
        return announcementRO;
    }
}
