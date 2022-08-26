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
    
    @GetMapping
    public String welcomepage () {
        return "index";
    }

    @GetMapping (path="register")
    public String registerpage () {
        return "register";
    }

    @PostMapping 
    public String checkUser (@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {
        String name = form.getFirst("name");
        String password = form.getFirst("password");

        Boolean[] isExist = accountSvc.checkUser(name, password);

        Boolean isName = isExist[0];
        Boolean isPass = isExist[1];

        if (isName == true && isPass == true) {
            sess.setAttribute("name", name);
            return "redirect:/home";

        } else if (isName == true && isPass == false) {
            model.addAttribute("loginerror", "Password is wrong");
            return "index";

        } else if (isName == false && isPass == false) {
            model.addAttribute("loginerror", "User does not exist, please register");
        }

        return "index";
    }

    @PostMapping (path="createacc")
    public String createAccount (@RequestBody MultiValueMap<String, String> form, Model model, HttpSession sess) {
        String name = form.getFirst("name");
        String password = form.getFirst("password");

        accountSvc.createAccount(name, password);
        sess.setAttribute("name", name);

        return "redirect:/home";
    }

}
