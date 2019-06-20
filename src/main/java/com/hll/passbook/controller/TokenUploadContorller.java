package com.hll.passbook.controller;

import com.hll.passbook.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *<h1> PassTemplate Token Unload </h1>
 */
@Slf4j
@Controller
public class TokenUploadContorller {
    /** redis 客户端 */
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public TokenUploadContorller(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/token")
    public String tokenFileUpload(@RequestParam("merchantsId") String merchantsId,
                                  @RequestParam("passTemplateId") String passTemplateId,
                                  @RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) {
        if (passTemplateId == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "passTemplateId is null or file is empty!");
            return "redirect:/uploadStatus";
        }

        File cur = new File(Constants.TOKEN_DIR, merchantsId);
        if (!cur.exists()) {
            log.info("Create File: {}", cur.mkdir());
        }

        Path path = Paths.get(Constants.TOKEN_DIR, merchantsId, passTemplateId);
        try {
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (writeTokenToRedis(path, passTemplateId)) {
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Write token error.");
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

    /**
     * <h2>将 Token 写入到 Redis </h2>
     * @param path {@link Path}
     * @param key redis key
     * @return true/false
     */
    private Boolean writeTokenToRedis(Path path, String key) {
        Set<String> tokens;

        try(Stream<String> stream = Files.lines(path)) {
            tokens = stream.collect(Collectors.toSet());
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        if (CollectionUtils.isEmpty(tokens)) {
            return false;
        }

        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory == null) {
            return false;
        }
        RedisClusterConnection clusterConnection = connectionFactory.getClusterConnection();
        for (String token : tokens) {
            clusterConnection.sAdd(key.getBytes(), token.getBytes());
        }

        return true;
    }
}
