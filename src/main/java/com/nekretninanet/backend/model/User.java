package com.nekretninanet.backend.model;

import com.nekretninanet.backend.view.QueryViews;
import com.nekretninanet.backend.view.ReviewViews;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.view.UserViews;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({UserViews.RegularUserSummary.class, UserViews.SupportAccountSummary.class})
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-zČĆŽŠĐčćžšđ -]+$", message = "First name can contain only letters, spaces, and dashes")
    @NotBlank(message = "First name cannot be blank")
    @JsonView({UserViews.RegularUserSummary.class, UserViews.SupportAccountSummary.class})
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-zČĆŽŠĐčćžšđ -]+$", message = "Last name can contain only letters, spaces, and dashes")
    @JsonView({UserViews.RegularUserSummary.class, UserViews.SupportAccountSummary.class})
    private String lastName;

    @Column(nullable = false, unique = true, length = 15)
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9._]+$",
            message = "Username can contain only letters, numbers, dot and underscore"
    )
    @JsonView({UserViews.SupportAccountSummary.class, UserViews.RegularUserSummary.class, QueryViews.SupportRequestSummary.class, ReviewViews.SupportReviewSummary.class})
    private String username;

    @Column(name = "hash_password", nullable = false)
    @NotBlank(message = "Password cannot be blank")
    private String hashPassword;

    @Column(length = 100)
    @Size(max = 100, message = "Address cannot exceed 100 characters")
    @JsonView(UserViews.RegularUserSummary.class)
    private String address;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 50, message = "Email cannot exceed 50 characters")
    @JsonView({UserViews.RegularUserSummary.class, UserViews.SupportAccountSummary.class})
    private String email;

    @Column(name = "phone_number", unique = true)
    @Pattern(
            regexp = "^\\d{3}-\\d{3}-\\d{3}$",
            message = "Phone number format must be XXX-XXX-XXX"
    )
    @JsonView(UserViews.RegularUserSummary.class)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;


    public User() {
    }

    public User(String firstName, String lastName, String username, String hashPassword, String address, String email, String phoneNumber, UserType userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.hashPassword = hashPassword;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", hashPassword='" + hashPassword + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userType=" + userType +
                '}';
    }
}
