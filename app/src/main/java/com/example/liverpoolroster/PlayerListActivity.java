package com.example.liverpoolroster;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

public class PlayerListActivity extends SingleFragmentActivity {
    String TAG = getClass().getSimpleName();
    @Override
    protected Fragment createFragment() {
        return new PlayerListFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("PlayerListActivity", "onBackPressed() get called....");
        Intent intent = new Intent( Intent.ACTION_MAIN );
        intent.addCategory( Intent.CATEGORY_HOME );
        startActivity( intent );
    }
}
