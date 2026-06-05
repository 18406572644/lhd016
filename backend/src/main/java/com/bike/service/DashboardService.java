package com.bike.service;

import com.bike.entity.dto.CategoryStatsVO;
import com.bike.entity.dto.DashboardStatsVO;
import com.bike.entity.dto.HelpTrendStatsVO;

import java.util.List;

public interface DashboardService {

    DashboardStatsVO getStats();

    List<CategoryStatsVO> getCategoryStats();

    List<HelpTrendStatsVO> getHelpTrendStats();
}
