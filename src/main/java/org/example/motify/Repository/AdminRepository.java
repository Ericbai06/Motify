package org.example.motify.Repository;

import org.example.motify.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    /**
     * 根据用户名查找管理员 - 使用原生SQL
     */
    @Query(value = "SELECT * FROM admins WHERE username = :username", nativeQuery = true)
    Optional<Admin> findByUsername(@Param("username") String username);
    
    // /**
    //  * 检查用户名是否存在 - 使用原生SQL
    //  */
    // @Query(value = "SELECT COUNT(*) > 0 FROM admins WHERE username = :username", nativeQuery = true)
    // boolean existsByUsername(@Param("username") String username);
    
    /**
     * 更新管理员最后登录时间 - 使用原生SQL
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE admins SET last_login_time = :loginTime WHERE admin_id = :adminId", nativeQuery = true)
    void updateLastLoginTime(@Param("adminId") Long adminId, @Param("loginTime") LocalDateTime loginTime);
    
    /**
     * 根据邮箱查找管理员 - 使用原生SQL
     */
    @Query(value = "SELECT * FROM admins WHERE email = :email", nativeQuery = true)
    Optional<Admin> findByEmail(@Param("email") String email);
    
    /**
     * 激活或禁用管理员账户 - 使用原生SQL
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE admins SET is_active = :isActive WHERE admin_id = :adminId", nativeQuery = true)
    void updateActiveStatus(@Param("adminId") Long adminId, @Param("isActive") boolean isActive);
}