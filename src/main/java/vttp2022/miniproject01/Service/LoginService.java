package vttp2022.miniproject01.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp2022.miniproject01.Repository.MovieRepository;

@Service
public class LoginService {
    
    @Autowired
    private MovieRepository movieRepo;

    public Boolean isUserValid (String name) {
        return movieRepo.isUserValid(name);
    }

}
