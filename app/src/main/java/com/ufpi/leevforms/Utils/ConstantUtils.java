package com.ufpi.leevforms.Utils;

public class ConstantUtils {

    public static final String APPLICATION_ID = "com.ufpi.leevforms";

    public static final int USER_TYPE_TEACHER = 1;
    public static final int USER_TYPE_STUDENT = 2;

    public static final String DEVELOPMENT_BRANCH = "Desenvolvimento";
    public static final String PRODUCTION_BRANCH = "Produção";

    public static final String DATABASE_ACTUAL_BRANCH = DEVELOPMENT_BRANCH;

    public static final String USERS_BRANCH = "users";

    public static final String USER_FIELD_ID = "id";
    public static final String USER_FIELD_EMAIL = "email";
    public static final String USER_FIELD_NAME = "name";
    public static final String USER_FIELD_PROJECTS = "projects";
    public static final String USER_FIELD_USERTYPE = "userType";
    public static final String USER_FIELD_REGISTERFINALIZED = "isRegisterFinalized";
    public static final String USER_FIELD_VISIBLE = "isVisible";
    public static final String USER_FIELD_IDADVISOR = "idAdvisor";
    public static final String USER_FIELD_ID_INSTANCE_FREQUENCY = "idInstanceFrequency";
    public static final String USER_FIELD_ID_INSTANCE_FORMS = "idInstanceForms";

    public static final String FREQUENCIES_BRANCH = "frequencies";
    public static final String FREQUENCY_FIELD_DATE = "date";

    public static final String FORMS_BRANCH = "forms";
    public static final String FORMS_FIELD_ID = "id";
    public static final String FORMS_FIELD_NAME = "name";
    public static final String FORMS_FIELD_DESCRIPTION = "description";
    public static final String FORMS_FIELD_CREATIONDATE = "creationDate";

    public static final String QUESTIONS_BRANCH = "questions";
    public static final String QUESTIONS_FIELD_ID = "id";
    public static final String QUESTIONS_FIELD_DESCRIPTION = "description";
    public static final String QUESTIONS_FIELD_TYPE = "type";
    public static final String QUESTIONS_FIELD_ANSWEROPTIONS = "answerOptions";

    public static final int QUESTION_TYPE_SUBJETIVE = 1;
    public static final int QUESTION_TYPE_OBJETIVE_SINGLE_ANSWER = 2;
    public static final int QUESTION_TYPE_OBJETIVE_MULTIPLE_ANSWER = 3;

    public static final String ANSWERS_BRANCH = "answers";
    public static final String ANSWERS_FIELD_ID = "id";
    public static final String ANSWERS_FIELD_DESCRIPTION = "description";
}
