package com.example.httpnode.service;

import com.example.httpnode.dto.*;
import com.example.httpnode.exception.ConflictException;
import com.example.httpnode.exception.InvalidFieldException;
import com.example.httpnode.exception.UnauthorizedException;
import com.example.httpnode.other.CustomPrinter;
import com.example.httpnode.repository.IDMRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class IDMService {
    private IDMRepository idmRepository;
    private ParameterValidateService parameterValidateService;
    private TokenService tokenService;
    private static final HashMap<String, UserDto> listOfUsers = new HashMap<>();
    private static final HashMap<String, UserCode> listOfCode = new HashMap<>();
    private static final HashMap<String, ResetPasswordDto> listOfCodePassword = new HashMap<>();

    public void saveUser(UserDto userDto) throws InvalidFieldException, ConflictException {
        if (!parameterValidateService.usernameRegex(userDto.getUsername()))
            throw new InvalidFieldException("Username-ul poate conține doar caractere a-z A-Z, cifre, punct și cratimă. Dimensiunea maximă este de 100 de caractere, iar cea minima de cinci.", "The username can only contain characters a-z A-Z, digits, dot, and dash. The maximum size is 100 characters, and the minimum is five.");
        if (!parameterValidateService.emailValidate(userDto.getEmail()))
            throw new InvalidFieldException("Adresă de email invalidă!", "Invalid email!");
        if (!parameterValidateService.passwordValidate(userDto.getPassword()))
            throw new InvalidFieldException("Parola poate conține doar caractere a-z A-Z, cifre, punct și cratimă. Dimensiunea maximă este de 100 de caractere, iar cea minima de cinci.", "The password can only contain characters a-z A-Z, digits, dot, and dash. The maximum size is 100 characters, and the minimum is five.");
        if (!parameterValidateService.name(userDto.getFirstName()) || !parameterValidateService.name(userDto.getLastName()))
            throw new InvalidFieldException("Numele poate conține doar litere. Dimensiunea maximă este de 100 de caractere, iar cea minima de cinci.", "The name can only contain letters. The maximum size is 100 characters, and the minimum is five.");

        userDto.setPassword(this.passwordEncoder().encode(userDto.getPassword()));
        Optional<UserDto> userDtoInDb = this.idmRepository.findByEmail(userDto.getEmail());
        if (userDtoInDb.isPresent()) {
            throw new ConflictException("Există deja un utilizator cu acest email", "Already exists a user with this email.");
        }
        userDtoInDb = this.idmRepository.findByUsername(userDto.getUsername());
        if (userDtoInDb.isPresent()) {
            throw new ConflictException("Există deja un utilizator cu acest nume de utilizator", "Already exists a user with this username.");
        }

        listOfUsers.put(userDto.getEmail(), userDto);
        int code = this.getCode();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime newDateTime = currentDateTime.plusMinutes(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNewDateTime = newDateTime.format(formatter);
        listOfCode.put(userDto.getEmail(), new UserCode(userDto.getEmail(), code, newDateTime));
        if (Objects.equals(userDto.getLanguage(), "ro")) {
            EmailService.send(userDto.getEmail(), "Confirmare creare cont",
                    "<html lang=\"ro\"><meta charset=\"UTF-8\">" +
                            "<p>Bună ziua,</p>" +
                            "<p>Vă mulțumim că sunteți interesat de platforma noastră.</p>" +
                            "<p>Cod de confirmare este:<strong>&nbsp;&nbsp;&nbsp;" + code + "&nbsp;&nbsp;&nbsp;<strong></p>" +
                            "<p>Vă rugăm să introduceți acest cod în pagina de confirmare pentru a finaliza procesul de înregistrare.</p>" +
                            "<p> Codul poate fi folosit până la: " + formattedNewDateTime + "</p>" +
                            "<p>Vă mulțumim pentru înțelegere!</p>" +
                            "<p>O zi frumoasă!</p>", userDto.getLanguage());
        } else {
            EmailService.send(userDto.getEmail(), "Account Creation Confirmation",
                    "<p>Dear User,</p>" +
                            "<p>Thank you for registering on our platform. To confirm your account, please enter the following confirmation code:</p>" +
                            "<p>Confirmation Code:<strong>&nbsp;&nbsp;&nbsp;" + code + "&nbsp;&nbsp;<strong></p>" +
                            "<p>Please enter this code on the confirmation page to complete the registration process.</p>" +
                            "<p>The code can be used until: " + formattedNewDateTime + "</p>" +
                            "<p>Thank you for your understanding!</p>" +
                            "<p>Have a great day!</p>", userDto.getLanguage());
        }
    }

    public UserDto saveUserToDatabase(UserCode userCode) throws InvalidFieldException, ConflictException, UnauthorizedException {
        try {

            if (Objects.equals(listOfUsers.get(userCode.getEmail()).getEmail(), userCode.getEmail()) && userCode.getEmail() != null && listOfCode.get(userCode.getEmail()) != null) {
                if (userCode.getCode() == listOfCode.get(userCode.getEmail()).getCode()) {
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    if (currentDateTime.isAfter(listOfCode.get(userCode.getEmail()).getTime())) {
                        throw new UnauthorizedException("Codul este invalid, acesta a expirat!", "The code is invalid, it has expired!");
                    }
                    listOfCode.remove(userCode.getEmail());
                    return this.idmRepository.save(listOfUsers.get(userCode.getEmail()));
                } else {
                    throw new UnauthorizedException("Codul este invalid!", "The code is invalid!");
                }
            } else {
                throw new UnauthorizedException("Nu există nici un cod pentru acest email, ați folosit deja codul sau ați intodus un cod invalid.",
                        "T\"There is no code for this email, you have already used the code, or you have entered an invalid code.\"");

            }

        } catch (Exception ex) {
            if (ex.toString().contains("UK_USERNAME"))
                throw new ConflictException("Există deja un utilizator cu acest nume de utilizator", "Already exists a user with this username.");
            if (ex.toString().contains("UK_EMAIL"))
                throw new ConflictException("Există deja un utilizator cu acest email", "Already exists a user with this email.");
            throw ex;
        }
    }

    public ShortUser findUserById(String id) throws InvalidFieldException {
        if (!parameterValidateService.isValidUUID(id)) {
            throw new InvalidFieldException("Id-ul nu este un identificator valid", "The ID is not a valid identifier.");
        }
        Optional<UserDto> user = this.idmRepository.findById(id);
        return user.map(userDto -> new ShortUser(userDto.getId(), userDto.getUsername(), userDto.getFirstName(), userDto.getLastName(), userDto.getDescription())).orElse(null);
    }

    public UserDto findUserByIdFull(String id) throws InvalidFieldException {
        if (!parameterValidateService.isValidUUID(id)) {
            throw new InvalidFieldException("Id-ul nu este un identificator valid", "The ID is not a valid identifier.");
        }
        Optional<UserDto> user = this.idmRepository.findById(id);
        return user.orElse(null);
    }


    @Bean
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public UserIdentityDto login(LoginDto loginDto) throws Exception {
        if (!parameterValidateService.usernameRegex(loginDto.getIdentity()) && !parameterValidateService.emailValidate(loginDto.getIdentity()))
            throw new InvalidFieldException("Câmpul folosit pentru identitate nu respectă formatul nici pentru email nici pentru nume de utilizator!", "The field used for identity does not adhere to the format for either email or username!");
        Optional<UserDto> userEmail = this.idmRepository.findByEmail(loginDto.getIdentity());
        if (userEmail.isPresent()) {
            boolean isValidPassword = this.passwordEncoder().matches(loginDto.getPassword(), userEmail.get().getPassword());
            if (!isValidPassword) {
                throw new UnauthorizedException("Parolă invalidă", "Invalid password!");
            } else {
                try {
                    return new UserIdentityDto(userEmail.get().getId(), userEmail.get().getUsername(), userEmail.get().getFirstName(), userEmail.get().getLastName(), userEmail.get().getDescription(), tokenService.createToken(userEmail.get().getId(), String.valueOf(userEmail.get().getRole())), userEmail.get().getRole().toString());
                } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    throw new Exception(noSuchAlgorithmException.toString());
                }
            }
        } else {
            Optional<UserDto> userUsername = this.idmRepository.findByUsername(loginDto.getIdentity());
            if (userUsername.isPresent()) {
                boolean isValidPassword = this.passwordEncoder().matches(loginDto.getPassword(), userUsername.get().getPassword());
                if (!isValidPassword) {
                    throw new UnauthorizedException("Parolă invalidă", "Invalid password!");
                } else {
                    try {
                        return new UserIdentityDto(userUsername.get().getId(), userUsername.get().getUsername(), userUsername.get().getFirstName(), userUsername.get().getLastName(), userUsername.get().getDescription(), tokenService.createToken(userUsername.get().getId(), String.valueOf(userUsername.get().getRole())), userUsername.get().getRole().toString());
                    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                        throw new Exception(noSuchAlgorithmException.toString());
                    }
                }
            } else {
                throw new UnauthorizedException("Nu există nici un utilizator cu acest email sau nume de utilizator", "There is no user with this email or username");
            }
        }
    }

    public int getCode() {
        Random random = new Random();
        return random.nextInt(900000) + 100000;
    }

    public void sendCoeResetPassword(UsernameOrEmail usernameOrEmail) throws UnauthorizedException {
        int code = getCode();
        Optional<UserDto> userDto1 = this.idmRepository.findByUsername(usernameOrEmail.getField());
        Optional<UserDto> userDto2 = this.idmRepository.findByEmail(usernameOrEmail.getField());
        String email;
        if (userDto1.isPresent()) {
            email = userDto1.get().getEmail();

        } else if (userDto2.isPresent()) {
            email = userDto2.get().getEmail();
        } else {
            throw new UnauthorizedException("Nu există nici un utilizator cu acest email sau nume de utilizator", "There is no user with this email or username");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();

        LocalDateTime newDateTime = currentDateTime.plusMinutes(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNewDateTime = newDateTime.format(formatter);
        listOfCodePassword.put(email, new ResetPasswordDto(email, code, newDateTime));
        if (Objects.equals(usernameOrEmail.getLanguage(), "ro")) {
            EmailService.send(email, "Resetare parolă",
                    "<html lang=\"ro\"><meta charset=\"UTF-8\">" +
                            "<p>Bună ziua,</p>" +
                            "<p>Cod de resetare a parolei este:<strong>&nbsp;&nbsp;&nbsp;" + code + "&nbsp;&nbsp;&nbsp;<strong></p>" +
                            "<p>Vă rugăm să introduceți acest cod în pagina de resetare pentru a finaliza procesul.</p>" +
                            "<p> Codul poate fi folosit până la: " + formattedNewDateTime + "</p>" +
                            "<p>Vă mulțumim!</p>" +
                            "<p>O zi frumoasă!</p>", usernameOrEmail.getLanguage());
        } else {
            EmailService.send(email, "Password Reset",
                    "<html lang=\"ro\"><meta charset=\"UTF-8\">" +
                            "<p>Hello,</p>" +
                            "<p>The password reset code is:<strong>&nbsp;&nbsp;&nbsp;" + code + "&nbsp;&nbsp;&nbsp;<strong></p>" +
                            "<p>Please enter this code on the reset page to complete the process.</p>" +
                            "<p>The code can be used until: " + formattedNewDateTime + "</p>" +
                            "<p>Thank you!</p>" +
                            "<p>Have a great day!</p>", usernameOrEmail.getLanguage());
        }
    }

    public String userCheckCode(UserCode userCode) throws NoSuchAlgorithmException, UnauthorizedException {
        if (listOfCodePassword.get(userCode.getEmail()) != null) {
            Optional<UserDto> userDto = this.idmRepository.findByEmail(userCode.getEmail());
            if (userDto.isPresent()) {
                LocalDateTime currentDateTime = LocalDateTime.now();
                if ((listOfCodePassword.get(userCode.getEmail()).getCode() == userCode.getCode()) &&
                        !currentDateTime.isAfter(listOfCodePassword.get(userCode.getEmail()).getTime())) {
                    listOfCodePassword.remove(userCode.getEmail());
                    return tokenService.createToken(userDto.get().getId(), String.valueOf(userDto.get().getRole()));
                } else {
                    throw new UnauthorizedException("Codul este invalid sau acesta a expirat!", "The code is invalid or it has expired!");
                }
            } else {

                throw new UnauthorizedException("Nu există nici un utilizator cu acest email.", "There is no user with this email.");
            }
        } else {
            throw new UnauthorizedException("Nu există nici un cod pentru acest email sau ati folosit deja codul.",
                    "There is no code for this email or you have already used the code.");
        }
    }

    public UserIdentityDto changePassword(String userId, String password) throws UnauthorizedException, NoSuchAlgorithmException, InvalidFieldException {
        if (!parameterValidateService.passwordValidate(password))
            throw new InvalidFieldException("Parola poate conține doar caractere a-z A-Z, cifre, punct și cratimă. Dimensiunea maximă este de 100 de caractere, iar cea minima de cinci.", "The password can only contain characters a-z A-Z, digits, dot, and dash. The maximum size is 100 characters, and the minimum is five.");
        Optional<UserDto> user = this.idmRepository.findById(userId);
        if (user.isPresent()) {
            this.idmRepository.updatePasswordById(userId, this.passwordEncoder().encode(password));
            return new UserIdentityDto(user.get().getId(), user.get().getUsername(), user.get().getFirstName(), user.get().getLastName(), user.get().getDescription(), tokenService.createToken(user.get().getId(), String.valueOf(user.get().getRole())), user.get().getRole().toString());
        } else {
            CustomPrinter.printErr("Nu exista utilizatorul");
            throw new UnauthorizedException("Nu există utilizatorul.", "The user does not exist.");
        }
    }


    public void updateUser(ShortUser shortUser) throws InvalidFieldException, ConflictException {
        if (!parameterValidateService.usernameRegex(shortUser.getUsername()))
            throw new InvalidFieldException("Username-ul poate conține doar caractere a-z A-Z, cifre, punct și cratimă. Dimensiunea maximă este de 100 de caractere, iar cea minima de cinci.", "The username can only contain characters a-z A-Z, digits, dot, and dash. The maximum size is 100 characters, and the minimum is five.");
        if (!parameterValidateService.name(shortUser.getFirstName()) && ! parameterValidateService.name(shortUser.getLastName()))
            throw new InvalidFieldException("Numele poate conține doar litere. Dimensiunea maximă este de 100 de caractere, iar cea minima de cinci.", "The name can only contain letters. The maximum size is 100 characters, and the minimum is five.");
        Optional<UserDto>  userDtoInDb = this.idmRepository.findByUsername(shortUser.getUsername());
        if (userDtoInDb.isPresent() && !Objects.equals(userDtoInDb.get().getId(), shortUser.getId())) {
            throw new ConflictException("Există deja un utilizator cu acest nume de utilizator", "Already exists a user with this username.");
        }
        this.idmRepository.updateUserInfo(shortUser.getId(),shortUser.getFirstName(),shortUser.getLastName(),shortUser.getUsername(),shortUser.getDescription());
    }

    public Optional<UserDto> findByEmail(String email){
        return this.idmRepository.findByEmail(email);
    }
}
