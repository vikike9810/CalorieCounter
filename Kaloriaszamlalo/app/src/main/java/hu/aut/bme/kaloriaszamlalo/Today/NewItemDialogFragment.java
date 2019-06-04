package hu.aut.bme.kaloriaszamlalo.Today;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import hu.aut.bme.kaloriaszamlalo.Model.Food.HintItem;
import hu.aut.bme.kaloriaszamlalo.Model.Food.Hints;
import hu.aut.bme.kaloriaszamlalo.Model.Item;
import hu.aut.bme.kaloriaszamlalo.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewItemDialogFragment extends AppCompatDialogFragment {

    private EditText kcal;
    private EditText mennyiseg;
    private Spinner categorySpinner;
    private Hints hint;
    private String[] testResults = new String[] { "apple", "apple2", "apple3" };
    private AutoCompleteTextView actv;
    private ProgressBar progbar;
    private String[] foods;
    private boolean writing=false;

    public static final String TAG = "NewItemDialogFragment";

    public interface INewItemDialogListener {
        void onItemCreated(Item newItem);
    }

    private INewItemDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof INewItemDialogListener) {
            listener = (INewItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the INewItemDialogListener interface!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.uj)
                .setView(getContentView())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isValid()) {
                            listener.onItemCreated(getItem());
                        }
                    }

                    private boolean isValid() {
                        return actv.getText().length() > 0;
                    }

                    private Item getItem() {
                        Item KItem = new Item();
                        KItem.name = actv.getText().toString();
                        try {
                            KItem.kcal = Integer.parseInt(kcal.getText().toString());
                        } catch (NumberFormatException e) {
                            KItem.kcal = 0;
                        }
                        try {
                            KItem.mennyiseg = Integer.parseInt(mennyiseg.getText().toString());
                        } catch (NumberFormatException e) {
                            KItem.kcal = 0;
                        }
                        KItem.category = Item.Category.getByOrdinal(categorySpinner.getSelectedItemPosition());
                        KItem.save();
                        return KItem;
                    }
                })
                .setNegativeButton(R.string.Megsem, null)
                .create();
    }


    private View getContentView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_item, null);
        kcal = (EditText) contentView.findViewById(R.id.n_kcal);
        categorySpinner = (Spinner) contentView.findViewById(R.id.n_kategoria);
        mennyiseg = (EditText) contentView.findViewById(R.id.n_mennyiseg);
        categorySpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.category_items)));
        actv= contentView.findViewById(R.id.autoCompleteTextView1);
        actv.setThreshold(1);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                int index=0;
                for(String food: foods){
                    if(food==item.toString()){
                        if(hint!=null){
                            actv.setText(hint.hints.get(index).food.label);
                            int ikcal=(int)(hint.hints.get(index).food.nutrients.ENERC_KCAL);
                            kcal.setText(Integer.toString(ikcal));
                            break;
                        }
                        index++;
                    }
                }
                actv.dismissDropDown();
            }
        });
        progbar= contentView.findViewById(R.id.progbar);
        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(actv.length()>2 ){
                        HttpReq(actv.getText().toString());
                        progbar.setVisibility(View.VISIBLE);
                }
            }
        });
        return contentView;
    }

    public void HttpReq(String param){
        OkHttpClient client=new OkHttpClient();
        String url="https://api.edamam.com/api/food-database/parser?ingr="+param+"&category=generic-foods&app_id=2882dd4e&app_key=acccc0e3fb36132468805bd6d374cb0c&fbclid=IwAR2CQZEdr-qRPJ3yxeqeKxEJhTgy0G-VLles0jpZhzEfEZnpxoatLlRYU7A";
        final Request request= new Request.Builder()
                .url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    final String json = response.body().string();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonConvert(json);
                        }
                    });
                }
            }
        });

    }

    public void JsonConvert(String json){
        Gson gson = new Gson();
        hint = gson.fromJson(json, Hints.class);
        foods = getListFromHints(hint);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, foods);
        actv.setAdapter(adapter);
        actv.showDropDown();
        progbar.setVisibility(View.INVISIBLE);
    }

    public String[] getListFromHints(Hints hints){
        String[] foodlist= new String[hints.hints.size()];
        int index=0;
        for (HintItem item: hints.hints) {
            if(index<hints.hints.size()) {
                String food = item.food.label + " " +
                        item.food.nutrients.ENERC_KCAL +
                        " kcal";
                foodlist[index] = food;
                index++;
            }
            else break;
        }
        return foodlist.clone();
    }


}