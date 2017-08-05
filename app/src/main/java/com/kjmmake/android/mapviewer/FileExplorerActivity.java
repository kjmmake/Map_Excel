package com.kjmmake.android.mapviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.mapviewer.R;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileExplorerActivity extends Activity {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private String mFileName;
    private ListView lvFileControl;
    private Context mContext = this;

    private List<String> lItem = null;
    private List<String> lPath = null;
    private String mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
    private TextView mPath;

    public ArrayList<Person> persons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explorer);
        mPath = (TextView) findViewById(R.id.tvPath);
        lvFileControl = (ListView) findViewById(R.id.lvFileControl);
        getDir(mRoot);

        lvFileControl.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                File file = new File(lPath.get(position));

                if (file.isDirectory()) {
                    if (file.canRead())
                        getDir(lPath.get(position));
                    else {
                        Toast.makeText(mContext, "No files in this folder.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mFileName = file.getName();
                    String result = file.getAbsolutePath();
                    try {
                        excelRead(result);
                    }catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "파일 열기 실패");
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "파일 열기 실패");
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public  void onBackPressed(){
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "파일 열기 취소");
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(this,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }


    private void getDir(String dirPath) {
        mPath.setText("Location: " + dirPath);

        lItem = new ArrayList<String>();
        lPath = new ArrayList<String>();

        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(mRoot)) {
            //item.add(root); //to root.
            //path.add(root);
            lItem.add("../"); //to parent folder
            lPath.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            lPath.add(file.getAbsolutePath());

            if (file.isDirectory())
                lItem.add(file.getName() + "/");
            else
                lItem.add(file.getName());
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lItem);
        lvFileControl.setAdapter(fileList);
    }

    public void excelRead(String path) throws FileNotFoundException, IOException
    {

            Workbook tempWorkbook;

            if (path.endsWith(".xls"))
            {
                tempWorkbook = new HSSFWorkbook(new FileInputStream(path));
            }
            else if (path.endsWith(".xlsx"))
            {
                tempWorkbook = new XSSFWorkbook(new FileInputStream(path));
            }
            else
            {
                throw new IllegalAccessError("xls / xlsx 확장자만 읽을 수 있습니다.");
            }
        persons = new ArrayList<>();
            try (Workbook workbook = tempWorkbook)
            {
                Sheet sheet = workbook.getSheetAt(0);

                // 행을 가져옵니다.
                for (Row row : sheet)
                {
                    String name = "";
                    String address = "";
                    double priority = 0;
                    for(int i=0;i<row.getLastCellNum();i++){
                        if(i == 0)
                            name = row.getCell(i).getStringCellValue();
                        else if(i==1)
                            address = row.getCell(i).getStringCellValue();
                        else if (i==2)
                            priority = row.getCell(i).getNumericCellValue();
                    }
                    persons.add(new Person(name,address,priority));
                }
            }catch(Exception e){
                e.printStackTrace();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "파일 열기 실패");
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }

            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", persons);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

    }

}