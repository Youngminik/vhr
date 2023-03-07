package com.example.vhr.controller.salary;

import com.example.vhr.model.Employee;
import com.example.vhr.model.RespBean;
import com.example.vhr.model.RespPageBean;
import com.example.vhr.model.Salary;
import com.example.vhr.service.EmployeeService;
import com.example.vhr.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary/sobcfg")
public class SobConfigController {
    @Autowired
    EmployeeService employeeService;
    @Autowired
    SalaryService salaryService;

    @GetMapping("/")
    public RespPageBean getEmployeeByPageWithSalary(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return employeeService.getEmployeeByPageWithSalary(page, size);
    }

    @GetMapping("/salaries")
    public List<Salary> getAllSalaries() {
        return salaryService.getAllSalaries();
    }

    @PutMapping("/")
    public RespBean updateEmployeeSalaryById(Integer eid, Integer sid) {
        /*更新sql中的replace是先删除后插入，会有两次操作，所以插入或更新等于1或等于2都可以*/
        Integer result = employeeService.updateEmployeeSalaryById(eid, sid);
        if (result == 1||result==2) {
            return RespBean.ok("更新成功！");
        }
        return RespBean.error("更新失败！");
    }
}
