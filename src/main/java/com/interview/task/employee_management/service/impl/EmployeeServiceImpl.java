package com.interview.task.employee_management.service.impl;

import com.interview.task.employee_management.exceptionhandling.DatabaseException;
import com.interview.task.employee_management.exceptionhandling.EmployeeNotFoundException;
import com.interview.task.employee_management.exceptionhandling.InvalidInputException;
import com.interview.task.employee_management.model.Department;
import com.interview.task.employee_management.model.Employee;
import com.interview.task.employee_management.repository.DepartmentRepository;
import com.interview.task.employee_management.repository.EmployeeRepository;
import com.interview.task.employee_management.service.EmailService;
import com.interview.task.employee_management.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmailService emailService;

    @Value("${email.new-emp-template.body}")
    private String emailBody;


    @Value("${email.new-emp-template.subject}")
    private String emailSubject;

    //This method firstly checks on the provided Dept; if exists then, then set the existing
    // to employee dept obj to be maneged entity. else, save dept .
    // secondly, save the employee with making sure the email provided is unique.

    @Override
    public Employee createEmployee(Employee employee) {
        this.checkOnDept(employee);
        // Validate email before saving to the database
        if (!emailService.isValidEmail(employee.getEmail())) {
            throw new InvalidInputException("Invalid email");
        }
        try {
            employee = employeeRepository.save(employee);
        } catch (Exception ex) {
            Employee existingEmployee = employeeRepository.findByEmail(employee.getEmail());
            if (existingEmployee != null)
                throw new InvalidInputException("Failed to save employee: an existing employee with the same email was found. The email must be unique.");
            throw new DatabaseException("Failed to save Employee with email: " + employee.getEmail());
        }
        emailService.sendEmail(employee.getEmail(), emailSubject, emailBody);
        return employee;
    }

    private void checkOnDept(Employee employee) {
        if (employee.getDepartment() != null) {
            // Fetch the existing department from the database
            Department existingDepartment = departmentRepository.findByName(employee.getDepartment().getName());
            if (existingDepartment == null) //new Dept.
                try {
                    //then save it
                    existingDepartment = departmentRepository.save(employee.getDepartment());
                } catch (Exception ex) {
                    throw new DatabaseException("Failed to save Dept: " + existingDepartment.getName());
                }
            // Set department instance to the employee
            employee.setDepartment(existingDepartment);
        }
    }

    @Override
    public Employee getEmployeeById(Long id) {
        Optional<Employee> optional = employeeRepository.findById(id);
        Employee employee = null;
        if (optional.isPresent()) {
            employee = optional.get();
        } else {
            logger.error("Employee not found for id :: ", id);
            throw new EmployeeNotFoundException("Employee with ID " + id + " not found");
        }
        return employee;
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        Employee existingEmployee = getEmployeeById(id);
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.getDepartment().setName(employee.getDepartment().getName());
        existingEmployee.setSalary(employee.getSalary());
        try {
            existingEmployee = employeeRepository.save(existingEmployee);
        } catch (Exception ex) {
            throw new DatabaseException("Failed to Update employee with Id: " + id);
        }
        return existingEmployee;
    }

    @Override
    public void deleteEmployee(Long id) {
        try {
            employeeRepository.deleteById(id);
        } catch (Exception ex) {
            throw new DatabaseException("Failed to delete employee with Id: " + id);
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
