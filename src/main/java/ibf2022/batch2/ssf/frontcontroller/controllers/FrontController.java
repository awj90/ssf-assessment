package ibf2022.batch2.ssf.frontcontroller.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ibf2022.batch2.ssf.frontcontroller.models.Captcha;
import ibf2022.batch2.ssf.frontcontroller.models.User;
import ibf2022.batch2.ssf.frontcontroller.services.AuthenticationService;
import jakarta.validation.Valid;

@Controller
public class FrontController {

	@Autowired
	AuthenticationService authenticationService;

	// TODO: Task 2, Task 3, Task 4, Task 6
	
	@GetMapping(path="/")
	public String renderLandingPage(Model model) {
		model.addAttribute("user", new User());
		
		return "view0";
	}
	
	@PostMapping(path="/login")
	public String logInHandler(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) throws Exception {

		if (bindingResult.hasErrors()) {
			return "view0";
		}

		boolean success = false;

		try {
			user = authenticationService.authenticate(user.getUsername(), user.getPassword());
			success = user.isAuthenticated();

		} catch (Exception e) {
			user.incrFailedLogInAttempts();

		} 

		if (user.isLocked()) {
			model.addAttribute("username", user.getUsername());
			return "view2";
		}

		if (success) {
			return "view1";
		} 

		String logInFailedMessage = "Username and password do not match. Remaining log in attempts for %s: %d".formatted(user.getUsername(), AuthenticationService.getMaxAllowableLogInAttempts() - user.getFailedLogInAttempts());
		FieldError err = new FieldError("user", "password", logInFailedMessage);
		bindingResult.addError(err);

		model.addAttribute("user", user);
		model.addAttribute("captcha", new Captcha());
		return "view0";
			
	}

	// @GetMapping(path="/logout")
	// public String logout(HttpSession session) {
	// 	User user = (User) session.getAttribute("user");
	// 	authenticationService.logout(user);
	// 	session.invalidate();
	// 	return "view0";
	// }

}
