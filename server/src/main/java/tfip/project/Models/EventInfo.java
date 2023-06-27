package tfip.project.Models;

import java.time.LocalDate;

public class EventInfo {

    private String title;
    private String description;
    private LocalDate cookDate;
    private String oauthCode;
    
    public EventInfo() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOauthCode() {
        return oauthCode;
    }

    public void setOauthCode(String oauthCode) {
        this.oauthCode = oauthCode;
    }

    public LocalDate getCookDate() {
        return cookDate;
    }

    public void setCookDate(LocalDate cookDate) {
        this.cookDate = cookDate;
    }
    
}
