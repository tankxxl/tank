package com.thinkgem.jeesite.common.jwt;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class TokenController {

    // @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    // public String login(String account) {
    //     User user = userService.login(account);
    //
    //     DTO dto = new DTO();
    //     if (user == null) {
    //         dto.code = "-1";
    //         dto.msg = "Have not registered";
    //     } else {
    //         // 把用户登录信息放进Session
    //         Map<String, Object> loginInfo = new HashMap<>();
    //         loginInfo.put("userId", user.getId());
    //         String sessionId = JavaWebToken.createJavaWebToken(loginInfo);
    //         System.out.println("sessionId=" + sessionId);
    //         dto.data = sessionId;
    //     }
    //     return JSON.toJSONString(dto);
    // }

    //修改昵称
    // @RequestMapping(value = "/updateName", method = {RequestMethod.GET, RequestMethod.POST})
    // public String updateName(HttpServletRequest request, String name) {
    //     DTO dto = new DTO();
    //     try {
    //         //从session拿到token，再解密得到userid
    //         Long userId = AuthUtil.getUserId(request);
    //         boolean userIsExist = userService.updateName(userId, name);
    //         if (userIsExist == false) {
    //             dto.code = "-1";
    //             dto.msg = "Have not updateAvatar";
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return JSON.toJSONString(dto);
    // }
}
