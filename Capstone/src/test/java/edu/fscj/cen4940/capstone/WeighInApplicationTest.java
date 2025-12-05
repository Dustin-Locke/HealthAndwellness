package edu.fscj.cen4940.capstone;

import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.entity.WeighIn;
import edu.fscj.cen4940.capstone.repository.WeighInRepository;
import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestHelperConfig.class)
@DisplayName("Weigh In Repository Test")
public class WeighInApplicationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WeighInRepository weighInRepository;

    @Autowired
    private CreateAndPersist createAndPersist;


    @Test
    @DisplayName("Save WeighIn")
    public void saveWeighIn_ShouldSaveWeighIn() {
        WeighIn savedWeighIn =
                createAndPersist.weighIn("weightTest", 174.5, 169.7);

        User user = savedWeighIn.getUser();

        WeighIn foundWeighIn = entityManager.find(WeighIn.class, savedWeighIn.getWeighInId());
        assertThat(foundWeighIn).isEqualTo(savedWeighIn);
        assertThat(foundWeighIn.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Find by ID should return empty when WeighIn does not exist")
    public void findById_ShouldReturnEmpty_WhenWeighInDoesNotExist() {
        Integer nonExistentId = 9999;
        Optional<WeighIn> foundWeighIn = weighInRepository.findById(nonExistentId);
        assertThat(foundWeighIn).isEmpty();
    }

    @Test
    @DisplayName("Find WeighIns by weight range")
    public void findByWeightBetween_ShouldReturnCorrectWeighIns() {
        User user = createAndPersist.user("weightRangeTest");

        // create some weigh-ins
        createAndPersist.weighIn("weightRangeTest", 160.0, 170.0);
        createAndPersist.weighIn("weightRangeTest", 175.0, 180.0);
        createAndPersist.weighIn("weightRangeTest", 185.0, 190.0);

        List<WeighIn> foundWeighIn = weighInRepository.findByWeightBetween(165.0, 180.0);

        assertThat(foundWeighIn).extracting(WeighIn::getWeight).allMatch(w -> w >= 165.0 && w <= 180.0);
        assertThat(foundWeighIn).hasSize(2);
    }

}
