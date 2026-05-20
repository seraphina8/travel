package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    
    @Select("SELECT p.* FROM permission p WHERE p.status = 1 ORDER BY p.sort_order ASC, p.id ASC")
    List<Permission> selectByRoleId(Long roleId);
}
