package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.common.ImageUtils;
import com.cobnet.common.PuzzledImage;
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
import java.util.Date;

@Service
public class HumanValidatorService {

    public <T extends Serializable> ResponseResult<HumanValidationRequestStatus> create(T key) throws IOException {

        if(!ProjectBeanHolder.getSecurityConfiguration().isSessionLimitEnable() || (ProjectBeanHolder.getCurrentHttpRequest() == null || ProjectBeanHolder.getSecurityConfiguration().getSessionCreatedTimeRequire().compareTo(DateUtils.getInterval(new Date(ProjectBeanHolder.getCurrentHttpRequest().getSession(true).getCreationTime()), DateUtils.now())) > 0)) {

            return new ResponseResult<>(HumanValidationRequestStatus.REJECTED);
        }

        if(this.isValidated(key)) {

            return new ResponseResult<>(HumanValidationRequestStatus.VALIDATED);
        }

        HumanValidationCache cache = this.getCache(key);

        if(cache != null) {

            if(DateUtils.addDuration(cache.getCreatedTime(), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationCreateInterval()).before(DateUtils.now())) {

                return generateImage(key);
            }

            return new ResponseResult<>(HumanValidationRequestStatus.INTERVAL_LIMITED);
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

        return !(cache != null && !DateUtils.addDuration(cache.getCreatedTime(), ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire()).before(DateUtils.now()));
    }

    public <T extends Serializable> boolean isValidated(T key) {

        HumanValidationCache cache = getCache(key);

        return !isExpired(key) && cache.isValidated();
    }

    private <T extends Serializable> ResponseResult<HumanValidationRequestStatus> generateImage(T key) throws IOException {

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

        return new ResponseResult<>(HumanValidationRequestStatus.SUCCESS, new ObjectWrapper<>("y-axis", image.getJigsawY()), new Base64Image(image.getImage(), "png"), new Base64Image(image.getJigsawImage(), "png"));
    }

    public <T extends Serializable> HumanValidationCache getCache(T key) {

        return ProjectBeanHolder.getCacheService().get(HumanValidationCache.HumanValidatorKey, key, HumanValidationCache.class);
    }

    public <T extends Serializable> ResponseResult<HumanValidationValidateStatus> validate(T key, double position) {

        //TODO more advance to check is human operating

        HumanValidationCache cache = getCache(key);

        if(!this.isExpired(key)) {

            try {

                PuzzledImage image = cache.getImage();

                if (Math.abs(image.getJigsawX() - position) < 8) {

                    cache.setValidated(true);

                    return new ResponseResult<>(HumanValidationValidateStatus.SUCCESS);
                }

                return new ResponseResult<>(HumanValidationValidateStatus.WRONG_POSITION);

            } finally {

                ProjectBeanHolder.getCacheService().set(HumanValidationCache.HumanValidatorKey, key, cache, ProjectBeanHolder.getSecurityConfiguration().getHumanValidationExpire());
            }
        }

        return new ResponseResult<>(HumanValidationValidateStatus.TIMEOUT);
    }
}
