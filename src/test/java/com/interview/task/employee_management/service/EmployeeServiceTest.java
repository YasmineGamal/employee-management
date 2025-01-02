package com.interview.task.employee_management.service;

import com.interview.task.employee_management.model.Department;
import com.interview.task.employee_management.model.Employee;
import com.interview.task.employee_management.repository.DepartmentRepository;
import com.interview.task.employee_management.repository.EmployeeRepository;
import com.interview.task.employee_management.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    //@Mock
    // private ExternalApiService externalApiService;  // Service for third-party API validation (e.g., email validation)

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Department department;

    @Test
    void testCreateEmployee_existingDept() {
        department = new Department(1l,"IT");
        employee = new Employee(1l,"Yasmine", "Saad", "yasmine.saad@gmail.com", 100.0, department);
        when(departmentRepository.findByName(anyString())).thenReturn(department); // Existing department
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee createdEmployee = employeeService.createEmployee(employee);

        // Assert
        assertNotNull(createdEmployee);
        assertEquals("yasmine.saad@gmail.com", createdEmployee.getEmail());
        assertEquals("IT", createdEmployee.getDepartment().getName());
        verify(employeeRepository, times(1)).save(employee); // Verify that save was called
    }

    @Test
    void testCreateEmployee_newDepartment() {
        // Arrange
        department = new Department(1l,"IT");
        employee = new Employee(1l,"Yasmine", "Saad", "yasmine.saad@gmail.com", 100.0, department);
        when(departmentRepository.findByName(anyString())).thenReturn(null); // No existing department
        when(departmentRepository.save(any(Department.class))).thenReturn(department); // Save new department
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee); // Successful save

        // Act
        Employee createdEmployee = employeeService.createEmployee(employee);

        // Assert
        assertNotNull(createdEmployee);
        assertEquals("yasmine.saad@gmail.com", createdEmployee.getEmail());
        assertEquals("IT", createdEmployee.getDepartment().getName());
        verify(departmentRepository, times(1)).save(department); // Verify department save
        verify(employeeRepository, times(1)).save(employee); // Verify employee save
    }

    @Test
    void testGetEmployeeById() {
        Employee employee = new Employee(1L, "Yasmine", "Saad",
                "Yasmine.Saad@gmail.com", 100, new Department("IT"));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Yasmine", result.getFirstName());
    }

    @Test
    void testUpdateEmployee() {
        Employee employee = new Employee(1L, "Yasmine", "Saad",
                "Yasmine.Saad@gmail.com", 10, new Department("IT"));
        Employee updatedEmployee = new Employee(1L, "Yasmine", "Saad",
                "Yasmine.Saad@gmail.com", 100, new Department("HR"));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        Employee result = employeeService.updateEmployee(1L, updatedEmployee);

        assertNotNull(result);
        assertEquals("Yasmine Saad", result.getFirstName().concat(" ").concat(result.getLastName()));
    }

    @Test
    void testDeleteEmployee() {
        Long employeeId = 1L;
        doNothing().when(employeeRepository).deleteById(employeeId);

        employeeService.deleteEmployee(employeeId);

        verify(employeeRepository, times(1)).deleteById(employeeId);
    }

    @Test
    void testListAllEmployees() {
        List<Employee> employees = Collections.singletonList(new Employee("Yasmine", "Saad", "Yasmine.Saad@gmail.com", 10, new Department("IT")));
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
