package com.nekretninanet.backend.service;

import com.nekretninanet.backend.model.AuditLog;
import com.nekretninanet.backend.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAdminUpdateSupportUser(Long adminId, String supportUsername) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminId(adminId);
        auditLog.setAction("ADMIN_UPDATE_SUPPORT_USER");
        auditLog.setUsername(supportUsername);
        auditLog.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }
}
