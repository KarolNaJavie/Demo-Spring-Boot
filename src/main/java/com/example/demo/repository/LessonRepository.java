package com.example.demo.repository;

import com.example.demo.model.Lesson;
import com.example.demo.model.Teacher;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Lock(LockModeType.OPTIMISTIC)
        //Optimistic force increment wymusza zwiekszenie wersji, nawet kiedy nie ma zmian
    Optional<Lesson> findWithOptimisticLockById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Lesson> findWithLockById(Long id);

    boolean existsByTeacherAndDatetimeGreaterThanAndDatetimeLessThanAndIdNot(Teacher teacher, LocalDateTime from, LocalDateTime to, long id);
}
