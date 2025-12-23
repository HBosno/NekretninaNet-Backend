package com.nekretninanet.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_token_versions")
public class UserTokenVersion {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "token_version", nullable = false)
    private Integer tokenVersion;


    public UserTokenVersion() {}

    public UserTokenVersion(Long userId) {
        this.userId = userId;
        this.tokenVersion = 0;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getTokenVersion() {
        return tokenVersion;
    }

    public void setTokenVersion(Integer tokenVersion) {
        this.tokenVersion = tokenVersion;
    }

    public void incrementTokenVersion() {
        this.tokenVersion++;
    }

    @Override
    public String toString() {
        return "UserTokenVersion{" +
                "userId=" + userId +
                ", tokenVersion=" + tokenVersion +
                '}';
    }
}
