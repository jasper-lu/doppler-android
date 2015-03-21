package com.jasperlu.doppler;

/**
 * Created by Jasper on 3/21/2015.
 */
public class Calibrator {
    private double previousDiff = 0;
    private double previousDirection = 0;
    private double directionChanges = 0;
    private int iteration = 0;

    private final int upThreshold = 5;
    private final int downThreshold = 0;
    private final double upAmount = 1.1;
    private final double downAmount = 0.9;
}
