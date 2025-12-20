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
import jakarta.transaction.Transactional;
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

    public void register(RegisterRequestDto dto) {
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

        userRepository.save(user);
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

    @Transactional
    public void deleteSupportUserCascading(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support user not found"));

        if (user.getUserType() != UserType.SUPPORT) {
            throw new BadRequestException("User is not a support account");
        }

        // Query gdje je user autor
        List<Query> userQueries = queryRepository.findByUser(user);
        queryRepository.deleteAll(userQueries);

        // Nekretnine usera
        List<RealEstate> estates = realEstateRepository.findByUser(user);

        if (!estates.isEmpty()) {
            // ID-jevi nekretnina
            List<Long> estateIds = estates.stream()
                    .map(RealEstate::getId)
                    .toList();

            // Query po nekretninama
            List<Query> estateQueries =
                    queryRepository.findByRealEstateIdIn(estateIds);
            queryRepository.deleteAll(estateQueries);

            // Review po nekretninama
            List<Review> estateReviews =
                    reviewRepository.findByRealEstateIdIn(estateIds);
            reviewRepository.deleteAll(estateReviews);
        }

        // Review gdje je user autor
        List<Review> userReviews = reviewRepository.findByUser(user);
        reviewRepository.deleteAll(userReviews);

        // Briši nekretnine
        realEstateRepository.deleteAll(estates);

        // Briši usera
        userRepository.delete(user);
    }


    @Transactional
    public void deleteRegularUserCascading(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regular user not found"));

        if (user.getUserType() != UserType.USER) {
            throw new BadRequestException("User is not a regular account");
        }

        // Query gdje je user autor
        List<Query> userQueries = queryRepository.findByUser(user);
        queryRepository.deleteAll(userQueries);

        // Nekretnine usera
        List<RealEstate> estates = realEstateRepository.findByUser(user);

        if (!estates.isEmpty()) {
            List<Long> estateIds = estates.stream()
                    .map(RealEstate::getId)
                    .toList();

            // Query po nekretninama
            List<Query> estateQueries = queryRepository.findByRealEstateIdIn(estateIds);
            queryRepository.deleteAll(estateQueries);

            // Review po nekretninama
            List<Review> estateReviews = reviewRepository.findByRealEstateIdIn(estateIds);
            reviewRepository.deleteAll(estateReviews);
        }

        // Review gdje je user autor
        List<Review> userReviews = reviewRepository.findByUser(user);
        reviewRepository.deleteAll(userReviews);

        // Briši nekretnine
        realEstateRepository.deleteAll(estates);

        // Briši usera
        userRepository.delete(user);
    }

}
