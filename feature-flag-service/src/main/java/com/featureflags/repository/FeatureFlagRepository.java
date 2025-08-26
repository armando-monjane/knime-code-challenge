package com.featureflags.repository;

import com.featureflags.model.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    Optional<FeatureFlag> findByName(String name);
    boolean existsByName(String name);
    List<FeatureFlag> findByEnabled(boolean enabled);

    /**
     * Find feature flags by name containing the given string (case-insensitive)
     */
    @Query("SELECT f FROM FeatureFlag f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<FeatureFlag> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Count enabled feature flags
     */
    @Query("SELECT COUNT(f) FROM FeatureFlag f WHERE f.enabled = true")
    long countEnabledFlags();

    /**
     * Count disabled feature flags
     */
    @Query("SELECT COUNT(f) FROM FeatureFlag f WHERE f.enabled = false")
    long countDisabledFlags();
}
