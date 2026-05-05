package com.ia.root.backend.professional.internal.application;

import com.ia.root.backend.professional.internal.domain.model.Experience;
import com.ia.root.backend.professional.internal.domain.model.UserProfile;
import com.ia.root.backend.professional.internal.domain.repository.ExperienceRepository;
import com.ia.root.backend.professional.internal.domain.repository.UserProfileRepository;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.ExperienceRequest;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.UserProfileRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProfessionalService {

    private final UserProfileRepository userProfileRepository;
    private final ExperienceRepository experienceRepository;

    public ProfessionalService(UserProfileRepository userProfileRepository, ExperienceRepository experienceRepository) {
        this.userProfileRepository = userProfileRepository;
        this.experienceRepository = experienceRepository;
    }

    public UserProfile getProfile(UUID userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
    }

    @Transactional
    public UserProfile updateProfile(UUID userId, UserProfileRequest request) {
        UserProfile profile = getProfile(userId);
        
        if (request.name() != null) profile.setName(request.name());
        if (request.surname() != null) profile.setSurname(request.surname());
        if (request.contactEmail() != null) profile.setContactEmail(request.contactEmail());
        if (request.aboutMe() != null) profile.setAboutMe(request.aboutMe());
        if (request.city() != null) profile.setCity(request.city());
        if (request.birthday() != null) profile.setBirthday(request.birthday());
        if (request.zipcode() != null) profile.setZipcode(request.zipcode());
        if (request.phoneNumber() != null) profile.setPhoneNumber(request.phoneNumber());
        if (request.photoUrl() != null) profile.setPhotoUrl(request.photoUrl());
        if (request.jobTitle() != null) profile.setJobTitle(request.jobTitle());
        if (request.education() != null) profile.setEducation(request.education());
        
        return userProfileRepository.save(profile);
    }

    public List<Experience> getExperiences(UUID userId) {
        return experienceRepository.findByUserIdOrderByStartDateDesc(userId);
    }

    @Transactional
    public Experience addExperience(UUID userId, ExperienceRequest request) {
        Experience experience = new Experience();
        experience.setUserId(userId);
        experience.setCompanyName(request.companyName());
        experience.setDepartment(request.department());
        experience.setPosition(request.position());
        experience.setStartDate(request.startDate());
        experience.setFinishDate(request.finishDate());
        experience.setFunctions(request.functions());
        
        return experienceRepository.save(experience);
    }

    @Transactional
    public Experience updateExperience(UUID experienceId, UUID userId, ExperienceRequest request) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
                
        if (!experience.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to update this experience");
        }
        
        if (request.companyName() != null) experience.setCompanyName(request.companyName());
        if (request.department() != null) experience.setDepartment(request.department());
        if (request.position() != null) experience.setPosition(request.position());
        if (request.startDate() != null) experience.setStartDate(request.startDate());
        experience.setFinishDate(request.finishDate()); // can be nullable
        if (request.functions() != null) experience.setFunctions(request.functions());
        
        return experienceRepository.save(experience);
    }

    @Transactional
    public void deleteExperience(UUID experienceId, UUID userId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));
                
        if (!experience.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this experience");
        }
        
        experienceRepository.delete(experience);
    }
}
