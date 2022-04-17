package com.smart.garage.utility;

import com.smart.garage.exceptions.IllegalVehicleBrand;
import com.smart.garage.models.dtos.VehicleModelDTO;

public class VehicleModelExtractor {

    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 32;

    public static final String MULTIPLE_MODELS_ERROR = "Multiple models selected.";
    public static final String INVALID_MODEL_ERROR = "Invalid model provided.";
    public static final String INVALID_NAME_LENGTH = "Makes and models need to be between 2 and 32 symbols.";
    public static final String DUPLICATE_MODEL_NAME = "Make or model already exists.";

    public static void validateNonDualModelProvided(VehicleModelDTO modelDTO) {
        if (((containsExistingModel(modelDTO)) && (anyHybridProvided(modelDTO) || anyVerbatimProvided(modelDTO))) ||
                ((anyHybridProvided(modelDTO)) && (containsExistingModel(modelDTO) || anyVerbatimProvided(modelDTO))) ||
                ((anyVerbatimProvided(modelDTO)) && (containsExistingModel(modelDTO) || anyHybridProvided(modelDTO))))
            throw new IllegalVehicleBrand(MULTIPLE_MODELS_ERROR);
    }

    public static boolean containsExistingModel(VehicleModelDTO vehicleModelDTO) {
        return vehicleModelDTO.getModelID() > 0;
    }

    public static boolean anyHybridProvided(VehicleModelDTO vehicleModelDTO) {
        return vehicleModelDTO.getMakeID() > 0 || nameNotEmpty(vehicleModelDTO.getNewModel());
    }

    public static boolean anyVerbatimProvided(VehicleModelDTO vehicleModelDTO) {
        return (nameNotEmpty(vehicleModelDTO.getVerbatimMake()) || nameNotEmpty(vehicleModelDTO.getVerbatimModel()));
    }

    public static boolean containsValidHybridModel(VehicleModelDTO vehicleModelDTO) {
        return vehicleModelDTO.getMakeID() > 0 && (nameNotEmpty(vehicleModelDTO.getNewModel()));
    }

    public static boolean containsValidVerbatimModel(VehicleModelDTO vehicleModelDTO) {
        return (nameNotEmpty(vehicleModelDTO.getVerbatimMake())) &&
                (nameNotEmpty(vehicleModelDTO.getVerbatimModel()));
    }

    public static boolean nameNotEmpty(String field) {
        return !(field == null || field.isBlank());
    }

    public static void validateNameLength(String name) {
        if (!name.isBlank() && ((name.trim().length() < NAME_MIN_LENGTH || name.trim().length() > NAME_MAX_LENGTH)))
            throw new IllegalVehicleBrand(INVALID_NAME_LENGTH);
    }
}
