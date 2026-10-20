package com.csc340.glowup.repository;

import com.csc340.glowup.entity.UserAccount;
import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Admin-only queries over user accounts. Kept separate from UserAccountRepository
 * so the admin search does not change the customer/provider code.
 */
@Repository
public interface AdminUserRepository extends JpaRepository<UserAccount, Long> {

    /** pattern must already be lower-case and wrapped in %, e.g. "%laura%" ("%%" matches everyone). */
    @Query("""
            select u from UserAccount u
            left join Provider p on p.userAccount = u
            left join Customer c on c.userAccount = u
            where (:status is null or u.accountStatus = :status)
              and (:role is null or u.accountRole = :role)
              and (lower(u.email) like :pattern
                   or lower(coalesce(p.name, '')) like :pattern
                   or lower(coalesce(c.name, '')) like :pattern)
            """)
    Page<UserAccount> search(@Param("status") AccountStatus status,
                             @Param("role") AccountRole role,
                             @Param("pattern") String pattern,
                             Pageable pageable);

    /** Role and sign-up time of every non-admin account created in [start, end). */
    @Query("""
            select u.accountRole, u.createdAt from UserAccount u
            where u.createdAt >= :start and u.createdAt < :end
              and u.accountRole <> :excludedRole
            """)
    List<Object[]> findSignupsBetween(@Param("start") OffsetDateTime start,
                                      @Param("end") OffsetDateTime end,
                                      @Param("excludedRole") AccountRole excludedRole);

    long countByAccountRoleAndAccountStatus(AccountRole accountRole, AccountStatus accountStatus);

    @Query("select c.name from Customer c where c.userAccount.id = :userId")
    Optional<String> findCustomerName(@Param("userId") Long userId);
}
