package edu.fscj.cen4940.capstone;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fscj.cen4940.capstone.config.TestHelperConfig;
import edu.fscj.cen4940.capstone.dto.ExerciseDTO;
import edu.fscj.cen4940.capstone.enums.ExerciseType;
import edu.fscj.cen4940.capstone.repository.ExerciseRepository;
import edu.fscj.cen4940.capstone.service.EmailService;
import edu.fscj.cen4940.capstone.service.ExerciseService;
import edu.fscj.cen4940.capstone.util.CreateAndPersist;
import edu.fscj.cen4940.capstone.config.DataSeeder;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Import(TestHelperConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Exercise Controller Integration Tests")
public class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreateAndPersist createAndPersist;

    @MockBean
    private ExerciseService exerciseService;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("Create exercise should return 201 without altering DB")
    void createExercise_ShouldReturnCreated() throws Exception {
        ExerciseDTO dto = new ExerciseDTO();
        dto.setName("Jumping Jacks");
        dto.setType(ExerciseType.AEROBIC);

        ExerciseDTO returnedDto = new ExerciseDTO();
        returnedDto.setId(999); // fake ID
        returnedDto.setName(dto.getName());
        returnedDto.setType(dto.getType());

        when(exerciseService.save(any(ExerciseDTO.class))).thenReturn(returnedDto);

        mockMvc.perform(post("/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.name").value("Jumping Jacks"))
                .andExpect(jsonPath("$.type").value("AEROBIC"));
    }

    @Test
    @DisplayName("Get all exercises should return 200 and list")
    void getAllExercises_ShouldReturnList() throws Exception {
        createAndPersist.exercise(ExerciseType.AEROBIC);

        mockMvc.perform(get("/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /exercises/{id} returns exercise if found, 404 otherwise")
    void getExerciseById_ShouldReturnExerciseOrNotFound() throws Exception {
        ExerciseDTO dto = new ExerciseDTO(1, ExerciseType.ANAEROBIC, "Push-Ups");

        when(exerciseService.findById(1)).thenReturn(Optional.of(dto));
        when(exerciseService.findById(99999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/exercises/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Push-Ups"))
                .andExpect(jsonPath("$.type").value("ANAEROBIC"));

        mockMvc.perform(get("/exercises/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /exercises/name/{name} returns exercises")
    void getExercisesByName_ShouldReturnExercises() throws Exception {
        ExerciseDTO dto = new ExerciseDTO(18, ExerciseType.ANAEROBIC, "Push-Ups");

        when(exerciseService.findByName("Push-Ups")).thenReturn(List.of(dto));

        mockMvc.perform(get("/exercises/name/Push-Ups")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Push-Ups"))
                .andExpect(jsonPath("$[0].type").value("ANAEROBIC"));
    }

    @Test
    @DisplayName("GET /exercises/type/{type} returns exercises of given type")
    void getExercisesByType_ShouldReturnExercises() throws Exception {
        ExerciseDTO dto1 = new ExerciseDTO(18, ExerciseType.ANAEROBIC, "Push-Ups");
        ExerciseDTO dto2 = new ExerciseDTO(17, ExerciseType.ANAEROBIC, "Pull-Ups");

        when(exerciseService.findByType(ExerciseType.ANAEROBIC))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/exercises/type/ANAEROBIC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("ANAEROBIC"))
                .andExpect(jsonPath("$[?(@.name == 'Push-Ups')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Pull-Ups')]").exists());
    }
    
    @Test
    @DisplayName("PUT /exercises/{id} updates an exercise without touching DB")
    void updateExercise_ShouldReturnUpdatedExercise() throws Exception {
        ExerciseDTO exerciseDTO = new ExerciseDTO();
        exerciseDTO.setId(1);
        exerciseDTO.setName("Situps");
        exerciseDTO.setType(ExerciseType.ANAEROBIC);

        when(exerciseService.updateExercise(eq(1), any(ExerciseDTO.class)))
                .thenReturn(Optional.of(exerciseDTO));

        mockMvc.perform(put("/exercises/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Situps"))
                .andExpect(jsonPath("$.type").value("ANAEROBIC"));
    }

    @Test
    @DisplayName("DELETE /exercises/{id} does not remove anything from DB")
    void deleteExercise_ShouldReturnNoContent() throws Exception {
        when(exerciseService.existsById(1)).thenReturn(true);
        doNothing().when(exerciseService).deleteById(1);

        mockMvc.perform(delete("/exercises/1"))
                .andExpect(status().isNoContent());

        verify(exerciseService, times(1)).deleteById(1);
    }
}

