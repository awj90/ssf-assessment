package ibf2022.batch2.ssf.frontcontroller.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ibf2022.batch2.ssf.frontcontroller.models.User;
import ibf2022.batch2.ssf.frontcontroller.services.AuthenticationService;
import jakarta.servlet.http.HttpSession;
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
			// e.printStackTrace();

		} 

		if (user.isLocked()) {
			model.addAttribute("username", user.getUsername());
			return "view2";
		}

		if (success) {
			return "view1";
		} 

		FieldError err = new FieldError("user", "password", "Username and password does not match");
		bindingResult.addError(err);
		return "view0";
			
	}

}
