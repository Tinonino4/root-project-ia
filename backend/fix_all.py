import os
import re

base_dir = "/home/tino/Projects/root-project-ia/backend/src/main/java/com/ia/root/backend/auth/internal/"

def replace_in_file(path, replacements):
    full_path = os.path.join(base_dir, path)
    if not os.path.exists(full_path): return
    with open(full_path, 'r') as f:
        content = f.read()
    
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(full_path, 'w') as f:
        f.write(content)

# Global replaces for all files in the tree
for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".java"):
            full_path = os.path.join(root, file)
            with open(full_path, 'r') as f:
                content = f.read()
            
            # Remove all old auth imports
            lines = content.split('\n')
            new_lines = []
            for line in lines:
                if line.startswith('import com.ia.root.backend.auth.') and not line.startswith('import com.ia.root.backend.auth.UserRequiresOtpEvent'):
                    pass # remove
                else:
                    new_lines.append(line)
            content = '\n'.join(new_lines)
            
            # Add new correct imports based on file type
            imports_to_add = set()
            if "domain/model" in full_path and "UserOtp.java" in full_path:
                imports_to_add.add("import com.ia.root.backend.auth.internal.domain.model.User;")
            
            if "domain/repository" in full_path:
                imports_to_add.add("import com.ia.root.backend.auth.internal.domain.model.*;")
                
            if "application" in full_path:
                imports_to_add.add("import com.ia.root.backend.auth.internal.domain.model.*;")
                imports_to_add.add("import com.ia.root.backend.auth.internal.domain.repository.*;")
                imports_to_add.add("import com.ia.root.backend.auth.internal.infrastructure.web.dto.*;")
                imports_to_add.add("import com.ia.root.backend.auth.internal.infrastructure.security.JwtProvider;")
                
            if "infrastructure/web" in full_path and "AuthController.java" in full_path:
                imports_to_add.add("import com.ia.root.backend.auth.internal.application.AuthService;")
                imports_to_add.add("import com.ia.root.backend.auth.internal.infrastructure.web.dto.*;")
                
            if "infrastructure/security" in full_path:
                imports_to_add.add("import com.ia.root.backend.auth.internal.domain.model.User;")
                imports_to_add.add("import com.ia.root.backend.auth.internal.domain.repository.UserRepository;")

            lines = content.split('\n')
            for i, line in enumerate(lines):
                if line.startswith('package '):
                    for imp in imports_to_add:
                        lines.insert(i+1, imp)
                    break
                    
            with open(full_path, 'w') as f:
                f.write('\n'.join(lines))

