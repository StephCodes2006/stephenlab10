// Stephen Akanniolu - n01725208
package com.stephenakanniolu.lab1011;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AsnaFragment extends Fragment {

    private EditText steNameInput, steDescInput;
    private DatabaseReference steDatabase;
    private RecyclerView steRepo;
    private CourseAdapter steAdapter;
    private List<Course> steCourseList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_asna, container, false);

        steDatabase = FirebaseDatabase.getInstance().getReference("courses");
        steNameInput = v.findViewById(R.id.steEditCourseName);
        steDescInput = v.findViewById(R.id.steEditCourseDesc);
        steRepo = v.findViewById(R.id.steRecyclerView);

        // Requirement 55.b.ii: Force Uppercase
        steNameInput.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        steCourseList = new ArrayList<>();
        steRepo.setLayoutManager(new LinearLayoutManager(getContext()));

        v.findViewById(R.id.steBtnAdd).setOnClickListener(view -> steAddCourse());
        v.findViewById(R.id.steBtnDelete).setOnClickListener(view -> steDeleteAll());

        steLoadData();
        return v;
    }

    private void steAddCourse() {
        String name = steNameInput.getText().toString().trim();
        String desc = steDescInput.getText().toString().trim();

        // Requirement 55.d.vi: Regex for 4 letters - 3 or 4 numbers
        if (!name.matches("^[A-Z]{4}-\\d{3,4}$")) {
            steNameInput.setError("Invalid Format (e.g. CENG-258)");
            return;
        }

        if (desc.isEmpty()) {
            steDescInput.setError("Description required");
            return;
        }

        String id = steDatabase.push().getKey();
        Course course = new Course(id, name, desc);
        steDatabase.child(id).setValue(course);

        steNameInput.setText("");
        steDescInput.setText("");
    }

    private void steLoadData() {
        // Requirement 55.h: Real-time updates
        steDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                steCourseList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Course course = postSnapshot.getValue(Course.class);
                    steCourseList.add(course);
                }
                steAdapter = new CourseAdapter(steCourseList, course -> {
                    // Requirement 55.i: Delete on long press
                    steDatabase.child(course.id).removeValue();
                    return true;
                });
                steRepo.setAdapter(steAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void steDeleteAll() {
        if (steCourseList.isEmpty()) {
            Toast.makeText(getContext(), "No data to delete", Toast.LENGTH_SHORT).show();
        } else {
            steDatabase.removeValue();
        }
    }
}