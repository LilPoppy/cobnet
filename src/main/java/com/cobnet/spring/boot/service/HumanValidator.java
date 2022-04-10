package com.cobnet.spring.boot.service;

import com.cobnet.common.PuzzledImage;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.HumanValidationResult;
import com.cobnet.spring.boot.dto.support.HumanValidationFailureReason;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

@Service
public class HumanValidator {

    @Async
    public <T extends Serializable> PuzzledImage createImageValidation(T key) throws IOException {
        //TODO multi image repo
        PuzzledImage image = new PuzzledImage(ImageIO.read(ProjectBeanHolder.getRandomImageProvider().getFromPicsum(256, 128).get()), 55, 45, 8, 4);

        HttpServletRequest request = ProjectBeanHolder.getCurrentHttpRequest();

        Objects.requireNonNull(ProjectBeanHolder.getRedisCacheManager().getCache(this.getClass().getSimpleName())).put(key, image);

        //TODO configuration timeout
        ProjectBeanHolder.getRedisTemplate().expire(this.getClass().getSimpleName() + "::" + key, Duration.ofMinutes(3));

        return image;
    }

    public <T extends Serializable> HumanValidationResult imageValidate(T key, double movement) {

        PuzzledImage image = Objects.requireNonNull(ProjectBeanHolder.getRedisCacheManager().getCache(this.getClass().getSimpleName())).get(key, PuzzledImage.class);

        if(image == null) {

            return new HumanValidationResult(false, HumanValidationFailureReason.TIMEOUT);
        }

        ProjectBeanHolder.getRedisCacheManager().getCache(this.getClass().getSimpleName()).evict(key);

        if(Math.abs(image.getJigsawX() - movement) > 10) {

            return new HumanValidationResult(true, HumanValidationFailureReason.NONE);
        }

        return new HumanValidationResult(false, HumanValidationFailureReason.WRONG_POSITION);
    }
}
