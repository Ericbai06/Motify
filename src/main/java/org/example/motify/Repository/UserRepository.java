package org.example.motify.Repository;

import org.example.motify.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 基础CRUD操作由JpaRepository提供
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
     @Query("SELECT u FROM User u LEFT JOIN FETCH u.cars WHERE u.userId = :userId")
    Optional<User> findByIdWithCars(@Param("userId") Long userId);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cars WHERE u.username = :username")
    Optional<User> findByUsernameWithCars(@Param("username") String username);
} 