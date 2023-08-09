package com.daysmatter.repository;

import com.daysmatter.data.entity.DaysMatter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * DaysMatter interface
 *
 * @author 22234
 * @date 2023-06-17 17:11:53
 */
public interface DaysMatterRepository extends JpaRepository<DaysMatter, Long>, JpaSpecificationExecutor<DaysMatter> {
}
