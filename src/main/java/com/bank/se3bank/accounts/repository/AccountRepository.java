package com.bank.se3bank.accounts.repository;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    List<Account> findByUserId(Long userId);
    
    List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);
    
    List<Account> findByAccountType(AccountType accountType);
    
    @Query("SELECT a FROM Account a WHERE a.parentGroup.id = :groupId")
    List<Account> findByGroupId(@Param("groupId") Long groupId);
    
    boolean existsByAccountNumber(String accountNumber);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId AND a.status = 'ACTIVE'")
    Double getTotalBalanceByUserId(@Param("userId") Long userId);

    List<Account> findByStatus(AccountStatus status);

    @Query("SELECT a FROM Account a WHERE " +
        "LOWER(a.accountNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
        "OR LOWER(a.user.username) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
        "OR LOWER(a.user.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
        "OR LOWER(a.user.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Account> searchAccounts(@Param("searchText") String searchText);

    @Query("SELECT a FROM Account a WHERE " +
        "LOWER(a.accountNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
        "OR LOWER(a.user.username) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
        "OR LOWER(a.user.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
        "OR LOWER(a.user.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Account> searchAccounts(@Param("searchText") String searchText, Pageable pageable);

    // إضافة إذا لم تكن موجودة
    @SuppressWarnings("null")
    List<Account> findAll();
    
}