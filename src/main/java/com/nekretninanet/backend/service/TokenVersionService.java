package com.nekretninanet.backend.service;

import com.nekretninanet.backend.model.UserTokenVersion;
import com.nekretninanet.backend.repository.UserTokenVersionRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenVersionService {
    private final UserTokenVersionRepository repo;

    public TokenVersionService(UserTokenVersionRepository repo) {
        this.repo = repo;
    }


    public int getCurrentVersion(Long userId) {
        return repo.findById(userId)
                .orElseGet(() -> repo.save(new UserTokenVersion(userId)))
                .getTokenVersion();
    }

    public int incrementVersion(Long userId) {
        UserTokenVersion tv = repo.findById(userId)
                .orElseGet(() -> new UserTokenVersion(userId));

        tv.incrementTokenVersion();
        repo.save(tv);
        return tv.getTokenVersion();
    }
}
