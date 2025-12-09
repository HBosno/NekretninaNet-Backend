package com.nekretninanet.backend.service;

import com.nekretninanet.backend.dto.CreateSupportUserRequest;
import com.nekretninanet.backend.dto.UpdateUserDTO;
import com.nekretninanet.backend.exception.BadRequestException;
import com.nekretninanet.backend.exception.ResourceNotFoundException;
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
        List<User> supportUsers = userRepository.findByUserType(UserType.SUPPORT);

        if (supportUsers == null || supportUsers.isEmpty()) {
            throw new ResourceNotFoundException("No support accounts found.");
        }

        return supportUsers;
    }

    public List<User> getAllRegularUsers() {
        List<User> regularUsers = userRepository.findByUserType(UserType.USER);

        if (regularUsers == null || regularUsers.isEmpty()) {
            throw new ResourceNotFoundException("No regular users found.");
        }

        return regularUsers;
    }

    public User createSupportUser(CreateSupportUserRequest req) {

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists.");
        }

        User user = new User(
                req.getFirstName(),
                req.getLastName(),
                req.getUsername(),
                req.getHashPassword(),
                req.getAddress(),
                req.getEmail(),
                req.getPhoneNumber(),
                UserType.SUPPORT // fiksno zbog sigurnosti, a req ide preko dto
        );

        return userRepository.save(user);
    }

    public User createRegularUser(User user) {
        user.setUserType(UserType.USER);
        return userRepository.save(user);
    }

    public User updateSupportUser(String username, UpdateUserDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Support user not found"));

        if (user.getUserType() != UserType.SUPPORT) {
            throw new BadRequestException("User is not a support account");
        }

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getHashPassword() != null) user.setHashPassword(dto.getHashPassword());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

        return userRepository.save(user);
    }

    public User updateRegularUser(String username, UpdateUserDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Regular user not found"));

        if (user.getUserType() != UserType.USER) {
            throw new IllegalArgumentException("User is not a regular account");
        }

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getHashPassword() != null) user.setHashPassword(dto.getHashPassword());

        return userRepository.save(user);
    }

    public void deleteSupportUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Support user not found"));

        if (user.getUserType() != UserType.SUPPORT) {
            throw new IllegalArgumentException("User is not a support account");
        }

        userRepository.delete(user);
    }


    public void deleteRegularUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Regular user not found"));

        if (user.getUserType() != UserType.USER) {
            throw new IllegalArgumentException("User is not a regular account");
        }

        userRepository.delete(user);
    }

}
