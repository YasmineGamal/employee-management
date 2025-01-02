package com.interview.task.employee_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.task.employee_management.exceptionhandling.EmployeeNotFoundException;
import com.interview.task.employee_management.model.Department;
import com.interview.task.employee_management.model.Employee;
import com.interview.task.employee_management.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void testCreateEmployee() throws Exception {
        Employee employee = new Employee(1L, "Yasmine", "Saad",
                "yasmine.saad@gmail.com", 100.0, new Department("IT"));
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Yasmine"))
                .andExpect(jsonPath("$.email").value("yasmine.saad@gmail.com"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        Employee employee = new Employee(1L, "Yasmine", "Saad",
                "yasmine.saad@gmail.com", 100.0, new Department("IT"));

        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/api/employees/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Yasmine"))
                .andExpect(jsonPath("$.email").value("yasmine.saad@gmail.com"));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        Employee employee = new Employee(1L, "Yasmine", "Saad",
                "yasmine.saad@gmail.com", 100.0, new Department("IT"));

        Employee updatedEmployee = new Employee(1L, "Yasmine", "Saad",
                "yasmine.saad@gmail.com", 10.0, new Department("HR"));

        when(employeeService.updateEmployee(eq(1L), any(Employee.class))).thenReturn(updatedEmployee);

        mockMvc.perform(put("/api/employees/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salary").value(10.0))
                .andExpect(jsonPath("$.department.name").value("HR"));


    }

    @Test
    void testDeleteEmployee() throws Exception {
        Long employeeId = 1L;
        doNothing().when(employeeService).deleteEmployee(employeeId);

        mockMvc.perform(delete("/api/employees/{id}", employeeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testListAllEmployees() throws Exception {
        List<Employee> employees = Collections.singletonList(new Employee
                (1L, "Yasmine", "Saad", "yasmine.saad@gmail.com", 10.0,
                        new Department("IT")));
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Yasmine"))
                .andExpect(jsonPath("$[0].email").value("yasmine.saad@gmail.com"));
    }

    @Test
    void testEmployeeNotFound() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenThrow(new EmployeeNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employees/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }

    @Test
    void testCreateEmployeeWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\", \"email\":\"invalid-email\", \"department\":{\"name\":\"IT\"}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Invalid email format"))
                .andExpect(jsonPath("$.firstName").value("Firstname is mandatory"))
                .andExpect(jsonPath("$.lastName").value("Lastname is mandatory"));


    }
}

