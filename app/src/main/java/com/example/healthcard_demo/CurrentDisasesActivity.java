package com.example.healthcard_demo;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CurrentDisasesActivity extends AppCompatActivity {
LinearLayout ll;
TestAdapter adapter;
Context context=this;
TableLayout layout;
String usermedicalid;
String mediid,udname;
    String diseasename;
String mid,ddate,dname,dsymt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_disases);

        layout=(TableLayout) findViewById(R.id.table_currentdisease);
        Bundle bundle = getIntent().getExtras();
        usermedicalid = bundle.getString("MedicalID");

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            TableRow row1 = new TableRow(this);
            TableLayout.LayoutParams lp2 = new TableLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            lp2.setMargins(0, 30, 0, 0);
            TextView tv2 = new TextView(this);
            tv2.setText("Medical Id.");
            tv2.setTextSize(20);
            row1.addView(tv2);

            TextView tv3 = new TextView(this);
            tv3.setText("Diseases.");
            tv3.setTextSize(20);
            row1.addView(tv3);
            layout.addView(row1);

            layout.setStretchAllColumns(true);
            Cursor cursor = adapter.selectHelathissue(usermedicalid);
            int l=0;
            while (cursor.moveToNext()) {
                TableRow row2 = new TableRow(this);
                row2.setMinimumHeight(50);
                if (l % 2 == 0)
                    row2.setBackgroundColor(getResources().getColor(R.color.purple_100));
                else
                    row2.setBackgroundColor(getResources().getColor(R.color.purple_200));
                row2.getBackground().setAlpha(40);

                final TextView oid = new TextView(this);
                oid.setText(cursor.getString(0));
                mediid = (cursor.getString(0));
                oid.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                oid.setTextColor(Color.BLACK);
                oid.setPadding(0, 0, 0, 0);


                final TextView name = new TextView(this);
                name.setText(cursor.getString(2));
                udname = (cursor.getString(2));
                name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                name.setTextColor(Color.BLACK);

                name.setPadding(0, 0, 0, 0);
                row2.addView(oid);
                row2.addView(name);
                row2.setLayoutParams(lp2);
                layout.addView(row2);
                l++;
             row2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder alertbuilder=new AlertDialog.Builder(context);
                        //alertbuilder.setTitle("           Current Diseases");
                        alertbuilder.setTitle(Html.fromHtml("<font color='#FF6200EE'>Current Diseases</font>"));
                        TableLayout layout=new TableLayout(context);
                        layout.setGravity(Gravity.CENTER);

                        Cursor c = adapter.selectcurrentdises(udname);
                        while (c.moveToNext()) {
                            final TableRow tableRow=new TableRow(context);
                            tableRow.setBackgroundResource(R.color.purple_100);
                            layout.addView(tableRow);


                            final TableRow r=new TableRow(context);
                            TextView tv=new TextView(context);
                            tv.setText("Medical Id");
                            tv.setTextSize(20);
                            tv.setTextColor(Color.BLACK);
                            //r.setBackgroundResource(R.drawable.row);
                            r.addView(tv);


                            final	TextView tv1=new TextView(context);
                            tv1.setText("      " +c.getString(0));

                            tv1.setTextSize(20);
                            tv1.setPadding(0,20,0,0);
                            tv1.setTextColor(Color.BLACK);
                            r.addView(tv1);
                            layout.addView(r);


                            final TableRow tableRow5=new TableRow(context);
                           // tableRow5.setBackgroundResource(R.drawable.line);
                            layout.addView(tableRow5);

                            final TableRow r2=new TableRow(context);
                            TextView tv2=new TextView(context);
                            tv2.setText("Date");
                            tv2.setTextSize(20);
                            tv2.setTextColor(Color.BLACK);
                            // r2.setBackgroundResource(R.drawable.rrow);
                            r2.addView(tv2);

                            final	TextView ttv1=new TextView(context);
                            ttv1.setText("      "+c.getString(1));
                            ttv1.setTextSize(20);
                            ttv1.setPadding(0,20,0,0);
                            ttv1.setTextColor(Color.BLACK);
                            r2.addView(ttv1);
                            layout.addView(r2);


                            final TableRow tableRow6=new TableRow(context);
                            //tableRow6.setBackgroundResource(R.drawable.line);
                            layout.addView(tableRow6);

                            final TableRow r3=new TableRow(context);
                            TextView tv3=new TextView(context);
                            tv3.setText("Diseases");
                            tv3.setTextSize(20);
                            tv3.setTextColor(Color.BLACK);
                            //r3.setBackgroundResource(R.drawable.row);
                            r3.addView(tv3);

                            final	TextView ttv2=new TextView(context);
                            ttv2.setText("      " +c.getString(2));
                            ttv2.setTextSize(20);
                            diseasename=(c.getString(2));
                            ttv2.setPadding(0,20,0,0);
                            ttv2.setTextColor(Color.BLACK);
                            r3.addView(ttv2);
                            layout.addView(r3);

                            final TableRow tableRow7=new TableRow(context);
                           //tableRow7.setBackgroundResource(R.drawable.line);
                            layout.addView(tableRow7);


                            final TableRow r4=new TableRow(context);
                            TextView tv4=new TextView(context);
                            tv4.setText("Symptons");
                            tv4.setTextColor(Color.BLACK);
                            tv4.setTextSize(20);
                            //r4.setBackgroundResource(R.drawable.anay);
                            r4.addView(tv4);

                            final	TextView ttv4=new TextView(context);
                            ttv4.setText("     " +c.getString(3));
                            ttv4.setTextSize(20);
                            ttv4.setPadding(0,20,0,0);
                            ttv4.setTextColor(Color.BLACK);
                            r4.addView(ttv4);
                            layout.addView(r4);

                            alertbuilder.setPositiveButton("Now Healthy",new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                    //mid,ddate,dname,dsymt

                                    mid=tv1.getText().toString();
                                    ddate=ttv1.getText().toString();
                                    dname=ttv2.getText().toString();
                                    dsymt=ttv4.getText().toString();

                                    long i=adapter.InsertDisesinhistory(mid,ddate,dname,dsymt);


                                   Toast.makeText(getApplicationContext()," Now Healthy...."+i, Toast.LENGTH_SHORT).show();;
                                    deletedisease();


                                }
                            });

                            alertbuilder.setNegativeButton("Not Now ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            alertbuilder.setView(layout);

                        }



                        AlertDialog alertDialog= alertbuilder.create();
                        alertDialog.show();

                    };


                });
            }




            }catch (Exception e){}


    }

    private void deletedisease() {

        adapter.DeleteDieases(diseasename);
        Toast.makeText(getApplicationContext()," Record Saved in History....", Toast.LENGTH_SHORT).show();;
    }


}
