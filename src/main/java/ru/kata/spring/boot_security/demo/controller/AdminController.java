package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;


@Controller
public class AdminController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/admin")
    public String showAll(Model model, @ModelAttribute("user") User user, @AuthenticationPrincipal User userDetails) {
        String userEmail = userDetails.getEmail();
        model.addAttribute("authUser", userService.getUserByEmail(userEmail));
        model.addAttribute("users", userService.getAllUsers());
        return "admin/all";
    }

    @GetMapping("/admin/new")
    public String newUser(@ModelAttribute("user") User user) {
        return "admin/new";
    }

    @PostMapping("/admin/new")
    public String create(@RequestParam(required = false) String[] roleNames,
                         @ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult /*Всегда идет после аргумента с анотацией валид. Сюда пишутся ошибки валидности*/,
                         Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("Error POST");
            System.out.println(bindingResult.getAllErrors());
//            return "admin/new";
//            return "admin/all";
            return "redirect:/admin";
        }

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
//            System.out.println("Пароли не совпадают");
//            model.addAttribute("passwordError", "Пароли не совпадают");
            System.out.println("Пароли не совпадают");
//            return "admin/new";
//            return "admin/all";
            return "redirect:/admin";
        }

        if (!userService.saveUser(user.getName(), user.getEmail(), (byte) user.getAge(), user.getPassword(), roleNames)) {
//            model.addAttribute("emailError", "Пользователь с такой почтой уже существует");
            System.out.println("Пользователь с такой почтой уже существует или не выбрана ниодна из ролей");
//            return "admin/new";
//            return "admin/all";
            return "redirect:/admin";
        }

        return "redirect:/admin";
    }

    @GetMapping("/admin/{id}/edit")
    public String edit(@PathVariable("id") long id, Model model) {
        model.addAttribute(userService.getUserById(id));
        return "admin/edit";
    }

    @PatchMapping("/admin/{id}")
    public String update(@RequestParam(required = false) String[] roleNames,
                         @ModelAttribute("user") @Valid User user,
                         BindingResult bindingResult,
                         @PathVariable("id") long id,
                         Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
//            return "admin/edit";
            return "redirect:/admin";
        }

//        User editedUser = userService.getUserById(user.getId());

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
//            model.addAttribute("passwordError", "Пароли не совпадают");
            System.out.println("Пароли не совпадают");
//            return "admin/new";
//            return "admin/all";
            return "redirect:/admin";
        }

        //замена пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));


        userService.updateUser(user, roleNames);

        return "redirect:/admin";
    }


    @DeleteMapping(value = "/admin/{id}")
    public String remove(@PathVariable("id") long id) {
        userService.removeUserById(id);
        return "redirect:/admin";
    }
}
