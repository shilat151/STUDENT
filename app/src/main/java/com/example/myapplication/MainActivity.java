package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.content.SharedPreferences;
import android.util.Patterns;

public class MainActivity extends Activity {

    EditText firstName, lastName, email;
    RadioGroup classGroup;
    Spinner major;
    CheckBox notifications, terms;
    Button register;
    TextView result;

    SharedPreferences data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);

        classGroup = findViewById(R.id.classGroup);
        major = findViewById(R.id.major);

        notifications = findViewById(R.id.notifications);
        terms = findViewById(R.id.terms);

        register = findViewById(R.id.register);
        result = findViewById(R.id.result);

        data = getSharedPreferences("StudentData", MODE_PRIVATE);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.majors,
                        android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        major.setAdapter(adapter);

        if (data.getBoolean("saved", false)) {
            showSavedData();
        }

        register.setOnClickListener(v -> saveStudent());
    }

    public void saveStudent() {

        firstName.setError(null);
        lastName.setError(null);
        email.setError(null);
        terms.setError(null);

        String name = firstName.getText().toString().trim();
        String last = lastName.getText().toString().trim();
        String mail = email.getText().toString().trim();

        boolean correct = true;

        if (name.isEmpty()) {
            firstName.setError(getString(R.string.error_first));
            correct = false;
        }

        if (last.isEmpty()) {
            lastName.setError(getString(R.string.error_last));
            correct = false;
        }

        if (mail.isEmpty()) {
            email.setError(getString(R.string.error_email));
            correct = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError(getString(R.string.error_email_invalid));
            correct = false;
        }

        if (classGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, R.string.error_class,
                    Toast.LENGTH_SHORT).show();
            correct = false;
        }

        if (major.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.error_major,
                    Toast.LENGTH_SHORT).show();
            correct = false;
        }

        if (!terms.isChecked()) {
            terms.setError(getString(R.string.error_terms));
            correct = false;
        }

        if (!correct) {
            return;
        }

        String schoolClass = "";

        int selected = classGroup.getCheckedRadioButtonId();

        if (selected == R.id.class10) {
            schoolClass = getString(R.string.class10);
        } else if (selected == R.id.class11) {
            schoolClass = getString(R.string.class11);
        } else if (selected == R.id.class12) {
            schoolClass = getString(R.string.class12);
        }

        String selectedMajor = major.getSelectedItem().toString();
        boolean getNotifications = notifications.isChecked();

        student student = new student(
                name,
                last,
                mail,
                schoolClass,
                selectedMajor,
                getNotifications
        );

        SharedPreferences.Editor editor = data.edit();

        editor.putString("firstName", student.firstName);
        editor.putString("lastName", student.lastName);
        editor.putString("email", student.email);
        editor.putString("class", student.studentClass);
        editor.putString("major", student.major);
        editor.putBoolean("notifications", student.notifications);
        editor.putBoolean("saved", true);

        editor.apply();

        showSavedData();

        Toast.makeText(this, R.string.success,
                Toast.LENGTH_SHORT).show();
    }

    public void showSavedData() {

        String name = data.getString("firstName", "");
        String last = data.getString("lastName", "");
        String mail = data.getString("email", "");
        String schoolClass = data.getString("class", "");
        String selectedMajor = data.getString("major", "");
        boolean getNotifications =
                data.getBoolean("notifications", false);

        String text =
                getString(R.string.saved) +
                        "\nשם פרטי: " + name +
                        "\nשם משפחה: " + last +
                        "\nאימייל: " + mail +
                        "\nכיתה: " + schoolClass +
                        "\nמגמה: " + selectedMajor +
                        "\nקבלת התראות: " +
                        (getNotifications ? "כן" : "לא");

        result.setText(text);
        result.setVisibility(View.VISIBLE);
    }
}