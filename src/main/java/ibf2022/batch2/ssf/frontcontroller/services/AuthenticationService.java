package ibf2022.batch2.ssf.frontcontroller.services;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ibf2022.batch2.ssf.frontcontroller.models.User;
import ibf2022.batch2.ssf.frontcontroller.respositories.AuthenticationRepository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class AuthenticationService {

	// @Value("${ssf.assessment.authentication.api.url}")
    // private String url; // https://auth.chuklee.com/api/authenticate

	@Autowired
	AuthenticationRepository authenticationRepository;

	private static final int MAX_ALLOWABLE_LOG_IN_ATTEMPTS = 3;

	// TODO: Task 2
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write the authentication method in here
	public boolean authenticate(String username, String password) throws Exception {

		if (isLocked(username)) {
			
		}

		// Set up request entity and exchange for response entity
        RequestEntity<String> req = 
                        RequestEntity.post("https://auth.chuklee.com/api/authenticate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .body(Json.createObjectBuilder()
												.add("username", username)
												.add("password", password)
												.build()
												.toString());
        
        RestTemplate template = new RestTemplate();
		
		try {

			ResponseEntity<String> resp = template.exchange(req, String.class); 
			
			// Marshal the response body
			StringReader sr = new StringReader(resp.getBody());
			JsonReader jsonReader = Json.createReader(sr);
			JsonObject jsonObject = jsonReader.readObject();
			String message = jsonObject.getString("message");
		
			User user = new User(username);
		
			if (message.equals("Authenticated %s".formatted(username))) {			
				user.setAuthenticated(true); // mark isAuthenticated as true
				user.setFailedLogInAttempts(0); // reset failedLogInAttempts
				return true;
			} else {
				user.incrFailedLogInAttempts(); // increment failedLogInAttempts
				if (user.getFailedLogInAttempts() > MAX_ALLOWABLE_LOG_IN_ATTEMPTS) {
					disableUser(username);
				}
				return false;
				// redisplay view0 with error message and a captcha
				// if failedLogInAttempts > 3, call disableUser method below
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// TODO: Task 3
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write an implementation to disable a user account for 30 mins
	public void disableUser(String username) {
		authenticationRepository.addToDisabledList(username);
	}

	// TODO: Task 5
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write an implementation to check if a given user's login has been disabled
	public boolean isLocked(String username) {
		return authenticationRepository.isDisabled(username);
	}
}
