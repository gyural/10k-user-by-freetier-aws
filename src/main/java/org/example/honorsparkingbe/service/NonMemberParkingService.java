package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.dto.request.NonMemberParkingRequest;
import org.example.honorsparkingbe.dto.response.NonMemberParkingListResponse;
import org.example.honorsparkingbe.dto.response.SyncNonMemberInoutListResponse;

public interface NonMemberParkingService {

    SyncNonMemberInoutListResponse getInoutByVehicleNumber(NonMemberParkingRequest request);

    NonMemberParkingListResponse getParkingStatus(String vehicleNumber);
}
//5