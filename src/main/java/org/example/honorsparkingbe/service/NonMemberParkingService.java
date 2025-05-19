package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.dto.request.NonMemberParkingRequest;
import org.example.honorsparkingbe.dto.response.NonMemberParkingResponse;
import org.example.honorsparkingbe.dto.response.SyncNonMemberInoutResponse;

public interface NonMemberParkingService {
    SyncNonMemberInoutResponse getInoutByVehicleNumber(NonMemberParkingRequest request);

    NonMemberParkingResponse getParkingStatus(String vehicleNumber);
}
//5