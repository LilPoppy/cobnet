package com.cobnet.spring.boot.service;

import com.cobnet.common.PuzzledImage;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.Base64Image;
import com.cobnet.spring.boot.dto.HumanValidationRequire;
import com.cobnet.spring.boot.dto.HumanValidationResult;
import com.cobnet.spring.boot.dto.support.HumanValidationFailureReason;
import com.cobnet.spring.boot.dto.support.HumanValidationRequireResult;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

@Service
public class HumanValidator {

    public static final String VALIDATED_KEY = "VALIDATED";

    public <T extends Serializable> Duration getIntervalLeft(T key) {

        PuzzledImage image = getPuzzledImage(key);

        if(image == null) {

            return Duration.ZERO;
        }

        long ms = System.currentTimeMillis() - image.getCreatedTime().getTime();

        return ms > 0 ? Duration.ofMillis(ms) : Duration.ZERO;
    }


    public <T extends Serializable> HumanValidationRequire createImageValidation(T key) throws IOException {

        if(!this.getIntervalLeft(key).isZero()) {

            return new HumanValidationRequire(HumanValidationRequireResult.INTERVAL_LIMITED);
        }

        PuzzledImage image = new PuzzledImage(ImageIO.read(ProjectBeanHolder.getRandomImageProvider().getFromPicsum(256, 128).get()), 55, 45, 8, 4);

        Objects.requireNonNull(ProjectBeanHolder.getRedisCacheManager().getCache(this.getClass().getSimpleName())).put(key, image);

        ProjectBeanHolder.getRedisTemplate().expire(this.getClass().getSimpleName() + "::" + key, ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

        return new HumanValidationRequire(HumanValidationRequireResult.SUCCESS, new Base64Image(image.getImage(), "png"), new Base64Image(image.getJigsawImage(), "png"));
    }

    public <T extends Serializable> PuzzledImage getPuzzledImage(T key) {

        return Objects.requireNonNull(ProjectBeanHolder.getRedisCacheManager().getCache(this.getClass().getSimpleName())).get(key, PuzzledImage.class);
    }

    public <T extends Serializable> HumanValidationResult imageValidate(T key, double movement) {

        PuzzledImage image = getPuzzledImage(key);

        if(image == null) {

            return new HumanValidationResult(false, HumanValidationFailureReason.TIMEOUT);
        }

        ProjectBeanHolder.getRedisCacheManager().getCache(this.getClass().getSimpleName()).evict(key);
        System.out.println(image.getJigsawX());
        System.out.println(Math.abs(image.getJigsawX() - movement));
        if(Math.abs(image.getJigsawX() - movement) < 10) {

            ProjectBeanHolder.getRedisCacheManager().getCache(this.getClass().getSimpleName()).put(key + "::" + HumanValidator.VALIDATED_KEY, true);

            ProjectBeanHolder.getRedisTemplate().expire(this.getClass().getSimpleName() + "::" + key + "::" + HumanValidator.VALIDATED_KEY, ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

            return new HumanValidationResult(true, HumanValidationFailureReason.NONE);
        }

        return new HumanValidationResult(false, HumanValidationFailureReason.WRONG_POSITION);
    }
}
