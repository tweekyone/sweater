package com.example.sweater.controllers;

import com.example.sweater.domain.User;
import com.example.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

//наследуется от Component, имеет дополнительные возможности
@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    //объект User создается спрингом из полей формы (по их названию)
    @PostMapping("/registration")
    public String addUser(
            @RequestParam("password2") String passwordConfirmation,
            @Valid @ModelAttribute User user,
            BindingResult bindingResult,
            Model model){
        boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirmation);

        if(isConfirmEmpty){
            model.addAttribute("password2Error", "Password confirmation cannot be empty");
        }

        if(user.getPassword() != null && !user.getPassword().equals(passwordConfirmation)){
            model.addAttribute("passwordError", "Passwords are different");
            return "registration";
        }

        if(isConfirmEmpty || bindingResult.hasErrors()){
            Map<String, String> errorMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorMap);
            model.addAttribute("user", user);
            return "registration";
        }

        if(!userService.addUser(user)) {
            model.addAttribute("userError", "User exists!");
            return "registration";
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);

        if(isActivated){
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code is not found!");
        }

        return"login";
    }
}
