package app.mealsmadeeasy.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("/")
public class GreetingController {

    @GetMapping("/greeting")
    @ResponseBody
    public String get() {
        return "Hello, World!";
    }

}
