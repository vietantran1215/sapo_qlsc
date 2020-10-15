package com.sapo.qlsc.controller;

import com.sapo.qlsc.dto.MaintenanceCardDTO;
import com.sapo.qlsc.dto.UserDTO;
import com.sapo.qlsc.entity.MaintenanceCard;
import com.sapo.qlsc.entity.User;
import com.sapo.qlsc.exception.CodeExistedException;
import com.sapo.qlsc.exception.commonException.NotFoundException;
import com.sapo.qlsc.exception.userException.DuplicateEmailException;
import com.sapo.qlsc.model.MaintenanceCardFilter;
import com.sapo.qlsc.repository.UserRepository;
import com.sapo.qlsc.repository.UserRepositoryCustom;
import com.sapo.qlsc.service.MaintenanceCardService;
import com.sapo.qlsc.service.UserService;
import com.sapo.qlsc.ulti.PasswordPoJo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {


    private UserService userService;
    private MaintenanceCardService maintenanceCardService;
    private UserRepositoryCustom userRepositoryCustom;

    @Autowired
    public UserController(UserService userService ,MaintenanceCardService maintenanceCardService,UserRepositoryCustom userRepositoryCustom) {
        this.userService = userService;
        this.maintenanceCardService = maintenanceCardService;
        this.userRepositoryCustom = userRepositoryCustom;
    }

    @GetMapping("/admin/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(@RequestParam(name = "pageNum", defaultValue = "1", required = false) int pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize,
                                      @RequestParam(value = "sortBy", defaultValue = "modified_date") String sortBy,
                                      @RequestParam(value = "descending", defaultValue = "desc") String descending,
                                      @RequestParam(value = "param", defaultValue = "") String param) {

        Map<String, Object> allUser = userService.getListUser(pageNum, pageSize, sortBy, descending, param);
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        return new ResponseEntity(allUser, HttpStatus.OK);
    }

    @GetMapping("/admin/users/maintenanceCard")
    public ResponseEntity getTotalMaintenanceCardByRepairman(@RequestParam(defaultValue = "1", required = false) int page,
                                                             @RequestParam(defaultValue = "5", required = false) int size,
                                                             @RequestParam(defaultValue = "", required = false) String key) {
        HashMap<String, Object> allUser = userService.getTotalMaintenanceCardByRepairman(page, size, key);
        return new ResponseEntity(allUser, HttpStatus.OK);
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<?> getInfoUser(@PathVariable("id") Long id) throws NotFoundException {
        UserDTO userDTO = userService.getUserById(id);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping("/admin/users")
    public ResponseEntity<UserDTO> insertUser(@RequestBody UserDTO userDTO)throws DuplicateEmailException, CodeExistedException, ConstraintViolationException {
        return new ResponseEntity<>(userService.insertUser(userDTO), HttpStatus.OK);
        }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable("id") Long id) throws NotFoundException, CodeExistedException {
        userDTO = userService.updateUser(userDTO, id);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/users/delete")
    public ResponseEntity<String> deleteUsers(@RequestParam("listID") List<Long> listID) throws Exception {
        boolean isDelete = userService.deleteUserById(listID);
        if (isDelete) {
            return new ResponseEntity<>("Delete SuccessFully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Delete Failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/admin/users/changePassword")
    public ResponseEntity<UserDTO> ChangePassword(@RequestBody PasswordPoJo passwordPoJo) throws NotFoundException {
        return new ResponseEntity<>(userService.changePassword(passwordPoJo), HttpStatus.OK);
    }


    @GetMapping("/admin/users/maintenanceCards/{userid}")
    public ResponseEntity<Map<String,Object>> getMaintenanceCardByUserId(@RequestParam(name = "pageNum", defaultValue = "1", required = false) int pageNum,
                                                                         @RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize,
                                                                         @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
                                                                         @RequestParam(value = "descending", defaultValue = "false") boolean descending,
                                                                         @RequestParam(value = "code", defaultValue = "") String code,
                                                                         @PathVariable(value = "userid",required = true)Long userid,
                                                                         @RequestParam(value = "payStatus",required = false,defaultValue = "0,1") byte[] payStatus,
                                                                         @RequestParam(value = "workStatus",required = false,defaultValue = "0,1,2") byte[] workStatus
    ) throws NotFoundException{



        Map<String,Object> map =maintenanceCardService.getMaintenanceCardByRepairMan(pageNum,pageSize,sortBy,descending,userid,code,payStatus,workStatus);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
    @GetMapping("/admin/checkUser")
    public ResponseEntity<UserDTO> getInformationUser() throws NotFoundException {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDTO userDTO = userService.checkUserNameUser(authentication.getName());
        return new ResponseEntity<>(userDTO,HttpStatus.OK);
    }
}
