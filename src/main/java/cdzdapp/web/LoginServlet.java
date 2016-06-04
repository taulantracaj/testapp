package cdzdapp.web;

import cdzdapp.domain.User;
import cdzdapp.repository.InMemoryUserRepository;
import cdzdapp.repository.UserRepository;
import cdzdapp.util.EnvironmentDetection;
import cdzdapp.util.ServerInfo;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginServlet extends HttpServlet {
    private UserRepository userRepository = InMemoryUserRepository.INSTANCE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if ((session != null) && (session.getAttribute("user") != null)) {
            response.sendRedirect("/friends");
            return;
        }

        ServerInfo serverInfo = new ServerInfo();
        Map<String, Object> values = new HashMap<>();
        values.put("server", serverInfo.getHostName());
        values.put("version", serverInfo.getVersion());
        values.put("env", EnvironmentDetection.detectEnvironment());

        response.setCharacterEncoding("UTF-8");
        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        Mustache mustache = mustacheFactory.compile("templates/login.html");
        mustache.execute(response.getWriter(), values).flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("user");
        System.out.println("User: " + name);

        if (name != null && !name.isEmpty()) {
            User user = userRepository.getUserByName(name.trim());
            if (user == null) {
                user = new User(name.trim());
                userRepository.addUser(user);
            }

            request.getSession(true).setAttribute("user", user.getId().toString());
            response.sendRedirect("/friends");
            return;
        }

        response.sendRedirect("/");
    }
}