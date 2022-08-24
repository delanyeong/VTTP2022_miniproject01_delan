package vttp2022.miniproject01.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import vttp2022.miniproject01.Service.LoginService;

@Controller
@RequestMapping
public class LoginController {
    
    @Autowired
    private LoginService loginSvc;

    @PostMapping
    public String postUser
    (@RequestBody MultiValueMap<String, String> form,
    Model model) {

        String name = form.getFirst("name");
        Boolean isUserValid = loginSvc.isUserValid(name);

        model.addAttribute("name", name);

        return "welcomepage";
    }

}
