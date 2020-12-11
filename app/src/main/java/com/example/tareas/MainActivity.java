package com.example.tareas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tareas.model.Tarea;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private List<Tarea> listTarea = new ArrayList<Tarea>();
    ArrayAdapter<Tarea> arrayAdapterTarea;

    EditText tareaP, descripcionP, responsableP;
    ListView listV_tareas;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Tarea tareaSeleccioanda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tareaP = findViewById(R.id.etTarea);
        descripcionP = findViewById(R.id.etDescripcion);
        responsableP = findViewById(R.id.etResponsable);

        listV_tareas = findViewById(R.id.lv_datosTareas);
        inicializarFirebase();
        listarDatos();

        listV_tareas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tareaSeleccioanda = (Tarea) adapterView.getItemAtPosition(i);
                tareaP.setText(tareaSeleccioanda.getTarea());
                descripcionP.setText(tareaSeleccioanda.getDescripcion());
                responsableP.setText(tareaSeleccioanda.getResponsable());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Tarea").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTarea.clear();
                for (DataSnapshot objSnap : snapshot.getChildren()){
                    Tarea t = objSnap.getValue(Tarea.class);
                    listTarea.add(t);

                    arrayAdapterTarea = new ArrayAdapter<Tarea>(MainActivity.this, android.R.layout.simple_list_item_1, listTarea);
                    listV_tareas.setAdapter(arrayAdapterTarea);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String tarea = tareaP.getText().toString();
        String descripcion = descripcionP.getText().toString();
        String responsable = responsableP.getText().toString();

        switch (item.getItemId()){

            case R.id.icon_add:{

                if(tarea.equals("") || descripcion.equals("") || responsable.equals("")){
                    validador();
                }else{
                    try {
                        Tarea t = new Tarea();
                        t.setUid(UUID.randomUUID().toString());
                        t.setTarea(tarea);
                        t.setDescripcion(descripcion);
                        t.setResponsable(responsable);
                        databaseReference.child("Tarea").child(t.getUid()).setValue(t);
                        Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show();
                        limpiarTxt();
                    }catch (Exception e){
                        Toast.makeText(this, "Llena el formulario para continuar", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }case R.id.icon_save:{
                try {
                    Tarea t = new Tarea();
                    t.setUid(tareaSeleccioanda.getUid());
                    t.setTarea(tareaP.getText().toString());
                    t.setDescripcion(descripcionP.getText().toString());
                    t.setResponsable(responsableP.getText().toString());
                    databaseReference.child("Tarea").child(t.getUid()).setValue(t);
                    limpiarTxt();
                    Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(this, "Selecciona una tarea primero", Toast.LENGTH_SHORT).show();
                }

                break;

            }case R.id.icon_delete:{
                try {
                    Tarea t = new Tarea();
                    t.setUid(tareaSeleccioanda.getUid());
                    databaseReference.child("Tarea").child(t.getUid()).removeValue();
                    Toast.makeText(this, "Eliminar", Toast.LENGTH_SHORT).show();
                    limpiarTxt();
                }catch (Exception e){
                    Toast.makeText(this, "Selecciona una tarea primero", Toast.LENGTH_SHORT).show();
                }
                break;

            }
            default:break;
        }
        return true;
    }

    private void limpiarTxt() {
        tareaP.setText("");
        descripcionP.setText("");
        responsableP.setText("");
    }

    private void validador() {
        String tarea = tareaP.getText().toString();
        String descripcion = descripcionP.getText().toString();
        String responsable = responsableP.getText().toString();

        if (tarea.equals("")){
            tareaP.setError("Ingresa la tarea");
        }else if (descripcion.equals("")){
            descripcionP.setError("Ingresa la descripción");
        }else if (responsable.equals("")){
            responsableP.setError("Ingresa el responsable");
        }
    }
}