package org.example.config;

import org.example.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {

       // return customer;

        /*DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date dob = df.parse(customer.getDob());
        Date today = Date.from(Instant.now());

        Calendar dobCal = Calendar.getInstance();
        Calendar todayCal = Calendar.getInstance();
        dobCal.setTime(df.parse(customer.getDob()));
        todayCal.setTime(today);

        dobCal.add(GregorianCalendar.YEAR, 18);

        if(dobCal.before(todayCal)) {
            System.out.println("dob: " + dob);
            return customer;
        }
        else return null;*/

        try{
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date dob = df.parse(customer.getDob());
            return customer;
        }catch (Exception e){
            throw e;
        }

    }

}
