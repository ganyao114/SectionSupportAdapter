package sectionsupport.swift.com.sectionsupportadapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.swift.sectionsupport.MyDividerItemDecoration;
import com.swift.sectionsupport.SectionSupportAdapter;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SectionSupportAdapter sectionSupportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final TestAdapter testAdapter = new TestAdapter();
        sectionSupportAdapter = new SectionSupportAdapter(testAdapter, new SectionSupportAdapter.SectionSupport() {
            @Override
            public int sectionHeaderLayoutId() {
                return R.layout.item_section;
            }

            @Override
            public Object itemType(int position) {
                return testAdapter.getSection(position);
            }

            @Override
            public void setSectionTitle(final Object itemType, View sectionView) {
                sectionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sectionSupportAdapter.toggle(itemType);
                    }
                });
                TextView textView = sectionView.findViewById(R.id.text);
                textView.setText(itemType.toString());
            }
        });
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, MyDividerItemDecoration.VERTICAL).setPadding(32, 0).setCanDrawDividerCallback(new MyDividerItemDecoration.CanDrawDividerCallback() {
            @Override
            public boolean canDrawDivider(int position, View itemView) {
                return !sectionSupportAdapter.isPrivatePosition(position) && !sectionSupportAdapter.isPrivatePosition(position + 1) && position != sectionSupportAdapter.getItemCount() - 1;
            }
        }));
        recyclerView.setAdapter(sectionSupportAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
