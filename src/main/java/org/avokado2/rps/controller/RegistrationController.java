package org.avokado2.rps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.avokado2.rps.maneger.PlayerManager;
import org.avokado2.rps.maneger.PlayerUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Controller
public class RegistrationController {
    private PlayerUserDetailsManager playerUserDetailsManager;

    private final PlayerManager playerManager;
    @RequestMapping(value = "/registration2", method = RequestMethod.GET)
    public ModelAndView openRegister() {
        return new ModelAndView("registration2");
    }
    @RequestMapping(value = "/registration2", method = RequestMethod.POST)
    public ModelAndView register(@RequestParam(value = "login") String login,
                                 @RequestParam(value = "password")String password,
                                 @RequestParam(value = "confirmPassword") String confirmPassword) {
        log.info("Player registration2. Login: {}. Password: {}. Confirm password: {}", login, password, confirmPassword);
        Map model = new HashMap<>();
        if (password.isEmpty()){
            model.put("errorMessage", "password is not set");
            return new ModelAndView("registration2", model);
        }
        if (confirmPassword.isEmpty()){
            model.put("errorMessage", "confirm password is not set");
            return new ModelAndView("registration2", model);
        }
        if (!password.equals(confirmPassword)){
            model.put("errorMessage", "passwords don't match");
            return new ModelAndView("registration2", model);
        }
        if (login.isEmpty()){
            model.put("errorMessage", "login is not set");
            return new ModelAndView("registration2", model);
        }
        if (login.length() < 3 ){
            model.put("errorMessage", "login is too short");
            return new ModelAndView("registration2", model);
        }
        if (login.length() > 50 ){
            model.put("errorMessage", "login is too long");
            return new ModelAndView("registration2", model);
        }
        if (password.length() > 50 ){
            model.put("errorMessage", "password is too long");
            return new ModelAndView("registration2", model);
        }
        if (password.length() < 6 ){
            model.put("errorMessage", "password is too short");
            return new ModelAndView("registration2", model);
        }

        try {
            if (!playerManager.registerPlayer(login, password)){
                model.put("errorMessage", "the login is already taken");
                return new ModelAndView("registration2", model);
            }
        } catch (Exception e) {
            model.put("errorMessage", "error, please repeat");
            return new ModelAndView("registration2", model);
        }
        return new ModelAndView("redirect:/login");
    }

}
