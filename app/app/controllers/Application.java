package controllers;

import play.mvc.Controller;
import play.mvc.Result;

@org.springframework.stereotype.Controller
public class Application extends Controller {

    public Result index() {
        return ok("hello app");
    }

}