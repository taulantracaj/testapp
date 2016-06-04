package cdzdapp;

import cdzdapp.domain.Friend;
import cdzdapp.domain.User;
import cdzdapp.repository.InMemoryFriendRepository;
import cdzdapp.repository.InMemoryUserRepository;
import cdzdapp.web.FriendsServlet;
import cdzdapp.web.LoginServlet;
import cdzdapp.web.LogoutServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Main {
    public static final int HTTP_SERVER_PORT = 9100;

    private static Server httpServer;
    private static Server shutdownServer;

    public static void main(String[] args) {
        start();
        try {
            httpServer.join();
        } catch (Exception e) {
            System.out.println("Unable to start server !");
            e.printStackTrace();
        }
    }

    public static void start() {
        createTestData();

        createHttpServer();
        createShutdownServer();

        try {
            shutdownServer.start();
            httpServer.start();
        } catch (Exception e) {
            System.out.println("Unable to start server !");
            e.printStackTrace();
        }
    }

    public static void stop() {
        new Thread() {
            @Override
            public void run() {
                try {
                    shutdownServer.stop();
                    httpServer.stop();
                } catch (Exception e) {
                    System.out.println("Unable to shutdown server !");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void createTestData() {
        User bart = new User("Bart");
        InMemoryUserRepository.INSTANCE.addUser(bart);

        InMemoryFriendRepository.INSTANCE.addFriend(new Friend(bart, "Homer", "Simpson"));
        InMemoryFriendRepository.INSTANCE.addFriend(new Friend(bart, "Ned", "Flanders"));
        InMemoryFriendRepository.INSTANCE.addFriend(new Friend(bart, "Montgommery", "Burns"));
    }

    private static void createHttpServer() {
        httpServer = new Server(HTTP_SERVER_PORT);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setBaseResource(Resource.newClassPathResource("web"));
        webAppContext.addServlet(LoginServlet.class.getName(), "/");
        webAppContext.addServlet(FriendsServlet.class.getName(), "/friends");
        webAppContext.addServlet(LogoutServlet.class.getName(), "/logout");
        webAppContext.setConfigurations(new Configuration[] {new WebXmlConfiguration()});

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{webAppContext, new DefaultHandler()});
        httpServer.setHandler(handlers);
    }

    private static void createShutdownServer() {
        shutdownServer = new Server(9099);
        shutdownServer.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                if ("GET".equals(request.getMethod())) {
                    try {
                        Main.stop();
                    } catch (Exception e) {
                        System.out.println("Unable to shutdown server !");
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}