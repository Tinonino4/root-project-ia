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

    public UserProfile getProfileByUsername(String username) {
        return userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
    }

    @Transactional
    public UserProfile updateProfile(UUID userId, UserProfileRequest request) {
        UserProfile profile = getProfile(userId);

        // Validar y actualizar username si se ha proporcionado
        if (request.username() != null && !request.username().trim().isEmpty()) {
            String newUsername = request.username().trim().toLowerCase();
            if (!newUsername.equals(profile.getUsername())) {
                if (!newUsername.matches("^[a-z0-9-_]+$")) {
                    throw new IllegalArgumentException("Formato de nombre de usuario inválido");
                }
                if (userProfileRepository.existsByUsername(newUsername)) {
                    throw new IllegalArgumentException("El nombre de usuario ya está en uso");
                }
                profile.updateUsername(newUsername);
            }
        }

        profile.updatePersonalInfo(
            request.name(), request.surname(), request.city(),
            request.birthday(), request.zipcode(), request.phoneNumber()
        );
        profile.updateProfessionalInfo(request.jobTitle(), request.education(), request.aboutMe());
        profile.updateContactEmail(request.contactEmail());
        profile.updatePhotoUrl(request.photoUrl());

        return userProfileRepository.save(profile);
    }

    @Transactional
    public UserProfile updateAvatar(UUID userId, String photoUrl) {
        UserProfile profile = getProfile(userId);
        profile.updatePhotoUrl(photoUrl);
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

    public List<com.ia.root.backend.professional.internal.infrastructure.web.dto.CandidateSearchDTO> searchCandidates(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return userProfileRepository.searchByKeyword(query).stream()
                .map(p -> new com.ia.root.backend.professional.internal.infrastructure.web.dto.CandidateSearchDTO(
                    p.getUserId(), p.getName(), p.getSurname(), p.getJobTitle(), p.getPhotoUrl(), p.getCity()
                ))
                .toList();
    }
}
