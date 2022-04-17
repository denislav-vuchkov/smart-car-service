package com.smart.garage.models.dtos;

import com.smart.garage.models.VehicleModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehicleModelDTO {

    private int modelID;
    private int makeID;
    private String newModel;
    private String verbatimMake;
    private String verbatimModel;

    public VehicleModelDTO(VehicleModel vehicleModel) {
        this.modelID = vehicleModel.getId();
    }
}
