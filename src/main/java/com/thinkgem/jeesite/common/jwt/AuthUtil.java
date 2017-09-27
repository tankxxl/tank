package com.thinkgem.jeesite.common.jwt;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 从request去拿session，并且解密session得到token，得到用户id的类
 *
 * 使用例子：
 * 登录的时候把信息放进session，存到map里，再交由JWT得到token保存起来
 *
 * rgz
 */
public class AuthUtil {

    private static Map<String, Object> getClientLoginInfo(HttpServletRequest request) throws Exception {
        Map<String, Object> r = new HashMap<String, Object>();
        String sessionId = request.getHeader("sessionId");
        if (sessionId != null) {
            r = decodeSession(sessionId);
            return r;
        }
        throw new Exception("session解析错误");
    }

    public static Long getUserId(HttpServletRequest request) throws Exception {
        return Long.valueOf((Integer)getClientLoginInfo(request).get("userId"));
    }

    /**
     * session解密
     * @param sessionId
     * @return
     */
    public static Map<String, Object> decodeSession(String sessionId) {
        try {
            return JavaWebToken.verifyJavaWebToken(sessionId);
        } catch (Exception e) {
            return null;
        }
    }
}
