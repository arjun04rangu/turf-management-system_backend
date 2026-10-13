package com.turfly;
import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import jakarta.persistence.LockModeType; import java.time.*; import java.util.*;
interface UserRepository extends JpaRepository<AppUser,Long>{Optional<AppUser> findByEmail(String email); boolean existsByEmail(String email);}
interface TurfRepository extends JpaRepository<Turf,Long>{ Optional<Turf> findByName(String name);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select t from Turf t where t.id=:id") Optional<Turf> lockById(@Param("id") Long id);
 @Query("select t from Turf t where t.status=com.turfly.TurfStatus.ACTIVE and (:q is null or lower(t.name) like lower(concat('%',:q,'%')) or lower(t.location) like lower(concat('%',:q,'%')) or lower(t.sport) like lower(concat('%',:q,'%'))) and (:sport is null or lower(t.sport)=lower(:sport))") List<Turf> search(@Param("q") String q,@Param("sport") String sport);
}
interface BookingRepository extends JpaRepository<Booking,Long>{
 @Query("select count(b)>0 from Booking b where b.turf.id=:turf and b.date=:date and b.status=com.turfly.BookingStatus.CONFIRMED and b.startTime<:end and b.endTime>:start") boolean overlaps(@Param("turf") Long turf,@Param("date") LocalDate date,@Param("start") LocalTime start,@Param("end") LocalTime end);
 List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId); List<Booking> findAllByOrderByCreatedAtDesc(); long countByStatus(BookingStatus status);
 @Query("select coalesce(sum(b.amount),0) from Booking b where b.status=com.turfly.BookingStatus.CONFIRMED and b.paymentStatus=com.turfly.PaymentStatus.PAID") java.math.BigDecimal revenue();
}
