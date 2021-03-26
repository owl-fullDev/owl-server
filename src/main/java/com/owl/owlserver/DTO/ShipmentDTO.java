package com.owl.owlserver.DTO;

import com.owl.owlserver.model.ShipmentDetail;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ShipmentDTO {

    private int shipmentId;
    private LocalDateTime sendTimestamp;
    private LocalDateTime receivedTimestamp;

    private int originId;
    private String originType;
    private String originName;
    private String originAddress;

    private int destinationId;
    private String destinationType;
    private String destinationName;
    private String destinationAddress;

    private String status;

    private List<ShipmentDetail> shipmentDetailList;

}

