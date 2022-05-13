package com.cobnet.spring.boot.service;

import com.cobnet.common.DateUtils;
import com.cobnet.common.ImageUtils;
import com.cobnet.common.PuzzledImage;
import com.cobnet.exception.ResponseFailureStatusException;
import com.cobnet.interfaces.spring.repository.HumanValidationCacheRepository;
import com.cobnet.spring.boot.cache.HumanValidationCache;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.*;
import com.cobnet.exception.support.HumanValidationRequestStatus;
import com.cobnet.exception.support.HumanValidationValidateStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

@Service
public class HumanValidatorService {

    @Autowired
    private HumanValidationCacheRepository repository;

    public PuzzledImage create(String key) throws IOException, ResponseFailureStatusException {

        if(this.isValidated(key)) {

            throw new ResponseFailureStatusException(HumanValidationRequestStatus.VALIDATED);
        }

        HumanValidationCache cache = this.getCache(key);

        if(cache != null) {

            if(cache.getCount() < ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getInitialCount() || DateUtils.addDuration(cache.getCreationTime(), ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getCreateInterval()).before(DateUtils.now())) {

                return generateImage(key);
            }

            throw new ResponseFailureStatusException(HumanValidationRequestStatus.INTERVAL_LIMITED, new MessageWrapper("time-remain", ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getCreateInterval().minus(DateUtils.getInterval(DateUtils.now(), cache.getCreationTime()))));
        }

        return generateImage(key);
    }

    private boolean isDarkImage(BufferedImage image) {

        float[] hsv = ImageUtils.getImageAverageHSV(image);

        BufferedImage grayscale = ImageUtils.toGrayscale(image);

        return ImageUtils.getLuminance(grayscale) <= 50 && hsv[2] <= 0.150f;
    }

    public boolean isExpired(String key) {

        HumanValidationCache cache = getCache(key);

        return !(cache != null && !DateUtils.addDuration(cache.getCreationTime(), ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getExpire()).before(DateUtils.now()));
    }

    public boolean isValidated(String key) {

        HumanValidationCache cache = getCache(key);

        return !isExpired(key) && cache.isValidated();
    }

    private PuzzledImage generateImage(String key) throws IOException {

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

        repository.save(new HumanValidationCache(key, image, DateUtils.now(), cache != null ? cache.getCount() + 1 : 1, false), ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getExpire());

        return image;
    }

    public HumanValidationCache getCache(String key) {

        Optional<HumanValidationCache> optional = repository.findById(key);

        if(optional.isEmpty()) {

            return null;
        }

        return optional.get();
    }

    public boolean validate(String key, double position) throws ResponseFailureStatusException {

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

                repository.save(cache, ProjectBeanHolder.getSecurityConfiguration().getHumanValidation().getExpire());
            }
        }

        throw new ResponseFailureStatusException(HumanValidationValidateStatus.TIMEOUT);
    }
}
