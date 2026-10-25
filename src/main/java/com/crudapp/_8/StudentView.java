package com.crudapp._8;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class StudentView extends VerticalLayout {

    private final StudentRepository repository;

    private final Grid<Student> grid = new Grid<>(Student.class, false);

    private final TextField nameField = new TextField("Student Name");
    private final TextField emailField = new TextField("Email Address");

    private Student selectedStudent;

    public StudentView(StudentRepository repository) {
        this.repository = repository;

        add(new H2("Student Management System"));

        grid.addColumn(Student::getId).setHeader("ID");
        grid.addColumn(Student::getName).setHeader("Name");
        grid.addColumn(Student::getEmail).setHeader("Email Address");

        grid.setItems(repository.findAll());
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedStudent = event.getValue();

            if (selectedStudent != null) {
                nameField.setValue(selectedStudent.getName());
                emailField.setValue(selectedStudent.getEmail());
            } else {
                clearFields();
            }
        });

        Button addButton = new Button("Add Student", event -> addStudent());
        Button updateButton = new Button("Update Student", event -> updateStudent());
        Button deleteButton = new Button("Delete Student", event -> deleteStudent());
        Button clearButton = new Button("Clear", event -> clearFields());

        HorizontalLayout form = new HorizontalLayout(nameField, emailField);
        HorizontalLayout buttons = new HorizontalLayout(
                addButton, updateButton, deleteButton, clearButton
        );

        add(form, buttons, grid);

        setSizeFull();
        grid.setWidthFull();
    }

    private void addStudent() {
        if (nameField.isEmpty() || emailField.isEmpty()) {
            Notification.show("Please enter the student's name and email address.");
            return;
        }

        repository.save(new Student(
                nameField.getValue(),
                emailField.getValue()
        ));

        refreshGrid();
        clearFields();

        Notification.show("Student added successfully!!");
    }

    private void updateStudent() {
        if (selectedStudent == null) {
            Notification.show("Please select a student to update.");
            return;
        }

        if (nameField.isEmpty() || emailField.isEmpty()) {
            Notification.show("Please enter the student's name and email address.");
            return;
        }

        selectedStudent.setName(nameField.getValue());
        selectedStudent.setEmail(emailField.getValue());

        repository.save(selectedStudent);

        refreshGrid();
        clearFields();

        Notification.show("Student updated successfully!");
    }

    private void deleteStudent() {
        if (selectedStudent == null) {
            Notification.show("Please select a student to delete.");
            return;
        }

        repository.delete(selectedStudent);

        refreshGrid();
        clearFields();

        Notification.show("Student deleted successfully!!");
    }

    private void refreshGrid() {
        grid.setItems(repository.findAll());
    }

    private void clearFields() {
        selectedStudent = null;
        nameField.clear();
        emailField.clear();
        grid.deselectAll();
    }
}
