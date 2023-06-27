package tfip.project.Controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import tfip.project.Models.EventInfo;
import tfip.project.Services.CalendarService;

@Controller
@RequestMapping(path="/api/calender")
public class CalendarController {
    
    @Autowired
    private CalendarService calendarSvc;

    @GetMapping(path = "/oauth")
    public ResponseEntity<String> getAuthorizationUrl(@RequestParam String userId, @RequestParam String recipeTitle) throws IOException, GeneralSecurityException {
        String url = calendarSvc.getAuthorizationUrl(userId, recipeTitle);
        JsonObject jo = Json.createObjectBuilder()
            .add("response", url)
            .build();
        System.out.println(url);
        return ResponseEntity.status(HttpStatus.OK).body(jo.toString());
    }

    @PostMapping(path="/event/{userId}")
    public ResponseEntity<String> createEvent(@PathVariable String userId, @RequestBody EventInfo eventInfo) throws IOException, GeneralSecurityException, URISyntaxException {  
        calendarSvc.createEvent(userId, eventInfo);
        JsonObject jo = Json.createObjectBuilder()
                        .add("response", "Event created successfully")
                        .build();
        return ResponseEntity.status(HttpStatus.OK).body(jo.toString());
    }

}   
