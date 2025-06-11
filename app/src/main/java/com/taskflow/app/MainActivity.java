package com.taskflow.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private FloatingActionButton fabAddTask;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupRecyclerView();
        loadTasks();
        setupFab();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewTasks);
        fabAddTask = findViewById(R.id.fabAddTask);
        sharedPreferences = getSharedPreferences("TaskFlowPrefs", MODE_PRIVATE);
        gson = new Gson();
        taskList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
    }

    private void setupFab() {
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        final EditText input = new EditText(this);
        input.setHint("Enter task description");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskText = input.getText().toString().trim();
            if (!taskText.isEmpty()) {
                addTask(taskText);
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addTask(String description) {
        Task newTask = new Task(System.currentTimeMillis(), description, false);
        taskList.add(0, newTask);
        taskAdapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
        saveTasks();
        Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskClick(int position) {
        Task task = taskList.get(position);
        task.setCompleted(!task.isCompleted());
        taskAdapter.notifyItemChanged(position);
        saveTasks();
        
        String message = task.isCompleted() ? "Task completed!" : "Task marked as pending";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this task?");
        
        builder.setPositiveButton("Delete", (dialog, which) -> {
            taskList.remove(position);
            taskAdapter.notifyItemRemoved(position);
            saveTasks();
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveTasks() {
        String json = gson.toJson(taskList);
        sharedPreferences.edit().putString("tasks", json).apply();
    }

    private void loadTasks() {
        String json = sharedPreferences.getString("tasks", null);
        if (json != null) {
            Type type = new TypeToken<List<Task>>(){}.getType();
            List<Task> savedTasks = gson.fromJson(json, type);
            if (savedTasks != null) {
                taskList.addAll(savedTasks);
                taskAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            showAboutDialog();
            return true;
        } else if (item.getItemId() == R.id.action_clear_completed) {
            clearCompletedTasks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About TaskFlow");
        builder.setMessage("TaskFlow v1.0\n\nA simple and elegant task management app to help you stay organized and productive.\n\n© 2025 TaskFlow");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void clearCompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : taskList) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }
        
        if (completedTasks.isEmpty()) {
            Toast.makeText(this, "No completed tasks to clear", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clear Completed Tasks");
        builder.setMessage("Remove " + completedTasks.size() + " completed task(s)?");
        
        builder.setPositiveButton("Clear", (dialog, which) -> {
            taskList.removeAll(completedTasks);
            taskAdapter.notifyDataSetChanged();
            saveTasks();
            Toast.makeText(this, completedTasks.size() + " tasks cleared", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
