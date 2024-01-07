package com.apelet.admin.config;


import com.apelet.admin.ApeletAdminApplication;
import com.apelet.common.config.AgileBootConfig;
import com.apelet.common.constant.Constants.UploadSubDir;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;

@SpringBootTest(classes = ApeletAdminApplication.class)
@RunWith(SpringRunner.class)
public class AgileBootConfigTest {

    @Resource
    private AgileBootConfig config;

    @Test
    public void testConfig() {
        String fileBaseDir = "D:\\agileboot\\profile";

        Assertions.assertEquals("AgileBoot", config.getName());
        Assertions.assertEquals("1.0.0", config.getVersion());
        Assertions.assertEquals("2022", config.getCopyrightYear());
        Assertions.assertTrue(config.isDemoEnabled());
        Assertions.assertEquals(fileBaseDir, AgileBootConfig.getFileBaseDir());
        Assertions.assertFalse(AgileBootConfig.isAddressEnabled());
        Assertions.assertEquals("math", AgileBootConfig.getCaptchaType());
        Assertions.assertEquals("math", AgileBootConfig.getCaptchaType());
        Assertions.assertEquals(fileBaseDir + "\\import",
            AgileBootConfig.getFileBaseDir() + File.separator + UploadSubDir.IMPORT_PATH);
        Assertions.assertEquals(fileBaseDir + "\\avatar",
            AgileBootConfig.getFileBaseDir() + File.separator + UploadSubDir.AVATAR_PATH);
        Assertions.assertEquals(fileBaseDir + "\\download",
            AgileBootConfig.getFileBaseDir() + File.separator + UploadSubDir.DOWNLOAD_PATH);
        Assertions.assertEquals(fileBaseDir + "\\upload",
            AgileBootConfig.getFileBaseDir() + File.separator + UploadSubDir.UPLOAD_PATH);
    }

}