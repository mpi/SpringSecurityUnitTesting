package mpi;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class SecurityTesting {

    public static void destroy() {
        SecurityContextHolder.clearContext();
    }

    public static void userHasNoRoles() {
        userHasRoles();
    }

    public static void userHasRoles(final String... roles) {

        TestingSecurityAuthentication auth = setupAuthentication();
        auth.addRoles(roles);
    }

    public static void userPrincipalIs(Object principal) {
        
        TestingSecurityAuthentication auth = setupAuthentication();
        auth.principal = principal;
    }
    
    private static TestingSecurityAuthentication setupAuthentication() {
        
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if(authentication == null || !(authentication instanceof TestingSecurityAuthentication)){
            authentication = new TestingSecurityAuthentication();
            context.setAuthentication(authentication);
        }
        
        return (TestingSecurityAuthentication) authentication;
    }

    @SuppressWarnings("unchecked")
    public static <T> T secure(T bean) {
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setConfigLocation("classpath:test-security-context.xml");
        context.refresh();
        
        return (T) context.getAutowireCapableBeanFactory().initializeBean(bean, "secured");
    }
    
    private static class TestingSecurityAuthentication implements Authentication{

        private static final long serialVersionUID = 1L;

        private String name = "Username";
        private Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        private Object credentials = "password";
        private Object details = null;
        private Object principal = "Username";
        private boolean authenticated = true;

        @Override
        public String getName() {
            return name;
        }

        public void addRoles(String... roles) {
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public Object getCredentials() {
            return credentials;
        }

        @Override
        public Object getDetails() {
            return details;
        }

        @Override
        public Object getPrincipal() {
            return principal;
        }

        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        }
        
    }
}
