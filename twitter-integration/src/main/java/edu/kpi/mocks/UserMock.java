package edu.kpi.mocks;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class UserMock implements User {

    public static final int BOUND_ID = 100000;
    private static final Random RANDOM = new Random();
    public static final String MOCK_NAME = "Mock";
    private static final String FILENAME = "twitter-integration/src/main/resources/mock-names.txt";
    private static final String IMAGE_URL = "https://images.app.goo.gl/PSLcrnzQzyzggC5v8";

    @Override
    public long getId() {
        return RANDOM.nextInt(BOUND_ID);
    }

    @Override
    public String getName() {
        try (BufferedInputStream ignored = new BufferedInputStream(new FileInputStream(FILENAME))) {
            List<String> names = Files.lines(Path.of(FILENAME)).collect(Collectors.toList());
            return names.get(RANDOM.nextInt(names.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MOCK_NAME;
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getScreenName() {
        return "User" + getId();
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean isContributorsEnabled() {
        return false;
    }

    @Override
    public String getProfileImageURL() {
        return null;
    }

    @Override
    public String getBiggerProfileImageURL() {
        return IMAGE_URL;
    }

    @Override
    public String getMiniProfileImageURL() {
        return null;
    }

    @Override
    public String getOriginalProfileImageURL() {
        return null;
    }

    @Override
    public String get400x400ProfileImageURL() {
        return null;
    }

    @Override
    public String getProfileImageURLHttps() {
        return null;
    }

    @Override
    public String getBiggerProfileImageURLHttps() {
        return null;
    }

    @Override
    public String getMiniProfileImageURLHttps() {
        return null;
    }

    @Override
    public String getOriginalProfileImageURLHttps() {
        return null;
    }

    @Override
    public String get400x400ProfileImageURLHttps() {
        return null;
    }

    @Override
    public boolean isDefaultProfileImage() {
        return false;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public int getFollowersCount() {
        return 0;
    }

    @Override
    public Status getStatus() {
        return null;
    }

    @Override
    public String getProfileBackgroundColor() {
        return null;
    }

    @Override
    public String getProfileTextColor() {
        return null;
    }

    @Override
    public String getProfileLinkColor() {
        return null;
    }

    @Override
    public String getProfileSidebarFillColor() {
        return null;
    }

    @Override
    public String getProfileSidebarBorderColor() {
        return null;
    }

    @Override
    public boolean isProfileUseBackgroundImage() {
        return false;
    }

    @Override
    public boolean isDefaultProfile() {
        return false;
    }

    @Override
    public boolean isShowAllInlineMedia() {
        return false;
    }

    @Override
    public int getFriendsCount() {
        return 0;
    }

    @Override
    public Date getCreatedAt() {
        return null;
    }

    @Override
    public int getFavouritesCount() {
        return 0;
    }

    @Override
    public int getUtcOffset() {
        return 0;
    }

    @Override
    public String getTimeZone() {
        return null;
    }

    @Override
    public String getProfileBackgroundImageURL() {
        return null;
    }

    @Override
    public String getProfileBackgroundImageUrlHttps() {
        return null;
    }

    @Override
    public String getProfileBannerURL() {
        return null;
    }

    @Override
    public String getProfileBannerRetinaURL() {
        return null;
    }

    @Override
    public String getProfileBannerIPadURL() {
        return null;
    }

    @Override
    public String getProfileBannerIPadRetinaURL() {
        return null;
    }

    @Override
    public String getProfileBannerMobileURL() {
        return null;
    }

    @Override
    public String getProfileBannerMobileRetinaURL() {
        return null;
    }

    @Override
    public String getProfileBanner300x100URL() {
        return null;
    }

    @Override
    public String getProfileBanner600x200URL() {
        return null;
    }

    @Override
    public String getProfileBanner1500x500URL() {
        return null;
    }

    @Override
    public boolean isProfileBackgroundTiled() {
        return false;
    }

    @Override
    public String getLang() {
        return null;
    }

    @Override
    public int getStatusesCount() {
        return 0;
    }

    @Override
    public boolean isGeoEnabled() {
        return false;
    }

    @Override
    public boolean isVerified() {
        return false;
    }

    @Override
    public boolean isTranslator() {
        return false;
    }

    @Override
    public int getListedCount() {
        return 0;
    }

    @Override
    public boolean isFollowRequestSent() {
        return false;
    }

    @Override
    public URLEntity[] getDescriptionURLEntities() {
        return new URLEntity[0];
    }

    @Override
    public URLEntity getURLEntity() {
        return null;
    }

    @Override
    public String[] getWithheldInCountries() {
        return new String[0];
    }

    @Override
    public int compareTo(User o) {
        return 0;
    }

    @Override
    public RateLimitStatus getRateLimitStatus() {
        return null;
    }

    @Override
    public int getAccessLevel() {
        return 0;
    }
}
