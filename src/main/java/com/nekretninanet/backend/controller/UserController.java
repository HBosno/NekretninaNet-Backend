package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.dto.CreateSupportUserRequest;
import com.nekretninanet.backend.dto.LoginRequestDto;
import com.nekretninanet.backend.dto.RegisterRequestDto;
import com.nekretninanet.backend.dto.UpdateUserDTO;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.view.UserViews;

import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequestDto dto) {
        User user = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<User> login(@RequestBody LoginRequestDto dto) {
        User user = userService.login(dto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin/support-accounts")
    @JsonView(UserViews.SupportAccountSummary.class)
    public ResponseEntity<List<User>> getAllSupportAccounts() {
        List<User> supportUsers = userService.getAllSupportUsers();
        return ResponseEntity.ok(supportUsers);
    }

    @PostMapping("/admin/support-accounts")
    @JsonView(UserViews.SupportAccountSummary.class)
    public ResponseEntity<User> createSupportAccount(@RequestBody @Valid CreateSupportUserRequest request) {
        User created = userService.createSupportUser(request);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/admin/support-accounts/{id}")
    @JsonView(UserViews.SupportAccountSummary.class)
    public ResponseEntity<User> updateSupportAccount(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserDTO dto) {
        User updatedUser = userService.updateSupportUser(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/admin/support-accounts/{id}")
    public ResponseEntity<Void> deleteSupportAccount(@PathVariable Long id) {
        userService.deleteSupportUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/support/regular-users-accounts")
    @JsonView(UserViews.RegularUserSummary.class)
    public ResponseEntity<List<User>> getAllRegularUsers() {
        List<User> regularUsers = userService.getAllRegularUsers();
        return ResponseEntity.ok(regularUsers);
    }

    @PatchMapping("/support/regular-users-accounts/{id}")
    @JsonView(UserViews.RegularUserSummary.class)
    public ResponseEntity<User> updateRegularUserBySupport(
            @PathVariable Long id,
            @RequestBody UpdateUserDTO dto) {
        User updatedUser = userService.updateRegularUser(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/support/regular-users-accounts/{id}")
    public ResponseEntity<Void> deleteRegularUserBySupport(@PathVariable Long id) {
        userService.deleteRegularUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/account/{id}")
    @JsonView(UserViews.RegularUserSummary.class)
    public ResponseEntity<User> getRegularUserById(@PathVariable Long id) {
        User user = userService.getRegularUserById(id);
        return ResponseEntity.ok(user);
    }

    /*
        ista logika kao za /support/regular-users-accounts/{username}. ko poziva ovu metodu jedina razlika.
        gore SUPPORT, ovdje USER. taj dio se negdje drugo obezbjedjuje. spring security?/ autentikacija i autorizacija,
        role based access
    */
    @PatchMapping("/user/account/{id}")
    public ResponseEntity<User> updateRegularUserByRegular(
            @PathVariable Long id,
            @RequestBody UpdateUserDTO dto) {
        userService.updateRegularUser(id, dto);
        return ResponseEntity.noContent().build();
    }

    // analogno kao za prethodni endpoint. vec postoji identicna logika za delete /support/regular-users-accounts/{username}
    @DeleteMapping("/user/account/{id}")
    public ResponseEntity<Void> deleteRegularUserByRegular(@PathVariable Long id) {
        userService.deleteRegularUser(id);
        return ResponseEntity.ok().build();
    }
}
