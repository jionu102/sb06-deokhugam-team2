package com.codeit.sb06deokhugamteam2.dashboard.repository;

import com.codeit.sb06deokhugamteam2.dashboard.entity.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DashboardRepository extends JpaRepository<Dashboard, UUID>, DashboardRepositoryCustom {
}
