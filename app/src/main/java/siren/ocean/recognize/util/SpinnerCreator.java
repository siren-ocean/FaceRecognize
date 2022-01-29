package siren.ocean.recognize.util;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import androidx.annotation.IdRes;
import siren.ocean.recognize.R;

/**
 * Spinner创建对象，简化创建流程
 * Created by Siren on 2022/1/19.
 */
public class SpinnerCreator<V> {

    private final Activity activity;
    private final Spinner spinner;

    public interface Callback<V> {
        void onItemSelected(V v);
    }

    public SpinnerCreator(Activity activity, @IdRes int id) {
        this.activity = activity;
        spinner = activity.findViewById(id);
    }

    public SpinnerCreator<V> setCallback(Callback<V> callback) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                V value = (V) spinner.getAdapter().getItem(position);
                callback.onItemSelected(value);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return this;
    }

    public SpinnerCreator<V> setData(List<V> data) {
        ArrayAdapter<V> adapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item, android.R.id.text1, data);
        spinner.setAdapter(adapter);
        return this;
    }

    public SpinnerCreator<V> setSelection(int position) {
        spinner.setSelection(position);
        return this;
    }
}
