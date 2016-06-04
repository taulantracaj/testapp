package cdzdapp.web;

import cdzdapp.domain.Friend;
import cdzdapp.domain.User;
import cdzdapp.repository.FriendRepository;
import cdzdapp.repository.InMemoryFriendRepository;
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

public class FriendsServlet extends HttpServlet {
    private final UserRepository userRepository = InMemoryUserRepository.INSTANCE;
    private final FriendRepository friendRepository = InMemoryFriendRepository.INSTANCE;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = getUser(request);
        if (user == null) {
            request.getRequestDispatcher("/logout").forward(request, response);
            return;
        }

        ServerInfo serverInfo = new ServerInfo();
        Map<String, Object> values = new HashMap<>();
        values.put("server", serverInfo.getHostName());
        values.put("version", serverInfo.getVersion());
        values.put("env", EnvironmentDetection.detectEnvironment());
        values.put("name", user.getName());
        values.put("friends", friendRepository.getFriendsForUser(user));

        response.setCharacterEncoding("UTF-8");
        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        Mustache mustache = mustacheFactory.compile("templates/friends.html");
        mustache.execute(response.getWriter(), values).flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = getUser(request);
        if (user == null) {
            request.getRequestDispatcher("/logout").forward(request, response);
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        System.out.println("First Name: " + firstName + " / Last Name: " + lastName);

        if ((firstName != null && !firstName.trim().isEmpty()) && (lastName != null && !lastName.trim().isEmpty())) {
            friendRepository.addFriend(new Friend(user, firstName.trim(), lastName.trim()));
        }

        response.sendRedirect("/friends");
    }

    private User getUser(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        String userId = (String) session.getAttribute("user");
        if (userId == null) {
            return null;
        }

        return userRepository.getUserById(Integer.valueOf(userId));
    }
}