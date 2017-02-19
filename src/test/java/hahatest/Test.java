package hahatest;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {
	public Test() {

	}

	@SuppressWarnings("resource")
	public static void main(String args[]) {
		@SuppressWarnings("unused")
		final AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"classpath:applicationContext.xml");
	}

}
