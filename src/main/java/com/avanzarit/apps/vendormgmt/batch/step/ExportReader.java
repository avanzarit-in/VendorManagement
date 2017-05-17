package com.avanzarit.apps.vendormgmt.batch.step;

import com.avanzarit.apps.vendormgmt.model.Vendor;
import org.springframework.batch.item.database.JdbcCursorItemReader;

import javax.sql.DataSource;

/**
 * Created by SPADHI on 5/17/2017.
 */
public class ExportReader {

    private static DataSource dataSource;

    public ExportReader(DataSource dataSource){
        this.dataSource=dataSource;
    }
    public static JdbcCursorItemReader<Vendor> reader(String path) {
        JdbcCursorItemReader<Vendor> reader = new JdbcCursorItemReader<Vendor>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT vendorid, vendorname1, vendorname2, vendorname3, telephonenumberextn, mobileno, email, faxnumberextn, buildingno, address1, address2, address3, address4, address5, city, postcode, region, country, accountholdername, accountnumber, pan, vatnumber FROM VENDOR");
        reader.setRowMapper(new VendorRowMapper());
        return reader;
    }
}
