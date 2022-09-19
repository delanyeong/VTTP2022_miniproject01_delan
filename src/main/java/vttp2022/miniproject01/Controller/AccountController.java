package vttp2022.miniproject01.Controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import vttp2022.miniproject01.Service.AccountService;

@Controller
@RequestMapping
public class AccountController {

    @Autowired
    private AccountService accountSvc;
    
    /*
     * First-Landing-Login page when app launches
     */
    @GetMapping
    public String welcomepage () {
        return "index";
    }

    /*
     * Form-Post-Method for Checking for Existing User
     */
    @PostMapping 
    public String checkUser (@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {
        String name = form.getFirst("name");
        String password = form.getFirst("password");
        
        Boolean[] isExist = accountSvc.checkUser(name, password);
        
        Boolean isName = isExist[0]; //Username
        Boolean isPass = isExist[1]; //Password
        
        // CASE 1: Username CORRECT, Password CORRECT - redirect to Trending Page
        if (isName == true && isPass == true) {
            sess.setAttribute("name", name);
            sess.setAttribute("page", "trend");
            return "redirect:/home";
        
        // CASE 2: Username CORRECT, Password WRONG - return to Login page
        } else if (isName == true && isPass == false) { 
            model.addAttribute("loginerror", "Password is wrong");
            return "index";
        
        // CASE 3: Username WRONG, Password WRONG - return to Login page
        } else if (isName == false && isPass == false) { 
            model.addAttribute("loginerror", "User does not exist, please register");
        }
        
        return "index";
    }
    
    /*
     * Register account page
     */
    @GetMapping (path="register")
    public String registerpage () {
        return "register";
    }

    /* 
     * Form-Post-Method for Registering User
     * redirects to trending page after registering
     */
    @PostMapping (path="createacc")
    public String createAccount (@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {
        String name = form.getFirst("name");
        String password = form.getFirst("password");

        accountSvc.createAccount(name, password);

        // Attribute for Database Access + UI
        sess.setAttribute("name", name);

        // Attribute for Returning Same Page after Favouriting Movie (trend/search/recommend)
        sess.setAttribute("page", "trend"); 

        return "redirect:/home";
    }

}
