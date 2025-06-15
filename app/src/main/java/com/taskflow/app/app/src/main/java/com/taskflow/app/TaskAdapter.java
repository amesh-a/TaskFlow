package com.taskflow.app;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(int position);
        void onTaskLongClick(int position);
    }

    public TaskAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task, position);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBoxCompleted;
        private TextView textViewDescription;
        private TextView textViewDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }

        public void bind(Task task, int position) {
            textViewDescription.setText(task.getDescription());
            checkBoxCompleted.setChecked(task.isCompleted());

            // Format date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            textViewDate.setText(sdf.format(new Date(task.getCreatedAt())));

            // Style completed tasks
            if (task.isCompleted()) {
                textViewDescription.setPaintFlags(textViewDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textViewDescription.setAlpha(0.6f);
                textViewDate.setAlpha(0.6f);
            } else {
                textViewDescription.setPaintFlags(textViewDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                textViewDescription.setAlpha(1.0f);
                textViewDate.setAlpha(0.7f);
            }

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(position);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onTaskLongClick(position);
                }
                return true;
            });

            checkBoxCompleted.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(position);
                }
            });
        }
    }
}
