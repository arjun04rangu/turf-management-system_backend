package com.turfly;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;

@Configuration class Bootstrap {
 @Bean CommandLineRunner seedDemo(TurfRepository turfs, UserRepository users, PasswordEncoder encoder,
     @Value("${ADMIN_EMAIL:}") String email, @Value("${ADMIN_PASSWORD:}") String password) {
  return args -> {
   String[] names={"The Green Arena","Cityside Cricket Park","Baseline Sports Club"};
   String[] previousNames={"The Green Arena","Cityside Football Park","Baseline Sports Club"};
   String[] places={"Gachibowli, Hyderabad","Madhapur, Hyderabad","Kondapur, Hyderabad"};
   String[] sports={"Football","Cricket","Badminton"};
   String[] images={
    "https://images.unsplash.com/photo-1522778119026-d647f0596c20?w=1000",
    "https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=1000",
    "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=1000"
   };
   boolean emptyDatabase=turfs.count()==0;
   for(int i=0;i<names.length;i++) {
    Turf turf=turfs.findByName(names[i]).orElseGet(()->null);
    if(turf==null) turf=turfs.findByName(previousNames[i]).orElse(null);
    if(turf!=null) {
     // Migrate the existing sample listings while preserving any admin edits.
     turf.name=names[i]; turf.location=places[i]; turfs.save(turf);
    } else if(emptyDatabase) {
     turf=new Turf(); turf.name=names[i]; turf.location=places[i]; turf.sport=sports[i];
     turf.description="Professionally maintained, bookable sports venue with quality lighting and changing facilities.";
     turf.pricePerHour=BigDecimal.valueOf(800+i*250); turf.imageUrl=images[i]; turf.status=TurfStatus.ACTIVE;
     turfs.save(turf);
    }
   }
   if(!email.isBlank()&&!password.isBlank()&&!users.existsByEmail(email.toLowerCase()))
    users.save(new AppUser("Administrator",email.toLowerCase(),"",encoder.encode(password),Role.ADMIN));
  };
 }
}
