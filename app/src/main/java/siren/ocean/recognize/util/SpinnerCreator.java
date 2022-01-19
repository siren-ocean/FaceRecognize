package siren.ocean.recognize.util;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import siren.ocean.recognize.R;

/**
 * Spinner创建对象，简化创建流程
 * Created by Siren on 2022/1/19.
 */
public class SpinnerCreator<V> {

    public interface Callback<V> {
        void onItemSelected(V v);
    }

    public void build(Activity activity, int id, List<V> data, int position, Callback<V> callback) {
        ArrayAdapter<V> adapter = new ArrayAdapter<V>(activity, R.layout.support_simple_spinner_dropdown_item, android.R.id.text1, data);
        Spinner spinner = activity.findViewById(id);
        spinner.setAdapter(adapter);
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                callback.onItemSelected(adapter.getItem(position));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}
