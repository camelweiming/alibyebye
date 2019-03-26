package com.abb.bye.web;

import com.alibaba.boot.velocity.annotation.VelocityLayout;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * @author cenpeng.lwm
 * @since 2019/3/26
 */
@Controller
public class HelloController {
    private static OAuth20Service service = new ServiceBuilder("08dc1958197963ae68f5")
        .apiSecret("b5bbbf8af867b87c06294514200bbbad222a7c04")
        .callback("http://localhost:8080/hello")
        .build(GitHubApi.instance());
    private static final String PROTECTED_RESOURCE_URL = "https://api.github.com/user";

    @RequestMapping(value = "hello", method = {RequestMethod.POST, RequestMethod.GET})
    @VelocityLayout("/velocity/layout/index.vm")
    void hello(HttpServletRequest request) {
        String code = request.getParameter("code");
        if (code == null) {
            String error = request.getParameter("error");
            String errorDesc = request.getParameter("error_description");
            System.out.println("[" + error + "]:" + errorDesc);
            return;
        }
        System.out.println("success:" + code);
        final OAuth2AccessToken accessToken;
        try {
            accessToken = service.getAccessToken(code);
            System.out.println("Got the Access Token!");
            System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
            System.out.println();

            // Now let's go and ask for a protected resource!
            System.out.println("Now we're going to access a protected resource...");
            final OAuthRequest req = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            service.signRequest(accessToken, req);
            final Response response = service.execute(req);
            System.out.println("Got it! Lets see what we found...");
            System.out.println();
            System.out.println(response.getCode());
            System.out.println(response.getBody());

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final String secretState = "secret" + new Random().nextInt(999_999);
        final String authorizationUrl = service.createAuthorizationUrlBuilder()
            .state(secretState)
            .build();
        System.out.println(authorizationUrl);
    }
}
