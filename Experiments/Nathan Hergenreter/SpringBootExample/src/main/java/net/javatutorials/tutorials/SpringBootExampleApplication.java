package net.javatutorials.tutorials;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Controller
@SpringBootApplication
public class SpringBootExampleApplication {

	int test_num=100;

//	String planetstuff() {
//		ArrayList<Planet> planets = MapGenerator.generate(test_num);
//		for(int i=0; i<test_num-1; i++)
//		{
//			output(planets.get(i).toString());
//		}
//		output(planets.get(test_num-1).toString());
//		ArrayList<Planet> planets = MapGenerator.generate(100);
//		return planets.get(0).toString();
//		Planet testp = new Planet(0);
//		return testp.toString();
//	}

	@RequestMapping("/")
	@ResponseBody
	String home()
	{
//		ArrayList<Planet> planets = MapGenerator.generate(100);
//		return planets.get(0).toString();
//		return planetstuff();
		String ret = "";
		ArrayList<Planet> planets = MapGenerator.generate(test_num);
		for(int i=0; i<test_num; i++)
		{
			ret+=planets.get(i).toString();
			ret+="\n\n";
		}
		return ret;
	}
	
//	String output(String s)
//	{
//		return s;
//	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootExampleApplication.class, args);
	}

}

