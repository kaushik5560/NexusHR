package com.zidio.nexushr.service;

import com.zidio.nexushr.dto.JwtResponse;
import com.zidio.nexushr.dto.LoginRequest;
import com.zidio.nexushr.dto.MessageResponse;
import com.zidio.nexushr.dto.SignupRequest;
import com.zidio.nexushr.entity.Employee;
import com.zidio.nexushr.entity.EmployeeStatus;
import com.zidio.nexushr.entity.Role;
import com.zidio.nexushr.entity.RoleType;
import com.zidio.nexushr.repository.EmployeeRepository;
import com.zidio.nexushr.repository.RoleRepository;
import com.zidio.nexushr.security.JwtUtils;
import com.zidio.nexushr.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    // 1. Employee Registration Logic (Signup)
    @Transactional
    public MessageResponse registerEmployee(SignupRequest signUpRequest) {
        if (employeeRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        // Auto-generate employee code: e.g. EMP-1001, EMP-1002
        long count = employeeRepository.count();
        String employeeCode = String.format("EMP-%04d", count + 1001);

        // Resolve Roles
        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role employeeRole = roleRepository.findByName(RoleType.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new RuntimeException("Error: Role ROLE_EMPLOYEE not found."));
            roles.add(employeeRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_ADMIN not found."));
                        roles.add(adminRole);
                        break;
                    case "HR":
                    case "ROLE_HR":
                        Role hrRole = roleRepository.findByName(RoleType.ROLE_HR)
                                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_HR not found."));
                        roles.add(hrRole);
                        break;
                    case "MANAGER":
                    case "ROLE_MANAGER":
                        Role managerRole = roleRepository.findByName(RoleType.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_MANAGER not found."));
                        roles.add(managerRole);
                        break;
                    default:
                        Role defaultRole = roleRepository.findByName(RoleType.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_EMPLOYEE not found."));
                        roles.add(defaultRole);
                }
            });
        }

        // Build Employee Entity with Argon2 Hashed Password
        Employee employee = Employee.builder()
                .employeeCode(employeeCode)
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .phoneNumber(signUpRequest.getPhoneNumber())
                .department(signUpRequest.getDepartment() != null ? signUpRequest.getDepartment() : "General")
                .designation(signUpRequest.getDesignation() != null ? signUpRequest.getDesignation() : "Associate")
                .salary(signUpRequest.getSalary())
                .status(EmployeeStatus.ACTIVE)
                .hireDate(LocalDate.now())
                .roles(roles)
                .build();

        employeeRepository.save(employee);
        return new MessageResponse("Employee registered successfully with Code: " + employeeCode);
    }

    // 2. Authentication Logic (Login & JWT Generation)
    public JwtResponse authenticateEmployee(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Employee employee = employeeRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: Employee details not found."));

        return JwtResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .roles(roles)
                .build();
    }
}