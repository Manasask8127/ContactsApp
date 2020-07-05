package com.example.manasask.contactappdemo.model;

public class UserContact {
   /* public UserContact( String contactUserFirstName, String contactUserLastName, String contactUserAddress, String contactUserPhone) {
        this.contactUserFirstName = contactUserFirstName;
        this.contactUserLastName = contactUserLastName;
        this.contactUserAddress = contactUserAddress;
        this.contactUserPhone = contactUserPhone;
    }*/

    String contactUserId;
    byte[] contactUserImage;
    String contactUserFirstName;
    String contactUserLastName;
    String contactUserAddress;
    String contactUserPhone;
    String contactUserSecond;

    String contactGroupId;
    String contactGroupName;

    public String getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(String contactUserId) {
        this.contactUserId = contactUserId;
    }

    public byte[] getContactUserImage() {
        return contactUserImage;
    }

    public void setContactUserImage(byte[] contactUserImage) {
        this.contactUserImage = contactUserImage;
    }

    public String getContactUserFirstName() {
        return contactUserFirstName;
    }

    public void setContactUserFirstName(String contactUserFirstName) {
        this.contactUserFirstName = contactUserFirstName;
    }

    public String getContactUserLastName() {
        return contactUserLastName;
    }

    public void setContactUserLastName(String contactUserLastName) {
        this.contactUserLastName = contactUserLastName;
    }

    public String getContactUserAddress() {
        return contactUserAddress;
    }

    public void setContactUserAddress(String contactUserAddress) {
        this.contactUserAddress = contactUserAddress;
    }

    public String getContactUserPhone() {
        return contactUserPhone;
    }

    public void setContactUserPhone(String contactUserPhone) {
        this.contactUserPhone = contactUserPhone;
    }

    public String getContactUserSecond() {
        return contactUserSecond;
    }

    public void setContactUserSecond(String contactUserSecond) {
        this.contactUserSecond=contactUserSecond;
    }

    public String getContactGroupId() {
        return contactGroupId;
    }

    public void setContactGroupId(String contactGroupId) {
        this.contactGroupId = contactGroupId;
    }

    public String getContactGroupName() {
        return contactGroupName;
    }

    public void setContactGroupName(String contactGroupName) {
        this.contactGroupName = contactGroupName;
    }

}
