package com.thinkgem.jeesite.modules.api;

import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 *
 To Create a resource : HTTP POST should be used
 To Retrieve a resource : HTTP GET should be used
 To Update a resource : HTTP PUT should be used
 To Delete a resource : HTTP DELETE should be used


 ==========================
 GET request to /api/user/ returns a list of users
 GET request to /api/user/1 returns the user with ID 1
 POST request to /api/user/ with a user object as JSON creates a new user
 PUT request to /api/user/3 with a user object as JSON updates the user with ID 3
 DELETE request to /api/user/4 deletes the user with ID 4
 DELETE request to /api/user/ deletes all the users


 * Created by rgz on 28/04/2017.
 */
// @CrossOrigin
// @RestController
// @RequestMapping(value = "${apiPath}/greeting")
public class GreetingCotroller {

    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    @Autowired
    public SystemService systemService;

    // @CrossOrigin()
    @RequestMapping(value = "/greeting", method = RequestMethod.GET, headers = "Accept=application/json")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET, headers = "Accept=application/json")
    public Greeting hello(
            @RequestHeader(value = "Accept") String accept,
            @RequestHeader(value = "Accept-Language") String acceptLanguage,
            @RequestHeader(value = "User-Agent", defaultValue = "foo") String userAgent,
            HttpServletResponse response
    ) {
        System.out.println("accept: " + accept);
        System.out.println("acceptLanguage: " + acceptLanguage);
        System.out.println("userAgent: " + userAgent);

        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    public User getUser(@PathVariable int id) {
        User user = UserUtils.getByLoginName("rengangzai");

        return user;
    }

    @RequestMapping(value = "/getUsers", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<User> getUsers() {
        List<User> users = systemService.findUser(new User());
        // 只返回VO对象
        return users;
    }


    @RequestMapping("/myUser")
    public HttpEntity<User> getMyUser() {
        User user = new User();
        // 返回VO和Http状态码
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }




}
