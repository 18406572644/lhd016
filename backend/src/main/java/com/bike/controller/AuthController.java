package com.bike.controller;

import com.bike.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", "bike-admin-token-" + System.currentTimeMillis());
            data.put("username", "admin");
            data.put("name", "系统管理员");
            return Result.success("登录成功", data);
        }
        return Result.error(401, "用户名或密码错误");
    }

    @ApiOperation("登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.successMsg("登出成功");
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", "admin");
        data.put("name", "系统管理员");
        data.put("avatar", "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        data.put("roles", new String[]{"admin"});
        return Result.success(data);
    }

    public static class LoginRequest implements Serializable {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
