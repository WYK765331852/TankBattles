package com.tju.tank;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tju.tank.control.ControlActivity;
import com.tju.tank.support.Key;

public class ChooseTank extends Activity {
    RadioGroup tank;
    Button btn_main_ok;
    String tank_n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosetank_layout);
        tank = findViewById(R.id.tank);
        btn_main_ok = findViewById(R.id.btn_main2_ok);
        btn_main_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedId = tank.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(checkedId);
                tank_n = radioButton.getText().toString();
                Intent intent = getIntent();
                String pModel = intent.getStringExtra("model_data");
                Toast.makeText(ChooseTank.this, "Model: " + pModel + " You have chosen " + tank_n, Toast.LENGTH_LONG).show();
                alert_edit();
            }
        });
    }

    public void alert_edit() {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("设置IP号ַ")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String IPAddress = et.getText().toString();
                        Intent intent2 = new Intent(ChooseTank.this, ControlActivity.class);
                        intent2.putExtra("data", tank_n);
                        intent2.putExtra(Key.SERVER_IP_KEY, IPAddress);
                        startActivity(intent2);
                    }
                }).setNegativeButton("取消", null).show();
    }
}
