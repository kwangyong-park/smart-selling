package com.pky.smartselling.controller.api;

import com.pky.smartselling.configuration.security.JwtTokenProvider;
import com.pky.smartselling.controller.api.dto.RegisterDto;
import com.pky.smartselling.controller.api.dto.SignInDto;
import com.pky.smartselling.domain.employee.Employee;
import com.pky.smartselling.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api/v1/employee/")
@RestController
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public RegisterDto register(@RequestBody @Valid RegisterDto.Request request) {
        final Employee copyEmployee = new Employee();
        BeanUtils.copyProperties(request,  copyEmployee);

        final Employee savedEmployee = employeeService.register(copyEmployee);
        return new RegisterDto.Response();
    }

    @PostMapping("/signIn")
    public ResponseEntity signIn(@RequestBody @Valid SignInDto.Request request) {
        try {
            String username = request.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));

            String token = jwtTokenProvider.createToken(username);
            return ResponseEntity.ok(new SignInDto.Response("Bearer "+ token));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

}