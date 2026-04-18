package com.ddas.backend.repository;

import com.ddas.backend.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private List<User> users = new ArrayList<>();

    public UserRepository() {
        // Pre-registered users — change email/password as needed
        users.add(new User("admin@ddas.com", "admin123"));
        users.add(new User("user@institute.edu", "pass1234"));
    }

    public boolean validate(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public boolean emailExists(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) return true;
        }
        return false;
    }

    public void register(String email, String password) {
        users.add(new User(email, password));
    }
}
