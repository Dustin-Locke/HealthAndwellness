package edu.fscj.cen4940.capstone.repository;

import edu.fscj.cen4940.capstone.entity.Reminder;
import edu.fscj.cen4940.capstone.enums.ReminderFrequency;
import edu.fscj.cen4940.capstone.enums.ReminderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {

    List<Reminder> findByUserId(Integer userId);
    void deleteByUserId(Integer userId);
    List<Reminder> findByUserIdAndEnabledTrue(Integer userId);
    List<Reminder> findByUserIdAndType(Integer userId, ReminderType type);
    List<Reminder> findByUserIdAndEnabledTrueAndNotifyTimeAfter(Integer userId, LocalTime time);
    List<Reminder> findByEnabledTrue();
}
