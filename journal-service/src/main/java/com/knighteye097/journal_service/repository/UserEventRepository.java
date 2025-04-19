package com.knighteye097.journal_service.repository;

import com.knighteye097.journal_service.entity.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
}