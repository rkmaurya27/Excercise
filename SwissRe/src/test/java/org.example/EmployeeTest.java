package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import static org.example.Complete.readGivenCSV;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeTest {

    private Complete.Company company;

    @BeforeEach
    public void setUp() {
        company = new Complete.Company();
    }

    @Test
    public void testReadCSV() throws IOException {
        company = readGivenCSV("employee.csv");
        assertNotNull(company.employees);
    }

    @Test
    public void testAnalyzeSalaries() throws IOException {
        company = readGivenCSV("employee.csv");
        assertEquals(45000, company.employees.get(124).salary());
    }

    @Test
    public void testAnalyzeReportingLines() throws IOException {
        company = readGivenCSV("employee.csv");
        assertEquals(4,company.getReportingLineLength(306));
    }
}
