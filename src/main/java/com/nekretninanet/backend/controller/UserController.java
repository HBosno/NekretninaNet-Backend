package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.dto.CreateSupportUserRequest;
import com.nekretninanet.backend.dto.UpdateUserDTO;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/support-accounts")
    public ResponseEntity<List<User>> getAllSupportAccounts() {
        List<User> supportUsers = userService.getAllSupportUsers();
        return ResponseEntity.ok(supportUsers);
    }

    @PostMapping("/admin/support-accounts")
    public ResponseEntity<User> createSupportAccount(@RequestBody @Valid CreateSupportUserRequest request) {
        User created = userService.createSupportUser(request);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/admin/support-accounts/{username}")
    public ResponseEntity<User> updateSupportAccount(
            @PathVariable String username,
            @RequestBody @Valid UpdateUserDTO dto) {
        User updatedUser = userService.updateSupportUser(username, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/admin/support-accounts/{username}")
    public ResponseEntity<Void> deleteSupportAccount(@PathVariable String username) {
        userService.deleteSupportUser(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/support/regular-users-accounts")
    public ResponseEntity<List<User>> getAllRegularUsers() {
        List<User> regularUsers = userService.getAllRegularUsers();
        return ResponseEntity.ok(regularUsers);
    }

    @PatchMapping("/support/regular-users-accounts/{username}")
    public ResponseEntity<User> updateRegularUserBySupport(
            @PathVariable String username,
            @RequestBody UpdateUserDTO dto) {
        User updatedUser = userService.updateRegularUser(username, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/support/regular-users-accounts/{username}")
    public ResponseEntity<Void> deleteRegularUserBySupport(@PathVariable String username) {
        userService.deleteRegularUser(username);
        return ResponseEntity.ok().build();
    }

    /*
        ista logika kao za /support/regular-users-accounts/{username}. ko poziva ovu metodu jedina razlika.
        gore SUPPORT, ovdje USER. taj dio se negdje drugo obezbjedjuje. spring security?/ autentikacija i autorizacija,
        role based access
    */
    @PatchMapping("/user/account/{username}")
    public ResponseEntity<User> updateRegularUserByRegular(
            @PathVariable String username,
            @RequestBody UpdateUserDTO dto) {
        User updatedUser = userService.updateRegularUser(username, dto);
        return ResponseEntity.ok(updatedUser);
    }

    // analogno kao za prethodni endpoint. vec postoji identicna logika za delete /support/regular-users-accounts/{username}
    @DeleteMapping("/user/account/{username}")
    public ResponseEntity<Void> deleteRegularUserByRegular(@PathVariable String username) {
        userService.deleteRegularUser(username);
        return ResponseEntity.ok().build();
    }
}
