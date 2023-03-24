package ibf2022.batch2.ssf.frontcontroller.respositories;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import ibf2022.batch2.ssf.frontcontroller.models.User;

@Repository
public class AuthenticationRepository {

	@Autowired
	RedisTemplate<String,Object> redisTemplate;

	private static final String LIST_OF_USERS = "listOfUserObjects";

	// TODO Task 5
	// Use this class to implement CRUD operations on Redis

	public void addToDisabledList(String username) {
		redisTemplate.opsForValue().set(username, username);
		redisTemplate.expire(username, Duration.ofMinutes(30L));
	}

	public boolean isDisabled(String username) {
		return redisTemplate.hasKey(username);
	}

	public void saveUsers(User user) {
		redisTemplate.opsForHash().put(LIST_OF_USERS, user.getUsername(), user.toJsonObject().toString());
	}

	public User getUser(String username) {

		if (redisTemplate.opsForHash().hasKey(LIST_OF_USERS, username)) {
			return User.jsonStringToJavaObject((String) redisTemplate.opsForHash().get(LIST_OF_USERS, username));
		} else {
			return null;
		}
	}
}
