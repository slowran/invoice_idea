package org.invoice.action;

import org.apache.log4j.Logger;
import org.invoice.model.Authority;
import org.invoice.model.User;
import org.invoice.security.HashUtil;
import org.invoice.utils.CaptchaUtil;
import org.invoice.service.UserService;
import org.invoice.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


/**
 * 用户控制器
 * Created by 李浩然 on 2017/4/8.
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    private static final Logger logger = Logger.getLogger(UserController.class);

    @RequestMapping(value = "/no_login")
    public String noLogin() {
        return "no_login";
    }


    @RequestMapping(value = "/login", name = "登录", method = RequestMethod.GET)
    public ModelAndView inputLoginInformation(HttpSession session){
        logger.info("input Login Information");
        ModelAndView modelAndView = new ModelAndView("login");
        if (session.getAttribute(SessionContext.ATTR_USER_ID) != null) {
            userService.logout(Integer.parseInt(session.getAttribute(SessionContext.ATTR_USER_ID).toString()), session);
        }
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @RequestMapping(value = "/login", name = "登录", method = RequestMethod.POST)
    public ModelAndView validateLoginInformation(@ModelAttribute("user") User user,
                                                 BindingResult bindingResult, HttpSession session,
                                                 RedirectAttributes redirectAttributes,
                                                 @RequestParam("captcha") String captcha) {
        ModelAndView modelAndView = new ModelAndView();
        logger.info("validate Login Information");
        if (session.getAttribute("randomString") == null ||
                !session.getAttribute("randomString").toString().toLowerCase().equals(captcha.toLowerCase())) {
            modelAndView.setViewName("login");
            return modelAndView.addObject("has_error", true)
                    .addObject("error_message", "验证码错误！");
        }
        if (!userService.login(user, bindingResult, session)) {
            FieldError fieldError = bindingResult.getFieldError();
            logger.info("Code:" + fieldError.getCode() + ", field" + fieldError.getField());
            modelAndView.setViewName("login");
            return modelAndView.addObject("has_error", true)
                    .addObject("error_message", "用户名或密码错误！");
        } else {
            redirectAttributes.addFlashAttribute("message", "Login successful!");
            redirectAttributes.addFlashAttribute("user", userService.findUserByUserName(user.getUsername()));
            modelAndView.setViewName("redirect:/main");
            return modelAndView;
        }
    }

    @RequestMapping(value = "/logout", name = "注销", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpSession session) {
        userService.logout(Integer.parseInt(session.getAttribute(SessionContext.ATTR_USER_ID).toString()), session);
        return "redirect:/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register(@ModelAttribute("user") User user) {
        logger.info("input register information");
        ModelAndView modelAndView = new ModelAndView("signin");
        if (user == null) {
            logger.info("user is null");
            modelAndView.addObject("user", new User());
        } else {
            modelAndView.addObject("user", user);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(@ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           @RequestParam("captcha")  String captcha,
                           HttpSession session) {
        logger.info("validate register information");
        ModelAndView modelAndView = new ModelAndView();
        if (session.getAttribute("randomString") == null ||
                !session.getAttribute("randomString").toString().toLowerCase().equals(captcha.toLowerCase())) {
            modelAndView.setViewName("signin");
            return modelAndView.addObject("has_error", true)
                    .addObject("error_message", "验证码错误！");
        }
        if (!userService.register(user, bindingResult)) {
            FieldError fieldError = bindingResult.getFieldError();
            logger.info("Code:" + fieldError.getCode() + ", field" + fieldError.getField());
            modelAndView.setViewName("signin");
            return modelAndView.addObject("has_error", false)
                    .addObject("error_message", "");
        } else {
            redirectAttributes.addFlashAttribute("message", "Login successful!");
            modelAndView.setViewName("redirect:/login");
            return modelAndView.addObject("has_error", false)
                    .addObject("error_message", "");
        }
    }

    @RequestMapping(value = "/user_manage", method = RequestMethod.GET)
    public ModelAndView userManage(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("user_manage");
        int userId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        User user = userService.findUserByUserId(userId);
        modelAndView.addObject("display_name", user.getName());
        if ((user.getAuthority() & Authority.AUTHORITY_MANAGE_USER) == 0) { // 验证管理用户的的权限
            modelAndView.addObject("has_authority", false);
            return modelAndView;
        }
        List<User> userList = userService.getAllUserList(userId);
        modelAndView.addObject("user", null);
        modelAndView.addObject("edit_user", false);
        modelAndView.addObject("index", -1);
        modelAndView.addObject("user_list", userList);
        modelAndView.addObject("has_authority", true);
        return modelAndView;
    }

    @RequestMapping(value = "/user_manage", method = RequestMethod.POST)
    public ModelAndView userManage(HttpSession session, @RequestParam("index") int index,
                                   @RequestParam("user_id") int editUserId) {
        ModelAndView modelAndView = new ModelAndView("user_manage");
        int userId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        User user = userService.findUserByUserId(userId);
        if ((user.getAuthority() & Authority.AUTHORITY_MANAGE_USER) == 0) { // 验证管理用户的的权限
            modelAndView.addObject("has_authority", false);
            modelAndView.addObject("display_name", user.getName());
            return modelAndView;
        }
        List<User> userList = userService.getAllUserList(userId);
        User editUser = null;
        for (User u : userList) {
            if (u.getUserId() == editUserId) {
                editUser = u;
                break;
            }
        }
        modelAndView.addObject("display_name", user.getName());
        modelAndView.addObject("user", editUser);
        modelAndView.addObject("edit_user", editUser != null);
        modelAndView.addObject("index", index);
        modelAndView.addObject("user_list", userList);
        modelAndView.addObject("has_authority", true);
        return modelAndView;
    }


    @RequestMapping(value = "/modify_authority", method = RequestMethod.POST)
    public ModelAndView modifyAuthority(@RequestParam("index") int index, @RequestParam("user_id") int editUserId,
                                        HttpSession session, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("redirect:/user_manage");
        int userId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        User user = userService.findUserByUserId(userId);
        if ((user.getAuthority() & Authority.AUTHORITY_MANAGE_USER) == 0) { // 验证管理用户的的权限
            modelAndView.addObject("has_authority", false);
            return modelAndView;
        }
        int authority = 0;
        List<User> userList = userService.getAllUserList(userId);
        User editUser = null;
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId() == editUserId) {
                editUser = userList.get(i);
                break;
            }
        }
        String[] authorityStr = request.getParameterValues("authority");
        if (editUser != null) {
            editUser.setAuthority(0);
            for (String str : authorityStr) {
                logger.info(str);
                editUser.addAuthority(Authority.authorityMap.get(str));
            }
            if (editUser.getUsername().equals("admin"))
                editUser.addAuthority(Authority.AUTHORITY_MANAGE_USER);

            User tmp = userService.findUserByUserId(editUserId);
            if (tmp != null) {
                tmp.setAuthority(editUser.getAuthority());
            }
            logger.info("successful: " + userService.modifyUserInfo(editUser, null));
        }
        return modelAndView;
    }


    @RequestMapping(value = "/main", name = "主页", method = RequestMethod.GET)
    public ModelAndView loginSuccessfulAndTurnToMainPage(HttpSession session) {
        int userId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        User user = userService.findUserByUserId(userId);
        return new ModelAndView("main")
                .addObject("user", user)
                .addObject("display_name", user.getName())
                .addObject("auth", user.getAuthorityMap())
                .addObject("edit_password", false)
                .addObject("has_message", false);
    }

    @RequestMapping(value = "/main", name = "主页", method = RequestMethod.POST)
    public ModelAndView modifyPassword(HttpSession session) {
        int userId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        User user = userService.findUserByUserId(userId);
        return new ModelAndView("main")
                .addObject("user", user)
                .addObject("display_name", user.getName())
                .addObject("auth", user.getAuthorityMap())
                .addObject("edit_password", true)
                .addObject("has_message", false);
    }

    @RequestMapping(value = "modify_password", method = RequestMethod.POST)
    public ModelAndView modifyPassword(HttpSession session,
                                       @RequestParam("old_password") String oldPassword,
                                       @RequestParam("new_password") String newPassword,
                                       @RequestParam("confirm_password") String confirmPassword,
                                       @RequestParam("captcha") String captcha) {
        int userId = Integer.parseInt(session.getAttribute(SessionContext.ATTR_USER_ID).toString());
        User user = userService.findUserByUserId(userId);
        ModelAndView modelAndView = new ModelAndView("main")
                .addObject("user", user)
                .addObject("display_name", user.getName())
                .addObject("auth", user.getAuthorityMap())
                .addObject("has_error", true);
        if (session.getAttribute("randomString") == null ||
                !session.getAttribute("randomString").toString().toLowerCase().equals(captcha.toLowerCase())) {
            return modelAndView.addObject("has_error", true)
                    .addObject("error_message", "验证码错误！")
                    .addObject("edit_password", true);
        }
        if (!HashUtil.verify(oldPassword, userService.findUserByUserNameFromDB(user.getUsername()).getPassword())) {
            modelAndView.addObject("error_message", "原始密码错误!")
                    .addObject("edit_password", true);
        } else if (newPassword.length() < 8) {
            modelAndView.addObject("error_message", "新密码长度必须大于8!")
                    .addObject("edit_password", true);
        } else if (!newPassword.equals(confirmPassword)) {
            modelAndView.addObject("error_message", "两次密码输入不一致!")
                    .addObject("edit_password", true);
        } else if (userService.modifyUserPassword(userId, newPassword, null)){
            modelAndView.addObject("error_message", "修改密码成功!")
                    .addObject("edit_password", false);
        } else {
            modelAndView.addObject("error_message", "修改密码失败!")
                    .addObject("edit_password", true);
        }
        return modelAndView;
    }

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    @ResponseBody
    public void captcha(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CaptchaUtil.outputCaptcha(request, response);
    }
}
