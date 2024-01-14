package com.apelet.framework.mybatisplus;

import com.apelet.common.user.web.SystemLoginUser;
import com.apelet.framework.security.AuthenticationUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Mybatis Plus允许在插入或者更新的时候
 * 自定义设定值
 * @author valarchie
 */
@Component
@Slf4j
public class CustomMetaObjectHandler implements MetaObjectHandler {

    public static final String CREATE_TIME_FIELD = "createTime";
    public static final String CREATOR_ID_FIELD = "creatorId";

    public static final String UPDATE_TIME_FIELD = "updateTime";
    public static final String UPDATER_ID_FIELD = "updaterId";


    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter(CREATE_TIME_FIELD)) {
            this.setFieldValByName(CREATE_TIME_FIELD, new Date(), metaObject);
        }

        Long userId = getUserIdSafely();
        if (metaObject.hasSetter(CREATOR_ID_FIELD) && userId != null) {
            this.strictInsertFill(metaObject, CREATOR_ID_FIELD, Long.class, getUserIdSafely());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasSetter(UPDATE_TIME_FIELD)) {
            this.setFieldValByName(UPDATE_TIME_FIELD, new Date(), metaObject);
        }

        Long userId = getUserIdSafely();
        if (metaObject.hasSetter(UPDATER_ID_FIELD) && userId != null) {
            this.strictUpdateFill(metaObject, UPDATER_ID_FIELD, Long.class, getUserIdSafely());
        }
    }

    public Long getUserIdSafely() {
        Long userId = null;
        try {
            SystemLoginUser loginUser = AuthenticationUtils.getSystemLoginUser();
            userId = loginUser.getUserId();
        } catch (Exception e) {
            log.warn("can not find user in current thread.");
        }
        return userId;
    }

}
