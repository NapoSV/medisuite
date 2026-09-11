package com.sv.grupo7.medisuite.dao;

import com.sv.grupo7.medisuite.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndTenantId(String email, Long tenantId);

    @Modifying
    @Query("""
            update User u
               set u.failedLoginAttempts = u.failedLoginAttempts + 1,
                   u.lockedUntil = case
                       when u.failedLoginAttempts + 1 >= :maxAttempts then :lockUntil
                       else u.lockedUntil
                   end
             where u.id = :userId
            """)
    int incrementFailedAttempts(@Param("userId") Long userId,
                                @Param("maxAttempts") int maxAttempts,
                                @Param("lockUntil") OffsetDateTime lockUntil);

    @Modifying
    @Query("""
            update User u
               set u.failedLoginAttempts = 0,
                   u.lockedUntil = null
             where u.id = :userId
            """)
    int resetFailedAttempts(@Param("userId") Long userId);
}
