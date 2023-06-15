package com.project.IntelligentEditorAPI.repositories;

import com.project.IntelligentEditorAPI.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
