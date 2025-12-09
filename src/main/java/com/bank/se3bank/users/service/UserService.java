package com.bank.se3bank.users.service;
import com.bank.se3bank.shared.enums.Role;
import com.bank.se3bank.shared.exceptions.UserNotFoundException;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * إنشاء مستخدم جديد
     */
    @Transactional
    public User createUser(User user) {
        log.info("👤 إنشاء مستخدم جديد: {}", user.getUsername());
        
        // تشفير كلمة المرور
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // تعيين دور افتراضي إذا لم يكن موجوداً
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(Role.ROLE_CUSTOMER));
        }
        
        User savedUser = userRepository.save(user);
        
        log.info("✅ تم إنشاء المستخدم: {} مع ID: {}", 
                savedUser.getUsername(), savedUser.getId());
        
        return savedUser;
    }

    /**
     * الحصول على مستخدم بواسطة ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * الحصول على مستخدم بواسطة اسم المستخدم
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    /**
     * الحصول على جميع المستخدمين
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * تحديث آخر تسجيل دخول
     */
    @Transactional
    public void updateLastLogin(Long userId) {
        User user = getUserById(userId);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        log.debug("تم تحديث آخر تسجيل دخول للمستخدم: {}", userId);
    }

    /**
     * إضافة دور للمستخدم
     */
    @Transactional
    public User addRoleToUser(Long userId, Role role) {
        User user = getUserById(userId);
        user.addRole(role);
        return userRepository.save(user);
    }

    /**
     * إزالة دور من المستخدم
     */
    @Transactional
    public User removeRoleFromUser(Long userId, Role role) {
        User user = getUserById(userId);
        user.removeRole(role);
        return userRepository.save(user);
    }

    /**
     * تعطيل/تمكين حساب المستخدم
     */
    @Transactional
    public User setUserActiveStatus(Long userId, boolean isActive) {
        User user = getUserById(userId);
        user.setIsActive(isActive);
        return userRepository.save(user);
    }

    /**
     * التحقق من وجود مستخدم بالبريد الإلكتروني
     */
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * التحقق من وجود مستخدم باسم المستخدم
     */
    public boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * البحث عن المستخدمين بالاسم
     */
    public List<User> searchUsersByName(String name) {
        return userRepository.findAll().stream()
                .filter(user -> user.getFullName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }
}