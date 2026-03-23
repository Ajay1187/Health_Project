package com.example.healthcard_demo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;

public class TestAdapter {
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private com.example.healthcard_demo.DatahelperActivity mDbHelper;

    public TestAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new com.example.healthcard_demo.DatahelperActivity(mContext);

        Log.v(TAG, "TestAdapter");
    }

    public TestAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }

        Log.v(TAG, "TestAdapter createDatabase");
        return this;
    }

    public TestAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }

        Log.v(TAG, "TestAdapter Open");
        return this;
    }

    public void close() {
        mDbHelper.close();

        Log.v(TAG, "TestAdapter close");

    }
/***********Insert User Query************/
    public long Insertuser(String sname, String semial,String saddress, String sphone,String sadhar,String sdbdte,
                           String squestion,String sans, String spassword) {
        long i;
        ContentValues values = new ContentValues();
        values.put("MedicalId", sname);
        values.put("U_Name", semial);
        values.put("Address", saddress);
        values.put("Mobile", sphone);
        values.put("Adhar",sadhar);
        values.put("Bdate",sdbdte);
        values.put("Question",squestion);
        values.put("Ans",sans);
        values.put("Password", spassword);

        i = mDb.insert("User", null, values);
        return i;
    }
    public Cursor getalluserdetails(String id) {
        String q = "Select * from User where Adhar='" + id + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }
    public long Updateuser(String mid, String spass) {
        long i;
        ContentValues values = new ContentValues();
        values.put("MedicalId", mid);
        values.put("Password", spass);
        i=mDb.update("User", values, "MedicalId"+"="+mid, null);
        return i;

    }


    public int checkUserIDandQuestions(String id,String question, String sans) {
        int i;
        String sql = "select count(*) from User where MedicalId='" + id + "' and Question='"+question+"' " +
                " and Ans='" + sans + "'";
        Cursor c = mDb.rawQuery(sql, null);
        //Log.w("Cursor", c.getString(0));
        c.moveToNext();
        i = Integer.parseInt(c.getString(0));
        c.close();
        return (i);
    }

    public Cursor getMedicalid(String mobile) {
        String q = "Select * from User where MedicalId='" + mobile + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public int checkmedicalid(String medicalid, String usermobile) {
        int i;
        String sql = "select count(*) from User where MedicalId='" + medicalid + "' and Mobile='" + usermobile + "'";
        Cursor c = mDb.rawQuery(sql, null);
        //Log.w("Cursor", c.getString(0));
        c.moveToNext();
        i = Integer.parseInt(c.getString(0));
        c.close();
        return (i);
    }
    public Cursor userincrementid() {
        String sql = "select max(MedicalId) from User";
        Cursor c = mDb.rawQuery(sql, null);
        return c;
    }

    public Cursor getUserdetails(String medicalid) {
        String q = "Select * from User where MedicalId='" + medicalid + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    /*****************Check User Query*******************/
    public int checkUserLogin(String smobile, String spassword) {
        int i;
        String sql = "select count(*) from User where MedicalId='" + smobile + "' and Password='" + spassword + "'";
        Cursor c = mDb.rawQuery(sql, null);
        //Log.w("Cursor", c.getString(0));
        c.moveToNext();
        i = Integer.parseInt(c.getString(0));
        c.close();
        return (i);
    }

    public Cursor selectUser() {
        String sql="select * from User";
        Cursor c=mDb.rawQuery(sql, null);
        return c;
    }

    public Cursor userselect() {
        String sql="select * from User";
        Cursor c=mDb.rawQuery(sql, null);
        return c;
    }

   /* public long InserDoctor(String id, String name, String gender, String age, String add,
                            String phone, String day, String time, String pass, String spl) {

        long i;
        ContentValues values = new ContentValues();
        values.put("D_ID", id);
        values.put("D_Name", name);
        values.put("D_Gen", gender);
        values.put("D_Age", age);
        values.put("D_Add", add);
        values.put("D_Phone", phone);
        values.put("D_Day", day);
        values.put("D_Time", time);
        values.put("D_Pass", pass);
        values.put("D_Spl", spl);

        i = mDb.insert("Doctor", null, values);
        return i;
    }*/

    public long InsertHealthissue(String medicalid, String disdate, String dname, String sname) {

        long i;
        ContentValues values = new ContentValues();
        values.put("M_Id", medicalid);
        values.put("D_Date", disdate);
        values.put("D_Name", dname);
        values.put("D_Symptons", sname);

        i = mDb.insert("Healthissue", null, values);
        return i;
    }

    public Cursor selectHelathissue(String usermobile) {
        String q = "Select * from Healthissue where M_Id='" + usermobile + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }


    public void DeleteDieases(String udname) {
        //mDb.delete("Healthissue","D_Name"+"="+udname, null);
        mDb.delete("Healthissue", "D_Name=?", new String[]{udname});
    }

    public Cursor selectcurrentdises(String udname) {
        String q = "Select * from Healthissue where D_Name='" + udname + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public long InsertDisesinhistory(String mid, String ddate, String dname, String dsymt) {
        long i;
        ContentValues values = new ContentValues();
        values.put("D_Id", mid);
        values.put("D_Date", ddate);
        values.put("D_Name", dname);
        values.put("D_Symptons", dsymt);

        i = mDb.insert("Diseases", null, values);
        return i;
    }

    public Cursor selectDisesehistory(String usermedicalid) {
        String q = "Select * from Diseases where D_Id='" + usermedicalid + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public long InsertDoctor(String medicalid, String semial, String saddress, String sphone,
                             String sadhar, String squestion, String sans, String spassword) {
        long i;
        ContentValues values = new ContentValues();
        values.put("DoctorId", medicalid);
        values.put("Name", semial);
        values.put("Address", saddress);
        values.put("Mobile", sphone);
        values.put("License",sadhar);
        values.put("Question",squestion);
        values.put("Ans",sans);
        values.put("Password", spassword);

        i = mDb.insert("Doctor", null, values);
        return i;

    }


    public Cursor getDoctorid(String drid) {
        String q = "Select * from Doctor where Mobile='" + drid + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public int checDoctorLogin(String smedicalid, String spassword) {
        int i;
        String sql = "select count(*) from Doctor where Mobile='" + smedicalid + "' and Password='" + spassword + "'";
        Cursor c = mDb.rawQuery(sql, null);
        //Log.w("Cursor", c.getString(0));
        c.moveToNext();
        i = Integer.parseInt(c.getString(0));
        c.close();
        return (i);
    }

    public int checkDoctorandQuestions(String id, String question, String sans) {
        int i;
        String sql = "select count(*) from Doctor where DoctorId='" + id + "' and Question='"+question+"' " +
                " and Ans='" + sans + "'";
        Cursor c = mDb.rawQuery(sql, null);
        //Log.w("Cursor", c.getString(0));
        c.moveToNext();
        i = Integer.parseInt(c.getString(0));
        c.close();
        return (i);
    }

    public long Updatedoctor(String id, String spass) {
        long i;
        ContentValues values = new ContentValues();
        values.put("DoctorId", id);
        values.put("Password", spass);
        i=mDb.update("Doctor", values, "DoctorId"+"="+id, null);
        return i;

    }

    public Cursor selectdoctor() {
        String sql="select * from Doctor";
        Cursor c=mDb.rawQuery(sql, null);
        return c;
    }

    public Cursor doctorincrementid() {
        String sql = "select max(DoctorId) from Doctor";
        Cursor c = mDb.rawQuery(sql, null);
        return c;
    }




    /****************Databse Query************************/















/**************************Demo*******************************************
    public long InsertUser(String uname, String umob, String upass) {
        long i;
        ContentValues values = new ContentValues();
        values.put("FullName", uname);
        values.put("Mobile", umob);
        values.put("Password", upass);
        i = mDb.insert("User", null, values);
        return i;

    }



    public Cursor getpass() {
        String sql="select Mobile from User";
        Cursor c=mDb.rawQuery(sql, null);
        return c;

    }

    public long SaveAddress(String aaone, String aatwo, String aathree, String almark,
                            String aastate, String aacity, String aarea, String azip) {
        long i;
        ContentValues values = new ContentValues();


        values.put("A_One", aaone);
        values.put("A_Two", aatwo);
        values.put("A_Three", aathree);
        values.put("A_Mark", almark);
        values.put("A_State", aastate);
        values.put("A_City", aacity);
        values.put("A_Area", aarea);
        values.put("A_Zip", azip);
        i = mDb.insert("Address", null, values);
        return i;


    }

    public long InsertFoodmenu(String ldinn, String sdate, String sename, String semname,
                               String seprice, String seaddress, String secontact) {


        long i;
        ContentValues values = new ContentValues();
        values.put("F_Type", ldinn);
        values.put("F_Date", sdate);
        values.put("F_Name", sename);
        values.put("F_Mname", semname);
        values.put("F_Mprice", seprice);
        values.put("F_Add", seaddress);
        values.put("F_Mob", secontact);

        i = mDb.insert("Menu", null, values);
        return i;

    }


    public Cursor selecfood() {
        String q = "Select * from Menu ";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public Cursor getMenuPrice(String cost) {
        String q = "Select * from Menu where F_Mname='" + cost + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public long InsertCard(String ctype,String cardno,String bnam, String cardem, String cardey, String cardcv) {

        long i;
        ContentValues values = new ContentValues();
        values.put("C_Type", ctype);
        values.put("C_Bname",bnam);
        values.put("C_No", cardno);
        values.put("C_Emm", cardem);
        values.put("C_Eyy", cardey);
        values.put("C_Date", cardcv);

        i = mDb.insert("Addcard", null, values);
        return i;
    }

    public Cursor getCardNumber() {
        String q = "Select * from Addcard ";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public Cursor getcarddetails() {
        String q = "Select * from Addcard ";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public int checkrecord() {
        int i;
        String sql="select count(*) from Addcard";
        Cursor c=mDb.rawQuery(sql,null);
        //Log.w("Cursor", c.getString(0));
        c.moveToNext();
        i=Integer.parseInt(c.getString(0));
        c.close();
        return (i);

    }

    public long insertAmount(String amount, String cvvnumber) {
        long i;
        ContentValues values = new ContentValues();
        values.put("Amt", amount);
        values.put("Cvv",cvvnumber);
        i = mDb.insert("Amount", null, values);
        return i;
    }

    public Cursor getAmount() {
        String q="SELECT SUM(Amt) FROM Amount";
        Cursor c=mDb.rawQuery(q,null);
        return  c;
    }


    public int checkwalletbalance() {
        int i;
        String sql="select count(*) from Amount";
        Cursor c=mDb.rawQuery(sql,null);
        //Log.w("Cursor", c.getString(0));
        c.moveToNext();
        i=Integer.parseInt(c.getString(0));
        c.close();
        return (i);

    }



    public long DevloperRegistration(String dno, String dname, String dmob, String dadd, String dedu) {
        long i;
        ContentValues values = new ContentValues();
        values.put("D_Id", dno);
        values.put("D_Name",dname);
        values.put("D_Add", dadd);
        values.put("D_Mob",dmob);
        values.put("D_Edu", dedu);
      //  values.put("D_Tech",dtch);
       // values.put("D_Pass",dpass);

        i = mDb.insert("DevloperReg", null, values);
        return i;
    }


    public long CustomerSignup(String id, String name, String add, String mob, String bname) {
        long i;
        ContentValues values = new ContentValues();
        values.put("C_Id", id);
        values.put("C_Name",name);
        values.put("C_Address", add);
        values.put("C_Mobile",mob);
        values.put("C_Business", bname);


        i = mDb.insert("CustomerReg", null, values);
        return i;
    }

    public int checkUserLogin(String userid, String password) {

        int i;
        String sql = "select count(*) from CustomerReg where C_Mobile='" + userid + "' and C_Business='" + password + "'";
        Cursor c = mDb.rawQuery(sql, null);
        //Log.w("Cursor", c.getString(0));
        c.moveToNext();
        i = Integer.parseInt(c.getString(0));
        c.close();
        return (i);
    }

    public Cursor selectdevloper() {
        String q = "Select * from DevloperReg ";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }
    public Cursor developerincrementid() {
        String sql = "select max(D_Id) from DevloperReg";
        Cursor c = mDb.rawQuery(sql, null);
        return c;
    }



    public long InsertAppData(String appid, String pbus, String appname, String appdb, String appuse, String appdet) {
        long i;
        ContentValues values = new ContentValues();
        values.put("A_id", appid);
        values.put("A_Bus",pbus);
        values.put("A_Name", appname);
        values.put("A_Dbtype",appdb);
        values.put("A_Appuse", appuse);
        values.put("A_odetails",appdet);

        i = mDb.insert("AppData", null, values);
        return i;

    }

    public Cursor selectappdata() {
        String q = "Select * from AppData ";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public Cursor appdataincrementid() {
        String sql = "select max(A_id) from AppData";
        Cursor c = mDb.rawQuery(sql, null);
        return c;
    }

    public Cursor getDevloperdetalis() {
        String q = "Select * from AppData ";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public Cursor getApiDetalis(String type) {
        String q = "Select * from APITable where A_Name='" + type + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }


    public Cursor getapi() {
        String q = "Select * from APITable ";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }


    public long AddMaskData(String name, String time) {
        long i;
        ContentValues values = new ContentValues();
        values.put("Yes", name+  time);



        i = mDb.insert("Mask", null, values);
        return i;
    }

    public Cursor selecmaskinfo() {
        String q = "Select * from Mask ";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public long AddNoMaskData(String name, String time) {
        long i;
        ContentValues values = new ContentValues();
        values.put("No", name+  time);



        i = mDb.insert("Mask", null, values);
        return i;
    }

    public long InsertComplaint(String num, String cdate, String name, String station,
                                String assuced, String complaint, String address, String age,
                                String adhar, String mobile, String relation,String sact,String scomp) {

        long i;
        ContentValues values = new ContentValues();
        values.put("C_No", num);
        values.put("C_Date", cdate);
        values.put("C_Name", name);
        values.put("C_Station", station);
        values.put("C_Accused", assuced);
        values.put("C_Complaint", complaint);
        values.put("C_Address", address);
        values.put("C_Age", age);
        values.put("C_Aadhar", adhar);
        values.put("C_Mobile", mobile);
        values.put("C_Relation", relation);
        values.put("Status", sact);
       // values.put("Status", scomp);



        i = mDb.insert("Complaint", null, values);
        return i;
    }

    public Cursor getAllComplaintstatus(String compid) {
        String q = "Select * from Complaint where C_No='" + compid + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }
    public long InsertUpadteComplaintStatus(String svno, String textactive) {
        long i;
        ContentValues values = new ContentValues();
        values.put("C_No", svno);

        values.put("Status", textactive);
        i=mDb.update("Complaint", values, "C_No"+"="+svno, null);
        return i;
    }

    public long InsertUpadteComplaintStatuscomplete(String svno, String textcomplete) {
        long i;
        ContentValues values = new ContentValues();
        values.put("C_No", svno);

        values.put("Status", textcomplete);
        i=mDb.update("Complaint", values, "C_No"+"="+svno, null);
        return i;
    }

   /* public long InsertUpadteComplaint(String svno, String textactive, String textcomplete) {

        long i;
        ContentValues values = new ContentValues();
        values.put("C_No", svno);

        values.put("Status", textactive);
        values.put("Status", textcomplete);

        i=mDb.update("Complaint", values, "C_No"+"="+svno, null);
        return i;
    }

    public Cursor getAllComplaintdetails(String compid) {
        String q = "Select * from Complaint where C_No='" + compid + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;

    }

    public Cursor selectComplaint() {
        String q = "Select * from Complaint ";
        Cursor c = mDb.rawQuery(q, null);
        return c;

    }

    public Cursor complaintincrementid() {
        String sql = "select max(C_No) from Complaint";
        Cursor c = mDb.rawQuery(sql, null);
        return c;
    }

    public long InsertCriminal(String pcaseid, String pcasetype, String pddate, String pcrname,
                               String gender, String page, String pmobile, String ppstationno,
                               String pfno, String paname, String pfyear, String psection) {

        long i;
        ContentValues values = new ContentValues();
        values.put("C_No", pcaseid);
        values.put("C_Type", pcasetype);
        values.put("C_Date", pddate);
        values.put("C_Name", pcrname);
        values.put("C_Gender", gender);
        values.put("C_Age", page);
        values.put("C_Mobile", pmobile);
        values.put("C_Police", ppstationno);
        values.put("C_FIRNo", pfno);
        values.put("C_PName", paname);
        values.put("C_Firyear", pfyear);
        values.put("C_Section", psection);

        i = mDb.insert("Criminal", null, values);
        return i;
    }
    public Cursor getAllCriminaldetails(String compid) {
        String q = "Select * from Criminal where C_No='" + compid + "'";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }
    public Cursor selectCriminal() {
        String q = "Select * from Criminal ";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public Cursor criminalincrementid() {
        String sql = "select max(C_No) from Criminal";
        Cursor c = mDb.rawQuery(sql, null);
        return c;
    }


    public long Insertuser(String sno, String sname, String sadd,
                           String smobile, String sgen, String sadhar, String spass) {
        long i;
        ContentValues values = new ContentValues();
        values.put("U_No",sno);
        values.put("Name",sname);
        values.put("Address",sadd);
        values.put("Gender", sgen);
        values.put("Mobile", smobile);
        values.put("Aadhar", sadhar);

        values.put("Password", spass);


        i = mDb.insert("Register", null, values);
        return i;
    }


    public Cursor selectuser() {
        String q = "Select * from Register ";
        Cursor c = mDb.rawQuery(q, null);
        return c;
    }

    public Cursor userincrementid() {
        String sql = "select max(U_No) from Register";
        Cursor c = mDb.rawQuery(sql, null);
        return c;
    }

    public int checkUserLoginn(String username, String upassword) {
        int i;
        String sql = "select count(*) from Register where Mobile='" + username + "' and Password='" + upassword + "'";
        Cursor c = mDb.rawQuery(sql, null);
        //Log.w("Cursor", c.getString(0));
        c.moveToNext();
        i = Integer.parseInt(c.getString(0));
        c.close();
        return (i);
    }*********************************************************************************************************************/



}











	
	
	