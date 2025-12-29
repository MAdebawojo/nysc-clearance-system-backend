package com.madebawojo.nysc.ppa.clearance.repository;


import com.madebawojo.nysc.ppa.clearance.core.enums.ClearanceStatus;
import com.madebawojo.nysc.ppa.clearance.core.enums.Role;
import com.madebawojo.nysc.ppa.clearance.entity.clearance.ClearanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Month;
import java.util.List;
import java.util.Optional;

public interface ClearanceRequestRepository extends JpaRepository<ClearanceRequest, Long> {

    // Fetch all requests for a corper
    List<ClearanceRequest> findByCorper_Id(Long corperId);

    int deleteByCorper_Id(Long corperId);

    // Check if a request already exists for this corper and month
    List<ClearanceRequest> findByCorper_IdAndClearanceMonth(Long corperId, Month clearanceMonth);

    // Check if a request already exists for this corper and month
    boolean existsByCorper_IdAndClearanceMonth(Long corperId, Month clearanceMonth);

    // Optionally, filter by status too
    List<ClearanceRequest> findByCorper_IdAndStatus(Long corperId, ClearanceStatus status);

    // ##################################################3####################################
    // These queries need to be optimized. Initially, the design was for the unit head (admin)
    // to see only pending requests. However, this has changed: the unit head should now also
    // have access to a history of the requests they approved, along with the final status after
    // each request is actioned by the super admin.
    // #########################################################################################
    @Query("SELECT cr FROM ClearanceRequest cr " +
            "WHERE cr.corper.unit.id = :unitId " +
            "AND cr.status IN ('LEVEL_ONE', 'PENDING', 'REJECTED') " +
            "AND (cr.rejectedBy = :role OR cr.rejectedBy IS NULL)")
    List<ClearanceRequest> findAllByUnitId(@Param("unitId") Long unitId,
                                           @Param("role") Role role);

    @Query("SELECT cr FROM ClearanceRequest cr " +
            "WHERE cr.corper.ppa.id = :ppaId " +
            "AND cr.status IN ('LEVEL_ONE', 'CLEARED', 'REJECTED') " +
            "AND (cr.rejectedBy = :role OR cr.rejectedBy IS NULL)")
    List<ClearanceRequest> findAllByPpaId(@Param("ppaId") Long ppaId,
                                          @Param("role") Role role);

    @Query("SELECT cr FROM ClearanceRequest cr " +
            "WHERE cr.corper.unit.id = :unitId " +
            "AND cr.status IN ('LEVEL_ONE', 'CLEARED', 'REJECTED') ")
    List<ClearanceRequest> getUnitClearanceHistory(@Param("unitId") Long unitId);

    @Query("SELECT cr FROM ClearanceRequest cr " +
            "WHERE cr.corper.ppa.id = :ppaId " +
            "AND cr.status IN ('CLEARED', 'REJECTED') " +
            "AND (cr.rejectedBy = :role OR cr.rejectedBy IS NULL)")
    List<ClearanceRequest> getPpaClearanceHistory(@Param("ppaId") Long ppaId,
                                                   @Param("role") Role role);
}
