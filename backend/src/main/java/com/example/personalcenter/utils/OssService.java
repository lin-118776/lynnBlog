package com.example.personalcenter.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * 阿里云 OSS 上传服务：配置缺失或未启用时自动降级为不可用（upload 返回 null，由调用方回退本地存储）
 */
@Slf4j
@Component
public class OssService {

    private final boolean enabled;
    private final String urlPrefix;
    private final OSS client;
    private final String bucket;

    public OssService(@Value("${oss.enabled:false}") boolean enabled,
                      @Value("${oss.endpoint:}") String endpoint,
                      @Value("${oss.bucket:}") String bucket,
                      @Value("${oss.access-key:}") String accessKey,
                      @Value("${oss.access-secret:}") String accessSecret,
                      @Value("${oss.url-prefix:}") String urlPrefix) {
        boolean ready = enabled
                && StringUtils.hasText(endpoint)
                && StringUtils.hasText(bucket)
                && StringUtils.hasText(accessKey)
                && StringUtils.hasText(accessSecret)
                && StringUtils.hasText(urlPrefix);
        this.enabled = ready;
        this.bucket = bucket;
        this.urlPrefix = urlPrefix;
        if (ready) {
            this.client = new OSSClientBuilder().build(endpoint, accessKey, accessSecret);
            log.info("OSS 已启用：bucket={}, urlPrefix={}", bucket, urlPrefix);
        } else {
            this.client = null;
            log.warn("OSS 配置缺失或未启用，文件将存本地");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 上传文件流到 OSS，返回对象路径（不含前缀），如 article/2026/09/xxx.jpg
     */
    public String upload(InputStream in, String objectKey, String contentType) {
        if (!enabled || client == null) {
            throw new IllegalStateException("OSS 未启用");
        }
        try {
            com.aliyun.oss.model.ObjectMetadata meta = new com.aliyun.oss.model.ObjectMetadata();
            if (StringUtils.hasText(contentType)) {
                meta.setContentType(contentType);
            }
            client.putObject(bucket, objectKey, in, meta);
            return objectKey;
        } catch (Exception e) {
            log.error("OSS 上传失败：objectKey={}", objectKey, e);
            throw new RuntimeException("OSS 上传失败：" + e.getMessage(), e);
        }
    }

    /** 拼接完整访问 URL */
    public String buildUrl(String objectKey) {
        return urlPrefix.replaceAll("/+$", "") + "/" + objectKey;
    }

    /** 删除对象（尽力而为，不抛异常） */
    public void delete(String objectKey) {
        if (!enabled || client == null || !StringUtils.hasText(objectKey)) {
            return;
        }
        try {
            client.deleteObject(bucket, objectKey);
        } catch (Exception e) {
            log.warn("OSS 删除失败：objectKey={}", objectKey, e);
        }
    }
}
