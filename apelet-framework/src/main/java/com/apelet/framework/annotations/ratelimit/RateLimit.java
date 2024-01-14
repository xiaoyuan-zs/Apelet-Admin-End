package com.apelet.framework.annotations.ratelimit;

import cn.hutool.extra.servlet.ServletUtil;
import com.apelet.common.exception.ApiException;
import com.apelet.common.exception.error.ErrorCode;
import com.apelet.common.user.app.AppLoginUser;
import com.apelet.common.user.web.SystemLoginUser;
import com.apelet.common.utils.ServletHolderUtil;
import com.apelet.framework.security.AuthenticationUtils;

import java.lang.annotation.*;

/**
 * 限流注解
 *
 * @author valarchie
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流key
     */
    String key() default "None";

    /**
     * 限流时间,单位秒
     */
    int time() default 60;

    /**
     * 限流次数
     */
    int maxCount() default 100;

    /**
     * 限流条件类型
     */
    LimitType limitType() default LimitType.GLOBAL;

    /**
     * 限流使用的缓存类型
     */
    CacheType cacheType() default CacheType.REDIS;



    enum LimitType {
        /**
         * 默认策略全局限流  不区分IP和用户
         */
        GLOBAL{
            @Override
            public String generateCombinedKey(RateLimit rateLimiter) {
                return rateLimiter.key() + this.name();
            }
        },

        /**
         * 根据请求者IP进行限流
         */
        IP {
            @Override
            public String generateCombinedKey(RateLimit rateLimiter) {
                String clientIP = ServletUtil.getClientIP(ServletHolderUtil.getRequest());
                return rateLimiter.key() + clientIP;
            }
        },

        /**
         * 按Web用户限流
         */
        SYSTEM_USER {
            @Override
            public String generateCombinedKey(RateLimit rateLimiter) {
                SystemLoginUser loginUser = AuthenticationUtils.getSystemLoginUser();
                if (loginUser == null) {
                    throw new ApiException(ErrorCode.Client.COMMON_NO_AUTHORIZATION);
                }
                return rateLimiter.key() + loginUser.getUsername();
            }
        },

        /**
         * 按App用户限流
         */
        APP_USER {
            @Override
            public String generateCombinedKey(RateLimit rateLimiter) {
                AppLoginUser loginUser = AuthenticationUtils.getAppLoginUser();
                if (loginUser == null) {
                    throw new ApiException(ErrorCode.Client.COMMON_NO_AUTHORIZATION);
                }
                return rateLimiter.key() + loginUser.getUsername();
            }
        };


        public abstract String generateCombinedKey(RateLimit rateLimiter);

    }

    enum CacheType {

        /**
         * 使用redis做缓存
         */
        REDIS,

        /**
         * 使用map做缓存
         */
        Map

    }

}
