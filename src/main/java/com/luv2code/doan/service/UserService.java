package com.luv2code.doan.service;

import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class UserService {
    public static final int USER_PER_PAGE = 9;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private org.thymeleaf.spring5.SpringTemplateEngine templateEngine;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public User saveUser(User user) {
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        user.setIsActive(false);
        user.setRegistrationDate(new Date());

        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);
        return userRepository.save(user);
    }

    public User saveEditUser(User user) {
        return userRepository.save(user);
    }


    public void sendVerificationEmail(User user, String siteUrl)
                throws UnsupportedEncodingException, MessagingException {
       String subject = "Please verify your registration";
       String senderName = "iMusic Team";
       String verifyUrl = siteUrl + "/verify?code=" + user.getVerificationCode();

       Map<String, Object> model = new HashMap<String, Object>();
       model.put("verifyUrl", verifyUrl);
       Context context = new Context();
       context.setVariables(model);

       String html = templateEngine.process("email/verify-code", context);

       MimeMessage message = emailSender.createMimeMessage();
       MimeMessageHelper helper = new MimeMessageHelper(message);

       helper.setFrom(senderEmail, senderName);
       helper.setTo(user.getEmail());
       helper.setSubject(subject);
       helper.setText(html, true);
       emailSender.send(message);

    }

    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.getIsActive()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setIsActive(true);
            userRepository.save(user);

            return true;
        }
    }

    public User getUserByID(int id) throws UserNotFoundException {
        try {
            User user = userRepository.findById(id).get();
            return user;

        }
        catch(NoSuchElementException ex) {
            throw new UserNotFoundException("Could not find any user with ID " + id);

        }
    }

    public User getUserByEmail(String email) {
        User user = userRepository.getUserByEmail(email);
        return user;
    }


    public Page<User> listByPage(Integer pageNum, String keyword, String sortField, String sortDir) {
        Pageable pageable = null;

        if(sortField != null && !sortField.isEmpty()) {
            Sort sort = Sort.by(sortField);
            sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
            pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);
        }
        else {
            pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE);
        }

        if (keyword != null && !keyword.isEmpty()) {
            return userRepository.findAll(keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }
}
