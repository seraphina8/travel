package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Trip;
import com.travel.entity.TripDetail;
import com.travel.mapper.TripMapper;
import com.travel.mapper.TripDetailMapper;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/trip")
@CrossOrigin
public class TripController {

    @Autowired
    private TripMapper tripMapper;

    @Autowired
    private TripDetailMapper tripDetailMapper;

    @Autowired
    private JwtUtils jwtUtils;

    // 分页查询公开行程列表
    @GetMapping
    public Result<Page<Trip>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String tag) {
        Page<Trip> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Trip> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Trip::getStatus, 1);
        wrapper.eq(Trip::getIsPublic, 1); // 只显示公开的行程
        if (tag != null && !tag.trim().isEmpty()) {
            String tagKeyword = tag.trim();
            wrapper.and(w -> w
                    .like(Trip::getTags, tagKeyword)
                    .or()
                    .like(Trip::getTitle, tagKeyword)
                    .or()
                    .like(Trip::getDescription, tagKeyword)
            );
        }
        wrapper.orderByDesc(Trip::getCreateTime);
        return Result.success(tripMapper.selectPage(page, wrapper));
    }

    // 切换行程公开/私有状态
    @GetMapping("/user/{userId}/public")
    public Result<List<Trip>> userPublicTrips(@PathVariable Long userId) {
        LambdaQueryWrapper<Trip> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Trip::getUserId, userId);
        wrapper.eq(Trip::getStatus, 1);
        wrapper.eq(Trip::getIsPublic, 1);
        wrapper.orderByDesc(Trip::getCreateTime);
        return Result.success(tripMapper.selectList(wrapper));
    }

    @PutMapping("/{id}/public")
    public Result<String> togglePublic(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam Integer isPublic) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            Trip trip = tripMapper.selectById(id);
            if (trip == null || !trip.getUserId().equals(userId)) {
                return Result.error("无权限操作");
            }
            trip.setIsPublic(isPublic);
            tripMapper.updateById(trip);
            return Result.success(isPublic == 1 ? "已公开" : "已设为私有");
        } catch (Exception e) {
            return Result.error("操作失败");
        }
    }

    // 获取行程详情（基本信息 + 每日明细）
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Trip trip = tripMapper.selectById(id);
        if (trip == null) {
            return Result.error("行程不存在");
        }
        // 查询行程明细
        LambdaQueryWrapper<TripDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TripDetail::getTripId, id);
        wrapper.orderByAsc(TripDetail::getDayNum);
        wrapper.orderByAsc(TripDetail::getSortOrder);
        List<TripDetail> details = tripDetailMapper.selectList(wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("trip", trip);
        data.put("details", details);
        return Result.success(data);
    }

    // 获取行程明细列表
    @GetMapping("/{id}/details")
    public Result<List<TripDetail>> getDetails(@PathVariable Long id) {
        LambdaQueryWrapper<TripDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TripDetail::getTripId, id);
        wrapper.orderByAsc(TripDetail::getDayNum);
        wrapper.orderByAsc(TripDetail::getSortOrder);
        return Result.success(tripDetailMapper.selectList(wrapper));
    }

    // 保存AI生成的行程详情（含数据清洗）
    @PostMapping("/{id}/ai-details")
    public Result<String> saveAiDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody List<TripDetail> details) {
        try {
            System.out.println("========== 保存AI行程详情 ==========");
            System.out.println("行程ID: " + id);
            System.out.println("接收详情数量: " + details.size());

            // 权限验证
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            Trip trip = tripMapper.selectById(id);
            if (trip == null || !trip.getUserId().equals(userId)) {
                return Result.error("无权限操作");
            }

            // ========== 数据清洗和去重 ==========
            List<TripDetail> cleanedDetails = new ArrayList<>();
            Set<String> uniqueKeySet = new HashSet<>();

            for (TripDetail detail : details) {
                // 清洗景点名称
                String name = cleanAttractionName(detail.getAttractionName());
                if (name == null || name.isEmpty()) {
                    System.out.println("跳过: 景点名称为空");
                    continue;
                }

                // 清洗描述
                String desc = cleanDescription(detail.getDescription());

                // 去重（按天+景点名称）
                String uniqueKey = detail.getDayNum() + "_" + name;
                if (uniqueKeySet.contains(uniqueKey)) {
                    System.out.println("跳过重复: " + uniqueKey);
                    continue;
                }
                uniqueKeySet.add(uniqueKey);

                // 设置清洗后的数据
                detail.setAttractionName(name);
                detail.setDescription(desc);
                detail.setTripId(id);
                detail.setId(null); // 确保插入新记录

                cleanedDetails.add(detail);
            }

            System.out.println("清洗后详情数量: " + cleanedDetails.size());

            // 先删除旧数据
            LambdaQueryWrapper<TripDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TripDetail::getTripId, id);
            tripDetailMapper.delete(wrapper);

            // 批量插入新数据
            int inserted = 0;
            for (TripDetail detail : cleanedDetails) {
                int result = tripDetailMapper.insert(detail);
                if (result > 0) inserted++;
            }

            System.out.println("成功插入数量: " + inserted);
            System.out.println("========== 保存完成 ==========");

            return Result.success("保存成功，共" + inserted + "个景点");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    // 清洗景点名称（移除特殊符号、序号、无效内容）
    private String cleanAttractionName(String name) {
        if (name == null) {
            return null;
        }

        // 移除 Markdown 符号和括号，不删除普通连接符
        name = name.replaceAll("[\\*\\#\\_\\[\\]\\{\\}\\(\\)]", "")
                .replaceAll("\\s+", " ")
                .trim();

        // 如果误传了“08:30-10:00 武侯祠”，去掉时间，只保留景点名
        name = name.replaceAll("^\\d{1,2}:\\d{2}\\s*[-—~至到]\\s*\\d{1,2}:\\d{2}\\s*", "");

        // 兼容“08:3010:00 武侯祠”
        name = name.replaceAll("^\\d{1,2}:\\d{2}\\d{1,2}:\\d{2}\\s*", "");

        // 移除常见前缀
        name = name.replaceAll("^(景点|休闲活动|美食|餐厅|打卡|游览)\\s*[:：]?\\s*", "");

        // 移除数字序号
        name = name.replaceAll("^\\d+[\\.、]\\s*", "");

        // 限制长度
        if (name.length() > 50) {
            name = name.substring(0, 50);
        }

        // 过滤无效内容
        if (name.length() < 2 ||
                name.matches("^[0-9]+$") ||
                name.matches("^[a-zA-Z]+$")) {
            return null;
        }

        return name;
    }

    // 清洗描述内容
    private String cleanDescription(String desc) {
        if (desc == null) {
            return "";
        }

        // 只移除 Markdown 符号，不删除时间范围中的横线
        desc = desc.replaceAll("[\\*\\#\\_]", "")
                .replaceAll("\\s+", " ")
                .trim();

        // 修复已经被错误清洗过的时间格式：08:3010:00 -> 08:30-10:00
        desc = desc.replaceAll("(\\d{1,2}:\\d{2})(\\d{1,2}:\\d{2})", "$1-$2");

        // 限制长度
        if (desc.length() > 200) {
            desc = desc.substring(0, 200);
        }

        return desc;
    }

    // 保存行程明细
    @PostMapping("/{id}/details")
    public Result<String> saveDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody List<TripDetail> details) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            Trip trip = tripMapper.selectById(id);
            if (trip == null || !trip.getUserId().equals(userId)) {
                return Result.error("无权限操作");
            }

            // 删除旧明细
            LambdaQueryWrapper<TripDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TripDetail::getTripId, id);
            tripDetailMapper.delete(wrapper);

            // 插入新明细
            for (TripDetail detail : details) {
                detail.setId(null);
                detail.setTripId(id);
                tripDetailMapper.insert(detail);
            }
            return Result.success("保存成功");
        } catch (Exception e) {
            return Result.error("保存失败");
        }
    }

    // 获取推荐行程
    @GetMapping("/recommend")
    public Result<List<Trip>> recommend() {
        LambdaQueryWrapper<Trip> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(Trip::getUserId);
        wrapper.eq(Trip::getStatus, 1);
        wrapper.orderByDesc(Trip::getCreateTime);
        wrapper.last("LIMIT 6");
        return Result.success(tripMapper.selectList(wrapper));
    }

    // 创建行程
    @PostMapping
    public Result<Long> create(
            @RequestHeader("Authorization") String token,
            @RequestBody Trip trip) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            trip.setUserId(userId);
            trip.setStatus(1);
            trip.setCreateTime(LocalDateTime.now());
            tripMapper.insert(trip);
            return Result.success("创建成功", trip.getId());
        } catch (Exception e) {
            return Result.error("创建失败");
        }
    }

    // 获取我的行程列表
    @GetMapping("/my")
    public Result<List<Trip>> myTrips(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<Trip> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Trip::getUserId, userId);
            wrapper.orderByDesc(Trip::getCreateTime);
            return Result.success(tripMapper.selectList(wrapper));
        } catch (Exception e) {
            return Result.error("获取失败");
        }
    }

    // 更新行程
    @PutMapping("/{id}")
    public Result<String> update(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Trip trip) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            Trip existing = tripMapper.selectById(id);
            if (existing == null || !existing.getUserId().equals(userId)) {
                return Result.error("无权限修改");
            }
            trip.setId(id);
            tripMapper.updateById(trip);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败");
        }
    }

    // 删除行程
    @DeleteMapping("/{id}")
    public Result<String> delete(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<Trip> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Trip::getId, id);
            wrapper.eq(Trip::getUserId, userId);
            tripMapper.delete(wrapper);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败");
        }
    }
}
