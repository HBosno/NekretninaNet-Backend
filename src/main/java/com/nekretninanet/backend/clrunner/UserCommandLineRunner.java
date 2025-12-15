package com.nekretninanet.backend.clrunner;

import com.nekretninanet.backend.dto.CreateSupportUserRequest;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.model.UserType;
import com.nekretninanet.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class UserCommandLineRunner implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        /*
        CreateSupportUserRequest user = new CreateSupportUserRequest("Test Name",
                "Test lastName",
                "test",
                "test",
                "test",
                "mailtest",
                "123-456-789"
                );

        userService.createSupportUser(user);
        //userService.createRegularUser(user);
*/
        User user = new User("Test Name",
                "Test lastName",
                "test",
                "test",
                "test",
                "mailtest",
                "123-456-789",
                UserType.USER
        );

        userService.createRegularUser(user);
        System.out.println("------------------------------------------------");
     //   System.out.println(userService.getAllSupportUsers());
        System.out.println("------------------------------------------------");

    }

}
