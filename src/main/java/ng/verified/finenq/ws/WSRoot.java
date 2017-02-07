package ng.verified.finenq.ws;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import ng.verified.finenq.ws.wrapper.AcctEnqWrapper;

@ApplicationPath(value = "/api")
public class WSRoot extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		// TODO Auto-generated method stub
		
		Set<Class<?>> classes = new HashSet<>();
		classes.add(AcctEnqWrapper.class);
		
		return classes;
	}
	
	

}
