package com.example.metroTicketBooking;

public class TrainSchedule {
    private String source;
    private String destination;
    private String departureTime;
    private String destinationTime;
    private String timeDifference;

    private String stationbtw;

    public String getStationbtw() {
        return stationbtw;
    }

    public void setStationbtw(String stationbtw) {
        this.stationbtw = stationbtw;
    }

    public TrainSchedule(String source, String destination, String departureTime, String destinationTime, String timeDifference, String stationbtw) {
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.destinationTime = destinationTime;
        this.timeDifference = timeDifference;
        this.stationbtw = stationbtw;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getDestinationTime() {
        return destinationTime;
    }

    public String getTimeDifference() {
        return timeDifference;
    }
}
