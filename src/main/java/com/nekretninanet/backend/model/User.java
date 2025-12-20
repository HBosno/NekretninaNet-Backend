package com.nekretninanet.backend.model;

import com.nekretninanet.backend.view.QueryViews;
import com.nekretninanet.backend.view.ReviewViews;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.view.UserViews;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({UserViews.RegularUserSummary.class, UserViews.SupportAccountSummary.class})
    private Long id;

    @Column(name = "first_name")
    @JsonView({UserViews.RegularUserSummary.class, UserViews.SupportAccountSummary.class})
    private String firstName;

    @Column(name = "last_name")
    @JsonView({UserViews.RegularUserSummary.class, UserViews.SupportAccountSummary.class})
    private String lastName;

    @Column(unique = true)
    @JsonView({UserViews.RegularUserSummary.class, QueryViews.SupportRequestSummary.class, ReviewViews.SupportReviewSummary.class})
    private String username;

    @Column(name = "hash_password")
    private String hashPassword;

    @JsonView(UserViews.RegularUserSummary.class)
    private String address;
    @JsonView({UserViews.RegularUserSummary.class, UserViews.SupportAccountSummary.class})
    private String email;

    @Column(name = "phone_number")
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
