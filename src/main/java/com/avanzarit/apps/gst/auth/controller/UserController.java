package com.avanzarit.apps.gst.auth.controller;

import com.avanzarit.apps.gst.Layout;
import com.avanzarit.apps.gst.auth.model.User;
import com.avanzarit.apps.gst.auth.model.UserStatusEnum;
import com.avanzarit.apps.gst.auth.repository.UserRepository;
import com.avanzarit.apps.gst.auth.service.SecurityService;
import com.avanzarit.apps.gst.auth.service.UserService;
import com.avanzarit.apps.gst.auth.validator.UserValidator;
import com.avanzarit.apps.gst.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * Created by SPADHI on 5/4/2017.
 */
@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private EmailService emailService;
    @Autowired
    SimpleMailMessage resetTokenMessage;


    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/add";
    }

    @Layout(value = "layouts/blank")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") != null) {
                model.addAttribute("error", "Your username and password is invalid.");
                session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            }
        }
        return "login";
    }

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String welcome(Model model) {
        UserDetails auth = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<? extends GrantedAuthority> authCollection = auth.getAuthorities();

        String userName = auth.getUsername();
        if (userName.equals("admin")) {
            return "redirect:/vendorListView";
        } else {
            return "redirect:/get";
        }
    }

    @RequestMapping(value = {"/updatePassword"}, method = RequestMethod.GET)
    public String updatePassword(Model model) {
        UserDetails auth = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = auth.getUsername();
        User user = new User();
        user.setUsername(userName);
        model.addAttribute("user", user);
        return "updatePassword";
    }

    @RequestMapping(value = {"/updatePassword"}, method = RequestMethod.POST)
    public String changePassword(RedirectAttributes redirectAttributes, @ModelAttribute("user") User user) {

        String password = user.getPassword();
        UserDetails auth = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = auth.getUsername();
        User repUser = userRepository.findByUsername(userName);
        repUser.setLastLoginDate(new Date());
        repUser.setUserStatus(UserStatusEnum.ACTIVE);
        repUser.setPassword(password);
        userService.save(repUser);
        securityService.autologin(user.getUsername(), password);
        redirectAttributes.addFlashAttribute("message", "Please login with the new Password");
        return "/logout";
    }

    @Layout(value = "layouts/blank")
    @RequestMapping(value = {"/resetPassword"}, method = RequestMethod.GET)
    public String loadResetPasswordPage(Model model) {

        return "resetPassword";
    }

    private String getContextPath(HttpServletRequest request) throws MalformedURLException {
        String context = "";
        URL url = new URL(request.getRequestURL().toString());
        String host = url.getHost();
        String scheme = url.getProtocol();
        int port = url.getPort();

        if (port == 80 || port == 0) {
            context = scheme + "://" + host;
        } else {
            context = scheme + "://" + host + ":" + port;
        }
        return context;
    }

    @Layout(value = "layouts/blank")
    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String showChangePasswordPage(RedirectAttributes redirectAttributes, @RequestParam("id") String id, @RequestParam("token") String token) {
        String result = securityService.validatePasswordResetToken(id, token);
        if (result != null) {
            redirectAttributes.addFlashAttribute("error", result);
            return "redirect:/login";
        }
        return "redirect:/updatePassword";
    }

    @RequestMapping(value = "/resetPassword",
            method = RequestMethod.POST)
    public String resetPassword(HttpServletRequest request, RedirectAttributes redirectAttributes,
                                @RequestParam("email") String userEmail) throws MalformedURLException {
        String contextPath = getContextPath(request);
        User user = userService.findByEmail(userEmail);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User with this e-mail ID does not exist, please enter a valid e-mail ID");
            return "redirect:/resetPassword";
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        redirectAttributes.addFlashAttribute("message", "We have sent you a mail on your registered E-mail ID with a link to reset your password");
        emailService.sendSimpleMessageUsingTemplate(user.getEmail(), "Reset Password", resetTokenMessage, contextPath, user.getUsername(), token);
        return "redirect:/login";
    }

    // for 403 access denied page
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public ModelAndView accesssDenied(Principal user) {

        ModelAndView model = new ModelAndView();

        if (user != null) {
            model.addObject("msg", "Hi " + user.getName()
                    + ", you do not have permission to access this page!");
        } else {
            model.addObject("msg",
                    "You do not have permission to access this page!");
        }

        model.setViewName("403");
        return model;

    }

    // for 403 access denied page
    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public ModelAndView pageNotFound() {
        ModelAndView model = new ModelAndView();
        model.setViewName("404");
        return model;
    }

}