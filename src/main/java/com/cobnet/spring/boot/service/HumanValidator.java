package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.common.ImageUtils;
import com.cobnet.common.PuzzledImage;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.Base64Image;
import com.cobnet.spring.boot.dto.HumanValidationRequest;
import com.cobnet.spring.boot.dto.HumanValidationValidate;
import com.cobnet.spring.boot.dto.support.HumanValidationValidateStatus;
import com.cobnet.spring.boot.dto.support.HumanValidationRequestStatus;
import com.cobnet.spring.boot.service.support.HumanValidationCache;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

@Service
public class HumanValidator {

    public <T extends Serializable> HumanValidationRequest createImageValidation(T key) throws IOException {

        HumanValidationCache cache = this.getCache(key);

        if(cache != null) {

            if(DateUtils.addDuration(cache.getCreatedTime(), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationCreateInterval()).before(DateUtils.now())) {

                return generateImage(key);
            }

            return new HumanValidationRequest(HumanValidationRequestStatus.INTERVAL_LIMITED);
        }

        return generateImage(key);

    }

    private boolean isDarkImage(BufferedImage image) {

        float[] hsv = ImageUtils.getImageAverageHSV(image);

        BufferedImage grayscale = ImageUtils.toGrayscale(image);

        return ImageUtils.getLuminance(grayscale) <= 30 && hsv[2] <= 0.125f;
    }

    private <T extends Serializable> HumanValidationRequest generateImage(T key) throws IOException {

        System.out.println("aaa");

        BufferedImage pulled = ImageIO.read(ProjectBeanHolder.getRandomImageProvider().getFromPicsum(256, 128).get());

        if(this.isDarkImage(pulled)) {

            return generateImage(key);
        }

        PuzzledImage image = new PuzzledImage(pulled, 55, 45, 8, 4);

        if(this.isDarkImage(image.getJigsawImage())) {

            return generateImage(key);
        }

        ProjectBeanHolder.getCacheService().set(HumanValidationCache.HumanValidatorKey, key, new HumanValidationCache(image, DateUtils.now(), false, false), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

        return new HumanValidationRequest(HumanValidationRequestStatus.SUCCESS, image.getJigsawY(), new Base64Image(image.getImage(), "png"), new Base64Image(image.getJigsawImage(), "png"));
    }

    public <T extends Serializable> HumanValidationCache getCache(T key) {

        return ProjectBeanHolder.getCacheService().get(HumanValidationCache.HumanValidatorKey, key, HumanValidationCache.class);
    }

    public <T extends Serializable> HumanValidationValidate imageValidate(T key, double position) {

        //TODO more advance to check is human operating

        HumanValidationCache cache = getCache(key);

        if(cache != null && DateUtils.addDuration(cache.getCreatedTime(), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire()).before(DateUtils.now())) {

            try {

                PuzzledImage image = cache.getImage();

                if (Math.abs(image.getJigsawX() - position) < 8) {

                    cache.setValidated(true);

                    return new HumanValidationValidate(HumanValidationValidateStatus.SUCCESS);
                }

                return new HumanValidationValidate(HumanValidationValidateStatus.WRONG_POSITION);

            } finally {

                ProjectBeanHolder.getCacheService().set(HumanValidationCache.HumanValidatorKey, key, cache, ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

            }
        }

        return new HumanValidationValidate(HumanValidationValidateStatus.TIMEOUT);
    }
}
