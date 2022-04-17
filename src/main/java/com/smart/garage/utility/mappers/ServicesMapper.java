package com.smart.garage.utility.mappers;

import com.smart.garage.models.Servicez;
import com.smart.garage.models.dtos.ServicesDTOIn;
import com.smart.garage.services.contracts.ServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServicesMapper {

    private final ServicesService servicesService;

    @Autowired
    public ServicesMapper(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    public Servicez toObject(ServicesDTOIn servicesDTOIn) {
        Servicez service = new Servicez();

        service.setName(servicesDTOIn.getServiceName());
        service.setPriceBGN(servicesDTOIn.getPrice());

        return service;
    }

    public Servicez toObject(int id, ServicesDTOIn servicesDTOIn) {
        Servicez service = servicesService.getById(id);

        service.setName(servicesDTOIn.getServiceName());
        service.setPriceBGN(servicesDTOIn.getPrice());

        return service;
    }

}
