package com.example.sweater.controllers;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repos.UserRepo;
import com.example.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//наследуется от Component, имеет дополнительные возможности
@Controller
//подписан целый класс вместо методов
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    //Проверяет наличие прав
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(Model model){
        model.addAttribute("users", userService.findAll());

        return "userList";
    }

    //Проверяет наличие прав
    @PreAuthorize("hasAuthority('ADMIN')")
    //помимо /user, который указан в RequestMapping, через "/" ожидается идентификатор
    //@PathVariable User user позволяет через спринг получить user из БД по идентификатору
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    //Проверяет наличие прав
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form, //все поля формы, тк количество ролей может меняться
            @RequestParam("userId") User user){
        userService.saveUser(user, username, form);

        return "redirect:/user";
    }

    //получает заавторизованного user из контекста
    @GetMapping("profile")
    public String getProfile(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());

        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam String password,
            @RequestParam String email){
        userService.updateProfile(user, password, email);

        return "redirect:/user/profile";
    }
}
