package com.example.metroTicketBooking;

public class TicketDetails {
    String source, destination, fare, journey_type;
    public long timestamp;

    TicketDetails(){

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getJourney_type() {
        return journey_type;
    }

    public void setJourney_type(String journey_type) {
        this.journey_type = journey_type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    TicketDetails(String source,
                  String destination,
                  String fare,
                  String journey_type,
                  long timestamp){
        this.destination = destination;
        this.source = source;
        this.fare = fare;
        this.journey_type = journey_type;
        this.timestamp = timestamp;
    }
}
