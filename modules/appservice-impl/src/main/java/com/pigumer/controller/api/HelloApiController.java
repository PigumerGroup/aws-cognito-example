package com.pigumer.controller.api;

import com.pigumer.controller.model.InlineResponse200;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class HelloApiController implements HelloApi {
    private static final Logger logger = LoggerFactory.getLogger(HelloApiController.class);

    private final NativeWebRequest request;
    private final HttpSession session;

    @org.springframework.beans.factory.annotation.Autowired
    public HelloApiController(NativeWebRequest request, HttpSession session) {
        this.request = request;
        this.session = session;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<InlineResponse200> hello() {
        String idToken = (String) session.getAttribute("idToken");
        logger.info("idToken: " + idToken);
        if (idToken == null) {
           return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        InlineResponse200 res = new InlineResponse200();
        res.setMessage("Hello World!!");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
