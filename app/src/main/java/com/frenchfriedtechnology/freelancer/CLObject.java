package com.frenchfriedtechnology.freelancer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matteo on 2/3/16.
 */
public class CLObject implements Parcelable {

    public static final Parcelable.Creator<CLObject> CREATOR
            = new Parcelable.Creator<CLObject>() {
        public CLObject createFromParcel(Parcel in) {
            return new CLObject(in);
        }

        public CLObject[] newArray(int size) {
            return new CLObject[size];
        }
    };
    private int id;
    private String name;
    private String name2;
    private String email;
    private String phone;
    private String address;
    private String rate;
    private String recurrence;
    private String notes;

    public CLObject() {

    }

    public CLObject(Parcel input) {
        id = input.readInt();
        name = input.readString();
        name2 = input.readString();
        email = input.readString();
        phone = input.readString();
        address = input.readString();
        rate = input.readString();
        recurrence = input.readString();
        notes = input.readString();

    }

    public CLObject(int id,
                    String name,
                    String name2,
                    String email,
                    String phone,
                    String address,
                    String rate,
                    String recurrence,
                    String notes) {
        this.id = id;
        this.name = name;
        this.name2 = name2;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.rate = rate;
        this.recurrence = recurrence;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "\nID: " + id +
                "\nName " + name +
                "\nName2 " + name2 +
                "\nEmail " + email +
                "\nPhone " + phone +
                "\nAddress " + address +
                "\nRate " + rate +
                "\nRecurrence " + recurrence +
                "\nNotes " + notes +
                "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(name2);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(rate);
        dest.writeString(recurrence);
        dest.writeString(notes);


    }
}
