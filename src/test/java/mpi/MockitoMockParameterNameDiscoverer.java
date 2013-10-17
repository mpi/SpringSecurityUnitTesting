package mpi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.mockito.cglib.proxy.Enhancer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * Spring Security requires debug information about local variable names in order to use
 * parameter names in @PreAuthorize expresions (e.g. @PreAuthorize("#document.ownedBy == principal"). 
 * Because Mockito uses CGLIB, that do not save debug informations, local variable names has to
 * be extracted from first not enhanced superclass. 
 */
public class MockitoMockParameterNameDiscoverer implements ParameterNameDiscoverer {

    private ParameterNameDiscoverer delegate = new LocalVariableTableParameterNameDiscoverer();
    
    public String[] getParameterNames(Method m) {
        
        Class<?> notProxyClass = unwrapFromProxy(m.getDeclaringClass());
        
        try {
            
            Method notProxiedMethod = notProxyClass.getMethod(m.getName(), m.getParameterTypes());
            return delegate.getParameterNames(notProxiedMethod);
            
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        
    }

    public String[] getParameterNames(@SuppressWarnings("rawtypes") Constructor c) {

        Class<?> notProxyClass = unwrapFromProxy(c.getDeclaringClass());
        
        try {
            
            Constructor<?> notProxiedConstructor = notProxyClass.getConstructor(c.getParameterTypes());
            return delegate.getParameterNames(notProxiedConstructor);
            
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Class<?> unwrapFromProxy(Class<?> proxied) {
        while(Enhancer.isEnhanced(proxied)){
            proxied = proxied.getSuperclass();
        }
        return proxied;
    }

}
