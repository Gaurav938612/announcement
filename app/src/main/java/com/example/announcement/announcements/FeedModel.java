package com.example.announcement.announcements;

import java.util.Date;

public class FeedModel {
    public String clubName, clubNotificationYear, clubHeading, clubDetails, clubVenue, clubDate;
    Date date;

    public FeedModel() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getClubName() {
        return clubName;
    }

    public String getClubDate() {
        return clubDate;
    }


    public void setClubDate(String clubDate) {
        this.clubDate = (clubDate.isEmpty()) ? null: clubDate;
    }

    public void setClubName(String clubName) {
        this.clubName = (clubName.isEmpty()) ?null: clubName;
    }

    public String getClubNotificationYear() {
        return clubNotificationYear;
    }

    public void setClubNotificationYear(String clubNotificationYear) {
        this.clubNotificationYear = (clubNotificationYear.isEmpty())? null : clubNotificationYear;
    }

    public String getClubHeading() {
        return clubHeading;
    }

    public void setClubHeading(String clubHeading) {
        this.clubHeading = (clubHeading.isEmpty())?null: clubHeading;
    }

    public String getClubDetails() {
        return clubDetails;
    }

    public void setClubDetails(String clubDetails) {
        this.clubDetails =(clubDetails.isEmpty())?null: clubDetails;
    }

    public String getClubVenue() {
        return clubVenue;
    }

    public void setClubVenue(String clubVenue) {
        this.clubVenue = (clubVenue.isEmpty())?null: clubVenue;
    }
}
