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
	public User authenticate(String username, String password) throws Exception {
 
		User user = authenticationRepository.getUser(username);
		if (user == null) {
			user = new User(username);
			user.setPassword(password);
		}

		user.setAuthenticated(false);

		// Check if user is locked 
		if (isLocked(username)) {
			return user;
		} else {
			user.setLocked(false);
		}

		// If user is not locked, set up request entity and exchange for response entity
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
		
			if (message.equals("Authenticated %s".formatted(username))) {			
				user.setAuthenticated(true); // mark isAuthenticated as true
				user.setFailedLogInAttempts(0); // reset failedLogInAttempts
			} 
			authenticationRepository.saveUsers(user);

		} catch (Exception e) {
			user.setAuthenticated(false);
			user.incrFailedLogInAttempts(); // increment failedLogInAttempts
			if (user.getFailedLogInAttempts() > MAX_ALLOWABLE_LOG_IN_ATTEMPTS) {
				disableUser(username);
				user.setLocked(true);
				user.setFailedLogInAttempts(0);
			}
			authenticationRepository.saveUsers(user);
			throw e;
		}
		
		return user;
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
