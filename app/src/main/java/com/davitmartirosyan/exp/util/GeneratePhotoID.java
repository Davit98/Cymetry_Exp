package com.davitmartirosyan.exp.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GeneratePhotoID {
    public static String getUniqueId() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyyHH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
