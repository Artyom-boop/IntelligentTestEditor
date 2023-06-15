package com.project.IntelligentEditorAPI.repositories;

import com.project.IntelligentEditorAPI.model.Test;
import com.project.IntelligentEditorAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository  extends JpaRepository<Test, Long> {

    List<Test> findTestByUser(User user);
}
