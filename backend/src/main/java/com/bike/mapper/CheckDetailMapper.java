package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bike.entity.CheckDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckDetailMapper extends BaseMapper<CheckDetail> {

    List<CheckDetail> selectByCheckId(@Param("checkId") Long checkId);

    int deleteByCheckId(@Param("checkId") Long checkId);
}
