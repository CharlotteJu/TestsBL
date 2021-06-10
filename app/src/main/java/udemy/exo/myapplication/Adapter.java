package udemy.exo.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {

    private List<Task> list;

    public Adapter(List<Task> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.configView(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class ViewHolder extends RecyclerView.ViewHolder {

    TextView name, age, species, owner;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        age = itemView.findViewById(R.id.age);
        species = itemView.findViewById(R.id.species);
        owner = itemView.findViewById(R.id.owner);
    }

    protected void configView(Task task) {
        name.setText(task.getName());
        age.setText(task.getAge() + "");
        species.setText(task.getSpecies());
        owner.setText(task.getOwner());
    }
}
