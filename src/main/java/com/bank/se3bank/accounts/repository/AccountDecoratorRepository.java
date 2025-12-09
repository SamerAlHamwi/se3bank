package com.bank.se3bank.accounts.repository;

import com.bank.se3bank.accounts.decorators.AccountDecorator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountDecoratorRepository extends JpaRepository<AccountDecorator, Long> {
    
    List<AccountDecorator> findByDecoratedAccountId(Long accountId);
    
    List<AccountDecorator> findByDecoratedAccountIdAndIsActiveTrue(Long accountId);
    
    List<AccountDecorator> findByIsActiveTrue();
    
    @Query("SELECT d FROM AccountDecorator d WHERE " +
           "d.decoratedAccount.id = :accountId AND " +
           "LOWER(d.decoratorName) LIKE LOWER(CONCAT('%', :decoratorType, '%'))")
    List<AccountDecorator> findByDecoratedAccountIdAndDecoratorNameContainingIgnoreCase(
            @Param("accountId") Long accountId,
            @Param("decoratorType") String decoratorType);
    
    @Query("SELECT d FROM AccountDecorator d WHERE " +
           "d.decoratedAccount.id = :accountId AND " +
           "TYPE(d) = :decoratorClass")
    List<AccountDecorator> findByDecoratedAccountIdAndType(
            @Param("accountId") Long accountId,
            @Param("decoratorClass") Class<?> decoratorClass);
    
    boolean existsByDecoratedAccountIdAndDecoratorName(Long accountId, String decoratorName);
}