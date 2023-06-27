package tfip.project.Services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import tfip.project.Models.EventInfo;

@Service
public class CalendarService {
    
    private static final String APPLICATION_NAME = "TFIP";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);
    private static final String rootUrl = "https://tfipcookado-production.up.railway.app/event";
    private static final String CALENDAR_ID = "primary";
    
    public String getAuthorizationUrl(String userId, String recipeTitle) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = CalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            // .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setDataStoreFactory(new MemoryDataStoreFactory())
            .setAccessType("offline")
            .build();
        String url = flow.newAuthorizationUrl()
            .setAccessType("offline")
            .setClientId(clientSecrets.getDetails().getClientId())
            .setRedirectUri(rootUrl)
            .setResponseTypes(Arrays.asList("code"))
            .setScopes(SCOPES)
            .setState(userId+recipeTitle)
            .toString();
        return url;
    }

    public void createEvent(String userId, EventInfo eventInfo) throws GeneralSecurityException, IOException  {
        String code = eventInfo.getOauthCode();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, userId, code))
            .setApplicationName(APPLICATION_NAME)
            .build();
        Event event = new Event()
            .setSummary(eventInfo.getTitle())
            .setDescription(eventInfo.getDescription());
        EventDateTime eventDate = new EventDateTime()
            .setDate(new DateTime("2023-06-28"))
            .setTimeZone("Asia/Singapore");
        event.setStart(eventDate);
        event.setEnd(eventDate);
        service.events().insert(CALENDAR_ID, event).execute();
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String userId, String code) throws IOException {
        InputStream in = CalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            // .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setDataStoreFactory(new MemoryDataStoreFactory())
            .setAccessType("offline")
            .build();
        Credential credential = flow.loadCredential(userId);
        if (credential == null) {
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(rootUrl)
                .execute();
            credential = flow.createAndStoreCredential(tokenResponse, userId);
        }
        return credential;
    }

}
