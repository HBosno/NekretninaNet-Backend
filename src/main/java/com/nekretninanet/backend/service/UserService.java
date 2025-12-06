package com.nekretninanet.backend.service;

import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.model.UserType;
import com.nekretninanet.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllSupportUsers() {
        return userRepository.findByUserType(UserType.SUPPORT);
    }

    public List<User> getAllRegularUsers() {
        return userRepository.findByUserType(UserType.USER);
    }

    public User createSupportUser(User user) {
        user.setUserType(UserType.SUPPORT);
        return userRepository.save(user);
    }

    public User createRegularUser(User user) {
        user.setUserType(UserType.USER);
        return userRepository.save(user);
    }

    public User updateUser(Long id, User newUserData) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setFirstName(newUserData.getFirstName());
        existingUser.setLastName(newUserData.getLastName());
        existingUser.setUsername(newUserData.getUsername());
        existingUser.setHashPassword(newUserData.getHashPassword());
        existingUser.setAddress(newUserData.getAddress());
        existingUser.setEmail(newUserData.getEmail());
        existingUser.setPhoneNumber(newUserData.getPhoneNumber());
        existingUser.setUserType(newUserData.getUserType());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

}
