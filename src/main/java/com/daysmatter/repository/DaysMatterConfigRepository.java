package com.daysmatter.repository;

import com.daysmatter.data.entity.DaysMatterConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DaysMatterConfig interface
 *
 * @author 22234
 * @date 2023-06-17 17:11:53
 */
public interface DaysMatterConfigRepository extends JpaRepository<DaysMatterConfig, Long> {
}
