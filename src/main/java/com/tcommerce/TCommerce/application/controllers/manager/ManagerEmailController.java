package com.tcommerce.TCommerce.application.controllers.manager;

import com.tcommerce.TCommerce.application.controllers.ApiPaths;
import com.tcommerce.TCommerce.application.query.EmailLogFilter;
import com.tcommerce.TCommerce.application.services.communication.EmailLogService;
import com.tcommerce.TCommerce.domain.entities.communication.EmailLog;
import com.tcommerce.TCommerce.interfaces.dto.communication.EmailLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(ApiPaths.V1 + "/manager/emails")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
@Tag(name = "Admin Management")
public class ManagerEmailController {

    private final EmailLogService emailLogService;

    @Operation(summary = "Get sent emails logs")
    @GetMapping
    public ResponseEntity<Page<EmailLogResponse>> getEmailLogs(
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        EmailLogFilter filter = new EmailLogFilter(userEmail, startDate, endDate);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<EmailLog> logs = emailLogService.getEmailLogs(filter, pageable);
        
        Page<EmailLogResponse> response = logs.map(log -> new EmailLogResponse(
                log.getId(),
                log.getRecipientEmail(),
                log.getSubject(),
                log.getContent(),
                log.getUserId(),
                log.getCreatedAt()
        ));

        return ResponseEntity.ok(response);
    }
}
