package com.nekretninanet.backend.dto;

import jakarta.validation.constraints.*;

public class UpdateUserDTO {
    public UpdateUserDTO(String firstName, String lastName, String username, String password, String address, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-zČĆŽŠĐčćžšđ -]+$", message = "First name can contain only letters, spaces, and dashes")
    private String firstName;
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-zČĆŽŠĐčćžšđ -]+$", message = "Last name can contain only letters, spaces, and dashes")
    private String lastName;
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9._]+$",
            message = "Username can contain only letters, numbers, dot and underscore"
    )
    private String username;
    @Size(min = 12, max = 100, message = "Password must be between 12 and 100 characters")
    private String password;
    @Size(max = 100, message = "Address cannot exceed 100 characters")
    private String address;
    @Email(message = "Email must be valid")
    @Size(max = 50, message = "Email cannot exceed 50 characters")
    private String email;
    @Pattern(
            regexp = "^\\d{3}-\\d{3}-\\d{3}$",
            message = "Phone number format must be XXX-XXX-XXX"
    )
    private String phoneNumber;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
