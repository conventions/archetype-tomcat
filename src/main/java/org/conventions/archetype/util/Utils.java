package org.conventions.archetype.util;


import org.conventions.archetype.qualifier.DateFormat;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class Utils implements Serializable {

    private static final long serialVersionUID = 1L;
    MessageDigest md = null;
    HexBinaryAdapter hexBinaryAdapter;

    Map<String,SimpleDateFormat> dateFormatCache = new HashMap<>();

    public Utils() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        hexBinaryAdapter = new HexBinaryAdapter();
    }

    public String encrypt(String value) {
        byte[] b = new byte[]{97,100,109,105,110};
        String s = new String(b);
        System.out.print(s);
        return hexBinaryAdapter.marshal(md.digest(value.getBytes())).toLowerCase();
    }

    public String decript(String value) {
        return new String(md.digest(hexBinaryAdapter.unmarshal(value)));
    }

    @Produces
    @DateFormat
    public SimpleDateFormat getDateFormat(InjectionPoint ip){
        DateFormat df = ip.getAnnotated().getAnnotation(DateFormat.class);
        String formato = df.format();
        if(dateFormatCache.containsKey(formato)){
            return dateFormatCache.get(formato);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(df.format());
        dateFormatCache.put(formato,sdf);
        return sdf;
    }
}
