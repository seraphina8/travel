package com.travel.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.User;
import com.travel.service.UserService;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;

    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final long CAPTCHA_EXPIRE_MILLIS = 5 * 60 * 1000;
    private static final ConcurrentHashMap<String, CaptchaEntry> CAPTCHA_STORE = new ConcurrentHashMap<>();

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{4,20}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d_@#$%^&*!.-]{6,20}$");
    }

    private boolean isValidNickname(String nickname) {
        return nickname != null && nickname.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9_-]{2,20}$");
    }

    private boolean isValidPhone(String phone) {
        return phone == null || phone.trim().isEmpty() || phone.matches("^1[3-9]\\d{9}$");
    }

    private boolean isValidEmail(String email) {
        return email == null || email.trim().isEmpty()
                || email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidAvatar(String avatar) {
        return avatar == null || avatar.trim().isEmpty()
                || avatar.startsWith("/uploads/")
                || avatar.startsWith("http://")
                || avatar.startsWith("https://");
    }

    // 获取图形验证码
    @GetMapping("/captcha")
    public Result<Map<String, String>> captcha() {
        String code = randomCaptchaCode();
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        CAPTCHA_STORE.put(captchaId, new CaptchaEntry(code, System.currentTimeMillis() + CAPTCHA_EXPIRE_MILLIS));

        Map<String, String> data = new HashMap<>();
        data.put("captchaId", captchaId);
        data.put("image", generateCaptchaImage(code));
        return Result.success(data);
    }

    // 用户登录
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser) {

        if (loginUser == null || isBlank(loginUser.getUsername()) || isBlank(loginUser.getPassword())) {
            return Result.error("用户名和密码不能为空");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUser.getUsername());
        wrapper.eq(User::getPassword, DigestUtil.md5Hex(loginUser.getPassword()));
        wrapper.eq(User::getStatus, 1);
        User user = userService.getOne(wrapper);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        user.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.success("登录成功", data);
    }

    // 修改密码
    @PutMapping("/password")
    public Result<String> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> params) {
        if (!verifyCaptcha(params.get("captchaId"), params.get("captchaCode"))) {
            return Result.error("验证码错误或已过期");
        }

        String oldPassword = params.get("oldPassword") == null ? "" : params.get("oldPassword").trim();
        String newPassword = params.get("newPassword") == null ? "" : params.get("newPassword").trim();

        if (oldPassword.isEmpty()) {
            return Result.error("原密码不能为空");
        }
        if (!isValidPassword(newPassword)) {
            return Result.error("新密码应为6到20位，且至少包含字母和数字");
        }

        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            if (!DigestUtil.md5Hex(oldPassword).equals(user.getPassword())) {
                return Result.error("原密码不正确");
            }
            user.setPassword(DigestUtil.md5Hex(newPassword));
            userService.updateById(user);
            return Result.success("密码修改成功，请重新登录");
        } catch (Exception e) {
            return Result.error(401, "未登录或登录已过期");
        }
    }

    // 忘记密码：通过用户名和已绑定手机号/邮箱重置
    @PostMapping("/forgot-password")
    public Result<String> forgotPassword(@RequestBody Map<String, String> params) {
        if (!verifyCaptcha(params.get("captchaId"), params.get("captchaCode"))) {
            return Result.error("验证码错误或已过期");
        }

        String username = params.get("username") == null ? "" : params.get("username").trim();
        String identity = params.get("identity") == null ? "" : params.get("identity").trim();
        String newPassword = params.get("newPassword") == null ? "" : params.get("newPassword").trim();

        if (username.isEmpty() || identity.isEmpty()) {
            return Result.error("用户名和绑定信息不能为空");
        }
        if (!isValidPassword(newPassword)) {
            return Result.error("新密码应为6到20位，且至少包含字母和数字");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userService.getOne(wrapper);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            return Result.error("用户不存在或已被禁用");
        }

        boolean identityMatched = identity.equals(user.getPhone()) || identity.equalsIgnoreCase(user.getEmail());
        if (!identityMatched) {
            return Result.error("绑定手机号或邮箱不匹配");
        }

        user.setPassword(DigestUtil.md5Hex(newPassword));
        userService.updateById(user);
        return Result.success("密码重置成功，请登录");
    }

    // 用户注册
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        if (user == null) {
            return Result.error("注册信息不能为空");
        }

        String username = user.getUsername() == null ? "" : user.getUsername().trim();
        String password = user.getPassword() == null ? "" : user.getPassword().trim();
        String nickname = user.getNickname() == null ? "" : user.getNickname().trim();
        String phone = user.getPhone() == null ? "" : user.getPhone().trim();
        String email = user.getEmail() == null ? "" : user.getEmail().trim();
        String avatar = user.getAvatar() == null ? "" : user.getAvatar().trim();

        if (!isValidUsername(username)) {
            return Result.error("用户名应为4到20位，只能包含字母、数字或下划线");
        }

        if (!isValidPassword(password)) {
            return Result.error("密码应为6到20位，且至少包含字母和数字");
        }

        if (!nickname.isEmpty() && !isValidNickname(nickname)) {
            return Result.error("昵称应为2到20位，只能包含中文、字母、数字、下划线或短横线");
        }

        if (!isValidPhone(phone)) {
            return Result.error("手机号格式不正确");
        }

        if (!isValidEmail(email)) {
            return Result.error("邮箱格式不正确");
        }

        if (!isValidAvatar(avatar)) {
            return Result.error("头像地址不合法");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (userService.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        user.setUsername(username);
        user.setPassword(DigestUtil.md5Hex(password));
        user.setNickname(nickname.isEmpty() ? username : nickname);
        user.setPhone(phone.isEmpty() ? null : phone);
        user.setEmail(email.isEmpty() ? null : email);
        user.setAvatar(avatar.isEmpty() ? null : avatar);

        // 防止前端恶意传 role/status
        user.setStatus(1);
        user.setRole(0);
        user.setCreateTime(LocalDateTime.now());

        boolean success = userService.save(user);
        return success ? Result.success("注册成功") : Result.error("注册失败");
    }

    // 获取当前登录用户信息
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserId(actualToken);
            User user = userService.getById(userId);
            if (user != null) {
                user.setPassword(null);
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(401, "未登录或登录已过期");
        }
    }

    // 分页查询用户列表
    @GetMapping("/list")
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        Page<User> page = userService.getUserList(pageNum, pageSize, username, status);
        page.getRecords().forEach(this::hidePassword);
        return Result.success(page);
    }

    // 根据ID查询用户详情
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        hidePassword(user);
        return Result.success(user);
    }

    // 更新用户状态（启用/禁用，管理员功能）
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = userService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    // 搜索用户（按用户名或昵称）
    @GetMapping("/search")
    public Result<java.util.List<User>> searchUsers(@RequestParam String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(User::getUsername, keyword).or().like(User::getNickname, keyword));
        wrapper.eq(User::getStatus, 1);
        wrapper.last("LIMIT 20");
        java.util.List<User> users = userService.list(wrapper);
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    // 推荐用户
    @GetMapping("/recommend")
    public Result<java.util.List<User>> recommendUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, 1);
        wrapper.orderByDesc(User::getCreateTime);
        wrapper.last("LIMIT 10");
        java.util.List<User> users = userService.list(wrapper);
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

//    更新个人资料
    @PutMapping("/profile")
    public Result<User> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> params) {
        try {
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtils.getUserId(actualToken);
            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            if (params.containsKey("nickname")) {
                String nickname = params.get("nickname") == null ? "" : params.get("nickname").trim();

                if (nickname.isEmpty()) {
                    return Result.error("昵称不能为空");
                }

                if (!isValidNickname(nickname)) {
                    return Result.error("昵称应为2到20位，只能包含中文、字母、数字、下划线或短横线");
                }

                user.setNickname(nickname);
            }

            if (params.containsKey("phone")) {
                String phone = params.get("phone") == null ? "" : params.get("phone").trim();

                if (!isValidPhone(phone)) {
                    return Result.error("手机号格式不正确");
                }

                user.setPhone(phone.isEmpty() ? null : phone);
            }

            if (params.containsKey("email")) {
                String email = params.get("email") == null ? "" : params.get("email").trim();

                if (!isValidEmail(email)) {
                    return Result.error("邮箱格式不正确");
                }

                user.setEmail(email.isEmpty() ? null : email);
            }

            if (params.containsKey("avatar")) {
                String avatar = params.get("avatar") == null ? "" : params.get("avatar").trim();

                if (!isValidAvatar(avatar)) {
                    return Result.error("头像地址不合法");
                }

                user.setAvatar(avatar.isEmpty() ? null : avatar);
            }

            userService.updateById(user);
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    private void hidePassword(User user) {
        if (user != null) {
            user.setPassword(null);
        }
    }

    private String randomCaptchaCode() {
        StringBuilder code = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 4; i++) {
            code.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return code.toString();
    }

    private String generateCaptchaImage(String code) {
        int width = 120;
        int height = 42;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, width, height);

        for (int i = 0; i < 8; i++) {
            g.setColor(new Color(random.nextInt(120, 220), random.nextInt(120, 220), random.nextInt(120, 220)));
            g.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }

        g.setFont(new Font("Arial", Font.BOLD, 26));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(random.nextInt(30, 90), random.nextInt(70, 130), random.nextInt(120, 190)));
            g.drawString(String.valueOf(code.charAt(i)), 18 + i * 24, 29 + random.nextInt(-3, 4));
        }
        g.dispose();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }

    private boolean verifyCaptcha(String captchaId, String captchaCode) {
        if (isBlank(captchaId) || isBlank(captchaCode)) {
            return false;
        }

        CaptchaEntry entry = CAPTCHA_STORE.remove(captchaId);
        if (entry == null || entry.expireAt < System.currentTimeMillis()) {
            return false;
        }
        return entry.code.equalsIgnoreCase(captchaCode.trim());
    }

    private record CaptchaEntry(String code, long expireAt) {
    }
}
