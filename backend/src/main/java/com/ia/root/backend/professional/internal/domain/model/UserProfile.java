package com.ia.root.backend.professional.internal.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    private String name;
    private String surname;
    
    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "about_me", columnDefinition = "TEXT")
    private String aboutMe;

    private String city;
    private LocalDate birthday;
    private String zipcode;
    
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "job_title")
    private String jobTitle;

    private String education;

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private ZonedDateTime updatedAt;

    protected UserProfile() {}

    public UserProfile(UUID userId, String name, String contactEmail) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.contactEmail = contactEmail;
    }

    public void updatePersonalInfo(String name, String surname, String city,
                                   LocalDate birthday, String zipcode, String phoneNumber) {
        if (name != null) this.name = name;
        if (surname != null) this.surname = surname;
        if (city != null) this.city = city;
        if (birthday != null) this.birthday = birthday;
        if (zipcode != null) this.zipcode = zipcode;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
    }

    public void updateProfessionalInfo(String jobTitle, String education, String aboutMe) {
        if (jobTitle != null) this.jobTitle = jobTitle;
        if (education != null) this.education = education;
        if (aboutMe != null) this.aboutMe = aboutMe;
    }

    public void updateContactEmail(String contactEmail) {
        if (contactEmail != null) this.contactEmail = contactEmail;
    }

    public void updatePhotoUrl(String photoUrl) {
        if (photoUrl != null) this.photoUrl = photoUrl;
    }

    public void updateName(String name) { this.name = Objects.requireNonNull(name); }
    public void updateSurname(String surname) { this.surname = surname; }
    public void updateCity(String city) { this.city = city; }
    public void updateBirthday(LocalDate birthday) { this.birthday = birthday; }
    public void updateZipcode(String zipcode) { this.zipcode = zipcode; }
    public void updatePhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void updateJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void updateEducation(String education) { this.education = education; }
    public void updateAboutMe(String aboutMe) { this.aboutMe = aboutMe; }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getContactEmail() { return contactEmail; }
    public String getAboutMe() { return aboutMe; }
    public String getCity() { return city; }
    public LocalDate getBirthday() { return birthday; }
    public String getZipcode() { return zipcode; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPhotoUrl() { return photoUrl; }
    public String getJobTitle() { return jobTitle; }
    public String getEducation() { return education; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
}
