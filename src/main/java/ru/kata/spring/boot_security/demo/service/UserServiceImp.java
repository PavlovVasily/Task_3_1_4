package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.dao.UserDaoImp;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    UserDao userDao;

    @PostConstruct
    void ini(){
        UserDaoImp userDaoImp = (UserDaoImp) userDao;
        userDaoImp.iniRoles();
//         DB initializer
//         admin
        User admin = new User();
        admin.setAge((byte)29);
        admin.setName("ADMIN");
        admin.setEmail("admin@mail.ru");

        admin.setPassword("123456");
        String [] rolesNames = {"ROLE_USER", "ROLE_ADMIN"};
        saveUser(admin.getName(), admin.getEmail(), (byte) admin.getAge(), admin.getPassword(), rolesNames);
    }

    @Override
    public boolean saveUser(String name, String email, byte age, String password, String[] roleNames) {
        return userDao.saveUser(name, email, age, password, roleNames);
    }

    @Override
    public void removeUserById(long id) {
        userDao.removeUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public boolean updateUser(User user, String[] roleNames) {
        return userDao.updateUser(user, roleNames);
    }

    @Override
    public User getUserById(Long id) {
        return userDao.getUserById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.getUserByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }*/

}
