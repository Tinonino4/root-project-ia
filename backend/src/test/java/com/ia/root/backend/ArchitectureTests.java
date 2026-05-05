package com.ia.root.backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ArchitectureTests {

    ApplicationModules modules = ApplicationModules.of(BackendApplication.class);

    @Test
    void verifyModularity() {
        modules.verify();
    }

    @Test
    void writeDocumentation() {
        new Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml();
    }
}
