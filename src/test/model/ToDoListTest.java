package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.ToDoList.COMPLETED_TASK_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

public class ToDoListTest {
    ToDoList todo;

    @BeforeEach
    public void setUp() {
        todo = new ToDoList();
    }

    @Test
    public void testConstructor() {
        assertEquals(0, todo.getNumToDos());
    }

    @Test
    public void testAddTask() {
        Task todo1 = new Task("Todo 1");
        Task todo2 = new Task("Todo 2");
        todo.addTask(todo1);
        todo.addTask(todo2);

        assertEquals(2, todo.getNumToDos());
        assertTrue(todo.getTasks().contains(todo1));
        assertTrue(todo.getTasks().contains(todo2));
    }

    @Test
    public void testRemoveTask() {
        Task todo1 = new Task("Todo 1");
        todo.addTask(todo1);
        todo.removeTask(0);

        assertEquals(0, todo.getNumToDos());
        assertFalse(todo.getTasks().contains(todo1));
    }

    @Test
    public void testCompleteTask() {
        Task todo1 = new Task("Todo 1");
        Task todo2 = new Task("Todo 2");
        todo.addTask(todo1);
        todo.addTask(todo2);
        todo.completeTask(1);

        assertFalse(todo.getTasks().get(0).getComplete());
        assertTrue(todo.getTasks().get(1).getComplete());
    }

    @Test
    public void testGetAllTaskDescriptions() {
        Task todo1 = new Task("Todo 1");
        Task todo2 = new Task("Todo 2");
        todo.addTask(todo1);
        todo.addTask(todo2);
        todo.completeTask(1);

        List<String> descriptions = todo.getAllTaskDescriptions();

        assertEquals(2, descriptions.size());
        assertEquals("Todo 1", descriptions.get(0));
        assertEquals(COMPLETED_TASK_PREFIX + "Todo 2", descriptions.get(1));
    }
}
