package org.example;
/* Record Classes in Pojo
* https://openjdk.org/jeps/395
* */
public record Employee (
    int id,
    String firstName,
    String lastName,
    double salary,
    Integer managerId){}
