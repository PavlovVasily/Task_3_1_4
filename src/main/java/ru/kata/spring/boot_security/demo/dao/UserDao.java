package ru.kata.spring.boot_security.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

public interface UserDao {
    boolean saveUser(String name, String email, byte age, String password, String[] roleNames);

    void removeUserById(long id);

    List<User> getAllUsers();

    boolean updateUser(User user, String[] roleNames);

    User getUserById(Long id);

    User getUserByEmail(String email);

//    User findByEmail(String userEmail);
}
