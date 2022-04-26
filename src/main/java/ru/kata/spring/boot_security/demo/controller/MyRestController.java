package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.exception_handling.NoSuchUserException;
import ru.kata.spring.boot_security.demo.exception_handling.UserIncorrectData;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MyRestController {

    @Autowired
    private UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public List<User> showAllUsers (){
        List<User> allUsers = userService.getAllUsers();
        System.out.println(allUsers);
        return allUsers;
    }

    @ResponseBody
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable long id){
        System.out.println("users/" + id);
        User user = userService.getUserById(id);

        if (user == null) {
            throw new NoSuchUserException("Пользователя с ID = " +
                    id + " в базе данных нет");
        }
        return user;
    }

    @PostMapping("/users")
    public User addNewUser(@RequestBody User user) {

        //Роли
        String[] roleNames = new String[2];
        int count = 0;
        for (Role role : user.getRoles()) {
            roleNames[count] = role.getRole();
            count++;
        }

        userService.saveUser(user.getName(), user.getEmail(), (byte) user.getAge(), user.getPassword(), roleNames);

        return userService.getUserByEmail(user.getEmail());
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        String[] roleNames = new String[2];
        int count = 0;
        for (Role role : user.getRoles()) {
            roleNames[count] = role.getRole();
            count++;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.updateUser(user, roleNames);

        return userService.getUserByEmail(user.getEmail());
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable long id) {
        User user = userService.getUserById(id);

        if (user == null) {
            throw new NoSuchUserException("Пользователя с ID = " +
                    id + " в базе данных нет");
        }

        userService.removeUserById(id);
        return "Пользователь с ID = " + id + " был удален";
    }
}
