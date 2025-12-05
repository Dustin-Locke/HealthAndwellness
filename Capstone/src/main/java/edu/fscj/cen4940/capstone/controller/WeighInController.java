package edu.fscj.cen4940.capstone.controller;

import edu.fscj.cen4940.capstone.dto.WeighInDTO;
import edu.fscj.cen4940.capstone.entity.WeighIn;
import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.service.UserService;
import edu.fscj.cen4940.capstone.service.WeighInService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/weighin")
public class WeighInController {

    @Autowired
    private WeighInService weighInService;

    @Autowired
    private UserService userService;

    // Get all weighIns
    @GetMapping
    public ResponseEntity<List<WeighIn>> getAllWeighIns() {
        return ResponseEntity.ok(weighInService.getAllWeighIns());
    }

    // Get weighIns by user ID
    @PreAuthorize("#userId == authentication.principal.user.id")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WeighIn>> getWeighInsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(weighInService.getWeighInsByUserId(userId));
    }

    // Get weighIn by ID
    @GetMapping("/{id}")
    public ResponseEntity<WeighIn> getWeighInById(@PathVariable Integer id) {
        Optional<WeighIn> weighIn = weighInService.getWeighInById(id);
        return weighIn.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Submit weighIn
    @PostMapping
    public ResponseEntity<WeighIn> createWeighIn(@Valid @RequestBody WeighInDTO dto) {
        System.out.println("📥 Received weigh-in: " + dto);
        if (dto.getHeight() == null) {
            Optional<User> possibleUser = userService.findById(dto.getUserId());
            User user = possibleUser.orElse(new User());
            dto.setHeight(user.getHeight());
        }
        WeighIn entity = toEntity(dto);
        WeighIn saved = weighInService.saveWeighIn(entity);
        System.out.println("✅ Saved weigh-in with ID: " + saved.getWeighInId());
        return ResponseEntity.ok(saved);
    }

    // Update an existing weighIn
    @PutMapping("/{id}")
    public ResponseEntity<WeighIn> updateWeighIn(@PathVariable Integer id, @Valid @RequestBody WeighInDTO dto) {
        Optional<WeighIn> existing = weighInService.getWeighInById(id);
        if (!existing.isPresent()) {  // ✅ Fixed this check
            return ResponseEntity.notFound().build();
        }
        WeighIn updated = existing.get();
        updated.setDate(dto.getDate());
        updated.setWeight(dto.getWeight());
        updated.setHeight(dto.getHeight());
        updated.setNotes(dto.getNotes());
        return ResponseEntity.ok(weighInService.saveWeighIn(updated));
    }

    // Delete a weighIn
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeighIn(@PathVariable Integer id) {
        weighInService.deleteWeighIn(id);
        return ResponseEntity.noContent().build();
    }

    private WeighIn toEntity(WeighInDTO dto) {
        WeighIn entity = new WeighIn();
        entity.setDate(dto.getDate());
        entity.setWeight(dto.getWeight());
        entity.setHeight(dto.getHeight());
        entity.setNotes(dto.getNotes());

        if (dto.getUserId() != null) {
            User user = new User();
            user.setId(dto.getUserId());
            entity.setUser(user);
        }

        return entity;
    }
}