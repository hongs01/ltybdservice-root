package com.ltybdservice.hiveudf;

import com.ltybdservice.geohashutil.GeoHash;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class geocode extends UDF {
    public String evaluate(double lon,double lat){
        GeoHash hash = GeoHash.withBitPrecision(lat, lon, 40);
        return hash.toBase32();
    }
}
