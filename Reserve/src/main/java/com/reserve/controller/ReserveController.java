package com.reserve.controller;

import com.infrastructure.form.OutputAPIForm;
import com.infrastructure.utility.InfraSecurityUtils;
import com.reserve.service.services.IReserveSlotSrv;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reserve Controller", description = "Reserve management APIs")
public class ReserveController {
    @Autowired
    private IReserveSlotSrv reserveSlotSrv;

    @PostMapping("")
    @Operation(summary = "Get Nearest Reservation  Base on current User ", description = "Returns a DTO of Reservation")
    @ApiResponse(responseCode = "200", description = "succeed reservation")
    @ApiResponse(responseCode = "401", description = "unauthorized")
    public ResponseEntity<OutputAPIForm> reserve(){
        OutputAPIForm retVal = new OutputAPIForm();
        try{
            retVal = reserveSlotSrv.reserveSlot(InfraSecurityUtils.getCurrentUser());
        }catch (Exception e){
            log.error("Error in save user",e);
            retVal.setSuccess(false);
        }
        return ResponseEntity.ok().body(retVal);
    }
    @Operation(summary = "Cancel Reservation Base on ID ", description = "Returns a DTO succeed")
    @ApiResponse(responseCode = "200", description = "succeed True or Succeed False")
    @ApiResponse(responseCode = "401", description = "unauthorized")
    @DeleteMapping("/{id}")
    public ResponseEntity<OutputAPIForm> cancellation(@PathVariable Long id){
        OutputAPIForm retVal = new OutputAPIForm();
        try{
            retVal = reserveSlotSrv.cancelReserveSlot(id);
        }catch (Exception e){
            log.error("Error in save user",e);
            retVal.setSuccess(false);
        }
        return ResponseEntity.ok().body(retVal);
    }
}
