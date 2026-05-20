package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    
    @Select("SELECT a.*, r.name as role_name FROM admin a LEFT JOIN role r ON a.role_id = r.id WHERE a.id = #{id}")
    Admin selectWithRole(Long id);
}
