package com.smart.garage.controllers.mvc;

import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.models.ResetPasswordTokens;
import com.smart.garage.models.User;
import com.smart.garage.models.dtos.UserDTOOut;
import com.smart.garage.models.dtos.UserPasswordDTO;
import com.smart.garage.services.contracts.ResetPasswordTokensService;
import com.smart.garage.services.contracts.UserService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.smart.garage.utility.AuthenticationHelper.UNAUTHORISED_LOGGED;

@Controller
@RequestMapping("")
public class AuthenticationMVC {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final ResetPasswordTokensService resetPasswordTokensService;
    private final UserMapper userMapper;

    @Autowired
    public AuthenticationMVC(AuthenticationHelper authenticationHelper,
                             UserService userService,
                             ResetPasswordTokensService resetPasswordTokensService,
                             UserMapper userMapper) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.resetPasswordTokensService = resetPasswordTokensService;
        this.userMapper = userMapper;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (authenticationHelper.isAuthenticated()) {
            model.addAttribute("errorMessage", UNAUTHORISED_LOGGED);
            return "unauthorised";
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(Model model) {
        if (!authenticationHelper.isAuthenticated()) {
            model.addAttribute("errorMessage", UNAUTHORISED_LOGGED);
            return "unauthorised";
        }

        return "redirect:/";
    }

    @GetMapping("/forgotten-password")
    public String showForgottenPasswordPage(Model model) {
        if (authenticationHelper.isAuthenticated()) {
            model.addAttribute("errorMessage", UNAUTHORISED_LOGGED);
            return "unauthorised";
        }

        model.addAttribute("isEmailValid", true);
        model.addAttribute("errorMessage", "");

        return "request-password";
    }

    @PostMapping("/forgotten-password")
    public String generateUpdatePasswordEmail(@RequestParam(name = "email", required = false) String email, Model model) {
        try {
            User user = userService.getByField("email", email);

            if (user.isDeleted()) throw new EntityNotFoundException("User has been deleted");

            userService.generateResetPasswordEmail(user);

            return "request-password-success";
        } catch (EntityNotFoundException | DuplicateEntityException e) {
            model.addAttribute("isEmailValid", false);
            model.addAttribute("errorMessage", e.getMessage());
            return "request-password";
        }
    }

    @GetMapping("reset-password")
    public String showResetPasswordPage(@RequestParam String token, Model model) {
        if (authenticationHelper.isAuthenticated()) {
            model.addAttribute("errorMessage", UNAUTHORISED_LOGGED);
            return "unauthorised";
        }

        try {
            userService.confirmToken(token);

            ResetPasswordTokens resetToken = resetPasswordTokensService.findByToken(token);

            model.addAttribute("token", token);
            model.addAttribute("user", userMapper.toDTOOut(resetToken.getUser()));
            model.addAttribute("passwordDTO", new UserPasswordDTO());
            return "reset-password";
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }

    }

    @PostMapping("reset-password")
    public String showResetPasswordPage(@RequestParam String token,
                                        @Valid @ModelAttribute("passwordDTO") UserPasswordDTO passwordDTO,
                                        BindingResult errors,
                                        @ModelAttribute("user") UserDTOOut userDTOOut,
                                        Model model) {
        if (authenticationHelper.isAuthenticated()) {
            model.addAttribute("errorMessage", UNAUTHORISED_LOGGED);
            return "unauthorised";
        }

        model.addAttribute("token", token);

        if (errors.hasErrors()) {
            return "reset-password";
        }

        if (!passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())) {
            errors.rejectValue("password", "passwords_do_not_match", "Your passwords did not match.");
            return "reset-password";
        }

        try {
            ResetPasswordTokens resetToken = resetPasswordTokensService.findByToken(token);

            userService.updatePassword(resetToken.getUser(), resetToken.getUser().getId(), passwordDTO.getPassword());
            return "reset-password-success";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }

    }


}
