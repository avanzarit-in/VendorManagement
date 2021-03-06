package com.avanzarit.apps.gst.repository;

import com.avanzarit.apps.gst.model.ServiceSacMaster;
import com.avanzarit.apps.gst.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceSacMasterRepository extends JpaRepository<ServiceSacMaster, String> {
    ServiceSacMaster findById(String id);

    List<ServiceSacMaster> findByVendor(Vendor vendor);
}
