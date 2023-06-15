package com.project.IntelligentEditorAPI.repositories;

import com.project.IntelligentEditorAPI.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
