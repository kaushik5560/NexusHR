package com.zidio.nexushr.config;

import com.zidio.nexushr.entity.DispatchLog;
import com.zidio.nexushr.entity.Notification;
import com.zidio.nexushr.entity.Role;
import com.zidio.nexushr.entity.RoleType;
import com.zidio.nexushr.repository.DispatchLogRepository;
import com.zidio.nexushr.repository.NotificationRepository;
import com.zidio.nexushr.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final RoleRepository roleRepository;
    private final NotificationRepository notificationRepository;
    private final DispatchLogRepository dispatchLogRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Automatically seed system roles if they don't already exist
        Arrays.stream(RoleType.values()).forEach(roleType -> {
            if (roleRepository.findByName(roleType).isEmpty()) {
                Role role = Role.builder()
                        .name(roleType)
                        .build();
                roleRepository.save(role);
                logger.info("Seeded system role into PostgreSQL: {}", roleType);
            }
        });

        // 2. Seed initial notifications if empty
        if (notificationRepository.count() == 0) {
            notificationRepository.save(Notification.builder()
                    .recipientEmail("rahul@nexus.com")
                    .title("October 2026 Payslip Ready")
                    .message("Your October 2026 salary of ₹49,500 has been processed (Ref: PAY-202610-EMP1001).")
                    .type("PAYROLL")
                    .isRead(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .recipientEmail("rahul@nexus.com")
                    .title("360° Review Finalized")
                    .message("Priya Singh evaluated your performance: Overall Score 4.5 / 5.0 ⭐.")
                    .type("REVIEW")
                    .isRead(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .recipientEmail("priya@nexus.com")
                    .title("AI Workforce Flight Risk Alert")
                    .message("NexusAI detected 1 high flight risk employee in Engineering (Rahul Sharma, 66%).")
                    .type("AI_ALERT")
                    .isRead(false)
                    .build());

            logger.info("Seeded initial enterprise notifications.");
        }

        // 3. Seed initial communication dispatch logs if empty
        if (dispatchLogRepository.count() == 0) {
            dispatchLogRepository.save(DispatchLog.builder()
                    .channel("EMAIL")
                    .recipient("rahul@nexus.com")
                    .subject("Welcome to NexusHR Platform")
                    .content("Welcome to NexusHR! Your employee credentials have been provisioned.")
                    .deliveryStatus("DELIVERED")
                    .latencyMs(34L)
                    .build());

            dispatchLogRepository.save(DispatchLog.builder()
                    .channel("SMS")
                    .recipient("+91-9876543210")
                    .subject("NexusHR Biometric Alert")
                    .content("NexusHR Biometric: Check-in verified at 09:30 AM.")
                    .deliveryStatus("DELIVERED")
                    .latencyMs(52L)
                    .build());

            logger.info("Seeded initial communication dispatch logs.");
        }
    }
}
