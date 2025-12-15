package com.nekretninanet.backend.service;

import com.nekretninanet.backend.dto.CreateSupportUserRequest;
import com.nekretninanet.backend.dto.LoginRequestDto;
import com.nekretninanet.backend.dto.RegisterRequestDto;
import com.nekretninanet.backend.dto.UpdateUserDTO;
import com.nekretninanet.backend.exception.BadRequestException;
import com.nekretninanet.backend.exception.ResourceNotFoundException;
import com.nekretninanet.backend.model.*;
import com.nekretninanet.backend.repository.QueryRepository;
import com.nekretninanet.backend.repository.RealEstateRepository;
import com.nekretninanet.backend.repository.ReviewRepository;
import com.nekretninanet.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private QueryRepository queryRepository;
    @Autowired
    private RealEstateRepository realEstateRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterRequestDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        user.setHashPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserType(UserType.USER);

        return userRepository.save(user);
    }

    public User login(LoginRequestDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getHashPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return user;
    }

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

    public User getRegularUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regular user not found"));

        if (user.getUserType() != UserType.USER) {
            throw new IllegalArgumentException("User is not a regular account");
        }

        return user;
    }

    // Password validation inspired by NIST SP 800-63B:
    // - minimum length 12
    // - mixed character types for increased entropy (upper, lower, digit)
    // - po potrebi pojacati, dodati obavezan simbol?
    private void validatePassword(String password) {
        if (password.length() < 12) {
            throw new BadRequestException("Password must be at least 12 characters long.");
        }

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        if (!hasUpper || !hasLower || !hasDigit) {
            throw new BadRequestException(
                    "Password must contain uppercase, lowercase letters and a number."
            );
        }
    }

    public User createSupportUser(CreateSupportUserRequest req) {

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists.");
        }

        validatePassword(req.getPassword());

        String hashedPassword = passwordEncoder.encode(req.getPassword());

        User user = new User(
                req.getFirstName(),
                req.getLastName(),
                req.getUsername(),
                hashedPassword,
                req.getAddress(),
                req.getEmail(),
                req.getPhoneNumber(),
                UserType.SUPPORT
        );

        return userRepository.save(user);
    }

    public User createRegularUser(User user) {
        user.setUserType(UserType.USER);
        return userRepository.save(user);
    }

    public User updateSupportUser(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support user not found"));

        if (user.getUserType() != UserType.SUPPORT) {
            throw new BadRequestException("User is not a support account");
        }

        if (dto.getUsername() != null) {
            if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
                throw new BadRequestException("Username already exists.");
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

        if (dto.getPassword() != null) {
            validatePassword(dto.getPassword());
            user.setHashPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return userRepository.save(user);
    }

    public User updateRegularUser(Long id, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regular user not found"));

        if (user.getUserType() != UserType.USER) {
            throw new IllegalArgumentException("User is not a regular account");
        }

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getPassword() != null) {
            validatePassword(dto.getPassword());
            user.setHashPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return userRepository.save(user);
    }

    public void deleteSupportUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support user not found"));

        if (user.getUserType() != UserType.SUPPORT) {
            throw new IllegalArgumentException("User is not a support account");
        }

        List<Review> reviews = reviewRepository.findByUser(user);
        reviewRepository.deleteAll(reviews);

        List<Query> queries = queryRepository.findByUser(user);
        queryRepository.deleteAll(queries);

        List<RealEstate> estates = realEstateRepository.findByUser(user);
        realEstateRepository.deleteAll(estates);

        userRepository.delete(user);
    }


    public void deleteRegularUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regular user not found"));

        if (user.getUserType() != UserType.USER) {
            throw new IllegalArgumentException("User is not a regular account");
        }

        List<Review> reviews = reviewRepository.findByUser(user);
        reviewRepository.deleteAll(reviews);

        List<Query> queries = queryRepository.findByUser(user);
        queryRepository.deleteAll(queries);

        List<RealEstate> estates = realEstateRepository.findByUser(user);
        realEstateRepository.deleteAll(estates);

        userRepository.delete(user);
    }

}
