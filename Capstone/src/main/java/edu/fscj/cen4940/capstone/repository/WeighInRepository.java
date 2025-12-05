package edu.fscj.cen4940.capstone.repository;

import edu.fscj.cen4940.capstone.entity.WeighIn;
import edu.fscj.cen4940.capstone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeighInRepository extends JpaRepository<WeighIn, Integer> {

    // find weight by user ID
    List<WeighIn> findById(int id);

    // find all weigh-ins by user
    List<WeighIn> findByUser(User user);

    // find all weigh ins on a specific date
    List<WeighIn> findByDate(LocalDate date);

    // find weight between a range
    List<WeighIn> findByWeightBetween(Double min, Double max);
}