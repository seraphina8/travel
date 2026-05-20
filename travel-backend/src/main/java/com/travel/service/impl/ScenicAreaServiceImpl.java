package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.ScenicArea;
import com.travel.mapper.ScenicAreaMapper;
import com.travel.service.ScenicAreaService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ScenicAreaServiceImpl extends ServiceImpl<ScenicAreaMapper, ScenicArea> implements ScenicAreaService {

    // 分页查询景区列表（支持名称和省份筛选）
    @Override
    public Page<ScenicArea> getScenicList(Integer pageNum, Integer pageSize, String name, String province, String tag) {
        Page<ScenicArea> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ScenicArea> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(ScenicArea::getStatus, 1);

        if (StringUtils.hasText(name)) {
            String kw = name.trim();
            wrapper.and(w -> w
                    .like(ScenicArea::getName, kw)
                    .or()
                    .like(ScenicArea::getProvince, kw)
                    .or()
                    .like(ScenicArea::getCity, kw)
            );
        }

        if (StringUtils.hasText(province)) {
            wrapper.eq(ScenicArea::getProvince, province);
        }
        if (StringUtils.hasText(tag)) {
            String tagKeyword = tag.trim();
            wrapper.and(w -> w
                    .like(ScenicArea::getTags, tagKeyword)
                    .or()
                    .like(ScenicArea::getName, tagKeyword)
                    .or()
                    .like(ScenicArea::getDescription, tagKeyword)
                    .or()
                    .like(ScenicArea::getProvince, tagKeyword)
                    .or()
                    .like(ScenicArea::getCity, tagKeyword)
            );
        }
        wrapper.orderByDesc(ScenicArea::getRating);
        wrapper.orderByDesc(ScenicArea::getViewCount);
        wrapper.orderByDesc(ScenicArea::getCreateTime);
        return this.page(page, wrapper);
    }

    // 更新景区状态（启用/禁用）
    @Override
    public boolean updateStatus(Long id, Integer status) {
        ScenicArea scenic = new ScenicArea();
        scenic.setId(id);
        scenic.setStatus(status);
        return this.updateById(scenic);
    }
}
