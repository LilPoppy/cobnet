package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.common.ImageUtils;
import com.cobnet.common.PuzzledImage;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.spring.boot.dto.support.HumanValidationRequestStatus;
import com.cobnet.spring.boot.dto.support.HumanValidationValidateStatus;
import com.cobnet.spring.boot.service.support.HumanValidationCache;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

@Service
public class HumanValidatorService {

    public <T extends Serializable> PuzzledImage create(T key) throws IOException, ResponseFailureStatusException {

        if(this.isValidated(key)) {

            throw new ResponseFailureStatusException(HumanValidationRequestStatus.VALIDATED);
        }

        HumanValidationCache cache = this.getCache(key);

        if(cache != null) {

            if(cache.count() < ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getInitialCount() || DateUtils.addDuration(cache.creationTime(), ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getCreateInterval()).before(DateUtils.now())) {

                return generateImage(key);
            }

            throw new ResponseFailureStatusException(HumanValidationRequestStatus.INTERVAL_LIMITED, new ObjectWrapper<>("time-remain", ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getCreateInterval().minus(DateUtils.getInterval(DateUtils.now(), cache.creationTime()))));
        }

        return generateImage(key);
    }

    private boolean isDarkImage(BufferedImage image) {

        float[] hsv = ImageUtils.getImageAverageHSV(image);

        BufferedImage grayscale = ImageUtils.toGrayscale(image);

        return ImageUtils.getLuminance(grayscale) <= 50 && hsv[2] <= 0.150f;
    }

    public <T extends Serializable> boolean isExpired(T key) {

        HumanValidationCache cache = getCache(key);

        return !(cache != null && !DateUtils.addDuration(cache.creationTime(), ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getExpire()).before(DateUtils.now()));
    }

    public <T extends Serializable> boolean isValidated(T key) {

        HumanValidationCache cache = getCache(key);

        return !isExpired(key) && cache.isValidated();
    }

    private <T extends Serializable> PuzzledImage generateImage(T key) throws IOException {

        //TODO create image provider upstream pool

        BufferedImage pulled = ImageIO.read(ProjectBeanHolder.getRandomImageProvider().getFromPicsum(256, 128).get());

        if(this.isDarkImage(pulled)) {

            return generateImage(key);
        }

        PuzzledImage image = new PuzzledImage(pulled, 55, 45, 8, 4);

        if(this.isDarkImage(image.getJigsawImage())) {

            return generateImage(key);
        }

        HumanValidationCache cache = this.getCache(key);

        ProjectBeanHolder.getCacheService().set(HumanValidationCache.HumanValidatorKey, key, new HumanValidationCache(image, DateUtils.now(),cache != null ? cache.count() + 1 : 1, false), ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getExpire());

        return image;
    }

    public <T extends Serializable> HumanValidationCache getCache(T key) {

        return ProjectBeanHolder.getCacheService().get(HumanValidationCache.HumanValidatorKey, HumanValidationCache.class, key);
    }

    public <T extends Serializable> boolean validate(T key, double position) throws ResponseFailureStatusException {

        //TODO more advance to check is human operating

        HumanValidationCache cache = getCache(key);

        if(!this.isExpired(key)) {

            try {

                PuzzledImage image = cache.getImage();

                if (Math.abs(image.getJigsawX() - position) < 8) {

                    cache.setValidated(true);

                    return true;
                }

                throw new ResponseFailureStatusException(HumanValidationValidateStatus.WRONG_POSITION);

            } finally {

                ProjectBeanHolder.getCacheService().set(HumanValidationCache.HumanValidatorKey, key, cache, ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getExpire());
            }
        }

        throw new ResponseFailureStatusException(HumanValidationValidateStatus.TIMEOUT);
    }
}
