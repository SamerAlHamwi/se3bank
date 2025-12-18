package com.bank.se3bank.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DataFixer {

    @Bean
    public CommandLineRunner fixAccountTypes(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // تحديث القيم القديمة التي تسبب الخطأ
                int updatedCount = jdbcTemplate.update(
                    "UPDATE accounts SET account_type = 'ACCOUNT_GROUP' WHERE account_type = 'AccountGroup'"
                );
                
                if (updatedCount > 0) {
                    log.info("✅ تم إصلاح {} سجل في قاعدة البيانات (تحديث 'AccountGroup' إلى 'ACCOUNT_GROUP')", updatedCount);
                }
            } catch (Exception e) {
                log.warn("⚠️ لم يتمكن من تنفيذ إصلاح البيانات (قد يكون الجدول غير موجود أو المشكلة غير موجودة): {}", e.getMessage());
            }
        };
    }
}