package com.apelet.admin.customize.service.login;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.apelet.common.enums.BasicEnumUtil;
import com.apelet.common.enums.common.DataScopeEnum;
import com.apelet.common.enums.common.UserStatusEnum;
import com.apelet.common.exception.ApiException;
import com.apelet.common.exception.error.ErrorCode;
import com.apelet.common.user.web.RoleInfo;
import com.apelet.common.user.web.SystemLoginUser;
import com.apelet.domain.system.menu.db.SysMenuEntity;
import com.apelet.domain.system.menu.db.SysMenuService;
import com.apelet.domain.system.role.db.SysRoleEntity;
import com.apelet.domain.system.role.db.SysRoleService;
import com.apelet.domain.system.user.db.SysUserEntity;
import com.apelet.domain.system.user.db.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 自定义加载用户信息通过用户名
 * 用于SpringSecurity 登录流程
 * @see com.apelet.framework.config.SecurityConfig#filterChain(HttpSecurity)
 * @author xiaoyuan-zs
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserService userService;

    private final SysMenuService menuService;

    private final SysRoleService roleService;

    private final TokenService tokenService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserEntity userEntity = userService.getUserByUserName(username);
        if (userEntity == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new ApiException(ErrorCode.Business.USER_NON_EXIST, username);
        }
        if (!Objects.equals(UserStatusEnum.NORMAL.getValue(), userEntity.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ApiException(ErrorCode.Business.USER_IS_DISABLE, username);
        }

        RoleInfo roleInfo = getRoleInfo(userEntity.getRoleId(), userEntity.getIsAdmin());

        SystemLoginUser loginUser = new SystemLoginUser(userEntity.getUserId(), userEntity.getIsAdmin(), userEntity.getUsername(),
            userEntity.getPassword(), roleInfo, userEntity.getDeptId());
        loginUser.fillLoginInfo();
        loginUser.setAutoRefreshCacheTime(loginUser.getLoginInfo().getLoginTime()
            + TimeUnit.MINUTES.toMillis(tokenService.getAutoRefreshTime()));
        return loginUser;
    }

    public RoleInfo getRoleInfo(Long roleId, boolean isAdmin) {
        if (roleId == null) {
            return RoleInfo.EMPTY_ROLE;
        }

        if (isAdmin) {
            LambdaQueryWrapper<SysMenuEntity> menuQuery = Wrappers.lambdaQuery();
            menuQuery.select(SysMenuEntity::getMenuId);
            List<SysMenuEntity> allMenus = menuService.list(menuQuery);

            Set<Long> allMenuIds = allMenus.stream().map(SysMenuEntity::getMenuId).collect(Collectors.toSet());

            return new RoleInfo(RoleInfo.ADMIN_ROLE_ID, RoleInfo.ADMIN_ROLE_KEY, DataScopeEnum.ALL, SetUtils.emptySet(),
                RoleInfo.ADMIN_PERMISSIONS, allMenuIds);

        }

        SysRoleEntity roleEntity = roleService.getById(roleId);

        if (roleEntity == null) {
            return RoleInfo.EMPTY_ROLE;
        }

        List<SysMenuEntity> menuList = roleService.getMenuListByRoleId(roleId);

        Set<Long> menuIds = menuList.stream().map(SysMenuEntity::getMenuId).collect(Collectors.toSet());
        Set<String> permissions = menuList.stream().map(SysMenuEntity::getPermission).collect(Collectors.toSet());

        DataScopeEnum dataScopeEnum = BasicEnumUtil.fromValue(DataScopeEnum.class, roleEntity.getDataScope());

        Set<Long> deptIdSet = SetUtils.emptySet();
        if (StrUtil.isNotEmpty(roleEntity.getDeptIdSet())) {
            deptIdSet = StrUtil.split(roleEntity.getDeptIdSet(), ",").stream()
                .map(Convert::toLong).collect(Collectors.toSet());
        }

        return new RoleInfo(roleId, roleEntity.getRoleKey(), dataScopeEnum, deptIdSet, permissions, menuIds);
    }


}
