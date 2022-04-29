package com.cobnet.spring.boot.dto.support;

public enum SurveyReferralOption {

    SEARCH_ENGINE("survey.referral.search-engine"),
    RECOMMENDATION("survey.referral.recommendation"),
    SOCIAL_MEDIA("survey.referral.social-media"),
    BLOG("survey.referral.blog"),
    OTHER("survey.referral.other");

    private String key;

    private SurveyReferralOption(String key) {
        this.key = key;
    }

    public String getKey() {

        return this.key;
    }
}
