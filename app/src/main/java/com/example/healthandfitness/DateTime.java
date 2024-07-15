package com.example.healthandfitness;

import java.text.SimpleDateFormat;

public class DateTime {
    long dateTime;
    SimpleDateFormat sdf;
    public DateTime(){}
    public String getDateTime(String format){
        dateTime = System.currentTimeMillis();

        switch (format){
            case "date":
                sdf = new SimpleDateFormat("yyyy.MM.dd");
                break;
            case "dayDate":
                sdf = new SimpleDateFormat("EEE, d MMM yyyy");
                break;
            case "dateTime":
                sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                break;
            case "time":
                sdf = new SimpleDateFormat("HH:mm");
                break;
        }
    return sdf.format(dateTime);}
}
