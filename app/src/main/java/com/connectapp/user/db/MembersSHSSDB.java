package com.connectapp.user.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.connectapp.user.data.Member;

import java.util.ArrayList;

public class MembersSHSSDB implements DBConstants {
    private static MembersSHSSDB obj = null;


    public synchronized static MembersSHSSDB obj() {

        if (obj == null)
            obj = new MembersSHSSDB();
        return obj;

    }

    public Boolean saveMembersData(Context context, ContentValues cv) {

        System.out.println(" ----------  SAVING MEMBER --------- ");
        SQLiteDatabase mdb = ConnectAppDBHelper.getInstance(context).getWritableDatabase();
        mdb.beginTransaction();
        try {
            mdb.insert(MEMBERS_DIRECTORY_SHSS_TABLE, null, cv);
            mdb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            mdb.endTransaction();
            return true;
        }

    }

    /**
     * fetch members according to cityName
     */
    public ArrayList<Member> getMembers(Context context, String cityName) {

        ArrayList<Member> members = new ArrayList<Member>();
        String[] columns = {_ID, CITY, ID, NAME, ID_NO, SPOUSE_NAME, CONTACT_NO, MOBILE, EMAIL, DESIGNATION, ADD1, ADD2, ADD3,
                PIN, TOWN, PIC};

        SQLiteDatabase mdb = ConnectAppDBHelper.getInstance(context).getReadableDatabase();
        String orderProtocol = "CAST (" + SEQUENCE + " AS INTEGER)" + " ASC";
        Cursor cur = mdb.query(MEMBERS_DIRECTORY_SHSS_TABLE, columns, CITY + "=?", new String[]{cityName}, null, null, orderProtocol);


        if (!isDatabaseEmpty(cur)) {
            try {
                if (cur.moveToFirst()) {
                    do {
                        Member member = new Member();
                        member.city = cur.getString(cur.getColumnIndex(DBConstants.CITY));
                        member.id = cur.getString(cur.getColumnIndex(DBConstants.ID));
                        member.name = cur.getString(cur.getColumnIndex(DBConstants.NAME));
                        member.idNo = cur.getString(cur.getColumnIndex(DBConstants.ID_NO));
                        member.spouseName = cur.getString(cur.getColumnIndex(DBConstants.SPOUSE_NAME));
                        member.contactNo = cur.getString(cur.getColumnIndex(DBConstants.CONTACT_NO));
                        member.mobile = cur.getString(cur.getColumnIndex(DBConstants.MOBILE));
                        member.email = cur.getString(cur.getColumnIndex(DBConstants.EMAIL));
                        member.designation = cur.getString(cur.getColumnIndex(DBConstants.DESIGNATION));
                        member.add1 = cur.getString(cur.getColumnIndex(DBConstants.ADD1));
                        member.add2 = cur.getString(cur.getColumnIndex(DBConstants.ADD2));
                        member.add3 = cur.getString(cur.getColumnIndex(DBConstants.ADD3));
                        member.pin = cur.getString(cur.getColumnIndex(DBConstants.PIN));
                        member.town = cur.getString(cur.getColumnIndex(DBConstants.TOWN));
                        member.picUrl = cur.getString(cur.getColumnIndex(DBConstants.PIC));
                        members.add(member);
                    } while (cur.moveToNext());
                }
                cur.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return members;
    }

    private Boolean isDatabaseEmpty(Cursor mCursor) {

        if (mCursor.moveToFirst()) {
            // NOT EMPTY
            return false;

        } else {
            // IS EMPTY
            return true;
        }

    }

    public ArrayList<Member> getSearchResult(Context mContext, String name) {

        Log.d("Search", "Called");
        ArrayList<Member> memberList = new ArrayList<Member>();
        String[] columns = {_ID, CITY, ID, NAME, ID_NO, SPOUSE_NAME, CONTACT_NO, MOBILE, EMAIL, DESIGNATION, ADD1, ADD2, ADD3,
                PIN, TOWN, PIC};
        SQLiteDatabase mdb = ConnectAppDBHelper.getInstance(mContext).getReadableDatabase();
        //Cursor cur = mdb.query(MEMBERS_DIRECTORY_TABLE, columns, NAME + " LIKE '%" + name + "%'", null, null, null, null, null);
        Cursor cur = mdb.query(MEMBERS_DIRECTORY_SHSS_TABLE, columns, NAME + " LIKE '%" + name + "%'" + " OR " + MOBILE + " LIKE '%"
                + name + "%'" + " OR " + EMAIL + " LIKE '%" + name + "%'", null, null, null, null, null);

        if (!isDatabaseEmpty(cur)) {
            try {
                if (cur.moveToFirst()) {
                    do {
                        Member member = new Member();
                        member.city = cur.getString(cur.getColumnIndex(DBConstants.CITY));
                        member.id = cur.getString(cur.getColumnIndex(DBConstants.ID));
                        member.name = cur.getString(cur.getColumnIndex(DBConstants.NAME));
                        member.idNo = cur.getString(cur.getColumnIndex(DBConstants.ID_NO));
                        member.spouseName = cur.getString(cur.getColumnIndex(DBConstants.SPOUSE_NAME));
                        member.contactNo = cur.getString(cur.getColumnIndex(DBConstants.CONTACT_NO));
                        member.mobile = cur.getString(cur.getColumnIndex(DBConstants.MOBILE));
                        member.email = cur.getString(cur.getColumnIndex(DBConstants.EMAIL));
                        member.designation = cur.getString(cur.getColumnIndex(DBConstants.DESIGNATION));
                        member.add1 = cur.getString(cur.getColumnIndex(DBConstants.ADD1));
                        member.add2 = cur.getString(cur.getColumnIndex(DBConstants.ADD2));
                        member.add3 = cur.getString(cur.getColumnIndex(DBConstants.ADD3));
                        member.pin = cur.getString(cur.getColumnIndex(DBConstants.PIN));
                        member.town = cur.getString(cur.getColumnIndex(DBConstants.TOWN));
                        member.picUrl = cur.getString(cur.getColumnIndex(DBConstants.PIC));
                        memberList.add(member);

                    } while (cur.moveToNext());
                }
                cur.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Member", "cursor is empty");
        }
        Log.d("Member", "Size: " + memberList.size());
        return memberList;

    }

    /**
     * fetch members according to ID
     */
    public ArrayList<Member> getMemberFromId(Context context, String id) {

        ArrayList<Member> members = new ArrayList<Member>();
        String[] columns = {_ID, CITY, ID, NAME, ID_NO, SPOUSE_NAME, CONTACT_NO, MOBILE, EMAIL, DESIGNATION, ADD1, ADD2, ADD3,
                PIN, TOWN, PIC};

        SQLiteDatabase mdb = ConnectAppDBHelper.getInstance(context).getReadableDatabase();
        Cursor cur = mdb.query(MEMBERS_DIRECTORY_SHSS_TABLE, columns, ID + "=?", new String[]{id}, null, null, null);

		/*Cursor cur = mdb.query(HISTORY_TABLE, columns, BLOCK_DISTRICT_ID + "=?" + "AND " + BLOCK_PROJ_TYPE + "=?", new String[] { districtId,
				projectType }, null, null, null);*/
        //Cursor cur = mdb.query(HISTORY_TABLE, null, null, null, null, null, null);

        if (!isDatabaseEmpty(cur)) {
            try {
                if (cur.moveToFirst()) {
                    do {
                        Member member = new Member();
                        member.city = cur.getString(cur.getColumnIndex(DBConstants.CITY));
                        member.id = cur.getString(cur.getColumnIndex(DBConstants.ID));
                        member.name = cur.getString(cur.getColumnIndex(DBConstants.NAME));
                        member.idNo = cur.getString(cur.getColumnIndex(DBConstants.ID_NO));
                        member.spouseName = cur.getString(cur.getColumnIndex(DBConstants.SPOUSE_NAME));
                        member.contactNo = cur.getString(cur.getColumnIndex(DBConstants.CONTACT_NO));
                        member.mobile = cur.getString(cur.getColumnIndex(DBConstants.MOBILE));
                        member.email = cur.getString(cur.getColumnIndex(DBConstants.EMAIL));
                        member.designation = cur.getString(cur.getColumnIndex(DBConstants.DESIGNATION));
                        member.add1 = cur.getString(cur.getColumnIndex(DBConstants.ADD1));
                        member.add2 = cur.getString(cur.getColumnIndex(DBConstants.ADD2));
                        member.add3 = cur.getString(cur.getColumnIndex(DBConstants.ADD3));
                        member.pin = cur.getString(cur.getColumnIndex(DBConstants.PIN));
                        member.town = cur.getString(cur.getColumnIndex(DBConstants.TOWN));
                        member.picUrl = cur.getString(cur.getColumnIndex(DBConstants.PIC));
                        members.add(member);
                    } while (cur.moveToNext());
                }
                cur.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return members;
    }

    public boolean clearTable(Context context) {
        System.out.println(" ----------  Clear Table --------- ");
        SQLiteDatabase mdb = ConnectAppDBHelper.getInstance(context).getWritableDatabase();
        mdb.beginTransaction();
        try {
            mdb.delete(MEMBERS_DIRECTORY_SHSS_TABLE, null, null);
            mdb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            mdb.endTransaction();
            return true;
        }
    }

    public Boolean isMembersDirectoryEmpty(Context context) {
        SQLiteDatabase mdb = ConnectAppDBHelper.getInstance(context).getReadableDatabase();
		/*Cursor cur = mdb.query(HISTORY_TABLE, columns, BLOCK_DISTRICT_ID + "=?" + "AND " + BLOCK_PROJ_TYPE + "=?", new String[] { districtId,
				projectType }, null, null, null);*/
        Cursor cur = mdb.query(MEMBERS_DIRECTORY_SHSS_TABLE, null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            // NOT EMPTY
            return false;

        } else {
            // IS EMPTY
            return true;
        }

    }
}
