package com.samdev.videoOnDemand.Entity;


import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name="user_register_verification")
public class UserRegistrationVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="username", nullable = false)
    private String username;
    @Column(name="otp", nullable=false)
    private Integer otp;
    @Column(name="expired", nullable=false)
    private Date expired;
    @Column(name="isused", nullable=false)
    private Boolean isUsed;

    public UserRegistrationVerification() {
    }

    public UserRegistrationVerification(Long id, String username, Integer otp, Date expired, Boolean isUsed) {
        this.id = id;
        this.username = username;
        this.otp = otp;
        this.expired = expired;
        this.isUsed = isUsed;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Integer getOtp() {
        return otp;
    }

    public Date getExpired() {
        return expired;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }
}
