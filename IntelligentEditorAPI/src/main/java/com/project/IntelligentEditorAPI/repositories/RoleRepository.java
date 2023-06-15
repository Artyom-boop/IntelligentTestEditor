package com.project.IntelligentEditorAPI.repositories;

import java.util.Optional;

import com.project.IntelligentEditorAPI.model.ERole;
import com.project.IntelligentEditorAPI.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
