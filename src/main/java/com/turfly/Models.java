package com.turfly;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.*;
enum Role { CUSTOMER, ADMIN } enum TurfStatus { ACTIVE, INACTIVE } enum BookingStatus { CONFIRMED, CANCELLED } enum PaymentStatus { PAID, REFUNDED }
@Entity @Table(name="app_users") class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(nullable=false) String name; @Column(nullable=false,unique=true) String email; String phone; @Column(nullable=false) String password; @Enumerated(EnumType.STRING) @Column(nullable=false) Role role=Role.CUSTOMER;
 protected AppUser(){} AppUser(String n,String e,String p,String pw,Role r){name=n;email=e;phone=p;password=pw;role=r;}
}
@Entity @Table(name="turfs") class Turf {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(nullable=false) String name; @Column(nullable=false) String location; @Column(nullable=false) String sport; @Column(length=2000) String description; @Column(name="price_per_hour",nullable=false,precision=10,scale=2) BigDecimal pricePerHour; @Column(length=1000) String imageUrl; @Enumerated(EnumType.STRING) @Column(nullable=false) TurfStatus status=TurfStatus.ACTIVE;
 protected Turf(){}
 public Long getId(){return id;} public String getName(){return name;} public String getLocation(){return location;} public String getSport(){return sport;} public String getDescription(){return description;} public BigDecimal getPricePerHour(){return pricePerHour;} public String getImageUrl(){return imageUrl;} public TurfStatus getStatus(){return status;}
}
@Entity @Table(name="bookings", indexes=@Index(name="idx_booking_slot",columnList="turf_id,booking_date,start_time,end_time")) class Booking {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="user_id") AppUser user; @ManyToOne(optional=false,fetch=FetchType.EAGER) @JoinColumn(name="turf_id") Turf turf; @Column(name="booking_date",nullable=false) LocalDate date; @Column(name="start_time",nullable=false) LocalTime startTime; @Column(name="end_time",nullable=false) LocalTime endTime; @Column(nullable=false,precision=10,scale=2) BigDecimal amount; @Enumerated(EnumType.STRING) @Column(name="booking_status",nullable=false) BookingStatus status=BookingStatus.CONFIRMED; @Enumerated(EnumType.STRING) @Column(name="payment_status",nullable=false) PaymentStatus paymentStatus=PaymentStatus.PAID; @Column(name="created_at",nullable=false) LocalDateTime createdAt=LocalDateTime.now(); protected Booking(){}
}
