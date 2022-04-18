package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.common.ImageUtils;
import com.cobnet.common.PuzzledImage;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.AutocompleteResult;
import com.cobnet.spring.boot.dto.Base64Image;
import com.cobnet.spring.boot.dto.HumanValidationRequestResult;
import com.cobnet.spring.boot.dto.HumanValidationValidate;
import com.cobnet.spring.boot.dto.support.AutocompleteResultStatus;
import com.cobnet.spring.boot.dto.support.HumanValidationRequestStatus;
import com.cobnet.spring.boot.dto.support.HumanValidationValidateStatus;
import com.cobnet.spring.boot.service.support.HumanValidationCache;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

@Service
public class HumanValidator {

    public <T extends Serializable> HumanValidationRequestResult create(T key) throws IOException {

        if(!ProjectBeanHolder.getSecurityConfiguration().isSessionLimitEnable() || (ProjectBeanHolder.getCurrentHttpRequest() == null || ProjectBeanHolder.getSecurityConfiguration().getSessionCreatedTimeRequire().compareTo(DateUtils.getInterval(new Date(ProjectBeanHolder.getCurrentHttpRequest().getSession(true).getCreationTime()), DateUtils.now())) > 0)) {

            return new HumanValidationRequestResult(HumanValidationRequestStatus.REJECTED);
        }

        HumanValidationCache cache = this.getCache(key);

        if(cache != null) {

            if(DateUtils.addDuration(cache.getCreatedTime(), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationCreateInterval()).before(DateUtils.now())) {

                return generateImage(key);
            }

            return new HumanValidationRequestResult(HumanValidationRequestStatus.INTERVAL_LIMITED);
        }

        return generateImage(key);
    }

    private boolean isDarkImage(BufferedImage image) {

        float[] hsv = ImageUtils.getImageAverageHSV(image);

        BufferedImage grayscale = ImageUtils.toGrayscale(image);

        return ImageUtils.getLuminance(grayscale) <= 50 && hsv[2] <= 0.150f;
    }

    public <T extends Serializable> boolean isValidated(T key) {

        HumanValidationCache cache = getCache(key);

        return cache != null && !DateUtils.addDuration(cache.getCreatedTime(), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire()).before(DateUtils.now());
    }

    private <T extends Serializable> HumanValidationRequestResult generateImage(T key) throws IOException {

        //TODO create image provider upstream pool

        BufferedImage pulled = ImageIO.read(ProjectBeanHolder.getRandomImageProvider().getFromPicsum(256, 128).get());

        if(this.isDarkImage(pulled)) {

            return generateImage(key);
        }

        PuzzledImage image = new PuzzledImage(pulled, 55, 45, 8, 4);

        if(this.isDarkImage(image.getJigsawImage())) {

            return generateImage(key);
        }

        ProjectBeanHolder.getCacheService().set(HumanValidationCache.HumanValidatorKey, key, new HumanValidationCache(image, DateUtils.now(), false), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());

        return new HumanValidationRequestResult(HumanValidationRequestStatus.SUCCESS, image.getJigsawY(), new Base64Image(image.getImage(), "png"), new Base64Image(image.getJigsawImage(), "png"));
    }

    public <T extends Serializable> HumanValidationCache getCache(T key) {

        return ProjectBeanHolder.getCacheService().get(HumanValidationCache.HumanValidatorKey, key, HumanValidationCache.class);
    }

    public <T extends Serializable> HumanValidationValidate validate(T key, double position) {

        //TODO more advance to check is human operating

        HumanValidationCache cache = getCache(key);

        if(this.isValidated(key)) {

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
