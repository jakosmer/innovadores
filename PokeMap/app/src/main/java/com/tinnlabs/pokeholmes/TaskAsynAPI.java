package com.tinnlabs.pokeholmes;

import android.app.ProgressDialog;

/**
 * Created by jorgmecs on 2016/08/18.
 */
public class TaskAsynAPI {

    public TaskAsynAPI(Class classobj){
 //       ((Callback)classobj).call(null);
    }

}

interface Callback{
    void call(ProgressDialog dialog);
}