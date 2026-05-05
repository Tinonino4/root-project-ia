import os

files = [
    ('internal/infrastructure/web/AuthController.java', [
        'import com.ia.root.backend.auth.internal.application.AuthService;',
        'import com.ia.root.backend.auth.internal.infrastructure.web.dto.*;'
    ]),
    ('internal/application/AuthService.java', [
        'import com.ia.root.backend.auth.internal.domain.model.*;',
        'import com.ia.root.backend.auth.internal.domain.repository.*;',
        'import com.ia.root.backend.auth.internal.infrastructure.web.dto.*;'
    ]),
    ('internal/domain/repository/UserRepository.java', [
        'import com.ia.root.backend.auth.internal.domain.model.User;'
    ]),
    ('internal/domain/repository/UserOtpRepository.java', [
        'import com.ia.root.backend.auth.internal.domain.model.UserOtp;'
    ]),
    ('internal/infrastructure/security/CustomUserDetailsService.java', [
        'import com.ia.root.backend.auth.internal.domain.model.User;',
        'import com.ia.root.backend.auth.internal.domain.repository.UserRepository;'
    ])
]

for filepath, imports in files:
    full_path = f"/home/tino/Projects/root-project-ia/backend/src/main/java/com/ia/root/backend/auth/{filepath}"
    with open(full_path, 'r') as f:
        content = f.read()
    
    # insert after package
    lines = content.split('\n')
    for i, line in enumerate(lines):
        if line.startswith('package '):
            for imp in imports:
                lines.insert(i+1, imp)
            break
            
    with open(full_path, 'w') as f:
        f.write('\n'.join(lines))
