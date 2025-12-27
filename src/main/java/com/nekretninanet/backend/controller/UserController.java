package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.dto.CreateSupportUserRequest;
import com.nekretninanet.backend.dto.LoginRequestDto;
import com.nekretninanet.backend.dto.RegisterRequestDto;
import com.nekretninanet.backend.dto.UpdateUserDTO;
import com.nekretninanet.backend.model.AuditLog;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.service.AuditLogService;
import com.nekretninanet.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.view.UserViews;

import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    private final UserService userService;
    private final AuditLogService auditLogService;

    public UserController(UserService userService, AuditLogService auditLogService) {

        this.userService = userService;
        this.auditLogService = auditLogService;
    }


    @GetMapping("/admin/support-accounts")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UserViews.SupportAccountSummary.class)
    public ResponseEntity<List<User>> getAllSupportAccounts() {
        List<User> supportUsers = userService.getAllSupportUsers();
        return ResponseEntity.ok(supportUsers);
    }

    @PostMapping("/admin/support-accounts")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UserViews.SupportAccountSummary.class)
    public ResponseEntity<User> createSupportAccount(@RequestBody @Valid CreateSupportUserRequest request) {
        User created = userService.createSupportUser(request);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/admin/support-accounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(UserViews.SupportAccountSummary.class)
    public ResponseEntity<User> updateSupportAccount(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long adminId = userService.findByUsername(userDetails.getUsername()).getId();
        User updatedUser = userService.updateSupportUser(id, adminId, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/admin/support-accounts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSupportAccount(@PathVariable Long id) {
        userService.deleteSupportUserCascading(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/support/regular-users-accounts")
    @PreAuthorize("hasRole('SUPPORT')")
    @JsonView(UserViews.RegularUserSummary.class)
    public ResponseEntity<List<User>> getAllRegularUsers() {
        List<User> regularUsers = userService.getAllRegularUsers();
        return ResponseEntity.ok(regularUsers);
    }

    @PatchMapping("/support/regular-users-accounts/{id}")
    @PreAuthorize("hasRole('SUPPORT')")
    @JsonView(UserViews.RegularUserSummary.class)
    public ResponseEntity<User> updateRegularUserBySupport(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO dto) {
        User updatedUser = userService.updateRegularUser(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/support/regular-users-accounts/{id}")
    @PreAuthorize("hasRole('SUPPORT')")
    public ResponseEntity<Void> deleteRegularUserBySupport(@PathVariable Long id) {
        userService.deleteRegularUserCascading(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/account/{id}")
    @PreAuthorize("hasRole('USER')")
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<User> updateRegularUserByRegular(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO dto) {
        userService.updateRegularUser(id, dto);
        return ResponseEntity.noContent().build();
    }

    // analogno kao za prethodni endpoint. vec postoji identicna logika za delete /support/regular-users-accounts/{username}
    @DeleteMapping("/user/account/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteRegularUserByRegular(@PathVariable Long id) {
        userService.deleteRegularUserCascading(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/log")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        List<AuditLog> logs = auditLogService.getAllAuditLogs();

        if (logs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(logs);
    }
}
