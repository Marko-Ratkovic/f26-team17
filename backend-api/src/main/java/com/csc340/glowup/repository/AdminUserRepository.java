package com.csc340.glowup.repository;

import com.csc340.glowup.dto.AdminUserResponse;
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

@Repository
public interface AdminUserRepository extends JpaRepository<UserAccount, Long> {

    @Query(value = """
            select new com.csc340.glowup.dto.AdminUserResponse(
                u.id, coalesce(p.name, c.name), u.email, u.phoneNumber,
                u.accountRole, u.accountStatus, u.createdAt)
            from UserAccount u
            left join Provider p on p.userAccount = u
            left join Customer c on c.userAccount = u
            where (:status is null or u.accountStatus = :status)
              and (:role is null or u.accountRole = :role)
              and (lower(u.email) like :pattern escape '\\'
                   or lower(coalesce(p.name, '')) like :pattern escape '\\'
                   or lower(coalesce(c.name, '')) like :pattern escape '\\')
            """,
            countQuery = """
            select count(u)
            from UserAccount u
            left join Provider p on p.userAccount = u
            left join Customer c on c.userAccount = u
            where (:status is null or u.accountStatus = :status)
              and (:role is null or u.accountRole = :role)
              and (lower(u.email) like :pattern escape '\\'
                   or lower(coalesce(p.name, '')) like :pattern escape '\\'
                   or lower(coalesce(c.name, '')) like :pattern escape '\\')
            """)
    Page<AdminUserResponse> search(@Param("status") AccountStatus status,
                                   @Param("role") AccountRole role,
                                   @Param("pattern") String pattern,
                                   Pageable pageable);

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

    @Query("select p.name from Provider p where p.userAccount.id = :userId")
    Optional<String> findProviderName(@Param("userId") Long userId);
}
