package domain;

import java.util.Date;

public class RideCreationData {
    private String from;
    private String to;
    private Date date;
    private int nPlaces;
    private float price;

    public RideCreationData(String from, String to, Date date, int nPlaces, float price) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.nPlaces = nPlaces;
        this.price = price;
    }

    // Getters
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public Date getDate() { return date; }
    public int getNPlaces() { return nPlaces; }
    public float getPrice() { return price; }
}
