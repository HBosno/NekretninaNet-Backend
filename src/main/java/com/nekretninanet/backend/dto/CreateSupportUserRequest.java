package com.nekretninanet.backend.dto;

import jakarta.validation.constraints.*;

public class CreateSupportUserRequest {
    public CreateSupportUserRequest(String firstName, String lastName, String username, String password, String address, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-zČĆŽŠĐčćžšđ -]+$", message = "First name can contain only letters, spaces, and dashes")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-zČĆŽŠĐčćžšđ -]+$", message = "Last name can contain only letters, spaces, and dashes")
    private String lastName;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 10, message = "Username must be between 4 and 10 characters")
    @Pattern(regexp = "^[A-Za-z._]+$", message = "Username can contain only letters, dot, and underscore")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 12, max = 100, message = "Password must be between 12 and 100 characters")
    private String password;
    @Size(max = 100, message = "Address cannot exceed 100 characters")
    private String address;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 50, message = "Email cannot exceed 50 characters")
    private String email;
    @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{3}$", message = "Phone number format must be XXX-XXX-XXX")
    private String phoneNumber;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
