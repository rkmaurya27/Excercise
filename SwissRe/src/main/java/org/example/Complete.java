package org.example;

import java.io.*;
import java.util.*;

public class Complete {

    public static String DELIMITER = ",";
    /*
     * Nested static class as Company which will work supporter class
     * */
    static class Company {
        Map<Integer, Employee> employees = new HashMap<>();
        Map<Integer, List<Employee>> managersList = new HashMap<>();
        Employee employeeCEO;

        void makeEmployeeList(Employee employee) {
            employees.put(employee.id(), employee);
            if (employee.managerId() != null) {
                managersList.computeIfAbsent(employee.managerId(), l -> new ArrayList<>()).add(employee);
            } else {
                //Since CEO does not have manager
                employeeCEO = employee;
            }
        }
        /*
         * Auditing salary related anomalies & reporting lines
         * */
        void auditReports() {
            List<String> salaryCase = new ArrayList<>();
            List<String> longerReportingLinesCase = new ArrayList<>();

            // Check salary anomalies for each manager
            for (Map.Entry<Integer, Employee> entry : employees.entrySet()) {
                Employee managerEmp = entry.getValue();
                // Ignore CEO since he/she has no manager
                if (managerEmp.managerId() != null) {
                    double meanSalary = directReporterSalary(managerEmp.id());
                    if (meanSalary > 0) {
                        //Board wants to make sure that every manager earns
                        //at least 20% more than the average salary of its direct subordinates but no more than 50% more
                        //than that average
                        double minSalary = meanSalary * 1.2; //at least 20% which means 1+(20/100)
                        double maxSalary = meanSalary * 1.5; //at most 50% which means 1+(50/100)
                        if (managerEmp.salary() < minSalary) {
                            salaryCase.add(String.format("Manager %s %s earns less than the average salary by %.2f",
                                    managerEmp.firstName(), managerEmp.lastName(), minSalary - managerEmp.salary()));
                        } else if (managerEmp.salary() > maxSalary) {
                            salaryCase.add(String.format("Manager %s %s earns more than the average salary by %.2f",
                                    managerEmp.firstName(), managerEmp.lastName(), managerEmp.salary() - maxSalary));
                        }
                    }
                }
            }

            // Check reporting line lengths related anomalies
            for (Map.Entry<Integer, Employee> entry : employees.entrySet()) {
                Employee employee = entry.getValue();
                int lineLength = getReportingLineLength(employee.id());
                if (lineLength > 4) {
                    longerReportingLinesCase.add(String.format("Employee/Subordinate %s %s has a reporting line that is too long by %d",
                            employee.firstName(), employee.lastName(), lineLength - 4));
                }
            }
            System.out.println("Managers with salary related concerns -> ");
            for (String salaryIssue : salaryCase) {
                System.out.println(salaryIssue);
            }
            System.out.println("\nEmployees with long reporting lines -> ");
            for (String lineIssue : longerReportingLinesCase) {
                System.out.println(lineIssue);
            }
        }
        /*
         * This method will fetch us direct reporter's salary
         * */
        double directReporterSalary(int managerId) {
            List<Employee> reporterList = managersList.get(managerId);
            if (reporterList == null || reporterList.isEmpty()) return 0;
            double totalSalary = 0;
            for (Employee reporter : reporterList) {
                totalSalary += reporter.salary();
            }
            return totalSalary / reporterList.size();
        }
        /*
         * Find the number of steps in the reporting line
         * */
        int getReportingLineLength(int employeeId) {
            int steps = 0;
            Employee current = employees.get(employeeId);
            while (current != null && current.managerId() != null) {
                current = employees.get(current.managerId());
                steps++;
            }
            return steps;
        }
    }

    /*
     * This method read the CSV file and populate the data
     * */
    public static Company readGivenCSV(String filePath) throws IOException {
        Company company = new Company();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(DELIMITER);
                int id = Integer.parseInt(fields[0]);
                String firstName = fields[1];
                String lastName = fields[2];
                double salary = Double.parseDouble(fields[3]);
                //Integer managerId = fields[4].isEmpty() ? null : Integer.parseInt(fields[4]);
                try {
                    if (!fields[4].isEmpty()) {
                        Integer managerId = fields[4].isEmpty() ? null : Integer.parseInt(fields[4]);
                        company.makeEmployeeList(new Employee(id, firstName, lastName, salary, managerId));
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return company;
    }

    public static void main(String[] args) {
        try {
            // Read the CSV and read the data
            String csvFilePath = "employee.csv";
            Company company = readGivenCSV(csvFilePath);
            company.auditReports();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

