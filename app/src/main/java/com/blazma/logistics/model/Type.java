package com.blazma.logistics.model;

public class Type{
    public static String TAG_SELECTED_TAB_TYPE = "tag_selected_tab_type";
    public static String TAG_SELECTED_TASK_TYPE = "task_selected_task_type";

    public static class TabType {
        public static int MedicalTask = 0;
        public static int SwapTask = 1;
        public static int PharmaTask = 2;
        public static int CarTask = 3;
    }

    public static class TaskType{
        public static int ListTask = 0;
        public static int FreezerPlacement = 1;
        public static int DeliveryTask = 2;
    }
}






