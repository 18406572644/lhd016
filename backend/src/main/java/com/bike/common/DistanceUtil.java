package com.bike.common;

import java.math.BigDecimal;

public class DistanceUtil {

    private static final double EARTH_RADIUS = 6371.0;

    public static double calculateDistance(BigDecimal lng1, BigDecimal lat1, BigDecimal lng2, BigDecimal lat2) {
        if (lng1 == null || lat1 == null || lng2 == null || lat2 == null) {
            return -1;
        }
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLng = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    public static int estimateArrivalTime(double distanceKm, double speedKmh) {
        if (distanceKm < 0 || speedKmh <= 0) {
            return -1;
        }
        return (int) Math.ceil(distanceKm / speedKmh * 60);
    }

    public static int estimateArrivalTime(double distanceKm) {
        return estimateArrivalTime(distanceKm, 30.0);
    }
}
