package com.nekretninanet.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequestDto {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9._]+$",
            message = "Username can contain only letters, numbers, dot and underscore"
    )
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 12, max = 100, message = "Password must be between 12 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter and one number"
    )
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 50, message = "Email cannot exceed 50 characters")
    private String email;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-zČĆŽŠĐčćžšđ -]+$", message = "First name can contain only letters, spaces, and dashes")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-zČĆŽŠĐčćžšđ -]+$", message = "Last name can contain only letters, spaces, and dashes")
    private String lastName;

    @Size(max = 100, message = "Address cannot exceed 100 characters")
    private String address;

    @Pattern(
            regexp = "^(\\d{3}-\\d{3}-\\d{3})?$",
            message = "Phone number must be in format XXX-XXX-XXX"
    )
    private String phoneNumber;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}