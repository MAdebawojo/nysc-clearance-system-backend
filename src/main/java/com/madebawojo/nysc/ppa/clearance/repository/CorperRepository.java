package com.madebawojo.nysc.ppa.clearance.repository;

import com.madebawojo.nysc.ppa.clearance.entity.user.profile.Corper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorperRepository extends JpaRepository<Corper, Long> {
    List<Corper> findAllByUnitId(Long unitId);
    List<Corper> findAllByPpaId(Long ppaId);
    boolean existsByCallUpNumber(String callUpNumber);
    boolean existsByStateCode(String stateCode);

}
