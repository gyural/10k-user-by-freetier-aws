package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.dto.request.NonMemberParkingRequest;
import org.example.honorsparkingbe.dto.response.NonMemberParkingResponse;
import org.example.honorsparkingbe.dto.response.SyncInoutResponse;

public interface NonMemberParkingService {
    SyncInoutResponse getInoutByVehicleNumber(NonMemberParkingRequest request);

    NonMemberParkingResponse getParkingStatus(String vehicleNumber);
}
//5