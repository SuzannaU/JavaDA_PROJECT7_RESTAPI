package com.nnk.springboot.domain;

import jakarta.persistence.*;
//import org.springframework.beans.factory.annotation.Required;
//
//import javax.persistence.*;
//import javax.validation.constraints.Digits;
//import javax.validation.constraints.NotBlank;
//import java.sql.Date;
//import java.sql.Timestamp;

@Entity
@Table(name = "bidlist")
public class BidList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer bidListId;
    private String account;
    private String type;
    private double bidQuantity;

    public BidList(String account, String type, double bidQuantity) {
        this.account = account;
        this.type = type;
        this.bidQuantity = bidQuantity;
    }
// TODO: Map columns in data table BIDLIST with corresponding java fields


    public Integer getBidListId() {
        return bidListId;
    }

    public void setBidListId(Integer bidListId) {
        this.bidListId = bidListId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBidQuantity() {
        return bidQuantity;
    }

    public void setBidQuantity(double bidQuantity) {
        this.bidQuantity = bidQuantity;
    }
}
