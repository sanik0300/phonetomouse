package com.sanikshomemade.phonetomouse.activities;

import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.sanikshomemade.phonetomouse.MyApp;
import com.sanikshomemade.phonetomouse.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends MyApp.MyMultilangCompatActivity {

    private String[] getLinesArrayFromRaw(int resid) {
        InputStream inputStream = getResources().openRawResource(resid);
        BufferedReader bfr = new BufferedReader(new InputStreamReader(inputStream));
        ArrayList<String> result = new ArrayList<>();
        String crtLine="";
        while (crtLine != null) {
            try {
                crtLine = bfr.readLine();
                if(crtLine.isEmpty()) {
                    continue;
                }
                result.add(crtLine);
            }
            catch (Exception e) {
                continue;
            }
        }
        return result.toArray(new String[result.size()]);
    }

    private void fillAdapterSection(String[] entries, ArrayList<ArrayList<Map<String, String>>> totalArrayList){
        ArrayList<Map<String, String>> сhildDataItemList1 = new ArrayList<>();
        for (String month : entries) { // заполняем список атрибутов для каждого элемента
            Map<String, String> category1Map = new HashMap<>();
            category1Map.put("lineName", month);
            сhildDataItemList1.add(category1Map);
        }
        totalArrayList.add(сhildDataItemList1); // добавляем в коллекцию коллекций
    }

    private void fillExpandableList(ExpandableListView elv) {

        ArrayList<Map<String, String>> groupDataList = new ArrayList<>();// заполняем коллекцию групп из массива с названиями групп

        for (String group : this.getResources().getStringArray(R.array.info_categories)) {
            Map<String, String> titleMap = new HashMap<>();
            titleMap.put("categoryName", group);
            groupDataList.add(titleMap);
        }

        String groupFrom[] = new String[] { "categoryName" };// список атрибутов групп
        int groupTo[] = new int[] { android.R.id.text1 };// список ID view-элементов, в которые будет помещены атрибуты групп

        ArrayList<ArrayList<Map<String, String>>> totalChildDataList = new ArrayList<>();

        fillAdapterSection(getLinesArrayFromRaw(R.raw.how_to_connect), totalChildDataList);
        fillAdapterSection(this.getResources().getStringArray(R.array.gestures_explain), totalChildDataList);
        fillAdapterSection(this.getResources().getStringArray(R.array.disconnect_options), totalChildDataList);

        String childFrom[] = new String[] { "lineName" };// список атрибутов элементов
        int childTo[] = new int[] { android.R.id.text1 }; // список ID view-элементов, в которые будет помещены атрибуты элементов

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this, groupDataList,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, totalChildDataList, android.R.layout.simple_list_item_1,
                childFrom, childTo);
        elv.setAdapter(adapter);
    }

    @Override
    protected int getResIdForActionBarText() {
        return R.string.info_action_text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        fillExpandableList(this.findViewById(R.id.info_expand));
    }
}