package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.Strategy;
import com.travel.mapper.StrategyMapper;
import com.travel.service.StrategyService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StrategyServiceImpl extends ServiceImpl<StrategyMapper, Strategy> implements StrategyService {

    // 分页查询攻略列表（支持标题、标签、状态筛选和排序）
    @Override
    public Page<Strategy> getStrategyList(Integer pageNum, Integer pageSize, String title, String tag, String sortBy, Integer status) {
        Page<Strategy> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();

        // 状态条件放在最前面
        if (status != null) {
            wrapper.eq(Strategy::getStatus, status);
        }

        if (StringUtils.hasText(title)) {
            wrapper.like(Strategy::getTitle, title);
        }

        // 标签搜索使用嵌套查询，避免or破坏其他条件
        if (StringUtils.hasText(tag)) {
            wrapper.and(w -> w
                    .like(Strategy::getTitle, tag)
                    .or()
                    .like(Strategy::getContent, tag)
                    .or()
                    .like(Strategy::getTags, tag));
        }

        // 排序
        if ("viewCount".equals(sortBy)) {
            wrapper.orderByDesc(Strategy::getViewCount);
        } else if ("likeCount".equals(sortBy)) {
            wrapper.orderByDesc(Strategy::getLikeCount);
        } else {
            wrapper.orderByDesc(Strategy::getCreateTime);
        }
        return this.page(page, wrapper);
    }

    // 根据ID查询攻略详情
    @Override
    public Strategy getStrategyById(Long id) {
        return baseMapper.selectWithUser(id);
    }

    // 更新攻略状态
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Strategy strategy = new Strategy();
        strategy.setId(id);
        strategy.setStatus(status);
        return this.updateById(strategy);
    }
}
