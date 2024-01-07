package com.apelet.api.controller.app;

import com.apelet.api.customize.service.JwtTokenService;
import com.apelet.common.core.base.BaseController;
import com.apelet.common.core.dto.ResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调度日志操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/app")
@AllArgsConstructor
public class AppController extends BaseController {

    private final JwtTokenService jwtTokenService;

    /**
     * 访问首页，提示语
     */
    @PreAuthorize("hasAuthority('annie')")
    @GetMapping("/list")
    public ResponseDTO<?> appLogin() {
        return ResponseDTO.ok();
    }







}
