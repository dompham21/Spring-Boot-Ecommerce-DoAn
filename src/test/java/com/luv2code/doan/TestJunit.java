package com.luv2code.doan;

import org.junit.Before;
import org.junit.Test;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertLinkPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.assertTitleEquals;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLink;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestJunit {


    private MockMvc mockWvc;
    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockWvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void requiresAuthentication() throws Exception {
        mockWvc
                .perform(get("/"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void authenticationFailed() throws Exception {
        mockWvc
                .perform(formLogin()
                        .user("email","dompham300721@gmail.com")
                        .password("123456"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    public void authenticationSuccess() throws Exception {
        mockWvc
                .perform(formLogin()
                        .user("email","dompham300721@gmail.com")
                        .password("123456789"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated());
    }

    @Test
    public void authenticationWithEmailNotVerify() throws Exception {
        mockWvc
                .perform(formLogin()
                        .user("email","n19dccn019@student.ptithcm.edu.vn")
                        .password("123456789"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }
    @Test
    public void authenticationWithPasswordBlank() throws Exception {
        mockWvc
                .perform(formLogin()
                        .user("email","n19dccn019@student.ptithcm.edu.vn")
                        .password("            "))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

}
