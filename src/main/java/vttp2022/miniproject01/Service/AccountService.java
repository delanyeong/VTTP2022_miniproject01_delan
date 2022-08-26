package vttp2022.miniproject01.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp2022.miniproject01.Repository.MovieRepository;

@Service
public class AccountService {
    
    @Autowired
    private MovieRepository movieRepo;

    public Boolean[] checkUser (String name, String password) {
        Boolean[] isExist = movieRepo.checkUser(name, password);
        return isExist;
    }

    public Boolean createAccount (String name, String password) {
        return movieRepo.createAccount(name, password);
    }

}
