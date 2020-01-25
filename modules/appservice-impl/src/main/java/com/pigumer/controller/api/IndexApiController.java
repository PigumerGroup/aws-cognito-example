package com.pigumer.controller.api;

import com.pigumer.adapter.TokenAdapter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class IndexApiController implements TokenAdapter {

    private static final Logger logger = LoggerFactory.getLogger(IndexApiController.class);

    private final NativeWebRequest request;
    private final HttpSession session;

    private final String protocol = "https";
    private final int port = 443;

    @Value("${openid.host}")
    private String host;
    @Value("${openid.client_id}")
    private String clientId;
    @Value("${openid.client_secret}")
    private String clientSecret;

    @Value("${openid.redirect_uri}")
    private String redirectUri;

    @org.springframework.beans.factory.annotation.Autowired
    public IndexApiController(NativeWebRequest request, HttpSession session) {
        this.request = request;
        this.session = session;
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    private URL getAuthorizeUrl() {
        logger.info("host: " + host);
        logger.info("client_id: " + clientId);
        logger.info("client_secret: " + clientSecret);
        logger.info("redirect_uri: " + redirectUri);
        try {
            String path = "/oauth2/authorize";
            String query = "response_type=code&" +
                    "client_id=" + clientId + "&" +
                    "redirect_uri=" + redirectUri + "&" +
                    "scope=email+openid";

            return new URI(protocol, null, host, port, path, query, null).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public URI getTokenUri() {
        try {
            String path = "/oauth2/token";
            return new URI(protocol, null, host, port, path, null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation(value = "Index", nickname = "index", notes = "index", response = String.class, tags={ "index", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "インデックスページを返す", response = String.class) })
    @RequestMapping(value = "/index",
            produces = { "text/html" },
            method = RequestMethod.GET)
    public ModelAndView index(@ApiParam(value = "") @Valid @RequestParam(value = "code", required = false) String code, @ApiParam(value = "") @Valid @RequestParam(value = "state", required = false) String state) {
        logger.info("start index");
        if (code != null) {
            logger.info("code: " + code);
            String idToken = validateAuthorizationCode(code);
            if (idToken != null) {
                session.setAttribute("idToken", idToken);
            }
        }
        Map<String, String> indexModel = new HashMap<>();
        indexModel.put("loginUrl", getAuthorizeUrl().toExternalForm());
        return new ModelAndView("index", "index", indexModel);
    }
}
