package org.example.motify.Repository;

import org.example.motify.Entity.Wage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WageRepository extends JpaRepository<Wage, Long> {

    /**
     * 根据年份和月份查询工资记录
     *
     * @param year  年份
     * @param month 月份
     * @return 工资记录列表
     */
    @Query(value = "SELECT * FROM wages WHERE year = :year AND month = :month", nativeQuery = true)
    List<Wage> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * 根据维修人员ID查询工资记录，按年份和月份降序排序
     *
     * @param repairmanId 维修人员ID
     * @return 工资记录列表
     */
    @Query(value = "SELECT * FROM wages WHERE repairman_id = :repairmanId ORDER BY year DESC, month DESC", nativeQuery = true)
    List<Wage> findByRepairmanIdOrderByYearDescMonthDesc(@Param("repairmanId") Long repairmanId);

    /**
     * 查询指定维修人员在指定年月的工资记录
     *
     * @param repairmanId 维修人员ID
     * @param year        年份
     * @param month       月份
     * @return 工资记录列表
     */
    @Query(value = "SELECT * FROM wages WHERE repairman_id = :repairmanId AND year = :year AND month = :month", nativeQuery = true)
    List<Wage> findByRepairmanIdAndYearAndMonth(@Param("repairmanId") Long repairmanId, @Param("year") int year,
            @Param("month") int month);

    /**
     * 查询指定维修人员在指定年份的所有工资记录
     *
     * @param repairmanId 维修人员ID
     * @param year        年份
     * @return 工资记录列表
     */
    @Query(value = "SELECT * FROM wages WHERE repairman_id = :repairmanId AND year = :year ORDER BY month ASC", nativeQuery = true)
    List<Wage> findByRepairmanIdAndYear(@Param("repairmanId") Long repairmanId, @Param("year") int year);
}
