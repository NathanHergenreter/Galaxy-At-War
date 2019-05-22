package com.server.demo;

import com.server.demo.Configuration.MvcConfig;
import com.server.demo.Controller.GameController;
import com.server.demo.Controller.UserController;
import com.server.demo.Entities.User;
import com.server.demo.Model.GameManager;
import com.server.demo.Model.UserManager;
import de.codecentric.boot.admin.server.web.ApplicationsController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

///@RunWith(MockitoJUnitRunner.StrictStubs.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WebAppConfiguration
@ContextConfiguration(classes={MvcConfig.class, SecurityConfig.class})

public class HttpTests {

    @Mock
    private GameManager mockGameManager;

    @Mock
    private UserManager userManager;

    @Mock
    GameController gameController;

    @Mock
    UserController userController;

    @Autowired
    WebApplicationContext context;

    @Autowired
    MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void UserRegTest(){

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        User u = userManager.getUser("TestHost555");
        if (u!=null){
            userManager.deleteUser(u);
        }

        try {
            HttpSessionCsrfTokenRepository rp = new HttpSessionCsrfTokenRepository();
            CsrfToken token = rp.generateToken(new MockHttpServletRequest());
            this.mockMvc.perform(post("/user/register?name=TestHost555&password=Password")
                    .with(csrf())
                    .sessionAttr("csrf", token))
                    .andDo(print())
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        u = userManager.getUser("TestHost555");
        assertThat(u!=null);
        assertThat(u.getName().equals("TestHost555"));
        assertThat(u.getPassword().equals("Password"));

        try {
            this.mockMvc.perform(post("/user/login?name=TestHost555&password=Password"))
                    .andDo(print())
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        u = userManager.getUser("TestHost555");
        assertThat(u==null);

        userController.unRegisterUser(null, u);
    }

}
