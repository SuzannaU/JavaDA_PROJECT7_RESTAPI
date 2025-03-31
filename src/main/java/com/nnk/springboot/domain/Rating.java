package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @NotBlank(message = "MoodysRating is mandatory")
    @Column(name="moodysRating")
    private String moodysRating;

    @NotBlank(message = "SandRating is mandatory")
    @Column(name="sandRating")
    private String sandRating;

    @NotBlank(message = "FitchRating is mandatory")
    @Column(name="fitchRating")
    private String fitchRating;

    @DecimalMin(value = "1", message = "Order Number must be at least 1")
    @Column(name="orderNumber")
    private int orderNumber;

    public Rating(String moodysRating, String sandRating, String fitchRating, int orderNumber) {
        this.moodysRating = moodysRating;
        this.sandRating = sandRating;
        this.fitchRating = fitchRating;
        this.orderNumber = orderNumber;
    }

    public Rating() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMoodysRating() {
        return moodysRating;
    }

    public void setMoodysRating(String moodysRating) {
        this.moodysRating = moodysRating;
    }

    public String getSandRating() {
        return sandRating;
    }

    public void setSandRating(String sandRating) {
        this.sandRating = sandRating;
    }

    public String getFitchRating() {
        return fitchRating;
    }

    public void setFitchRating(String fitchRating) {
        this.fitchRating = fitchRating;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
