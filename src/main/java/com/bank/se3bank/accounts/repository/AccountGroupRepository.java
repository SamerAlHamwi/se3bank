package com.bank.se3bank.accounts.repository;

import com.bank.se3bank.accounts.model.AccountGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountGroupRepository extends JpaRepository<AccountGroup, Long> {
    
    List<AccountGroup> findByUserId(Long userId);
    
    Optional<AccountGroup> findByGroupNameAndUserId(String groupName, Long userId);
    
    List<AccountGroup> findByGroupType(String groupType);
    
    @Query("SELECT g FROM AccountGroup g WHERE g.user.id = :userId AND SIZE(g.childAccounts) > 0")
    List<AccountGroup> findNonEmptyGroupsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT g FROM AccountGroup g WHERE g.user.id = :userId AND SIZE(g.childAccounts) >= :minAccounts")
    List<AccountGroup> findGroupsWithMinAccounts(@Param("userId") Long userId, 
                                                 @Param("minAccounts") int minAccounts);
    
    boolean existsByGroupNameAndUserId(String groupName, Long userId);
}