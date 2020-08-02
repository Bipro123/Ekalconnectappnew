package com.connectapp.user.db;

public interface DBConstants {

    public static final int DB_VERSION = 7; // 7th Sept 2018

    public static final String DB_NAME = "ConnectAppDB.db";
    /* public static final String DB_NAME = Environment.getExternalStorageDirectory() + "/SubmissionHistory.db";*/ //This is for debugging purpose only

    public static final String _ID = "_id";

    final String CREATE_TABLE_BASE = "CREATE TABLE IF NOT EXISTS ";

    final String ON = " ON ";

    final String PRIMARY_KEY = " PRIMARY KEY";

    final String INTEGER = " Integer";

    final String TEXT = " TEXT";

    final String DATE_TIME = " DATETIME";

    final String BLOB = " BLOB";

    final String AUTO_ICNREMENT = " AUTOINCREMENT";

    final String UNIQUE = "UNIQUE";

    final String START_COLUMN = " ( ";

    final String FINISH_COLUMN = " ) ";

    final String COMMA = ",";

    final String ON_CONFLICT_REPLACE = "ON CONFLICT REPLACE";

    // Threads Table
    public static final String THREADS_TABLE = " threadsTable";

    public static final String THREAD_ID = "threadID";

    public static final String THREAD_NAME = "threadName";

    // OFFLINE Table
    public static final String HISTORY_TABLE = " historyTable";

    public static final String MU_ID = "muID";

    public static final String THREAD_ID_HISTORY = "threadId";

    public static final String IMAGE = "image";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String COMMENTS = "comments";

    public static final String KEYWORDS = "keywords";

    public static final String ADDRESS = "address";

    public static final String DATE = "date";

    public static final String TIME = "time";

    public static final String SCHOOL_CODE = "schoolCode";

    public static final String RATH_NUMBER = "rathNumber";

    public static final String VILLAGE_NAME = "villageName";

    public static final String VILLAGE_NAME2 = "villageName2";

    public static final String ANCHAL_DATA="Anchal";

    public static final String PATIENT_NAME = "patientname";

    public static final String HEAD_OF_FAMILY = "headfamily";

    public static final String AGE= "age";

//    public static final String HISTORY_TAKING = "historytaking";

    public static final String VISION_VR = "visionvr";
    public static final String VISION_VRWO = "visionvrwo";
    public static final String GENDER = "gender";

    public static final String GLASSES = "glasses";

    public static final String DATE_OF_EXAMINATION = "dateofexamination";

    public static final String VISION_VL = "visionvl";
    public static final String VISION_VLWO = "visionvlwo";
    public static final String HISTORY_COMPLAINTS = "historycomplaints";

    public static final String PAST_HISTORY = "pasthistory";



    public static final String PRESENT_HISTORY = "presenthistory";

    public static final String BP_SYSTOLIC = "bpsystolic";

    public static final String BP_DIASTOLIC = "bpdiastolic";

    public static final String BMI_HEIGHT = "bmiheight";


    public static final String BMI_WEIGHT = "bmiweight";

    public static final String BMI_OBESITY = "bmiobesity";

    public static final String SUGAR_FASTING = "sugarfasting";

    public static final String SUGAR_PP = "sugarpp";

    public static final String SUGAR_RANDOM = "sugarrandom";

    public static final String MEDICINE = "medicine";

    public static final String EYE_DROP = "eyedrop";

    public static final String AMOUNT = "amount";

    public static final String STATE = "statenew";

    public static final String REGISTRATION_CODE = "regis_code";
    public static final String AADHAR_NUMBER = "aadhar_num";
    public static final String PAITENT_MOBILE = "pat_mob";
    public static final String OCCUPATION = "occupation";
    public static final String ILLNESS_CAUSE = "illness_cause";
    public static final String DURATION = "duration";
    public static final String DIABETIC = "diabetic";
    public static final String CARDIAC = "cardiac";
    public static final String ONE_EYED = "one_eyed";
    public static final String HYPERNATION = "hypernation";
    public static final String ASTHAMATIC = "asthamatic";
    public static final String OTHERS = "others";
//    public static final String SPH_RIGHT = "sph_right";
//    public static final String CYCL_RIGHT = "cycl_right";
//    public static final String AXIS_RIGHT = "axis_right";
//    public static final String SPH_LEFT = "sph_left";
//    public static final String CYCL_LEFT = "cycl_left";
//    public static final String AXIS_LEFT = "axis_left";
//    public static final String NEAR = "near";
//    public static final String INSTRUCTION = "instruct";





    public static final String OTHER_DATA = "otherData";

    //MEMBERS DIRECTORY

    public static final String MEMBERS_DIRECTORY_TABLE = " membersDirectoryTable";

    public static final String CITY = "city";

    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String ID_NO = "idNo";

    public static final String SPOUSE_NAME = "spouseName";

    public static final String CONTACT_NO = "contactNo";

    public static final String MOBILE = "mobile";

    public static final String EMAIL = "email";

    public static final String DESIGNATION = "designation";

    public static final String ADD1 = "add1";

    public static final String ADD2 = "add2";

    public static final String ADD3 = "add3";

    public static final String PIN = "pin";

    public static final String TOWN = "town";

    public static final String PIC = "pic";

    public static final String SEQUENCE = "sequence";

    public static final String MEMBERS_DIRECTORY_SHSS_TABLE = " membersDirectorySHSSTable";




}
