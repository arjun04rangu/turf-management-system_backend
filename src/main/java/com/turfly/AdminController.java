package com.turfly;
import jakarta.validation.Valid; import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import org.springframework.web.server.ResponseStatusException;import java.util.*;
@RestController @RequestMapping("/api/admin") class AdminController {
 final UserRepository users;final TurfRepository turfs;final BookingRepository bookings;final BookingController bookingView;
 AdminController(UserRepository u,TurfRepository t,BookingRepository b,BookingController v){users=u;turfs=t;bookings=b;bookingView=v;}
 @GetMapping("/dashboard") Map<String,Object> dashboard(){return Map.of("users",users.count(),"turfs",turfs.count(),"bookings",bookings.count(),"activeBookings",bookings.countByStatus(BookingStatus.CONFIRMED),"revenue",bookings.revenue());}
 @GetMapping("/users") List<Map<String,Object>> userList(){return users.findAll().stream().map(u->Map.<String,Object>of("id",u.id,"name",u.name,"email",u.email,"phone",u.phone==null?"":u.phone,"role",u.role)).toList();}
 @PatchMapping("/users/{id}/role") Map<String,Object> role(@PathVariable Long id,@RequestBody Map<String,String> body){AppUser u=users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));try{u.role=Role.valueOf(body.get("role"));}catch(Exception e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Role must be CUSTOMER or ADMIN");}users.save(u);return AuthController.view(u);}
 @GetMapping("/bookings") List<Map<String,Object>> bookingList(){return bookings.findAllByOrderByCreatedAtDesc().stream().map(bookingView::view).toList();}
 @PatchMapping("/bookings/{id}/cancel") Map<String,Object> cancel(@PathVariable Long id){Booking b=bookings.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(b.status==BookingStatus.CONFIRMED){b.status=BookingStatus.CANCELLED;b.paymentStatus=PaymentStatus.REFUNDED;}return bookingView.view(bookings.save(b));}
 @GetMapping("/turfs") List<Turf> allTurfs(){return turfs.findAll();}
 @PostMapping("/turfs") ResponseEntity<Turf> add(@Valid @RequestBody TurfRequest r){Turf t=new Turf();apply(t,r);return ResponseEntity.status(HttpStatus.CREATED).body(turfs.save(t));}
 @PutMapping("/turfs/{id}") Turf edit(@PathVariable Long id,@Valid @RequestBody TurfRequest r){Turf t=turfs.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));apply(t,r);return turfs.save(t);}
 @DeleteMapping("/turfs/{id}") ResponseEntity<?> delete(@PathVariable Long id){Turf t=turfs.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));t.status=TurfStatus.INACTIVE;turfs.save(t);return ResponseEntity.noContent().build();}
 void apply(Turf t,TurfRequest r){t.name=r.name();t.location=r.location();t.sport=r.sport();t.description=r.description();t.pricePerHour=r.pricePerHour();t.imageUrl=r.imageUrl();t.status=r.status()==null?TurfStatus.ACTIVE:r.status();}
}
