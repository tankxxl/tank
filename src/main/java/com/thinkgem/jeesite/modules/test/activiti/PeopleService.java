package com.thinkgem.jeesite.modules.test.activiti;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jason Westra
 *
 */
@Service
public class PeopleService {

	public static AtomicInteger callCount = new AtomicInteger(0);
	
	/**
	 * 
	 */
	public PeopleService() {
		
	}
	
	public List<String> getPeopleByLocation(String location) {
		System.out.println("PeopleService: getPeopleByLocation called: "+location);
		
		callCount.incrementAndGet();

		if (location.equals("CN")) {
			String[] people = {"rgz"};

			List<String> list = Arrays.asList(people);

			System.out.println("PeopleService: getPeopleByLocation returning: "+list);
			return list;
		}

		if (location.equals("CN4")) {
			String[] people = {"rgz1", "rgz2", "rgz3", "rgz4"};

			List<String> list = Arrays.asList(people);

			System.out.println("PeopleService: getPeopleByLocation returning: "+list);
			return list;
		}
		
		if (location.equals("US")) {
			String[] people = {"Joe", "Bob", "Betty"};
			
			List<String> list = Arrays.asList(people);
			
			System.out.println("PeopleService: getPeopleByLocation returning: "+list);
			return list;
		}
		
		if (location.equals("CA")) {
			String[] people = {"Canada-Bill", "Canada-Fred"};
			
			List<String> list = Arrays.asList(people);
			
			System.out.println("PeopleService: getPeopleByLocation returning: "+list);
			return list;
		}


		
		return Collections.emptyList();
	}

}
