package com.avanzarit.apps.vendormgmt.repository;

import com.avanzarit.apps.vendormgmt.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by SPADHI on 5/4/2017.
 */
public interface VendorRepository extends JpaRepository<Vendor, String> {
    Vendor findByVendorId(String vendorId);
}
