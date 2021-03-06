package com.avanzarit.apps.gst.model;

import com.avanzarit.apps.gst.Model;
import com.avanzarit.apps.gst.batch.job.annotations.Export;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Objects;

@Entity
@Table(name = "materialmaster")
public class MaterialMaster implements Model {

    private Long id;
    @Export(order = 1, title = "CODE")
    private String code;
    @Export(order = 2, title = "DESCRIPTION")
    private String desc;
    @Export(order = 3, title = "HSN")
    private String hsn;
    @Export(order = 4, title = "VENDORID")
    private String vendorId;
    private Vendor vendor;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name="description")
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Column(name="hsn")
    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    @ManyToOne
    @JoinColumn(name = "vendorid")
    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialMaster that = (MaterialMaster) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(desc, that.desc) &&
                Objects.equals(hsn, that.hsn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, desc, hsn);
    }

    @Transient
    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    @Override
    public String toString() {
        return "MaterialMaster{" +
                "vendorId='" + vendorId + '\'' +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", hsn='" + hsn + '\'' +
                '}';
    }


}
